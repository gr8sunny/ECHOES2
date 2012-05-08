package renderingEngine.src.environment;

//import com.sun.opengl.util.GLUT;
import javax.media.opengl.GL2;
import renderingEngine.src.screenCanvas;

@SuppressWarnings("unused")
public class Score extends EchoesSceneElement
{
  private screenCanvas canvas = screenCanvas.theCanvas;
  private float pos[] = new float[3];
  private float fontSize = (float)0.5;
  private float romanCharHeight;
  private float romanCharWidth;
  private Integer score = new Integer(0);
  
  public Score(boolean autoAdd, boolean fadeIn, int fadingFrames, float fontsize)
  {
    super(autoAdd, fadeIn, fadingFrames);
    pos[0] = (float)(-canvas.orthoCoordWidth/2.0 + 0.2);
    pos[1] = (float)(-canvas.orthoCoordWidth / canvas.aspectRatio / 4.0 - fontsize);
    pos[2] = 10;
    fontSize = fontsize;
    romanCharHeight = (float) (119.05 + 33.33);
    romanCharWidth = (float) 104.76;
  }
                
  public void renderObj(GL2 gl)
  { 
    float charscale = fontSize / romanCharHeight; 
    gl.glLineWidth(8);
    gl.glColor4d(0.141, 0.278, 0.929, 0.4*transparancy);
    gl.glPushMatrix();
    gl.glDisable(GL2.GL_DEPTH_TEST);
    gl.glDisable(GL2.GL_LIGHTING);     
    gl.glTranslatef(pos[0],pos[1],pos[2]);
    gl.glScalef(charscale, charscale, charscale);
    //String s = score.toString();
    //for (char letter : s.toCharArray())
    //  gl.glutStrokeCharacter(GLUT.GLUT_STROKE_ROMAN, letter);
    gl.glEnable(GL2.GL_DEPTH_TEST);
    gl.glEnable(GL2.GL_LIGHTING);
    gl.glPopMatrix();  
    gl.glLineWidth(1);
  }

  public void setScore(int value)
  {
    score = value;
  }
      
   public void reset()
   {
      score = 0;
   }
      
   public void increment()
   {
      score += 1;
   }
}
