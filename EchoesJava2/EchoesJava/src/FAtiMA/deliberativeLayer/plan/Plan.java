/** 
 * Plan.java - Represents a plan and implements part of its functionality
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
 * Created: 11/01/2004 
 * @author: João Dias
 * Email to: joao.assis@tagus.ist.utl.pt
 * 
 * History: 
 * João Dias: 11/01/2004 - File created
 * João Dias: 16/05/2006 - Added the Method getStep(Integer)
 * João Dias: 16/05/2006 - Completely Reimplemented the AddOrderingConstraint Method
 * 						   The old method had problems in detecting non-direct cycles.
 * 						   The new version uses OrderRelations
 * João Dias: 16/05/2006 - Made changes according to the new definition of OpenPreconditions,
 * 						   CausalLinks, CausalConflicts
 * João Dias: 16/05/2006 - Added Methods CheckCausalConflicts() and CheckCausalConflict(Step), 
 * 						   removed part of the code from AddStep Method to these ones.
 * João Dias: 16/05/2006 - (Refactoring) Added Method CheckProtectionConstraints(Step) and moved 
 * 						   the code that implemented this functionality from the AddStep method
 * 						   to this one.
 * João Dias: 16/05/2006 - (Refactoring) Renamed method CheckLinks() to UpdatePlan()
 * João Dias: 16/05/2006 - Added the Compare(Step,Step) method, that compares two plan's 
 * 						   step and tell us which one must come first in the plan
 * João Dias: 16/05/2006 - small changes in CheckRedundantStep. Now it receives a Step's ID.
 * João Dias: 16/05/2006 - Changed RemoveOpenPrecondition, RemoveCausalLinks,  
 * 						   RemoveOrderingConstraint, NumberOfSourceLinks and HasNoneBefore
 *  					   so that they receive the ID of the step instead of the step
 * João Dias: 22/05/2006 - Removed constructor Plan(KnowledgeBase, ArrayList, Name) that was
 * 						   not being used
 * João Dias: 22/05/2006 - Removed methods setStart(Step) and setFinish(Step) that were
 * 						   not being used
 * João Dias: 22/05/2006 - Added comments to each public method's header
 * João Dias: 10/07/2006 - the class is now serializable
 * João Dias: 12/07/2006 - the class is now clonable, renamed method Clone() to clone()
 * João Dias: 12/07/2006 - Replaced the deprecated Ground methods for the new ones.
 *                         Old Ground private method in the plan renamed to MakeGround
 * João Dias: 15/07/2006 - Removed the KnowledgeBase from the Class fields since the KB is now
 * 						   a singleton that can be used anywhere without previous references.
 * João Dias: 17/07/2006 - Removed the field Goal and the method getGoal() from the class. They 
 * 						   were not being used and caused problems with the plan serialization.
 * João Dias: 22/07/2006 - Changed method GetStep(id). Altough less efficient, it allows us
 * 						   to remove steps from the plan without having to change all steps ID's
 * João Dias: 26/09/2006 - Added method CheckProtectedConstraints()
 * João Dias: 02/10/2006 - The UpdatePlan() method now updates more than one continuous planner flaw
 * 					       in each function call
 * 						 - Now, a plan's probability is being updated properly
 * 						 - The method that performs the update of plan's according to continuous
 * 						   planning flaws is now divided into a two-stage proccess. In the first stage,
 * 						   we check for conditions that may be satisfied by the Start Step, and thus, 
 * 						   we can remove the corresponding causal links and steps from the plan. 
 *						   The second phase, checks if there are any links that are not supported by 
 *						   Start Step anymore and removes them. This separation solved a problem that 
 *						   would sometimes occur. 
 * João Dias: 03/10/2006 - We no longer add to a plan's ProtectedConditions (conditions of InterestGoals)
 * 					       the goal's failure conditions. Altough it could be an interesting idea, we were
 * 						   adding them with the wrong value (these failureconditions should not be protected,
 * 						   should be avoided!), and besides we would need specific code to handle threats
 * 						   to this type of conditions. For the moment, we just removed this.
 * João Dias: 13/11/2006 - Added the method Compare(Integer, Integer) to the class.
 * 						 - Solved a bug that was causing causal conflicts to be wrongly added after being solved
 * 						   by ordering constraints. This was causing the planner to take longer to develop plans.
 * João Dias: 14/11/1006 - The method CheckRedundantStep now returns whether the step was removed from the plan or
 * 						   not (a boolean value)
 *						 - By removing a redundant step (in method UpdatePlan) we were forgetting that other
 *						   steps may also become redundants. Now the UpdatePlan method ensures that all redundant
 *						   steps are removed.
 *						 - We were forgetting to remove a redundant step from existing causal conflicts, ignored
 *						   conflicts and protection threats. This was causing erroneous spawn of multiple invalid
 *						   plans, when a redundant step was removed from a plan with causal conflicts referencing
 *						   such step.
 * João Dias: 15/11/2006 - The method CheckRedundant step does not remove the step anymore, it just checks
 * 					       if the step is redundant. The removal is performed by the methods that use
 * 						   this function.
 * João Dias: 22/11/2006 - We were forgetting to check for causal conflicts when a causal link was extended
 * 					       to the start step in the plan Update method (continuous planning). This was causing
 * 						   the generation of invalid plans in some situations.
 * João Dias: 22/12/2006 - The method UnexecutedAction now may return actions of other agents that contain
 * 						   unbound variables. This is extremely usefull to handle actions of other agents
 * 						   that have unspecified parameters, which is being used to handle the user's interaction.
 * 						   However, actions performed by the agent itself must still be grounded.
 * 						 - Improved the plan UpdateMethod. Now it can handle the update of actions that have
 * 						   unbound variables, as it may happen when using actions of other agents in the plans
 * João Dias: 20/03/2007 - Solved a bug in MakeGround method when removing Inequality Constraints
 * 						    
 */

package FAtiMA.deliberativeLayer.plan;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import echoesEngine.ControlPanel;
import FAtiMA.autobiographicalMemory.AutobiographicalMemory;
import FAtiMA.conditions.Condition;
import FAtiMA.deliberativeLayer.goals.ActivePursuitGoal;
import FAtiMA.wellFormedNames.Inequality;
import FAtiMA.wellFormedNames.Name;
import FAtiMA.wellFormedNames.Substitution;
import FAtiMA.wellFormedNames.Symbol;

/**
 * Represents a plan and implements part of its functionality
 * 
 * @author João Dias
 */

public class Plan implements Cloneable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Name _name;

	private Step _finish;

	private Step _start;

	private ArrayList _steps;

	private HashMap _bindingConstraints;

	private ArrayList _causalConflicts;

	private ArrayList _ignoredConflicts;

	private ArrayList _inequalityConstraints;

	private ArrayList _links;

	private ArrayList _openPreconditions;

	private ArrayList _orderingConstraints;

	private ArrayList _protectedConditions;

	private ArrayList _protectionThreats;

	private float _probability;

	private int _stepCounter;

	private boolean _valid;

	private boolean _probabilityChanged;

	private boolean chosenRecentlyAsBestPlan = false;

	private Plan() {
	}

	/**
	 * Creates a new empty Plan
	 * 
	 * @param protectedConditions
	 *            - a list of ProtectedConditions that must be preserved during
	 *            the planning proccess
	 * @param goal
	 *            - the goal that this plan tries to achieve
	 */
	public Plan(ArrayList protectedConditions, ActivePursuitGoal goal) {
		_name = goal.GetName();
		_valid = true;
		_stepCounter = 0;
		_links = new ArrayList();
		_openPreconditions = new ArrayList();
		_causalConflicts = new ArrayList();
		_bindingConstraints = new HashMap();
		_orderingConstraints = new ArrayList();
		_inequalityConstraints = new ArrayList();
		_steps = new ArrayList();
		_protectedConditions = new ArrayList();
		_protectionThreats = new ArrayList();
		_ignoredConflicts = new ArrayList();
		_probability = 1.0f;
		_probabilityChanged = false;

		_start = new Step(new Symbol(AutobiographicalMemory.GetInstance()
				.getSelf()), new Symbol("Start"), 1, null, null);
		_start.SetID(new Integer(_stepCounter++));
		_finish = new Step(new Symbol(AutobiographicalMemory.GetInstance()
				.getSelf()), new Symbol("Finish"), 1, goal
				.GetSuccessConditions(), null);
		_finish.SetID(new Integer(_stepCounter++));

		for (int i = 0; i < goal.GetSuccessConditions().size(); i++) {
			this._openPreconditions.add(new OpenPrecondition(this._finish
					.getID(), new Integer(i)));
		}

		_protectedConditions.addAll(protectedConditions);
	}

	/**
	 * Adds a binding constraint to the plan of the form [X]/John or [X]/[X23]
	 * or [X]!=Luke
	 * 
	 * @param bind
	 *            - the binding constraint to add (Substitution)
	 * 
	 * @see Substitution
	 */
	public void AddBindingConstraint(Substitution bind) {
		Iterator it;
		Substitution subst;
		Substitution newSubst;
		Symbol substValue;
		Symbol bindValue;
		Inequality ineq;
		ArrayList substs;
		substs = new ArrayList();

		if (bind instanceof Inequality) {
			// we'r adding a inequality constraint like {[x]!=john}
			it = _bindingConstraints.values().iterator();
			while (it.hasNext()) {
				substs.add(it.next());
			}
			ineq = (Inequality) ((Inequality) bind).clone();
			ineq.MakeGround(substs);
			// the plan is not valid if after apllying the plan's substitution
			// we have an inequalit of the type:
			// ex: {[x]!= [x]}, {john!=john}
			if (ineq.getValue().equals(ineq.getVariable())) {
				_valid = false;
			} else if (!ineq.isGrounded()) {
				_inequalityConstraints.add(ineq);
			}
			return;
		}

		subst = (Substitution) _bindingConstraints.get(bind.getVariable()
				.toString());
		if (subst != null) {
			substValue = subst.getValue();
			bindValue = bind.getValue();
			if (bindValue.isGrounded() && substValue.isGrounded()) {
				_valid = bindValue.equals(substValue);
				if (!_valid)
					return;
			}
			if (bindValue.isGrounded()) {
				newSubst = new Substitution(substValue, bindValue);
				_bindingConstraints.put(substValue.toString(), newSubst);
				_bindingConstraints.put(bind.getVariable().toString(), bind);
				substs.add(newSubst);
				substs.add(bind);
			} else if (substValue.isGrounded()) {
				newSubst = new Substitution(bindValue, substValue);
				_bindingConstraints.put(bindValue.toString(), newSubst);
				substs.add(newSubst);
			} else {
				_valid = false;
				return;
				// podemos ter ciclos com bindings....
			}
		} else {
			_bindingConstraints.put(bind.getVariable().toString(), bind);
			substs.add(bind);
		}

		this.MakeGround(substs);
	}

	/**
	 * Adds a set of binding constraint to the plan of the form [X]/John or
	 * [X]/[X23] or [X]!=Luke
	 * 
	 * @param bind
	 *            - a list of binding constraints to add (Substitutions)
	 * 
	 * @see Substitution
	 */
	public void AddBindingConstraints(ArrayList substs) {
		ListIterator li;

		li = substs.listIterator();
		while (li.hasNext()) {
			AddBindingConstraint((Substitution) li.next());
		}
	}

	/**
	 * Adds a CausalLink of the Form A--p-->B to the Plan
	 * 
	 * @param l
	 *            - the CausalLink to add
	 * 
	 * @see CausalLink
	 */
	public void AddLink(CausalLink l) {
		_links.add(l);
		_probabilityChanged = true;
		AddOrderingConstraint(l.getOrderConstraint());
	}

	/**
	 * Adds an Ordering Constraint of the Type A > B
	 * 
	 * @param order
	 *            - the OrderConstraint to add
	 */
	public void AddOrderingConstraint(OrderingConstraint order) {
		AddOrderingConstraint(order.getBefore(), order.getAfter());
	}

	/**
	 * Adds a new step to the plan Adds the step's preconditions to the
	 * beginning of the _openPreconditions list
	 * 
	 * @param step
	 *            - the step to add
	 */
	public void AddStep(Step step) {
		ListIterator li;
		ArrayList tempOpenPreconditionList = new ArrayList();

		step.SetID(new Integer(_stepCounter++));
		_steps.add(step);

		li = step.getPreconditions().listIterator();
		int i = 0;
		while (li.hasNext()) {
			li.next();
			tempOpenPreconditionList.add(new OpenPrecondition(step.getID(),
					new Integer(i++)));
			// _openPreconditions.add(new OpenPrecondition(step.getID(),
			// new Integer(i++)));
		}

		for (int j = 0; j < _openPreconditions.size(); j++) {
			tempOpenPreconditionList.add(_openPreconditions.get(j));
		}

		_openPreconditions = tempOpenPreconditionList;

		CheckProtectedConstraints(step);

		CheckCausalConflicts(step);
	}

	/**
	 * Checks if a given Step threatens any of the conditions protected by the
	 * agent's InterestGoals
	 * 
	 * @param step
	 *            - the Step to check
	 */
	private void CheckProtectedConstraints(Step step) {
		ListIterator li;
		ListIterator li2;
		ProtectedCondition pCond;
		Condition cond;
		Effect eff;

		li = this._protectedConditions.listIterator();

		while (li.hasNext()) {
			pCond = (ProtectedCondition) li.next();
			cond = pCond.getCond();
			li2 = step.getEffects().listIterator();
			while (li2.hasNext()) {
				eff = (Effect) li2.next();
				if (eff.GetEffect().ThreatensCondition(cond)) {
					_protectionThreats.add(new GoalThreat(pCond, step, eff));
				}
			}
		}
	}

	/**
	 * Checks if any of the plan's steps has a conflict with a protected
	 * condition of an InterestGoal
	 */
	public void CheckProtectedConstraints() {
		for (ListIterator li = this._steps.listIterator(); li.hasNext();) {
			CheckProtectedConstraints((Step) li.next());
		}
	}

	/**
	 * Checks if the Step just introduced in the plan caused any causal conflict
	 * with an existing CausalLink.
	 * 
	 * @param step
	 *            - the step to check
	 */
	private void CheckCausalConflicts(Step step) {
		Condition cond;
		CausalLink link;
		ListIterator li;
		ListIterator li2;
		Effect eff;
		// initialise to true - if it's satisfied by intermediary step it will
		// be set to false
		boolean threatened = false;

		// the next code checks if the step introduced a causal conflict
		li = this._links.listIterator();
		while (li.hasNext()) {
			link = (CausalLink) li.next();
			if (!link.getDestination().equals(step.getID())
					&& !link.getSource().equals(step.getID())) {
				cond = this.getStep(link.getDestination()).getPrecondition(
						link.getCondition());
				li2 = step.getEffects().listIterator();
				while (li2.hasNext()) {
					eff = (Effect) li2.next();
					// ControlPanel.writeLog("Checking if effect " + eff +
					// " threatens condition " + cond);
					if (eff.GetEffect().ThreatensCondition(cond)) {
						threatened = true;
						for (int j = 0; j < this.getSteps().size(); j++) {
							Step aPlanStep = (Step) this.getSteps().get(j);
							if (aPlanStep.getID() < step.getID()) {
								// ControlPanel
								// .writeLog("Checking the effects of step: "
								// + aPlanStep
								// + " as its ID "
								// + aPlanStep.getID()
								// + " is lower than "
								// + step.getID()
								// + " the id of the new step: "
								// + step
								// + " and therefore occurs after it.");
								for (int k = 0; k < aPlanStep.getEffects()
										.size(); k++) {
									Effect aPlanStepEffect = (Effect) aPlanStep
											.getEffects().get(k);
									if (aPlanStepEffect.GetEffect().getName()
											.toString().equals(
													cond.getName().toString())
											&& aPlanStepEffect.GetEffect()
													.GetValue().equals(
															cond.GetValue())) {
										// ControlPanel
										// .writeLog("the threatening step can be ignored because of a subsequent step's effect that re-satisfies the threatened condition");
										// ControlPanel
										// .writeLog("the threatening effect is: "
										// + eff
										// +
										// " and the step that satisfies it is: "
										// + aPlanStep);
										threatened = false;
										break;
									}
								}
							}
						}
						if (threatened) {
							ControlPanel.writeLog("the effect: " + eff
									+ " of step " + step
									+ " threatens the condition " + cond
									+ " of the link between "
									+ link.getDestination()
									+ " (destination) and " + link.getSource()
									+ " (source)");
							// the threat only exits if the step can
							// occur between
							// the source and the destination
							if (Compare(step.getID(), link.getSource()) >= 0
									&& Compare(step.getID(), link
											.getDestination()) <= 0) {
								ControlPanel
										.writeLog("and the step occurs between the source and destination, so a conflict added");
								this._causalConflicts
										.add(new CausalConflictFlaw(link, step
												.getID(), eff));
							} else {
								ControlPanel
										.writeLog("but the step doesn't occur between the source and destination so no conflict added");
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Checks if any of the plan's steps introduced a causal conflict with an
	 * existing CausalLink
	 */
	public void CheckCausalConflicts() {
		ListIterator li = this._steps.listIterator();
		Step s;
		while (li.hasNext()) {
			s = (Step) li.next();
			CheckCausalConflicts(s);
		}
	}

	/**
	 * Compares two Steps, taking into account the existing ordering constraints
	 * in the plan, and specifies which one must become after the other
	 * 
	 * @param s1
	 *            - The left step to compare
	 * @param s2
	 *            - The right step to compare
	 * @return Returns -1 if the step s1 must become before s2, +1 if s1 must
	 *         become after s2 and 0 if there is no ordering contraint between
	 *         the two steps
	 */
	public int Compare(Step s1, Step s2) {
		return Compare(s1.getID(), s2.getID());
	}

	/**
	 * Compares two Steps, taking into account the existing ordering constraints
	 * in the plan, and specifies which one must become after the other
	 * 
	 * @param step1
	 *            - The id of the left step to compare
	 * @param step2
	 *            - The id of the right step to compare
	 * @return Returns -1 if the step s1 must become before s2, +1 if s1 must
	 *         become after s2 and 0 if there is no ordering contraint between
	 *         the two steps
	 */
	public int Compare(Integer step1, Integer step2) {
		OrderRelation order;
		ListIterator li = this._orderingConstraints.listIterator();

		while (li.hasNext()) {
			order = (OrderRelation) li.next();
			if (order.getStepID().equals(step1)) {
				return order.Compare(step2);
			} else if (order.getStepID().equals(step2)) {
				return -order.Compare(step1);
			}
		}

		return 0;
	}

	/**
	 * Method that implements the Continuous Planner features. It is responsible
	 * for detecting if the plan is not consistent with the world state anymore,
	 * and to fix such continuous planning flaws. This method should be called
	 * whenever the world changes.
	 */
	public void UpdatePlan() {
		CausalLink link;
		ArrayList linksToRemove = new ArrayList();
		ArrayList linksToAdd = new ArrayList();
		Condition cond;
		Step s;

		ControlPanel.writeLog("Updating plan");
		// this method needs to have two distinct stages. In the first one, we
		// check
		// for conditions that may be satisfied in the Start Step, and thus, we
		// can
		// remove the corresponding causal links and steps from the plan.
		// The second phase, and it's important that it only starts after the
		// first
		// one ends, checks if there are any links that are not supported by
		// Start
		// Step anymore. The reason of this two-stage proccess is that if you
		// have
		// a step s that has p as precondition and !p as effect, you need to
		// realize
		// that once !p is achieved you no longer need s (and therefore you
		// don't need
		// p). If you search for unsupported links first, you will detect that p
		// is
		// no longer achieved by Start, and will wrongly reintroduce p as an
		// Open
		// Precondition (because the planner thinks p is still important for the
		// plan).

		// first stage, searching and removing unnecessary steps

		for (ListIterator li = _links.listIterator(); li.hasNext();) {
			link = (CausalLink) li.next();
			if (!link.getSource().equals(this._start.getID())) {

				// TODO Extending a causal link to start might be causing
				// problems with
				// the planning algorithm
				s = getStep(link.getDestination());
				cond = s.getPrecondition(link.getCondition());

				// the link's condition is verified by the Start Step
				// and thus we can extend a causal link to start and
				// remove the previous link
				// TODO this doesn't work if the condition is not grounded...
				// think whether I should change this
				if (cond.isGrounded()) {
					if (cond.CheckCondition()) {
						linksToAdd.add(new CausalLink(this._start.getID(),
								new Integer(-1), link.getDestination(), link
										.getCondition(), cond.toString()));
						// need to have a check here for whether the step is a
						// walkTo action, but there is also a walkTo action that
						// will
						// occur before it so if the second walkTo action
						// precondition is not verified (i.e. the agent is at
						// the location that the second walkTo
						// action will take him, then don't remove if there is
						// going to be another walkTo action beforehand, as this
						// will move the agent again.
						// The problem is that if the second walkTo action IS
						// removed, it will get added later when needed (i.e.
						// when the previous walkTo action has now moved the
						// agent), but it will be added ahead of other actions,
						// so the agent will end up walking back and forth
						boolean dontRemove = false;
						if (getStep(link.getSource()).getName()
								.GetFirstLiteral().getName().equals(
										"SelfWalkTo")) {
							for (ListIterator planStepLi = _steps
									.listIterator(); planStepLi.hasNext();) {
								Step planStep = (Step) planStepLi.next();
								if (planStep.getID() > getStep(link.getSource())
										.getID()
										&& planStep.getName().GetFirstLiteral()
												.getName().equals("SelfWalkTo")) {
									ControlPanel
											.writeLog("link source step is walk to and step with greater id (i.e. comes before the link source step: "
													+ planStep.getID()
													+ " is walk to so not removing");
									dontRemove = true;
								}
							}
						}
						if (!dontRemove) {
							ControlPanel.writeLog("removing link: "
									+ link.toString());
							linksToRemove.add(link);
						}
					}
				} else // if the condition is not grounded we must do another
				// thing
				{
					// this piece of code is causing big problems!
					// System.out.println("Segue o caminho BBB");
					if (cond.GetValidBindings() != null) {

						// this means that there is at least one possible
						// substitution that can
						// establish the condition from the start step.
						// Therefore, we must remove
						// this link, and reintroduce the condition as an open
						// precondition, so that
						// the planning process can figure out if the
						// substitution(s) is valid.
						System.out.println("condition verified in start: "
								+ cond);
						linksToRemove.add(link);
						_openPreconditions.add(new OpenPrecondition(link
								.getDestination(), link.getCondition()));
					}

				}

			}
		}

		// this is still the first stage, now we need to add to the plan
		// all the links to Start created
		for (ListIterator li = linksToAdd.listIterator(); li.hasNext();) {
			AddLink((CausalLink) li.next());
			// when a link to start is created (or extended), we also need to
			// check if it introduced new causal conflicts with existing links.
			CheckCausalConflicts();
		}

		// Removing unnecessary links. If by removing a link, a step stops
		// supplying links to the plan, it is considered a redundant step
		// and its also removed
		if (linksToRemove.size() > 0) {
			for (ListIterator li = linksToRemove.listIterator(); li.hasNext();) {
				link = (CausalLink) li.next();
				RemoveCausalLink(link);
				if (CheckRedundantStep(link.getSource())) {
					Integer stepID = link.getSource();
					RemoveCausalLinks(stepID);
					RemoveCausalConflicts(stepID);
					RemoveIgnoredConflicts(stepID);
					RemoveProtectionThreats(stepID);
					RemoveOrderingConstraints(stepID);
					RemoveOpenPreconditions(stepID);
					RemoveStep(stepID);
				}
			}
			// there is one additional problem, removing a reduntant step may
			// cause other steps to become redundant. Therefore, we must check
			// every other step for redundancy, and stop only when no more
			// steps are removed.

			while (RemoveRedundantSteps()) {
			}

			linksToRemove.clear();
		}

		// second stage. Now that all redundant links and steps have been
		// removed, we can safely search for unsupported links
		for (ListIterator li = _links.listIterator(); li.hasNext();) {
			link = (CausalLink) li.next();
			if (link.getSource().equals(this._start.getID())) {
				s = getStep(link.getDestination());
				cond = s.getPrecondition(link.getCondition());

				// if the condition is not verified we must select
				// for removal the unsupportedlink and create an
				// OpenPrecondition that must again be satisfied by
				// the plan
				if (!cond.CheckCondition()) {
					boolean verifiedLater = false;
					for (int i = 0; i < _steps.size(); i++) {
						Step aPlanStep = (Step) _steps.get(i);
						ControlPanel.writeLog("checking step: " + aPlanStep
								+ " whose ID is " + aPlanStep.getID()
								+ " against step " + s + " whose id is "
								+ s.getID());
						// if a step, that occurs *before* the step in the link
						// (i.e. has a higher ID, as it's added later to the
						// plan), verifies the condition then keep the link
						// e.g. if stacking multiple objects the plan will
						// include pickUp(object1) stack(object1)
						// pickUp(object2) stack(object2). When pickUp(object1)
						// is executed the new Knowledge Base shows that
						// HandsFree(Paul) = False, but PickUp(object2) requires
						// that HandsFree(Paul) = True. So when the plan is
						// updated in this method (without the code that
						// follows) it will add HandsFree(Paul) = True as an
						// open precondition. However, intermediary
						// action stack(object1) has the effect that
						// HandsFree(Paul) = True. Need to check for such
						// intermediary steps.
						if (aPlanStep.getID() > s.getID()) {
							for (int j = 0; j < aPlanStep.getEffects().size(); j++) {
								Effect aPlanStepEffect = (Effect) aPlanStep
										.getEffects().get(j);
								ControlPanel.writeLog("Checking effect "
										+ aPlanStepEffect.GetEffect().getName()
										+ " whose value is "
										+ aPlanStepEffect.GetEffect()
												.GetValue() + " against "
										+ cond.getName() + " whose value is "
										+ cond.GetValue());
								if (cond.getName().toString().equals(
										aPlanStepEffect.GetEffect().getName()
												.toString())
										&& cond.GetValue().toString().equals(
												aPlanStepEffect.GetEffect()
														.GetValue().toString())) {
									verifiedLater = true;
									ControlPanel.writeLog("The condition: "
											+ cond.getName() + " (name) "
											+ cond.GetValue() + " (value) "
											+ " is verified later by the step "
											+ aPlanStep
											+ " so the link with step " + s
											+ " is not removed");
								}
							}
						}
					}
					if (!verifiedLater) {
						_openPreconditions.add(new OpenPrecondition(link
								.getDestination(), link.getCondition()));
						linksToRemove.add(link);
						ControlPanel.writeLog("The link with step " + s
								+ " is removed because the condition "
								+ cond.getName() + " (name) " + cond.GetValue()
								+ " (value) is no longer verified.");
					}
				}
			}
		}

		// finally at the end of the second stage we must still remove
		// all the unsupported links that were found
		for (ListIterator li = linksToRemove.listIterator(); li.hasNext();) {
			link = (CausalLink) li.next();
			RemoveCausalLink(link);
			// in this case we don't need to remove redundant steps, because
			// the Start Step is never redundant, and futhermore we cannot
			// remove it
		}
	}

	/**
	 * Checks if a given Step is redundant and not needed anymore, because the
	 * CausalLinks it establishes are not needed anymore.
	 * 
	 * @param stepID
	 *            - the ID in the plan of the step to remove
	 * @return true if the step is redundant, false otherwise
	 */
	public boolean CheckRedundantStep(Integer stepID) {
		return NumberOfSourceLinks(stepID) == 0;
	}

	private boolean RemoveRedundantSteps() {
		Step s;
		Integer stepID;
		boolean foundRedundantSteps = false;

		for (ListIterator li = _steps.listIterator(); li.hasNext();) {
			s = (Step) li.next();
			stepID = s.getID();
			if (CheckRedundantStep(stepID)) {
				RemoveCausalLinks(stepID);
				RemoveCausalConflicts(stepID);
				RemoveIgnoredConflicts(stepID);
				RemoveProtectionThreats(stepID);
				RemoveOrderingConstraints(stepID);
				RemoveOpenPreconditions(stepID);

				// finally remove the step from the list of activeSteps
				li.remove();
				foundRedundantSteps = true;
			}
		}

		return foundRedundantSteps;
	}

	/**
	 * Clones this Plan, returning an equal hard copy. If the new plan is
	 * changed afterwards, the original plan remains the same.
	 * 
	 * @return The plan's copy.
	 */
	public Object clone() {
		ListIterator li;

		Plan p = new Plan();
		p._name = (Name) this._name.clone();
		p._valid = this._valid;
		p._stepCounter = _stepCounter;
		p._probabilityChanged = this._probabilityChanged;
		p._probability = this._probability;

		p._start = (Step) this._start.clone();
		p._finish = (Step) this._finish.clone();

		// Clone member by member
		p._inequalityConstraints = new ArrayList();
		li = this._inequalityConstraints.listIterator();
		while (li.hasNext()) {
			p._inequalityConstraints.add(((Inequality) li.next()).clone());
		}

		p._orderingConstraints = new ArrayList();
		li = this._orderingConstraints.listIterator();
		while (li.hasNext()) {
			p._orderingConstraints.add(((OrderRelation) li.next()).clone());
		}

		p._steps = new ArrayList();
		li = this._steps.listIterator();
		while (li.hasNext()) {
			p._steps.add(((Step) li.next()).clone());
		}

		// no need to clone the elements
		p._bindingConstraints = (HashMap) _bindingConstraints.clone();
		p._openPreconditions = (ArrayList) _openPreconditions.clone();
		p._links = (ArrayList) _links.clone();
		p._causalConflicts = (ArrayList) _causalConflicts.clone();

		// TODO think about these ones
		p._protectedConditions = _protectedConditions;
		p._protectionThreats = (ArrayList) _protectionThreats.clone();
		p._ignoredConflicts = (ArrayList) _ignoredConflicts.clone();

		return p;
	}

	/**
	 * Gets the special Finish Step
	 * 
	 * @return the Finish Step
	 */
	public Step getFinish() {
		return _finish;
	}

	/**
	 * Gets a list of CausalConflicts ignored due to emotion-focused coping
	 * strategies (denial or whishfull thinking)
	 * 
	 * @return an ArrayList with ignored CausalConflicts
	 */
	public ArrayList getIgnoredConflicts() {
		return _ignoredConflicts;
	}

	/**
	 * Gets the plan CausalLinks
	 * 
	 * @return an ArrayList with all the plan's CausalLinks
	 */
	public ArrayList getLinks() {
		return _links;
	}

	/**
	 * Gets the plan's Name
	 * 
	 * @return the plan's name
	 */
	public Name getName() {
		return _name;
	}

	/**
	 * Gets the plan's OpenPreconditions
	 * 
	 * @return an ArrayList with all the plan's preconditions
	 */
	public ArrayList getOpenPreconditions() {
		return _openPreconditions;
	}

	/**
	 * Gets the OrderConstraints or OrderRelations in the plan
	 * 
	 * @return an ArrayList with OrderRelations between the distinct steps in
	 *         the plan
	 */
	public ArrayList getOrderingConstraints() {
		return _orderingConstraints;
	}

	/**
	 * gets the plan's probability of success
	 * 
	 * @return the plan's probability
	 */
	public float getProbability() {
		if (_probabilityChanged) {
			UpdatePlanProbability();
			_probabilityChanged = false;
		}
		return _probability;
	}

	/**
	 * Gets the plan's Step who's ID is the received ID
	 * 
	 * @param stepID
	 *            - the ID of the Step in the plan to be searched
	 * @return the Step that corresponds to the received ID
	 */
	public Step getStep(Integer stepID) {
		Step s;

		if (stepID.intValue() == 0) {
			return this._start;
		} else if (stepID.intValue() == 1) {
			return this._finish;
		}

		for (ListIterator li = _steps.listIterator(); li.hasNext();) {
			s = (Step) li.next();
			if (s.getID().equals(stepID)) {
				return s;
			}
		}

		return null;
	}

	/**
	 * Requests an explicit recalculation of a plan's probability. This method
	 * must be called whenever there is a change in a plan that may lead to
	 * different success probability
	 */
	public void UpdateProbabilities() {
		_probabilityChanged = true;
	}

	/**
	 * Gets the special Start Step
	 * 
	 * @return the Start Step
	 */
	public Step getStart() {
		return _start;
	}

	/**
	 * Gets all the plan's steps
	 * 
	 * @return an ArrayList with all the plan's steps
	 */
	public ArrayList getSteps() {
		return _steps;
	}

	/**
	 * Gets the plan's threats to InterestGoals
	 * 
	 * @return an ArrayList with the plan's GoalThreats to InterestGoals
	 */
	public ArrayList getThreatenedInterestConstraints() {
		return _protectionThreats;
	}

	/**
	 * Gets a heuristic value H for the plan. Usefull for comparing plans. The
	 * lowest value of H corresponds to the likely better plan to continue
	 * planning.
	 * 
	 * @return the value H
	 */
	public float h() {
		return (1 + _steps.size() + _openPreconditions.size() + _protectionThreats
				.size() * 2)
				/ this.getProbability();
	}

	/**
	 * Ignores a given CausalConflict. This corresponds to the Wishfull
	 * thinking/Denial emotion-focused coping strategy.
	 * 
	 * @param flaw
	 *            - the CausalConflictFlaw to ignore
	 */
	public void IgnoreConflict(CausalConflictFlaw flaw) {
		_ignoredConflicts.add(flaw);
		this.UpdatePlanProbability();
		_probabilityChanged = false;
	}

	public boolean isFinished() {
		return false;
	}

	/**
	 * indicates if the plan is valid or not
	 * 
	 * @return true if the plan is valid, i.e., it does not have unsolvable
	 *         causal conflicts and cycles in the OrderRelations
	 */
	public boolean isValid() {
		return _valid;
	}

	/**
	 * Gets the next CausalConflictFlaw in the plan
	 * 
	 * @return null if there are no CausalConflicts in the plan, otherwise it
	 *         returns the next CausalConflict in the list
	 */
	public CausalConflictFlaw NextFlaw() {
		if (_causalConflicts.size() > 0)
			return (CausalConflictFlaw) _causalConflicts.remove(0);
		else
			return null;
	}

	/**
	 * Gets the number of CausalLinks where the received Step participates as
	 * the Source for the CausalLink, i.e., the method determines how many
	 * CausalLinks the Step supports
	 * 
	 * @param stepID
	 *            - the ID of the step to calculate the result
	 * @return the number of CausalLinks that the step supports
	 */
	public int NumberOfSourceLinks(Integer stepID) {
		ListIterator li;
		int number = 0;
		li = _links.listIterator();

		while (li.hasNext()) {
			if (((CausalLink) li.next()).getSource().equals(stepID))
				number++;
		}

		return number;
	}

	/**
	 * Removes a Step from the plan
	 * 
	 * @param stepID
	 *            - the ID of the step to be removed
	 */
	public void RemoveStep(Integer stepID) {
		Step s;
		for (ListIterator li = _steps.listIterator(); li.hasNext();) {
			s = (Step) li.next();
			if (s.getID().equals(stepID)) {
				li.remove();
				return;
			}
		}
	}

	/**
	 * Removes a CausalLink from the plan
	 * 
	 * @param link
	 *            - the link to be removed
	 */
	public void RemoveCausalLink(CausalLink link) {
		RemoveOrderingConstraint(link.getOrderConstraint());
		_links.remove(link);
		_probabilityChanged = true;
	}

	/**
	 * Removes all the CausalLinks that protected a given step's preconditions
	 * 
	 * @param stepID
	 *            - the ID of the step that we want to remove the CausalLinks
	 */
	public void RemoveCausalLinks(Integer stepID) {
		ListIterator li;
		CausalLink link;
		li = _links.listIterator();

		while (li.hasNext()) {
			link = (CausalLink) li.next();
			if (link.getDestination().equals(stepID)) {
				RemoveOrderingConstraint(link.getOrderConstraint());
				li.remove();
			}
		}
	}

	/**
	 * Remove all OpenPreconditions referenced by a given Step
	 * 
	 * @param stepID
	 *            - the ID of the step that we want to remove its preconditions
	 *            from the OpenPreconditions list
	 */
	public void RemoveOpenPreconditions(Integer stepID) {
		ListIterator li;
		OpenPrecondition openPre;

		li = _openPreconditions.listIterator();

		while (li.hasNext()) {
			openPre = (OpenPrecondition) li.next();
			if (openPre.getStep().equals(stepID)) {
				li.remove();
			}
		}
	}

	private void RemoveCausalConflicts(Integer stepID) {
		CausalConflictFlaw conflict;

		for (ListIterator li = _causalConflicts.listIterator(); li.hasNext();) {
			conflict = (CausalConflictFlaw) li.next();
			// the causal conflict between A-p->B and C-!p-> should be removed
			// if:
			// 1) Step C is going to be removed (the step ID received)
			// 2) Step B is going to be removed (the step ID received)
			if (conflict.GetStep().equals(stepID)
					|| conflict.GetCausalLink().getDestination().equals(stepID)) {
				li.remove();
			}
		}
	}

	private void RemoveIgnoredConflicts(Integer stepID) {
		CausalConflictFlaw conflict;

		for (ListIterator li = _ignoredConflicts.listIterator(); li.hasNext();) {
			conflict = (CausalConflictFlaw) li.next();
			// the causal conflict between A-p->B and C-!p-> should be removed
			// if:
			// 1) Step C is going to be removed (the step ID received)
			// 2) Step B is going to be removed (the step ID received)
			if (conflict.GetStep().equals(stepID)
					|| conflict.GetCausalLink().getDestination().equals(stepID)) {
				li.remove();
			}
		}
	}

	private void RemoveProtectionThreats(Integer stepID) {
		GoalThreat threat;

		for (ListIterator li = _protectionThreats.listIterator(); li.hasNext();) {
			threat = (GoalThreat) li.next();
			if (threat.getStep().getID().equals(stepID)) {
				li.remove();
			}
		}
	}

	/**
	 * Removes an OrderingConstraint between two steps (A > B) from the plan
	 * 
	 * @param order
	 *            - The ordering constraint to remove
	 */
	public void RemoveOrderingConstraint(OrderingConstraint orderConstraint) {
		ListIterator li;
		OrderRelation order;
		li = this._orderingConstraints.listIterator();
		// TODO this does not work properly because there might exist order
		// relations
		// between steps caused by previous relations. By deleting the former
		// relations,
		// the subsequent relations should also be erased but it does not work
		// like that now
		while (li.hasNext()) {
			order = (OrderRelation) li.next();
			if (order.getStepID().equals(orderConstraint.getBefore())) {
				order.getAfter().remove(orderConstraint.getAfter());
			} else if (order.getStepID().equals(orderConstraint.getAfter())) {
				order.getBefore().remove(orderConstraint.getBefore());
			}
		}
	}

	/**
	 * Removes all OrderingRelations refering to a given Step
	 * 
	 * @param stepID
	 *            - the ID of the step to remove from OrderRelations
	 */
	public void RemoveOrderingConstraints(Integer stepID) {
		ListIterator li;
		OrderRelation order;
		li = this._orderingConstraints.listIterator();

		// TODO this does not work properly because there might exist order
		// relations
		// between steps caused by previous relations. By deleting the former
		// relations,
		// the subsequent relations should also be erased but it does not work
		// like that now
		while (li.hasNext()) {
			order = (OrderRelation) li.next();
			if (order.getStepID().equals(stepID)) {
				li.remove();
			} else {
				order.getBefore().remove(stepID);
				order.getAfter().remove(stepID);
			}
		}
	}

	/**
	 * Converts the plan to a String
	 * 
	 * @return the converted String
	 */
	public String toString() {
		return _name.toString() + "Steps: " + _steps;
	}

	/**
	 * Gets the next action that we must execute in the plan in order to achieve
	 * it
	 * 
	 * @return the next action to execute
	 */
	public Step UnexecutedAction() {
		ListIterator li;
		Step op;
		ArrayList reversedSteps = new ArrayList();

		// reverse elements of _steps so that we perform the first action to not
		// have ordering constraints from the start of the plan not the end
		int k = _steps.size() - 1; // because arrays start from 0, so index of
		// last element will be array size - 1
		for (int i = 0; i < _steps.size(); i++) {
			Step tempStep = (Step) _steps.get(k - i);
			reversedSteps.add(tempStep);
		}
		// li = _steps.listIterator();
		li = reversedSteps.listIterator();
		while (li.hasNext()) {
			op = (Step) li.next();
			if (HasNoneBefore(op.getID())) {
				ControlPanel.writeLog("The step has no steps before it " + op);
				// possible next action detected
				// additional restrictions, if the next action correspond to an
				// action performed
				// by self, it must necessarely be grounded
				if (!op.getAgent().isGrounded()
						|| op.getAgent().toString().equals(
								AutobiographicalMemory.GetInstance().getSelf())) {
					if (!op.getName().isGrounded()) {
						System.out
								.println("The next action by self is not grounded: "
										+ op.getName());
						return null;
					}
				}

				// the next action must have the preconditions verified
				if (!op.CheckPreconditions()) {
					System.out
							.println("The next action does not have the preconditions verified: "
									+ op.getName());
					return null;
				}

				return op;
			}
			ControlPanel.writeLog("The step has steps before: " + op);
		}
		return null;
	}

	/**
	 * Adds an Ordering Constraint of the Type A > B
	 * 
	 * @param before
	 *            - the id of the step A in A > B
	 * @param after
	 *            - the id of the step B in A > B
	 */
	public void AddOrderingConstraint(Integer before, Integer after) {

		OrderRelation orderBefore = null;
		OrderRelation orderAfter = null;
		OrderRelation order;
		OrderRelation aux;

		boolean afterChanged = false;
		boolean beforeChanged = false;

		ListIterator li;
		Integer stepID;

		if (before.equals(after)) {
			return;
		}
		// If we try to add Finish > A or A > Start, it should fail
		if (before.equals(this._finish.getID())
				|| after.equals(this._start.getID())) {
			this._valid = false;
			return;
		}

		li = this._orderingConstraints.listIterator();
		while (li.hasNext()) {
			order = (OrderRelation) li.next();

			if (order.getStepID().equals(before)) {
				orderBefore = order;

				if (order.getBefore().contains(after)) {
					// contradition in ordering constraints
					this._valid = false;
					return;
				}

				if (!order.getAfter().contains(after)) {
					beforeChanged = true;
					order.InsertAfter(after);
				}
			}
			if (order.getStepID().equals(after)) {
				orderAfter = order;

				if (order.getAfter().contains(before)) {
					// contradition in ordering constraints
					this._valid = false;
					return;
				}

				if (!order.getBefore().contains(before)) {
					afterChanged = true;
					order.InsertBefore(before);
				}
			}
		}

		if (orderBefore == null) {
			aux = new OrderRelation(before);
			aux.InsertAfter(after);
			this._orderingConstraints.add(aux);
		}

		if (orderAfter == null) {
			aux = new OrderRelation(after);
			aux.InsertBefore(before);
			this._orderingConstraints.add(aux);
		}

		if (beforeChanged) {
			// Adding ciclic relations
			li = orderBefore.getBefore().listIterator();
			while (li.hasNext()) {
				stepID = (Integer) li.next();
				AddOrderingConstraint(stepID, after);
			}
		}

		if (afterChanged) {
			// Adding ciclic relations
			li = orderAfter.getAfter().listIterator();
			while (li.hasNext()) {
				stepID = (Integer) li.next();
				AddOrderingConstraint(before, stepID);
			}
		}
	}

	/**
	 * Applies a set of substitutions to the Plan, grounding all variables in
	 * it. Example: Applying the substitution "[X]/John" in the name "Weak([X])"
	 * returns "Weak(John)".
	 * 
	 * @param bindings
	 *            - A list of substitutions of the type "[Variable]/value"
	 * @see Substitution
	 */
	private void MakeGround(ArrayList substs) {
		ListIterator li;
		Inequality ineq;

		for (li = this._inequalityConstraints.listIterator(); li.hasNext();) {
			ineq = (Inequality) li.next();
			ineq.MakeGround(substs);

			if (ineq.getVariable().equals(ineq.getValue())) {
				// the inequality is not valid, therefore the plan is not valid
				this._valid = false;
				return;
			}
			if (ineq.isGrounded()) {
				li.remove();
			}
		}

		li = this._steps.listIterator();
		while (li.hasNext()) {
			((Step) li.next()).MakeGround(substs);
		}

		this._start.MakeGround(substs);
		this._finish.MakeGround(substs);
		this._name.MakeGround(substs);
	}

	private boolean HasNoneBefore(Integer stepID) {

		ListIterator li;
		OrderRelation order;
		li = _orderingConstraints.listIterator();

		while (li.hasNext()) {
			order = (OrderRelation) li.next();
			if (order.getStepID().equals(stepID)) {
				if (order.getBefore().size() > 1)
					return false;
				if (order.getBefore().size() == 0)
					return true;
				return order.getBefore().contains(this._start.getID());
			}
		}
		return true;
	}

	private void UpdatePlanProbability() {
		ListIterator li;
		CausalLink l;
		CausalConflictFlaw conflict;
		Effect e;
		Step s;
		float prob = 1;

		for (li = _steps.listIterator(); li.hasNext();) {
			s = (Step) li.next();
			if (!s.getID().equals(_start.getID())
					&& !s.getID().equals(_finish.getID())) {
				prob = prob * s.GetProbability();
			}
		}

		li = _links.listIterator();

		while (li.hasNext()) {
			l = (CausalLink) li.next();
			if (!l.getSource().equals(_start.getID())) {
				e = getStep(l.getSource()).getEffect(l.getEffect());
				prob = prob * e.GetProbability();
			}
		}

		li = _ignoredConflicts.listIterator();

		while (li.hasNext()) {
			conflict = (CausalConflictFlaw) li.next();
			prob = prob * (1 - conflict.GetEffect().GetProbability());
		}

		_probability = prob;
		if (_probability == 0)
			_valid = false;
	}

	public boolean getChosenRecently() {
		return chosenRecentlyAsBestPlan;
	}

	public void setChosenRecently(boolean chosen) {
		chosenRecentlyAsBestPlan = chosen;
	}
}