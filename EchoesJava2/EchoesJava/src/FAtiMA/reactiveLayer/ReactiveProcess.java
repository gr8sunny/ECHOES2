/** 
 * ReactiveProcess.java - Implements FearNot's Agent Reactive Process (appraisal and coping)
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
 * Created: 21/12/2004 
 * @author: João Dias
 * Email to: joao.assis@tagus.ist.utl.pt
 * 
 * History: 
 * João Dias: 21/12/2004 - File created
 * João Dias: 23/05/2006 - Added comments to each public method's header
 * João Dias: 10/07/2006 - the class is now serializable 
 * João Dias: 15/07/2006 - Removed the EmotionalState from the Class fields since the 
 * 						   EmotionalState is now a singleton that can be used anywhere 
 * 					       without previous references.
 * João Dias: 17/07/2006 - The following methods were moved from class Agent to this class
 * 							  - AddEmotionalReaction
 * 							  - GetEmotionalReactions
 * 							  - GetActionTendencies
 * João Dias: 21/07/2006 - The class constructor now only receives the agent's name
 * João Dias: 05/09/2006 - Changed the way in which Attribution Emotions are generated. From
 * 						   now on, is no longer necessary to specify attribution reactions for
 * 						   a character. They are automatically generated when a "look-at" action
 * 						   is perceived, and the like appraisal variable is retrieved from semantic
 * 						   memory (the KnowledgeBase)
 * 						 - Changed the way in which FortuneOfOthers Emotions are determined, now 
 * 						   the interpersonal relationShip (like/dislike) between characters affects the desirability
 * 						   of the event and thus the final emotions being generated.
 * João Dias: 06/09/2006 - Another change in the way that FortuneOfOthers Emotions are determined,
 * 						   they now are generated for the Actor that performs the actions, and the
 * 						   object or character that is the target of the action. We take into account
 * 						   the like relations between every character to determine the event's desirability.
 * João Dias: 18/09/2006 - Small change in the generation of FortuneOfOther emotions. Now they consider the 
 * 						   the new other variable. If this variable is defined the FortuneOfOther emotion
 * 						   created will be directed to the character specified in the variable. If the variable
 * 						   is not defined (null) the emotion is proccessed as before.
 * João Dias: 20/09/2006 - Removed the method RemoveSelectedAction. The method
 * 						   GetSelectedMethod now additionally has the functionality 
 * 						   of the RemoveSelectedAction method
 * João Dias: 03/02/2007 - the Reset command now removes the selectAction if any
 * João Dias: 04/08/2007 - The intensity of attribution emotions (Love/Hate) were halved
 */

package FAtiMA.reactiveLayer;

import java.util.ListIterator;

import FAtiMA.AgentProcess;
import FAtiMA.ValuedAction;
import FAtiMA.autobiographicalMemory.AutobiographicalMemory;
import FAtiMA.emotionalState.BaseEmotion;
import FAtiMA.emotionalState.EmotionalState;
import FAtiMA.sensorEffector.Event;
import FAtiMA.socialRelations.LikeRelation;
import FAtiMA.util.enumerables.EmotionType;
import FAtiMA.wellFormedNames.Name;
import FAtiMA.wellFormedNames.Symbol;

/**
 * Implements FearNot's Agent Reactive Layer (appraisal and coping processes)
 * @author João Dias
 */
public class ReactiveProcess extends AgentProcess {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final long IGNOREDURATION = 30000;
	
	private ActionTendencies _actionTendencies;
	private EmotionalReactionTreeNode _emotionalReactions;
	private ValuedAction _selectedAction;
	
	/**
	 * Creates a new ReactiveProcess
	 * @param name - the name of the agent
	 */
	public ReactiveProcess(String name) {
		super(name);
		this._actionTendencies = new ActionTendencies();
		this._emotionalReactions = new EmotionalReactionTreeNode(EmotionalReactionTreeNode.subjectNode);
	}
	
	/**
	 * Gets the agent's emotional reactions
	 * @return the root EmotionalReactionTreeNode that stores 
	 * 		   the emotional reaction rules
	 */
	public EmotionalReactionTreeNode getEmotionalReactions() {
		return _emotionalReactions;
	}
	
	/**
	 * Adds a emotional Reacion to the agent's emotional reactions
	 * @param emotionalReaction - the Reaction to add
	 */
	public void AddEmotionalReaction(Reaction emotionalReaction) {
	    _emotionalReactions.AddEmotionalReaction(emotionalReaction);
	}
	
	/**
	 * Gets the agent's action tendencies
	 * @return the agent's ActionTendencies
	 */
	public ActionTendencies getActionTendencies() {
		return _actionTendencies;
	}
	
	/**
	 * Determines an answer to a SpeechAct according to the agent's emotional reactions
	 * @return the best answer to give according to emotional reactions
	 */
	/*
	public ValuedAction AnswerToSpeechAct(SpeechAct speechAct) {
		String answer;
		ActiveEmotion em;
		
		em = EmotionalState.GetInstance().GetStrongestEmotion(speechAct.toEvent());
		if(em != null) {
			if(em.GetValence() == EmotionValence.POSITIVE) {
				answer = "Reply(" + speechAct.getSender() + "," + speechAct.getMeaning() + ",positiveanswer)";
			}
			else {
				answer = "Reply(" + speechAct.getSender() + "," + speechAct.getMeaning() + ",negativeanswer)";
			}
			return new ValuedAction(Name.ParseName(answer),em);
		}
		return null;
	}*/
	
	public void EnforceCopingStrategy(String coping)
	{
		_actionTendencies.ReinforceActionTendency(coping);
	}
	
	
	/**
	 * Reactive appraisal. Appraises received events according to the emotional
	 * reaction rules
	 */
	public void Appraisal() {
		ListIterator li;
		Event event;
		Reaction emotionalReaction;
		
		synchronized (_eventPool) {
			li = _eventPool.listIterator();
			while (li.hasNext()) {
				event = (Event) li.next();
				if(event.GetAction().equals("look-at"))
				{
					int relationShip = Math.round(LikeRelation.getRelation(AutobiographicalMemory.GetInstance().getSelf(), event.GetTarget()).getValue());
					GenerateAttributionEmotions(event,relationShip);
				}
				
				emotionalReaction = _emotionalReactions.MatchEvent(event);
				if(emotionalReaction != null)
				{
					emotionalReaction = (Reaction) emotionalReaction.clone();
					emotionalReaction.MakeGround(event.GenerateBindings());
					GenerateEmotions(event, emotionalReaction);
				}
			}
			_eventPool.clear();
		}
	}
	
	/**
	 * Reactive Coping. Consists in selecting the most relevant action (reaction)
	 * according to the emotional state.
	 */
	public void Coping() {
		ValuedAction action;
		action = _actionTendencies.SelectAction(EmotionalState.GetInstance());
		if(_selectedAction == null || (action != null && action.GetValue() > _selectedAction.GetValue())) {
			_selectedAction = action;
		}
	}
	
	/**
	 * Gets the action selected for execution in the last Coping process,
	 * @return the action selected for execution
	 */
	public ValuedAction GetSelectedAction() {
		
		if(_selectedAction == null)
		{
			return null;
		}
		
		return _selectedAction;
	}
	
	public void RemoveSelectedAction()
	{
		if(_selectedAction == null)
		{
			return;
		}
		
		/*
		 * Temporarily removes the action selected for execution. This means 
		 * that when a action is executed it should not be selected again for a while,
		 * or else we will have a character reacting in the same way several times
		 */
		_actionTendencies.IgnoreActionForDuration(_selectedAction,IGNOREDURATION);
		
		_selectedAction = null;
	}
	
	/**
	 * Resets the reactive layer, clearing all received events that
	 * were not appraised yet
	 */
	public void Reset() {
		_eventPool.clear();
		_selectedAction = null;
	}
	
	/**
	 * prepares the reactive layer for a shutdown
	 */
	public void ShutDown() {
	}
	
	private void GenerateActionBasedEmotions(Event event, int praiseworthiness) {
		BaseEmotion em;
		
		if(praiseworthiness >= 0) {
			if(event.GetSubject().equals(_self)) {
				em = new BaseEmotion(EmotionType.PRIDE, praiseworthiness, event, Name.ParseName("SELF"));
			}
			else {
				em = new BaseEmotion(EmotionType.ADMIRATION, praiseworthiness, event, Name.ParseName(event.GetSubject()));
			}
		}
		else {
			if(event.GetSubject().equals(_self)) {
				em = new BaseEmotion(EmotionType.SHAME, -praiseworthiness, event, Name.ParseName("SELF"));
			}
			else {
				em = new BaseEmotion(EmotionType.REPROACH, -praiseworthiness, event, Name.ParseName(event.GetSubject()));
			}
		}
		
		EmotionalState.GetInstance().AddEmotion(em);
	}
	
	
	private void GenerateAttributionEmotions(Event event, int like) {
		BaseEmotion em;
		
		if(like >= 0) {
			em = new BaseEmotion(EmotionType.LOVE, like*0.7f, event, Name.ParseName(event.GetTarget()));
		}
		else {
			em = new BaseEmotion(EmotionType.HATE, -like*0.7f, event, Name.ParseName(event.GetTarget()));
		}
		
		EmotionalState.GetInstance().AddEmotion(em);
	}
	
	private void GenerateEmotions(Event event, Reaction emReaction) {
		Integer desirability;
		Integer desirabilityForOther;
		Integer praiseworthiness;
		Symbol other;
		
		desirability = emReaction.getDesirability();
		desirabilityForOther = emReaction.getDesirabilityForOther();
		praiseworthiness = emReaction.getPraiseworthiness();
		other = emReaction.getOther();
		
		//WellBeingEmotions: Joy, Distress
		if (desirability != null) {
			GenerateWellBeingEmotions(event, desirability.intValue());
				
			//FortuneOfOtherEmotions: HappyFor, Gloating, Resentment, Pitty
			if(desirabilityForOther != null) {
				GenerateFortuneForAll(event, desirability.intValue(), desirabilityForOther.intValue(), other);
			}
		}
		
		if (praiseworthiness != null) {
			GenerateActionBasedEmotions(event, praiseworthiness.intValue());
		}
	}
	
	private void GenerateFortuneForAll(Event event, int desirability, int desirabilityForOther, Symbol other)
	{
		float targetBias = 0;
		float subjectBias = 0;
		float bias;
		int newDesirability = 0;
		
		String target = event.GetTarget();
		String subject = event.GetSubject();
		
		if(other != null && other.isGrounded())
		{
			target = other.toString();
		}		

				
		if(target != null && !target.equals(_self))
		{	
			targetBias = LikeRelation.getRelation(AutobiographicalMemory.GetInstance().getSelf(),
					event.GetTarget()).getValue() * desirabilityForOther/10;
			bias = targetBias;
			if(!subject.equals(_self))
			{
				subjectBias = LikeRelation.getRelation(AutobiographicalMemory.GetInstance().getSelf(),
						event.GetSubject()).getValue();
				bias = (bias + subjectBias)/2;
			}
		}
		else 
		{
			subjectBias = LikeRelation.getRelation(AutobiographicalMemory.GetInstance().getSelf(),
					event.GetSubject()).getValue() * desirabilityForOther/10;
			bias = subjectBias;
		}

		newDesirability = Math.round((desirability + bias)/2);

		if(target != null && !target.equals(_self))
		{
			GenerateFortuneOfOtherEmotions(event, newDesirability, desirabilityForOther,target);
			if(!subject.equals(_self))
			{
				GenerateFortuneOfOtherEmotions(event, newDesirability, 10, subject);
			}
		}
		else
		{
			GenerateFortuneOfOtherEmotions(event, newDesirability, desirabilityForOther, subject);
		}
	}
	
	private void GenerateFortuneOfOtherEmotions(Event event, int desirability, int desirabilityForOther, String target) {
		BaseEmotion em;
		float potential;
		
		potential = (Math.abs(desirabilityForOther) + Math.abs(desirability)) / 2.0f;
		
		if(desirability >= 0) {
			if(desirabilityForOther >= 0) {
				em = new BaseEmotion(EmotionType.HAPPYFOR, potential, event, Name.ParseName(target));	
			}
			else {
				em = new BaseEmotion(EmotionType.GLOATING, potential, event, Name.ParseName(target));
			}
		}
		else {
			if(desirabilityForOther >= 0) {
				em = new BaseEmotion(EmotionType.RESENTMENT, potential, event, Name.ParseName(target));
			}
			else {
				em = new BaseEmotion(EmotionType.PITTY, potential, event, Name.ParseName(target));
			}
		}
		
		EmotionalState.GetInstance().AddEmotion(em);
	}
	
	private void GenerateWellBeingEmotions(Event event, int desirability) {
		BaseEmotion em;
		
		if(desirability >= 0) {
			em = new BaseEmotion(EmotionType.JOY, desirability, event, null);
		}
		else {
			em = new BaseEmotion(EmotionType.DISTRESS, -desirability, event, null);
		}
		
		EmotionalState.GetInstance().AddEmotion(em);
	}
}