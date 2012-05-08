/** 
 * ActivePursuitGoal.java - Implements OCC's ActivePursuit goals that have activation
 * conditions
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
 * João Dias: 24/05/2006 - Added comments to each public method's header
 * João Dias: 10/07/2006 - the class is now serializable
 * João Dias: 12/07/2006 - The check methods now receive a reference to the KnowledgeBase
 * João Dias: 12/07/2006 - Changes in groundable methods, the class now implements
 * 						   the IGroundable Interface, the old ground methods are
 * 					       deprecated
 * João Dias: 12/07/2006 - Replaced the deprecated Ground methods for the new ones.
 * João Dias: 12/07/2006 - the class is now Clonable 
 * João Dias: 31/08/2006 - Important conceptual change: Since we have now two types of memory,
 * 						   the KnowledgeBase (Semantic memory) and Autobiographical memory (episodic memory),
 * 						   and we have RecentEvent and PastEvent conditions that are searched in episodic
 * 						   memory (and the old conditions that are searched in the KB), it does not make 
 * 						   sense anymore to receive a reference to the KB in searching methods 
 * 						   (checkActivation, CheckSuccess, etc) for Goals. Since both the KB 
 * 						   and AutobiographicalMemory are singletons that can be accessed from any part of 
 * 						   the code, these methods do not need to receive any argument. It's up to each type
 * 						   of condition to decide which memory to use when searching for information.
 * João Dias: 07/09/2006 - Changed the test being performed in the methods CheckSuccessConditions and
 * 						   CheckFailureConditions. The previous test would allways return false if the condition
 * 						   to be grounded was not grounded. Now I'm trying to see if there any possible bindings
 * 						   that can make the condition true. This means that now is possible to have success and
 * 						   failure conditions that have unbound variables.
 * 						 - Fixed a small bug in the CheckActivation method that occurred when you had more than
 * 						   one possible SubstitutionSet for verifying a goal's precondition ex: {[X]/John} and 
 * 						   {[X]/Luke}. If the first substitution failed to achieve the condition, the method would
 * 						   return failure and would not test the next Substitution.
 * João Dias: 12/09/2006 - Fixed a bug in method CheckActivation introduced by the previous change
 * João Dias: 03/10/2006 - Important refactorization: the method CheckActivation() was moved from this
 * 						   class to the Condition, since it is indeed a very general method that can be 
 * 						   reused to test a list of preconditions. The only thing that the old method was
 * 						   using from an ActivePursuitGoal was the precondition list being tested. Therefore,
 * 						   this method has become a static method that receives as a parameter the list 
 * 						   of preconditions to test.
 * 						 - The active pursuit goal now uses the static CheckActivation(ArrayList) method
 * 						   from the class Condition to test its preconditions;
 */

package FAtiMA.deliberativeLayer.goals;

import java.util.ArrayList;
import java.util.ListIterator;

import FAtiMA.IntegrityValidator;
import FAtiMA.conditions.Condition;
import FAtiMA.exceptions.UnreachableGoalException;
import FAtiMA.wellFormedNames.Name;
import FAtiMA.wellFormedNames.Substitution;


/**
 * Implements OCC's ActivePursuit goals that have activation
 * conditions
 * 
 * @author João Dias
 */
public class ActivePursuitGoal extends Goal {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private boolean _active;
	private ArrayList _failureConditions;
	private ArrayList _preConditions;
	private ArrayList _successConditions;

	/**
	 * Creates a new ActivePursuitGoal
	 * @param description - the goal's name or description
	 */
	public ActivePursuitGoal(Name description) {
		super(description);
		_preConditions = new ArrayList(5);
		_successConditions = new ArrayList(2);
		_failureConditions = new ArrayList(2);
		_active = false;
	}

	private ActivePursuitGoal() {
	}

	/**
	 * Adds a condition to the goal
	 * @param conditionType - the type of the condition: 
	 * 						  PreConditions
	 * 			  			  SuccessConditions
	 * 						  FailureConditions 
	 * @param cond - the condition to add
	 */
	public void AddCondition(String conditionType, Condition cond) {
		if (conditionType.equals("PreConditions"))
			_preConditions.add(cond);
		else if (conditionType.equals("SuccessConditions"))
			_successConditions.add(cond);
		else if (conditionType.equals("FailureConditions")) 
		    _failureConditions.add(cond);
	}
	
	
	
	/**
	 * Checks an ActivePursuitGoal's failure conditions
	 * if at least one of them is verified the goal fails
	 * @return true if the goal failed, false otherwise
	 */
	public boolean CheckFailure() {
	    ListIterator li;
		Condition cond;
		li = _failureConditions.listIterator();
		
		while (li.hasNext()) {
			cond = (Condition) li.next();
			if (cond.GetValidBindings() != null)
				return true;
		}
		return false;
	}
	
	/**
	 * Checks the integrity of the goal. For instance it checks if the goal's 
	 * success conditions are reachable by at leas one action in the domain operators.
	 * If not it means that the goal will never be achieve and probably is a typo in
	 * the goal's definition (or in the actions file) 
	 * @param val - the validator used to check the goal
	 * @throws UnreachableGoalException - thrown if a goal's success conditions can never
	 * 									  be achieved because there is no operator with such
	 * 									  effects
	 */
	public void CheckIntegrity(IntegrityValidator val) throws UnreachableGoalException {
	    if(val.FindUnreachableConditions(_name.toString(),_successConditions)) {
	        throw new UnreachableGoalException(_name.toString());
	    }
	}

	/**
	 * Checks an ActivePursuitGoal's success conditions
	 * if all of them are verified the goal succeeds
	 * @return true if the goal succeeded, false otherwise
	 */
	public boolean CheckSucess() {
	    ListIterator li;
		Condition cond;
		li = _successConditions.listIterator();
		
		while (li.hasNext()) {
			cond = (Condition) li.next();
			if(cond.GetValidBindings() == null)
			{
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Gets the goal's failure conditions
	 * @return a list with the goal's failure conditions
	 */
	public ArrayList GetFailureConditions() {
	    return _failureConditions;
	}
	
	/**
	 * Gets the goal's success conditions
	 * @return a list with the goal's success conditions 
	 */
	public ArrayList GetSuccessConditions() {
		return _successConditions;
	}
	
	/**
	 * Gets the goal's preconditions
	 * @return a list with the goal's preconditions
	 */
	public ArrayList GetPreconditions() {
		return _preConditions;
	}
	
	/**
     * @deprecated use ReplaceUnboundVariables(int) instead.
	 * Replaces all unbound variables in the object by applying a numeric
	 * identifier to each one.
	 * Example: the variable [X] becomes [X4] if the received ID is 4.
	 * @param variableID - the identifier to be applied
	 * @return a new Goal with the variables changed 
	 */
	public Object GenerateName(int id)
	{
		ActivePursuitGoal aux = (ActivePursuitGoal) this.clone();
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
    	ListIterator li;
    	
    	this._name.ReplaceUnboundVariables(variableID);
    	
    	li = this._preConditions.listIterator();
    	while(li.hasNext())
    	{
    		((Condition) li.next()).ReplaceUnboundVariables(variableID);
    	}
    	
    	li = this._failureConditions.listIterator();
    	while(li.hasNext())
    	{
    		((Condition) li.next()).ReplaceUnboundVariables(variableID);
    	}
    	
    	li = this._successConditions.listIterator();
    	while(li.hasNext())
    	{
    		((Condition) li.next()).ReplaceUnboundVariables(variableID);
    	}
    	
    }
    
    /**
     * @deprecated use the method MakeGround(ArrayList) instead
	 * Applies a set of substitutions to the object, grounding it.
	 * Example: Applying the substitution "[X]/John" in the name "Weak([X])" returns
	 * "Weak(John)".
	 * @param bindings - A list of substitutions of the type "[Variable]/value"
	 * @return a new Goal with the substitutions applied
	 * @see Substitution
	 */
	public Object Ground(ArrayList bindingConstraints) 
	{
		ActivePursuitGoal aux = (ActivePursuitGoal) this.clone();
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
    	ListIterator li;
    	
    	this._name.MakeGround(bindings);
    	
    	li = this._preConditions.listIterator();
    	while(li.hasNext())
    	{
    		((Condition) li.next()).MakeGround(bindings);
    	}
    	
    	li = this._failureConditions.listIterator();
    	while(li.hasNext())
    	{
    		((Condition) li.next()).MakeGround(bindings);
    	}
    	
    	li = this._successConditions.listIterator();
    	while(li.hasNext())
    	{
    		((Condition) li.next()).MakeGround(bindings);
    	}
    }
    
   
    /**
     * @deprecated use the method MakeGround(Substitution) instead
	 * Applies a substitution to the object, grounding it.
	 * Example: Applying the substitution "[X]/John" in the name "Weak([X])" returns
	 * "Weak(John)".
	 * @param subst - a substitution of the type "[Variable]/value"
	 * @return a new Goal with the substitution applied
	 * @see Substitution
	 */
	public Object Ground(Substitution subst)
	{
		ActivePursuitGoal aux = (ActivePursuitGoal) this.clone();
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
    	ListIterator li;
    	
    	this._name.MakeGround(subst);
    	
    	li = this._preConditions.listIterator();
    	while(li.hasNext())
    	{
    		((Condition) li.next()).MakeGround(subst);
    	}
    	
    	li = this._failureConditions.listIterator();
    	while(li.hasNext())
    	{
    		((Condition) li.next()).MakeGround(subst);
    	}
    	
    	li = this._successConditions.listIterator();
    	while(li.hasNext())
    	{
    		((Condition) li.next()).MakeGround(subst);
    	}
    }
	
	/**
	 * Clones this ActivePursuitGoal, returning an equal copy.
	 * If this clone is changed afterwards, the original object remains the same.
	 * @return The Goal's copy.
	 */
	public Object clone()
	{
		ListIterator li;
		ActivePursuitGoal g = new ActivePursuitGoal();
		g._goalID = this._goalID;
		g._active = this._active;
		g._name = (Name) this._name.clone();
		g._baseIOF = this._baseIOF;
		g._baseIOS = this._baseIOS;
		g._dynamicIOF = (Name) this._dynamicIOF.clone();
		g._dynamicIOS = (Name) this._dynamicIOS.clone();
		
		if(this._preConditions != null)
		{
			g._preConditions = new ArrayList(this._preConditions.size());
			li = this._preConditions.listIterator();
			while(li.hasNext())
			{
				g._preConditions.add(((Condition) li.next()).clone());
			}
		}
		
		if(this._failureConditions != null)
		{
			g._failureConditions = new ArrayList(this._failureConditions.size());
			li = this._failureConditions.listIterator();
			while(li.hasNext())
			{
				g._failureConditions.add(((Condition) li.next()).clone());
			}
		}
		
		if(this._successConditions != null)
		{
			
			g._successConditions = new ArrayList(this._successConditions.size());
			li = this._successConditions.listIterator();
			while(li.hasNext())
			{
				g._successConditions.add(((Condition) li.next()).clone());
			}
		}
		
		return g;
		
	}
	
	/**
	 * Converts the ActivePursuitGoal to a String
	 * @return the converted String
	 */
	public String toString() {
		return "ActivePursuitGoal: " + super.toString(); 
	}
}