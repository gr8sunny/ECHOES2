package pedagogicComponent;

import java.util.List;
import utils.Interfaces.*;
import utils.Enums.*;
import pedagogicComponent.data.*;

/**
 * Called when the child performs an action (not any action on the screen, but
 * an action on the object or agent) calls appropriate components to decide what
 * to do next.
 * 
 * @author katerina
 * 
 */
public class ChildActionHandler extends PCcomponentHandler {

	// Use this proxy to publish "userAction" events when necessary
	private IEventListener eventPublisher;
	private String agentTargetObject;
	private IChildModel cmPrx;
	@SuppressWarnings("unused")
  private IStateManager smPrx;
	@SuppressWarnings("unused")
  private String lastChildAction;
	@SuppressWarnings("unused")
  private List<String> lastActionDetails;
	private boolean tickleEnabled = false;

	public ChildActionHandler(PCcomponents pCc, IDramaManager dmPrx,
			IActionEngine aePrx, IChildModel cmPrx, IStateManager smPrx,
			IEventListener eventPublisher) 
	{
		super(pCc, dmPrx, aePrx);
		this.eventPublisher = eventPublisher;
		this.cmPrx = cmPrx;
		this.smPrx = smPrx;
		agentTargetObject = "";
		lastChildAction = "";
	}

	/**
	 * Sets the object that the agent has chosen as the target, when a specific
	 * request is made
	 */
	public void setTargetObject(String objId) {
		this.agentTargetObject = objId;
		System.out.println("target object id set to: " + objId);
		// Also tell the child model, because it's important for engagement
		// estimation
		cmPrx.setTargetObject(objId);
	}
	
	public void setTickle(boolean tickleEnabled) {
		this.tickleEnabled = tickleEnabled;
	}

	/**
	 * Called when the child has performed an action.
	 * 
	 * This only corresponds to an action on an object. Other kind of actions
	 * are of interest to the CM and can influence the PC by changing CM
	 * variables that are then communicated to the PC.
	 * 
	 * @param agentInWorld
	 * @param childAction
	 * @throws StringLabelException
	 */
	public String interpretChildAction(boolean agentInWorld,
			String childAction, String targetObjId) {
		System.out.println("interpreting child action: " + childAction
				+ " with targetObjId: " + targetObjId);
		System.out.println("was last agent action a bid? "
				+ getPCcs().agentH.getWasAgentActionBid() + ", last bid type: "
				+ getPCcs().agentH.getLastBidType() + " current activity: "
				+ getPCcs().agentH.getCurrentActivity());
		if (!agentInWorld) {
			return childAction;
		} else {
			if (childAction.equals(ChildAction.noAction.getName())) {
				return ChildAction.noAction.getName();
			} else if ((getPCcs().agentH.getCurrentActivity() == EchoesActivity.TickleAndTree || tickleEnabled)
					&& childAction.equals(ChildAction.userTouchedAgent
							.getName())) {
				return ChildAction.userTouchedAgent.getName();
			} else if (getPCcs().agentH.getWasAgentActionBid()) {
				if (getPCcs().agentH.getLastBidType().equals("pointBidType")
						|| getPCcs().agentH.getLastBidType().equals(
								"verbalBidType")
						|| getPCcs().agentH.getLastBidType().equals(
								"lookBidType")
						|| getPCcs().agentH.getLastBidType().equals(
								"touchBidType")) {
					if (getPCcs().agentH.getCurrentActivity() == EchoesActivity.CloudRain
							&& childAction.equals(ChildAction.cloud_rain
									.getName())) {
						return ChildAction.respondedToBid.getName();
					} else if (getPCcs().agentH.getCurrentActivity() == EchoesActivity.FlowerGrow
							&& childAction.equals(ChildAction.cloud_rain
									.getName())) {
						return ChildAction.respondedToBid.getName();
					} else if (getPCcs().agentH.getCurrentActivity() == EchoesActivity.FlowerPickToBasket
							&& childAction
									.equals(ChildAction.flower_placeInBasket
											.getName())) {
						if (agentTargetObject.equals(targetObjId)) {
							return ChildAction.respondedToBid.getName();
						} else {
							return ChildAction.respondedDiffObject.getName();
						}
					} else if (getPCcs().agentH.getCurrentActivity() == EchoesActivity.PotStackRetrieveObject
							&& childAction.equals(ChildAction.stack_pot
									.getName())) {
						return ChildAction.respondedToBid.getName();
					} else if (getPCcs().agentH.getCurrentActivity() == EchoesActivity.FlowerTurnToBall
							&& childAction.equals(ChildAction.flower_ball
									.getName())) {
						return ChildAction.respondedToBid.getName();
					} else if (getPCcs().agentH.getCurrentActivity() == EchoesActivity.BallThrowing
							&& childAction.equals(ChildAction.cloud_ball
									.getName())) {
						return ChildAction.respondedToBid.getName();
					} else if (getPCcs().agentH.getCurrentActivity() == EchoesActivity.BallSorting
							&& childAction.equals(ChildAction.container_ball
									.getName())) {
						return ChildAction.respondedToBid.getName();
					}
					// check for request bids
					if (getPCcs().agentH.getLastBidPurpose().equals(
							"requestObject")) {
						if (getPCcs().agentH.getLastObjectType().equals(
								EchoesObjectType.Flower.toString())
								&& childAction.equals(ChildAction.gave_flower
										.getName())) {
							return ChildAction.gaveRequestedObject.getName();
						} else if (getPCcs().agentH.getLastObjectType().equals(
								EchoesObjectType.Pot.toString())
								&& childAction
										.equals(ChildAction.gave_flowerpot
												.getName())) {
							return ChildAction.gaveRequestedObject.getName();
						} else if (getPCcs().agentH.getLastObjectType().equals(
								EchoesObjectType.Basket.toString())
								&& childAction.equals(ChildAction.gave_basket
										.getName())) {
							return ChildAction.gaveRequestedObject.getName();
						} else if (getPCcs().agentH.getLastObjectType().equals(
								EchoesObjectType.Ball.toString())
								&& childAction.equals(ChildAction.gave_ball
										.getName())) {
							return ChildAction.gaveRequestedObject.getName();
						}
					}
				}
			} else {
				if ((getPCcs().agentH.getCurrentActivity() == EchoesActivity.CloudRain && childAction
						.equals(ChildAction.cloud_rain.getName()))
						|| (getPCcs().agentH.getCurrentActivity() == EchoesActivity.FlowerGrow && childAction
								.equals(ChildAction.cloud_rain.getName()))
						|| (getPCcs().agentH.getCurrentActivity() == EchoesActivity.FlowerPickToBasket && childAction
								.equals(ChildAction.flower_placeInBasket
										.getName()))
						|| (getPCcs().agentH.getCurrentActivity() == EchoesActivity.PotStackRetrieveObject && childAction
								.equals(ChildAction.stack_pot.getName()))
						|| (getPCcs().agentH.getCurrentActivity() == EchoesActivity.FlowerTurnToBall && childAction
								.equals(ChildAction.flower_ball.getName()))
						|| (getPCcs().agentH.getCurrentActivity() == EchoesActivity.BallThrowing && childAction
								.equals(ChildAction.cloud_ball.getName()))
						|| (getPCcs().agentH.getCurrentActivity() == EchoesActivity.BallSorting && childAction
								.equals(ChildAction.container_ball.getName()))) {
					return ChildAction.activityRelevantAction.getName();
				}
			}

			if ((childAction.equals(ChildAction.gave_flowerpot.getName()) && getPCcs().agentH
					.getCurrentActivity() == EchoesActivity.PotStackRetrieveObject)
					// || (childAction.equals(ChildAction.gave_flower.getName())
					// && getPCcs().agentH
					// .getCurrentActivity() ==
					// EchoesActivity.FlowerPickToBasket)
					|| (childAction
							.equals(ChildAction.gave_flowerpot.getName()) && getPCcs().agentH
							.getCurrentActivity() == EchoesActivity.FlowerGrow)
					|| (childAction.equals(ChildAction.gave_flower.getName()) && getPCcs().agentH
							.getCurrentActivity() == EchoesActivity.FlowerGrow)
					|| (childAction.equals(ChildAction.gave_flower.getName()) && getPCcs().agentH
							.getCurrentActivity() == EchoesActivity.FlowerTurnToBall)
					// && getPCcs().agentH.checkPotWithoutFlower()
					|| (childAction.equals(ChildAction.gave_ball.getName()) && getPCcs().agentH
							.getCurrentActivity() == EchoesActivity.BallThrowing)
					|| (childAction.equals(ChildAction.gave_ball.getName()) && getPCcs().agentH
							.getCurrentActivity() == EchoesActivity.BallSorting)

					|| (childAction.equals(ChildAction.gave_ball.getName()) && getPCcs().agentH
							.getCurrentActivity() == EchoesActivity.ExploreWithAgent)
					|| (childAction.equals(ChildAction.gave_flower.getName()) && getPCcs().agentH
							.getCurrentActivity() == EchoesActivity.ExploreWithAgent)
					|| (childAction
							.equals(ChildAction.gave_flowerpot.getName()) && getPCcs().agentH
							.getCurrentActivity() == EchoesActivity.ExploreWithAgent)) {
				return ChildAction.gaveObjectRelevantActivity.getName();
			} else if (childAction.equals(ChildAction.gave_flower.getName())
					|| childAction.equals(ChildAction.gave_ball.getName())
					|| childAction.equals(ChildAction.gave_flowerpot.getName())
					|| childAction.equals(ChildAction.gave_basket.getName())) {
				return ChildAction.offeredObject.getName();
			} else {
				// not of interest to pc what the child did if not in response
				// to a bid
				// the CM will inform the PC if the agent needs to change focus
				return ChildAction.irrelevantAction.getName();
			}
		}
	}

	/**
	 * Called by the agentListener.
	 * 
	 * NOTE need to add the recognisable child actions in the agentListener
	 * method!
	 * 
	 * @param childAction
	 */
	public void handleChildAction(String childAction, List<String> details) {
		System.out.println("action published: " + childAction);
		if (childAction.equals(ChildAction.noAction.getName())) {
			getPCcs().agentH.respondToChildAction(ChildAction.noAction
					.getName());
		} else if (childAction.equals(ChildAction.stack_pot.getName())
				|| childAction.equals(ChildAction.cloud_rain.getName())
				|| childAction.equals(ChildAction.flower_grow.getName())
				|| childAction.equals(ChildAction.flower_ball.getName())
				|| childAction.equals(ChildAction.flower_bubble.getName())
				|| childAction.equals(ChildAction.touch_leaves.getName())
				|| childAction.equals(ChildAction.cloud_ball.getName())
				// this is called from the RE listener in the
				// agentbehaviourhandler
				|| childAction.equals(ChildAction.userTouchedAgent.getName())
				|| childAction.equals(ChildAction.flower_placeInBasket
						.getName())
				|| childAction.equals(ChildAction.container_ball.getName())) {
			if (childAction.equals(ChildAction.flower_placeInBasket.getName()))
				handleChildAction(getPCcs().agentH.getIsAgentInWorld(),
						childAction, details.get(1));
			else
				handleChildAction(getPCcs().agentH.getIsAgentInWorld(),
						childAction, details.get(0));
			lastChildAction = childAction;
			lastActionDetails = details;
			/*
			 * } else if (childAction.equals("drag")) { String objId =
			 * details.get(0); if (lastChildAction != null &&
			 * lastActionDetails.get(0).equals(objId) &&
			 * !lastChildAction.equals("drag")) {
			 * Application.communicator().getLogger().trace( "info",
			 * "Ignoring drag event on " + objId +
			 * " because the last action was " + lastChildAction +
			 * " with the same arg"); return; } // Did they just drag an object
			 * that is now "near" the agent? Map<String, String> props =
			 * smPrx.getProperties(objId);
			 * Application.communicator().getLogger().trace("info",
			 * "Properties of dragged object: " + props); if
			 * (Boolean.valueOf(props.get("nearAgent"))) {
			 * Application.communicator().getLogger().trace("info",
			 * "Dragged object is near agent!"); // TODO turn towards the
			 * offered object -- here or in the // response? String type =
			 * props.get("type"); if (type.equals("Pot")) {
			 * handleChildAction(getPCcs().agentH.getIsAgentInWorld(),
			 * ChildAction.gave_flowerpot.getName(), objId); } else if
			 * (type.equals("Flower")) {
			 * handleChildAction(getPCcs().agentH.getIsAgentInWorld(),
			 * ChildAction.gave_flower.getName(), objId); } else if
			 * (type.equals("Ball")) {
			 * handleChildAction(getPCcs().agentH.getIsAgentInWorld(),
			 * ChildAction.gave_ball.getName(), objId); } else if
			 * (type.equals("Basket")) {
			 * handleChildAction(getPCcs().agentH.getIsAgentInWorld(),
			 * ChildAction.gave_basket.getName(), objId); } else {
			 * Application.communicator().getLogger().warning(
			 * "Child offered a " + type + "; no action constant to handle it");
			 * } }
			 */
		} else {
			System.out
					.println("user action not recognised so ignoring user action: "
							+ childAction);
		}
	}

	/**
	 * Called when a child action has been performed. The child action is at the
	 * level of performing an action on an object (not just touching the screen
	 * or touching an object - that info is useful to the child model).
	 * 
	 * @param agentInWorld
	 * @param childAction
	 */
	public void handleChildAction(boolean agentInWorld, String childAction,
			String targetObjId) {
		EchoesScene currentScene = dmPrx.getCurrentScene();
		System.out.println("Current scene is: " + dmPrx.getCurrentScene());
		if (!SceneDetails.agentCanEnter(currentScene)) {
			getPCcs().nonAgentSceneH.respondToChildAction(interpretChildAction(
					false, childAction, targetObjId));
			System.out
					.println("Non agent scene - child action interpretation: "
							+ interpretChildAction(false, childAction,
									targetObjId));
		} else {
			// here if system will be reasoning about whether and when to
			// involve the agent re-add code to decide this
			// at the moment only process the child action if there is an agent
			// in the world
			if (getPCcs().agentH.getIsAgentInWorld()) {

				// direct to agentBehaviourHander and inform whether there
				// is agent in world

				// here must get last agent action and interpret the child's
				// action
				String interpretedChildAction = interpretChildAction(
						agentInWorld, childAction, targetObjId);

				System.out
						.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ interpreted child action is: "
								+ interpretedChildAction);
				// then publish whether it's a response in the eventPublisher so
				// that the AE can give feedback
				if (interpretedChildAction.equals(ChildAction.respondedToBid
						.getName())
				// || interpretedChildAction
				// .equals(ChildAction.respondedDiffObject
				// .getName())
				) {
					eventPublisher
							.userAction(UserActionType.UserRespondedToBid);
				} else if (interpretedChildAction
						.equals(ChildAction.activityRelevantAction.getName())) {
					eventPublisher
							.userAction(UserActionType.UserActivityRelevantAction);
				} else if (interpretedChildAction.equals(ChildAction.noAction
						.getName())) {
					eventPublisher.userAction(UserActionType.UserNoAction);
					// } else if (interpretedChildAction
					// .equals(ChildAction.initiatedBid.getName())) {
					// eventPublisher.userAction(UserActionType.UserInitiated);
				} else if (interpretedChildAction
						.equals(ChildAction.gaveRequestedObject.getName())) {
					eventPublisher
							.userAction(UserActionType.UserGaveRequestedObject);
				} else if (
				// interpretedChildAction
				// .equals(ChildAction.gaveDiffObject.getName())
				// ||
				interpretedChildAction.equals(ChildAction.offeredObject
						.getName())) {
					eventPublisher
							.userAction(UserActionType.UserGaveUnrequestedObject);
					// and unrelated action
					// do nothing for the moment
				} else if (interpretedChildAction
						.equals(ChildAction.userTouchedAgent.getName())) {
					eventPublisher.userAction(UserActionType.UserTouchedAgent);
				} else if (interpretedChildAction
						.equals(ChildAction.respondedDiffObject.getName())) {
					System.out
							.println("this should only happen in flower picking - child picked flower not indicated by andy");
					getPCcs().agentH.setAgentGoal("dontWait");
				} else {
					System.out.println("child action not recognised");
				}
				System.out
						.println("Agent scene, agent present, respond to child action: child action interpretation: "
								+ interpretChildAction(agentInWorld,
										childAction, targetObjId));
				getPCcs().agentH.respondToChildAction(interpretedChildAction);
			}
		}
	}
}
