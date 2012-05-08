/**
 * Inequality.java - Represents the condition that a variable cannot have a specified value 
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
 * João Dias: 17/05/2006 - Added Clone Method
 * João Dias: 22/05/2006 - Added comments to each public method's header
 * João Dias: 10/07/2006 - the class is now serializable 
 * João Dias: 12/07/2006 - Changes in groundable methods, the class now implements
 * 						   the IGroundable Interface, the old ground methods are
 * 					       deprecated
 */
package FAtiMA.wellFormedNames;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents the condition that a variable cannot have a specified value.
 * 
 * @author João Dias
 */

public class Inequality extends Substitution implements IGroundable, Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
     * Creates a new Inequality Condition of the type [x]!=4
     * @param var - the variable to be constrained
     * @param value - the value that the variable cannot have
     */
	public Inequality(Symbol var, Symbol value) {
		super(var, value);
	}
	
	/**
	 * Creates a new Inequality Condition from an existing Substitution 
	 * (by negating the Substitution)
	 * @param subs - the Substitution to be negated
	 * @see Substitution
	 */
	public Inequality(Substitution subst) {
		super(subst._variable, subst._value);
	}
	
	 /**
     * @deprecated use ReplaceUnboundVariables(int) instead.
	 * Replaces all unbound variables in the object by applying a numeric
	 * identifier to each one.
	 * Example: the variable [X] becomes [X4] if the received ID is 4.
	 * @param variableID - the identifier to be applied
	 * @return a new name with the variables changed 
	 */
    public Object GenerateName(int id)
    {
    	Inequality aux;
    	aux = (Inequality) this.clone();
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
    public void ReplaceUnboundVariables(int id)
    {
    	this._variable.ReplaceUnboundVariables(id);
    	this._value.ReplaceUnboundVariables(id);
    }
	
	/**
	 * @deprecated use MakeGround(ArrayList) together with clone method instead
	 * Applies a set of substitutions to the object, grounding it.
	 * Example: Applying the substitution "[X]/John" in the name "Weak([X])" returns
	 * "Weak(John)".
	 * @param bindings - A list of substitutions of the type "[Variable]/value"
	 * @see Substitution
	 */
	public Object Ground(ArrayList substs) {
		Inequality aux = (Inequality) this.clone();
		aux.MakeGround(substs);
		return aux;
	}
	
	public void MakeGround(ArrayList substs)
	{
		this._variable.MakeGround(substs);
		this._value.MakeGround(substs);
	}
	
	/**
     * @deprecated use the method MakeGround(Substitution) instead
	 * Applies a substitution to the object, grounding it.
	 * Example: Applying the substitution "[X]/John" in the name "Weak([X])" returns
	 * "Weak(John)".
	 * @param subst - a substitution of the type "[Variable]/value"
	 * @return a new Name with the substitution applied
	 * @see Substitution
	 */
    public Object Ground(Substitution subst)
    {
    	Inequality aux = (Inequality) this.clone();
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
    	this._variable.MakeGround(subst);
    	this._value.MakeGround(subst);
    }
    
	/**
	 * Indicates if the Inequality is grounded (no unbound variables in it's WFN)
	 * Example: 2 != 4 is grounded while [x] != 4 is not.
	 * @return true if the Inequality is grounded, false otherwise
	 */
	public boolean isGrounded() 
	{
		return _value.isGrounded() && _variable.isGrounded();
	}
	
	/**
	 * Creates a new copy, which is initially equal to this inequality.
	 * If the new inequality changes, the original object remains the same.
	 * @return the new created Inequality
	 */
	public Object clone()
    {
        return new Inequality((Symbol) this._variable.clone(), (Symbol) this._value.clone());
    }
	
	
	/**
	 * Converts the inequality to a String
	 * @return the converted String
	 */
	public String toString() {
		return _variable + "!=" + _value;
	}
}