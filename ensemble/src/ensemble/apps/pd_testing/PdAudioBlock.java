package ensemble.apps.pd_testing;

public class PdAudioBlock 
{
	private float[ ] samples;
	private String source;
	private String target;
	public PdAudioBlock ( float[ ] new_samples, String new_source )
	{
		samples = new_samples;
		source = new_source;
		target = null;
	}
	public float[ ] get_samples ( )
	{
		return samples;
	}
	public String get_source ( )
	{
		return source;
	}
	public String get_target ( )
	{
		return target;
	}
	public void set_source ( String new_source )
	{
		source = new_source;
	}
	public void set_target ( String new_target )
	{
		target = new_target;
	}
}
