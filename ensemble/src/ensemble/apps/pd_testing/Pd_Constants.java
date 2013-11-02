package ensemble.apps.pd_testing;

public final class Pd_Constants 
{
	/*
	 * Numbers.
	 */
	public static final int SAMPLE_RATE = 44100;
	public static final int BYTES_PER_SAMPLE = 2;
	public static final int BITS_PER_SAMPLE = 8 * BYTES_PER_SAMPLE;
	public static final int INPUT_CHANNELS = 0;
	public static final int OUTPUT_CHANNELS = 2;
	public static final int BANG_OUTLETS = 5;
	public static final int FLOAT_OUTLETS = 5;
	
	public static final float DEFAULT_SECONDS = 1;
	/*
	 * Text.
	 */
	public static final String PATCH_ARGUMENT = "PATCH";
	public static final String TICK_TARGET = "get_tick_size";
	public static final String SECONDS_TARGET = "get_seconds";
	public static final String PROCESSING_ON = "processing_on";
	public static final String PROCESSING_OFF = "processing_off";
	public static final String BANG = "ensemble_bang";
	public static final String FLOAT = "ensemble_float";
	public static enum CONTROL_SYMBOL
	{
		TICK ( "send_tick_size" ),
		SECONDS ( "send_seconds" ),
		AUDIO_TOGGLE ( "audio_toggle" ),
		AUDIO_ON ( "audio_on" ),
		AUDIO_OFF ( "audio_off" );
		
		private String name;
		private CONTROL_SYMBOL ( String new_name )
		{
			this.name = new_name;
		}
		public String get_value ( )
		{
			return this.name;
		}
	}
}