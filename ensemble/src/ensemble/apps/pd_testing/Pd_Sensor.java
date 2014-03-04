package ensemble.apps.pd_testing;

import ensemble.Sensor;

public class Pd_Sensor extends Sensor
{
	@Override
	public boolean configure ( )
	{
		setEventType ( Pd_Constants.EVENT_TYPE );
		return true;		
	}
}
