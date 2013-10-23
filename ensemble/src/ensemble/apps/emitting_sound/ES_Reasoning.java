/**
 * @author Pedro Bruel
 */

package ensemble.apps.emitting_sound;

import ensemble.*;
import ensemble.memory.*;

/*
 * A simple extension of Ensemble default Reasoning class. 
 */

public class ES_Reasoning extends Reasoning
{
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
		try 
		{
			speaker_memory.writeMemory( 84 );
		} 
		catch ( MemoryException e ) 
		{
			e.printStackTrace ( );
		}
		speaker.act ( );
		System.err.println ( "Reasoning Says: Processing..." );
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
