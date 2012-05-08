/** 
 * GoalThreat.java - Represents a Threat to an interest goals' protected condition
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
 * Created: 18/12/2004 
 * @author: João Dias
 * Email to: joao.assis@tagus.ist.utl.pt
 * 
 * History: 
 * João Dias: 18/12/2004 - File created
 * João Dias: 22/05/2006 - Added comments to each public method's header
 * João Dias: 10/07/2006 - the class is now serializable 
 */

package FAtiMA.deliberativeLayer.plan;

import java.io.Serializable;

/**
 * Represents an InterestGoal Threat
 * 
 * @author João Dias
 */

public class GoalThreat implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private ProtectedCondition _cond;
	private Effect _effect;
	private Step _step;
	
	/**
	 * Creates a new GoalThreat
	 * @param precond - the Goal's condition that we want to protect and that is 
	 * 					being threatened
	 * @param s - the Step that threatens the protected condition
	 * @param eff - the effect that threatens the protected condition
	 */
	public GoalThreat(ProtectedCondition precond, Step s, Effect eff) {
		_cond = precond;
		_step = s;
		_effect = eff;
	}
	
	/**
	 * Gets the Goal's protected condition being threatened 
	 * @return the Goal's ProtectedCondition
	 */
	public ProtectedCondition getCond() {
		return _cond;
	}
	
	/**
	 * Gets the Effect that threatens the protected condition
	 * @return the threatening effect
	 */
	public Effect getEffect() {
		return _effect;
	}
	
	/**
	 * Gets the Step that threatens the protected condition
	 * @return the threatening Step
	 */
	public Step getStep() {
		return _step;
	}
}