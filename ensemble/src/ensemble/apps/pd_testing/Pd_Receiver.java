package ensemble.apps.pd_testing;

import java.util.ArrayList;
import org.puredata.core.utils.PdDispatcher;

public class Pd_Receiver extends PdDispatcher 
{	
	private ArrayList< Pd_Float > floats;
	private ArrayList< String > bangs;
	private ArrayList< Pd_Message > messages;
	ArrayList< String > default_control_symbols;
	ArrayList< String > user_control_symbols;

	public Pd_Receiver ( )
	{
		floats = new ArrayList< Pd_Float > ( );
		bangs = new ArrayList< String > ( );
		messages = new ArrayList< Pd_Message > ( );
		default_control_symbols = new ArrayList< String > ( );
		user_control_symbols = new ArrayList< String > ( );
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
	@Override
	public void receiveSymbol ( String source, String symbol )
	{
		print ( "SRC=" + source + " SYM=" + symbol );
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
	public void register_symbol ( String new_symbol )
	{
		user_control_symbols.add ( new_symbol );
	}
	public void deregister_symbol ( String target )
	{
		user_control_symbols.remove ( target );
	}
	public void register_default_symbol ( String new_symbol )
	{
		default_control_symbols.add ( new_symbol );
	}
	public void deregister_default_symbol ( String target )
	{
		default_control_symbols.remove ( target );
	}
	public ArrayList< String > get_user_symbols ( )
	{
		return user_control_symbols;
	}
	public ArrayList< String > get_default_symbols ( )
	{
		return default_control_symbols;
	}
	public void start_new_cycle ( )
	{
		floats = new ArrayList< Pd_Float > ( );
		bangs = new ArrayList< String > ( );
		messages = new ArrayList< Pd_Message > ( );
	}
}