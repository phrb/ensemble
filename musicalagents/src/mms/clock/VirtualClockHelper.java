package mms.clock;

import jade.core.Agent;
import jade.core.ServiceHelper;
import jade.core.behaviours.Behaviour;

public interface VirtualClockHelper extends ServiceHelper {

	public void updateClock(long units);

	public void updateClock();
	
	public double getCurrentTime(TimeUnit unit);

	// TODO Deve ser tipo o TimerTask do Java
//	public void schedule(Agent a, Behaviour b, long wakeupTime);
	public void schedule(Agent a, Runnable b, long wakeupTime);
	
	public void execute(Agent a, Runnable b);
	
}
