package mms.apps.lm;

import mms.Actuator;
import mms.Parameters;

public class LM_FoodActuator extends Actuator {

	@Override
	public boolean configure() {
		setEventType("ENERGY");
		return true;
	}

}
