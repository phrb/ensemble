package mms.processing;

import mms.LifeCycle;
import mms.Parameters;

public abstract class Processor implements LifeCycle {

	protected Parameters arguments;
	
	@Override
	public void setParameters(Parameters parameters) {
		this.arguments = parameters;
	}
	
	@Override
	public Parameters getParameters() {
		return arguments;
	}
	
	@Override
	public boolean configure() {
		return true;
	}
	
	@Override
	public boolean start() {
		
		// Check for arguments
		// TODO Deveriamos ter uma lista dos argumentos necessário (e opcionais) para cada process, e checar a existência deles aqui!
		if (arguments == null) {
			System.err.println("ERROR: No arguments was passed to the Process class!");
			return false;
		}
		
		// Call the user initialization method
		init();
		
		// Everything is ok!
		return true;
		
	}

	@Override
	public boolean stop() {
		
		finit();
		
		return true;
	}
	
	@Override
	public boolean parameterUpdate(String name, String newValue) {
		return true;
	}
	
	/**
	 * Process method
	 * @param arguments
	 * @param in
	 * @return
	 */
	public abstract Object process(Parameters arguments, Object in);


}
