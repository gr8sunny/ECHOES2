package renderingEngine.agents;

import java.util.HashMap;
import java.util.Map;
import renderingEngine.visual.EchoesGLCanvas;

public class EchoesAgent
{
  Map<String, String> properties = new HashMap<String, String>();
  Integer objectID = new Integer(-1);
  boolean collisonTest = true;
  
  public EchoesAgent(boolean autoAdd, Map<String, String> props)
  {
    properties = props;
    if (autoAdd)
      objectID = EchoesGLCanvas.theCanvas.addAgent(this, props);
  }
  
  public void render()
  {
    // All objects are added to the scene as plugins (see EchoesGLCanvas) 
    // and need to provide a render function if (they want to draw anything 
  }
      
  public void remove()
  {
      EchoesGLCanvas.removeAgent(objectID);
  }
}
  

