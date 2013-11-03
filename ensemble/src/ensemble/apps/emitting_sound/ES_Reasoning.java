/**
 * @author Pedro Bruel
 */

package ensemble.apps.emitting_sound;

import ensemble.*;
import ensemble.memory.*;
import java.util.Random;

/*
 * A simple extension of Ensemble default Reasoning class. 
 */

public class ES_Reasoning extends Reasoning
{
	private int MIN_MIDI;
	private int MIDI_INTERVAL;
	private int MAX_INTERVAL;
	private int VELOCITY;
	
	private Random note_generator;
	private Random interval_generator;
	/*
	 * Period of sound emissions, 
	 * measured in number of
	 * process ( ) calls.
	 * 
	 */
	private int sing_interval;
	private int sing_time;
	/* 
	 * This reasoning will have its own
	 * sound Actuator.
	 */
	private Actuator speaker;
	private Memory speaker_memory;
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
		MIN_MIDI = Integer.parseInt ( parameters.get ( ES_Constants.MIDI_MIN ) );
		MIDI_INTERVAL = Integer.parseInt ( parameters.get ( ES_Constants.MIDI_INTERVAL ) );
		MAX_INTERVAL = Integer.parseInt ( parameters.get ( ES_Constants.MAX_INTERVAL ) );
		VELOCITY = Integer.parseInt( parameters.get ( ES_Constants.VELOCITY ) );
		note_generator = new Random ( );
		interval_generator = new Random ( );
		sing_time = 0;
		sing_interval = 1 + interval_generator.nextInt ( MAX_INTERVAL );
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
		if ( sing_time % sing_interval == 0 )
		{
			int note = MIN_MIDI + note_generator.nextInt ( MIDI_INTERVAL );
			try 
			{
				speaker_memory.writeMemory( new ES_Note ( note, VELOCITY ) );
			} 
			catch ( MemoryException e ) 
			{
				e.printStackTrace ( );
			}
			speaker.act ( );
		}
		sing_time++;
		if ( sing_time >= sing_interval )
		{
			sing_interval = 1 + interval_generator.nextInt ( MAX_INTERVAL );
			sing_time = 0;
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
		System.err.println ( "Reasoning Says: Registered Event Handler." );
		speaker = ( ES_Speaker ) event_handler;
		speaker_memory = getAgent ( ).getKB ( ).getMemory ( speaker.getComponentName ( ) );
	}
}
