package ensemble.apps.emitting_sound;

import java.util.ArrayList;
import ensemble.*;
import ensemble.apps.emitting_sound.ES_World;

import javax.sound.midi.*;

public class ES_SoundEventServer extends EventServer 
{
	ES_World world;
	protected String agent_name;
	protected String agent_component_name;
	private int event_pointer;
	private ArrayList< ES_Note > events = new ArrayList< ES_Note > ( );
	/*
	 * MIDI
	 */
	Synthesizer	synth;
	MidiChannel	channel;
	@Override
	public boolean configure ( ) 
	{
		System.err.println ( "Sound_Event_Server: Configured." );
		setEventType ( "SOUND" );
		return true;
	}
	
	/* (non-Javadoc)
	 * @see ensemble.EventServer#init()
	 */
	@Override
	public boolean init ( ) 
	{
		System.err.println ( "Sound_Event_Server: Initialized." );
		world = ( ES_World ) envAgent.getWorld ( );
		event_pointer = 0;		/*
		 * MIDI opening.
		 */
		try 
		{
			synth = MidiSystem.getSynthesizer ( );
			synth.open ( );
			MidiChannel[ ] channels = synth.getChannels ( );
			channel = channels[ 0 ];

		} 
		catch ( MidiUnavailableException e ) 
		{
			e.printStackTrace ( );
		}
		return true;
	}
	@Override
	public boolean finit ( )
	{
		return true;
	}
	public void process ( )
	{
		if ( event_pointer < events.size ( ) )
		{
			ES_Note event = events.get ( event_pointer );
			event_pointer += 1;
			if ( event != null )
			{
				/*
				 * MIDI
				 */
				int velocity = event.get_velocity ( );
				int note = event.get_note ( );
				channel.noteOn( note, velocity );
			}
		}
	}
	@Override
	public void processSense ( Event event ) 
	{		
		events.add ( ( ES_Note ) event.objContent );
	}
}
