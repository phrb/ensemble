package mmsportaudio;

import java.nio.ByteBuffer;

public abstract class PaCallback {

	public final static int paContinue = 0;
	public final static int paComplete = 1;
	public final static int paAbort = 2;
	
//		public abstract int callback(ByteBuffer input, ByteBuffer output, long frameCount, PaStreamCallbackTimeInfo timeInfo);
	public abstract int callback(long stream,
									ByteBuffer input, 
									ByteBuffer output, 
									long frameCount, 
									double inputBufferAdcTime, 
									double currentTime, 
									double outputBufferDacTime);

	public abstract void hook(long stream);

}
