import java.util.Map;

//translation from Py May 11th
public class LifeTree extends EchoesObject
{    
 //   public classdocs
    //__init__(autoAdd=true, props={"type" "LifeTree"}, fadeIn = false, fadingFrames = 100, callback=None)
	private float [][]shape = {{-0.5, -0.5}, {0.5, -0.5}, {0.5, 0.5}, {-0.5, 0.5}};
	private float [][]texshape = {{0, 0}, {1, 0}, {1, 1}, {0, 1}};
	private float [] pos= {-2.5,-0.5,-1};
	public LifeTree(boolean autoAdd, Map<String, String> props, boolean fadeIn, int fadingFrames, Object callback)
    {        
        super(autoAdd, props, fadeIn, fadingFrames, callback);
        
        this.size = 3.5;
        
        loadTexture("visual/images/LifeTree.png");
        
        this.leaves = [None, None, None, None];
    }
    public void setAttr(String item, String value)
    {
    	if (item == "pos")
    	{//    pass
    	}             
        setAttr(item, value);
    }
    public void getFreeBranch()
    {
    	branch = 0;
        for leaf in this.leaves
        {
        	if (!leaf)
                return branch;
            branch += 1;
        }
        return -1;
    }   
    public void renderObj(GL2 gl)
    {    
     //   overwriting the render method to draw the flower
                    
        gl.glPushMatrix();
        gl.glEnable( GL2.GL_ALPHA_TEST );
        gl.glAlphaFunc( GL2.GL_GREATER, 0.1 );
        gl.glEnable( GL2.GL_TEXTURE_2D );
        gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
        gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
        gl.glBindTexture(GL2.GL_TEXTURE_2D, this.texture);
        gl.glTranslate(this.pos[0], this.pos[1], this.pos[2]) ; 
        gl.glScalef(this.size, this.size, this.size);
        gl.glColor4f(1, 1, 1, this.transperancy);
        gl.glBegin(GL2.GL_QUADS);
        int ti = 0;
        for(float[] v : shape)
        {
        	gl.gl.glTexCoord2d(this.texshape[ti][0], this.texshape[ti][1]);
            gl.gl.glVertex3f(v[0], v[1], this.pos[2]);
            ti += 1;
        }
        gl.gl.glEnd();
        gl.gl.glDisable( GL2.GL_TEXTURE_2D );
        gl.gl.glDisable( GL2.GL_ALPHA_TEST );
        gl.gl.glPopMatrix();
    }
}