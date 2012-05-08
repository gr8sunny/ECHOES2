package echoesEngine;


import java.util.List;
import FAtiMA.knowledgeBase.KnowledgeBase;
import FAtiMA.wellFormedNames.Name;
import FAtiMA.wellFormedNames.Substitution;
import FAtiMA.wellFormedNames.SubstitutionSet;
import utils.Enums.*;
import utils.Interfaces.*;
import static echoesEngine.KbUtilities.*;

public class ActionEngine implements IActionEngine 
{
	@SuppressWarnings("unused")
	private IStateManager stateManager;
	private IRenderingEngine rePrx;

	public ActionEngine(IStateManager stateManager, IRenderingEngine rePrx) 
	{
		this.stateManager = stateManager;
		this.rePrx = rePrx;
	}

	/*
	 * @see echoes._ActionEngineOperations#setGoal(java.lang.String,
	 * java.lang.String, Ice.Current)
	 */
	public void setGoal(String goal) {
		System.out.println("set goal " + goal);

		if (goal.equals("enterECHOES")) {
			kbTell("enterECHOES()", true);
			kbTell("wait()", false);
			kbTell("pcAgentWait()", false);
            // disable other goals that may still be active due to events before
            // the activity ended
			kbTell("makeBid()", false);
			kbTell("noticeEvent()", false);
			kbTell("reactToEvent()", false);
			kbTell("reengage()", false);
		} else if (goal.equals("wait")) {
			kbTell("wait()", true);
			kbTell("pcAgentWait()", true);
		} else if (goal.equals("dontWait")) {
			kbTell("wait()", false);
			kbTell("pcAgentWait()", false);
		} else if (goal.equals("reengage")) {
			kbTell("reengage()", true);
		} else if (goal.equals("leave")) {
        	kbTell("leave()", true);
        	kbTell("wait()", false);
        	kbTell("pcAgentWait()", false);
            // disable other goals that may still be active due to events before
            // the activity ended
        	kbTell("makeBid()", false);
        	kbTell("noticeEvent()", false);
        	kbTell("reactToEvent()", false);
        	kbTell("enterECHOES()", false);
        	kbTell("noticedChild()", false);
        	kbTell("explore()", false);

        	// also set activity to null!!!! so agent does not respond to child
        	// actions based on ended activity
        	setChosenActivity("");
        } else if (goal.equals("walkOff")) {
        	kbTell("walkOff()", true);
        	kbTell("wait()", false);
        	kbTell("pcAgentWait()", false);
            // disable other goals that may still be active due to events before
            // the activity ended
        	kbTell("makeBid()", false);
        	kbTell("noticeEvent()", false);
        	kbTell("reactToEvent()", false);
        	kbTell("enterECHOES()", false);
        	kbTell("noticedChild()", false);
        	kbTell("explore()", false);

        	// also set activity to null!!!! so agent does not respond to child
        	// actions based on ended activity
        	setChosenActivity("");
        } else if (goal.equals("getOutOfTheWay")) {
        	kbTell("move()", true);
        } else if (goal.equals("giveFeedbackEndOfActivity")) {
        	kbTell("activityEnded()", true);
        	kbTell("wait()", false);
        	kbTell("pcAgentWait()", false);
            // disable other goals that may still be active due to events before
            // the activity ended
        	kbTell("makeBid()", false);
        	kbTell("noticeEvent()", false);
        	kbTell("reactToEvent()", false);
        	kbTell("childOfferedObject", false);

        } else if (goal.equals("makeBid")) {
        	kbTell("makeBid()", true);
        	kbTell("wait()", false);
        	kbTell("pcAgentWait()", false);
        }
        else if (goal.equals("popBubble")) {
        	kbTell("popBubble", true);
        } else if (goal.equals("acceptObject")) {
        	kbTell("childOfferedObject", true);

        	// reset pick up action flags
        	kbTell("pickedUpPotToPutDown()", false);
        	kbTell("pickedUpFlowerToPutInPot()", false);
        	kbTell("pickedUpFlower()", false);
        	kbTell("pickedUpBasket()", false);
        	kbTell("pickedUpPot()", false);
        	kbTell("acceptBall()", false);
        } else if (goal.equals("beExpressiveGeneral")) {
        	kbTell("reactToGeneralEvent()", true);
        } else {
        	kbTell("interactWithChild()", false);
        }
	}

    public void resetGoal(String goalName) {
		if (goalName.equals("popBubble")) {
			kbTell("popBubble", false);
		}
	}

	/**
	 * lookOnly - sets whether to just notice by looking or to react to (show
	 * excitement and say wow!) event - is the event to react to, this is the
	 * String with the name of the object animation shareWithChild - sets
	 * whether to share the event/action by pointing/looking at the object/child
	 * after reacting to it
	 * 
	 */
	public void setReactToEvent(boolean lookOnly, String event, String objId,
			boolean shareWithChild) {
		// if the agent is not in echoes then don't react to an event. Need this
		// because the agent is added before he is set to enter ECHOES, so he'll
		// be reacting and talking when he's not in the scene.
	    if (kbAskTrue("agentIsInEchoes()")) {

			// if the agent is already set to react to something then ignore the
			// new event to notice/react to. Otherwise he reacts to the wrong
			// event/object.
            if (kbAskTrue("reactToEvent()")) {
				// do nothing
			} else {
				System.out.println("=================== object focus set to object id: " + objId);

				// reset (objectFocus) property of all objects to False
				resetBindings("(objectFocus)");

				// reset (event) to react to
				resetBindings("(event)");

				if (lookOnly) {
					System.out.println("look don't react");
					kbTell(objId + "(objectFocus)", true);
					kbTell("noticeEvent()", true);
				} else {
					System.out.println("don't only look also react" + objId);
					kbTell(objId + "(objectFocus)", true);
					kbTell("noticeEvent()", true);
					kbTell("reactToEvent()", true);
					kbTell(event + "(event)", "Occurred");
					kbTell("shareEventWithChild()", shareWithChild);
				}
			}
		}
	}

    /*
     * (non-Javadoc)
     * 
     * @see echoes._ActionEngineOperations#setTarget(java.lang.String,
     * Ice.Current)
     */
    public void setTarget(String objectId) {
        // remove existing bindings - to override previous setting
        resetGroundBindings("(objectIsTarget)");
        // set the new one
        kbTell(objectId + "(objectIsTarget)", true);
    }

    public void setBidRepeat(String bidRepeat) {
        // remove existing bindings - to override previous setting
        resetGroundBindings("(isChosenBidRepeat)");
        // set the new one
        kbTell(bidRepeat + "(isChosenBidRepeat)", true);
    }

    public void setBidType(String bid) {
        // remove existing bindings - to override previous setting
        resetGroundBindings("(isChosenBidType)");
        // set the new one
        kbTell(bid + "(isChosenBidType)", true);
    }

    public void setChosenActivity(String activity) {
        // remove existing bindings - to override previous setting
        resetGroundBindings("(isChosenActivity)");

        // reset need to introduce activity
        kbTell("lookedAroundMadeComment(" + activity + ")", false);
        kbTell("suggestedPotFlowerGrowing()", false);

        System.out.println("setting chosen activity to: " + activity);
        // set the new one
        kbTell(activity + "(isChosenActivity)", true);
        // satisfy the goal to make pots available
        if (! activity.equals("FlowerGrow")) {
            kbTell("madeAPotAvailable()", true);
        }

        // reset need to say my turn so that he doesn't do it the first time
        kbTell("indicatedTurn()", true);
    }

    public void setBidPurpose(String purpose) {
        // remove existing bindings - to override previous setting
        resetGroundBindings("(isChosenPurpose)");
        // set the new one
        kbTell(purpose + "(isChosenPurpose)", true);
    }

	
	public void resetGoalSuccessConditions() {
		// for all goals
	    kbTell("interactWithChild()", false);
	}
	
	public void activityEnded() {
		kbTell("activityEnded", true);
	}
    
    public String getTargetObject(EchoesObjectType objectType) {
        List <SubstitutionSet> bindings = kbGetBindings("(type)");

        if (objectType == EchoesObjectType.Pot) {
            if (kbAskTrue("PotStackRetrieveObject(isChosenActivity)")) {
                for (SubstitutionSet subSet : bindings) {
                    for (Object object : subSet.GetSubstitutions()) {
                        Substitution sub = (Substitution) object;
                        String name = sub.getValue().getName();
                        if (kbAskEqual(name + "(type)", "Pot")) {
                            if (! kbAskTrue(name + "(isStacked)")) {
                                return name;
                            }
                        }
                    }
                }
            } else if (kbAskTrue("FlowerGrow(isChosenActivity)")) {
                for (SubstitutionSet subSet : bindings) {
                    for (Object object : subSet.GetSubstitutions()) {
                        Substitution sub = (Substitution) object;
                        String name = sub.getValue().getName();
                        if (kbAskEqual(name + "(type)", "Pot")) {
                            System.out.println("choosing target pot. hasObject is: "
                                               + kbAsk(name + "(hasObject)")
                                               + " and hasFlower is: "
                                               + kbAsk(name + "(hasFlower)")
                                               + " for target: "
                                               + name);
                            boolean hasObject = kbAskTrue(name + "(hasObject)");
                            boolean hasFlower = kbAskTrue(name + "(hasFlower)");
                            if (! hasObject && ! hasFlower) {
                                return name;
                            }
                        }
                    }
                }
            }
        } else if (objectType == EchoesObjectType.Flower) {
            // if the activity is flower picking
            if (kbAskTrue("FlowerPickToBasket(isChosenActivity)")) {
                for (SubstitutionSet subSet : bindings) {
                    for (Object object : subSet.GetSubstitutions()) {
                        Substitution sub = (Substitution) object;
                        String name = sub.getValue().getName();
                        if (kbAskEqual(name + "(type)", "Flower")) {
                            if (! kbAskTrue(name + "(isInBasket)")) {
                                return name;
                            }
                        }
                    }
                }
            } else if (kbAskTrue("FlowerTurnToBall(isChosenActivity)")) {
                for (SubstitutionSet subSet : bindings) {
                    for (Object object : subSet.GetSubstitutions()) {
                        Substitution sub = (Substitution) object;
                        String name = sub.getValue().getName();
                        Object type = kbAsk(name + "(type)");
                        if (type != null) {
                            System.out.println("sdfsdfsdfs: " + type);
                        }
                        if (kbAskEqual(name + "(type)", "Flower")) {
                            if (! kbAskTrue(name + "(toBeRemoved)")) {
                                return name;
                            }
                        }
                    }
                }
            }
        } else if (objectType == EchoesObjectType.Cloud) {
            for (SubstitutionSet subSet : bindings) {
                for (Object object : subSet.GetSubstitutions()) {
                    Substitution sub = (Substitution) object;
                    String name = sub.getValue().getName();
                    if (kbAskEqual(name + "(type)", "Cloud")) {
                        return name;
                    }
                }
            }
        } else if (objectType == EchoesObjectType.Basket) {
            for (SubstitutionSet subSet : bindings) {
                for (Object object : subSet.GetSubstitutions()) {
                    Substitution sub = (Substitution) object;
                    String name = sub.getValue().getName();
                    if (kbAskEqual(name + "(type)", "Basket")) {
                        return name;
                    }
                }
            }
        } else if (objectType == EchoesObjectType.Ball) {
            if (kbAskTrue("BallThrowing(isChosenActivity)")) {
                for (SubstitutionSet subSet : bindings) {
                    for (Object object : subSet.GetSubstitutions()) {
                        Substitution sub = (Substitution) object;
                        String name = sub.getValue().getName();
                        if (kbAskEqual(name + "(type)", "Ball")) {
                            Object colour = kbAsk(name + "(ball_colour)");
                            if (colour == null || colour.equals("red")) {
                                return name;
                            }
                        }
                    }
                }
            } else if (kbAskTrue("BallSorting(isChosenActivity)")) {
                for (SubstitutionSet subSet : bindings) {
                    for (Object object : subSet.GetSubstitutions()) {
                        Substitution sub = (Substitution) object;
                        String name = sub.getValue().getName();
                        if (kbAskEqual(name + "(type)", "Ball")) {
                            System.out.println("JJJJJJJJJJJJJJJJJJJJJ");
                            Object container = kbAsk(name + "(ball_container)");
                            if (container == null || container.equals("None")) {
                                System.out.println("KKKKKKKKKKKKKKKKKKKKKKKKKKKK");
                                System.out.println("ball_container value for: "
                                                   + name
                                                   + " is: "
                                                   + container);
                                return name;
                            }
                        }
                    }
                }
            }
        }
        return "";
    }

	/**
	 * sets _canAct back to True and clears the actions
	 * 
	 * Not sure this will work.
	 * 
	 */
	
	public void resetAgentPlan() {
		// reset _canAct - changing the following KB value is picked up in the
		// planner, so that this class doesn't need to have access to the
		// planner directly
	    kbTell("reset(canAct)", true);
	}

	
	public void cancelAllGoals() {
		kbTell("interactWithChild()", false);
		kbTell("giveFeedback()", false);
		kbTell("activityEnded()", false);
		kbTell("childOfferedObject()", false);
		kbTell("reengage()", false);
		kbTell("leave()", false);
		kbTell("childTouchedAgent()", false);
		kbTell("move()", false);
	}

    
    /**
     * Used to set objId for popping bubbles and accepting objects
     */
    public void setObjectFocus(String objId, String purpose) {
        resetBindings("(bubbleToPop)");
        resetBindings("(objectToAccept)");

        if (purpose.equals("popBubble")) {
            kbTell(objId + "(bubbleToPop)", true);
        } else if (purpose.equals("acceptObject")) {
            kbTell(objId + "(objectToAccept)", true);

            if (kbAskTrue("BallSorting(isChosenActivity)")) {
                String ballColour = (String) kbAsk(objId + "(ball_colour)");
                System.out.println("ball colourL " + ballColour);

                String containerId = "";
                for (SubstitutionSet subSet : kbGetBindings("(type)")) {
                    for (Object object : subSet.GetSubstitutions()) {
                        Substitution sub = (Substitution) object;
                        String name = sub.getValue().getName();
                        if (kbAskEqual(name + "(type)", "Container")) {
                            Object colour = kbAsk(name + "(container_colour)");
                            if (colour != null) {
                                System.out.println("container_colour for: "
                                                   + name
                                                   + " is "
                                                   + colour);
                            }
                            if (colour != null && colour.equals(ballColour)) {
                                System.out.println("ball colourL " + ballColour);
                                containerId = name;
                            }
                        }
                    }
                }

                kbTell(containerId + "(isChosenContainer)", true);
            }
        }
    }

    
    public void setReward(String ballColour) {
        for (SubstitutionSet subSet : kbGetBindings("(type)")) {
            for (Object object : subSet.GetSubstitutions()) {
                Substitution sub = (Substitution) object;
                String name = sub.getValue().getName();
                if (kbAskEqual(name + "(type)", "Container")) {
                    Object containerColour = kbAsk(name + "(container_colour)");
                    if (ballColour != null && ballColour.equals(containerColour)) {
                        if (ballColour.equals("red")) {
                            System.out.println("red reward");
                            rePrx.setObjectProperty(name, "Reward", "Bubbles");
                        } else if (ballColour.equals("yellow")) {
                            System.out.println("yellow reward");
                            rePrx.setObjectProperty(name, "Reward", "Bees");
                        } else if (ballColour.equals("blue")) {
                            System.out.println("blue reward");
                            rePrx.setObjectProperty(name, "Reward", "Fireworks");
                        }
                    }
                }
            }
        }
    }

    
    public void setBallSortingTargets(String ballId, String containerId) {
        System.out.println("OOOOOOOOOOOOOOOOOOOOOOOOOO" + ballId + "   " + containerId);
        resetBindings("(isChosenBall)");
        resetBindings("(isChosenContainer)");

        kbTell(ballId + "(isChosenBall)", true);
        kbTell(containerId + "(isChosenContainer)", true);
    }

	
	public void setChildname(String name) {
		kbTell("childName()", name);
	}

    /**
     * Find all the bindings of the given property and set them to "False".
     * 
     * @param property
     * the property.
     */
    private void resetBindings(String property) {
        for (SubstitutionSet subSet : kbGetBindings(property)) {
            for (Object object : subSet.GetSubstitutions()) {
                Substitution sub = (Substitution) object;
                String name = sub.getValue().getName();
                if (kbAsk(name + property) != null) {
                    kbTell(name + property, false);
                }
            }
        }
    }

    /**
     * Make ground names from the current bindings of the given property and set
     * them to "False".
     * 
     * @param property
     * the property.
     */
    private void resetGroundBindings(String property) {
        for (SubstitutionSet subSet : kbGetBindings(property)) {
            Name tmpName = (Name) Name.ParseName(property).clone();
            tmpName.MakeGround(subSet.GetSubstitutions());
            KnowledgeBase.GetInstance().Tell(tmpName, "False");
            System.out.println("resetting true targets to false: " + tmpName);
        }
    }
}
