package mms.audio.javasound;

import java.util.ArrayList;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import mms.Actuator;
import mms.Constants;
import mms.Constants.EA_STATE;
import mms.EventHandler;
import mms.Reasoning;
import mms.audio.AudioConstants;
import mms.clock.TimeUnit;
import mms.memory.Memory;
import mms.memory.MemoryException;
import mms.tools.AudioTools;

public class JavaInputReasoning extends Reasoning {

	/**
	 * Actuator used to send audio events
	 */
	Actuator 	mouth;
	/**
	 * Actuator memory
	 */
	Memory 		mouthMemory;
	
	/**
	 * Number of samples in a frame
	 */
	int chunk_size;
	double period;

	/**
	 * Mic line
	 */
	TargetDataLine 		targetDataLine;
	ArrayList<double[]> queue = new ArrayList<double[]>();
	byte[] 				buffer;
	
	/**
	 * Init the Mic Line
	 */
	@Override
	public boolean init() {
		AudioFormat audioFormat = new AudioFormat(44100f, 16, 1, true, false);
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);
		if (!AudioSystem.isLineSupported(info)) {
//			getAgent().logger.severe("[" + getComponentName() + "] " + "Line not supported");
			return false;
		}
		try
		{
			targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
			targetDataLine.open(audioFormat);
			System.out.println("TargetDataLine initialized!");
		}
		catch (LineUnavailableException e)
		{
//			getAgent().logger.severe("[" + getComponentName() + "] " + "Line not available");
			return false;
		}
		return true;
	}
	
	/**
	 * Finalizes the Mic Line
	 */
	@Override
	public boolean finit() {
		targetDataLine.stop();
		targetDataLine.close();
		return true;
	}
	
	@Override
	protected void eventHandlerRegistered(EventHandler evtHdl) {

		// Checar se é um atuador de som e adicionar na lista
		if (evtHdl instanceof Actuator && evtHdl.getEventType().equals(AudioConstants.EVT_TYPE_AUDIO)) {
			mouth = (Actuator)evtHdl;
			mouth.registerListener(this);
			mouthMemory = getAgent().getKB().getMemory(mouth.getComponentName());
			chunk_size = Integer.parseInt(mouth.getParameter(Constants.PARAM_CHUNK_SIZE, "0"));
			period = Double.valueOf(mouth.getParameter("PERIOD"))/1000.0;
			buffer = new byte[chunk_size*2];
		}

	}

	@Override
	public void needAction(Actuator sourceActuator, double instant, double duration) {
		System.out.println("[" + (long)getAgent().getClock().getCurrentTime(TimeUnit.MILLISECONDS) + "] instant = " + instant + " queue size = " + queue.size());
		if (!queue.isEmpty()) {
			double[] chunk = queue.remove(0); 
			try {
				mouthMemory.writeMemory(chunk, instant + (2*period), duration, TimeUnit.SECONDS);
				mouth.act();
			} catch (MemoryException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("ERROR: Queue is empty!");
		}
	}
	
	@Override
	public void process() throws Exception {
		if (getState() == EA_STATE.INITIALIZED && targetDataLine.isRunning() && buffer != null) {
			int frames = targetDataLine.read(buffer, 0, buffer.length);
			double[] samples = AudioTools.convertByteDouble(buffer, 0, buffer.length);
//			System.out.println("samples = " + samples.length);
			queue.add(samples);
		}
	}
	
}
