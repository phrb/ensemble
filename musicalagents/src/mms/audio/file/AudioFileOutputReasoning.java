package mms.audio.file;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import javax.sound.sampled.SourceDataLine;

//import jade.util.Logger;

import mms.Constants;
import mms.EventHandler;
import mms.MusicalAgent;
import mms.Reasoning;
import mms.Sensor;
import mms.audio.AudioConstants;
import mms.clock.TimeUnit;
import mms.memory.Memory;
import mms.tools.AudioTools;

public class AudioFileOutputReasoning extends Reasoning {

	// Log
//	public static Logger logger = Logger.getMyLogger(MusicalAgent.class.getName());

	// Memories
	private HashMap<String, Memory> earMemories = new HashMap<String, Memory>();
	private HashMap<String, Integer> numChannels = new HashMap<String, Integer>();

	// Files
	private HashMap<String, FileOutputStream> outFiles = new HashMap<String, FileOutputStream>();

	// Parameters
	int 	device;
	int 	channel;
	int 	maxChannels;
	
	@Override
	public boolean init() {
		
		return true;
		
	}

	public boolean finit() {

		Collection<FileOutputStream> files = outFiles.values();
		for (FileOutputStream file : files) {
			try {
				file.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return true;
	}

	@Override
	protected void eventHandlerRegistered(EventHandler evtHdl) {
	
		if (evtHdl instanceof Sensor && evtHdl.getEventType().equals(AudioConstants.EVT_TYPE_AUDIO)) {

			// Stores ear's memory
			Sensor ear = (Sensor)evtHdl;
			ear.registerListener(this);
			earMemories.put(ear.getComponentName(), getAgent().getKB().getMemory(ear.getComponentName()));
		
			// Assigns a channel in the audio interface to this ear
			if (ear.getParameters().containsKey("CHANNELS")) {
				int channels = Integer.valueOf(ear.getParameter("CHANNELS"));
				numChannels.put(ear.getComponentName(), channels);
				// Creates a file
				for (int n = 0; n < channels; n++) {
					try {
						FileOutputStream file = new FileOutputStream("tests/" + getAgent().getAgentName()+"_"+n+"_out.dat");
						outFiles.put(ear.getComponentName()+"_"+n, file);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}
			} 
			else {
				// Gets the next available channel
				// TODO O QUE FAZER AQUI? TEMOS QUE TER UMA LISTA dos canais já utilizados!
				System.out.println("SEM PARAMETROS DE CANAL!!!");
			}			
			
		}
			
	}

	@Override
	public void newSense(Sensor sourceSensor, double instant, double duration) {

		String earName = sourceSensor.getComponentName();
		Memory earMemory = earMemories.get(earName);
		int channels = numChannels.get(earName);
		if (channels > 1) {
			double[][] buf = (double[][])earMemory.readMemory(instant, duration, TimeUnit.SECONDS);
			for (int n = 0; n < channels; n++) {
//				System.out.println("Vou gravar chunck do canal " + n);
				FileOutputStream file = outFiles.get(earName+"_"+n);
				if (file != null) {
					try {
						file.write(AudioTools.convertDoubleByte(buf[n], 0, buf[n].length));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		else {
			double[] buf = (double[])earMemory.readMemory(instant, duration, TimeUnit.SECONDS);
			FileOutputStream file = outFiles.get(earName);
			if (file != null) {
				try {
					file.write(AudioTools.convertDoubleByte(buf, 0, buf.length));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
			
	}

}
