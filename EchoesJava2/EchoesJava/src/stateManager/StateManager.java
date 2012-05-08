package stateManager;

import java.util.List;
import java.util.Map;
import echoesEngine.ListenerManager;
import utils.Enums.*;
import utils.Interfaces.*;

public class StateManager implements IStateManager 
{
	private FusionImpl fusionImpl;
	private String userName;

	public StateManager() 
	{
	  ListenerManager listenerMgr = ListenerManager.GetInstance();
	  IEventListener eventListener = (IEventListener)listenerMgr.retrieve(ListenerType.event);
	  fusionImpl = new FusionImpl(eventListener);
  }

  /* (non-Javadoc)
   * @see echoes._StateManagerOperations#getAgents(Ice.Current)
   */
  public List<String> getAgents() 
  {
      return null;
  }

  /* (non-Javadoc)
   * @see echoes._StateManagerOperations#getGazeObject(Ice.StringHolder, Ice.DoubleHolder, Ice.LongHolder, Ice.Current)
   */
  public void getGazeObject(String objId, Double confidence, Long duration) 
  {   
  }

  /* (non-Javadoc)
   * @see echoes._StateManagerOperations#getObjects(Ice.Current)
   */
  public List<String> getObjects() 
  {
  	return fusionImpl.getScreenObjects();
  }

  /* (non-Javadoc)
   * @see echoes._StateManagerOperations#getProperties(java.lang.String, Ice.Current)
   */
  public Map<String, String> getProperties(String objId)
  {
  	return fusionImpl.getProperties(objId);
  }

	public String getUserName() 
	{
		return userName;
	}

	public void setUserName(String userName) 
	{
		this.userName = userName;
	}
}
