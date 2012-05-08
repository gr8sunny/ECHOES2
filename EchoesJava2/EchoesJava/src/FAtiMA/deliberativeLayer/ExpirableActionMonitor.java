/** 
 * ExpirableActionMonitor.java - Implements a monitor capable of verifying if a given
 * action has been achieved. But this monitor waits only for a limited amount of time.
 *  
 * Copyright (C) 2006 GAIPS/INESC-ID 
 *  
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Company: GAIPS/INESC-ID
 * Project: FAtiMA
 * Created: 30/12/2004 
 * @author: João Dias
 * Email to: joao.assis@tagus.ist.utl.pt
 * 
 * History: 
 * João Dias: 30/12/2004 - File created
 * João Dias: 24/05/2006 - Added comments to each public method's header
 * João Dias: 02/07/2006 - Replaced System's timer by an internal agent simulation timer
 * João Dias: 10/07/2006 - the class is now serializable 
 */

package FAtiMA.deliberativeLayer;

import echoesEngine.EchoesAgent;
import FAtiMA.AgentSimulationTime;
import FAtiMA.deliberativeLayer.plan.Step;
import FAtiMA.knowledgeBase.KnowledgeBase;
import FAtiMA.sensorEffector.Event;
import FAtiMA.sensorEffector.Parameter;
import FAtiMA.wellFormedNames.Name;

/**
 * Implements a monitor capable of verifying if a given action has been
 * achieved. But this monitor waits only for a limited amount of time.
 * 
 * @author João Dias
 */
public class ExpirableActionMonitor extends ActionMonitor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long _endTime;

	/**
	 * Creates a new ActionMonitor that expires after some time
	 * 
	 * @param waitTime
	 *            - how long should the monitor wait before expiring
	 * @param s
	 *            - the plan's step (action) that we want to monitor
	 * @param actionEnd
	 *            - the event that we should wait for. If this event happens, it
	 *            means that the action finished
	 */
	public ExpirableActionMonitor(long waitTime, Step s, Event actionEnd) {
		super(s, actionEnd);
		this._endTime = AgentSimulationTime.GetInstance().Time() + waitTime;
	}

	/**
	 * indicates if the ActionMonitor expired and we should wait no more.
	 * 
	 * @return true if the Monitor waited more than specified, false otherwise
	 */
	public boolean Expired() {
		boolean expired = AgentSimulationTime.GetInstance().Time() > _endTime;
		if (expired) {
			// Perceive the timeout
			Event e = new Event(_step.getAgent().getName(), "Timeout", _step
					.getName().GetFirstLiteral().getName());
			boolean first = true;
			String params = "";
			for (Object obj : _step.getName().GetLiteralList()) {
				if (first) {
					first = false;
					params = obj.toString();
				} else {
					e.AddParameter(new Parameter("param", obj.toString()));
					params += "," + obj.toString();
				}
			}
			EchoesAgent.getInstance().PerceiveEvent(e);

			// Add the relevant effect to the KB
			Name timeoutName = Name.ParseName("timedOut("
					+ _step.getAgent().getName() + "," + params + ")");
			KnowledgeBase.GetInstance().Tell(timeoutName, "True");
			resetRequestsToUserInKB();
		}
		return expired;
	}

	/**
	 * Resets the knowledge base properties that set whether the agent has
	 * requested the user to do something. Otherwise it'll be assumed that the
	 * agent has requested the uesr to e.g. give an object when this was done
	 * previously
	 */
	public void resetRequestsToUserInKB() {
		// for stacking activity and receive target flower activity
		for (int i = 1; i < 11; i++) {
			KnowledgeBase
					.GetInstance()
					.Tell(
							Name
									.ParseName("selfRequestedUserTransformObjectToStackable(object"
											+ i + ")"), "False");
			KnowledgeBase.GetInstance().Tell(
					Name.ParseName("selfRequestedUserStackObject(object" + i
							+ ")"), "False");
			KnowledgeBase.GetInstance().Tell(
					Name.ParseName("selfRequestedObjectFromUser(object" + i
							+ ")"), "False");
		}
		KnowledgeBase
				.GetInstance()
				.Tell(
						Name
								.ParseName("selfRequestedUserTransformObjectToStackable(redFlower"),
						"False");
		KnowledgeBase.GetInstance().Tell(
				Name.ParseName("selfRequestedUserStackObject(redFlower)"),
				"False");
		KnowledgeBase.GetInstance().Tell(
				Name.ParseName("selfRequestedObjectFromUser(redFlower)"),
				"False");

		KnowledgeBase
				.GetInstance()
				.Tell(
						Name
								.ParseName("selfRequestedUserTransformObjectToStackable(blueFlower"),
						"False");
		KnowledgeBase.GetInstance().Tell(
				Name.ParseName("selfRequestedUserStackObject(blueFlower)"),
				"False");
		KnowledgeBase.GetInstance().Tell(
				Name.ParseName("selfRequestedObjectFromUser(blueFlower)"),
				"False");

		KnowledgeBase.GetInstance().Tell(
				Name.ParseName("attractedAttentionToOwnEyes()"), "False");
		KnowledgeBase.GetInstance().Tell(
				Name.ParseName("attractedAttentionToOwnHand()"), "False");
		// for explore object properties activity
		KnowledgeBase.GetInstance().Tell(
				Name.ParseName("requestedExploreObjectProperties()"), "False");


	}
}