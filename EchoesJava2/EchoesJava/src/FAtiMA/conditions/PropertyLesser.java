/**
 * PropertyLesser.java - Class that represents a specific property test, in this case checks
 * if one property is smaller than another value (only works with numeric properties)
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
 * Created: 16/01/2004 
 * @author: João Dias
 * Email to: joao.assis@tagus.ist.utl.pt
 * 
 * History: 
 * João Dias: 16/01/2004 - File created
 * João Dias: 17/05/2006 - Added clone method
 * João Dias: 22/05/2006 - Added comments to each public method's header
 * João Dias: 10/07/2006 - the class is now serializable 
 * João Dias: 12/07/2006 - Removed the Reference to a KB stored in conditions. It didn't 
 * 						   make much sense and it causes lots of problems with serialization
 * 						   Because of this, there are additional changes in some of the methods
 * 						   that need to receive a reference to the KB
 * João Dias: 12/07/2006 - Changes in groundable methods, the class now implements
 * 						   the IGroundable Interface, the old ground methods are
 * 					       deprecated
 * João Dias: 31/08/2006 - Important conceptual change: Since we have now two types of memory,
 * 						   the KnowledgeBase (Semantic memory) and Autobiographical memory (episodic memory),
 * 						   and we have RecentEvent and PastEvent conditions that are searched in episodic
 * 						   memory (and the old conditions that are searched in the KB), it does not make 
 * 						   sense anymore to receive a reference to the KB in searching methods 
 * 						   (checkCondition, getValidBindings, etc) for Conditions. Since both the KB 
 * 						   and AutobiographicalMemory are singletons that can be accessed from any part of 
 * 						   the code, these methods do not need to receive any argument. It's up to each type
 * 						   of condition to decide which memory to use when searching for information.
 * João Dias: 07/08/2006 - Small change in CheckCondition method: instead of assuming that the object stored
 * 						   in the KB is a String (sometimes it isn't and was causing an error), we convert it
 * 						   to a String (by invocating the toString() method) before building the Float.
 */

package FAtiMA.conditions;

import FAtiMA.knowledgeBase.KnowledgeBase;
import FAtiMA.wellFormedNames.Name;

/**
 * Test that compares if a property is smaller than a given value. Only works with numeric values.
 * 
 * @author João Dias
 */
public class PropertyLesser extends PropertyCondition {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Creates a new PropertyTest of Type Lesser
     * 
     * @param kb -
     *            a reference to the KnowledgeBase
     * @param name -
     *            the PropertyTest's name
     * @param value -
     *            the PropertyTest's value
     */
	public PropertyLesser(Name name, Name value) {
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
		return aux.floatValue() < aux2.floatValue();
	}

	/**
	 * Clones this PropertyTest, returning an equal copy.
	 * If this clone is changed afterwards, the original object remains the same.
	 * @return The PropertyTest's copy.
	 */
	public Object clone()
	{
	    return new PropertyLesser((Name) this._name.clone(), (Name) this._value.clone());
	}

	/**
	 * Prints the PropertyTest to the Standard Output
	 */
	public void Print() {
		super.Print();
		System.out.println(" Operator: Lesser");
	}

	/**
     * Converts the PropertyTest to a String
     * @return the Converted String
     */
	public String toString() {
		return _name + " < " + _value;
	}
}