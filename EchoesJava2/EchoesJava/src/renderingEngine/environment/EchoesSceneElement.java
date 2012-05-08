package renderingEngine.src.environment;

import javax.media.opengl.GL2;
import renderingEngine.src.screenCanvas;

public class EchoesSceneElement
{
  protected boolean autoAdd = true;
  protected boolean fadingIn = false;
  protected boolean fadingOut = false;
  protected float transparancy = 1;
  protected int fadingFrames = 100;
  protected int id;
  
  public EchoesSceneElement(boolean autoAdd, boolean fadeIn, int framesToFade)
  {
    id = autoAdd ? screenCanvas.theCanvas.addSceneElement(this) : -1;
    fadingOut = false;
    fadingIn = fadeIn;
    fadingFrames = framesToFade;
    if(fadeIn)
      transparancy = 0;
    else
      transparancy = 1;
    }
          
    public void render(GL2 gl)
    {
      if (fadingIn)
      {
        transparancy += 1.0 / fadingFrames;
        if(transparancy >= 1.0)
        {
          transparancy = 1;
            fadingIn = false;
        }
      }
      else if (fadingOut)
      {
        transparancy -= 1.0 / fadingFrames;
        if(transparancy <= 0)
          remove(false, 100);
      }
      
      renderObj(gl);
    }
    
    public void renderObj(GL2 gl)
    {
    }
  
    public void remove(boolean fadeOut, int frames)
    {
      if(fadeOut)
      {
        fadingOut = true;
        fadingFrames = frames;
        transparancy = 1;
      }
      else
        screenCanvas.theCanvas.removeSceneElement(id);
    }
  }
