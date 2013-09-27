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

package ensemble;

// TODO: Auto-generated Javadoc
/**
 * The Class EventHandlerInfo.
 */
public class EventHandlerInfo {

	/** The agent name. */
	public String agentName;
	
	/** The component name. */
	public String componentName;
	
	/** The event type. */
	public String eventType;
	
	/** The eh type. */
	public String ehType;
	
	/**
	 * Instantiates a new event handler info.
	 *
	 * @param agentName the agent name
	 * @param componentName the component name
	 * @param eventType the event type
	 * @param ehType the eh type
	 */
	public EventHandlerInfo(String agentName, String componentName, String eventType, String ehType) {
		this.agentName = agentName;
		this.componentName = componentName;
		this.eventType = eventType;
		this.ehType = ehType;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return (agentName+":"+componentName+":"+eventType+":"+ehType);
	}
	
	/**
	 * Parses the.
	 *
	 * @param str the str
	 * @return the event handler info
	 */
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
