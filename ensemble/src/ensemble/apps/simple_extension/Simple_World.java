/**
 * @author Pedro Bruel
 */

package ensemble.apps.simple_extension;

import ensemble.world.World;

/*
 * A simple extension of Ensemble default World class.
 */

public class Simple_World extends World 
{
	@Override
	public boolean init ( ) 
	{
		System.err.println ( "World Says: Initialized." );
		return true;
	}
}
