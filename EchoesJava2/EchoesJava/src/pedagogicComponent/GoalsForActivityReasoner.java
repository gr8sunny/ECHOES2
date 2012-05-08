package pedagogicComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import utils.Interfaces.*;
import utils.Enums.*;
import utils.Logger;

/**
 * Important!! the names defined here are the ones used in the preconditions in
 * the action definitions in FAtiMA
 * 
 * @author katerina avramides
 * 
 */

public class GoalsForActivityReasoner extends PCcomponentHandler {

	private ArrayList<HashMap<String, String>> interactionHistory;
	private ArrayList<String> chosenBidTypes = new ArrayList<String>();
	private int turnNum;
	private EchoesActivity currentActivity;
	private String currentBidType = "";
	private EchoesObjectType targetObjectType;

	// bid types
	private static String pointBidType = "pointBidType";
	private static String verbalBidType = "verbalBidType";
	private static String lookBidType = "lookBidType";
	private static String touchBidType = "touchBidType";

	// bid purpose
	private static String bidAction = "bidAction";

	protected static HashMap<String, Integer> bidMethodAbility = new HashMap<String, Integer>();
	

	// in increasing order of difficulty!
	static {
		bidMethodAbility.put(pointBidType, new Integer(0));
		bidMethodAbility.put(verbalBidType, new Integer(0));
		bidMethodAbility.put(lookBidType, new Integer(0));
		bidMethodAbility.put(touchBidType, new Integer(0));
	}

	public GoalsForActivityReasoner(PCcomponents pCc, IDramaManager dmPrx,
			IActionEngine aePrx) {
		super(pCc, dmPrx, aePrx);
		interactionHistory = new ArrayList<HashMap<String, String>>();
		turnNum = 0;
		// add new HashMap for the first turn
		interactionHistory.add(turnNum, new HashMap<String, String>());
	}

	public void addChildActionToHistory(String childAction) {
		interactionHistory.get(turnNum).put("child", childAction);
		Logger.Log("info", "added child action: " + childAction + " to history, turn num: " + turnNum);
		// increase turn number here!
		// careful not to use turnNum to access current child action in methods
		// setting next action
		turnNum++;
		// add new HashMap for next turn
		interactionHistory.add(turnNum, new HashMap<String, String>());
	}

	public HashMap<String, String> getInteractionFromHistory(int turn) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Returns the bid typeF
	 * 
	 * @return
	 */
	public String getBidType() {
		String bidType = "";
		Random r = new Random();
		if (chosenBidTypes.isEmpty()) {
			initialiseBidTypesArray();
		}
		bidType = chosenBidTypes.get(r.nextInt(chosenBidTypes.size()));
		// remove chosen bid type so that it's not selected again
		chosenBidTypes.remove(bidType);
		if (bidType.equals("verbal")) {
			if (currentActivity == EchoesActivity.FlowerPickToBasket) {
				currentBidType = lookBidType;
			} else {
				currentBidType = verbalBidType;
			}
		} else if (bidType.equals("point")) {
			currentBidType = pointBidType;
		} else if (bidType.equals("look")) {
			currentBidType = lookBidType;
		} else if (bidType.equals("touch")) {
			if (currentActivity == EchoesActivity.CloudRain)
				currentBidType = pointBidType;
			else
				currentBidType = touchBidType;
		}
		// if (currentActivity == EchoesActivity.FlowerGrow) {
		// currentBidType = verbalBidType;
		// } else if (currentActivity == EchoesActivity.FlowerPickToBasket) {
		// currentBidType = pointBidType;
		// } else if (currentActivity == EchoesActivity.PotStackRetrieveObject)
		// {
		// currentBidType = pointBidType;
		// }
		return currentBidType;
	}

	/**
	 * When the bid type needs to be set by the agentBehaviourHandler
	 * 
	 * @param bidType
	 */
	public void setBidType(String bidType) {
		currentBidType = bidType;
	}

	public String getDefaultBidPurpose() {
		return bidAction;
	}

	// has to be called after getBidMethod - as this determines whether need an
	// object close by or far
	public String getTargetObject(EchoesObjectType requestObjectType) {
		if (requestObjectType != null) {
			this.targetObjectType = requestObjectType;
			return dmPrx.getTargetObject(requestObjectType, false);
		} else {
			EchoesObjectType objectType = null;
			if (currentActivity.equals(EchoesActivity.PotStackRetrieveObject)) {
				objectType = EchoesObjectType.Pot;
			} else if (currentActivity.equals(EchoesActivity.CloudRain)) {
				objectType = EchoesObjectType.Cloud;
			} else if (currentActivity
					.equals(EchoesActivity.FlowerPickToBasket)) {
				objectType = EchoesObjectType.Flower;
			} else if (currentActivity.equals(EchoesActivity.FlowerGrow)) {
				objectType = EchoesObjectType.Pot;
			} else if (currentActivity.equals(EchoesActivity.FlowerTurnToBall)) {
				objectType = EchoesObjectType.Flower;
			} else if (currentActivity.equals(EchoesActivity.BallThrowing)) {
				objectType = EchoesObjectType.Ball;
			} else if (currentActivity.equals(EchoesActivity.BallSorting)) {
				objectType = EchoesObjectType.Ball;
			}

			boolean contact = false;
			// if (interactionHistory.get(turnNum).get(bidMethod)
			// .equals(point_contact)
			// || interactionHistory.get(turnNum).get(bidMethod).equals(
			// point_contact_verbal)
			// || interactionHistory.get(turnNum).get(bidMethod).equals(
			// point_contact_gaze_verbal)) {
			// contact = true;
			// }
			this.targetObjectType = objectType;
			return dmPrx.getTargetObject(objectType, contact);
		}
	}

	public EchoesObjectType getTargetObjectType() {
		return this.targetObjectType;
	}

	public int getTurnNum() {
		return turnNum;
	}

	public void resetTurnNum() {
		turnNum = 0;
	}

	public void setCurrentActivity(EchoesActivity activity) {
		this.currentActivity = activity;
	}

	public void initialiseBidTypesArray() {
		chosenBidTypes.add("verbal");
		chosenBidTypes.add("look");
		chosenBidTypes.add("point");
		chosenBidTypes.add("touch");
	}

}
