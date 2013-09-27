package mms.audio.jack;

import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Hashtable;

import jade.util.Logger;

import mms.Constants;
import mms.EventHandler;
import mms.Reasoning;
import mms.Sensor;
import mms.audio.AudioConstants;
import mms.clock.TimeUnit;
import mms.memory.Memory;
import mmsjack.JACKCallback;
import mmsjack.JackPortFlags;
import mmsjack.SWIGTYPE_p_jack_client_t;
import mmsjack.SWIGTYPE_p_jack_port_t;
import mmsjack.mmsjack;
import mmsjack.mmsjackConstants;

public class JACKOutputReasoning extends Reasoning {

	// Log
//	public static Logger logger = Logger.getMyLogger(MusicalAgent.class.getName());

	// JACK
	String 						client_name;
	SWIGTYPE_p_jack_client_t 	client;
	double 						callbackStartTime;
	double 						step = 1/44100.0;
	Hashtable<String,String> mapping = new Hashtable<String, String>();
	Hashtable<String, SWIGTYPE_p_jack_port_t> ports = new Hashtable<String,SWIGTYPE_p_jack_port_t>(2);
	
	// Sensor
	Hashtable<String,Memory> earMemories = new Hashtable<String, Memory>(2);

	@Override
	public boolean init() {
		
		String[] str = getParameter("mapping", "").split("-");
		if (str.length == 1) {
			mapping.put(str[0], "");
		}
		else if (str.length == 2) {
			mapping.put(str[0], str[1]);
		} 
		else {
			System.err.println("[" + this.getAgent().getAgentName() + ":" + getComponentName()+ "] " + "no mapping in parameters!");
		}
		
		// JACK
		client_name = Constants.FRAMEWORK_NAME+"_"+getAgent().getAgentName()+"_"+getComponentName();
		client = mmsjack.jack_client_open(client_name, new Process());
		if (client == null) {
			System.err.println("[" + getAgent().getAgentName() + ":" + getComponentName() + "] JACK server not running... JACK will not be available!");
            return false;
		}
		// Activates the JACK client
		if (mmsjack.jack_activate(client) != 0) {
			System.err.println("[" + getAgent().getAgentName() + ":" + getComponentName() + "] Cannot activate JACK client... JACK will not be available!");
			return false;
		}

		return true;
		
	}

	@Override
	public boolean finit() {

		mmsjack.jack_client_close(client);
		
		return true;
		
	}

	@Override
	protected void eventHandlerRegistered(EventHandler evtHdl) {
		
		if (evtHdl instanceof Sensor && evtHdl.getEventType().equals(AudioConstants.EVT_TYPE_AUDIO)) {
			Sensor ear = (Sensor)evtHdl;
			String sensorName = ear.getComponentName();
			if (mapping.containsKey(sensorName)) {
				earMemories.put(ear.getComponentName(), getAgent().getKB().getMemory(ear.getComponentName()));
				// Creats a JACK client
				ports.put(sensorName, mmsjack.jack_port_register(client, 
											sensorName,
											mmsjackConstants.JACK_DEFAULT_AUDIO_TYPE, 
											JackPortFlags.JackPortIsOutput));
				// If specified, connects the port
				String connectPort = mapping.get(sensorName);
				if (connectPort != null && !connectPort.equals("")) {
					// Searches the desired playback port
					String[] playback_ports = mmsjack.jack_get_ports(client, connectPort, null, JackPortFlags.JackPortIsInput);
					if (playback_ports == null) {
						System.err.println("[" + getAgent().getAgentName() + ":" + getComponentName() + "] Cannot find any physical playback ports");
						return;
					}
					if (playback_ports.length > 1) {
						System.err.println("[" + getAgent().getAgentName() + ":" + getComponentName() + "] More than one port with that name");
//						return;
					}
					// Connects the port
					if (mmsjack.jack_connect(client, client_name+":"+sensorName, playback_ports[0]) != 0) {
						System.err.println("[" + getAgent().getAgentName() + ":" + getComponentName() + "] Cannot connect playback ports");
						return;
					}
				}
			}
		}
		
	}

	@Override
	protected void eventHandlerDeregistered(EventHandler evtHdl) {
		if (evtHdl instanceof Sensor && evtHdl.getEventType().equals(AudioConstants.EVT_TYPE_AUDIO)) {
			String sensorName = evtHdl.getComponentName();
			if (ports.containsKey(sensorName)) {
				ports.remove(sensorName);
				mmsjack.jack_port_unregister(client, ports.get(sensorName));
			}
		}
	}
		
	class Process implements JACKCallback {

		boolean firstCall = true;
		double instant = 0;

		@Override
		public int process(int nframes, double time) {

			if (firstCall) {
				instant = getAgent().getClock().getCurrentTime(TimeUnit.SECONDS) - (nframes * step);
				firstCall = false;
			}
			
			double duration = (double)(nframes) * step;
			
			for (String sensorName : ports.keySet()) {
				Memory earMemory = earMemories.get(sensorName);
				double[] buf = (double[])earMemory.readMemory(instant, duration, TimeUnit.SECONDS);
				FloatBuffer fOut = mmsjack.jack_port_get_buffer(ports.get(sensorName), nframes).order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer();
				int ptr = 0;
				while (fOut.remaining() > 0) {
					fOut.put((float)buf[ptr++]);
				}
			}
			
			instant = instant + duration;
			
			return 0;
			
		}

	};
	
}
