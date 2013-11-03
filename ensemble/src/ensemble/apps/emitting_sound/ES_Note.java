package ensemble.apps.emitting_sound;

public class ES_Note 
{
	private int note;
	private int velocity;
	
	public ES_Note ( int new_note, int new_velocity )
	{
		note = new_note;
		velocity = new_velocity;		
	}
	public int get_note ( )
	{
		return note;
	}
	public int get_velocity ( )
	{
		return velocity;
	}
}
