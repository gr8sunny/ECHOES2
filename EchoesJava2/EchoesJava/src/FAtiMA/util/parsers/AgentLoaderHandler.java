/** 
 * AgentLoaderHandler.java - Parses an agent's personality 
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
 * João Dias: 12/07/2006 - Removed the reference to the KB from the class.
 * 						   It is no longer needed.
 * João Dias: 15/07/2006 - Removed the EmotionalState from the Class fields since the 
 * 						   EmotionalState is now a singleton that can be used anywhere 
 * 					       without previous references.
 * João Dias: 17/07/2006 - Removed the class constructor that received an agent. The
 * 						   constructor stopped working because of the changes to the 
 * 						   Agent class
 * João Dias: 21/07/2006 - Now the constructor only receives the reactive and deliberative layers
 * João Dias: 31/08/2006 - Added parsing for RecentEvents as conditions
 * 						 - Added parsing for PastEvents as conditions
 * João Dias: 05/09/2006 - Added parsing for InterpersonalRelations
 * João Dias: 07/09/2006 - Changes the parsing of importanceOfSuccess so that you can parse
 * 						   the definition of goals can be made with the previous version of
 * 						   FearNot! files, wich had a typo and defined the importanceOfSuccess
 * 						   with only one c (importanceOfSucess)
 * João Dias: 18/09/2006 - Added parsing for the attribute other when specifying an emotional
 * 					       reaction that relates to an event considering another character
 * João Dias: 28/09/2006 - Added parsing for EmotionConditions
 * João Dias: 10/02/2007 - Added parsing for MoodConditions
 */

package FAtiMA.util.parsers;

import org.xml.sax.Attributes;

import FAtiMA.autobiographicalMemory.AutobiographicalMemory;
import FAtiMA.conditions.EmotionCondition;
import FAtiMA.conditions.EventCondition;
import FAtiMA.conditions.MoodCondition;
import FAtiMA.conditions.PastEventCondition;
import FAtiMA.conditions.PredicateCondition;
import FAtiMA.conditions.PropertyCondition;
import FAtiMA.deliberativeLayer.DeliberativeProcess;
import FAtiMA.emotionalState.BaseEmotion;
import FAtiMA.emotionalState.EmotionDisposition;
import FAtiMA.emotionalState.EmotionalState;
import FAtiMA.exceptions.InvalidEmotionTypeException;
import FAtiMA.exceptions.UnknownGoalException;
import FAtiMA.reactiveLayer.Action;
import FAtiMA.reactiveLayer.Reaction;
import FAtiMA.reactiveLayer.ReactiveProcess;
import FAtiMA.sensorEffector.Event;
import FAtiMA.socialRelations.LikeRelation;
import FAtiMA.socialRelations.RespectRelation;
import FAtiMA.util.enumerables.EmotionType;
import FAtiMA.wellFormedNames.Name;
import FAtiMA.wellFormedNames.Substitution;
import FAtiMA.wellFormedNames.Symbol;



public class AgentLoaderHandler extends ReflectXMLHandler {
	
    private String _characterName;
    private Substitution _self;
    private ReactiveProcess _reactiveLayer;
    private DeliberativeProcess _deliberativeLayer;
    
	private Action _action;
    private String _goalName;
    private BaseEmotion _elicitingEmotion;
    private Reaction _eventReaction;
    
    public AgentLoaderHandler(String characterName, ReactiveProcess reactiveLayer, DeliberativeProcess deliberativeLayer)
    {
    	this._characterName = characterName;
    	this._reactiveLayer = reactiveLayer;
    	this._deliberativeLayer = deliberativeLayer;
    	this._self = new Substitution(new Symbol("[SELF]"),new Symbol(characterName));
    }
    
    public void ActionTendency(Attributes attributes) {
    	_action = new Action(Name.ParseName(attributes.getValue("action")));
    }
    
    public void CauseEvent(Attributes attributes) {
    	Event event = Event.ParseEvent(_characterName,attributes);
    	_elicitingEmotion.SetCause(event);
    }

    public void ElicitingEmotion(Attributes attributes) throws InvalidEmotionTypeException {
    	String emotionName;
    	short type;
    	Integer minIntensity;
    	
    	emotionName = attributes.getValue("type");
    	type = EmotionType.ParseType(emotionName);
    	
    	minIntensity = new Integer(attributes.getValue("minIntensity"));
    	_elicitingEmotion = new BaseEmotion(type,minIntensity.intValue(),null,null);
    	_action.SetElicitingEmotion(_elicitingEmotion);
    	_reactiveLayer.getActionTendencies().AddAction(_action);
    }

    public void EmotionalReaction(Attributes attributes) {
    	Integer desirability=null;
    	Integer desirabilityForOther=null;
    	Integer praiseworthiness=null;
    	Symbol other = null;
    	String aux;
    	
    	aux = attributes.getValue("desirability");
    	if(aux!=null) desirability = new Integer(aux);
    	
    	aux = attributes.getValue("desirabilityForOther");
    	if(aux!=null) desirabilityForOther = new Integer(aux);
    	
    	aux = attributes.getValue("praiseworthiness");
    	if(aux!=null) praiseworthiness = new Integer(aux);
    	
    	aux = attributes.getValue("other");
    	if(aux!=null) other = new Symbol(aux);
    	
    	_eventReaction = new Reaction(desirability, desirabilityForOther, praiseworthiness, other);
    }

    public void EmotionalThreshold(Attributes attributes) throws InvalidEmotionTypeException {
    	String emotionName;
    	short type;
    	
    	emotionName = attributes.getValue("emotion");
    	type = EmotionType.ParseType(emotionName);
        EmotionalState.GetInstance().AddEmotionDisposition(new EmotionDisposition(type,
                                                              new Integer(attributes.getValue("threshold")).intValue(),
                                                              new Integer(attributes.getValue("decay")).intValue()));
    }

    public void Event(Attributes attributes) 
    {
    	Event event = Event.ParseEvent(_characterName, attributes);
     
    	_eventReaction.setEvent(event);
    	_reactiveLayer.getEmotionalReactions().AddEmotionalReaction(_eventReaction);
    }

    public void Goal(Attributes attributes) throws UnknownGoalException 
    {
  
      float impOfSucess;
      float impOfFailure;

      _goalName = attributes.getValue("name");
      String aux = attributes.getValue("importanceOfSucess");
      if(aux == null)
      {
    	  aux = attributes.getValue("importanceOfSuccess");
      }
      impOfSucess = Float.parseFloat(aux);
      impOfFailure = Float.parseFloat(attributes.getValue("importanceOfFailure"));
			
      _deliberativeLayer.AddGoal(_goalName,impOfSucess,impOfFailure); 
    }
    
    public void Predicate(Attributes attributes) 
    {
    	PredicateCondition cond;
    	
    	cond = PredicateCondition.ParsePredicate(attributes);
    	cond.MakeGround(_self);
    	_action.AddPreCondition(cond); 
    }

    public void Property(Attributes attributes) 
    {
      PropertyCondition cond;
      
      cond = PropertyCondition.ParseProperty(attributes);
      cond.MakeGround(_self);
      _action.AddPreCondition(cond);
    }
    
    public void RecentEvent(Attributes attributes)
    {
    	EventCondition event;
    	
    	event = EventCondition.ParseEvent(attributes);
    	event.MakeGround(_self);
    	_action.AddPreCondition(event);
    }
    
    public void PastEvent(Attributes attributes)
    {
    	PastEventCondition event;
    	
    	event = new PastEventCondition(EventCondition.ParseEvent(attributes));
    	event.MakeGround(_self);
    	_action.AddPreCondition(event);
    }
    
    public void EmotionCondition(Attributes attributes)
    {
    	EmotionCondition ec;
    	try
    	{
    		ec = EmotionCondition.ParseEmotionCondition(attributes);
        	ec.MakeGround(_self);
        	_action.AddPreCondition(ec);
    	}
    	catch (InvalidEmotionTypeException e)
    	{
    		e.printStackTrace();
    	}
    }
    
    public void MoodCondition(Attributes attributes)
    {
    	MoodCondition mc;
    	try
    	{
    		mc = MoodCondition.ParseMoodCondition(attributes);
        	mc.MakeGround(_self);
        	_action.AddPreCondition(mc);
    	}
    	catch(Exception e) 
    	{
    		e.printStackTrace();
    	}
    }
    
    public void Relation(Attributes attributes)
    {
    	float respect;
    	String target = attributes.getValue("target");
    	float like = Float.parseFloat(attributes.getValue("like"));
    	LikeRelation.getRelation(AutobiographicalMemory.GetInstance().getSelf(), target).setValue(like);
    	
    	String auxRespect = attributes.getValue("respect");
    	if(auxRespect == null)
    	{
    		respect = 0;
    	}
    	else 
    	{
    		respect = Float.parseFloat(auxRespect);
    	}
    	RespectRelation.getRelation(AutobiographicalMemory.GetInstance().getSelf(), target).setValue(respect);
    }
}