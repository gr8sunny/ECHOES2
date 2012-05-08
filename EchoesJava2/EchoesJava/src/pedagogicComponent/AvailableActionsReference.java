package pedagogicComponent;

import java.util.ArrayList;
import java.util.HashMap;

import utils.Enums.EchoesActivity;
import utils.Enums.EchoesObjectType;
import pedagogicComponent.data.AgentAction;

/**
 * Used when communicating the agent action to the pedagogic component in order
 * to interpret the child's subsequent action. For example, if the agent made a
 * bid for the child to spin the flower and the child then span the flower, this
 * is a response to the bid.
 * 
 * @author katerina avramides
 * 
 */
public class AvailableActionsReference {

	// bid for the child to perform the action
	private HashMap<EchoesActivity, ArrayList<String>> bidForAction = new HashMap<EchoesActivity, ArrayList<String>>();
	// bid for the child to take their turn
	private HashMap<EchoesActivity, ArrayList<String>> bidForTurn = new HashMap<EchoesActivity, ArrayList<String>>();
	// bid for the child to perform the action
	private HashMap<EchoesActivity, ArrayList<String>> doAction = new HashMap<EchoesActivity, ArrayList<String>>();
	// request object
	private HashMap<EchoesObjectType, ArrayList<String>> requestObject = new HashMap<EchoesObjectType, ArrayList<String>>();

	public AvailableActionsReference() {

		// bid for child to take their turn

		// pickflower
		ArrayList<String> bidForTurn_pickFlower = new ArrayList<String>();
		// level 1 - by position in arrayList
		bidForTurn_pickFlower.add(AgentAction.requestTakeTurnGeneric_pickFlower.getName());
		// level 2 - by position in arrayList
		bidForTurn_pickFlower
				.add(AgentAction.requestTakeTurnSpecific_pickFlower.getName());
		// add the bidding for turn taking for the pickFlower activity
		bidForTurn.put(EchoesActivity.FlowerPickToBasket,
				bidForTurn_pickFlower);

		// flowerGrow
		ArrayList<String> bidForTurn_flowerGrow = new ArrayList<String>();
		// level 1 - by position in arrayList
		bidForTurn_flowerGrow.add(AgentAction.requestTakeTurnGeneric_flowerGrow.getName());
		// level 2 - by position in arrayList
		bidForTurn_flowerGrow
				.add(AgentAction.requestTakeTurnSpecific_flowerGrow.getName());
		// add the bidding for turn taking for the pickFlower activity
		bidForTurn.put(EchoesActivity.FlowerGrow, bidForTurn_flowerGrow);

		// flowerTurnToBall
		ArrayList<String> bidForTurn_flowerTurnToBall = new ArrayList<String>();
		// level 1 - by position in arrayList
		bidForTurn_flowerTurnToBall.add(AgentAction.requestTakeTurnGeneric_flowerBall
				.getName());
		// level 2 - by position in arrayList
		bidForTurn_flowerTurnToBall
				.add(AgentAction.requestTakeTurnSpecific_flowerBall.getName());
		// add the bidding for turn taking for the pickFlower activity
		bidForTurn.put(EchoesActivity.FlowerTurnToBall,
				bidForTurn_flowerTurnToBall);

		// cloudRain
		ArrayList<String> bidForTurn_cloudRain = new ArrayList<String>();
		// level 1 - by position in arrayList
		bidForTurn_cloudRain.add(AgentAction.requestTakeTurnGeneric_cloudRain.getName());
		// level 2 - by position in arrayList
		bidForTurn_cloudRain.add(AgentAction.requestTakeTurnSpecific_cloudRain
				.getName());
		// add the bidding for turn taking for the pickFlower activity
		bidForTurn.put(EchoesActivity.CloudRain, bidForTurn_cloudRain);

/*		// leavesFly
		ArrayList<String> bidForTurn_leavesFly = new ArrayList<String>();
		// level 1 - by position in arrayList
		bidForTurn_leavesFly.add(AgentAction.requestTakeTurnGeneric_flyLeaves.getName());
		// level 2 - by position in arrayList
		bidForTurn_leavesFly
				.add(AgentAction.requestTakeTurnSpecific_flyLeaves.getName());
		// add the bidding for turn taking for the pickFlower activity
		bidForTurn.put(EchoesActivity.LeavesFly, bidForTurn_leavesFly);

		// leavesGrow
		ArrayList<String> bidForTurn_leavesGrow = new ArrayList<String>();
		// level 1 - by position in arrayList
		bidForTurn_leavesGrow.add(AgentAction.requestTakeTurnGeneric_leavesGrow.getName());
		// level 2 - by position in arrayList
		bidForTurn_leavesGrow
				.add(AgentAction.requestTakeTurnSpecific_leavesGrow.getName());
		// add the bidding for turn taking for the pickFlower activity
		bidForTurn.put(EchoesActivity.LeavesGrow, bidForTurn_leavesGrow);
*/
		
		// potStackRetrieveObject
		ArrayList<String> bidForTurn_potStackRetrieveObject = new ArrayList<String>();
		// level 1 - by position in arrayList
		bidForTurn_potStackRetrieveObject
				.add(AgentAction.requestTakeTurnGeneric_stackPot.getName());
		// level 2 - by position in arrayList
		bidForTurn_potStackRetrieveObject
				.add(AgentAction.requestTakeTurnSpecific_stackPot.getName());
		// add the bidding for turn taking for the pickFlower activity
		bidForTurn.put(EchoesActivity.PotStackRetrieveObject,
				bidForTurn_potStackRetrieveObject);

		// request object

		// flower
		ArrayList<String> requestObject_flower = new ArrayList<String>();
		// level 1
		requestObject_flower.add(AgentAction.requestObjectGeneric_flower
				.getName());
		// level 2
		requestObject_flower.add(AgentAction.requestObjectSpecific_flower
				.getName());
		// add request object flower
		requestObject.put(EchoesObjectType.Flower, requestObject_flower);

		// flowerpot
		ArrayList<String> requestObject_flowerpot = new ArrayList<String>();
		// level 1
		requestObject_flowerpot.add(AgentAction.requestObjectGeneric_flowerpot
				.getName());
		// level 2
		requestObject_flowerpot.add(AgentAction.requestObjectSpecific_flowerpot
				.getName());
		// add request object flowerpot
		requestObject.put(EchoesObjectType.Pot,
				requestObject_flowerpot);

		// ball
		ArrayList<String> requestObject_ball = new ArrayList<String>();
		// level 1
		requestObject_ball.add(AgentAction.requestObjectGeneric_ball.getName());
		// level 2
		requestObject_ball
				.add(AgentAction.requestObjectSpecific_ball.getName());
		// add request object ball
		requestObject.put(EchoesObjectType.Ball, requestObject_ball);

	}

	/**
	 * Returns the agent's action in the form of the name of an AgentAction
	 * 
	 * Used by the PC to interpret the child's action - i.e. what action the
	 * agent performed prior to the child's action. E.g. if the agent made a bid
	 * for interaction and the child performed the requested action then the
	 * chid's action was a response to the agent's bid.
	 * 
	 * @param action
	 * @param activity
	 * @param bidType
	 * @param bidMethod
	 * @param purpose
	 * @return
	 */
	public String getAgentAction(String bidType, EchoesActivity activity,
			String bidMethod, EchoesObjectType objectType) {
		
		int bidMethodInt = 0;
		// if only verbal then will be generic
		if (bidMethod.equals("verbal")) {
			bidMethodInt = 0;
			// else will be a specific reference
		} else {
			bidMethodInt = 1;
		}
		String agentAction = "";
		if (bidType.equals("bidForTurn")) {
			agentAction = bidForTurn.get(activity).get(bidMethodInt);
			// NEED to add the rest of the actions/activities
		} else if (bidType.equals("requestObject")) {
			agentAction = requestObject.get(objectType).get(bidMethodInt);
		} else if (bidType.equals("promptForInitiation")) {
			agentAction = "promptForInitiation";
		}

		return agentAction;
	}

	public HashMap<EchoesActivity, ArrayList<String>> getBidForAction() {
		return bidForAction;
	}

	public HashMap<EchoesActivity, ArrayList<String>> getBidForTurn() {
		return bidForTurn;
	}

	public HashMap<EchoesActivity, ArrayList<String>> getDoAction() {
		return doAction;
	}

	public HashMap<EchoesObjectType, ArrayList<String>> getRequestObject() {
		return requestObject;
	}
}
