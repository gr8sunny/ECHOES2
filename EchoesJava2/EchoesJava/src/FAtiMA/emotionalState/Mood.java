/** 
 * Mood.java - Represents a character's mood
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
 * João Dias: 20/01/2004 - File created
 * João Dias: 02/07/2006 - Replaced System's timer by an internal agent simulation timer
 * João Dias: 10/07/2006 - the class is now serializable 
 */

package FAtiMA.emotionalState;

import java.io.Serializable;

import FAtiMA.AgentSimulationTime;
import FAtiMA.util.enumerables.EmotionValence;


/**
 * Class that represents a character's mood.
 */
public class Mood implements Serializable {

	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private float _intensityATt0; 
	private long _t0;
	private float _intensity;
	 
	 /**
	  * Creates a new neutral Mood
	  */
	 public Mood()
	 {
	 	//the mood allways starts as neutral
	 	this._intensityATt0 = 0;
	 	this._intensity = 0;
	 	this._t0 = 0;
	 }
	 
	 /**
	  * Gets a float value that represents mood. Mood is ranged 
	  * between [-10;10], a negative value represents a bad mood,
	  * a positive value represents good mood and values near 0
	  * represent a neutral mood
	  * @return a float value representing mood ranged in [-10;10]
	  */
	 public float GetMoodValue()
	 {
	 	return this._intensity;
	 }
	 
	 /**
	  * Decays the mood according to the system's timer.
	  * @return the mood's intensity after being decayed
	  */
	 public float DecayMood()
	 {
	 	if(_intensityATt0 == 0)
	 	{
	 		_intensity = 0;
	 		return 0;
	 	}
	 	
	 	long deltaT;
		deltaT = (AgentSimulationTime.GetInstance().Time() - _t0)/1000;
		_intensity = _intensityATt0 * ((float) Math.exp(- EmotionalPameters.MoodDecayFactor * deltaT));
		
		if(Math.abs(_intensity) < EmotionalPameters.MinimumMoodValue)
		{
			_intensityATt0 = 0;
			_t0 = AgentSimulationTime.GetInstance().Time();
			_intensity = 0;
		}
		return _intensity;
	 }
	 
	 /**
	  * Updates the character's mood when a given emotion is "felt" by
	  * the character. 
	  * @param em - the ActiveEmotion that will influence the character's
	  * 		    current mood
	  */
	 public void UpdateMood(ActiveEmotion em)
	 {
	 	float newMood;
	 	
	 	if(em.GetValence() == EmotionValence.POSITIVE) {
		    newMood = _intensity + (em.GetIntensity() * EmotionalPameters.EmotionInfluenceOnMood);
		    //mood is limited between -10 and 10
		    newMood = Math.min(newMood, 10);
		}
		else 
		{
		    newMood = _intensity - (em.GetIntensity() * EmotionalPameters.EmotionInfluenceOnMood);
		    //mood is limited between -10 and 10
			newMood = Math.max(newMood, -10);
		}
	 	
	 	_intensityATt0 = _intensity = newMood;
	 	_t0 = AgentSimulationTime.GetInstance().Time();
	 }
	 
	 /**
	  * Manually sets a new value for Mood. Use it with caution and only
	  * when you explicitly want to change a character's mood without anything
	  * happening
	  * @param newMood - the new value of the mood. Must be ranged in [-10;10].
	  *                  Remember that -10 represents a very bad mood, 10 a very
	  * 			     good mood and values near 0 represent a neutral mood
	  */
	 public void SetMood(float newMood)
	 {
	 	float aux;
	 	
	 	if(newMood > 10)
	 	{
	 		aux = 10; 
	 	}
	 	else if(newMood < -10)
	 	{
	 		aux = -10;
	 	}
	 	else
	 	{
	 		aux = newMood;
	 	}
	 	
	 	_intensityATt0 = _intensity = aux;
	 	_t0 = AgentSimulationTime.GetInstance().Time();
	 }
	 
	 public String toXml()
	 {
		return "<Mood>" + this._intensity + "</Mood>";
	 }
}
