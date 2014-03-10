package ensemble.apps.pd_testing;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

import ensemble.*;
import ensemble.apps.pd_testing.Pd_World;
import javax.sound.sampled.*;

public class Pd_AudioEventServer extends EventServer 
{
	Pd_World world;
	protected String agent_name;
	protected String agent_component_name;
	private int[ ] samples_per_instant = new int[ Pd_Constants.PD_EVENT_BUFFER_SIZE ];
	private int agent_number;
	CopyOnWriteArrayList< Pd_Audio_Buffer > events = new CopyOnWriteArrayList< Pd_Audio_Buffer > ( );
	private int current_sample = 0;

	/*
	 * Audio
	 */
	private SourceDataLine line;   // to play the sound
	
	ByteArrayOutputStream byte_stream = new ByteArrayOutputStream ( );
	DataOutputStream data_stream = new DataOutputStream ( byte_stream );

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
			samples_per_instant[ i ] = 0;
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
	private float[ ] add_buffers ( float[ ] samples, Pd_Audio_Buffer old_buffer )
	{
		float[ ] previous_samples = old_buffer.get_audio_samples ( );
		
		if ( samples.length >= previous_samples.length )
		{
			for ( int i = 0; i < samples.length; i++ )
			{
				if ( i < previous_samples.length )
				{
					samples[ i ] = samples[ i ] + previous_samples[ i ];
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
					previous_samples[ i ] = previous_samples[ i ] + samples[ i ];
				}
			}
			return previous_samples;
		}
	}
	private void add_new_buffer ( int instant, float[ ] new_buffer )
	{
		events.set ( instant, new Pd_Audio_Buffer ( new_buffer, instant, "EVENT_SERVER" ) );
	}
	private void process_audio_buffer ( Pd_Audio_Buffer new_buffer, int instant )
	{
		Pd_Audio_Buffer event = new_buffer;
		float[ ] samples = event.get_audio_samples ( );
		for ( int i = 0; i < samples.length; i++ )
		{	
			samples[ i ] = ( samples[ i ] / agent_number ); 
		}
		if ( events.get( instant ) != null )
		{
			Pd_Audio_Buffer previous_event = events.get ( instant );
			events.set ( instant, new Pd_Audio_Buffer ( add_buffers ( samples, previous_event ), instant, "EVENT_SERVER" ) );
			samples_per_instant[ instant ] += 1;
		}
		else
		{
			add_new_buffer ( instant, samples );
			samples_per_instant[ instant ] += 1;
		}
	}
	protected void process ( )
	{
		float[ ] samples;
		byte[ ] byte_samples;
		current_sample %= Pd_Constants.PD_EVENT_BUFFER_SIZE;
		if ( samples_per_instant [ current_sample ] >=  agent_number )
		{
			samples = events.get ( current_sample ).get_audio_samples ( );
			for ( Float sample : samples )
			{
				try 
				{
					data_stream.writeFloat( sample );
				} 
				catch ( IOException e ) 
				{
					e.printStackTrace ( );
				}
			}
			byte_samples = byte_stream.toByteArray ( );
			System.err.println ( "Bytes produced: " + byte_samples.length );
			System.err.println ( "Line Buffer Available: " + line.available ( ) );
			System.err.println ( "Line Buffer Size: " + line.getBufferSize ( ) );
			System.err.println ( "Float Samples: " + samples.length );
			line.write( byte_samples, 0, byte_samples.length );
			try 
			{
				data_stream.flush ( );
				byte_stream.flush ( );
				byte_stream.reset ( );
			}
			catch (IOException e) 
			{
				e.printStackTrace ( );
			}
			
			samples_per_instant [ current_sample ] = 0;
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
