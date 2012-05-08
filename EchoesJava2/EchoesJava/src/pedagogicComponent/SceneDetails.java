package pedagogicComponent;

import java.util.LinkedList;
import java.util.List;

import utils.Enums.EchoesActivity;
import utils.Enums.EchoesScene;

public class SceneDetails {

	public static boolean agentCanEnter(EchoesScene scene) {
		if (scene == EchoesScene.GardenTask
				|| scene == EchoesScene.GardenSocialGame)
			return true;
		else
			return false;
	}

	public static List<EchoesActivity> getSceneActivities(EchoesScene scene) {
		List<EchoesActivity> result = new LinkedList<EchoesActivity>();

		switch (scene) {

		case Garden:
			result.add(EchoesActivity.FlowerPickToBasket);
			result.add(EchoesActivity.FlowerTurnToBall);
			result.add(EchoesActivity.FlowerGrow);
			result.add(EchoesActivity.CloudRain);
			// result.add(EchoesActivity.LeavesFly);
			// result.add(EchoesActivity.LeavesSort);
			// result.add(EchoesActivity.LeavesGrow);
			result.add(EchoesActivity.PotStackRetrieveObject);
			// result.add(EchoesActivity.BallThrowInBasket);
			// result.add(EchoesActivity.BallBounce);
			result.add(EchoesActivity.AgentPoke);
			break;
		}

		return result;
	}
}
