/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 1.3.31
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package xtract.core;

public class xtract_function_descriptor_t_result_scalar {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected xtract_function_descriptor_t_result_scalar(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(xtract_function_descriptor_t_result_scalar obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if(swigCPtr != 0 && swigCMemOwn) {
      swigCMemOwn = false;
      xtractJNI.delete_xtract_function_descriptor_t_result_scalar(swigCPtr);
    }
    swigCPtr = 0;
  }

  public void setMin(float value) {
    xtractJNI.xtract_function_descriptor_t_result_scalar_min_set(swigCPtr, this, value);
  }

  public float getMin() {
    return xtractJNI.xtract_function_descriptor_t_result_scalar_min_get(swigCPtr, this);
  }

  public void setMax(float value) {
    xtractJNI.xtract_function_descriptor_t_result_scalar_max_set(swigCPtr, this, value);
  }

  public float getMax() {
    return xtractJNI.xtract_function_descriptor_t_result_scalar_max_get(swigCPtr, this);
  }

  public void setUnit(xtract_unit_t value) {
    xtractJNI.xtract_function_descriptor_t_result_scalar_unit_set(swigCPtr, this, value.swigValue());
  }

  public xtract_unit_t getUnit() {
    return xtract_unit_t.swigToEnum(xtractJNI.xtract_function_descriptor_t_result_scalar_unit_get(swigCPtr, this));
  }

  public xtract_function_descriptor_t_result_scalar() {
    this(xtractJNI.new_xtract_function_descriptor_t_result_scalar(), true);
  }

}
