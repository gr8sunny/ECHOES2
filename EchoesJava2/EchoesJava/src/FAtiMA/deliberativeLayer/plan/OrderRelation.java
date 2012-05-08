/**
 * OrderRelation.java - Represents a relation of order between a step and other steps in a Plan
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
 * Created: 16/Mai/2006
 * @author: João Dias
 * Email to: joao.assis@tagus.ist.utl.pt
 * 
 * History: 
 * João Dias: 16/Mai/2006 - File created
 * João Dias: 17/05/2006 - Added Clone Method
 * João Dias: 22/05/2006 - Added comments to each public method's header
 * João Dias: 10/07/2006 - the class is now serializable 
 */
package FAtiMA.deliberativeLayer.plan;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents an order relation between a step and other steps in the plan
 * 
 * @author João Dias
 */
public class OrderRelation implements Cloneable, Serializable
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer _stepID;
    private ArrayList _after;
    private ArrayList _before;

    /**
     * Creates a new empty OrderRelation between a step and other steps in the plan 
     * @param stepID - the ID of the main Step referenced by this OrderRelation
     */
    public OrderRelation(Integer stepID)
    {
        this._stepID = stepID;
        this._after = new ArrayList();
        this._before = new ArrayList();
    }

    private OrderRelation()
    {
    }

    /**
     * Gets the ID of the main step in the order relation
     */
    public Integer getStepID()
    {
        return this._stepID;
    }

    /**
     * Gets the id's of all steps that occur after the main step
     * @return the id's stored in a arraylist
     */
    public ArrayList getAfter()
    {
        return this._after;
    }

    /**
     * Gets the id's of all steps that occur before the main step
     * @return the id's stored in a arraylist
     */
    public ArrayList getBefore()
    {
        return this._before;
    }

    /**
     * Compares a given step with the main step in the Order Relation
     * if the received step occurs before, the function returns +1. -1 if
     * the received step occurs after, and 0 if no order relation is defined
     * 
     * @param stepID - the ID of the step to compare
     */
    public int Compare(Integer stepID)
    {
        if(this._before.contains(stepID)) return +1;
        if(this._after.contains(stepID)) return -1;
        return 0;
    }

    /**
     * Asserts that the received Step must occur after this step
     * @param stepID - the ID of the step to insert after
     */
    public void InsertAfter(Integer stepID)
    {
       this._after.add(stepID);
    }

    /**
     * Asserts that the received Step must occur before this step
     * @param stepID - the ID of the step to insert before
     */
    public void InsertBefore(Integer stepID)
    {
        this._before.add(stepID);
    }
    
    /**
	 * Clones this OrderRelation, returning an equal copy.
	 * If this clone is changed afterwards, the original object remains the same.
	 * @return The OrderRelations's copy.
	 */
    public Object clone()
    {
        OrderRelation order = new OrderRelation();
        order._stepID = this._stepID;
        order._after = (ArrayList) this._after.clone();
        order._before = (ArrayList) this._before.clone();

        return order;
    }
}
