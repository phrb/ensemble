package ensemble.apps.pd_testing;

import java.util.ArrayList;

import org.puredata.core.utils.PdDispatcher;

public class Pd_Receiver extends PdDispatcher 
{	
	private ArrayList< Pd_Float > float_list = new ArrayList< Pd_Float > ( );
	private ArrayList< String > bang_list = new ArrayList< String > ( );

	@Override
	public void print ( String pd_message ) 
	{
		System.err.println ( pd_message );
	}
	@Override
	public void receiveFloat ( String source, float number )
	{
		float_list.add ( new Pd_Float ( source, number ) );		
	}
	@Override
	public void receiveBang ( String source )
	{
		bang_list.add ( source );
	}
	public ArrayList< Pd_Float > get_float_list ( )
	{
		return float_list;
	}
	public ArrayList< String > get_bang_list ( )
	{
		return bang_list;
	}
	public void start_new_turn ( )
	{
		float_list = new ArrayList< Pd_Float > ( );
		bang_list = new ArrayList< String > ( );
	}
}