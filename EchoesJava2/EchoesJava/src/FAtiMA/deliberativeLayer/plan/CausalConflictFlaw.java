/** 
 * CausalConflictFlaw.java - Represents a threat to a causal link
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
 * João Dias: 16/05/2004 - Instead of storing a reference to the Step in the Class, now
 * 						   we store only the Step's identifier in the plan
 * João Dias: 22/05/2006 - Added comments to each public method's header
 * João Dias: 10/07/2006 - the class is now serializable 
 */

package FAtiMA.deliberativeLayer.plan;

import java.io.Serializable;

/**
 * Represents a threat to a causal conflict A --p--> B. It means that exists a Step S,
 * that can occur between A and B in the plan and that has ~p as effect
 * 
 * @author João Dias
 */

public class CausalConflictFlaw implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Effect _effect;
	private CausalLink _link;
	private Integer _step;
	
	/**
	 * Creates a new CausalConflictFlaw
	 * @param link - the CausalLink referenced by the threat 
	 * @param step - the id of the Step that threatens the CausalLink
	 * @param eff - the effect of the Step that threatens the CausalLink 
	 */
	public CausalConflictFlaw(CausalLink link, Integer step, Effect eff) {
		_link = link;
		_step = step;
		_effect = eff;
	}
	
	/**
	 * Gets the CausalLink referenced by the threat
	 * @return the threatened CausalLink
	 */
	public CausalLink GetCausalLink() {
		return _link;
	}
	
	/**
	 * Gets the effect that threatens the CausalLink
	 * @return the Effect that threatens the CausalLink
	 */
	public Effect GetEffect() {
		return _effect;
	}
	
	/**
	 * Gets the Step that threatens the CausalLink
	 * @return the ID in the Plan of the step that
	 *         threatens the CausalLink
	 */
	public Integer GetStep() {
		return _step;
	}
}
