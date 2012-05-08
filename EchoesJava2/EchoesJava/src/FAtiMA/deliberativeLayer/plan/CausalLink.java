/** 
 * CausalLink.java - Represents a plan's causal link A -p-> B 
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
 * João Dias: 03/05/2006 - A Causal Link now refers a condition P instead of an effect P.
 *                         added probability to Causal Links
 * João Dias: 16/05/2006 - Class Restructured. Instead of storing direct references to
 * 						   effects and conditions, we now store only numeric identifiers.
 * João Dias: 22/05/2006 - Added comments to each public method's header
 * João Dias: 10/07/2006 - the class is now serializable
 * João Dias: 02/10/2006 - Removed the attribute probability, because this value cannot be
 * 						   static and stored here anymore, it changes as the effect's probability change
 * João Dias: 02/10/2006 - Added the attribute effect that stores the ID of the effect. This id
 * 						   id can be used to retrieve the appropriate effect and thus determine
 * 						   the correct link's probability
 */

package FAtiMA.deliberativeLayer.plan;

import java.io.Serializable;

/**
 * Represents a Causal Link between two Steps A and B. The causal link A --p--> B 
 * means that A achieves the precondition P for B.
 * 
 * @author João Dias
 */

public class CausalLink implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected Integer source;
    protected Integer destination;
    protected Integer condition;
    protected Integer effect;
    
    protected String description;
    protected OrderingConstraint orderConstraint;

    /**
     * Creates a new Causal Link A --p--> B
     * @param source - the ID of the Step that provides the effect (ID of Step A)
     * @param effect - the ID of the source's effect that establishes the link 
     * 				   (ID of p in A)
     * @param destination - the ID of the Step that needs the effect (ID of Step B)
     * @param condition - the ID of the destination's precondition that is established
     * 				      by the link (ID of p in B)
     * @param description - a string that displays the condition p,
     * 					    usefull for output purpouses
     */
    public CausalLink(Integer source, Integer effect, Integer destination, Integer condition, String description)
    {
    	this.effect = effect;
        this.condition = condition;
        this.destination = destination;
        this.source = source;
        this.description = description;
        this.orderConstraint = new OrderingConstraint(source, destination);
    }

    /**
     * Creates a new Causal Link A --p--> B
     * @param source - the ID of the Step that provides the effect (ID of Step A)
     * @param effect - the ID of the source's effect that establishes the link 
     * 				   (ID of p in A)
     * @param destination - the ID of the Step that needs the effect (ID of Step B)
     * @param condition - the ID of the destination's precondition that is established
     * 				      by the link (ID of p in B)
     * @param probability - the probability of the causal link succedding, corresponds to the
     * 		                probality of p in A
     */
    public CausalLink(Integer source, Integer effect, Integer destination, Integer condition)
    {
    	this.effect = effect;
        this.condition = condition;
        this.destination = destination;
        this.source = source;
        this.description = condition.toString();
        this.orderConstraint = new OrderingConstraint(source, destination);
    }

    /**
     * Gets the step A in the Causal Link A --p--> B
     * @return the ID of the Step A in the plan
     */
    public Integer getSource()
    {
        return this.source;
    }
    
    /**
     * Gets Effect p in the Causal Link A --p--> B
     * @return The ID of Effect p in step A
     */
    public Integer getEffect()
    {
    	return this.effect;
    }

    /**
     * Gets the step B in the Causal Link A --p--> B
     * @return the ID of the Step B in the plan
     */
    public Integer getDestination()
    {
        return this.destination;
    }

    /**
     * Gets Condition p in the Causal Link A --p--> B
     * @return The ID of condition p in step B
     */
    public Integer getCondition()
    {
        return this.condition;
    }
    
    /**
     * Gets the description of the link
     */
    public String getDescription() {
    	return description;
    }
    
    /**
     * Sets the condition p in the Causal Link A --p--> B
     * @param condition - The ID of condition p in step B
     */
    public void setCondition(Integer condition)
    {
        this.condition = condition;
    }

 
    /**
     * Gets the ordering constraint A < B that is implicit in the Causal Link A --p--> B
     * @return the OrderingConstraint A < B
     */
    public OrderingConstraint getOrderConstraint()
    {
        return this.orderConstraint;
    }

    /**
     * Converts the causal link to a String
     * @return the converted String
     */
    public String toString()
    {
        return this.source + " --" + this.description + "--> " + this.destination;
    }
}
