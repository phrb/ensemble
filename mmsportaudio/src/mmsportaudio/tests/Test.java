package mmsportaudio.tests;

import java.nio.ByteBuffer;

import mmsportaudio.PaCallback;
import mmsportaudio.PaDeviceInfo;
import mmsportaudio.PaHostApiInfo;
import mmsportaudio.PaStreamInfo;
import mmsportaudio.PaStreamParameters;
import mmsportaudio.portaudio;

public class Test {
	
	enum Running_Mode {LIST, DEFAULT, CONFIGURABLE};
	static Running_Mode mode = Running_Mode.DEFAULT;
	
	static int device = 2;
	static int channel = 0;
	
	static portaudio pa;
	
	public static long createStream(int device, int channel) {
		// Gets DeviceInfo
		PaDeviceInfo info = pa.Pa_GetDeviceInfo(device);
		System.out.println("channels = " + info.getMaxOutputChannels());
		int channelCount = info.getMaxOutputChannels();
		// Sets Parameters
		PaStreamParameters outputParameters = new PaStreamParameters();
		outputParameters.setChannelCount(channelCount);
		outputParameters.setDevice(device);
		outputParameters.setHostApiSpecificStreamInfo(null);
		outputParameters.setSampleFormat(pa.SIGNED_INTEGER_16);
		outputParameters.setSuggestedLatency(info.getDefaultLowOutputLatency());
		// Opens the stream
		Callback cb = new Callback();
		cb.device = device;
		cb.channel = channel;
		cb.channelCount = channelCount;
		System.out.println(device + " " + channel);
		long stream = pa.Pa_OpenStream(null, outputParameters, 44100.0, 256, 0, cb);
		System.out.println("BLA");
		// StremInfo
//		PaStreamInfo streamInfo = pa.Pa_GetStreamInfo(stream);
//		System.out.println("latency = " + streamInfo.getOutputLatency());
		//
		System.out.println("Java::stream = " + stream);
		return stream;
	}
	
	public static void main(String[] args) {
		
		if (args.length == 1 && args[0].equals("list")) {
			mode = Running_Mode.LIST;
		}
		else if (args.length == 2) {
			mode = Running_Mode.CONFIGURABLE;
			device = Integer.valueOf(args[0]);
			channel = Integer.valueOf(args[1]);
		}
		
		pa = portaudio.getInstance();
//		int err = Portaudio.Pa_Initialize();
		
		switch (mode) {
		case LIST:
			System.out.println("devices = " + pa.Pa_GetDeviceCount());
			for (int i = 0; i < pa.Pa_GetDeviceCount(); i++) {
				PaDeviceInfo deviceInfo = pa.Pa_GetDeviceInfo(i);
				PaHostApiInfo hostApiInfo = pa.Pa_GetHostApiInfo(deviceInfo.getHostApi());
				System.out.printf("[%d] %s::%s (%d,%d)\n", 
										i, 
										hostApiInfo.getName(), 
										deviceInfo.getName(), 
										deviceInfo.getMaxInputChannels(), 
										deviceInfo.getMaxOutputChannels());
			}
			System.exit(0);
			break;
		case DEFAULT:
			System.out.println("Opening default stream...");
			Callback cb = new Callback();
			cb.channelCount = 1;
			cb.channel = 0;
			long stream = pa.Pa_OpenDefaultStream( 
												0, 
												1, 
												pa.SIGNED_INTEGER_16, 
												44100, 
												256, 
												cb);
			System.out.println("Java::stream = " + stream);
			// StremInfo
			PaStreamInfo streamInfo = pa.Pa_GetStreamInfo(stream);
			System.out.println("latency = " + streamInfo.getOutputLatency());
			System.out.println("Opening stream...");
			System.out.println("Starting stream...");
			int err = pa.Pa_StartStream(stream);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			System.out.println("Stoping stream...");
			err = pa.Pa_StopStream(stream);
			
			System.out.println("Closing stream...");
			err = pa.Pa_CloseStream(stream);
			
			break;
		case CONFIGURABLE:
			stream = createStream(device, channel);
			System.out.println("Java::stream = " + stream);
			long stream2 = createStream(device, channel+1);
			System.out.println("Java::stream = " + stream2);
			
			System.out.println("Starting stream...");
			err = pa.Pa_StartStream(stream);
			err = pa.Pa_StartStream(stream2);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			System.out.println("Stoping stream...");
			err = pa.Pa_StopStream(stream);
			err = pa.Pa_StopStream(stream2);
			
			System.out.println("Closing stream...");
			err = pa.Pa_CloseStream(stream);
			err = pa.Pa_StopStream(stream2);
			break;
		}

		portaudio.deleteInstance();
//		err = Portaudio.Pa_Terminate();

	}

}

class Callback extends PaCallback {

	static double freq = 440.0;
	static double fs = 44100.0;
	static double step = 1/fs;

	double t = 0;
	
	public int channelCount;
	public int device;
	public int channel;
	
	@Override
	public int callback(long stream, ByteBuffer input, ByteBuffer output,
			long frameCount, double inputBufferAdcTime,
			double currentTime, double outputBufferDacTime) {

		System.out.println("Java::callback() - " + device + ":" + channel + " - t = " + t + " - " + frameCount + " - " + outputBufferDacTime);
		while (output.remaining() > 0) {
			for (int i = 0; i < channelCount; i++) {
				// If it is the chosen channel
				if (i == channel) {
					double dSample = 0.5 * Math.sin(2 * Math.PI * freq * t);
					int nSample = (int) Math.round(dSample * 32767.0); // scaling and conversion to integer
					output.put((byte)(nSample & 0xFF));
					output.put((byte)((nSample >> 8) & 0xFF));
				}
				// Else, silence
				else {
					output.put((byte)(0 & 0xFF));
					output.put((byte)((0 >> 8) & 0xFF));
				}
			}
			t = t + step;
		}
		return paContinue;
		
	}
	
	@Override
	public void hook(long stream) {
		System.out.println("Java::hook() + " + stream);
	}		
}