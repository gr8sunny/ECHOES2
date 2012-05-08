/** 
 * EpisodicMemory.java - 
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
 * João Dias: 06/09/2006 - changed the way that InterpersonalRelations are determined
 * 						   and stored in memory. Now the like relation can be retrieved
 * 						   directly from semantic memory. So I removed the methods related
 * 						   to InterpersonalRelations from this class
 * João Dias: 09/01/2007 - the field location in the AM is now being created properly when 
 * 						   new information is stored
 * João Dias: 16/02/2007 - the method SummarizeLastEvent now returns an XML description of the summary
 * 						   ready to be used by the LanguageEngine
 * João Dias: 22/02/2007 - the Autobiographical memory now registers if new data was added to it and 
 * 						   provides a method that verifies if new data was added since last time it
 * 						   was verified
 * **/

package FAtiMA.autobiographicalMemory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.ListIterator;

import FAtiMA.AgentSimulationTime;
import FAtiMA.conditions.EventCondition;
import FAtiMA.emotionalState.ActiveEmotion;
import FAtiMA.knowledgeBase.KnowledgeBase;
import FAtiMA.sensorEffector.Event;
import FAtiMA.wellFormedNames.Name;


public class AutobiographicalMemory implements Serializable {
	
	/**
	 * for serialization purposes
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Singleton pattern 
	 */
	private static AutobiographicalMemory _memoryInstance;
	
	public static AutobiographicalMemory GetInstance()
	{
		if(_memoryInstance == null)
		{
			_memoryInstance = new AutobiographicalMemory();
		}
		
		return _memoryInstance;
	} 
	
	/**
	 * Saves the state of the AutobiographicalMemory to a file,
	 * so that it can be later restored from file
	 * @param fileName - the name of the file where we must write
	 * 		             the AutobiographicalMemory
	 */
	public static void SaveState(String fileName)
	{
		try 
		{
			FileOutputStream out = new FileOutputStream(fileName);
	    	ObjectOutputStream s = new ObjectOutputStream(out);
	    	
	    	s.writeObject(_memoryInstance);
        	s.flush();
        	s.close();
        	out.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Loads a specific state of the AutobiographicalMemory from a 
	 * previously saved file
	 * @param fileName - the name of the file that contains the stored
	 * 					 AutobiographicalMemory
	 */
	public static void LoadState(String fileName)
	{
		try
		{
			FileInputStream in = new FileInputStream(fileName);
        	ObjectInputStream s = new ObjectInputStream(in);
        	_memoryInstance = (AutobiographicalMemory) s.readObject();
        	s.close();
        	in.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private String _self;
	private ArrayList _memoryEvents;
	private boolean _newData;
	
	private AutobiographicalMemory()
	{
		this._memoryEvents = new ArrayList();
		this._newData = false;
	}
	
	public void setSelf(String selfName)
	{
		this._self = selfName;
	}
	
	public String getSelf()
	{
		return this._self; 
	}
	
	public void StoreAction(Event e)
	{
		MemoryEpisode event;
		String oldLocation;
		Name locationKey = Name.ParseName(_self + "(location)");
		
		
		String newLocation = (String) KnowledgeBase.GetInstance().AskProperty(locationKey);
		
		synchronized (this) {
			if(this._memoryEvents.size() == 0)
			{
				event = new MemoryEpisode(newLocation);
				this._memoryEvents.add(event);
			}
			else
			{
				event = (MemoryEpisode) this._memoryEvents.get(this._memoryEvents.size()-1);
				oldLocation = event.getLocation();
				if(oldLocation == null) {
					event.setLocation(newLocation);
				}
				else if(!event.getLocation().equals(newLocation) ||
						(AgentSimulationTime.GetInstance().Time() - event.getTime().getNarrativeTime()) > 900000)
				{
					event = new MemoryEpisode(newLocation);
					this._memoryEvents.add(event);
				}
			}	
			event.AddActionDetail(e);
			
			this._newData = true;
		}
	}
	
	public void AssociateEmotionToAction(ActiveEmotion em, Event cause)
	{
		if(this._memoryEvents.size() > 0)
		{
			synchronized (this)
			{
				MemoryEpisode event = (MemoryEpisode) this._memoryEvents.get(this._memoryEvents.size()-1);
				event.AssociateEmotionToDetail(em,cause);
			}
		}
	}
	
	/**
	 * This methods verifies if any new data was added to the AutobiographicalMemory since
	 * the last time this method was called. 
	 * @return
	 */
	public boolean HasNewData()
	{
		boolean aux = this._newData;
		this._newData = false;
		return aux;
	}
	
	public Object GetSyncRoot()
	{
		return this;
	}
	
	public ArrayList GetAllEpisodes()
	{
		return this._memoryEvents;
	}
	
	public int countMemoryDetails()
	{
		int aux = 0;
		MemoryEpisode episode;
		
		synchronized(this)
		{
			ListIterator li = this._memoryEvents.listIterator();
			while(li.hasNext())
			{
				episode = (MemoryEpisode) li.next();
				aux += episode.getDetails().size();
			}
			return aux;
		}	
	}
	
	public ArrayList SearchForRecentEvents(ArrayList searchKeys)
	{
		MemoryEpisode currentEpisode;
		
		synchronized (this) {
			if(this._memoryEvents.size() > 0)
			{
				currentEpisode = (MemoryEpisode) this._memoryEvents.get(this._memoryEvents.size()-1);
				return currentEpisode.GetDetailsByKeys(searchKeys);
			}
			return new ArrayList();
		}
	}
	
	public ArrayList SearchForPastEvents(ArrayList keys) 
	{
		MemoryEpisode episode;
		ArrayList details;
		ActionDetail action;
		ListIterator li;
		
		synchronized(this)
		{
			ArrayList foundPastEvents = new ArrayList();
			if(this._memoryEvents.size() > 1)
			{
				for(int i=0; i<this._memoryEvents.size()-1; i++)
				{
					episode = (MemoryEpisode) this._memoryEvents.get(i);
					details = episode.GetDetailsByKeys(keys);
					li = details.listIterator();
					while(li.hasNext())
					{
						action = (ActionDetail) li.next();
						if(!foundPastEvents.contains(action))
						{
							foundPastEvents.add(action);
						}
					}
				}
			}
			
			return foundPastEvents;
		}
	}
	
	public boolean ContainsRecentEvent(EventCondition event)
	{
		MemoryEpisode currentEpisode;
		
		synchronized (this) {
			if(this._memoryEvents.size() > 0)
			{
				currentEpisode = (MemoryEpisode) this._memoryEvents.get(this._memoryEvents.size()-1);
				
				return currentEpisode.VerifiesKeys(event.GetSearchKeys());
			}
			return false;
		}
	}
	
	public boolean ContainsPastEvent(EventCondition event)
	{
		synchronized (this) {
			if(this._memoryEvents.size() > 1)
			{
				ArrayList keys = event.GetSearchKeys();
				
				for(int i=0; i<this._memoryEvents.size()-1; i++)
				{
					if(((MemoryEpisode)this._memoryEvents.get(i)).VerifiesKeys(keys))
					{
						return true;
					}
				}
			}
			return false;
		}
	}
	
	public String SummarizeLastEvent()
	{	
		
		String AMSummary = "";
		
		System.out.println("Number of Events in AM: " + this._memoryEvents.size());
		
		if(this._memoryEvents.size() > 1)
		{
			MemoryEpisode episode = (MemoryEpisode) this._memoryEvents.get(this._memoryEvents.size() - 2);
			long elapsedTime = episode.getTime().getElapsedRealTime();
			float avgEmotion = episode.determineEmotionAverage();
			float stdDev = episode.determineEmotionStdDeviation();
			System.out.println("Preparing Summary Generation...");
			System.out.println("Time elapsed since episode happened: " + elapsedTime);
			System.out.println("Average of the episode's emotions: " + avgEmotion);
			System.out.println("Std deviation of the episode's emotions: " + stdDev);
			//if an hour passed since the user seen the last episode, we should report a summary
			//we should also report if the episode is ambiguous, i.e if it is not very far from a neutral average
			//if(elapsedTime >= 36000000 || stdDev >= 2.5)
			//{
				AMSummary += episode.GenerateSummary();
			//}
		}
		
		return AMSummary;
	}
	
	public String toXML()
	{
		String am  = "<AutobiographicMemory>";
		for(ListIterator li = this._memoryEvents.listIterator();li.hasNext();)
		{
			MemoryEpisode episode = (MemoryEpisode) li.next();
			am += episode.toXML();
		}
		am += "</AutobiographicMemory>";
		return am; 
	}
	
	/*public ArrayList Reconstruct(ArrayList searchKeys)
	{
		ArrayList reconstruction = new ArrayList();
		MemoryEpisode memoryEvent;
		
		synchronized(this)
		{
			ListIterator li = this._memoryEvents.listIterator();
			
			while(li.hasNext())
			{
				memoryEvent = (MemoryEpisode) li.next();
				if(memoryEvent.VerifiesKeys(searchKeys))
				{
					reconstruction.add(memoryEvent);
				}
			}
			
			return reconstruction;
		}
	}*/
	
	/*public ArrayList Reconstruct(SearchKey k)
	{
		ArrayList reconstruction = new ArrayList();
		MemoryEpisode memoryEvent;
		
		synchronized(this)
		{
			ListIterator li = this._memoryEvents.listIterator();
			
			while(li.hasNext())
			{
				memoryEvent = (MemoryEpisode) li.next();
				if(memoryEvent.VerifiesKey(k))
				{
					reconstruction.add(memoryEvent);
				}
			}
			
			return reconstruction;
		}
	}*/
}
