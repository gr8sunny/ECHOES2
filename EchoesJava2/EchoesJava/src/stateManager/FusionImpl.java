package stateManager;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import utils.Interfaces.*;
import utils.Enums.*;
import utils.Logger;

public class FusionImpl implements IUserHeadListener, IRenderingListener, IAgentListener 
{
	private IEventListener eventPrx;
  private ScreenRegion lastGazeRegion;
  private long regionTimestamp;
  private boolean gazePublished;
  private Map<String, Map<String, String>> screenObjects;
  private ScreenRegion agentRegion = ScreenRegion.ScreenTopMiddle;  
  /** How long to wait before considering what they did a fixation */
  private long gazeTimeout;
    
  public FusionImpl(IEventListener eventPrx) 
  {
  	this.eventPrx = eventPrx;
      
    // Initial default
    this.gazeTimeout = 200;
    
    reset();
  }
  
  public void reset() 
  {
    this.screenObjects = Collections.synchronizedMap(new HashMap<String, Map<String,String>>());
    this.agentRegion = null;
    this.lastGazeRegion = null;
  }

  public List<String> getScreenObjects() {
  	return new LinkedList<String>(screenObjects.keySet());
  }
  
  public Map<String, String> getProperties(String objId) {
  	synchronized(screenObjects) {
  		if (screenObjects.containsKey(objId)) {
  			return screenObjects.get(objId);
  		} else {
  			Logger.LogWarning("No object with ID " + objId);
  			return null;
  		}
  	}
  }
  
  public void setGazeTimeout (long gazeTimeout) {
  	Logger.Log("fine", "Set threshold to " + gazeTimeout);
      this.gazeTimeout = gazeTimeout;
  }
  
  /* (non-Javadoc)
   * @see echoes._UserHeadListenerOperations#gaze(echoes.ScreenRegion, Ice.Current)
   */
  public synchronized void gaze(ScreenRegion region) {
  	Logger.Log("fine", "gaze(" + region + ")");
      if (region != lastGazeRegion) {
      	Logger.Log("fine", "New gaze region: " + region);
          lastGazeRegion = region;
          regionTimestamp = System.currentTimeMillis();
          gazePublished = false;
      } else {
          long duration = System.currentTimeMillis() - regionTimestamp;
      	Logger.Log("fine", "gazed at " + region + " for " + duration + " ms");

          if (!gazePublished && duration > gazeTimeout) {
          	if (region != ScreenRegion.ScreenUnknown) {
                String details = "NONE";
                if (region == agentRegion || region == ScreenRegion.ScreenTopMiddle) {
                    details = "AGENT";
                } else {
                    for (String objId : screenObjects.keySet()) {
                    	String regionStr = screenObjects.get(objId).get("ScreenRegion");
                    	if (regionStr != null) {
	                        ScreenRegion objRegion = ScreenRegion.valueOf(regionStr);
	                        if (objRegion == region) {
	                            details = objId;
	                            break;
	                        }
                    	}
                    }
                }
                Logger.Log("fine", "Publishing gazeEvent " + details + " " + duration);
                eventPrx.userGazeEvent(details, duration);
          	}
              // gazePublished = true;
              regionTimestamp = System.currentTimeMillis();
          } else if (gazePublished) {
          	Logger.Log("fine", "gaze already published; not publishing again");
          } else {
          	Logger.Log("fine", "not publishing because duration " + duration + " is <= threshold " + gazeTimeout);
          }
      }
  }

  /* (non-Javadoc)
   * @see echoes._UserHeadListenerOperations#userExpression(echoes.FacialExpression, Ice.Current)
   */
  public void userExpression(FacialExpression expression) {
      // TODO Auto-generated method stub
      
  }

  /* (non-Javadoc)
   * @see echoes._UserHeadListenerOperations#userLocation(double, double, double, Ice.Current)
   */
  public void userLocation(double x, double y, double z) {
      // TODO Auto-generated method stub
      
  }

  /* (non-Javadoc)
   * @see echoes._RenderingListenerOperations#agentAdded(java.lang.String, java.util.Map, Ice.Current)
   */
  public void agentAdded(String agentId, Map<String, String> props) {
      // TODO Auto-generated method stub
      
  }

  /* (non-Javadoc)
   * @see echoes._RenderingListenerOperations#agentPropertyChanged(java.lang.String, java.lang.String, java.lang.String, Ice.Current)
   */
  public void agentPropertyChanged(String agentId, String propName, String propValue) {
      if (propName.equals("ScreenRegion")) {
          agentRegion = ScreenRegion.valueOf(propValue);
      }
  }

  /* (non-Javadoc)
   * @see echoes._RenderingListenerOperations#agentRemoved(java.lang.String, Ice.Current)
   */
  public void agentRemoved(String agentId) {
      // TODO Auto-generated method stub
      
  }

  /* (non-Javadoc)
   * @see echoes._RenderingListenerOperations#objectAdded(java.lang.String, java.util.Map, Ice.Current)
   */
  public void objectAdded(String objId, Map<String, String> props) {
      synchronized(screenObjects) {
          if (screenObjects.get(objId) != null) {
              Logger.LogWarning("Object with id " + objId + " already exists");
          }
          screenObjects.put(objId, props);
          // Logger.Log("info", "Screen objects now " + screenObjects);
      }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * echoes._RenderingListenerOperations#objectPropertyChanged(java.lang.String
   * , java.lang.String, java.lang.String, Ice.Current)
   */
  public void objectPropertyChanged(String objId, String propName, String propValue) {
      synchronized (screenObjects) {
          Map<String, String> properties = screenObjects.get(objId);
          if (properties == null) {
              Logger.LogWarning(
                      "No object with ID " + objId + " exists; can't modify it");
          } else {
              properties.put(propName, propValue);
          }
          // Logger.Log("info", "Screen objects now " + screenObjects);
      }
  }

  /* (non-Javadoc)
   * @see echoes._RenderingListenerOperations#objectRemoved(java.lang.String, Ice.Current)
   */
  public void objectRemoved(String objId) {
      synchronized(screenObjects) {
          if (screenObjects.remove(objId) == null) {
              Logger.LogWarning("No object with id " + objId + " found");
          }
          // Logger.Log("info", "Screen objects now " + screenObjects);
      }
  }

  /* (non-Javadoc)
   * @see echoes._RenderingListenerOperations#scenarioEnded(java.lang.String, Ice.Current)
   */
  public void scenarioEnded(String name) {
      // TODO Auto-generated method stub
      
  }

  /* (non-Javadoc)
   * @see echoes._RenderingListenerOperations#scenarioStarted(java.lang.String, Ice.Current)
   */
  public void scenarioStarted(String name) {
  	Logger.Log("info", "Scenario started: " + name);
  }

  /* (non-Javadoc)
   * @see echoes._RenderingListenerOperations#worldPropertyChanged(java.lang.String, java.lang.String, Ice.Current)
   */
  public void worldPropertyChanged(String propName, String propValue) {
      // TODO Auto-generated method stub
      
  }

  /* (non-Javadoc)
   * @see echoes._AgentListenerOperations#agentActionCompleted(java.lang.String, java.lang.String, java.util.List, Ice.Current)
   */
  public void agentActionCompleted(String agentId, String action, List<String> details) {
      // TODO Auto-generated method stub
  }

  /* (non-Javadoc)
   * @see echoes._AgentListenerOperations#agentActionFailed(java.lang.String, java.lang.String, java.util.List, java.lang.String, Ice.Current)
   */
  public void agentActionFailed(String agentId, String action, List<String> details,String reason) {
      // TODO Auto-generated method stub 
  }

  /* (non-Javadoc)
   * @see echoes._AgentListenerOperations#agentActionStarted(java.lang.String, java.lang.String, java.util.List, Ice.Current)
   */
  public void agentActionStarted(String agentId, String action, List<String> details) {
      // TODO Auto-generated method stub
      
  }
  
  /* (non-Javadoc)
   * @see echoes._RenderingListenerOperations#userTouchedObject(java.lang.String, Ice.Current)
   */
  public void userTouchedObject(String objId) {
  	Logger.Log("info", "userTouchedObject " + objId);
      eventPrx.userTouchEvent(objId);
  }

	public void userStarted(String name) {
		// TODO Auto-generated method stub
		
	}

	public void faceSeen(boolean faceLeft, boolean faceRight,
			boolean faceMiddle) {
		// TODO Auto-generated method stub
		
	}

	public void userTouchedAgent(String agentId) {
		// TODO Auto-generated method stub
	}
}
