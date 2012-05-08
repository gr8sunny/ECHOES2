package dramaManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import utils.Enums.*;
import utils.Interfaces.IRenderingEngine;

/**
 * Communicates with the rendering engine to modify the ECHOES environment as
 * directed by the PC
 * 
 * @author katerina avramides
 * 
 */

public class SceneAndObjectDirector 
{
	private SceneAndActivitiesMonitor sam;
	private IRenderingEngine rePrx;
	private ArrayList<String> groundObjectLocations;
	private EchoesActivity currentActivity;

	int potLoc = 0;
	int containerLoc = 0;
	int flowerLoc = 0;
	boolean changed = false;
	String rightLoc = "";
	String leftLoc = "";
	String centreLoc = "";

	// for ball sorting
	private int yellowBalls = 0;
	private int greenBalls = 0;
	private int blueBalls = 0;
	private int yellowContainers = 0;
	private int greenContainers = 0;
	private int blueContainers = 0;

	public SceneAndObjectDirector(IRenderingEngine rePrx, SceneAndActivitiesMonitor sam) 
	{
		this.rePrx = rePrx;
		this.sam = sam;
		groundObjectLocations = new ArrayList<String>();
		initialiseObjectLocations();
	}

	/**
	 * Called by the PC to set a particular activity.
	 * 
	 * Given the current state of the world, it decides what objects need to be
	 * added/removed for the activity to become available
	 * 
	 * @throws StringLabelException
	 * 
	 */
	public void arrangeSceneForActivity(EchoesScene scene,
			EchoesActivity activity, int numRepetitions, boolean contingent) {
		this.currentActivity = activity;

		// for ball sorting
		yellowBalls = 0;
		greenBalls = 0;
		blueBalls = 0;
		yellowContainers = 0;
		greenContainers = 0;
		blueContainers = 0;

		ArrayList<String> potsInScene = new ArrayList<String>();

		for (String objectInScene : sam.getObjectsInScene().keySet()) {
			if (sam.getObjectsInScene().get(objectInScene).equals(
					EchoesObjectType.Pot)) {
				potsInScene.add(objectInScene);
			}
		}
		if (!potsInScene.isEmpty()) {
			for (String pot : potsInScene) {
				removeObject(pot);
			}
		}

		sam.setCurrentActivity(activity, contingent);
		sam.setActivityStarted(false);

		// set flower transformable to ball/bubble if activity appropriate
		for (String objectInScene : sam.getObjectsInScene().keySet()) {
			if (sam.getObjectsInScene().get(objectInScene).equals(
					EchoesObjectType.Flower)) {
				if (activity == EchoesActivity.Explore
						|| activity == EchoesActivity.ExploreWithAgent) {
					rePrx.setObjectProperty(objectInScene, "CanTurnIntoBall",
							"True");
					rePrx.setObjectProperty(objectInScene, "CanTurnIntoBubble",
							"True");
				} else if (activity == EchoesActivity.FlowerTurnToBall) {
					rePrx.setObjectProperty(objectInScene, "CanTurnIntoBall",
							"True");
					rePrx.setObjectProperty(objectInScene, "CanTurnIntoBubble",
							"False");
				} else {
					rePrx.setObjectProperty(objectInScene, "CanTurnIntoBall",
							"False");
					rePrx.setObjectProperty(objectInScene, "CanTurnIntoBubble",
							"False");
				}
			}
		}
		// set ball to not bounce off scene in ball throwing activity
		for (String objectInScene : sam.getObjectsInScene().keySet()) {
			if (sam.getObjectsInScene().get(objectInScene).equals(
					EchoesObjectType.Ball)) {
				if (activity == EchoesActivity.BallThrowing
						|| activity == EchoesActivity.BallThrowingContingent
						|| activity == EchoesActivity.BallSorting
						|| activity == EchoesActivity.FlowerTurnToBall
						|| activity == EchoesActivity.FlowerTurnToBallContingent) {
					rePrx.setObjectProperty(objectInScene, "BounceWithinScene",
							"True");
				} else {
					rePrx.setObjectProperty(objectInScene, "BounceWithinScene",
							"False");
				}
			}
		}
		// also need to set this in sam when objects get added

		// if the chosen scene is not the current scene set new scene
		if (sam.getCurrentScene() != scene) {
			setScene(scene);
		}
		ArrayList<EchoesObjectType> objectTypeNeeded = ActivityDetails
				.getObjectsNeeded(activity);

		int k = numRepetitions;
		// for now leave at 1
		k = 1;
		// hashmap of objects to add to the scene for the given activity
		HashMap<EchoesObjectType, Integer> objectInstancesToAdd = new HashMap<EchoesObjectType, Integer>();

		// add the number of objects needed for that type of object
		for (EchoesObjectType objectType : objectTypeNeeded) {
			// for some activities, e.g. cloud, it's only appropriate to have
			// one instance of the object needed. So set k = numRepetitions - 1
			// and the objects will only be added once
			if (ActivityDetails.getOneInstanceAppropriate(activity)) {
				k = 1;
			}
			if (objectInstancesToAdd.containsKey(objectType)) {
				k = 1 + objectInstancesToAdd.get(objectType).intValue();
				objectInstancesToAdd.put(objectType, k);
			}
			objectInstancesToAdd.put(objectType, k);
			// reset
			k = numRepetitions;
		}

		// add objects that are needed
		for (EchoesObjectType objectType : objectInstancesToAdd.keySet()) {
			for (int i = 0; i < objectInstancesToAdd.get(objectType).intValue(); i++) {
				// set object in world using rendering engine name
				if (currentActivity == EchoesActivity.FlowerPickToBasket
						&& objectType.equals("Flower"))
					addObject(objectType, "");
				else
					addObject(objectType, chooseObjectLocation(objectType));
			}
		}

		if (currentActivity == EchoesActivity.CloudRain
				|| currentActivity == EchoesActivity.FlowerPickToBasket)
			rePrx.setWorldProperty("DisplayScore", "True");
		else
			rePrx.setWorldProperty("DisplayScore", "False");

	}

	/**
	 * Chooses an appropriate position to place the objects. For the tree and
	 * the shed these are hardwired.
	 * 
	 * For the flower, flowerpot that are on the ground this is x: -5 = 5; y:
	 * ground, middle-ground; z: front, middle, back
	 * 
	 * for leaves - yet to be defined
	 * 
	 * for ball, bubbles, assume anywhere
	 * 
	 * @param objectType
	 * @return
	 */
	public String chooseObjectLocation(EchoesObjectType objectType) {
		String location = "";

		if (objectType.equals(EchoesObjectType.LifeTree)) {
			location = "";
		} else if (objectType.equals(EchoesObjectType.Shed)) {
			location = "-2, middle-ground, back";
		} else if (objectType.equals(EchoesObjectType.Pot)) {
			// slightly different y positions so that they stack
			if (potLoc == 0) {
				location = "-4, 3, front";
				potLoc++;
			} else if (potLoc == 1) {
				location = "-2, 3.5, front";
				potLoc++;
			} else if (potLoc == 2) {
				location = "0, 4, front";
				potLoc++;
			} else if (potLoc == 3) {
				location = "2, 4.5, front";
				potLoc++;
			} else {
				location = "4, 5, front";
				potLoc = 0;
			}
		} else if (objectType.equals(EchoesObjectType.Flower)) {
			if (currentActivity == EchoesActivity.FlowerPickToBasket) {
				// position gets chosen in addObject method
			} else if (currentActivity == EchoesActivity.Explore
					|| currentActivity == EchoesActivity.ExploreWithAgent) {
				location = "-3, -1, front";
			} else {
				for (int i = 0; i < groundObjectLocations.size(); i++) {
					boolean loc = false;
					for (String objectLocation : sam.getObjectLocationMap()
							.values()) {
						// if there is not an object already in this location
						// NOTE flower must always be "middle"
						if (objectLocation.equals(groundObjectLocations.get(i)
								+ ", front")) {
							loc = true;
							break;
						}
					}
					if (!loc) {
						location = groundObjectLocations.get(i) + ", front";
						break;
					}
				}
			}
		} else if (objectType.equals(EchoesObjectType.Ball)) {
			if (potLoc == 0) {
				location = "-4, ground, front";
				potLoc++;
			} else if (potLoc == 1) {
				location = "-2, ground, front";
				potLoc++;
			} else if (potLoc == 2) {
				location = "-1, ground, front";
				potLoc++;
			} else if (potLoc == 3) {
				location = "1, ground, front";
				potLoc++;
			} else if (potLoc == 4) {
				location = "4, 0, front";
				potLoc++;
			} else if (potLoc == 5) {
				location = "-2, 0, front";
				potLoc++;
			} else if (potLoc == 6) {
				location = "-3.8, 0, front";
				potLoc++;
			} else if (potLoc == 7) {
				location = "-1.8, 0, front";
				potLoc++;
			} else if (potLoc == 8) {
				location = "1.8, 0, front";
				potLoc = 0;
			}
		} else if (objectType.equals(EchoesObjectType.Cloud)) {
			location = "-2, sky, back";
		} else if (objectType.equals(EchoesObjectType.Basket)) {
			location = "1, ground, front";
		} else if (objectType.equals(EchoesObjectType.Container)) {
			if (containerLoc == 0) {
				location = "3, -0.7, front";
				containerLoc++;
			} else if (containerLoc == 1) {
				location = "-3, -0.7, front";
				containerLoc++;
			} else if (containerLoc == 2) {
				location = "0, -0.7, front";
				containerLoc = 0;
			}
		} else if (objectType.equals(EchoesObjectType.MagicLeaves)) {
			location = "";
		} else {
			location = "";
		}
		return location;
	}

	/**
	 * Set intro scene, need child's name as arguments
	 * 
	 */
	public void setIntroScene(EchoesScene scene, String childName) {
		rePrx.setWorldProperty("UserList", childName);
		setScene(scene);
	}

	/**
	 * Called by the PC to set a scene.
	 * 
	 * The current scene is ended and the new one loaded. The current scene is
	 * never null because it is set as the intro scene when the class
	 * CurrentScene is constructed.
	 * 
	 */
	public void setScene(EchoesScene scene) {
		if (scene == EchoesScene.Intro) {
			rePrx.loadScenario("Intro");
			sam.setCurrentScene(EchoesScene.Intro);
		} else {
			// end current scene
			endScene();
			sam.setSceneToBeLoaded(scene);

			ArrayList<String> objs = new ArrayList<String>();

			for (String objectInScene : sam.getObjectsInScene().keySet()) {
				objs.add(objectInScene);
			}
			for (String obj : objs) {
				removeObject(obj);
			}

			switch (scene) {

			case Bubbles:
				rePrx.loadScenario("BubbleWorld");
				rePrx.addObject(EchoesObjectType.IntroBubble.toString());
				break;

			case Garden:
				rePrx.loadScenario("Garden");
				rePrx.addObject(EchoesObjectType.IntroBubble.toString());
				break;

			case GardenTask:
				rePrx.loadScenario("GardenTask");
				rePrx.addObject(EchoesObjectType.IntroBubble.toString());
				break;

			case GardenSocialGame:
				rePrx.loadScenario("GardenSocialGame");
				rePrx.addObject(EchoesObjectType.IntroBubble.toString());
				break;

			}
			// it's not ideal to set the scene from here - it should be done
			// from the RElistener, but the scenes in the RE are the same scene
			// with different background, so cannot do this. Perhaps add set
			// background method in the listener?
			sam.setCurrentScene(scene);
		}
	}

	/**
	 * Ends current scene
	 * 
	 */
	public void endScene() {
		// reset
		leftLoc = "";
		centreLoc = "";
		rightLoc = "";

		if (sam.getCurrentScene() == null) {
			return;
		}
		switch (sam.getCurrentScene()) {

		case Bubbles:
			rePrx.endScenario("BubbleWorld");
			break;
		case Garden:
			rePrx.endScenario("Garden");
			break;
		case GardenTask:
			rePrx.endScenario("GardenTask");
			break;
		case GardenSocialGame:
			rePrx.endScenario("GardenSocialGame");
			break;
		}

	}

	/**
	 * Sets all the properties in the bubble scene
	 * 
	 */
	public void setBubbleSceneProperties(int numBubbles, boolean displayScore) {
		rePrx
				.setWorldProperty("numBubbles", new Integer(numBubbles)
						.toString());
		if (displayScore) {
			rePrx.setWorldProperty("DisplayScore", "True");
		} else {
			rePrx.setWorldProperty("DisplayScore", "False");
		}
	}

	/**
	 * Adds objects to the garden scenes
	 * 
	 */
	public void addObject(EchoesObjectType objectType, String position) {
		if (currentActivity == EchoesActivity.FlowerPickToBasket
				&& objectType == EchoesObjectType.Flower && !leftLoc.isEmpty()
				&& !rightLoc.isEmpty() && !centreLoc.isEmpty()) {
			// if neither left nor right slot is available then don't add flower
			return;
		}

		String objId = rePrx.addObject(objectType.toString());

		if (currentActivity == EchoesActivity.FlowerPickToBasket
				&& objectType == EchoesObjectType.Flower) {
			rePrx.setObjectProperty(objId, "Interactive", "False");

			if (position.isEmpty()) {
				if (leftLoc.isEmpty()) {
					position = "-4, -2, front";
					leftLoc = objId;
				} else if (rightLoc.isEmpty()) {
					position = "4, -2, front";
					rightLoc = objId;
				} else if (centreLoc.isEmpty()) {
					position = "0, -2, front";
					centreLoc = objId;
				} else {
					System.out
							.println("warning - neither left nor right position empty");
				}
			}
		}

		// Call objectAdded manually here just to be sure that the effects are
		// processed immediately
		HashMap<String, String> props = new HashMap<String, String>();
		props.put("type", objectType.toString());
		sam.getRenderingListener().objectAdded(objId, props);

		rePrx.setObjectProperty(objId, "Pos", position);

		// make flower grow
		if (objectType == EchoesObjectType.Flower) {
			rePrx.setObjectProperty(objId, "Size", "0.1");
			rePrx.setObjectProperty(objId, "GrowToSize", "0.3");
			if (currentActivity == EchoesActivity.FlowerPickToBasket) {
				Random r = new Random();
				String flowerColour = "yellow";
				int randomint = r.nextInt(4);
				if (randomint == 0)
					flowerColour = "yellow";
				else if (randomint == 1)
					flowerColour = "green";
				else if (randomint == 2)
					flowerColour = "red";
				else if (randomint == 3)
					flowerColour = "blue";
				rePrx.setObjectProperty(objId, "Colour", flowerColour);
			}
		}

		// set ball and container colours for ball sorting activity
		if (currentActivity == EchoesActivity.BallSorting) {
			if (objectType == EchoesObjectType.Ball) {
				System.out.println("it's ball sorting and it's a ball");
				if (yellowBalls <= 2) {
					rePrx.setObjectProperty(objId, "Colour", "yellow");
					yellowBalls++;
				} else if (greenBalls <= 2) {
					rePrx.setObjectProperty(objId, "Colour", "red");
					greenBalls++;
				} else if (blueBalls <= 2) {
					rePrx.setObjectProperty(objId, "Colour", "blue");
					blueBalls++;
				}
			} else if (objectType == EchoesObjectType.Container) {
				System.out.println("it's ball sorting and it's a container");
				if (yellowContainers <= 0) {
					rePrx.setObjectProperty(objId, "Colour", "yellow");
					yellowContainers++;
				} else if (greenContainers <= 0) {
					rePrx.setObjectProperty(objId, "Colour", "red");
					greenContainers++;
				} else if (blueContainers <= 0) {
					rePrx.setObjectProperty(objId, "Colour", "blue");
					blueContainers++;
				}
			}
		}

		// !!! this should be set from the rendering engine callback when an
		// object has been added
		sam.updateObjectLocation(objId, position);
	}

	/**
	 * Removes an object from the scenes
	 * 
	 * @param objId
	 */
	public void removeObject(String objId) {
		rePrx.removeObject(objId);

		// Call objectRemoved manually here just to be sure that the effects are
		// processed immediately
		sam.getRenderingListener().objectRemoved(objId);
	}

	/**
	 * Adds the agent to the ECHOES environment
	 * 
	 * check about id that it is in fact the agent's name
	 * 
	 * NOTE this will be done in the action engine, as agentId needed
	 * 
	 */
	public void addAgentToScene(String agentName) {
		rePrx.addAgent(agentName);
	}

	public void initialiseObjectLocations() {
		String groundLoc14 = "3, ground";
		String groundLoc15 = "2, ground";
		String groundLoc16 = "1, ground";
		String groundLoc19 = "-2, ground";
		String groundLoc20 = "-3, ground";
		String groundLoc21 = "-4, ground";
		groundObjectLocations.add(groundLoc16);
		groundObjectLocations.add(groundLoc19);
		groundObjectLocations.add(groundLoc14);
		groundObjectLocations.add(groundLoc20);
		groundObjectLocations.add(groundLoc15);
		groundObjectLocations.add(groundLoc21);
	}

	public void dimScene() {
		rePrx.setWorldProperty("LightLevel", "0.5");
	}

	public void moveFlowerToBasket(String flowerId) {
		rePrx.setObjectProperty(flowerId, "MoveToBasket", "");
	}

	public void setFlowerLoc(String flowerId) {
		if (flowerId.equals(leftLoc)) {
			leftLoc = "";
		} else if (flowerId.equals(rightLoc)) {
			rightLoc = "";
		} else if (flowerId.equals(centreLoc)) {
			centreLoc = "";
		}
		// rePrx.setO
	}

	public void setFlowerMovable(String flowerId, boolean interactive) {
		if (interactive)
			rePrx.setObjectProperty(flowerId, "Interactive", "True");
		else
			rePrx.setObjectProperty(flowerId, "Interactive", "False");

	}

}
