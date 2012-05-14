
import java.util.Map;
//all done
        
public class Sun extends EchoesObject
{ 
 //   public classdocs
    //Sun(autoAdd=true, props={"type" "Sun"}, fadeIn = false, fadingFrames = 100, callback=None)
    private double [] pos={3, 2.25, -0.5};
	private double size=0.5;
	private boolean objectCollisionTest = false;
	private boolean agentCollisionTest = false;
	private float[][] shape = new float[40][2];
	
    public Sun(boolean autoAdd, Map<String, String> properties, boolean fadeIn, int fadingFrames, Object callback)
    {       
        //********whats app?
    	super(autoAdd, properties, fadeIn, fadingFrames, callback);
    	int pointIndex = 1;
		for (float deg = 0; deg < 370; deg+=10)
		{
			shape[pointIndex][0] = (float)(Math.cos(Math.toRadians(deg)));
			shape[pointIndex][1] = (float)Math.sin(Math.toRadians(deg));
			pointIndex++;
		}
    }   
               
    public void renderObj(GL2 gl)
    {   
        gl.glPushMatrix();
        gl.glDisable(GL2.GL_DEPTH_TEST);

        gl.glTranslate(this.pos[0],this.pos[1],this.pos[2]);
        gl.glColor4f(1,1,0, this.transperancy);
        gl.glBegin(GL2.GL_TRIANGLE_FAN);
        gl.glVertex2f(0,0);
        gl.glColor4f(0.741, 0.878, 0.929, 0);
        for (float [] v : this.shape)
        { 
        	gl.glVertex2f(v[0], v[1]);
        }
        gl.glEnd();

        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glPopMatrix(); 
    }
}