package mms.world;

import java.util.HashMap;

/**
 * Represents the state of an entity in the environment (agent or not) at a given instant. 
 * @author lfthomaz
 *
 */
public class EntityState {

	// State instant in seconds
	public double instant;
	
	protected HashMap<String,Object> attributes;
	
	public EntityState() {
		attributes = new HashMap<String, Object>();
	}
	
	public double getInstant() {
		return this.instant;
	}
	
	public Object getEntityStateAttribute(String attributeName) {
		return attributes.get(attributeName);
	}
		
    /**
     * Updates a variable from an entity's state. This method can be overloaded by the user to check the data beign updated.
     * @param entityName
     * @param variable
     * @param value
     * @return if the update was successful
     */
    public boolean updateEntityStateAttribute(String entityName, String variable, Object value) {
    	// Aqui podemos fazer uma checagem no valor variável, para ver se é válido
    	boolean result = true;
    	attributes.put(variable, value);
    	return result;
    }
    
}
