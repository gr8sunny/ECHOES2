/** 
 * Action.java - Represents a Reactive Action that can be used in the Action Tendencies
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
 * Created: 14/01/2004 
 * @author: João Dias
 * Email to: joao.assis@tagus.ist.utl.pt
 * 
 * History: 
 * João Dias: 14/01/2004 - File created
 * João Dias: 23/05/2006 - Added comments to each public method's header
 * João Dias: 10/07/2006 - the class is now serializable
 * João Dias: 12/07/2006 - Replaced the deprecated Ground methods for the new ones.
 * João Dias: 18/09/2006 - Improved EmotionCheck method by adding substitutions related
 * 						   to an event's parameters. If the event has parameters,
 * 						   the returned binding list will contain the substitutions
 * 						   [P1]/param1, [P2]/param2, etc..
 * João Dias: 03/10/2006 - The activation of ActionTendencies now properly takes into account
 * 						   the list of preconditions. Now, more than one specific action can be
 * 						   activated by a generic ActionTendency (one for each possible 
 * 						   substitution set). 
 * 						 - Removed private method EmotionCheck(ActiveEmotion) that was no longer being
 * 						   used
 * João Dias: 27/12/2006 - Added the getters getName and getElicitingEmotion
 * 						 - Added the methods ReinforceAction and SuppressAction 
 */

package FAtiMA.reactiveLayer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

import FAtiMA.IIntegrityTester;
import FAtiMA.IntegrityValidator;
import FAtiMA.ValuedAction;
import FAtiMA.conditions.Condition;
import FAtiMA.emotionalState.ActiveEmotion;
import FAtiMA.emotionalState.BaseEmotion;
import FAtiMA.exceptions.UnknownSpeechActException;
import FAtiMA.sensorEffector.Event;
import FAtiMA.wellFormedNames.Name;
import FAtiMA.wellFormedNames.SubstitutionSet;


/**
 * Represents a Reactive Action Tendency
 * @author João Dias
 */
public class Action implements IIntegrityTester, Serializable {
    
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private BaseEmotion _elicitingEmotion;
	private Name _name;
	private ArrayList _preConditions;

	/**
	 * Creates a new Action (Action Tendency)
	 * @param name - the name of the action
	 */
	public Action(Name name) {
		_name = name;
		_preConditions = new ArrayList(3);
	}
	
	/**
	 * Gets the Action's name
	 * @return - the Name of the ActionTendency
	 */
	public Name getName()
	{
		return this._name;
	}
	
	/**
	 * Get's the Action Tendency's eliciting emotion
	 * @return the BaseEmotion that may trigger the Action Tendency
	 */
	public BaseEmotion GetElicitingEmotion()
	{
		return _elicitingEmotion;
	}

	/**
	 * Adds a precondition to the ActionTendency (that must be verified in order
	 * for the action to be executed)
	 * @param cond - the Condition to add as precondition
	 */
	public void AddPreCondition(Condition cond) {
		_preConditions.add(cond);
	}
	 
	/**
	 * Checks the integrity of the Action Tendency, it checks if the action references
	 * a SpeechAct that is not defined, in that case it throws an exception
	 */
	public void CheckIntegrity(IntegrityValidator val) throws UnknownSpeechActException {
	    val.CheckSpeechAction(_name);
	}

	/**
	 * Sets the emotion that elicits this action tendency. For example, the victim's
	 * action tendency to cry is elicited by a strong distress emotion
	 * @param emotion - the emotion that will elicit this action
	 */
	public void SetElicitingEmotion(BaseEmotion emotion) {
		_elicitingEmotion = emotion;
	}
	
	/**
	 * Reinforces the likelihood of an action tendency to be activated,
	 * by lowering the minimum emotional intensity necessary to activate the AT
	 * by a given value
	 * @param value - the ammount to lower the minimum intensity
	 */
	public void ReinforceAction(int value)
	{
		if(_elicitingEmotion != null)
		{
			float newPotential = _elicitingEmotion.GetPotential() - value;
			if(newPotential < 0)
			{
				newPotential = 0;
			}
			_elicitingEmotion = new BaseEmotion(_elicitingEmotion.GetType(),
					newPotential,
					_elicitingEmotion.GetCause(),
					_elicitingEmotion.GetDirection());
		}
	}
	
	/**
	 * Suppresses the likelihood of an action tendency to be activated, by
	 * increasing the minimum emotional intensity necessary to activate the AT
	 * by a given value 
	 * @param value - the ammount used to increase the minimum intensity
	 */
	public void SuppressAction(int value)
	{
		if(_elicitingEmotion != null)
		{
			float newPotential = _elicitingEmotion.GetPotential() + value;
			if(newPotential > 10)
			{
				newPotential = 10;
			}
			_elicitingEmotion = new BaseEmotion(_elicitingEmotion.GetType(),
					newPotential,
					_elicitingEmotion.GetCause(),
					_elicitingEmotion.GetDirection());
		}
	}
	
	
	public ValuedAction TriggerAction(Iterator emotionsIterator) {
		ActiveEmotion em;
		float maxValue = 0;
		Name action;
		ValuedAction va = null;
		ArrayList substitutionSets;
		SubstitutionSet subSet;
		Event groundEvent;
		
		//first we need to test the action tendency preconditions
		if(_preConditions.size() > 0)
		{
			substitutionSets = Condition.CheckActivation(_preConditions);
		}
		else
		{
			substitutionSets = new ArrayList();
			substitutionSets.add(new SubstitutionSet());
		}
		 
		if(substitutionSets != null) {
			for(Iterator it = emotionsIterator; it.hasNext();)
			{
				em = (ActiveEmotion) it.next();
				if(em.GetType() == _elicitingEmotion.GetType() &&
				   em.GetIntensity() >= _elicitingEmotion.GetPotential())
				{
					//if the emotion has passed these two first tests, we need to
					//check if applying any possible SubstitutionSet to the expected
					//event will match with the perceived emotion event
					for(ListIterator li = substitutionSets.listIterator();li.hasNext();)
					{
						subSet = (SubstitutionSet) li.next();
						groundEvent = (Event) _elicitingEmotion.GetCause().clone();
						groundEvent.MakeGround(subSet.GetSubstitutions());
						if(Event.MatchEvent(groundEvent,em.GetCause()))
						{
							subSet.AddSubstitutions(em.GetCause().GenerateBindings());
							action = (Name) _name.clone();
							action.MakeGround(subSet.GetSubstitutions());
							//the action is selected only if elicited by the most intense emotion
							//who's intensity is greater than the specified minimum intensity (for this particular action)
							if (em.GetIntensity() > maxValue && action.isGrounded()) {
								maxValue = em.GetIntensity();
								va = new ValuedAction(action, em);
							}
						}
					}
				}		
			}
		}
		return va;
	}
	
	public String toString()
	{
		return "AT " + _name + "- PreConditions " + _preConditions + " Emotion: " + _elicitingEmotion;
		
	}
}