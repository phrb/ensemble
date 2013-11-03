/**
 * @author Pedro Bruel
 */

package ensemble.apps.simple_extension;

import ensemble.Reasoning;

/*
 * A simple extension of Ensemble default Reasoning class. 
 */

public class Simple_Reasoning extends Reasoning
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
		System.err.println ( "Reasoning Says: Processing..." );		
	}
}
