package ensemble.apps.pd_testing;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import ensemble.*;
import ensemble.apps.pd_testing.Pd_Constants;
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
public class Pd_Reasoning extends Reasoning
{
	private ConcurrentHashMap< String, Pd_Actuator > actuators = new ConcurrentHashMap<String, Pd_Actuator> ( );
	private ConcurrentHashMap< String, Memory > actuator_memories = new ConcurrentHashMap<String, Memory> ( );
	
	private ConcurrentHashMap< String, Pd_Sensor > sensors = new ConcurrentHashMap<String, Pd_Sensor> ( );
	private ConcurrentHashMap< String, Memory > sensor_memories = new ConcurrentHashMap<String, Memory> ( );
	
	private ConcurrentHashMap< String, Pd_Event > senses = new ConcurrentHashMap< String, Pd_Event > ( );
    
	private String subpatch;
	private String agent_name;
	private Pd_Receiver receiver;
	
	private void access_knowledge_base ( String source, Object[ ] arguments )
	{
		String value = getAgent ( ).getKB ( ).readFact ( ( String ) arguments[ 0 ] );
		if ( value != null )
		{
			for ( Actuator actuator : actuators.values ( ) )
			{
				String[ ] target_agent = source.split ( Pd_Constants.SEPARATOR );
				String[ ] actuator_target = actuator.getParameter ( Pd_Constants.SCOPE ).split ( Pd_Constants.SEPARATOR );
				if ( actuator_target[ 0 ].equals ( target_agent[ 0 ] ) ||
						actuator_target[ 0 ].equals ( Pd_Constants.GLOBAL_KEY ) )
				{
					Pd_Message new_message = new Pd_Message ( source, value );
					Pd_Event pd_event = new Pd_Event ( Pd_Constants.MESSAGE, new_message );
					try 
					{
						Float float_value = Float.parseFloat ( value );
						receiver.send_float ( actuator.getParameter ( Pd_Constants.SCOPE ), float_value );
						actuator_memories.get ( actuator.getComponentName ( ) ).writeMemory ( pd_event );
						actuator.act ( );
					}
					catch ( MemoryException e ) 
					{
						e.printStackTrace ( );
					}
					catch ( NumberFormatException e )
					{
						receiver.send_message ( new Pd_Message ( actuator.getParameter ( Pd_Constants.SCOPE ), value ) );
					}
				}
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
    private void process_messages ( Pd_Message message )
    {
		String message_source = message.get_source ( );
		String symbol = message.get_symbol ( );
		Object[ ] arguments = message.get_arguments ( );
		System.err.println ( "Type: Message\n" + "Source: " + message_source + " Symbol: " + symbol );
		if ( symbol.equals ( Pd_Constants.READ_FACT ) )
		{
			access_knowledge_base ( message_source, arguments );
		}
		else if ( symbol.equals ( Pd_Constants.ADD_TO_FACT ) )
		{
			add_to_fact ( arguments );
		}
		else if ( symbol.equals ( Pd_Constants.MULTIPLY_FACT ) )
		{
			multiply_fact ( arguments );
		}
		else if ( symbol.equals ( Pd_Constants.UPDATE_FACT ) )
		{
			getAgent ( ).getKB ( ).updateFact ( ( String ) arguments[ 0 ], ( String ) arguments[ 1 ] );
		}
    }
	@Override
	public boolean init ( ) 
	{
		agent_name = getAgent ( ).getAgentName ( );

		receiver = Pd_Receiver.get_instance ( );
		subpatch = parameters.get ( Pd_Constants.SUBPATCH ) + Pd_Constants.SEPARATOR;
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
		for ( String sensor : senses.keySet ( ) )
		{
			Pd_Event event = senses.get ( sensor );
			String type = event.get_type ( );
			Object content = event.get_content ( );
			
			System.err.println ( getAgent ( ).getAgentName ( ) + ":" + sensor );
			System.err.print ( "Received Event from: " );
			if ( type.equals( Pd_Constants.BANG ) )
			{
				System.err.println ( "Type: Bang\n" + "Source: " + content );
			}
			else if ( type.equals( Pd_Constants.MESSAGE ) )
			{
				process_messages ( ( Pd_Message ) content );
			}
		}
		senses.clear ( );
	}
	@Override
	protected void eventHandlerRegistered ( EventHandler event_handler ) 
	{
		if ( event_handler instanceof Actuator )
		{
			actuators.put ( event_handler.getComponentName ( ), ( Pd_Actuator ) event_handler );
			actuator_memories.put ( event_handler.getComponentName ( ), getAgent ( ).getKB ( ).getMemory ( event_handler.getComponentName ( ) ) );
		}
		else if ( event_handler instanceof Sensor )
		{
			event_handler.registerListener( this );
			sensors.put ( event_handler.getComponentName ( ), ( Pd_Sensor ) event_handler );
			sensor_memories.put ( event_handler.getComponentName ( ), getAgent ( ).getKB ( ).getMemory ( event_handler.getComponentName ( ) ) );
		}
	}
	public void newSense ( Sensor source, double instant, double duration ) throws Exception 
	{
		Memory source_memory = sensor_memories.get( source.getComponentName ( ) );
		Pd_Event event = ( Pd_Event ) source_memory.readMemory ( instant, duration, TimeUnit.SECONDS );
		
		senses.put ( source.getComponentName ( ), event );

	}
}
