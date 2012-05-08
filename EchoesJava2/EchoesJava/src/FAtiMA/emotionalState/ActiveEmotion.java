/** 
 * ActiveEmotion.java - Emotion with intensity that is active in the character's 
 * emotional state, i.e, the character is feeling the emotion
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
 * João Dias: 24/05/2006 - Added comments to each public method's header
 * João Dias: 02/07/2006 - Replaced System's timer by an internal agent simulation timer
 * João Dias: 26/07/2006 - from now on, the intensity for an emotion cannot be greater than 10
 * João Dias: 06/08/2007 - Added a getPotential method that overrides the BaseEmotion getPotential
 */

package FAtiMA.emotionalState;

import FAtiMA.AgentSimulationTime;
import FAtiMA.util.enumerables.EmotionType;

/**
 * Represents an Emotion with intensity that is active in the character's 
 * emotional state, i.e, the character is feeling the emotion
 * @author João Dias
 */
public class ActiveEmotion extends BaseEmotion {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private float _intensityATt0; 
	private long _t0=0;
	protected int _decay;
	protected float _intensity;	
	protected int _threshold;
	
	/**
	 * Creates a new ActiveEmotion
	 * @param potEm - the BaseEmotion that is the base for this ActiveEmotion
	 * @param potential - the potential for the intensity of the emotion
	 * @param threshold - the threshold for the specific emotion
	 * @param decay - the decay rate for the specific emotion
	 */
	public ActiveEmotion(BaseEmotion potEm, float potential, int threshold, int decay){
		super(potEm);
		_potential = potential;
		_threshold = threshold;
		_decay = decay;
		SetIntensity(potential);
	}

	/**
	 * Decays the emotion according to the system's timer
	 * @return the intensity of the emotion after being decayed
	 */
	public float DecayEmotion() {
		long deltaT;
		deltaT = (AgentSimulationTime.GetInstance().Time() - _t0)/1000;
		_intensity = _intensityATt0 * ((float) Math.exp(- EmotionalPameters.EmotionDecayFactor * _decay * deltaT));
		return _intensity;
	}
	
	/**
	 * Gets the emotion's intensity
	 * @return the intensity of the emotion
	 */
	public float GetIntensity() {
		return _intensity;
	}

	/**
	 * Reforces the intensity of the emotion by a given potential
	 * @param potential - the potential for the reinformcement of the emotion's intensity
	 */
	public void ReforceEmotion(float potential) {
		SetIntensity((float) Math.log(Math.exp(_intensity + _threshold) + Math.exp(potential)));		
	}

	/**
	 * Sets the decay rate for the emotion
	 * @param decay - the new decay rate (ranged between 1 and 10)
	 */
	public void SetDecay(int decay) {
		_decay = decay;
	}
	
	/**
	 * Sets the intensity of the emotion
	 * @param potential - the potential for the emotion's intensity
	 */
	public void SetIntensity(float potential) {
		_t0 = AgentSimulationTime.GetInstance().Time();
		_intensity = potential - _threshold;
		if(_intensity > 10)
		{
			_intensity = 10;
		}
		else if(_intensity < 0)
		{
			_intensity = 0;
		}
		_intensityATt0 = _intensity;
	}
	
	/**
	 * Gets the emotion's potential
	 * @return - the potential for the intensity of the emotion
	 */
	public float GetPotential() {
		return _intensity + _threshold;
	}

	/**
	 * Sets the threshold for the specific emotion
	 * @param threshold - the new threshold (ranged between 1 and 10)
	 */
	public void SetThreshold(int threshold) {
		_threshold = threshold;
	}

	/**
	 * Converts the ActiveEmotion to XMl
	 * @return a XML String that contains all information about the ActiveEmotion
	 */
	public String toXml() {
		return "<Emotion t0=\"" + _t0 + "\" type=\"" + EmotionType.GetName(_type) + 
				"\" valence=\"" + _valence + 
				"\" cause=\"" + _cause + "\" direction=\"" + _direction
				+ "\" intensity=\"" + _intensity + "\" />";
	}
}