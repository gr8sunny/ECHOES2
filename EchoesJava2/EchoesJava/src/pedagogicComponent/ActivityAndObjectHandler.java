package pedagogicComponent;

import java.util.ArrayList;
import java.util.HashMap;
import utils.Interfaces.IActionEngine;
import utils.Interfaces.*;
import utils.Enums.*;
import utils.Logger;
import pedagogicComponent.data.StringLabelException;

/**
 * Reasons about what activity to choose/make available based on the child's
 * preferences in relation to objects, activities, etc.
 * 
 * @author katerina avramides
 * 
 */
public class ActivityAndObjectHandler extends PCcomponentHandler {

	HashMap<ScertsGoal, ArrayList<EchoesActivity>> goalNotAppropriateActivityMap = new HashMap<ScertsGoal, ArrayList<EchoesActivity>>();
	HashMap<EchoesActivity, Integer> activityLikeMap = new HashMap<EchoesActivity, Integer>();
	HashMap<EchoesObjectType, Integer> objectLikeMap = new HashMap<EchoesObjectType, Integer>();
	// a threshold value of how much the child likes the ability, below which
	// the activity is not chosen
	static int thresholdLikeActivityValue = 0;
	ArrayList<EchoesActivity> currentSessionActivities = new ArrayList<EchoesActivity>();
	// used if best to pursue a specific activity, e.g. if the child has started
	// interacting with a specific object and the agent wants to get involved
	private EchoesActivity targetActivity;

	public ActivityAndObjectHandler(PCcomponents pCc, IDramaManager dmPrx,
			IActionEngine aePrx) {
		super(pCc, dmPrx, aePrx);
		goalNotAppropriateActivityMap = getPCcs().initialiser
				.initialiseGoalNotAppropriateActivityMap(goalNotAppropriateActivityMap);
	}

	/**
	 * Sets a specific activity
	 * 
	 */
	public void setTargetActivity(EchoesActivity activity) 
	{
		this.targetActivity = activity;
	}

	/**
	 * Sets the activity likeability from the child profile at start-up
	 * 
	 * Need to specify a value if the activity has not been tried yet
	 * 
	 */
	public void setActivityLikeability(HashMap<EchoesActivity, Integer> activityLikeMap) 
	{
		this.activityLikeMap = activityLikeMap;
	}

	/**
	 * Sets the object likeability from the child profile at start-up
	 * 
	 * Need to specify a value if the object has not been used yet
	 * 
	 */
	public void setObjectLikeability(HashMap<EchoesObjectType, Integer> objectLikeMap) 
	{
		this.objectLikeMap = objectLikeMap;
	}

	/**
	 * Registers changes to the activity likeability and calls appropriate
	 * method to re-direct
	 * 
	 */
	public void changeActivityLikeability(EchoesActivity activity, Integer like) 
	{
		activityLikeMap.put(activity, like);
	}

	/**
	 * Registers changes to the object likeability and calls appropriate method
	 * to re-direct
	 * 
	 */
	public void changeObjectLikeability(EchoesObjectType objectType,Integer like) 
	{
		objectLikeMap.put(objectType, like);
	}

	/**
	 * Decides whether to change activity or repeat the same one
	 * 
	 */
	public boolean decideChangeActivity(boolean involveAgent) 
	{
		// if (involveAgent)
		return false;
	}

	/**
	 * Decides which activity to engage the child in.
	 * 
	 * The parameter supportGoal indicates whether there is an agent and so a
	 * scerts goal, the parameter agentPresent is relevant when there is no goal
	 * and it indicates whether the agent leaves the scene or is present in the
	 * background.
	 * 
	 * @throws StringLabelException
	 * 
	 */
	public void chooseActivity(boolean supportGoal, boolean agentPresent,
			EchoesScene scene, boolean changeScene, ScertsGoal goal,
			int levelDirection) {
		Logger.Log("info",
				"Choosing activity in " + scene);
		Logger.Log("info",
				"Supporting goal: " + goal);
		EchoesActivity activity = null;
		int likeValue = 0;

		// if there is a target activity then choose that one
		if (targetActivity == null) {

			// if current activity is ok then don't necessarily change
			// e.g. if the agent is just appearing, best stick with the same
			// activity
			if (decideChangeActivity(supportGoal)) {
				// TO DO
			}

			ArrayList<EchoesActivity> possibleActivities = new ArrayList<EchoesActivity>();

			// add all activities to the list of possible ones
			for (EchoesActivity key : activityLikeMap.keySet()) {
				possibleActivities.add(key);
				Logger.Log("info",
						"adding activity to list: " + key);
			}

			// TO DO some reasoning about whether it's appropriate to potential
			// change scene if an activity is chosen that requires it

			// if it's not OK to change the scene
			// i.e. when the agent is first appearing
			if (!changeScene) {
				// remove all activities that are not available in the scene
				for (int k = 0; k < possibleActivities.size(); k++) {
					if (!SceneDetails.getSceneActivities(scene).contains(
							possibleActivities.get(k))) {
						Logger.Log(
								"info",
								"removing activity from list because not available in specified scene: "
										+ possibleActivities.get(k));
						possibleActivities.remove(k);
					}
				}
			}

			// if working with an agent and goal
			if (supportGoal) {
				// remove the activities that are not appropriate to the goal
				ArrayList<EchoesActivity> goalNotAppActivities = goalNotAppropriateActivityMap
						.get(goal);
				for (int i = 0; i < goalNotAppActivities.size(); i++) {
					for (int y = 0; y < possibleActivities.size(); y++) {
						if (goalNotAppActivities.get(i).equals(
								possibleActivities.get(y))) {
							Logger.Log(
									"info",
									"removing activity from list because not suited to supporting specific goal: "
											+ possibleActivities.get(y));
							possibleActivities.remove(y);
						}
					}
				}
			}

			// remove all activities that have been done in the current session
			for (EchoesActivity key : activityLikeMap.keySet()) {
				for (int i = 0; i < currentSessionActivities.size(); i++) {
					if (key.equals(currentSessionActivities.get(i))) {
						Logger.Log("info", "removing activity: " + key + " because it has already been chosen in the current session");
						possibleActivities.remove(key);
					}
				}
			}

			likeValue = 10;
			for (int y = 0; y < possibleActivities.size(); y++) {
				if (activityLikeMap.get(possibleActivities.get(y)).intValue() < likeValue) {
					Logger.Log("info", "replaced with least liked activity: " + possibleActivities.get(y));
					activity = possibleActivities.get(y);
				}
			}

		} else {
			Logger.Log("info","had already set target activity: " + targetActivity);
			activity = targetActivity;
		}

		// add chosen activity to those chosen in the current session
		currentSessionActivities.add(activity);
		Logger.Log("info", "activities chosen in the current session: " + currentSessionActivities);

		// also add reasoning about whether to choose a new activity if child
		// has not tried many
		// but this will also depend on child affective state from CM so will
		// leave until this is sorted out

		// if at some point we have more objects for an activity, e.g. this
		// might be in the sorting, then add method here to choose which object
		// to work with

		getPCcs().director.directDMandAEhandler(supportGoal, agentPresent,
				scene, goal, levelDirection, activity);
		// reset target activity to empty
		targetActivity = null;
	}
}
