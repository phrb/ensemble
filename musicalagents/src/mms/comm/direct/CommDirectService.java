package mms.comm.direct;

import java.util.concurrent.ConcurrentHashMap;

import mms.Event;
import mms.MusicalAgent;
import mms.comm.Comm;
import jade.core.Agent;
import jade.core.BaseService;
import jade.core.ServiceException;
import jade.util.Logger;

public class CommDirectService extends BaseService {

	//----------------------------------------------------------
	// Log
//	private static Logger logger = Logger.getMyLogger(MusicalAgent.class.getName());
	
	//----------------------------------------------------------
	// Nome do servi�o
	public static final String NAME = "CommDirect";

	//----------------------------------------------------------
	private ConcurrentHashMap<String, Comm> comms = new ConcurrentHashMap<String, Comm>();
	
	@Override
	public String getName() {
		return NAME;
	}

	public void boot(jade.core.Profile p) throws ServiceException {
		
		super.boot(p);
		
        System.out.println("[" + getName() + "] CommDirect service started");

	}
	
	public CommDirectHelper getHelper(Agent a) {
		
		return new CommDirectHelperImp();
	
	}
	
	public class CommDirectTask implements Runnable {

		Comm comm;
		Event evt;
		
		public CommDirectTask(Comm comm, Event evt) {
			this.comm 	= comm;
			this.evt 	= evt;
		}
		
		@Override
		public void run() {
			if (comm != null) {
				comm.receive(evt);
			} else {
				System.err.println("[ERROR] Comm object does not exist!");
			}
		}
		
	}

	public class CommDirectHelperImp implements CommDirectHelper {

		@Override
		public void init(Agent a) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void register(String agentName, String accessPoint, Comm comm) {

			comms.put(agentName + ":" + accessPoint, comm);
			
		}

		@Override
		public void deregister(String agentName, String accessPoint) {

			comms.remove(agentName + ":" + accessPoint);
			
		}

		@Override
		public void send(Event evt) {

			CommDirect comm = (CommDirect)comms.get(evt.destAgentName + ":" + evt.destAgentCompName);
			// TODO talvez utilizar uma pool de threads?!
//			logger.info("[CommDirect] " + " Vou chamar a Thread!");
			CommDirectTask task = new CommDirectTask(comm, evt);
			(new Thread(task)).start();
//			comm.receive(evt);
			
		}
	}
	
}
