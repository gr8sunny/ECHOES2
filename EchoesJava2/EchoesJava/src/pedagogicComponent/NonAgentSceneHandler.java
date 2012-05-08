package pedagogicComponent;

import utils.Interfaces.IActionEngine;
import utils.Interfaces.IDramaManager;
import utils.Enums.EchoesScene;
import pedagogicComponent.data.ChildAction;

/**
 * Makes decisions about the parameters of non agent scenes, currently only the
 * bubble scene
 * 
 * @author katerina avramides
 * 
 */
public class NonAgentSceneHandler extends PCcomponentHandler {

	private int bubbleComplexity;
	private boolean displayBubbleScore;
	private int bubblePopCount;
	private int bubbleMergeCount;
	private int thresholdNumBubbleAcitons = 20;

	/**
	 * @param dmPrx
	 */
	public NonAgentSceneHandler(PCcomponents pCc, IDramaManager dmPrx,
			IActionEngine aePrx) {
		super(pCc, dmPrx, aePrx);
		bubblePopCount = 0;
		bubbleMergeCount = 0;
	}

	public void decideNonAgentSceneParameters(EchoesScene scene) {
		if (scene == EchoesScene.Bubbles) {
			System.out
					.println("NonAgentSceneParameters is telling the drama manager to set the bubbles scene with num bubbles "
							+ bubbleComplexity
							+ " and displayScore: "
							+ displayBubbleScore);
			dmPrx.setScene(EchoesScene.Bubbles);
			dmPrx
					.setBubbleSceneParameters(bubbleComplexity,
							displayBubbleScore);
		}
	}

	/**
	 * Used to set the initial value from the child's profile
	 * 
	 * @param complexity
	 */
	public void setBubbleComplexityAndScore(int complexity, boolean score) {
		this.bubbleComplexity = complexity;
		this.displayBubbleScore = score;
		System.out.println("Set bubble complexity: " + bubbleComplexity
				+ " and display score: " + displayBubbleScore);
	}

	/**
	 * Called when there has been a change
	 * 
	 * @param complexity
	 */
	public void changeBubbleComplexity(int complexity) {
		// check if current scene is bubble scene, else ignore
		if (dmPrx.getCurrentScene() == EchoesScene.Bubbles)
			dmPrx
					.setBubbleSceneParameters(bubbleComplexity,
							displayBubbleScore);
	}

	/**
	 * Increase complexity of the scene
	 * 
	 */
	public void increaseComplexity() {
		if (dmPrx.getCurrentScene() == EchoesScene.Bubbles) {
			bubbleComplexity = bubbleComplexity + 2;
			{
				System.out.println("Increasing bubble complexity to: "
						+ bubbleComplexity);
				dmPrx.setBubbleSceneParameters(bubbleComplexity,
						displayBubbleScore);
			}
		}
	}

	/**
	 * Decrease complexity of the scene
	 * 
	 */
	public void decreaseComplexity() {
		if (dmPrx.getCurrentScene() == EchoesScene.Bubbles) {
			if (bubbleComplexity > 2) {
				bubbleComplexity = bubbleComplexity - 2;
				System.out.println("Decreasing bubble complexity to: "
						+ bubbleComplexity);
				dmPrx.setBubbleSceneParameters(bubbleComplexity,
						displayBubbleScore);
			} else {
				System.out.println("cannot decrease bubble complexity");
			}
		}
	}

	/**
	 * Called when the child has performed an action in a non agent scene.
	 * 
	 * This only corresponds to an action on an object. Other kind of actions
	 * are of interest to the CM and can influence the PC by changing CM
	 * variables that are then communicated to the PC.
	 * 
	 */
	public void respondToChildAction(String childAction) {
		if (childAction.equals(ChildAction.bubble_pop.getName())) {
			bubblePopCount++;
		} else if (childAction.equals(ChildAction.bubble_merge.getName())) {
			bubbleMergeCount++;
		}
		// if have popped or merged more than threshold number of bubbles
		// then move on to garden scene
		if (bubblePopCount > thresholdNumBubbleAcitons
				|| bubbleMergeCount > thresholdNumBubbleAcitons) {
			System.out
					.println("Poppoed "
							+ bubblePopCount
							+ " and merged "
							+ bubbleMergeCount
							+ " bubbles so moving to garden scene (or one with appropriate activity");
			bubblePopCount = 0;
			bubbleMergeCount = 0;
//			getPCcs().agentInvolvementH.decideAgentInvolvement(
//					EchoesScene.Garden, true);
		}

	}
}
