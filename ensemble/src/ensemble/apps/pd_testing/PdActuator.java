package ensemble.apps.pd_testing;

import ensemble.Actuator;
import ensemble.Event;

public class PdActuator extends Actuator 
{
	@Override
	public boolean configure ( )
	{
		String new_event_type = parameters.get( PdConstants.EVENT_TYPE );
		if ( new_event_type == null )
		{
			setEventType ( PdConstants.DEFAULT_EVENT_TYPE );
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