package ensemble.apps.pd_testing;

import ensemble.MusicalAgent;
import ensemble.Parameters;

/*
 * A simple extension of Ensemble default MusicalAgent class.
 */

public class Pd_Agent extends MusicalAgent
{
	private static final long serialVersionUID = 1L;
	private String reasoning_patch;
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
		reasoning_patch = parameters.get( Pd_Constants.PATCH_ARGUMENT );
		if ( reasoning_patch == null )
		{
			System.err.print ( "PURE_DATA: NO_PATCH_ERROR\n" );
			return false;
		}
		else
		{
			Parameters reasoning_parameters = new Parameters ( );
			reasoning_parameters.put( Pd_Constants.PATCH_ARGUMENT, reasoning_patch );
			/*
			 * Desired Components are added here.
			 */
			this.addComponent ( "Reasoning", "ensemble.apps.pd_testing.Pd_Reasoning", reasoning_parameters );
			this.addComponent ( "Speaker", "ensemble.apps.pd_testing.Pd_Speaker", new Parameters ( ) );
			return true;	
		}
	}
	@Override
	public boolean init ( )
	{
		configure ( );
		return true;
	}
}
