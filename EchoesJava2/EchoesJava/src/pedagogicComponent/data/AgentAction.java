package pedagogicComponent.data;

import java.util.HashMap;
import java.util.Map;

public class AgentAction {

	static {
		ACTION_MAP = new HashMap<String, AgentAction>();
	}

	/**
	 * The list of agent action known to the Pedagogic Component.
	 * 
	 * These are either specific action, e.g. spin flower or types of action,
	 * e.g. play without interacting with the child
	 * 
	 * each are then passed on to FAtiMA in the form of FAtiMA goals
	 * 
	 */

	// other
	public static final AgentAction look_atChild = new AgentAction(
			"look_atChild");

	public static final AgentAction enter = new AgentAction("enter");

	public static final AgentAction wave = new AgentAction("wave");

	// verbal
	// greet
	public static final AgentAction greet = new AgentAction("greet");
	public static final AgentAction say_goodbye = new AgentAction("say_goodbye");

	// re-engage
	public static final AgentAction say_ready = new AgentAction("say_ready");

	// bid for interaction - not needed with more detailed actions below
	/*public static final AgentAction bid_pickFlower = new AgentAction(
			"bid_pickFlower");
	public static final AgentAction bid_flowerBubble = new AgentAction(
			"bid_flowerBubble");
	public static final AgentAction bid_flowerBall = new AgentAction(
			"bid_flowerBall");
	public static final AgentAction bid_flowerGrow = new AgentAction(
			"bid_flowerGrow");
	public static final AgentAction bid_cloudRain = new AgentAction(
			"bid_cloudRain");
	public static final AgentAction bid_createStack = new AgentAction(
			"bid_createStack");
	public static final AgentAction bid_leavesFly = new AgentAction(
			"bid_leavesFly");
	public static final AgentAction bid_leavesGrow = new AgentAction(
			"bid_leavesGrow");*/

	// request action (generic)
	public static final AgentAction requestActionGeneric_pickFlower = new AgentAction(
			"requestActionGeneric_pickFlower");
	public static final AgentAction requestActionGeneric_flowerBubble = new AgentAction(
			"requestActionGeneric_flowerBubble");
	public static final AgentAction requestActionGeneric_flowerBall = new AgentAction(
			"requestActionGeneric_flowerBall");
	public static final AgentAction requestActionGeneric_flowerGrow = new AgentAction(
			"requestActionGeneric_flowerGrow");
	public static final AgentAction requestActionGeneric_cloudRain = new AgentAction(
			"requestActionGeneric_cloudRain");
	public static final AgentAction requestActionGeneric_stackPot = new AgentAction(
			"requestActionGeneric_stackPot");

	// request action (specific with gesture)
	public static final AgentAction requestActionSpecific_pickFlower = new AgentAction(
			"requestActionSpecific_pickFlower");
	public static final AgentAction requestActionSpecific_flowerBubble = new AgentAction(
			"requestActionSpecific_flowerBubble");
	public static final AgentAction requestActionSpecific_flowerBall = new AgentAction(
			"requestActionSpecific_flowerBall");
	public static final AgentAction requestActionSpecific_flowerGrow = new AgentAction(
			"requestActionSpecific_flowerGrow");
	public static final AgentAction requestActionSpecific_cloudRain = new AgentAction(
			"requestActionSpecific_cloudRain");
	public static final AgentAction requestActionSpecific_stackPot = new AgentAction(
			"requestActionSpecific_stackPot");

	// request generic - take turn
	public static final AgentAction requestTakeTurnGeneric_pickFlower = new AgentAction(
			"requestTakeTurnGeneric_pickFlower");
	public static final AgentAction requestTakeTurnGeneric_flowerBubble = new AgentAction(
	"requestTakeTurnGeneric_flowerBuble");
	public static final AgentAction requestTakeTurnGeneric_flowerBall = new AgentAction(
	"requestTakeTurnGeneric_flowerBall");
	public static final AgentAction requestTakeTurnGeneric_flowerGrow = new AgentAction(
	"requestTakeTurnGeneric_flowerGrow");
	public static final AgentAction requestTakeTurnGeneric_cloudRain = new AgentAction(
	"requestTakeTurnGeneric_couldRain");
	public static final AgentAction requestTakeTurnGeneric_stackPot = new AgentAction(
	"requestTakeTurnGeneric_stackPot");
	
	// request specific - take turn
	public static final AgentAction requestTakeTurnSpecific_pickFlower = new AgentAction(
			"requestTakeTurnSpecific_pickFlower");
	public static final AgentAction requestTakeTurnSpecific_flowerBubble = new AgentAction(
			"requestTakeTurnSpecific_flowerBubble");
	public static final AgentAction requestTakeTurnSpecific_flowerBall = new AgentAction(
			"requestTakeTurnSpecific_flowerBall");
	public static final AgentAction requestTakeTurnSpecific_flowerGrow = new AgentAction(
			"requestTakeTurnSpecific_flowerGrow");
	public static final AgentAction requestTakeTurnSpecific_cloudRain = new AgentAction(
			"requestTakeTurnSpecific_cloudRain");
	public static final AgentAction requestTakeTurnSpecific_stackPot = new AgentAction(
			"requestTakeTurnSpecific_stackPot");

	// request object - generic
	public static final AgentAction requestObjectGeneric_flower = new AgentAction(
			"requestObjectGeneric_flower");
	public static final AgentAction requestObjectGeneric_flowerpot = new AgentAction(
			"requestObjectGeneric_flowerpot");
	public static final AgentAction requestObjectGeneric_ball = new AgentAction(
			"requestObjectGeneric_ball");

	// request object - specific
	public static final AgentAction requestObjectSpecific_flower = new AgentAction(
			"requestObjectSpecific_flower");
	public static final AgentAction requestObjectSpecific_flowerpot = new AgentAction(
			"requestObjectSpecific_flowerpot");
	public static final AgentAction requestObjectSpecific_ball = new AgentAction(
			"requestObjectSpecific_ball");

	// points
	public static final AgentAction gesture_point = new AgentAction(
			"gesture_point");

	// bubble
	public static final AgentAction bubble_pop = new AgentAction("bubble_pop");

	// flower
	public static final AgentAction flower_pick = new AgentAction("flower_pick");

	public static final AgentAction flower_ball = new AgentAction("flower_ball");

	public static final AgentAction flower_bubble = new AgentAction("flower_bubble");

	public static final AgentAction flower_putInPot = new AgentAction(
			"flower_putInPot");

	// cloud
	public static final AgentAction cloud_rain = new AgentAction("cloud_rain");

	// flowerpot
	public static final AgentAction flowerpot_growFlower = new AgentAction(
			"flowerpot_growFlower");

	public static final AgentAction flowerpot_stack = new AgentAction(
			"flowerpot_stack");

	private static Map<String, AgentAction> ACTION_MAP;

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
	private AgentAction(String name) {
		this.name = name;
		ACTION_MAP.put(name, this);
	}

	public static AgentAction getAgentAction(String actionName)
			throws StringLabelException {
		AgentAction action = ACTION_MAP.get(actionName);
		if (action == null)
			throw new StringLabelException("Action label does not match"
					+ actionName);
		return action;
	}

	public String getName() {
		return name;
	}

}
