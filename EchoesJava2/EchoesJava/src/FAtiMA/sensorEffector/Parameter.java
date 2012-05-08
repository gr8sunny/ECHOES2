/** 
 * Parameter.java - Represents a name-value parameter
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
 * João Dias: 23/05/2006 - Added comments to each public method's header
 * João Dias: 10/07/2006 - the class is now serializable
 * João Dias: 21/07/2006 - added the clone method 
 * João Dias: 25/08/2006 - changed the implementation of the toString method
 * 						   now it only shows the value of the parameter and not the 
 * 						   name
 */

package FAtiMA.sensorEffector;

import java.io.Serializable;

/**
 * Represents a name-value parameter
 * @author João Dias
 */
public class Parameter implements Cloneable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String _parameterName;
	private Object _value;

	/**
	 * Creates a new Parameter
	 * @param parameterName - the name of the parameter
	 * @param value - the value of the parameter
	 */
	public Parameter(String parameterName, Object value) {
		_parameterName = parameterName;
		_value = value;
	}

	/**
	 * Gets the Parameter's name
	 * @return the parameter's name
	 */
	public String GetName() {
		return _parameterName;
	}

	/**
	 * Gets the Parameter's value
	 * @return - the parameter's value
	 */
	public Object GetValue() {
		return _value;
	}
	
	/**
	 * Converts the Parameter to a String
	 * @return the converted String
	 */
	public String toString() {
	    return  _value.toString();
	}
	
	/**
	 * Creates a new copy of the Parameter
	 * @return a new Parameter equal to this one
	 */
	public Object clone()
	{
		return new Parameter(this._parameterName,this._value);
	}
}