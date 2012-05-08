package pedagogicComponent;

import java.util.ArrayList;
import java.util.HashMap;
import utils.Interfaces.IActionEngine;
import utils.Interfaces.IDramaManager;
import utils.Enums.ScertsGoal;

/**
 * Reasons about the child's skills in relation to the SCERTS goals
 * 
 * @author katerina
 * 
 */
public class SCERTSgoalHandler extends PCcomponentHandler {

	// map of the child's ability for each SCERTS goal (0, 1, 2)
	HashMap<ScertsGoal, Integer> goalAbilityMap = new HashMap<ScertsGoal, Integer>();
	ArrayList<ScertsGoal> childProfileGoals = new ArrayList<ScertsGoal>();
	ScertsGoal currentGoal;
	ScertsGoal targetGoal;

	public SCERTSgoalHandler(PCcomponents pCc, IDramaManager dmPrx,
			IActionEngine aePrx) {
		super(pCc, dmPrx, aePrx);
		goalAbilityMap = getPCcs().initialiser.initialiseGoalAbilityMap();
	}

	public void setTargetGoal(ScertsGoal goal) {
		this.targetGoal = goal;
	}

	/**
	 * Sets the initial values for the goal ability at start up from the Child
	 * Model
	 * 
	 * @param goalAbility
	 */
	public void setGoalAbility(
			HashMap<ScertsGoal, Integer> goalAbilityM) {
		this.goalAbilityMap = goalAbilityM;
		System.out.println("Set goal ability map: " + goalAbilityMap);
	}

	/**
	 * Sets the scerts goals specified in the child profile by the practitioner
	 * 
	 */
	public void setChildProfileGoals(ArrayList<ScertsGoal> childProfileGoals) {
		this.childProfileGoals = childProfileGoals;
	}

	/**
	 * Called by the agentInvolvementHandler when the decision has been made not
	 * to involve the agent. Checked by the agentBehaviourHandler, whether when
	 * the agent is present in the world he/she is pursuing a goal.
	 * 
	 */
	public void resetCurrentGoal() {
		currentGoal = null;
	}

	/**
	 * Decides which SCERTS goal to pursue
	 * 
	 * The changeScene argument indicates whether can change scene when
	 * selecting an appropriate activity
	 * 
	 */
	public ScertsGoal chooseSCERTSgoal() {
		ScertsGoal goal = null;

		System.out.println("Choosing a SCERTS goal to support");

		// if there is a target goal (set by practitioner)
		if (targetGoal == null) {
			// if there are practitioner specified scerts goals
			if (!childProfileGoals.isEmpty()) {
				System.out
						.println("The child profile has specified goals specified in order of priority");
				for (int i = 0; i < childProfileGoals.size(); i++) {
					// if one of these goals has an ability of 0 or 1
					System.out.println("the goals ability is: "
							+ goalAbilityMap.get(childProfileGoals.get(i))
									.intValue());
					if ((goalAbilityMap.get(childProfileGoals.get(i))
							.intValue() < 2)) {
						System.out
								.println("Chosen first goal with score 0 or 1: "
										+ childProfileGoals.get(i));
						goal = childProfileGoals.get(i);
						break;
					}
				}
				// if all goals have a score of 2 then choose the first
				// which is the highest priority
				if (goal == null) {
					System.out
							.println("All goals have score 2, so chosen first priority goal: "
									+ childProfileGoals.get(0));
					goal = childProfileGoals.get(0);
				}
			} else {
				System.out.println("The child profile has no specified goals");
				// choose from all goals available
				for (ScertsGoal key : goalAbilityMap.keySet()) {
					// if there's a goal with a score that's 0 or 1
					if (goalAbilityMap.get(key).intValue() < 2) {
						System.out.println("Chosen goal with score 0 or 1: "
								+ key);
						goal = key;
						break;
					}
				}
				// if all goals have a score of 2
				// choose one that's not the current one
				for (ScertsGoal key : goalAbilityMap.keySet()) {
					System.out
							.println("All goals have score 2, so chosen first priority goal: "
									+ key);
					if (!key.equals(currentGoal)) {
						goal = key;
						break;
					}
				}
			}
		} else {
			goal = targetGoal;
		}

		// for now default
		return ScertsGoal.BriefInteraction;
		
		// reset target goal to empty String
	//	targetGoal = null;
	}
}
