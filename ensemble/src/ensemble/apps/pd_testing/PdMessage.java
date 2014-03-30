package ensemble.apps.pd_testing;

public class PdMessage 
{
	private String source;
	private Object symbol;
	private Object[ ] arguments = null;
	
	public PdMessage ( String new_source, String new_symbol, Object... new_arguments )
	{
		arguments = new_arguments;
		source = new_source;
		symbol = new_symbol;
	}
	public PdMessage ( String new_source, String new_symbol )
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
	public void set_arguments ( Object[ ] new_arguments )
	{
		arguments = new_arguments;
	}
}
