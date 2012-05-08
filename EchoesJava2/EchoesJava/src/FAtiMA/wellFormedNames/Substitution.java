/**
 * Substitution.java - represents a substitution of a variable for another variable or constant symbol 
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
 * Created: 11/01/2004 
 * @author: João Dias
 * Email to: joao.assis@tagus.ist.utl.pt 
 * 
 * History: 
 * João Dias: 11/01/2004 - File created
 * João Dias: 14/05/2006 - overriden equals Method
 * João Dias: 22/05/2006 - Added comments to each public method's header
 * João Dias: 10/07/2006 - the class is now serializable
 * João Dias: 29/09/2006 - Now the class constructor clones the received Symbols. This is 
 * 						   because otherwise you cannot garantee that the Symbols stored
 * 						   in the substitution will not be changed externally. This fact
 * 						   was causing abnormal behavior, as sometimes the programmer would
 * 						   change the value of a substitution without realizing it.  
 * 					
 */

package FAtiMA.wellFormedNames;

import java.io.Serializable;

/**
 * Represents a substitution of a variable for another variable or constant symbol
 * 
 * @author João Dias
 */

public class Substitution implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected final Symbol _value;

	protected final Symbol _variable;

	/**
	 * Creates a new Substitution of the type variable/value
	 * @param var - the variable to be replaced
	 * @param value - the new value to apply in the place of the old variable
	 */
	public Substitution(Symbol var, Symbol value) {
		_variable = (Symbol) var.clone();
		_value = (Symbol) value.clone();
	}

	/**
	 * Gets the substitution's value - Right side
	 * @return the right side of the substitution
	 */
	public Symbol getValue() {
		return _value;
	}

	/**
	 * Gets the substitution's variable - Left side
	 * @return the left side of the substitution
	 */
	public Symbol getVariable() {
		return _variable;
	}

	/**
	 * Converts the substitution to a String
	 * @return the converted String
	 */
	public String toString() {
		return _variable + "/" + _value;
	}
	

	/**
	 * Compares a substitution with a given object
	 * 
	 * @param obj - the object to compare
	 * @return If the object corresponds to an equal substitution returns true, false
	 * otherwise
	 */
    public boolean equals(Object obj)
    {
        if(obj == null) return false;
        if(!(obj instanceof Substitution)) return false;
        
        Substitution aux = (Substitution) obj;

        return (this._value.equals(aux._value) &&
            this._variable.equals(aux._variable));
    }
}