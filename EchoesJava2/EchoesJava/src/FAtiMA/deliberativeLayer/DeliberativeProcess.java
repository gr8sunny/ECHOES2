/** 
 * DeliberativeProcess.java - Implements the Agent's Architecture deliberative processes
 * (deliberative appraisal + problem-focused coping/emotion-focused coping).
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
 * @author: João Dias
 * Email to: joao.assis@tagus.ist.utl.pt
 * 
 * History: 
 * João Dias: 21/12/2004 - File created
 * João Dias: 10/07/2006 - the class is now serializable 
 * João Dias: 12/07/2006 - Replaced the deprecated Ground methods for the new ones.
 * João Dias: 15/07/2006 - Removed the KnowledgeBase from the Class's fields since the KB is now
 * 						   a singleton that can be used anywhere without previous references.
 * João Dias: 15/07/2006 - Removed the EmotionalState from the Class's fields since the 
 * 						   EmotionalState is now a singleton that can be used anywhere 
 * 					       without previous references.
 * João Dias: 17/07/2006 - The following methods were moved from Agent class to this class 
 * 							  - AddGoal
 * 							  - ChangeGoalImportance
 * 							  - RemoveGoal
 * 							  - RemoveAllGoals
 * 							  - GetGoals
 * 							  - getGoalLibrary
 * 							  - getEmotionalPlanner
 * João Dias: 17/07/2006 - Added the field GoalLibrary and changed the class constructor
 * 						   accordingly
 * João Dias: 20/07/2006 - small change in add goal method, now we have to create
 * 						   a protected condition from the conditions stored in an 
 * 						   InterestGoal
 * João Dias: 21/07/2006 - removed the list of goals received in the class constructor
 * João Dias: 24/07/2006 - removed the field _selectedActionValue and added a new one
 * 						   _selectedActionEmotion. This means that instead of storing the value
 * 						   associated with a selected action, we store the emotion associated with
 * 						   the action.
 * João Dias: 08/09/2006 - removed some unreachable code from method Appraisal
 * João Dias: 18/09/2006 - small changes due to changes in Question SpeechActs
 * João Dias: 20/09/2006 - Added the new functionality of InferenceOperators
 * 						 - Added new funcionality of WaitFor actions
 *  					 - Removed the method RemoveSelectedAction. The method
 * 						   GetSelectedMethod now additionally has the functionality 
 * 						   of the RemoveSelectedAction method
 * 						 - I was forgeting to update an effect's probability when
 * 						   an expirableActionMonitor expired. In this situation,  
 * 						   all the step effects probability must be updated. I solved this
 * 						   problem.
 * 						 - Added ForceUpdate method. Read description.
 * João Dias: 21/09/2006 - Small change in the way that I was updating a Step's probability
 * 						   of execution and the probability of its effects.
 * 						 - Now the deliberative proccess can deal with actions of other agents
 * 						   in the planning proccess. If such action (of another agent) is selected
 * 						   for execution, the agent waits (through an ExpirableActionMonitor), hoping
 * 						   that the corresponding agent will decide to execute the action. If the action
 * 						   is indeed executed, the agent will increase the step's probability. If after
 * 						   some time, the action is still not executed, the agent gives up waiting and
 * 						   lowers its probability of execution.
 * João Dias: 29/09/2006 - There was a problem when the agent tried to execute a plan's action where the
 * 						   agent is not specified. In these situations, if the agent is unspecified it 
 * 						   means that any agent can do the action, and so the agent decides to do it himself
 * 						   since he wants to achieve the goal. The problem was that we were forgetting to apply
 * 						   the substitution of the agent's name to the step, and so the step's effects were
 * 						   wrong. Now it works properly.
 * 					     - When the agent would choose for execution an action of other agent, we would create
 * 						   an ExpirableActionMonitor to wait for such action. However, in addition to expiring
 * 					       after a predetermined time, the action monitor would be stopped by any action done by
 * 						   any character. It was implemented in this way to make sure that the agent would be 
 * 						   able to do something else if the environment would change. However, this has the problem
 * 						   of always lowering the step's probability even if the agent that is supposed to perform the action
 * 						   does not act. Now, we only stop the monitor if it expires or if the agent that we expect to
 * 						   act does something. In that case, if it was what we were expecting, the step's probability is 
 * 						   increased. If not, it is lowered. 
 * João Dias: 02/10/2006 - Added attribute selectedPlan, this attribute contains the plan last developed or selected
 * 					       for execution by the deliberative layer
 * João Dias: 03/10/2006 - the deliberative layer no longer needs to add the effects of internal operators (when they are 
 * 						   selected for execution) to the KnowledgeBase. It's up to the KB to detect when it should use
 * 						   the operators, and add the new knowledge resulting from the inference
 * João Dias: 04/10/2006 - Before trying to execute an action that does not have agency defined, the agent has to check if he
 * 						   can do it, by adding the self substitution and verifying if the resulting plan is valid. Only in
 * 						   this situation he can try to execute the action.
 * João Dias: 20/10/2006 - The probability of a step's effects was being wrongly updated in some situations where the action
 * 						   corresponded to an action by other agent. This bug is solved.
 * João Dias: 06/12/2006 - If a complete plan does not have a valid next action to execute (ex: the next
 *						   action to execute contains unboundvariables), it means that the plan cannot be executed, 
 *						   and therefore the plan is removed from the intention structure
 * João Dias: 07/12/2006 - The RemoveAllGoals method additionaly removes current goals, intentions and plans from the
 * 						   planner
 * João Dias: 07/12/2006 - the methods that used or accessed the agent's goals are now synchronized. This was necessary
 * 						   because in some particular situations simultaneous accesses to the goal list structure was
 * 						   being carried out, causing the agents to crash.
 * João Dias: 22/12/2006 - Appraisal Method: when monitoring an action of another agent, we must test the received event
 * 						   by trying to unify it with the expected action, instead of just comparing the names. This is
 * 						   because now an action of another agent may contain unbound variables
 * João Dias: 22/02/2006 - Refactoring: the inference procedure was removed from the Appraisal Method. The inference is now
 * 						   performed at the agent level. Additionaly, the appraisal method now allways checks goal activation.
 * 						   From now on, its the agent's responsability to decide whether to call the appraisal method or not.
 * 						 - Because of the above refactorization, the method ForceUpdate was now unnecessary and was removed. 	  
 * 			
 */

package FAtiMA.deliberativeLayer;

import java.util.ArrayList;
import java.util.ListIterator;
import echoesEngine.ControlPanel;
import FAtiMA.AgentProcess;
import FAtiMA.ValuedAction;
import FAtiMA.autobiographicalMemory.AutobiographicalMemory;
import FAtiMA.conditions.Condition;
import FAtiMA.deliberativeLayer.goals.ActivePursuitGoal;
import FAtiMA.deliberativeLayer.goals.Goal;
import FAtiMA.deliberativeLayer.goals.GoalLibrary;
import FAtiMA.deliberativeLayer.goals.InterestGoal;
import FAtiMA.deliberativeLayer.plan.Plan;
import FAtiMA.deliberativeLayer.plan.ProtectedCondition;
import FAtiMA.deliberativeLayer.plan.Step;
import FAtiMA.emotionalState.ActiveEmotion;
import FAtiMA.exceptions.UnknownGoalException;
import FAtiMA.knowledgeBase.KnowledgeBase;
import FAtiMA.sensorEffector.Event;
import FAtiMA.sensorEffector.Parameter;
import FAtiMA.wellFormedNames.Name;
import FAtiMA.wellFormedNames.Substitution;
import FAtiMA.wellFormedNames.SubstitutionSet;
import FAtiMA.wellFormedNames.Symbol;
import FAtiMA.wellFormedNames.Unifier;

/**
 * Implements the Agent's Architecture deliberative processes (deliberative
 * appraisal + problem-focused coping/emotion-focused coping).
 * 
 * @author João Dias
 */
public class DeliberativeProcess extends AgentProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final long waitingTime = 5000;

	private ArrayList _goals;
	private GoalLibrary _goalLibrary;
	private EmotionalPlanner _planner;

	private ActionMonitor _actionMonitor;
	private Plan _selectedPlan;
	private Step _selectedAction;
	private ActiveEmotion _selectedActionEmotion;

	private DeliberativeProcess() {
	}

	/**
	 * Creates a new DeliberativeProcess
	 * 
	 * @param name
	 *            - the name of the agent
	 * @param goalLibrary
	 *            - the GoalLibrary with all domain's goals
	 * @param planner
	 *            - the EmotionalPlanner that will be used by the deliberative
	 *            layer
	 * @param kb
	 *            - a reference to the KnowledgeBase
	 */
	public DeliberativeProcess(String name, GoalLibrary goalLibrary,
			EmotionalPlanner planner) {
		super(name);
		_goals = new ArrayList();
		_goalLibrary = goalLibrary;
		_planner = planner;
		_actionMonitor = null;
		_selectedAction = null;
		_selectedPlan = null;
	}

	/**
	 * Adds a goal to the agent's Goal List
	 * 
	 * @param goal
	 *            - the goal to add
	 */
	private void AddGoal(Goal goal) {
		InterestGoal iGoal;
		ArrayList protectionConstraints;
		ListIterator li;

		synchronized (this) {
			if (!_goals.contains(goal)) {
				_goals.add(goal);
				if (goal instanceof InterestGoal) {
					iGoal = (InterestGoal) goal;
					protectionConstraints = iGoal.getProtectionConstraints();
					if (protectionConstraints != null) {
						li = protectionConstraints.listIterator();
						while (li.hasNext()) {
							_planner
									.AddProtectionConstraint(new ProtectedCondition(
											iGoal, (Condition) li.next()));
						}
					}
				}
			}
		}
	}

	/**
	 * Changes a Goal's Importance
	 * 
	 * @param goalName
	 *            - the name of the goal to change
	 * @param importance
	 *            - the new value for the importance
	 * @param importanceType
	 *            - the type of importance: the String "CIS" changes the
	 *            importance of success the String "CIF" changes the importance
	 *            of failure
	 */
	public void ChangeGoalImportance(String goalName, float importance,
			String importanceType) {
		ListIterator li;

		synchronized (this) {
			li = _goals.listIterator();
			Goal g;

			while (li.hasNext()) {
				g = (Goal) li.next();
				if (goalName.equals(g.GetName().toString())) {
					if (importanceType.equals("CIS")) {
						g.SetImportanceOfSuccess(importance);
					} else {
						g.SetImportanceOfFailure(importance);
					}
					break;
				}
			}
		}
	}

	/**
	 * Removes a given goal from the agent's goal list
	 * 
	 * @param goalName
	 *            - the name of the goal to remove
	 */
	public void RemoveGoal(String goalName) {
		Goal g;

		synchronized (this) {
			for (int i = 0; i < _goals.size(); i++) {
				g = (Goal) _goals.get(i);
				if (goalName.equals(g.GetName().toString())) {
					_goals.remove(i);
					break;
				}
			}
		}
	}

	/**
	 * Removes all the agent's goals
	 * 
	 */
	public void RemoveAllGoals() {

		synchronized (this) {
			_goals.clear();
		}
		_planner.ClearGoals();
	}

	/**
	 * Adds a goal to the agent's Goal List
	 * 
	 * @param goalName
	 *            - the name of the Goal
	 * @param importanceOfSuccess
	 *            - the goal's importance of success
	 * @param importanceOfFailure
	 *            - the goal's importance of failure
	 * @throws UnknownGoalException
	 *             - thrown if the goal is not specified in the GoalLibrary
	 *             file. You can only add goals defined in the GoalLibrary.
	 */
	public void AddGoal(String goalName, float importanceOfSuccess,
			float importanceOfFailure) throws UnknownGoalException {
		Goal g = _goalLibrary.GetGoal(Name.ParseName(goalName));
		if (g != null) {
			g.SetImportanceOfSuccess(importanceOfSuccess);
			g.SetImportanceOfFailure(importanceOfFailure);
			AddGoal(g);
		} else {
			throw new UnknownGoalException(goalName);
		}
	}

	/**
	 * Gets the agent's goals
	 * 
	 * @return a list with the agent's goals
	 */
	public ArrayList GetGoals() {
		return _goals;
	}

	/**
	 * Gets the library of goals (all goals specified for the domain)
	 * 
	 * @return the GoalLibrary with all goals specified for the domain
	 */
	public GoalLibrary getGoalLibrary() {
		return _goalLibrary;
	}

	/**
	 * Gets the agent's emotional planner used in the deliberative reasoning
	 * process
	 * 
	 * @return the agent's EmotionalPlanner
	 */
	public EmotionalPlanner getEmotionalPlanner() {
		return _planner;
	}

	public void EnforceCopingStrategy(String coping) {
		Goal g;
		coping = coping.toLowerCase();
		for (ListIterator li = _goalLibrary.GetGoals(); li.hasNext();) {
			g = (Goal) li.next();
			if (g.GetName().toString().toLowerCase().startsWith(coping)
					|| (coping.equals("standup") && g.GetName().toString()
							.startsWith("ReplyNegatively"))) {
				System.out.println("");
				System.out.println("Enforcing coping strategy: " + g.GetName());
				System.out.println("");
				g.IncreaseImportanceOfFailure(2);
				g.IncreaseImportanceOfSuccess(2);
			}
		}
	}

	/**
	 * Determines an answer to a SpeechAct according to the agent's goals and
	 * plans
	 * 
	 * @return the best answer to give according to its influence on the agent's
	 *         goals and plans
	 */
	/*
	 * public ValuedAction AnswerToSpeechAct(SpeechAct speechAct) { Step
	 * positiveAnswer; Step negativeAnswer; Name positiveSpeech; Name
	 * negativeSpeech; float positiveAnswerIntensity; float
	 * negativeAnswerIntensity; ArrayList bindings; Name action; Name
	 * goalFailure; float actionValue; Goal g;
	 * 
	 * positiveSpeech = Name.ParseName("Reply(" + speechAct.getSender() + "," +
	 * speechAct.getMeaning() + ",positiveanswer)"); negativeSpeech =
	 * Name.ParseName("Reply(" + speechAct.getSender() + "," +
	 * speechAct.getMeaning() + ",negativeanswer)");
	 * 
	 * //check if the speech act refers to any goal synchronized (this) {
	 * ListIterator li = _goals.listIterator(); while(li.hasNext()) { g = (Goal)
	 * li.next();
	 * if(g.GetName().GetFirstLiteral().toString().equals(speechAct.getMeaning
	 * ())) { //in this case, the user is suggesting that the agent should try
	 * to achieve this goal //if the agent tried previously to achieve it and
	 * the goal failed, it will reply no way goalFailure =
	 * Name.ParseName(g.GenerateGoalStatus(Goal.GOALFAILURE)); bindings =
	 * KnowledgeBase.GetInstance().GetPossibleBindings(goalFailure); if
	 * (bindings != null) { return new ValuedAction(negativeSpeech,10); } else {
	 * //if the goal didn't failed before, the agent will accept the user
	 * sugestion by increasing //the goal's importance
	 * g.IncreaseImportanceOfFailure(4); g.IncreaseImportanceOfSuccess(4);
	 * return new ValuedAction(positiveSpeech,10); } } } }
	 * 
	 * 
	 * positiveAnswer = _planner.GetStep(positiveSpeech); if(positiveAnswer !=
	 * null) { positiveAnswerIntensity =
	 * _planner.AppraiseAnswer(positiveAnswer); } else positiveAnswerIntensity =
	 * 0; negativeAnswer = _planner.GetStep(negativeSpeech); if(negativeAnswer
	 * != null) { negativeAnswerIntensity =
	 * _planner.AppraiseAnswer(negativeAnswer); } else negativeAnswerIntensity =
	 * 0;
	 * 
	 * if(positiveAnswerIntensity >= negativeAnswerIntensity) {
	 * if(positiveAnswer != null) { action = positiveAnswer.getName();
	 * actionValue = positiveAnswerIntensity - negativeAnswerIntensity; } else
	 * return null; } else { if(negativeAnswer != null) { action =
	 * negativeAnswer.getName(); actionValue = negativeAnswerIntensity -
	 * positiveAnswerIntensity; } else return null; }
	 * 
	 * return new ValuedAction(action,actionValue); }
	 */

	/**
	 * Deliberative appraisal process. Checks goal activation and inserts
	 * intentions to achieve recently activated goals. Generates initial
	 * Hope/Fear emotions for each activated goal.
	 */
	public void Appraisal() {
		ListIterator li;
		ListIterator li2;
		Goal g;
		Event event;
		ActivePursuitGoal aGoal;
		ActivePursuitGoal intention;
		SubstitutionSet subSet;
		ArrayList substitutionSets;

		if (_actionMonitor != null && _actionMonitor.Expired()) {
			System.out.println("Action monitor expired");
			// If the action expired we must check the plan links (continuous
			// planning)
			// just to make sure
			_planner.CheckLinks();
			if (_actionMonitor.GetStep().getName().toString().startsWith(
					"WaitFor")) {
				_actionMonitor.GetStep().UpdateEffectsProbability();
			} else {
				_actionMonitor.GetStep().DecreaseProbability();
			}
			_planner.UpdateProbabilities();
			_actionMonitor = null;
		}

		synchronized (_eventPool) {
			li = _eventPool.listIterator();
			if (li.hasNext()) {
				if (_actionMonitor != null) {
					while (li.hasNext()) {
						event = (Event) li.next();
						if (_actionMonitor.MatchEvent(event)) {
							System.out.println("Action Monitor matched event: "
									+ event.toString());
							ControlPanel
									.writeLog("Action monitor matched event: "
											+ event.toString());
							if (_actionMonitor.GetStep().getAgent()
									.isGrounded()
									&& !_actionMonitor.GetStep().getAgent()
											.toString().equals(
													AutobiographicalMemory
															.GetInstance()
															.getSelf())) {
								// the agent was waiting for an action of other
								// agent to be complete
								// since the step of another agent may contain
								// unbound variables,
								// we cannot just compare the names, we need to
								// try to unify them
								if (Unifier.Unify(event.toStepName(),
										_actionMonitor.GetStep().getName()) != null) {
									_actionMonitor.GetStep()
											.IncreaseProbability();
									_actionMonitor.GetStep()
											.UpdateEffectsProbability();
								} else {
									_actionMonitor.GetStep()
											.DecreaseProbability();
								}
							} else {
								_actionMonitor.GetStep()
										.UpdateEffectsProbability();
							}

							_planner.UpdateProbabilities();
							_actionMonitor = null;
							break;
						}
					}
				}
				_eventPool.clear();

				// If there were any external events we must update the plans
				// according to the continuous planning techniques
				_planner.CheckLinks();
			}
		}

		// TODO GARANTIR QUE SEMPRE QUE UM PLANO É ACTUALIZADO a EMOÇÃO É
		// ACTUALIZADA

		// TODO optimize the goal activation verification
		synchronized (this) {
			// a bit of a hack... to get the agent to wait until the PC tells
			// him to move on

			// if the PC is telling the agent to wait
			// then reset the KB to re-activate the Wait goal
			if (KnowledgeBase.GetInstance().AskProperty(
					Name.ParseName("pcAgentWait()")) != null) {
				if (KnowledgeBase.GetInstance().AskProperty(
						Name.ParseName("pcAgentWait()")).equals("True")) {
					KnowledgeBase.GetInstance().Tell(Name.ParseName("wait()"),
							"True");
				}
			}
			// to keep the explore goal continuously activated
			if (KnowledgeBase.GetInstance().AskProperty(
					Name.ParseName("explore()")) != null) {
				if (KnowledgeBase.GetInstance().AskProperty(
						Name.ParseName("explore()")).equals("True")) {
					KnowledgeBase.GetInstance().Tell(
							Name.ParseName("interactWithObject()"), "False");
				}
			}

			li = _goals.listIterator();
			while (li.hasNext()) {
				g = (Goal) li.next();
				if (g instanceof ActivePursuitGoal) {
					aGoal = (ActivePursuitGoal) g;
					substitutionSets = Condition.CheckActivation(aGoal
							.GetPreconditions());
					if (substitutionSets != null) {
						// System.out.println("Substitution sets for " + g +
						// " are " + substitutionSets);
						li2 = substitutionSets.listIterator();
						while (li2.hasNext()) {
							subSet = (SubstitutionSet) li2.next();
							intention = (ActivePursuitGoal) aGoal.clone();
							intention.MakeGround(subSet.GetSubstitutions());
							_planner.AddIntention(intention);
						}
					}
				}
			}
		}
	}

	/**
	 * Deliberative Coping process. Gets the most relevant intention, thinks
	 * about it for one reasoning cycle (planning) and if possible selects an
	 * action for execution.
	 */
	public void Coping() {
		Intention i;
		ActiveEmotion fear;
		ActiveEmotion hope;

		Step copingAction;

		_selectedActionEmotion = null;
		_selectedAction = null;
		_selectedPlan = null;

		i = _planner.GetMostRelevantIntention();
		if (i != null) {
			// System.out.println("DP Current Intention: " +
			// i.getGoal().GetName());
			_selectedPlan = _planner.ThinkAbout(i);
			// if (_selectedPlan != null) System.out.println("DP Plan steps: " +
			// _selectedPlan.getSteps());
		}

		// a hack to communicate action failure in the EchoesRemoteAgent to the
		// deliberative process
		if (KnowledgeBase.GetInstance().AskProperty(
				Name.ParseName("actionFailed()")) != null
				&& KnowledgeBase.GetInstance().AskProperty(
						Name.ParseName("actionFailed()")).equals("True")) {
			KnowledgeBase.GetInstance().Tell(Name.ParseName("actionFailed()"),
					"False");
		//	System.out
		//			.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! setting action monitor back to null");
			_actionMonitor = null;
		}

		if (_actionMonitor == null && _selectedPlan != null) {
			copingAction = _selectedPlan.UnexecutedAction();
			ControlPanel
					.writeLog("Coping action (unexecuted action from plan) "
							+ _selectedPlan + " is " + copingAction);
			if (copingAction != null) {
				if (!copingAction.getName().GetFirstLiteral().toString()
						.startsWith("Inference")) {
					i.SetAnActionWasMade(true);
					fear = i.GetFear();
					hope = i.GetHope();
					if (hope != null) {
						if (fear != null) {
							if (hope.GetIntensity() >= fear.GetIntensity()) {
								_selectedActionEmotion = hope;
							} else {
								_selectedActionEmotion = fear;
							}
						} else {
							_selectedActionEmotion = hope;
						}
					} else {
						_selectedActionEmotion = fear;
					}

					_selectedAction = copingAction;
				} else {
					// this should never be selected
					System.out
							.println("InferenceOperator selected for execution");
				}
			} else {
				// If a complete plan does not have a valid next action to
				// execute (ex: the next
				// action to execute by self contains unboundvariables),
				// it means that the plan cannot be executed, and the plan must
				// be removed
				i.RemovePlan(_selectedPlan);
				System.out.println("Plan with invalid next action removed!");
			}
		}

	}

	public void AppraiseSelfActionFailed(Event e) {
		if (_actionMonitor != null) {
			if (_actionMonitor.MatchEvent(e)) {
				_actionMonitor = null;
			}
		}
	}

	/**
	 * Gets the action selected in the coping cycle, if any.
	 * 
	 * @return the action selected for execution, or null if no such action
	 *         exists
	 */
	public ValuedAction GetSelectedAction() {

		Event e;

		if (_selectedAction == null) {
			return null;
		}

		/*
		 * Prepares the action selected for execution, by adding a monitor to it
		 * (that detects when the action finishes). Finally it removes the
		 * action from the selected state so that it is not selected again.
		 */

		if (!_selectedAction.getAgent().isGrounded()) {
			// in this situation the agent that is going to perform the action
			// is not defined. Since the agent needs this action to be performed
			// it will check if he can do it himself by applying the [SELF]
			// substitution to the plan and testing if the resulting plan is
			// valid

			Substitution sub = new Substitution(_selectedAction.getAgent(),
					new Symbol(_self));

			Plan clonedPlan = (Plan) _selectedPlan.clone();
			clonedPlan.AddBindingConstraint(sub);
			if (clonedPlan.isValid()) {
				// this means that the agent can indeed perform the action

				// TODO I should clone the step before grounding it,
				// however I can only do it if the UpdatePlan method in class
				// Plan starts to work propertly with unbound preconditions
				// _selectedAction = (Step) _selectedAction.clone();
				// I know that this sucks, but for the moment I have to do it
				// like this
				_selectedPlan.AddBindingConstraint(sub);
				_selectedPlan.CheckProtectedConstraints();
				_selectedPlan.CheckCausalConflicts();
			} else {
				// the agent cannot perform the action, we must inform
				// the step that it cannot be executed by the agent, so
				// that the plan's probability is correctly updated
				_selectedAction.SetSelfExecutable(false);
			}
		} else if (!_selectedAction.getAgent().toString().equals(_self)) {
			// we have to wait for another agent to act
			System.out.println("Waiting for agent "
					+ _selectedAction.getAgent().toString() + " to do:"
					+ _selectedAction.getName().toString());
			System.out.println("Probability: "
					+ _selectedAction.GetProbability());
			e = new Event(_selectedAction.getAgent().toString(), null, null);
			_actionMonitor = new ExpirableActionMonitor(waitingTime,
					_selectedAction, e);
			_selectedAction = null;
			_selectedActionEmotion = null;
			return null;
		}

		return new ValuedAction(_selectedAction.getName(),
				_selectedActionEmotion);
	}

	public void RemoveSelectedAction() {
		String action;
		String target = null;
		Event e;

		if (_selectedAction == null) {
			return;
		}

		ListIterator li = _selectedAction.getName().GetLiteralList()
				.listIterator();

		action = li.next().toString();

		if (li.hasNext()) {
			target = li.next().toString();
		}

		e = new Event(_self, action, target);
		_actionMonitor = new ActionMonitor(_selectedAction, e);

		while (li.hasNext()) {
			e.AddParameter(new Parameter("parameter", li.next().toString()));
		}

		_selectedActionEmotion = null;
		_selectedAction = null;
	}

	/**
	 * Resets the deliberative layer. Clears the events to be appraised, the
	 * current intentions and actions.
	 */
	public void Reset() {
		_eventPool.clear();
		_planner.ClearGoals();
		_actionMonitor = null;
		_selectedAction = null;
		_selectedActionEmotion = null;
	}

	/**
	 * Prepares the deliberative layer for a shutdown
	 */
	public void ShutDown() {
	}

	public Plan getSelectedPlan() {
		return _selectedPlan;
	}
}
