/******************************************************************************

Copyright 2011 Leandro Ferrari Thomaz

This file is part of Ensemble.

Ensemble is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Ensemble is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Ensemble.  If not, see <http://www.gnu.org/licenses/>.

******************************************************************************/

package ensemble.tools;

import java.io.IOException;
import java.util.ArrayList;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

/* Unused Imports:
 * 
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
*/

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import ensemble.Constants;
import ensemble.EnvironmentAgent;
import ensemble.MusicalAgent;
import ensemble.Parameters;

/*
 * Importing Pure Data utilities:
 * TODO: Move Pd-related classes
 * to ensemble.tools or related.
 * 
 */
import ensemble.apps.pd_testing.Pd_Constants;
import ensemble.apps.pd_testing.Pd_Message;
import ensemble.apps.pd_testing.Pd_Receiver;
import org.puredata.core.PdBase;
import ensemble.apps.pd_testing.Pd_Agent_Class_Information;
import ensemble.apps.pd_testing.Pd_Agent_Instance;


// TODO: Auto-generated Javadoc
/*** Runs the system.
 * @author Leandro
 *
 */
// TODO Criação de um GUI para ver o estado de todos os agentes presentes no sistema e para a criação/destruição de agentes
public class Loader {

	// Configuration File's Constants
	/** The Constant CONF_GLOBAL_PARAMETERS. */
	private static final String CONF_GLOBAL_PARAMETERS = "GLOBAL_PARAMETERS";
	
	/** The Constant CONF_ENVIRONMENT_AGENT_CLASS. */
	private static final String CONF_ENVIRONMENT_AGENT_CLASS = "ENVIRONMENT_AGENT_CLASS";
	
	/** The Constant CONF_MUSICAL_AGENT_CLASS. */
	private static final String CONF_MUSICAL_AGENT_CLASS = "MUSICAL_AGENT_CLASS";
	
	/** The Constant CONF_MUSICAL_AGENT. */
	private static final String CONF_MUSICAL_AGENT = "MUSICAL_AGENT";
	
	/** The Constant CONF_EVENT_SERVER. */
	private static final String CONF_EVENT_SERVER = "EVENT_SERVER";
	
	/** The Constant CONF_COMPONENTS. */
	private static final String CONF_COMPONENTS= "COMPONENTS";
	
	/** The Constant CONF_COMP_REASONING. */
	private static final String CONF_COMP_REASONING = "REASONING";
	
	/** The Constant CONF_COMP_SENSOR. */
	private static final String CONF_COMP_SENSOR = "SENSOR";
	
	/** The Constant CONF_COMP_ACTUATOR. */
	private static final String CONF_COMP_ACTUATOR = "ACTUATOR";
	
	/** The Constant CONF_COMP_EVENT_TYPE. */
	private static final String CONF_COMP_EVENT_TYPE = "EVENT_TYPE";
	
	/** The Constant CONF_COMP. */
	private static final String CONF_COMP = "COMP";
	
	/** The Constant CONF_NAME. */
	private static final String CONF_NAME = "NAME";
	
	/** The Constant CONF_CLASS. */
	private static final String CONF_CLASS = "CLASS";
	
	/** The Constant CONF_COMM. */
	private static final String CONF_COMM = "COMM";
	
	/** The Constant CONF_PERIOD. */
	private static final String CONF_PERIOD = "PERIOD";
	
	/** The Constant CONF_ARG. */
	private static final String CONF_ARG = "ARG";
	
	/** The Constant CONF_ARG_COMP. */
	private static final String CONF_ARG_COMP = "ARG_COMP";
	
	/** The Constant CONF_VALUE. */
	private static final String CONF_VALUE = "VALUE";
	
	/** The Constant CONF_KB. */
	private static final String CONF_KB = "KB";
	
	/** The Constant CONF_FACT. */
	private static final String CONF_FACT = "FACT";
	
	/** The Constant CONF_PUBLIC. */
	private static final String CONF_PUBLIC = "PUBLIC";
	
	/** The Constant CONF_QUANTITY. */
	private static final String CONF_QUANTITY = "QUANTITY";
	
	private static final String CONF_WORLD = "WORLD";
	private static final String CONF_LAW = "LAW";
	
	private static final String pd_arg = "arg";
	private static final String pd_value = "value";
	private static final String pd_environment = "environment";
	private static final String pd_class = "class";
	private static final String pd_type = "type";
	private static final String pd_add_agent = "add_agent";
	private static final String pd_new_agent_type = "new_agent_type";
	private static final String pd_global = "global";
	private static final String pd_name = "name";
	private static final String pd_world = "world";
	private static final String pd_patch = "patch";


	
//	private Logger logger = Logger.getLogger("");

	// JADE Variables
	/** The jade_runtime. */
    private static Runtime jade_runtime = null;
	
	/** The jade_profile. */
	private static Profile jade_profile = null;
	
	/** The jade_container_controller. */
	private static ContainerController jade_container_controller = null;
	
	//--------------------------------------------------------------------------------
	// System initialization / termination
	//--------------------------------------------------------------------------------
	
	/**
	 * Start jade.
	 *
	 * @param elem_ensemble the elem_ensemble
	 * @param nogui the nogui
	 */
	private static void startJADE(Element elem_ensemble, boolean nogui) {

		// Cria o Container JADE
		jade_runtime = Runtime.instance();
		jade_profile = new ProfileImpl();
		jade_profile.setParameter(Profile.MAIN_HOST, "localhost");
		String services = "ensemble.clock.VirtualClockService;" +
							"ensemble.comm.direct.CommDirectService;";

		// Load Global Parameters	
		NodeList global_parameters_node_list = elem_ensemble.getElementsByTagName(CONF_GLOBAL_PARAMETERS);
		if (global_parameters_node_list.getLength() == 1) {
			Element elem_gp = (Element)global_parameters_node_list.item(0);
			jade_profile.setParameter(Constants.CLOCK_MODE, readAttribute(elem_gp, Constants.CLOCK_MODE, Constants.CLOCK_CPU));
			jade_profile.setParameter(Constants.PROCESS_MODE, readAttribute(elem_gp, Constants.PROCESS_MODE, Constants.MODE_REAL_TIME));
			jade_profile.setParameter(Constants.SCHEDULER_THREADS, readAttribute(elem_gp, Constants.SCHEDULER_THREADS, "5"));
		}
	
		jade_profile.setParameter(Profile.SERVICES, services);

		jade_container_controller = jade_runtime.createMainContainer(jade_profile);
		
		// Creates special agents (they live outside the Virtual Environment)
		AgentController jade_agent_controller;
		try {
			// Command Router
			jade_agent_controller = jade_container_controller.createNewAgent("Router", "ensemble.router.RouterAgent", null);
			jade_agent_controller.start();
			// Sniffer
			if (!nogui) {
				jade_agent_controller = jade_container_controller.createNewAgent("Sniffer", "ensemble.sniffer.Sniffer", null);
				jade_agent_controller.start();
			}
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}
	}
	private static void startJADE ( Pd_Receiver config_receiver, boolean nogui ) 
	{

		// Cria o Container JADE
		jade_runtime = Runtime.instance ( );
		jade_profile = new ProfileImpl ( );
		jade_profile.setParameter ( Profile.MAIN_HOST, "localhost" );
		String services = "ensemble.clock.VirtualClockService;" +
							"ensemble.comm.direct.CommDirectService;";
		jade_profile.setParameter ( Constants.CLOCK_MODE, Constants.CLOCK_CPU );
		jade_profile.setParameter ( Constants.PROCESS_MODE,Constants.MODE_REAL_TIME );
		jade_profile.setParameter ( Constants.SCHEDULER_THREADS, "5" );
		for ( Pd_Message message : config_receiver.get_messages ( ) )
		{
			if ( message.get_source ( ).equals ( pd_global ) )
			{
				if ( message.get_symbol ( ).equals ( Constants.CLOCK_MODE ) )
				{
					jade_profile.setParameter ( Constants.CLOCK_MODE, ( String ) message.get_arguments ( )[ 0 ] );
				}
				else if ( message.get_symbol ( ).equals ( Constants.PROCESS_MODE ) )
				{
					jade_profile.setParameter ( Constants.PROCESS_MODE, ( String ) message.get_arguments ( )[ 0 ] );
				}
				else if ( message.get_symbol ( ).equals ( Constants.SCHEDULER_THREADS ) )
				{
					jade_profile.setParameter ( Constants.SCHEDULER_THREADS, ( String ) message.get_arguments ( )[ 0 ] );
				}
			}
		}
		jade_profile.setParameter ( Profile.SERVICES, services );
		jade_container_controller = jade_runtime.createMainContainer ( jade_profile );

		// Creates special agents (they live outside the Virtual Environment)
		AgentController jade_agent_controller;
		try 
		{
			// Command Router
			jade_agent_controller = jade_container_controller.createNewAgent ( "Router", "ensemble.router.RouterAgent", null );
			jade_agent_controller.start ( );
			// Sniffer
			if  ( ! ( nogui ) ) 
			{
				jade_agent_controller = jade_container_controller.createNewAgent ("Sniffer", "ensemble.sniffer.Sniffer", null );
				jade_agent_controller.start ( );
			}
		}
		catch ( StaleProxyException e ) 
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Stop jade.
	 */
	private static void stopJADE ( )
	{	
		jade_runtime.shutDown();	
	}
	/**
	 * Read attribute.
	 *
	 * @param elem the elem
	 * @param attributeName the attribute name
	 * @param defaultValue the default value
	 * @return the string
	 */
	private static String readAttribute(Element elem, String attributeName, String defaultValue) {

		String attribute_value;
		
		String read_attribute = elem.getAttribute(attributeName);
		if (read_attribute != null && !read_attribute.equals("")) {
			attribute_value = read_attribute;
		} else {
//			System.out.println("\tParameter " + attributeName + " not found in configuration file...");
			attribute_value = defaultValue;
		}
		return attribute_value;
	}
	/**
	 * Read arguments.
	 *
	 * @param elem the elem
	 * @return the parameters
	 */
	private static Parameters readArguments(Element elem) {

		Parameters parameters = new Parameters();
		
		NodeList attributes_node_list = elem.getElementsByTagName(CONF_ARG);
		for (int j = 0; j < attributes_node_list.getLength(); j++) {
			Element element_argument = (Element)attributes_node_list.item(j);
			if (element_argument.getParentNode() == elem) {
				parameters.put( readAttribute(element_argument, CONF_NAME, null), readAttribute(element_argument, CONF_VALUE, null ));
			}
		}
		
		return parameters;
	
	}
	private static Parameters readArguments ( Pd_Message message, int offset ) 
	{
		Parameters parameters = new Parameters ( );	
		Object[ ] attributes = message.get_arguments ( );
		for ( int i = offset; i < attributes.length; i++ ) 
		{
			/* 
			 * Checking mal-formed arguments:
			 */
			if ( attributes[ i ].equals ( pd_arg ) && 
					attributes[ i + 2 ].equals ( pd_value ) )
			{
				if ( attributes[ i + 3 ] instanceof Float )
				{
					parameters.put ( ( String ) attributes[ i + 1 ], String.valueOf( ( Float ) attributes[ i + 3 ] ) );
				}
				else if ( attributes[ i + 3 ] instanceof String )
				{
					parameters.put ( ( String ) attributes[ i + 1 ], attributes[ i + 3 ] );
				}
				i += 3;
			}
			else if ( attributes[ i ].equals ( pd_patch ) )
			{
				parameters.put( ( String ) attributes[ i ], ( String ) attributes[ i + 1 ] );
				i += 1;
			}
			else
			{
				break;
			}
		}
		return parameters;
	}
	
	/**
	 * Read component arguments.
	 *
	 * @param elem the elem
	 * @param component the component
	 * @return the parameters
	 */
	private static Parameters readComponentArguments(Element elem, String component) {

		Parameters parameters = new Parameters();
		
		NodeList components_node_list = elem.getElementsByTagName(CONF_ARG_COMP);
		for (int j = 0; j < components_node_list.getLength(); j++) {
			Element element_argument = (Element)components_node_list.item(j);
			String new_component = readAttribute(element_argument, CONF_COMP, ""); 
			if (new_component.equals(component)) {
				parameters.put(readAttribute(element_argument, CONF_NAME, null), readAttribute(element_argument, CONF_VALUE, null));
			}
		}
		
		return parameters;
	
	}
	
	/**
	 * Read facts.
	 *
	 * @param elem the elem
	 * @return the parameters
	 */
	private static Parameters readFacts(Element elem) {

		Parameters parameters = new Parameters();
		
		NodeList fact_node_list = elem.getElementsByTagName(CONF_FACT);
		for (int j = 0; j < fact_node_list.getLength(); j++) {
			Element element_argument = (Element)fact_node_list.item(j);
			parameters.put(readAttribute(element_argument, CONF_NAME, null), readAttribute(element_argument, CONF_VALUE, null));
		}
		
		return parameters;
	
	}
	
	/**
	 * Load xml file.
	 *
	 * @param xmlFile the xml file
	 * @return the document
	 */
	private static Document loadXMLFile(String xmlFile) {
		
		Document xml_document = null;
		
		System.out.println("[Loader] Loading configuration file for Ensemble: " + xmlFile);
		
		try {
			DocumentBuilderFactory document_builder_factory = DocumentBuilderFactory.newInstance();
			document_builder_factory.setValidating(false);
			DocumentBuilder document_builder = document_builder_factory.newDocumentBuilder();
			xml_document = document_builder.parse(xmlFile);
		} catch (Exception e) {
			System.err.println("Error while loading XML file!");
			System.err.println(e.toString());
			System.exit(1);
		}
		
		return xml_document;
		
	}
	
	/**
	 * Load system.
	 *
	 * @param elem_ensemble the elem_ensemble
	 */
	private static void loadSystem(Element elem_ensemble) {
		
		NodeList loading_elements_node_list;

		// Load Environment Agent
		loading_elements_node_list = elem_ensemble.getElementsByTagName(CONF_ENVIRONMENT_AGENT_CLASS);
		if (loading_elements_node_list.getLength() == 1) {
			Element environment_agent_element = (Element)loading_elements_node_list.item(0);

			String environment_agent_name = Constants.ENVIRONMENT_AGENT;
			String environment_agent_class_name = readAttribute(environment_agent_element, CONF_CLASS, "ensemble.EnvironmentAgent");
			Parameters environment_agent_parameters = readArguments(environment_agent_element);
			
			try {
				// Criar nova instância do EA solicitado
				Class<?> environment_agent_class = Class.forName(environment_agent_class_name);
				EnvironmentAgent environment_agent = (EnvironmentAgent)environment_agent_class.newInstance();
				Object[] arguments;
				arguments = new Object[1];
				arguments[0] = environment_agent_parameters;
				environment_agent.setArguments(arguments);
				
				// Load World Parameters
				loading_elements_node_list = environment_agent_element.getElementsByTagName("WORLD");
				if (loading_elements_node_list.getLength() == 1) {
					Element elem_gp = (Element)loading_elements_node_list.item(0);
					String world_class_name = readAttribute(elem_gp, CONF_CLASS, null);
					if (world_class_name == null) {
						System.err.println("ERROR: World class not defined");
						world_class_name = "ensemble.world.World";
					}
					Parameters world_param = readArguments(elem_gp);
					environment_agent.addWorld(world_class_name, world_param);
					loading_elements_node_list = elem_gp.getElementsByTagName("LAW");
					if (loading_elements_node_list.getLength() > 0) {
						for (int i = 0; i < loading_elements_node_list.getLength(); i++) {
							Element elem_law = (Element)loading_elements_node_list.item(i);
							String law_class = readAttribute(elem_law, CONF_CLASS, null);
							Parameters law_param = readArguments(elem_law);
							environment_agent.getWorld().addLaw(law_class, law_param);
						}
					}
				}
								
				// Load Event Servers
				loading_elements_node_list = environment_agent_element.getElementsByTagName(CONF_EVENT_SERVER);
				for (int i = 0; i < loading_elements_node_list.getLength(); i++) {
					Element elem_es = (Element)loading_elements_node_list.item(i);
					String es_class = readAttribute(elem_es, CONF_CLASS, null);
					String es_comm_class = readAttribute(elem_es, CONF_COMM, "ensemble.comm.CommMessage");
					String es_period = readAttribute(elem_es, CONF_PERIOD, "");
					if (es_class == null) {
						System.err.println("ERROR: Event Server class not defined");
					} else {
						Parameters parameters = new Parameters();
						parameters.put(Constants.PARAM_COMM_CLASS, es_comm_class);
						parameters.put(Constants.PARAM_PERIOD, es_period);
						parameters.merge(readArguments(elem_es));
						environment_agent.addEventServer(es_class, parameters);
					}
				}

				// Inserir o Agente no Jade
				AgentController ac = jade_container_controller.acceptNewAgent(environment_agent_name, environment_agent);
				ac.start();

			} catch (ClassNotFoundException e) {
				System.err.println("FATAL ERROR: Class " + environment_agent_class_name + " not found");
				System.exit(-1);
			} catch (InstantiationException e) {
				System.err.println("FATAL ERROR: Not possible to create an instance of " + environment_agent_class_name);
				System.exit(-1);
			} catch (IllegalAccessException e) {
				System.err.println("FATAL ERROR: Not possible to create an instance of " + environment_agent_class_name);
				System.exit(-1);
			} catch (StaleProxyException e) {
				System.err.println("FATAL ERROR: Not possible to insert agent " + environment_agent_class_name + " in JADE");
				System.exit(-1);
			}

		}
		else {
			System.err.println("\tERROR: No Environment Agent defined...");
			stopJADE();
			System.exit(-1);
		}
		
		// Load Musical Agents
		loading_elements_node_list = elem_ensemble.getElementsByTagName(CONF_MUSICAL_AGENT);
		for (int i = 0; i < loading_elements_node_list.getLength(); i++) {

			Element elem_ma = (Element)loading_elements_node_list.item(i);
			String ma_class = readAttribute(elem_ma, CONF_CLASS, "ensemble.MusicalAgent");
			String ma_name_pre = readAttribute(elem_ma, CONF_NAME, ma_class);
			int ma_quantiy = Integer.valueOf(readAttribute(elem_ma, CONF_QUANTITY, "1"));
			
			for (int qty = 0; qty < ma_quantiy; qty++) {
				
				String ma_name;
				if (ma_quantiy > 1) {
					ma_name = ma_name_pre + "_" + qty;
				} else {
					ma_name = ma_name_pre;
				}
				
				// Reads Musical Agent instance arguments
				Parameters parameters = readArguments(elem_ma);
				
				// Le os facts a serem carregados na KB
				Parameters facts = readFacts(elem_ma);
	
				try {
					// Procurar a classe correspondente a esta instância
					boolean found_class = false;
					NodeList nl_ma_class = elem_ensemble.getElementsByTagName(CONF_MUSICAL_AGENT_CLASS);
					for (int j = 0; j < nl_ma_class.getLength(); j++) {
						Element elem_ma_class = (Element)nl_ma_class.item(j);
						String ma_class_name = readAttribute(elem_ma_class, CONF_NAME, null);
						if (ma_class_name.equals(ma_class)) {
							found_class = true;
							// Criar nova instância do MA solicitado
							String ma_class_class = readAttribute(elem_ma_class, CONF_CLASS, "ensemble.rt.MusicalAgent");
							Class<?> maClass = Class.forName(ma_class_class);
							MusicalAgent ma = (MusicalAgent)maClass.newInstance();
							//Parameters class_parameters = readArguments(elem_ma_class);
							parameters.merge(readArguments(elem_ma_class));
							// Coloca os argumentos (os da instância tem precedência sobre os da classe
							Object[] arguments;
							arguments = new Object[1];
							arguments[0] = parameters;
							ma.setArguments(arguments);
							
//							System.out.println("[Loader] Class " + ma_class_name);
							
							// Preenche a KB do agente
							NodeList nl_ma_class_kb = elem_ma_class.getElementsByTagName(CONF_KB);
							if (nl_ma_class_kb.getLength() == 1) {
								Element elem_ma_class_kb = (Element)nl_ma_class_kb.item(0);
								String kb_class = readAttribute(elem_ma_class_kb, CONF_CLASS, "ensemble.KnowledgeBase");
								Parameters args = readArguments(elem_ma_class_kb);
								ma.addKB(kb_class, args);
//								System.out.println("\tKNOWLEDGE_BASE" + "KnowledgeBase");
								NodeList nl_facts = elem_ma_class_kb.getElementsByTagName(CONF_FACT);
								for (int k = 0; k < nl_facts.getLength(); k++) {
									// Cria o fact na KB
									Element elem_fact = (Element)nl_facts.item(k);
									String fact_name = elem_fact.getAttribute(CONF_NAME);
									String fact_value = elem_fact.getAttribute(CONF_VALUE);
									Boolean fact_public = Boolean.valueOf(elem_fact.getAttribute(CONF_PUBLIC));
									ma.getKB().registerFact(fact_name, fact_value, fact_public);
									// Verifica se existe algum fact a ser sobrescrito para esta instância
									if (facts.containsKey(fact_name)) {
										ma.getKB().updateFact(fact_name, facts.get(fact_name));
									}
								}
							}
							
							// Inserir Componentes Musicais no MA
							NodeList nl_ma_class_comps = elem_ma_class.getElementsByTagName(CONF_COMPONENTS);
							if (nl_ma_class_comps.getLength() == 1) {
								Element elem_ma_class_comps = (Element)nl_ma_class_comps.item(0);
								
								// Inserir os Reasonings
								NodeList nl_reasonings = elem_ma_class_comps.getElementsByTagName(CONF_COMP_REASONING);
								for (int k = 0; k < nl_reasonings.getLength(); k++) {
									Element elem_reasoning = (Element)nl_reasonings.item(k);
									Parameters args = readArguments(elem_reasoning);
									String comp_name = readAttribute(elem_reasoning, CONF_NAME, null);
//									System.out.println("\tREASONING " + comp_name);
									Parameters args_comp = readComponentArguments(elem_ma, comp_name);
									args.merge(args_comp);
									String comp_class = readAttribute(elem_reasoning, CONF_CLASS, null);
									args.put(Constants.PARAM_REASONING_MODE, readAttribute(elem_reasoning, Constants.PARAM_REASONING_MODE, "REACTIVE"));
									if (args.get(Constants.PARAM_REASONING_MODE).equals("PERIODIC")) {
										args.put(Constants.PARAM_PERIOD, readAttribute(elem_reasoning, Constants.PARAM_PERIOD, "100"));
									}
									ma.addComponent(comp_name, comp_class, args);
								} 
								
								// Inserir os Sensors
								NodeList nl_sensors = elem_ma_class_comps.getElementsByTagName(CONF_COMP_SENSOR);
								for (int k = 0; k < nl_sensors.getLength(); k++) {
									Element elem_sensor = (Element)nl_sensors.item(k);
									Parameters args = readArguments(elem_sensor);
									String comp_name = readAttribute(elem_sensor, CONF_NAME, "Sensor");
//									System.out.println("\tSENSOR " + comp_name);
									Parameters args_comp = readComponentArguments(elem_ma, comp_name);
									args.merge(args_comp);
									String comp_class = readAttribute(elem_sensor, CONF_CLASS, "ensemble.Sensor");
									args.put(Constants.PARAM_EVT_TYPE, readAttribute(elem_sensor, CONF_COMP_EVENT_TYPE, "DUMMY"));
									args.put(Constants.PARAM_COMM_CLASS, readAttribute(elem_sensor, CONF_COMM, "ensemble.comm.CommMessage"));
									args.put(Constants.PARAM_MEMORY_CLASS, readAttribute(elem_sensor, Constants.PARAM_MEMORY_CLASS, null));
									args.put(Constants.PARAM_MEMORY_FUTURE, readAttribute(elem_sensor, Constants.PARAM_MEMORY_FUTURE, "1"));
									args.put(Constants.PARAM_MEMORY_PAST, readAttribute(elem_sensor, Constants.PARAM_MEMORY_PAST, "1"));
									args.put(Constants.PARAM_REL_POS, readAttribute(elem_sensor, Constants.PARAM_POSITION, "(0;0;0)"));
									ma.addComponent(comp_name, comp_class, args);
								}
	
								// Inserir os Actuators
								NodeList nl_actuators = elem_ma_class_comps.getElementsByTagName(CONF_COMP_ACTUATOR);
								for (int k = 0; k < nl_actuators.getLength(); k++) {
									Element elem_actuator = (Element)nl_actuators.item(k);
									Parameters args = readArguments(elem_actuator);
									String comp_name = readAttribute(elem_actuator, CONF_NAME, "Actuator");
//									System.out.println("\tACTUATOR " + comp_name);
									Parameters args_comp = readComponentArguments(elem_ma, comp_name);
									args.merge(args_comp);
									String comp_class = readAttribute(elem_actuator, CONF_CLASS, "ensemble.Actuator");
									args.put(Constants.PARAM_EVT_TYPE, readAttribute(elem_actuator, CONF_COMP_EVENT_TYPE, "DUMMY"));
									args.put(Constants.PARAM_COMM_CLASS, readAttribute(elem_actuator, CONF_COMM, "ensemble.comm.CommMessage"));
									args.put(Constants.PARAM_MEMORY_CLASS, readAttribute(elem_actuator, Constants.PARAM_MEMORY_CLASS, null));
									args.put(Constants.PARAM_MEMORY_FUTURE, readAttribute(elem_actuator, Constants.PARAM_MEMORY_FUTURE, "1"));
									args.put(Constants.PARAM_MEMORY_PAST, readAttribute(elem_actuator, Constants.PARAM_MEMORY_PAST, "1"));
									args.put(Constants.PARAM_REL_POS, readAttribute(elem_actuator, Constants.PARAM_POSITION, "(0;0;0)"));
									ma.addComponent(comp_name, comp_class, args);
								}
								
							}
	
							// Inserir o Agente no Jade
							AgentController ac = jade_container_controller.acceptNewAgent(ma_name, ma);
							ac.start();
							
							break;
						}
					}

					if (!found_class) {
						System.out.println("[Loader] ERROR: there is no " + ma_class + " class defined");
					}
					
				} catch (ClassNotFoundException e) {
					System.err.println("[Loader] FATAL ERROR: Class " + ma_class + " not found");
					System.exit(-1);
				} catch (InstantiationException e) {
					System.err.println("[Loader] FATAL ERROR: Not possible to create an instance of " + ma_class);
					System.exit(-1);
				} catch (IllegalAccessException e) {
					System.err.println("[Loader] FATAL ERROR: Not possible to create an instance of " + ma_class);
					System.exit(-1);
				} catch (StaleProxyException e) {
					System.err.println("[Loader] FATAL ERROR: Not possible to insert agent " + ma_class + " in JADE");
					System.exit(-1);
				}
				
			}
			
		}
		
	}
	private static void loadSystem ( Pd_Receiver config_receiver ) 
	{
		int argument_position = 0;
		
		ArrayList< Pd_Agent_Class_Information > agent_classes = new ArrayList< Pd_Agent_Class_Information >( );
		
		String environment_agent_name = Constants.ENVIRONMENT_AGENT;
		String environment_agent_class_name = "ensemble.EnvironmentAgent";
		String world_class_name = "ensemble.world.World";
		String law_class_name = null;
		Parameters law_parameters = null;

		Parameters environment_agent_parameters = null;
		Parameters world_param = null;
		Parameters event_server_parameters = null;
		boolean has_environment = false;
		
		for ( Pd_Message message : config_receiver.get_messages ( ) )
		{
			/* 
			 * Loading Environment Agent:
			 */
			if ( message.get_source ( ).equals ( pd_environment ) )
			{
				/*
				 * Checking for the class, and parameters:
				 */
				has_environment = true;
				if ( message.get_symbol ( ).equals ( pd_class ) )
				{
					environment_agent_class_name = ( String ) message.get_arguments ( )[ 0 ];
					if ( ! ( environment_agent_class_name.equals( "ensemble.EnvironmentAgent" ) ) )
					{
						argument_position += 1;
						environment_agent_parameters = readArguments ( message, argument_position );
						argument_position = 0;
					}
					else
					{
						System.err.println ( "ERROR: SHOULD DEFINE ENVIRONMENT CLASS!" );
						System.exit ( -1 );
					}
				}
				/*
				 * Checking for a world class and world parameters:
				 */
				else if ( message.get_symbol ( ).equals ( pd_world ) )
				{
					world_class_name = ( String ) message.get_arguments ( )[ 0 ];
					if ( ! ( world_class_name.equals( "ensemble.world.World" ) ) )
					{
						argument_position += 1;
						world_param = readArguments ( message, argument_position );
						argument_position = 0;
					}				
					else
					{
						System.err.println ( "ERROR: SHOULD DEFINE WORLD!" );
						System.exit ( -1 );
					}
				}
				/*
				 * Checking for a law and its parameters:
				 */
				else if ( message.get_symbol ( ).equals ( CONF_LAW ) )
				{
					law_class_name = ( String ) message.get_arguments ( )[ 0 ];
					if ( law_class_name != null )
					{
						argument_position += 1;
						law_parameters = readArguments ( message, argument_position );
						argument_position = 0;
					}				
					else
					{
						System.err.println ( "ERROR: SHOULD DEFINE LAW!" );
						System.exit ( -1 );
					}
				}
				/* 
				 * Wrong message type:
				 */
				else
				{
					System.err.println ( "ERROR: MALFORMED ENVIRONMENT!" );
					System.exit ( -1 );
				}
			}
			else if ( message.get_source ( ).equals ( CONF_EVENT_SERVER ) )
			{
				/*
				 * TODO: Handle Event Server Configuration.
				 */
			}
			/*
			 * Instance of Musical Agent:
			 */
			else if ( message.get_source ( ).equals ( pd_add_agent ) )
			{			
				String new_class_name = null;
				Parameters new_class_parameters = null;
				String new_name = null;
				
				if ( message.get_symbol ( ).equals ( pd_name ) )
				{
					new_name = ( String ) message.get_arguments ( )[ 0 ];
					argument_position += 1;
					
					new_class_name = Pd_Constants.PD_AGENT_CLASS;
					new_class_parameters = readArguments ( message, argument_position );
					argument_position = 0;
					agent_classes.add ( new Pd_Agent_Class_Information ( new_name, new_class_name, new_class_parameters ) );		
				}
				else
				{
					System.err.println ( "ADDING_AGENT: SHOULD DEFINE MUSICAL_AGENT CLASS!" );
					System.exit ( -1 );
				}
			}
		}
		try 
		{
			Class< ? > environment_agent_class = Class.forName ( environment_agent_class_name );
			EnvironmentAgent environment_agent = ( EnvironmentAgent ) environment_agent_class.newInstance ( );
			environment_agent_parameters.put( "PD_INIT", "TRUE" );
			Object[ ] arguments;
			arguments = new Object[ 1 ];
			arguments[ 0 ] = environment_agent_parameters;
			environment_agent.setArguments ( arguments );

			environment_agent.addWorld ( world_class_name, world_param );
			//environment_agent.getWorld ( ).addLaw ( law_class_name, law_parameters );
			/*
			 * TODO: Initiate Event Servers.
			 */
			/*
			 * Inserting environment into jade:
			 */
			AgentController agent_controller = jade_container_controller.acceptNewAgent ( environment_agent_name, environment_agent );
			agent_controller.start ( );
			/*
			 * Instantiate Agents:
			 */
			for ( Pd_Agent_Class_Information agent_class : agent_classes )
			{
				Class<?> new_agent_class = Class.forName ( agent_class.get_class_name ( ) );
				MusicalAgent musical_agent = ( MusicalAgent ) new_agent_class.newInstance ( );
				Object[ ] instance_arguments;
				instance_arguments = new Object[ 1 ];
				instance_arguments[ 0 ] = agent_class.get_parameters ( );
				musical_agent.setArguments ( instance_arguments );
				jade_container_controller.acceptNewAgent ( agent_class.get_name ( ), musical_agent ).start ( );
			}
		} 
		catch ( ClassNotFoundException e ) 
		{
			System.err.println ( "FATAL ERROR: Class " + environment_agent_class_name + " not found" );
			System.exit ( -1 );
		} 
		catch ( InstantiationException e ) 
		{
			System.err.println ( "FATAL ERROR: Not possible to create an instance of " + environment_agent_class_name );
			System.exit ( -1 );
		} 
		catch ( IllegalAccessException e ) 
		{
			System.err.println ( "FATAL ERROR: Not possible to create an instance of " + environment_agent_class_name );
			System.exit ( -1 );
		} 
		catch ( StaleProxyException e ) 
		{
			System.err.println ( "FATAL ERROR: Not possible to insert agent " + environment_agent_class_name + " in JADE" );
			System.exit ( -1 );
		}
		if ( ! ( has_environment ) )
		{
			System.err.println("\tERROR: No Environment Agent defined!");
			stopJADE();
			System.exit(-1);
		}		
	}
	/**
	 * Gracefully exit the Ensemble and Jade system.
	 */
	private void terminate ( ) {
		stopJADE();
		System.out.println("[Loader] Exiting Ensemble...");
		System.exit(0);
	}
	
	//--------------------------------------------------------------------------------
	// Main method
	//--------------------------------------------------------------------------------

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {

		// check the program arguments

        // TODO Add new argument check, so the user can tell 
        // Loader.java to load from Pure Data patches that will 
        // be run from within C code (libpd), which will be
        // loaded into Ensemble through JNI.
		boolean nogui = false;
		String xml_filename = null;
		String pd_patch_path = null;
		int patch = -1;
		Pd_Receiver config_receiver = null;
		for ( int i = 0; i < args.length; i++ ) 
		{
			if ( args[ i ].equals ( "-f" ) ) 
			{
				try 
				{
					xml_filename = args[ i + 1 ];
				} 
				catch ( Exception e ) 
				{
					System.out.println ( "ERROR: no xml file was specified" );
					System.exit ( -1 );
				}
				i++;
			}
			else if ( args[ i ].equals ( "-p" ) || args[ i ].equals( "--patch" ) )
			{
				pd_patch_path = args[ i + 1 ];
				/*
				 * Pd Setup
				 */
				config_receiver = Pd_Receiver.get_instance ( );
				/* 
				 * Registering config symbols to pd receiver:
				 */
				config_receiver.register_default_symbol ( pd_environment );
				config_receiver.register_default_symbol ( pd_new_agent_type );
				config_receiver.register_default_symbol ( pd_add_agent );
				config_receiver.register_default_symbol ( pd_global );
				/*
				 * Subscribing to control symbols:
				 */
				for ( String symbol : config_receiver.get_default_symbols ( ) )
				{
					PdBase.subscribe ( symbol );
				}
				PdBase.setReceiver ( config_receiver );
				try 
				{
					patch = PdBase.openPatch ( pd_patch_path );
					PdBase.pollPdMessageQueue ( );
				}
				catch ( IOException e ) 
				{
					e.printStackTrace ( );
					System.exit ( -1 );
				}
				i++;
			}
			else if ( args[ i ].equals ( "-nogui" ) ) 
			{
				nogui = true; 
			}
		}
        // TODO Another implementation of the functions provided by the methods
        //  Loader.startJADE and Loader.loadSystem must be able to extract the
        //  configuration parameters from Pure Data patches, and define patches
        //  as components of Agents, e.g. Reasonings.
		if ( xml_filename != null ) 
		{
			System.out.println("------------ Loading Ensemble ------------");
			Element elem_ensemble = Loader.loadXMLFile(xml_filename).getDocumentElement();
			Loader.startJADE(elem_ensemble, nogui);
			Loader.loadSystem(elem_ensemble);
		}
		else if ( pd_patch_path != null )
		{
			/*
			 * Process configuration Messages:
			 */
			Loader.startJADE ( config_receiver, nogui );
			Loader.loadSystem ( config_receiver );
			/*
			 * Close the patch & Pd, so we can restart Pd later:
			 */
			PdBase.closePatch ( patch );
		}
		else 
		{
			System.out.println("Loader usage: java Loader [-f <ensemble.properties> | [ [ -p | --patch ] <patch.pd> ] ] [ -nogui ]");
			System.exit(-1);
		}
		
		// TODO Colocar alguma condição para que o usuário possa encerrar a execução do sistema
//		while (true) {}

	}
	
}

