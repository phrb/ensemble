package mms.apps.lm;

import mms.Actuator;
import mms.Parameters;

public class LM_MovementActuator extends Actuator {

	@Override
	public boolean configure() {
		setEventType("MOVEMENT");
		return true;
	}

}
