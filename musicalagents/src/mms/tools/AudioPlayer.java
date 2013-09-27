package mms.tools;

import java.util.LinkedList;
import java.util.Queue;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import mms.Constants;
import mms.EventHandler;
import mms.Sensor;
import mms.audio.AudioEvent;

public class AudioPlayer {
	
	SourceDataLine	line 			= null;
	long 			currentChunk	= 1;
	long 			playedChunk		= 1;
	
	// TODO Utilizar uma estrutura de armazenar chunks de forma mais eficiente
	Queue<byte[]> queue = new LinkedList<byte[]>();

	public AudioPlayer() {
	}
	
	public void initAudioDevice() {
		AudioFormat format = new AudioFormat(44100f, 16, 1, true, false);
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
		if (!AudioSystem.isLineSupported(info)) {
		    // Handle the error.
		    }
		    // Obtain and open the line.
		try {
		    line = (SourceDataLine) AudioSystem.getLine(info);
		    line.open(format);
		    line.start();
		} catch (LineUnavailableException ex) {
		   	// Handle the error.
		}
	}
	
	public void stopAudioDevice() {
		line.stop();
	}
	
	public void playChunk(double[] chunk) {
		byte[] buffer = AudioTools.convertDoubleByte(chunk, 0, chunk.length);
		line.write(buffer, 0, buffer.length);
		currentChunk++;
	}

}
