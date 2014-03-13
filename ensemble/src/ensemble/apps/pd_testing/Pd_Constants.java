package ensemble.apps.pd_testing;

public final class Pd_Constants 
{
	private Pd_Constants ( ) { };
	/*
	 * Numbers:
	 */
	public static final int SAMPLE_RATE = 44100;
	public static final int PD_BLOCK_SIZE = 64;
	public static final int PD_EVENT_BUFFER_SIZE = 400;
	public static final int BYTES_PER_SAMPLE = 2;
	public static final int BITS_PER_SAMPLE = 8 * BYTES_PER_SAMPLE;
	public static final int INPUT_CHANNELS = 0;
	public static final int OUTPUT_CHANNELS = 2;	
	
	public static final int DEFAULT_TICKS = 100;
	/*
	 * Text:
	 */
	public static final String SEPARATOR = ":";
	public static final String PD_AGENT_CLASS = "ensemble.apps.pd_testing.Pd_Agent";
	public static final String PD_ACTUATOR_CLASS = "ensemble.apps.pd_testing.Pd_Actuator";
	public static final String PD_SENSOR_CLASS = "ensemble.apps.pd_testing.Pd_Sensor";
	public static final String PD_REASONING_CLASS = "ensemble.apps.pd_testing.Pd_Reasoning";
	/* 
	 * Arguments:
	 */
	public static final String BANG = "bang";
	public static final String FLOAT = "float";
	public static final String MESSAGE = "message";
	public static final String ENVIRONMENT_KEY = "environment";
	public static final String ADD_AGENT_KEY = "add_agent";
	public static final String GLOBAL_KEY = "global";
	public static final String PRIVATE_KEY = "private";

	public static final String DEFAULT_EVENT_TYPE = "event";
	public static final String CURRENT_INSTANT = "CURRENT_INSTANT";
	public static final String EVENT_TYPE = "type";
	public static final String PATCH_ARGUMENT = "patch";
	public static final String SUBPATCH = "subpatch_name";
	public static final String SCOPE = "scope";
	/*
	 * Ensemble will send bangs to these symbols:
	 */
	public static final String START_AGENT = "start";
	public static final String PROCESSING_ON = "processing_on";
	public static final String PROCESSING_OFF = "processing_off";
	/*
	 * Ensemble will subscribe to these symbols:
	 */
	public static final String SUBSCRIPTION = "subscribe";
	public static final String UNSUBSCRIPTION = "unsubscribe";
	public static final String ADD_FACT = "new_fact";
	public static final String ADD_ACTUATOR = "add_actuator";
	public static final String ADD_SENSOR = "add_sensor";
	public static final String ADD_REASONING = "add_reasoning";
}