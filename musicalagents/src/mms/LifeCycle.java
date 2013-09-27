package mms;

public interface LifeCycle {

	/**
	 * User-implemented method that configures the component, setting up arguments and essential properties
	 */
	public boolean configure();
	
	/**
	 * System-implemented initialization method
	 * @return 
	 */
	public boolean start();
	
	/**
	 * User-implemented initialization method, called by start()
	 */
	public boolean init();
	
//	public void process();
	
	/**
	 * User-implement method called when a parameter has been updated
	 */
	public boolean parameterUpdate(String name, String newValue);
	
	/**
	 * User-implemented finalization method, called by end()
	 * @return 
	 */
	public boolean finit();
	
	/**
	 * System-implemented finalization method
	 * @return 
	 */
	public boolean stop();
	
	public void setParameters(Parameters parameters);
	
	public Parameters getParameters();
	
}
