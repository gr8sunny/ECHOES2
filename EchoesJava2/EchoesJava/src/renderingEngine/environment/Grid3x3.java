package renderingEngine.src.environment;

import javax.media.opengl.GL2;
import renderingEngine.src.screenCanvas;

public class Grid3x3 extends EchoesSceneElement
{
  private screenCanvas canvas = screenCanvas.theCanvas;
  
  public Grid3x3(boolean autoAdd, boolean fadeIn, int fadingFrames)
  {
    super(autoAdd, fadeIn, fadingFrames);
  }
  
  public void renderObj(GL2 gl)
  {
    float w = canvas.orthoCoordWidth;
    float h = canvas.orthoCoordWidth / canvas.aspectRatio;

    gl.glDisable(GL2.GL_LIGHTING);
    // Draw the x-axis in red
    gl.glColor4f(1, 1, 0, (float)0.1*transparancy);
    for (int i = -1; i <= 1; i++)
    {
      gl.glBegin(GL2.GL_LINES);
      gl.glVertex3f((float)i*w/6, h/2, 0);
      gl.glVertex3f((float)i*w/6, -h/2, 0);
      gl.glEnd();
      gl.glBegin(GL2.GL_LINES);
      gl.glVertex3f(-w/2, (float)i*h/6,0);
      gl.glVertex3f(w/2, (float)i*h/6,0);
      gl.glEnd();
    }
    gl.glEnable(GL2.GL_LIGHTING);
  }
}