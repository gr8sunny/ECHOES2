package renderingEngine.src.environment;

import javax.media.opengl.GL2;
import renderingEngine.src.screenCanvas;

public class Axis extends EchoesSceneElement
{
  public Axis(boolean autoAdd, boolean fadeIn, int fadingFrames)
  {
    super( autoAdd, fadeIn, fadingFrames);
  }
             
  public void renderObj(GL2 gl)
  {
    gl.glDisable(GL2.GL_LIGHTING);
    // Draw the x-axis in red
    gl.glColor4d(1.0, 0.0, 0.0, transparancy);
    gl.glBegin(GL2.GL_LINES);
    gl.glVertex3d(0,0,0);
    gl.glVertex3d(1,0,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_TRIANGLE_FAN);
    gl.glVertex3d(1.0, 0.0,  0.0 );
    gl.glVertex3d(0.8, 0.07, 0.0 );
    gl.glVertex3d(0.8, 0.0,  0.07);
    gl.glVertex3d(0.8,-0.07, 0.0 );
    gl.glVertex3d(0.8, 0.0, -0.07);
    gl.glVertex3d(0.8, 0.07, 0.0 );
    gl.glEnd();

    // Draw the y-axis in green
    gl.glColor4d(0.0, 1.0, 0.0, transparancy);
    gl.glBegin(GL2.GL_LINES);
    gl.glVertex3d(0,0,0);
    gl.glVertex3d(0,1,0);
    gl.glEnd();
    gl.glBegin(GL2.GL_TRIANGLE_FAN);;
    gl.glVertex3d( 0.0,  1.0, 0.0 );
    gl.glVertex3d( 0.07, 0.8, 0.0 );
    gl.glVertex3d( 0.0,  0.8, 0.07);
    gl.glVertex3d(-0.07, 0.8, 0.0 );
    gl.glVertex3d( 0.0,  0.8,-0.07);
    gl.glVertex3d( 0.07, 0.8, 0.0 );
    gl.glEnd();

    // Draw the z-axis in blue
    gl.glColor4d(0.0, 0.0, 1.0, transparancy);
    gl.glBegin(GL2.GL_LINES);
    gl.glVertex3d(0,0,0);
    gl.glVertex3d(0,0,1);
    gl.glEnd();
    gl.glBegin(GL2.GL_TRIANGLE_FAN);
    gl.glVertex3d( 0.0,  0.0,  1.0);
    gl.glVertex3d( 0.07, 0.0,  0.8);
    gl.glVertex3d( 0.0,  0.07, 0.8);
    gl.glVertex3d(-0.07, 0.0,  0.8);
    gl.glVertex3d( 0.0, -0.07, 0.8);
    gl.glVertex3d( 0.07, 0.0,  0.8);
    gl.glEnd();
    gl.glEnable(GL2.GL_LIGHTING);
  }
}
