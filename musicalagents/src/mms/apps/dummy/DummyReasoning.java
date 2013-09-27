package mms.apps.dummy;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import mms.Actuator;
import mms.EventHandler;
import mms.MusicalAgent;
import mms.Reasoning;
import mms.Sensor;
import mms.clock.TimeUnit;
import mms.memory.Memory;
import mms.memory.MemoryException;

public class DummyReasoning extends Reasoning {

	Actuator actuator;
	Memory actuatorMemory;
	
	Sensor sensor;
	Memory sensorMemory;
	
	@Override
	public boolean init() {
		return true;
	}
	
	@Override
	protected void eventHandlerRegistered(EventHandler evtHdl) throws Exception {
		if (evtHdl instanceof Actuator && evtHdl.getEventType().equals("DUMMY")) {
			evtHdl.registerListener(this);
			actuatorMemory = getAgent().getKB().getMemory(evtHdl.getComponentName());
			actuator = (Actuator)evtHdl;
		}
		if (evtHdl instanceof Sensor && evtHdl.getEventType().equals("DUMMY")) {
			evtHdl.registerListener(this);
			sensorMemory = getAgent().getKB().getMemory(evtHdl.getComponentName());
			sensor = (Sensor)evtHdl;
		}
	}
	
	@Override
	public void newSense(Sensor sourceSensor, double instant, double duration) throws Exception {
//		System.out.println("newSense()");
		double[] buf = (double[])sensorMemory.readMemory(instant, duration, TimeUnit.SECONDS);
	}
	
	@Override
	public void needAction(Actuator sourceActuator, double instant, double duration) throws Exception {
		double[] buf = new double[1024];
		// Armazena o chunk de saída na memória e atua
		try {
			actuatorMemory.writeMemory(buf, instant, duration, TimeUnit.SECONDS);
			actuator.act();
		} catch (MemoryException e1) {
//			MusicalAgent.logger.warning("[" + getAgent().getAgentName() + ":" + getComponentName() + "] " + "Não foi possível armazenar na memória");
		}
	}

}
