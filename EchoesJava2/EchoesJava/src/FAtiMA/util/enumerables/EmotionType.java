/** 
 * EmotionType.java - Class that implements the Enumerable for OCC's 22 emotion types
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
 */

package FAtiMA.util.enumerables;

import FAtiMA.exceptions.InvalidEmotionTypeException;

/**
 * Enumerable for the 22 OCC's emotion types
 * 
 * @author João Dias
 */
public abstract class EmotionType {

	public static final short NEUTRAL = -1;
    public static final short JOY = 0;
	public static final short DISTRESS = 1;
	public static final short LOVE = 2;
	public static final short HATE = 3;
	public static final short HAPPYFOR = 4;
	public static final short RESENTMENT = 5;
	public static final short GLOATING = 6;
	public static final short PITTY = 7;
	public static final short PRIDE = 8;
	public static final short SHAME = 9;
	public static final short ADMIRATION = 10;
	public static final short REPROACH = 11;
	public static final short HOPE = 12;
	public static final short FEAR = 13;
	public static final short SATISFACTION = 14;
	public static final short DISAPPOINTMENT = 15;
	public static final short RELIEF = 16;
	public static final short FEARSCONFIRMED = 17;
	public static final short GRATIFICATION = 18;
	public static final short REGRET =19;
	public static final short GRATITUDE = 20;
	public static final short ANGER = 21;
	
	private static final String[] _emotionTypes = {"Joy",
												   "Distress",
												   "Love",
												   "Hate",
												   "Happy-For",
												   "Resentment",
												   "Gloating",
												   "Pitty",
												   "Pride",
												   "Shame",
												   "Admiration",
												   "Reproach",
												   "Hope",
												   "Fear",
												   "Satisfaction",
												   "Disappointment",
												   "Relief",
												   "Fears-Confirmed",
												   "Gratification",
												   "Remorse",
												   "Gratitude",
												   "Anger"};
	
	/**
	 * Parses a string that corresponds to the emotion type and returns the appropriate
	 * emotion type (enumerable)
	 * @param emotionType - the name of the emotion to search for
	 * @return - the id of the emotion type
	 * @throws InvalidEmotionTypeException - if the received string does not correspond
	 * 		   to a valid emotion type 
	 */
	public static short ParseType(String emotionType) throws InvalidEmotionTypeException {
		short i;
		if(emotionType == null) throw new InvalidEmotionTypeException(null);
		
		for(i=0; i < _emotionTypes.length; i ++) {
			if(_emotionTypes[i].equals(emotionType)) return i;
		}
		
		throw new InvalidEmotionTypeException(emotionType);
	}
	
	/**
	 * Gets the emotion's name, given its identifier
	 * @param the id of the emotionType
	 * @return the name of the emotionType
	 */
	public static String GetName(short emotionType) {
	    if(emotionType == - 1) return "Neutral";
		if(emotionType >= 0 && emotionType <=21) return _emotionTypes[emotionType];
		return null;
	}
}