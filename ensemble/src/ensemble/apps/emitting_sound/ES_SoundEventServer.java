package ensemble.apps.emitting_sound;

import java.util.ArrayList;

import ensemble.*;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiUnavailableException;

public class ES_SoundEventServer extends EventServer 
{
	ES_World world;
	protected String agent_name;
	protected String agent_component_name;
	ArrayList< Event > events = new ArrayList< Event > ( );
	/*
	 * MIDI
	 */
	Synthesizer	synth;
	MidiChannel	channel;
	
	@Override
	public boolean configure ( ) 
	{
		System.out.println ( "Sound_Event_Server: Configured." );
		setEventType ( "SOUND" );
		return true;
	}
	
	/* (non-Javadoc)
	 * @see ensemble.EventServer#init()
	 */
	@Override
	public boolean init ( ) 
	{
		System.out.println ( "Sound_Event_Server: Initialized." );
		world = ( ES_World ) envAgent.getWorld ( );	
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
	public void processSense ( Event event ) 
	{
		System.out.println ( "Sound_Event_Server: Note Emitted." );
		int velocity = 100;
		int note = ( int ) event.objContent;
		events.add ( event );
		
		channel.noteOn( note, velocity );		
	}
}
