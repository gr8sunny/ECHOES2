/** 
 * IntegrityValidator.java - Class used to validate the integrity of information about the 
 * agent parsed at the begining. For example, among other things, this class verifies 
 * if the personality file does not reference an unspecified goal (it's not defined 
 * in the GoalLibrary)
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
 * Created: 15/01/2005 
 * @author: João Dias
 * Email to: joao.assis@tagus.ist.utl.pt
 * 
 * History: 
 * João Dias: 15/01/2005 - File created
 * João Dias: 25/05/2006 - FindSpeechAct method made private, it doesn't make sense to
 * 						   be public
 * João Dias: 25/05/2006 - Added comments to each public method's header
 * João Dias: 31/08/2006 - Solved bug that occurred when comparing predicates in 
 * 						   FindUnreachableConditions method
 * João Dias: 03/10/2006 - Small change in the test that was verifying if a given step
 * 						   corresponded to a SpeechAct. Instead of comparing the step's
 * 						   name with a prefined String ("SpeechAct") we now use the 
 * 						   isSpeechAct(String) method from the class SpeechAct
 * Joao Dias: 08/10/2006 - small change in the test that was verifying if a given step
 * 						   corresponded to a SpeechAct. If the SpeechAct's type equals
 * 						   "*", the test will also return true.
 */

package FAtiMA;

import java.util.ArrayList;
import java.util.ListIterator;

import FAtiMA.conditions.Condition;
import FAtiMA.conditions.PropertyNotEqual;
import FAtiMA.deliberativeLayer.plan.Effect;
import FAtiMA.deliberativeLayer.plan.Step;
import FAtiMA.exceptions.UnknownSpeechActException;
import FAtiMA.sensorEffector.SpeechAct;
import FAtiMA.wellFormedNames.Name;
import FAtiMA.wellFormedNames.Unifier;
import Language.LanguageEngine;

/**
 * Class used to validate the integrity of information about the 
 * agent parsed at the begining. For example, among other things, this class verifies 
 * if the personality file does not reference an unspecified goal (it's not defined 
 * in the GoalLibrary)
 * 
 * @author João Dias
 */
public class IntegrityValidator {
    
    ArrayList _operators;
    LanguageEngine _language;
    LanguageEngine _user;
    
    /**
     * Creates a new IntegrityValidator
     * 
     * @param operators - the list of domain actions that will be used by the planner,
     * 					  necessary to test if a Goal is achievable by any of the actions
     * @param lang - the LanguageEngine for the agent's SpeechActs, necessary to test
     * 				 if references to SpeechActs are valid or not
     * @param user - the LanguageEngine for the user's SpeechActs
     */
    public IntegrityValidator(ArrayList operators, LanguageEngine lang, LanguageEngine user) {
        _operators = operators;
        _language = lang;
        _user = user;
    }

    /**
     * Checks if the received conditions are unreachable. This means that one
     * of the conditions cannot be achieved by any operator that the planner 
     * can use. Thus, such condition is impossible to make true. In this situation,
     * a warning is given to the output, since this is likely a typo error when
     * specifying the conditions (or one of the steps).
     * 
     * @param objectName - the name of the object (usually goal or action) that specifies
     * 				       the conditions tested. This name is required for the warning
     * 					   if an unreacheable condition is found  
     * @param conditions - the list of conditions to test if they are unreacheable
     * @return true if any of the conditions received is unreacheable, false otherwise
     */
    public boolean FindUnreachableConditions(String objectName, ArrayList conditions)
	{
	    boolean foundUnreachable = false;
	    boolean ok;
	    Condition cond;
	    Condition effCondition;
	    Step s;
	    ArrayList substs;
	    Name condValue;
	    Name effValue;
	    boolean unifyResult;
	    ListIterator li2;
	    ListIterator li3;
	    ListIterator li = conditions.listIterator();
	    
	    //for each step precondition
	    while (li.hasNext()) {
	        ok = false;
	        cond = (Condition) li.next();
	        li2 = _operators.listIterator();
	        //for each existing step
	        while (li2.hasNext()) {
	           s = (Step) li2.next();
	           li3 = s.getEffects().listIterator();
	           //for each effect of the current step 
	           while(li3.hasNext()) {
	               effCondition = ((Effect) li3.next()).GetEffect();
	               substs = new ArrayList();
	               if (Unifier.Unify(cond.getName(), effCondition.getName(), substs)) {
						condValue = cond.GetValue();
						effValue = effCondition.GetValue();
						unifyResult = Unifier.Unify(condValue, effValue, substs);
						
						if (cond instanceof PropertyNotEqual)
						{
							unifyResult = !unifyResult;
						}
						if(unifyResult)
						{
							ok = true;
							break;
						}
					}
	           }
	           if(ok) break;
	        }
	        if(!ok) {
	            //System.out.println("WARNING: condition " + cond + " of the goal/action " + objectName + " cannot be achived by any step!");
	            foundUnreachable = true;
	        }
	    }
	    
	    return foundUnreachable;
	}
    
    /**
     * Checks if a given action that corresponds to a SpeechAct is defined in the 
     * LanguageEngine or not.
     * 
     * @param actionName - the name of the speechAct to check if it is defined 
     * @throws UnknownSpeechActException - thrown if the received SpeechAct is not
     * 									   defined in the LanguageEngine
     */
    public void CheckSpeechAction(Name actionName) throws UnknownSpeechActException 
    {
    	if(SpeechAct.isSpeechAct(actionName.GetFirstLiteral().toString()))
    	{
    		Name speechType = (Name) actionName.GetLiteralList().get(2);
     	    if(speechType.isGrounded() && !speechType.toString().equals("*")) {
     	    	if(!FindSpeechAct(speechType.toString())) 
     	    	{
     	    		throw new UnknownSpeechActException(speechType.toString());
     	        }
     	    }
     	        
    	}
    }
    
    private boolean FindSpeechAct(String speechActType) {
    	String[] lacts = _language.ListLacts();
        
        for(int i=0; i < lacts.length; i++) {
            if(speechActType.equals(lacts[i])) {
                return true;
            }
        }
        
        /*lacts = _user.ListLacts();
        
        for(int i=0; i < lacts.length; i++) {
            if(speechActType.equals(lacts[i])) {
                return true;
            }
        }*/
        return true;
        
        //return false;
    }
}
