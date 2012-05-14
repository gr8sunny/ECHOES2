package objects

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

//translation from Py May 11th
//Created on 26 Oct 2010
/*
from EchoesObject import *
from OpenGL.GL import *
from OpenGL.GLU import *
from OpenGL.GLUT import *
import random, time, math
import echoes
import Motions
import environment
import PIL.Image
import sound.EchoesAudio
*/
public class Ball extends EchoesObject
{    
 //   public classdocs
    //Ball(boolean autoAdd=true, props = {"type" "Ball"}, fadeIn = false, fadingFrames = 100, randomSize = true, callback=None)
    private boolean canBeDraged = true;
    private boolean bounceWithinScene = false;
    private boolean publishBounce = true;
    private boolean thrownByAvatar = false;
    private boolean childCanChangeColour = true;
    private boolean droppedByAvatar = false;
    private float [] old_pos = {0, 0, 0};
    private float [] velocity = {0, 0};
    Hashtable colours = new Hashtable();
    private float [][] texshape = new float [80][2];
    private int num_sparks = 50;
    Vector spark_dist = new Vector();
    //private float [][] spark_d = new float [60][2];
    Vector spark_d = new Vector(); 
    private int spark_length = 2;
    private int spark_maxDist = 7;
    private float spin = 0;
    private float gravity = (float) 0.01;
    private float elasticity = (float) 0.8;
    
    private boolean isExploding = false;
    private int explodeInFrames = 0;
	private Object container;
    
	public Ball(boolean autoAdd, Map<String, String> props, boolean fadeIn, int fadingFrames, boolean randomSize, Object callback)
    {    
        super(autoAdd, props, fadeIn, fadingFrames, callback);
        
        if (randomSize)
            this.size = (float) ((float)0.2 + Math.random() * 0.2);
        else
            this.size = (float) 0.3;
        this.maxSize = (float) 1.5;
        this.moving = true;
        this.canBeClicked = true;
        this.container = null;//*****type? 
        
        this.avatarTCB = null;//*****type?
        this.old_pos = this.pos;
        
       // this.velocity = [0,0] //# this is (x,y) velocity
        //****no idea about following 3 lines
        this.left = -1*this.app.canvas.orthoCoordWidth/2;
        this.right = -this.left;
        this.floor = -1*this.app.canvas.orthoCoordWidth/2/this.app.canvas.aspectRatio * 0.8;
        
        
        if (this.props.containsKey("colour"))
            this.colour = this.props.get("colour");
        else
            this.colour = "red";
        
        float [] tempArray = { (float) 0.735, (float) 0.197, (float) 0.286};
        this.colours.put("red", tempArray);
        float [] tempArray1 =  {(float) 0.921, (float) 0.832, (float) 0.217};
        this.colours.put("yellow", tempArray1);
        float [] tempArray2 = {(float) 0.220, (float) 0.481, (float) 0.628};
        this.colours.put("blue", tempArray2);
        float [] tempArray3 = {(float) 0.439, (float) 0.633, (float) 0.245};
        this.colours.put("green", tempArray3);
        
        //this.patterntex = this.setImage("visual/images/Circles.png");//****no idea?                    
        loadTexture("visual/images/Circles.png");//is this fine?
        
        int pointIndex = 0;
        for(float deg=0; deg < 360; deg+=5)
        {
        	circle[pointIndex][0]=(float)(Math.cos(Math.toRadians(deg)));
            circle[pointIndex][1] = (float)(Math.sin(Math.toRadians(deg)));
            pointIndex++;
        }
        pointIndex = 0;
        for(float deg=0; deg < 360; deg+=5)
        {
        	texshape[pointIndex][0]=(float)((Math.cos(Math.toRadians(deg)+1)/2));//****I think it should be (Math.toRadians(deg))+1)
            texshape[pointIndex][1] = (float)((Math.sin(Math.toRadians(deg))+1)/2);
            pointIndex++;
        }
        
    }           
        
    public void setAttr(String item, String value)
    { 
    	if (item == "colour")
     	{
    		if (value == "green")
     	        loadTexture("visual/images/Ball-01.png");
            else if (value == "blue")
                loadTexture("visual/images/Ball-03.png");
            else if (value == "yellow")
                loadTexture("visual/images/Ball-04.png");
            else// # red is the public voidault
                loadTexture("visual/images/Ball-02.png");
            this.app.canvas.rlPublisher.objectPropertyChanged(str(this.id), "ball_colour", value);
     	}
        else if (item == "container")
            //***no idea...
        	if (value == null)
                this.app.canvas.rlPublisher.objectPropertyChanged(str(this.id), "ball_container", "None");
            else
                this.app.canvas.rlPublisher.objectPropertyChanged(str(this.id), "ball_container", str(value.id));
                
        setAttr(item, value);//****recursive call-is this intentional?
    }            
    public void renderObj(GL2 gl)
    {    
     //   overwriting the render method to draw the bubble
        //****whats hasattr?    
        if (!hasattr("explodeInFrames"))
        	return;
        if (this.explodeInFrames > 0)
        {
        	this.explodeInFrames -= 1;
            if (this.explodeInFrames == 0)
            	this.explode();
        }   
        if (!this.isExploding)
        { 
        	if (!this.avatarTCB && !this.beingDragged)
                this.bounce();
                            
            gl.glPushMatrix();
            gl.glEnable( GL2.GL_TEXTURE_2D );
            gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
            gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
            gl.glBindTexture(GL2.GL_TEXTURE_2D, this.texture);
            gl.glTranslate(this.pos[0], this.pos[1], this.pos[2]);
            gl.glScalef(this.size, this.size, this.size);
            gl.glColor4f(1, 1, 1, this.transperancy);
            gl.glBegin(GL2.GL_POLYGON);
            int ti = 0;
            //for v in this.circle
            for(float [] v : this.circle)
            {
            	gl.glTexCoord2d(this.texshape[ti][0], this.texshape[ti][1]);
                gl.glVertex3f(v[0], v[1], this.pos[2]);
                ti += 1;
            }
            gl.glEnd();            
            gl.glDisable( GL2.GL_TEXTURE_2D );        
            
            if (! this.childCanChangeColour)
            {
            	gl.glEnable( GL2.GL_TEXTURE_2D );
            	gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
                gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
                //gl.glBindTexture(GL2.GL_TEXTURE_2D, this.patterntex);
                gl.glColor4f(1, 1, 1, this.transperancy*0.5);
                gl.glTranslate(0,0,0.05);
                gl.glBegin(GL2.GL_POLYGON);
                ti = 0;
                for(float [] v : this.circle)
                {
                	gl.glTexCoord2d(this.texshape[ti][0], this.texshape[ti][1]);
                    gl.glVertex3f(v[0], v[1], this.pos[2]);
                    ti += 1;
                }
                gl.glEnd();
                gl.glDisable( GL2.GL_TEXTURE_2D );
            }
            gl.glPopMatrix();        
    
        }
        else //# exploding balls...
        {
            gl.glPushMatrix();
            gl.glTranslate(this.pos[0], this.pos[1], this.pos[2]);
            gl.glScalef(this.size, this.size, this.size);
            float [] c = (float[]) this.colours.get(this.colour);
            gl.glLineWidth(4.0);
            int i = 0;
            //float [] tempArray = (float[])this.spark_dist.toArray(); 
            Iterator spark_distItr = spark_dist.iterator();
            
            while(spark_distItr.hasNext()) 
            {
            	float d = (Float)spark_distItr.next();
            	if (d > this.spark_length)
              	{
            		gl.glBegin (GL2.GL_LINE_STRIP);
              	    gl.glColor4f(1,1,1,0);
                    gl.glVertex3f ((d-this.spark_length)*((float[])this.spark_d.get(i))[0], (d-this.spark_length)*((float[])this.spark_d.get(i))[1], 0);
                    gl.glColor4f(c[0], c[1], c[2], this.transperancy);
                    gl.glVertex3f (d*((float[])this.spark_d.get(i))[0], d*((float[])this.spark_d.get(i))[1], 0);    
                    gl.glEnd ();
                    this.spark_dist.add(i, d + Math.min(0.1, this.spark_maxDist/(50*d)));
              	}
                else
                {
                	this.spark_dist.add(i, d + 0.1);
                }
                if ((float)this.spark_dist.get(i) > this.spark_maxDist)
                {
                	this.spark_dist.remove(i);
                    this.spark_d.remove(i);
                }
                i += 1; 
            }
            gl.glPopMatrix();        
            if (this.spark_dist.isEmpty())
            {
            	this.remove(false, 100);
            }
        }
    }
    public void bounce()
    {
    	this.velocity[1] -= this.gravity;
        this.pos[0] = this.pos[0]+this.velocity[0];
        this.pos[1] = this.pos[1]+this.velocity[1];
        //, this.pos[2]);

        if (this.pos[1]-this.size < this.floor)
        {
        	this.velocity[1] *= -1*this.elasticity;
            this.pos[1] = this.floor+this.size;
            if (Math.abs(this.velocity[1]) > this.gravity)
            {
            	sound.EchoesAudio.bounce(Math.abs(this.velocity[1]));
                if (this.publishBounce)
                    this.app.canvas.rlPublisher.objectPropertyChanged(str(this.id), "ball_bounce", "floor");
                if (this.thrownByAvatar)
                	this.thrownByAvatar = false;
                if (this.droppedByAvatar)
                	this.droppedByAvatar = false;
            }
            else
                //# slow down the balls if (they roll along the floor.
                this.velocity[0] *= 0.99;                
        }
        if (this.bounceWithinScene)
        {
        	if (this.pos[0]-this.size < this.left)
        	{        
                this.velocity[0] *= -1*this.elasticity;
                this.pos = (this.left+this.size, this.pos[1], this.pos[2]);
                if (this.publishBounce)
                    this.app.canvas.rlPublisher.objectPropertyChanged(str(this.id), "ball_bounce", "left");
        	}
            if (this.pos[0]+this.size > this.right)
            {
            	this.velocity[0] *= -1*this.elasticity;
                this.pos = (this.right-this.size, this.pos[1], this.pos[2]);
                if (this.publishBounce)
                    this.app.canvas.rlPublisher.objectPropertyChanged(str(this.id), "ball_bounce", "right");
            }
        }
        else if (abs(this.pos[0]) > 5 + this.size)
        {
        	this.app.canvas.rlPublisher.objectPropertyChanged(str(this.id), "ball_off", "");
            this.remove(false, 100);
        }
    }   
    public void attachToJoint(float [] jpos, Object jori, boolean avatarTCB)
    {
    	this.avatarTCB = avatarTCB;
        this.objectCollisionTest = false;        
        this.pos[0] = jpos[0];
        this.pos[1] = max(jpos[1], this.floor);
        
        this.velocity[0] = (this.pos[0]-this.old_pos[0])/3;
        this.velocity[1] = (this.pos[1]-this.old_pos[1])/3;
        this.old_pos = this.pos;
    }       
    public void detachFromJoint()
    {
    	this.avatarTCB = null;
        this.objectCollisionTest = true;     
    }
    public void throw()//****this is trying to override "throw" in Java...should change the name
    {
    	this.thrownByAvatar = true;
        this.velocity[1] = (float) 0.19;
        if (this.velocity[0] > 0)
            this.velocity[0] = (float) -0.04;
        else
            this.velocity[0] = (float) 0.04;
    }
    //void explode(int inFrames = 0)
    public void explode(int inFrames)
    {
    	if (inFrames == 0)
    	{
    		for (int i=0; i < this.num_sparks; i++)
    		{
    			float angle = (float) (Math.random() * 2 * Math.PI);
    		    float dist = (float) ((Math.random() - 1) * this.spark_maxDist);
                this.spark_dist.add(dist);
                float [] tempArray= {(float) (this.spark_length*Math.cos(angle)), (float) (this.spark_length*Math.sin(angle))};
                this.spark_d.add(tempArray);
    		}
            this.interactive = false;
            this.isExploding = true;
            sound.EchoesAudio.playSound("fireworks.wav", vol=0.8);
    	}
        else
            this.explodeInFrames = inFrames;
    }
    //agentName, replace=true)
    public void click(Object agentName, boolean replace)
    {   
     //   click on the ball
        
    //    pass
    }
    public void startDrag(float [] pos)
    {
    	if (this.interactive)
           this.beingDragged = true;
    }
    public void stopDrag()
    {
    	if (this.interactive)
           this.beingDragged = false;
    }
    public void drag(float [] newXY)
    {
    	if (this.interactive && this.canBeDraged && this.beingDragged)
    	{    //# Based on http//web.iiit.ac.in/~vkrishna/data/unproj.html
            projection = glGetDoublev(GL2.GL_PROJECTION_MATRIX);
            modelview = glGetDoublev(GL2.GL_MODELVIEW_MATRIX);
            viewport = glGetIntegerv(GL2.GL_VIEWPORT);
            windowZ = glReadPixels(newXY[0], viewport[3]-newXY[1], 1, 1, GL2.GL_DEPTH_COMPONENT, GL2.GL_FLOAT);
            
            worldCoords = gluUnProject(newXY[0], viewport[3] - newXY[1], windowZ[0][0], modelview, projection, viewport);
            this.velocity = [(worldCoords[0]-this.pos[0])/3, (worldCoords[1]-this.pos[1])/3];
            this.pos = (worldCoords[0], max(this.floor, worldCoords[1]), this.pos[2]);
    	}
    }       
                
        //remove(fadeOut=false, fadingFrames=100)
    public void remove(boolean fadeOut, int fadingFrames)
    {
    	super.remove(fadeOut, fadingFrames);            
    }                               
}

