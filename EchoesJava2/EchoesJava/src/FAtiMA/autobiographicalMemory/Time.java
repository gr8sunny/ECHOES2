/** 
 * Time.java - 
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
 * Created: 18/Jul/2006 
 * @author: João Dias
 * Email to: joao.assis@tagus.ist.utl.pt
 * 
 * History: 
 * João Dias: 18/Jul/2006 - File created
 * **/

package FAtiMA.autobiographicalMemory;

import java.io.Serializable;

import FAtiMA.AgentSimulationTime;


public class Time implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static int _eventCounter = 1;
	
	private long _narrativeTime;
	private long _realTime;
	private int _eventSequence;
	
	public Time()
	{
		this._narrativeTime = AgentSimulationTime.GetInstance().Time();
		this._realTime = System.currentTimeMillis();
		this._eventSequence = _eventCounter;
		_eventCounter++;
	}
	
	public long getNarrativeTime()
	{
		return this._narrativeTime;
	}
	
	public long getRealTime()
	{
		return this._realTime;
	}
	
	public long getEventSequence()
	{
		return this._eventSequence;
	}
	
	public long getElapsedNarrativeTime()
	{
		long currentTime = AgentSimulationTime.GetInstance().Time();
		return currentTime - this._narrativeTime;
	}
	
	public long getElapsedRealTime()
	{
		long currentTime = System.currentTimeMillis();
		return currentTime - this._realTime;
	}
	
	public long getElapsedEvents()
	{
		return _eventCounter - this._eventSequence - 1;
	}
	
	public String toString()
	{
		return "(RT) " + _realTime + "\n(NT) " + _narrativeTime + "\n(ES) " + _eventSequence; 
	}
}
