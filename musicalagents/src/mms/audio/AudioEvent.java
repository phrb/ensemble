package mms.audio;

import mms.Event;

public class AudioEvent extends Event {
	
	// Tamanho e Formato do Chunk
	public int		sampleRate;
	
	// 0 - none, 1 - ambisonics 1st order, 2 - ambisonics 2nd order
	public int 		codification;
	public int 		numChannels;

	// Dados do Chunk (audio, MIDI etc.)
	public double[][] chunk;
	
	public AudioEvent(int sampleRate, int chunkLenght, int numChannels) {
		this.sampleRate 	= sampleRate;
		this.chunk 			= new double[numChannels][chunkLenght];
	}
	
}
