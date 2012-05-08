/** 
 * PredicateCondition.java - Class that Represents a predicate condition, used to represent preconditions,
 * success conditions, action effects, etc.
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
 * João Dias: 17/05/2004 - Added clone() method
 * João Dias: 22/05/2006 - Added comments to each public method's header
 * João Dias: 10/07/2006 - the class is now serializable 
 * João Dias: 12/07/2006 - Removed the Reference to a KB stored in conditions. It didn't 
 * 						   make much sense and it causes lots of problems with serialization
 * 						   Because of this, there are additional changes in some of the methods
 * 						   that need to receive a reference to the KB
 * João Dias: 12/07/2006 - Changes in groundable methods, the class now implements
 * 						   the IGroundable Interface, the old ground methods are
 * 					       deprecated
 * João Dias: 31/08/2006 - Class renamed from Predicate to PredicateCondition (Refactor)
 * 						 - the field _positive is now protected instead of private (it can be inherited)
 * 						 - Added an empty constructor
 * 						 - Important conceptual change: Since we have now two types of memory,
 * 						   the KnowledgeBase (Semantic memory) and Autobiographical memory (episodic memory),
 * 						   and we have RecentEvent and PastEvent conditions that are searched in episodic
 * 						   memory (and the old conditions that are searched in the KB), it does not make 
 * 						   sense anymore to receive a reference to the KB in searching methods 
 * 						   (checkCondition, getValidBindings, etc) for Conditions. Since both the KB 
 * 						   and AutobiographicalMemory are singletons that can be accessed from any part of 
 * 						   the code, these methods do not need to receive any argument. It's up to each type
 * 						   of condition to decide which memory to use when searching for information.
 */

package FAtiMA.conditions;

import java.util.ArrayList;

import org.xml.sax.Attributes;

import FAtiMA.knowledgeBase.KnowledgeBase;
import FAtiMA.wellFormedNames.Name;
import FAtiMA.wellFormedNames.Substitution;
import FAtiMA.wellFormedNames.Symbol;


/**
 * Represents a test to a predicate. Used to represent preconditions, success conditions, etc
 * 
 * @author João Dias
 */

public class PredicateCondition extends Condition {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Parses a Predicate given a XML attribute list
	 * @param attributes - A list of XMl attributes
	 * @return - the Predicate Parsed
	 */
    public static PredicateCondition ParsePredicate(Attributes attributes) {
		String aux;
		Name name;
		boolean positive = true;
		aux = attributes.getValue("name");
		if(aux.charAt(0) == '!') {
			aux = aux.substring(1);
			positive = false;
		}
		name = Name.ParseName(aux);
		
		return new PredicateCondition(positive,name);
	}
			
	protected boolean _positive;
	
	
	protected PredicateCondition()
	{
	}
		
	/**
	 * Creates a new Test to a Predicate
	 * @param positive - Indicates if the Predicate is positive or negative
	 * @param name - the predicate's name
	 */
	public PredicateCondition(boolean positive, Name name) {
		super(name);
		_positive = positive;
	}
	
	/**
	 * Checks if the Predicate is verified in the agent's KnowledgeBase
	 * @return true if the Predicate is verified, false otherwise
	 * @see KnowledgeBase
	 */
	public boolean CheckCondition() {
		boolean result;
		if(!_name.isGrounded()) return false;
		result = KnowledgeBase.GetInstance().AskPredicate(_name); 
		return _positive == result;
	}
	
	/**
	 * Gets the predicates's value - the object compared against the condition's name
	 * @return the predicates's value
	 */
	public Name GetValue() {
		if(_positive) return new Symbol("True");
		else return new Symbol("False");
	}
	
	 /**
     * @deprecated use ReplaceUnboundVariables(int) instead.
	 * Replaces all unbound variables in the object by applying a numeric
	 * identifier to each one.
	 * Example: the variable [X] becomes [X4] if the received ID is 4.
	 * @param variableID - the identifier to be applied
	 * @return a new Condition with the variables changed 
	 */
	public Object GenerateName(int id) {
		PredicateCondition aux = (PredicateCondition) this.clone();
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
    }
	
    /**
     * @deprecated use the method MakeGround(ArrayList) instead
	 * Applies a set of substitutions to the object, grounding it.
	 * Example: Applying the substitution "[X]/John" in the name "Weak([X])" returns
	 * "Weak(John)".
	 * @param bindings - A list of substitutions of the type "[Variable]/value"
	 * @return a new Predicate with the substitutions applied
	 * @see Substitution
	 */
	public Object Ground(ArrayList bindings) {
		PredicateCondition aux = (PredicateCondition) this.clone();
		aux.MakeGround(bindings);
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
    }
	
    /**
     * @deprecated use the method MakeGround(Substitution) instead
	 * Applies a substitution to the object, grounding it.
	 * Example: Applying the substitution "[X]/John" in the name "Weak([X])" returns
	 * "Weak(John)".
	 * @param subst - a substitution of the type "[Variable]/value"
	 * @return a new Predicate with the substitution applied
	 * @see Substitution
	 */
	public Object Ground(Substitution subst) {
		PredicateCondition aux = (PredicateCondition) this.clone();
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
    }
	
	/**
	 * Indicates if the Predicate is grounded (no unbound variables in it's WFN)
	 * Example: Stronger(Luke,John) is grounded while Stronger(John,[X]) is not.
	 * @return true if the Predicate is grounded, false otherwise
	 */
	public boolean isGrounded() {
		return _name.isGrounded();
	}
	
	/**
	 * Indicates if the Predicate is positive or negative.
     * A negative predicate corresponds to the negation of the original predicate
	 * @return True if the Predicate is positive, false otherwise.
	 */
	public boolean isPositive() {
		return _positive;
	}
	
	/**
	 * Converts the Predicate to a String
	 * @return the converted String
	 */
	public String toString() {
		if (_positive) return _name.toString();
		else return "!" + _name;
	}
	
	/**
	 * Clones this Predicate, returning an equal copy.
	 * If this clone is changed afterwards, the original object remains the same.
	 * @return The Predicates's copy.
	 */
	public Object clone()
	{
	    return new PredicateCondition(this._positive,(Name) this._name.clone());
	}
	
	/**
	 * Find a set of Substitutions for the second part of the Predicate, which will 
	 * make it become true. With this method it is possible to test conditions that
	 * have unbound variables in the second part such as: 
     * "Owner(Ball) = [x]" 
     * this condition will be true if there is anyone in the world that owns a Ball.
     * If John owns the ball, the method returns [x]/John
     * @return returns all set of Substitutions that make the condition valid.
	 */
	protected ArrayList GetValueBindings() {
		if(CheckCondition()) {
			return new ArrayList();
		}
		else return null;
	}
}
