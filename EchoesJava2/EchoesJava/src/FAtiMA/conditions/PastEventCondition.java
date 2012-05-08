/**
 * PastEventCondition.java - 
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
 * Created: 31/Ago/2006
 * @author: João Dias
 * Email to: joao.assis@tagus.ist.utl.pt
 * 
 * History: 
 * João Dias: 31/Ago/2006 - File created
 */

package FAtiMA.conditions;

import java.util.ArrayList;
import java.util.ListIterator;

import FAtiMA.autobiographicalMemory.AutobiographicalMemory;
import FAtiMA.wellFormedNames.Name;
import FAtiMA.wellFormedNames.Substitution;
import FAtiMA.wellFormedNames.Symbol;


/**
 * @author User
 *
 */
public class PastEventCondition extends EventCondition {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private PastEventCondition()
	{
	}
	
	public PastEventCondition(boolean ocurred, Symbol subject, Symbol action, Symbol target, ArrayList parameters)
	{
		super(ocurred, subject, action, target, parameters);
	}
	
	public PastEventCondition(boolean ocurred, Name event)
	{
		super(ocurred, event);
	}
	
	public PastEventCondition(EventCondition event)
	{
		this._positive = event._positive;
		this._name = event._name;
		this._subject = event._subject;
		this._action = event._action;
		this._target = event._target;
		this._parameters = event._parameters;
	}
	
	public Object clone() {
		PastEventCondition newEvent = new PastEventCondition();
		
		newEvent._positive = this._positive;
		
		newEvent._name = (Name) this._name.clone();
		newEvent._subject = (Symbol) this._subject.clone();
		newEvent._action = (Symbol) this._action.clone();
		if(this._target != null)
		{
			newEvent._target = (Symbol) this._target.clone();
		}
		
		newEvent._parameters = new ArrayList(this._parameters.size());
		
		ListIterator li = this._parameters.listIterator();
		
		while(li.hasNext())
		{
			newEvent._parameters.add(((Symbol)li.next()).clone());
		}
		
		return newEvent;
	}

	public Object GenerateName(int id) {
		PastEventCondition event = (PastEventCondition) this.clone();
		event.ReplaceUnboundVariables(id);
		return event;
	}

	public Object Ground(ArrayList bindingConstraints) {
		
		PastEventCondition event = (PastEventCondition) this.clone();
		event.MakeGround(bindingConstraints);
		return event;
	}

	public Object Ground(Substitution subst) {
		PastEventCondition event = (PastEventCondition) this.clone();
		event.MakeGround(subst);
		return event;
	}
	
	public ArrayList GetPossibleBindings()
	{
		return AutobiographicalMemory.GetInstance().
				SearchForPastEvents(GetSearchKeys());
	}
	
	/**
	 * Checks if the EventCondition is verified in the agent's AutobiographicalMemory
	 * @return true if the PastPredicate is verified, false otherwise
	 * @see AutobiographicalMemory
	 */
	public boolean CheckCondition() {
		
		if(!_name.isGrounded()) return false;
		
		return _positive == AutobiographicalMemory.GetInstance().ContainsPastEvent(this); 
	}

}
