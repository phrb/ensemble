package ensemble.apps.pd_testing;

import java.io.IOException;

import org.puredata.core.PdBase;

public class Pd_Audio_Server 
{
	private Pd_Receiver receiver;
	private static final Pd_Audio_Server INSTANCE = new Pd_Audio_Server ( );
	private Pd_Audio_Server ( )
	{
		receiver = Pd_Receiver.get_instance ( );
	}
	public static Pd_Audio_Server get_instance ( )
	{
		return INSTANCE;
	}
	public boolean process_ticks ( int ticks, short[ ] input, short[ ] output )
	{
		PdBase.process( ticks, input, output );
		/**/
		receiver.fetch_pd_messages ( );
		return true;
	}
	public void open_dsp ( String patch )
	{
    	PdBase.sendBang ( patch + Pd_Constants.PROCESSING_ON );
	}
	public void open_dsp ( int patch )
	{
    	PdBase.sendBang ( patch + Pd_Constants.PROCESSING_ON );
	}
	public void close_dsp ( String patch )
	{
    	PdBase.sendBang ( patch + Pd_Constants.PROCESSING_OFF );
	}
	public void close_dsp ( int patch )
	{
    	PdBase.sendBang ( patch + Pd_Constants.PROCESSING_OFF );
	}
	public int open_patch ( String path ) throws IOException
	{
		return PdBase.openPatch ( path );
	}
	public void close_patch ( int patch )
	{
		PdBase.closePatch ( patch );
	}
}
