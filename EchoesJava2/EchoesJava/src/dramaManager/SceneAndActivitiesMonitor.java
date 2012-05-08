package dramaManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import echoesEngine.ListenerManager;
import utils.Interfaces.IRenderingListener;
import utils.Interfaces.*;
import utils.Enums.*;

/**
 * Used to monitor what is going on in the scene and what activities the child
 * is engaged in/
 * 
 * @author katerina
 * 
 */
@SuppressWarnings("unused")
public class SceneAndActivitiesMonitor 
{
	private EchoesScene currentScene;
	private EchoesActivity currentActivity;
	private boolean activityStarted;
	private Map<String, EchoesObjectType> objectsInScene;
	private Map<String, String> objectLocationMap;
	private IPedagogicComponent pcPrx;
	private IPedagogicComponent pcPrxOneWay;
	private RenderingListenerImpl rlImpl;
	private AgentListenerImpl alImpl;
	private IActionEngine aePrx;
	private IRenderingEngine rePrx;
	private boolean contingent = false;
	private int numObjChildCantManipulate = 0;
  private EchoesScene sceneToBeLoaded = null;

	public SceneAndActivitiesMonitor(IPedagogicComponent pcPrx, IActionEngine aePrx, IRenderingEngine rePrx) 
	{
		currentScene = EchoesScene.NoScene;
		activityStarted = false;
		objectsInScene = Collections.synchronizedMap(new HashMap<String, EchoesObjectType>());
		objectLocationMap = Collections.synchronizedMap(new HashMap<String, String>());
		this.pcPrx = pcPrx;
		this.aePrx = aePrx;
		this.rePrx = rePrx;
		pcPrxOneWay = pcPrx;

		ListenerManager listenerMgr = ListenerManager.GetInstance();
		    
		rlImpl = new RenderingListenerImpl();
		listenerMgr.Subscribe(rlImpl);

		alImpl = new AgentListenerImpl();
		listenerMgr.Subscribe(alImpl);
	}

	public IRenderingListener getRenderingListener() {
		return (IRenderingListener) rlImpl;
	}

	/**
	 * You have to call this method when shutting down ...
	 */
	public void shutdown() 
	{
	  ListenerManager listenerMgr = ListenerManager.GetInstance();
    listenerMgr.Unsubscribe(rlImpl);
    listenerMgr.Unsubscribe(alImpl);
	}

	/**
	 * A class to listen for messages from the rendering engine updating the
	 * state of the world. Fill in the body of any methods that you need to use
	 * ...
	 * 
	 * @author Mary Ellen Foster
	 */
	private class RenderingListenerImpl implements IRenderingListener 
	{

		/**
		 * When an object is added to the world.
		 * 
		 * It updates the list of objects in the world, the object's location in
		 * DM coordinates, and the object location relative to the agent (needed
		 * by the action engine).
		 * 
		 * Needs to return the location of the object and whether the agent is
		 * now "at" the object, i.e. whether the agent is in a location from
		 * which he can interact with the object
		 * 
		 * @param objId
		 * @param props
		 * @param __current
		 */
		public void objectAdded(String objId, Map<String, String> props) 
		{
			synchronized (objectsInScene) {
				String reObjectType = props.get("type");
				if (reObjectType.equals("Flower")) {
					if (currentActivity == EchoesActivity.Explore
							|| currentActivity == EchoesActivity.ExploreWithAgent) {
						rePrx.setObjectProperty(objId, "CanTurnIntoBall",
								"True");
						rePrx.setObjectProperty(objId, "CanTurnIntoBubble",
								"True");
					} else if (currentActivity == EchoesActivity.FlowerTurnToBall) {
						rePrx.setObjectProperty(objId, "CanTurnIntoBall",
								"True");
						rePrx.setObjectProperty(objId, "CanTurnIntoBubble",
								"False");
					} else {
						rePrx.setObjectProperty(objId, "CanTurnIntoBall",
								"False");
						rePrx.setObjectProperty(objId, "CanTurnIntoBubble",
								"False");
					}

					if (currentActivity == EchoesActivity.FlowerTurnToBall
							&& contingent) {
						if (numObjChildCantManipulate < 3) {
							rePrx.setObjectProperty(objId,
									"ChildCanTurnIntoBall", "False");
							rePrx.setObjectProperty(objId,
									"ChildCanTurnIntoBubble", "False");
							numObjChildCantManipulate++;
						}
					}
				}
				if (reObjectType.equals("Ball")) {
					if (currentActivity == EchoesActivity.BallThrowing
							|| currentActivity == EchoesActivity.BallSorting
							|| currentActivity == EchoesActivity.BallThrowingContingent
							|| currentActivity == EchoesActivity.FlowerTurnToBall
							|| currentActivity == EchoesActivity.FlowerTurnToBallContingent) {
						rePrx.setObjectProperty(objId, "BounceWithinScene",
								"True");
					} else {
						rePrx.setObjectProperty(objId, "BounceWithinScene",
								"False");
					}

					if (currentActivity == EchoesActivity.BallThrowing
							&& contingent) {
						if (numObjChildCantManipulate < 3) {
							rePrx.setObjectProperty(objId,
									"ChildCanChangeColour", "False");
							numObjChildCantManipulate++;
						}
					}
				}
				if (reObjectType.equals("Cloud")
						&& currentActivity == EchoesActivity.BallThrowing) {
					rePrx.setObjectProperty(objId, "CanRain", "False");
				}
				try {
					EchoesObjectType type = EchoesObjectType
							.valueOf(reObjectType);
					objectsInScene.put(objId, type);
				} catch (IllegalArgumentException e) {
					System.out.println("DIDN'T MATCH RE OBJECT TYPE!!");
				}
			}
		}

		public void objectRemoved(String objId) {
			synchronized (objectsInScene) {
				if (!objectsInScene.containsKey(objId)) {
					//
				}
				objectsInScene.remove(objId);
				objectLocationMap.remove(objId);
			}
		}

		public void objectPropertyChanged(String objId, String propName,
				String propValue) {
		}

		public void userStarted(String name) 
		{
			aePrx.setChildname(name);
			pcPrx.loadChildProfile(name);
			rePrx.endScenario("Intro");
			pcPrxOneWay.changeScene();
		}

		public void userTouchedObject(String objId) {}
		public void agentAdded(String agentId, Map<String, String> props) {}
		public void agentRemoved(String agentId) {}
		public void agentPropertyChanged(String agentId, String propName,String propValue) {}
		public void worldPropertyChanged(String propName, String propValue) {
		}

		/**
		 * Sets the current scene from RE
		 * 
		 */
		public void scenarioStarted(String name) {
			/*
			 * the current scene should be set in sam via the RElistener" BUT
			 * this isn't going to work for the garden scenes, because in the re
			 * it's the same scenario with different backgrounds, so need to set
			 * the current scene from sod, where the re is called need to
			 * convert from Rendering Engine name to DM and PC name unless can
			 * check the background as well
			 */
		}

		public void scenarioEnded(String name) {
		}

		@Override
		public void userTouchedAgent(String agentId) {
		}

	}

	/**
	 * A class to listen for messages from the rendering engine indicating
	 * actions performed by the agent and the child.
	 * 
	 * Possibly need to convert rendering engine names to DM and PC names, e.g.
	 * for object animations
	 * 
	 * @author Mary Ellen Foster
	 */
	private class AgentListenerImpl implements IAgentListener
	{

		public void agentActionStarted(String agentId, String action,
				List<String> details) {
		}

		public void agentActionCompleted(String agentId, String action,
				List<String> details) {
			// do nothing - child actions handled from agentListener in the PC

		}

		public void agentActionFailed(String agentId, String action,
				List<String> details, String reason) {
		}
	}

	/**
	 * Sets the current scene
	 * 
	 */
	public void setCurrentScene(EchoesScene scene) {
		this.currentScene = scene;
	}

	/**
	 * Methods to get information from DM about current scene
	 * 
	 */

	/**
	 * Returns the current scene
	 * 
	 * @return
	 */
	public EchoesScene getCurrentScene() {
		return currentScene;
	}

	/**
	 * Returns an object of a specified type that is in the world.
	 * 
	 * @param objectType
	 * @param contact
	 *            specified whether this should be an object that is close to
	 *            the agent (contact point) or further away (distal point)
	 * @return
	 */
	public String getTargetObject(EchoesObjectType objectType, boolean contact) {
		synchronized (objectsInScene) {
			ArrayList<String> objectsInWorld = new ArrayList<String>();
			for (String key : objectsInScene.keySet()) {
				if (objectsInScene.get(key) == objectType) {
					objectsInWorld.add(key);
				}
			}
			if (!objectsInWorld.isEmpty()) {
				String object = aePrx.getTargetObject(objectType);
				return object;
			} else {
				System.out.println("warning - returning null target object");
				return null;
			}
		}
	}

	/**
	 * Gets the object in the world, of a specific type, that is closest to the
	 * agent. Useful for doing contact point.
	 * 
	 * The objecType should be the reId of the object.
	 * 
	 * @return
	 */
	public String getTargetObjectClosest(String objectType) {
		return null;

	}

	/**
	 * Gets the object in teh world, of a specific type, that is the furthest
	 * form the agent. Useful for doing distal point.
	 * 
	 * @param objecType
	 * @return
	 */
	public String getTargetObjectFurthest(String objecType) {
		return null;
	}

	/**
	 * Returns a list of objects in the ECHOES world based on their objectId
	 * used in the rendering engine
	 * 
	 * @return
	 */
	public Map<String, EchoesObjectType> getObjectsInScene() {
		return objectsInScene;
	}

	public synchronized void updateObjectLocation(String objId, String loc) {
		objectLocationMap.put(objId, loc);
	}

	public Map<String, String> getObjectLocationMap() {
		return objectLocationMap;
	}

	/**
	 * Checks whether all the objects necessary for an activity are present in
	 * the environment.
	 * 
	 * @param objectsInScene
	 * @return
	 */
	public boolean checkActivityStatus() {
		synchronized (objectsInScene) {
			HashMap<EchoesObjectType, Integer> activityObjectsPresent = new HashMap<EchoesObjectType, Integer>();
			HashMap<EchoesObjectType, Integer> objectsNeeded = new HashMap<EchoesObjectType, Integer>();
			ArrayList<EchoesObjectType> activityObjects = new ArrayList<EchoesObjectType>();
			// each activity requires one or more instances of certain
			// objects, e.g. {"flowerpot", flowerpot"} for stacking activity
			activityObjects = ActivityDetails.getObjectsNeeded(currentActivity);
			for (int i = 0; i < activityObjects.size(); i++) {
				if (!objectsNeeded.containsKey(activityObjects.get(i))) {
					objectsNeeded.put(activityObjects.get(i), new Integer(1));
				} else {
					Integer num = objectsNeeded.get(activityObjects.get(i));
					num = num + 1;
					objectsNeeded.put(activityObjects.get(i), num);
				}
			}
			for (EchoesObjectType objectType : objectsInScene.values()) {
				if (!activityObjectsPresent.containsKey(objectType)) {
					activityObjectsPresent.put(objectType, new Integer(1));
				} else {
					Integer num = activityObjectsPresent.get(objectType);
					num = num + 1;
					activityObjectsPresent.put(objectType, num);
				}
			}
			for (EchoesObjectType objectNeeded : objectsNeeded.keySet()) {
				if (activityObjectsPresent.containsKey(objectNeeded)) {
					if (activityObjectsPresent.get(objectNeeded) < objectsNeeded
							.get(objectNeeded)) {
						return false;
					}
				} else {
					return false;
				}
			}
			return true;
		}
	}

	public void setCurrentActivity(EchoesActivity activity, boolean contingent) {
		this.currentActivity = activity;
		this.contingent = contingent;
		numObjChildCantManipulate = 0;
	}

	public void setActivityStarted(boolean started) {
		this.activityStarted = started;
	}

	public void setSceneToBeLoaded(EchoesScene scene) {
		this.sceneToBeLoaded = scene;
	}
}
