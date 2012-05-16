import java.util.Map;
//all done

public class Shed extends EchoesObject
{    
 //   public classdocs
    //Shed(autoAdd=true, props={"type" "Shed"}, fadeIn = false, fadingFrames = 100, callback=None)
    private boolean objectCollisionTest = false;
    private boolean agentCollisionTest = false;
    private double [] pos={-2,-0.5,-3};
    private double size=2.5;
	private double [][]shape={{-0.5, -0.5}, {0.5, -0.5}, {0.5, 0.5}, {-0.5, 0.5}};
	private double[][] texshape = {{0, 0}, {1, 0}, {1, 1}, {0, 1}};
	
	public Shed(boolean autoAdd, Map<String, String> properties, boolean fadeIn, int fadingFrames, Object callback)
    {       
        super(autoAdd, properties, fadeIn, fadingFrames, callback);
        loadTexture("visual/images/Shed.png");//*****whats setImage?
    }
    public void setAttr(String item, String value)
    {
    	if (item == "pos")
        {
    		//pass
        }
                        
        setAttr(item, value);
    }       
    public void renderObj(GL2 gl)
    {     
        //overwriting the render method to draw the flower
        gl.glPushMatrix();
        gl.glEnable( GL2.GL_ALPHA_TEST );
        gl.glAlphaFunc( GL2.GL_GREATER, 0.1 );
        
        gl.glEnable( GL2.GL_TEXTURE_2D );
        gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
        gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
        gl.glBindTexture(GL2.GL_TEXTURE_2D, this.texture);
        
        gl.glTranslate(this.pos[0], this.pos[1], this.pos[2]);  
        gl.glScalef(this.size, this.size, this.size);
        gl.glColor4f(1, 1, 1, this.transperancy);
        gl.glBegin(GL2.GL_QUADS);
        int ti = 0;
        for(double[] v : this.shape)
        {
        	gl.glTexCoord2d(this.texshape[ti][0], this.texshape[ti][1]);
            gl.glVertex3f(v[0], v[1], this.pos[2]);
            ti += 1;
        }
        gl.glEnd();
        gl.glDisable(GL2.GL_TEXTURE_2D);
        gl.glDisable(GL2.GL_ALPHA_TEST);
        gl.glPopMatrix();
    }
}