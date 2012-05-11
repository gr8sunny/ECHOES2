import java.util.Map;
import java.util.*; 
//Translation from Py May 10 2012
public class Container
{   
   // public classdocs
	private float [] pos = {0, 0, 0};
	private float size = 0.6;
	private float [][] shape = {{-1,1,0}, {-1,-1,0}, {1,-1,0}, {1,1,0}};
	private boolean publishRegion = true;
	private boolean canBeDraged = false;
	private String colour = "red";
	Hashtable colours = new Hashtable(); 
    //Container(autoAdd=true, props={"type" "Container"}, fadeIn = false, fadingFrames = 100, callback = None)
    public Container(boolean autoAdd, Map<String, String> properties, boolean fadeIn, int fadingFrames, Object callback)
    {       
        super(app, autoAdd, properties, fadeIn, fadingFrames, callback);
       
        this.pos[1] = this.app.canvas.getRegionCoords("ground")[1][1];   
        float [] temporaryIntArray = {0.735, 0.197, 0.286};
        this.colours.put("red", temporaryIntArray); 
        float [] temporaryIntArray1 = {0.921,0.832,0.217};
        this.colours.put("yellow", temporaryIntArray1);
        float [] temporaryIntArray2 = {0.220,0.481,0.628};
        this.colours.put("blue", temporaryIntArray2);
        float [] temporaryIntArray3 = {0.439,0.633,0.245};
        this.colours.put("green", temporaryIntArray3);
        this.balls = [];//***array of what class?
    }
    
    public void setAttr(String item, String value)
    {
    	if (item == "colour")
            if (not value in this.colours)
            {
            	value = "red"; 
            }
            this.app.canvas.rlPublisher.objectPropertyChanged(str(this.id), "container_colour", str(value))      
    
        object.__setattr__(item, value)
    }
    
    public void renderObj()
    {
    	gl.glPushMatrix();
        gl.glDisable(GL2.GL_DEPTH_TEST);
        gl.glTranslate(this.pos[0], this.pos[1], this.pos[2]);
        gl.glScalef(this.size, this.size, this.size);
        c = this.colours[this.colour];
        gl.glColor4f(c[0], c[1], c[2], this.transperancy*0.4);
        gl.glBegin(GL2.GL_QUADS);
        for v in this.shape
        {
        	gl.glVertex3f(v[0],v[1],v[2]);
        }
        gl.glEnd();
        gl.glColor4f(c[0], c[1], c[2], this.transperancy);
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(this.shape[0][0]*1.1, this.shape[0][1], 0);
        gl.glVertex3f(this.shape[0][0]*0.9, this.shape[0][1], 0);
        gl.glVertex3f(this.shape[1][0]*0.9, this.shape[1][1], 0);
        gl.glVertex3f(this.shape[1][0]*1.1, this.shape[1][1], 0);
        gl.glEnd();
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(this.shape[1][0]*1.1, this.shape[1][1]*0.9, 0);
        gl.glVertex3f(this.shape[1][0]*1.1, this.shape[1][1]*1.1, 0);
        gl.glVertex3f(this.shape[2][0]*1.1, this.shape[2][1]*1.1, 0);
        gl.glVertex3f(this.shape[2][0]*1.1, this.shape[2][1]*0.9, 0);
        gl.glEnd();
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(this.shape[3][0]*1.1, this.shape[3][1], 0);
        gl.glVertex3f(this.shape[3][0]*0.9, this.shape[3][1], 0);
        gl.glVertex3f(this.shape[2][0]*0.9, this.shape[2][1], 0);
        gl.glVertex3f(this.shape[2][0]*1.1, this.shape[2][1], 0);
        gl.glEnd();
        gl.glEnable(GL2.GL_DEPTH_TEST);        
        gl.glPopMatrix();
    }                         
    public void startDrag(newXY)
    {
    	this.beingDragged = true;
     //   # Based on http//web.iiit.ac.in/~vkrishna/data/unproj.html
        projection = glGetDoublev(GL2.GL_PROJECTION_MATRIX);
        modelview = glGetDoublev(GL2.GL_MODELVIEW_MATRIX);
        viewport = glGetIntegerv(GL2.GL_VIEWPORT);
        windowZ = glReadPixels(newXY[0], viewport[3]-newXY[1], 1, 1, GL2.GL_DEPTH_COMPONENT, GL2.GL_FLOAT);
        worldCoords = gluUnProject(newXY[0], viewport[3] - newXY[1], windowZ[0][0], modelview, projection, viewport);
        this.worldDragOffset = [this.pos[0]-worldCoords[0], this.pos[1]-worldCoords[1], 0] ;
    } 
    public void stopDrag()
    {
    	this.beingDragged = false;
    }

    public void drag(newXY)
    {
    	if (this.interactive && this.canBeDraged)
    	{    //# Based on http//web.iiit.ac.in/~vkrishna/data/unproj.html
            projection = glGetDoublev(GL2.GL_PROJECTION_MATRIX);
            modelview = glGetDoublev(GL2.GL_MODELVIEW_MATRIX);
            viewport = glGetIntegerv(GL2.GL_VIEWPORT);
            windowZ = glReadPixels(newXY[0], viewport[3]-newXY[1], 1, 1, GL2.GL_DEPTH_COMPONENT, GL2.GL_FLOAT);
            
            worldCoords = gluUnProject(newXY[0], viewport[3] - newXY[1], windowZ[0][0], modelview, projection, viewport);
            if (this.beingDragged)
            {
            	if (this.fallTopublic voidaultHeight)
                {
                	this.pos = [worldCoords[0]+this.worldDragOffset[0], max(this.public voidaultHeight, worldCoords[1]+this.worldDragOffset[1]), this.pos[2]];
                }
                else
                {
                	this.pos = [worldCoords[0]+this.worldDragOffset[0], worldCoords[1]+this.worldDragOffset[1], this.pos[2]];                
                }
                this.locationChanged = true;
            }
    	}
    }  
    public void addBall(ball)
    {
    	this.balls.append(ball);
        ball.container = //*****equal to what?
        ball.floor = this.pos[1]-this.size;
        ball.left = this.pos[0]-this.size;
        ball.right = this.pos[0]+this.size;
        ball.bounceWithinScene = true;
        ball.size = max(this.size/2.5, ball.size/3);
        ball.stopDrag();
        ball.pos = [ball.pos[0], ball.pos[1], -10];
        ball.interactive = false;
        ball.publishBounce = false;
    }   
                       
    public void reward(type="Bubbles")
    {
    	num_balls = 0;
        if (type == "Bubbles")
        {
        	for b in this.balls
        	{
        		bubble = Bubbles.EchoesBubble(this.app, true, fadeIn=true, fadingFrames=10);
                bubble.setStartPos(b.pos);
                bubble.willBeReplaced = false; 
                bubble.canMerge = false;    
                b.remove(false);
                num_balls += 1;
        	}
            this.remove(true);
        }
        else if (type == "Fireworks")
        {   
        	for b in this.balls
        	{
        		b.explode(inFrames=random.randint(0,100));
                num_balls += 1;
        	}
            this.remove(true);
        }
        else if (type == "Bees")
        { 
        	for b in this.balls
        	{
        		bee = Bee(this.app, true, fadeIn=true, fadingFrames=10);
                bee.setStartPos(b.pos);
                bee.setTargetPos([random.randint(-5,5), 5]);
                bee.removeAtTargetPos = true;
                b.remove(false);
                num_balls += 1;
        	}
            this.remove(true);
        }
        this.app.canvas.rlPublisher.objectPropertyChanged(str(this.id), "container_reward", str(num_balls));
        
    }   
    public void remove(fadeOut = false, fadingFrames = 100)
    {
    	super(Container, ).remove(fadeOut, fadingFrames)
    }
               
}          
        