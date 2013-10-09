/**
 * @author Pedro Bruel
 */

package ensemble.apps.hello_world;

import ensemble.MusicalAgent;

/*
 * A simple extension of Ensemble default MusicalAgent class.
 */

public class Hello_World_Agent extends MusicalAgent
{
	private static final long serialVersionUID = 1L;
	/*
	 * init and configure are called once every time an instance of
	 * this Agent is inserted into the virtual environment.
	 * 
	 * (non-Javadoc)
	 * @see ensemble.EnsembleAgent#configure()
	 */
	@Override
	public boolean configure ( )
	{
		/*
		 * Desired Components are added here.
		 */
		this.addComponent("Reasoning", "ensemble.apps.hello_world.Hello_World_Reasoning", null);
		System.err.println ( "Agent Says: Reasoning Added." );
		return true;	
	}
	@Override
	public boolean init ( )
	{
		configure ( );
		System.err.println ( "Agent Says: Initialized." );
		return true;
	}
}