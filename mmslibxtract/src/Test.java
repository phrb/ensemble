import xtract.core.SWIGTYPE_p_float;
import xtract.core.SWIGTYPE_p_void;
import xtract.core.floatArray;
import xtract.core.xtract;
import xtract.core.xtract_features_;
import xtract.core.xtract_spectrum_;

public class Test {

	public static void main(String[] args) {

		System.loadLibrary("jxtract");
		
		int fft_size = 2048;
		double fs = 44100;
		double step = 1/fs;
		
		// Generates a sine wave
		float[] buffer = new float[fft_size];
		double t = 0;
		for (int i = 0; i < fft_size; i++) {
			buffer[i] = (float)(2+(0.8 * Math.sin(2 * Math.PI * 440 * t)));
			t = t + step;
		}
		
		// Finds the spectrum
		floatArray in = new floatArray(fft_size);
		for (int i = 0; i < fft_size; i++) {
			in.setitem(i, buffer[i]);
		}
		floatArray out = new floatArray(fft_size+2);
		
		xtract.xtract_init_fft(fft_size, xtract_features_.XTRACT_SPECTRUM.swigValue());
		// Spectrum
		floatArray argf = new floatArray(3);
		argf.setitem(0, (float)(fs/fft_size));
		argf.setitem(1, xtract_spectrum_.XTRACT_MAGNITUDE_SPECTRUM.swigValue());
		argf.setitem(2, 1);
		xtract.xtract_spectrum(in.cast(), fft_size, argf.cast(), out.cast());
		
		for (int i = 0; i < fft_size/2+1; i++) {
			System.out.println(out.getitem((fft_size/2)+i) + " Hz = " + out.getitem(i));
		}		
	}
	
}
