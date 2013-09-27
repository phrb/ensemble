package mms;

import java.util.HashMap;
import java.util.Set;

/**
 * 
 * @author Leandro
 *
 */
public class Parameters extends HashMap<String, Object> {

	/**
	 * Merges a parameters set to this one.
	 * @param param the Parameters object to be merged
	 */
	public synchronized void merge(Parameters param) {
		Set<String> keys = param.keySet();
		for (String key: keys) {
			if (!this.containsKey(key)) {
				this.put(key, param.get(key));
			}
		}
	}
	
	/**
	 * Gets a String parameter. 
	 * @param key name of the parameter
	 * @return value of the parameter
	 */
	public synchronized String get(String key) {
		Object obj = super.get(key);
		if (obj != null && obj instanceof String) {
			return (String)obj;
		} else {
			return null;
		}
	}
	
	/**
	 * Gets a String parameter with an optional value. 
	 * @param key name of the parameter
	 * @param def default value to be returned if the parameter doesn't exist
	 * @return value of the parameter, def if it doesn't exist
	 */
	public synchronized String get(String key, String def) {
		Object obj = super.get(key);
		if (obj != null && obj instanceof String) {
			return (String)obj;
		} else {
			return def;
		}
	}
	
	/**
	 * Gets an Object parameter. 
	 * @param key name of the parameter
	 * @return value of the parameter or null if it doesn't exist
	 */
	public synchronized Object getObject(String key) {
		return super.get(key);
	}
	
	public static Parameters parse(String str) {
		Parameters param = new Parameters();
		
		if (str.length() > 0) {
			str = str.substring(1, str.length()-1);
			String[] str2 = str.split(" ");
			for (int i = 0; i < str2.length; i++) {
				  String[] str3 = str2[i].split("=");
				  if (str3.length == 2) {
					  param.put(str3[0], str3[1]);
				  }
			}
		}
		
		return param;
	}
	
	@Override
	public Object put(String key, Object value) {
		if (value != null) {
			return super.put(key, value);
		}
		else {
			return null;
		}
	}
	
	// Format: {NAME=VALUE; NAME2=VALUE2; ..... }
	public String toString() {
		
		String str = "";
		if (this.size() > 0) { 
			Set<String> keys = this.keySet();
			for (String key: keys) {
				str = str + key + "=" + this.get(key) + " ";
			}
			str = "{" + str.substring(0, str.length()-1) + "}"; 
		} else {
			str = "{}";
		}
		
		return str;
		
	}
	
}
