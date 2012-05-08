package pedagogicComponent;

import java.util.List;
import utils.Interfaces.*;

public class PedagogicComponent implements IPedagogicComponent 
{
	private PCcomponentHandler pCcompH;

	
	public void setPCcomponentHandler(PCcomponentHandler value) 
  {
    this.pCcompH = value;
  }

	public void loadChildProfile(String name) 
	{
		pCcompH.getPCcs().childStateH.loadInitialChildAttributes(name);

		// Add this at the end so the agent shows up ...
		pCcompH.getPCcs().agentInvolvementH.setIsOpenToAgent(true);
	}

	public void changeScene() 
	{
		pCcompH.getPCcs().sceneH.decideNextScene();
	}

	public void sendActionStartedAEnameAndArgs(String actionName, List<String> actionArgs) 
	{
		pCcompH.getPCcs().agentH.setAEnameAndArgsForLastActionStarted(actionName, actionArgs);		
	}
}
