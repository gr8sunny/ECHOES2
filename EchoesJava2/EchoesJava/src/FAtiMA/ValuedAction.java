/** 
 * ValuedAction.java - Action that has associated with it an emotion. This emotion
 * was responsible in part for the selection of the action, and can be used to 
 * select between conflicting actions. The action supported by the strongest emotion wins.
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
 * João Dias: 25/05/2006 - Added comments to each public method's header
 * João Dias: 17/07/2006 - Instead of storing an instance of an emotion ,
 * 						   the class now stores only the hashkey of such emotion
 * 						   in order to make the class easily serializable
 * João Dias: 24/07/2006 - Now the class does not store the emotional intensity value separately,
 * 						   it only stores the emotion associated to an action. This emotion
 * 						   has the intensity stored. Added a new Constructor
 */

package FAtiMA;

import java.io.Serializable;

import FAtiMA.emotionalState.ActiveEmotion;
import FAtiMA.emotionalState.EmotionalState;
import FAtiMA.wellFormedNames.Name;


/**
 * Action that has associated with it an emotion. This emotion
 * was responsible in part for the selection of the action, and can be used to 
 * select between conflicting actions. The action supported by the strongest 
 * emotion wins.
 * 
 * @author João Dias
 */
public class ValuedAction implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Name _action;
	private String _emotionKey;
	private float _value;

	/**
	 * Creates a new ValuedAction
	 * @param action - the action
	 * @param emotion - the emotion associated to the action
	 */
	public ValuedAction(Name action, ActiveEmotion emotion) {
		_action = action;
		if(emotion != null) 
		{
			_emotionKey = emotion.GetHashKey();
		}
		else _emotionKey = null;
		_value = -1;
	}
	
	public ValuedAction(Name action, float value)
	{
		_action = action;
		_emotionKey = null;
		this._value = value;
	}

	/**
	 * Gets the Action's name
	 * @return the action to perform
	 */
	public Name GetAction() {
		return _action;
	}
	/**
	 * Gets the Emotion that supports this action
	 * @return the Emotion associated with the action
	 */
	public ActiveEmotion getEmotion() {
		if(_emotionKey == null) return null;
		return EmotionalState.GetInstance().GetEmotion(_emotionKey);
	}

	/**
	 * Gets an emotional value associated with the action
	 * @return a float representing how emotionally important is the action
	 */
	public float GetValue() {
		if(_value == -1)
		{
			ActiveEmotion aux = getEmotion();
			if(aux != null) return aux.GetIntensity();
			else return 0;
		}
		return _value;
	}

	/**
	 * Sets the Emotion associated with the action
	 * @param emotion - the new ActiveEmotion to associate to the action
	 */
	public void setEmotion(ActiveEmotion emotion) {
		if(emotion != null) _emotionKey = emotion.GetHashKey();
	}
	
	/**
	 * Converts the ValuedAction to a String
	 * @return the converted String
	 */
	public String toString() {
		return "Action: " + _action + " Value: " + GetValue();
	}
}