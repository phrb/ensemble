package ensemble.apps.pd_testing;

public class PdEvent
{	
	private String type;
	private Object content;
	
	public PdEvent ( String new_type, Object new_content )
	{
		type = new_type;
		content = new_content;
	}
	public String get_type ( )
	{
		return type;
	}
	public Object get_content ( )
	{
		return content;
	}
}