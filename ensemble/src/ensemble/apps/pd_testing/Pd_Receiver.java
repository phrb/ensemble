package ensemble.apps.pd_testing;

import java.util.concurrent.CopyOnWriteArrayList;

import org.puredata.core.PdBase;
import org.puredata.core.utils.PdDispatcher;

public class Pd_Receiver extends PdDispatcher 
{	
	private CopyOnWriteArrayList< Pd_Float > floats;
	private CopyOnWriteArrayList< String > bangs;
	private CopyOnWriteArrayList< Pd_Message > messages;
	private CopyOnWriteArrayList< String > default_control_symbols;
	private CopyOnWriteArrayList< String > user_control_symbols;
	
	private static final Pd_Receiver INSTANCE = new Pd_Receiver ( );

	private Pd_Receiver ( )
	{
		floats = new CopyOnWriteArrayList< Pd_Float > ( );
		bangs = new CopyOnWriteArrayList< String > ( );
		messages = new CopyOnWriteArrayList< Pd_Message > ( );
		default_control_symbols = new CopyOnWriteArrayList< String > ( );
		user_control_symbols = new CopyOnWriteArrayList< String > ( );
		PdBase.openAudio ( Pd_Constants.INPUT_CHANNELS, Pd_Constants.OUTPUT_CHANNELS, Pd_Constants.SAMPLE_RATE );
		PdBase.computeAudio( true );
		PdBase.setReceiver ( this );
	}
	public static Pd_Receiver get_instance ( ) 
	{
        return INSTANCE;
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
	@Override
	public void receiveList ( String source, Object... list )
	{
		print ( source );
		for ( Object string : list )
		{
			print ( ( String ) string );
		}
	}
	public CopyOnWriteArrayList< Pd_Float > get_floats ( )
	{
		return floats;
	}
	public CopyOnWriteArrayList< String > get_bangs ( )
	{
		return bangs;
	}
	public CopyOnWriteArrayList< Pd_Message > get_messages ( )
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
	public CopyOnWriteArrayList< String > get_user_symbols ( )
	{
		return user_control_symbols;
	}
	public CopyOnWriteArrayList< String > get_default_symbols ( )
	{
		return default_control_symbols;
	}
	public void start_new_cycle ( )
	{
		floats.clear ( );
		bangs.clear ( );
		messages.clear ( );
	}
}