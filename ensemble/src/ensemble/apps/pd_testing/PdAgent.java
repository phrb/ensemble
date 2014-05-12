package ensemble.apps.pd_testing;

import java.util.concurrent.CopyOnWriteArrayList;
import ensemble.MusicalAgent;
import ensemble.Parameters;

/*
 * A simple extension of Ensemble default MusicalAgent class.
 */

public class PdAgent extends MusicalAgent
{
	private static final long serialVersionUID = 1L;
	
	private PdReceiver receiver;
	String agent_name;
	
	private Parameters read_arguments ( PdMessage message, int offset ) 
	{
		Parameters new_parameters = new Parameters ( );	
		Object[ ] attributes = message.get_arguments ( );
		for ( int i = offset; i < attributes.length; i++ ) 
		{
			if ( attributes[ i ].equals ( PdConstants.SCOPE ) )
			{
				new_parameters.put( ( String ) attributes[ i ], ( String ) attributes[ i + 1 ] );
				i += 1;
			}
			else if ( attributes[ i ].equals ( PdConstants.EVENT_TYPE ) )
			{
				new_parameters.put( ( String ) attributes[ i ], ( String ) attributes[ i + 1 ] );
				i += 1;
			}
			else
			{
				break;
			}
		}
		return new_parameters;
	}
    private void process_config_messages ( )
    {
    	/*
    	 * Process all messages sent
    	 * to default control symbols.
    	 * 
    	 */
    	CopyOnWriteArrayList< PdMessage > messages = receiver.get_messages ( );
    	for ( PdMessage message : messages )
    	{
    		String source = message.get_source ( );
    		String component_name = message.get_symbol ( );
    		if ( source.equals ( PdConstants.SUBSCRIPTION ) )
    		{
    			receiver.register_symbol ( agent_name + PdConstants.SEPARATOR + component_name );
    		}
    		else if ( source.equals ( agent_name ) &&
    				component_name.equals ( PdConstants.ADD_ACTUATOR ) )
    		{
       			String actuator_name = ( String ) message.get_arguments ( )[ 0 ];
       			receiver.register_symbol ( agent_name + PdConstants.SEPARATOR + actuator_name );
				this.addComponent ( actuator_name, PdConstants.ACTUATOR_CLASS, read_arguments ( message, 1 ) );
    		}
    		else if ( source.equals ( agent_name ) &&
    				component_name.equals ( PdConstants.ADD_SENSOR ) )
    		{
    			String sensor_name = ( String ) message.get_arguments ( )[ 0 ];
       			receiver.register_symbol ( agent_name + PdConstants.SEPARATOR + sensor_name );
				this.addComponent ( sensor_name, PdConstants.SENSOR_CLASS, read_arguments ( message, 1 ) );
    		}
    		else if ( source.equals ( agent_name ) &&
    				component_name.equals ( PdConstants.ADD_FACT ) )
    		{
    			String scope = ( String ) message.get_arguments ( )[ 0 ];
    			String fact_name = ( String ) message.get_arguments ( )[ 1 ];
    			Object fact_value = message.get_arguments ( )[ 2 ];
    			String string_value;	
    			if ( fact_value instanceof Float )
    			{
    				string_value = fact_value.toString ( );
    			}
    			else
    			{
    				string_value = ( String ) fact_value;
    			}
    			if ( scope.equals( PdConstants.GLOBAL_KEY ) )
    			{
           			getKB ( ).registerFact ( fact_name, string_value, true );
    			}
    			else if ( scope.equals ( PdConstants.PRIVATE_KEY ) )
    			{
           			getKB ( ).registerFact ( fact_name, string_value, false );
    			}
    		}
    	}
    }
	/*
	 * init and configure are called once every time an instance of
	 * this Agent is inserted into the virtual environment.
	 * 
	 * (non-Javadoc)
	 * @see ensemble.EnsembleAgent#configure()
	 */
	@Override
	public boolean configure ( )
	{	
		return true;
	}
	@Override
	public boolean init ( )
	{
		agent_name = getAgentName ( );
		receiver = PdReceiver.get_instance ( );
		/* 
		 * Registering control symbols:
		 */
		receiver.register_symbol ( agent_name );

		receiver.send_bang( agent_name + PdConstants.SEPARATOR + PdConstants.START_AGENT );
		receiver.fetch_pd_messages ( );

		Parameters reasoning_parameters = new Parameters ( );
		reasoning_parameters.put ( PdConstants.SUBPATCH, agent_name );
		this.addComponent ( PdConstants.DEFAULT_REASONING_NAME, PdConstants.REASONING_CLASS, reasoning_parameters );
		
		Parameters self_actuator = new Parameters ( );
		Parameters self_sensor = new Parameters ( );
		
		self_actuator.put ( PdConstants.EVENT_TYPE, PdConstants.DEFAULT_EVENT_TYPE );
		self_actuator.put ( PdConstants.SCOPE, agent_name + PdConstants.SEPARATOR +
				PdConstants.SELF_SENSOR );
		
		self_sensor.put ( PdConstants.EVENT_TYPE, PdConstants.DEFAULT_EVENT_TYPE );
		self_sensor.put ( PdConstants.SCOPE, agent_name + PdConstants.SEPARATOR +
				PdConstants.SELF_ACTUATOR );
		
		receiver.register_symbol( agent_name + PdConstants.SEPARATOR + PdConstants.SELF_ACTUATOR );
		receiver.register_symbol( agent_name + PdConstants.SEPARATOR + PdConstants.SELF_SENSOR );
		
		this.addComponent( PdConstants.SELF_ACTUATOR, PdConstants.ACTUATOR_CLASS, self_actuator );
		this.addComponent( PdConstants.SELF_SENSOR, PdConstants.SENSOR_CLASS, self_sensor );
		
		process_config_messages ( );
		return true;
	}
}