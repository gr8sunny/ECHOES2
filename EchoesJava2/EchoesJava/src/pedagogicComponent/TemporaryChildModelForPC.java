package pedagogicComponent;

import java.util.HashMap;
import utils.Enums.EchoesActivity;
import utils.Enums.EchoesObjectType;
import utils.Enums.ScertsGoal;

/**
 * Temporary class to fake input from the child model
 * 
 * @author katerina
 * 
 */
public class TemporaryChildModelForPC {

	ChildStateHandler cs;
	boolean engagedECHOES = false;
	Integer activityLikeability = new Integer(0);
	Integer objectLikeability = new Integer(0);
	HashMap<String, Integer> goalAbility = new HashMap<String, Integer>();
	String affectiveState = "bored";

	public TemporaryChildModelForPC() {
	}

	/**
	 * 
	 * 
	 * @param activity
	 * @param objectName
	 * @param goal
	 * @param appropriateStartScene
	 *            is stable but needs to be updated in the CM at the end of the
	 *            session
	 * @param bubbleComplexity
	 */
	public HashMap<ScertsGoal, Integer> getGoalAbilityMap() {
		HashMap<ScertsGoal, Integer> goalAbilityMap = new HashMap<ScertsGoal, Integer>();
		goalAbilityMap.put(ScertsGoal.BriefInteraction, new Integer(
				0));
		goalAbilityMap.put(ScertsGoal.ExtendedInteraction,
				new Integer(2));

		System.out.println("ability map" + goalAbilityMap);

		return goalAbilityMap;
	}

	public HashMap<EchoesActivity, Integer> getActivityLikeMap() {
		HashMap<EchoesActivity, Integer> activityLikeMap = new HashMap<EchoesActivity, Integer>();
		activityLikeMap.put(EchoesActivity.FlowerPickToBasket, new Integer(0));
		activityLikeMap.put(EchoesActivity.FlowerTurnToBall, new Integer(0));
		activityLikeMap.put(EchoesActivity.FlowerGrow, new Integer(0));
		activityLikeMap.put(EchoesActivity.CloudRain, new Integer(0));
		activityLikeMap.put(EchoesActivity.PotStackRetrieveObject, new Integer(0));
		activityLikeMap.put(EchoesActivity.AgentPoke, new Integer(0));
		return activityLikeMap;
	}
	
	
	public HashMap<EchoesObjectType, Integer> getObjectLikeMap() {
		HashMap<EchoesObjectType, Integer> objectLikeMap = new HashMap<EchoesObjectType, Integer>();

		objectLikeMap.put(EchoesObjectType.IntroBubble, new Integer(0));
		objectLikeMap.put(EchoesObjectType.Bubble, new Integer(0));
		objectLikeMap.put(EchoesObjectType.Flower, new Integer(0));
		objectLikeMap.put(EchoesObjectType.MagicLeaves, new Integer(0));
		objectLikeMap.put(EchoesObjectType.Ball, new Integer(0));
		objectLikeMap.put(EchoesObjectType.Cloud, new Integer(0));
		objectLikeMap.put(EchoesObjectType.Pot, new Integer(0));
		return objectLikeMap;
	}
	
	
	/**
	 * Responds to changed in the child's engagement. At this level engagement
	 * is boolean.
	 * 
	 * @return boolean
	 */
	public void changeInChildEngagement(boolean isEngaged) {

		// TO-DO fill in reasoning process

		cs.setEngagedECHOES(isEngaged);
	}

	/**
	 * The PC can query the child model for the child's attitude towards the
	 * agent - this should be quite stable. But if any changes occur then the
	 * child model informs the PC.
	 * 
	 * @return
	 */
	public void changeAttitudeToAgent(boolean isOpen) {
		// TO DO add reasoning process for deducing whether the child is open to
		// interacting with the agent

	}

	public void changeBubbleSceneComplexity() {
	}
}
