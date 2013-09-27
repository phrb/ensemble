/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 1.3.31
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package xtract.core;

class xtractJNI {
  public final static native long get_descriptor(long jarg1, xtract_function_descriptor_t jarg1_, int jarg2);
  public final static native long create_filterbank(int jarg1, int jarg2);
  public final static native void destroy_filterbank(long jarg1, xtract_mel_filter jarg1_);
  public final static native long new_floatArray(int jarg1);
  public final static native void delete_floatArray(long jarg1);
  public final static native float floatArray_getitem(long jarg1, floatArray jarg1_, int jarg2);
  public final static native void floatArray_setitem(long jarg1, floatArray jarg1_, int jarg2, float jarg3);
  public final static native long floatArray_cast(long jarg1, floatArray jarg1_);
  public final static native long floatArray_frompointer(long jarg1);
  public final static native long new_intArray(int jarg1);
  public final static native void delete_intArray(long jarg1);
  public final static native int intArray_getitem(long jarg1, intArray jarg1_, int jarg2);
  public final static native void intArray_setitem(long jarg1, intArray jarg1_, int jarg2, int jarg3);
  public final static native long intArray_cast(long jarg1, intArray jarg1_);
  public final static native long intArray_frompointer(long jarg1);
  public final static native void test();
  public final static native int xtract_mean(long jarg1, int jarg2, long jarg3, float[] jarg4);
  public final static native int xtract_variance(long jarg1, int jarg2, long jarg3, float[] jarg4);
  public final static native int xtract_standard_deviation(long jarg1, int jarg2, long jarg3, float[] jarg4);
  public final static native int xtract_average_deviation(long jarg1, int jarg2, long jarg3, float[] jarg4);
  public final static native int xtract_skewness(long jarg1, int jarg2, long jarg3, float[] jarg4);
  public final static native int xtract_kurtosis(long jarg1, int jarg2, long jarg3, float[] jarg4);
  public final static native int xtract_spectral_mean(long jarg1, int jarg2, long jarg3, float[] jarg4);
  public final static native int xtract_spectral_variance(long jarg1, int jarg2, long jarg3, float[] jarg4);
  public final static native int xtract_spectral_standard_deviation(long jarg1, int jarg2, long jarg3, float[] jarg4);
  public final static native int xtract_spectral_average_deviation(long jarg1, int jarg2, long jarg3, float[] jarg4);
  public final static native int xtract_spectral_skewness(long jarg1, int jarg2, long jarg3, float[] jarg4);
  public final static native int xtract_spectral_kurtosis(long jarg1, int jarg2, long jarg3, float[] jarg4);
  public final static native int xtract_spectral_centroid(long jarg1, int jarg2, long jarg3, float[] jarg4);
  public final static native int xtract_irregularity_k(long jarg1, int jarg2, long jarg3, float[] jarg4);
  public final static native int xtract_irregularity_j(long jarg1, int jarg2, long jarg3, float[] jarg4);
  public final static native int xtract_tristimulus_1(long jarg1, int jarg2, long jarg3, float[] jarg4);
  public final static native int xtract_tristimulus_2(long jarg1, int jarg2, long jarg3, float[] jarg4);
  public final static native int xtract_tristimulus_3(long jarg1, int jarg2, long jarg3, float[] jarg4);
  public final static native int xtract_smoothness(long jarg1, int jarg2, long jarg3, float[] jarg4);
  public final static native int xtract_spread(long jarg1, int jarg2, long jarg3, float[] jarg4);
  public final static native int xtract_zcr(long jarg1, int jarg2, long jarg3, float[] jarg4);
  public final static native int xtract_rolloff(long jarg1, int jarg2, long jarg3, float[] jarg4);
  public final static native int xtract_loudness(long jarg1, int jarg2, long jarg3, float[] jarg4);
  public final static native int xtract_flatness(long jarg1, int jarg2, long jarg3, float[] jarg4);
  public final static native int xtract_flatness_db(long jarg1, int jarg2, long jarg3, float[] jarg4);
  public final static native int xtract_tonality(long jarg1, int jarg2, long jarg3, float[] jarg4);
  public final static native int xtract_noisiness(long jarg1, int jarg2, long jarg3, float[] jarg4);
  public final static native int xtract_rms_amplitude(long jarg1, int jarg2, long jarg3, float[] jarg4);
  public final static native int xtract_spectral_inharmonicity(long jarg1, int jarg2, long jarg3, float[] jarg4);
  public final static native int xtract_crest(long jarg1, int jarg2, long jarg3, float[] jarg4);
  public final static native int xtract_power(long jarg1, int jarg2, long jarg3, float[] jarg4);
  public final static native int xtract_odd_even_ratio(long jarg1, int jarg2, long jarg3, float[] jarg4);
  public final static native int xtract_sharpness(long jarg1, int jarg2, long jarg3, float[] jarg4);
  public final static native int xtract_spectral_slope(long jarg1, int jarg2, long jarg3, float[] jarg4);
  public final static native int xtract_lowest_value(long jarg1, int jarg2, long jarg3, float[] jarg4);
  public final static native int xtract_highest_value(long jarg1, int jarg2, long jarg3, float[] jarg4);
  public final static native int xtract_sum(long jarg1, int jarg2, long jarg3, float[] jarg4);
  public final static native int xtract_hps(long jarg1, int jarg2, long jarg3, float[] jarg4);
  public final static native int xtract_f0(long jarg1, int jarg2, long jarg3, float[] jarg4);
  public final static native int xtract_failsafe_f0(long jarg1, int jarg2, long jarg3, float[] jarg4);
  public final static native int xtract_nonzero_count(long jarg1, int jarg2, long jarg3, float[] jarg4);
  public final static native int xtract_flux(long jarg1, int jarg2, long jarg3, float[] jarg4);
  public final static native int xtract_lnorm(long jarg1, int jarg2, long jarg3, float[] jarg4);
  public final static native int xtract_difference_vector(long jarg1, int jarg2, long jarg3, long jarg4);
  public final static native int xtract_spectrum(long jarg1, int jarg2, long jarg3, long jarg4);
  public final static native int xtract_autocorrelation_fft(long jarg1, int jarg2, long jarg3, long jarg4);
  public final static native int xtract_mfcc(long jarg1, int jarg2, long jarg3, long jarg4);
  public final static native int xtract_dct(long jarg1, int jarg2, long jarg3, long jarg4);
  public final static native int xtract_autocorrelation(long jarg1, int jarg2, long jarg3, long jarg4);
  public final static native int xtract_amdf(long jarg1, int jarg2, long jarg3, long jarg4);
  public final static native int xtract_asdf(long jarg1, int jarg2, long jarg3, long jarg4);
  public final static native int xtract_bark_coefficients(long jarg1, int jarg2, long jarg3, long jarg4);
  public final static native int xtract_peak_spectrum(long jarg1, int jarg2, long jarg3, long jarg4);
  public final static native int xtract_harmonic_spectrum(long jarg1, int jarg2, long jarg3, long jarg4);
  public final static native int xtract_lpc(long jarg1, int jarg2, long jarg3, long jarg4);
  public final static native int xtract_lpcc(long jarg1, int jarg2, long jarg3, long jarg4);
  public final static native int xtract_subbands(long jarg1, int jarg2, long jarg3, long jarg4);
  public final static native int xtract_windowed(long jarg1, int jarg2, long jarg3, long jarg4);
  public final static native int xtract_features_from_subframes(long jarg1, int jarg2, int jarg3, long jarg4, long jarg5);
  public final static native int xtract_is_denormal(double jarg1);
  public final static native int XTRACT_BARK_BANDS_get();
  public final static native int XTRACT_WINDOW_SIZE_get();
  public final static native int XTRACT_NONE_get();
  public final static native int XTRACT_ANY_get();
  public final static native int XTRACT_UNKNOWN_get();
  public final static native int XTRACT_MAXARGS_get();
  public final static native int XTRACT_MAX_NAME_LENGTH_get();
  public final static native int XTRACT_MAX_AUTHOR_LENGTH_get();
  public final static native int XTRACT_MAX_DESC_LENGTH_get();
  public final static native int XTRACT_FEATURES_get();
  public final static native int XTRACT_INIT_MFCC_get();
  public final static native int XTRACT_HERTZ_get();
  public final static native void xtract_function_descriptor_t_id_set(long jarg1, xtract_function_descriptor_t jarg1_, int jarg2);
  public final static native int xtract_function_descriptor_t_id_get(long jarg1, xtract_function_descriptor_t jarg1_);
  public final static native void xtract_function_descriptor_t_argc_set(long jarg1, xtract_function_descriptor_t jarg1_, int jarg2);
  public final static native int xtract_function_descriptor_t_argc_get(long jarg1, xtract_function_descriptor_t jarg1_);
  public final static native void xtract_function_descriptor_t_is_scalar_set(long jarg1, xtract_function_descriptor_t jarg1_, int jarg2);
  public final static native int xtract_function_descriptor_t_is_scalar_get(long jarg1, xtract_function_descriptor_t jarg1_);
  public final static native void xtract_function_descriptor_t_is_delta_set(long jarg1, xtract_function_descriptor_t jarg1_, int jarg2);
  public final static native int xtract_function_descriptor_t_is_delta_get(long jarg1, xtract_function_descriptor_t jarg1_);
  public final static native long xtract_function_descriptor_t_result_get(long jarg1, xtract_function_descriptor_t jarg1_);
  public final static native long xtract_function_descriptor_t_argv_get(long jarg1, xtract_function_descriptor_t jarg1_);
  public final static native long xtract_function_descriptor_t_data_get(long jarg1, xtract_function_descriptor_t jarg1_);
  public final static native long xtract_function_descriptor_t_algo_get(long jarg1, xtract_function_descriptor_t jarg1_);
  public final static native long new_xtract_function_descriptor_t();
  public final static native void delete_xtract_function_descriptor_t(long jarg1);
  public final static native long xtract_function_descriptor_t_result_vector_get(long jarg1, xtract_function_descriptor_t_result jarg1_);
  public final static native long xtract_function_descriptor_t_result_scalar_get(long jarg1, xtract_function_descriptor_t_result jarg1_);
  public final static native long new_xtract_function_descriptor_t_result();
  public final static native void delete_xtract_function_descriptor_t_result(long jarg1);
  public final static native void xtract_function_descriptor_t_result_vector_format_set(long jarg1, xtract_function_descriptor_t_result_vector jarg1_, int jarg2);
  public final static native int xtract_function_descriptor_t_result_vector_format_get(long jarg1, xtract_function_descriptor_t_result_vector jarg1_);
  public final static native void xtract_function_descriptor_t_result_vector_unit_set(long jarg1, xtract_function_descriptor_t_result_vector jarg1_, int jarg2);
  public final static native int xtract_function_descriptor_t_result_vector_unit_get(long jarg1, xtract_function_descriptor_t_result_vector jarg1_);
  public final static native long new_xtract_function_descriptor_t_result_vector();
  public final static native void delete_xtract_function_descriptor_t_result_vector(long jarg1);
  public final static native void xtract_function_descriptor_t_result_scalar_min_set(long jarg1, xtract_function_descriptor_t_result_scalar jarg1_, float jarg2);
  public final static native float xtract_function_descriptor_t_result_scalar_min_get(long jarg1, xtract_function_descriptor_t_result_scalar jarg1_);
  public final static native void xtract_function_descriptor_t_result_scalar_max_set(long jarg1, xtract_function_descriptor_t_result_scalar jarg1_, float jarg2);
  public final static native float xtract_function_descriptor_t_result_scalar_max_get(long jarg1, xtract_function_descriptor_t_result_scalar jarg1_);
  public final static native void xtract_function_descriptor_t_result_scalar_unit_set(long jarg1, xtract_function_descriptor_t_result_scalar jarg1_, int jarg2);
  public final static native int xtract_function_descriptor_t_result_scalar_unit_get(long jarg1, xtract_function_descriptor_t_result_scalar jarg1_);
  public final static native long new_xtract_function_descriptor_t_result_scalar();
  public final static native void delete_xtract_function_descriptor_t_result_scalar(long jarg1);
  public final static native void xtract_function_descriptor_t_argv_type_set(long jarg1, xtract_function_descriptor_t_argv jarg1_, int jarg2);
  public final static native int xtract_function_descriptor_t_argv_type_get(long jarg1, xtract_function_descriptor_t_argv jarg1_);
  public final static native void xtract_function_descriptor_t_argv_min_set(long jarg1, xtract_function_descriptor_t_argv jarg1_, long jarg2);
  public final static native long xtract_function_descriptor_t_argv_min_get(long jarg1, xtract_function_descriptor_t_argv jarg1_);
  public final static native void xtract_function_descriptor_t_argv_max_set(long jarg1, xtract_function_descriptor_t_argv jarg1_, long jarg2);
  public final static native long xtract_function_descriptor_t_argv_max_get(long jarg1, xtract_function_descriptor_t_argv jarg1_);
  public final static native void xtract_function_descriptor_t_argv_def_set(long jarg1, xtract_function_descriptor_t_argv jarg1_, long jarg2);
  public final static native long xtract_function_descriptor_t_argv_def_get(long jarg1, xtract_function_descriptor_t_argv jarg1_);
  public final static native void xtract_function_descriptor_t_argv_unit_set(long jarg1, xtract_function_descriptor_t_argv jarg1_, long jarg2);
  public final static native long xtract_function_descriptor_t_argv_unit_get(long jarg1, xtract_function_descriptor_t_argv jarg1_);
  public final static native void xtract_function_descriptor_t_argv_donor_set(long jarg1, xtract_function_descriptor_t_argv jarg1_, long jarg2);
  public final static native long xtract_function_descriptor_t_argv_donor_get(long jarg1, xtract_function_descriptor_t_argv jarg1_);
  public final static native long new_xtract_function_descriptor_t_argv();
  public final static native void delete_xtract_function_descriptor_t_argv(long jarg1);
  public final static native void xtract_function_descriptor_t_data_format_set(long jarg1, xtract_function_descriptor_t_data jarg1_, int jarg2);
  public final static native int xtract_function_descriptor_t_data_format_get(long jarg1, xtract_function_descriptor_t_data jarg1_);
  public final static native void xtract_function_descriptor_t_data_unit_set(long jarg1, xtract_function_descriptor_t_data jarg1_, int jarg2);
  public final static native int xtract_function_descriptor_t_data_unit_get(long jarg1, xtract_function_descriptor_t_data jarg1_);
  public final static native long new_xtract_function_descriptor_t_data();
  public final static native void delete_xtract_function_descriptor_t_data(long jarg1);
  public final static native void xtract_function_descriptor_t_algo_name_set(long jarg1, xtract_function_descriptor_t_algo jarg1_, String jarg2);
  public final static native String xtract_function_descriptor_t_algo_name_get(long jarg1, xtract_function_descriptor_t_algo jarg1_);
  public final static native void xtract_function_descriptor_t_algo_p_name_set(long jarg1, xtract_function_descriptor_t_algo jarg1_, String jarg2);
  public final static native String xtract_function_descriptor_t_algo_p_name_get(long jarg1, xtract_function_descriptor_t_algo jarg1_);
  public final static native void xtract_function_descriptor_t_algo_desc_set(long jarg1, xtract_function_descriptor_t_algo jarg1_, String jarg2);
  public final static native String xtract_function_descriptor_t_algo_desc_get(long jarg1, xtract_function_descriptor_t_algo jarg1_);
  public final static native void xtract_function_descriptor_t_algo_p_desc_set(long jarg1, xtract_function_descriptor_t_algo jarg1_, String jarg2);
  public final static native String xtract_function_descriptor_t_algo_p_desc_get(long jarg1, xtract_function_descriptor_t_algo jarg1_);
  public final static native void xtract_function_descriptor_t_algo_author_set(long jarg1, xtract_function_descriptor_t_algo jarg1_, String jarg2);
  public final static native String xtract_function_descriptor_t_algo_author_get(long jarg1, xtract_function_descriptor_t_algo jarg1_);
  public final static native void xtract_function_descriptor_t_algo_year_set(long jarg1, xtract_function_descriptor_t_algo jarg1_, int jarg2);
  public final static native int xtract_function_descriptor_t_algo_year_get(long jarg1, xtract_function_descriptor_t_algo jarg1_);
  public final static native long new_xtract_function_descriptor_t_algo();
  public final static native void delete_xtract_function_descriptor_t_algo(long jarg1);
  public final static native void xtract_mel_filter_n_filters_set(long jarg1, xtract_mel_filter jarg1_, int jarg2);
  public final static native int xtract_mel_filter_n_filters_get(long jarg1, xtract_mel_filter jarg1_);
  public final static native void xtract_mel_filter_filters_set(long jarg1, xtract_mel_filter jarg1_, long jarg2);
  public final static native long xtract_mel_filter_filters_get(long jarg1, xtract_mel_filter jarg1_);
  public final static native long new_xtract_mel_filter();
  public final static native void delete_xtract_mel_filter(long jarg1);
  public final static native int xtract_init_mfcc(int jarg1, float jarg2, int jarg3, float jarg4, float jarg5, int jarg6, long jarg7);
  public final static native int xtract_init_bark(int jarg1, float jarg2, long jarg3);
  public final static native int xtract_init_fft(int jarg1, int jarg2);
  public final static native void xtract_free_fft();
  public final static native long xtract_init_window(int jarg1, int jarg2);
  public final static native void xtract_free_window(long jarg1);
  public final static native long xtract_make_descriptors();
  public final static native int xtract_free_descriptors(long jarg1, xtract_function_descriptor_t jarg1_);
}
