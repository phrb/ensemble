package ensemble.apps.pd_testing;

import java.io.IOException;

import org.puredata.core.PdBase;

public class PdProcessor 
{
	private static final PdProcessor INSTANCE = new PdProcessor ( );
	private PdProcessor ( ) { }
	public static PdProcessor get_instance ( )
	{
		return INSTANCE;
	}
	public boolean process_ticks ( int ticks, short[ ] input, short[ ] output )
	{
		PdBase.process( ticks, input, output );
		return true;
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
