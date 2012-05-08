/** 
 * OrderingConstraint.java - Represents an ordering constraint in a Plan
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
 * Created: 11/01/2004 
 * @author: João Dias
 * Email to: joao.assis@tagus.ist.utl.pt
 * 
 * History: 
 * João Dias: 11/01/2004 - File created
 * João Dias: 16/05/2004 - Instead of storing references to the steps involved in the
 * 						   Ordering Constraint, now the class stores only the ID's of those
 *						   steps in the plan
 * João Dias: 22/05/2006 - Added comments to each public method's header
 * João Dias: 10/07/2006 - the class is now serializable
 */

package FAtiMA.deliberativeLayer.plan;

import java.io.Serializable;

/**
 * Represents a plan's ordering constraint of the type A < B 
 * (Step A must be executed before B)
 * 
 * @author João Dias
 */
public class OrderingConstraint implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer _after;
	private Integer _before;

	/**
	 * Creates a new OrderingContraints between two Steps. (Receives the step's ID's)
	 * @param before - The Step A (ID) of the ordering constraint A > B
	 * @param after - The Step B (ID) of the ordering constraint A > B
	 */
	public OrderingConstraint(Integer before, Integer after) {
		this._before = before;
		this._after = after;
	}

	/**
	 * Gets the Step B in the ordering constraint A > B
	 * @return the step's ID
	 */
	public Integer getAfter() {
		return _after;
	}

	/**
	 * Gets the Step A in the ordering constraint A > B
	 * @return the step's ID
	 */
	public Integer getBefore() {
		return _before;
	}
	
	/**
	 * Converts the OrderingConstraint to a String
	 * @return the converted String
	 */
	public String toString() {
		return _before + " > " + _after;
	}
}