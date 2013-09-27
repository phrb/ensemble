/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 1.3.31
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package mmsjack;

import java.nio.ByteBuffer;

public class mmsjack implements mmsjackConstants {

	// Loads the portaudio JNI interface
	static {
		try {
			if (System.getProperty("os.name").startsWith("Windows")) {
				if (System.getProperty("sun.arch.data.model").equals("32")) {
					System.loadLibrary("mmsjack");
				} else {
					System.loadLibrary("mmsjack");
				}
			}
			else if (System.getProperty("os.name").startsWith("Mac OS X")) {
				System.loadLibrary("mmsjack");
			}
			else if (System.getProperty("os.name").startsWith("Unix")) {
				System.loadLibrary("mmsjack");
			}		
		} catch (UnsatisfiedLinkError  e) {
			e.printStackTrace();
			System.err.println("mmsjack library not found... JACK will not be available!");
		}
	}

	
  public static int jack_client_close(long client) {
    return mmsjackJNI.jack_client_close(client);
  }

  public static long jack_client_open(String client_name, Object callback) {
    return mmsjackJNI.jack_client_open(client_name, callback);
  }

  public static int jack_get_sample_rate(long arg0) {
    return mmsjackJNI.jack_get_sample_rate(arg0);
  }

  public static long jack_port_register(long client, String port_name, String port_type, long flags) {
    return mmsjackJNI.jack_port_register(client, port_name, port_type, flags);
  }

  public static int jack_port_unregister(long arg0, long arg1) {
	    return mmsjackJNI.jack_port_unregister(arg0, arg1);
  }

  public static int jack_activate(long client) {
    return mmsjackJNI.jack_activate(client);
  }

  public static String[] jack_get_ports(long arg0, String port_name_pattern, String type_name_pattern, long flags) {
    return mmsjackJNI.jack_get_ports(arg0, port_name_pattern, type_name_pattern, flags);
}

  public static int jack_connect(long arg0, String source_port, String destination_port) {
    return mmsjackJNI.jack_connect(arg0, source_port, destination_port);
  }

  public static int jack_disconnect(long arg0, String source_port, String destination_port) {
	    return mmsjackJNI.jack_disconnect(arg0, source_port, destination_port);
	  }

  public static String jack_port_name(long port) {
    return mmsjackJNI.jack_port_name(port);
  }

  public static ByteBuffer jack_port_get_buffer(long arg0, int arg1) {
	    return mmsjackJNI.jack_port_get_buffer(arg0, arg1);
  }

//  public static int jack_set_process_callback(long client, Object process_callback) {
//    return mmsjackJNI.jack_set_process_callback(client), process_callback);
//  }

  public static long jack_port_by_name(long arg0, String port_name) {
    return mmsjackJNI.jack_port_by_name(arg0, port_name);
  }

  public static int jack_port_get_latency(long port) {
    return mmsjackJNI.jack_port_get_latency(port);
  }

  public static int jack_port_get_total_latency(long arg0, long port) {
    return mmsjackJNI.jack_port_get_total_latency(arg0, port);
  }

  public static int jack_frames_since_cycle_start(long arg0) {
    return mmsjackJNI.jack_frames_since_cycle_start(arg0);
  }

  public static int jack_frame_time(long arg0) {
    return mmsjackJNI.jack_frame_time(arg0);
  }

  public static int jack_last_frame_time(long client) {
    return mmsjackJNI.jack_last_frame_time(client);
  }

  public static long jack_frames_to_time(long client, int arg1) {
    return mmsjackJNI.jack_frames_to_time(client, arg1);
  }

  public static int jack_time_to_frames(long client, long arg1) {
    return mmsjackJNI.jack_time_to_frames(client, arg1);
  }

  public static long jack_get_time() {
    return mmsjackJNI.jack_get_time();
  }
}
