package pedagogicComponent;

import utils.Interfaces.IActionEngine;
import utils.Interfaces.IDramaManager;
import utils.Enums.EchoesActivity;

/**
 * Monitors and makes calls to the appropriate PC methods depending on what the
 * PC is waiting for.
 * 
 * Based on number of times the activity has been repeated not time.
 * 
 * NOTE also need timer for when waiting for practitioner, but maybe that can be
 * done in terms of agent actions.
 * 
 * @author katerina
 * 
 */
public class ActivityDurationHandler extends PCcomponentHandler {

	private int numRepetitions;
	@SuppressWarnings("unused")
  private EchoesActivity currentActivity;
	
	public ActivityDurationHandler(PCcomponents pCc, IDramaManager dmPrx, IActionEngine aePrx) {
		super(pCc, dmPrx, aePrx);
	}

	/**
	 * Used when setting up the activity - e.g. how many flowerpots to add to
	 * 
	 * @param numRepetitions
	 */
	public void changeNumRepetitions(int numRepetitions) {
		this.numRepetitions = numRepetitions;
	}

	public int getNumRepetitions() {
		return numRepetitions;
	}

	public void setCurrentActivity(EchoesActivity activity) {
		this.currentActivity = activity;
	}

	public void activityExpired() {
		// if (currentEvent.equals("wait for practitioner to reengage")) {

		// } else if (currentEvent.equals("bubbleScene")) {

		// }
	}

	/*
	 * else { if (decideChangeScene()) {
	 * 
	 * } else { if
	 * (dmPrx.getCurrentScene().equals(ECHOESscene.bubbles.getName())) {
	 * 
	 * } else if (dmPrx.getCurrentScene().equals(ECHOESscene.garden.getName()))
	 * {
	 * 
	 * } else if
	 * (dmPrx.getCurrentScene().equals(ECHOESscene.garden_shed.getName())) {
	 * 
	 * } } // else if the current scene is not the intro scene // if the current
	 * scene is the bubble scene // then decide based on how engaged the child
	 * is and what is // appropriate // if decided to move to agentscene // if
	 * it's ok to involve the user if
	 * (getPCcs().agentInvolvementH.decideAgentInvolvement()) { // query DM
	 * about current scene and if apporpriate change scenes // ask DM to add the
	 * agent // chooose scert goal } else { // if agent is present then remove
	 * the agent from the // environment OR // make him wait in the background
	 * // else // choos scene that is appropriate - level of complexity //
	 * choose objects that the child likes } }
	 */

}
