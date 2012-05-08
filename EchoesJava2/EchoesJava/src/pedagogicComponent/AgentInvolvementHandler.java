package pedagogicComponent;

import utils.Interfaces.*;
import utils.Enums.EchoesScene;
import utils.Logger;

/**
 * Reasons about the child's attitude towards the agent. On this basis decides
 * whether the agent should try to interact with the child.
 * 
 * @author katerina avramides
 * 
 */
public class AgentInvolvementHandler extends PCcomponentHandler {

	// will need separate for each agent once we have two
	private boolean isOpenToAgent;
	// a count to determine whether to try and involve the agent
	private int nonAgentChildActionsCount;
	// an arbitrary num based on which will decide to involve agent after
	// certain number of child actions in the environment
	private static int thresholdChildActionsInvolveAgentAttempt = 10;

	public AgentInvolvementHandler(PCcomponents pCc, IDramaManager dmPrx,
			IActionEngine aePrx) {
		super(pCc, dmPrx, aePrx);
		nonAgentChildActionsCount = 0;
	}

	/**
	 * Sets the initial value for whether the child is open to interacting with
	 * the agent
	 * 
	 */
	public void setIsOpenToAgent(boolean isOpen) {
		this.isOpenToAgent = isOpen;
		// if setting no agent involvement reset count of noAgentChildActions
		if (!isOpenToAgent) {
			nonAgentChildActionsCount = 0;
		}
		Logger.Log("info", "Set is open to agent interaction: " + isOpenToAgent);
	}

	/**
	 * Is called when relevant values in the CM relating to whether the agent
	 * should get involved change
	 */
	public void decideAgentInvolvement() {
		System.out
				.println("deciding whether the child is ready to interact with an agent");
		if (nonAgentChildActionsCount > thresholdChildActionsInvolveAgentAttempt) {
			System.out
					.println("isOpenToAgent was set to: "
							+ isOpenToAgent
							+ " but number of child actions has passed threshold so setting to true");
			isOpenToAgent = true;
		}
		// if child is open to engaging with the agent
		if (isOpenToAgent) {
			System.out.println("involving the agent");
			// if the current scene is one that cannot involve the agent
			if (dmPrx.getCurrentScene() == EchoesScene.Bubbles) {
				// change scene to Garden

				// call method in agentH

			} else {
				// keep same scene

				// call method in agentH

			}
		} else {
			System.out.println("the child is not ready");

			// call method in agent behaviour handler

		}
	}


	public void increaseNoAgentChildActions() {
		nonAgentChildActionsCount++;
		System.out.println("increased child non agent actions to "
				+ nonAgentChildActionsCount);
	}

}
