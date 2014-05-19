package ensemble.apps.pd_testing;

import java.util.concurrent.ConcurrentHashMap;

import ensemble.*;
import ensemble.apps.pd_testing.PdConstants;
import ensemble.clock.TimeUnit;
import ensemble.memory.*;

/*
 * This ensemble.Reasoning extension
 * uses a pure-data patch as a source
 * of audio samples and other data.
 * 
 * It is able to receive bangs, floats and
 * messages from patches.
 */
public class PdReasoning extends Reasoning
{
	private ConcurrentHashMap< String, PdActuator > actuators = new ConcurrentHashMap<String, PdActuator> ( );
	private ConcurrentHashMap< String, Memory > actuator_memories = new ConcurrentHashMap<String, Memory> ( );
	
	private ConcurrentHashMap< String, PdSensor > sensors = new ConcurrentHashMap<String, PdSensor> ( );
	private ConcurrentHashMap< String, Memory > sensor_memories = new ConcurrentHashMap<String, Memory> ( );
	
	private ConcurrentHashMap< String, PdEvent > senses = new ConcurrentHashMap< String, PdEvent > ( );
    
	private String subpatch;
	protected String agent_name;
	private PdReceiver receiver;
	
	private void access_knowledge_base ( String source, Object[ ] arguments )
	{
		String value = getAgent ( ).getKB ( ).readFact ( ( String ) arguments[ 1 ] );
		if ( value != null )
		{
			String actuator_target = ( String ) arguments[ 0 ];
			String[ ] source_split = source.split ( PdConstants.SEPARATOR );
			Actuator actuator = actuators.get ( source_split[ 1 ] );

			PdMessage new_message = new PdMessage ( source, value, arguments );
			PdEvent pd_event = new PdEvent ( PdConstants.MESSAGE, new_message );
			try 
			{
				Float float_value = Float.parseFloat ( value );
				receiver.send_float ( actuator_target, float_value );
			}
			catch ( NumberFormatException e )
			{
				receiver.send_message ( new PdMessage ( actuator_target, value ) );
			}
			try
			{				
				actuator_memories.get ( source_split[ 1 ] ).writeMemory ( pd_event );			
				actuator.act ( );		
			}		
			catch ( MemoryException e ) 		
			{		
				e.printStackTrace ( );
			}
		}
	}
	private void add_to_fact ( Object[ ] arguments )
	{
		String value = getAgent ( ).getKB ( ).readFact ( ( String ) arguments[ 0 ] );
		if ( value != null )
		{
			try
			{
				Float old_value = Float.parseFloat ( value );
				Float new_value = old_value + ( Float ) arguments[ 1 ];
				getAgent ( ).getKB ( ).updateFact( ( String ) arguments[ 0 ], new_value.toString ( ) );
			}
			catch ( NumberFormatException e )
			{
				System.err.println ( "Fact \"" + ( String ) arguments[ 0 ] + "\" not a number." );
			}

		}
	}
	private void multiply_fact ( Object[ ] arguments )
	{
		String value = getAgent ( ).getKB ( ).readFact ( ( String ) arguments[ 0 ] );
		if ( value != null )
		{
			try
			{
				Float old_value = Float.parseFloat ( value );
				Float new_value = old_value * ( Float ) arguments[ 1 ];
				getAgent ( ).getKB ( ).updateFact( ( String ) arguments[ 0 ], new_value.toString ( ) );
			}
			catch ( NumberFormatException e )
			{
				System.err.println ( "Fact \"" + ( String ) arguments[ 0 ] + "\" not a number." );
			}

		}
	}
	private void create_memory_reader ( Object[ ] arguments )
	{
		String target_component = ( ( String ) arguments[ 0 ] );
		String[ ] split_target = target_component.split ( PdConstants.SEPARATOR );
		Float memory_offset = ( ( Float ) arguments[ 1 ] );
		if ( sensors.containsKey ( split_target[ 1 ] ) ||
			 actuators.containsKey ( split_target[ 1 ] ) )
		{
			PdMessage new_message = new PdMessage ( target_component, memory_offset.toString ( ), new Object[ 0 ] );
			PdEvent pd_event = new PdEvent ( PdConstants.READ_MEMORY_T, new_message );
			try
			{				
				Actuator actuator = actuators.get ( PdConstants.SELF_ACTUATOR );
				actuator_memories.get ( PdConstants.SELF_ACTUATOR ).writeMemory ( pd_event );			
				actuator.act ( );		
			}		
			catch ( MemoryException e ) 		
			{		
				e.printStackTrace ( );
			}
		}
	}
    private void process_messages ( PdMessage message )
    {
		String message_source = message.get_source ( );
		String symbol = message.get_symbol ( );
		Object[ ] arguments = message.get_arguments ( );
		if ( symbol.equals ( PdConstants.READ_FACT ) )
		{
			access_knowledge_base ( message_source, arguments );
		}
		else if ( symbol.equals ( PdConstants.READ_MEMORY_T ) )
		{
			create_memory_reader ( arguments );
		}
		else if ( symbol.equals ( PdConstants.ADD_TO_FACT ) )
		{
			add_to_fact ( arguments );
		}
		else if ( symbol.equals ( PdConstants.MULTIPLY_FACT ) )
		{
			multiply_fact ( arguments );
		}
		else if ( symbol.equals ( PdConstants.UPDATE_FACT ) )
		{
			getAgent ( ).getKB ( ).updateFact ( ( String ) arguments[ 1 ], ( String ) arguments[ 2 ] );
		}
    }
    private void process_audio_block ( PdAudioBlock audio_block )
    {
		String actuator_name = audio_block.get_source ( ).split ( PdConstants.SEPARATOR )[ 1 ];
		
		PdEvent pd_event = new PdEvent ( PdConstants.AUDIO_BLOCK, audio_block );
		PdActuator audio_actuator = actuators.get ( actuator_name );
		Memory actuator_memory = actuator_memories.get ( actuator_name );
		try 
		{
			actuator_memory.writeMemory ( pd_event );
		}
		catch ( MemoryException e ) 
		{
			e.printStackTrace ( );
		}
		audio_actuator.act ( );
    }
	@Override
	public boolean init ( ) 
	{
		agent_name = getAgent ( ).getAgentName ( );

		receiver = PdReceiver.get_instance ( );
		subpatch = parameters.get ( PdConstants.SUBPATCH ) + PdConstants.SEPARATOR;
		if ( subpatch == null )
		{
			System.err.println ( "Reasoning Error, no subpatch, this is embarassing!" );
			return false;
		}
		return true;
	}
	@Override
	public void process ( ) 
	{
		for ( String sense : senses.keySet ( ) )
		{
			PdEvent event = senses.get ( sense );
			String type = event.get_type ( );
			Object content = event.get_content ( );
			
			if ( type.equals( PdConstants.BANG ) )
			{
				
			}
			else if ( type.equals( PdConstants.MESSAGE ) )
			{
				process_messages ( ( PdMessage ) content );
			}
			else if ( type.equals ( PdConstants.AUDIO_BLOCK ) )
			{
				process_audio_block ( ( PdAudioBlock ) content );
			}
		}
		senses.clear ( );
	}
	@Override
	protected void eventHandlerRegistered ( EventHandler event_handler ) 
	{
		if ( event_handler instanceof Actuator )
		{
			actuators.put ( event_handler.getComponentName ( ), ( PdActuator ) event_handler );
			actuator_memories.put ( event_handler.getComponentName ( ), getAgent ( ).getKB ( ).getMemory ( event_handler.getComponentName ( ) ) );
		}
		else if ( event_handler instanceof Sensor )
		{
			event_handler.registerListener ( this );
			sensors.put ( event_handler.getComponentName ( ), ( PdSensor ) event_handler );
			sensor_memories.put ( event_handler.getComponentName ( ), getAgent ( ).getKB ( ).getMemory ( event_handler.getComponentName ( ) ) );
		}
	}
	public void newSense ( Sensor source, double instant, double duration ) throws Exception 
	{
		Memory source_memory = sensor_memories.get( source.getComponentName ( ) );
		PdEvent event = ( PdEvent ) source_memory.readMemory ( instant, duration, TimeUnit.SECONDS );
		senses.put ( source.getComponentName ( ), event );
	}
	/*DEBUG
	private void print_message ( PdMessage message )
	{
		String message_source = message.get_source ( );
		String symbol = message.get_symbol ( );
		Object[ ] arguments = message.get_arguments ( );
		System.err.println ( "Received Message: " );
		System.err.println ( "\tSource: " + message_source );
		System.err.println ( "\tSymbol: " + symbol );
		for ( Object argument : arguments )
		{
			try
			{
				System.err.println ( "\tArg: \"" + ( Float ) argument + "\"" );
			}
			catch ( ClassCastException e )
			{
				System.err.println ( "\tArg: \"" + ( String ) argument + "\"" );
			}
		}
	}*/
}
