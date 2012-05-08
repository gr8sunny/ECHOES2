/** 
 * OpenPrecondition.java - Represents an open precondition in a Plan
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
 * Created: 08/03/2004 
 * @author: João Dias
 * Email to: joao.assis@tagus.ist.utl.pt
 * 
 * History: 
 * João Dias: 08/03/2004 - File created
 * João Dias: 16/05/2006 - Class restructured. Instead of storing a direct reference to a 
 * 						   precondition, this class stores the ID of the Step that needs 
 * 						   the precondition satisfied, and the identifier of the
 * 						   precondition within the step
 * João Dias: 22/05/2006 - Added comments to each public method's header
 * João Dias: 10/07/2006 - the class is now serializable  
 */

package FAtiMA.deliberativeLayer.plan;

import java.io.Serializable;

/**
 * Represents a plan's Open Precondition. An Open Precondition corresponds to a condition
 * that its not achieved by any effect in the plan and must be satisfied in order to complete
 * the plan
 * 
 * @author João Dias
 */
public class OpenPrecondition implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer _condition;
	private Integer _step;
	
	/**
	 * Creates a new OpenPrecondition
	 * @param step - the ID of the step that needs the precondition to be satisfied
	 * @param cond - the ID of the precondition (in the step) that needs to be satified 
	 */
	public OpenPrecondition(Integer step, Integer cond) {
		this._condition = cond;
		this._step = step;
	}

	/**
	 * Gets the precondition that needs to be satisfied
	 * @return the ID of the precondition in the step
	 */
	public Integer getCondition() {
		return _condition;
	}

	/**
	 * Gets the step that needs the precondition satisfied
	 * @return the ID of the Step that needs the precondition satisfied
	 */
	public Integer getStep() {
		return _step;
	}

	/**
	 * Sets the condition that needs to be satisfied
	 * @param condition - the new ID of the precondition in the step
	 */
	public void setCondition(Integer condition) {
		this._condition = condition;
	}

	/**
	 * Sets the Step that needs to have the precondition satisfied
	 * @param operator - the new ID of the Step that needs the precondition satisfied
	 */
	public void setStep(Integer step) {
		this._step = step;
	}
	
	/**
	 * Converts the OpenPrecondition to a String
	 * @return the converted String
	 */
	public String toString() {
		return this._step + ": " + this._condition;
	}

}