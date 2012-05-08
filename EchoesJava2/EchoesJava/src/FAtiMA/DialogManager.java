/** 
 * DialogManager.java - Manages dialog and conversation between agents
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
 * João Dias: 28/09/2006 - Support speechActs do not update the SpeechContext
 * João Dias: 30/07/2007 - Added decay for context. After a predifined ammount of time
 * 						   where noone says anything the current speech context disappears
 */

package FAtiMA;

import java.io.Serializable;

import FAtiMA.knowledgeBase.KnowledgeBase;
import FAtiMA.sensorEffector.Event;
import FAtiMA.sensorEffector.SpeechAct;
import FAtiMA.wellFormedNames.Name;


/**
 * @author User
 *
 */
public class DialogManager implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final Name SPEECH_CONTEXT = Name.ParseName("SpeechContext()");
	private static final long CONTEXT_DURATION = 30000;
	
	private long _contextExpireTime;
	private boolean _canSpeak;
	private boolean _andCounting;
	
	public DialogManager()
	{
		this._canSpeak = true;
		this._contextExpireTime = 0;
		this._andCounting = false;
	}
	
	public void SpeechStarted()
	{
		this._canSpeak = false;
	}
	
	public void UpdateDialogState(Event speechEvent)
	{
		if(!speechEvent.GetAction().equals(SpeechAct.Reinforce))
		{
			KnowledgeBase.GetInstance().Tell(SPEECH_CONTEXT, speechEvent.toName().toString());
		}
		this._canSpeak = true;
		this._contextExpireTime = AgentSimulationTime.GetInstance().Time() + CONTEXT_DURATION;
		this._andCounting = true;
	}
	
	public boolean CanSpeak()
	{
		return this._canSpeak;
	}
	
	public void Reset()
	{
		this._canSpeak = true;
	}
	
	public void DecayCauseIDontHaveABetterName()
	{
		if(_andCounting)
		{
			if(AgentSimulationTime.GetInstance().Time() > this._contextExpireTime)
			{
				KnowledgeBase.GetInstance().Tell(SPEECH_CONTEXT, "");
				this._contextExpireTime = 0;
				this._andCounting = false;
			}
		}
	}

}
