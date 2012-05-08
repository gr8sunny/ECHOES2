/** 
 * EmotionalPlanner.java - Implements a Partially Ordered Continuous Planner that
 * uses problem-focused and emotion-focused strategies. The strategies applied to
 * the plan depend on the character's emotional state.
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
 * João Dias: 16/05/2006 - Made changes according to changes in Plan
 * João Dias: 16/05/2006 - Instead of selecting the more recent openPrecondition to solve
 * 						   we now select the oldest one
 * João Dias: 16/05/2006 - Fixed a bug related to resolving CausalConflicts which made the
 * 						   original plan with the conflict to not be removed from the intention structure
 * João Dias: 24/05/2006 - Added comments to each public method's header
 * João Dias: 10/07/2006 - the class is now serializable 
 * João Dias: 12/07/2006 - Replaced the deprecated Ground methods for the new ones.
 * João Dias: 15/07/2006 - Removed the KnowledgeBase from the Class fields since the KB is now
 * 						   a singleton that can be used anywhere without previous references.
 * 						 - Removed the EmotionalState from the Class fields since the 
 * 						   EmotionalState is now a singleton that can be used anywhere 
 * 					       without previous references.
 * 						 - Very important change in the way that mood was used to influence
 * 						   Acceptance coping strategy. Now it works exactly opposite to how 
 * 						   was working before.
 * João Dias: 20/07/2006 - Changed the method AddConstraints to AddConstraint that now receives
 * 						   one ProtectedCondition at a time;
 * João Dias: 31/08/2006 - Fixed a small bug in FindStepFor method that would occur when we tried
 * 					       to find a step for a PredicateCondition (and before was highlited as ToDo)
 * João Dias: 21/08/2006 - Changed UpdateProbability Method. There were no sense in updating a Step's
 * 						   effects probability inside this class. This method now forces the recalculation
 * 						   of every plan's probability. It doesn't receive any argument anymore.
 * João Dias: 26/09/2006 - Solved a bug introduced by planning with actions of other agents. There 
 * 						   was a problem detecting threats to protected conditions in the plan, because
 * 						   the checking was made only when a new step was introduced in the plan, which
 * 						   is not enough. Now we call the method CheckProtectionConstraints also when 
 * 						   new bindingconstraints are added.
 * João Dias: 27/09/2006 - Changes in the way that Acceptance Coping strategy is applied when we
 * 						   consider threats to interest goals. Now we always determine fear with
 * 						   probability 1.0 (if we accept the goal's failure, it will fail with 100%
 * 						   probability). And everytime that a plan is dropped or the goal fails, we
 * 						   use MentalDisengagement to lower the goal's importance of failure by 0.5
 * 						 - An ActivePursuit Goal can now become active more than once (even two or more
 * 						   in a row) if its preconditions are verified (and of course the goal is not
 * 						   activated already).
 * João Dias: 28/09/2006 - Now, the selection of the most relevant intention can be done properly
 * 						   even if there are no emotions active (because of personality thresholds),
 * 						   in this situation we use expected utility (or penalty) to selected between
 * 						   intentions
 * João Dias: 02/10/2006 - Small changes in the way that CausalLinks are created, due to the disappearing
 * 						   of the link's probability from a CausalLink
 * João Dias: 03/10/2006 - Removed the duplicated and redundant method AddConstraint(ProtectionCondition)
 * 						   You can use the AddProtectionConstraing(ProtectedCondition) that does the same
 * 						   thing.
 * João Dias: 04/10/2006 - The planner now calls the method GetValidInequalities() instead of the method
 * 						   GetValidBindings() when he wants to test if a given PropertyNotEqual condition
 * 						   can be verified by the Start step
 * João Dias: 22/12/2006 - Intentions are now synchronized. There were some situations were trying to externally
 * 						   removing intentions caused synchronization errors.
 * João Dias: 27/01/2007 - Solved one problem that was happening because I was considering that once you find an effect
 * 						   of a given step that achieves the precondition you want, you can stop looking in other effects
 * 						   of that step. This consideration was wrong, so I now test every effect allways.
 * João Dias: 03/02/2007 - The success or failure of an active goal is now registered in the AM instead of the KB
 * 						 - Renamed the methods RegisterGoalFailure and RegisterGoalSuccess to RegisterIntentionFailure and
 * 						   RegisterIntentionSuccess. Changed the method signature. 
 * João Dias: 15/02/2007 - Slightly changed the event associated to prospect based emotions when they are stored
 * 						   in the EmotionalState or AM. The changes were performed in the methods RegisterIntentionFailure
 * 						   and RegisterIntentionSuccess
 * João Dias: 18/02/2007 - Before, the activation of a given intention was only stored in the AM if it generated emotions. 
 * 						   Now the activation is allways registered independently of generating emotions or not
 * 						 - Added private method RegisterGoalActivation
 * 						 - Refactorization: the creation of the event used to described a goal's activation, success 
 * 					       and failure is now handled by calling the corresponding methods in the class Goal
 */

package FAtiMA.deliberativeLayer;

/**
 * Implements a Partially Ordered Continuous Planner that
 * uses problem-focused and emotion-focused strategies. The strategies applied to
 * the plan depend on the character's emotional state.
 * 
 * @author João Dias
 */

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Set;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import echoesEngine.ControlPanel;
import FAtiMA.IntegrityValidator;
import FAtiMA.autobiographicalMemory.AutobiographicalMemory;
import FAtiMA.conditions.Condition;
import FAtiMA.conditions.PropertyNotEqual;
import FAtiMA.deliberativeLayer.goals.ActivePursuitGoal;
import FAtiMA.deliberativeLayer.goals.Goal;
import FAtiMA.deliberativeLayer.plan.CausalConflictFlaw;
import FAtiMA.deliberativeLayer.plan.CausalLink;
import FAtiMA.deliberativeLayer.plan.Effect;
import FAtiMA.deliberativeLayer.plan.GoalThreat;
import FAtiMA.deliberativeLayer.plan.OpenPrecondition;
import FAtiMA.deliberativeLayer.plan.Plan;
import FAtiMA.deliberativeLayer.plan.ProtectedCondition;
import FAtiMA.deliberativeLayer.plan.Step;
import FAtiMA.emotionalState.ActiveEmotion;
import FAtiMA.emotionalState.EmotionalState;
import FAtiMA.exceptions.ActionsParsingException;
import FAtiMA.exceptions.UnknownSpeechActException;
import FAtiMA.exceptions.UnspecifiedVariableException;
import FAtiMA.knowledgeBase.KnowledgeBase;
import FAtiMA.sensorEffector.Event;
import FAtiMA.util.parsers.StripsOperatorsLoaderHandler;
import FAtiMA.wellFormedNames.Name;
import FAtiMA.wellFormedNames.SubstitutionSet;
import FAtiMA.wellFormedNames.Unifier;

public class EmotionalPlanner implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ArrayList _actions;
	private HashMap _intentions;
	// private HashMap _closedGoals;
	private ArrayList _protectionConstraints;
	private int _variableIdentifier;

	/**
	 * Creates a new EmotionalPlanner
	 * 
	 * @param operatorsFile
	 *            - the file that contains the actions to be used in the planner
	 * @param self
	 *            - the agent's name
	 * 
	 * @throws ActionsParsingException
	 *             - thrown when there is an error parsing the actions to be
	 *             used in the planner
	 * @throws UnspecifiedVariableException
	 *             - thrown when an action in the plan uses a unbound variable
	 *             in it's effects or preconditions withoud specifying it in the
	 *             action's name
	 */
	public EmotionalPlanner(String operatorsFile, String self)
			throws ActionsParsingException, UnspecifiedVariableException {
		_variableIdentifier = 1;
		_intentions = new HashMap();
		// _closedGoals = new HashMap();
		_actions = new ArrayList();
		_protectionConstraints = new ArrayList();

		Load(operatorsFile, self);
	}

	/**
	 * Creates a new EmotionalPlanner
	 * 
	 * @param operators
	 *            - a list with the actions to be used in the planner
	 * @param es
	 *            - the character's emotional state
	 */
	public EmotionalPlanner(ArrayList operators) {
		this._variableIdentifier = 1;
		this._intentions = new HashMap();
		// this._closedGoals = new HashMap();
		this._actions = operators;
		this._protectionConstraints = new ArrayList();
	}

	/**
	 * Creates and Adds an intention to the set of intentions that the planner
	 * is currently trying to achieve (however the planner only picks one of
	 * them at each reasoning cycle)
	 * 
	 * @param goal
	 *            - the goal that we want to add
	 */
	public void AddIntention(ActivePursuitGoal goal) {
		Plan newPlan;
		Intention intention;

		synchronized (this) {
			if (!_intentions.containsKey(goal.GetName().toString())) {
				// && !_closedGoals.containsKey((goal.GetName().toString()))) {
				// System.out.println("Adding a new Intention: " +
				// goal.GetName().toString());
				// System.out.println("Objective: " +
				// goal.GetSuccessConditions());
				newPlan = new Plan(_protectionConstraints, goal);

				intention = new Intention(newPlan, goal);
				_intentions.put(goal.GetName().toString(), intention);
				// System.out.println("adding goal: " +
				// goal.GetName().toString());
				RegisterIntentionActivation(intention);
			}
		}
	}

	/**
	 * Adds a ProtectionConstraint to the Planner. The planner will detect when
	 * there are threats to these ProtectionConstraints and deal with them with
	 * emotion-focused coping strategies.
	 * 
	 * @param cond
	 *            - the ProtectedCondition to add
	 * @see ProtectedCondition
	 */
	public void AddProtectionConstraint(ProtectedCondition cond) {
		_protectionConstraints.add(cond);
	}

	/**
	 * Adds an operator/step/action to the planner, so that he can used in when
	 * developing planns
	 * 
	 * @param op
	 *            - the operator to add
	 * @see Step
	 */
	public void AddStripsOperator(Step op) {
		_actions.add(op);
	}

	/**
	 * Appraises an possible answer to a SpeechAct, to see what kind of effects
	 * the answer will have in the agent's plans. The method then returns an
	 * overall value of utility for the answer. If the utility is negative, it
	 * means that the answer has negative effects in the plans, it the utility
	 * is positive the answer has positive effects.
	 * 
	 * @param step
	 *            - the step that corresponds to an answer speechAct
	 * @return the overall utility of the answer (according to the answer's
	 *         effect in the agent's plans)
	 */
	public float AppraiseAnswer(Step step) {
		ListIterator li;
		ListIterator li2;
		ProtectedCondition pCond;
		Condition cond;
		Effect eff;
		float prob;
		float answerUtility = 0;
		ActiveEmotion threatEmotion;

		// TODO considerar a hipótese de fazer um teste mais completo...

		// the next code checks if the step threatens a protection constraint
		li = _protectionConstraints.listIterator();
		while (li.hasNext()) {
			pCond = (ProtectedCondition) li.next();
			cond = pCond.getCond();
			li2 = step.getEffects().listIterator();
			while (li2.hasNext()) {
				eff = (Effect) li2.next();
				if (eff.GetEffect().ThreatensCondition(cond)) {
					prob = eff.GetProbability();
					threatEmotion = EmotionalState.GetInstance()
							.AppraiseGoalFailureProbability(pCond.getGoal(),
									prob);
					if (threatEmotion != null) {
						answerUtility -= threatEmotion.GetIntensity();
					}
				}
			}
		}

		return answerUtility;
	}

	/**
	 * Checks the integrity of the Planner operators/Steps/actions. For instance
	 * it checks if a operator references a SpeechAct not defined, or if it uses
	 * a unbound variable (in effects or preconditions) not used in the
	 * operator's name
	 * 
	 * @param val
	 *            - the IntegrityValidator used to detect problems
	 * @throws UnspecifiedVariableException
	 *             - thrown when the operator uses a unbound variable in the
	 *             effects or preconditions without using the same variable in
	 *             the step's name
	 * @throws UnknownSpeechActException
	 *             - thrown when the operator references a SpeechAct not defined
	 */
	public void CheckIntegrity(IntegrityValidator val)
			throws UnspecifiedVariableException, UnknownSpeechActException {
		ListIterator li = _actions.listIterator();

		while (li.hasNext()) {
			((Step) li.next()).CheckIntegrity(val);
		}
	}

	/**
	 * Updates all the plans that the emotional planner is currently working
	 * with, i.e., it updates all plans of all current active intentions
	 */
	public void CheckLinks() {
		Iterator it;

		synchronized (this) {
			it = _intentions.values().iterator();
			while (it.hasNext()) {
				((Intention) it.next()).CheckLinks();
			}
		}
	}

	/**
	 * Clears the planner current goals and intentions
	 * 
	 */
	public void ClearGoals() {
		synchronized (this) {
			_intentions.clear();
			// _closedGoals.clear();
		}
	}

	/**
	 * Registers and appraises the activation of a given intention
	 * 
	 * @param intention
	 *            - the intention that was activated
	 */
	private void RegisterIntentionActivation(Intention i) {
		Goal g = i.getGoal();
		Event e = g.GetActivationEvent();

		AutobiographicalMemory.GetInstance().StoreAction(e);

		float probability = i.GetProbability();
		ActiveEmotion hope = EmotionalState.GetInstance()
				.AppraiseGoalSucessProbability(g, probability);
		ActiveEmotion fear = EmotionalState.GetInstance()
				.AppraiseGoalFailureProbability(g, 1 - probability);

		i.SetHope(hope);
		i.SetFear(fear);
	}

	/**
	 * Registers and appraises the failure of a given intention
	 * 
	 * @param intention
	 *            - the intention that failed
	 */
	private void RegisterIntentionFailure(Intention i) {
		Goal g = i.getGoal();
		Event e = g.GetFailureEvent();

		AutobiographicalMemory.GetInstance().StoreAction(e);
		EmotionalState.GetInstance().AppraiseGoalFailure(i.GetHope(),
				i.GetFear(), g);

		// _closedGoals.put(g.GetName().toString(),g);
	}

	/**
	 * Registers and appraises the success of a given intention
	 * 
	 * @param intention
	 *            - the intention that has succeeded
	 */
	private void RegisterIntentionSuccess(Intention i) {
		Goal g = i.getGoal();
		Event e = g.GetSuccessEvent();

		AutobiographicalMemory.GetInstance().StoreAction(e);
		EmotionalState.GetInstance().AppraiseGoalSuccess(i.GetHope(),
				i.GetFear(), g);

		// _closedGoals.put(g.GetName().toString(),g);
	}

	/**
	 * Tries to find steps that achieves a given precondition and adds each one
	 * of those steps as possible plan alternatives
	 * 
	 * @param intention
	 *            - the intention that this plan tries to achieve
	 * @param p
	 *            - the plan that the method analizes
	 * @param openPrecond
	 *            - the precondition that we want to satisfy
	 * @param newStep
	 *            - a boolean variable stating if the method should look for
	 *            steps that already exist in the plan, or for new steps from
	 *            the list of possible operators. true - gets a new Step, false
	 *            - uses the steps that the received plan contains
	 */
	public void FindStepFor(Intention intention, Plan p,
			OpenPrecondition openPrecond, boolean newStep) {
		ListIterator li;
		Condition cond;
		Effect effect;
		Condition effectCond;
		ArrayList substs;
		Name condValue;
		Name effectValue;
		Step step;
		Step stepToAdd;
		Step oldStep;
		Plan newPlan;
		boolean unifyResult;

		// used when checking if the step has a precondition that is determined
		// by the child model and is contrary to current value. If so this step
		// is not considered.
		boolean skipThisStep;
		// used to shuffle available steps
		ArrayList availableSteps = new ArrayList();

		oldStep = p.getStep(openPrecond.getStep());
		cond = oldStep.getPrecondition(openPrecond.getCondition());

		if (newStep) {
			ListIterator it1 = _actions.listIterator();
			while (it1.hasNext()) {
				availableSteps.add(it1.next());
			}
		} else {
			ListIterator it2 = p.getSteps().listIterator();
			while (it2.hasNext()) {
				availableSteps.add(it2.next());
			}
		}

		for (int i = 0; i < 3; i++) {
			Collections.shuffle(availableSteps);
		}

		li = availableSteps.listIterator();

		while (li.hasNext()) {
			step = (Step) li.next();
			skipThisStep = false;
			if (!step.getName().equals(oldStep.getName())) {
				if (newStep) {
					step = (Step) step.clone();
					step.ReplaceUnboundVariables(_variableIdentifier);
				}

				// check whether the step's preconditions goes against what the
				// child model dictates. Used to prevent adding user actions
				// when child model dictates not to, otherwise planner goes into
				// perpetual action of adding user actions and then removing
				// them.
				if (checkStepAgainstPedagogicalComponentDirections(step)) {
					skipThisStep = true;
				}

				if (!skipThisStep) {
					for (int i = 0; i < step.getEffects().size(); i++) {
						effect = (Effect) step.getEffects().get(i);
						effectCond = effect.GetEffect();
						substs = new ArrayList();
						if (Unifier.Unify(cond.getName(), effectCond.getName(),
								substs)) {
							condValue = cond.GetValue();
							effectValue = effectCond.GetValue();
							unifyResult = Unifier.Unify(condValue, effectValue,
									substs);

							if (cond instanceof PropertyNotEqual) {
								return;
								// unifyResult = !unifyResult;
							}
							if (unifyResult) {
								newPlan = (Plan) p.clone();

								if (newStep) {
									stepToAdd = (Step) step.clone();
									_variableIdentifier++;
									newPlan.AddStep(stepToAdd);
								} else {
									stepToAdd = step;
								}

								ControlPanel.writeLog("adding step to plan: "
										+ step);
								ControlPanel.writeLog("because of effect: "
										+ effect);

								newPlan.AddLink(new CausalLink(stepToAdd
										.getID(), new Integer(i), openPrecond
										.getStep(), openPrecond.getCondition(),
										effect.toString()));
								ControlPanel
										.writeLog("Adding binding constraints");
								newPlan.AddBindingConstraints(substs);
								ControlPanel
										.writeLog("Checking causal conflicts");
								newPlan.CheckCausalConflicts();
								ControlPanel
										.writeLog("Checking protected constraints");
								newPlan.CheckProtectedConstraints();
								if (newPlan.isValid()) {
									// System.out.println("Adding new plan from FindStep: "
									// + newPlan);
									intention.AddPlan(newPlan);
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Gets the most relevant intention from the set of possible
	 * intentions/goals. Corresponds to Focusing on a given goal
	 * 
	 * @return - the most relevant intention (the one supported by the strongest
	 *         emotions)
	 */
	public Intention GetMostRelevantIntention() {
		Iterator it;
		ActiveEmotion fearEmotion;
		ActiveEmotion hopeEmotion;
		ActivePursuitGoal g;
		Intention intention;
		Intention maxIntention = null;

		float highestIntensity = 0;
		float fearIntensity;
		float hopeIntensity;
		float intensity;

		synchronized (this) {
			it = _intentions.values().iterator();

			while (it.hasNext()) {

				hopeIntensity = 0;
				fearIntensity = 0;

				intention = (Intention) it.next();

				if ((hopeEmotion = intention.GetHope()) != null) {
					hopeIntensity = hopeEmotion.GetIntensity();
				}
				if ((fearEmotion = intention.GetFear()) != null) {
					fearIntensity = fearEmotion.GetIntensity();
				}

				g = intention.getGoal();
				if (g.CheckSucess()) { //
					// System.out.println("Goal Success: " + g.GetName());
					it.remove();
					RegisterIntentionSuccess(intention);
				} else if (g.CheckFailure()) {
					System.out.println("Goal Failure: " + g.GetName());
					it.remove();
					RegisterIntentionFailure(intention);
				} else {
					// comment out to make sure selection is based on importance
					// of
					// success/failure only
					// it's been a bit unpredictable

					// intensity = Math.max(hopeIntensity, fearIntensity);

					// if (intensity > highestIntensity) {
					// highestIntensity = intensity;
					// maxIntention = intention;
					// }
				}
			}

			if (_intentions.size() > 0 && maxIntention == null) {
				// if we reached this point, it means that there are intentions,
				// but none of them is generating emotions, so we must check the
				// intention
				// list again, comparing just importanceOfsuccess and
				// ImportanceOfFailure
				highestIntensity = -1; // just to make sure we're going to
				// select one
				for (it = this._intentions.values().iterator(); it.hasNext();) {
					intention = (Intention) it.next();
					// float p = intention.GetProbability();
					hopeIntensity = intention.getGoal()
							.GetImportanceOfSuccess();
					// * p;
					// fearIntensity = intention.getGoal()
					// .GetImportanceOfFailure();
					// * (1 - p);
					// intensity = Math.max(hopeIntensity, fearIntensity);
					intensity = hopeIntensity;
					if (intensity > highestIntensity) {
						highestIntensity = intensity;
						maxIntention = intention;
					}
				}
			}
		}

		// System.out.println("intentions: " + _intentions + " max intention: "
		// + maxIntention);
		return maxIntention;
	}

	/**
	 * Gets the planner's operators/sets/actions
	 * 
	 * @return a list with Steps
	 */
	public ArrayList GetOperators() {
		return _actions;
	}

	/**
	 * Gets a set of IntentionKeys
	 * 
	 * @return a set with the keys used to store all intentions
	 */
	public Set GetIntentionKeysSet() {
		synchronized (this) {
			return _intentions.keySet();
		}
	}

	/**
	 * Gets a iterator that allows you to iterate over the set of active
	 * Intentions
	 * 
	 * @return
	 */
	public Iterator GetIntentionsIterator() {
		return _intentions.values().iterator();
	}

	/**
	 * Gets the operator that corresponds to the given name
	 * 
	 * @param name
	 *            - the name of the step to get
	 * @return the searched step if it is found, null otherwise
	 */
	public Step GetStep(Name name) {
		ListIterator li;
		Step s;
		ArrayList substs;
		ArrayList bestSubsts = null;
		Step bestStep = null;

		li = _actions.listIterator();
		while (li.hasNext()) {
			s = (Step) li.next();
			substs = new ArrayList();
			if (Unifier.Unify(s.getName(), name, substs)) {
				if (bestSubsts != null) {
					if (substs.size() < bestSubsts.size()) {
						bestSubsts = substs;
						bestStep = (Step) s.clone();
						bestStep.MakeGround(substs);
					}
				} else {
					bestSubsts = substs;
					bestStep = (Step) s.clone();
					bestStep.MakeGround(substs);
				}
			}
		}
		return bestStep;
	}

	/**
	 * Implements a cycle of the reasoning/planning process. Given an Intention,
	 * it selects the best current plan to achieve the intention. Next it brings
	 * the best plan into focus and generates/updates emotions: Hope and Fear.
	 * Afterwards, these emotions together with mood will be used to decide what
	 * kind of coping strategy will be applied to fix the plan's flaw
	 * 
	 * @param intention
	 *            - the intention that will be the focus of reasoning
	 * @return - if the best plan for the intention is complete and no flaws
	 *         were detected, this best plan is returned. If not, the method
	 *         returns null
	 */
	public Plan ThinkAbout(Intention intention) {
		Plan p;
		Plan newPlan;
		/**
		 * indicates whether there has been a change to the plan (that is the
		 * best plan that the method is working with) so that it can remove that
		 * plan
		 */
		boolean newPlans = false;
		CausalConflictFlaw flaw;
		SubstitutionSet subSet;
		OpenPrecondition openPrecond;
		Condition cond;
		ArrayList substitutionSets;
		ArrayList openConditions;
		ListIterator li;
		GoalThreat goalThreat;

		ActiveEmotion fearEmotion;
		ActiveEmotion hopeEmotion;
		ActiveEmotion threatEmotion;

		float fearIntensity = 0;
		float hopeIntensity = 0;
		float threatIntensity = 0;
		float prob;

		// ControlPanel.writeLog("Current plans: " + intention.getPlans());

		// remove plans that are inconsistent with Child Model direction
		// need to check because the CM values will change during the execution
		// of a plan and so need to remove the plans that are currently stored
		// as alternatives
		ArrayList plansToRemove = new ArrayList();

		ListIterator intPlans = (ListIterator) intention.getPlans()
				.listIterator();
		while (intPlans.hasNext()) {
			Plan pl = (Plan) intPlans.next();
			ListIterator plSteps = pl.getSteps().listIterator();
			while (plSteps.hasNext()) {
				if (checkStepAgainstPedagogicalComponentDirections((Step) plSteps
						.next())) {
					plansToRemove.add(pl);
				}
			}
		}

		ListIterator plansToRemoveIt = plansToRemove.listIterator();
		while (plansToRemoveIt.hasNext()) {
			Plan pl = (Plan) plansToRemoveIt.next();
			System.out.println("removing plan: " + pl);
			intention.RemovePlan(pl);

		}

		p = intention.GetBestPlan(); // gets the best plan so far to achieve the
		// intention
		// System.out.println("BEST PLAN: " + p);
		ControlPanel.writeLog("Best plan: " + p);

		// just for the print out
		if (p != null) {
			ControlPanel.writeLog("p.GetOpenPreconditions() = "
					+ p.getOpenPreconditions());
			ControlPanel.writeLog("p.getSteps() = " + p.getSteps());
		}

		if (p == null) {
			// There's no possible plan to achieve the goal, the goal fails
			// mental disengagement consists in lowering the goal's importance
			intention.getGoal().DecreaseImportanceOfFailure(0.5f);
			synchronized (this) {
				_intentions.remove(intention.getGoal().GetName().toString());
			}
			RegisterIntentionFailure(intention);
			System.out.println("(1) Goal FAILED - "
					+ intention.getGoal().GetName().toString());
			ControlPanel.writeLog("(1) Goal FAILED - "
					+ intention.getGoal().GetName().toString());

			resetSelectedFailedGoals(intention.getGoal().GetName().toString());
			return null;
		} else if (p.getOpenPreconditions().size() == 0
				&& p.getSteps().size() == 0) {
			// There aren't open conditions left and no steps in the plan, it
			// means that the goal has been achieved
			synchronized (this) {
				_intentions.remove(intention.getGoal().GetName().toString());
			}
			RegisterIntentionSuccess(intention);
			System.out.println("Goal SUCCESS - "
					+ intention.getGoal().GetName().toString());
			ControlPanel.writeLog("Goal SUCCESS - "
					+ intention.getGoal().GetName().toString());
			return null;
		}

		// APPRAISAL/REAPPRAISAL - the plan brought into the agent's mind will
		// generate/update
		// hope and fear emotions according to the plan probability
		hopeEmotion = EmotionalState.GetInstance()
				.AppraiseGoalSucessProbability(intention.getGoal(),
						p.getProbability());
		fearEmotion = EmotionalState.GetInstance()
				.AppraiseGoalFailureProbability(intention.getGoal(),
						1 - p.getProbability());
		intention.SetHope(hopeEmotion);
		intention.SetFear(fearEmotion);
		if (hopeEmotion != null)
			hopeIntensity = hopeEmotion.GetIntensity();
		if (fearEmotion != null)
			fearIntensity = fearEmotion.GetIntensity();

		// emotion-focused coping: Acceptance - if the plan probability is too
		// low the agent will not consider
		// this plan, but the mood also influences this threshold, characters on
		// positive moods will give up
		// goals more easily and thus the threshold is higher, character on
		// negative moods will have a lower
		// threshold. This threshold is ranged between 5% and 15%, it is 10% for
		// characters in a neutral mood
		float threshold = 0.1f + EmotionalState.GetInstance().GetMood() * 0.0167f;
		if (p.getProbability() < threshold) {
			// this coping strategy is used in tandem with mental
			// disengagement...
			// that consists in lowering the goal importance
			intention.getGoal().DecreaseImportanceOfFailure(0.5f);
			intention.RemovePlan(p);
			System.out.println("ACCEPTANCE - Plan prob to low - "
					+ intention.getGoal().GetName().toString());
			ControlPanel.writeLog("ACCEPTANCE - Plan prob to low - "
					+ intention.getGoal().GetName().toString());
			// p.getSteps().get(p.getSteps().size()).
			System.out.println("knowledge base - last action prob: "
					+ KnowledgeBase.GetInstance().AskProperty(
							((Step) p.getSteps().get(p.getSteps().size() - 1))
									.GetBiasName()));
			// this means that the step probability becomes 2 for a user action,
			// because if a user action it adds
			// the knowledge base value to the base value
			Step lastStep = ((Step) p.getSteps().get(p.getSteps().size() - 1));
			KnowledgeBase.GetInstance().Tell(lastStep.GetBiasName(), (float) 0);
			// System.out.println("knowledge base - last action prob: " +
			// KnowledgeBase.GetInstance().AskProperty(lastStep.GetBiasName()));
			// System.out.println("Step name is: " +
			// lastStep.getName().GetFirstLiteral().getName());
			// if
			// (lastStep.getName().GetFirstLiteral().getName().equals("LookAtFace"))
			// {

			// }

			return null;
		}

		li = p.getThreatenedInterestConstraints().listIterator();
		while (li.hasNext()) {
			float threatImportance;
			float failureImportance;
			float aux;

			goalThreat = (GoalThreat) li.next();
			prob = goalThreat.getEffect().GetProbability();
			threatImportance = goalThreat.getCond().getGoal()
					.GetImportanceOfFailure();
			aux = prob * threatImportance;
			failureImportance = intention.getGoal().GetImportanceOfFailure();

			threatEmotion = EmotionalState.GetInstance()
					.AppraiseGoalFailureProbability(
							goalThreat.getCond().getGoal(), prob);
			if (threatEmotion != null) { // if does not exist a fear caused by
				// the threat, emotion coping is not
				// necessary
				threatIntensity = threatEmotion.GetIntensity();
			} else {
				threatIntensity = 0;
			}

			/*
			 * System.out.println("");
			 * System.out.println("Comparing coping emotions for the goal: " +
			 * intention.getGoal().GetName());
			 * System.out.println("Plan probability: " + p.getProbability());
			 * System.out.println("Plan steps: " + p.getSteps());
			 * System.out.println("ImportanceOfFailure: " +
			 * failureImportance*p.getProbability());
			 * System.out.println("ThreatFear: " + threatIntensity);
			 * System.out.println("Hope: " + hopeIntensity);
			 */

			/**
			 * we give up a plan in favour of a threat if the utility of
			 * pursuing the plan and ignoring the threat is lesser or equal than
			 * the utility of giving up the goal and avoiding the treath, which
			 * is given by the inequality: %g*IS(g) - (1-%g)*IF(g)) - %t*IF(t)
			 * <= - IF(g) where %g is the probability of achieving goal g, IS is
			 * the importance of success, IF is the importance of failure, %t is
			 * the chance of the threat t to occur due to the plan. It is
			 * important to mention that we're ignoring the Importance of
			 * avoiding the threat. The equation can be tranformed into: IF(g)
			 * <= (1-%g)*IF(g) + %t*IF(t) - %g*IS(g) By realizing that
			 * (1-%g)*IF(g) generates the Fear of not achieving the goal,
			 * %t*IF(t) generates the Fear of the threat and %g*IS(g) generates
			 * the hope of achieving the goal, we can use the intensity of
			 * emotions to determine whether to give up the goal IF(g) <=
			 * Intensity(Fear) + Intensity(ThreatFear) - Intensity(Hope)
			 * 
			 * This would be the ideal, but unfortunately emotions are too
			 * dependent on mood, and I cannot have two fear emotions being used
			 * to determine acceptance, so we have to use the following test
			 * instead
			 * 
			 */

			if (failureImportance * p.getProbability() <= threatIntensity
					- hopeIntensity) {
				// if(threatIntensity >= hopeIntensity && aux >=
				// failureImportance) {

				// this coping strategy is used in tandem with mental
				// disengagement...
				// that consists in lowering the goal importance
				intention.getGoal().DecreaseImportanceOfFailure(0.5f);
				// coping strategy: Acceptance. This plan is rejected by the
				// agent
				intention.RemovePlan(p);
				System.out.println("ACCEPTANCE - GoalThreat - "
						+ intention.getGoal().GetName().toString());
				ControlPanel.writeLog("ACCEPTANCE - GoalThreat - "
						+ intention.getGoal().GetName().toString());
				return null;
			} else {
				if (prob >= 0.7) {
					// coping strategy: Acceptance. The agent accepts that the
					// interest goal
					// will fail
					li.remove();
					goalThreat.getCond().getGoal().DecreaseImportanceOfFailure(
							0.5f);
					System.out.println("ACCEPTANCE - Interest goal droped - "
							+ goalThreat.getCond().getGoal().GetName());
					ControlPanel
							.writeLog("ACCEPTANCE - Interest goal droped - "
									+ goalThreat.getCond().getGoal().GetName());
				}
				/*
				 * else if(prob >= 0.2) { //Denial/Whishfull Thinking
				 * goalThreat.getEffect().DecreaseProbability();
				 * System.out.println
				 * ("DENIAL - Interest Effect probability lowered - " +
				 * goalThreat.getEffect().GetEffect()); }
				 */
			}
		}

		// emotion-focused coping: Denial/Positive Thinking
		ArrayList ignoredConflicts = p.getIgnoredConflicts();
		if (ignoredConflicts.size() > 0) {
			float deltaPot = fearIntensity - hopeIntensity;
			if (deltaPot >= 0 && deltaPot <= 2) {
				li = ignoredConflicts.listIterator();
				while (li.hasNext()) {
					flaw = (CausalConflictFlaw) li.next();
					flaw.GetEffect().DecreaseProbability();
					System.out.println("DENIAL - Effect probability lowered - "
							+ intention.getGoal().GetName().toString());
					ControlPanel
							.writeLog("DENIAL - Effect probability lowered - "
									+ intention.getGoal().GetName().toString());
				}
			}
		}

		// Causal conflicts: promotion, demotion or emotion focused coping (to
		// ignore the conflict
		flaw = p.NextFlaw();
		if (flaw != null) {
			newPlans = true;
			newPlan = (Plan) p.clone();
			newPlan.AddOrderingConstraint(
					flaw.GetCausalLink().getDestination(), flaw.GetStep());
			if (newPlan.isValid())
				intention.AddPlan(newPlan);

			newPlan = (Plan) p.clone();
			newPlan.AddOrderingConstraint(flaw.GetStep(), flaw.GetCausalLink()
					.getSource());
			if (newPlan.isValid())
				intention.AddPlan(newPlan);

			// the conflict is ignored)
			/*
			 * newPlan = (Plan) p.clone(); newPlan.IgnoreConflict(flaw); if
			 * (newPlan.isValid()) { intention.AddPlan(newPlan);
			 * System.out.println
			 * ("WISHFULLTHINKING - causal conflict ignored - " +
			 * intention.getGoal().GetName().toString()); }
			 */
			ControlPanel.writeLog("There is conflict/flaw: "
					+ " so plan is removed - plan: " + p);
			ControlPanel.writeLog("Flaw causal link: "
					+ flaw.GetCausalLink().getDestination());
			ControlPanel.writeLog("Flaw step: " + flaw.GetStep());
			ControlPanel.writeLog("Flaw effect: " + flaw.GetEffect());
			ControlPanel.writeLog("Link description "
					+ flaw.GetCausalLink().getDescription());

			// !!!!!!!!!!!!!! have commented out removing plan, so is ignoring
			// conflict because this was causing problems in the planning. E.g.
			// if pickUp object had been selected once then it couldn't be
			// selected again because HandsFree would become False

			// ControlPanel.writeLog("Ignoring conflict");
			intention.RemovePlan(p);
			if (intention.NumberOfAlternativePlans() == 0) {
				// mental disengagement consists in lowering the goal's
				// importance
				intention.getGoal().DecreaseImportanceOfFailure(0.5f);
				synchronized (this) {
					_intentions
							.remove(intention.getGoal().GetName().toString());
				}
				RegisterIntentionFailure(intention);
				System.out.println("(2) Goal FAILED - "
						+ intention.getGoal().GetName().toString());
				ControlPanel.writeLog("(2) Goal FAILED - "
						+ intention.getGoal().GetName().toString());
				System.out.println("-----------------------------------------");
				System.out.println("-----------------------------------------");

				resetSelectedFailedGoals(intention.getGoal().GetName()
						.toString());
			}
			return null;
		}

		// If there are open preconditions
		openConditions = p.getOpenPreconditions();
		if (openConditions.size() > 0) {
			newPlans = true;
			openPrecond = (OpenPrecondition) openConditions.remove(0);

			// gets the precondition (from the precondition ID)
			cond = p.getStep(openPrecond.getStep()).getPrecondition(
					openPrecond.getCondition());

			ControlPanel.writeLog("Plan: " + p);
			ControlPanel.writeLog("Step: " + p.getStep(openPrecond.getStep())
					+ " whose id is "
					+ p.getStep(openPrecond.getStep()).getID());
			ControlPanel.writeLog("Open Precondition: " + cond);

			// first we must determine if the condition is verified in the start
			// step
			// TODO I've just realized a PROBLEM, even if the condition is
			// grounded and verified
			// in the start step, we must check whether adding a new operator is
			// a better move!

			// need to have a check here for whether the step is a walkTo
			// action, but there is also a walkTo action that will occur before
			// it
			// so if the second walkTo action precondition is not verified (i.e.
			// the agent is at the location that the second walkTo action will
			// take him, then don't remove if there is going to be another
			// walkTo action beforehand, as this will move the agent again.
			// The problem is that if the second walkTo action IS removed, it
			// will get added later when needed (i.e. when the previous walkTo
			// action has now moved the agent), but it will be added ahead of
			// other actions, so the agent will end up walking back and forth
			boolean dontRemove = false;
			if (!cond.CheckCondition()
					&& p.getStep(openPrecond.getStep()).getName()
							.GetFirstLiteral().getName().equals("SelfWalkTo")) {
				for (ListIterator planStepLi = p.getSteps().listIterator(); planStepLi
						.hasNext();) {
					Step planStep = (Step) planStepLi.next();
					if (planStep.getID() > p.getStep(openPrecond.getStep())
							.getID()
							&& planStep.getName().GetFirstLiteral().getName()
									.equals("SelfWalkTo")) {
						ControlPanel
								.writeLog("There is also a walk to action that comes before so don't remove: "
										+ planStep.getID());
						dontRemove = true;
					}
				}
			}

			// this code is needed so that the WalkTo action is added even if
			// the agent is currently at the location. This is necessary for the
			// actions specified below, which also require an additional walk to
			// action to pick up something.
			boolean irrelevantThatTrue = false;
			if (cond.CheckCondition()) {
				if ((p.getStep(openPrecond.getStep()).getName()
						.GetFirstLiteral().getName().equals(
								"SelfPutFlowerInPot")
						|| p.getStep(openPrecond.getStep()).getName()
								.GetFirstLiteral().getName().equals(
										"SelfStackFlowerpot")
						|| p.getStep(openPrecond.getStep()).getName()
								.GetFirstLiteral().getName().equals(
										"SelfPlaceFlowerPotPond") || p.getStep(
						openPrecond.getStep()).getName().GetFirstLiteral()
						.getName().equals("SelfExploreCloudAndPot"))
						&& cond.getName().GetFirstLiteral().getName().equals(
								"atAgent")) {
					ControlPanel
							.writeLog("step is one that requires a second WalkTo action");
					irrelevantThatTrue = true;
				}
			}

			if (!cond.CheckCondition()
					&& p.getStep(openPrecond.getStep()).getName()
							.GetFirstLiteral().getName().equals("SelfWalkTo")
					&& (p.getStep(openPrecond.getStep() - 1).getName()
							.GetFirstLiteral().getName() != null)
					&& (p.getStep(openPrecond.getStep() - 1).getName()
							.GetFirstLiteral().getName().equals(
									"SelfPutFlowerInPot")
							|| p.getStep(openPrecond.getStep() - 1).getName()
									.GetFirstLiteral().getName().equals(
											"SelfStackFlowerpot")
							|| p.getStep(openPrecond.getStep()).getName()
									.GetFirstLiteral().getName().equals(
											"SelfPlaceFlowerPotPond") || p
							.getStep(openPrecond.getStep() - 1).getName()
							.GetFirstLiteral().getName().equals(
									"SelfExploreCloudAndPot"))) {

				// needs to be the opposite because SelfWalkTo atAgent
				// precondition uses != operator
				if (cond.GetValue().toString().equals("True")) {
					KnowledgeBase.GetInstance().Tell(cond.getName(), "False");
				} else if (cond.GetValue().toString().equals("False")) {
					KnowledgeBase.GetInstance().Tell(cond.getName(), "True");
				}

				ControlPanel
						.writeLog("changing kb so that WalkTo action can be added to: "
								+ KnowledgeBase.GetInstance().AskProperty(
										cond.getName()));
				;
			}

			if (!irrelevantThatTrue
					&& ((cond.isGrounded() && cond.CheckCondition()) || ((cond
							.isGrounded() && dontRemove)))) {
				ControlPanel.writeLog("Condition: " + cond
						+ " grounded and verified at start");
				// in this case, we don't have to do much, just add a causal
				// link from start
				newPlan = (Plan) p.clone();
				newPlan.AddLink(new CausalLink(p.getStart().getID(),
						new Integer(-1), openPrecond.getStep(), openPrecond
								.getCondition(), cond.toString()));
				newPlan.CheckCausalConflicts();
				if (newPlan.isValid()) {
					intention.AddPlan(newPlan);
				}
			} else {
				// if the condition is not grounded, we must test if exists
				// a
				// binding set
				// which makes the condition verified

				// this if is not the best way to do it, but only
				// PropertyNotEqual conditions have this method, and
				// it is the only situation where i need to call it
				// I'll think about it latter
				if (cond instanceof PropertyNotEqual) {
					ControlPanel.writeLog("Testing != operator: " + cond);
					substitutionSets = ((PropertyNotEqual) cond)
							.GetValidInequalities();
				} else {
					ControlPanel
							.writeLog("Condition not verified at start and not PropertyNotEqual so looking for substitution sets in plan KB.");
					substitutionSets = cond.GetValidBindings();
					ControlPanel.writeLog("Subs sets: " + substitutionSets);
				}

				if (substitutionSets != null && !irrelevantThatTrue) {
					// shuffle list 3 times
					for (int i = 0; i < 3; i++) {
						Collections.shuffle(substitutionSets);
					}
					li = substitutionSets.listIterator();
					while (li.hasNext()) {
						subSet = (SubstitutionSet) li.next();
						newPlan = (Plan) p.clone();
						newPlan
								.AddBindingConstraints(subSet
										.GetSubstitutions());
						newPlan.AddLink(new CausalLink(p.getStart().getID(),
								new Integer(-1), openPrecond.getStep(),
								openPrecond.getCondition(), cond.toString()));
						newPlan.CheckProtectedConstraints();
						newPlan.CheckCausalConflicts();
						if (newPlan.isValid()) {
							// System.out.println("Adding new Plan: " +
							// newPlan);
							intention.AddPlan(newPlan);
						}
					}
				} else {

					ControlPanel
							.writeLog("Substitution sets are null so adding step");
					// TODO talvez possa fazer isto de uma maneira mais
					// eficiente
					// Tries to find a step in the plan that achieves the
					// precondition
					ControlPanel.writeLog("From existing actions");
					FindStepFor(intention, p, openPrecond, false);
					// tries to find a new step from the available actions
					// that
					// achieves
					// the precondition
					ControlPanel
							.writeLog("From other available actions not in plan");
					FindStepFor(intention, p, openPrecond, true);
					ControlPanel.writeLog("End of adding step");
				}

			}

		}

		if (newPlans) {
			intention.RemovePlan(p);
			if (intention.NumberOfAlternativePlans() == 0) {
				synchronized (this) {
					_intentions
							.remove(intention.getGoal().GetName().toString());
				}
				RegisterIntentionFailure(intention);
				System.out.println("(3) Goal FAILED - "
						+ intention.getGoal().GetName().toString());
				ControlPanel.writeLog("(3) Goal FAILED - "
						+ intention.getGoal().GetName().toString());
				System.out.println("-----------------------------------------");
				System.out.println("-----------------------------------------");

				resetSelectedFailedGoals(intention.getGoal().GetName()
						.toString());
				return null;
			}
			return null;
		}
		// the plan is complete, no flaw was removed
		else
			return p;
	}

	/**
	 * Check whether any of the given step's preconditions require a value that
	 * is different from that dictated by the pedagogical component. Used for
	 * directions on whether to involve the user or not in various steps.
	 * Necessary to cut time in formulating a plan, otherwise planer goes into
	 * perpetual cycle of adding user actions and then removing them.
	 * 
	 * @param step
	 *            the step whose preconditions we are checking
	 * @return True if there is inconsistency between precondition and Knowledge
	 *         Base False if there is no inconsistency
	 */
	public boolean checkStepAgainstPedagogicalComponentDirections(Step step) {
		ListIterator stepPreconds = step.getPreconditions().listIterator();
		while (stepPreconds.hasNext()) {
			Condition cond = (Condition) stepPreconds.next();
			if (cond.getName().toString().equals("PCdontRequestStackObject()")
					&& cond.GetValue().toString().equals("False")
					&& KnowledgeBase.GetInstance().AskProperty(
							Name.ParseName("PCdontRequestStackObject()"))
							.toString().equals("True")) {
				return true;
			} else if (cond.getName().toString().equals(
					"PCdontRequestGiveObject()")
					&& cond.GetValue().toString().equals("False")
					&& KnowledgeBase.GetInstance().AskProperty(
							Name.ParseName("PCdontRequestGiveObject()"))
							.toString().equals("True")) {
				return true;
			} else if (cond.getName().toString().equals(
					"PCdontRequestTransformObjectStackable()")
					&& cond.GetValue().toString().equals("False")
					&& KnowledgeBase
							.GetInstance()
							.AskProperty(
									Name
											.ParseName("PCdontRequestTransformObjectStackable()"))
							.toString().equals("True")) {
				return true;
			} else if (cond.getName().toString().equals(
					"PCdontRequestExploreObjectProperties()")
					&& cond.GetValue().toString().equals("False")
					&& KnowledgeBase
							.GetInstance()
							.AskProperty(
									Name
											.ParseName("PCdontRequestExploreObjectProperties()"))
							.toString().equals("True")) {
				return true;
			} else if (cond.getName().toString().equals(
					"PCassumeUserWontActWithoutPrompting()")
					&& cond.GetValue().toString().equals("False")
					&& KnowledgeBase
							.GetInstance()
							.AskProperty(
									Name
											.ParseName("PCassumeUserWontActWithoutPrompting()"))
							.toString().equals("True")) {
				return true;
			} else if (cond.getName().toString().equals("PCwaitLetUserAct()")
					&& cond.GetValue().toString().equals("False")
					&& KnowledgeBase.GetInstance().AskProperty(
							Name.ParseName("PCwaitLetUserAct()")).toString()
							.equals("True")) {
				return true;
			} else if (cond.getName().toString().equals(
					"PCrequireEstablishAttentionToSelf()")
					&& cond.GetValue().toString().equals("False")
					&& KnowledgeBase
							.GetInstance()
							.AskProperty(
									Name
											.ParseName("PCrequireEstablishAttentionToSelf()"))
							.toString().equals("True")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Tries to develop a plan offline, very usefull for testing if the planner
	 * is working properly
	 * 
	 * @param goal
	 *            - The goal to plan for
	 * @return - A list of actions that if executed in the specified order will
	 *         achieve the goal
	 */
	public Plan DevelopPlan(ActivePursuitGoal goal) {
		Plan p = new Plan(new ArrayList(), goal);
		Intention i = new Intention(p, goal);
		Plan completePlan = null;

		while (i.NumberOfAlternativePlans() > 0) {
			completePlan = ThinkAbout(i);
			if (completePlan != null) {
				return completePlan;
			}
		}
		return null;
	}

	/**
	 * Forces the recalculation of all plan's probability
	 */
	public void UpdateProbabilities() {

		Iterator it;

		it = _intentions.values().iterator();
		while (it.hasNext()) {
			((Intention) it.next()).UpdateProbabilities();
		}
	}

	private void Load(String operatorsFile, String self)
			throws ActionsParsingException, UnspecifiedVariableException {
		StripsOperatorsLoaderHandler op = LoadOperators(operatorsFile, self);

		ListIterator li = op.getOperators().listIterator();
		while (li.hasNext()) {
			AddStripsOperator((Step) li.next());
		}
	}

	private StripsOperatorsLoaderHandler LoadOperators(String xmlFile,
			String self) throws ActionsParsingException {
		System.out.println("LOAD: " + xmlFile);
		// com.sun.xml.parser.Parser parser;
		// parser = new com.sun.xml.parser.Parser();
		StripsOperatorsLoaderHandler op = new StripsOperatorsLoaderHandler(self);
		// parser.setDocumentHandler(op);
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			parser.parse(new File(xmlFile), op);
			// InputSource input = Resolver.createInputSource(new
			// File(xmlFile));
			// parser.parse(input);
			return op;
		} catch (Exception ex) {
			throw new ActionsParsingException(
					"Error parsing the actions file.", ex);
		}
	}

	public void resetSelectedFailedGoals(String goalName) {
		if (goalName.equals("beExpressive()")) {
			KnowledgeBase.GetInstance().Tell(Name.ParseName("reactToEvent()"),
					"False");
			System.out.println("De-activating beExpressive goal");
		} else if (goalName.equals("noticeEvent()")) {
			KnowledgeBase.GetInstance().Tell(Name.ParseName("noticeEvent()"),
					"False");
			System.out.println("De-activating noticeEvent goal");
		} else if (goalName.equals("makeBid()")) {
			KnowledgeBase.GetInstance().Tell(Name.ParseName("makeBid()"),
					"False");
			System.out.println("De-activating makeBid goal");
		}
	}
}
