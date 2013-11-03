/**
 * @author Pedro Bruel
 */

package ensemble.apps.simple_extension;

import ensemble.MusicalAgent;

/*
 * A simple extension of Ensemble default MusicalAgent class.
 */

public class Simple_Agent extends MusicalAgent
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
		this.addComponent("Reasoning", "ensemble.apps.simple_extension.Simple_Reasoning", null);
		System.err.println ( "Agent Says: Reasoning Added." );
		return true;	
	}
	@Override
	public boolean init ( )
	{
		System.err.println ( "Agent Says: Initialized." );
		return true;
	}
}