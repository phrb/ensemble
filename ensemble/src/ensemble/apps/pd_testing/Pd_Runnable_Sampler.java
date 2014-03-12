package ensemble.apps.pd_testing;

import org.puredata.core.PdBase;

public class Pd_Runnable_Sampler implements Runnable 
{
	String subpatch = null;
	int patch;
	int ticks;
	short[ ] input;
	short[ ] output;
    private synchronized void open_dsp ( )
    {
    	if ( subpatch == null )
    	{
        	PdBase.sendBang ( patch + Pd_Constants.PROCESSING_ON );
    	}
    	else
    	{
        	PdBase.sendBang ( subpatch + "-" + Pd_Constants.PROCESSING_ON );
    	}
    }
    private synchronized void close_dsp ( )
    {
    	if ( subpatch == null )
    	{
        	PdBase.sendBang ( patch + Pd_Constants.PROCESSING_OFF );
    	}
    	else
    	{
        	PdBase.sendBang ( subpatch + "-" + Pd_Constants.PROCESSING_OFF );
    	}
    }
	public synchronized void start ( String new_subpatch, int new_patch, int ticks, short[ ] new_input, short[ ] new_output )
	{
		subpatch = new_subpatch;
		patch = new_patch;
		input = new_input;
		output = new_output;
	}
	private synchronized void process_pd_ticks ( )
	{
		open_dsp ( );
		/*
		 * Only sends/receives to/from adc/dac.
		 */
		PdBase.process ( ticks, input, output );
        close_dsp ( );
	}
	@Override
	public synchronized void run ( ) 
	{
		process_pd_ticks ( );
	}
}
