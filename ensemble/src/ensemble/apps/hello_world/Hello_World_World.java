package ensemble.apps.hello_world;

import ensemble.world.World;

/*
 * A simple extension of Ensemble default World class.
 */

public class Hello_World_World extends World 
{
	@Override
	public boolean init ( ) 
	{
		System.err.println ( "World Says: Initialized." );
		return true;
	}
}
