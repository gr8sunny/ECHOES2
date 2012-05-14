import java.util.Map;

//Translation from Py May 8 2012

/*
import environment
import objects.Plants
import PIL.Image
import sound.EchoesAudio
import Bubbles, PlayObjects
import Motions
import Logger
*/
/*Not sure if Cloud extends EchoesObject but seeing the super() call I guessed that should be it
 * 
 */
public class Bee extends EchoesObject
{    
 //   public classdocs
	//Bee(autoAdd=true, props = {"type" "Bee"}, fadeIn = false, fadingFrames = 100, randomSize = true, callback=None)
	private BezierMotion bezierMotion = new BezierMotion();
	private float [] orientation = {0, 0, 0};
    
    private float [][] shape = {{-1, -1}, {1, -1}, {1, 1}, {-1, 1}};
    private float [][] texshape = {{1, 0}, {1, 1}, {0, 1}, {0, 0}};
    private float maxSize = 1.5;
    private float speed = 0.002;
    private boolean moving = true;
    private boolean floatingXY = true;
    private boolean floatingSound = false;
    private boolean canBeClicked = true;
    private boolean canBeDraged = true;


	public void Bee(boolean autoAdd, Map<String, String> props, boolean fadeIn, int fadingFrames, boolean randomSize, Object callback)
    {       
        super(autoAdd, props, fadeIn, fadingFrames, callback);
        //super(Bee, ).initBezierVars();
        
        if (randomSize)
            this.size = 0.15 + Math.random() * 0.1;
        else
            this.size = 0.3; 
            
        loadTexture("visual/images/bee.png");        
        
        newStartPos();
        bezierMotion.newCtrlPoints(null);
        
        if (sound.EchoesAudio.soundPresent)
            this.buzz = sound.EchoesAudio.playSound("buzz.wav", loop=true, vol=0.0);
        else
            this.buzz = None;
    }               
    public void setAttr(String item, String value)
    {
    	setAttr(item, value);
    }                     
    public void renderObj()
    {     
     //   overwriting the render method to draw the bubble
        oldpos = this.pos
        if (this.moving && !this.beingDragged)
        {
        	this.pos = this.nextBezierPos(this.floatingXY);
            this.orientation[0] = this.pos[0]-oldpos[0]; 
            this.orientation[1] = this.pos[1]-oldpos[1]; 
            this.orientation[2] = this.pos[2]-oldpos[2];              
            if (this.removeAtTargetPos && this.bezierIndex > 0.95)
            {
            	this.remove(true, 100);
            }
        }
        if (this.buzz && !this.fadingOut)
        {
        	vel = Math.hypot(this.orientation[0], this.orientation[1]);
            this.buzz.mul = min(0.8, vel*200);
            this.buzz.speed =  1 + (vel*10);
        }
                    
        gl.glPushMatrix();
        gl.glEnable( GL2.GL_ALPHA_TEST );
        gl.glAlphaFunc( GL2.GL_GREATER, 0.1 );        
        gl.glEnable( GL2.GL_TEXTURE_2D );
        gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
        gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
        gl.glBindTexture(GL2.GL_TEXTURE_2D, this.texture);
        gl.glTranslate(this.pos[0], this.pos[1], this.pos[2]);
        gl.glRotate(math.degrees(math.atan2(this.orientation[1], this.orientation[0])), 0,0,1);
        gl.glScalef(this.size, this.size, this.size);
        gl.glColor4f(1, 1, 1, this.transperancy);
        gl.glBegin(GL2.GL_QUADS);
        ti = 0;
        for(float [] v : this.shape)
        {
        	gl.glTexCoord2d(this.texshape[ti][0], this.texshape[ti][1]);
            gl.glVertex3f(v[0], v[1], this.pos[2]);
            ti += 1;
        }
        gl.glEnd();
        gl.glDisable( GL2.GL_TEXTURE_2D );
        gl.glDisable( GL2.GL_ALPHA_TEST );
        gl.glPopMatrix();
    
        if (this.showCtrlPoints)
        {
        	gl.glPushMatrix();
            gl.glPointSize (4.0);
            gl.glColor4f(1.0,0,0,1.0);
            gl.glBegin (GL2.GL_POINTS);
            for i,p in this.ctrlpoints.iteritems()
            {
            	gl.glVertex3f (p[0], p[1], p[2]);
            }
            gl.glEnd ();
            gl.glPointSize (1.0);
            gl.glPopMatrix();
        }
    }         
    public void newstartpos()
    {
    	x = canvas.orthoCoordWidth/2 - (Math.random() * canvas.orthoCoordWidth);
        y = random.choice([-1,1])*canvas.orthoCoordWidth/2/canvas.aspectRatio + this.size * 1.1;
        this.pos = (x,y,0);
    }   
    //click(agentName, replace=true)
    public void click(agentName, boolean replace)
    {    
     //   click
    }   
    
    public void startDrag(float []pos)
    {   
    	if (this.interactive && this.canBeDraged)
      	{
    		this.beingDragged = true;
      	    this.locationChanged = false;
            this.dragStartXY = pos;
            this.dragStartWorld = this.pos;
      	}
    }   
    public void stopDrag()
    {
    	if (this.interactive && this.canBeDraged)
    	{
    		this.beingDragged = false;
            this.locationChanged = false;
            this.newctrlpoints();
    	}
    }
    public void drag(float [] newXY)
    {
    	if (this.interactive && this.canBeDraged)
    	{    //# Based on http//web.iiit.ac.in/~vkrishna/data/unproj.html
            projection = glGetDoublev(GL2.GL_PROJECTION_MATRIX);
            modelview = glGetDoublev(GL2.GL_MODELVIEW_MATRIX);
            viewport = glGetIntegerv(GL2.GL_VIEWPORT);
            windowZ = glReadPixels(newXY[0], viewport[3]-newXY[1], 1, 1, GL2.GL_DEPTH_COMPONENT, GL2.GL_FLOAT);
            
            worldCoords = gluUnProject(newXY[0], viewport[3] - newXY[1], windowZ[0][0], modelview, projection, viewport);
            if (this.floatingXY)
            {
            	this.pos = (worldCoords[0], worldCoords[1], this.pos[2]);
            }
            else
            {
            	this.pos = (worldCoords[0], worldCoords[1], worldCoords[2]);
            }
                            
            this.locationChanged = true;
    	}
    }   
    //remove(fadeOut=false, fadingFrames=100)
    public void remove(boolean fadeOut, int fadingFrames)
    {
    	if (this.buzz)
    	{
    		if (fadeOut) 
    		{
                this.buzz.mul = this.buzz.mul / 2;
    		}
            else
            {
            	this.buzz.stop();
            }
    	}
        super.remove(fadeOut, fadingFrames);            
    }
}