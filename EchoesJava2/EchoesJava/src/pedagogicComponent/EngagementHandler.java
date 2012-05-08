package pedagogicComponent;

import java.util.ArrayList;

import utils.Interfaces.IActionEngine;
import utils.Interfaces.IDramaManager;
import utils.Enums.EchoesActivity;
import utils.Enums.EchoesScene;
import pedagogicComponent.data.AgentAction;
import pedagogicComponent.data.StringLabelException;

/**
 * Reasons about whether the child is engaged (the CM will provide a degree
 * which this class will use to decide whether to act) and how to re-engage the
 * child. Uses information from the child model about whether the child is
 * engaged.
 * 
 * @author katerina avramides
 * 
 */
public class EngagementHandler extends PCcomponentHandler {

	ArrayList<String> reengagementMethods;

	public EngagementHandler(PCcomponents pCc, IDramaManager dmPrx,
			IActionEngine aePrx) {
		super(pCc, dmPrx, aePrx);
		reengagementMethods = new ArrayList<String>();
		reengagementMethods = initialiseEngagementMethods(reengagementMethods);
		this.dmPrx = dmPrx;
	}

	private ArrayList<String> initialiseEngagementMethods(ArrayList<String> methods) {
		methods.add(AgentAction.say_ready.getName());
		return methods;
	}

	/**
	 * Called when the child is considered to be disengaged with ECHOES. Decides
	 * how to try to re-engage the child.
	 * 
	 * Calls the methods to direct the DM and FAtiMA directly.
	 * 
	 * @throws StringLabelException
	 * 
	 */
	public void reengage() throws StringLabelException {

		System.out.println("Selecting re-egngagement method");
		if (dmPrx.getCurrentScene() == EchoesScene.Bubbles) {
			System.out.println("In bubble scene, so changing scene to garden");
			// change scene to garden
			// has the choice of choosing to involve the agent

			dmPrx.setScene(EchoesScene.Garden);
			dmPrx.arrangeScene(EchoesScene.Garden, EchoesActivity.Explore, 1, false);
			getPCcs().agentH.directAgentChangeInvolvement(false);

		} else if (dmPrx.getCurrentScene() == EchoesScene.Garden) {
			System.out.println("In garden scene, so agent says ready?");
			aePrx.setGoal("reengage");
		}

		// iterate through reengagementMethods and choose most appropriate one

		// TO DO fill in reasoning process to decide which method will be most
		// effective - if we have more than one in the future

		// if sound effects added then can use those
	}

}
