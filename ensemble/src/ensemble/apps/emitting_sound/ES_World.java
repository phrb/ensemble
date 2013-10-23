/**
 * @author Pedro Bruel
 */

package ensemble.apps.emitting_sound;

import ensemble.world.World;

/*
 * A simple extension of Ensemble default World class.
 */

public class ES_World extends World 
{
	@Override
	public boolean init ( ) 
	{
		System.err.println ( "World Says: Initialized." );
		return true;
	}
}
