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
		/* TODO: Dinamically load multiple patches
		 */
		reasoning_patch = parameters.get ( Pd_Constants.PATCH_ARGUMENT );
		if ( reasoning_patch == null )
		{
			System.err.print ( "PURE_DATA: NO_PATCH_ERROR\n" );
			return false;
		}
		else
		{
			int actuator_index = 0;
			int sensor_index = 0;
			
			String sensor_class = parameters.get( "SENSOR" + "_" + sensor_index );
			String actuator_class = parameters.get ( "ACTUATOR" + "_" + actuator_index );
			
			Parameters reasoning_parameters = new Parameters ( );
			reasoning_parameters.put( Pd_Constants.PATCH_ARGUMENT, reasoning_patch );
			/*
			 * Desired Components are added here.
			 */
			this.addComponent ( "Reasoning", "ensemble.apps.pd_testing.Pd_Reasoning", reasoning_parameters );
			while ( actuator_class != null )
			{
				this.addComponent ( "Actuator" + "_" + actuator_index, actuator_class, new Parameters ( ) );
				actuator_index += 1;
				actuator_class = parameters.get ( "ACTUATOR" + "_" + actuator_index );
			}
			while ( sensor_class != null )
			{
				this.addComponent ( "Sensor" + "_" + sensor_index, sensor_class, new Parameters ( ) );
				sensor_index += 1;
				sensor_class = parameters.get ( "SENSOR" + "_" + sensor_index );
			}
			return true;	
		}
	}
	@Override
	public boolean init ( )
	{
		return true;
	}
}