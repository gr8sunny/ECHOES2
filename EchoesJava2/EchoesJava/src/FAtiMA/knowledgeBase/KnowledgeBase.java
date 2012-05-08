/** 
 * KnowledgeBase.java - Implements a KnowledgeBase that stores properties and predicates about the
 * world. Provides a very fast method of searching for the information stored inside it. At the moment
 * this KnowledgeBase also provides a limited kind of Logical Inference that can be applyed to
 * generate new properties and predicates. This logical inference is performed by specific 
 * inference operators that must be explicitly added to the KB.
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
 * Created: 29/12/2003
 * @author: João Dias
 * Email to: joao.assis@tagus.ist.utl.pt
 * 
 * History: 
 * João Dias: 29/12/2003 - File created
 * João Dias: 05/04/2006 - Fixed a bug in AskPredicate and Retract Methods; added the Count Method
 * João Dias: 05/04/2006 - Now the GetPossibleBindings Method works properly when it receives a ground Name
 * João Dias: 21/04/2006 - Now the AskProperty Method returns null instead of "null" when it does not find the property
 * João Dias: 21/04/2006 - The Assert Method inserts the Symbol "True" instead of the String "True"
 * João Dias: 22/05/2006 - Added comments to each public method's header
 * João Dias: 10/07/2006 - the class is now serializable 
 * João Dias: 12/07/2006 - Found and fixed a bug in askPredicate method that occured with serialized KB's
 * João Dias: 15/07/2006 - Very important change. The KnowledgeBase is now a Singleton. It means that there is 
 * 						   only one KnowledgeBase in the Agent and the kb can be accessed from anywhere
 * 						   through the method KnowledgeBase.GetInstance
 * 						   The class constructor is now private
 * João Dias: 15/07/2006 - solved small bug in Clear method that would not clear the FactList (used only for display)
 * João Dias: 30/08/2006 - solved small bug in Tell method that caused the Display of 
 * 						   the KnowledgeBase to present data that was not updated
 * João Dias: 05/09/2006 - Added Get and SetInterpersonalRelationship methods
 * João Dias: 06/09/2006 - The Like relationship is now stored as a float, thus the Get and 
 * 				           SetInterpersonalRelationship methods work with floats
 * João Dias: 03/10/2006 - Added InferenceOperators to the KnowledgeBase. These operators allow the 
 * 						   KB to do forward inference when new Knowledge is added to the KB. However,
 * 						   the facts added due to inference are not removed.
 * João Dias: 03/10/2006 - Added method AddInferenceOperator
 * João Dias: 03/10/2006 - We don't want to take the risk of stalling the KnowledgeBase with ciclic inference.
 * 						   Therefore, the KB's inference process should be controled externally by calling
 * 						   the new method PerformInference. This method should be called every simulation cycle, and
 * 					       will detect if there is new knowledge in the KB and if so try to apply InferenceOperator.
 * 						   Note, that if new knowledge results from this process, it will be added immediately to the
 * 						   KB. However, the inference will not continue (by trying to use the new knowledge to activate
 * 					       more operators) until the method PerformInference is called in next cycle.
 * João Dias: 03/10/2006 - Added method HasNewKnowledge() that returns whether new Knowledge has been added
 * 						   to the KB
 * João Dias: 06/12/2006 - Removed the test that was checking if there was new knowledge in the KB in order to execute
 * 						   the inference rules in the PerformInference method. The method has to perform the inferences
 * 						   even if there is no new knowledge, because some of the inference preconditions can refer to the
 * 						   autobiographical memory or to the emotional state.
 * João Dias: 10/02/2007 - added the Respect relation to the KB, defining the operators GetRespect and SetRespect
 * Bruno Azenha: 09/04/2007 - Removed the methods Get/SetInterpersonalRelation and Respect. This functionality is now
 * 							  provided by the SocialRelations Package 
 */

package FAtiMA.knowledgeBase;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

import FAtiMA.conditions.Condition;
import FAtiMA.deliberativeLayer.plan.Effect;
import FAtiMA.deliberativeLayer.plan.Step;
import FAtiMA.util.ApplicationLogger;
import FAtiMA.wellFormedNames.Name;
import FAtiMA.wellFormedNames.Substitution;
import FAtiMA.wellFormedNames.SubstitutionSet;
import FAtiMA.wellFormedNames.Symbol;


/**
 * Implements a KnowledgeBase that stores properties and predicates about the
 * world. Provides a very fast method of searching for the information stored 
 * inside it. At the moment this KnowledgeBase also provides a limited kind of 
 * forward inference that can be applyed to generate new properties and predicates.
 * This logical inference is performed by specific inference operators that must be
 * explicitly added to the KB. This KB works as an efficient information repository.
 * You cannot create a KB, since there is one and only one KB for the agent. 
 * If you want to access it use KnowledgeBase.GetInstance() method.
 * 
 * @author João Dias
 */

public class KnowledgeBase implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Singleton pattern 
	 */
	private static KnowledgeBase _kbInstance = null;
	
	/**
	 * Gets the Agent's KnowledgeBase
	 * @return the KnowledgeBase
	 */
	public static KnowledgeBase GetInstance()
	{
		if(_kbInstance == null)
		{
			_kbInstance = new KnowledgeBase();
		}
		return _kbInstance;
	}
	
	/**
	 * Saves the state of the current KnowledgeBase to a file,
	 * so that it can be later restored from file
	 * @param fileName - the name of the file where we must write
	 * 		             the KnowledgeBase
	 */
	public static void SaveState(String fileName)
	{
		try 
		{
			FileOutputStream out = new FileOutputStream(fileName);
	    	ObjectOutputStream s = new ObjectOutputStream(out);
	    	
	    	s.writeObject(_kbInstance);
        	s.flush();
        	s.close();
        	out.close();
		}
		catch(Exception e)
		{
			ApplicationLogger.Write(e.getMessage());
		}
	}
	
	/**
	 * Loads a specific state of the KnowledgeBase from a previously
	 * saved file
	 * @param fileName - the name of the file that contains the stored
	 * 					 KnowledgeBase
	 */
	public static void LoadState(String fileName)
	{
		try
		{
			FileInputStream in = new FileInputStream(fileName);
        	ObjectInputStream s = new ObjectInputStream(in);
        	_kbInstance = (KnowledgeBase) s.readObject();
        	s.close();
        	in.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private KnowledgeSlot _kB;
	private ArrayList _factList;
	private ArrayList _inferenceOperators;
	private boolean _newKnowledge;
	private ArrayList _newFacts;

	/**
	 * Creates a new Empty KnowledgeBase
	 */
	private KnowledgeBase() {
		_kB = new KnowledgeSlot("KB");
		_factList = new ArrayList();
		_inferenceOperators = new ArrayList();
		_newKnowledge = false;
		_newFacts = new ArrayList();
	}
	
	/**
	 * Adds an InferenceOperator to the KnowledgeBase
	 * @param op - the inference operator to Add
	 */
	public void AddInferenceOperator(Step op)
	{
		_inferenceOperators.add(op);
	}
	
	/**
	 * Gets the number of elements (predicates or properties) stored in the KnowledgeBase
	 * @return the number of elements stored in the KB
	 */
    public int Count()
    {
    	return this._kB.CountElements();
    }
    
    /**
     * Gets a value that indicates whether new Knowledge has been added to the KnowledgeBase since
     * the last inference process
     * @return true if there is new Knowledge in the KB, false otherwise
     */
    public boolean HasNewKnowledge()
    {
    	return this._newKnowledge;
    }
    
    public ArrayList GetNewFacts()
    {
    	return this._newFacts;
    }
    
    /**
     *  This method should be called every simulation cycle, and will try to apply InferenceOperators.
     * 	Note, that if new knowledge results from this process, it will be added immediately to the
     *  KB. However, the inference will not continue (by trying to use the new knowledge to activate
     *  more operators) until the method PerformInference is called in next cycle.
     * 
     * @return true if the Inference resulted in new Knowledge being added, false if no
     * new knowledge was infered
     */
    public boolean PerformInference()
    {
    	Step infOp;
    	Step groundInfOp;
    	ArrayList substitutionSets;
    	SubstitutionSet sSet;
    	
    	_newKnowledge = false;
    	_newFacts.clear();
    	
		for(ListIterator li = _inferenceOperators.listIterator();li.hasNext();)
		{
			infOp = (Step) li.next();
			substitutionSets = Condition.CheckActivation(infOp.getPreconditions());
			if(substitutionSets != null)
			{
				for(ListIterator li2 = substitutionSets.listIterator();li2.hasNext();)
				{
					sSet = (SubstitutionSet) li2.next();
					groundInfOp = (Step) infOp.clone();
					groundInfOp.MakeGround(sSet.GetSubstitutions());
					InferEffects(groundInfOp);
				}
			}
		}
    	
    	return _newKnowledge;
    }
    
    private void InferEffects(Step infOp)
    {
    	Effect eff;
    	for(ListIterator li = infOp.getEffects().listIterator();li.hasNext();)
    	{
    		eff = (Effect) li.next();
    		if(eff.isGrounded())
    		{
    			Tell(eff.GetEffect().getName(),eff.GetEffect().GetValue().toString());
    			
    		}
    	}
    }
    

    /**
     * Asks the KnowledgeBase the Truth value of the received predicate
     * @param predicate - The predicate to search in the KnowledgeBase
     * @return Under the Closed World Assumption, the predicate is considered 
     * true if it is stored in the KB and false otherwise.
     */
    
	public boolean AskPredicate(Name predicate) 
	{
        KnowledgeSlot ks = (KnowledgeSlot) Ask(predicate);
        if (ks != null && ks.getValue() != null && ks.getValue().toString().equals("True"))
        {
            return true;
        }
        else
        {
            return false;
        }
	}

	/**
	 * Asks the KnowledgeBase the value of a given property
	 * @param property - the property to search in the KnowledgeBase
	 * @return the value stored inside the property, if the property exists. If the 
     *         property does not exist, it returns null
	 */
	public Object AskProperty(Name property) {
		KnowledgeSlot prop = (KnowledgeSlot) Ask(property);
		if (prop == null)
			return null;
		else
			return prop.getValue();
	}

	/**
	 * Inserts a Predicate in the KnowledgeBase
	 * @param predicate - the predicate to be inserted
	 */
	public void Assert(Name predicate) {
		this.Tell(predicate,new Symbol("True"));
	}

	/**
	 * Empties the KnowledgeBase
	 *
	 */
	public void Clear() {
		synchronized (this) {
			this._kB.clear();
			this._factList.clear();
			this._newFacts.clear();
			this._newKnowledge = false;
		}
	}

	/**
	 * This method provides a way to search for properties/predicates in the KnowledgeBase 
     * that match with a specified name with unbound variables.
     *
     * In order to understand this method, let’s examine the following example. Suppose that 
     * the memory only contains properties about two characters: Luke and John.
     * Furthermore, it only stores two properties: their name and strength. So the KB will 
     * only store the following objects:
     * - Luke(Name) : Luke
     * - Luke(Strength) : 8
     * - John(Name) : John 
     * - John(Strength) : 4
     * 
     * The next table shows the result of calling the method with several distinct names. 
     * The function works by finding substitutions for the unbound variables, which make 
     * the received name equal to the name of an object stored in memory.
     * 
     * Name	        Substitutions returned
     * Luke([x])	    {{[x]/Name},{[x]/Strength}}
     * [x](Strength)    {{[x]/John},{[x]/Luke}}
     * [x]([y])	        {{[x]/John,[y]/Name},{[x]/John,[y]/Strength},{[x]/Luke,[y]/Name},{[x]/Luke,[y]/Strength}}
     * John(Name)	    {{}}
     * John(Height)	    null
     * Paul([x])	    null
     *
     * In the first example, there are two possible substitutions that make “Luke([x])”
     * equal to the objects stored above. The third example has two unbound variables,
     * so the returned set contains all possible combinations of variable attributions.
     * 
     * If this method receives a ground name, as seen on examples 4 and 5, it checks
     * if the received name exists in memory. If so, a set with the empty substitution is
     * returned, i.e. the empty substitution makes the received name equal to some object
     * in memory. Otherwise, the function returns null, i.e. there is no substitution
     * that applied to the name will make it equal to an object in memory. This same result
     * is returned in the last example, since there is no object named Paul, and therefore no 
     * substitution of [x] will match the received name with an existing object.
	 * @param name - a name (that correspond to a predicate or property)
	 * @return a list of SubstitutionSets that make the received name to match predicates or 
     *         properties that do exist in the KnowledgeBase
	 */
	public ArrayList GetPossibleBindings(Name name) {
		ArrayList bindingSets = null;

		bindingSets = MatchLiteralList(name.GetLiteralList(), 0, _kB);
		
		if (bindingSets == null || bindingSets.size() == 0)
			return null;
		else
			return bindingSets;

	}

	/**
     * Removes a predicate from the KnowledgeBase
	 * @param predicate - the predicate to be removed
	 */
	public void Retract(Name predicate) {
		
		KnowledgeSlot aux = _kB;
		KnowledgeSlot currentSlot = _kB;
		ArrayList fetchList = predicate.GetLiteralList();
		ListIterator li = fetchList.listIterator();
		Symbol l = null;
			
		synchronized (this) {
			
			while (li.hasNext()) {
				currentSlot =  aux;
				l = (Symbol) li.next();
				if (currentSlot.containsKey(l.toString())) {
					aux = currentSlot.get(l.toString());
				} else
					return;
			}
			if (aux.CountElements() > 0)
            {
                //this means that there are elements under the aux node, and thus 
                //we cannot delete it, just put it null
                aux.setValue(null);
            }
            else if (l != null)
            {
                currentSlot.remove(l.toString());
            }
			
			KnowledgeSlot ks;
			li = _factList.listIterator();
			while(li.hasNext())
			{
				ks = (KnowledgeSlot) li.next();
				if(ks.getName().equals(predicate.toString()))
				{
					li.remove();
					return;
				}
			}
		}
	}

	/**
	 * Adds a new property or sets its value (if already exists) in the KnowledgeBase
	 * @param property - the property to be added/changed
	 * @param value - the value to be stored in the property
	 */
	public void Tell(Name property, Object value) {

		boolean newProperty = false;
		KnowledgeSlot aux = _kB;
		KnowledgeSlot currentSlot;
		ArrayList fetchList = property.GetLiteralList();
		ListIterator li = fetchList.listIterator();
		Symbol l;
		

		synchronized (this) {
			while (li.hasNext()) {
				currentSlot = aux;
				l = (Symbol) li.next();
				if (currentSlot.containsKey(l.toString())) {
					aux = currentSlot.get(l.toString());
				} else {
					newProperty = true;
					_newKnowledge = true;
					aux = new KnowledgeSlot(l.toString());
					currentSlot.put(l.toString(), aux);
				} 
			}
			if(aux.getValue() == null || 
					!aux.getValue().equals(value))
			{
				aux.setValue(value);
				_newKnowledge = true;
				KnowledgeSlot ksAux = new KnowledgeSlot(property.toString());
				ksAux.setValue(value);
				_newFacts.add(ksAux);
			}
			
			if(newProperty)
			{
				KnowledgeSlot ks = new KnowledgeSlot(property.toString());
				ks.setValue(value);
				_factList.add(ks);
				_newFacts.add(ks);
			}
			else
			{
				KnowledgeSlot ks;
				li = _factList.listIterator();
				while(li.hasNext())
				{
					ks = (KnowledgeSlot) li.next();
					if(ks.getName().equals(property.toString()))
					{
						ks.setValue(value);
					}
				}
			}
		}
	}
	
	public KnowledgeSlot GetObjectDetails(String objectName)
	{
		return _kB.get(objectName);
	}
	
	/*public float GetInterpersonalRelationShip(String target)
	{
		Float result;
		String self = AutobiographicalMemory.GetInstance().getSelf();
		Name likeProperty = Name.ParseName("Like(" + self + "," + target + ")");
		result = (Float) AskProperty(likeProperty);
		if(result == null)
		{
			Tell(likeProperty, new Float(0));
			return 0;
		}
		return result.floatValue();
	}
	
	public void SetInterpersonalRelationShip(String target, float like)
	{
		String self = AutobiographicalMemory.GetInstance().getSelf();
		Name likeProperty = Name.ParseName("Like(" + self + "," + target + ")");
		Tell(likeProperty, new Float(like));
	}
	
	public float GetRespect(String target)
	{
		Float result;
		String self = AutobiographicalMemory.GetInstance().getSelf();
		Name respectProperty = Name.ParseName("Respect(" + self + "," + target + ")");
		result = (Float) AskProperty(respectProperty);
		if(result == null)
		{
			Tell(respectProperty, new Float(0));
			return 0;
		}
		return result.floatValue();
	}
	
	public void SetRespect(String target, float like)
	{
		String self = AutobiographicalMemory.GetInstance().getSelf();
		Name respectProperty = Name.ParseName("Respect(" + self + "," + target + ")");
		Tell(respectProperty, new Float(like));
	}*/
	
	public ListIterator GetFactList() {
	    return _factList.listIterator();
	}

	private Object Ask(Name name) {
		KnowledgeSlot aux = _kB;
		KnowledgeSlot currentSlot;
		ArrayList fetchList = name.GetLiteralList();
		ListIterator li = fetchList.listIterator();
		Symbol l;

		synchronized (this) {
			while (li.hasNext()) {
					currentSlot = aux;
					l = (Symbol) li.next();
					if (currentSlot.containsKey(l.toString())) {
						aux = currentSlot.get(l.toString());
					} 
					else return null;
			}
			return aux;
		}
	}

	private ArrayList MatchLiteralList(ArrayList literals, int index, KnowledgeSlot kSlot) {
		Symbol l;
		String key;
		ArrayList bindingSets;
		ArrayList newBindingSets;
		SubstitutionSet subSet;
		ListIterator li;
		Iterator it;

		newBindingSets = new ArrayList();

		if (index >= literals.size()) {
			newBindingSets.add(new SubstitutionSet());
			return newBindingSets;
		}

		synchronized (this) {
			l = (Symbol) literals.get(index++);

			if (l.isGrounded()) {
				if (kSlot.containsKey(l.toString())) {
					return MatchLiteralList(literals, index, kSlot.get(l
							.toString()));
				} else
					return null;
			}

			it = kSlot.getKeyIterator();
			while (it.hasNext()) {
				key = (String) it.next();
				bindingSets = MatchLiteralList(literals, index, kSlot
						.get(key));
				if (bindingSets != null) {
					li = bindingSets.listIterator();
					while (li.hasNext()) {
						subSet = (SubstitutionSet) li.next();
						subSet.AddSubstitution(new Substitution(l, new Symbol(
								key)));
						newBindingSets.add(subSet);
					}
				}
			}
		}

		if (newBindingSets.size() == 0)
			return null;
		else
			return newBindingSets;
	}
	
	/**
	 * Converts the Information stored in the KB to one String
	 * @return the converted String
	 */
	public String toString() {
	    return _kB.toString();
	}
}