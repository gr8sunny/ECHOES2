/**
 * Unifier.cs - Static Class that implements the unifying algorithm 
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
 * Created: 19/01/2004 
 * @author: João Dias
 * Email to: joao.assis@tagus.ist.utl.pt 
 * 
 * History: 
 * João Dias: 19/01/2004 - File created
 * João Dias: 13/03/2006 - Minor change in FindSubst(Symbol, Symbol,...) Method
 * João Dias: 14/03/2006 - Improved Unification Algorithm - Now works much better 
 * João Dias: 14/05/2006 - Added the Unify(Name,Name) Method
 * João Dias: 12/07/2006 - Replaced the deprecated Ground methods for the new ones
 */

package FAtiMA.wellFormedNames;

import java.util.ArrayList;
import java.util.ListIterator;

/**
 * Static Class that implements the Unifying algorithm
 * 
 * @author João Dias
 */

public abstract class Unifier {

    /**
     * @see Name
     * @see Substitution
     * 
     * Unifying Method, receives two WellFormedNames and tries 
     * to find a list of Substitutions that will make 
     * both names syntatically equal. The algorithm does not perform Occur Check,
     * since  the unification of [X] and Luke(Strength) is allways assumed to fail.
     * 
     * The method goes on each symbol (for both names) at a time, and tries to find 
     * a substitution between them. Take into account that the Unification between
     * [X](John,Paul) and Friend(John,[X]) fails because the algorithm considers [X]
     * to be the same variable
     * 
     * @param n1 - The first Name
     * @param n2 - The second Name
     * @param binding - Place an empty Substitution List here
     * @return True if the names are unifyable, in this case the bindings list will
     * 		   contain the found Substitutions, otherwise it will be empty
     */
	public static boolean Unify(Name n1, Name n2, ArrayList bindings) {
		Name aux1;
		Name aux2;
		ArrayList bindAux;

		if (n1 == null || n2 == null)
			return false;
		//parto do principio que a lista de bindings está consistente
		aux1 = (Name) n1.clone();
		aux2 = (Name) n2.clone();
		aux1.MakeGround(bindings);
		aux2.MakeGround(bindings);
		if (aux1.isGrounded() && aux2.isGrounded()) {
			return aux1.equals(aux2);
		}
		bindAux = FindSubst(aux1, aux2);
		if (bindAux == null)
			return false;
		bindings.addAll(bindAux);
		return true;
	}
	
	/**
     * @see Name
     * @see Substitution
     * 
     * Unifying Method, receives two WellFormedNames and tries 
     * to find a list of Substitutions that will make 
     * both names syntatically equal. The algorithm does not perform Occur Check,
     * since  the unification of [X] and Luke(Strength) is allways assumed to fail.
     * 
     * The method goes on each symbol (for both names) at a time, and tries to find 
     * a substitution between them. Take into account that the Unification between
     * [X](John,Paul) and Friend(John,[X]) fails because the algorithm considers [X]
     * to be the same variable
     * 
     * @param n1 - The first Name
     * @param n2 - The second Name
     * @return A list of substitutions if the names are unifyable, otherwise returns null
     */
	public static ArrayList Unify(Name n1, Name n2)
    {
	    ArrayList subs = new ArrayList();

        if (Unify(n1, n2, subs))
        {
            return subs;
        }
        else return null;
    }

	private static boolean FindSubst(Symbol l1, Symbol l2, ArrayList bindings) {
		Symbol aux1 = (Symbol) l1.clone();
		Symbol aux2 = (Symbol) l2.clone();
		
		aux1.MakeGround(bindings);
		aux2.MakeGround(bindings);
		
		if (!aux1.isGrounded()) 
		{
			if (!aux1.equals(aux2))
            {
                bindings.add(new Substitution(aux1, aux2));
            }
		}
		else if(!aux2.isGrounded()) 
		{
			bindings.add(new Substitution(aux2, aux1));
		}
			
		else 
		{
			return aux1.equals(aux2);
		}
	
		return true;
	}

	private static ArrayList FindSubst(Name n1, Name n2) {
		ListIterator li1;
		ListIterator li2;

		Symbol l1;
		Symbol l2;
		ArrayList bindings = new ArrayList();

		li1 = n1.GetLiteralList().listIterator();
		li2 = n2.GetLiteralList().listIterator();

		while (li1.hasNext()) {
			l1 = (Symbol) li1.next();
			if (li2.hasNext()) {
				l2 = (Symbol) li2.next();
				if (!FindSubst(l1, l2, bindings))
					return null;
			}
			else
				return null;
		}

		if (li2.hasNext())
			return null;
		return bindings;
	}
}