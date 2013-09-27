package mms.apps.dummy;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import mms.Command;
import mms.Event;
import mms.Sensor;
import mms.clock.TimeUnit;

public class DummySensor extends Sensor {

	PrintWriter file_perf;
	
	@Override
	public boolean init() {
		try {
			file_perf = new PrintWriter(new FileOutputStream("out_"+getAgent().getAgentName()+".txt"), false);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	@Override
	protected void process(Event evt) throws Exception {
//		System.out.println("[Sensor] process()");
		// Performance
		long wf = (long)Math.ceil((getAgent().getClock().getCurrentTime(TimeUnit.MILLISECONDS) - startTime) / period);
		if (wf == evt.frame) {
			file_perf.printf("%d\n", wf);
			file_perf.flush();
		}
	}
	
}
