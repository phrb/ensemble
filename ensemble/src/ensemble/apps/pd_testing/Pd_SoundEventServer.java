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

	@Override
	public boolean configure ( ) 
	{
		setEventType ( "SOUND" );
		return true;
	}
	
	/* (non-Javadoc)
	 * @see ensemble.EventServer#init()
	 */
	@Override
	public boolean init ( ) 
	{
		world = ( Pd_World ) envAgent.getWorld ( );
		
        AudioFormat format = new AudioFormat ( ( float ) Pd_Constants.SAMPLE_RATE, Pd_Constants.BITS_PER_SAMPLE, Pd_Constants.OUTPUT_CHANNELS, true, true );
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
