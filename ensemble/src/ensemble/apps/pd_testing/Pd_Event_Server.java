package ensemble.apps.pd_testing;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import ensemble.*;
import ensemble.apps.pd_testing.Pd_World;
import javax.sound.sampled.*;

import org.puredata.core.PdBase;

public class Pd_Event_Server extends EventServer 
{
	Pd_World world;
	protected String agent_name;
	protected String agent_component_name;
	
	protected ArrayList < Pd_Event > events;
	
	/*
	 * Audio
	 */
	int frames = PdBase.blockSize() * Pd_Constants.DEFAULT_TICKS;
	
    private byte[ ] output_samples;
    private short[ ] input_samples;
    private short[ ] short_samples;
    private Pd_Audio_Server sampler;
    private Pd_Receiver receiver;
    
    SourceDataLine line;
    
	ByteBuffer byte_buffer;
	ShortBuffer short_buffer;
	
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
	    output_samples = new byte[ frames * Pd_Constants.OUTPUT_CHANNELS * Pd_Constants.BYTES_PER_SAMPLE ];
	    input_samples = new short[ frames * Pd_Constants.INPUT_CHANNELS ];
	    short_samples = new short[ frames * Pd_Constants.OUTPUT_CHANNELS ];
	    
	    sampler = Pd_Audio_Server.get_instance ( );
	    receiver = Pd_Receiver.get_instance ( );
	    
	    events = new ArrayList< Pd_Event > ( );
	    
		byte_buffer = ByteBuffer.wrap ( output_samples );
		short_buffer = byte_buffer.asShortBuffer ( );
		
		world = ( Pd_World ) envAgent.getWorld ( );
		AudioFormat format = new AudioFormat ( ( float ) Pd_Constants.SAMPLE_RATE, Pd_Constants.BITS_PER_SAMPLE, Pd_Constants.OUTPUT_CHANNELS, true, true );
		DataLine.Info info = new DataLine.Info ( SourceDataLine.class, format );
		try 
		{
			line = ( SourceDataLine ) AudioSystem.getLine ( info );
			line.open ( format, PdBase.blockSize ( ) * Pd_Constants.DEFAULT_TICKS );
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
	protected void new_message_event ( String target, Pd_Message new_message )
	{
		String[ ] event_target = target.split ( Pd_Constants.SEPARATOR );
		
		Pd_Event pd_event = new Pd_Event ( Pd_Constants.MESSAGE, new_message );
		Event new_event = new Event ( );
		new_event.objContent = pd_event;
		
		addOutputEvent ( event_target[ 0 ], event_target[ 1 ], new_event );
	}
	protected void new_bang_event ( String target, String bang )
	{
		String[ ] event_target = target.split ( Pd_Constants.SEPARATOR );
		
		Pd_Event pd_event = new Pd_Event ( Pd_Constants.BANG, bang );
		Event new_event = new Event ( );
		new_event.objContent = pd_event;
		
		addOutputEvent ( event_target[ 0 ], event_target[ 1 ], new_event );
	}
	protected void new_float_event ( String target, Pd_Float value )
	{
		String[ ] event_target = target.split ( Pd_Constants.SEPARATOR );
		
		Pd_Event pd_event = new Pd_Event ( Pd_Constants.FLOAT, value );
		Event new_event = new Event ( );
		new_event.objContent = pd_event;
		
		addOutputEvent ( event_target[ 0 ], event_target[ 1 ], new_event );
	}
	protected void process_message ( Pd_Message message )
	{
		String[ ] source = message.get_source ( ).split ( Pd_Constants.SEPARATOR );
		String symbol = message.get_symbol ( );
		Object[ ] arguments = message.get_arguments ( );
		if ( actuators.containsKey ( source[ 0 ] + Pd_Constants.SEPARATOR + source[ 1 ] ) )
		{
			String actuator_target = ( String ) arguments[ 0 ];
			Parameters targeted_sensor = sensors.get ( actuator_target );
			if ( source[ 1 ].equals ( Pd_Constants.SELF_ACTUATOR ) )
			{
				new_message_event ( source[ 0 ] + Pd_Constants.SEPARATOR + Pd_Constants.SELF_SENSOR, message );
			}
			else if ( targeted_sensor != null )
			{
				new_message_event ( actuator_target, message );
				Pd_Message sensor_message = new Pd_Message ( actuator_target, symbol, 
						Arrays.copyOf ( arguments, arguments.length - 1 ) );
				receiver.send_message ( sensor_message );
			}
			else if ( actuator_target.equals ( Pd_Constants.GLOBAL_KEY ) )
			{
				for ( String sensor : sensors.keySet ( ) )
				{
					if ( ! ( sensor.split ( Pd_Constants.SEPARATOR )[ 1 ].equals ( Pd_Constants.SELF_SENSOR ) ) )
					{
						new_message_event ( sensor, message );						
						Pd_Message sensor_message = new Pd_Message ( sensor, symbol, 
								Arrays.copyOf ( arguments, arguments.length - 1 ) );
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
			String[ ] source = bang.split( Pd_Constants.SEPARATOR );
			String actuator_target = actuators.get ( bang ).get ( Pd_Constants.SCOPE );
			Parameters targeted_sensor = sensors.get ( actuator_target );
			if ( source[ 1 ].equals ( Pd_Constants.SELF_ACTUATOR ) )
			{
				new_bang_event ( source[ 0 ] + Pd_Constants.SEPARATOR + Pd_Constants.SELF_SENSOR, bang );
			}
			else if ( targeted_sensor != null )
			{
				new_bang_event ( actuator_target, bang );
				receiver.send_bang ( actuator_target );
			}
			else if ( actuator_target.equals ( Pd_Constants.GLOBAL_KEY ) )
			{
				for ( String sensor : sensors.keySet ( ) )
				{
					if ( ! ( sensor.split ( Pd_Constants.SEPARATOR )[ 1 ].equals( Pd_Constants.SELF_SENSOR ) ) )
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
	protected void process_float ( Pd_Float value )
	{
		String source = value.get_source ( );
		String[ ] split_source = source.split( Pd_Constants.SEPARATOR );
		if ( actuators.containsKey ( source ) )
		{
			String actuator_target = actuators.get ( source ).get ( Pd_Constants.SCOPE );
			Parameters targeted_sensor = sensors.get ( actuator_target );
			if ( split_source[ 1 ].equals ( Pd_Constants.SELF_ACTUATOR ) )
			{
				new_float_event ( split_source[ 0 ] + Pd_Constants.SEPARATOR + Pd_Constants.SELF_SENSOR, value );
			}
			else if ( targeted_sensor != null )
			{
				new_float_event ( actuator_target, value );
				receiver.send_float ( actuator_target, value.get_value ( ) );
			}
			else if ( actuator_target.equals( Pd_Constants.GLOBAL_KEY ) )
			{
				for ( String sensor : sensors.keySet ( ) )
				{
					if ( ! ( sensor.split ( Pd_Constants.SEPARATOR )[ 1 ].equals( Pd_Constants.SELF_SENSOR ) ) )
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
			Pd_Event event = events.get ( i );
			String type = event.get_type ( );
			if ( type.equals( Pd_Constants.BANG ) )
			{
				receiver.send_bang( ( String ) event.get_content ( ) );
			}
			else if ( type.equals ( Pd_Constants.MESSAGE ) )
			{
				Pd_Message message = ( Pd_Message ) event.get_content ( );
				receiver.send_message ( message );
			}
			else if ( type.equals ( Pd_Constants.FLOAT ) )
			{
				Pd_Float new_float = ( Pd_Float ) event.get_content ( );
				receiver.send_float ( new_float.get_source ( ), new_float.get_value ( )  );
			}
		}
		events.clear ( );

		sampler.process_ticks ( Pd_Constants.DEFAULT_TICKS, input_samples, short_samples );
		short_buffer.rewind ( );
		short_buffer.put( short_samples );
		line.write ( output_samples, 0, output_samples.length );

		receiver.fetch_pd_messages ( );

		for ( Pd_Message message : receiver.get_messages ( ) )
		{
			process_message ( message );
		}
		for ( String bang : receiver.get_bangs ( ) )
		{
			process_bang ( bang );
		}
		for ( Pd_Float pd_float : receiver.get_floats ( ) )
		{
			process_float ( pd_float );
		}
		receiver.start_new_cycle ( );
		act ( );
	}
	@Override
	public void processSense ( Event new_event ) 
	{
		Pd_Event event = ( Pd_Event ) new_event.objContent;
		events.add ( event );
	}
	@Override
	protected Parameters actuatorRegistered ( String agentName, String actuatorName, Parameters userParam ) throws Exception 
	{
		System.err.println ( "Pd_Event_Server Says: " + agentName + " registered actuator: " + actuatorName );
		for ( String parameter : userParam.keySet ( ) )
		{
			System.err.println ( parameter + ": " + userParam.get ( parameter ) );
		}
		return userParam;
	}
	protected Parameters sensorRegistered ( String agentName, String sensorName, Parameters userParam ) throws Exception 
	{
		System.err.println ( "Pd_Event_Server Says: " + agentName + " registered sensor: " + sensorName );
		for ( String parameter : userParam.keySet ( ) )
		{
			System.err.println ( parameter + ": " + userParam.get ( parameter ) );
		}
		return userParam;
	}
}
