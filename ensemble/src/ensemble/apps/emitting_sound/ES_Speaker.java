package ensemble.apps.emitting_sound;

import ensemble.*;

public class ES_Speaker extends Actuator 
{
	@Override
	public boolean configure ( )
	{
		setEventType ( "SOUND" );
		return true;		
	}

}
