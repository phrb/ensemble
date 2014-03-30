package ensemble.apps.pd_testing;

import java.io.IOException;

import org.puredata.core.PdBase;

public class PdAudioServer 
{
	private PdReceiver receiver;
	private static final PdAudioServer INSTANCE = new PdAudioServer ( );
	private PdAudioServer ( )
	{
		receiver = PdReceiver.get_instance ( );
	}
	public static PdAudioServer get_instance ( )
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
    	PdBase.sendBang ( patch + PdConstants.PROCESSING_ON );
	}
	public void open_dsp ( int patch )
	{
    	PdBase.sendBang ( patch + PdConstants.PROCESSING_ON );
	}
	public void close_dsp ( String patch )
	{
    	PdBase.sendBang ( patch + PdConstants.PROCESSING_OFF );
	}
	public void close_dsp ( int patch )
	{
    	PdBase.sendBang ( patch + PdConstants.PROCESSING_OFF );
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
