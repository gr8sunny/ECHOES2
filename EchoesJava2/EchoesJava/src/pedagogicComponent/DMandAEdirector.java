package pedagogicComponent;

import utils.Interfaces.IActionEngine;
import utils.Interfaces.IDramaManager;
import utils.Enums.EchoesActivity;
import utils.Enums.EchoesScene;
import utils.Enums.ScertsGoal;
import pedagogicComponent.data.StringLabelException;

/**
 * Directs the drama manager and fatima for the scenes where an agent can be
 * present (the bubble scene is handled by the NonAgentSceneParameterHandler).
 * 
 * @author katerina avramides
 * 
 */
public class DMandAEdirector extends PCcomponentHandler {

	public DMandAEdirector(PCcomponents pCc, IDramaManager dmPrx,
			IActionEngine aePrx) {
		super(pCc, dmPrx, aePrx);
	}

	/**
	 * Directs the DM and FAtiMA based on what objects are in the world.
	 * Adds/removes objects accordingly.
	 * 
	 * Keep it simple to begin with, only one object/activity available at a
	 * time
	 * 
	 * @param supportGoal
	 * @param agentPresent
	 * @param sceneName
	 * @param goal
	 * @param levelDirection
	 * @param activity
	 * @throws StringLabelException
	 */
	public void directDMandAEhandler(boolean supportGoal, boolean agentPresent,
			EchoesScene scene, ScertsGoal goal, int levelDirection,
			EchoesActivity activity) {
		System.out.println("In DMandAEdirector");
		// deactivate current agent goal if there is one active
		aePrx.setGoal("noGoal");

		// choose the number of times to repeat activity
		int numRep = getPCcs().activityDurationH.getNumRepetitions();
		// set the current activity in the activity duration handler
		getPCcs().activityDurationH.setCurrentActivity(activity);

		dmPrx.arrangeScene(scene, activity, numRep, false);
		// has been reset to false in DM sceneAndObjectsDirector
		dmPrx.setActivityStarted(true);

		// if the agent is supporting a goal
		if (supportGoal) {
			System.out.println("supporting a scerts goal, chosen activity: "
					+ activity);
			// communicate goal, level of direction and activity to the
			// AgentBehaviourHandler
			// the first value (true) indicates that the agent is interacting
			// with the child (or trying to...)
	//		getPCcs().agentH.setGoalAndActivity(true, goal, levelDirection,
	//				activity, numRep);
		} else {
			// if the agent is not supporting a goal
			System.out
					.println("NOT supporting a scerts goal, chosen activity: "
							+ activity);
			// if the agent is present
			if (agentPresent) {
				// communicate that agent present, but not supporting a goal to
				// the AgentBehaviourHander
		//		getPCcs().agentH.setGoalAndActivity(false, goal,
		//				levelDirection, activity, numRep);
				// get the agent to leave
				aePrx.setGoal("beAvailableWithoutInitiating");
				// if the agent is not present
			} else {
				// communicate for the agent to leave the scene if present
				aePrx.setGoal("exit");
			}

		}
	}
}
