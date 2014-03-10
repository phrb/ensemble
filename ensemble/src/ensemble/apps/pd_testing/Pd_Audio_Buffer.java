package ensemble.apps.pd_testing;

public class Pd_Audio_Buffer 
{
	private float[ ] audio_samples;
	private int pd_time_tag;
	private String source;
	
	public Pd_Audio_Buffer ( float[ ] new_audio_samples, int new_time_tag, String new_source )
	{
		audio_samples = new_audio_samples;
		pd_time_tag = new_time_tag;
		source = new_source;
	}
	public float[ ] get_audio_samples ( )
	{
		return audio_samples;
	}
	public int get_pd_time_tag ( )
	{
		return pd_time_tag;
	}
	public String get_source ( )
	{
		return source;
	}
	public void set_time_tag ( int new_time_tag )
	{
		pd_time_tag = new_time_tag;
	}
}
