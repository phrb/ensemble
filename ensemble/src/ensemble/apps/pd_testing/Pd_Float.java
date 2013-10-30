package ensemble.apps.pd_testing;

public class Pd_Float 
{
	private float value;
	private String name;
	
	public Pd_Float ( String new_name, float new_value )
	{
		value = new_value;
		name = new_name;
	}
	public float get_value ( )
	{
		return value;
	}
	public String get_name ( )
	{
		return name;
	}
}
