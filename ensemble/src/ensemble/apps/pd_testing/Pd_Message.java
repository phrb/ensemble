package ensemble.apps.pd_testing;

public class Pd_Message 
{
	private String source;
	private Object symbol;
	private Object[ ] arguments = null;
	
	public Pd_Message ( String new_source, String new_symbol, Object... new_arguments )
	{
		arguments = new_arguments;
		source = new_source;
		symbol = new_symbol;
	}
	public Pd_Message ( String new_source, String new_symbol )
	{
		source = new_source;
		symbol = new_symbol;
	}
	public String get_source ( )
	{
		return source;
	}
	public String get_symbol ( )
	{
		if ( symbol instanceof Float )
		{
			return symbol.toString ( );
		}
		else
		{
			return ( String ) symbol;	
		}
	}
	public Object[ ] get_arguments ( )
	{
		return arguments;
	}
}
