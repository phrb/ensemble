package mms.processing.audio;

import xtract.core.floatArray;
import xtract.core.xtract;

import mms.Parameters;
import mms.processing.Processor;

public class LibXtract_RMS extends Processor {

	@Override
	public boolean init() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean finit() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Object process(Parameters arguments, Object in) {

		double[] chunk = (double[])in;
	
		// Create the input vector
		floatArray input = new floatArray(chunk.length);
		for (int i = 0; i < chunk.length; i++) {
			input.setitem(i, (float)chunk[i]);
		}

		float[] ret_f = new float[chunk.length];

		xtract.xtract_rms_amplitude(input.cast(), chunk.length, null, ret_f);
	
		return (double)ret_f[0];

	}

}
