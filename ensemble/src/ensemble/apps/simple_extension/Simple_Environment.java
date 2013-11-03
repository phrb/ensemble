/**
 * @author Pedro Bruel
 */

package ensemble.apps.simple_extension;

import ensemble.EnvironmentAgent;

/*
 * A simple extension of Ensemble default EnvironmentAgent class.
 */

public class Simple_Environment extends EnvironmentAgent
{
	private static final long serialVersionUID = 1L;
	/*
	 * init and configure are called once when the
	 * EnvironmentAgent is instantiated.
	 */
	@Override
	public boolean configure ( )
	{
		System.err.println ( "Environment Says: Configured!" );
		return true;
	}
	@Override
	public boolean init ( )
	{
		System.err.println ( "Environment Says: Initialized." );
		return true;
	}
}
