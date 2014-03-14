package ensemble.apps.pd_testing;

import ensemble.Actuator;
import ensemble.Event;

public class Pd_Actuator extends Actuator 
{
	@Override
	public boolean configure ( )
	{
		String new_event_type = parameters.get( Pd_Constants.EVENT_TYPE );
		if ( new_event_type == null )
		{
			setEventType ( Pd_Constants.DEFAULT_EVENT_TYPE );
		}
		else
		{
			setEventType ( new_event_type );
		}
		return true;		
	}
	public void process( Event evt )
	{
	}
}