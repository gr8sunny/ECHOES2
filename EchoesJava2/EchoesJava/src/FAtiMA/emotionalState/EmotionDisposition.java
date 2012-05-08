/** 
 * EmotionDisposition.java - Represents the character's emotional disposition 
 * (Emotional Threshold + Decay Rate) towards an Emotion Type.
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
 * João Dias: 10/07/2006 - the class is now serializable
 * João Dias: 23/05/2006 - Added comments to each public method's header 
 */

package FAtiMA.emotionalState;

import java.io.Serializable;

/**
 * Represents the character's emotional disposition 
 * (Emotional Threshold + Decay Rate) towards an Emotion Type.
 * 
 * @author João Dias
 */
public class EmotionDisposition implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int _decay;
	private short _emotionType;
	private int _threshold;

	/**
	 * Creates a new EmotionDisposition
	 * 
	 * @param emotionType - a short value representing the type of the Emotion (ex: Fear, Hope, etc)
	 * @param threshold - the threshold for the emotion
	 * @param decay - the decay rate for the emotion
	 * 
	 * @see the enumerable EmotionType to see the possible types of Emotion
	 */
	public EmotionDisposition(short emotionType, int threshold, int decay) {
		_emotionType = emotionType;
		_threshold = threshold;
		_decay = decay;
	}

	/**
	 * Gets the decay rate for the emotion
	 * @return the decay rate
	 */
	public int GetDecay() {
		return _decay;
	}

	/**
	 * Gets the emotion's type
	 * @return a short representing the emotion type (enumerable)
	 * @see the enumerable EmotionType
	 */
	public short GetEmotionType() {
		return _emotionType;
	}

	/**
	 * gets the emotion's threshold
	 * @return the threshold
	 */
	public int GetThreshold() {
		return _threshold;
	}
	
	/**
	 * Converts the emotional disposition to a String
	 * @return the converted String
	 */
	public String toString() {
		return "Emotion: " + _emotionType + " Threshold: " + _threshold + " Decay: " + _decay;
	}
}