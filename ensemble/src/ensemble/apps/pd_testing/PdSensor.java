package ensemble.apps.pd_testing;

import ensemble.Event;
import ensemble.Sensor;

public class PdSensor extends Sensor
{
	PdAudioBlock[ ] audio_blocks = new PdAudioBlock[ PdConstants.PD_AUDIO_BLOCKS_PERSISTENCE ];
	int last_block_written = 0;
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
		PdEvent new_event = ( PdEvent ) evt.objContent;
		if ( new_event.get_type ( ).equals ( PdConstants.AUDIO_BLOCK ) )
		{
			audio_blocks[ last_block_written ] = ( PdAudioBlock ) new_event.get_content ( );
			last_block_written += 1;
			if ( last_block_written >= PdConstants.PD_AUDIO_BLOCKS_PERSISTENCE )
			{
				last_block_written = 0;
			}
		}
	}
	public PdAudioBlock get_next ( int offset )
	{
		int block = last_block_written - ( offset * 3 );
		if ( block >= 0 )
		{
			return audio_blocks[ block ];
		}
		else
		{
			return null;	
		}
	}
}
