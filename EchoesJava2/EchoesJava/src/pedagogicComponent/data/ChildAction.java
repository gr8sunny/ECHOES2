package pedagogicComponent.data;

import java.util.HashMap;
import java.util.Map;

public class ChildAction {

	static {
		ACTION_MAP = new HashMap<String, ChildAction>();
	}

	/**
	 * The list of child action known to the Pedagogic Component.
	 * 
	 * These are defined according to what is needed for the PC to define the
	 * agent's behaviour.
	 * 
	 */

	// high level definition of actions, related to agent's action
	// responded by performing the action indicated by the agent and if an
	// object was specified, on the specified object
	public static final ChildAction respondedToBid = new ChildAction(
			"respondedToBid");
	// resopnded by performing the action indicated by the agent, but not on the
	// specified object
	public static final ChildAction respondedDiffObject = new ChildAction(
			"respondedDiffObject");
	// touched the agnet - initiated interaction
	public static final ChildAction initiatedBid = new ChildAction(
			"initiatedBid");
	// gave the agent the object he requested
	public static final ChildAction gaveRequestedObject = new ChildAction(
			"gaveRequestedObject");
	// gave an object that wasn't requested but relevant to activity
	public static final ChildAction gaveObjectRelevantActivity = new ChildAction(
			"gaveObjectRelevantActivity");
	// gave the agent an object after he had requested one, but a different
	// object to the one requested
	public static final ChildAction gaveDiffObject = new ChildAction(
			"gaveDiffObject");
	// offered an object when the agent had not requested one
	public static final ChildAction offeredObject = new ChildAction(
			"offeredObject");
	// an action that is relevant to the activity - agent gives thumbs up
	public static final ChildAction activityRelevantAction = new ChildAction(
			"activityRelevantAction");
	// performed no action
	public static final ChildAction noAction = new ChildAction("noAction");
	// touched the agent
	public static final ChildAction userTouchedAgent = new ChildAction(
			"userTouchedAgent");
	// performed no action
	public static final ChildAction irrelevantAction = new ChildAction(
			"irrelevantAction");

	// giving object actions
	public static final ChildAction gave_ball = new ChildAction("gave_ball");
	public static final ChildAction gave_flower = new ChildAction("gave_flower");
	public static final ChildAction gave_flowerpot = new ChildAction(
			"gave_flowerpot");
	public static final ChildAction gave_basket = new ChildAction("gave_basket");

	// actions that are unrelated to agent's bids
	public static final ChildAction unrelatedAction_bubble_pop = new ChildAction(
			"unrelatedAction_bubble_pop");
	public static final ChildAction unrelatedAction_bubble_merge = new ChildAction(
			"unrelatedAction_bubble_merge");
	public static final ChildAction unrelatedAction_flower_pick = new ChildAction(
			"unrelatedAction_flower_pick");
	public static final ChildAction unrelatedAction_flower_ball = new ChildAction(
			"unrelatedAction_flower_ball");
	public static final ChildAction unrelatedAction_flower_bubble = new ChildAction(
			"unrelatedAction_flower_bubble");
	public static final ChildAction unrelatedAction_cloud_rain = new ChildAction(
			"unrelatedAction_cloud_rain");
	public static final ChildAction unrelatedAction_leaves_fly = new ChildAction(
			"unrelatedAction_leaves_fly");
	public static final ChildAction unrelatedAction_leaves_grow = new ChildAction(
			"unrelatedAction_leaves_grow");
	public static final ChildAction unrelatedAction_cloud_move = new ChildAction(
			"unrelatedAction_cloud_move");
	public static final ChildAction unrelatedAction_flower_putInPot = new ChildAction(
			"unrelatedAction_flower_putInPot");
	public static final ChildAction unrelatedAction_flower_grow = new ChildAction(
			"unrelatedAction_flower_grow");
	public static final ChildAction unrelatedAction_flowerpot_stack = new ChildAction(
			"unrelatedAction_flowerpot_stack");
	public static final ChildAction unrelatedAction_leaves_recharge = new ChildAction(
			"unrelatedAction_leaves_recharge");
	public static final ChildAction unrelatedAction_ball_throw = new ChildAction(
			"unrelatedAction_ball_throw");
	public static final ChildAction unrelatedAction_ball_bounce = new ChildAction(
			"unrelatedAction_ball_bounce");

	// lower level definition of actions, related to performing actions on
	// objects
	// bubble
	public static final ChildAction bubble_pop = new ChildAction("bubble_pop");

	public static final ChildAction bubble_merge = new ChildAction(
			"bubble_merge");

	// flower
	public static final ChildAction flower_pick = new ChildAction("flower_pick");

	public static final ChildAction flower_ball = new ChildAction("flower_ball");

	public static final ChildAction flower_bubble = new ChildAction(
			"flower_bubble");

	public static final ChildAction flower_putInPot = new ChildAction(
			"flower_putInPot");
	public static final ChildAction flower_placeInBasket = new ChildAction(
			"flower_placeInBasket");

	// cloud
	public static final ChildAction cloud_rain = new ChildAction("cloud_rain");

	public static final ChildAction cloud_move = new ChildAction("cloud_move");

	// flowerpot
	public static final ChildAction flower_grow = new ChildAction("flower_grow");

	public static final ChildAction stack_pot = new ChildAction("stack_pot");

	// leaves
	public static final ChildAction touch_leaves = new ChildAction(
			"touch_leaves");
	// ball throwing
	public static final ChildAction cloud_ball = new ChildAction("cloud_ball");
	// ball sorting
	public static final ChildAction container_ball = new ChildAction(
			"container_ball");

	private static Map<String, ChildAction> ACTION_MAP;

	private String name;

	/**
	 * Create a new instance with the given name.
	 * 
	 * Note that the constructor is private so that only the above static
	 * instances can be created.
	 * 
	 * @param name
	 *            the name of this instance.
	 * 
	 */
	private ChildAction(String name) {
		this.name = name;
		ACTION_MAP.put(name, this);
	}

	public static ChildAction getChildAction(String actionName)
			throws StringLabelException {
		ChildAction action = ACTION_MAP.get(actionName);
		if (action == null)
			throw new StringLabelException("Child action label does not match"
					+ actionName);
		return action;
	}

	public String getName() {
		return name;
	}

}
