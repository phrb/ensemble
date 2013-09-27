package mms.apps.lm;

import mms.Event;
import mms.MusicalAgent;
import mms.Parameters;
import mms.Sensor;

public class LM_FoodSensor extends Sensor {

	@Override
	public boolean configure() {
		setEventType("ENERGY");
		return true;
	}

	@Override
	protected void process(Event evt) {

		// Ao receber um sense de comida, adiciona a quantidade de energia
		float energy = Float.valueOf(getAgent().getKB().readFact("Energy")); 
		float food = Float.valueOf((String)evt.objContent);		
		getAgent().getKB().updateFact("Energy", (String.valueOf(energy + food)));
		
	}

}
