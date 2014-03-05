package ensemble.apps.pd_testing;

import ensemble.Event;
import ensemble.Sensor;

public class Pd_Sensor extends Sensor
{
	@Override
	public boolean configure ( )
	{
		setEventType ( Pd_Constants.EVENT_TYPE );
		return true;		
	}
	@Override
	protected void process ( Event evt ) 
	{
		Pd_Audio_Buffer event = ( Pd_Audio_Buffer ) evt.objContent;
		System.err.println ( getAgent ( ).getAgentName ( ) + ":" + getComponentName ( ) + " Received event from \"" + event.get_source ( ) + "\" at \"" + event.get_pd_time_tag ( ) + "\"." );
		
	}
}
