package mms.comm;

import mms.Acting;
import mms.Constants;
import mms.Event;
import mms.LifeCycle;
import mms.MMSAgent;
import mms.Parameters;
import mms.Sensing;
import jade.core.Agent;

public abstract class Comm implements LifeCycle {
	
	protected MMSAgent 		myAgent;
	protected Sensing 		mySensor;
	protected Acting		myActuator;
	protected String 		myAccessPoint;
	
	protected Parameters 	parameters;
	
	/*
	 *  Controlam se um Comm está apto a receber eventos
	 */
	public boolean sensing 	= true;					
	/*
	 *  Controlam se o Comm está apto a enviar eventos
	 */
	public boolean actuating 	= true;					
	
	@Override
	public void setParameters(Parameters parameters) {
		this.parameters = parameters;
	}

	@Override
	public Parameters getParameters() {
		return this.parameters;
	}

	@Override
	public final boolean start() {
		
		if (parameters == null) {
			System.err.println("[COMM] Parameters not set! Comm not initialized!");
			return false;
		}
		
		try {
			myAgent = (MMSAgent)parameters.getObject(Constants.PARAM_COMM_AGENT);
			mySensor = (Sensing)parameters.getObject(Constants.PARAM_COMM_SENSING);
			myActuator = (Acting)parameters.getObject(Constants.PARAM_COMM_ACTING);
			myAccessPoint = parameters.get(Constants.PARAM_COMM_ACCESS_POINT, "");
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		if (myAgent == null) {
			System.err.println("[COMM] There is no agent in parameters! Comm not initialized!");
			return false;
		}
				
		if (mySensor == null && myActuator == null) {
			System.err.println("[COMM] There is no sensing or acting in parameters! Comm not initialized!");
			return false;
		}

		if (myAccessPoint.equals("")) {
			System.err.println("[COMM] There is no access point in parameters! Comm not initialized!");
			return false;
		}
		
		if (mySensor == null) {
			sensing = false;
		}
		if (myActuator == null) {
			actuating = false;
		}
		
		// Calls the user initializarion method
		if (!init()) {
			return false;
		}
		
		return true;
	}
	
	@Override
	public final boolean stop() {
		return true;
	}

	//--------------------------------------------------------------------------------
	// User implemented methods
	//--------------------------------------------------------------------------------
	
	/**
	 * Event listener
	 */
	public abstract void receive(Event evt);

	/**
	 * Event source
	 */
	public abstract void send(Event evt);
	
	@Override
	public boolean parameterUpdate(String name, String newValue) {
		return false;
	}

}
