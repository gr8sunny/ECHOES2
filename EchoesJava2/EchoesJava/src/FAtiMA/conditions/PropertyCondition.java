/**
 * PropertyCondition.java - Abstract class that Represents a property test, used to represent
 * preconditions that refer to properties, success conditions, action effects, etc.
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
 * João Dias: 22/05/2006 - Added comments to each public method's header
 * João Dias: 10/07/2006 - the class is now serializable 
 * João Dias: 12/07/2006 - Removed the Reference to a KB stored in conditions. It didn't 
 * 						   make much sense and it causes lots of problems with serialization
 * 						   Because of this, there are additional changes in some of the methods
 * 						   that need to receive a reference to the KB
 * João Dias: 12/07/2006 - Changes in groundable methods, the class now implements
 * 						   the IGroundable Interface, the old ground methods are
 * 					       deprecated
 * João Dias: 31/08/2006 - Class renamed from Property to PropertyCondition
 * 						 - Important conceptual change: Since we have now two types of memory,
 * 						   the KnowledgeBase (Semantic memory) and Autobiographical memory (episodic memory),
 * 						   and we have RecentEvent and PastEvent conditions that are searched in episodic
 * 						   memory (and the old conditions that are searched in the KB), it does not make 
 * 						   sense anymore to receive a reference to the KB in searching methods 
 * 						   (checkCondition, getValidBindings, etc) for Conditions. Since both the KB 
 * 						   and AutobiographicalMemory are singletons that can be accessed from any part of 
 * 						   the code, these methods do not need to receive any argument. It's up to each type
 * 						   of condition to decide which memory to use when searching for information.
 * João Dias: 14/10/2006 - I was wrongly assuming that the result of evaluating a property would always return
 * 						   a symbol. Altough this was true before, it stopped being when we introduced property 
 * 						   values that start with # and evaluate to the same value (constants). This assumption
 * 						   was causing property conditions with # to fail in certain situations. 
 */

package FAtiMA.conditions;

import java.util.ArrayList;

import org.xml.sax.Attributes;

import FAtiMA.knowledgeBase.KnowledgeBase;
import FAtiMA.wellFormedNames.Name;
import FAtiMA.wellFormedNames.Substitution;
import FAtiMA.wellFormedNames.Unifier;


/**
 * Represents a test to a property. Used in preconditions, success conditions, etc..
 * This property test is composed by the property name, and a second name that specifies 
 * a comparison value. A PropertyTest can be one of: PropertyEqual, PropertyNotEqual, 
 * PropertyLesser, PropertyGreater.
 * 
 * @author João Dias
 */

public abstract class PropertyCondition extends Condition {

    /**
	 * Parses a PropertyTest given a XML attribute list
	 * @param attributes - A list of XMl attributes
	 * @return - the PropertyTest Parsed
	 */
	public static PropertyCondition ParseProperty(Attributes attributes) {
		PropertyCondition cond;
		Name name;
		String op;
		Name value;

		name = Name.ParseName(attributes.getValue("name"));
		op = attributes.getValue("operator");
		value = Name.ParseName(attributes.getValue("value"));

		if (op == null || op.equals("="))
			cond = new PropertyEqual(name, value);
		else if (op.equals("!="))
			cond = new PropertyNotEqual(name, value);
		else if (op.equals("GreaterThan"))
			cond = new PropertyGreater(name, value);
		else if (op.equals("LesserThan"))
			cond = new PropertyLesser(name, value);
		else if (op.equals("GreaterEqual"))
			cond = new PropertyGreaterEqual(name, value);
		else if (op.equals("LesserEqual"))
			cond = new PropertyLesserEqual(name,value);
		else
			cond = new PropertyEqual(name, value);
			
		return cond;
	}

	protected Name _value;

	/**
	 * Creates a new Property
	 * @param name - the property's name
	 * @param value - the property's value
	 */
	public PropertyCondition(Name name, Name value) {
		super(name);
		_value = value;
	}

	/**
	 * Checks if the Property Condition is verified in the agent's memory (KB + AM)
	 * @return true if the condition is verified, false otherwise
	 */
	public boolean CheckCondition() {
		if (!_name.isGrounded() && !_value.isGrounded())
			return false;
		return true;
	}

	/**
	 * Gets the Property's test value
	 * @return the test value of the property
	 */
	public Name GetValue() {
		return _value;
	}
	
	/**
     * @deprecated use ReplaceUnboundVariables(int) instead.
	 * Replaces all unbound variables in the object by applying a numeric
	 * identifier to each one.
	 * Example: the variable [X] becomes [X4] if the received ID is 4.
	 * @param variableID - the identifier to be applied
	 * @return a new Property with the variables changed 
	 */
	public Object GenerateName(int id)
	{
		PropertyCondition aux = (PropertyCondition) this.clone();
		aux.ReplaceUnboundVariables(id);
		return aux;
	}
	
	
	/**
	 * Replaces all unbound variables in the object by applying a numeric 
     * identifier to each one. For example, the variable [x] becomes [x4]
     * if the received ID is 4. 
     * Attention, this method modifies the original object.
     * @param variableID - the identifier to be applied
	 */
    public void ReplaceUnboundVariables(int variableID) 
    {
    	this._name.ReplaceUnboundVariables(variableID);
    	this._value.ReplaceUnboundVariables(variableID);
    }
    
    /**
     * @deprecated use the method MakeGround(ArrayList) instead
	 * Applies a set of substitutions to the object, grounding it.
	 * Example: Applying the substitution "[X]/John" in the name "Weak([X])" returns
	 * "Weak(John)".
	 * @param bindings - A list of substitutions of the type "[Variable]/value"
	 * @return a new Property with the substitutions applied
	 * @see Substitution
	 */
	public Object Ground(ArrayList bindingConstraints)
	{
		PropertyCondition aux = (PropertyCondition) this.clone();
		aux.MakeGround(bindingConstraints);
		return aux;
	}

	/**
	 * Applies a set of substitutions to the object, grounding it.
	 * Example: Applying the substitution "[X]/John" in the name "Weak([X])" returns
	 * "Weak(John)". 
	 * Attention, this method modifies the original object.
	 * @param bindings - A list of substitutions of the type "[Variable]/value"
	 * @see Substitution
	 */
    public void MakeGround(ArrayList bindings)
    {
    	this._name.MakeGround(bindings);
    	this._value.MakeGround(bindings);
    }
    
   
    /**
     * @deprecated use the method MakeGround(Substitution) instead
	 * Applies a substitution to the object, grounding it.
	 * Example: Applying the substitution "[X]/John" in the name "Weak([X])" returns
	 * "Weak(John)".
	 * @param subst - a substitution of the type "[Variable]/value"
	 * @return a new Property with the substitution applied
	 * @see Substitution
	 */
	public Object Ground(Substitution subst)
	{
		PropertyCondition aux = (PropertyCondition) this.clone();
		aux.MakeGround(subst);
		return aux;
	}

	/**
	 * Applies a set of substitutions to the object, grounding it.
	 * Example: Applying the substitution "[X]/John" in the name "Weak([X])" returns
	 * "Weak(John)". 
	 * Attention, this method modifies the original object.
	 * @param subst - a substitution of the type "[Variable]/value"
	 * @see Substitution
	 */
    public void MakeGround(Substitution subst)
    {
    	this._name.MakeGround(subst);
    	this._value.MakeGround(subst);
    }

	/**
	 * Indicates if the condition is grounded (no unbound variables in it's WFN)
	 * Example: Stronger(Luke,John) is grounded while Stronger(John,[X]) is not.
	 * @return true if the condition is grounded, false otherwise
	 */
	public boolean isGrounded() {
		return (_name.isGrounded() && _value.isGrounded());
	}

	/**
	 * Prints the PropertyTest to the Standard Output
	 */
	public void Print() {
		System.out.print("    Property= " + _name + " value= " + _value);
	}
	
	protected ArrayList GetBindings(Name groundValue, Name value) {
		Object val;
		ArrayList bindings;
		if (!groundValue.isGrounded())
			return null;
		if (!value.isGrounded()) {
			val = groundValue.evaluate(KnowledgeBase.GetInstance());
			if (val != null) {
				bindings = new ArrayList();
				if(Unifier.Unify(value, Name.ParseName((String) val), bindings))
					return bindings;
				else return null;
			}
			else return null;
		}
		else if (this.CheckCondition()) {
			return new ArrayList();
		}
		else return null;
	}

	/**
	 * Find a set of Substitutions for the second part of the condition, which will 
	 * make it become true. With this method it is possible to test conditions that
	 * have unbound variables in the second part such as: 
     * "Owner(Ball) = [x]" 
     * this condition will be true if there is anyone in the world that owns a Ball.
     * If John owns the ball, the method returns [x]/John
     * @return returns all set of Substitutions that make the condition valid.
     */
	protected ArrayList GetValueBindings() {
		return GetBindings(_name, _value);
	}
}