package mms.apps.demo;

import mms.Constants;
import mms.MusicalAgent;
import mms.Parameters;

public class AvatarAgent extends MusicalAgent {
	
	@Override
	public boolean configure() {

		Parameters arguments = new Parameters();
		arguments.put(Constants.PARAM_EVT_TYPE, "AUDIO");
		arguments.put("mapping", getParameter("mapping"));
		addComponent("AudioReasoning", "mms.audio.jack.JACKOutputReasoning", arguments);
		
		arguments = new Parameters();
		arguments.put(Constants.PARAM_EVT_TYPE, "AUDIO");
		arguments.put(Constants.PARAM_POSITION, getParameter(Constants.PARAM_POSITION));
		addComponent("Ear", "mms.Sensor", arguments);
		
		arguments = new Parameters();
		arguments.put(Constants.PARAM_EVT_TYPE, "MOVEMENT");
		addComponent("Legs", "mms.Actuator", arguments);
		
		arguments = new Parameters();
		arguments.put(Constants.PARAM_EVT_TYPE, "MOVEMENT");
		addComponent("Eyes", "mms.Sensor", arguments);
		
		return true;
	}
	
}
