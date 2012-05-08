/** 
 * InvalidEmotionTypeException.java - Exception thrown when an invalid EmotionType 
 * is parsed in the enumerable Class of EmotionTypes
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
 * Created: 15/01/2005 
 * @author: João Dias
 * Email to: joao.assis@tagus.ist.utl.pt
 * 
 * History: 
 * João Dias: 15/01/2005 - File created
 */

package FAtiMA.exceptions;

public class InvalidEmotionTypeException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidEmotionTypeException(String emotion) {
        super("ERROR: Invalid emotion type " + emotion);
    }
    
    public InvalidEmotionTypeException(int num) {
        super("ERROR: invalid emotion type indentifier " + num);
    }
}
