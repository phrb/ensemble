package ensemble.apps.pd_testing;

import org.puredata.core.PdBase;

import ensemble.EnvironmentAgent;
import ensemble.Parameters;

/*
 * A simple extension of Ensemble default EnvironmentAgent class.
 */

public class PdEnvironment extends EnvironmentAgent
{
	private static final long serialVersionUID = 1L;
	public PdReceiver receiver;
	/*
	 * init and configure are called once when the
	 * EnvironmentAgent is instantiated.
	 */
	@Override
	public boolean configure ( )
	{
		this.addEventServer ( "ensemble.apps.pd_testing.PdEventServer", new Parameters ( ) );
		return true;
	}
	public PdReceiver get_receiver ( )
	{
		return receiver;
	}
	@Override
	public boolean init ( )
	{
		if ( parameters.containsKey( "PD_INIT" ) && parameters.get( "PD_INIT" ).equals( "TRUE" ) )
		{
			System.err.println ( "PURE DATA: INITIALISED AT LOADER LEVEL." );
		}
		/*
		 * Pd Setup
		 */
		System.err.println ( "PURE DATA: INITIALISED." );
		return true;
	}
	@Override
	public boolean finit ( )
	{
		PdBase.release ( );
		return true;
	}
}
