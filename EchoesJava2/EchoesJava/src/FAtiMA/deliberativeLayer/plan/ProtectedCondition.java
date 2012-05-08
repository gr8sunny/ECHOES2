/** 
 * ProtectedCondition.java - Represents a condition that the character wants to protect
 * and corresponds to an Interest Goal
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
 * Created: 6/06/2004 
 * @author: João Dias
 * Email to: joao.assis@tagus.ist.utl.pt
 * 
 * History: 
 * João Dias: 06/06/2004 - File created
 * João Dias: 22/05/2006 - Added comments to each public method's header
 * João Dias: 10/07/2006 - the class is now serializable 
 */

package FAtiMA.deliberativeLayer.plan;

import java.io.Serializable;

import FAtiMA.conditions.Condition;
import FAtiMA.deliberativeLayer.goals.Goal;
import FAtiMA.deliberativeLayer.goals.InterestGoal;


/**
 * Represents an InterestGoal's ProtectedCondition that we want to preserve
 * during the planning proccess.
 *  
 * @author João Dias
 *
 * @see InterestGoal 
 */

public class ProtectedCondition implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Condition _cond;
	private Goal _goal;
	
	/**
	 * Creates a new ProtectedCondition
	 * @param goal - the InterestGoal that this ProtectedCondition references
	 * @param cond - the condition that we want to protect
	 */
	public ProtectedCondition(Goal goal, Condition cond) {
		_goal = goal;
		_cond = cond;
	}
	
	/**
	 * Gets the condition that we want to protect
	 * @return - the protected condition
	 */
	public Condition getCond() {
		return _cond;
	}

	/**
	 * Gets the Goal that wants the condition to be protected
	 * @return the Interest Goal that protects the condition
	 */
	public Goal getGoal() {
		return _goal;
	}
	
	/**
	 * Converts the ProtectedCondition to a String
	 * @return the converted String
	 */
	public String toString() {
		return "ProtectedCondition: " + _cond + " Goal: " + _goal;
	}
}