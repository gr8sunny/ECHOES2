/* MoodCondition.java - 
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
 * Created: 09/02/2007
 * @author: João Dias
 * Email to: joao.assis@tagus.ist.utl.pt
 * 
 * History: 
 * João Dias: 09/02/2007 - File created
 */

package FAtiMA.conditions;

import java.util.ArrayList;

import org.xml.sax.Attributes;

import FAtiMA.autobiographicalMemory.AutobiographicalMemory;
import FAtiMA.emotionalState.EmotionalState;
import FAtiMA.exceptions.InvalidMoodOperatorException;
import FAtiMA.exceptions.NoMoodOperatorDefinedException;
import FAtiMA.wellFormedNames.Name;
import FAtiMA.wellFormedNames.Substitution;
import FAtiMA.wellFormedNames.SubstitutionSet;
import FAtiMA.wellFormedNames.Symbol;


public class MoodCondition extends Condition {
	
	private static final long serialVersionUID = 1L;
	
	protected static final short operatorGreater = 0;
	protected static final short operatorGreaterEqual = 1;
	protected static final short operatorLesser = 2;
	protected static final short operatorLesserEqual = 3;
	protected static final short operatorEqual = 4;
	protected static final short operatorNotEqual = 5;
	protected static final short invalidOperator = 6;
	
	protected float _value;
	protected short _operator;

	
	/**
	 * Parses a EmotionCondition given a XML attribute list
	 * @param attributes - A list of XMl attributes
	 * @return - the EmotionCondition Parsed
	 */
	public static MoodCondition ParseMoodCondition(Attributes attributes) throws InvalidMoodOperatorException, NoMoodOperatorDefinedException
	{
		float value = 0;
		short operator = invalidOperator;
		
		String aux;
		
		aux = attributes.getValue("value");
		if(aux != null)
		{
			value = Float.parseFloat(aux);
		}

		aux = attributes.getValue("operator");
		if(aux == null)
		{
			throw new NoMoodOperatorDefinedException();
		}
		else
		{
			if(aux.equals("GreaterThan"))
			{
				operator = operatorGreater;
			}
			else if(aux.equals("LesserThan"))
			{
				operator = operatorLesser;
			}
			else if(aux.equals("GreaterEqual"))
			{
				operator = operatorGreaterEqual;
			}
			else if(aux.equals("LesserEqual"))
			{
				operator = operatorLesserEqual;
			}
			else if(aux.equals("="))
			{
				operator = operatorEqual;
			}
			else if(aux.equals("!="))
			{
				operator = operatorNotEqual;
			}
			else
			{
				throw new InvalidMoodOperatorException(aux);
			}
		}
	
		MoodCondition mc = new MoodCondition(operator,value);
			
		return mc;
	}
	
	private MoodCondition()
	{
	}
	
	public MoodCondition(short operator, float value)
	{
		this._operator = operator;
		
		if(value > 10) {
			this._value = 10;
		}
		else if(value < -10)
		{
			this._value = -10;
		}
		else
		{
			this._value = value;
		}
		
		UpdateName();
	}
	
	private void UpdateName()
	{
		String aux = AutobiographicalMemory.GetInstance().getSelf() + "(mood," + this._operator + ")";
		this._name = Name.ParseName(aux);
	}
	
	/**
	 * Gets the condition's value - the object compared against the condition's name
	 * @return the condition's value
	 */
	 public Name GetValue()
	 {
		return new Symbol(Float.toString(this._value));
	 }
	
	/**
	 * Checks if the Predicate is verified in the agent's KnowledgeBase
	 * @return true if the Predicate is verified, false otherwise
	 * @see KnowledgeBase
	 */
	public boolean CheckCondition() {
		
		float currentMood = EmotionalState.GetInstance().GetMood();
		
		switch(this._operator)
		{
			case operatorEqual:
			{
				return currentMood == this._value;
			}
			case operatorNotEqual:
			{
				return currentMood != this._value;
			}
			case operatorGreater:
			{
				return currentMood > this._value;
			}
			case operatorGreaterEqual:
			{
				return currentMood >= this._value;
			}
			case operatorLesser:
			{
				return currentMood < this._value;
			}
			case operatorLesserEqual:
			{
				return currentMood <= this._value;
			}
		}
		return false;
	}
	
	/**
	 * This method finds all the possible sets of Substitutions that applied to the 
	 * condition will make it valid (true) according to the agent's EmotionalState 
     * @return A list with all SubstitutionsSets that make the condition valid
	 * @see EmotionalState
	 */
	public ArrayList GetValidBindings() {
		if(CheckCondition())
		{
			ArrayList bindings = new ArrayList();
			bindings.add(new SubstitutionSet());
			return bindings;
		}
		else return null;
	}
	
	public ArrayList GetValueBindings()
	{
		if(CheckCondition()) {
			return new ArrayList();
		}
		else return null;
	}
	
	public boolean isGrounded()
	{
		return true;
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
		MoodCondition aux = (MoodCondition) this.clone();
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
		MoodCondition aux = (MoodCondition) this.clone();
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
		MoodCondition aux = (MoodCondition) this.clone();
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
    }
	
	/**
	 * Clones this EmotionCondition, returning an equal copy.
	 * If this clone is changed afterwards, the original object remains the same.
	 * @return The EmotionCondition's copy.
	 */
	public Object clone()
	{
		MoodCondition mc = new MoodCondition();
		mc._operator = this._operator;
		mc._value = this._value;
		mc._name = (Name) this._name.clone();
		
		return mc;
	}
}