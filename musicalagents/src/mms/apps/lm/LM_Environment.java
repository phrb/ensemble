package mms.apps.lm;

import mms.EnvironmentAgent;
import mms.Parameters;

public class LM_Environment extends EnvironmentAgent {

	@Override
	public boolean configure() {

		this.addEventServer("mms.apps.lm.LM_MovementEventServer", new Parameters());
		this.addEventServer("mms.apps.lm.LM_SoundEventServer", new Parameters());
//		this.addEventServer("mms.apps.lm.LM_EnergyEventServer", new Parameters());
		this.addEventServer("mms.apps.lm.LM_LifeCycleEventServer", new Parameters());
		
		return true;

	}

	@Override
	protected void preUpdateClock() {
		
		world.getWorldGUI().update();
		
	}
	
}
