/** 
 * Name.java - Well Formed Name, used to store propositional information in the KB,
 * and for unification.
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
 * João Dias: 17/05/2006 - Added clone() method
 * João Dias: 22/05/2006 - Added comments to each public method's header
 * João Dias: 10/07/2006 - the class is now serializable 
 * João Dias: 12/07/2006 - Added the evaluate method to names
 * João Dias: 12/07/2006 - The isConstant method is now deprecated
 * João Dias: 12/07/2006 - The class now implements the newly created IGroundable Interface
 */

package FAtiMA.wellFormedNames;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.StringTokenizer;

import FAtiMA.knowledgeBase.KnowledgeBase;


/**
 * Abstract Well Formed Name
 * A well formed name is used to specify goal/action names, objects, properties,
 * constants, and relations.
 * It's syntax is based on first order logic symbols, variables and predicates.
 * a Name can be either a Symbol or a ComposedName (composed by several symbols)
 * @see Symbol
 * @see	ComposedName
 * 
 * @author João Dias
 */

public abstract class Name implements IGroundable, Cloneable, Serializable {

    protected boolean _constant;
	protected boolean _grounded;
	
    /**
     * @see Symbol
     * @see ComposedName
     * 
     * Parses a Name from a string that corresponds to a Well Formed Name 
     * The alphabet that makes up the symbols expressions of well formed names consists of:
     *  • The set of letters, upper and lowercase.
     *  • The set of digits, 0,1,..,9
     *  • The symbols “_” and “-”
     * Symbols expressions begin with a letter and are followed by any sequence of these
     * legal characters. Well formed names are composed by four types of symbols:
     *  1. The Truth symbols "True" and "False".
     *  2. Constant symbols, which are simple symbol expressions.
     *  3. Variables symbols, which are symbol expressions enclosed in square parentheses.
     *     Ex: [x] represents the variable x.
     *  4. The Self symbol [SELF], a reserved special variable which refers to the agent.
     *     
     * a) All Symbols <seealso cref="Symbol"/> are well formed names.
     * b) If S and s1,s2,...sn are symbols, then S(s1,s2,...,sn) is called 
     *    a Composed Name which is also a well formed name.
     * The following are examples of WFNs:
     * Stronger(Luke,John)
     * John(Position)
     * Owner(Ball,[Y])
     * Ball
     * 
     * @param description - the String to be parsed
     * @return the parsed Name (can be either a Symbol or a ComposedName)
     */
    public static Name ParseName(String description) 
    {
		StringTokenizer st;
		String name;
		String literals;
		ComposedName aux;
		boolean evaluate = true;
		
		if (description == null)
			return null;
		if (description.length() == 0) return null;
		
		if (description.charAt(0) == '?') {
			description = description.substring(1);
		}
		else if(description.charAt(0) == '#')
		{
			description = description.substring(1);
			evaluate = false;
		}

		st = new StringTokenizer(description, "(");
		name = st.nextToken();
		if (st.hasMoreTokens()) 
		{
			st = new StringTokenizer(st.nextToken(), ")");
			if(st.hasMoreTokens()) {
				literals = st.nextToken();
			}
			else literals = null;
			
			aux = new ComposedName(name, literals);
			aux.SetEvaluation(evaluate);
			return aux;
		}
		else
			return new Symbol(name);
	}

    /**
     * Creates a new Abstract Name - Not used since it's an abstract class
     */
	public Name() 
	{
	}
	
	/**
	 * Compares the current Name with another Name
	 * @param o - the object(Name) to compare to
	 * @return true if the Names are syntatically equal 
	 */
	public boolean equals(Object o) 
	{
	    if (o == null) return false;
        if (!(o instanceof Name)) return false;
        return this.toString().equals(o.toString());
	}
	
	/**
	 * Gets the Name's First Symbol or Literal
	 * @return the first Symbol
	 */
	public abstract Symbol GetFirstLiteral();

	/**
	 * Generates a list with all symbols contained in the Name
	 * @return the list with the symbols
	 */
	public abstract ArrayList GetLiteralList();

	
	/**
	 * Clones this Name, returning an equal copy.
	 * If this clone is changed afterwards, the original object remains the same.
	 * @return The Name's copy.
	 */
	public abstract Object clone();
	
	/**
	 * Evaluates this Name according to the data stored in the
	 * KnowledgeBase
	 * @param kb - a reference to the KnowledgeBase
	 * @return if the name is a symbol, it returns its name, otherwise
	 * 		   it returns the value associated to the name in the KB
	 */
	public abstract Object evaluate(KnowledgeBase kb);

	/**
	 * @deprecated please do not use. This is deprecated.
	 * @return true if the name corresponds to a constant symbol,
	 *         i.e. if the name corresponds to a Symbol
	 */
	public boolean isConstant() {
		return _constant;
	}
	
	public boolean isGrounded()
	{
		return this._grounded;
	}
}
