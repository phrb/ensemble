package ensemble.apps.pd_testing;

import java.io.IOException;

import org.puredata.core.PdBase;

public class PdProcessor 
{
	private static final PdProcessor INSTANCE = new PdProcessor ( );
	private static final int frames = ( PdConstants.DEFAULT_TICKS * PdConstants.PD_BLOCK_SIZE ) / PdConstants.BYTES_PER_SAMPLE;
	private static final short[ ] dummy_adc = new short[ frames * PdConstants.INPUT_CHANNELS ];
	private static final short[ ] dummy_dac = new short[ frames * PdConstants.OUTPUT_CHANNELS ];
	private PdProcessor ( ) { }
	public static PdProcessor get_instance ( )
	{
		return INSTANCE;
	}
	public boolean process_ticks ( int ticks )
	{
		PdBase.process ( ticks, dummy_adc, dummy_dac );
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
