package dramaManager;

import java.util.ArrayList;

import utils.Enums.EchoesActivity;
import utils.Enums.EchoesObjectType;

public class ActivityDetails {

	public static ArrayList<EchoesObjectType> getObjectsNeeded(
			EchoesActivity activity) {
		ArrayList<EchoesObjectType> result = new ArrayList<EchoesObjectType>();
		switch (activity) {

		case FlowerPickToBasket:
			result.add(EchoesObjectType.Flower);
			result.add(EchoesObjectType.Flower);
			result.add(EchoesObjectType.Flower);
			result.add(EchoesObjectType.Basket);
			break;
			
		case FlowerTurnToBall:
			result.add(EchoesObjectType.Flower);
			result.add(EchoesObjectType.Flower);
			result.add(EchoesObjectType.Flower);
			result.add(EchoesObjectType.Flower);
			result.add(EchoesObjectType.Flower);
			result.add(EchoesObjectType.Flower);
			break;

		case FlowerGrow:
			result.add(EchoesObjectType.Cloud);
			result.add(EchoesObjectType.Pot);
			result.add(EchoesObjectType.Pot);
			result.add(EchoesObjectType.Pot);
			result.add(EchoesObjectType.Pot);
			result.add(EchoesObjectType.Pot);
			break;

		case CloudRain:
			result.add(EchoesObjectType.Cloud);
			break;

		/*
		 * case LeavesSort: case LeavesGrow:
		 * result.add(EchoesObjectType.Leaves); break;
		 */

		case PotStackRetrieveObject:
			result.add(EchoesObjectType.Pot);
			result.add(EchoesObjectType.Pot);
			result.add(EchoesObjectType.Pot);
			result.add(EchoesObjectType.Pot);
			result.add(EchoesObjectType.Pot);
			break;

		/*
		 * "pot_scale"?? case PotScale: result.add(EchoesObjectType.Pot); break;
		 */

		/*
		 * case BallThrowInBasket: case BallBounce:
		 * result.add(EchoesObjectType.Ball); break;
		 */

		case AgentPoke:
			// Nothing
			break;

		case ExploreWithAgent:
		case Explore:
			result.add(EchoesObjectType.Pot);
			result.add(EchoesObjectType.Pot);
			result.add(EchoesObjectType.Pot);
			result.add(EchoesObjectType.Pot);
			result.add(EchoesObjectType.Cloud);
			result.add(EchoesObjectType.Flower);
			result.add(EchoesObjectType.Basket);
			break;

		case BallThrowing:
			result.add(EchoesObjectType.Ball);
			result.add(EchoesObjectType.Ball);
			result.add(EchoesObjectType.Ball);
			result.add(EchoesObjectType.Ball);
			result.add(EchoesObjectType.Ball);
			result.add(EchoesObjectType.Ball);
			result.add(EchoesObjectType.Ball);
			result.add(EchoesObjectType.Ball);
			result.add(EchoesObjectType.Ball);
			result.add(EchoesObjectType.Cloud);
			break;
			
		case BallSorting:
			result.add(EchoesObjectType.Ball);
			result.add(EchoesObjectType.Ball);
			result.add(EchoesObjectType.Ball);
			result.add(EchoesObjectType.Ball);
			result.add(EchoesObjectType.Ball);
			result.add(EchoesObjectType.Ball);
			result.add(EchoesObjectType.Ball);
			result.add(EchoesObjectType.Ball);
			result.add(EchoesObjectType.Ball);
			result.add(EchoesObjectType.Container);
			result.add(EchoesObjectType.Container);
			result.add(EchoesObjectType.Container);
			break;

		case TickleAndTree:
			result.add(EchoesObjectType.LifeTree);
			result.add(EchoesObjectType.MagicLeaves);
			result.add(EchoesObjectType.MagicLeaves);
			result.add(EchoesObjectType.MagicLeaves);
			result.add(EchoesObjectType.MagicLeaves);
			break;
		}

		return result;
	}

	public static boolean getOneInstanceAppropriate(EchoesActivity activity) {
		return (activity == EchoesActivity.CloudRain
				|| activity == EchoesActivity.Explore || activity == EchoesActivity.AgentPoke);
	}
}
