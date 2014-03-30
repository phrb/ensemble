package ensemble.apps.pd_testing;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import ensemble.*;
import ensemble.apps.pd_testing.PdWorld;
import javax.sound.sampled.*;

import org.puredata.core.PdBase;

public class PdEventServer extends EventServer 
{
	PdWorld world;
	protected String agent_name;
	protected String agent_component_name;
	
	protected ArrayList < PdEvent > events;
	
	/*
	 * Audio
	 */
	int frames = PdBase.blockSize() * PdConstants.DEFAULT_TICKS;
	
    private byte[ ] output_samples;
    private short[ ] input_samples;
    private short[ ] short_samples;
    private PdAudioServer sampler;
    private PdReceiver receiver;
    
    SourceDataLine line;
    
	ByteBuffer byte_buffer;
	ShortBuffer short_buffer;
	
	@Override
	public boolean configure ( )
	{
		setEventType ( PdConstants.DEFAULT_EVENT_TYPE );
		return true;
	}
	
	/* (non-Javadoc)
	 * @see ensemble.EventServer#init()
	 */
	@Override
	public boolean init ( ) 
	{
	    output_samples = new byte[ frames * PdConstants.OUTPUT_CHANNELS * PdConstants.BYTES_PER_SAMPLE ];
	    input_samples = new short[ frames * PdConstants.INPUT_CHANNELS ];
	    short_samples = new short[ frames * PdConstants.OUTPUT_CHANNELS ];
	    
	    sampler = PdAudioServer.get_instance ( );
	    receiver = PdReceiver.get_instance ( );
	    
	    events = new ArrayList< PdEvent > ( );
	    
		byte_buffer = ByteBuffer.wrap ( output_samples );
		short_buffer = byte_buffer.asShortBuffer ( );
		
		world = ( PdWorld ) envAgent.getWorld ( );
		AudioFormat format = new AudioFormat ( ( float ) PdConstants.SAMPLE_RATE, PdConstants.BITS_PER_SAMPLE, PdConstants.OUTPUT_CHANNELS, true, true );
		DataLine.Info info = new DataLine.Info ( SourceDataLine.class, format );
		try 
		{
			line = ( SourceDataLine ) AudioSystem.getLine ( info );
			line.open ( format, PdBase.blockSize ( ) * PdConstants.DEFAULT_TICKS );
			line.start ( );
		}
		catch (LineUnavailableException e) 
		{
			e.printStackTrace();
		}
		return true;
	}
	@Override
	public boolean finit ( )
	{
	    line.drain ( );
	    line.stop ( );
		return true;
	}
	protected void new_message_event ( String target, PdMessage new_message )
	{
		String[ ] event_target = target.split ( PdConstants.SEPARATOR );
		
		PdEvent pd_event = new PdEvent ( PdConstants.MESSAGE, new_message );
		Event new_event = new Event ( );
		new_event.objContent = pd_event;
		
		addOutputEvent ( event_target[ 0 ], event_target[ 1 ], new_event );
	}
	protected void new_bang_event ( String target, String bang )
	{
		String[ ] event_target = target.split ( PdConstants.SEPARATOR );
		
		PdEvent pd_event = new PdEvent ( PdConstants.BANG, bang );
		Event new_event = new Event ( );
		new_event.objContent = pd_event;
		
		addOutputEvent ( event_target[ 0 ], event_target[ 1 ], new_event );
	}
	protected void new_float_event ( String target, PdFloat value )
	{
		String[ ] event_target = target.split ( PdConstants.SEPARATOR );
		
		PdEvent pd_event = new PdEvent ( PdConstants.FLOAT, value );
		Event new_event = new Event ( );
		new_event.objContent = pd_event;
		
		addOutputEvent ( event_target[ 0 ], event_target[ 1 ], new_event );
	}
	protected void process_message ( PdMessage message )
	{
		String source = message.get_source ( );
		String[ ] split_source = source.split ( PdConstants.SEPARATOR );
		String symbol = message.get_symbol ( );
		Object[ ] arguments = message.get_arguments ( );
		if ( actuators.containsKey ( source ) )
		{
			String actuator_target = ( String ) arguments[ 0 ];
			Parameters targeted_sensor = sensors.get ( actuator_target );
			if ( split_source[ 1 ].equals ( PdConstants.SELF_ACTUATOR ) )
			{
				System.err.println ( "MESSAGE TO SENSOR: " + actuator_target + " NULITY OF SENSOR: " + targeted_sensor +
						"\n Got to senseself though...");
				new_message_event ( split_source[ 0 ] + PdConstants.SEPARATOR + PdConstants.SELF_SENSOR, message );
			}
			else if ( targeted_sensor != null )
			{
				System.err.println ( "MESSAGE TO SENSOR: " + actuator_target + " NULITY OF SENSOR: " + targeted_sensor +
						"\n Got to directed message.");
				new_message_event ( actuator_target, message );
				PdMessage sensor_message = new PdMessage ( actuator_target, symbol, arguments );
				receiver.send_message ( sensor_message );
			}
			else if ( actuator_target.equals ( PdConstants.GLOBAL_KEY ) )
			{
				System.err.println ( "MESSAGE TO SENSOR: " + actuator_target + " NULITY OF SENSOR: " + targeted_sensor +
						"\n Got to friggin global...");
				for ( String sensor : sensors.keySet ( ) )
				{
					if ( ! ( sensor.split ( PdConstants.SEPARATOR )[ 1 ].equals ( PdConstants.SELF_SENSOR ) ) )
					{
						new_message_event ( sensor, message );						
						PdMessage sensor_message = new PdMessage ( sensor, symbol, arguments );
						receiver.send_message ( sensor_message );
					}
				}
			}
			else
			{
				System.err.println ( "Error: Message with no destination!" );
			}
		}
	}
	protected void process_bang ( String bang )
	{
		if ( actuators.containsKey ( bang ) )
		{
			String[ ] source = bang.split( PdConstants.SEPARATOR );
			String actuator_target = actuators.get ( bang ).get ( PdConstants.SCOPE );
			Parameters targeted_sensor = sensors.get ( actuator_target );
			if ( source[ 1 ].equals ( PdConstants.SELF_ACTUATOR ) )
			{
				new_bang_event ( source[ 0 ] + PdConstants.SEPARATOR + PdConstants.SELF_SENSOR, bang );
			}
			else if ( targeted_sensor != null )
			{
				new_bang_event ( actuator_target, bang );
				receiver.send_bang ( actuator_target );
			}
			else if ( actuator_target.equals ( PdConstants.GLOBAL_KEY ) )
			{
				for ( String sensor : sensors.keySet ( ) )
				{
					if ( ! ( sensor.split ( PdConstants.SEPARATOR )[ 1 ].equals( PdConstants.SELF_SENSOR ) ) )
					{
						new_bang_event ( sensor, bang );
						receiver.send_bang ( sensor );
					}
				}
			}
			else
			{
				System.err.println ( "Error: Bang to actuator with no destination!" );
			}
		}
	}
	protected void process_float ( PdFloat value )
	{
		String source = value.get_source ( );
		String[ ] split_source = source.split( PdConstants.SEPARATOR );
		if ( actuators.containsKey ( source ) )
		{
			String actuator_target = actuators.get ( source ).get ( PdConstants.SCOPE );
			Parameters targeted_sensor = sensors.get ( actuator_target );
			if ( split_source[ 1 ].equals ( PdConstants.SELF_ACTUATOR ) )
			{
				new_float_event ( split_source[ 0 ] + PdConstants.SEPARATOR + PdConstants.SELF_SENSOR, value );
			}
			else if ( targeted_sensor != null )
			{
				new_float_event ( actuator_target, value );
				receiver.send_float ( actuator_target, value.get_value ( ) );
			}
			else if ( actuator_target.equals( PdConstants.GLOBAL_KEY ) )
			{
				for ( String sensor : sensors.keySet ( ) )
				{
					if ( ! ( sensor.split ( PdConstants.SEPARATOR )[ 1 ].equals( PdConstants.SELF_SENSOR ) ) )
					{
						new_float_event ( sensor, value );
						receiver.send_float ( sensor, value.get_value ( ) );
					}
				}
			}
		}
	}
	protected void process ( )
	{
		for ( int i = 0; i < events.size ( ); i++ )
		{
			PdEvent event = events.get ( i );
			String type = event.get_type ( );
			if ( type.equals( PdConstants.BANG ) )
			{
				receiver.send_bang( ( String ) event.get_content ( ) );
			}
			else if ( type.equals ( PdConstants.MESSAGE ) )
			{
				PdMessage message = ( PdMessage ) event.get_content ( );
				System.err.println ( "========================================================" );
				System.err.println ( "Processing Message: \nFrom: " + message.get_source ( ) +
						" Symbol: " + message.get_symbol ( ) );
				for ( Object object : message.get_arguments ( ) )
				{
					System.err.println ( "Arg: " + object );
				}
				System.err.println ( "========================================================" );
				receiver.send_message ( message );
			}
			else if ( type.equals ( PdConstants.FLOAT ) )
			{
				PdFloat new_float = ( PdFloat ) event.get_content ( );
				receiver.send_float ( new_float.get_source ( ), new_float.get_value ( )  );
			}
		}
		events.clear ( );

		sampler.process_ticks ( PdConstants.DEFAULT_TICKS, input_samples, short_samples );
		short_buffer.rewind ( );
		short_buffer.put( short_samples );
		line.write ( output_samples, 0, output_samples.length );

		receiver.fetch_pd_messages ( );

		for ( PdMessage message : receiver.get_messages ( ) )
		{
			System.err.println ( "========================================================" );
			System.err.println ( "Processing Message: \nFrom: " + message.get_source ( ) +
					" Symbol: " + message.get_symbol ( ) );
			for ( Object object : message.get_arguments ( ) )
			{
				System.err.println ( "Arg: " + object );
			}
			System.err.println ( "========================================================" );
			process_message ( message );
		}
		for ( String bang : receiver.get_bangs ( ) )
		{
			process_bang ( bang );
		}
		for ( PdFloat pd_float : receiver.get_floats ( ) )
		{
			process_float ( pd_float );
		}
		receiver.start_new_cycle ( );
		act ( );
	}
	@Override
	public void processSense ( Event new_event ) 
	{
		PdEvent event = ( PdEvent ) new_event.objContent;
		events.add ( event );
	}
	@Override
	protected Parameters actuatorRegistered ( String agentName, String actuatorName, Parameters userParam ) throws Exception 
	{
		System.err.println ( "PdEventServer Says: " + agentName + " registered actuator: " + actuatorName );
		for ( String parameter : userParam.keySet ( ) )
		{
			System.err.println ( parameter + ": " + userParam.get ( parameter ) );
		}
		return userParam;
	}
	protected Parameters sensorRegistered ( String agentName, String sensorName, Parameters userParam ) throws Exception 
	{
		System.err.println ( "PdEventServer Says: " + agentName + " registered sensor: " + sensorName );
		for ( String parameter : userParam.keySet ( ) )
		{
			System.err.println ( parameter + ": " + userParam.get ( parameter ) );
		}
		return userParam;
	}
}
