package ensemble.apps.pd_testing;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import ensemble.*;
import ensemble.apps.pd_testing.Pd_Constants.CONTROL_SYMBOLS;
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
	 * audio Actuator.
	 */
	private Actuator speaker;
	private Memory speaker_memory;
	/*
	 * Buffers and constants for
	 * initialising Pd.
	 */
    private float seconds = Pd_Constants.DEFAULT_SECONDS;
    /* Number of Pd ticks to get one second worth of samples. */
    private int ticks = ( int ) ( seconds * ( Pd_Constants.SAMPLE_RATE / ( float ) PdBase.blockSize ( ) ) );
    
	private int patch;
	private PdReceiver receiver;
	private boolean pd_audio_output = true;
	private boolean mute_patch = false;
	
    private int frames;
    private short[ ] dummy_pd_input;
    private short[ ] samples;
    private byte[ ] raw_samples;
    private byte[ ] dummy_samples;
    
    private ByteBuffer buf;
    private ShortBuffer shortBuf;
    
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
			if ( sent_float.get_source ( ).equals ( CONTROL_SYMBOLS.TICK.get_value ( ) ) )
			{
				ticks = ( int ) sent_float.get_value ( );
				seconds = ( float ) ticks / ( Pd_Constants.SAMPLE_RATE / ( float )PdBase.blockSize ( ) );
				System.err.print ( "PURE_DATA_SETTING_SECONDS: " + seconds + "\n" );
				System.err.print ( "PURE_DATA_SETTING_TICK_SIZE: " + ticks + "\n" );
			}
			else if ( sent_float.get_source ( ).equals ( CONTROL_SYMBOLS.SECONDS.get_value ( ) ) )
			{
				seconds = sent_float.get_value ( );
				ticks = ( int ) ( seconds * ( Pd_Constants.SAMPLE_RATE / ( float ) PdBase.blockSize ( ) ) );
				System.err.print ( "PURE_DATA_SETTING_SECONDS: " + seconds + "\n" );
				System.err.print ( "PURE_DATA_SETTING_TICK_SIZE: " + ticks + "\n" );
			}
    	}
    	for ( String sent_bang : bangs )
    	{
    		if ( sent_bang.equals ( CONTROL_SYMBOLS.AUDIO_OFF.get_value ( ) ) )
    		{
    			pd_audio_output = false;
    			System.err.println ( "PURE_DATA: AUDIO_OFF" );
    		}
    		else if ( sent_bang.equals ( CONTROL_SYMBOLS.AUDIO_ON.get_value ( ) ) )
    		{
    			pd_audio_output = true;
    			System.err.println ( "PURE_DATA: AUDIO_ON" );
    		}
    		else if ( sent_bang.equals ( CONTROL_SYMBOLS.AUDIO_TOGGLE.get_value ( ) ) )
    		{			
    			pd_audio_output = ! ( pd_audio_output );
    			System.err.println ( "PURE_DATA: AUDIO_TOGGLED" );
    		}
    		else if ( sent_bang.equals ( CONTROL_SYMBOLS.MUTE.get_value ( ) ) )
    		{
    			mute_patch = true;
    			pd_audio_output = ! ( mute_patch );
    			System.err.println ( "PURE_DATA: PATCH WITH NO AUDIO OUTPUT." );
    		}
    	}
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
	 * User can implement message checking here.
	 */
    private void process_pd_messages ( ) { };
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
		getAgent ( ).getKB ( ).registerFact ( Pd_Constants.CURRENT_INSTANT, String.valueOf ( Pd_Constants.START_INSTANT ), false );
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
		/*
		 * Subscribing to known control symbols.
		 */
		for ( CONTROL_SYMBOLS symbol : CONTROL_SYMBOLS.values( ) )
		{
			PdBase.subscribe( symbol.get_value ( ) );
		}
		/*
         * Patch opening.
         */
		try 
		{
			patch = PdBase.openPatch ( parameters.get( Pd_Constants.PATCH_ARGUMENT ) );
			process_ensemble_control_messages ( );
			process_pd_messages ( );
	    	( ( Pd_Receiver ) receiver ).start_new_cycle ( );
			System.err.print ( "PURE_DATA: OPENED PATCH ID=" + patch + "\n" );
		} 
		catch ( IOException e ) 
		{
			e.printStackTrace ( );
		}
		/* TODO: Subscribe to float outlets dinamically?
		 *       Subscribe to bang outlets dinamically? 
		 */
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
		close_dsp ( patch );
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
		int current_instant = Integer.parseInt ( getAgent ( ).getKB ( ).readFact ( Pd_Constants.CURRENT_INSTANT ) );

		process_ensemble_control_messages ( );
		process_pd_messages ( );
    	( ( Pd_Receiver ) receiver ).start_new_cycle ( );
		process_pd_ticks ( patch );
		
		if ( pd_audio_output && ! ( mute_patch ) )
		{
			output = raw_samples;
		}
		else
		{
			output = dummy_samples;
		}
		try 
		{
			speaker_memory.writeMemory ( new Pd_Audio_Buffer ( output, current_instant, getAgent ( ).getAgentName ( ) ) );
		} 
		catch ( MemoryException e ) 
		{
			e.printStackTrace ( );
		}
		current_instant += 1;
		getAgent ( ).getKB ( ).updateFact ( Pd_Constants.CURRENT_INSTANT, String.valueOf ( current_instant ) );
		speaker.act ( );
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
