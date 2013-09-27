package mms.comm.direct;

import mms.Event;
import mms.comm.Comm;
import jade.core.ServiceHelper;

public interface CommDirectHelper extends ServiceHelper {

	public void register(String agentName, String accessPoint, Comm comm);
	
	public void deregister(String agentName, String accessPoint);

	public void send(Event evt);
	
}	