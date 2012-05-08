package pedagogicComponent;

import java.util.ArrayList;
import java.util.HashMap;
import utils.Interfaces.IDramaManager;
import utils.Enums.ScertsGoal;
import pedagogicComponent.data.AgentAction;
import pedagogicComponent.data.ObjectAnimation;
import pedagogicComponent.data.StringLabelException;
import utils.Enums.EchoesActivity;

public class ECHOESworldDataInitialiser 
{
  @SuppressWarnings("unused")
  private IDramaManager dmPrx;

	/**
	 * @param dmPrx
	 */
	public ECHOESworldDataInitialiser(IDramaManager dmPrx) 
	{
		this.dmPrx = dmPrx;
	}

	/**
	 * Sets the SCERTS goals to work on.
	 * 
	 * @return
	 * 
	 */
	public HashMap<ScertsGoal, Integer> initialiseGoalAbilityMap() {
		HashMap<ScertsGoal, Integer> goalAbilityMap = new HashMap<ScertsGoal, Integer>();
		goalAbilityMap.put(ScertsGoal.BriefInteraction, new Integer(0));
		goalAbilityMap.put(ScertsGoal.ExtendedInteraction, new Integer(0));
		goalAbilityMap.put(ScertsGoal.InitiateSocialGame, new Integer(0));
		System.out.println("ability map" + goalAbilityMap);

		return goalAbilityMap;
	}

	/**
	 * Sets the activities that can be used to support each scerts goal
	 * 
	 */
	public HashMap<ScertsGoal, ArrayList<EchoesActivity>> initialiseGoalNotAppropriateActivityMap(
			HashMap<ScertsGoal, ArrayList<EchoesActivity>> goalNotAppropriateActivityMap) {
		// engage in brief interaction
		ArrayList<EchoesActivity> brief_interaction = new ArrayList<EchoesActivity>();
		brief_interaction.add(EchoesActivity.FlowerPickToBasket);
		brief_interaction.add(EchoesActivity.PotStackRetrieveObject);
		goalNotAppropriateActivityMap.put(ScertsGoal.BriefInteraction,
				brief_interaction);

		// engage in extended interaction
		ArrayList<EchoesActivity> extended_interaction = new ArrayList<EchoesActivity>();
		goalNotAppropriateActivityMap.put(ScertsGoal.ExtendedInteraction,
				extended_interaction);

		// child initiates a social game
		ArrayList<EchoesActivity> initiate_socialGame = new ArrayList<EchoesActivity>();
		goalNotAppropriateActivityMap.put(ScertsGoal.InitiateSocialGame,
				initiate_socialGame);

		// child responds to bid to interact
		// ArrayList<EchoesActivity> respond_bid = new
		// ArrayList<EchoesActivity>();
		// goalNotAppropriateActivityMap.put(ScertsGoal.RespondBid,
		// respond_bid);


		return goalNotAppropriateActivityMap;
	}

	/**
	 * Initialises the methods for reengaging the learner
	 * 
	 * @throws StringLabelException
	 * 
	 */
	public ArrayList<Object> initialiseReengagementMethods()
			throws StringLabelException {
		ArrayList<Object> reengagementMethods = new ArrayList<Object>();
		reengagementMethods
				.add(AgentAction.getAgentAction("reengage_sayReady"));
		reengagementMethods.add(ObjectAnimation
				.getObjectAnimation("sound_explosion"));
		return reengagementMethods;
	}

	/**
	 * Initialises the repertoire of behaviours the agent has that do not
	 * involve interacting with the child
	 * 
	 * @throws StringLabelException
	 * 
	 */
	public ArrayList<Object> initialiseNonChildDirectedBehaviours()
			throws StringLabelException {
		ArrayList<Object> nonChildDirectedBehaviours = new ArrayList<Object>();
		nonChildDirectedBehaviours
				.add(AgentAction.getAgentAction("play_onOwn"));
		nonChildDirectedBehaviours
				.add(AgentAction.getAgentAction("leaveScene"));
		return nonChildDirectedBehaviours;
	}

}
