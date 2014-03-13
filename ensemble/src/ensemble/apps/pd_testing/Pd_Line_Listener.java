package ensemble.apps.pd_testing;

import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

public class Pd_Line_Listener implements LineListener 
{
	@Override
	public void update(LineEvent event) 
	{
		System.err.println ( "Line Has: " + event.getType ( ) );		
	}
}
