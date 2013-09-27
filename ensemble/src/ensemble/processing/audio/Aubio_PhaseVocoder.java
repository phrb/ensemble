/******************************************************************************

Copyright 2011 Leandro Ferrari Thomaz

This file is part of Ensemble.

Ensemble is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Ensemble is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Ensemble.  If not, see <http://www.gnu.org/licenses/>.

******************************************************************************/

package ensemble.processing.audio;

import ensemble.Parameters;
import ensemble.processing.Processor;
import aubio.SWIGTYPE_p_aubio_pvoc_t;
import aubio.SWIGTYPE_p_cvec_t;
import aubio.SWIGTYPE_p_fvec_t;
import aubio.aubiowrapper;

// TODO: Auto-generated Javadoc
/**
 * The Class Aubio_PhaseVocoder.
 */
public class Aubio_PhaseVocoder extends Processor {

	/** The Constant PARAM_SIZE. */
	private static final String PARAM_SIZE 			= "size";
	
	/** The Constant PARAM_HOPSIZE. */
	private static final String PARAM_HOPSIZE 		= "hopsize";
	
	/** The Constant PARAM_SAMPLE_RATE. */
	private static final String PARAM_SAMPLE_RATE	= "sample_rate";
	
	/** The Constant PARAM_OUTPUT_TYPE. */
	private static final String PARAM_OUTPUT_TYPE 	= "output_type";
	
	/** The Constant PARAM_INVERSE. */
	private static final String PARAM_INVERSE		= "inverse";

	/** The pv. */
	private SWIGTYPE_p_aubio_pvoc_t pv;
	
	/** The size. */
	private int size;
	
	/** The hopsize. */
	private int hopsize;
	
	/** The sample_rate. */
	private double sample_rate;
	
	/** The output_type. */
	private String output_type;
	
	/** The inverse. */
	private boolean inverse;
	
	/* (non-Javadoc)
	 * @see ensemble.LifeCycle#init()
	 */
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

	/* (non-Javadoc)
	 * @see ensemble.processing.Processor#process(ensemble.Parameters, java.lang.Object)
	 */
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

	/* (non-Javadoc)
	 * @see ensemble.LifeCycle#finit()
	 */
	@Override
	public boolean finit() {
		aubiowrapper.del_aubio_pvoc(pv);
		return true;
	}

}
