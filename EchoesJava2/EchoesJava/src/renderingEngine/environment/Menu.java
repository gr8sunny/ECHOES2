package renderingEngine.src.environment;

import javax.media.opengl.GL2;
import utils.Logger;
import renderingEngine.src.screenCanvas;

@SuppressWarnings("unused")
public class Menu extends EchoesSceneElement
{
  private screenCanvas canvas = screenCanvas.theCanvas;
  private String userList[];
  private String utexts[];
  private float firstPos[] = new float[3];
  private float pos[] = new float[3];
  private float fontSize = (float)0.5;
  private float newLine = (float)0.5;
  private float romanCharHeight;
  private float romanCharWidth;
  private int selection = -1;
  private Integer score = new Integer(0);

  public Menu(boolean autoAdd, boolean fadeIn, int framesToFade)
  {
    super(autoAdd, fadeIn, framesToFade);
  }
  
  public Menu(boolean autoAdd, boolean fadeIn, int framesToFade, float fontsize, String userlist[], float posn[])
  {
    super(autoAdd, fadeIn, framesToFade);
    pos = posn;
    fontSize = fontsize;
    newLine = (float)(fontSize * 1.5);
    userList = userlist;
  }

  public void renderObj(GL2 gl)
  {
    int nl = 0;
    for (String user : utexts)
    {
      //user.draw_text(firstPos[0], firstPos[1]-nl, 1.0/50 * fontSize);
      nl += newLine;
    }
  }

  public void click(float pos[])
  {
    int index = 0;
    int nl = 0;
    for (String user : utexts)
    {
      float h = 10; //user._aloc_text._texture_size;
      float w = h;
      w *= (float)1.0/50 * fontSize;
      h *= (float)1.0/50 * fontSize;
      float y = 10; //(firstPos[0], firstPos[1]-nl);
      float x = y;
      if (pos[0] > x && pos[0] < x+w && pos[1] > y && pos[1] < y+h)
      {
          Logger.Log("info", "Selected " + userList[index]);
          selection = index;
          //canvas.rlPublisher.userStarted(userList[index]);
          break;
      }
      index +=1;
      nl += newLine;
    }
  }
}
        
