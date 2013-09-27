package mms.apps.demo;

import mms.Constants;
import mms.MusicalAgent;
import mms.Parameters;

public class PlayerAgent extends MusicalAgent {
	
	@Override
	public boolean configure() {

		Parameters arguments;
		
		arguments = new Parameters();
		arguments.put(Constants.PARAM_EVT_TYPE, "AUDIO");
		arguments.put(Constants.PARAM_POSITION, getParameter(Constants.PARAM_POSITION));
		addComponent("Mouth", "mms.Actuator", arguments);
		
		arguments = new Parameters();
		arguments.put(Constants.PARAM_EVT_TYPE, "MOVEMENT");
		addComponent("Legs", "mms.Actuator", arguments);
		
		arguments = new Parameters();
		arguments.put(Constants.PARAM_EVT_TYPE, "MOVEMENT");
		addComponent("Eyes", "mms.Sensor", arguments);
		
		arguments = new Parameters();
		addComponent("AudioReasoning", "mms.audio.file.AudioFileInputReasoning", arguments);
				
		arguments = new Parameters();
		arguments.put("REASONING_MODE", "PERIODIC");
		arguments.put("PERIOD", "50");
		arguments.put("waypoints", "(30;40;0) 8.0:(-40;-30;0) 7.0:(20;-40;0) 10.0");
		arguments.put("loop", "true");
		addComponent("MovementReasoning", "mms.movement.MovementReasoning", arguments);

		return true;
	}
	
	@Override
	public boolean init() {
		getKB().registerFact("filename", getParameter("filename"), false);
		return true;
	}
	
}
