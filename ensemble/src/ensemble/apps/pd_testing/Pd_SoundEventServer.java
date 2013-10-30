package ensemble.apps.pd_testing;

import java.util.ArrayList;
import ensemble.*;
import ensemble.apps.pd_testing.Pd_World;
import javax.sound.sampled.*;
import org.puredata.core.*;

public class Pd_SoundEventServer extends EventServer 
{
	Pd_World world;
	protected String agent_name;
	protected String agent_component_name;
	ArrayList< Event > events = new ArrayList< Event > ( );
	/*
	 * Audio
	 */
	private SourceDataLine line;   // to play the sound
    public int SAMPLE_RATE = 44100;
    private int BYTES_PER_SAMPLE = 2;                // 16-bit audio

	@Override
	public boolean configure ( ) 
	{
		System.err.println ( "Sound_Event_Server: Configured." );
		setEventType ( "SOUND" );
		return true;
	}
	
	/* (non-Javadoc)
	 * @see ensemble.EventServer#init()
	 */
	@Override
	public boolean init ( ) 
	{
		System.err.println ( "Sound_Event_Server: Initialized." );
		world = ( Pd_World ) envAgent.getWorld ( );
		
        AudioFormat format = new AudioFormat ( ( float ) SAMPLE_RATE, 8 * BYTES_PER_SAMPLE, 2, true, true );
        DataLine.Info info = new DataLine.Info ( SourceDataLine.class, format );
        try 
        {
			line = ( SourceDataLine ) AudioSystem.getLine ( info );
	        line.open ( format );

		} 
        catch ( LineUnavailableException e ) 
        {
			e.printStackTrace();
		}
        line.start ( );
		return true;
	}
	@Override
	public boolean finit ( )
	{
		PdBase.release ( );
        line.drain();
        line.stop();
		return true;
	}
	@Override
	public void processSense ( Event event ) 
	{
        line.write( ( byte [ ] ) event.objContent, 0, ( ( byte [ ] ) event.objContent ).length );
	}
}
