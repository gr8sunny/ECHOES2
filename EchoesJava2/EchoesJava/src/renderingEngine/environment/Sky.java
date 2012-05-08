package renderingEngine.src.environment;

import javax.media.opengl.GL2;
import renderingEngine.src.screenCanvas;

public class Sky extends EchoesSceneElement
{
  private screenCanvas canvas = screenCanvas.theCanvas;
  
  public Sky(boolean autoAdd, boolean fadeIn, int fadingFrames)
  {
    super(autoAdd, fadeIn, fadingFrames);
  }   
  
  @Override
  public void renderObj(GL2 gl)
  {
    gl.glPushMatrix();
    gl.glDisable(GL2.GL_DEPTH_TEST);
    gl.glDisable(GL2.GL_LIGHTING);
    gl.glScaled(canvas.orthoCoordWidth/2, canvas.orthoCoordWidth/2/canvas.aspectRatio, 1);
    gl.glBegin(GL2.GL_QUADS);
    gl.glColor4d(0.303, 0.648, 0.853, transparancy);
    gl.glVertex2f(-1,-1);
    gl.glColor4d(0.303, 0.648, 0.853, transparancy);
    gl.glVertex2f(1,-1);
    gl.glColor4d(1, 1, 1, transparancy);
    gl.glVertex2f(1, 1);
    gl.glColor4d(0.303, 0.648, 0.853, transparancy);
    gl.glVertex2f(-1, 1);
    gl.glEnd();
    gl.glEnable(GL2.GL_LIGHTING);
    gl.glEnable(GL2.GL_DEPTH_TEST);
    gl.glPopMatrix();
  }
}
