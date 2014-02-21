/**
 * @author Pedro Bruel
 */

package ensemble.apps.emitting_sound;

import ensemble.*;

/*
 * A simple extension of Ensemble default EnvironmentAgent class.
 */

public class ES_Environment extends EnvironmentAgent
{
	private static final long serialVersionUID = 1L;
	/*
	 * init and configure are called once when the
	 * EnvironmentAgent is instantiated.
	 */
	@Override
	public boolean configure ( )
	{
		this.addEventServer ( "ensemble.apps.emitting_sound.ES_AudioEventServer", new Parameters ( ) );
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
