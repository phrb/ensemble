package ensemble.apps.pd_testing;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import ensemble.*;
import ensemble.apps.pd_testing.Pd_World;
import javax.sound.sampled.*;

import org.puredata.core.PdBase;

public class Pd_AudioEventServer extends EventServer 
{
	Pd_World world;
	protected String agent_name;
	protected String agent_component_name;
	private CopyOnWriteArrayList< Integer > samples_per_instant = new CopyOnWriteArrayList< Integer >( );
	/*Pd_Constants.PD_EVENT_BUFFER_SIZE*/
	private int agent_number;
	private CopyOnWriteArrayList< ArrayList < Pd_Audio_Buffer > > events = new CopyOnWriteArrayList< ArrayList < Pd_Audio_Buffer > > ( );
	private int current_sample = 0;

	/*
	 * Audio
	 */
	private CopyOnWriteArrayList < SourceDataLine > lines = new CopyOnWriteArrayList < SourceDataLine > ( );	 
	@Override
	public boolean configure ( )
	{
		setEventType ( Pd_Constants.DEFAULT_EVENT_TYPE );
		return true;
	}
	
	/* (non-Javadoc)
	 * @see ensemble.EventServer#init()
	 */
	@Override
	public boolean init ( ) 
	{
		world = ( Pd_World ) envAgent.getWorld ( );
		
		for ( int i = 0; i < Pd_Constants.PD_EVENT_BUFFER_SIZE; i++ )
		{
			samples_per_instant.add( 0 );
		}
		for ( int i = 0; i < Pd_Constants.PD_EVENT_BUFFER_SIZE; i++ )
		{
			events.add ( i, null );
		}
		agent_number = ( int ) Float.parseFloat ( envAgent.getParameter ( Pd_Constants.AGENT_NUMBER_ARGUMENT ) );
		AudioFormat format = new AudioFormat ( ( float ) Pd_Constants.SAMPLE_RATE, Pd_Constants.BITS_PER_SAMPLE, Pd_Constants.OUTPUT_CHANNELS, true, true );
		DataLine.Info info = new DataLine.Info ( SourceDataLine.class, format );
		try 
		{
			for ( int i = 0; i < agent_number; i++ )
			{
				SourceDataLine line = ( SourceDataLine ) AudioSystem.getLine ( info );
				line.open ( format, PdBase.blockSize ( ) * Pd_Constants.DEFAULT_TICKS );
				line.flush ( );
				line.start ( );
				lines.add ( line );	
			}
		} 
		catch ( LineUnavailableException e ) 
		{
			e.printStackTrace();
		}
		return true;
	}
	@Override
	public boolean finit ( )
	{
		for ( SourceDataLine line : lines )
		{
	        line.drain ( );
	        line.stop ( );
		}
		return true;
	}
	private void add_new_buffer ( int instant, byte[ ] new_buffer )
	{
		ArrayList < Pd_Audio_Buffer > buffer_list = new ArrayList< Pd_Audio_Buffer > ( );
		buffer_list.add ( new Pd_Audio_Buffer ( new_buffer, instant, "EVENT_SERVER" ) );
		events.set ( instant, buffer_list );
	}
	private void process_audio_buffer ( Pd_Audio_Buffer new_buffer, int instant )
	{
		Pd_Audio_Buffer event = new_buffer;
		byte[ ] samples = event.get_audio_samples ( );
		if ( events.get( instant ) != null )
		{
			events.get ( instant ).add ( new Pd_Audio_Buffer ( samples, instant, "EVENT_SERVER" ) );
			int old_samples = samples_per_instant.get ( instant );
			samples_per_instant.set ( instant, old_samples + 1 );
		}
		else
		{
			add_new_buffer ( instant, samples );
			int old_samples = samples_per_instant.get ( instant );
			samples_per_instant.set ( instant, old_samples + 1 );
		}
	}
	public void play_samples ( byte[ ] buffer, SourceDataLine line )
	{
		Pd_Runnable_Player play_samples_runner = new Pd_Runnable_Player ( ); 
		play_samples_runner.start ( buffer, line );
		Thread player_thread = new Thread ( play_samples_runner );
		player_thread.start ( );
	}
	protected void process ( )
	{
		byte[ ] samples;
		current_sample %= Pd_Constants.PD_EVENT_BUFFER_SIZE;
		if ( samples_per_instant.get( current_sample ) >=  agent_number )
		{
			ArrayList< Pd_Audio_Buffer > buffer_list = events.get ( current_sample );
			SourceDataLine line;
			for ( int i = 0; i < buffer_list.size ( ); i++ )
			{
				samples = buffer_list.get ( i ).get_audio_samples ( );
				line = lines.get( i );
				play_samples ( samples, line );
				//line.write( samples, 0, samples.length );
			}
			samples_per_instant.set ( current_sample, 0 );
			events.set ( current_sample, null );
			current_sample += 1;
		}
	}
	@Override
	public void processSense ( Event new_event ) 
	{
		for ( String sensor : sensors.keySet ( ) )
		{
			String[ ] target = sensor.split( ":" );
			if ( ! ( target[ 0 ].equals ( new_event.oriAgentName ) ) )
			{
				addOutputEvent ( target[ 0 ], target[ 1 ], new_event );				
			}
		}
		act ( );
		Pd_Audio_Buffer event = ( Pd_Audio_Buffer ) new_event.objContent;
		int instant = event.get_pd_time_tag ( ) % Pd_Constants.PD_EVENT_BUFFER_SIZE;
		process_audio_buffer ( event, instant );
	}
	@Override
	protected Parameters actuatorRegistered ( String agentName, String actuatorName, Parameters userParam ) throws Exception 
	{
		System.err.println ( "Pd_AudioEventServer Says: " + agentName + " registered: " + actuatorName );
		return userParam;
	}
	protected Parameters sensorRegistered ( String agentName, String sensorName, Parameters userParam ) throws Exception 
	{
		System.err.println ( "Pd_AudioEventServer Says: " + agentName + " registered: " + sensorName );
		return userParam;
	}
}
