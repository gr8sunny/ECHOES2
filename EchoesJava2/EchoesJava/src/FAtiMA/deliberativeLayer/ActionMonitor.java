/** 
 * ActionMonitor.java - Implements a monitor capable of verifying if a given
 * action has been achieved. This monitor waits forever.
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
 * João Dias: 10/07/2006 - the class is now serializable 
 */

package FAtiMA.deliberativeLayer;

import java.io.Serializable;

import FAtiMA.deliberativeLayer.plan.Step;
import FAtiMA.sensorEffector.Event;


/**
 * Implements a monitor capable of verifying if a given
 * action has been achieved. This monitor waits forever.
 * @author João Dias
 */
public class ActionMonitor implements Serializable {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected Step _step;
    protected Event _event;
    
    /**
     * Creates a new ActionMonitor that waits forever
     * @param step - the plan's step (action) that we want to monitor
     * @param actionEnd - the event that we should wait for. If this event
     * 					  happens, it means that the action finished
     */
    public ActionMonitor(Step step, Event actionEnd) {
        _step = step;
        _event = actionEnd;
    }
    
    /**
     * Gets the action that the ActionMonitor is monitoring
     * @return - the monitored step
     */
    public Step GetStep() {
        return _step;
    }
    
    /**
     * Matches a received event to see if it corresponds to the end
     * of the action that is being monitored
     * @param e - the event to compare
     * @return true if the received event corresponds to the end of the
     * 		   monitored action, false otherwise
     */
    public boolean MatchEvent(Event e) {
        return Event.MatchEvent(_event,e);
    }
    
    /**
     * indicates if the ActionMonitor expired and we should wait no more.
     * Since this action monitor waits forever, it never expires.
     * @return allways returns false
     */
    public boolean Expired() {
        return false;
    }
}