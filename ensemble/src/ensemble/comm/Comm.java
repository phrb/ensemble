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

package ensemble.comm;

import ensemble.Acting;
import ensemble.Constants;
import ensemble.Event;
import ensemble.LifeCycle;
import ensemble.EnsembleAgent;
import ensemble.Parameters;
import ensemble.Sensing;
import jade.core.Agent;

// TODO: Auto-generated Javadoc
/**
 * The Class Comm.
 */
public abstract class Comm implements LifeCycle {
	
	/** The my agent. */
	protected EnsembleAgent 		myAgent;
	
	/** The my sensor. */
	protected Sensing 		mySensor;
	
	/** The my actuator. */
	protected Acting		myActuator;
	
	/** The my access point. */
	protected String 		myAccessPoint;
	
	/** The parameters. */
	protected Parameters 	parameters;
	
	/** Controlam se um Comm está apto a receber eventos. */
	public boolean sensing 	= true;					
	
	/** Controlam se o Comm está apto a enviar eventos. */
	public boolean actuating 	= true;					
	
	/* (non-Javadoc)
	 * @see ensemble.LifeCycle#setParameters(ensemble.Parameters)
	 */
	@Override
	public void setParameters(Parameters parameters) {
		this.parameters = parameters;
	}

	/* (non-Javadoc)
	 * @see ensemble.LifeCycle#getParameters()
	 */
	@Override
	public Parameters getParameters() {
		return this.parameters;
	}

	/* (non-Javadoc)
	 * @see ensemble.LifeCycle#start()
	 */
	@Override
	public final boolean start() {
		
		if (parameters == null) {
			System.err.println("[COMM] Parameters not set! Comm not initialized!");
			return false;
		}
		
		try {
			myAgent = (EnsembleAgent)parameters.getObject(Constants.PARAM_COMM_AGENT);
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
	
	/* (non-Javadoc)
	 * @see ensemble.LifeCycle#stop()
	 */
	@Override
	public final boolean stop() {
		return true;
	}

	//--------------------------------------------------------------------------------
	// User implemented methods
	//--------------------------------------------------------------------------------
	
	/**
	 * Event listener.
	 *
	 * @param evt the evt
	 */
	public abstract void receive(Event evt);

	/**
	 * Event source.
	 *
	 * @param evt the evt
	 */
	public abstract void send(Event evt);
	
	/* (non-Javadoc)
	 * @see ensemble.LifeCycle#parameterUpdate(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean parameterUpdate(String name, String newValue) {
		return false;
	}

}
