package ensemble.apps.pd_testing;

import ensemble.Parameters;

public class PdAgentClassInformation
{
	private String name;
	private String class_name;
		
	private Parameters arguments;
	private Parameters knowledge_base;
	
	public PdAgentClassInformation ( String new_name, String new_class_name, Parameters new_arguments, Parameters new_knowledge_base )
	{
		name = new_name;
		class_name = new_class_name;
		arguments = new_arguments;
		knowledge_base = new_knowledge_base;
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
	public Parameters get_knowledge_base ( )
	{
		return knowledge_base;
	}
}
