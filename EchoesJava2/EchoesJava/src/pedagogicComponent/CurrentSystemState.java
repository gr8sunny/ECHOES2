package pedagogicComponent;

import java.util.ArrayList;

import utils.Interfaces.IDramaManager;
import utils.Enums.EchoesActivity;
import utils.Enums.EchoesObjectType;
import utils.Enums.EchoesScene;
import utils.Enums.ScertsGoal;

public class CurrentSystemState {

	private EchoesScene currentScene;
	private ArrayList<EchoesObjectType> objectsInScene;
	private ScertsGoal currentGoal;
	private EchoesActivity currentActivity;
	private ArrayList<EchoesActivity> previous3activities;
  @SuppressWarnings("unused")
  private IDramaManager dmPrx;

	public CurrentSystemState(IDramaManager dmPrx) 
	{
	    this.dmPrx = dmPrx;
	}

	
	public void addOject(EchoesObjectType object) {
		objectsInScene.add(object);
	}
		
	public EchoesScene getCurrentScene() {
		return currentScene;
	}

	public void setCurrentScene(EchoesScene currentScene) {
		this.currentScene = currentScene;
	}

	public ScertsGoal getCurrentGoal() {
		return currentGoal;
	}

	public void setCurrentGoal(ScertsGoal currentGoal) {
		this.currentGoal = currentGoal;
	}

	public EchoesActivity getCurrentActivity() {
		return currentActivity;
	}

	/*
	 * Also updates attribute previous3activities
	 * 
	 */
	public void setCurrentActivity(EchoesActivity currentActivity) {
		this.currentActivity = currentActivity;
		previous3activities.remove(0);
		previous3activities.add(currentActivity);
	}

	public ArrayList<EchoesObjectType> getObjectsInScene() {
		return objectsInScene;
	}

	public ArrayList<EchoesActivity> getPrevious3activities() {
		return previous3activities;
	}
}
