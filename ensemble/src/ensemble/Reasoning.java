/******************************************************************************

Copyright 2011 Leandro Ferrari Thomaz

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

package ensemble;

import ensemble.Constants.EA_STATE;
import ensemble.Constants.MA_STATE;
import ensemble.clock.TimeUnit;
import ensemble.clock.VirtualClockHelper;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.core.behaviours.ThreadedBehaviourFactory;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.AgentState;

// TODO: Auto-generated Javadoc
/**
 * The Class Reasoning.
 */
public class Reasoning extends MusicalAgentComponent {

	/**
	 * The Enum ReasoningMode.
	 */
	enum ReasoningMode {/** The reactive. */
REACTIVE, /** The periodic. */
 PERIODIC, /** The cyclic. */
 CYCLIC};
	
	/** The reasoning mode. */
	private ReasoningMode reasoningMode;
	
	/** The reasoning period. */
	private long reasoningPeriod = 0;
	
	/** The tbf. */
	ThreadedBehaviourFactory tbf = new ThreadedBehaviourFactory();
	
	/** The cyclic behaviour. */
	Behaviour cyclicBehaviour = null;
	
	/** The clock. */
	VirtualClockHelper clock;

	// TODO No caso de batch, que o process é chamado por uma thread, o que acontece com os métodos newSense e needActuatuion nesse caso?!?!
	/* (non-Javadoc)
	 * @see ensemble.LifeCycle#start()
	 */
	@Override
	public final boolean start() {
		
		// Sets component type
		setType(Constants.COMP_REASONING);
		
		Command cmd = new Command(getAddress(), "/console", "CREATE");
		cmd.addParameter("AGENT", getAgent().getAgentName());
		cmd.addParameter("COMPONENT", getComponentName());
		cmd.addParameter("CLASS", this.getClass().toString());
		cmd.addParameter("TYPE", getType());
		cmd.addUserParameters(parameters);
		sendCommand(cmd);
		
		// Gets clock service
		clock = getAgent().getClock();

		// Sets reasoning mode
		String rm = parameters.get(Constants.PARAM_REASONING_MODE, "REACTIVE");
		if (rm.equals("PERIODIC")) {
			reasoningMode = ReasoningMode.PERIODIC;
			reasoningPeriod = Long.valueOf(parameters.get(Constants.PARAM_PERIOD, "100"));
			
		}
		else if (rm.equals("CYCLIC")) {
			reasoningMode = ReasoningMode.CYCLIC;
		}
		else {
			reasoningMode = ReasoningMode.REACTIVE;
		}
		
		// TODO Verificar quais são os eventHandlers necessários para o funcionamento do raciocinio
		
		// Calls user initialization code
		if (!init()) {
			return false;
		}
		
		// Sets the agent's state to INITIALIZED
		setState(EA_STATE.INITIALIZED);

		// Informs the console
		cmd = new Command(getAddress(), "/console", "UPDATE");
		cmd.addParameter("AGENT", getAgent().getAgentName());
		cmd.addParameter("COMPONENT", getComponentName());
		cmd.addParameter("STATE", "INITIALIZED");
		sendCommand(cmd);
		
		// Cycle Behaviour que controla o raciocínio
		switch (reasoningMode) {
		case PERIODIC:
			cyclicBehaviour = new TickerBehaviour(getAgent(), reasoningPeriod) {
				@Override
				protected void onTick() {
					try {
						process();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			getAgent().addBehaviour(tbf.wrap(cyclicBehaviour));
			break;
		case CYCLIC:
//			cyclicBehaviour = new CyclicBehaviour() {
//				@Override
//				public void action() {
//					try {
//						process();
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			};
			cyclicBehaviour = new ReasonCyclic(getAgent());
			getAgent().addBehaviour(tbf.wrap(cyclicBehaviour));
			break;
		default:
			break;
		}

		return true;

	}
	
	/* (non-Javadoc)
	 * @see ensemble.LifeCycle#stop()
	 */
	@Override
	public final boolean stop() {

		// Removes the CyclicBehaviour
		if (cyclicBehaviour != null) {
			if (cyclicBehaviour instanceof TickerBehaviour) {
				((TickerBehaviour)cyclicBehaviour).stop();
			}
			else {
				((ReasonCyclic)cyclicBehaviour).stop();
			}
			getAgent().removeBehaviour(cyclicBehaviour);
		}
		
		// Calls user finalization method
		if (!finit()) {
			return false;
		}
		
		// Sets the agent's state to 
		setState(Constants.EA_STATE.FINALIZED);
		
		Command cmd = new Command(getAddress(), "/console", "DESTROY");
		cmd.addParameter("AGENT", getAgent().getAgentName());
		cmd.addParameter("COMPONENT", getComponentName());
		sendCommand(cmd);
		
		return true;
	}
	
	/**
	 * Sets the wake up.
	 *
	 * @param time the new wake up
	 */
	public void setWakeUp(long time) {
		
		// No caso de processamento Batch, coloca o Agente para dormir até o primeiro turno
		getAgent().getClock().schedule(getAgent(), new ReasonBatch(), time);
		
	}
	
	/**
	 * The Class ReasonBatch.
	 */
	class ReasonBatch implements Runnable {

		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
		
			// Apenas processa o raciocínio se o agente estiver ativo
			if (getAgent().state == MA_STATE.REGISTERED) {
			
//				MusicalAgent.logger.info("[" + getAgent().getAgentName() + "] " + "Iniciei o raciocínio");
				
				try {
					process();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
//				if (getAgent().getProperty(Constants.PROCESS_MODE, null).equals(Constants.MODE_BATCH)) {
//					//System.out.println(getAgent().getAgentName() + " foi dormir!");
//					// TODO E se quiser dormir mais de 1 turno???
//					long when = (long)clock.getCurrentTime(TimeUnit.TURNS) + 1; 
//					setWakeUp(when);
//				}
//				
				getAgent().reasoningProcessDone(getComponentName());
				
			}
			
		}
		
	}
	
	/**
	 * Processamento cíclico do raciocínio.
	 *
	 * @author lfthomaz
	 */
	private class ReasonCyclic extends SimpleBehaviour {

		/** The terminated. */
		private boolean terminated = false;
		
		/**
		 * Instantiates a new reason cyclic.
		 *
		 * @param a the a
		 */
		public ReasonCyclic(Agent a) {
			super(a);
		}
		
		/* (non-Javadoc)
		 * @see jade.core.behaviours.Behaviour#action()
		 */
		public void action() {
			try {
				process();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/* (non-Javadoc)
		 * @see jade.core.behaviours.Behaviour#done()
		 */
		@Override
		public boolean done() {
			return terminated;
		}

		/**
		 * Stop.
		 */
		public void stop() {
			terminated = true;
		}
	
	}
	
	//--------------------------------------------------------------------------------
	// User implemented methods
	//--------------------------------------------------------------------------------

	/**
	 * Chamado no momento em que um novo EventHandler é registrado no Agente Musical.
	 *
	 * @param evtHdl the evt hdl
	 * @throws Exception the exception
	 */
	protected void eventHandlerRegistered(EventHandler evtHdl) throws Exception {};
	
	/**
	 * Chamado no momento em que um novo EventHandler é registrado no Agente Musical.
	 *
	 * @param evtHdl the evt hdl
	 * @throws Exception the exception
	 */
	protected void eventHandlerDeregistered(EventHandler evtHdl) throws Exception {};

	/**
	 * Chamado no momento que o Agente Musical recebe um novo evento (e o raciocínio está registrado no Sensor).
	 *
	 * @param sourceSensor the source sensor
	 * @param instant the instant
	 * @param duration the duration
	 * @throws Exception the exception
	 */
	public void newSense(Sensor sourceSensor, double instant, double duration) throws Exception {};

	/**
	 * Chamado no caso de eventos frequentes, quando existe a necessidade de uma ação.
	 *
	 * @param sourceActuator the source actuator
	 * @param workingFrame the working frame
	 * @throws Exception the exception
	 */
	public void needAction(Actuator sourceActuator, long workingFrame) throws Exception {};
	
	/**
	 * Chamado no caso de eventos frequentes, quando existe a necessidade de uma ação.
	 *
	 * @param sourceActuator the source actuator
	 * @param instant the instant
	 * @param duration the duration
	 * @throws Exception the exception
	 */
	public void needAction(Actuator sourceActuator, double instant, double duration) throws Exception {};

	/**
	 * Método de processamento do raciocínio.
	 *
	 * @throws Exception the exception
	 */
	public void process() throws Exception {}

}
