/** 
 * ActionTendencies.java - Implements a character's Action Tendencies
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
 * Created: 17/01/2004 
 * @author: João Dias
 * Email to: joao.assis@tagus.ist.utl.pt
 * 
 * History: 
 * João Dias: 17/01/2004 - File created
 * João Dias: 23/05/2006 - Added comments to each public method's header
 * João Dias: 02/07/2006 - Replaced System's timer by an internal agent simulation timer
 * João Dias: 10/07/2006 - the class is now serializable
 * João Dias: 27/12/2006 - Added Method ReinforceActionTendency
 */

package FAtiMA.reactiveLayer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;

import FAtiMA.AgentSimulationTime;
import FAtiMA.IntegrityValidator;
import FAtiMA.ValuedAction;
import FAtiMA.emotionalState.EmotionalState;
import FAtiMA.exceptions.UnknownSpeechActException;


/**
 * Implements a character's set of Action Tendencies and implements
 * the selection mechanism
 * 
 * @author João Dias
 */
public class ActionTendencies implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private ArrayList _actions;
	private HashMap _filteredActions;
	
	/**
	 * Create a new ActionTendenciesSet
	 */
	public ActionTendencies() {
		_actions = new ArrayList();
		_filteredActions = new HashMap();
	}

	/**
	 * Adds a Action to the ActionTendencies Set
	 * @param action
	 */
	public void AddAction(Action action) {
		_actions.add(action);
	}
	
	/**
	 * Checks the Integrity of all the actions by testing if any of them
	 * refers to a SpeechAct not defined
	 * @param val - an IntegrityValidator used do detect undefined speechActs
	 * @throws UnknownSpeechActException - thrown when an action references a SpeechAct
	 * 				 					   not defined
	 * @see IntegrityValidator
	 */
	public void CheckIntegrity(IntegrityValidator val) throws UnknownSpeechActException {
	    ListIterator li = _actions.listIterator();
	
	    while(li.hasNext()) {
	       ((Action) li.next()).CheckIntegrity(val);
	    }
	}
	
	/**
	 * Temporarly deactives an action tendency. It means that even if the
	 * action's preconditions and emotions are verified, the action is not 
	 * selected for execution
	 * @param va -  the action tendency to be deactivated
	 * @param time - the amount of time that the action should be deactivated
	 */
	public void IgnoreActionForDuration(ValuedAction va, long time) {
		Long wakeUpTime = new Long(AgentSimulationTime.GetInstance().Time() + time);
		_filteredActions.put(va.GetAction().toString(),wakeUpTime);
	}
	
	private boolean isIgnored(ValuedAction va) {
		String actionName = va.GetAction().toString();
		if(_filteredActions.containsKey(actionName)) {
			Long wakeUpTime = (Long)_filteredActions.get(actionName);
			return AgentSimulationTime.GetInstance().Time() < wakeUpTime.longValue();
		}
		else return false;
	}
	
	/**
	 * Selects the most appropriate ActionTendency given the 
	 * character's emotional state 
	 * @param emState - the agent's emotional state that influences the actions performed
	 * @return the most relevant Action (according to the emotional state)
	 */
	public ValuedAction SelectAction(EmotionalState emState) {
		Iterator it;
		Action a;
		ValuedAction va;
		ValuedAction bestAction = null;
		
		it = _actions.iterator();
		while(it.hasNext()) {
			a = (Action) it.next();
			va = a.TriggerAction(emState.GetEmotionsIterator());
			if (va != null && !isIgnored(va)) {
				if(bestAction == null || va.GetValue() > bestAction.GetValue()) 
				{
				    bestAction = va;
				}
			}	
		}
		
		return bestAction;
	}
	
	public void ReinforceActionTendency(String action)
	{
		action = action.toLowerCase();
		Action a;
		for(ListIterator li = _actions.listIterator();li.hasNext();)
		{
			a = (Action) li.next();
			if(a.getName().toString().toLowerCase().contains(action))
			{
				System.out.println("");
				System.out.println("Reinforcing AT: " + a.getName());
				System.out.println("");
				a.ReinforceAction(2);
			}
		}
	}
	
	public void Print()
	{
		Action act;
		for(ListIterator li = _actions.listIterator();li.hasNext();)
		{
			act = (Action) li.next();
			System.out.println(act.toString());
		}
	}
}