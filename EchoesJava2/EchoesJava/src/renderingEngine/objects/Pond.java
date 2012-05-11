import java.util.Map;

//Translation from Py May 10 2012

public class Pond
{    
    //public classdocs
    //Pond(autoAdd=true, props={"type" "Pond"}, fadeIn = false, fadingFrames = 100, callback = None)
	private float shape = new float[40][2];
	private double[] pos={0, -2.5, 0};
    private float size=0.2;
    private float maxsize=0.1;
    //this.maxSize = 1
    //this.maxSize = 0.1...Py code had two declarations
    private boolean canGrow=true;
    private boolean canShrink=true;
    
	public Pond(boolean autoAdd, Map<String, String> properties, boolean fadeIn, int fadingFrames, Object callback)
    {       
        //*****whats app?
    	super(autoAdd, properties, fadeIn, fadingFrames, callback);
    	int pointIndex = 1;
		for (float deg = -180; deg < 1; deg+=10)
		{
			shape[pointIndex][0] = (float)(Math.cos(Math.toRadians(deg)));
			shape[pointIndex][1] = (float)Math.sin(Math.toRadians(deg);
			pointIndex++;
		}
		for (float deg = -90; deg < 91; deg+=10)
		{
			shape[pointIndex][0] = (float)(1+0.5*Math.cos(Math.toRadians(deg)));
			shape[pointIndex][1] = (float)(0.5+0.5*Math.sin(Math.toRadians(deg));
			pointIndex++;
		}
		for (float deg = -90; deg < 91; deg+=10)
		{
			shape[pointIndex][0] = (float)(1+0.2*Math.cos(Math.toRadians(deg)));
			shape[pointIndex][1] = (float)(1.2+0.2*Math.sin(Math.toRadians(deg)));
			pointIndex++;
		}
		for (float deg = 0; deg < 181; deg+=10)
		{
			shape[pointIndex][0] = (float)(Math.cos(Math.toRadians(deg)));
			shape[pointIndex][1] = (float)(1.4+0.5*Math.sin(Math.toRadians(deg)));
			pointIndex++;
		}
		for (float deg = 90; deg < 271; deg+=10)
		{
			shape[pointIndex][0] = (float)(-1+0.2*Math.cos(Math.toRadians(deg)));
			shape[pointIndex][1] = (float)(1.2+0.2*Math.sin(Math.toRadians(deg)));
			pointIndex++;
		}
		for (float deg = 90; deg < 271; deg+=10)
		{
			shape[pointIndex][0] = (float)(-1+0.5*Math.cos(Math.toRadians(deg)));
			shape[pointIndex][1] = (float)(0.5+0.5*Math.sin(Math.toRadians(deg)));
			pointIndex++;
		}
        //this.shape = [(math.cos(math.radians(deg)), math.sin(math.radians(deg))) for deg in xrange(-180, 1, 10)]
        //this.shape += [(1+ 0.5*math.cos(math.radians(deg)), 0.5+0.5*math.sin(math.radians(deg))) for deg in xrange(-90, 91, 10)]
        //this.shape += [(1+ 0.2*math.cos(math.radians(deg)), 1.2+0.2*math.sin(math.radians(deg))) for deg in xrange(-90, 91, 10)]
        //this.shape += [(math.cos(math.radians(deg)), 1.4+0.5*math.sin(math.radians(deg))) for deg in xrange(0, 181, 10)]
        //this.shape += [(-1+ 0.2*math.cos(math.radians(deg)), 1.2+0.2*math.sin(math.radians(deg))) for deg in xrange(90, 271, 10)]
        //this.shape += [(-1+ 0.5*math.cos(math.radians(deg)), 0.5+0.5*math.sin(math.radians(deg))) for deg in xrange(90, 271, 10)]
     }         
    public void renderObj(GL2 gl)
    {  
        gl.glPushMatrix();
        gl.glDisable(GL2.GL_DEPTH_TEST);

        gl.glTranslate(this.pos[0],this.pos[1],this.pos[2]);
        gl.glScale(this.size, this.size, this.size);
        
        gl.glRotate(70.0,1.0,0.0,0.0);
        gl.glRotate(35.0,0.0,0.0,-1.0);
        gl.glColor4f(0.576, 0.918, 1.0, this.transperancy);
        gl.glBegin(GL2.GL_LINE_STRIP);
        for v in this.shape
        {
        	gl.glVertex2f(v[0], v[1]);
        }
        gl.glEnd();
        gl.glVertex2f(this.shape[0][0], this.shape[0][1]);
        gl.glColor4f(0.376, 0.718, 1.0, this.transperancy);
        gl.glBegin(GL2.GL_TRIANGLE_FAN);
        gl.glVertex2f(0,0);
        for v in this.shape
        {
        	gl.glVertex2f(v[0], v[1]);
        }
        gl.glEnd();

        
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glPopMatrix()        ;
    }   
    public void grow()
    {
    	if (this.size < this.maxSize)
    	{
    		this.size += 0.005;
            this.app.canvas.rlPublisher.objectPropertyChanged(str(this.id), "pond_grow", str(this.size));
    	}
        else
        {
        	this.canGrow = false;
            this.canShrink = true;
        }
    }
    public void shrink()
    {   
    	if (this.size > this.minSize)
    	{
    		this.size -= 0.005;
            this.app.canvas.rlPublisher.objectPropertyChanged(str(this.id), "pond_shrink", str(this.size));
    	}
        else 
        {
        	this.canGrow = true;
            this.canShrink = false;
        }
    }
}