package ensemble.apps.pd_testing;

import ensemble.Actuator;

public class Pd_Speaker extends Actuator 
{
	@Override
	public boolean configure ( )
	{
		setEventType ( "SOUND" );
		return true;		
	}
}