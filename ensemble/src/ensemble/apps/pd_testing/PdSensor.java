package ensemble.apps.pd_testing;

import ensemble.Event;
import ensemble.Sensor;

public class PdSensor extends Sensor
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
	@Override
	protected void process ( Event evt ) 
	{
	}
}
