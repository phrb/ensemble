/**
 * @author Pedro Bruel
 */

package ensemble.apps.emitting_sound;

import ensemble.*;

/*
 * A simple extension of Ensemble default MusicalAgent class.
 */

public class ES_Agent extends MusicalAgent
{
	private static final long serialVersionUID = 1L;
	/*
	 * init and configure are called once every time an instance of
	 * this Agent is inserted into the virtual environment.
	 * 
	 * (non-Javadoc)
	 * @see ensemble.EnsembleAgent#configure()
	 */
	@Override
	public boolean configure ( )
	{
		/*
		 * Desired Components are added here.
		 */
		this.addComponent ( "Reasoning", "ensemble.apps.emitting_sound.ES_Reasoning", null );
		this.addComponent ( "Speaker", "ensemble.apps.emitting_sound.ES_Speaker", new Parameters ( ) );
		System.err.println ( "Agent Says: Reasoning Added." );
		return true;	
	}
	@Override
	public boolean init ( )
	{
		configure ( );
		System.err.println ( "Agent Says: Initialized." );
		return true;
	}
}