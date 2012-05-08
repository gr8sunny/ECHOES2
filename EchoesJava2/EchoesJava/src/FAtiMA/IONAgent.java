/** 
 * IONAgent.java - Represents the FAtiMA agent, but prepared to be loaded by 
 * a story facilitator in the ION Framework. This class agregates all 
 * the agent's architecture componnents, and its responsible for initiallizing
 * and defining the communication ports between them. Contains the Main method.
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
 * Created: 07/12/2006 
 * @author: João Dias
 * Email to: joao.dias@inesc-id.pt
 * 
 * History: 
 * João Dias: 07/12/2006 - File created from the version of the Agent in FAtiMA-ION
 * 
 * Differences between IONAgent and Agent:
 * - The main method now longer receives Properties and Goals. The agent is created
 * 	 with an empty GoalList that must be filled by external messages from the 
 * 	 Story facilitator
 * - The agent now receives one initial argument when is created, the save directory.
 * 	 This arguments specifies in which directory should the agent save its state if
 * 	 necessary.
 * - Changed the arguments order 
 * - Minor changes to the Load and Save Methods
 * - The goals defined in the character's personality are no longer initially
 * 	 loaded when the agent is created.
 * - The IONAgent class uses an IONRemoteAgent to communicate with the virtual world
 *   (ION Framework in this case), while the Agent class uses a RemoteAgent
 * - An IONAgent can be loaded directly from a file when it is initially created by calling
 * 	 the main function with 4 arguments: the name of the virtual world server, the server's port,
 * 	 the directory where the agent's files are stored and the name of the agent to load.
 *
 * João Dias: 22/02/2007 - Added the field eventsPerceived that contains the external events perceived by the agent in the
 * 						   last cycle. Changed the methods Save, Load, PerceiveEvent and Run accordingly
 * João Dias: 20/07/2007 - Removed the LanguageEngine from the agent. The LE is now used at the framework level.
 * 		 
 */

package FAtiMA;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import FAtiMA.Display.AgentDisplay;
import FAtiMA.autobiographicalMemory.AutobiographicalMemory;
import FAtiMA.deliberativeLayer.DeliberativeProcess;
import FAtiMA.deliberativeLayer.EmotionalPlanner;
import FAtiMA.deliberativeLayer.goals.GoalLibrary;
import FAtiMA.emotionalState.EmotionalState;
import FAtiMA.knowledgeBase.KnowledgeBase;
import FAtiMA.reactiveLayer.ReactiveProcess;
import FAtiMA.sensorEffector.IONRemoteAgent;
import FAtiMA.sensorEffector.SpeechAct;
import FAtiMA.util.enumerables.EmotionType;
import FAtiMA.util.parsers.AgentLoaderHandler;

/**
 * This class will correspond to an agent in the virtual world. 
 * Agregates all the agent architecture componnents, and is 
 * responsible for initiallizing and defining the communication ports
 * between them. Contains the Main method.
 * 
 * @author João Dias
 */
public class IONAgent extends Agent {

    /**
     * The main method
     */
	static public void main(String args[]) throws Exception {
		
		if(args.length == 10)
		{
			new IONAgent(args[0], Integer.parseInt(args[1]), args[2], args[3], args[4], args[5], args[6], args[7], args[8], Boolean.parseBoolean(args[9]));
		}
		else if(args.length == 4)
		{
			new IONAgent(args[0], Integer.parseInt(args[1]), args[2], args[3]);
		}
		else
		{
			System.out.println("Wrong number of arguments!");
		}
	}
	
	private String _saveDirectory;
	//private String _userLanguageFile;
	//private String _languageActsFile;

	/**
	 * Creates a new Agent
	 * @param host - the host (server) that is running the virtual world
	 * @param port - the port of the server's socket that the agent connects to
	 * @param saveDirectory - the directory that is used to save the Agent's state
	 * @param name - the agent's name
	 * @param lActDatabase - the XML file with the LanguageActs for the agent's 
	 * @param userLActDatabase - the XML file with the user's LanguageActs
	 * @param sex - the agent's sex
	 * @param role - the agent's role (Bully, Victim, Defender, etc)
	 * @param displayName - the agent's external name (what the user sees)
	 * @param displayMode - a boolean value specifying if the agent should display
	 * 				        internal debugging information in a graphical Swing Panel
	 * 					    (EmotionalState, Goals, etc)
	 */
	public IONAgent(String host, int port, String saveDirectory, String name, String lActDatabase, String userLActDatabase, String sex, String role, String displayName, boolean displayMode) {
		
	    _shutdown = false;
	    _numberOfCycles = 0;
		_self = name;
		_role = role;
		_sex = sex;
		_displayName = displayName;
		_showStateWindow = displayMode;
		
		_saveDirectory = saveDirectory;
		//_userLanguageFile = userLActDatabase;
		//_languageActsFile = lActDatabase;
		
		_currentEmotion = EmotionType.NEUTRAL;//neutral emotion - no emotion
		
		_actionsForExecution = new ArrayList();
		_perceivedEvents = new ArrayList();
		
		_dialogManager = new DialogManager();
		
		AutobiographicalMemory.GetInstance().setSelf(_self);
	
		//TODO mudar função recursiva
		
		// LOADING AgentFiles
		
		//LanguageEngine language;
		//LanguageEngine userLanguage;
		
		// Load language engine
        try
        {
        	//language = new LanguageEngine(name,sex,role,new File(MIND_PATH + _languageActsFile));
        	//userLanguage = new LanguageEngine("user","m","user",new File(MIND_PATH + _userLanguageFile));
        
			// Load Plan Operators
	        EmotionalPlanner planner = new EmotionalPlanner(MIND_PATH + "Actions.xml", _self);
	        
	        // Load GoalLibrary
			GoalLibrary goalLibrary = new GoalLibrary(MIND_PATH + "GoalLibrary.xml", _self);
			
			//For efficiency reasons these two are not real processes
			
			_reactiveLayer = new ReactiveProcess(_self);
			
			_deliberativeLayer = new DeliberativeProcess(_self,
				goalLibrary,
				planner);
			
			// Load Personality
			String personalityFile = MIND_PATH + "roles/" + role + "/" + role + ".xml";
			System.out.println("LOADING Personality: " + personalityFile);
			
			AgentLoaderHandler c = new AgentLoaderHandler(_self,_reactiveLayer,_deliberativeLayer);
	
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			parser.parse(new File(personalityFile), c);
			
			//The ION Agent does not load the goals initially from the personality file, therefore we
			//must clear all the goals loaded.
			_deliberativeLayer.RemoveAllGoals();
			
			//TODO refazer o integrityvalidator para que não precise do LE
			//tests to the data parsed from the agent files
			//IntegrityValidator val = new IntegrityValidator(planner.GetOperators(), language, null);
			//IntegrityValidator val = new IntegrityValidator(planner.GetOperators(), language, userLanguage);
		    
		   //first, lets check the actions file (the one with the strips operators)
		    //planner.CheckIntegrity(val);
		    
			//second, verify the goals
		    /*for(ListIterator li = goalLibrary.GetGoals(); li.hasNext();)
		    {
		    	((Goal) li.next()).CheckIntegrity(val);
		    }*/
			
			//third, verify the action tendencies
			//_reactiveLayer.getActionTendencies().CheckIntegrity(val);
			
			//fourth, verify the reaction rules
			//_reactiveLayer.getEmotionalReactions().CheckIntegrity(val);
	
			_remoteAgent = new IONRemoteAgent(host, port, this);
			//_remoteAgent = new IONRemoteAgent(host, port, language, MIND_PATH + _userLanguageFile, this);
			
			/*
			 * This call will initialize the timer for the agent's
			 * simulation time
			 */
			AgentSimulationTime.GetInstance();
			
			_remoteAgent.start();
			
			if(_showStateWindow) _agentDisplay = new AgentDisplay(this);
			
			this.Run();
		}
		catch (Exception e) {
			e.printStackTrace();
			//System.exit(-1);
		}
		
		 _deliberativeLayer.ShutDown();
		 _reactiveLayer.ShutDown();
		 _remoteAgent.ShutDown();
		 if(_showStateWindow && _agentDisplay != null) _agentDisplay.dispose();
		
	}
	
	//Creates an agent who's state is loaded from the harddisk
	public IONAgent(String host, int port, String directory, String name)
	{
		try{
			_shutdown = false;
			_numberOfCycles = 0;
			 
			LoadAgentState(host,port,directory + name);
			 
			_remoteAgent.start();
	        
			
			if(_showStateWindow) _agentDisplay = new AgentDisplay(this);
			
			this.Run();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	
		 _deliberativeLayer.ShutDown();
		 _reactiveLayer.ShutDown();
		 _remoteAgent.ShutDown();
		 if(_showStateWindow && _agentDisplay != null) _agentDisplay.dispose();
	}
	
	public void SaveAgentState(String agentName)
	{
		String fileName = _saveDirectory + agentName;
		
		AgentSimulationTime.SaveState(fileName+"-Timer.dat");
		EmotionalState.SaveState(fileName+"-EmotionalState.dat");
		KnowledgeBase.SaveState(fileName+"-KnowledgeBase.dat");
		AutobiographicalMemory.SaveState(fileName+"-AutobiographicalMemory.dat");
		_remoteAgent.SaveState(fileName+"-RemoteAgent.dat");
		
		try
		{
			FileOutputStream out = new FileOutputStream(fileName);
	    	ObjectOutputStream s = new ObjectOutputStream(out);
	    	
	    	s.writeObject(_deliberativeLayer);
	    	s.writeObject(_reactiveLayer);
	    	s.writeObject(_dialogManager);
	    	s.writeObject(_role);
	    	s.writeObject(_self);
	    	s.writeObject(_sex);
	    	s.writeObject(_speechAct);
	    	s.writeObject(new Short(_currentEmotion));
	    	s.writeObject(_displayName);
	    	s.writeObject(new Boolean(_showStateWindow));
	    	s.writeObject(_actionsForExecution);
	    	s.writeObject(_perceivedEvents);
	    	
	    	s.writeObject(_self);
	    	s.writeObject(_saveDirectory);
	    	s.writeObject(_dialogManager);
	    	/*s.writeObject(_languageActsFile);
	    	s.writeObject(_userLanguageFile);*/
	    	
        	s.flush();
        	s.close();
        	out.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void SaveAM(String agentName)
	{
		String fileName = _saveDirectory + agentName + "-AM.txt";
		try
		{
			FileOutputStream out = new FileOutputStream(fileName);
			out.write(AutobiographicalMemory.GetInstance().toXML().getBytes());
			out.flush();
			out.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void LoadAgentState(String host, int port, String fileName) throws ClassNotFoundException, IOException
	{
		FileInputStream in = new FileInputStream(fileName);
    	ObjectInputStream s = new ObjectInputStream(in);
    	
    	this._deliberativeLayer = (DeliberativeProcess) s.readObject();
    	this._reactiveLayer = (ReactiveProcess) s.readObject();
    	this._dialogManager = (DialogManager) s.readObject();
		this._role = (String) s.readObject();
    	this._self = (String) s.readObject();
    	this._sex = (String) s.readObject();
    	this._speechAct = (SpeechAct) s.readObject();
    	this._currentEmotion = ((Short) s.readObject()).shortValue();
    	this._displayName = (String) s.readObject();
    	this._showStateWindow = ((Boolean) s.readObject()).booleanValue();
    	this._actionsForExecution = (ArrayList) s.readObject();
    	this._perceivedEvents = (ArrayList) s.readObject();
    	
    	this._self = (String) s.readObject();
    	this._saveDirectory = (String) s.readObject();
    	this._dialogManager = (DialogManager) s.readObject();
    	/*this._languageActsFile = (String) s.readObject();
    	this._userLanguageFile = (String) s.readObject();*/
    	
    	s.close();
    	in.close();
		
		KnowledgeBase.LoadState(fileName+"-KnowledgeBase.dat");
		EmotionalState.LoadState(fileName+"-EmotionalState.dat");
		AgentSimulationTime.LoadState(fileName+"-Timer.dat");
		AutobiographicalMemory.LoadState(fileName+"-AutobiographicalMemory.dat");
		
		/*String userLanguageDatabase = MIND_PATH + _userLanguageFile;
		LanguageEngine language = new LanguageEngine(_self,_sex,_role,new File(MIND_PATH + _languageActsFile));*/
		
		//_remoteAgent = new IONRemoteAgent(host, port, language, userLanguageDatabase, this);
		_remoteAgent = new IONRemoteAgent(host, port, this);
		_remoteAgent.LoadState(fileName+"-RemoteAgent.dat");
	}
}