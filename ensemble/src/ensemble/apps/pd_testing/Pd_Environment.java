package ensemble.apps.pd_testing;

import org.puredata.core.PdBase;
import org.puredata.core.PdReceiver;

import ensemble.EnvironmentAgent;
import ensemble.Parameters;

/*
 * A simple extension of Ensemble default EnvironmentAgent class.
 */

public class Pd_Environment extends EnvironmentAgent
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
		this.addEventServer ( "ensemble.apps.pd_testing.Pd_AudioEventServer", new Parameters ( ) );
		return true;
	}
	@Override
	public boolean init ( )
	{
		if ( parameters.containsKey( "PD_INIT" ) && parameters.get( "PD_INIT" ).equals( "TRUE" ) )
		{
			System.err.println ( "PURE DATA: ALREADY INITIALISED." );
		}
		else
		{
			/*
			 * Pd Setup
			 */
			PdBase.openAudio ( Pd_Constants.INPUT_CHANNELS, Pd_Constants.OUTPUT_CHANNELS, Pd_Constants.SAMPLE_RATE );
			PdBase.computeAudio( true );
		}
		return true;
	}
	@Override
	public boolean finit ( )
	{
		PdBase.release ( );
		return true;
	}
}
