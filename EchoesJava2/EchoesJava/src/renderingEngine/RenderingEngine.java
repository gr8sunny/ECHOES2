package renderingEngine;

import java.util.List;

import utils.Interfaces.IRenderingEngine;

public class RenderingEngine implements IRenderingEngine
{

  @Override
  public void loadScenario(String name)
  {
  
  }

  @Override
  public void endScenario(String name)
  {
  
  }

  @Override
  public String addObject(String objectType)
  {
    return null;
  }

  @Override
  public void removeObject(String objId)
  {

  }

  @Override
  public void setWorldProperty(String propName, String propValue)
  {
 
  }

  @Override
  public void setObjectProperty(String objId, String propName, String propValue)
  {

  }

  @Override
  public String addAgent(String agentType)
  {
    return null;
  }

  @Override
  public String addAgentWithPose(String agentType, String pose)
  {
    return null;
  }

  @Override
  public String removeAgent(String agentId)
  {
    return null;
  }

  @Override
  public boolean executeAction(String agentId, String action,
      List<String> details)
  {
    return false;
  }

  @Override
  public String getAttentionProbability(String objectId)
  {
    return null;
  }

}
