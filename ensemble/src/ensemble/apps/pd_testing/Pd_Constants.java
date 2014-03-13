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
	public static final int BANG_OUTLETS = 5;
	public static final int FLOAT_OUTLETS = 5; 	
	public static final int START_INSTANT = 0;
	
	public static final float DEFAULT_SECONDS = 1;	
	public static final int DEFAULT_TICKS = 1024;
	/*
	 * Text:
	 */
	public static final String PD_AGENT_CLASS = "ensemble.apps.pd_testing.Pd_Agent";
	public static final String PD_ACTUATOR_CLASS = "ensemble.apps.pd_testing.Pd_Actuator";
	public static final String PD_SENSOR_CLASS = "ensemble.apps.pd_testing.Pd_Sensor";
	public static final String PD_REASONING_CLASS = "ensemble.apps.pd_testing.Pd_Reasoning";
	/* 
	 * Arguments:
	 */
	public static final String DEFAULT_EVENT_TYPE = "audio";
	public static final String CURRENT_INSTANT = "CURRENT_INSTANT";
	public static final String EVENT_TYPE = "type";
	public static final String PATCH_ARGUMENT = "patch";
	public static final String TARGET = "target";
	public static final String AGENT_NUMBER_ARGUMENT = "AGENTS";
	/*
	 * Ensemble will send bangs to these symbols:
	 */
	public static final String PROCESSING_ON = "processing_on";
	public static final String PROCESSING_OFF = "processing_off";
	/*
	 * Ensemble will subscribe to these symbols:
	 */
	public static final String SUBSCRIPTION = "subscribe";
	public static final String UNSUBSCRIPTION = "unsubscribe";
	public static final String BANG = "bang";
	public static final String FLOAT = "float";
	public static final String NEW_SUBSCRIPTION = "subscribe";
	public static final String AUDIO_TOGGLE = "audio_toggle";
	public static final String AUDIO_ON = "audio_on";
	public static final String AUDIO_OFF = "audio_off";
	public static final String TICK = "set_ticks";
	public static final String SECONDS = "set_seconds";
	public static final String MUTE = "MUTE";
	public static final String ADD_ACTUATOR = "add_actuator";
	public static final String ADD_SENSOR = "add_sensor";
	public static final String ADD_REASONING = "add_reasoning";
}