package ensemble.apps.demo;

import ensemble.Constants;
import ensemble.MusicalAgent;
import ensemble.Parameters;

// TODO: Auto-generated Javadoc
/**
 * The Class AvatarAgent.
 */
public class AvatarAgent extends MusicalAgent {
	
	/* (non-Javadoc)
	 * @see ensemble.EnsembleAgent#configure()
	 */
	@Override
	public boolean configure() {

		Parameters arguments = new Parameters();
		arguments.put(Constants.PARAM_EVT_TYPE, "AUDIO");
		arguments.put("mapping", getParameter("mapping"));
		addComponent("AudioReasoning", "ensemble.audio.jack.JACKOutputReasoning", arguments);
		
		arguments = new Parameters();
		arguments.put(Constants.PARAM_EVT_TYPE, "AUDIO");
		arguments.put(Constants.PARAM_POSITION, getParameter(Constants.PARAM_POSITION));
		addComponent("Ear", "ensemble.Sensor", arguments);
		
		arguments = new Parameters();
		arguments.put(Constants.PARAM_EVT_TYPE, "MOVEMENT");
		addComponent("Legs", "ensemble.Actuator", arguments);
		
		arguments = new Parameters();
		arguments.put(Constants.PARAM_EVT_TYPE, "MOVEMENT");
		addComponent("Eyes", "ensemble.Sensor", arguments);
		
		return true;
	}
	
}
