package ensemble.apps.pd_testing;

public final class PdConstants 
{
	private PdConstants ( ) { };
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
	
	public static final int DEFAULT_TICKS = 64;
	/*
	 * Text:
	 */
	public static final String SEPARATOR = ":";
	public static final String AGENT_CLASS = "ensemble.apps.pd_testing.PdAgent";
	public static final String ACTUATOR_CLASS = "ensemble.apps.pd_testing.PdActuator";
	public static final String SENSOR_CLASS = "ensemble.apps.pd_testing.PdSensor";
	public static final String REASONING_CLASS = "ensemble.apps.pd_testing.PdReasoning";
	public static final String DEFAULT_REASONING_NAME = "Reasoning";
	/* 
	 * Ensemble Methods:
	 */
	public static final String MULTIPLY_FACT = "*";
	public static final String ADD_TO_FACT = "+";
	public static final String UPDATE_FACT = "update_fact";
	public static final String READ_FACT = "read";
	public static final String ADD_FACT = "new_fact";
	public static final String ADD_AGENT = "add_agent";
	public static final String ADD_ACTUATOR = "add_actuator";
	public static final String ADD_SENSOR = "add_sensor";
	public static final String ADD_REASONING = "add_reasoning";
	public static final String SUBSCRIPTION = "subscribe";
	public static final String UNSUBSCRIPTION = "unsubscribe";
	/* 
	 * Arguments:
	 */
	public static final String SELF_ACTUATOR = "actself";
	public static final String SELF_SENSOR = "senseself";
	public static final String BANG = "bang";
	public static final String FLOAT = "float";
	public static final String MESSAGE = "message";
	public static final String AUDIO_BLOCK = "audio_block";
	public static final String ENVIRONMENT_KEY = "environment";
	public static final String GLOBAL_KEY = "global";
	public static final String PRIVATE_KEY = "private";

	public static final String DEFAULT_EVENT_TYPE = "pd_event";
	public static final String SUB_TYPE = "sub_type";
	public static final String AUDIO_EVENT = "audio";
	public static final String MISC_EVENTS = "event";
	public static final String EVENT_TYPE = "type";
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
}