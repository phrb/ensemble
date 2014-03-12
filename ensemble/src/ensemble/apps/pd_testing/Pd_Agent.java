package ensemble.apps.pd_testing;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

import org.puredata.core.PdBase;

import ensemble.MusicalAgent;
import ensemble.Parameters;

/*
 * A simple extension of Ensemble default MusicalAgent class.
 */

public class Pd_Agent extends MusicalAgent
{
	private static final long serialVersionUID = 1L;
	
	private String patch_path;
	private int patch;
	private Pd_Receiver receiver;
	String agent_name;
	
	private Parameters read_arguments ( Pd_Message message, int offset ) 
	{
		Parameters new_parameters = new Parameters ( );	
		Object[ ] attributes = message.get_arguments ( );
		for ( int i = offset; i < attributes.length; i++ ) 
		{
			if ( attributes[ i ].equals ( Pd_Constants.TARGET ) )
			{
				new_parameters.put( ( String ) attributes[ i ], ( String ) attributes[ i + 1 ] );
				i += 1;
			}
			else if ( attributes[ i ].equals ( Pd_Constants.PATCH_ARGUMENT ) )
			{
				new_parameters.put( ( String ) attributes[ i ], ( String ) attributes[ i + 1 ] );
				i += 1;
			}
			else if ( attributes[ i ].equals ( Pd_Constants.EVENT_TYPE ) )
			{
				new_parameters.put( ( String ) attributes[ i ], ( String ) attributes[ i + 1 ] );
				i += 1;
			}
			/*
			 * TODO: Dinamically receive arguments. (arg, value)
			 */
			else
			{
				break;
			}
		}
		return new_parameters;
	}
    private void process_subpatch_control_messages ( )
    {
    	/*
    	 * Process all messages sent
    	 * to default control symbols.
    	 * 
    	 */
    	PdBase.pollPdMessageQueue ( );
    	CopyOnWriteArrayList< Pd_Message > messages = receiver.get_messages ( );
    	int actuator = 0;
    	int sensor = 0;
    	for ( Pd_Message message : messages )
    	{
    		String source = message.get_source ( );
    		String component_name = message.get_symbol ( );
    		if ( source.equals ( agent_name ) &&
    				component_name.equals ( Pd_Constants.ADD_ACTUATOR ) )
    		{
       			String actuator_name = "Actuator " + actuator; 			
				this.addComponent ( actuator_name, Pd_Constants.PD_ACTUATOR_CLASS, read_arguments ( message, 0 ) );
				actuator += 1;
    		}
    		else if ( source.equals ( agent_name ) &&
    				component_name.equals ( Pd_Constants.ADD_SENSOR ) )
    		{
    			String sensor_name = "Sensor " + sensor;
				this.addComponent ( sensor_name, Pd_Constants.PD_SENSOR_CLASS, read_arguments ( message, 0 ) );
				sensor += 1;
    		}
    	}
    }
    private void process_patch_control_messages ( )
    {
    	/*
    	 * Process all messages sent
    	 * to default control symbols.
    	 * 
    	 */
    	PdBase.pollPdMessageQueue ( );
    	CopyOnWriteArrayList< Pd_Message > messages = receiver.get_messages ( );
    	for ( Pd_Message message : messages )
    	{
    		String source = message.get_source ( );
    		if ( source.equals ( agent_name + "_" + Pd_Constants.ADD_ACTUATOR ) )
    		{
       			String actuator_name = message.get_symbol ( );			
				this.addComponent ( actuator_name, Pd_Constants.PD_ACTUATOR_CLASS, read_arguments ( message, 0 ) );
    		}
    		else if ( source.equals ( agent_name + "_" + Pd_Constants.ADD_SENSOR ) )
    		{
    			String sensor_name = message.get_symbol ( );
				this.addComponent ( sensor_name, Pd_Constants.PD_SENSOR_CLASS, read_arguments ( message, 0 ) );
    		}
    		else if ( source.equals ( agent_name + "_" + Pd_Constants.ADD_REASONING ) )
    		{
    			String reasoning_name = message.get_symbol ( );
				this.addComponent ( reasoning_name, Pd_Constants.PD_REASONING_CLASS, read_arguments ( message, 0 ) );
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
		patch_path = parameters.get( Pd_Constants.PATCH_ARGUMENT );
		receiver = Pd_Receiver.get_instance ( );
		/* 
		 * Registering control symbols:
		 */
		receiver.register_default_symbol ( agent_name + "_" + Pd_Constants.ADD_ACTUATOR );
		receiver.register_default_symbol ( agent_name + "_" + Pd_Constants.ADD_SENSOR );
		receiver.register_default_symbol ( agent_name + "_" + Pd_Constants.ADD_REASONING );
		receiver.register_default_symbol ( agent_name );

		/*
		 * Subscribing to known control symbols.
		 */
		for ( String symbol : receiver.get_default_symbols ( ) )
		{
			PdBase.subscribe ( symbol );
		}
		PdBase.sendBang( agent_name + "-start" );
		if ( patch_path == null )
		{
			Parameters reasoning_parameters = new Parameters ( );
			reasoning_parameters.put ( "subpatch_name", agent_name );
			this.addComponent ( "Reasoning", Pd_Constants.PD_REASONING_CLASS, reasoning_parameters );
			process_subpatch_control_messages ( );
	    	( ( Pd_Receiver ) receiver ).start_new_cycle ( );
			return true;
		}
		else
		{
			/*
	         * Patch opening.
	         */
			try 
			{
				patch = PdBase.openPatch ( patch_path );
				System.err.println ( "PURE_DATA: PATCH_ID=" + patch + " PATH=" + "\"" + parameters.get( Pd_Constants.PATCH_ARGUMENT ) + "\"" );
			} 
			catch ( IOException e ) 
			{
				e.printStackTrace ( );
			}
			process_patch_control_messages ( );
	    	//( ( Pd_Receiver ) receiver ).start_new_cycle ( );
	    	PdBase.closePatch ( patch );
	    	return true;
		}
	}
}