package mmsportaudio;

public class portaudio {

	// Loads the portaudio JNI interface
	static {
		if (System.getProperty("os.name").startsWith("Windows")) {
			if (System.getProperty("sun.arch.data.model").equals("32")) {
				System.loadLibrary("portaudio_x86");
				System.loadLibrary("mmsportaudio_x86");
			} else {
				System.loadLibrary("portaudio_x64");
				System.loadLibrary("mmsportaudio_x64");
			}
		}
		else if (System.getProperty("os.name").startsWith("Mac OS X")) {
			System.loadLibrary("mmsportaudio");
		}
		else if (System.getProperty("os.name").startsWith("Unix")) {
			System.loadLibrary("mmsportaudio");
		}		
	}
	
	public final static int FLOAT_32 = 0x00000001;
	public final static int SIGNED_INTEGER_8 = 0x00000010;
	public final static int UNSIGNED_INTEGER_8 = 0x00000020;
	public final static int SIGNED_INTEGER_16 = 0x00000008;
	public final static int SIGNED_INTEGER_24 = 0x00000004;
	public final static int SIGNED_INTEGER_32 = 0x00000002;
	
	 // Private constructor prevents instantiation from other classes
	private portaudio() {

	}
	
	private static int instances = 0;
	
	public synchronized static portaudio getInstance() {
		if (instances == 0) {
			Pa_Initialize();
		}
		instances++;
		return PortaudioHolder.INSTANCE;
	}
	
	public static void deleteInstance() {
		if (instances > 0) {
			instances--;
			if (instances == 0) {
				Pa_Terminate();
			}
		}
	}
	
	private static class PortaudioHolder {
		public static final portaudio INSTANCE = new portaudio();
	}
	
  public int Pa_GetVersion() {
    return portaudioJNI.Pa_GetVersion();
  }

  public String Pa_GetVersionText() {
    return portaudioJNI.Pa_GetVersionText();
  }

  public String Pa_GetErrorText(int errorCode) {
    return portaudioJNI.Pa_GetErrorText(errorCode);
  }

  private static int Pa_Initialize() {
	  System.out.println("Starting portaudio...");
	  return portaudioJNI.Pa_Initialize();
  }

  private static int Pa_Terminate() {
	System.out.println("Terminating portaudio...");
    return portaudioJNI.Pa_Terminate();
  }

  public int Pa_GetHostApiCount() {
    return portaudioJNI.Pa_GetHostApiCount();
  }

  public int Pa_GetDefaultHostApi() {
    return portaudioJNI.Pa_GetDefaultHostApi();
  }

  public PaHostApiInfo Pa_GetHostApiInfo(int hostApi) {
    long cPtr = portaudioJNI.Pa_GetHostApiInfo(hostApi);
    return (cPtr == 0) ? null : new PaHostApiInfo(cPtr, false);
  }

  public int Pa_HostApiTypeIdToHostApiIndex(SWIGTYPE_p_PaHostApiTypeId type) {
    return portaudioJNI.Pa_HostApiTypeIdToHostApiIndex(SWIGTYPE_p_PaHostApiTypeId.getCPtr(type));
  }

  public int Pa_HostApiDeviceIndexToDeviceIndex(int hostApi, int hostApiDeviceIndex) {
    return portaudioJNI.Pa_HostApiDeviceIndexToDeviceIndex(hostApi, hostApiDeviceIndex);
  }

  public PaHostErrorInfo Pa_GetLastHostErrorInfo() {
    long cPtr = portaudioJNI.Pa_GetLastHostErrorInfo();
    return (cPtr == 0) ? null : new PaHostErrorInfo(cPtr, false);
  }

  public int Pa_GetDeviceCount() {
    return portaudioJNI.Pa_GetDeviceCount();
  }

  public int Pa_GetDefaultInputDevice() {
    return portaudioJNI.Pa_GetDefaultInputDevice();
  }

  public int Pa_GetDefaultOutputDevice() {
    return portaudioJNI.Pa_GetDefaultOutputDevice();
  }

  public PaDeviceInfo Pa_GetDeviceInfo(int device) {
    long cPtr = portaudioJNI.Pa_GetDeviceInfo(device);
    return (cPtr == 0) ? null : new PaDeviceInfo(cPtr, false);
  }

  public int Pa_IsFormatSupported(PaStreamParameters inputParameters, PaStreamParameters outputParameters, double sampleRate) {
    return portaudioJNI.Pa_IsFormatSupported(PaStreamParameters.getCPtr(inputParameters), inputParameters, PaStreamParameters.getCPtr(outputParameters), outputParameters, sampleRate);
  }

  public long Pa_OpenStream(PaStreamParameters inputParameters, PaStreamParameters outputParameters, double sampleRate, long framesPerBuffer, long streamFlags, Object streamCallback) {
    return portaudioJNI.Pa_OpenStream(PaStreamParameters.getCPtr(inputParameters), inputParameters, PaStreamParameters.getCPtr(outputParameters), outputParameters, sampleRate, framesPerBuffer, streamFlags, streamCallback);
  }

  public long Pa_OpenDefaultStream(int numInputChannels, int numOutputChannels, long sampleFormat, double sampleRate, long framesPerBuffer, Object streamCallback) {
    return portaudioJNI.Pa_OpenDefaultStream(numInputChannels, numOutputChannels, sampleFormat, sampleRate, framesPerBuffer, streamCallback);
  }

  public int Pa_CloseStream(long stream) {
    return portaudioJNI.Pa_CloseStream(stream);
  }

  public int Pa_StartStream(long stream) {
    return portaudioJNI.Pa_StartStream(stream);
  }

  public int Pa_StopStream(long stream) {
    return portaudioJNI.Pa_StopStream(stream);
  }

  public int Pa_AbortStream(long stream) {
    return portaudioJNI.Pa_AbortStream(stream);
  }

  public int Pa_IsStreamStopped(long stream) {
    return portaudioJNI.Pa_IsStreamStopped(stream);
  }

  public int Pa_IsStreamActive(long stream) {
    return portaudioJNI.Pa_IsStreamActive(stream);
  }

  public PaStreamInfo Pa_GetStreamInfo(long stream) {
    long cPtr = portaudioJNI.Pa_GetStreamInfo(stream);
    return (cPtr == 0) ? null : new PaStreamInfo(cPtr, false);
  }

  public double Pa_GetStreamTime(long stream) {
    return portaudioJNI.Pa_GetStreamTime(stream);
  }

  public double Pa_GetStreamCpuLoad(long stream) {
    return portaudioJNI.Pa_GetStreamCpuLoad(stream);
  }

  public int Pa_ReadStream(long stream, SWIGTYPE_p_void buffer, long frames) {
    return portaudioJNI.Pa_ReadStream(stream, SWIGTYPE_p_void.getCPtr(buffer), frames);
  }

  public int Pa_WriteStream(long stream, SWIGTYPE_p_void buffer, long frames) {
    return portaudioJNI.Pa_WriteStream(stream, SWIGTYPE_p_void.getCPtr(buffer), frames);
  }

  public int Pa_GetStreamReadAvailable(long stream) {
    return portaudioJNI.Pa_GetStreamReadAvailable(stream);
  }

  public int Pa_GetStreamWriteAvailable(long stream) {
    return portaudioJNI.Pa_GetStreamWriteAvailable(stream);
  }

  public int Pa_GetSampleSize(long format) {
    return portaudioJNI.Pa_GetSampleSize(format);
  }

  public void Pa_Sleep(int msec) {
    portaudioJNI.Pa_Sleep(msec);
  }

}
