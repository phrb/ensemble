
	/******************************************************************************

	Copyright 2011 

	This file is part of Ensemble.

	Ensemble is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	Ensemble is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with Ensemble.  If not, see <http://www.gnu.org/licenses/>.

	******************************************************************************/

	package ensemble.audio.jack;

	import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Hashtable;

import jjack.JackCallback;
import jjack.JackPortFlags;
import jjack.jjack;
import jjack.jjackConstants;
import ensemble.Actuator;
import ensemble.Command;
import ensemble.Constants;
import ensemble.EventHandler;
import ensemble.Reasoning;
import ensemble.audio.AudioConstants;
import ensemble.clock.TimeUnit;
import ensemble.memory.Memory;
import ensemble.memory.MemoryException;


	// TODO: Auto-generated Javadoc
/**
	 * The Class JACKInputToMemoryReasoning.
	 */
	public class JACKInputToMemoryReasoning extends Reasoning {

		// Log
//		public static Logger logger = Logger.getMyLogger(MusicalAgent.class.getName());

		// JACK
		/** The client_name. */
		String 						client_name;
		
		/** The client. */
		long					 	client;
		
		/** The callback start time. */
		double 						callbackStartTime;
		
		/** The period. */
		double 						period;
		
		/** The step. */
		double 						step = 1/44100.0;
		
		/** The mapping. */
		Hashtable<String,String> mapping = new Hashtable<String, String>();
		
		/** The ports. */
		Hashtable<String, Long> ports = new Hashtable<String,Long>(2);
		
		// Actuator
		/** The mouths. */
		Hashtable<String,Actuator> mouths = new Hashtable<String, Actuator>(2);
		
		/** The mouth memories. */
		Hashtable<String,Memory> mouthMemories = new Hashtable<String, Memory>(2);
		
		
		// Reasoning state
		/**
		 * The Enum ReasoningState.
		 */
		enum ReasoningState {
			
			/** The not defined. */
			NOT_DEFINED,
			
			/** The bypass. */
			BYPASS,
			
			/** The stopped. */
			STOPPED,
			
			/** The error. */
			ERROR
		}
		
		/** The state. */
		ReasoningState state = ReasoningState.NOT_DEFINED;
		
		
		/* (non-Javadoc)
		 * @see ensemble.MusicalAgentComponent#init()
		 */
		@Override
		public boolean init() {
			
			String[] str = getParameter("mapping", "").split(";");
			for (int i = 0; i < str.length; i++) {
				String[] str2 = str[i].split("-");
				if (str2.length == 1) {
					mapping.put(str2[0], "");
				}
				else if (str2.length == 2) {
					mapping.put(str2[0], str2[1]);
				} 
				else {
					System.err.println("[" + this.getAgent().getAgentName() + ":" + getComponentName()+ "] " + "no mapping in parameters!");
				}
			}

			// JACK
			client_name = Constants.FRAMEWORK_NAME+"_"+getAgent().getAgentName()+"_"+getComponentName();
			client = jjack.jack_client_open(client_name, new Process());
			if (client == 0) {
				System.err.println("[" + getAgent().getAgentName() + ":" + getComponentName() + "] JACK server not running... JACK will not be available!");
	            return false;
			}

			state = ReasoningState.BYPASS;
			return true;
			
		}

		/* (non-Javadoc)
		 * @see ensemble.MusicalAgentComponent#finit()
		 */
		@Override
		public boolean finit() {
			
			jjack.jack_client_close(client);

			return true;
			
		}

		/* (non-Javadoc)
		 * @see ensemble.Reasoning#eventHandlerRegistered(ensemble.EventHandler)
		 */
		@Override
		protected void eventHandlerRegistered(EventHandler evtHdl) {
			
			if (evtHdl instanceof Actuator && evtHdl.getEventType().equals(AudioConstants.EVT_TYPE_AUDIO)) {
				Actuator mouth = (Actuator)evtHdl;
				String actuatorName = mouth.getComponentName();
				if (mapping.containsKey(actuatorName)) {
					mouth.registerListener(this);
					mouths.put(actuatorName, mouth);
					Memory auxMemory = getAgent().getKB().getMemory(mouth.getComponentName() + Constants.SUF_AUXILIAR_MEMORY);
					//if(auxMemory!=null)System.out.println("Memory available");
		            
					mouthMemories.put(actuatorName, auxMemory);
					period = Double.valueOf(mouth.getParameter(Constants.PARAM_PERIOD))/1000.0;
					// Activates the JACK client
					if (jjack.jack_activate(client) != 0) {
						System.err.println("[" + getAgent().getAgentName() + ":" + getComponentName() + "] Cannot activate JACK client... JACK will not be available!");
						return;
					}
					ports.put(actuatorName, jjack.jack_port_register(client, 
												actuatorName,
												jjackConstants.JACK_DEFAULT_AUDIO_TYPE, 
												JackPortFlags.JackPortIsInput));
					// If specified, connects the port
					String connectPort = mapping.get(actuatorName);
					if (connectPort != null && !connectPort.equals("")) {
						// Searches the desired playback port
						String[] capture_ports = jjack.jack_get_ports(client, connectPort, null, JackPortFlags.JackPortIsOutput);
						if (capture_ports == null) {
							System.err.println("[" + getAgent().getAgentName() + ":" + getComponentName() + "] Cannot find any physical capture ports");
							return;
						}
						if (capture_ports.length > 1) {
							System.err.println("[" + getAgent().getAgentName() + ":" + getComponentName() + "] More than one port with that name");
//							return;
						}
						// Connects the port
						if (jjack.jack_connect(client, capture_ports[0], client_name+":"+actuatorName) != 0) {
							System.err.println("[" + getAgent().getAgentName() + ":" + getComponentName() + "] Cannot connect playback ports");
							return;
						}
					}
				}
			}

		}

		/* (non-Javadoc)
		 * @see ensemble.Reasoning#eventHandlerDeregistered(ensemble.EventHandler)
		 */
		@Override
		protected void eventHandlerDeregistered(EventHandler evtHdl) {
			if (evtHdl instanceof Actuator && evtHdl.getEventType().equals(AudioConstants.EVT_TYPE_AUDIO)) {
				String actuatorName = evtHdl.getComponentName();
				if (ports.containsKey(actuatorName)) {
					mouths.remove(actuatorName);
					ports.remove(actuatorName);
					jjack.jack_port_unregister(client, ports.get(actuatorName));
				}
			}
		}

		/* (non-Javadoc)
		 * @see ensemble.Reasoning#needAction(ensemble.Actuator, double, double)
		 */
		@Override
		public void needAction(Actuator sourceActuator, double instant,
				double duration) throws Exception {

//			System.out.println("needAction() - t = " + instant + " até " + (instant+duration));
			// Teoricamente, já vai estar escrito na memória o que deve ser enviado,
			// pois foi preenchido pelo callback do JACK
			mouths.get(sourceActuator.getComponentName()).act();
			
		}
		
	/**
	 * The Class Process.
	 */
	class Process implements JackCallback {

		/** The d buffer. */
		double[] dBuffer;
		
		/** The first call. */
		boolean firstCall = true;
		
		/** The instant. */
		double instant = 0;

		/* (non-Javadoc)
		 * @see jjack.JackCallback#process(int, double)
		 */
		@Override
		public int process(int nframes, double time) {

			if (firstCall) {
				// It must be 2 frames plus the latency in the future
				instant = getAgent().getClock()
						.getCurrentTime(TimeUnit.SECONDS)
						+ (period * 2)
						+ (nframes * step);
				dBuffer = new double[nframes];
				firstCall = false;
			}

			double duration = (double) (nframes) * step;
			// System.out.println("Java::callback - t = " + instant + " até " +
			// (instant+duration));

			switch (state) {
			case BYPASS:

				for (String actuatorName : ports.keySet()) {
					FloatBuffer fIn = jjack
							.jack_port_get_buffer(ports.get(actuatorName),
									nframes).order(ByteOrder.LITTLE_ENDIAN)
							.asFloatBuffer();
					int ptr = 0;
					while (fIn.remaining() > 0) {
						dBuffer[ptr++] = (double) fIn.get();
					}

					// System.out.println("Process initiated");

					Memory auxMemory = getAgent().getKB().getMemory(
							actuatorName + Constants.SUF_AUXILIAR_MEMORY);
					

					try {

						auxMemory.writeMemory(dBuffer, instant, duration,
								TimeUnit.SECONDS);
						
						//System.out.println("Salvou na memoria auxiliar");
						// System.out.println("Instant: " + instant + " Duration: " + duration );
						// 0.011609977324263039

						
					} catch (MemoryException e) {
						e.printStackTrace();
					}
				}

				break;

			default:
				firstCall = true;
				break;
			}

			instant = instant + duration;

			return 0;

		}

	};

	/* (non-Javadoc)
	 * @see ensemble.MusicalAgentComponent#processCommand(ensemble.Command)
	 */
	public void processCommand(Command cmd) {

		if (cmd.getCommand().equals(JACKConstants.CMD_STOP)) {
			state = ReasoningState.STOPPED;
		}
		if (cmd.getCommand().equals(JACKConstants.CMD_START)) {
			state = ReasoningState.BYPASS;
		} 

	}
		
		
		
	}
