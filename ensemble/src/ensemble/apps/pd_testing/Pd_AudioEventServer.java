package ensemble.apps.pd_testing;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
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
	private CopyOnWriteArrayList< Pd_Audio_Buffer > events = new CopyOnWriteArrayList< Pd_Audio_Buffer > ( );
	private int current_sample = 0;

	/*
	 * Audio
	 */
	private SourceDataLine line;   // to play the sound	 
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
			line = ( SourceDataLine ) AudioSystem.getLine ( info );
			line.open ( format );
			line.flush ( );
		} 
		catch ( LineUnavailableException e ) 
		{
			e.printStackTrace();
		}
		line.start ( );
		return true;
	}
	@Override
	public boolean finit ( )
	{
        line.drain ( );
        line.stop ( );
		return true;
	}
	private byte[ ] add_buffers ( byte[ ] samples, Pd_Audio_Buffer old_buffer )
	{
		byte[ ] previous_samples = old_buffer.get_audio_samples ( );
		
		if ( samples.length >= previous_samples.length )
		{
			for ( int i = 0; i < samples.length; i++ )
			{
				if ( i < previous_samples.length )
				{
					samples[ i ] = ( byte ) ( samples[ i ] + previous_samples[ i ] );
				}
			}
			return samples;
		}
		else
		{
			for ( int i = 0; i < previous_samples.length; i++ )
			{
				if ( i < samples.length )
				{
					previous_samples[ i ] = ( byte ) ( previous_samples[ i ] + samples[ i ] );
				}
			}
			return previous_samples;
		}
	}
	private void add_new_buffer ( int instant, byte[ ] new_buffer )
	{
		events.set ( instant, new Pd_Audio_Buffer ( new_buffer, instant, "EVENT_SERVER" ) );
	}
	private void process_audio_buffer ( Pd_Audio_Buffer new_buffer, int instant )
	{
		Pd_Audio_Buffer event = new_buffer;
		byte[ ] samples = event.get_audio_samples ( );
		for ( int i = 0; i < samples.length; i++ )
		{	
			samples[ i ] = ( byte ) ( samples[ i ] / ( float ) agent_number );
		}
		if ( events.get( instant ) != null )
		{
			Pd_Audio_Buffer previous_event = events.get ( instant );
			events.set ( instant, new Pd_Audio_Buffer ( add_buffers ( samples, previous_event ), instant, "EVENT_SERVER" ) );
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
	protected void process ( )
	{
		byte[ ] samples;
		current_sample %= Pd_Constants.PD_EVENT_BUFFER_SIZE;
		if ( samples_per_instant.get( current_sample ) >=  agent_number )
		{
			samples = events.get ( current_sample ).get_audio_samples ( );
			line.write( samples, 0, samples.length );
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
