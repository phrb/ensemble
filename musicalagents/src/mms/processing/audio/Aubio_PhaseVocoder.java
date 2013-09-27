package mms.processing.audio;

import aubio.SWIGTYPE_p_aubio_pvoc_t;
import aubio.SWIGTYPE_p_cvec_t;
import aubio.SWIGTYPE_p_fvec_t;
import aubio.aubiowrapper;
import mms.Parameters;
import mms.processing.Processor;

public class Aubio_PhaseVocoder extends Processor {

	private static final String PARAM_SIZE 			= "size";
	private static final String PARAM_HOPSIZE 		= "hopsize";
	private static final String PARAM_SAMPLE_RATE	= "sample_rate";
	private static final String PARAM_OUTPUT_TYPE 	= "output_type";
	private static final String PARAM_INVERSE		= "inverse";

	private SWIGTYPE_p_aubio_pvoc_t pv;
	private int size;
	private int hopsize;
	private double sample_rate;
	private String output_type;
	private boolean inverse;
	
	@Override
	public boolean init() {

		// Validate arguments
		size = Integer.valueOf(arguments.get(PARAM_SIZE, "512"));
		hopsize = Integer.valueOf(arguments.get(PARAM_HOPSIZE, "256"));
		sample_rate = Double.valueOf(arguments.get(PARAM_SAMPLE_RATE, "44100"));
		output_type = arguments.get(PARAM_OUTPUT_TYPE, "polar"); // real, complex, polar
		inverse = Boolean.parseBoolean(arguments.get(PARAM_INVERSE, "false"));
		
		// Initializes pv
		pv = aubiowrapper.new_aubio_pvoc(size, hopsize, 1);
		
		return true;
	}

	@Override
	public Object process(Parameters arguments, Object in) {

		// Valide input data
		if (!(in instanceof double[])) {
			System.out.println("FFT: input must be a double[]");
			return null;
		}
		double[] chunk = (double[])in;
		
		// Creates the input vector
		SWIGTYPE_p_fvec_t in_fvec = aubiowrapper.new_fvec(size, 1);
		for (int i = 0; i < size; i++) {
			aubiowrapper.fvec_write_sample(in_fvec, (float)chunk[i], 0, i);
		}
		
		// Creates the output vector
		SWIGTYPE_p_cvec_t out_cvec = aubiowrapper.new_cvec(size, 1);
      
		aubiowrapper.aubio_pvoc_do(pv, in_fvec, out_cvec);
		
		// Creates output vector
		
		return null;
	}

	@Override
	public boolean finit() {
		aubiowrapper.del_aubio_pvoc(pv);
		return true;
	}

}
