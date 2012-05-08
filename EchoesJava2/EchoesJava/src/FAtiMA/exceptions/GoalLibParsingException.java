/** 
 * GoalLibParsingException.java - Exception thrown when there is an error parsing the
 * Goal Library file
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
 * Created: 12/12/2004 
 * @author: João Dias
 * Email to: joao.assis@tagus.ist.utl.pt
 * 
 * History: 
 * João Dias: 12/12/2004 - File created
 */

package FAtiMA.exceptions;

public class GoalLibParsingException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GoalLibParsingException() {
        super("Error parsing the Goal Library xml file");
   }
   
   /**
    * Construct an exception passing a message back 
    * @param msg message
    */
   public GoalLibParsingException(String msg) {
         super(msg);
   }

   /**
    * @param msg message
    * @param ex wrapped error/exception
    */
   public GoalLibParsingException(String msg, Throwable ex) {
         super(msg, ex);
   }

   /**
    * @param ex wrapped error/exception
    */
   public GoalLibParsingException(Throwable ex) {
         super(ex);
   }
}