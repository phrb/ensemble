package ensemble.apps.pd_testing;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

import ensemble.*;
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
	private boolean pd_sound_output;
	
    protected int frames;
    protected short[ ] dummy_input;
    protected short[ ] dummy_output;
    protected short[ ] samples;
    protected byte[ ] raw_samples;
    
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
    private void process_messages ( )
    {
    	PdBase.pollPdMessageQueue ( );
    	for ( Pd_Float sent_float : ( ( Pd_Receiver ) receiver ).get_float_list ( ) )
    	{
    		System.err.println ( "PURE_DATA: FLOATS RECEIVED:" );
    		System.err.print ( "NAME=" + sent_float.get_name ( ) + " VALUE=" + sent_float.get_value ( ) + "\n" );
    	}
    	for ( String sent_bang : ( ( Pd_Receiver ) receiver ).get_bang_list ( ) )
    	{
    		if ( sent_bang == patch + Pd_Constants.AUDIO_OFF )
    		{
    			pd_sound_output = false;
    		}
    		else if ( sent_bang == patch + Pd_Constants.AUDIO_ON )
    		{
    			pd_sound_output = true;
    		}
    		else if ( sent_bang == patch + Pd_Constants.AUDIO_TOGGLE )
    		{
    			pd_sound_output = ! ( pd_sound_output );
    		}
    		System.err.println ( "PURE_DATA: BANGS RECEIVED:" );
    		System.err.print ( "SOURCE=" + sent_bang + "\n" );
    	}
    	( ( Pd_Receiver ) receiver ).start_new_cycle ( );
    }
	private void get_samples ( int target_patch )
	{
		open_dsp ( target_patch );
        PdBase.process ( ticks, dummy_input, samples );
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
		
		dummy_input = new short[ Pd_Constants.INPUT_CHANNELS ];
		dummy_output = new short [ PdBase.blockSize ( ) * Pd_Constants.OUTPUT_CHANNELS * Pd_Constants.BYTES_PER_SAMPLE ];
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
		pd_sound_output = ! ( PdBase.exists ( patch + "NOSOUND" ) );
		/*
		 * Subscribing to known control symbols:
		 */
		PdBase.subscribe ( patch + Pd_Constants.AUDIO_ON );
		PdBase.subscribe ( patch + Pd_Constants.AUDIO_OFF );
		PdBase.subscribe ( patch + Pd_Constants.AUDIO_TOGGLE );
		/* TODO: Subscribe to float outlets dinamically?
		 *       Subscribe to bang outlests dinamically? 
		 */
		if ( ! ( pd_sound_output ) )
		{
			System.err.println ( "PURE_DATA: PATCH WITH NO SOUND OUTPUT." );			
		}
		/*
		 * Setting tick size from
		 * Pd patch.
		 */
		if ( PdBase.exists ( patch + Pd_Constants.TICK_TARGET ) )
		{
			PdBase.subscribe ( patch + Pd_Constants.TICK );
			PdBase.sendBang( patch + Pd_Constants.TICK_TARGET );
			PdBase.process( 1, dummy_input, dummy_output );
			process_messages ( );
			for ( Pd_Float number : ( ( Pd_Receiver ) receiver).get_float_list ( ) )
			{
				if ( number.get_name ( ).equals( patch + Pd_Constants.TICK ) )
				{
					ticks = ( int ) number.get_value ( );
					seconds = ( float ) ticks / ( Pd_Constants.SAMPLE_RATE / ( float )PdBase.blockSize ( ) );
					System.err.print ( "PURE_DATA_SETTING_SECONDS: " + seconds + "\n" );
					System.err.print ( "PURE_DATA_SETTING_TICK_SIZE: " + ticks + "\n" );
				}
			}
			( ( Pd_Receiver ) receiver ).start_new_cycle ( );
		}
		else if ( PdBase.exists ( patch + Pd_Constants.SECONDS_TARGET ) )
		{
			PdBase.subscribe ( patch + Pd_Constants.SECONDS );
			PdBase.sendBang( patch + Pd_Constants.SECONDS_TARGET );
			PdBase.process( 1, dummy_input, dummy_output );
			process_messages ( );
			for ( Pd_Float number : ( ( Pd_Receiver ) receiver).get_float_list ( ) )
			{
				if ( number.get_name ( ).equals( patch + Pd_Constants.SECONDS ) )
				{
					seconds = number.get_value ( );
					ticks = ( int ) ( seconds * ( Pd_Constants.SAMPLE_RATE / ( float ) PdBase.blockSize ( ) ) );
					System.err.print ( "PURE_DATA_SETTING_SECONDS: " + seconds + "\n" );
					System.err.print ( "PURE_DATA_SETTING_TICK_SIZE: " + ticks + "\n" );
				}
			}
			( ( Pd_Receiver ) receiver ).start_new_cycle ( );
		}
		/*
		 * Initialising Pd audio buffers.
		 */
		frames = PdBase.blockSize ( ) * ticks;        
		samples = new short[ frames * Pd_Constants.OUTPUT_CHANNELS ];       
		raw_samples = new byte[ samples.length * Pd_Constants.BYTES_PER_SAMPLE ];
		buf = ByteBuffer.wrap ( raw_samples );
		shortBuf = buf.asShortBuffer ( );
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
		get_samples ( patch );
		process_messages ( );
		if ( pd_sound_output )
		{
			try 
			{
				speaker_memory.writeMemory( raw_samples );
			} 
			catch ( MemoryException e ) 
			{
				e.printStackTrace ( );
			}
			speaker.act ( );
		}
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
