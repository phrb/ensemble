package ensemble.apps.pd_testing;

import javax.sound.sampled.SourceDataLine;

public class Pd_Runnable_Player implements Runnable 
{
	byte[ ] buffer;
	SourceDataLine line;
	public synchronized void start ( byte[ ] new_buffer, SourceDataLine new_line )
	{
		line = new_line;
		buffer = new_buffer;
	}
	@Override
	public synchronized void run ( )
	{
		line.write( buffer, 0, buffer.length );
		line.flush ( );
	}
}
