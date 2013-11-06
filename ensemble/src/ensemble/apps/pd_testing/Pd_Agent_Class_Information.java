package ensemble.apps.pd_testing;

import ensemble.Parameters;

public class Pd_Agent_Class_Information
{
	private String name;
	private String class_name;
		
	private Parameters arguments;
	
	public Pd_Agent_Class_Information ( String new_name, String new_class_name, Parameters new_arguments )
	{
		name = new_name;
		class_name = new_class_name;
		arguments = new_arguments;
	}
	public String get_name ( )
	{
		return name;
	}
	public String get_class_name ( )
	{
		return class_name;
	}
	public Parameters get_parameters ( )
	{
		return arguments;
	}
}
