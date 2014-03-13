package ensemble.apps.pd_testing;

import ensemble.Event;
import ensemble.Sensor;

public class Pd_Sensor extends Sensor
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
	@Override
	protected void process ( Event evt ) 
	{

	}
}
