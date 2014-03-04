package ensemble.apps.pd_testing;

import ensemble.Actuator;

public class Pd_Actuator extends Actuator 
{
	@Override
	public boolean configure ( )
	{
		setEventType ( Pd_Constants.EVENT_TYPE );
		return true;		
	}
}