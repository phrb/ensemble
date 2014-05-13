package ensemble.apps.pd_testing;

public class PdAudioBlock 
{
	private float[ ] samples;
	private String source;
	public PdAudioBlock ( float[ ] new_samples, String new_source )
	{
		samples = new_samples;
		source = new_source;
	}
	public float[ ] get_samples ( )
	{
		return samples;
	}
	public String get_source ( )
	{
		return source;
	}
}
