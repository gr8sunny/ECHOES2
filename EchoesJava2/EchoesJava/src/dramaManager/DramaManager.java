package dramaManager;


import utils.Enums.*;
import utils.Interfaces.IDramaManager;

public class DramaManager implements IDramaManager 
{
	private SceneAndActivitiesMonitor sam;
	private SceneAndObjectDirector sod;

	public DramaManager(SceneAndActivitiesMonitor sam,
			SceneAndObjectDirector sod) {
		this.sam = sam;
		this.sod = sod;
	}

	public EchoesScene getCurrentScene() {
		return sam.getCurrentScene();
	}

	public void setIntroScene(EchoesScene scene, String childName) {
		sod.setIntroScene(scene, childName);
	}

	public void setScene(EchoesScene scene) {
		sod.setScene(scene);
	}

	public void removeObject(String objId) {
		sod.removeObject(objId);
	}

	public void setBubbleSceneParameters(int numBubbles, boolean displayScore) {
		System.out.println("check!");
		sod.setBubbleSceneProperties(numBubbles, displayScore);
	}

	public void addObject(EchoesObjectType objectType) {
		System.out.println("In DMIMpl adding object");
		sod.addObject(objectType, "");
	}

	public void arrangeScene(EchoesScene scene, EchoesActivity activity,
			int numRepetitions, boolean contingent) {
		System.out.println("test");
		sod.arrangeSceneForActivity(scene, activity, numRepetitions, contingent);
	}

	public String getTargetObject(EchoesObjectType objectType, boolean contact) {
		return sam.getTargetObject(objectType, contact);
	}

	@Override
	public void setActivityStarted(boolean activityStarted) {
		sam.setActivityStarted(activityStarted);
	}

	@Override
	public String getObjectType(String objId) {
		String objectType = "";
		synchronized (sam.getObjectsInScene()) {
			if (sam.getObjectsInScene().get(objId) != null) {
				objectType = sam.getObjectsInScene().get(objId).toString();
			} else {
				return "";
			}
		}
		if (objectType != null) {
			return objectType;
		} else {
			return "";
		}
	}
	
	public void dimScene() {
		sod.dimScene();
	}

	@Override
	public void updateObjectLocation(String objId, String propValue) {
		sam.updateObjectLocation(objId, propValue);
	}

	@Override
	public void moveFlower(String flowerId) {
		sod.moveFlowerToBasket(flowerId);
	}

	@Override
	public void setFlowerLoc(String flowerId) {
		sod.setFlowerLoc(flowerId);
	}

	@Override
	public void setFlowerInteractivity(String flower, boolean interactive) {
		sod.setFlowerMovable(flower, interactive);
	}

}
