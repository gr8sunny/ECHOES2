/**
 * PropertyLesserEqual.java - Class that represents a specific property test, in this case checks
 * if one property is smaller or equal than another value (only works with numeric properties)
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
 * Created: 12/02/2007 
 * @author: João Dias
 * Email to: joao.assis@tagus.ist.utl.pt
 * 
 * History: 
 * João Dias: 12/02/2007 - File created
 */

package FAtiMA.conditions;

import FAtiMA.knowledgeBase.KnowledgeBase;
import FAtiMA.wellFormedNames.Name;

/**
 * Test that compares if a property is smaller than a given value. Only works with numeric values.
 * 
 * @author João Dias
 */
public class PropertyLesserEqual extends PropertyCondition {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Creates a new PropertyTest of Type LesserEqual
     * 
     * @param kb -
     *            a reference to the KnowledgeBase
     * @param name -
     *            the PropertyTest's name
     * @param value -
     *            the PropertyTest's value
     */
	public PropertyLesserEqual(Name name, Name value) {
		super(name, value);
	}

	/**
     * Checks if the Property Condition is verified in the agent's Memory (KB + AM)
     * @return true if the condition is verified, false otherwise
     */
	public boolean CheckCondition() {
		Object propertyValue;
		Object value;
		Float aux;
		Float aux2;

		if (!super.CheckCondition())
			return false;
		KnowledgeBase kb = KnowledgeBase.GetInstance();
		propertyValue = this._name.evaluate(kb);
		value = this._value.evaluate(kb);

		if (propertyValue == null || value == null || propertyValue =="null" || value == "null")
			return false;
		aux = new Float(propertyValue.toString());
		aux2 = new Float(value.toString());
		return aux.floatValue() <= aux2.floatValue();
	}

	/**
	 * Clones this PropertyTest, returning an equal copy.
	 * If this clone is changed afterwards, the original object remains the same.
	 * @return The PropertyTest's copy.
	 */
	public Object clone()
	{
	    return new PropertyLesserEqual((Name) this._name.clone(), (Name) this._value.clone());
	}

	/**
	 * Prints the PropertyTest to the Standard Output
	 */
	public void Print() {
		super.Print();
		System.out.println(" Operator: LesserEqual");
	}

	/**
     * Converts the PropertyTest to a String
     * @return the Converted String
     */
	public String toString() {
		return _name + " <= " + _value;
	}
}