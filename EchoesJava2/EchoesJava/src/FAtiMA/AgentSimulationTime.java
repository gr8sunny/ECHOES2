/** 
 * AgentSimulationTime.java - Class that implements the simulation time experienced
 * 							  by the agent. It allows the agent to freeze its time, 
 * 							  to advance time, and to speed up time
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
 * Created: 2/Jul/2006 
 * @author: João Dias
 * Email to: joao.assis@tagus.ist.utl.pt
 * 
 * History: 
 * João Dias: 2/Jul/2006 - File created
 * João Dias: 10/07/2006 - the class is now serializable
 * João Dias: 17/07/2006 - added the SaveState and LoadState methods
 * João Dias: 29/08/2006 - Solved small bug in LoadState method
 */
package FAtiMA;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * @author João Dias
 * Class that implements the simulation time experienced
 * by the agent. It allows the agent to freeze its time, 
 * to advance time, and to speed up time. You cannot create
 * an AgentSimulationTime since there is one and only instance for the agent. 
 * If you want to access it use AgentSimulationTime.GetInstance() method.
 */
public class AgentSimulationTime implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Singleton pattern 
	 */
	private static AgentSimulationTime _timerInstance = null;
	
	/**
	 * Gets a timer responsible for updating and controlling
	 * this agent's simulation time
	 * 
	 * @return an AgentSimulationTime timer
	 */
	public static AgentSimulationTime GetInstance()
	{
		if(_timerInstance == null)
		{
			_timerInstance = new AgentSimulationTime();
		}
		return _timerInstance;
	}
	
	/**
	 * Saves the state of the current AgentSimulationTimer to a file,
	 * so that it can be later restored from file
	 * @param fileName - the name of the file where we must write
	 * 		             the state of the timer
	 */
	public static void SaveState(String fileName)
	{
		try 
		{
			FileOutputStream out = new FileOutputStream(fileName);
	    	ObjectOutputStream s = new ObjectOutputStream(out);
	    	
	    	s.writeObject(_timerInstance);
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
	 * Loads a specific state of the AgentSimulationTimer from a previously
	 * saved file
	 * @param fileName - the name of the file that contains the stored
	 * 					 timer
	 */
	public static void LoadState(String fileName)
	{
		try
		{
			FileInputStream in = new FileInputStream(fileName);
        	ObjectInputStream s = new ObjectInputStream(in);
        	_timerInstance = (AgentSimulationTime) s.readObject();
        	_timerInstance.Resume();
        	
        	s.close();
        	in.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private long _simulationTime;
	private float _timeMultiplier;
	
	private long _lastUpdateTime;
	private boolean _running;
	
	/**
	 * Creates a new timer for the Agent's simulation
	 *
	 */
	private AgentSimulationTime()
	{
		//the agent is born, the agent time is 0
		this._simulationTime = 0;
		this._timeMultiplier = 1;
		this._running = true;
		
		this._lastUpdateTime = System.currentTimeMillis();
	}
	
	/**
	 * Stops the agent's timer. While the timer is stopped, no
	 * time elapses for the agent, even though real time is 
	 * elapsing
	 *
	 */
	public void Stop()
	{
		this._running = false;
	}
	
	/**
	 * Resumes the agent's timer. Time will now elapse for the agent
	 */
	public void Resume()
	{
		this._lastUpdateTime = System.currentTimeMillis();
		this._running = true;
	}
	
	/**
	 * Tick Tack. Updates the agent's timer according to the 
	 * current system's time. You should to call this method in
	 * each simulation round so that time is propertly updated.
	 */
	public void Tick()
	{
		long currentTime = System.currentTimeMillis();
		long elapsedRealTime = currentTime - this._lastUpdateTime;
		
		this._lastUpdateTime = currentTime;
		if(this._running)
		{
			this._simulationTime += elapsedRealTime * this._timeMultiplier;
		}
	}
	
	/**
	 * Advances the agent's simulation time. Very usefull if you
	 * want to skip time. 
	 * @param seconds - the number of seconds you want to advance
	 *  				in time
	 */
	public void AdvanceTime(int seconds)
	{
		this._simulationTime += 1000*seconds;
	}
	
	/**
	 * Speeds up the agent simulation time in relation to real time
	 * @param speed - how many times faster should the simulation time
	 * 				  run in relation to real time. For instance, if 
	 * 				  speed is 7, one second of real time will seem like 
	 * 				  7 seconds to the agent 
	 * 				  The value provided must be greater than 1
	 */
	public void SpeedUpTimeTo(int speed) 
	{
		if(speed > 1)
		{
			this._timeMultiplier = speed;
		}
	}
	
	/**
	 * Slows down the agent simulation time in relation to real time
	 * @param speed - how many times slower should the simulation time
	 * 			      run in relation to real time. For instance, if speed
	 * 				  is 5, 10 second of real time will correspond to only 2
	 * 				  for the agent.
	 * 				  The value provided must be greater than 1
	 */
	public void SlowDownTimeTo(int speed)
	{
		if(speed > 1)
		{
			this._timeMultiplier = 1/speed;
		}
	}
	
	/**
	 * Sets the agent simulation time to real time.
	 * One second of real time, will correspond to exactly
	 * one second for the agent
	 */
	public void SetNormalTime()
	{	
		this._timeMultiplier = 1;
	}
	
	/**
	 * Gets the agent's simulation time. 
	 * @return the agent's simulation time
	 */
	public long Time() 
	{
		return this._simulationTime;
	}
}
