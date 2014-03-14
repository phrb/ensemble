package ensemble.apps.pd_testing;

import java.util.concurrent.CopyOnWriteArrayList;

import org.puredata.core.PdBase;
import org.puredata.core.utils.PdDispatcher;

public class Pd_Receiver extends PdDispatcher 
{	
	private CopyOnWriteArrayList< Pd_Float > floats;
	private CopyOnWriteArrayList< String > bangs;
	private CopyOnWriteArrayList< Pd_Message > messages;
	private CopyOnWriteArrayList< String > control_symbols;
	
	private static final Pd_Receiver INSTANCE = new Pd_Receiver ( );

	private Pd_Receiver ( )
	{
		floats = new CopyOnWriteArrayList< Pd_Float > ( );
		bangs = new CopyOnWriteArrayList< String > ( );
		messages = new CopyOnWriteArrayList< Pd_Message > ( );
		control_symbols = new CopyOnWriteArrayList< String > ( );
		
		/*
		 * Registering config symbols to pd receiver:
		 */
		register_symbol ( Pd_Constants.ENVIRONMENT_KEY );
		register_symbol ( Pd_Constants.ADD_AGENT_KEY );
		register_symbol ( Pd_Constants.GLOBAL_KEY );
		register_symbol ( Pd_Constants.SUBSCRIPTION );
		register_symbol ( Pd_Constants.UNSUBSCRIPTION );
		
		PdBase.openAudio ( Pd_Constants.INPUT_CHANNELS, Pd_Constants.OUTPUT_CHANNELS, Pd_Constants.SAMPLE_RATE );
		PdBase.computeAudio( true );
		PdBase.setReceiver ( this );
	}
	public static Pd_Receiver get_instance ( ) 
	{
        return INSTANCE;
    }
	public void send_bang ( String target )
	{
		PdBase.sendBang ( target );
	}
	public void send_message ( Pd_Message message )
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