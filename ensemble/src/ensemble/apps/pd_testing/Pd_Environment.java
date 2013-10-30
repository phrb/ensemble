package ensemble.apps.pd_testing;

import ensemble.EnvironmentAgent;
import ensemble.Parameters;

/*
 * A simple extension of Ensemble default EnvironmentAgent class.
 */

public class Pd_Environment extends EnvironmentAgent
{
	private static final long serialVersionUID = 1L;
	/*
	 * init and configure are called once when the
	 * EnvironmentAgent is instantiated.
	 */
	@Override
	public boolean configure ( )
	{
		this.addEventServer ( "ensemble.apps.pd_testing.Pd_SoundEventServer", new Parameters ( ) );
		System.err.println ( "Environment Says: Configured!" );
		return true;
	}
	@Override
	public boolean init ( )
	{
		configure ( );
		System.err.println ( "Environment Says: Initialized." );
		return true;
	}
}
