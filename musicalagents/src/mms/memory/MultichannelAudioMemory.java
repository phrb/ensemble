package mms.memory;

import java.io.BufferedWriter;
import java.io.IOException;

import jade.util.Logger;

import mms.Constants;
import mms.MMSAgent;
import mms.Parameters;
import mms.audio.AudioConstants;
import mms.clock.TimeUnit;
import mms.clock.VirtualClockHelper;

// TODO Problemas de concorrências ao ler e ao escrever no HashMap
// TODO O que acontece se for tentar escrever próximo da mudança de ciclo?
// TODO O que fazer com os chunks zerados que recebemos? Talvez seja melhor nem enviar caso não exista 
/**
 * Implementação da memória para eventos de áudio.
 * A estrutura utilizada para armazenar as amostras de áudio é esparsa e baseada em uma lista ligada (ArrayList).
 */
public class MultichannelAudioMemory extends Memory {

//	public BufferedWriter os;

	private final double EPSILON = 1E-9; 
	private final String MEMORY_TYPE = AudioConstants.EVT_TYPE_AUDIO;
	
	private final String PARAM_OVERWRITE = "OVERWRITE";
	
	// TODO Musical Agent ou qualquer tipo de Agent?
	private double		step;
	private double		startTime;
	private double		period;
	private boolean 	overwrite;
	private int 		numChannels;
	
	// Control variables
	private int samples;
	private int halfSamples;
	private int ptrBegin;
	private long ptrAbsolutZero;
	private int ptrNow;
	private int ptrEnd;
	private double instantBegin;
	private double instantEnd;
	private double[][] buffer;
	
	@Override
	public boolean init() {
		
		// Obter os parâmetros necessários
		this.period = Long.valueOf(parameters.get(Constants.PARAM_PERIOD));
		this.startTime = Long.valueOf(parameters.get(Constants.PARAM_START_TIME));
		this.step = Double.valueOf(parameters.get(Constants.PARAM_STEP,"0.0000226757369615"));
		this.overwrite = Boolean.valueOf(parameters.get(PARAM_OVERWRITE, "FALSE"));
		this.numChannels = Integer.valueOf(parameters.get(Constants.PARAM_CHANNELS, "1"));

		// Cria a memória
		// TODO Pode ter OutOfMemory aqui!
		samples = (int)((past + future) / step);
		halfSamples = (int)(past / step);
		buffer = new double[numChannels][samples];
//		instantBegin = -past;
//		instantEnd = instantBegin + past + future;
		instantBegin = -halfSamples * step;
		instantEnd = halfSamples * step;
//		System.out.println("sample = " + samples + " - halfSamples = " + halfSamples + " - instantBegin = " + instantBegin+ " - instantEnd = " + instantEnd);
		ptrBegin = 0;
		ptrAbsolutZero = 0;
		ptrNow = halfSamples;
		ptrEnd = samples - 1;
		
		return true;
		
	}
	
	@Override
	public double getFirstInstant() {
		return instantBegin;
	}

	@Override
	public double getLastInstant() {
		return instantEnd;
	}

	private static double round(double number, int decimalPlaces) {
		double modifier = Math.pow(10.0, decimalPlaces);
		return Math.round(number * modifier)/modifier;
	}

	/**
	 * Retorna o instante atual, para ser utilizado na leitura/escrita relativa 
	 */
	// TODO Por enquanto retorna em segundos, mas deve seguir a prática do TimeUnit
	private Object getNow(TimeUnit unit) {
		
		Object ret = null;
		
		// No caso de um processamento periódico, inserir a partir do instante em que começa o próxima o frame
		double instant;
		if (period > 0) {
			long now = (long)clock.getCurrentTime(TimeUnit.MILLISECONDS);
			long nextFrame = (long)Math.floor((now - startTime) / period) + 1;
			instant = startTime + (nextFrame * period);
			// Transforma em segundos
			instant = instant / 1000;
		}
		// Caso contrário, inserir a partir do instante atual
		else {
			instant = clock.getCurrentTime(TimeUnit.SECONDS);
		}

		switch (unit) {
			case SECONDS:
			case MILLISECONDS:
			case MICROSECONDS:
			case NANOSECONDS:
				ret = instant;
				break;
			case SAMPLES:
				ret = (ptrBegin + ((int)Math.round((instant - instantBegin) / step))) % samples;
				break;
		}
		
		return ret;
		
	}

	private void updateMemory() {
		
//		System.out.println("updateMemory()");
		
//		long start = System.nanoTime();

		// Algumas definições:
		// - consideramos que now (faz mesmo? o que é now? e no caso de eventos periodicos) faz parte do passado, assim, 
		//   em um buffer de tamanho 10 (5 para o passado e 5 para o futuro), o índice 4 é o now
		
//		double now = (double)clock.getCurrentTime() / 1000;
		double now = (Double)getNow(TimeUnit.SECONDS);
		
		// NOVA IMPLEMENTAÇÃO!!!
		int displaced_samples = (int)Math.floor((now - instantBegin) / step) - halfSamples;
//		int diff_begin_now = ptrNow - ptrBegin;
		ptrAbsolutZero = ptrAbsolutZero + displaced_samples;
		ptrNow = (ptrNow + displaced_samples)%samples;
		
		// Determinar a distância, em samples, entre o ptrBegin e o ptrNow
		if (displaced_samples < 0) {
			System.err.println("ERRO!!! getNow() voltou no tempo!!!");
			return;
		}
		else if (displaced_samples == 0) {
			// não houve mudança no tempo, retornar sem alterar nada
//			System.out.println("Sem mudanças!");
			return;
		}
		else if (displaced_samples > 0 && displaced_samples < samples) {
			
			int ptrNewBegin = (ptrBegin + displaced_samples) % samples;
//			int ptrNewBegin = (ptrBegin + diffSamples - halfSamples - 1) % samples;
//			System.out.println("Zerei: " + ptrBegin + " - " + ptrNewBegin);
			// Preenche a memória com zeros onde for necessário
			for (int i = ptrBegin; i != ptrNewBegin; i = (i+1)%samples) {
				for (int n = 0; n < numChannels; n++) {
					buffer[n][i] = 0.0;
				}
			}
			ptrBegin = ptrNewBegin;
		} else {
//			System.out.println("diff_begin_now >= samples");
			ptrBegin = 0;
			// Preenche toda a memória com zeros onde for necessário
			for (int i = 0; i < samples; i++) {
				for (int n = 0; n < numChannels; n++) {
					buffer[n][i] = 0.0;
				}
			}
		}
		
//		instantBegin = round(now - past, 10);
//		instantEnd = round(now + future, 10);
		instantBegin = (ptrAbsolutZero - halfSamples) * step;
		instantEnd = (ptrAbsolutZero + halfSamples) * step;
//		if (name.equals("AUDIO")) {
//			System.out.println(name + " - " + now + " " + displaced_samples + " " + ptrBegin + " " + instantBegin);
//		}
//		System.out.println("ptrBegin = " + ptrBegin);
		
//		long duration = System.nanoTime() - start;
//		System.out.println("update() duration = " + duration);
		
	}
	
	@Override
	public Object readMemory(double instant, TimeUnit unit) {
		
		double[] ret = new double[numChannels];

		switch (unit) {

		case SECONDS:
		
			for (int n = 0; n < ret.length; n++) {
				// TODO Ainda existe um problema com os casos limites (começo e fim)
				if (instant >= instantBegin && instant < instantEnd) {
					double sample = ptrBegin + (instant - instantBegin) / step;
					int sample_low = (int)Math.floor(sample) % samples;
					int sample_high = (int)Math.ceil(sample) % samples;
					double fraction = (sample - sample_low) % samples;
					double value_low = buffer[n][sample_low];
					double value_high = buffer[n][sample_high];
					// Liner Interpolation
					ret[n] = value_low + (fraction * (value_high - value_low));
				}
			}
			
			break;
			
		case MILLISECONDS:
		case MICROSECONDS:
		case NANOSECONDS:
		case SAMPLES:
		case EVENTS:

			System.err.println("Not implemented yet...");
			break;

		}

//		System.out.println("[" + name + "] readMemory(" + instant + " - " + ret + ")");

		return ret;

	}

	// TODO Não devolver valores que já expiraram
	// TODO Interpolar valores caso o instante inicial não coincida com a amostra
	// TODO Considerar o caso de initialInstant < startTime
	@Override
	public Object readMemory(double instant, double duration, TimeUnit unit) {

//		System.out.println("[" + name + "] readMemory(" + instant + ", " + duration + ")");
		
		double[][] ret = null;

		switch (unit) {

		case SECONDS:
			
			int chunk_size = (int)Math.round(duration/step);		
			ret = new double[numChannels][chunk_size];
			
			int ptrBuffer, ptrChunk;
			if (instant >= instantBegin && instant < instantEnd) {
				ptrBuffer = (ptrBegin + (int)Math.round((instant - instantBegin) / step)) % samples;
				ptrChunk = 0;
			}
			else if (instant < instantBegin && instant + duration >= instantBegin) {
				ptrBuffer = ptrBegin;
				ptrChunk = (int)Math.round((instantBegin - instant) / step);
			}
			else {
				return ret;
			}
			
//			System.out.println("Entrou!!! - " + instant + " - " + duration + " - " + ptrBegin + " - " + ptrChunk);

			// Descobrir qual o delta entre o começo de um sample e o começo do intervalo pedido
//			double delta = round((instant - startTime) % step, 10);

			// Copia os dados do buffer
			do {
				for (int n = 0; n < numChannels; n++) {
					ret[n][ptrChunk] = buffer[n][ptrBuffer];
				}
				ptrChunk++;
				ptrBuffer = (ptrBuffer+1) % samples;
			} while (ptrChunk < chunk_size && ptrBuffer != ptrBegin);

			break;

		case MILLISECONDS:
		case MICROSECONDS:
		case NANOSECONDS:
			
			System.err.println("Not implemented yet.");
			break;

		case SAMPLES:

			System.err.println("Not available for SAMPLE! Use readMemoryRelative() instead.");
			break;
		
		case EVENTS:
			
			System.err.println("Not available for EVENTS! Use readMemoryRelative() instead.");
			break;
		}
		
		return ret;
		
	}

//	@Override
//	public Object readMemoryRelative(double offset, double duration, TimeUnit unit) {
//		double now = (Double)getNow(TimeUnit.SECONDS) + offset;
//		double[] value = (double[])readMemoryAbsolut(now, duration, unit);
//		return value;
//	}
//
//	@Override
//	public Object readMemoryRelative(double offset, TimeUnit unit) {
//		double now = (Double)getNow(TimeUnit.SECONDS) + offset;
//		double value = (Double)readMemoryAbsolut(now, unit);
//		return value;
//	}

	@Override
	public void resetMemory() {
		
		for (int i = 0; i < buffer.length; i++) {
			for (int n = 0; n < numChannels; n++) {
				buffer[n][i] = 0.0;
			}
		}
		
	}

	@Override
	// TODO Deixar ele sobrescrever a memória recebida? De qualquer maneira necessitamos de um método para que o sensor possa escrever
	// TODO Ainda não estou considerando a duração (duration)
	// TODO Se tiver muitos zeros, podemos desconsiderar o chunk para econimizar espaço
	public void writeMemory(Object object, double instant, double duration, TimeUnit unit) throws MemoryException {

//		System.out.println("[" + name + "] writeMemoryAbsolut(" + instant + ", " + duration + ")");

//		long start = System.nanoTime();
		int num_channels;
		int chunk_size;

		// Verifica qual foi o objeto passado
		double[][] chunk;
		if (object instanceof double[][]) {
			chunk = (double[][])object;
			num_channels = chunk.length;
			chunk_size = chunk[0].length;
		}
		else if (object instanceof double[]) {
			chunk = new double[1][];
			chunk[0] = (double[])object;
			num_channels = 1;
			chunk_size = chunk[0].length;
		}
		else {
			throw new MemoryException("Wrong object type");
		}

		// Atualiza a memória
		updateMemory();
		
		// Encontra o sample onde se iniciará a cópia do buffer na memória
		int ptrBuffer;
		int ptrChunk;
		if (instant >= instantBegin && instant < instantEnd) {
			ptrBuffer = (ptrBegin + (int)Math.round((instant - instantBegin) / step)) % samples;
			ptrChunk = 0;
		}
		else if (instant < instantBegin && instant + duration >= instantBegin) {
			ptrBuffer = ptrBegin;
			ptrChunk = (int)Math.round((instantBegin - instant) / step);
		}
		else {
			return;
		}
		int splDuration = (int)Math.round(duration / step);
//		System.out.println("ptrBuffer = " + ptrBuffer + " - ptrChunk = " + ptrChunk + " - splDuration = " + splDuration);
		
		// Copia
		int ptrBufferOld = ptrBuffer;
//		System.out.print("Escrevi (" + chunk.length + "): " + ptrBuffer);
		if (overwrite) {
			do {
				for (int n = 0; n < numChannels; n++) {
					buffer[n][ptrBuffer] = chunk[n][ptrChunk];
				}
				ptrChunk++;
				ptrBuffer = (ptrBuffer+1) % samples;
			} while (ptrChunk < chunk_size && ptrBuffer != ptrBegin);
 		} else {
 			do {
				for (int n = 0; n < numChannels; n++) {
					buffer[n][ptrBuffer] += chunk[n][ptrChunk];
				}
 				ptrChunk++;
 				ptrBuffer = (ptrBuffer+1) % samples;
 			} while (ptrChunk < chunk_size && ptrBuffer != ptrBegin);
 		}
			
//		System.out.println("readMemoryAbsolut() duration = " + (System.nanoTime() - start));

//		System.out.println("content = " + buffer[ptrBufferOld]);
//		System.out.println(" - " + ptrBegin + " - " + ptrChunk + " - " + ptrBuffer);
	}

	@Override
		public void writeMemory(Object object, double instant, TimeUnit unit) throws MemoryException {
			if (object instanceof double[][]) {
				double[][] chunk = (double[][])object;
				if (chunk.length > 0) {
					double duration = (double)chunk[0].length * step;
					writeMemory(object, instant, duration, TimeUnit.SECONDS);
				}
			}
			else if (object instanceof double[]) {
				double[] chunk = (double[])object;
				if (chunk.length > 0) {
					writeMemory(object, instant, step, TimeUnit.SECONDS);
				}
			}
		}
	
	@Override
	public void writeMemory(Object object) throws MemoryException {

		double instant = clock.getCurrentTime(TimeUnit.SECONDS);
		if (object instanceof double[][]) {
			double[][] chunk = (double[][])object;
			if (chunk.length > 0) {
				double duration = (double)chunk[0].length * step;
				writeMemory(object, instant, duration, TimeUnit.SECONDS);
			}
		}
		else if (object instanceof double[]) {
			double[] chunk = (double[])object;
			if (chunk.length > 0) {
				writeMemory(object, instant, step, TimeUnit.SECONDS);
			}
		}

	}
	
//	@Override
//	// TODO O que acontece se está próximo da mudança de frame?
//	// TODO Considerar o TimeUnit no caso do offset
//	public void writeMemoryRelative(Object object, double offset, double duration, TimeUnit unit) throws MemoryException {
//
//		double now = (Double)getNow(TimeUnit.SECONDS) + offset;
//		writeMemoryAbsolut(object, now, duration, unit);
//		
//	}
	
}
