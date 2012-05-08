package echoesEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import FAtiMA.wellFormedNames.Substitution;
import FAtiMA.wellFormedNames.SubstitutionSet;
import utils.Enums.EchoesActivity;

public class ActionNameConverter {

    private String reName;
    private LinkedList <String> reArgs;
    // need to keep record of original name and args when setting the perceived
    // EVENT
    private String originalAEname;
    private LinkedList <String> originalAEargs;

    private static final Random GENERATOR = new Random();

    private static final Map <String, String> ACTION_MAP = new HashMap <String, String>();
    static {

        // bidding actions
        // ACTION_MAP.put("SelfRequestObject", "Gesture");
        ACTION_MAP.put("SelfIndicateTakingTurn", "Gesture");
        ACTION_MAP.put("SelfPromptInitiation", "LookAtPoint");
        ACTION_MAP.put("SelfPointBid", "LookAtObject");
        ACTION_MAP.put("SelfVerbalBid", "Gesture");
        ACTION_MAP.put("SelfLookBid", "LookAtObject");
        ACTION_MAP.put("SelfTouchBid", "LookAtObject");

        // react to event and share/don't share with child
        ACTION_MAP.put("SelfReactToEvent", "Gesture");
        ACTION_MAP.put("SelfReactToAndDontShareEvent", "LookAtChild");
        ACTION_MAP.put("SelfReactToAndShareEvent", "LookAtChild");
        ACTION_MAP.put("SelfNoticeEvent", "LookAtObject");
        ACTION_MAP.put("SelfReactToEventGeneral", "Gesture");

        // single actions
        ACTION_MAP.put("SelfWait", "Gesture");
        ACTION_MAP.put("SelfGreetChild", "Gesture");
        ACTION_MAP.put("SelfGiveThumbsUp", "Gesture");
        ACTION_MAP.put("SelfEndActivity", "Gesture");
        ACTION_MAP.put("SelfGiggle", "Gesture");

        // entering actions
        ACTION_MAP.put("SelfWalkInDontNoticeChild", "WalkTo");
        ACTION_MAP.put("SelfWalkInNoticeChild", "WalkTo");

        // look around spot object/make comment on scene
        ACTION_MAP.put("SelfLookAroundSpotObject", "LookAtObject");
        ACTION_MAP.put("SelfLookAroundMakeComment", "Gesture");

        // actions on cloud
        ACTION_MAP.put("SelfRainCloud", "MakeRain");
        ACTION_MAP.put("SelfPutPotUnderCloud", "PutPotDown");
        ACTION_MAP.put("SelfPushCloud", "WalkToObject");
        ACTION_MAP.put("SelfTakePotOffStack", "PutPotDown");
        ACTION_MAP.put("SelfPickUpPotToMakeAvailable", "PickUpPot");

        // actions for flower picking
        ACTION_MAP.put("SelfPutFlowerInBasket", "PutFlowerInBasket");
        ACTION_MAP.put("SelfFindBasket", "PutBasketDown");
        ACTION_MAP.put("SelfPickUpBasket", "PickUpBasket");

        // actions for pot stacking
        ACTION_MAP.put("SelfStackFlowerpot", "StackPot");
        ACTION_MAP.put("SelfRemoveFlowerFromPot", "TouchFlower-Bubble");

        // action for popping bubbles
        ACTION_MAP.put("SelfPopBubble", "LookAtObject");

        // actions to accept objects relevant to activity
        ACTION_MAP.put("SelfAcceptFlower", "PickFlower");
        ACTION_MAP.put("SelfPutAcceptedFLowerInBasket", "PutFlowerInBasket");
        ACTION_MAP.put("SelfStackAcceptedPot", "StackPot");
        ACTION_MAP.put("SelfAcceptPot", "PickUpPot");
        ACTION_MAP.put("SelfPutAcceptedBasketDown", "PutBasketDown");
        ACTION_MAP.put("SelfAcceptBasket", "PickUpBasket");
        ACTION_MAP.put("SelfAcceptPotToPutDown", "PickUpPot");
        ACTION_MAP.put("SelfPutDownAcceptedPot", "PutPotDown");
        ACTION_MAP.put("SelfAcceptFlowerToInPot", "PickFlower");
        ACTION_MAP.put("SelfPutAcceptedFlowerInPot", "PutFlowerInPot");

        ACTION_MAP.put("SelfTurnAcceptedFlowerToBall", "TouchFlower-Ball");
        ACTION_MAP.put("SelfPutAcceptedFLowerDownTurnToBall", "PutFlowerDown");
        ACTION_MAP.put("SelfAcceptFlowerTurnToBall", "PickFlower");

        ACTION_MAP.put("SelfWaitForActionEffect", "LookAtPoint");

        ACTION_MAP.put("SelfWalkTo", "WalkToObject");
        ACTION_MAP.put("SelfPickUpPot", "PickUpPot");
        ACTION_MAP.put("SelfPickUpFlower", "PickFlower");
        ACTION_MAP.put("SelfPutDownObject", "PutPotDown");

        ACTION_MAP.put("SelfThrowBallThroughCloud", "ThrowBall");
        ACTION_MAP.put("SelfPickUpBallToThrow", "PickUpBall");
        ACTION_MAP.put("SelfThrowAcceptedBall", "ThrowBall");
        ACTION_MAP.put("SelfAcceptBall", "PickUpBall");

        ACTION_MAP.put("SelfPutBallInPile", "PutBallIntoContainer");
        ACTION_MAP.put("SelfPickUpBallToSort", "PickUpBall");
        ACTION_MAP.put("SelfSortAcceptedBall", "PutBallIntoContainer");
        ACTION_MAP.put("SelfAcceptBallToSort", "PickUpBall");

        // leaving scene
        ACTION_MAP.put("SelfLeaveScene", "Gesture");
        ACTION_MAP.put("SelfTellChildLeaving", "Gesture");
        ACTION_MAP.put("SelfWalkOut", "WalkTo");
        ACTION_MAP.put("SelfWalkOutEndActivity", "WalkTo");
        ACTION_MAP.put("SelfSayFollowMe", "TurnToChild");

        ACTION_MAP.put("SelfPutFlowerInPot", "PutFlowerInPot");
        ACTION_MAP.put("SelfTurnFlowerToBubble", "TouchFlower-Bubble");
        ACTION_MAP.put("SelfTurnFlowerToBall", "TouchFlower-Ball");
        ACTION_MAP.put("SelfPlaceFlowerPotPond", "PutPotDown");

        ACTION_MAP.put("SelfSayReady", "LookAtChild");

        ACTION_MAP.put("SelfMoveToSide", "WalkTo");
        ACTION_MAP.put("SelfLookAtChildMoveToSide", "LookAtChild");

        // explore with agent actions

        ACTION_MAP.put("SelfBallCloudExploration", "ThrowBall");
        ACTION_MAP.put("SelfBallCloudExplorationAcceptBall", "PickUpBall");
        ACTION_MAP.put("SelfFlowerBallExploration", "TouchFlower-Ball");
        ACTION_MAP.put("SelfFlowerBallExplorationAcceptFlower", "PickFlower");
        ACTION_MAP.put("SelfFlowerBasketExploration", "PutFlowerInBasket");
        ACTION_MAP.put("SelfFlowerBasketExplorationAcceptFlower", "PickFlower");
        ACTION_MAP.put("SelfPotStackExploration", "StackPot");
        ACTION_MAP.put("SelfPotStackExplorationAcceptPot", "PickUpPot");

        ACTION_MAP.put("SelfDummyExploreExplorationWithAgentWait", "Gesture");
        ACTION_MAP.put("SelfDummyExploreTickleAndTreeWait", "Gesture");

    }

    public ActionNameConverter() {
        reName = "";
        reArgs = new LinkedList <String>();
        originalAEname = "";
        originalAEargs = new LinkedList <String>();
    }

    /**
     * This method converts the Action Engine's action name and argument to the
     * Rendering Engine's action names and arguments. So need to look at the
     * action definitions to see which argument is what and select the correct
     * one needed for the Rendering Engine.
     * 
     * @param actionName
     * @param args
     */
    public void convert(String actionName, LinkedList <String> args) {
        System.out.println("converting action: " + actionName + " with arguments: " + args);
        originalAEname = actionName;
        originalAEargs = args;
        reArgs.clear();

        if (actionName.equals("SelfIndicateTakingTurn")) {
            reName = ACTION_MAP.get("SelfIndicateTakingTurn");
            reArgs.add("my_turn");
            reArgs.add("My_turn.wav");
        } else if (actionName.equals("SelfPromptInitiation")) {
            reName = ACTION_MAP.get("SelfPromptInitiation");
            reArgs.add("0");
            reArgs.add("0");
            reArgs.add("-10");
        } else if (actionName.equals("SelfPointBid")
                   || actionName.equals("SelfLookBid")
                   || actionName.equals("SelfTouchBid")) {
            reName = "LookAtObject";
            reArgs.add(args.get(0));
            reArgs.add("hold=1");
            reArgs.add("speed=0.2");

            if (args.get(1).equals("FlowerGrow")) {
                reArgs.add("grow-flower-here.wav");

            } else if (args.get(1).equals("FlowerPickToBasket")) {
                reArgs.add("Pick_this_flower_put_basket.wav");
            } else if (args.get(1).equals("PotStackRetrieveObject")) {
                reArgs.add("Stack_this_pot2.wav");
            } else if (args.get(1).equals("FlowerTurnToBall")) {
                reArgs.add("that_flower.wav");
            } else if (args.get(1).equals("BallThrowing")) {
                reArgs.add("Throw_ball_through_cloud.wav");
            }
        } else if (actionName.equals("SelfVerbalBid")) {
            reName = ACTION_MAP.get("SelfVerbalBid");
            if (args.get(2).equals("bidAction")) {
                if (KbUtilities.kbAskTrue("FlowerPickToBasket(isChosenActivity)")) {
                    // don't indicate 'your turn'
                } else {
                    reArgs.add("your_turn");
                    reArgs.add("hold=1");
                }
            } else if (args.get(2).equals("requestObject")) {
                reArgs.add("request_object");
                reArgs.add("hold=1");
            }

            if (args.get(1).equals("FlowerPickToBasket") && args.get(2).equals("requestObject")) {
                reArgs.add("Give_basket.wav");
            } else {
                if (KbUtilities.kbAskTrue("FlowerPickToBasket(isChosenActivity)")) {
                    // don't indicate 'your turn'
                } else {
                    reArgs.add(getYourTurnArg());
                }
            }

        } else if (actionName.equals("SelfReactToEvent")) {
            reName = ACTION_MAP.get("SelfReactToEvent");
            reArgs.add("excitement");
            int randint = GENERATOR.nextInt(7);
            if (randint == 0) {
                reArgs.add("cool-2.wav");
            } else if (randint == 1) {
                reArgs.add("cool-4.wav");
            } else if (randint == 2) {
                reArgs.add("woah.wav");
            } else if (randint == 3) {
                reArgs.add("cool.wav");
            } else if (randint == 4) {
                reArgs.add("cool-3.wav");
            } else if (randint == 5) {
                reArgs.add("that's-fun.wav");
            } else if (randint == 6) {
                reArgs.add("wow-3.wav");
            }
        } else if (actionName.equals("SelfReactToAndDontShareEvent")) {
            reName = ACTION_MAP.get("SelfReactToAndDontShareEvent");
            // reArgs.add(args.get(0));
        } else if (actionName.equals("SelfReactToAndShareEvent")) {
            reName = ACTION_MAP.get("SelfReactToAndShareEvent");
            // reArgs.add("");
            if (args.get(1).equals("flower_basket")) {
                reArgs.add("Look_flower_in_the_basket.wav");
            } else if (args.get(1).equals("cloud_hitby")) {
                reArgs.add("ball_changed_colour.wav");
            } else {
                reArgs.add("Look_what_I_did.wav");
            }
        } else if (actionName.equals("SelfNoticeEvent")) {
            reName = ACTION_MAP.get("SelfNoticeEvent");
            reArgs.add(args.get(0));
        } else if (actionName.equals("SelfReactToEventGeneral")) {
            reName = ACTION_MAP.get("SelfReactToEventGeneral");
            reArgs.add("excitement");
            int randint = GENERATOR.nextInt(7);
            if (randint == 0) {
                reArgs.add("cool-2.wav");
            } else if (randint == 1) {
                reArgs.add("cool-4.wav");
            } else if (randint == 2) {
                reArgs.add("woah.wav");
            } else if (randint == 3) {
                reArgs.add("cool.wav");
            } else if (randint == 4) {
                reArgs.add("cool-3.wav");
            } else if (randint == 5) {
                reArgs.add("that's-fun.wav");
            } else if (randint == 6) {
                reArgs.add("wow-3.wav");
            }
        } else if (actionName.equals("SelfWait")
                   || actionName.equals("SelfDummyExploreExplorationWithAgentWait")
                   || actionName.equals("SelfDummyExploreTickleAndTreeWait")) {
            reName = ACTION_MAP.get("SelfWait");
            int randint = GENERATOR.nextInt(3);
            if (randint == 0) {
                reArgs.add("shuffle1");
            } else if (randint == 1) {
                reArgs.add("shuffle2");
            } else if (randint == 2) {
                reArgs.add("shuffle3");
            }
            reArgs.add("0,10");
        } else if (actionName.equals("SelfPutFlowerInBasketDummy")) {
            reName = "TurnToChild";
            reArgs.add("");
        } else if (actionName.equals("SelfGreetChild")) {
            reName = ACTION_MAP.get("SelfGreetChild");
            reArgs.add("wave");
            reArgs.add(getGreetingArg());
        } else if (actionName.equals("SelfGiveThumbsUp")) {
            reName = ACTION_MAP.get("SelfGiveThumbsUp");
            reArgs.add("thumbs_up");
            reArgs.add("goodjob.wav");
            reArgs.add("hold=1.5");
        } else if (actionName.equals("SelfEndActivity")) {
            reName = ACTION_MAP.get("SelfEndActivity");
            reArgs.add("all_done1");
            if (args.get(0).equals("FlowerGrow")) {
                reArgs.add("allDone.wav");
            } else if (args.get(0).equals("FlowerPickToBasket")) {
                int randomint2 = GENERATOR.nextInt(2);
                if (randomint2 == 0) {
                    reArgs.add("all-done.wav,we-picked-all-flowers.wav");
                } else if (randomint2 == 1) {
                    reArgs.add("all-done.wav,there-are-no-more-flowers-to-pick.wav");
                }

            } else if (args.get(0).equals("PotStackRetrieveObject")) {
                int randomint3 = GENERATOR.nextInt(2);
                if (randomint3 == 0) {
                    reArgs.add("all-done-2.wav,there-are-no-more-pots.wav");
                } else if (randomint3 == 1) {
                    reArgs.add("all-done-2.wav,have-we-used-all-pots.wav");
                }
            } else if (args.get(0).equals("FlowerTurnToBall")) {
                reArgs.add("all-done-2.wav,no_more_flowers.wav");
            } else if (args.get(0).equals("BallThrowing")) {
                reArgs.add("all-done-2.wav");
            } else if (args.get(0).equals("BallSorting")) {
                reArgs.add("all-done-2.wav");
            }
        } else if (actionName.equals("SelfAcceptFlower")) {
            reName = ACTION_MAP.get("SelfAcceptFlower");
            reArgs.add(args.get(0));
            reArgs.add("WalkTo=True");
            reArgs.add("Thanks_in_the_basket.wav");

        } else if (actionName.equals("SelfPutAcceptedFLowerInBasket")) {
            reName = ACTION_MAP.get("SelfPutAcceptedFLowerInBasket");
            reArgs.add(args.get(1));
            reArgs.add("WalkTo=True");
        } else if (actionName.equals("SelfAcceptBasket")) {
            reName = ACTION_MAP.get("SelfAcceptBasket");
            reArgs.add(args.get(0));
            reArgs.add("WalkTo=True");
            reArgs.add("thank-you-very-much.wav");
        } else if (actionName.equals("SelfPutAcceptedBasketDown")) {
            reName = ACTION_MAP.get("SelfPutAcceptedBasketDown");
            // reArgs.add("WalkTo=" + getAvailablePosition());
        } else if (actionName.equals("SelfAcceptPot")) {
            reName = ACTION_MAP.get("SelfAcceptPot");
            reArgs.add(args.get(0));
            reArgs.add("WalkTo=True");
            reArgs.add("Thanks_will_stack_it.wav");
        } else if (actionName.equals("SelfStackAcceptedPot")) {
            reName = ACTION_MAP.get("SelfStackAcceptedPot");
            reArgs.add(args.get(1));
            reArgs.add("WalkTo=True");
        } else if (actionName.equals("SelfAcceptPotToPutDown")) {
            reName = ACTION_MAP.get("SelfAcceptPotToPutDown");
            reArgs.add(args.get(0));
            reArgs.add("WalkTo=True");
            reArgs.add("thanks.wav");
        } else if (actionName.equals("SelfPutDownAcceptedPot")) {
            reName = ACTION_MAP.get("SelfPutDownAcceptedPot");
            reArgs.add(args.get(0));
            reArgs.add("WalkTo=" + getAvailablePosition());
            reArgs.add("Ill_grow_flower_here.wav");

        } else if (actionName.equals("SelfExploreCloudPushedCloudAcceptedPot")) {
            reName = ACTION_MAP.get("SelfRainCloud");
            reArgs.add(args.get(0));
            reArgs.add("WalkTo=True");
        } else if (actionName.equals("SelfAcceptFlowerToInPot")) {
            reName = ACTION_MAP.get("SelfAcceptFlowerToInPot");
            reArgs.add(args.get(0));
            reArgs.add("WalkTo=True");
        } else if (actionName.equals("SelfPutAcceptedFlowerInPot")) {
            reName = ACTION_MAP.get("SelfPutAcceptedFlowerInPot");
            reArgs.add(args.get(1));
            reArgs.add("WalkTo=True");

        } else if (actionName.equals("SelfTurnAcceptedFlowerToBall")) {
            reName = ACTION_MAP.get("SelfTurnAcceptedFlowerToBall");
            reArgs.add(args.get(0));
        } else if (actionName.equals("SelfPutAcceptedFLowerDownTurnToBall")) {
            reName = ACTION_MAP.get("SelfPutAcceptedFLowerDownTurnToBall");
            reArgs.add(args.get(0));
            reArgs.add("WalkTo=" + getAvailablePosition());
        } else if (actionName.equals("SelfAcceptFlowerTurnToBall")) {
            reName = ACTION_MAP.get("SelfAcceptFlowerTurnToBall");
            reArgs.add(args.get(0));
            reArgs.add("WalkTo=True");
            reArgs.add("Thanks_will_turn_into_ball.wav");

        } else if (actionName.equals("SelfPutBallInPile")) {
            reName = ACTION_MAP.get("SelfPutBallInPile");
            reArgs.add(args.get(0));
            reArgs.add("WalkTo=True");
        } else if (actionName.equals("SelfPickUpBallToSort")) {
            reName = ACTION_MAP.get("SelfPickUpBallToSort");
            reArgs.add(args.get(0));
            reArgs.add("WalkTo=True");

        } else if (actionName.equals("SelfSortAcceptedBall")) {
            reName = ACTION_MAP.get("SelfSortAcceptedBall");
            reArgs.add(args.get(0));
            reArgs.add("WalkTo=True");
        } else if (actionName.equals("SelfAcceptBallToSort")) {
            reName = ACTION_MAP.get("SelfAcceptBallToSort");
            reArgs.add(args.get(0));
            reArgs.add("WalkTo=True");
            reArgs.add("Thanks_in_the_box.wav");

        } else if (actionName.equals("SelfGiggle")) {
            reName = ACTION_MAP.get("SelfGiggle");
            reArgs.add("giggle");
            reArgs.add("that_tickles.wav");

        } else if (actionName.equals("SelfWalkInDontNoticeChild")) {
            reName = ACTION_MAP.get("SelfWalkInDontNoticeChild");
            reArgs.add("0");
            reArgs.add("-0.5");
            // reArgs.add("0,10");
        } else if (actionName.equals("SelfWalkInNoticeChild")) {
            reName = ACTION_MAP.get("SelfWalkInNoticeChild");
            reArgs.add("0");
            reArgs.add("-0.5");
            reArgs.add("0,5");

        } else if (actionName.equals("SelfLookAroundSpotObject")) {
            reName = ACTION_MAP.get("SelfLookAroundSpotObject");
            reArgs.add(args.get(0));

        } else if (actionName.equals("SelfLookAroundMakeComment")) {
            reName = ACTION_MAP.get("SelfLookAroundMakeComment");
            reArgs.add("looking_around_floor");
            if (args.get(0).equals(EchoesActivity.CloudRain.toString())) {
                reArgs.add("Look_a_cloud.wav");
            } else if (args.get(0).equals(EchoesActivity.FlowerGrow.toString())) {
                reArgs.add("I_know_grow_flowers.wav");
            } else if (args.get(0).equals(EchoesActivity.FlowerPickToBasket.toString())) {
                reArgs.add("Let's_pick_flowers.wav");
            } else if (args.get(0).equals(EchoesActivity.PotStackRetrieveObject.toString())) {
                reArgs.add("Lets_tidy_pots.wav");
            } else if (args.get(0).equals(EchoesActivity.FlowerTurnToBall.toString())) {
                reArgs.add("balls_from_flower.wav");
            } else if (args.get(0).equals(EchoesActivity.BallThrowing.toString())) {
                reArgs.add("throw_balls_through_cloud.wav");
            } else if (args.get(0).equals(EchoesActivity.BallSorting.toString())) {
                reArgs.add("Tidy_balls_into_boxes.wav");
            }
        } else if (actionName.equals("SelfExploreCloud")
                   || actionName.equals("SelfExploreCloudPushedCloud")) {
            reName = ACTION_MAP.get("SelfRainCloud");
            reArgs.add(args.get(0));
            reArgs.add("WalkTo=True");

        } else if (actionName.equals("SelfPushCloud")) {
            reName = ACTION_MAP.get("SelfPushCloud");
            reArgs.add(args.get(1));
        } else if (actionName.equals("SelfTakePotOffStack")) {
            reName = ACTION_MAP.get("SelfTakePotOffStack");
            reArgs.add("WalkTo=" + getAvailablePosition());
        } else if (actionName.equals("SelfPickUpPotToMakeAvailable")) {
            reName = ACTION_MAP.get("SelfPickUpPotToMakeAvailable");
            reArgs.add(args.get(0));

        } else if (actionName.equals("SelfPutFlowerInBasket")) {
            reName = ACTION_MAP.get("SelfPutFlowerInBasket");
            reArgs.add(args.get(1));
            reArgs.add("WalkTo=True");
        } else if (actionName.equals("SelfFindBasket")) {
            reName = ACTION_MAP.get("SelfFindBasket");
            reArgs.add(args.get(0));
            reArgs.add("WalkTo=" + getAvailablePosition());
        } else if (actionName.equals("SelfPickUpBasket")) {
            reName = ACTION_MAP.get("SelfPickUpBasket");
            reArgs.add(args.get(0));
            reArgs.add("WalkTo=True");
        } else if (actionName.equals("SelfStackFlowerpot")) {
            reName = ACTION_MAP.get("SelfStackFlowerpot");
            reArgs.add(args.get(1));
            reArgs.add("WalkTo=True");
        } else if (actionName.equals("SelfRemoveFlowerFromPot")) {
            reName = ACTION_MAP.get("SelfRemoveFlowerFromPot");
            reArgs.add(args.get(0));

        } else if (actionName.equals("SelfWalkTo")) {
            reName = ACTION_MAP.get("SelfWalkTo");
            reArgs.add(args.get(0));
        } else if (actionName.equals("SelfPickUpPot")) {
            reName = ACTION_MAP.get("SelfPickUpPot");
            reArgs.add(args.get(0));

        } else if (actionName.equals("SelfPickUpFlower")) {
            reName = ACTION_MAP.get("SelfPickUpFlower");
            reArgs.add(args.get(0));

        } else if (actionName.equals("SelfPopBubble")) {
            reName = ACTION_MAP.get("SelfPopBubble");
            reArgs.add(args.get(0));

        } else if (actionName.equals("SelfTurnFlowerToBubble")) {
            reName = ACTION_MAP.get("SelfTurnFlowerToBubble");
            reArgs.add(args.get(0));
        } else if (actionName.equals("SelfTurnFlowerToBall")) {
            reName = ACTION_MAP.get("SelfTurnFlowerToBall");
            reArgs.add(args.get(0));
        } else if (actionName.equals("SelfRainCloud")) {
            reName = ACTION_MAP.get("SelfRainCloud");
            reArgs.add(args.get(0));

        } else if (actionName.equals("SelfPutFlowerInPot")) {
            reName = ACTION_MAP.get("SelfPutFlowerInPot");
            reArgs.add(args.get(1));

        } else if (actionName.equals("SelfThrowBallThroughCloud")) {
            reName = ACTION_MAP.get("SelfThrowBallThroughCloud");
            reArgs.add(args.get(0));
        } else if (actionName.equals("SelfPickUpBallToThrow")) {
            reName = ACTION_MAP.get("SelfPickUpBallToThrow");
            reArgs.add(args.get(0));
            reArgs.add("WalkTo=True");
        } else if (actionName.equals("SelfThrowAcceptedBall")) {
            reName = ACTION_MAP.get("SelfThrowAcceptedBall");
            reArgs.add(args.get(0));
        } else if (actionName.equals("SelfAcceptBall")) {
            reName = ACTION_MAP.get("SelfAcceptBall");
            reArgs.add(args.get(0));
            reArgs.add("WalkTo=True");
            reArgs.add("Thanks_throw_through_cloud.wav");

        } else if (actionName.equals("SelfPlaceFlowerPotPond")) {
            reName = ACTION_MAP.get("SelfPlaceFlowerPotPond");
            reArgs.add(args.get(0));
        } else if (actionName.equals("SelfPutDownObject")) {
            reName = ACTION_MAP.get("SelfPutDownObject");
            reArgs.add(args.get(0));

        } else if (actionName.equals("SelfJustPerformAction")) {
            reName = ACTION_MAP.get("SelfJustPerformAction");
            reArgs.add("");

        } else if (actionName.equals("SelfSayReady")) {
            reName = ACTION_MAP.get("SelfSayReady");
            reArgs.add("ready.wav");
        } else if (actionName.equals("SelfLeaveScene")) {
            reName = ACTION_MAP.get("SelfLeaveScene");
            reArgs.add("wave");
            reArgs.add("bye.wav");
        } else if (actionName.equals("SelfTellChildLeaving")) {
            reName = ACTION_MAP.get("SelfTellChildLeaving");
            reArgs.add("shuffle1");
            reArgs.add("need_to_go_home.wav");
        } else if (actionName.equals("SelfWalkOutEndActivity")) {
            reName = ACTION_MAP.get("SelfWalkOutEndActivity");
            reArgs.add("6");
            reArgs.add("0");
        } else if (actionName.equals("SelfSayFollowMe")) {
            reName = ACTION_MAP.get("SelfSayFollowMe");
            reArgs.add("Follow_me.wav");
        } else if (actionName.equals("SelfWalkOut")) {
            reName = ACTION_MAP.get("SelfWalkOut");
            reArgs.add("6");
            reArgs.add("0");
        } else if (actionName.equals("SelfMoveToSide")) {
            reName = ACTION_MAP.get("SelfMoveToSide");
            reArgs.add("4");
            reArgs.add("0");
        } else if (actionName.equals("SelfLookAtChildMoveToSide")) {
            reName = ACTION_MAP.get("SelfLookAtChildMoveToSide");
            reArgs.add("");

            //
        } else if (actionName.equals("SelfBallCloudExploration")) {
            reName = ACTION_MAP.get("SelfBallCloudExploration");
            reArgs.add(args.get(0));
            reArgs.add("WalkTo=True");
        } else if (actionName.equals("SelfBallCloudExplorationAcceptBall")) {
            reName = ACTION_MAP.get("SelfBallCloudExplorationAcceptBall");
            reArgs.add(args.get(0));
            reArgs.add("WalkTo=True");
            reArgs.add("Thanks_throw_through_cloud.wav");
        } else if (actionName.equals("SelfFlowerBallExploration")) {
            reName = ACTION_MAP.get("SelfFlowerBallExploration");
            reArgs.add(args.get(0));
            reArgs.add("WalkTo=True");
        } else if (actionName.equals("SelfFlowerBallExplorationAcceptFlower")) {
            reName = ACTION_MAP.get("SelfFlowerBallExplorationAcceptFlower");
            reArgs.add(args.get(0));
            reArgs.add("WalkTo=True");
            reArgs.add("Thanks_will_turn_into_ball.wav");
        } else if (actionName.equals("SelfFlowerBasketExploration")) {
            reName = ACTION_MAP.get("SelfFlowerBasketExploration");
            reArgs.add(args.get(0));
            reArgs.add("WalkTo=True");
        } else if (actionName.equals("SelfFlowerBasketExplorationAcceptFlower")) {
            reName = ACTION_MAP.get("SelfFlowerBasketExplorationAcceptFlower");
            reArgs.add(args.get(0));
            reArgs.add("WalkTo=True");
            reArgs.add("Thanks_in_the_basket.wav");
        } else if (actionName.equals("SelfPotStackExploration")) {
            reName = ACTION_MAP.get("SelfPotStackExploration");
            reArgs.add(args.get(0));
            reArgs.add("WalkTo=True");
        } else if (actionName.equals("SelfPotStackExplorationAcceptPot")) {
            reName = ACTION_MAP.get("SelfPotStackExplorationAcceptPot");
            reArgs.add(args.get(0));
            reArgs.add("WalkTo=True");
            reArgs.add("Thanks_will_stack_it.wav");
        }

        System.out.println("converted to action: " + reName + " with arguments: " + reArgs);
    }

    public String getOriginalAEname() {
        return originalAEname;
    }

    public LinkedList <String> getOriginalAEargs() {
        return originalAEargs;
    }

    public String getReName() {
        return reName;
    }

    public LinkedList <String> getReArgs() {
        return reArgs;
    }

    public String getAvailablePosition() {
        String position = "";
        List <SubstitutionSet> bindings = KbUtilities.kbGetBindings("(available)");
        if (! bindings.isEmpty()) {
            for (SubstitutionSet subSet : bindings) {
                for (Object object : subSet.GetSubstitutions()) {
                    Substitution sub = (Substitution) object;
                    String name = sub.getValue().getName();
                    System.out.println("========================-------========== checking (available) for "
                                       + name
                                       + " which is: "
                                       + KbUtilities.kbAsk(name + "(available)"));
                    if (KbUtilities.kbAskTrue(name + "(available)")
                        && (name.equals("one")
                            || name.equals("three")
                            || name.equals("five")
                            || name.equals("seven")
                            || name.equals("nine"))) {
                        position = name;
                        System.out.println("&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&*&* position is available: "
                                           + position);
                    }
                }
            }
        } else {
            System.out.println("positions were null so choose number 1");
            position = "0";
        }
        // need different name in AE to RE because objectIds are also
        // numbers and updating the KB runs into problems
        String REpositionName;
        if (position.equals("one")) {
            REpositionName = "1";
        } else if (position.equals("two")) {
            REpositionName = "2";
        } else if (position.equals("three")) {
            REpositionName = "3";
        } else if (position.equals("four")) {
            REpositionName = "4";
        } else if (position.equals("five")) {
            REpositionName = "5";
        } else if (position.equals("six")) {
            REpositionName = "6";
        } else if (position.equals("seven")) {
            REpositionName = "7";
        } else if (position.equals("eight")) {
            REpositionName = "8";
        } else if (position.equals("nine")) {
            REpositionName = "9";
        } else {
            REpositionName = "1";
        }
        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ setting position to: "
                           + REpositionName);

        return REpositionName;
    }

    public String getYourTurnArg() {
        Object childName = KbUtilities.kbAsk("childName()");

        if (CHILD_NAMES_TURN.contains(childName)) {
            return childName + "_turn.wav";
        } else {
            return "your-turn.wav";
        }
    }

    public String getGreetingArg() {
        Object childName = KbUtilities.kbAsk("childName()");

        if (CHILD_NAMES_GREET.contains(childName)) {
            return childName + ".wav,pause.wav,I'm-Andy.wav";
        } else if (GENERATOR.nextInt(2) == 0) {
            return "hi-I'm-Andy-2.wav";
        } else {
            return "hi-I'm-Andy.wav";
        }
    }

    private static final List <String> CHILD_NAMES_TURN = new ArrayList <String>();
    static {
        List <String> names = new ArrayList <String>();

        names.add("Aimee_Leigh");
        names.add("Anthony");
        names.add("Bilal");
        names.add("Callum");
        names.add("Daniel");
        names.add("Elliot");
        names.add("Glenn");
        names.add("Grace");
        names.add("Hardjis");
        names.add("Harris");
        names.add("Harrison");
        names.add("Jake");
        names.add("Kaleem");
        names.add("Leon");
        names.add("Megan");
        names.add("Mertcan");
        names.add("Michael");
        names.add("Mujtabar");
        names.add("Nathan");
        names.add("Nawaal");
        names.add("Owen");
        names.add("Reid");
        names.add("Ross");
        names.add("Skye");
        names.add("Stevie");
        names.add("Teddy");
        names.add("Tyler");

        CHILD_NAMES_TURN.addAll(names);
    }

    private static final List <String> CHILD_NAMES_GREET = new ArrayList <String>();
    static {
        List <String> names = new ArrayList <String>(CHILD_NAMES_TURN);

        // we don't have a "<name>_turn.wav" file for these, only "<name>.wav"
        names.add("Ainsley");
        names.add("Gabriel");
        names.add("Lily");
        names.add("Reece");
        names.add("Rian");
        names.add("William");

        CHILD_NAMES_GREET.addAll(names);
    }
}
