package ensemble.apps.pd_testing;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

import ensemble.*;
import ensemble.apps.pd_testing.Pd_World;
import javax.sound.sampled.*;

import org.puredata.core.PdBase;

public class Pd_AudioEventServer extends EventServer 
{
	Pd_World world;
	protected String agent_name;
	protected String agent_component_name;
	/*
	 * Audio
	 */
	int frames = PdBase.blockSize() * Pd_Constants.DEFAULT_TICKS;
	
    private byte[ ] output_samples;
    private short[ ] input_samples;
    private short[ ] short_samples;
    private Pd_Audio_Server sampler;
    
    SourceDataLine line;
    
	ByteBuffer byte_buffer;
	ShortBuffer short_buffer;
	
	@Override
	public boolean configure ( )
	{
		setEventType ( Pd_Constants.DEFAULT_EVENT_TYPE );
		return true;
	}
	
	/* (non-Javadoc)
	 * @see ensemble.EventServer#init()
	 */
	@Override
	public boolean init ( ) 
	{
	    output_samples = new byte[ frames * Pd_Constants.OUTPUT_CHANNELS * Pd_Constants.BYTES_PER_SAMPLE ];
	    input_samples = new short [ frames * Pd_Constants.INPUT_CHANNELS ];
	    short_samples = new short [ frames * Pd_Constants.OUTPUT_CHANNELS ];
	    
	    sampler = Pd_Audio_Server.get_instance ( );
	    
		byte_buffer = ByteBuffer.wrap ( output_samples );
		short_buffer = byte_buffer.asShortBuffer ( );
		
		world = ( Pd_World ) envAgent.getWorld ( );
		AudioFormat format = new AudioFormat ( ( float ) Pd_Constants.SAMPLE_RATE, Pd_Constants.BITS_PER_SAMPLE, Pd_Constants.OUTPUT_CHANNELS, true, true );
		DataLine.Info info = new DataLine.Info ( SourceDataLine.class, format );
		try 
		{
			line = ( SourceDataLine ) AudioSystem.getLine ( info );
			line.addLineListener( new Pd_Line_Listener ( ) );
			line.open ( format, PdBase.blockSize ( ) * Pd_Constants.DEFAULT_TICKS );
			line.start ( );
		}
		catch (LineUnavailableException e) 
		{
			e.printStackTrace();
		}
		return true;
	}
	@Override
	public boolean finit ( )
	{
	    line.drain ( );
	    line.stop ( );
		return true;
	}
	protected void process ( )
	{
		sampler.process_ticks ( Pd_Constants.DEFAULT_TICKS, input_samples, short_samples );
		short_buffer.rewind ( );
		short_buffer.put( short_samples );
		line.write ( output_samples, 0, output_samples.length );
	}
	@Override
	public void processSense ( Event new_event ) 
	{
	}
	@Override
	protected Parameters actuatorRegistered ( String agentName, String actuatorName, Parameters userParam ) throws Exception 
	{
		System.err.println ( "Pd_AudioEventServer Says: " + agentName + " registered: " + actuatorName );
		return userParam;
	}
	protected Parameters sensorRegistered ( String agentName, String sensorName, Parameters userParam ) throws Exception 
	{
		System.err.println ( "Pd_AudioEventServer Says: " + agentName + " registered: " + sensorName );
		return userParam;
	}
}
