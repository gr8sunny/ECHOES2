/** 
 * EmotionalReactionTreeNode.java - Node in the emotional reaction tree, that is matched
 * against incoming events
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
 * Created: 09/12/2004 
 * @author: João Dias
 * Email to: joao.assis@tagus.ist.utl.pt
 * 
 * History: 
 * João Dias: 09/12/2004 - File created
 * João Dias: 23/05/2006 - Added comments to each public method's header
 * João Dias: 23/05/2006 - Removed the property value from the class (was not being used)
 * 						   and changed class constructor accordingly
 * João Dias: 10/07/2006 - the class is now serializable
 * João Dias: 28/09/2006 - There were problems when defining two or more emotional reactions
 * 						   with the same Subject, Action and Target but different parameters, 
 * 						   and only the last reaction would be stored. Now I extended the 
 * 						   reaction tree so that you can have emotional reactions that are
 * 						   only different in some of the parameters. However it only works with
 * 						   no more than 3 parameters for each reaction.
 * João Dias: 09/04/2007 - Solved a bug caused by the use of == when comparing node types. Although this
 * 						   worked normally, after serialization this test stops working because the strings
 * 						   correspond to different instances. Replaced this test by string.equals
 */

package FAtiMA.reactiveLayer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import FAtiMA.IIntegrityTester;
import FAtiMA.IntegrityValidator;
import FAtiMA.exceptions.UnknownSpeechActException;
import FAtiMA.sensorEffector.Event;
import FAtiMA.sensorEffector.Parameter;


/**
 * Represents a Node in the emotional reaction tree, that is matched
 * against incoming events. This class implements a very fast mechanism
 * of finding emotional reaction rules that match with a given event.
 * 
 * @author João Dias
 */

public class EmotionalReactionTreeNode implements IIntegrityTester, Serializable {
    
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static String actionNode = "action";
	public static String subjectNode = "subject";
	public static String targetNode = "target";
	public static String param1Node = "p1";
	public static String param2Node = "p2";
	public static String param3Node = "p3";
	private static String nullValue = "null";
	
	private HashMap _childs;

	private String _type;
	
	/**
	 * Creates a new EmotionalReactionTreeNode
	 * @param type - the type of the node: action,subject,target,null
	 * @param value - not used for anything
	 * 
	 * @deprecated do not use this constructor anymore, use
	 * EmotionalReactionTreeNode(String type) instead 
	 */
	public EmotionalReactionTreeNode(String type, String value) {
		_childs = new HashMap();
		_type = type;
	}
	
	/**
	* Creates a new EmotionalReactionTreeNode
	 * @param type - the type of the node: action,subject,target,null
	 */
	public EmotionalReactionTreeNode(String type)
	{
	    _childs = new HashMap();
	    _type = type;
	}
	
	/**
	 * Adds an Emotional Reaction rule bellow this node 
	 * @param er - the Reaction to add
	 */
	public void AddEmotionalReaction(Reaction er) {
		EmotionalReactionTreeNode child;
		ArrayList arguments;
		String key=null;
		String nextNodeType=null;
		
		if(_type.equals(param3Node))
		{
			arguments = er.getEvent().GetParameters();
			if(arguments != null & arguments.size() > 2)
			{
				key = (String)((Parameter) arguments.get(2)).GetValue();
			}
			if(key == null || key.equals("*")) key = nullValue;
			_childs.put(key,er);
			return;
		}
		else if(_type.equals(param2Node))
		{
			arguments = er.getEvent().GetParameters();
			if(arguments != null & arguments.size() > 1)
			{
				key = (String)((Parameter) arguments.get(1)).GetValue();
			}
			nextNodeType = param3Node;
		}
		else if(_type.equals(param1Node))
		{
			arguments = er.getEvent().GetParameters();
			if(arguments != null & arguments.size() > 0)
			{
				key = (String)((Parameter) arguments.get(0)).GetValue();
			}
			nextNodeType = param2Node;
		}
		if(_type.equals(targetNode)) {
			key = er.getEvent().GetTarget();
			nextNodeType = param1Node;
		}
		else if(_type.equals(actionNode)) {
			key = er.getEvent().GetAction();
			nextNodeType = targetNode;
		}
		else if(_type.equals(subjectNode)) {
			key = er.getEvent().GetSubject();
			nextNodeType = actionNode;
		}
		
		if(key == null || key.equals("*")) key = EmotionalReactionTreeNode.nullValue;
			
		if(_childs.containsKey(key)) {
			child = (EmotionalReactionTreeNode) _childs.get(key);
		}
		else {
			child = new EmotionalReactionTreeNode(nextNodeType);
			_childs.put(key,child);
		}
			
		child.AddEmotionalReaction(er);	
	}
	
	/**
	 * Checks the integrity of all emotional Reaction rules stored bellow the node,
	 * testing if one of them refers a SpeechAct not defined. In that case, it throws
	 * an exception
	 */
	public void CheckIntegrity(IntegrityValidator val) throws UnknownSpeechActException {
	    Iterator it = _childs.values().iterator();
	    
	    while(it.hasNext()) {
	        ((IIntegrityTester) it.next()).CheckIntegrity(val);
	    }
	}
	
	/**
	 * Tries to match a given event with all Reaction rules stored under this node.
	 * @param e - the event o match againts the reaction rules
	 * @return the appropriate reaction rule if a match is found, null otherwise
	 */
	public Reaction MatchEvent(Event e) {
		String key=null;
		ArrayList parameters;
		Object obj;
		Reaction r;
		
		if(_type.equals(subjectNode)) 
		{
			key = e.GetSubject();
		}
		else if(_type.equals(actionNode)) 
		{
			key = e.GetAction();
		}
		else if(_type.equals(targetNode)) 
		{
			key = e.GetTarget();
		}
		else if(_type.equals(param1Node))
		{
			parameters = e.GetParameters();
			if(parameters != null && parameters.size()>0)
			{
				key = (String)((Parameter) parameters.get(0)).GetValue();
			}
		}
		else if(_type.equals(param2Node))
		{
			parameters = e.GetParameters();
			if(parameters != null && parameters.size()>1)
			{
				key = (String)((Parameter) parameters.get(1)).GetValue();
			}
		}
		else if(_type.equals(param3Node))
		{
			parameters = e.GetParameters();
			if(parameters != null && parameters.size()>2)
			{
				key = (String)((Parameter) parameters.get(2)).GetValue();
			}
		}
		
		if(key != null) {		
			if(_childs.containsKey(key)) {
				obj = _childs.get(key);
				r = getReaction(e,obj);
				if(r != null) return r;
				else obj = _childs.get(nullValue); 
			}
			else {
				key = nullValue;
				obj = _childs.get(nullValue);
			}
		}
		else
		{
			obj = _childs.get(nullValue);
		}
			
		
		if(obj == null) return null;
		else return getReaction(e,obj);   
	}
	
	/**
	 * Converts the node to a String
	 * @return the converted String
	 */
	public String toString() {
		return  _type + ":" + _childs;
	}
	
	private Reaction getReaction(Event e, Object obj) {
		if(_type.equals(param3Node)) {
		    return (Reaction) obj;
		}
		else return ((EmotionalReactionTreeNode) obj).MatchEvent(e);
	}
}