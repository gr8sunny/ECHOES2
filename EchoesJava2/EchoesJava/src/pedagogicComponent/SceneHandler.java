package pedagogicComponent;

import utils.Interfaces.IActionEngine;
import utils.Interfaces.IDramaManager;
import utils.Enums.EchoesScene;

/**
 * Makes decisions about which scene to load/ whether to change scene and which
 * scene to change to
 * 
 * @author katerina avramides
 * 
 */
public class SceneHandler extends PCcomponentHandler {

	Activity initialActivity;

	/**
	 * 
	 * @param pCc
	 * @param dmPrx
	 * @param aePrx
	 */
	public SceneHandler(PCcomponents pCc, IDramaManager dmPrx,
			IActionEngine aePrx) {
		super(pCc, dmPrx, aePrx);
	}

	/**
	 * Sets the initial activity after the introduction scene.
	 * 
	 * @param initialActivity
	 * the initial activity.
	 */
	public void setInitialActivity(Activity initialActivity) {
		this.initialActivity = initialActivity;
		System.out.println("Set initial activity: "
				+ initialActivity);
	}

	/**
	 * Decides which scene to load. Communicates with other components of the PC
	 * to decide on activities, goals, etc
	 */
	public void decideNextScene() {
		// move on from the intro scene to an appropriate start activity
		EchoesScene currentScene = dmPrx.getCurrentScene();
		if (currentScene == EchoesScene.Intro) {
			if (initialActivity != null) {
                initialActivity.startActivity(getPCcs().ps);
			}
		}
	}
}
