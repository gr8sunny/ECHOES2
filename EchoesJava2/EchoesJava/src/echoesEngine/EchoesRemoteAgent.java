package echoesEngine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import FAtiMA.Agent;
import FAtiMA.AgentSimulationTime;
import FAtiMA.ValuedAction;
import FAtiMA.deliberativeLayer.DeliberativeProcess;
import FAtiMA.deliberativeLayer.plan.Effect;
import FAtiMA.deliberativeLayer.plan.Step;
import FAtiMA.knowledgeBase.KnowledgeBase;
import FAtiMA.sensorEffector.Event;
import FAtiMA.sensorEffector.Parameter;
import FAtiMA.sensorEffector.RemoteAgent;
import FAtiMA.wellFormedNames.ComposedName;
import FAtiMA.wellFormedNames.Name;
import FAtiMA.wellFormedNames.Substitution;
import FAtiMA.wellFormedNames.SubstitutionSet;
import FAtiMA.wellFormedNames.Symbol;
import FAtiMA.wellFormedNames.Unifier;
import echoesEngine.ListenerManager;
import utils.Logger;
import utils.Interfaces.*;
import utils.Enums.*;

@SuppressWarnings("unused")
public class EchoesRemoteAgent extends RemoteAgent implements IPauseListener
{
	private IRenderingEngine rePrx;
	private IPedagogicComponent pcPrx;
	private IAgentListener agentPublisher;
	private RenderingEngineListenerImpl rlImpl;
  private AgentListenerImpl agentImpl;
  private EventListenerImpl eventImpl;
	private String agentId;
	private ActionNameConverter actionNameConv;
	private DeliberativeProcess _deliberativeLayer;	
	private String actionStarted = "";
	private String actionsAndDetails = "";
	private boolean noticedCloud = false;
	private int giveFlowerFeedback = 0;

	private class ActionSpec {
		String action;

		public ActionSpec(String action, List<String> args) {
			this.action = action;
			this.args = args;
		}

		List<String> args;

		@Override
		public boolean equals(Object obj) {
			return obj instanceof ActionSpec
					&& ((ActionSpec) obj).action.equals(action)
					&& ((ActionSpec) obj).args.equals(args);
		}

		@Override
		public int hashCode() {
			return action.hashCode() + args.hashCode();
		}
	}

	private Set<ActionSpec> executingActions;

	@SuppressWarnings("rawtypes")
  public EchoesRemoteAgent(Agent agent,
			String agentId, HashMap properties,
			DeliberativeProcess _deliberativeLayer,
			IRenderingEngine rePrx, IPedagogicComponent pcPrx) 
	{
		this.agentId = agentId;
		this._deliberativeLayer = _deliberativeLayer;
		actionNameConv = new ActionNameConverter();
		executingActions = Collections.synchronizedSet(new HashSet<ActionSpec>());

		// initial values for KB
		KnowledgeBase kb = KnowledgeBase.GetInstance();
		// positions one to nine are available to choose from
		kb.Tell(Name.ParseName("one(available"), "True");
		kb.Tell(Name.ParseName("two(available"), "True");
		kb.Tell(Name.ParseName("three(available"), "True");
		kb.Tell(Name.ParseName("four(available"), "True");
		kb.Tell(Name.ParseName("five(available"), "True");
		kb.Tell(Name.ParseName("six(available"), "True");
		kb.Tell(Name.ParseName("seven(available"), "True");
		kb.Tell(Name.ParseName("eight(available"), "True");
		kb.Tell(Name.ParseName("nine(available"), "True");
		kb.Tell(Name.ParseName("objectOffered()"), "False");
		kb.Tell(Name.ParseName("needToRemoveFlowerFromPot()"), "False");
		// flags to say the right thing
		kb.Tell(Name.ParseName("suggestedPotFlowerGrowing()"), "False");
		kb.Tell(Name.ParseName("hasLookedAtPotFlowerGrow()"), "False");

		// Connect to (and automatically start) the rendering engine
		this.rePrx = rePrx;

		// connect to the pedagogic component
		this.pcPrx = pcPrx;

		_running = true;
		_canAct = true;
		_actions = new ArrayList();

		ListenerManager listenerMgr = ListenerManager.GetInstance();

		rlImpl = new RenderingEngineListenerImpl();
		listenerMgr.Subscribe(rlImpl);
		
		agentImpl = new AgentListenerImpl();
		listenerMgr.Subscribe(agentImpl);
		
		eventImpl = new EventListenerImpl();
		listenerMgr.Subscribe(eventImpl);

		listenerMgr.registerForEvents(this, ListenerType.pause);
		
		agentPublisher = (IAgentListener)listenerMgr.retrieve(ListenerType.agent);

		this._agent = agent;
	}

	@Override
	protected boolean Send(String msg) {
		// Logger.Log("info", "Send: " + msg);
		return true;
	}

	@Override
	public void run() {
		// Do nothing; we will get callbacks if anything of interest happens
	}

	@Override
	public void ShutDown() 
	{
		Logger.Log("info", "Unsubscribing ...");
		ListenerManager listenerMgr = ListenerManager.GetInstance();
		listenerMgr.Unsubscribe(rlImpl);
		listenerMgr.Unsubscribe(agentImpl);
		listenerMgr.Unsubscribe(eventImpl);
		super.ShutDown();
	}

	@SuppressWarnings("rawtypes")
  @Override
	protected void StartAction(ValuedAction vAction) {
		Logger.Log("info",
				"Starting action " + vAction);

		KnowledgeBase kb = KnowledgeBase.GetInstance();

		final String actionName = vAction.GetAction().GetFirstLiteral()
				.getName();
		final LinkedList<String> args = new LinkedList<String>();
		boolean first = true;
		for (Object obj : vAction.GetAction().GetLiteralList()) {
			if (first) {
				first = false;
			} else {
				args.add(((Symbol) obj).getName());
			}
		}

		// need this in order to ignore the first, second, firstAfter,
		// secondAfter, etc actions of an action when processing
		// ActionCompleted, so that each bunch of actions is only processed as
		// completed once
		actionStarted = actionName;

		// need to call this first or reName and reArgs will be null
		actionNameConv.convert(actionName, args);
		final String realActionName = actionNameConv.getReName();
		final LinkedList<String> realArgs = actionNameConv.getReArgs();

		System.out.println("the arguments for action: " + realActionName
				+ " are: " + realArgs);

		// hack because of delay in publishing object removed - when flower
		// turned to bubble/ball so that not chosen for bid
		if (actionName.equals("SelfTurnFlowerToBall")
				|| actionName.equals("SelfTurnFlowerToBubble")) {
			kb.Tell(Name.ParseName(args.get(0) + "(toBeRemoved)"), "True");
		}

		// hack because when he's unstacking a pot to make it available for
		// flower growing the (toBeUnstacked) property gets reset for the pot
		// he's holding so he doesn't put it down)
		ArrayList potsToBeUnstacked = kb.GetPossibleBindings(Name
				.ParseName("[x](toBeUnstacked)"));
		if (potsToBeUnstacked != null) {
			for (int i = 0; i < potsToBeUnstacked.size(); i++) {
				SubstitutionSet subSet = (SubstitutionSet) potsToBeUnstacked
						.get(i);
				ArrayList subs = subSet.GetSubstitutions();
				for (int j = 0; j < subs.size(); j++) {
					Substitution sub = (Substitution) subs.get(j);
					if (kb.AskProperty(Name.ParseName(sub.getValue().getName()
							+ "(toBeUnstacked)")) != null
							&& kb.AskProperty(
									Name.ParseName(sub.getValue().getName()
											+ "(toBeUnstacked)"))
									.equals("True")
							&& kb.AskProperty(Name
									.ParseName("madeAPotAvailable()")) != null
							&& !kb.AskProperty(
									Name.ParseName("madeAPotAvailable()"))
									.equals("True")) {
						kb.Tell(Name.ParseName(sub.getValue().getName()
								+ "(toBeUnstacked)"), "True");
					}
				}
			}
		}

		String firstActionName = "";
		LinkedList<String> firstActionArgs = new LinkedList<String>();

		String secondActionName = "";
		LinkedList<String> secondActionArgs = new LinkedList<String>();

		String firstAfterActionName = "";
		LinkedList<String> firstAfterActionArgs = new LinkedList<String>();

		String secondAfterActionName = "";
		LinkedList<String> secondAfterActionArgs = new LinkedList<String>();

		String thirdAfterActionName = "";
		LinkedList<String> thirdAfterActionArgs = new LinkedList<String>();

		// before the main action
		if (actionName.equals("SelfWalkInNoticeChild")) {
			firstActionName = "SetPosition";
			firstActionArgs.add("-6");
			firstActionArgs.add("0");
		}

		// set the agent to look at the child before making a bid
		if (actionName.equals("SelfPointBid")) {
			firstActionName = "TurnToChild";
			firstActionArgs.add("");
		}
		if (kb.AskProperty(Name
				.ParseName("FlowerPickToBasket(isChosenActivity)")) != null
				&& kb.AskProperty(
						Name.ParseName("FlowerPickToBasket(isChosenActivity)"))
						.equals("True")) {
			// don't indicate 'your turn'
		} else if (actionName.equals("SelfPointBid")) {
			secondActionName = "Gesture";
			secondActionArgs.add("your_turn");
			secondActionArgs.add("hold=1");
			secondActionArgs.add(actionNameConv.getYourTurnArg());
		}

		if (actionName.equals("SelfVerbalBid")) {
			firstActionName = "TurnToChild";
			firstActionArgs.add("");
		}
		if (actionName.equals("SelfLookBid")) {
			firstActionName = "TurnToChild";
			firstActionArgs.add("");
		}
		if (kb.AskProperty(Name
				.ParseName("FlowerPickToBasket(isChosenActivity)")) != null
				&& kb.AskProperty(
						Name.ParseName("FlowerPickToBasket(isChosenActivity)"))
						.equals("True")) {
			// don't indicate 'your turn'
		} else if (actionName.equals("SelfLookBid")) {
			secondActionName = "Gesture";
			secondActionArgs.add("your_turn");
			secondActionArgs.add("hold=1");
			secondActionArgs.add(actionNameConv.getYourTurnArg());
		}

		if (actionName.equals("SelfTouchBid")) {
			firstActionName = "TurnToChild";
			firstActionArgs.add("");
		}
		if (kb.AskProperty(Name
				.ParseName("FlowerPickToBasket(isChosenActivity)")) != null
				&& kb.AskProperty(
						Name.ParseName("FlowerPickToBasket(isChosenActivity)"))
						.equals("True")) {
			// don't indicate 'your turn'
		} else if (actionName.equals("SelfTouchBid")) {
			secondActionName = "Gesture";
			secondActionArgs.add("your_turn");
			secondActionArgs.add("hold=1");
			secondActionArgs.add(actionNameConv.getYourTurnArg());
		}

		if (actionName.equals("SelfWait")) {
			firstActionName = "TurnToChild";
			firstActionArgs.add("");
		}
		if (actionName.equals("SelfGreetChild")) {
			firstActionName = "TurnToChild";
			firstActionArgs.add("");
		}
		if (actionName.equals("SelfGiveThumbsUp")) {
			firstActionName = "TurnToChild";
			firstActionArgs.add("");
		}
		if (actionName.equals("SelfIndicateTakingTurn")) {
			firstActionName = "TurnToChild";
			firstActionArgs.add("");
		}
		if (actionName.equals("SelfEndActivity")) {
			firstActionName = "TurnToChild";
			firstActionArgs.add("");
			secondActionName = "Gesture";
			secondActionArgs.add("looking_around_floor");
			secondActionArgs.add("hmmm-2.wav");
			secondActionArgs.add("speed=0.8");
		}

		if (actionName.equals("SelfFindBasket")) {
			firstActionName = "TurnToChild";
			firstActionArgs.add("");
		}
		if (actionName.equals("SelfPutAcceptedBasketDown")) {
			firstActionName = "TurnToChild";
			firstActionArgs.add("");
		}
		if (actionName.equals("SelfLookAroundMakeComment")) {
			firstActionName = "TurnToChild";
			firstActionArgs.add("");
		}
		if (actionName.equals("SelfLookAroundSpotObject")) {
			firstActionName = "TurnToChild";
			firstActionArgs.add("");
		}

		if (actionName.equals("SelfLeaveScene")
				|| actionName.equals("SelfTellChildLeaving")) {
			firstActionName = "TurnToChild";
			firstActionArgs.add("");
		}

		if (actionName.equals("SelfExploreCloud")) {
			firstActionName = "LookAtObject";
			firstActionArgs.add(args.get(0));
			if (!noticedCloud) {
				firstActionArgs.add("Look_a_cloud.wav");
				noticedCloud = true;
			}
		}

		if (actionName.equals("SelfPushCloud")) {
			firstActionName = "AttachCloud";
			firstActionArgs.add(args.get(0));
		}

		if (actionName.equals("SelfPickUpFlower")) {
			firstActionName = "LookAtObject";
			firstActionArgs.add(args.get(0));
		}

		if (actionName.equals("SelfPickUpBasket")) {
			firstActionName = "LookAtObject";
			firstActionArgs.add(args.get(0));
		}
		if (actionName.equals("SelfPickUpPot")) {
			firstActionName = "LookAtObject";
			firstActionArgs.add(args.get(0));
		}
		if (actionName.equals("SelfTurnFlowerToBall")) {
			firstActionName = "LookAtObject";
			firstActionArgs.add(args.get(0));
		}

		// after the main action

		// make touch bid
		if (actionName.equals("SelfTouchBid")) {
			firstAfterActionName = "TouchObject";
			firstAfterActionArgs.add(args.get(0));
			firstAfterActionArgs.add("WalkTo=True");
		}
		if (actionName.equals("SelfTouchBid")) {
			secondAfterActionName = "LookAtChild";
			secondAfterActionArgs.add("");
		}

		// make look bid
		if (actionName.equals("SelfLookBid")) {
			firstAfterActionName = "LookAtChild";
			firstAfterActionArgs.add("");
		}

		// action to make a point bid
		if (actionName.equals("SelfPointBid")) {
			firstAfterActionName = "PointAt";
			firstAfterActionArgs.add(args.get(0));
		}
		if (actionName.equals("SelfPointBid")) {
			secondAfterActionName = "LookAtChild";
			secondAfterActionArgs.add("");
		}

		// action to walk in and notice child
		// if (actionName.equals("SelfWalkInNoticeChild")) {
		// firstAfterActionName = "Gesture";
		// firstAfterActionArgs.add("looking_around");
		// firstAfterActionArgs.add("hmmm-2.wav,nice-garden.wav");
		// }
		if (actionName.equals("SelfWalkInNoticeChild")) {
			secondAfterActionName = "LookAtChild";
			secondAfterActionArgs.add("");

		}

		// action to look around and spot an object
		if (actionName.equals("SelfLookAroundMakeComment")) {
			if (kb.AskProperty(Name.ParseName("noticedChild()")) != null
					&& kb.AskProperty(Name.ParseName("noticedChild()")).equals(
							"True")) {
				firstAfterActionName = "LookAtChild";
				firstAfterActionArgs.add("");
			}
		}

		// sharing event with child
		if (actionName.equals("SelfReactToAndShareEvent")) {
			firstAfterActionName = "LookAtObject";
			firstAfterActionArgs.add(args.get(0));
		}
		if (actionName.equals("SelfReactToAndShareEvent")) {
			secondAfterActionName = "PointAt";
			secondAfterActionArgs.add(args.get(0));
		}
		if (actionName.equals("SelfReactToAndShareEvent")) {
			thirdAfterActionName = "LookAtChild";
			thirdAfterActionArgs.add("");
		}

		// action to push cloud
		if (actionName.equals("SelfPushCloud")) {
			firstAfterActionName = "DetachCloud";
			firstAfterActionArgs.add("None");
		}

		// action to pop bubble
		if (actionName.equals("SelfPopBubble")) {
			firstAfterActionName = "PopBubble";
			firstAfterActionArgs.add(args.get(0));
		}

		// in exploration with agent when child gives flower and agent will turn
		// to ball first need to put down
		if (actionName.equals("SelfFlowerBallExplorationAcceptFlower")) {
			firstAfterActionName = "PutFlowerDown";
			firstAfterActionArgs.add(args.get(0));
			firstAfterActionArgs.add(actionNameConv.getAvailablePosition());
		}

		// reset
		if (actionName.equals("SelfWalkOut")) {
			noticedCloud = false;
		}
		// if (actionName.equals("SelfNoticeEvent")) {
		// firstAfterActionName = "TurnToChild";
		// firstAfterActionArgs.add("");
		// }

		final List<String> voiceArgs = new LinkedList<String>();
		// Speech

		if (actionName.equals("SelfPickUpPotToMakeAvailable")) {
			if (kb.AskProperty(Name.ParseName("suggestedPotFlowerGrowing()")) != null
					&& kb.AskProperty(
							Name.ParseName("suggestedPotFlowerGrowing()"))
							.equals("False")) {
				voiceArgs.add("wonder-what-pots-for.wav");
				kb.Tell(Name.ParseName("suggestedPotFlowerGrowing()"), "True");
			}
		} else if (actionName.equals("SelfPushCloud")) {
			if (kb.AskProperty(Name.ParseName("hasLookedAtPotFlowerGrow()")) != null
					&& kb.AskProperty(
							Name.ParseName("hasLookedAtPotFlowerGrow()"))
							.equals("False")) {
				voiceArgs.add("I_know_grow_flowers.wav");
				kb.Tell(Name.ParseName("suggestedPotFlowerGrowing()"), "True");
				kb.Tell(Name.ParseName("hasLookedAtPotFlowerGrow()"), "True");
			}
		}
		// facial expressions
		final LinkedList<String> expressionArgs = new LinkedList<String>();
		if (actionName.equals("SelfGreetChild")
				|| actionName.equals("SelfGiveThumbsUp")
				|| actionName.equals("SelfEndActivity")
				|| actionName.equals("SelfReactToEvent")
				|| actionName.equals("SelfReactToEventGeneral")
				|| actionName.equals("SelfAcceptBasket")
				|| actionName.equals("SelfGiggle")) {
			expressionArgs.add("Grin");
		} else if (actionName.equals("SelfLookAroundMakeComment")
				|| actionName.equals("SelfReactToAndShareEvent")
				|| actionName.equals("SelfSayReady")
				|| actionName.equals("SelfRequestObject")
				|| actionName.equals("SelfPointBid")
				|| actionName.equals("SelfLookBid")
				|| actionName.equals("SelfVerbalBid")
				|| actionName.equals("SelfLeaveScene")) {
			expressionArgs.add("Happy");
		} else {
			expressionArgs.add("Neutral");
		}

		_canAct = false;

		// send the PC the AE action name and args
		pcPrx.sendActionStartedAEnameAndArgs(actionName, args);

		synchronized (executingActions) {
			System.out.println("in executing actions");
			if (!firstActionName.isEmpty()) {
				if (rePrx.executeAction(agentId, firstActionName,
						firstActionArgs)) {
					executingActions.add(new ActionSpec(firstActionName,
							firstActionArgs));
				}
			}
			if (!secondActionName.isEmpty()) {
				if (rePrx.executeAction(agentId, secondActionName,
						secondActionArgs)) {
					executingActions.add(new ActionSpec(secondActionName,
							secondActionArgs));
				}
			}
			if (rePrx.executeAction(agentId, realActionName, realArgs)) {
				executingActions.add(new ActionSpec(realActionName, realArgs));
			} else {
				// reset the goal to notice the event - if the action has failed
				// (because the object is no longer in the world)
				if (actionName.equals("SelfNoticeEvent")) {
					kb.Tell(Name.ParseName("noticeEvent()"), "False");
				}
				// reset accept object goal
				kb.Tell(Name.ParseName("childOfferedObject()"), "False");
				// reset pick up action flags
				kb.Tell(Name.ParseName("pickedUpPotToPutDown()"), "False");
				kb.Tell(Name.ParseName("pickedUpFlowerToPutInPot()"), "False");
				kb.Tell(Name.ParseName("pickedUpFlower()"), "False");
				kb.Tell(Name.ParseName("pickedUpBasket()"), "False");
				kb.Tell(Name.ParseName("pickedUpPot()"), "False");
				kb.Tell(Name.ParseName("acceptBall()"), "False");
			}
			if (!firstAfterActionName.isEmpty()) {
				if (rePrx.executeAction(agentId, firstAfterActionName,
						firstAfterActionArgs)) {
					executingActions.add(new ActionSpec(firstAfterActionName,
							firstAfterActionArgs));
				}
			}
			if (!secondAfterActionName.isEmpty()) {
				if (rePrx.executeAction(agentId, secondAfterActionName,
						secondAfterActionArgs)) {
					executingActions.add(new ActionSpec(secondAfterActionName,
							secondAfterActionArgs));
				}
			}
			if (!thirdAfterActionName.isEmpty()) {
				if (rePrx.executeAction(agentId, thirdAfterActionName,
						thirdAfterActionArgs)) {
					executingActions.add(new ActionSpec(thirdAfterActionName,
							thirdAfterActionArgs));
				}
			}

			if (!voiceArgs.isEmpty()) {
				if (rePrx.executeAction(agentId, "Say", voiceArgs)) {
					executingActions.add(new ActionSpec("Say", voiceArgs));
				}
			}
			if (!expressionArgs.isEmpty()) {
				if (rePrx.executeAction(agentId, "FacialExpression",
						expressionArgs)) {
					executingActions.add(new ActionSpec("FacialExpression",
							expressionArgs));
				}
			}
			if (executingActions.isEmpty()) {
				System.out.println("Nothing got executed");
				// Nothing actually got executed ...
				_canAct = true;
				_deliberativeLayer.Reset();
			}
		}

	}
	
	// IPauseListener
	public void setPaused(boolean paused) 
	{
    // Adapted from RemoteAgent.CmdPerception
    Logger.Log("info","Setting paused to " + paused);
    _running = !paused;
    if (paused) {
      AgentSimulationTime.GetInstance().Stop();
    } else {
      AgentSimulationTime.GetInstance().Resume();
    }
  }

	private class RenderingEngineListenerImpl implements IRenderingListener 
	{

		public void agentAdded(String agentId, Map<String, String> props) {
			Logger.Log("info",
					"Adding an agent " + agentId + " " + props);
			KnowledgeBase kb = KnowledgeBase.GetInstance();
			for (String key : props.keySet()) {
				kb.Tell(new ComposedName(agentId, key), props.get(key));
			}
		}

		public void agentPropertyChanged(String agentId, String propName,
				String propValue) {
			KnowledgeBase.GetInstance().Tell(
					new ComposedName(propName, agentId), propValue);
			if (propName.equals("Visible")) {
				if (propValue.equals("True")) {
					KnowledgeBase.GetInstance().Tell(
							Name.ParseName("agentIsInEchoes()"), "True");
				} else {
					KnowledgeBase.GetInstance().Tell(
							Name.ParseName("agentIsInEchoes()"), "False");
				}
			}
		}

		public void agentRemoved(String agentId) {
			// Remove all KB properties that have this agent as an argument
			ComposedName query = new ComposedName("[x]", agentId);
			KnowledgeBase kb = KnowledgeBase.GetInstance();
			if (kb.GetPossibleBindings(query) != null) {
				for (Object obj : kb.GetPossibleBindings(query)) {
					SubstitutionSet bindings = (SubstitutionSet) obj;
					Name tmpName = (Name) query.clone();
					tmpName.MakeGround(bindings.GetSubstitutions());
					kb.Retract(tmpName);
				}
			}
		}

		public void objectAdded(String objId, Map<String, String> props) {
			Logger.Log("info",
					"Adding an object " + objId + " " + props);
			KnowledgeBase kb = KnowledgeBase.GetInstance();
			for (String key : props.keySet()) {
				kb.Tell(new ComposedName(objId, key), props.get(key));
			}
			kb.Tell(Name.ParseName(objId + "(type)"), props.get("type"));
			// if (props.get("type").equals("Pot")) {
			// kb.Tell(Name.ParseName(objId + "(isStacked)"), "False");
			// }
			System.out.println("in action engine, adding object: " + objId
					+ "(type): " + props.get("type"));

			// Hack again ... just for now
			kb.Tell(Name.ParseName("at(" + _agent.name() + "," + objId + ")"),
					"False");
			// kb.Tell(Name.ParseName("onground(" + objId + ")"), "True");
		}

		@SuppressWarnings("rawtypes")
    public void objectPropertyChanged(String objId, String propName,
				String propValue) {
			KnowledgeBase kb = KnowledgeBase.GetInstance();
			Logger.Log(
					"info",
					"%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%Object property changed for "
							+ objId + " " + propName + " " + propValue);
			// check for pot stacking activity: whether a pot has a flower in it
			if (kb.AskProperty(Name
					.ParseName("PotStackRetrieveObject(isChosenActivity)")) != null
					&& kb
							.AskProperty(
									Name
											.ParseName("PotStackRetrieveObject(isChosenActivity)"))
							.equals("True")) {
				ArrayList listOfPots = kb.GetPossibleBindings(Name
						.ParseName("[x](type)"));
				for (int i = 0; i < listOfPots.size(); i++) {
					SubstitutionSet subSet = (SubstitutionSet) listOfPots
							.get(i);
					ArrayList subs = subSet.GetSubstitutions();
					for (int j = 0; j < subs.size(); j++) {
						Substitution sub = (Substitution) subs.get(j);
						if (kb.AskProperty(Name.ParseName(sub.getValue()
								.getName()
								+ "(type)")) != null
								&& (kb.AskProperty(Name.ParseName(sub
										.getValue().getName()
										+ "(type)")).equals("Pot"))) {
							if (kb.AskProperty(Name.ParseName(sub.getValue()
									.getName()
									+ "(hasFlower)")) != null
									&& kb.AskProperty(
											Name.ParseName(sub.getValue()
													.getName()
													+ "(hasFlower)")).equals(
											"True")) {
								kb
										.Tell(
												Name
														.ParseName("needToRemoveFlowerFromPot()"),
												"True");
								kb.Tell(Name.ParseName(sub.getValue().getName()
										+ "(removeFlower)"), "True");
								System.out
										.println("********* updated to removed flower from "
												+ sub.getValue().getName());
							}
						}
					}
				}
			}

			// return value to false if appropriate
			boolean noFlowerNeedsRemoving = true;
			ArrayList anotherListOfPpots = kb.GetPossibleBindings(Name
					.ParseName("[x](type)"));
			for (int i = 0; i < anotherListOfPpots.size(); i++) {
				SubstitutionSet subSet = (SubstitutionSet) anotherListOfPpots
						.get(i);
				ArrayList subs = subSet.GetSubstitutions();
				for (int j = 0; j < subs.size(); j++) {
					Substitution sub = (Substitution) subs.get(j);
					if (kb.AskProperty(Name.ParseName(sub.getValue().getName()
							+ "(type)")) != null
							&& (kb.AskProperty(Name.ParseName(sub.getValue()
									.getName()
									+ "(type)")).equals("Pot"))) {
						if (kb.AskProperty(Name.ParseName(sub.getValue()
								.getName()
								+ "(removeFlower)")) != null
								&& kb.AskProperty(
										Name.ParseName(sub.getValue().getName()
												+ "(removeFlower)")).equals(
										"True")) {
							noFlowerNeedsRemoving = false;
						}
					}
				}
			}
			if (noFlowerNeedsRemoving) {
				kb.Tell(Name.ParseName("needToRemoveFlowerFromPot()"), "False");
			}

			if (propName.equals("atAgent")) {
				kb.Tell(Name.ParseName("atAgent(" + _agent.name() + "," + objId
						+ ")"), propValue);
			} else if (propName.equals("nearAgent")) {
				kb.Tell(Name.ParseName("nearAgent(" + _agent.name() + ","
						+ objId + ")"), propValue);
			} else if (propName.equals("HorizontalSlot")) {
				if (kb.AskProperty(Name.ParseName(objId + "(type)")) != null
						&& (kb.AskProperty(Name.ParseName(objId + "(type)"))
								.equals("Pot") || kb.AskProperty(
								Name.ParseName(objId + "(type)")).equals(
								"Basket"))) {
					// first update position if not null, for this need access
					// to old position so do this first
					if (kb.AskProperty(Name.ParseName(objId + "(position)")) != null) {
						String oldPosition = (String) KnowledgeBase
								.GetInstance().AskProperty(
										Name.ParseName(objId + "(position)"));
						// check if other pots are in the same position before
						// making it available (if pot in a stack)
						boolean makePosAvailable = true;
						if (kb.GetPossibleBindings(Name.ParseName("[x](type)")) != null) {
							ArrayList pots = kb.GetPossibleBindings(Name
									.ParseName("[x](type)"));
							for (int i = 0; i < pots.size(); i++) {
								SubstitutionSet subSet = (SubstitutionSet) pots
										.get(i);
								ArrayList subs = subSet.GetSubstitutions();
								for (int j = 0; j < subs.size(); j++) {
									Substitution sub = (Substitution) subs
											.get(j);
									if (kb.AskProperty(Name.ParseName(sub
											.getValue().getName()
											+ "(type)")) != null
											&& (kb.AskProperty(
													Name.ParseName(sub
															.getValue()
															.getName()
															+ "(type)"))
													.equals("Pot") || kb
													.AskProperty(
															Name.ParseName(sub
																	.getValue()
																	.getName()
																	+ "(type)"))
													.equals("Basket"))) {
										if (!sub.getValue().getName().equals(
												objId)
												&& kb.AskProperty(Name
														.ParseName(sub
																.getValue()
																.getName()
																+ "(position")) != null
												&& kb.AskProperty(
														Name.ParseName(sub
																.getValue()
																.getName()
																+ "(position"))
														.equals(oldPosition)) {
											makePosAvailable = false;
										}
									}
								}
							}
						}
						if (makePosAvailable) {
							kb
									.Tell(Name.ParseName(oldPosition
											+ "(available)"), "True");
						}
						System.out.println(oldPosition
								+ " was updated to available? "
								+ makePosAvailable);
					}
					// then update the new position in the database
					// convert to different names because objId are numbers
					String AEpositionName;
					if (propValue.equals("1")) {
						AEpositionName = "one";
					} else if (propValue.equals("2")) {
						AEpositionName = "two";
					} else if (propValue.equals("3")) {
						AEpositionName = "three";
					} else if (propValue.equals("4")) {
						AEpositionName = "four";
					} else if (propValue.equals("5")) {
						AEpositionName = "five";
					} else if (propValue.equals("6")) {
						AEpositionName = "six";
					} else if (propValue.equals("7")) {
						AEpositionName = "seven";
					} else if (propValue.equals("8")) {
						AEpositionName = "eight";
					} else if (propValue.equals("9")) {
						AEpositionName = "nine";
					} else {
						AEpositionName = "other";
					}
					kb.Tell(Name.ParseName(AEpositionName + "(available)"),
							"False");

					kb.Tell(Name.ParseName(objId + "(position)"),
							AEpositionName);
					System.out
							.println("updated position availability based on change in position of : "
									+ objId
									+ " so position: "
									+ AEpositionName
									+ " is now not available, and: "
									+ objId
									+ " has position: " + AEpositionName);
				}
			} else if (propName.equals("is_on_top_of")) {
				if (propValue.equals("None")) {
					kb.Tell(Name.ParseName(objId + "(isStacked)"), "False");
					String lowerPot = (String) kb.AskProperty(Name
							.ParseName("stackedOn(" + objId + ")"));
					if (lowerPot == null) {
						System.out.println("lowerpot null)");
					} else {
						kb.Tell(Name.ParseName("stackedOn(" + objId + ")"),
								"False");
						kb.Tell(Name.ParseName(lowerPot + "(hasObject)"),
								"False");
						// the boolean flags it's an unstack action, upper pot,
						// lower pot
						setDontStackConstraints(false, objId, lowerPot);
					}
				} else {
					kb.Tell(Name.ParseName(objId + "(isStacked)"), "True");
					kb.Tell(Name.ParseName("stackedOn(" + objId + ")"),
							propValue);
					kb.Tell(Name.ParseName(propValue + "(hasObject)"), "True");
					// the boolean flags it's a stack action, upper pot,
					// lower pot
					setDontStackConstraints(true, objId, propValue);
				}
				System.out
						.println("!!!!!!!!!****is on top of- published for pot: "
								+ objId + " is " + propValue);
			} else if (propName.equals("has_on_top")) {
				// updated above
			} else if (propName.equals("pot_flower")) {
				if (!propValue.equals("None")) {
					kb.Tell(Name.ParseName(objId + "(hasFlower)"), "True");
					kb.Tell(Name.ParseName(objId + "(removeFlower)"), "True");
				} else {
					kb.Tell(Name.ParseName(objId + "(hasFlower)"), "False");
					kb.Tell(Name.ParseName(objId + "(removeFlower)"), "False");
				}
			} else if (propName.equals("flower_pot")) {
				System.out.println("flower in pot? " + objId + " in pot "
						+ propValue);
				if (!propValue.equals("None")) {
					kb.Tell(Name.ParseName(objId + "(isInPot)"), "True");
				} else {
					kb.Tell(Name.ParseName(objId + "(isInPot)"), "False");
				}
			} else if (propName.equals("flower_basket")) {
				if (!propValue.equals("None")) {
					kb.Tell(Name.ParseName(objId + "(isInBasket)"), "True");
				} else {
					kb.Tell(Name.ParseName(objId + "(isInBasket)"), "False");
				}
			} else if (propName.equals("under_cloud")) {
				kb.Tell(Name.ParseName(objId + "(under_cloud)"), propValue);
			} else if (propName.equals("ball_colour")) {
				kb.Tell(Name.ParseName(objId + "(ball_colour)"), propValue);
			} else if (propName.equals("ball_container")) {
				kb.Tell(Name.ParseName(objId + "(ball_container)"), propValue);
			} else if (propName.equals("container_colour")) {
				kb
						.Tell(Name.ParseName(objId + "(container_colour)"),
								propValue);
			}
		}

		public void objectRemoved(String objId) {
			// hack to deal with bubbles popping and so disappearing
			KnowledgeBase.GetInstance().Tell(Name.ParseName("popBubble()"),
					"False");
			KnowledgeBase.GetInstance().Tell(Name.ParseName("noticeEvent()"),
					"False");

			System.out.println("Object removed + " + objId);
			// Remove all KB properties that have this agent as an argument
			ComposedName query = new ComposedName(objId, "[x]");
			KnowledgeBase kb = KnowledgeBase.GetInstance();
			if (kb.GetPossibleBindings(query) != null) {
				for (Object obj : kb.GetPossibleBindings(query)) {
					SubstitutionSet bindings = (SubstitutionSet) obj;
					Name tmpName = (Name) query.clone();
					tmpName.MakeGround(bindings.GetSubstitutions());
					kb.Retract(tmpName);
					System.out.println("removing from KB: " + tmpName);
				}
			}
		}

		public void scenarioEnded(String name) {
			KnowledgeBase.GetInstance().Tell(new Symbol("scenario"), name);
		}

		public void scenarioStarted(String name) {
			KnowledgeBase.GetInstance().Retract(new Symbol("scenario"));
		}

		public void worldPropertyChanged(String propName, String propValue) {
			KnowledgeBase.GetInstance().Tell(new Symbol(propName), propValue);
		}

		public void userTouchedObject(String objId) {}
		public void userTouchedAgent(String agentId) {}
		public void userStarted(String name) {}
	}

	private class AgentListenerImpl implements IAgentListener
	{

		/**
		 * The given action has completed successfully
		 * 
		 * @see RemoteAgent#ActionFinishedPerception
		 */
		@SuppressWarnings({ "unchecked", "rawtypes" })
    public void agentActionCompleted(String agentId, String action,
				List<String> details) {

			synchronized (executingActions) {
				Logger.Log(
						"info",
						"Agent " + agentId + " completed action " + action
								+ " with details " + details);
				if (agentId.equals("User")) {
					Logger.Log("info",
							"User performed action");

					// so that agent knows which object is being offered
					if (action.equals("drag")) {
						String objId = details.get(0);
						KnowledgeBase.GetInstance().Tell(
								Name.ParseName("objectOffered()"), objId);
					}
					return;
				} else if (agentId.equals("None")) {
					Logger.Log("info",
							"Ignoring 'None' action for now");
					return;
				} else {
					Logger.Log("info",
							"Agent is " + agentId);
				}

				Name actionName = Name.ParseName(action
						+ details.toString().replace('[', '(')
								.replace(']', ')').replaceAll(" +", ""));

				String agentName = agentId;
				if (agentId.equals(EchoesRemoteAgent.this.agentId)) {
					agentName = _agent.name();
				}

				if (action.equals("Say")
						|| action.equals("FacialExpression")
						|| action.equals("AttachCloud")
						|| action.equals("DetachCloud")
						|| (actionStarted.equals("SelfRequestObject") && action
								.equals("LookAtChild"))

						|| (actionStarted.equals("SelfPointBid") && action
								.equals("LookAtChild"))
						|| (actionStarted.equals("SelfVerbalBid") && action
								.equals("LookAtChild"))
						|| (actionStarted.equals("SelfLookBid") && action
								.equals("LookAtChild"))
						|| (actionStarted.equals("SelfWait") && action
								.equals("LookAtChild"))
						|| (actionStarted.equals("SelfGreetChild") && action
								.equals("LookAtChild"))
						|| (actionStarted.equals("SelfGiveThumbsUp") && action
								.equals("LookAtChild"))
						|| (actionStarted.equals("SelfEndActivity") && action
								.equals("LookAtChild"))
						|| (actionStarted.equals("SelfLookBid") && action
								.equals("LookAtChild"))
						|| (actionStarted.equals("SelfWalkInNoticeChild") && action
								.equals("Gesture"))
						|| (actionStarted.equals("SelfWalkInNoticeChild") && action
								.equals("LookAtChild"))
						|| (actionStarted.equals("SelfLookAroundSpotObject") && action
								.equals("LookAtObject"))
						|| (actionStarted.equals("SelfLookAroundMakeComment") && action
								.equals("LookAtChild"))
						|| (actionStarted.equals("SelfReactToAndShareEvent") && action
								.equals("LookAtObject"))
						|| (actionStarted.equals("SelfReactToAndShareEvent") && action
								.equals("PointAt"))

				) {
				} else {
					// Add the necessary KB consequences depending on the action
					// First look up the action itself
					for (Object obj : _agent.getDeliberativeLayer()
							.getEmotionalPlanner().GetOperators()) {
						Step step = (Step) obj;
            ArrayList bindings = new ArrayList();
						// don't need this because hardwired at the moment that
						// [AGENT]
						// is [SELF]
						// bindings.add(new Substitution(new Symbol("[SELF]"),
						// new
						// Symbol(
						// _agent.name())));
						// bindings.add(new Substitution(new Symbol("[AGENT]"),
						// new Symbol(agentName)));
						// a bit of hacking to get the effects of actions set in
						// the
						// KB,
						// because the rendering engine action has a different
						// name
						// with
						// different arguments to the FAtiMA actions
						if (!actionNameConv.getOriginalAEargs().isEmpty()) {
							if (actionNameConv.getOriginalAEargs().size() == 1) {
								bindings.add(new Substitution(new Symbol(
										"[location]"), new Symbol(
										actionNameConv.getOriginalAEargs().get(
												0))));
								bindings.add(new Substitution(new Symbol(
										"[object]"), new Symbol(actionNameConv
										.getOriginalAEargs().get(0))));
								bindings.add(new Substitution(new Symbol(
										"[bidMethod]"), new Symbol(
										actionNameConv.getOriginalAEargs().get(
												0))));
								bindings.add(new Substitution(new Symbol(
										"[activity]"), new Symbol(
										actionNameConv.getOriginalAEargs().get(
												0))));
								bindings.add(new Substitution(new Symbol(
										"[pot]"), new Symbol(actionNameConv
										.getOriginalAEargs().get(0))));
							} else if (actionNameConv.getOriginalAEargs()
									.size() == 2) {
								bindings.add(new Substitution(new Symbol(
										"[object]"), new Symbol(actionNameConv
										.getOriginalAEargs().get(0))));
								bindings.add(new Substitution(new Symbol(
										"[activity]"), new Symbol(
										actionNameConv.getOriginalAEargs().get(
												0))));
								bindings.add(new Substitution(new Symbol(
										"[flower]"), new Symbol(actionNameConv
										.getOriginalAEargs().get(0))));
								bindings.add(new Substitution(new Symbol(
										"[location]"), new Symbol(
										actionNameConv.getOriginalAEargs().get(
												0))));
								bindings.add(new Substitution(new Symbol(
										"[purpose]"), new Symbol(actionNameConv
										.getOriginalAEargs().get(1))));
								bindings.add(new Substitution(new Symbol(
										"[pot]"), new Symbol(actionNameConv
										.getOriginalAEargs().get(1))));
								bindings.add(new Substitution(new Symbol(
										"[basket]"), new Symbol(actionNameConv
										.getOriginalAEargs().get(1))));
								bindings.add(new Substitution(new Symbol(
										"[stackLocation]"), new Symbol(
										actionNameConv.getOriginalAEargs().get(
												1))));
								bindings.add(new Substitution(new Symbol(
										"[event]"), new Symbol(actionNameConv
										.getOriginalAEargs().get(1))));
							} else if (actionNameConv.getOriginalAEargs()
									.size() == 3) {

							} else if (actionNameConv.getOriginalAEargs()
									.size() == 4) {
								bindings.add(new Substitution(new Symbol(
										"[object]"), new Symbol(actionNameConv
										.getOriginalAEargs().get(0))));
								bindings.add(new Substitution(new Symbol(
										"[activity]"), new Symbol(
										actionNameConv.getOriginalAEargs().get(
												1))));
								bindings.add(new Substitution(new Symbol(
										"[purpose]"), new Symbol(actionNameConv
										.getOriginalAEargs().get(2))));
								bindings.add(new Substitution(new Symbol(
										"[bidRepeat]"), new Symbol(
										actionNameConv.getOriginalAEargs().get(
												3))));
							}
						}
						String arguments = "";
						Iterator it = actionNameConv.getOriginalAEargs().iterator();
						if (it.hasNext()) {
							arguments = (String) it.next();
						}
						while (it.hasNext()) {
							arguments = arguments + "," + it.next();
						}

						// need AE action name
						if (Unifier.Unify(step.getName(), Name
								.ParseName(actionNameConv.getOriginalAEname()
										+ "(" + arguments + ")"), bindings)) {

							Step matchingStep = (Step) step.clone();
							matchingStep.MakeGround(bindings);
							Logger.Log(
									"info",
									"Corresponding step: " + matchingStep);
							for (Object obj2 : matchingStep.getEffects()) {
								Effect effect = (Effect) obj2;
								String name = effect.GetEffect().getName()
										.toString();
								if (!name.startsWith("EVENT")
										&& !name.startsWith("SpeechContext")) {
									Logger.Log("info","Adding effect: " + effect);
									KnowledgeBase.GetInstance().Tell(
											effect.GetEffect().getName(),
											effect.GetEffect().GetValue()
													.toString());
								}
							}
							break;
						}
					}

					String target = null;
					Iterator it = actionNameConv.getOriginalAEargs().iterator();
					if (it.hasNext()) {
						target = it.next().toString();
					}
					// setting the perceived EVENT in the original name and args
					// (i.e.
					// the AE name not the RE one)
					System.out
							.println("Setting perceived event with origingal name: "
									+ actionNameConv.getOriginalAEname()
									+ " and args: " + target);

					Event e = new Event(agentName, actionNameConv
							.getOriginalAEname(), target);
					while (it.hasNext()) {
						e.AddParameter(new Parameter("param", it.next()
								.toString()));
					}
					_agent.PerceiveEvent(e);
				}
				System.out.println("agent id is: " + agentId
						+ ", and remote echoes agent is: "
						+ EchoesRemoteAgent.this.agentId);

				if (agentId.equals(EchoesRemoteAgent.this.agentId)) {
					ActionSpec spec = new ActionSpec(action, details);
					actionsAndDetails = actionsAndDetails + "\n" + action + " "
							+ details;
					if (actionNameConv.getOriginalAEname()
							.equals("SelfWalkOut")) {
						System.out.println(actionsAndDetails);
					}
					System.out.println("action completed: " + action
							+ " details: " + details);
					if (executingActions.remove(spec)) {
						Logger.Log("info",
								"Removed action spec");
					} else {
						Logger.LogWarning(
								"No corresponding action spec found!");
					}
					if (executingActions.isEmpty()) {
						KnowledgeBase.GetInstance().Tell(
								Name.ParseName("actionFailed()"), "True");
						_canAct = true;
					}
				}
				System.out.println("executing actions: " + executingActions);
				System.out.println("can act? " + _canAct);

				checkPotAvailability();

			}
		}

    public void agentActionFailed(String agentId, String action,
				List<String> details, String reason) {
			KnowledgeBase.GetInstance().Tell(Name.ParseName("actionFailed()"),
					"True");
			Logger.LogWarning(
					"Action failed: " + action + " " + details + " " + reason);
			// if action was to notice object and it's no longer in world so it
			// fails then reset notice event goal
			if (action.equals("LookAtObject")) {
				KnowledgeBase.GetInstance().Tell(
						Name.ParseName("noticeEvent()"), "False");
			}
			// reset accept object goal
			KnowledgeBase.GetInstance().Tell(
					Name.ParseName("childOfferedObject()"), "False");
			// reset pick up action flags
			KnowledgeBase.GetInstance().Tell(
					Name.ParseName("pickedUpPotToPutDown()"), "False");
			KnowledgeBase.GetInstance().Tell(
					Name.ParseName("pickedUpFlowerToPutInPot()"), "False");
			KnowledgeBase.GetInstance().Tell(
					Name.ParseName("pickedUpFlower()"), "False");
			KnowledgeBase.GetInstance().Tell(
					Name.ParseName("pickedUpBasket()"), "False");
			KnowledgeBase.GetInstance().Tell(Name.ParseName("pickedUpPot()"),
					"False");
			KnowledgeBase.GetInstance().Tell(Name.ParseName("acceptBall()"),
					"False");

			synchronized (executingActions) {
				if (agentId.equals(EchoesRemoteAgent.this.agentId)) {
					ActionSpec spec = new ActionSpec(action, details);
					if (executingActions.remove(spec)) {
						Logger.Log("info",
								"Removed action spec");
					} else {
						Logger.LogWarning("No corresponding action spec found!");
					}
					if (executingActions.isEmpty()) {
						_canAct = true;
					}
				}
			}
		}

		public void agentActionStarted(String agentId, String action,List<String> details) 
		{
			if (actionNameConv.getOriginalAEname().equals(
					"SelfPickUpBallToSort")) {
				System.out
						.println("action started put ball in pile for ball id: "
								+ actionNameConv.getOriginalAEargs().get(0));
				KnowledgeBase.GetInstance().Tell(
						Name.ParseName(details.get(0) + "(ball_container)"),
						"True");
			}
		}
	}

	private class EventListenerImpl implements IEventListener
	{
		private Timer timer = new Timer();

		/*
		 * (non-Javadoc)
		 * 
		 * @see echoes._EventListenerOperations#userGazeEvent(java.lang.String,
		 * long, Ice.Current)
		 */
		public void userGazeEvent(String details, long msec) {
			if ("AGENT".equals(details)) {
				agentPublisher.agentActionCompleted("User", "LookAtFace",
						Collections.singletonList(_agent.name()));
			} else {
				agentPublisher.agentActionCompleted("User", "LookAtObject",
						Collections.singletonList(details));
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see echoes._EventListenerOperations#userTouchEvent(java.lang.String,
		 * Ice.Current)
		 */
		public void userTouchEvent(final String objId) {
			// New mode -- touching immediately implies offering
			// agentPublisher.agentActionCompleted("User", "Offer", Collections
			// .singletonList(objId));

			/*
			 * // For now, we assume they're all PickFromGarden actions
			 * agentPublisher.agentActionCompleted("User", "PickFromGarden",
			 * Collections.singletonList(objId));
			 * 
			 * // And, picking implies offering (for now) TimerTask pickTask =
			 * new TimerTask() { public void run() { List<String> args = new
			 * LinkedList<String>(); args.add(_agent.name()); args.add(objId);
			 * agentPublisher.agentActionCompleted("User", "Offer", args); } };
			 * timer.schedule(pickTask, 1000);
			 */
		}

		@SuppressWarnings("rawtypes")
    @Override
		public void userAction(UserActionType action) {
			String target = "[]";
			// Iterator it = actionNameConv.getOriginalAEargs().iterator();
			// if (it.hasNext()) {
			// target = it.next().toString();
			// }

			if (action.toString().equals("UserRespondedToBid")
					|| action.toString().equals("UserActivityRelevantAction")) {

				if (KnowledgeBase.GetInstance().AskProperty(
						Name.ParseName("FlowerPickToBasket(isChosenActivity)")) != null
						&& KnowledgeBase
								.GetInstance()
								.AskProperty(
										Name
												.ParseName("FlowerPickToBasket(isChosenActivity)"))
								.equals("True")) {
					String basketId = "";
					KnowledgeBase kb = KnowledgeBase.GetInstance();
					if (kb.GetPossibleBindings(Name.ParseName("[x](type)")) != null) {
						ArrayList baskets = kb.GetPossibleBindings(Name
								.ParseName("[x](type)"));
						for (int i = 0; i < baskets.size(); i++) {
							SubstitutionSet subSet = (SubstitutionSet) baskets
									.get(i);
							ArrayList subs = subSet.GetSubstitutions();
							for (int j = 0; j < subs.size(); j++) {
								Substitution sub = (Substitution) subs.get(j);
								if (kb.AskProperty(Name.ParseName(sub
										.getValue().getName()
										+ "(type)")) != null
										&& kb.AskProperty(
												Name.ParseName(sub.getValue()
														.getName()
														+ "(type)")).equals(
												"Basket")) {
									basketId = sub.getValue().getName();
									System.out.println("basket id is: " + basketId);
								}
							}
						}
					}
					rePrx.setWorldProperty("IncrementScore", "");
					rePrx.setObjectProperty(basketId, "PlayFanfare", "");
					giveFlowerFeedback++;
					if (giveFlowerFeedback > 3) {
						giveFlowerFeedback = 0;
						KnowledgeBase.GetInstance().Tell(
								Name.ParseName("giveFeedback()"), "True");
					}
				} else {
					KnowledgeBase.GetInstance().Tell(
							Name.ParseName("giveFeedback()"), "True");
				}
				// } else if
				// (action.toString().equals("UserGaveRequestedObject")) {
				// KnowledgeBase.GetInstance().Tell(
				// Name.ParseName("childOfferedObject()"), "True");
				// } else if
				// (action.toString().equals("UserGaveUnrequestedObject")) {
				// KnowledgeBase.GetInstance().Tell(
				// Name.ParseName("childOfferedObject()"), "True");

			} else if (action.toString().equals("UserTouchedAgent")) {
				KnowledgeBase.GetInstance().Tell(
						Name.ParseName("childTouchedAgent()"), "True");
			}

			Event e = new Event("User", action.toString(), target);
			System.out.println("publishing event " + e.toString());
			_agent.PerceiveEvent(e);

		}
	}

	@SuppressWarnings("rawtypes")
  public void checkPotAvailability() {
		// checking whether there are pots that are not in a stack and don't
		// have a flower
		ArrayList<String> potsCanGrowFlower = new ArrayList<String>();

		KnowledgeBase kb = KnowledgeBase.GetInstance();
		if (kb.GetPossibleBindings(Name.ParseName("[x](type)")) != null) {
			ArrayList pots = kb
					.GetPossibleBindings(Name.ParseName("[x](type)"));
			for (int i = 0; i < pots.size(); i++) {
				SubstitutionSet subSet = (SubstitutionSet) pots.get(i);
				ArrayList subs = subSet.GetSubstitutions();
				for (int j = 0; j < subs.size(); j++) {
					Substitution sub = (Substitution) subs.get(j);
					if (kb.AskProperty(Name.ParseName(sub.getValue().getName()
							+ "(type)")) != null
							&& kb.AskProperty(
									Name.ParseName(sub.getValue().getName()
											+ "(type)")).equals("Pot")) {
						if ((kb.AskProperty(Name.ParseName(sub.getValue()
								.getName()
								+ "(hasObject)")) == null || !kb.AskProperty(
								Name.ParseName(sub.getValue().getName()
										+ "(hasObject)")).equals("True"))
								&& (kb.AskProperty(Name.ParseName(sub
										.getValue().getName()
										+ "(isStacked)")) == null || !kb
										.AskProperty(
												Name.ParseName(sub.getValue()
														.getName()
														+ "(isStacked)"))
										.equals("True"))
								&& (kb.AskProperty(Name.ParseName(sub
										.getValue().getName()
										+ "(hasFlower)")) == null || !kb
										.AskProperty(
												Name.ParseName(sub.getValue()
														.getName()
														+ "(hasFlower)"))
										.equals("True"))) {
							kb.Tell(Name.ParseName(sub.getValue().getName()
									+ "(unstackedNoFlower)"), "True");

							if (kb.AskProperty(Name
									.ParseName("needToPutPotDown()")) != null
									&& kb
											.AskProperty(
													Name
															.ParseName("needToPutPotDown()"))
											.equals("True")) {
								kb.Tell(Name.ParseName(sub.getValue().getName()
										+ "(toBeUnstacked)"), "True");
								/*
								 * System.out .println("is held? " + kb
								 * .AskProperty(Name
								 * .ParseName("needToPutPotDown()")) +
								 * " and to be unstacked? " + kb
								 * .AskProperty(Name .ParseName(sub .getValue()
								 * .getName() + "(toBeUnstacked)")));
								 */
							} else {
								kb.Tell(Name.ParseName(sub.getValue().getName()
										+ "(toBeUnstacked)"), "False");
							}

							// System.out.println("Pot: "
							// + sub.getValue().getName()
							// + " can be used to grow a flower");
							potsCanGrowFlower.add(sub.getValue().getName());
						} else {
							kb.Tell(Name.ParseName(sub.getValue().getName()
									+ "(unstackedNoFlower)"), "False");
							potsCanGrowFlower.remove(sub.getValue().getName());
							// System.out.println("Pot: "
							// + sub.getValue().getName()
							// + " can't be used to grow a flower");
							if (kb.AskProperty(Name
									.ParseName("needToPutPotDown()")) != null
									&& kb
											.AskProperty(
													Name
															.ParseName("needToPutPotDown()"))
											.equals("True")) {
								kb.Tell(Name.ParseName(sub.getValue().getName()
										+ "(toBeUnstacked)"), "True");
								/*
								 * System.out .println("is held? " + kb
								 * .AskProperty(Name
								 * .ParseName("needToPutPotDown()")) +
								 * " and to be unstacked? " + kb
								 * .AskProperty(Name .ParseName(sub .getValue()
								 * .getName() + "(toBeUnstacked)")));
								 */
							} else {
								kb.Tell(Name.ParseName(sub.getValue().getName()
										+ "(toBeUnstacked)"), "False");
							}
							if ((kb.AskProperty(Name.ParseName(sub.getValue()
									.getName()
									+ "(hasObject)")) == null || !kb
									.AskProperty(
											Name.ParseName(sub.getValue()
													.getName()
													+ "(hasObject)")).equals(
											"True"))
									&& (kb.AskProperty(Name.ParseName(sub
											.getValue().getName()
											+ "(isStacked)")) != null && kb
											.AskProperty(
													Name.ParseName(sub
															.getValue()
															.getName()
															+ "(isStacked)"))
											.equals("True"))) {

								kb.Tell(Name.ParseName(sub.getValue().getName()
										+ "(toBeUnstacked)"), "True");
								// System.out.println("Pot: "
								// + sub.getValue().getName()
								// + " marked to be unstacked ");
							}

						}
					}
				}
			}
		}

		// set KB: whether there are pots that are unstacked and don't have
		// a flower
		// System.out.println("unstackedFlowerLessPots: " + potsCanGrowFlower);
		if (!potsCanGrowFlower.isEmpty()) {
			kb.Tell(Name.ParseName("unstackedFlowerlessPots()"), "True");
		} else {
			kb.Tell(Name.ParseName("unstackedFlowerlessPots()"), "False");
			// to reactivate goal to unstack a pot
			if (kb.AskProperty(Name.ParseName("FlowerGrow(isChosenActivity)")) != null
					&& kb.AskProperty(
							Name.ParseName("FlowerGrow(isChosenActivity)"))
							.equals("True")) {
				kb.Tell(Name.ParseName("madeAPotAvailable()"), "False");
			}

		}
	}

	/**
	 * Is called when a pot gets stacked/unstacked based on updates from the
	 * pot's objectPropertyChanged.
	 * 
	 * Sets the constraints dontStack(pot1, pot2) for the agent to be able to
	 * correctly execute stacking (i.e. not choose to stack pots that are in the
	 * same stack).
	 * 
	 * @param stack
	 * @param upperPot
	 * @param lowerPot
	 */
	@SuppressWarnings("rawtypes")
  public void setDontStackConstraints(boolean stack, String upperPot,
			String lowerPot) {
		System.out.println("stack action : " + stack + " upperPot " + upperPot
				+ " lower pot: " + lowerPot);
		if (stack) {
			KnowledgeBase.GetInstance().Tell(
					Name.ParseName("dontStack(" + lowerPot + "," + upperPot
							+ ")"), "True");
			System.out.println("dontStack(" + lowerPot + "," + upperPot + ")");
			ArrayList stackedOnObjects = KnowledgeBase.GetInstance()
					.GetPossibleBindings(
							Name.ParseName("dontStack([x]," + lowerPot + ")"));
			System.out.println("checking for " + "dontStack([x]," + lowerPot
					+ ")");
			// don't stack any pot beneath lowerPot on UpperPot
			if (stackedOnObjects != null) {
				for (int i = 0; i < stackedOnObjects.size(); i++) {
					SubstitutionSet subSet = (SubstitutionSet) stackedOnObjects
							.get(i);
					ArrayList subs = subSet.GetSubstitutions();
					for (int j = 0; j < subs.size(); j++) {
						Substitution sub = (Substitution) subs.get(j);
						if (KnowledgeBase.GetInstance().AskProperty(
								Name.ParseName("dontStack("
										+ sub.getValue().getName() + ","
										+ lowerPot + ")")) != null
								&& KnowledgeBase.GetInstance().AskProperty(
										Name.ParseName("dontStack("
												+ sub.getValue().getName()
												+ "," + lowerPot + ")"))
										.equals("True")) {

							KnowledgeBase.GetInstance().Tell(
									Name.ParseName("dontStack("
											+ sub.getValue().getName() + ","
											+ upperPot + ")"), "True");

							System.out.println("also adding constraint: "
									+ "dontStack(" + sub.getValue().getName()
									+ "," + upperPot + ")" + "to True");

							ArrayList stackedUnderObjects = KnowledgeBase
									.GetInstance().GetPossibleBindings(
											Name.ParseName("dontStack("
													+ upperPot + ",[x])"));

							System.out.println("checking for " + "dontStack("
									+ upperPot + ",[x])");

							// don't stack any pot beneath LowerPot on any pot
							// above
							// UpperPot
							if (stackedUnderObjects != null) {
								for (int k = 0; k < stackedUnderObjects.size(); k++) {
									SubstitutionSet subSet2 = (SubstitutionSet) stackedUnderObjects
											.get(k);
									ArrayList subs2 = subSet2
											.GetSubstitutions();
									for (int l = 0; l < subs2.size(); l++) {
										Substitution sub2 = (Substitution) subs2
												.get(l);
										KnowledgeBase.GetInstance().Tell(
												Name.ParseName("dontStack("
														+ sub.getValue()
																.getName()
														+ ","
														+ sub2.getValue()
																.getName()
														+ ")"), "True");

										System.out
												.println("also adding constraint: "
														+ "dontStack("
														+ sub.getValue()
																.getName()
														+ ","
														+ sub2.getValue()
																.getName()
														+ ")" + "to True");

									}
								}
							}
						}
					}
				}
			}
			ArrayList stackedUnderObjects = KnowledgeBase.GetInstance()
					.GetPossibleBindings(
							Name.ParseName("dontStack(" + upperPot + ",[x])"));

			System.out.println("checking for " + "dontStack(" + upperPot
					+ ",[x])");

			// don't stack LowerPot on any pot above UpperPot
			if (stackedUnderObjects != null) {
				for (int i = 0; i < stackedUnderObjects.size(); i++) {
					SubstitutionSet subSet = (SubstitutionSet) stackedUnderObjects
							.get(i);
					ArrayList subs = subSet.GetSubstitutions();
					for (int j = 0; j < subs.size(); j++) {
						Substitution sub = (Substitution) subs.get(j);
						if (KnowledgeBase.GetInstance().AskProperty(
								Name.ParseName("dontStack(" + upperPot + ","
										+ sub.getValue().getName() + ")")) != null
								&& KnowledgeBase.GetInstance().AskProperty(
										Name.ParseName("dontStack(" + upperPot
												+ ","
												+ sub.getValue().getName()
												+ ")")).equals("True")) {

							KnowledgeBase.GetInstance().Tell(
									Name.ParseName("dontStack(" + lowerPot
											+ "," + sub.getValue().getName()
											+ ")"), "True");

							System.out.println("also adding constraint: "
									+ "dontStack(" + lowerPot + ","
									+ sub.getValue().getName() + ")"
									+ "to True");
						}
					}
				}
			}

			System.out.println("for upper pot: "
					+ upperPot
					+ " properties (isStacked), (hasObject): "
					+ KnowledgeBase.GetInstance().AskProperty(
							Name.ParseName(upperPot + "(isStacked)"))
					+ " and "
					+ KnowledgeBase.GetInstance().AskProperty(
							Name.ParseName(upperPot + "(hasObject)")));
			System.out.println("for lower pot: "
					+ lowerPot
					+ " properties (isStacked), (hasObject): "
					+ KnowledgeBase.GetInstance().AskProperty(
							Name.ParseName(lowerPot + "(isStacked)"))
					+ " and "
					+ KnowledgeBase.GetInstance().AskProperty(
							Name.ParseName(lowerPot + "(hasObject)")));

		} else {
			String unstackUpperPot = upperPot;
			String unstacklowerPot = lowerPot;

			ArrayList objectsStackedOnLower = KnowledgeBase.GetInstance()
					.GetPossibleBindings(
							Name.ParseName("dontStack(" + unstacklowerPot
									+ ",[x])"));

			System.out.println("checking for " + "dontStack(" + unstacklowerPot
					+ ",[x])");

			ArrayList objectsUpperStackedOn = KnowledgeBase.GetInstance()
					.GetPossibleBindings(
							Name.ParseName("dontStack([x]," + unstackUpperPot
									+ ")"));

			System.out.println("checking for " + "dontStack([x],"
					+ unstackUpperPot + ")");

			// undo constraints of not stacking LowerPot on any of the pots on
			// top of UpperPot
			if (objectsStackedOnLower != null) {
				for (int i = 0; i < objectsStackedOnLower.size(); i++) {
					SubstitutionSet subSet = (SubstitutionSet) objectsStackedOnLower
							.get(i);
					ArrayList subs = subSet.GetSubstitutions();
					for (int j = 0; j < subs.size(); j++) {
						Substitution sub = (Substitution) subs.get(j);
						KnowledgeBase.GetInstance()
								.Tell(
										Name.ParseName("dontStack("
												+ unstacklowerPot + ","
												+ sub.getValue().getName()
												+ ")"), "False");

						System.out.println("also removing constraint: "
								+ "dontStack(" + unstacklowerPot + ","
								+ sub.getValue().getName() + ")" + "to False");

						// undo constraints of not stacking any of pots beneath
						// LowerPot on any of pots above UpperPot
						if (objectsUpperStackedOn != null) {
							for (int k = 0; k < objectsUpperStackedOn.size(); k++) {
								SubstitutionSet subSet2 = (SubstitutionSet) objectsUpperStackedOn
										.get(k);
								ArrayList subs2 = subSet2.GetSubstitutions();
								for (int l = 0; l < subs.size(); l++) {
									Substitution sub2 = (Substitution) subs2
											.get(l);
									KnowledgeBase.GetInstance().Tell(
											Name.ParseName("dontStack("
													+ sub2.getValue().getName()
													+ ","
													+ sub.getValue().getName()
													+ ")"), "False");

									System.out
											.println("also removing constraint: "
													+ "dontStack("
													+ sub2.getValue().getName()
													+ ","
													+ sub.getValue().getName()
													+ ")" + "to False");

								}
							}
						}
					}
				}
			}
			// undo constraints of not stacking any pots below LowerPot on
			// UpperPot
			if (objectsUpperStackedOn != null) {
				for (int i = 0; i < objectsUpperStackedOn.size(); i++) {
					SubstitutionSet subSet = (SubstitutionSet) objectsUpperStackedOn
							.get(i);
					ArrayList subs = subSet.GetSubstitutions();
					for (int j = 0; j < subs.size(); j++) {
						Substitution sub = (Substitution) subs.get(j);
						KnowledgeBase.GetInstance().Tell(
								Name.ParseName("dontStack("
										+ sub.getValue().getName() + ","
										+ unstackUpperPot + ")"), "False");

						System.out.println("also removing constraint: "
								+ "dontStack(" + sub.getValue().getName() + ","
								+ unstackUpperPot + ")" + "to False");

						if (objectsStackedOnLower != null) {
							for (int k = 0; k < objectsStackedOnLower.size(); k++) {
								SubstitutionSet subSet2 = (SubstitutionSet) objectsStackedOnLower
										.get(k);
								ArrayList subs2 = subSet2.GetSubstitutions();
								for (int l = 0; l < subs2.size(); l++) {
									Substitution sub2 = (Substitution) subs2
											.get(j);
									KnowledgeBase.GetInstance().Tell(
											Name.ParseName("dontStack("
													+ sub.getValue().getName()
													+ ","
													+ sub2.getValue().getName()
													+ ")"), "False");

									System.out
											.println("also removing constraint: "
													+ "dontStack("
													+ sub.getValue().getName()
													+ ","
													+ sub2.getValue().getName()
													+ ")" + "to False");

								}
							}
						}
					}
				}
			}
		}
	}

}