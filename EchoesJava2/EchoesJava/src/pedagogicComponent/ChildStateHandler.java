package pedagogicComponent;

import java.util.ArrayList;
import java.util.HashMap;

import utils.Interfaces.IActionEngine;
import utils.Interfaces.IDramaManager;
import utils.Enums.EchoesActivity;
import utils.Enums.EchoesObjectType;
import utils.Enums.EchoesScene;
import utils.Enums.ScertsGoal;
import pedagogicComponent.data.StringLabelException;

/**
 * 
 * This class contains methods that are called by the child model when there is
 * a change in the child's state.
 * 
 * The child model holds profiles for each child. The PC only for the current
 * user.
 * 
 * Each method in this class will direct the PC depending on the updated value
 * from the child model.
 * 
 * @author katerina avramides
 * 
 */
public class ChildStateHandler extends PCcomponentHandler {

	// whether the child is engaged with ECHOES
	private boolean engagedECHOES;
	// the child's affective state
	@SuppressWarnings("unused")
  private String affectiveState;
	// whether the child is engaged with the agent
	@SuppressWarnings("unused")
  private boolean engagedWithAgent;	

	public ChildStateHandler(PCcomponents pCc, IDramaManager dmPrx,
			IActionEngine aePrx) {
		super(pCc, dmPrx, aePrx);
		engagedECHOES = false;
		engagedWithAgent = false;
		affectiveState = "";

	}
	
	public void setEngagedWithAgent(boolean engagedWithAgent){
		this.engagedWithAgent = engagedWithAgent;
	}


	/**
	 * Loads the child's profile.
	 * 
	 * Takes the child's name as argument and gets the child's profile from the
	 * Child Model.
	 * 
	 * @param name
	 */
	public void loadInitialChildAttributes(String name) {
		// get cm, get child name, load profile
		// for the moment
		System.out
				.println("Need to fake Child Model input. At the moment just loads the one profile");
		TemporaryChildModelForPC tempCM = new TemporaryChildModelForPC();

		// the scene that should follow from the intro scene (e.g. does the
		// child need to play in the bubble scene for a while or can she move
		// straight to one of the garden scenes? Which garden scene?
		final EchoesScene appropriateStartScene = EchoesScene.Bubbles;
		// the initial bubble complexity that is appropriate for the child
		final int bubbleComplexity = 3;
		// whether to display the score in the bubble scene - needed?
		final boolean displayScore = true;
		// whether the child is open to interacting with the agent - will
		// determine whether the agent present or not
		final boolean isOpenToAgent = false;
		// the number of times each activity should be repeated
		final int numRepetitions = 5;
		// the priority SCERTS goals for each child
		final ArrayList<ScertsGoal> childProfileGoals = new ArrayList<ScertsGoal>();


		// the SCERTS scoring for each goal
		final HashMap<ScertsGoal, Integer> goalAbilityMap = tempCM
				.getGoalAbilityMap();
		// how much the child likes each activity
		final HashMap<EchoesActivity, Integer> activityLikeMap = tempCM
				.getActivityLikeMap();
		// I'm not sure this will be needed even when we have more than one
		// object per activity... because each object will be defined in a
		// separate activity, e.g. sort_leaves and sort_flowers
		final HashMap<EchoesObjectType, Integer> objectLikeMap = tempCM
				.getObjectLikeMap();

		// now calls the method below to load these attributes
		loadInitialChildAttributes(appropriateStartScene, bubbleComplexity,
				displayScore, numRepetitions, isOpenToAgent, goalAbilityMap,
				childProfileGoals, activityLikeMap, objectLikeMap);

	}

	/**
	 * The initial values that need to be set before interaction with ECHOES
	 * begins. Loaded from CM child profile. CM then only communicates changes
	 * to these values.
	 * 
	 */
	public void loadInitialChildAttributes(EchoesScene appropriateStartScene,
			int bubbleComplexity, boolean displayScore, int numRepetitions,
			boolean isOpenToAgent, HashMap<ScertsGoal, Integer> goalAbilityMap,
			ArrayList<ScertsGoal> childProfileGoals,
			HashMap<EchoesActivity, Integer> activityLikeMap,
			HashMap<EchoesObjectType, Integer> objectLikeMap) {
		System.out
				.println("In child state handler, setting initial child attributes");
		getPCcs().nonAgentSceneH.setBubbleComplexityAndScore(bubbleComplexity,
				displayScore);
		getPCcs().agentInvolvementH.setIsOpenToAgent(isOpenToAgent);
		getPCcs().goalH.setGoalAbility(goalAbilityMap);
		getPCcs().activityObjectH.setActivityLikeability(activityLikeMap);
		getPCcs().activityObjectH.setObjectLikeability(objectLikeMap);
		getPCcs().goalH.setChildProfileGoals(childProfileGoals);
		getPCcs().activityDurationH.changeNumRepetitions(numRepetitions);
	}

	/**
	 * Methods that are called by the Child Model when there's a change in the
	 * child's state.
	 * 
	 * The PC tries to act appropriately.
	 * 
	 */

	/**
	 * This is called when the value of child engagement with ECHOES changes
	 * 
	 */
	public void setEngagedECHOES(boolean engagedECHOES) {
		this.engagedECHOES = engagedECHOES;
		System.out
				.println("Responding to change in engagement to: "
						+ engagedECHOES
						+ "!!!!!! temporarily disabled because of engagement estimate bug");
		/*
		 * if (!engagedECHOES) { // the method reengage() makes a direct call to
		 * directDM and // directFAtiMA try { getPCcs().engagementH.reengage();
		 * } catch (StringLabelException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 * 
		 * } else if (engagedECHOES) { // I'm not sure any action is needed here
		 * other than to note in the // CM that the method for reengaging
		 * worked. /* // if the scene is the bubbles scene not much that can be
		 * done if
		 * (dmPrx.getCurrentScene().equals(ECHOESscene.bubbles.getName())) {
		 * 
		 * } else { // has the option of involving the agent
		 * decideAgentInvolvement(dmPrx.getCurrentScene()); }
		 */
		/* } */
	}
	
	
	public void setChildEngagement(boolean engaged) {
		this.engagedECHOES = engaged;
		reasonBasedChildState();
	}

	public void changeBubbleComplexity(int complexity) {
		getPCcs().nonAgentSceneH.changeBubbleComplexity(complexity);
	}

	/**
	 * Called when there's a change in the child's affective state. Calls
	 * appropriate method to take action.
	 * 
	 * @param affectiveState
	 */
	public void setAffectiveState(String affectiveState) {
		this.affectiveState = affectiveState;
		if (affectiveState.equals("bored")) {
			System.out.println("the child is bored");
			motivate();
		} else if (affectiveState.equals("frustrated")) {
			System.out.println("the child is frustrated");
			alleaviateFrustration();
		} else if (affectiveState.equals("motivated")) {
			System.out.println("the child is motivated");
			sustainMotivation();
		}
	}

	/**
	 * Calls appropriate methods, depending on the current scene to motivate the
	 * child to engage.
	 * 
	 */
	public void motivate() {
		System.out.println("Selecting method to motivate");
		if (dmPrx.getCurrentScene() == EchoesScene.Bubbles) {
			System.out
					.println("In bubble scene, so increasing bubble complexity");
			// change scene
			getPCcs().nonAgentSceneH.increaseComplexity();
		} else if (dmPrx.getCurrentScene() == EchoesScene.Garden) {
			System.out.println("In garden scene, so agent says ready?");
			// getPCcs().director.directAE("performSinglePCAction", "say_ready",
			// "");
		}
	}

	/**
	 * Calls appropriate method depending on the current scene to alleviate
	 * frustration.
	 * 
	 */
	public void alleaviateFrustration() {
		System.out.println("Selecting method to alleviate fruatration");
		if (dmPrx.getCurrentScene() == EchoesScene.Bubbles) {
			System.out
					.println("In bubble scene, so decreasing bubble complexity");
			// change scene
			getPCcs().nonAgentSceneH.decreaseComplexity();
		} else if (dmPrx.getCurrentScene() == EchoesScene.Garden) {
			System.out.println("In garden scene, so agent says ready?");
			// getPCcs().director.directAE("performSinglePCAction", "say_ready",
			// "");
		}
	}

	/**
	 * Calls appropriate method depending on the current scene to sustain the
	 * child's motivation.
	 * 
	 */
	public void sustainMotivation() {
		System.out.println("Selecting method to sustain motivation");
		if (dmPrx.getCurrentScene() == EchoesScene.Bubbles) {
			System.out
					.println("In bubble scene, so increase bubble complexity");
			// change scene
			getPCcs().nonAgentSceneH.increaseComplexity();
		} else if (dmPrx.getCurrentScene() == EchoesScene.Garden) {
			System.out.println("In garden scene, so agent says ready?");
			// getPCcs().director.directAE("performSinglePCAction", "say_ready",
			// "");
		}
	}

	/**
	 * Reasons on action to take based on updated chlid state from the child
	 * model/practitioner
	 * 
	 */
	public void reasonBasedChildState() {
		if (!engagedECHOES) {
			try {
				getPCcs().engagementH.reengage();
			} catch (StringLabelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
