/** 
 * UnknownGoalException.java - Exception thrown when the personality file references 
 * a goal not specified in the GoalLibrary
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

public class UnknownGoalException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UnknownGoalException(String goalName) {
        super("Error: Unknown goal " + goalName + " in character xml file");
   }

   /**
    * @param msg message
    * @param ex wrapped error/exception
    */
   public UnknownGoalException(String msg, Throwable ex) {
         super(msg, ex);
   }
}
