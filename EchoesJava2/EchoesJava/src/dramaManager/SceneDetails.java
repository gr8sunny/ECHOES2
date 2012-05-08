package dramaManager;

import java.util.LinkedList;
import java.util.List;

import utils.Enums.EchoesObjectType;
import utils.Enums.EchoesScene;

public class SceneDetails {
	
	public static List<EchoesObjectType> getSceneObjects (EchoesScene scene) {
		List<EchoesObjectType> result = new LinkedList<EchoesObjectType>();
		
		switch (scene) {
		case Bubbles:
			result.add(EchoesObjectType.IntroBubble);
			result.add(EchoesObjectType.Bubble);
			break;
			
		case Garden:
			result.add(EchoesObjectType.IntroBubble);
			result.add(EchoesObjectType.Bubble);
			result.add(EchoesObjectType.Flower);
			result.add(EchoesObjectType.MagicLeaves);
			result.add(EchoesObjectType.Ball);
			result.add(EchoesObjectType.Cloud);
			result.add(EchoesObjectType.Pot);
			break;
		}

		return result;
	}
}
