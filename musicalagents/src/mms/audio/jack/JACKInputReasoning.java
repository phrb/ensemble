package mms.audio.jack;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Hashtable;

import jade.core.ServiceException;
import jade.domain.introspection.SuspendedAgent;
import jade.util.Logger;

import mms.Actuator;
import mms.Constants;
import mms.EventHandler;
import mms.MusicalAgent;
import mms.Reasoning;
import mms.Sensor;
import mms.audio.AudioConstants;
import mms.audio.jack.JACKOutputReasoning.Process;
import mms.clock.TimeUnit;
import mms.memory.Memory;
import mms.memory.MemoryException;
import mms.tools.AudioTools;
import mmsjack.JACKCallback;
import mmsjack.JackPortFlags;
import mmsjack.SWIGTYPE_p_jack_client_t;
import mmsjack.SWIGTYPE_p_jack_port_t;
import mmsjack.mmsjack;
import mmsjack.mmsjackConstants;

public class JACKInputReasoning extends Reasoning {

	// Log
//	public static Logger logger = Logger.getMyLogger(MusicalAgent.class.getName());

	// JACK
	String 						client_name;
	SWIGTYPE_p_jack_client_t 	client;
	double 						callbackStartTime;
	double 						period;
	double 						step = 1/44100.0;
	Hashtable<String,String> mapping = new Hashtable<String, String>();
	Hashtable<String, SWIGTYPE_p_jack_port_t> ports = new Hashtable<String,SWIGTYPE_p_jack_port_t>(2);
	
	// Actuator
	Hashtable<String,Actuator> mouths = new Hashtable<String, Actuator>(2);
	Hashtable<String,Memory> mouthMemories = new Hashtable<String, Memory>(2);
	
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
		
		if (evtHdl instanceof Actuator && evtHdl.getEventType().equals(AudioConstants.EVT_TYPE_AUDIO)) {
			Actuator mouth = (Actuator)evtHdl;
			String actuatorName = mouth.getComponentName();
			if (mapping.containsKey(actuatorName)) {
				mouths.put(actuatorName, mouth);
				mouthMemories.put(actuatorName, getAgent().getKB().getMemory(mouth.getComponentName()));
				period = Double.valueOf(mouth.getParameter(Constants.PARAM_PERIOD))/1000.0;
				// Creats a JACK client
				ports.put(actuatorName, mmsjack.jack_port_register(client, 
											actuatorName,
											mmsjackConstants.JACK_DEFAULT_AUDIO_TYPE, 
											JackPortFlags.JackPortIsOutput));
				// If specified, connects the port
				String connectPort = mapping.get(actuatorName);
				if (connectPort != null && !connectPort.equals("")) {
					// Searches the desired playback port
					String[] capture_ports = mmsjack.jack_get_ports(client, connectPort, null, JackPortFlags.JackPortIsOutput);
					if (capture_ports == null) {
						System.err.println("[" + getAgent().getAgentName() + ":" + getComponentName() + "] Cannot find any physical capture ports");
						return;
					}
					if (capture_ports.length > 1) {
						System.err.println("[" + getAgent().getAgentName() + ":" + getComponentName() + "] More than one port with that name");
//						return;
					}
					// Connects the port
					if (mmsjack.jack_connect(client, client_name+":"+actuatorName, capture_ports[0]) != 0) {
						System.err.println("[" + getAgent().getAgentName() + ":" + getComponentName() + "] Cannot connect playback ports");
						return;
					}
				}
			}
		}

	}

	@Override
	protected void eventHandlerDeregistered(EventHandler evtHdl) {
		if (evtHdl instanceof Actuator && evtHdl.getEventType().equals(AudioConstants.EVT_TYPE_AUDIO)) {
			String actuatorName = evtHdl.getComponentName();
			if (ports.containsKey(actuatorName)) {
				mouths.remove(actuatorName);
				ports.remove(actuatorName);
				mmsjack.jack_port_unregister(client, ports.get(actuatorName));
			}
		}
	}

	@Override
	public void needAction(Actuator sourceActuator, double instant,
			double duration) throws Exception {

//		System.out.println("needAction() - t = " + instant + " até " + (instant+duration));
		// Teoricamente, já vai estar escrito na memória o que deve ser enviado,
		// pois foi preenchido pelo callback do JACK
		mouths.get(sourceActuator).act();
		
	}
	
	class Process implements JACKCallback {

		double[] dBuffer;
		boolean firstCall = true;
		double instant = 0;

		@Override
		public int process(int nframes, double time) {

			if (firstCall) {
				// It must be 2 frames plus the latency in the future
				instant = getAgent().getClock().getCurrentTime(TimeUnit.SECONDS) + 
							(period * 2) + 
							(nframes * step);
				dBuffer = new double[nframes];
				firstCall = false;
			}
			
			double duration = (double)(nframes) * step;
//			System.out.println("Java::callback - t = " + instant + " até " + (instant+duration));
			
			for (String actuatorName : ports.keySet()) {
				FloatBuffer fIn = mmsjack.jack_port_get_buffer(ports.get(actuatorName), nframes).order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer();
				int ptr = 0;
				while (fIn.remaining() > 0) {
					dBuffer[ptr++] = (double)fIn.get();
				}
				Memory mouthMemory = mouthMemories.get(actuatorName);
				try {
					mouthMemory.writeMemory(dBuffer, instant, duration, TimeUnit.SECONDS);
//					System.out.println(now + " " + getAgent().getClock().getCurrentTime() + " Escrevi do instante " + (instant+period) + " até " + (instant+period+duration));
				} catch (MemoryException e) {
					e.printStackTrace();
				}
			}
			
			instant = instant + duration;
			
			return 0;
			
		}

	};

	
}
