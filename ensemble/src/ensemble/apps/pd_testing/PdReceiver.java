package ensemble.apps.pd_testing;

import java.util.concurrent.CopyOnWriteArrayList;

import org.puredata.core.PdBase;
import org.puredata.core.utils.PdDispatcher;

public class PdReceiver extends PdDispatcher 
{	
	private CopyOnWriteArrayList< PdFloat > floats;
	private CopyOnWriteArrayList< String > bangs;
	private CopyOnWriteArrayList< PdMessage > messages;
	private CopyOnWriteArrayList< String > control_symbols;
	
	private static final PdReceiver INSTANCE = new PdReceiver ( );

	private PdReceiver ( )
	{
		floats = new CopyOnWriteArrayList< PdFloat > ( );
		bangs = new CopyOnWriteArrayList< String > ( );
		messages = new CopyOnWriteArrayList< PdMessage > ( );
		control_symbols = new CopyOnWriteArrayList< String > ( );
		
		/*
		 * Registering config symbols to pd receiver:
		 */
		register_symbol ( PdConstants.ENVIRONMENT_KEY );
		register_symbol ( PdConstants.ADD_AGENT );
		register_symbol ( PdConstants.GLOBAL_KEY );
		register_symbol ( PdConstants.SUBSCRIPTION );
		register_symbol ( PdConstants.UNSUBSCRIPTION );
		
		PdBase.openAudio ( PdConstants.INPUT_CHANNELS, PdConstants.OUTPUT_CHANNELS, PdConstants.SAMPLE_RATE );
		PdBase.computeAudio( true );
		PdBase.setReceiver ( this );
	}
	public static PdReceiver get_instance ( ) 
	{
        return INSTANCE;
    }
	public void send_float ( String target, Float value )
	{
		PdBase.sendFloat ( target, value );
	}
	public void send_bang ( String target )
	{
		PdBase.sendBang ( target );
	}
	public void send_message ( PdMessage message )
	{
		if ( message.get_arguments ( ) == null )
		{
			PdBase.sendMessage ( message.get_source ( ), message.get_symbol ( ) );
		}
		else
		{
			PdBase.sendMessage( message.get_source ( ), message.get_symbol ( ), message.get_arguments ( ) );
		}
	}
	@Override
	public void print ( String pd_message ) 
	{
		System.err.println ( pd_message );
	}
	@Override
	public void receiveFloat ( String source, float number )
	{
		floats.add ( new PdFloat ( source, number ) );		
	}
	@Override
	public void receiveBang ( String source )
	{
		System.err.println ( "Received bang from " + source );
		bangs.add ( source );
	}
	@Override
	public void receiveMessage ( String source, String symbol, Object... args )
	{
		System.err.println ( "Message from " + source + " that said " + symbol );
		messages.add ( new PdMessage ( source, symbol, args ) );
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
	public CopyOnWriteArrayList< PdFloat > get_floats ( )
	{
		return floats;
	}
	public CopyOnWriteArrayList< String > get_bangs ( )
	{
		return bangs;
	}
	public CopyOnWriteArrayList< PdMessage > get_messages ( )
	{
		return messages;
	}
	public void register_symbol ( String new_symbol )
	{
		System.err.println ( "Registered symbol " + new_symbol );
		control_symbols.add ( new_symbol );
		PdBase.subscribe ( new_symbol );
	}
	public void deregister_symbol ( String target )
	{
		control_symbols.remove ( target );
		PdBase.unsubscribe ( target );
	}
	public CopyOnWriteArrayList< String > get_symbols ( )
	{
		return control_symbols;
	}
	public void fetch_pd_messages ( )
	{
		PdBase.pollPdMessageQueue ( );
	}
	public void start_new_cycle ( )
	{
		floats.clear ( );
		bangs.clear ( );
		messages.clear ( );
	}
}