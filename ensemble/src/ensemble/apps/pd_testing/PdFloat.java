package ensemble.apps.pd_testing;

public class PdFloat 
{
	private float value;
	private String name;
	
	public PdFloat ( String new_name, float new_value )
	{
		value = new_value;
		name = new_name;
	}
	public float get_value ( )
	{
		return value;
	}
	public String get_source ( )
	{
		return name;
	}
}
