package mms.apps.dummy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Enumeration;

import mms.Command;
import mms.Constants;
import mms.Event;
import mms.EventServer;
import mms.Parameters;
import mms.clock.TimeUnit;
import mms.memory.EventMemory;
import mms.memory.Memory;
import mms.world.World;

public class DummyEventServer extends EventServer {
	
	private World world;
	
//	PrintWriter file_perf;
	
	private int numberFrames = 1;
	private int eventsReceived = 0;
	private int eventsReceivedInFrame = 0;
	private int numberAgents = 0;
	
	@Override
	public boolean configure() {
		setEventType("DUMMY");
		if (parameters.containsKey(Constants.PARAM_PERIOD)) {
			String[] str = (parameters.get(Constants.PARAM_PERIOD)).split(" ");
			setEventExchange(Integer.valueOf(str[0]), Integer.valueOf(str[1]), Integer.valueOf(str[2]), Integer.valueOf(str[3]));
		} else {
			setEventExchange(500, 200, 400, 1000);
		}
		return true;
	}

	@Override
	public boolean init() {
		world = envAgent.getWorld();
//		try {
//			file_perf = new PrintWriter(new FileOutputStream("out_Environment.txt"), false);
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
		return true;
	}

	@Override
	public boolean finit() {
		
		// Fazer as contas!!!
		int agentsEventsReceived = 0;
		// abrir todos os arquivos dos agentes
//		for (int i = 0; i < array.length; i++) {
//			
//		}
//		File dir = new File("./tests");
//		File[] files = dir.listFiles(new FilenameFilter() {
//			public boolean accept(File arg0, String arg1) {
//				return arg1.startsWith("out_");
//			}
//		});
//		for (File file : files) {
//			try {
//				BufferedReader br = new BufferedReader(new FileReader(file));
//				while (br.readLine() != null) {
//					agentsEventsReceived++;
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			file.delete();
//		}
		System.out.println("Number of Agents = " + numberAgents);
		System.out.println("Events received ES = " + eventsReceived);
		System.out.println("Events received MAs = " + agentsEventsReceived);
		double expected = numberAgents * numberFrames;
		double ratio = (agentsEventsReceived / expected) * 100;
		System.out.printf("Ratio = %.1f", ratio);
		
//		// Cair fora
//		System.exit(0);
		
		return true;
	}
	
	@Override
	protected Parameters actuatorRegistered(String agentName, String eventHandlerName, Parameters userParam) throws Exception {

		numberAgents++;
		
		// Parâmetos
		Parameters userParameters = new Parameters();
//		userParameters.put(Constants.PARAM_CHUNK_SIZE, String.valueOf());
		
		// Cria uma memória para o atuador
		if (world.getEntityStateAttribute(agentName, "DUMMY") == null) {
	    	world.addEntityStateAttribute(agentName, "DUMMY", createMemory("DUMMY", userParameters));
		}
    	
		return userParameters;

	}
	
	@Override
	protected Parameters sensorRegistered(String agentName, String eventHandlerName, Parameters userParam) throws Exception {
		
		// Parâmetos
		Parameters userParameters = new Parameters();

		// Cria uma memória para o atuador
		if (world.getEntityStateAttribute(agentName, "DUMMY") == null) {
	    	world.addEntityStateAttribute(agentName, "DUMMY", createMemory("DUMMY", userParameters));
		}

		return userParameters;

	}
	
	@Override
	protected void processSense(Event evt) throws Exception {
 
		if (evt.frame == workingFrame) {
			eventsReceivedInFrame++;
			// Recebe o evento e armazena na memória
			Memory memory = (Memory)world.getEntityStateAttribute(evt.oriAgentName, "DUMMY");
			memory.writeMemory(evt);
		} else {
			System.out.println("PROBLEMAS!");
		}
		
	}
	
	@Override
	protected void process() throws Exception {
		
		if (workingFrame == numberFrames) {
//		if (workingFrame > numberFrames) {

//			Command cmd = new Command(getAddress(), "/"+Constants.FRAMEWORK_NAME+"/"+Constants.ENVIRONMENT_AGENT, "REMOVE_EVENT_SERVER");
//			cmd.addParameter("NAME", getEventType());
//			Command cmd = new Command(getAddress(), "/"+Constants.FRAMEWORK_NAME+"/"+Constants.ENVIRONMENT_AGENT, "STOP_SIMULATION");
//			sendCommand(cmd);
			
		} else {
		
			double instant = startTime + (workingFrame * period);
			
			eventsReceived += eventsReceivedInFrame;
//			file_perf.printf("%d - %d\n", workingFrame, eventsReceived);
//			file_perf.flush();
	 		System.out.println(envAgent.getClock().getCurrentTime(TimeUnit.MILLISECONDS) + " - process() - wf = " + workingFrame + "(" +  eventsReceivedInFrame + ")");
			eventsReceivedInFrame = 0;

			for (Enumeration<String> s = sensors.keys(); s.hasMoreElements();) {
	
				String s_key = s.nextElement();
				String[] sensor = s_key.split(":");
	
//				System.out.println(instant + " " + evt1.frame);
				Memory memory = (Memory)world.getEntityStateAttribute(sensor[0], "DUMMY");
				Event evt1 = (Event)memory.readMemory(instant, TimeUnit.MILLISECONDS);
				if (evt1 != null && evt1.frame == workingFrame) {
					// Cria o evento a ser enviado para o sensor
					Event evt = new Event();
					evt.destAgentName = sensor[0];
					evt.destAgentCompName = sensor[1];
					double[] buf = new double[1024];
					evt.objContent = buf;
		//			evt.instant = instant;
		//			evt.duration = (double)(CHUNK_SIZE * STEP);
					
					// Puts the newly created event in the output queue
					addOutputEvent(evt.destAgentName, evt.destAgentCompName, evt);
				}

				
			}
		
			
		}
		
	}

}
