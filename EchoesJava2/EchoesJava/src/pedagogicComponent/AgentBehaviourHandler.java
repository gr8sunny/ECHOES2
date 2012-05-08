package pedagogicComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observer;

import echoesEngine.ListenerManager;

import utils.Logger;
import utils.Interfaces.*;
import utils.Enums.*;
import pedagogicComponent.InterventionOptions.Intervention;
import pedagogicComponent.data.*;


public class AgentBehaviourHandler extends PCcomponentHandler 
{
  private AgentListenerImpl alImpl;
	private RenderingListenerImpl rlImpl;
	private boolean agentIsInEchoes;
	private boolean agentNotInvolved = false;;
	private HashMap<String, String> interactionHistory = new HashMap<String, String>();
	private EchoesActivity currentActivity;
	// used because need AE action names and args to process action completed
	private String lastAgentActionStartedAEname = "";
	private ArrayList<String> lastAgentActionStartedAEargs = new ArrayList<String>();
	private String lastAgentActionStarted = "";
	// whether the last agent action was a bid - some actions are ignored when
	// updating this, e.g. if the agent reacts to a object animation
	private boolean lastAgentActionWasBid;
	// verbal, point, or look
	private String lastBidType;
	// bid for child to do action, bid for request object
	private String lastBidPurpose;
	// target object of bid (relevant if point or look bid)
	private String lastObject;
	// the lastObject object type
	private EchoesObjectType lastObjectType;
	// used to keep track of state of world in order to assess end of activity
	private HashMap<String, String> pots = new HashMap<String, String>();
	private HashMap<String, String> potFlowerProperty = new HashMap<String, String>();
	private HashMap<String, String> potIsStackedProperty = new HashMap<String, String>();
	private HashMap<String, String> potHasObjectProperty = new HashMap<String, String>();
	private ArrayList<String> flowers = new ArrayList<String>();
	private HashMap<String, String> flowersInBasket = new HashMap<String, String>();
	private HashMap<String, String> ballHitCloud = new HashMap<String, String>();
	private HashMap<String, String> ballInContainer = new HashMap<String, String>();
	private HashMap<String, String> ballColours = new HashMap<String, String>();
	private boolean allGreenGone = false;
	private boolean allYellowGone = false;
	private boolean allBlueGone = false;
	private HashMap<String, String> containerColours = new HashMap<String, String>();
	// to check if flower picking is done (at moment it's when no pots have
	// flowers, but perhaps need to also check that flowers are in basket and
	// haven't just been turned to balls/bubbles)
	@SuppressWarnings("unused")
  private String numFlowersInBasket;

	// number of times the agent has made a bid (i.e. child has not responded to
	// it)
	@SuppressWarnings("unused")
  private int timesMadeBidNoResponse;

	// used to prevent processing of consecutive user action: cloud_rain
	private String previousUserAction = "";
	private String currentUserAction = "";

	// flag used to only end activity once (as it's ended based on reasoning
	// after an object animation, so potentially triggered more than once)
	// important to reset when a new activity is started in method
	// directAgentEndActivity
	private boolean activityEnded = false;

	// strings for the goals to avoid typos
	private static String wait = "wait";
	private static String dontWait = "dontWait";
	private static String makeBid = "makeBid";

	// used to keep track of child holding object for 2 seconds in order to
	// offer
	boolean userStillHoldingObject = false;
	String objectOfferedId = "";
	private boolean bidNext = false;
	private boolean sameBidType = false;

	// flags to set background and agent enter goals for new activity when the
	// agent has walked out
	private boolean doNextActivity = false;
	private EchoesActivity nextActivity;
	private EchoesObjectType nextObject;

	private boolean contingent = false;

	private EchoesScene sceneToStartAfterWalkingOut = null;
	
	/**
	 * @param dmPrx
	 */
	public AgentBehaviourHandler(PCcomponents pCc, IDramaManager dmPrx, IActionEngine aePrx) 
	{
		super(pCc, dmPrx, aePrx);
		lastAgentActionWasBid = false;
		lastBidType = "";
		lastBidPurpose = "";
		lastObject = "";
		agentIsInEchoes = false;
		numFlowersInBasket = "";
		timesMadeBidNoResponse = 0;

		ListenerManager listenerMgr = ListenerManager.GetInstance();
		
		rlImpl = new RenderingListenerImpl();
		listenerMgr.Subscribe(rlImpl);

		// Now create the listener and subscribe it
		alImpl = new AgentListenerImpl();
		listenerMgr.Subscribe(alImpl);
	}

	public IRenderingListener getRenderingListener() {
		return rlImpl;
	}

	/**
	 * You have to call this method when shutting down ...
	 */
	public void shutdown() 
	{
	  ListenerManager listenerMgr = ListenerManager.GetInstance();
	  listenerMgr.Unsubscribe(rlImpl);
    listenerMgr.Unsubscribe(alImpl);
	}

	/**
	 * A class to listen for messages from the rendering engine indicating
	 * actions performed by the agent and the child.
	 * 
	 * Possibly need to convert rendering engine names to DM and PC names, e.g.
	 * for object animations
	 * 
	 * @author Mary Ellen Foster
	 */
	private class AgentListenerImpl implements IAgentListener 
	{
		public void agentActionCompleted(String agentId, String action,List<String> details) 
		{
			// AGENT actions (at the moment defined as non-user which works for
			// current set-up
			if (!agentId.equals("User")) {

				System.out
						.println("******************* agent action: " + action
								+ " completed in PC, with details: " + details);

				System.out
						.println("will use the AE name to process, which is: "
								+ lastAgentActionStartedAEname
								+ " with arguments: "
								+ lastAgentActionStartedAEargs);

				// here we need to use the RE name to avoid processing
				// FacialExpressions, Speech, and Wait events
				// we also don't process reactions to events
				if (action.equals("FacialExpression")
						|| action.equals("Say")
						|| lastAgentActionStartedAEname.equals("SelfWait")
						|| lastAgentActionStarted.equals("SelfPopBubble")
						|| lastAgentActionStarted.equals("SelfReactToEvent")
						|| lastAgentActionStarted
								.equals("SelfReactToAndDontShareEvent")
						|| lastAgentActionStarted
								.equals("SelfReactToAndShareEvent")
						|| lastAgentActionStarted
								.equals("SelfReactToEventGeneral()")
						|| lastAgentActionStarted.equals("SelfNoticeEvent")) {
					// don't change lastAgentActionWasBid back to false
					System.out
							.println("don't change lastAgentActionWasBid whose value is: "
									+ lastAgentActionWasBid);
					// reset this. It's used to prevent processing consecutive
					// cloud rain actions
					previousUserAction = "";
					if (lastAgentActionStartedAEname.equals("SelfWait")
							|| lastAgentActionStarted.equals("SelfPopBubble")
							|| lastAgentActionStarted
									.equals("SelfReactToEvent")
							|| lastAgentActionStarted
									.equals("SelfReactToEventGeneral")
							|| lastAgentActionStarted
									.equals("SelfReactToAndDontShareEvent")
							|| lastAgentActionStarted
									.equals("SelfReactToAndShareEvent")
							|| lastAgentActionStarted.equals("SelfNoticeEvent")) {
						// check if after this action the activity has ended
						if (checkEndActivity()) {
							return;
						}
					}
				} else {
					// here we need to update that the last action was not a bid
					lastAgentActionWasBid = false;
				}
				// if the action was to end the activity then start a new
				// one
				if (lastAgentActionStarted.equals("SelfEndActivity")) {
					System.out
							.println("ended activity and now starting new one");
					directAgentActivityEnded();
					// temporarily
					currentActivity = null;
					aePrx.setChosenActivity("");
					return;
				}

				// check if after this action the activity has ended
				if (checkEndActivity()) {
					return;
				}

				System.out.println("agent acted: " + action + ", " + details);
				// if the action was to make the cloud rain and in the
				// cloudRain activity then switch to the flowerGrowing
				// activity
				if ((currentActivity == EchoesActivity.FlowerPickToBasket && bidNext)
						|| (currentActivity != EchoesActivity.FlowerPickToBasket
								&& bidNext && (lastAgentActionStarted
								.equals("SelfReactToEvent")
								|| lastAgentActionStarted
										.equals("SelfReactToEventGeneral")
								|| lastAgentActionStarted
										.equals("SelfReactToAndDontShareEvent")
								|| lastAgentActionStarted
										.equals("SelfReactToAndShareEvent") || lastAgentActionStarted
								.equals("SelfNoticeEvent")))) {
					System.out
							.println("********************** directing to make bid!!!");
					bidNext = false;
					sameBidType = false;
					// if agent has acted then he makes a bid
					// if in the ball throwing activity, the agent always
					// asks for the child to give him a ball so the bid is a
					// request

					// if (currentActivity == EchoesActivity.BallThrowing) {
					// directAgentToMakeRequest(EchoesObjectType.Ball);
					// } else {
					directAgentToMakeABid();
					// }

					// if the action was a bid then wait for the child's
					// response - the agent will wait until triggered by a
					// child action (which might also be no action from the
					// child)
					// } else if (lastAgentActionStarted
					// .equals("SelfExploreCloud")
					// && currentActivity == EchoesActivity.CloudRain) {
					// aePrx.setChosenActivity(EchoesActivity.FlowerGrow
					// .toString());
					// currentActivity = EchoesActivity.FlowerGrow;

				} else if (lastAgentActionStarted.equals("SelfPointBid")) {
					lastAgentActionWasBid = true;
					System.out.println("recognised point");
					getPCcs().reminder.startTime(2000, "madeBid");
					setAgentGoal(wait);
				} else if (lastAgentActionStarted.equals("SelfVerbalBid")) {
					lastAgentActionWasBid = true;
					System.out.println("recognised verbal bid");
					getPCcs().reminder.startTime(2000, "madeBid");
					setAgentGoal(wait);
				} else if (lastAgentActionStarted.equals("SelfLookBid")) {
					lastAgentActionWasBid = true;
					System.out.println("recognised look bid");
					getPCcs().reminder.startTime(2000, "madeBid");
					setAgentGoal(wait);
				} else if (lastAgentActionStarted.equals("SelfTouchBid")) {
					lastAgentActionWasBid = true;
					System.out.println("recognised touch bid");
					getPCcs().reminder.startTime(2000, "madeBid");
					setAgentGoal(wait);
				} else if (lastAgentActionStarted
						.equals("SelfPromptInitiation")) {
					lastAgentActionWasBid = true;
					System.out.println("recognised initiation prompt");
					setAgentGoal(wait);
				} else if (lastAgentActionStarted
						.equals("SelfWalkInNoticeChild")) {
					// setAgentGoal(wait);
				} else if (lastAgentActionStarted.equals("SelfGreetChild")) {
					setAgentGoal(wait);
				}

				if (lastAgentActionStarted.equals("SelfWalkOut")) {
					System.out.println("left scene, getting agent to wait");
					setAgentGoal(wait);
				}

				if (lastAgentActionStarted.equals("SelfGiveThumbsUp")) {
					setAgentGoal(dontWait);
				}

				// sometimes by the time the make bid goal is activated he's
				// already performing another action so tell to wait after
				// last action in each turn taking for each activity
				if (lastAgentActionStarted.equals("SelfThrowBallThroughCloud")
						|| lastAgentActionStarted
								.equals("SelfThrowAcceptedBall")

						|| lastAgentActionStarted
								.equals("SelfTurnFlowerToBall")
						|| lastAgentActionStarted
								.equals("SelfTurnAcceptedFlowerToBall")

						|| lastAgentActionStarted.equals("SelfStackFlowerpot")
						|| lastAgentActionStarted
								.equals("SelfStackAcceptedPot")

						|| lastAgentActionStarted
								.equals("SelfPutFlowerInBasket")
						|| lastAgentActionStarted
								.equals("SelfPutAcceptedFLowerInBasket")

						|| lastAgentActionStarted
								.equals("SelfThrowBallThroughCloud")
						|| lastAgentActionStarted
								.equals("SelfPutAcceptedFlowerInPot")

						|| lastAgentActionStarted
								.equals("SelfExploreCloudPushedCloud")
						|| lastAgentActionStarted
								.equals("SelfExploreCloudPushedCloudAcceptedPot")

						|| lastAgentActionStarted.equals("SelfExploreCloud")) {
					setAgentGoal(wait);

				}

				// tell the Child Model whether the action completed was a bid
				// and of what type - this is better done from here because of
				// the different RE/AE names
				if ((lastAgentActionStarted.equals("SelfVerbalBid") && action
						.equals("Gesture"))
						|| (lastAgentActionStarted.equals("SelfPointBid") && action
								.equals("PointAt"))
						|| (lastAgentActionStarted.equals("SelfTouchBid") && action
								.equals("TouchObject"))
						|| (lastAgentActionStarted.equals("SelfLookBid") && action
								.equals("LookAtObject"))) {
					// tellCMifActionBid();
				}

				// USER actions
			} else if (agentId.equals("User")) {
				currentUserAction = action;
				if (checkEndActivity()) {
					return;
				}
				System.out
						.println("*********************************************"
								+ " user action published: "
								+ action
								+ " with arguments: " + details);
				if (previousUserAction.equals(ChildAction.cloud_rain.getName())
						&& action.equals(ChildAction.cloud_rain.getName())) {
					// ignore consecutive cloud_rain actions
					System.out.println("ignoring consecutive cloud rain");
				} else {
					System.out.println("processing child action");
					getPCcs().childActionH.handleChildAction(action, details);
					previousUserAction = action;
				}
			}
		}

		public void agentActionFailed(String agentId, String action, List<String> details, String reason) {}

		public void agentActionStarted(String agentId, String action, List<String> details) 
		{
			if (lastAgentActionStartedAEname.equals("SelfWalkOut")) {
				dmPrx.dimScene();
			}
			// hack because there is delay when the objects in BallSorting are
			// added
			if (currentActivity == EchoesActivity.BallSorting) {
				setBallContainerColour();
			}
			// this is used to decide whether to react to an object animation
			// (in RElistener objectPropertyChanged). Because the animation will
			// be triggered before the action is complete we need to set the
			// last agent action based on action started not completed
			// NOTE we want to ignore facial expressions and speech
			if (!action.equals("FacialExpression") && !action.equals("Say")) {
				// ths should be OK because lastAgentActionStartedAEname is
				// updated when the AE starts the action and the
				// agentActionStarted method here is triggered by the RE
				lastAgentActionStarted = lastAgentActionStartedAEname;

				if (currentActivity == EchoesActivity.FlowerPickToBasket) {
					if ((lastAgentActionStarted.equals("SelfVerbalBid")
							|| lastAgentActionStarted.equals("SelfPointBid")
							|| lastAgentActionStarted.equals("SelfTouchBid") || lastAgentActionStarted
							.equals("SelfLookBid"))
							//&& action.equals("LookAtObject")
							&& !lastAgentActionStartedAEargs.get(2).equals(
									"requestObject")) {
						// for (String flower : flowers) {
						// dmPrx.setFlowerInteractivity(flower, true);
						// }
					}
				}

				System.out.println("agent action started: "
						+ lastAgentActionStarted);

				if ((lastAgentActionStarted.equals("SelfExploreCloud") && currentActivity == EchoesActivity.CloudRain)
						|| (lastAgentActionStarted
								.equals("SelfExploreCloudPushedCloud") && currentActivity == EchoesActivity.FlowerGrow)
						|| (lastAgentActionStarted
								.equals("SelfExploreCloudPushedCloudAcceptedPot") && currentActivity == EchoesActivity.FlowerGrow)
						|| (lastAgentActionStarted
								.equals("SelfPutFlowerInBasketDummy") && currentActivity == EchoesActivity.FlowerPickToBasket)
						|| (lastAgentActionStarted
								.equals("SelfPutFlowerInBasket") && currentActivity == EchoesActivity.FlowerPickToBasket)
						|| (lastAgentActionStarted
								.equals("SelfPutAcceptedFLowerInBasket") && currentActivity == EchoesActivity.FlowerPickToBasket)
						|| (lastAgentActionStarted.equals("SelfStackFlowerpot") && currentActivity == EchoesActivity.PotStackRetrieveObject)
						|| (lastAgentActionStarted
								.equals("SelfStackAcceptedPot") && currentActivity == EchoesActivity.PotStackRetrieveObject)
						|| (lastAgentActionStarted
								.equals("SelfTurnFlowerToBall") && currentActivity == EchoesActivity.FlowerTurnToBall)
						|| (lastAgentActionStarted
								.equals("SelfThrowBallThroughCloud") && currentActivity == EchoesActivity.BallThrowing)
						|| (lastAgentActionStarted
								.equals("SelfThrowAcceptedBall") && currentActivity == EchoesActivity.BallThrowing)
						|| (lastAgentActionStarted.equals("SelfPutBallInPile") && currentActivity == EchoesActivity.BallSorting)) {
					System.out.println("Setting to bid next!!!!!!!!!!!!");
					bidNext = true;
				}

				// a few hacks here...
				if (lastAgentActionStarted.equals("SelfGiveThumbsUp")
						|| lastAgentActionStarted.equals("SelfEndActivity")) {
					bidNext = false;
				}
				// hack to keep going after these actions and not wait
				if (lastAgentActionStarted.equals("SelfPutAcceptedBasketDown")
						|| lastAgentActionStarted
								.equals("SelfPutDownAcceptedPot")) {
					setAgentGoal(dontWait);
				}
			}
		}
	}

	/**
	 * A class to listen for messages from the rendering engine updating the
	 * state of the world. Fill in the body of any methods that you need to use
	 * ...
	 * 
	 * @author Mary Ellen Foster
	 */
	private class RenderingListenerImpl implements IRenderingListener 
	{

		public void agentAdded(String agentId, Map<String, String> props) {}

		public void agentPropertyChanged(String agentId, String propName, String propValue) 
		{
			if (propName.equals("Visible")) {

				if (propValue.equals("True")) {
					agentIsInEchoes = true;
				} else {
					agentIsInEchoes = false;
					// if the agent is already in the scene need him to walk out
					// before bubbles/garden scene started
					if (sceneToStartAfterWalkingOut == EchoesScene.Bubbles) {
						getPCcs().nonAgentSceneH
								.decideNonAgentSceneParameters(EchoesScene.Bubbles);
					} else if (sceneToStartAfterWalkingOut == EchoesScene.Garden) {
						dmPrx.setScene(EchoesScene.Garden);
						dmPrx.arrangeScene(EchoesScene.Garden,
								EchoesActivity.Explore, 1, false);
						getPCcs().agentH.directAgentChangeInvolvement(false);
					}
					sceneToStartAfterWalkingOut = null;
					if (doNextActivity) {
						// reset lists of object properties because of delay in
						// removeObject being published by RE
						pots.clear();
						potFlowerProperty.clear();
						potIsStackedProperty.clear();
						potHasObjectProperty.clear();
						flowers.clear();
						flowersInBasket.clear();
						ballHitCloud.clear();
						ballInContainer.clear();
						ballColours.clear();
						allGreenGone = false;
						allYellowGone = false;
						allBlueGone = false;
						containerColours.clear();

						startActivity();

					}
				}
			}
		}

		public void agentRemoved(String agentId) {}

		public void objectAdded(String objId, Map<String, String> props) 
		{
			Logger.Log("info","objectAdded:" + objId);
			if (props.get("type").equals("Pot")) {
				pots.put(objId, "");
				potFlowerProperty.put(objId, "False");
				potIsStackedProperty.put(objId, "False");
				potHasObjectProperty.put(objId, "False");
			}

			if (props.get("type").equals("Flower")) {
				flowers.add(objId);
				flowersInBasket.put(objId, "False");
			}

			if (props.get("type").equals("Ball")) {
				ballInContainer.put(objId, "False");
				ballHitCloud.put(objId, "False");
				ballColours.put(objId, "red");
			}

			if (props.get("type").equals("Container")) {
				containerColours.put(objId, "False");
			}
		}

		public void objectPropertyChanged(String objId, String propName, String propValue) 
		{
			// update properties
			// if the location is what has changed update the location
			if (propName.equals("Pos")) {
				// need to parse propValue when propName is location
				dmPrx.updateObjectLocation(objId, propValue);
				dmPrx.addObject(EchoesObjectType.Flower);

			}
			System.out.println("RElist object: " + objId + " property: "
					+ propName + " changed: " + propValue);

			if (propName.equals("draggedOverAgent")) {
				if (!propValue.equals("None")) {
					// only accept these types of objects
					if (dmPrx.getObjectType(objId).equals("Pot")
							|| dmPrx.getObjectType(objId).equals("Flower")
							|| dmPrx.getObjectType(objId).equals("Basket")
							|| dmPrx.getObjectType(objId).equals("Ball")) {

						System.out
								.println("@@@@@@@@@@@@@@@@@@@ child dragged object: "
										+ objId + " over agent");
						userStillHoldingObject = true;
						objectOfferedId = objId;

						if (currentActivity == EchoesActivity.FlowerPickToBasket) {
							getPCcs().reminder.startTime(10, "assessGive");
						} else {
							getPCcs().reminder.startTime(1000, "assessGive");
						}
					}
				} else {
					System.out
							.println("&&&&&&&&&&&&&&& has dragged object away");
					userStillHoldingObject = false;
				}

			} else if (propName.equals("overAgent")) {
				if (propValue.equals("None") && objId.equals(objectOfferedId)) {
					System.out
							.println("!!!!!!!!!!!! object no longer over agent");
					userStillHoldingObject = false;
				}

			} else if (propName.equals("nearAgent")) {
				// if the agent is near a bubble then trigger goal to pop it
				if (dmPrx.getObjectType(objId).equals("Bubble")) {
					if (!propValue.equals("False")) {
						aePrx.setObjectFocus(objId, "popBubble");
						setAgentGoal("popBubble");
					} else {
						resetAgentGoal("popBubble");
					}
				}

				// object animations - decide whether to react to them
				// flower is placed in pot
			} else if (propName.equals("flower_pot")) {
				if (!propValue.equals("None")) {
					aePrx.setReactToEvent(true, "flower_pot", objId, false);
				}
				// flower is in basket
			} else if (propName.equals("flower_basket")) {
				// System.out.println("*************************************************** flower: "
				// + objId + " publishing on basket " + propValue);

				if (!propValue.equals("None")) {
					if (lastAgentActionStarted.equals("SelfPutFlowerInBasket")
							|| lastAgentActionStarted
									.equals("SelfPutAcceptedFLowerInBasket")) {
						aePrx.setReactToEvent(false, "flower_basket", objId,
								true);
					} else {
						aePrx.setReactToEvent(true, "flower_basket", objId,
								false);
					}
					if (getCurrentActivity() == EchoesActivity.FlowerPickToBasket) {
						dmPrx.setFlowerInteractivity(objId, false);
						dmPrx.updateObjectLocation(objId, propValue);
						dmPrx.addObject(EchoesObjectType.Flower);
					}
				}

				// update flower hashmap to assess end of flower picking
				// activity
				if (!propValue.equals("None")) {
					flowersInBasket.put(objId, "True");
				} else {
					flowersInBasket.put(objId, "False");
				}

				// pot has flower
			} else if (propName.equals("pot_flower")) {

				// pot stacked on another pot
			} else if (propName.equals("is_on_top_of")) {
				if (propValue.equals("None")) {
					potIsStackedProperty.put(objId, "False");
				} else {
					potIsStackedProperty.put(objId, "isStacked");
				}
				// pot has another pot stacked on it
			} else if (propName.equals("has_on_top")) {
				if (propValue.equals("None")) {
					potHasObjectProperty.put(objId, "False");
				} else {
					potHasObjectProperty.put(objId, "hasObject");
				}
				// flower is turned into bubble
			} else if (propName.equals("flower_bubble")) {
				aePrx.setReactToEvent(true, "flowerBubble", propValue, false);

				// flower is turned into ball
			} else if (propName.equals("flower_ball")) {
				aePrx.setReactToEvent(true, "flowerBall", propValue, false);

				// cloud rains True/False when it stops
			} else if (propName.equals("cloud_rain")) {
				// if (propValue.equals("True")) {
				// if (agentIsInEchoes) {
				// if (currentActivity == EchoesActivity.FlowerGrow) {
				// aePrx.setReactToEvent(false, "cloudRain", objId,
				// false);
				// }
				// }
				// }
				// pot is stacked

			} else if (propName.equals("pot_stack")) {
				if (!propValue.equals("None")) {
					if (lastAgentActionStarted.equals("SelfStackFlowerpot")
							|| lastAgentActionStarted
									.equals("SelfStackAcceptedPot")) {
						aePrx.setReactToEvent(false, "pot_stack", objId, true);
					} else {
						aePrx.setReactToEvent(true, "pot_stack", objId, false);
					}
				}
				// leaves are flying
			} else if (propName.equals("leaves_flying")) {

				// pond shrinks
			} else if (propName.equals("pond_shrink")) {

				// pond grows
			} else if (propName.equals("pond_grow")) {

				// bubble pops
			} else if (propName.equals("bubble_pop")) {

				// bubble merges
			} else if (propName.equals("bubble_merge")) {

				// cloud grows a flower
			} else if (propName.equals("cloud_flower")) {

			} else if (propName.equals("is_growing")) {
				if (propValue.equals("True")) {
					if (currentActivity == EchoesActivity.CloudRain
							|| currentActivity == EchoesActivity.FlowerGrow) {
						// aePrx.setReactToEvent(false, "is_growing", objId,
						// false);
						// }
						// if (!propValue.equals("None")) {
						System.out.println("pot flower!!"
								+ " last agent action: "
								+ lastAgentActionStarted);
						if (lastAgentActionStarted
								.equals("SelfExploreCloudPushedCloud")
								&& currentActivity == EchoesActivity.FlowerGrow) {
							System.out
									.println("^^^^^^^^^^^^^^^^ setting react to event of growing a flower excited (object id is: "
											+ objId);
							aePrx.setReactToEvent(false, "pot_flower", objId,
									true);
						} else {
							System.out
									.println("^^^^^^^^^^^^^^^^ setting react to event of growing a flower look (object id is: "
											+ objId);

							aePrx.setReactToEvent(false, "pot_flower", objId,
									false);
						}
					}
				}
			}

			// count pots that have flower in them
			if (potFlowerProperty.containsKey(objId)
					&& propName.equals("pot_flower")) {
				if (propValue.equals("None")) {
					potFlowerProperty.put(objId, "False");
				} else {
					potFlowerProperty.put(objId, "True");
				}
			}

			// count number of flowers in basket
			if (propName.equals("basket_numflowers")) {
				numFlowersInBasket = propValue;
			}

			// if (propName.equals("ball_colour")) {
			// ballColours.put(objId, propValue);
			// }

			if (propName.equals("cloud_hitby")) {
				ballHitCloud.put(propValue, "True");
				aePrx.setReactToEvent(false, "cloud_hitby", propValue, true);
			} else if (propName.equals("ball_container")) {
				if (!propValue.equals("None")) {
					ballInContainer.put(objId, "True");
					boolean noMoreGreen = true;
					boolean noMoreBlue = true;
					boolean noMoreYellow = true;
					System.out.println("lskdfsld: " + ballInContainer);
					System.out.println("sdfsd " + ballColours);
					for (String ball : ballInContainer.keySet()) {
						if (ballInContainer.get(ball).equals("False")) {
							if (ballColours.get(ball).equals("red")) {
								noMoreGreen = false;
							} else if (ballColours.get(ball).equals("blue")) {
								noMoreBlue = false;
							} else if (ballColours.get(ball).equals("yellow")) {
								noMoreYellow = false;
							}
						}
					}
					if (noMoreGreen && !allGreenGone) {
						allGreenGone = true;
						aePrx.setReward("red");
					} else if (noMoreBlue && !allBlueGone) {
						allBlueGone = true;
						aePrx.setReward("blue");
					} else if (noMoreYellow && !allYellowGone) {
						allYellowGone = true;
						aePrx.setReward("yellow");
					}
					System.out.println("noMoreGreen: " + noMoreGreen
							+ ", noMoreBlue " + noMoreBlue + ", noMoreYellow "
							+ noMoreYellow + " , allGreenGone " + allGreenGone
							+ " allBlueGone " + allBlueGone + " allYellowGone "
							+ allYellowGone);
					setBallContainerColour();

					aePrx
							.setReactToEvent(false, "ball_container", objId,
									false);

					// setAgentGoal("beExpressiveGeneral");
				}
			} else if (propName.equals("ball_colour")) {
				ballColours.put(objId, propValue);
			} else if (propName.equals("container_colour")) {
				containerColours.put(objId, propValue);
			} else if (propName.equals("container_reward")) {
				// aePrx.setReactToEvent(false, "pot_flower", objId, true);
			}
		}

		public void objectRemoved(String objId) 
		{
			flowers.remove(objId);
			ballHitCloud.remove(objId);
			ballInContainer.remove(objId);
			ballColours.remove(objId);
			containerColours.remove(objId);
		}

		public void scenarioEnded(String name) {}
		public void scenarioStarted(String name) {}
		public void userStarted(String name) {}

		public void userTouchedAgent(String agentId) {
			ArrayList<String> details = new ArrayList<String>();
			details.add("");
			getPCcs().childActionH.handleChildAction(
					ChildAction.userTouchedAgent.getName(), details);
		}

		public void userTouchedObject(String objId) 
		{
			System.out.println("User touched object: " + objId);
			if (currentActivity == EchoesActivity.FlowerPickToBasket
					&& lastObject.equals(objId)) {
				lastObject = "";
				List<String> details = new ArrayList<String>();
				details.add(objId);
				details.add(objId);
				if (flowers.contains(objId)) {
					for (String flower : flowers) {
						dmPrx.setFlowerInteractivity(flower, false);
					}
					System.out.println("User touched a flower");
					dmPrx.moveFlower(objId);
					dmPrx.setFlowerLoc(objId);
					getPCcs().childActionH
							.handleChildAction(ChildAction.flower_placeInBasket
									.getName(), details);
				}
			}
		}

		public void worldPropertyChanged(String propName, String propValue) {}

	}

	/**
	 * Directs the agent's behaviour.
	 * 
	 * Only call from engagement/disengagement class when there's been a CHANGE
	 * in engagement
	 * 
	 */
	public void directAgentChangeInvolvement(boolean involveAgent) {
		// boolean agentInvolvement = getPCcs().agentInvolvementH
		// .decideAgentInvolvement();

		if (involveAgent) {
			if (!agentIsInEchoes) {
				agentNotInvolved = true;
				System.out.println("in echoes?");
				setAgentGoal("enterECHOES");
			} else {
				if (!agentNotInvolved) {
					setAgentGoal(dontWait);
					agentNotInvolved = true;
				}
			}
		} else {
			agentNotInvolved = false;
			setAgentGoal(wait);
		}
	}

	// temporary until CM can be queries
	// activity will be decided when making a bid
	public void directAgent(boolean involveAgent, EchoesObjectType object,
			EchoesActivity activity) {
		this.currentActivity = activity;
		if (activity == EchoesActivity.BallSorting) {
			setBallContainerColour();
		}
		aePrx.setChosenActivity(activity.toString());
		setAgentGoal("wait");
		directAgentChangeInvolvement(involveAgent);
		if (activity == EchoesActivity.FlowerPickToBasket) {
			directAgentToMakeRequest("verbalBidType", EchoesObjectType.Basket);
		}

		if (activity == EchoesActivity.TickleAndTree
				|| activity == EchoesActivity.ExploreWithAgent) {
			setAgentGoal("wait");
		} else {
			setAgentGoal("dontWait");
		}
	}

	/**
	 * Called when an activity is ended and a new one needs to be started
	 * 
	 */
	public void directAgentActivityEnded() {

		setAgentGoal(wait);

		activityEnded = false;
		return;

		// from growing flowers go to picking flowers
		// if (currentActivity == EchoesActivity.FlowerGrow) {
		// aePrx.setChosenActivity(EchoesActivity.FlowerPickToBasket
		// .toString());
		// currentActivity = EchoesActivity.FlowerPickToBasket;
		// directAgentToMakeRequest(EchoesObjectType.Basket);
		// reset to false with new activity

		// disable so that activities can be set manually
		// also set activity to null!!!! so agent does not respond to child
		// actions based on ended activity
		// setAgentGoal(wait);

		// activityEnded = false;
		// return;
		// from picking flowers go to stacking pots
		// } else if (currentActivity == EchoesActivity.FlowerPickToBasket) {
		// aePrx.setChosenActivity(EchoesActivity.PotStackRetrieveObject
		// .toString());
		// currentActivity = EchoesActivity.PotStackRetrieveObject;
		// setAgentGoal(dontWait);
		// reset to false with new activity

		// disable so that activities can be set manually
		// setAgentGoal(wait);

		// activityEnded = false;
		// return;
		// from stacking pots go to exploration (need to implement this)
		// } else if (currentActivity == EchoesActivity.PotStackRetrieveObject)
		// {
		// aePrx.setChosenActivity(EchoesActivity.PotStackRetrieveObject
		// .toString());
		// setAgentGoal(leave);
		// we don't reset to false because this is the last activity
		// activityEnded = true;

		// disable so that activities can be set manually
		// setAgentGoal(wait);
		// activityEnded = false;

		// return;
		// } else if (currentActivity == EchoesActivity.FlowerTurnToBall) {
		// aePrx.setChosenActivity(EchoesActivity.PotStackRetrieveObject
		// .toString());
		// setAgentGoal(leave);
		// we don't reset to false because this is the last activity
		// activityEnded = true;

		// disable so that activities can be set manually
		// setAgentGoal(wait);
		// activityEnded = false;

		// return;
		// }

	}

	/**
	 * Called when other methods have decided the agent should support a goal
	 * 
	 */
	public void directAgentToMakeABid() {
		System.out
				.println("in agentBehaviourHandler, setting record of the current activity: "
						+ currentActivity
						+ ", and that the agent is going to make a bid");
		getPCcs().goalsActivityR.setCurrentActivity(currentActivity);
		// sets the FAtiMA KB, which will select appropriate actions
		chooseBidAttributes("", "", null);
	}

	/**
	 * Called when bid is to request object
	 * 
	 */
	public void directAgentToMakeRequest(EchoesObjectType objectType) {
		System.out
				.println("in agentBehaviourHandler, directing agent to request object");
		chooseBidAttributes("", "requestObject", objectType);
	}

	/**
	 * Called when bid is to request object
	 * 
	 * Specified bid type - because e.g. for requesting basket don't want a
	 * point or look bid
	 */
	public void directAgentToMakeRequest(String bidType,
			EchoesObjectType objectType) {
		sameBidType = true;
		System.out
				.println("in agentBehaviourHandler, directing agent to request object");
		chooseBidAttributes(bidType, "requestObject", objectType);
	}

	/**
	 * Repeat previous bid
	 * 
	 */
	public void repeatBid() {
		if (!sameBidType) {
			if (lastBidType.equals("touchBidType")) {
				lastBidType = "touchBidType";
			} else if (lastBidType.equals("pointBidType")) {
				if (currentActivity == EchoesActivity.CloudRain)
					lastBidType = "pointBidType";
				else
					lastBidType = "touchBidType";
			} else if (lastBidType.equals("lookBidType")) {
				lastBidType = "pointBidType";
			} else if (lastBidType.equals("verbalBidType")) {
				lastBidType = "lookBidType";
			}
		}
		makeBid(true);
	}

	/**
	 * Returns agent action
	 * 
	 * bidType is only set if bidPurpose has also been specified
	 * 
	 */
	public void chooseBidAttributes(String bidType, String bidPurpose, EchoesObjectType objType) 
	{
		if (!bidPurpose.isEmpty()) 
		{
			this.lastBidPurpose = bidPurpose;

			if (!bidType.isEmpty()) {
				this.lastBidType = bidType;
				getPCcs().goalsActivityR.setBidType(bidType);
			} else {
				this.lastBidType = getPCcs().goalsActivityR.getBidType();
			}

			if (objType != null) {
				this.lastObject = getPCcs().goalsActivityR
						.getTargetObject(objType);
				this.lastObjectType = getPCcs().goalsActivityR
						.getTargetObjectType();
			} else {
				this.lastObject = getPCcs().goalsActivityR
						.getTargetObject(null);
				this.lastObjectType = getPCcs().goalsActivityR
						.getTargetObjectType();
			}
		} else {
			this.lastBidType = getPCcs().goalsActivityR.getBidType();
			this.lastBidPurpose = getPCcs().goalsActivityR
					.getDefaultBidPurpose();
			this.lastObject = getPCcs().goalsActivityR.getTargetObject(null);
			this.lastObjectType = getPCcs().goalsActivityR
					.getTargetObjectType();
		}

		// set target object in childActionHandler to check if responded to
		// specific bid
		getPCcs().childActionH.setTargetObject(lastObject);

		makeBid(false);
	}

	/**
	 * Direct FATIMA for bid
	 * 
	 */
	public void makeBid(boolean repeat) {
		aePrx.setBidType(lastBidType);
		aePrx.setBidPurpose(lastBidPurpose);
		if (repeat) {
			aePrx.setBidRepeat("second");
		} else {
			aePrx.setBidRepeat("first");
		}
		aePrx.setTarget(lastObject);
		System.out.println("In agentH just set, the bid type: " + lastBidType
				+ ", the target: " + lastObject);
		System.out.println("activating goal to make bid");
		// activate the agents goal to make a bid to the child
		aePrx.setGoal(makeBid);
	}

	/**
	 * User to set the agent's goal when there is no associated activity
	 * 
	 * @param goal
	 */
	public void setAgentGoal(String goal) {
		if (wait.equals(goal)) {
			notifyAgentIsWaiting();
		}

		aePrx.setGoal(goal);
	}

	public void resetAgentGoal(String goal) {
		aePrx.resetGoal(goal);
	}

	/**
	 * Cancels all active goals
	 * 
	 * @param goal
	 */
	public void cancelAllGoala() {
		aePrx.cancelAllGoals();
	}

	/**
	 * Resets the history for when a new goal is chosen
	 * 
	 */
	public void resetHistory() {
		for (String key : ((Map<String, String>) interactionHistory).keySet()) {
			interactionHistory.put(key, "");
		}
	}

	/**
	 * Check if activity is ended
	 * 
	 */
	public boolean checkEndActivity() 
	{
		boolean noMorePots = true;

		// cloudRain is an initial activity before going into flowerGrow (this
		// transition is hardwired and will always happen) - so cloudRain never
		// ends, it just turns into FlowerGrow
		if (currentActivity == EchoesActivity.CloudRain) {
			activityEnded = false;
		}

		for (String key : ((Map<String, String>) pots).keySet()) {
			if (potFlowerProperty.get(key).equals("False")) {
				noMorePots = false;
			}
		}
		if (pots.isEmpty()) {
			noMorePots = false;
		}
		// check activity status
		System.out.println("currentActivity: " + currentActivity
				+ " no more pots? " + noMorePots);
		// check activity end for flower growing activity
		if (currentActivity == EchoesActivity.FlowerGrow && noMorePots) {
			if (!activityEnded) {
				setAgentGoal("giveFeedbackEndOfActivity");
				activityEnded = true;
			}
		}

		// check activity end for flower picking
		boolean noFlowersOutsideBasket = true;
		for (String key : ((Map<String, String>) flowersInBasket).keySet()) {
			if (flowersInBasket.get(key).equals("False")) {
				System.out.println("there is a pot with a flower in it");
				noFlowersOutsideBasket = false;
			}
		}
		if (flowersInBasket.isEmpty()) {
			noFlowersOutsideBasket = false;
		}
		System.out.println("(((((((((((((((((( child action is: "
				+ currentUserAction);
		if (currentActivity == EchoesActivity.FlowerPickToBasket
				&& noFlowersOutsideBasket
				&& (lastAgentActionStarted.equals("SelfPutFlowerInBasket")
						|| lastAgentActionStarted
								.equals("SelfPutAcceptedFLowerInBasket")
						|| currentUserAction
								.equals(ChildAction.flower_placeInBasket
										.getName())
						|| currentUserAction.equals(ChildAction.flower_bubble
								.getName()) || currentUserAction
						.equals(ChildAction.flower_ball.getName()))) {

			// if number of flowers in pot are more or equal to 4

			if (!activityEnded) {
				// in most recent version the activity doesn't end

				// setAgentGoal("giveFeedbackEndOfActivity");
				// activityEnded = true;
				// activitySETtoEnd = true;
			}
			// else if the number is less than 4
			// currentActivity = EchoesActivity.FlowerPickToBasket;
			// but not activityEnded true in this case
		}

		// check activity end for pot stacking
		int numPotsNotStacked = 0;
		int numPotsHaveObject = 0;
		for (String key : ((Map<String, String>) pots).keySet()) {
			if (potIsStackedProperty.get(key).equals("False")) {
				numPotsNotStacked++;
			}
			if (potHasObjectProperty.get(key).equals("False")) {
				numPotsHaveObject++;
			}
		}
		boolean noMorePotsToStack = true;
		if (numPotsNotStacked > 1 || numPotsHaveObject > 1) {
			noMorePotsToStack = false;
		}

		if (pots.isEmpty()) {
			noMorePotsToStack = false;
		}

		if (currentActivity == EchoesActivity.PotStackRetrieveObject
				&& noMorePotsToStack) {
			if (!activityEnded) {
				setAgentGoal("giveFeedbackEndOfActivity");
				activityEnded = true;
			}
		}

		// check end of activity for flowerTurnToBall - no more flowers left
		if (currentActivity == EchoesActivity.FlowerTurnToBall) {
			if (flowers.isEmpty() && !ballHitCloud.isEmpty()) {
				if (!activityEnded) {
					setAgentGoal("giveFeedbackEndOfActivity");
					activityEnded = true;
				}
			}
		}

		if (currentActivity == EchoesActivity.BallThrowing) {

			boolean ballYetToHitCloud = false;
			System.out
					.println("checking status of ball throwing. ballHitCloud: "
							+ ballHitCloud);
			// check activity ended for throwing balls
			if (ballHitCloud != null) {
				for (String ball : ballHitCloud.keySet()) {
					if (ballHitCloud.get(ball).equals("False")) {
						ballYetToHitCloud = true;
					}
				}
			}
			if (ballHitCloud.isEmpty()) {
				ballYetToHitCloud = true;
			}

			if (!ballYetToHitCloud) {
				setAgentGoal("giveFeedbackEndOfActivity");
				activityEnded = true;
			}
		}

		if (currentActivity == EchoesActivity.BallSorting) {
			boolean thereAreBallsOutsideContainer = false;
			for (String ball : ballInContainer.keySet()) {
				if (ballInContainer.get(ball).equals("False")) {
					thereAreBallsOutsideContainer = true;
				}
			}
			if (ballInContainer.isEmpty()) {
				thereAreBallsOutsideContainer = true;
			}

			if (!thereAreBallsOutsideContainer) {
				setAgentGoal("giveFeedbackEndOfActivity");
				activityEnded = true;
			}

		}

		if (activityEnded) {
			notifyActivityHasEnded();
		}

		return activityEnded;

	}

	/**
	 * Called when the child has acted and there is an agent in the world
	 * 
	 * @param childAction
	 * @throws StringLabelException
	 */
	public void respondToChildAction(String interpretedChildAction) {
		// catch a mistyped child action name
		try {
			getPCcs().goalsActivityR.addChildActionToHistory(ChildAction
					.getChildAction(interpretedChildAction).getName());
			// set the KB, the goal is still active
			System.out.println("recognised child action, setting FAtiMA KB");

			if (interpretedChildAction.equals(ChildAction.noAction.getName())) {
				System.out.println("child action is NO ACTION");
				timesMadeBidNoResponse++;
				repeatBid();
			} else if (interpretedChildAction.equals(ChildAction.respondedToBid
					.getName())) {
				// reset count
				timesMadeBidNoResponse = 0;
				System.out
						.println("setting the agent to not wait - will re-activate Explore goal");
				setAgentGoal(dontWait);
			} else if (interpretedChildAction
					.equals(ChildAction.gaveRequestedObject.getName())) {
				aePrx.setObjectFocus(objectOfferedId, "acceptObject");
				setAgentGoal("acceptObject");
			} else if (interpretedChildAction
					.equals(ChildAction.gaveObjectRelevantActivity.getName())) {
				aePrx.setObjectFocus(objectOfferedId, "acceptObject");
				setAgentGoal("acceptObject");
			} else {
				// continue waiting
			}

		} catch (StringLabelException e) {
			e.printStackTrace();
		}
	}

	/**
	 * to process give actions - 2 seconds are up
	 * 
	 */
	public void checkGiveStatus() {
		if (userStillHoldingObject == true) {
			System.out.println("$$$$$$$$$$$$$$$$$$$$$ action is give");
			if (dmPrx.getObjectType(objectOfferedId).equals(
					EchoesObjectType.Basket.toString())) {
				respondToChildAction(getPCcs().childActionH
						.interpretChildAction(true, ChildAction.gave_basket
								.getName(), objectOfferedId));
			} else if (dmPrx.getObjectType(objectOfferedId).equals(
					EchoesObjectType.Pot.toString())) {
				respondToChildAction(getPCcs().childActionH
						.interpretChildAction(true, ChildAction.gave_flowerpot
								.getName(), objectOfferedId));
			} else if (dmPrx.getObjectType(objectOfferedId).equals(
					EchoesObjectType.Ball.toString())) {
				respondToChildAction(getPCcs().childActionH
						.interpretChildAction(true, ChildAction.gave_ball
								.getName(), objectOfferedId));
			} else if (dmPrx.getObjectType(objectOfferedId).equals(
					EchoesObjectType.Flower.toString())) {
				respondToChildAction(getPCcs().childActionH
						.interpretChildAction(true, ChildAction.gave_flower
								.getName(), objectOfferedId));
			}
		} else {
			System.out
					.println("$$$$$$$$$$$$$$$$$$$$$ timer up, action was not give");
		}
	}

	public boolean checkPotWithoutFlower() {
		boolean potWithoutFlower = false;
		for (String key : potFlowerProperty.keySet()) {
			if (potFlowerProperty.get(key).equals("False")) {
				potWithoutFlower = true;
			}
		}
		return potWithoutFlower;
	}

	public void setAEnameAndArgsForLastActionStarted(String actionName,
			List<String> args) {
		this.lastAgentActionStartedAEname = actionName;
		for (String arg : args) {
			lastAgentActionStartedAEargs.add(arg);
		}
	}

	/**
	 * Returns the current activity
	 * 
	 * @return
	 */
	public EchoesActivity getCurrentActivity() {
		return currentActivity;
	}

	public boolean getIsAgentInWorld() {
		return agentIsInEchoes;
	}

	public boolean getWasAgentActionBid() {
		return lastAgentActionWasBid;
	}

	public String getLastBidType() {
		return lastBidType;
	}

	public String getLastBidPurpose() {
		return lastBidPurpose;
	}

	public String getLastObject() {
		return lastObject;
	}

	public String getLastObjectType() {
		return lastObjectType.toString();
	}

	public void tellCMifActionBid() {

		String type = "";
		String objId = "";

		if (lastAgentActionWasBid) {
			if (lastAgentActionStartedAEname.equals("SelfVerbalBid")) {
				type = "verbal";
			} else if (lastAgentActionStartedAEname.equals("SelfPointBid")) {
				type = "point";
				objId = lastAgentActionStartedAEargs.get(0);
			} else if (lastAgentActionStartedAEname.equals("SelfLookBid")) {
				type = "look";
				objId = lastAgentActionStartedAEargs.get(0);
			} else if (lastAgentActionStartedAEname.equals("SelfTouchBid")) {
				type = "touch";
				objId = lastAgentActionStartedAEargs.get(0);
			}
			getPCcs().childM.agentMadeBid(type, objId);
		} else {
			getPCcs().childM.agentMadeBid("", "");
		}
	}

	public void setBallContainerColour() {
		String chosenBallId = "";
		String chosenContainerId = "";
		System.out.println("there are containers: " + ballInContainer.keySet());
		for (String ball : ballInContainer.keySet()) {
			if (ballInContainer.get(ball).equals("False")) {
				chosenBallId = ball;
				System.out.println("ball id chosen is: " + ball);
				for (String container : containerColours.keySet()) {
					System.out.println("container colour: "
							+ containerColours.get(container));
					System.out.println("ball colour: " + ballColours.get(ball));
					if (containerColours.get(container).equals(
							ballColours.get(ball))) {
						chosenContainerId = container;
						aePrx.setBallSortingTargets(chosenBallId,
								chosenContainerId);
						System.out.println("found ball and container id");
						return;
					}
				}
			}
		}
		System.out.println("didn't find ball and container id");
		aePrx.setBallSortingTargets(chosenBallId, chosenContainerId);
	}

	public void setNextActivityAndObject(EchoesActivity activity,
			EchoesObjectType object) {
		nextActivity = activity;
		nextObject = object;
		doNextActivity = true;
	}

	public void startActivity() {
		dmPrx.setScene(EchoesScene.GardenTask);
		dmPrx.arrangeScene(EchoesScene.GardenTask, nextActivity, 1, contingent);

		directAgent(true, nextObject, nextActivity);

		doNextActivity = false;
	}

	public void setActivityContingent(boolean contingent) {
		this.contingent = contingent;
	}

	public void setSceneToStartAfterWalkingOut(EchoesScene scene) {
		sceneToStartAfterWalkingOut = scene;
	}

	private static final int ONE_MINUTE = 60000;

	private final InterventionNotifier notifier = new InterventionNotifier();

	public void addInterventionHandler(Observer observer) {
		notifier.addObserver(observer);
	}

	void notifyAgentIsWaiting() {
		Intervention intervention;
		if (!getIsAgentInWorld()) {
			intervention = Intervention.AGENT_ENTER;
		} else if (lastAgentActionWasBid) {
			intervention = Intervention.AGENT_PROMPT_AGAIN;
		} else {
			intervention = Intervention.AGENT_TAKE_TURN;
		}
		notifier.scheduleIntervention(intervention, ONE_MINUTE);
	}

	void notifyActivityHasEnded() {
		notifier.scheduleIntervention(Intervention.END_SESSION, 0);
	}
}
