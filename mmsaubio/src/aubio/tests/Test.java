package aubio.tests;

import aubio.SWIGTYPE_p_aubio_biquad_t;
import aubio.SWIGTYPE_p_aubio_mfft_t;
import aubio.SWIGTYPE_p_cvec_t;
import aubio.SWIGTYPE_p_fvec_t;
import aubio.aubiowrapper;

public class Test {
	
    public static void main(String[] args) {
  
    	int N = 2048;
    	double fs = 44100;
		double step = 1/fs;
		double duration = 0.1;  

    	try {
    		System.loadLibrary("mmsaubio");
    	} catch (UnsatisfiedLinkError e) {
    		System.out.println("Failed to load the library \"mmsaubio\"");
    		System.out.println(e.toString());
    	} 

    	SWIGTYPE_p_aubio_mfft_t mfft_t = aubiowrapper.new_aubio_mfft(N, 1);
    
		// Generates a sine wave
		double[] buffer = new double[(int)(fs * duration)];
		double t = 0;
		for (int i = 0; i < buffer.length; i++) {
			buffer[i] = 0.8 * Math.sin(2 * Math.PI * 440 * t);
			t = t + step;
		}

		// Creates the input vector
		System.out.println("Input vector");
		SWIGTYPE_p_fvec_t in = aubiowrapper.new_fvec((int)(fs * duration), 1);
		for (int i = 0; i < (int)(fs * duration); i++) {
			aubiowrapper.fvec_write_sample(in, (float)buffer[i], 0, i);
//			System.out.printf("%.2f\n", chunk[i]);
		}
      
		float res = aubiowrapper.vec_median(in);
		System.out.println(res);
		
		SWIGTYPE_p_aubio_biquad_t biquad = aubiowrapper.new_aubio_biquad(1, 0, -1, 0.1, 0.9);
		aubiowrapper.aubio_biquad_do(biquad, in);
		System.out.println("Biquad Filter Results");
		for (int i = 0; i < (int)(fs * duration); i++) {
			System.out.printf("%.2f\n", aubiowrapper.fvec_read_sample(in, 0, i));
		}

//		// Creates the input vector
//		System.out.println("Input vector");
//		SWIGTYPE_p_fvec_t in = aubiowrapper.new_fvec(N, 1);
//		for (int i = 0; i < N; i++) {
//			aubiowrapper.fvec_write_sample(in, (float)buffer[i], 0, i);
////			System.out.printf("%.2f\n", chunk[i]);
//		}
//      
//		// Creates the output vector
//		SWIGTYPE_p_cvec_t out = aubiowrapper.new_cvec(N, 1);
//      
//		// FFT
//		aubiowrapper.aubio_mfft_do(mfft_t, in, out);
//      
//		// Results
//		System.out.println("FFT Results");
//		for (int i = 0; i < N/2+1; i++) {
//			float norm = aubiowrapper.cvec_read_norm(out, 0, i);
//			float phas = aubiowrapper.cvec_read_phas(out, 0, i);
//			double mag = Math.sqrt((norm * norm) + (phas * phas));
////			System.out.printf("%.2f Hz \t %.2f \t %.2f\n", i*Fs/N, norm, phas);
//			System.out.printf("%.2f Hz \t %.2f\n", i*fs/N, mag);
//		}
//    
//		// Creates the seconde output vector
//		SWIGTYPE_p_fvec_t out2 = aubiowrapper.new_fvec(N, 1);
//		
//		aubiowrapper.aubio_mfft_rdo(mfft_t, out, out2);
		
//		// Results
//		System.out.println("IFFT Results");
//		for (int i = 0; i < N; i++) {
//			System.out.printf("%.2f\n", aubiowrapper.fvec_read_sample(out2, 0, i));
//		}

    }
	
}
