/**
 * @author Pedro Bruel
 */

package ensemble.apps.hello_world;

import ensemble.Reasoning;

/*
 * A simple extension of Ensemble default Reasoning class. 
 */

public class Hello_World_Reasoning extends Reasoning
{
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
		System.err.println ( "Reasoning Says: Initialized. Hello, World!" );
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
		System.err.println ( "Reasoning Says: Processing..." );		
	}
}
