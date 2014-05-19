package ensemble.apps.pd_testing;

public class PdAudioBlockStream 
{
	private boolean full;
	private int write_pointer = 0;
	private int read_pointer = 0;
	private int buffer_size;
	private PdAudioBlock[ ] block_stream;
	
	public PdAudioBlockStream ( int new_buffer_size )
	{
		buffer_size = new_buffer_size;
		block_stream = new PdAudioBlock[ buffer_size ];
		full = false;
	}
	public void write_block ( PdAudioBlock audio_block )
	{
		if ( write_pointer >= buffer_size )
		{
			write_pointer = 0;
		}
		else if ( !full && write_pointer == ( buffer_size - 1 ) )
		{
			full = true;
		}
		block_stream[ write_pointer ] = audio_block;
		write_pointer += 1;
	}
	public float[ ] read_block ( )
	{
		if ( full )
		{
			PdAudioBlock audio_block;
			if ( read_pointer >= buffer_size )
			{
				read_pointer = 0;
			}
			audio_block = block_stream[ read_pointer ];
			read_pointer += 1;
			return audio_block.get_samples ( );
		}
		else
		{
			return null;
		}
	}
	public int get_buffer_size ( )
	{
		return buffer_size;
	}
	public int get_read_pointer ( )
	{
		return read_pointer;
	}
}
