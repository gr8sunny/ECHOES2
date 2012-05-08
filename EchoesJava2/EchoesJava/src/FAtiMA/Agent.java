/** 
 * Agent.java - Represents the FAtiMA agent. This class agregates all 
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
 * Created: 17/01/2004 
 * @author: João Dias
 * Email to: joao.assis@tagus.ist.utl.pt
 * 
 * History: 
 * João Dias: 17/01/2004 - File created
 * João Dias: 24/05/2006 - replaced the deprecated EmotionalReactionTreeNode(short,string) constructor
 * 						   for the EmotionalReactionTreeNode(short) constructor
 * João Dias: 02/07/2006 - Replaced System's timer by an internal agent simulation timer
 * 						   The Tick method is called in each simulation cycle to update the timer.
 * João Dias: 15/07/2006 - Removed the KnowledgeBase from the Agent's properties since the KB is now
 * 						   a singleton that can be used anywhere without previous references.
 * 						   Removed the getKB() method, it doesn't make sense with this change.
 * João Dias: 15/07/2006 - Removed the EmotionalState from the Class fields since the 
 * 						   EmotionalState is now a singleton that can be used anywhere 
 * 					       without previous references.
 * 						   Removed the getEmotionalState() method, it doesn't make sense with this change.
 * João Dias: 17/07/2006 - Very important change! Removed several fields from the class that didn't make 
 * 						   sense to be stored here and were causing problems with serialization:
 * 							  - ActionTendencies
 * 							  - EmotionalReactionTreeNode
 * 							  - Goals
 * 							  - GoalLibrary
 * 							  - EmotionalPlanner
 * 							  - LanguageEngine
 * 							  - UserLanguageDatabase
 * João Dias: 17/07/2006 - Moved the following methods to class DeliberativeProcess
 * 							  - AddGoal
 * 							  - ChangeGoalImportance
 * 							  - RemoveGoal
 * 							  - RemoveAllGoals
 * 							  - GetGoals
 * 							  - getGoalLibrary
 * 							  - getEmotionalPlanner
 * João Dias: 17/07/2006 - Moved the following methods to class ReactiveProcess
 * 							  - AddEmotionalReaction
 * 							  - GetEmotionalReactions
 * 							  - GetActionTendencies
 * João Dias: 17/07/2006 - Removed the methods 
 * 							  - LoadPersonality
 * 							  - LoadEmotionalReactionTreeNodes
 * 						   	  - getLanguageEngine
 * 							  - getUserLanguageDataBase	
 * João Dias: 17/07/2006 - Added the methods GetDeliberativeLayer
 * 						   and GetReactiveLayer
 * João Dias: 17/07/2006 - Added the methods SaveAgentState and LoadAgentState
 * João Dias: 17/07/2006 - Added a new Class constructor that loads the agent's
 * 						   state from a given file
 * João Dias: 17/07/2006 - Added the field sex 
 * João Dias: 24/07/2006 - The private methods SelectBestAction and SelectBestSpeech
 * 						   now return a ValuedAction instead of the name of the action. 
 * 						   This ValuedAction will be sent to the remote agent.
 * João Dias: 29/08/2006 - Changes in SaveAgentState and LoadAgentState
 * João Dias: 20/09/2006 - Minor changes in the way which actions are selected for executions.
 * 						   Now, both reactive and deliberative actions are moved into an execution
 * 						   list, and when its possible to execute an action, the best of the actions
 * 						   stored in the list is executed.
 * 						 - Added field DialogManager and added methods called from the RemoteAgent
 * 						   class that updates the dialog 
 * João Dias: 07/12/2006 - Changed the class attributes from private to protected so that they can be 
 * 						   inherited by the class IONAgent
 * 						 - Changed methods from private to protected for the same reason
 * 						 - Added the empty constructor so that we can create IONAgents
 * 						 - Attribute _displayMode renamed to _showStateWindow
 * João Dias: 16/01/2007 - Changed the minds directory
 * João Dias: 29/01/2007 - Added the concept of ActionContext. Similarly to the SpeechContext, ActionContext is a property
 * 						   stored in the KB that contains the last action perceived by the agent. This property is updated
 * 						   in the PerceiveEvent method
 * João Dias: 15/01/2007 - At the end of each simulation cycle, the agent now tests if there is any new knowledge in the KB,
 * 						   in order to report to the framework the change of any internal property due to inference operators
 * João Dias: 22/02/2007 - Added the field eventsPerceived that contains the external events perceived by the agent in the
 * 						   last cycle. Changed the methods Save, Load, PerceiveEvent and Run accordingly
 *						 - Refactoring: The inference of new Knowledge in the KB is now called by the agent instead of being called
 *						   by the deliberative layer. Additionally, from now on, its the agent's responsability to decide whether to 
 *						   call the appraisal methods or not, taking into consideration that the deliberative appraisal
 *						   always checks goal activation.
 * João Dias: 04/04/2007 - The agent doesn't save automatically when closing anymore. It must receive an external message from the
 * 					 	   virtual environment in order to save its state to the hard drive.
 */

package FAtiMA;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.StringTokenizer;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import echoesEngine.ControlPanel;
import FAtiMA.Display.AgentDisplay;
import FAtiMA.autobiographicalMemory.AutobiographicalMemory;
import FAtiMA.deliberativeLayer.DeliberativeProcess;
import FAtiMA.deliberativeLayer.EmotionalPlanner;
import FAtiMA.deliberativeLayer.goals.Goal;
import FAtiMA.deliberativeLayer.goals.GoalLibrary;
import FAtiMA.emotionalState.EmotionalState;
import FAtiMA.knowledgeBase.KnowledgeBase;
import FAtiMA.knowledgeBase.KnowledgeSlot;
import FAtiMA.reactiveLayer.ReactiveProcess;
import FAtiMA.sensorEffector.Event;
import FAtiMA.sensorEffector.RemoteAgent;
import FAtiMA.sensorEffector.SpeechAct;
import FAtiMA.util.enumerables.EmotionType;
import FAtiMA.util.parsers.AgentLoaderHandler;
import FAtiMA.wellFormedNames.Name;

/**
 * This class will correspond to an agent in the virtual world. Agregates all
 * the agent architecture componnents, and is responsible for initiallizing and
 * defining the communication ports between them. Contains the Main method.
 * 
 * @author João Dias
 */
public class Agent {

	/**
	 * The main method
	 */
	static public void main(String args[]) throws Exception {
		int i;
		StringTokenizer st;
		String left;
		HashMap properties = new HashMap();
		ArrayList goals = new ArrayList();

		if (args.length < 7) {
			if (args.length == 3) {
				new Agent(args[0], Integer.parseInt(args[1]), args[2]);
			} else {
				System.out.println("Wrong number of arguments!");
				return;
			}
		}

		for (i = 7; i < args.length; i++) {
			st = new StringTokenizer(args[i], ":");
			left = st.nextToken();
			if (left.equals("GOAL")) {
				goals.add(st.nextToken());
			} else
				properties.put(left, st.nextToken());
		}

		new Agent(args[0], Integer.parseInt(args[1]), Boolean
				.parseBoolean(args[2]), args[3], args[4], args[5], args[6],
				properties, goals);
	}

	protected boolean _shutdown;
	protected DeliberativeProcess _deliberativeLayer;
	protected ReactiveProcess _reactiveLayer;
	protected DialogManager _dialogManager;

	protected ArrayList _actionsForExecution;
	protected ArrayList _perceivedEvents;

	protected RemoteAgent _remoteAgent;
	protected String _role;
	protected String _self; // the agent's name
	protected String _sex;
	protected String _displayName;
	protected SpeechAct _speechAct;
	protected short _currentEmotion;
	protected long _numberOfCycles;
	protected long _totalexecutingtime = 0;

	protected AgentDisplay _agentDisplay;
	protected boolean _showStateWindow;

	public static final String MIND_PATH = "data/characters/minds/";
	private static final Name ACTION_CONTEXT = Name
			.ParseName("ActionContext()");

	/**
	 * Empty Constructor
	 */
	protected Agent() {
	}

	/**
	 * Creates a new Agent
	 * 
	 * @param host
	 *            - the host (server) that is running the virtual world
	 * @param port
	 *            - the port of the server's socket that the agent connects to
	 * @param displayMode
	 *            - a boolean value specifying if the agent should display
	 *            internal debugging information in a graphical Swing Panel
	 *            (EmotionalState, Goals, etc)
	 * @param name
	 *            - the agent's name
	 * @param sex
	 *            - the agent's sex
	 * @param role
	 *            - the agent's role (Bully, Victim, Defender, etc)
	 * @param displayName
	 *            - the agent's external name (what the user sees)
	 * @param properties
	 *            - an hashmap with the agent's properties in the world (ex:
	 *            Strength, Name, Position, etc)
	 * @param goalList
	 *            - a list of goals that the agent should pay more importance
	 */
	public Agent(String host, int port, boolean displayMode, String name,
			String sex, String role, String displayName, HashMap properties,
			ArrayList goalList) {

		_shutdown = false;
		_numberOfCycles = 0;
		_self = name;
		_role = role;
		_sex = sex;
		_displayName = displayName;
		_showStateWindow = displayMode;

		_currentEmotion = EmotionType.NEUTRAL;// neutral emotion - no emotion

		_actionsForExecution = new ArrayList();
		_perceivedEvents = new ArrayList();

		properties.put("name", _self);
		properties.put("role", _role);
		properties.put("sex", _sex);

		_dialogManager = new DialogManager();

		AutobiographicalMemory.GetInstance().setSelf(_self);

		// TODO permitir desigualdades nas substituições
		// TODO mudar função recursiva

		// LOADING AgentFiles
		String path = "";

		try {

			// Load Plan Operators
			EmotionalPlanner planner = new EmotionalPlanner(path + MIND_PATH
					+ "Actions.xml", _self);

			// Load GoalLibrary
			GoalLibrary goalLibrary = new GoalLibrary(path + MIND_PATH
					+ "GoalLibrary.xml", _self);

			// Load Personality
			String personalityFile = path + MIND_PATH + "roles/" + role + "/"
					+ role + ".xml";

			System.out.println("LOADING Personality: " + personalityFile);

			// For efficiency reasons these two are not real processes

			_reactiveLayer = new ReactiveProcess(_self);

			_deliberativeLayer = new DeliberativeProcess(_self, goalLibrary,
					planner);

			AgentLoaderHandler c = new AgentLoaderHandler(_self,
					_reactiveLayer, _deliberativeLayer);

			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			parser.parse(new File(personalityFile), c);

			// loads additional goals provided in the starting goal list
			ListIterator lt = goalList.listIterator();
			String goal;
			String goalName;
			Goal g;
			StringTokenizer st;
			float impOfSuccess;
			float impOfFailure;
			while (lt.hasNext()) {
				goal = (String) lt.next();
				st = new StringTokenizer(goal, "|");
				goalName = st.nextToken();
				impOfSuccess = Float.parseFloat(st.nextToken());
				impOfFailure = Float.parseFloat(st.nextToken());

				_deliberativeLayer
						.AddGoal(goalName, impOfSuccess, impOfFailure);
			}

			// tests to the data parsed from the agent files
			// IntegrityValidator val = new
			// IntegrityValidator(planner.GetOperators(),null, null);
			// IntegrityValidator val = new
			// IntegrityValidator(planner.GetOperators(), language,
			// userLanguage);

			// first, lets check the actions file (the one with the strips
			// operators)
			// planner.CheckIntegrity(val);

			// second, verify the goals
			/*
			 * ListIterator li = goalLibrary.GetGoals(); while(li.hasNext()) { g
			 * = (Goal)li.next(); g.CheckIntegrity(val); }
			 * 
			 * 
			 * //third, verify and print the action tendencies
			 * _reactiveLayer.getActionTendencies().CheckIntegrity(val);
			 * _reactiveLayer.getActionTendencies().Print();
			 * 
			 * //fourth, verify the reaction rules
			 * _reactiveLayer.getEmotionalReactions().CheckIntegrity(val);
			 */

			_remoteAgent = new RemoteAgent(host, port, this, properties);

			/*
			 * This call will initialize the timer for the agent's simulation
			 * time
			 */
			AgentSimulationTime.GetInstance();

			_remoteAgent.start();

			if (_showStateWindow)
				_agentDisplay = new AgentDisplay(this);

			this.Run();
		} catch (Exception e) {
			e.printStackTrace();
		}

		_deliberativeLayer.ShutDown();
		_reactiveLayer.ShutDown();
		_remoteAgent.ShutDown();
		if (_showStateWindow && _agentDisplay != null)
			_agentDisplay.dispose();

	}

	public Agent(String host, int port, String fileName) {
		try {
			_shutdown = false;
			_numberOfCycles = 0;

			_remoteAgent = new RemoteAgent(host, port, this);

			LoadAgentState(fileName);

			_remoteAgent.start();

			if (_showStateWindow)
				_agentDisplay = new AgentDisplay(this);

			this.Run();
		} catch (Exception e) {
			e.printStackTrace();
			// System.exit(-1);
		}

		_deliberativeLayer.ShutDown();
		_reactiveLayer.ShutDown();
		_remoteAgent.ShutDown();
		if (_showStateWindow && _agentDisplay != null)
			_agentDisplay.dispose();
	}

	/**
	 * Gets the name of the agent
	 * 
	 * @return the agent's name
	 */
	public String name() {
		return _self;
	}

	/**
	 * Gets the gender of the agent
	 * 
	 * @return the agent's sex
	 */
	public String sex() {
		return _sex;
	}

	/**
	 * Gets the agent's name that is displayed externally
	 * 
	 * @return the agent's external name
	 */
	public String displayName() {
		return _displayName;
	}

	/**
	 * Gets the agent's Reactive Layer that you can use to get access to
	 * reactive structures such as ActionTendencies and EmotionalReactions
	 * 
	 * @return the agent's Reactive Layer
	 */
	public ReactiveProcess getReactiveLayer() {
		return this._reactiveLayer;
	}

	/**
	 * Gets the agent's Deliberative Layer that you can use to get access to
	 * Deliberative structures such as the goals and planner
	 * 
	 * @return the agent's Deliberative Layer
	 */
	public DeliberativeProcess getDeliberativeLayer() {
		return this._deliberativeLayer;
	}

	/**
	 * Specifies that the agent must give an answer to a received SpeechAct
	 * 
	 * @param speechAct
	 *            - the SpeechAct that needs an answer
	 */
	public void AnswerToSpeechAct(SpeechAct speechAct) {
		_speechAct = speechAct;
	}

	/**
	 * Perceives a given event from the virtual world
	 * 
	 * @param e
	 *            - the Event to perceive
	 */
	public void PerceiveEvent(Event e) {
		synchronized (this) {
			_perceivedEvents.add(e);
		}
	}

	/**
	 * Resets the agent's reasoning layers (deliberative + cognitive)
	 * 
	 */
	public void Reset() {
		// _emotionalState.Clear();
		_dialogManager.Reset();
		_reactiveLayer.Reset();
		_deliberativeLayer.Reset();
		_perceivedEvents.clear();
	}

	/**
	 * Gets the agent's role
	 * 
	 * @return the role of the agent (Victim, Bully, etc)
	 */
	public String role() {
		return _role;
	}

	/**
	 * Runs the agent, endless loop until there is a shutdown
	 */
	public void Run() {
		ValuedAction action;
		long updateTime = System.currentTimeMillis();

		while (!_shutdown) {
			try {

				if (_remoteAgent.isShutDown()) {
					_shutdown = true;
				}

				// updates the agent's simulation timer
				AgentSimulationTime.GetInstance().Tick();

				_numberOfCycles++;
				long startCycleTime = System.currentTimeMillis();

				if (_remoteAgent.isRunning()) {
					// decay the agent's emotional state
					EmotionalState.GetInstance().Decay();
					_dialogManager.DecayCauseIDontHaveABetterName();

					// perceives and appraises new events
					synchronized (this) {
						for (ListIterator li = this._perceivedEvents
								.listIterator(); li.hasNext();) {
							Event e = (Event) li.next();
							System.out.println("Perceiving event: "
									+ e.toName());
							ControlPanel.writeLog("Perceiving event: "
									+ e.toName());
							// inserting the event in AM
							AutobiographicalMemory.GetInstance().StoreAction(e);
							// registering an Action Context property in the KB
							KnowledgeBase.GetInstance().Tell(ACTION_CONTEXT,
									e.toName().toString());

							if (SpeechAct.isSpeechAct(e.GetAction())) {
								_dialogManager.UpdateDialogState(e);
							}

							// adds the event to the deliberative and reactive
							// layers so that they can appraise
							// the events
							_reactiveLayer.AddEvent(e);
							_deliberativeLayer.AddEvent(e);
						}
						this._perceivedEvents.clear();
					}

					// if there was new data or knowledge added we must apply
					// inference operators
					// update any inferred property to the outside and appraise
					// the events
					if (AutobiographicalMemory.GetInstance().HasNewData()
							|| KnowledgeBase.GetInstance().HasNewKnowledge()) {
						// calling the KnowledgeBase inference process
					
						//!! comment out, not needed for ECHOES
						//	KnowledgeBase.GetInstance().PerformInference();

						synchronized (KnowledgeBase.GetInstance()) {
							ArrayList facts = KnowledgeBase.GetInstance()
									.GetNewFacts();

							for (ListIterator li = facts.listIterator(); li
									.hasNext();) {
								KnowledgeSlot ks = (KnowledgeSlot) li.next();
								if (ks.getName().startsWith(this._self)) {

									_remoteAgent.ReportInternalPropertyChange(
											Name.ParseName(ks.getName()), ks
													.getValue());
								}
							}
						}
					}

					// Appraise the events and changes in data
					ControlPanel.writeLog("reactive appraisal");
					_reactiveLayer.Appraisal();
					ControlPanel.writeLog("deliberative appraisal");

					_deliberativeLayer.Appraisal();

					ControlPanel.writeLog("reactive coping");

					_reactiveLayer.Coping();

					ControlPanel.writeLog("deliberative coping");

					_deliberativeLayer.Coping();

					ControlPanel
							.writeLog("remote agent is finished executing?: "
									+ _remoteAgent.FinishedExecuting());
					if (_remoteAgent.FinishedExecuting()
							&& _remoteAgent.isRunning()) {

						// System.out.println("Agent finished executing and is running");
						/*
						 * action = _reactiveLayer.GetSelectedAction();
						 * if(action != null) {
						 * _actionsForExecution.add(action); }
						 * 
						 * action = _deliberativeLayer.GetSelectedAction();
						 * if(action != null) {
						 * _actionsForExecution.add(action); }
						 * 
						 * bestAction = SelectBestAction(); if(bestAction !=
						 * null) { _remoteAgent.AddAction(bestAction); }
						 */

						action = FilterSpeechAction(_reactiveLayer
								.GetSelectedAction());

						if (action != null) {
							_reactiveLayer.RemoveSelectedAction();
							_remoteAgent.AddAction(action);
						} else {
							action = FilterSpeechAction(_deliberativeLayer
									.GetSelectedAction());
							if (action != null) {
								_deliberativeLayer.RemoveSelectedAction();
								_remoteAgent.AddAction(action);
							}
						}

						if (_remoteAgent.getActions()) {
							System.out.println("The current plan is: "
									+ _deliberativeLayer.getSelectedPlan());
							_remoteAgent.ExecuteNextAction();
						}
					}

					if (System.currentTimeMillis() - updateTime > 1000) {
						if (_showStateWindow && _agentDisplay != null) {
							_agentDisplay.update();
						}

						_remoteAgent.ReportInternalState();

						/*
						 * ActiveEmotion auxEmotion =
						 * EmotionalState.GetInstance().GetStrongestEmotion();
						 * short nextEmotion; if(auxEmotion != null) {
						 * nextEmotion = auxEmotion.GetType(); } else
						 * nextEmotion = EmotionType.NEUTRAL;
						 * 
						 * if(_currentEmotion != nextEmotion) { _currentEmotion
						 * = nextEmotion;
						 * _remoteAgent.ExpressEmotion(EmotionType
						 * .GetName(_currentEmotion)); }
						 */

						updateTime = System.currentTimeMillis();
					}
				}

				long cycleExecutionTime = System.currentTimeMillis()
						- startCycleTime;
				_totalexecutingtime += cycleExecutionTime;
				// System.out.println("Cycle execution (in Millis): " +
				// cycleExecutionTime);
				// System.out.println("Average time per cycle (in Millis): " +
				// _totalexecutingtime / _numberOfCycles);
				Thread.sleep(50);
			} catch (Exception ex) {
				// _shutdown = true;
				ex.printStackTrace();
				// System.out.println(ex);
			}
		}
		// SaveAgentState(_self);
		/*
		 * System.out.println("Agent is exiting. Press any key to complete the shutdown!"
		 * ); try { System.in.read(); } catch(IOException e) {
		 * e.printStackTrace(); }
		 */
	}

	private ValuedAction FilterSpeechAction(ValuedAction action) {
		ValuedAction aux = null;

		if (action != null) {
			String actionName = action.GetAction().GetFirstLiteral().toString();
			if (_dialogManager.CanSpeak() || !SpeechAct.isSpeechAct(actionName)) {
				aux = action;
			}
		}

		return aux;
	}

	public void AppraiseSelfActionFailed(Event e) {
		_deliberativeLayer.AppraiseSelfActionFailed(e);
	}

	/*
	 * public void UpdateDialogState(SpeechAct speechAct) {
	 * _dialogManager.UpdateDialogState(speechAct); }
	 */

	public void SpeechStarted() {
		_dialogManager.SpeechStarted();
	}

	protected ValuedAction SelectBestAction() {

		ValuedAction bestAction = null;
		ValuedAction action;
		int removeHere = -1;

		for (int i = 0; i < _actionsForExecution.size(); i++) {
			action = (ValuedAction) _actionsForExecution.get(i);
			if (bestAction == null || action.GetValue() > bestAction.GetValue()) {
				bestAction = action;
				removeHere = i;
			}
		}

		if (bestAction != null) {
			_actionsForExecution.remove(removeHere);
		}
		return bestAction;
	}

	public void EnforceCopingStrategy(String coping) {
		_deliberativeLayer.EnforceCopingStrategy(coping);
		_reactiveLayer.EnforceCopingStrategy(coping);
	}

	/*
	 * protected ValuedAction SelectBestSpeech(ValuedAction reactiveSpeech,
	 * ValuedAction deliberativeSpeech) {
	 * 
	 * if(reactiveSpeech != null) { if(deliberativeSpeech != null) {
	 * if(reactiveSpeech.GetValue() >= deliberativeSpeech.GetValue()) { return
	 * reactiveSpeech; } else { return deliberativeSpeech; } } else { return
	 * reactiveSpeech; } } else if(deliberativeSpeech != null) { return
	 * deliberativeSpeech; } return null; }
	 */

	public void SaveAgentState(String fileName) {
		AgentSimulationTime.SaveState(fileName + "-Timer.dat");
		EmotionalState.SaveState(fileName + "-EmotionalState.dat");
		KnowledgeBase.SaveState(fileName + "-KnowledgeBase.dat");
		AutobiographicalMemory.SaveState(fileName
				+ "-AutobiographicalMemory.dat");
		_remoteAgent.SaveState(fileName + "-RemoteAgent.dat");

		try {
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
			s.flush();
			s.close();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void LoadAgentState(String fileName) {
		try {
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
			s.close();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		KnowledgeBase.LoadState(fileName + "-KnowledgeBase.dat");
		EmotionalState.LoadState(fileName + "-EmotionalState.dat");
		AgentSimulationTime.LoadState(fileName + "-Timer.dat");
		AutobiographicalMemory.LoadState(fileName
				+ "-AutobiographicalMemory.dat");
		_remoteAgent.LoadState(fileName + "-RemoteAgent.dat");
	}

	/*
	 * public void ShutDown() { _deliberativeLayer.ShutDown();
	 * _reactiveLayer.ShutDown(); //_remoteAgent.destroy(); System.exit(-1); }
	 */
}