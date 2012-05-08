/** 
 * IIntegrityTester.java - Interface that specifies that a class is testable 
 * for integrity. It must receive an integrity validator and use it to verify if 
 * all its data is valid.
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
 * João Dias: 25/05/2006 - Added comments to each public method's header
 */

package FAtiMA;

import FAtiMA.exceptions.UnknownSpeechActException;

/**
 * Interface that specifies that a class is testable 
 * for integrity. It must receive an integrity validator and use it to verify if 
 * all its data is valid.
 * 
 * @author João Dias
 */
public interface IIntegrityTester {
    
    /**
     * Checks the integrity of a particular object. For instance, it
     * checks if the object references SpeechActs not defined
     * @param val - an IntegrityValidator that the implementation
     * 				of the method should use to check the integrity 
     * @throws UnknownSpeechActException - thrown when a undefined SpeechAct
     * 									   is referenced
     * @see IntegrityValidator
     */
    public void CheckIntegrity(IntegrityValidator val) throws UnknownSpeechActException;

}
