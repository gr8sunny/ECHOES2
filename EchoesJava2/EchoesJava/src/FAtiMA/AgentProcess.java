/** 
 * AgentProcess.java - Abstract Class that represents an agent's reasoning process,
 * which can be a reactive or deliberative process.
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
 * Created: 21/12/2004 
 * @author: Jo�o Dias
 * Email to: joao.assis@tagus.ist.utl.pt
 * 
 * History: 
 * Jo�o Dias: 21/12/2004 - File created
 * Jo�o Dias: 25/05/2006 - Added comments to each public method's header
 * Jo�o Dias: 10/07/2006 - The class is now Serializable
 * Jo�o Dias: 15/07/2006 - Removed the EmotionalState from the Class fields since the 
 * 						   EmotionalState is now a singleton that can be used anywhere 
 * 					       without previous references.
 * Jo�o Dias: 20/09/2006 - Removed the method RemoveSelectedAction. The method
 * 						   GetSelectedMethod now additionally has the functionality 
 * 						   of the RemoveSelectedAction method
 */

package FAtiMA;

import java.io.Serializable;
import java.util.ArrayList;

import FAtiMA.emotionalState.EmotionalState;
import FAtiMA.sensorEffector.Event;


/**
 * Abstract Class that represents an agent's reasoning layer,
 * which can be a reactive or deliberative process.
 * 
 * @author Jo�o Dias
 */
public abstract class AgentProcess implements Serializable {

	protected ArrayList _eventPool;
	protected String _self;
	
	protected AgentProcess()
	{
	}
	
	/**
	 * Creates a new AgentProcess. Not used directly since its an 
	 * abstract class
	 * @param name - the agent's name
	 * @param emS - the agent's EmotionalState
	 * 
	 * @see EmotionalState
	 */
	public AgentProcess(String name) {
		_self = name;
		_eventPool = new ArrayList();
	}
	
	/**
	 * Adds an event to the layer so that it can be appraised
	 * @param event - the event to be appraised
	 */
	public void AddEvent(Event event) {
		synchronized (_eventPool) {
			_eventPool.add(event);
		}
	}
	
	/**
	 * Determines an answer to a SpeechAct according to specific layer
	 * @return the best answer to give according to the layer
	 */
	//public abstract ValuedAction AnswerToSpeechAct(SpeechAct speechAct);
	
	/**
	 * runs one round of the Appraisal process. 
	 */
	public abstract void Appraisal();
	
	/**
	 * runs one round of the Coping process
	 */
	public abstract void Coping();
	
	/**
	 * Gets the action selected in the coping cycle, if any.
	 * @return the action selected for execution, or null 
	 * 	       if no such action exists 
	 */
	public abstract ValuedAction GetSelectedAction();
	
	/**
	 * Resets the layer.
	 */
	public abstract void Reset();
	
	/**
	 * Prepares the layer or process for a shutdown
	 *
	 */
	public abstract void ShutDown();
	
	/*public void Start() {	
		while (true) {
			try {
				if (_remoteAgent.isRunning()) {
					Appraisal();
					Coping();
				}
				Thread.sleep(_sleepInterval);
			}
			catch (Exception ex) {
				return;
			}
		}
	}*/
}
