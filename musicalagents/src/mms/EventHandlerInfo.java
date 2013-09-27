package mms;

public class EventHandlerInfo {

	public String agentName;
	public String componentName;
	public String eventType;
	public String ehType;
	
	public EventHandlerInfo(String agentName, String componentName, String eventType, String ehType) {
		this.agentName = agentName;
		this.componentName = componentName;
		this.eventType = eventType;
		this.ehType = ehType;
	}

	public String toString() {
		return (agentName+":"+componentName+":"+eventType+":"+ehType);
	}
	
	public static EventHandlerInfo parse(String str) {
		String[] str2 = str.split(":");
		if (str2.length == 4) {
			if (!str2[0].isEmpty() && !str2[1].isEmpty() && !str2[2].isEmpty() && !str2[3].isEmpty()) {
				return new EventHandlerInfo(str2[0], str2[1], str2[2], str2[3]);
			}
		}
		return null;
	}
	
}
