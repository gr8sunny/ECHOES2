/** 
 * EmotionalParameters.java - Defines the values for emotional parameters
 * 							  used in the dynamics of the emotional state. ex:
 * 							  the decay of an emotion, decay of mood, influence of
 * 							  mood in emotion.
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
 */
package FAtiMA.emotionalState;

/**
 * Defines the values for emotional parameters used in the dynamics of the 
 * emotional state. ex: the decay of an emotion, decay of mood, influence of
 * mood in emotion.
 */

public abstract class EmotionalPameters {
	
	/**
	 * Constant value that defines how fast should a emotion decay over time.
	 * This value is adjusted so that the slowest decaying emotions takes
	 * aproximately 15 seconds to decay to half of the original intensity, 
	 * the fastest decaying emotions take 4 seconds, and the normal ones takes
	 * 7 seconds.
	 */
	public static final float EmotionDecayFactor = 0.02f;
	
	/**
	 * Constant value that defines how fast should mood decay over time.
	 * This value is adjusted so that mood decays 3 times slower
 	 * than the slowest decaying emotion in order to represent 
 	 * a longer persistence and duration of mood over emotions.
 	 * So, it takes aproximately 60 seconds for the mood to decay to half
 	 * of the initial value.
 	 */
	public static final float MoodDecayFactor = 0.01f;
	
	/**
	 * Defines how strong is the influence of the emotion's intensity
	 * on the character's mood. Since we don't want the mood to be very
	 * volatile, we only take into account 30% of the emotion's intensity
	 */
	public static final float EmotionInfluenceOnMood = 0.3f;
	
	/**
	 * Defines how strong is the influence of the current mood 
	 * in the intensity of the emotion. We don't want the influence
	 * of mood to be that great, so we only take into account 30% of 
	 * the mood's value
	 */
	public static final float MoodInfluenceOnEmotion = 0.3f;
	
	/**
	 * Defines the minimum absolute value that mood must have,
	 * in order to be considered for influencing emotions. At the 
	 * moment, values of mood ranged in ]-0.5;0.5[ are considered
	 * to be neutral moods that do not infuence emotions  
	 */
	public static final float MinimumMoodValue = 0.5f;

}
