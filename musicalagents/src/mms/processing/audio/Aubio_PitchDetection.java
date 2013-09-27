package mms.processing.audio;

import aubio.SWIGTYPE_p_aubio_pitchdetection_t;
import aubio.SWIGTYPE_p_fvec_t;
import aubio.aubio_pitchdetection_mode;
import aubio.aubio_pitchdetection_type;
import aubio.aubiowrapper;
import mms.Parameters;
import mms.processing.Processor;

public class Aubio_PitchDetection extends Processor {

	private static final String ARG_BUFSIZE = "bufsize";
	private static final String ARG_HOPSIZE = "hopsize";
	private static final String ARG_SAMPLE_RATE = "sample_rate";
	private static final String ARG_TYPE = "type";
	private static final String ARG_MODE = "mode";
	
	private SWIGTYPE_p_aubio_pitchdetection_t p_t;
	
	private int default_bufsize = 512;
	private int default_hopsize = 256;
	private int default_sample_rate = 44100;
	private aubio_pitchdetection_type default_type = aubio_pitchdetection_type.aubio_pitch_schmitt;
	private aubio_pitchdetection_mode default_mode = aubio_pitchdetection_mode.aubio_pitchm_midi;
	
	
	public boolean init() {
		
		long start = System.currentTimeMillis();
		p_t = aubiowrapper.new_aubio_pitchdetection(default_bufsize, default_hopsize, 1, default_sample_rate, default_type, default_mode);
		long end = System.currentTimeMillis();
		System.out.println("time = " + (end-start));
		
		return true;
		
	}
	
	@Override
	public Object process(Parameters arguments, Object in) {

		// Validate arguments
		int bufsize = Integer.valueOf(arguments.get(ARG_BUFSIZE, "512"));
//		double Fs = Double.valueOf(arguments.get(PARAM_SAMPLE_RATE, "44100"));
//		String fft_output = arguments.get(PARAM_OUTPUT_TYPE, "polar"); // real, complex, polar
//		boolean inverse = Boolean.parseBoolean(arguments.get(PARAM_INVERSE, "false"));

		// Valide input data
		if (!(in instanceof double[])) {
			System.out.println("PITCH_DETECTION: input must be a double[]");
			return null;
		}
		double[] chunk = (double[])in;
		
		// Creates the input vector
		SWIGTYPE_p_fvec_t in_fvec = aubiowrapper.new_fvec(default_bufsize, 1);
		for (int i = 0; i < default_bufsize; i++) {
			aubiowrapper.fvec_write_sample(in_fvec, (float)chunk[i], 0, i);
		}
		
		float smpl_t = aubiowrapper.aubio_pitchdetection(p_t, in_fvec);
		
		return smpl_t;
		
	}

	@Override
	public boolean finit() {

		return true;

	}

}
