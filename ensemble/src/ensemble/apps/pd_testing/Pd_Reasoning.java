package ensemble.apps.pd_testing;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import ensemble.*;
import ensemble.apps.pd_testing.Pd_Constants.CONTROL_SYMBOL;
import ensemble.memory.*;
import org.puredata.core.*;

/*
 * This is a simple Reasoning,
 * that assumes an instance of Pd
 * opened with zero input channels,
 * and two output channels.
 * 
 */

public class Pd_Reasoning extends Reasoning
{
	/* 
	 * This reasoning will have its own
	 * sound Actuator.
	 */
	private Actuator speaker;
	private Memory speaker_memory;
	/*
	 * Buffers and constants for
	 * initialising Pd.
	 */
    protected float seconds = Pd_Constants.DEFAULT_SECONDS;
    /* Number of Pd ticks to get one second worth of samples. */
    protected int ticks = ( int ) ( seconds * ( Pd_Constants.SAMPLE_RATE / ( float ) PdBase.blockSize ( ) ) );
    
	protected int patch;
	protected PdReceiver receiver;
	private boolean pd_audio_output;
	
    protected int frames;
    protected short[ ] dummy_pd_input;
    protected short[ ] dummy_pd_output;
    protected short[ ] samples;
    protected byte[ ] raw_samples;
    protected byte[ ] dummy_samples;
    
    protected ByteBuffer buf;
    protected ShortBuffer shortBuf;
    
    private void open_dsp ( int target_patch )
    {
    	PdBase.sendBang ( target_patch + Pd_Constants.PROCESSING_ON );
    }
    private void close_dsp ( int target_patch )
    {
    	PdBase.sendBang ( target_patch + Pd_Constants.PROCESSING_OFF );
    }
    private void process_ensemble_control_messages ( )
    {
    	/*
    	 * Process all messages sent
    	 * to default control symbols.
    	 * 
    	 */
    	PdBase.pollPdMessageQueue ( );
    	ArrayList< Pd_Float > floats = ( ( Pd_Receiver ) receiver ).get_float_list ( );
    	ArrayList< String > bangs = ( ( Pd_Receiver ) receiver ).get_bang_list ( );
    	for ( Pd_Float sent_float : floats )
    	{
			if ( sent_float.get_source ( ).equals ( patch + CONTROL_SYMBOL.TICK.get_value ( ) ) )
			{
				ticks = ( int ) sent_float.get_value ( );
				seconds = ( float ) ticks / ( Pd_Constants.SAMPLE_RATE / ( float )PdBase.blockSize ( ) );
				System.err.print ( "PURE_DATA_SETTING_SECONDS: " + seconds + "\n" );
				System.err.print ( "PURE_DATA_SETTING_TICK_SIZE: " + ticks + "\n" );
			}
			else if ( sent_float.get_source ( ).equals ( patch + CONTROL_SYMBOL.SECONDS.get_value ( ) ) )
			{
				seconds = sent_float.get_value ( );
				ticks = ( int ) ( seconds * ( Pd_Constants.SAMPLE_RATE / ( float ) PdBase.blockSize ( ) ) );
				System.err.print ( "PURE_DATA_SETTING_SECONDS: " + seconds + "\n" );
				System.err.print ( "PURE_DATA_SETTING_TICK_SIZE: " + ticks + "\n" );
			}
    	}
    	for ( String sent_bang : bangs )
    	{
    		if ( sent_bang.equals ( patch + CONTROL_SYMBOL.AUDIO_OFF.get_value ( ) ) )
    		{
    			pd_audio_output = false;
    			System.err.println ( "PURE_DATA: SOUND_OFF" );
    		}
    		else if ( sent_bang.equals ( patch + CONTROL_SYMBOL.AUDIO_ON.get_value ( ) ) )
    		{
    			pd_audio_output = true;
    			System.err.println ( "PURE_DATA: AUDIO_ON" );
    		}
    		else if ( sent_bang.equals ( patch + CONTROL_SYMBOL.AUDIO_TOGGLE.get_value ( ) ) )
    		{			
    			pd_audio_output = ! ( pd_audio_output );
    			System.err.println ( "PURE_DATA: AUDIO_TOGGLED" );
    		}
    	}
    }
    private void process_pd_messages ( )
    {
    	/*
    	 * User can implement message checking here.
    	 */
    	( ( Pd_Receiver ) receiver ).start_new_cycle ( );
    }
	private void process_pd_ticks ( int target_patch )
	{
		open_dsp ( target_patch );
        PdBase.process ( ticks, dummy_pd_input, samples );
        shortBuf.rewind ( );
        shortBuf.put ( samples );
        close_dsp ( target_patch );
	}
	/*
	 * The init method will be called once every time this
	 * Reasoning is included in an Agent's Components.
	 * 
	 * (non-Javadoc)
	 * @see ensemble.MusicalAgentComponent#init()
	 */
	@Override
	public boolean init ( ) 
	{
		
		/*
		 * Pd Setup
		 * 
		 * Each reasoning has its own
		 * receiver.
		 * 
		 */
		receiver = new Pd_Receiver ( );
		PdBase.setReceiver ( receiver );
		
		dummy_pd_input = new short[ Pd_Constants.INPUT_CHANNELS ];
		dummy_pd_output = new short [ PdBase.blockSize ( ) * Pd_Constants.OUTPUT_CHANNELS * Pd_Constants.BYTES_PER_SAMPLE ];
        /*
         * Patch opening.
         */
		try 
		{
			patch = PdBase.openPatch ( parameters.get( Pd_Constants.PATCH_ARGUMENT ) );
			close_dsp ( patch );
			System.err.print( "PURE_DATA: OPENED PATCH ID = " + patch + "\n" );
		} 
		catch ( IOException e ) 
		{
			e.printStackTrace ( );
		}
		/*
		 * Checking for patches that output no sound.
		 */
		pd_audio_output = ! ( PdBase.exists ( patch + "NOSOUND" ) );
		/*
		 * Subscribing to known control symbols:
		 */
		for ( CONTROL_SYMBOL symbol : CONTROL_SYMBOL.values( ) )
		{
			PdBase.subscribe( patch + symbol.get_value ( ) );
		}
		/* TODO: Subscribe to float outlets dinamically?
		 *       Subscribe to bang outlests dinamically? 
		 */
		if ( ! ( pd_audio_output ) )
		{
			System.err.println ( "PURE_DATA: PATCH WITH NO SOUND OUTPUT." );			
		}
		/*
		 * Setting tick size from
		 * Pd patch.
		 */
		if ( PdBase.exists ( patch + Pd_Constants.TICK_TARGET ) )
		{
			PdBase.sendBang( patch + Pd_Constants.TICK_TARGET );
			PdBase.process( 1, dummy_pd_input, dummy_pd_output );
			process_ensemble_control_messages ( );
			process_pd_messages ( );
		}
		else if ( PdBase.exists ( patch + Pd_Constants.SECONDS_TARGET ) )
		{
			PdBase.sendBang( patch + Pd_Constants.SECONDS_TARGET );
			PdBase.process( 1, dummy_pd_input, dummy_pd_output );
			process_ensemble_control_messages ( );
			process_pd_messages ( );
		}
		/*
		 * Initialising Pd audio buffers.
		 */
		frames = PdBase.blockSize ( ) * ticks;        
		samples = new short[ frames * Pd_Constants.OUTPUT_CHANNELS ];       
		raw_samples = new byte[ samples.length * Pd_Constants.BYTES_PER_SAMPLE ];
		dummy_samples = new byte[ samples.length * Pd_Constants.BYTES_PER_SAMPLE ];
		buf = ByteBuffer.wrap ( raw_samples );
		shortBuf = buf.asShortBuffer ( );
		/*
		 * The dummy samples buffer is used when a patch is not
		 * suposed to produced samples, but we do not want to
		 * lose real-time due to lack of samples to play, so we
		 * just play zeroes.
		 * 
		 */
		for ( int i = 0; i < dummy_samples.length; i++ )
		{
			dummy_samples[ i ] = 0;
		}
		return true;
	}
	/*
	 * The process method is called once every cycle
	 * of the framework.
	 * 
	 * (non-Javadoc)
	 * @see ensemble.Reasoning#process()
	 */
	@Override
	public void process ( ) 
	{
		byte[ ] output;
		process_pd_ticks ( patch );
		if ( pd_audio_output )
		{
			output = raw_samples;
		}
		else
		{
			output = dummy_samples;
		}
		try 
		{
			speaker_memory.writeMemory( output );
		} 
		catch ( MemoryException e ) 
		{
			e.printStackTrace ( );
		}
		speaker.act ( );
		process_ensemble_control_messages ( );
		process_pd_messages ( );
	}
	/*
	 * Called when and event handler is registered in the agent.
	 * 
	 * (non-Javadoc)
	 * @see ensemble.Reasoning#eventHandlerRegistered(ensemble.EventHandler)
	 */
	@Override
	protected void eventHandlerRegistered ( EventHandler event_handler ) 
	{
		speaker = ( Pd_Speaker ) event_handler;
		speaker_memory = getAgent ( ).getKB ( ).getMemory ( speaker.getComponentName ( ) );
	}
}
