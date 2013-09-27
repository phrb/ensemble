package ensemble.apps.pp;

 
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;

import FIPA.DateTime;

import ensemble.Actuator;
import ensemble.Command;
import ensemble.Constants;
import ensemble.EventHandler;
import ensemble.Reasoning;
import ensemble.Sensor;
import ensemble.apps.pp.PP_SampleReasoning.ReasoningState;
import ensemble.audio.AudioConstants;
import ensemble.clock.TimeUnit;
import ensemble.memory.Memory;
import ensemble.memory.MemoryException;
import ensemble.movement.MovementConstants;
import ensemble.router.MessageConstants;
import ensemble.world.Vector;


// TODO: Auto-generated Javadoc
/**
 * Given a set of waypoints and a time constraint to get there, this reasoning tries to walk.
 */
public class PP_OscMovementReasoning extends Reasoning {

	/** The Constant EPSILON. */
	public static final double EPSILON = 1e-14;

	//	private KnowledgeBase kb;
	
	/** The legs. */
	private Actuator	legs;
	
	/** The eyes. */
	private Sensor 		eyes;
	
	/** The antenna. */
	private Sensor 		antenna;
	
	/** The legs memory. */
	private Memory 		legsMemory;
	
	/** The eyes memory. */
	private Memory 		eyesMemory;
	
	/** The antenna memory. */
	private Memory 		antennaMemory;
	
	/** The active. */
	private boolean 			active;
	
	/** The allow change. */
	private boolean 			allowChange;
	
	/** The time diff. */
	private long				timeDiff;
	
	/** The last change. */
	private Date				lastChange;
	
	// Waypoints
	/** The waypoints. */
	private ArrayList<Vector> 	waypoints = new ArrayList<Vector>();
	
	/** The time_constrains. */
	private ArrayList<Double> 	time_constrains = new ArrayList<Double>();
	
	/** The loop. */
	private boolean 			loop = false;
	
	/** The active_waypoint. */
	private int					active_waypoint = 0;;
	
	/** The precision. */
	private double 				precision = 0.01;
	
	/** The last_distance. */
	private double 				last_distance = Double.MAX_VALUE;
	
	/** The total_distance. */
	private double 				total_distance = 0.0;
	
	/** The last_acc. */
	private Vector 				last_acc;
	
	/** The inverted. */
	private boolean 			inverted;
	
	// 
	/** The actual_pos. */
	private Vector 				actual_pos = null;
	
	/** The actual_vel. */
	private Vector 				actual_vel = null;
	
	/** The actual_ori. */
	private Vector 				actual_ori = null;
	
	
	/** The audio file reference. */
	private Hashtable<String, String> audioFileReference =  new Hashtable<String, String>();
	
	/** The sinos. */
	private Hashtable<String, String> sinos =  new Hashtable<String, String>();
	
	/** The tremolos. */
	private Hashtable<String, String> tremolos =  new Hashtable<String, String>();
	
	/** The nylon. */
	private Hashtable<String, String> nylon =  new Hashtable<String, String>();
	
	/** The percussao sino. */
	private Hashtable<String, String> percussaoSino =  new Hashtable<String, String>();
	
	/** The pizzicatos. */
	private Hashtable<String, String> pizzicatos =  new Hashtable<String, String>();
	
	/** The percussoes. */
	private Hashtable<String, String> percussoes =  new Hashtable<String, String>();
	
	/** The curto. */
	private Hashtable<String, String> curto =  new Hashtable<String, String>();
	
	/** The longo. */
	private Hashtable<String, String> longo =  new Hashtable<String, String>();
	
	// 
	/** The max aceleration. */
	private double MAX_ACELERATION = 10.0;
	
	/** The default aceleration. */
	private double DEFAULT_ACELERATION = 0.02;
	
	/** The default duration. */
	private double DEFAULT_DURATION = 0.2;
	
	/** The iso mvt type. */
	private int ISO_MVT_TYPE = 1;
	
	// Direction state
	/**
	 * The Enum DirectionState.
	 */
	enum DirectionState {
		
		/** The not defined. */
		NOT_DEFINED,
		
		/** The up. */
		UP,
		
		/** The down. */
		DOWN,
		
		/** The right. */
		RIGHT,
		
		/** The left. */
		LEFT,
		
		/** The still. */
		STILL
	}
	
	/** The state. */
	DirectionState state = DirectionState.NOT_DEFINED;
	
	/* (non-Javadoc)
	 * @see ensemble.MusicalAgentComponent#init()
	 */
	public boolean init() {
		
//		kb = getAgent().getKB();
		
		String str = getParameter("waypoints", null);
		if (str != null) {
			String[] wps = str.split(":");
			for (int i = 0; i < wps.length; i++) {
				String[] wp = wps[i].split(" "); 
				waypoints.add(Vector.parse(wp[0]));
				time_constrains.add(Double.valueOf(wp[1]));
//				System.out.println("add wp " + waypoints.get(i) + " - time " + time_constrains.get(i));
			}
			loop = Boolean.parseBoolean(getParameter("loop", "false"));
//			System.out.println("loop = " + loop);
		}
		
		active = true;
		allowChange = true;
		
		
		//Inicializa vetores dos samples
		
		//FILES
		sinos.put("1", "media/piano_preparado/sino/sino_.wav");
		sinos.put("2", "media/piano_preparado/sino/sino_abismo_.wav");
		sinos.put("3", "media/piano_preparado/sino/sino_agudo_perc_.wav");
		sinos.put("4", "media/piano_preparado/sino/sino_vertigem_.wav");
		sinos.put("5", "media/piano_preparado/sino/sinoressonancia_.wav");
		
		
		tremolos.put("1", "media/piano_preparado/glissando_ataque_raspado_.wav");
		tremolos.put("2", "media/piano_preparado/notagrave_ress_aguda_.wav");
		tremolos.put("3", "media/piano_preparado/tremolo_.wav");
		tremolos.put("4", "media/piano_preparado/tremolo_gliss_.wav");
		
		nylon.put("1", "media/piano_preparado/nylon/nylon.wav");
		nylon.put("2", "media/piano_preparado/nylon/nylongravelongo.wav");
		
		percussaoSino.put("1", "media/piano_preparado/percussao_sino/ataque_perc_.wav");
		percussaoSino.put("2", "media/piano_preparado/percussao_sino/bolinha_gliss_.wav");
		percussaoSino.put("3", "media/piano_preparado/percussao_sino/frase_agudopizz_.wav");
		percussaoSino.put("4", "media/piano_preparado/percussao_sino/perc_sino2_.wav");
		percussaoSino.put("5", "media/piano_preparado/percussao_sino/pizzicato_madeira@_.wav");
		
		pizzicatos.put("1", "media/piano_preparado/pizzicato/estalos_otimos_.wav");
		pizzicatos.put("2", "media/piano_preparado/pizzicato/pizzicato_.wav");
		pizzicatos.put("3", "media/piano_preparado/pizzicato/pizzicato_seco2_.wav");
		pizzicatos.put("4", "media/piano_preparado/pizzicato/pizzicato2_.wav");
		
		percussoes.put("1", "media/piano_preparado/percussoes/bass_curto_.wav");
		percussoes.put("2", "media/piano_preparado/percussoes/perc_sino_madeira_ress_.wav");
		percussoes.put("3", "media/piano_preparado/percussoes/perc_sino_madeira_ress2_.wav");
		percussoes.put("4", "media/piano_preparado/percussoes/perc_sino_ress_batimento_.wav");
		percussoes.put("5", "media/piano_preparado/percussoes/perc_tamborilando_.wav");
		percussoes.put("6", "media/piano_preparado/percussoes/percussao_surda_.wav");
		
		
		curto.put("1", "media/piano_preparado/curto/perc_sino_madeira_ress2_.wav");
		curto.put("2", "media/piano_preparado/curto/perc_sino_ress_batimento_.wav");
		curto.put("3", "media/piano_preparado/curto/pizzicato_madeira@_.wav");
		curto.put("4", "media/piano_preparado/curto/pizzicato_seco2_.wav");
		curto.put("5", "media/piano_preparado/curto/pizzicato2_.wav");
		curto.put("6", "media/piano_preparado/curto/sino_.wav");
		curto.put("1", "media/piano_preparado/curto/sino_agudo_perc_.wav");
		
		longo.put("1", "media/piano_preparado/longo/tremolo_.wav");
		longo.put("2", "media/piano_preparado/longo/notagrave_ress_aguda_.wav");
		longo.put("3", "media/piano_preparado/longo/perc_tamborilando_.wav");
		longo.put("4", "media/piano_preparado/longo/tremolo_gliss_.wav");
		
		
		
		return true;
	}

	/* (non-Javadoc)
	 * @see ensemble.Reasoning#eventHandlerRegistered(ensemble.EventHandler)
	 */
	@Override
	protected void eventHandlerRegistered(EventHandler evtHdl) {
		if (evtHdl instanceof Actuator && evtHdl.getEventType().equals(MovementConstants.EVT_TYPE_MOVEMENT)) {
			legs = (Actuator)evtHdl;
			legs.registerListener(this);
			legsMemory = getAgent().getKB().getMemory(legs.getComponentName());
		}
		else if (evtHdl instanceof Sensor && evtHdl.getEventType().equals(MovementConstants.EVT_TYPE_MOVEMENT)) {
			eyes = (Sensor)evtHdl;
			eyes.registerListener(this);
			eyesMemory = getAgent().getKB().getMemory(eyes.getComponentName());
		}else if (evtHdl instanceof Sensor && evtHdl.getEventType().equals(MessageConstants.EVT_TYPE_MESSAGE)) {
			antenna = (Sensor)evtHdl;
			antenna.registerListener(this);
			antennaMemory = getAgent().getKB().getMemory(antenna.getComponentName());
		}

	}

	/* (non-Javadoc)
	 * @see ensemble.Reasoning#newSense(ensemble.Sensor, double, double)
	 */
	@Override
	public void newSense(Sensor sourceSensor, double instant, double duration) {
		
		if (sourceSensor.getEventType().equals(
				MovementConstants.EVT_TYPE_MOVEMENT))
		{
		String str = (String)eyesMemory.readMemory(instant, TimeUnit.SECONDS);
		Command cmd = Command.parse(str);
		if (cmd != null) {
			actual_pos = Vector.parse(cmd.getParameter(MovementConstants.PARAM_POS));
			actual_vel = Vector.parse(cmd.getParameter(MovementConstants.PARAM_VEL));
			actual_ori = Vector.parse(cmd.getParameter(MovementConstants.PARAM_ORI));
		}
		}else if(sourceSensor.getEventType().equals(
				MessageConstants.EVT_TYPE_MESSAGE)){
			
			
			String str = (String) antennaMemory.readMemory(instant,
					TimeUnit.SECONDS);
			
		
			
			Command cmd = Command.parse(str);
			
			
			if (cmd != null && cmd.getCommand()!= MessageConstants.CMD_INFO) {
				
			
				if (cmd.getParameter(MessageConstants.PARAM_TYPE).equals(MessageConstants.ANDOSC_TYPE)) {
					
					
					if (cmd.getParameter(MessageConstants.PARAM_ACTION).equals(MessageConstants.ANDOSC_TOUCH_POS)) {
						
						/*System.out.println("Recebeu mensagem "
								+ cmd.getParameter(MessageConstants.PARAM_TYPE));
						*/	
						String[] val = cmd.getParameter(MessageConstants.PARAM_ARGS).split(" ");
						if(val.length ==2)
						{
						double valX = Double.parseDouble(val[0]);
						double valY = Double.parseDouble(val[1]);
						sendTransportCommand(getOscVector(100, valX,valY));
						}
					}else  if (cmd.getParameter(MessageConstants.PARAM_ACTION).equals(
							MessageConstants.ANDOSC_ACCELEROMETER)) {

						
						
						String[] val = cmd.getParameter(
								MessageConstants.PARAM_ARGS).split(" ");

					//SWITCH
						float orientacaoX = Float.parseFloat(val[0]);
						
						/*System.out.println("X: " + orientacaoX + "allowChange "
								+ allowChange);*/
						
						if (orientacaoX <= -7 && allowChange) {
							active = !active;
							allowChange = false;
							lastChange = new Date();
							 
							//System.out.println("CHUTE!!" + lastChange.toString());
						} else if (orientacaoX >= -0.5 && !allowChange && lastChange != null) {
							timeDiff = Math.abs((new Date()).getTime() - lastChange.getTime());
							if(timeDiff > 800){
								allowChange = true;
								//System.out.println("PODE chutar de novo!! " + timeDiff);	
							}
							
						}

					}
				}else if (cmd.getParameter(MessageConstants.PARAM_TYPE).equals(MessageConstants.DIRECTION_TYPE)){
					// Considers a change of direction
					String[] val = cmd
							.getParameter(MessageConstants.PARAM_ARGS).split(
									" ");
					if (val.length == 1) {
						System.out.println("Changed direction: " + val[0]);

						int valX = Integer.parseInt(val[0]);
						changeDirection(valX);
						changeSample(valX);

					}
				} else if (cmd.getParameter(MessageConstants.PARAM_TYPE)
						.equals(MessageConstants.ISO_TYPE)) {
						//TRATAMENTO DE ISO SWARM para posicao
					if (cmd.getParameter(MessageConstants.PARAM_ACTION).equals(
							MessageConstants.ISO_POSITION)) {

						String[] val = cmd.getParameter(
								MessageConstants.PARAM_ARGS).split(" ");

						/*
						 * System.out.println("Recebeu mensagem " +
						 * cmd.getParameter(MessageConstants.PARAM_TYPE));
						 */

						//System.out.println("Recebeu mensagem " + cmd.getParameter(MessageConstants.PARAM_ARGS));
						//System.out.println("val.length =" + val.length + val[4]);

						if (val.length == 5) {
							int agentNum = 1 + Integer.parseInt(val[4]);

							if (getAgent().getAgentName().trim()
									.indexOf("M" + agentNum) >= 0) {

								// System.out.println( getAgent().getAgentName()
								// + " M" + agentNum);

								double valX = Double.parseDouble(val[0]);
								double valY = Double.parseDouble(val[1]);
								
								//No caso de transportar diretamente
								sendTransportCommand(getIsoOscVector(100, valX,
										valY));
								//Vectorial
								//time_constrains.add(0.1);
								//waypoints.add(Vector.parseSingle(valX, valY, 0));
								//waypoints.add(getIsoOscVector(100, valX,
									//	valY));

							}
						} else if (val.length == 6 && Integer.parseInt(val[3]) == ISO_MVT_TYPE) {
							// Recebimento direto de ISO Position

							//&& Integer.parseInt(val[3]) == ISO_MVT_TYPE
							
							
							int agentNum = 1 + Integer.parseInt(val[4]);
							
							//System.out.println("AGENT_NUMBER: " + agentNum);
							
							if (getAgent().getAgentName().trim()
									.indexOf("M" + agentNum) >= 0 ) {
								double valX = -Double.parseDouble(val[0]);
								double valY = Double.parseDouble(val[1]);
								sendTransportCommand(getIsoOscVector(100, valX,
										valY));
							}

						}
					}
				}else if (cmd.getParameter(MessageConstants.PARAM_TYPE)
						.equals(MessageConstants.CONTROL_OSC_TYPE)) {
					
					if (cmd.getParameter(MessageConstants.PARAM_ACTION).equals(MessageConstants.CONTROL_OSC_POSITION)) {
						if (cmd.containsParameter(MessageConstants.PARAM_ARGS)) {

/*							System.out.println("[" + getAgent().getAgentName()
									+ "] movement type...");
*/
							String[] val = cmd.getParameter(
									MessageConstants.PARAM_ARGS).split(" ");
							
							// PARAMETROS X[0-7], Y[0-7], PRESSED(1)-OFF(0) 
							int novoX = Integer.parseInt(val[0]);
							int novoY = Integer.parseInt(val[1]);
							
							//0, 1, 2 Movement types
							if(novoX>=0 && novoX<=1){
								ISO_MVT_TYPE = 1;							
							}else if(novoX>=2 && novoX<=3){
								ISO_MVT_TYPE = 2;							
							}if(novoX>=4 && novoX<=5){
								ISO_MVT_TYPE = 3;							
							}if(novoX>=6 && novoX<=7){
								ISO_MVT_TYPE = -1;							
							}
														
							System.out.println("MOVEMENT:" + ISO_MVT_TYPE);
						}
					}
					
				} else if (cmd.getParameter(MessageConstants.PARAM_TYPE)
						.equals(MessageConstants.PP_OSC_TYPE)) {
					if (cmd.getParameter(MessageConstants.PARAM_ACTION).equals(
							MessageConstants.CONTROL_OSC_POSITION)) {
						String[] val = cmd.getParameter(
								MessageConstants.PARAM_ARGS).split(" ");

						int novoMvt = Integer.parseInt(val[0]) + 1;

						ISO_MVT_TYPE = novoMvt;
					}
					
					
				}
			}
		}
		// System.out.println(getAgent().getAgentName() + " - new position " + actual_pos + " velocity " + actual_vel);

	}
	
	/* (non-Javadoc)
	 * @see ensemble.MusicalAgentComponent#processCommand(ensemble.Command)
	 */
	@Override
	public void processCommand(Command cmd) {
		
		if (cmd.getCommand().equals(MovementConstants.CMD_WALK)) {
			if (cmd.containsParameter(MovementConstants.PARAM_POS) && cmd.containsParameter(MovementConstants.PARAM_TIME)) {
				sendStopCommand();
				waypoints.clear();
				time_constrains.clear();
				active_waypoint = 0;
				last_distance = Double.MAX_VALUE;
				loop = false;
				time_constrains.add(Double.valueOf(cmd.getParameter(MovementConstants.PARAM_TIME)));
				waypoints.add(Vector.parse(cmd.getParameter(MovementConstants.PARAM_POS)));
			}
		}
		else if (cmd.getCommand().equals("ADD_WAYPOINT")) {
			System.out.println("[" + getAgent().getAgentName() + "] Add waypoint...");
			time_constrains.add(Double.valueOf(cmd.getParameter("time")));
			waypoints.add(Vector.parse(cmd.getParameter("wp")));
		}
		else if (cmd.getCommand().equals(MovementConstants.CMD_STOP)) {
			System.out.println("[" + getAgent().getAgentName() + "] Stoping...");
			waypoints.clear();
			time_constrains.clear();
			active_waypoint = 0;
			sendStopCommand();
		}
		
	}
	
	/* (non-Javadoc)
	 * @see ensemble.Reasoning#process()
	 */
	@Override
	public void process() {

		if (actual_pos == null) {
			String str = (String)eyesMemory.readMemory(eyesMemory.getLastInstant(), TimeUnit.SECONDS);
			Command cmd = Command.parse(str);
			if (cmd != null) {
				actual_pos = Vector.parse(cmd.getParameter(MovementConstants.PARAM_POS));
				actual_vel = Vector.parse(cmd.getParameter(MovementConstants.PARAM_VEL));
				actual_ori = Vector.parse(cmd.getParameter(MovementConstants.PARAM_ORI));
			}
		}

		if (legsMemory != null && actual_pos != null && waypoints.size() != 0) {
			// Tenho destino?
			if (active_waypoint < waypoints.size()) {
				Vector dest_pos = waypoints.get(active_waypoint);
				double actual_distance = actual_pos.getDistance(dest_pos);
//				System.out.println("distance = " + actual_distance);
//				System.out.println("last_distance = " + last_distance);
				// Se passei da metade, desacelarar?
				if (!inverted && actual_distance < (total_distance/2)) {
					// Vou inverter a minha aceleração
//					System.out.println("METADE DO CAMINHO!!!");
					inverted = true;
					last_acc.inverse();
					sendAccCommand(last_acc, 0.0);
				}
				// Cheguei?
				if (actual_distance < precision || last_distance < actual_distance) {
					System.out.println("Cheguei no waypoint " + active_waypoint + " - " + waypoints.get(active_waypoint));
					// Parar o agente
					sendStopCommand();
					// Mudar o waypoint
					last_distance = Double.MAX_VALUE;
					active_waypoint++;
//					System.out.println("active wp = " + active_waypoint);
					if (active_waypoint == waypoints.size() && loop) {
						active_waypoint = 0;
					} else if (active_waypoint == waypoints.size() && !loop) {
						waypoints.clear();
						time_constrains.clear();
					}
				}
				else {
					// Estou parado ou passei
					if (actual_vel != null) {
						if (actual_vel.getMagnitude() == 0) {
							// TODO Mudar para o m�todo de Newton!!!
							// Calcular quanto e por quanto tempo devo acelerar
							double t = time_constrains.get(active_waypoint);
//							System.out.println("actual_pos = " + actual_pos + " - dest_pos = " + dest_pos + " - time_constraint = " + time_constrain);
							double acc_mag = MAX_ACELERATION;
							double t1 = 0.2; 
							boolean found = false;
							int iterations = 0;
							
							acc_mag = 1;
						    while (Math.abs((2*actual_distance-acc_mag*t*t) / (-t*t)) > EPSILON || iterations > 10) {
						        acc_mag = acc_mag - (2*actual_distance-acc_mag*t*t) / (-t*t);
//								System.out.println("["+iterations+"] acc_mag = " + acc_mag);
						        iterations++;
						    }
						    acc_mag = Math.min(acc_mag, MAX_ACELERATION);

							// Calcular a direção na qual deve andar
							total_distance = actual_pos.getDistance(dest_pos);
							Vector acc = new Vector((dest_pos.getValue(0)-actual_pos.getValue(0)), 
									(dest_pos.getValue(1)-actual_pos.getValue(1)), 
									(dest_pos.getValue(2)-actual_pos.getValue(2)));
							acc.normalizeVector();
							acc.product(acc_mag);
//							System.out.println("acc_vec = " + acc);
							// Enviar comando
							last_acc = acc;
							inverted = false;
							sendAccCommand(acc, 0.0);
//							sendAccCommand(acc, t1);
						} 
						else if (actual_vel.getMagnitude() > 0 && last_distance < actual_distance) {
							sendStopCommand();
						}
					}
					last_distance = actual_distance;
				}
			} 
			// Não tenho destino
			else {
				// Se estiver em movimento, parar
				if (actual_vel != null && actual_vel.getMagnitude() > 0) {
					sendStopCommand();
				}
			}
		
		}
		
	}
	
	/**
	 * Send stop command.
	 */
	private void sendStopCommand() {
		String cmd = MovementConstants.CMD_STOP;
//		System.out.println(cmd);
		try {
			legsMemory.writeMemory(cmd);
			legs.act();
		} catch (MemoryException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Send acc command.
	 *
	 * @param acc the acc
	 * @param dur the dur
	 */
	private void sendAccCommand(Vector acc, double dur) {
		String cmd = MovementConstants.CMD_WALK + 
			" :" + MovementConstants.PARAM_ACC + " " + acc.toString();
		if (dur > 0.0) {
			cmd += " :" + MovementConstants.PARAM_DUR + " " + Double.toString(dur);
		}
		//System.out.println("acc command: " + cmd);
		try {
			
			legsMemory.writeMemory(cmd);
			legs.act();
		} catch (MemoryException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Send transport command.
	 *
	 * @param pos the pos
	 */
	private void sendTransportCommand(Vector pos) {
		
		
		String cmd = MovementConstants.CMD_TRANSPORT+ 
			" :" + MovementConstants.PARAM_POS + " (" + pos.getValue(0)+ ";" + pos.getValue(1)+ ";" + pos.getValue(2)+ ")";
		
		//System.out.println("transport command: " + cmd);
		try {
			legsMemory.writeMemory(cmd);
			legs.act();
		} catch (MemoryException e) {
			e.printStackTrace();
		}
	}
	
/**
 * Gets the osc vector.
 *
 * @param size the size
 * @param oscX the osc x
 * @param oscY the osc y
 * @return the osc vector
 */
private Vector getOscVector(int size, double oscX, double oscY ){
		
			
		/*String str = (String)eyesMemory.readMemory(eyesMemory.getLastInstant(), TimeUnit.SECONDS);
		Command cmd = Command.parse(str);
		if (cmd != null) {
			actual_pos = Vector.parse(cmd.getParameter(MovementConstants.PARAM_POS));
		}
		
*/		
		int x = 0;
		double valX = 50 - (oscX/232)*100;
		double valY = 50 - (oscY/296)*100;
		double valZ = 0;
					
		Vector v = new Vector(valY,valX, valZ );
		//System.out.println("X: " + valX + " Y:" + valY + " Z:"+ valZ );
			return v; 
		
	}

/**
 * Gets the iso osc vector.
 *
 * @param size the size
 * @param oscX the osc x
 * @param oscY the osc y
 * @return the iso osc vector
 */
private Vector getIsoOscVector(int size, double oscX, double oscY ){
	
	
	int x = 0;
	double valX = -oscX;
	double valY = oscY;
	double valZ = 0;
				
	Vector v = new Vector(valY,valX, valZ );
	//System.out.println("X: " + valX + " Y:" + valY + " Z:"+ valZ );
		return v; 
	
}


/**
 * Change direction.
 *
 * @param direction the direction
 */
private void changeDirection(int direction)
 {
		
	System.out.println("Change direction " + direction);
		
		// Calcular a direção na qual deve andar
		Vector acc;
		switch (direction) {
		case 1:
			// Direita
			acc = new Vector(0, 1, 0);
			break;
		case 2:
			// Esquerda
			acc = new Vector(0, -1, 0);
			break;
		case 3:
			// Acima
			acc = new Vector(1, 0, 0);
			break;
		case 4:
			// Baixo
			acc = new Vector(-1, 0, 0);
			break;

		default:
			acc = null;
			break;

		}

		/*
		 * new Vector((dest_pos.getValue(0)-actual_pos.getValue(0)),
		 * (dest_pos.getValue(1)-actual_pos.getValue(1)),
		 * (0-actual_pos.getValue(2)));
		 */
		if (acc != null && (getAgent().getKB().getParameter("directionState")==null || getAgent().getKB().getParameter("directionState")!=""+direction)) {
			acc.normalizeVector();
			acc.product(DEFAULT_ACELERATION);

			// Enviar comando
			//System.out.println(acc.toString());
			waypoints.clear();
			
			//sendAccCommand(acc, 0);
			
			sendAccCommand(acc, 0.2);
			
			getParameters().put("directionState", ""+direction);

		}
	}



/**
 * Change sample.
 *
 * @param direction the direction
 */
private void changeSample(int direction)
{
	

	String audioFileName;
	
	System.out.println("Change sample" + direction);
	
	
	ArrayList<String> filePaths = new ArrayList<String>();			
		
		// Calcular a direção na qual deve andar
		Vector acc;
		switch (direction) {
		case 1:
			// Direita
			audioFileReference = longo;
			break;
		case 2:
			// Esquerda
			audioFileReference = curto;
			break;
		case 3:
			// Acima - Percussivo
			audioFileReference = percussoes;
			break;
		case 4:
			// Baixo
			audioFileReference = sinos;
			break;

		default:
			acc = null;
			break;

		}

		Iterator<String> itr = audioFileReference.values().iterator(); 
		String files = new String();
		while(itr.hasNext()) {
			String aux = itr.next();
			filePaths.add((String) aux);
			files = files + (String) aux + ";";
		} 		
		 Collections.shuffle(filePaths);
		 audioFileName = filePaths.get(0);
	     //System.out.println(files);
		 
		 
		 if(getAgent().getKB().getParameter("playState")!=null && getAgent().getKB().getParameter("playState")==AudioConstants.CMD_STOP){
				Command cmd = new Command(getAddress(), "/"+ Constants.FRAMEWORK_NAME + "/" + getAgent().getAgentName() + "/FileInputReasoning", AudioConstants.CMD_PLAY);
				
				cmd.addParameter("filename", audioFileName);
				cmd.addParameter("files", files);
				
				sendCommand(cmd);
									
				getParameters().put("playState", "PLAY");
				getParameters().put("FileIndex", String.valueOf(0));

				
				
				
			}
			

	}


}
