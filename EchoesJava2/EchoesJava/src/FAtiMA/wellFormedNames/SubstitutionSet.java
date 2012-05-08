/** 
 * SubstitutionSet.java - Stores a list of Substitutions
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
 * Created: 24/03/2004 
 * @author: João Dias
 * Email to: joao.assis@tagus.ist.utl.pt
 * 
 * History: 
 * João Dias: 24/03/2004 - File created
 * João Dias: 15/05/2004 - Added the equals(object) Method
 * João Dias: 22/05/2006 - Added comments to each public method's header
 * João Dias: 10/07/2006 - the class is now serializable 
 * João Dias: 12/08/2006 - Added the toString() method
 */

package FAtiMA.wellFormedNames;

/**
 * Stores a list of Substitutions
 * @see Substitution
 * 
 * @author João Dias
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.ListIterator;

public class SubstitutionSet implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private ArrayList _substitutionSet;
	
	/**
	 * Creates a new Set of Substitutions
	 */
	public SubstitutionSet() {
		_substitutionSet = new ArrayList();
	}
	
	/**
	 * Creates a new Set of substitutions and adds all 
	 * substitutions received in the argument 
	 * @param substitutions - a list with initial substitutions to add to
	 * the SubstitutionSet
	 */
	public SubstitutionSet(ArrayList substitutions) {
		_substitutionSet = substitutions;
	}
	
	/**
	 * Adds a given Substitution to the SubstitutionSet
	 * @param subst - the Substitution to add
	 */
	public void AddSubstitution(Substitution subst) {
		_substitutionSet.add(subst);
	}
	
	/**
	 * Adds a list of Substitutions to the SubstitutionSet
	 * @param substs - the list of Substitutions to Add
	 */
	public void AddSubstitutions(ArrayList substs) {
		_substitutionSet.addAll(substs);
	}
	
	/**
	 * Gets a list with all the substitutions stored in the 
	 * SubstitutionSet
	 * @return - the list with all substitutions
	 */
	public ArrayList GetSubstitutions() {
		return _substitutionSet;
	}
	
	/**
	 * Compares the current SubstitutionSet with another Set
	 * @return True if the elements of both sets are equal.
	 */
	public boolean equals(Object o)
	{
	    if(o == null) return false;
	    if(!(o instanceof SubstitutionSet)) return false;
	    
	    SubstitutionSet ss = (SubstitutionSet)o;
	    if (this._substitutionSet.size() != ss._substitutionSet.size()) return false;
	    
	    ListIterator li1 = this._substitutionSet.listIterator();
        ListIterator li2 = ss._substitutionSet.listIterator();
        
        while(li1.hasNext() && li2.hasNext()) 
        {
        	if(!li1.next().equals(li2.next())) return false;
        }
        
        return true;
	}
	
	/**
	 * Converts the SubstitutionSet to a String 
	 */
	public String toString()
	{
		return _substitutionSet.toString();
	}
}