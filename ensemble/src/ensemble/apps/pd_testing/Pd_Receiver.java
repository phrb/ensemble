package ensemble.apps.pd_testing;

import java.util.ArrayList;
import org.puredata.core.utils.PdDispatcher;

public class Pd_Receiver extends PdDispatcher 
{	
	private ArrayList< Pd_Float > floats;
	private ArrayList< String > bangs;
	private ArrayList< Pd_Message > messages;

	public Pd_Receiver ( )
	{
		floats = new ArrayList< Pd_Float > ( );
		bangs = new ArrayList< String > ( );
		messages = new ArrayList< Pd_Message > ( );
	}
	@Override
	public void print ( String pd_message ) 
	{
		System.err.println ( pd_message );
	}
	@Override
	public void receiveFloat ( String source, float number )
	{
		floats.add ( new Pd_Float ( source, number ) );		
	}
	@Override
	public void receiveBang ( String source )
	{
		bangs.add ( source );
	}
	@Override
	public void receiveMessage ( String source, String symbol, Object... args )
	{
		messages.add ( new Pd_Message ( source, symbol, args ) );
	}
	
	public ArrayList< Pd_Float > get_floats ( )
	{
		return floats;
	}
	public ArrayList< String > get_bangs ( )
	{
		return bangs;
	}
	public ArrayList< Pd_Message > get_messages ( )
	{
		return messages;
	}
	public void start_new_cycle ( )
	{
		floats = new ArrayList< Pd_Float > ( );
		bangs = new ArrayList< String > ( );
		messages = new ArrayList< Pd_Message > ( );
	}
}