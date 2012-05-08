/**
 * PropertyNotEqual.java - Class that represents a specific property test, in this case checks
 * if two properties are different
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
 * João Dias: 16/05/2006 - Minor Change in CheckCondition method, now it works even when
 * 						   the values compared are null
 * João Dias: 17/05/2006 - Added clone() method
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
 * João Dias: 03/10/2006 - The test used in PropertyNotEqual conditions was wrong. The test performed is 
 * 						   completely changed and it works like this: if the second part of a PropertyNotEqual
 * 						   (value) is not grounded, it is not possible to determine the possible range of 
 * 						   substitutions that will make the condition true, so we just return false in those
 * 						   situations (like in other inequality conditions, ex: false RecentEventConditions).
 * 						   If both first part and second part of the != condition are grounded, we just compare
 * 						   the value of both. Finally, when the first part of the condition is not grounded, we
 * 					       get all possible bindings for the first part (according to the KB) and select the ones
 * 						   that make the first part's value different from the second part's value.
 * João Dias: 04/10/2006 - We need to have an additional method that searches for ValidBindings for NotEqual
 * 					       conditions. The reason for this is that while goal Activation needs that this method
 * 						   returns each possible substitution that makes the condition true, we cannot use the
 * 						   same method in planning because it will explode the number of plans. In planning, we
 * 						   we're not interessed in the possible values that will make the NotEqual condition true,
 * 						   we just want the ones that will make it false (and thus add an inequality to the plan,
 * 						   intead of creating n distinct plans). This functionally was actually implemented in the
 * 						   previous version of the method GetValidBindings() (before 03/10/2006), but it had problems
 * 						   when it was being called in goal activation conditions. Therefore we've added the method
 * 						   again with the name GetValidInequalities(). This method should be called by the planner
 * 						   to test the NotEqualCondition.
 */

package FAtiMA.conditions;

import java.util.ArrayList;
import java.util.ListIterator;

import FAtiMA.knowledgeBase.KnowledgeBase;
import FAtiMA.wellFormedNames.Inequality;
import FAtiMA.wellFormedNames.Name;
import FAtiMA.wellFormedNames.Substitution;
import FAtiMA.wellFormedNames.SubstitutionSet;


/**
 * Test that compares if a property is different from a given value
 * 
 * @author João Dias
 */
public class PropertyNotEqual extends PropertyCondition {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Creates a new PropertyTest of Type NotEqual
     * 
     * @param name -
     *            the PropertyTest's name
     * @param value -
     *            the PropertyTest's value
     */
    public PropertyNotEqual(Name name, Name value) {
		super(name, value);
	}
	
	/**
     * Checks if the Property Condition is verified in the agent's Memory (KB + AM)
     * @return true if the condition is verified, false otherwise
     */
	public boolean CheckCondition() {
		Object propertyValue;
		Object value;

		if (!super.CheckCondition())
			return false;
		KnowledgeBase kb = KnowledgeBase.GetInstance();
		propertyValue = this._name.evaluate(kb);
		value = this._value.evaluate(kb);

		if (propertyValue == null || value == null) 
		{
		    //if at least one of them is null, we consider that:
		    //if both are null - they are equal
		    //if only one is null - they are different and thus the condition is true
		    return propertyValue != value;
		}
		    
		return !propertyValue.equals(value);
	}

	/**
	 * This method finds all the possible sets of Substitutions that applied to the condition
     * will make it valid (true) according to the current Memory
     * @return A list with all SubstitutionsSets that make the condition valid
	 */
	public ArrayList GetValidBindings() {
		ArrayList validSubstitutionSets = new ArrayList();
		ArrayList bindingSets;
		SubstitutionSet subSet;
		Condition cond;
		
		//if the value part of a PropertyNotEqual is not grounded, we cannot
		//determine the possible range of substitutions for it, so we assume
		//that this property test returns false in those situations
		if(!_value.isGrounded())
		{
			return null;
		}
		if (_name.isGrounded()) {
			//if the name is ground, both name and value are grounded and we
			//just need to call the checkcondition function
			if(CheckCondition())
			{
				validSubstitutionSets.add(new SubstitutionSet());
				return validSubstitutionSets;
			}
			else return null;
		}
		
		//if the name is not grounded we try to get all possible bindings for it
		bindingSets = KnowledgeBase.GetInstance().GetPossibleBindings(_name);
		if (bindingSets == null)
			return null;

		for(ListIterator li = bindingSets.listIterator(); li.hasNext();)
		{
			subSet = (SubstitutionSet) li.next();
			cond = (Condition) this.clone();
			cond.MakeGround(subSet.GetSubstitutions());
			if(cond.CheckCondition())
			{
				validSubstitutionSets.add(subSet);
			}
		}
		
		if(validSubstitutionSets.size() == 0)
		{
			return null;
		}
		else return validSubstitutionSets;
	}
	
	/**
	 * This method finds all the possible sets of Inequalities that 
     * that result from the condition. In order for the method to work
     * properly, at least one part of the NotEqual Condition must be 
     * grounded. i.e, we cannot determine inequalities between [X] != [Y]
     * 
     * @return A list with all SubstitutionSets (with inequalities inside)
     * that if they are verified, the NotEqualCondition is also verified
	 */
	public ArrayList GetValidInequalities() {
		ArrayList validSubstitutionSets = new ArrayList();
		ListIterator li;
		ArrayList bindings;
		SubstitutionSet subSet;

		
		//in order to work, at least one part of the NotEqual Condition 
		//must be grounded. i.e, we cannot determine inequalities between
		// [X] != [Y]
		if (_name.isGrounded()) {
			bindings = this.GetBindings(_name,_value);
			if (bindings == null)
				return null;
		}
		else if(_value.isGrounded()) {
			bindings = this.GetBindings(_value,_name);
			if (bindings == null)
				return null;
		}
		else return null;
		
		subSet = new SubstitutionSet();
		
		li = bindings.listIterator();
		while (li.hasNext()) {
			subSet.AddSubstitution(new Inequality((Substitution)li.next()));
		}
		validSubstitutionSets.add(subSet);
		return validSubstitutionSets;
	}
	
	
	protected ArrayList GetValueBindings()
	{
		//this method should never be called
		return null;
	}
	
	/**
	 * Clones this PropertyTest, returning an equal copy.
	 * If this clone is changed afterwards, the original object remains the same.
	 * @return The PropertyTest's copy.
	 */
	public Object clone()
	{
	    return new PropertyNotEqual((Name) this._name.clone(), (Name) this._value.clone());
	}

	/**
	 * Prints the PropertyTest to the Standard Output
	 */
	public void Print() {
		super.Print();
		System.out.println(" Operator: NotEqual");
	}

	/**
     * Converts the PropertyTest to a String
     * @return the Converted String
     */
	public String toString() {
		return _name + " != " + _value;
	}
}