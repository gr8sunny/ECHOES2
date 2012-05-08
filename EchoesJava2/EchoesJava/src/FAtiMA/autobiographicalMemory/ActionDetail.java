/** 
 * ActionDetail.java - 
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
 * Created: 18/07/2006 
 * @author: João Dias
 * Email to: joao.assis@tagus.ist.utl.pt
 * 
 * History: 
 * João Dias: 18/07/2006 - File created
 * João Dias: 06/09/2006 - Changed everything about the evaluation
 * 						   used to determine the interpersonal relation between
 * 						   characters and objects. Now, the evaluation field is an arraylist
 * 						   of objects and characters and corresponding like values. These values
 * 						   are changed by emotions such as Pitty, HappyFor, Repproach
 * João Dias: 02/10/2006 - Changes in the way that parameters are compared for MemoryRetrieval
 * Bruno Azenha: 09/04/2007 - Reimplemented the method UpdateEmotionValues so that it uses the SocialRelation
 * 							  package
 */
package FAtiMA.autobiographicalMemory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.ListIterator;

import FAtiMA.emotionalState.ActiveEmotion;
import FAtiMA.emotionalState.BaseEmotion;
import FAtiMA.knowledgeBase.KnowledgeBase;
import FAtiMA.knowledgeBase.KnowledgeSlot;
import FAtiMA.sensorEffector.Event;
import FAtiMA.sensorEffector.Parameter;
import FAtiMA.socialRelations.LikeRelation;
import FAtiMA.socialRelations.RespectRelation;
import FAtiMA.util.enumerables.EmotionType;


/**
 * @author João Dias
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ActionDetail implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int _id;
	
	private String _subject;
	private String _action;
	private String _target;
	private ArrayList _parameters = null;
	
	private KnowledgeSlot _subjectDetails = null;
	private KnowledgeSlot _targetDetails = null;

	
	private BaseEmotion _emotion;
	
	private ArrayList _evaluation;
		
	public ActionDetail(int ID, Event e)
	{
		this._id = ID;
		
		this._subject = e.GetSubject();
		this._action = e.GetAction();
		this._target = e.GetTarget();
		
		if(this._subject != null)
		{
			_subjectDetails = KnowledgeBase.GetInstance().GetObjectDetails(this._subject);
		}
		
		if(this._target != null)
		{
			_targetDetails = KnowledgeBase.GetInstance().GetObjectDetails(this._target);
		}
		
		if(e.GetParameters() != null)
		{
			this._parameters = new ArrayList(e.GetParameters());
		}
		
		this._emotion = new BaseEmotion(EmotionType.NEUTRAL,0,null,null);
		
		this._evaluation = new ArrayList();
	}
	
	public String getSubject()
	{
		return this._subject;
	}
	
	public String getAction()
	{
		return this._action;
	}
	
	public String getTarget()
	{
		return this._target;
	}
	
	public ArrayList getParameters()
	{
		return this._parameters;
	}
	
	public int getID()
	{
		return this._id;
	}
	
	
	public Object getSubjectDetails(String property)
	{
		KnowledgeSlot aux;
		if(this._subjectDetails != null)
		{
			aux = this._subjectDetails.get(property);
			if(aux != null)
			{
				return aux.getValue();
			}
		}
		return null;
	}
	
	public Object getTargetDetails(String property)
	{
		KnowledgeSlot aux;
		if(this._targetDetails != null)
		{
			aux = this._targetDetails.get(property);
			if(aux != null)
			{
				return aux.getValue();
			}
		}
		return null;
	}
	
	public BaseEmotion getEmotion()
	{
		return this._emotion;
	}
	
	public ArrayList getEvaluation()
	{
		return this._evaluation;
	}
	
//	TODO em revisao 15.03.2007
	public boolean UpdateEmotionValues(ActiveEmotion em)
	{
		boolean updated = false;
		if(em.GetIntensity() > this._emotion.GetPotential())
		{
			this._emotion = new BaseEmotion(em.GetType(),em.GetIntensity(),em.GetCause(),em.GetDirection());
			updated = true;
		}
		
		switch(em.GetType())
		{
			case EmotionType.ADMIRATION:
			{
				String aux = LikeRelation.getRelation(AutobiographicalMemory.GetInstance().getSelf(),em.GetDirection().toString()).increment(em.GetIntensity());
				RespectRelation.getRelation(AutobiographicalMemory.GetInstance().getSelf(),em.GetDirection().toString()).increment(em.GetIntensity());
				this._evaluation.add(aux);
				break;
			}
			case EmotionType.REPROACH:
			{
				String aux = LikeRelation.getRelation(AutobiographicalMemory.GetInstance().getSelf(),em.GetDirection().toString()).decrement(em.GetIntensity());
				RespectRelation.getRelation(AutobiographicalMemory.GetInstance().getSelf(),em.GetDirection().toString()).decrement(em.GetIntensity());
				this._evaluation.add(aux);
				break;
			}
			case EmotionType.HAPPYFOR:
			{
				String aux = LikeRelation.getRelation(AutobiographicalMemory.GetInstance().getSelf(),em.GetDirection().toString()).increment(em.GetIntensity());
				this._evaluation.add(aux);
				break;
			}
			case EmotionType.GLOATING:
			{
				String aux = LikeRelation.getRelation(AutobiographicalMemory.GetInstance().getSelf(),em.GetDirection().toString()).decrement(em.GetIntensity());
				this._evaluation.add(aux);
				break;
			}
			case EmotionType.PITTY:
			{
				String aux = LikeRelation.getRelation(AutobiographicalMemory.GetInstance().getSelf(),em.GetDirection().toString()).increment(em.GetIntensity());
				this._evaluation.add(aux);
				break;
			}
			case EmotionType.RESENTMENT:
			{
				String aux = LikeRelation.getRelation(AutobiographicalMemory.GetInstance().getSelf(),em.GetDirection().toString()).decrement(em.GetIntensity());
				this._evaluation.add(aux);
				break;
			}
			
			case EmotionType.JOY:
			{
				if(_target != null && _target.equals(AutobiographicalMemory.GetInstance().getSelf()))
				{
					String aux = LikeRelation.getRelation(AutobiographicalMemory.GetInstance().getSelf(),_subject).increment(em.GetIntensity());
					this._evaluation.add(aux);
				}
				break;
			}
			case EmotionType.DISTRESS:
			{
				if(_target != null && _target.equals(AutobiographicalMemory.GetInstance().getSelf()))
				{
					String aux = LikeRelation.getRelation(AutobiographicalMemory.GetInstance().getSelf(),_subject).decrement(em.GetIntensity());
					this._evaluation.add(aux);
				}
				break;
			}
		}
		
		return updated;
	}
	
	public boolean ReferencesEvent(Event e)
	{
		if(this._subject != null) {
			if(!this._subject.equals(e.GetSubject()))
			{
				return false;
			}
		}
		if(this._action != null)
		{
			if(!this._action.equals(e.GetAction()))
			{
				return false;
			}
		}
		if(this._target != null)
		{
			if(!this._target.equals(e.GetTarget()))
			{
				return false;
			}
		}
		if(this._parameters != null)
		{
			if(e.GetParameters() != null)
			{
				if(!this._parameters.toString().equals(e.GetParameters().toString()))
				{
					return false;
				}
			}
			else return false;
		}
		else if(e.GetParameters() != null)
		{
			return false;
		}
		return true;
	}
	
	public boolean verifiesKey(SearchKey key)
	{
		if(key.getField() == SearchKey.ACTION) 
		{
			return key.getKey().equals(this._action);
		}
		else if(key.getField() == SearchKey.SUBJECT)
		{
			return key.getKey().equals(this._subject);
		}
		else if(key.getField() == SearchKey.TARGET)
		{
			return key.getKey().equals(this._target);
		}
		else if(key.getField() == SearchKey.PARAMETERS)
		{
			ArrayList params = (ArrayList) key.getKey();
			String aux;
			Parameter p;
			if(this._parameters.size() < params.size())
			{
				return false;
			}
			for(int i=0; i < params.size(); i++)
			{
				aux = (String) params.get(i);
				p = (Parameter) this._parameters.get(i);
				if(!aux.equals("*") && !aux.equals(p.GetValue()))
				{
					return false;
				}
			}
			return true;
		}
		else return false;
	}
	
	public boolean verifiesKeys(ArrayList keys)
	{
		ListIterator li = keys.listIterator();
		while(li.hasNext())
		{
			if(!this.verifiesKey((SearchKey)li.next()))
			{
				return false;
			}
		}
		return true;
	}

	public boolean equals(Object o)
	{
		ActionDetail action;
		
		if(!(o instanceof ActionDetail))
		{
			return false;
		}
		
		action = (ActionDetail) o;
		
		if(this._subject != null) {
			if(!this._subject.equals(action._subject))
			{
				return false;
			}
		}
		if(this._action != null)
		{
			if(!this._action.equals(action._action))
			{
				return false;
			}
		}
		if(this._target != null)
		{
			if(!this._target.equals(action._target))
			{
				return false;
			}
		}
		if(this._parameters != null)
		{
			if(action._parameters != null)
			{
				if(!this._parameters.toString().equals(action._parameters.toString()))
				{
					return false;
				}
			}
			else return false;
		}
		else if(action._parameters != null)
		{
			return false;
		}
		
		return true;
	}
	
	public String toXML()
	{
		String action = "<Event>";
		action += "<Emotion>" + EmotionType.GetName(this.getEmotion().GetType()) + " " + this.getEmotion().GetPotential() + "</Emotion>";
		action += "<Subject>" + this.getSubject() + "</Subject>";
		action += "<Action>" + this.getAction() + "</Action>";
		action += "<Target>" + this.getTarget() + "</Target>";
		action += "<Parameters>" + this.getParameters() + "</Parameters>";
		
		action += "</Event>\n";
		
		return action;
	}
}
