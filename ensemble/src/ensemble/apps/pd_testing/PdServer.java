package ensemble.apps.pd_testing;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import ensemble.*;
import ensemble.apps.pd_testing.PdWorld;
import javax.sound.sampled.*;

import org.puredata.core.PdBase;

public class PdServer extends EventServer 
{
	PdWorld world;
	protected String agent_name;
	protected String agent_component_name;
	
	protected ArrayList < PdEvent > events;
	
	/*
	 * Audio
	 */
	int frames = PdConstants.DEFAULT_TICKS * PdConstants.PD_BLOCK_SIZE;
	
    
	short[ ] dummy_adc = new short[ frames * PdConstants.INPUT_CHANNELS ];
	short[ ] dummy_dac = new short[ frames * PdConstants.OUTPUT_CHANNELS ];
	private FloatBuffer float_buffer;
    private ByteBuffer byte_buffer;
    
    private PdProcessor pd_processor;
    private PdReceiver pd_receiver;
    
    private SourceDataLine line;
	
	
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
	    pd_processor = PdProcessor.get_instance ( );
	    pd_receiver = PdReceiver.get_instance ( );
	    
	    events = new ArrayList< PdEvent > ( );
		
	    float_buffer = FloatBuffer.allocate ( PdConstants.DEFAULT_SAMPLES_PER_BUFFER );
		byte_buffer = ByteBuffer.allocate ( float_buffer.capacity ( ) * 4 );
				
		world = ( PdWorld ) envAgent.getWorld ( );
		AudioFormat format = new AudioFormat ( ( float ) PdConstants.SAMPLE_RATE, PdConstants.BITS_PER_SAMPLE, PdConstants.OUTPUT_CHANNELS, true, true );
		DataLine.Info info = new DataLine.Info ( SourceDataLine.class, format );
		try 
		{
			line = ( SourceDataLine ) AudioSystem.getLine ( info );
			line.open ( format, PdConstants.DEFAULT_SAMPLES_PER_BUFFER * 8 );
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
	protected void new_audio_block_event ( String target, PdAudioBlock audio_block )
	{
		String[ ] event_target = target.split ( PdConstants.SEPARATOR );
		
		PdEvent pd_event = new PdEvent ( PdConstants.AUDIO_BLOCK, audio_block );
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
				new_message_event ( split_source[ 0 ] + PdConstants.SEPARATOR + PdConstants.SELF_SENSOR, message );
			}
			else if ( targeted_sensor != null )
			{
				new_message_event ( actuator_target, message );
				PdMessage sensor_message = new PdMessage ( actuator_target, symbol, arguments );
				pd_receiver.send_message ( sensor_message );
			}
			else if ( actuator_target.equals ( PdConstants.GLOBAL_KEY ) )
			{
				for ( String sensor : sensors.keySet ( ) )
				{
					if ( ! ( sensor.split ( PdConstants.SEPARATOR )[ 1 ].equals ( PdConstants.SELF_SENSOR ) ) )
					{
						new_message_event ( sensor, message );						
						PdMessage sensor_message = new PdMessage ( sensor, symbol, arguments );
						pd_receiver.send_message ( sensor_message );
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
				pd_receiver.send_bang ( actuator_target );
			}
			else if ( actuator_target.equals ( PdConstants.GLOBAL_KEY ) )
			{
				for ( String sensor : sensors.keySet ( ) )
				{
					if ( ! ( sensor.split ( PdConstants.SEPARATOR )[ 1 ].equals( PdConstants.SELF_SENSOR ) ) )
					{
						new_bang_event ( sensor, bang );
						pd_receiver.send_bang ( sensor );
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
				pd_receiver.send_float ( actuator_target, value.get_value ( ) );
			}
			else if ( actuator_target.equals( PdConstants.GLOBAL_KEY ) )
			{
				for ( String sensor : sensors.keySet ( ) )
				{
					if ( ! ( sensor.split ( PdConstants.SEPARATOR )[ 1 ].equals( PdConstants.SELF_SENSOR ) ) )
					{
						new_float_event ( sensor, value );
						pd_receiver.send_float ( sensor, value.get_value ( ) );
					}
				}
			}
		}
	}
	protected void process_sensor_block ( PdAudioBlock audio_block )
	{
		String[ ] source = audio_block.get_source ( ).split( PdConstants.SEPARATOR );
		
		for ( String sensor : pd_receiver.get_audio_sensors ( ) )
		{
			if ( ! ( sensor.split ( PdConstants.SEPARATOR )[ 0 ].equals( source[ 0 ] ) ) )
			{
				new_audio_block_event ( sensor, audio_block );
			}
		}
	}
	protected void process_actuator_block ( PdAudioBlock audio_block )
	{
		String[ ] source = audio_block.get_source ( ).split( PdConstants.SEPARATOR );
		new_audio_block_event ( source[ 0 ] + PdConstants.SEPARATOR + PdConstants.SELF_SENSOR, audio_block );
	}
	protected void process ( )
	{
		for ( int i = 0; i < events.size ( ); i++ )
		{
			PdEvent event = events.get ( i );
			String type = event.get_type ( );
			if ( type.equals( PdConstants.BANG ) )
			{
				pd_receiver.send_bang( ( String ) event.get_content ( ) );
			}
			else if ( type.equals ( PdConstants.MESSAGE ) )
			{
				PdMessage message = ( PdMessage ) event.get_content ( );
				pd_receiver.send_message ( message );
			}
			else if ( type.equals ( PdConstants.FLOAT ) )
			{
				PdFloat new_float = ( PdFloat ) event.get_content ( );
				pd_receiver.send_float ( new_float.get_source ( ), new_float.get_value ( )  );
			}
			else if ( type.equals ( PdConstants.AUDIO_BLOCK ) )
			{
				PdAudioBlock audio_block = ( PdAudioBlock ) event.get_content ( );
				//float[ ] samples = audio_block.get_samples ( );
				
				/*System.err.println ( "Byte buffer: " + byte_buffer.array ( ).length );
				System.err.println ( "Bytebuffer: " + byte_buffer.capacity ( ) );
				System.err.println ( "Float buffer: " + samples.length );
				System.err.println ( "Floatbuffer: " + float_buffer.capacity ( ) );*/

				float[ ] samples = audio_block.get_samples ( );
				FloatBuffer floatBuffer = FloatBuffer.wrap(samples);
				ByteBuffer byteBuffer = ByteBuffer.allocate(floatBuffer.capacity() * 4);
				byteBuffer.asFloatBuffer().put(samples);
				byte[ ] bytes = new byte [ samples.length * 4 ]; 
				byteBuffer.get ( bytes );				
				//line.write ( bytes, 0, bytes.length );
				
				System.err.println ( "Server received audio buffer" );
				System.err.println ( "\tSamples: \n[" );
				for ( float sample : samples )
				{
					System.err.println ( sample );
				}
				System.err.println ( "]" );
				/* Tocar? */
			}
		}
		events.clear ( );
		
		pd_processor.process_ticks ( PdConstants.DEFAULT_TICKS, dummy_adc, dummy_dac );
		pd_receiver.fetch_pd_messages ( );

		for ( PdMessage message : pd_receiver.get_messages ( ) )
		{
			process_message ( message );
		}
		for ( String bang : pd_receiver.get_bangs ( ) )
		{
			process_bang ( bang );
		}
		for ( PdFloat pd_float : pd_receiver.get_floats ( ) )
		{
			process_float ( pd_float );
		}
		/* Loop for audio from actuators: */
		for ( String actuator : pd_receiver.get_audio_actuators ( ) )
		{
			float[ ] samples = new float[ PdConstants.PD_BLOCK_SIZE ];
			PdBase.readArray ( samples, 0 , actuator, 0, PdConstants.PD_BLOCK_SIZE );
			process_actuator_block ( new PdAudioBlock ( samples, actuator ) );
		}
		/* Loop for audio TO audio sensors:
		for ( String actuator : pd_receiver.get_audio_actuators ( ) )
		{
			float[ ] samples = new float[ PdConstants.PD_BLOCK_SIZE ];
			PdBase.readArray ( samples, 0 , actuator, 0, PdConstants.PD_BLOCK_SIZE );
			process_audio_block ( new PdAudioBlock ( samples, actuator ) );
		}*/
		pd_receiver.start_new_cycle ( );	
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
		System.err.println ( "Registered " + agentName + PdConstants.SEPARATOR + actuatorName );
		String sub_type = userParam.get ( PdConstants.SUB_TYPE );
		if ( ! ( actuatorName.equals( PdConstants.SELF_ACTUATOR ) ) && sub_type.equals ( PdConstants.AUDIO_EVENT ) )
		{
			System.err.println ( "Registered " + agentName + PdConstants.SEPARATOR + actuatorName + " into audio actuators list." );
			pd_receiver.register_audio_actuator ( agentName + PdConstants.SEPARATOR + actuatorName );
		}
		return userParam;
	}
	protected Parameters sensorRegistered ( String agentName, String sensorName, Parameters userParam ) throws Exception 
	{
		System.err.println ( "Registered " + agentName + PdConstants.SEPARATOR + sensorName );
		String sub_type = userParam.get ( PdConstants.SUB_TYPE );
		if ( ! ( sensorName.equals( PdConstants.SELF_SENSOR ) ) && sub_type.equals ( PdConstants.AUDIO_EVENT ) )
		{
			System.err.println ( "Registered " + agentName + PdConstants.SEPARATOR + sensorName + " into audio sensors list." );
			pd_receiver.register_audio_sensor ( agentName + PdConstants.SEPARATOR + sensorName );
		}
		return userParam;
	}
}
