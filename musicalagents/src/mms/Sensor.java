package mms;

//import java.io.PrintWriter;

import java.util.ArrayList;

import mms.Constants.EA_STATE;
import mms.Constants.EH_STATUS;
import mms.clock.TimeUnit;
import mms.memory.MemoryException;

public class Sensor extends EventHandler implements Sensing {
	
	protected ArrayList<Event> early_events = new ArrayList<Event>();
	
	@Override
	public final boolean start() {

		// Sets component type
		setType(Constants.COMP_SENSOR);
		
		Command cmd = new Command(getAddress(), "/console", "CREATE");
		cmd.addParameter("AGENT", getAgent().getAgentName());
		cmd.addParameter("COMPONENT", getComponentName());
		cmd.addParameter("CLASS", this.getClass().toString());
		cmd.addParameter("TYPE", getComponentType());
		cmd.addParameter("EVT_TYPE", parameters.get("EVT_TYPE"));
		cmd.addUserParameters(parameters);
		sendCommand(cmd);

		if (!super.start()) {
			return false;
		}
		
		myComm.sensing = true;

		// Calls user initialization code
		if (!init()) {
			return false;
		}
		
		// Sets the agent's state to INITIALIZED
		setState(EA_STATE.INITIALIZED);
		
		cmd = new Command(getAddress(), "/console", "UPDATE");
		cmd.addParameter("AGENT", getAgent().getAgentName());
		cmd.addParameter("COMPONENT", getComponentName());
		cmd.addParameter("STATE", "INITIALIZED");
		sendCommand(cmd);

		return true;

	}
	
	@Override
	public final boolean stop() {

		// Calls user initialization code
		if (!finit()) {
			return false;
		}
		
		if (!super.stop()) {
			return false;
		}
		
		Command cmd = new Command(getAddress(), "/console", "DESTROY");
		cmd.addParameter("AGENT", getAgent().getAgentName());
		cmd.addParameter("COMPONENT", getComponentName());
		sendCommand(cmd);
		
		return true; 
		
	}
	
	// Chamado por um raciocínio para registrá-lo como listener dos eventos
	public void registerListener(Reasoning reasoning) {
		listeners.add(reasoning);
	}
	
	// Método chamado pelo Comm ao receber um evento
	public void sense(Event evt) {
		
		if (status == EH_STATUS.REGISTERED && evt.eventType.equals(eventType)) {
//			MusicalAgent.logger.info("[" + getAgent().getAgentName() + ":" + getComponentName() + "] " + "Event received");
//			System.out.println("[" + getAgent().getAgentName() + ":" + getComponentName() + "] " + "Event received - " + evt.objContent);
			
			// Chama o método do usuário
			// TODO Melhor antes ou depois? Ou ter os dois?
			try {
				process(evt);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			// Armazenar o evento na Base de Conhecimentos
			try {
				if (myMemory == null) {
					System.err.println("ERROR: no memory registered");
				} else {
					myMemory.writeMemory(evt.objContent, evt.instant, evt.duration, TimeUnit.SECONDS);
				}
//				System.out.println("[" + getAgent().getAgentName() + "] Guardei na memória um evento no instante " + evt.instant + " de duração " + evt.duration);
			} catch (MemoryException e1) {
//				MusicalAgent.logger.warning("[" + getAgent().getAgentName() + ":" + getComponentName() + "] " + "Não foi possível armazenar na memória");
			}
			
			// Avisar os raciocínios registrados
			for (Reasoning reasoning : listeners) {
				try {
					reasoning.newSense(this, evt.instant, evt.duration);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			// No caso de event BATCH, envia o ACK
			if (getAgent().getProperty(Constants.PROCESS_MODE, null).equals(Constants.MODE_BATCH)) {
				Command cmd = new Command(getAddress(), "/" + Constants.FRAMEWORK_NAME + "/" + Constants.ENVIRONMENT_AGENT, Constants.CMD_BATCH_EVENT_ACK);
				sendCommand(cmd);
			}
			
//			MusicalAgent.logger.info("[" + getAgent().getAgentName() + ":" + getComponentName() + "] " + "Processei evento " + evt.timestamp);
		}
		else if (evt.eventType.equals(eventType)) {
			// Stores this event for later, since it was sent between the REGSITER-EVENT and REGISTER-EVENT-ACK
			early_events.add(evt);
		}
		
	}

	//--------------------------------------------------------------------------------
	// User implemented methods
	//--------------------------------------------------------------------------------

	/**
	 * Faz o pré-processamento do evento recebido.
	 */
	protected void process(Event evt) throws Exception  {
//		System.out.println("[Sensor] Entrei no process()");
	}

}
