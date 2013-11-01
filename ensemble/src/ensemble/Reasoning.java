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
import ensemble.clock.VirtualClockHelper;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.core.behaviours.ThreadedBehaviourFactory;
import jade.core.behaviours.TickerBehaviour;

/**
 * This is the brains of an Ensemble Musical Agent.
 */
public class Reasoning extends MusicalAgentComponent 
{
	/**
	 * Possible Modes.
	 */
	enum ReasoningMode 
	{
		REACTIVE, 
		PERIODIC, 
		CYCLIC	
	};	
	/** The reasoning mode. */
	private ReasoningMode reasoning_mode;
	/** The reasoning period. */
	private long reasoning_period = 0;
	/** The threaded_behavior_factory. */
	ThreadedBehaviourFactory threaded_behavior_factory = new ThreadedBehaviourFactory();
	/** The cyclic behaviour. */
	Behaviour cyclic_behaviour = null;
	/** The clock. */
	VirtualClockHelper clock;
	// TODO No caso de batch, que o process é chamado por uma thread, o que acontece com os métodos newSense e needActuatuion nesse caso?!?!
	/* (non-Javadoc)
	 * @see ensemble.LifeCycle#start()
	 */
	@Override
	public final boolean start ( ) 
	{
		// Sets component type
		setType(Constants.COMP_REASONING);
		
		Command command = new Command ( getAddress ( ), "/console", "CREATE" );
		command.addParameter ( "AGENT", getAgent ( ).getAgentName ( ) );
		command.addParameter ( "COMPONENT", getComponentName ( ) );
		command.addParameter ( "CLASS", this.getClass ( ).toString ( ) );
		command.addParameter ( "TYPE", getType ( ) );
		command.addUserParameters ( parameters );
		sendCommand ( command );
		// Gets clock service
		clock = getAgent ( ).getClock ( );
		// Sets reasoning mode
		String reasoning_mode_name = parameters.get ( Constants.PARAM_REASONING_MODE, "REACTIVE" );
		if ( reasoning_mode_name.equals( "PERIODIC" ) ) 
		{
			reasoning_mode = ReasoningMode.PERIODIC;
			reasoning_period = Long.valueOf ( parameters.get ( Constants.PARAM_PERIOD, "100" ) );	
		}
		else if ( reasoning_mode_name.equals ( "CYCLIC" ) )
		{
			reasoning_mode = ReasoningMode.CYCLIC;
		}
		else 
		{
			reasoning_mode = ReasoningMode.REACTIVE;
		}
		// TODO Verificar quais são os eventHandlers necessários para o funcionamento do raciocinio
		// Calls user initialization code
		if ( ! ( init ( ) ) ) 
		{
			return false;
		}
		// Sets the agent's state to INITIALIZED
		setState ( EA_STATE.INITIALIZED );
		// Informs the console
		command = new Command ( getAddress ( ), "/console", "UPDATE" );
		command.addParameter ( "AGENT", getAgent ( ).getAgentName ( ) );
		command.addParameter ( "COMPONENT", getComponentName ( ) );
		command.addParameter ( "STATE", "INITIALIZED" );
		sendCommand ( command );
		// Cycle Behaviour que controla o raciocínio
		switch ( reasoning_mode ) 
		{
		case PERIODIC:
			cyclic_behaviour = new TickerBehaviour(getAgent(), reasoning_period) {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				protected void onTick() 
				{
					try 
					{
						process();
					}
					catch (Exception e) 
					{
						e.printStackTrace();
					}
				}
			};
			getAgent().addBehaviour(threaded_behavior_factory.wrap(cyclic_behaviour));
			break;
			
		case CYCLIC:
			cyclic_behaviour = new ReasonCyclic(getAgent());
			getAgent().addBehaviour(threaded_behavior_factory.wrap(cyclic_behaviour));
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
	public final boolean stop ( ) 
	{
		// Removes the CyclicBehaviour
		if ( cyclic_behaviour != null ) 
		{
			if ( cyclic_behaviour instanceof TickerBehaviour ) 
			{
				( ( TickerBehaviour ) cyclic_behaviour ).stop ( );
			}
			else 
			{
				( ( ReasonCyclic ) cyclic_behaviour ).stop ( );
			}
			getAgent ( ).removeBehaviour ( cyclic_behaviour );
		}
		// Calls user finalization method
		if ( ! ( finit ( ) ) ) 
		{
			return false;
		}
		// Sets the agent's state to 
		setState(Constants.EA_STATE.FINALIZED);
		
		Command command = new Command(getAddress(), "/console", "DESTROY");
		command.addParameter("AGENT", getAgent().getAgentName());
		command.addParameter("COMPONENT", getComponentName());
		sendCommand(command);
		return true;
	}
	/**
	 * Sets the wake up.
	 *
	 * @param time the new wake up
	 */
	public void setWakeUp ( long time ) 
	{	
		// No caso de processamento Batch, coloca o Agente para dormir até o primeiro turno
		getAgent ( ).getClock ( ).schedule ( getAgent ( ), new ReasonBatch ( ), time );	
	}
	
	/**
	 * The Class ReasonBatch.
	 */
	class ReasonBatch implements Runnable 
	{
		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		public void run ( ) 
		{
			// Apenas processa o raciocínio se o agente estiver ativo
			if ( getAgent ( ).state == MA_STATE.REGISTERED ) 
			{	
				try 
				{
					process();
				} 
				catch ( Exception e ) 
				{
					e.printStackTrace ( );
				}
				getAgent ( ).reasoningProcessDone ( getComponentName ( ) );
			}
		}
	}
	/**
	 * Processamento cíclico do raciocínio.
	 *
	 * @author lfthomaz
	 */
	private class ReasonCyclic extends SimpleBehaviour 
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		/** The terminated. */
		private boolean terminated = false;	
		/**
		 * Instantiates a new reason cyclic.
		 *
		 * @param agent the a
		 */
		public ReasonCyclic ( Agent agent )
		{
			super ( agent );
		}
		/* (non-Javadoc)
		 * @see jade.core.behaviours.Behaviour#action()
		 */
		public void action ( ) 
		{
			try 
			{
				process ( );
			} 
			catch ( Exception e ) 
			{
				e.printStackTrace ( );
			}
		}
		/* (non-Javadoc)
		 * @see jade.core.behaviours.Behaviour#done()
		 */
		@Override
		public boolean done ( ) 
		{
			return terminated;
		}
		/**
		 * Stop.
		 */
		public void stop ( ) 
		{
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