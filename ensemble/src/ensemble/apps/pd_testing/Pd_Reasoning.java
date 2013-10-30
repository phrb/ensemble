package ensemble.apps.pd_testing;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

import ensemble.*;
import ensemble.memory.*;
import org.puredata.core.*;

/*
 * A simple extension of Ensemble default Reasoning class. 
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
    protected int SAMPLE_RATE = 44100;
    protected int BYTES_PER_SAMPLE = 2;                
    protected int ticks = 64;
	protected int patch;
	protected PdReceiver receiver;
	private boolean pd_sound_output;
	
    protected int frames;
    protected short[ ] dummy;
    protected short[ ] dummy_out;
    protected short[ ] samples;
    protected byte[ ] raw_samples;
    protected ByteBuffer buf;
    protected ShortBuffer shortBuf;
    
    private void open_dsp ( int target_patch )
    {
    	PdBase.sendBang ( target_patch + "dsp_on" );
    }
    private void close_dsp ( int target_patch )
    {
    	PdBase.sendBang ( target_patch + "dsp_off" );
    }
	private void play ( int target_patch )
	{
		open_dsp ( target_patch );
        PdBase.process ( ticks, dummy, samples );
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
		 */
		PdBase.openAudio ( 0, 2, 44100);
		PdBase.computeAudio( true );
		receiver = new Pd_Receiver ( );
		PdBase.setReceiver ( receiver );
		
		dummy = new short[ 0 ];
		dummy_out = new short [ PdBase.blockSize ( ) * 2 * BYTES_PER_SAMPLE ];
        /*
         * Patch opening.
         */
		try 
		{
			/*
			 * TODO: Receive patch name dinamically,
			 * 		 from Agent initialisation.
			 */
			patch = PdBase.openPatch ( "teste.pd" );
		} 
		catch ( IOException e ) 
		{
			e.printStackTrace ( );
		}
		/*
		 * Checking for patches that output no sound.
		 */
		pd_sound_output = ! ( PdBase.exists ( patch + "NOSOUND" ) );
		if ( ! ( pd_sound_output ) )
		{
			System.err.println ( "PURE_DATA: PATCH WITH NO SOUND OUTPUT." );			
		}
		/*
		 * Setting tick size from
		 * Pd patch.
		 */
		if ( PdBase.exists ( patch + "get_tick_size" ) )
		{
			PdBase.subscribe ( patch + "send_tick_size" );
			PdBase.sendBang( patch + "get_tick_size" );
			PdBase.process( 1, dummy, dummy_out );
			PdBase.pollPdMessageQueue ( );
			for ( Pd_Float number : ( ( Pd_Receiver ) receiver).get_float_list ( ) )
			{
				if ( number.get_name ( ).equals( patch + "send_tick_size" ) )
				{
					ticks = ( int )number.get_value ( );
					System.err.print ( "PURE_DATA_SETTING_TICK_SIZE: " + ticks + "\n" );
				}
			}
			( ( Pd_Receiver ) receiver ).start_new_turn ( );
		}
		close_dsp ( patch );
		/*
		 * Initialising Pd audio buffers.
		 */
		frames = PdBase.blockSize ( ) * ticks;        
		samples = new short[ frames * 2 ];       
		raw_samples = new byte[ samples.length * BYTES_PER_SAMPLE ];
		buf = ByteBuffer.wrap ( raw_samples );
		shortBuf = buf.asShortBuffer ( );
		System.err.println ( "Reasoning Says: Initialized." );
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
		if ( pd_sound_output )
		{
			play ( patch );
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
		PdBase.pollPdMessageQueue ( );
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
		System.err.println ( "Reasoning Says: Registered Event Handler." );
		speaker = ( Pd_Speaker ) event_handler;
		speaker_memory = getAgent ( ).getKB ( ).getMemory ( speaker.getComponentName ( ) );
	}
}
