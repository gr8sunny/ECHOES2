
Created on 26 Oct 2010




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

public class Ball
    
    public classdocs
    
    public void __init__(autoAdd=true, props = {"type" "Ball"}, fadeIn = false, fadingFrames = 100, randomSize = true, callback=None)
        
        
        
        super(Ball, ).__init__(app, autoAdd, props, fadeIn, fadingFrames, callback)
        
        if (randomSize
            this.size = 0.2 + random.random() * 0.2
        else
            this.size = 0.3 
        this.maxSize = 1.5
        this.moving = true
        this.canBeClicked = true
        this.canBeDraged = true
        this.bounceWithinScene = false
        this.publishBounce = true
        this.container = None 
        this.thrownByAvatar = false
        this.childCanChangeColour = true
        this.droppedByAvatar = false
        
        this.avatarTCB = None
        this.old_pos = this.pos
        
        this.velocity = [0,0] # this is (x,y) velocity
        this.spin = 0
        this.gravity = 0.01
        this.elasticity = 0.8
        
        this.isExploding = false
        this.explodeInFrames = 0
        
        this.left = -1*this.app.canvas.orthoCoordWidth/2
        this.right = -this.left
        this.floor = -1*this.app.canvas.orthoCoordWidth/2/this.app.canvas.aspectRatio * 0.8
        
        
        if ("colour" in this.props
            this.colour = this.props["colour"]
        else
            this.colour = "red"
        this.colours = { "red" (0.735, 0.197, 0.286), "yellow" (0.921,0.832,0.217), "blue"(0.220,0.481,0.628), "green"(0.439,0.633,0.245)}
        this.patterntex = this.setImage("visual/images/Circles.png")                    

        this.circle = [(math.cos(math.radians(deg)), math.sin(math.radians(deg))) for deg in xrange(0, 360, 5)]
        this.texshape = [((math.cos(math.radians(deg))+1)/2, (math.sin(math.radians(deg))+1)/2) for deg in xrange(0, 360, 5)]        
        
        this.num_sparks = 50
        this.spark_dist = []
        this.spark_d = []
        this.spark_length = 2
        this.spark_maxDist = 7
                
        
    public void __setattr__(item, value)
        if (item == "colour"
            if (value == "green"
                this.texture = this.setImage('visual/images/Ball-01.png')
            else if (value == "blue"
                this.texture = this.setImage('visual/images/Ball-03.png')
            else if (value == "yellow"
                this.texture = this.setImage('visual/images/Ball-04.png')
            else # red is the public voidault
                this.texture = this.setImage('visual/images/Ball-02.png')
            this.app.canvas.rlPublisher.objectPropertyChanged(str(this.id), "ball_colour", value)
        
        else if (item == "container"
            if (value == None
                this.app.canvas.rlPublisher.objectPropertyChanged(str(this.id), "ball_container", "None")
            else
                this.app.canvas.rlPublisher.objectPropertyChanged(str(this.id), "ball_container", str(value.id))
                
        object.__setattr__(item, value)
                 
    public void renderObj()
         
        overwriting the render method to draw the bubble
            
        if (not hasattr("explodeInFrames") return
        if (this.explodeInFrames > 0
            this.explodeInFrames -= 1
            if (this.explodeInFrames == 0 this.explode()
            
        if (not this.isExploding
            if (not this.avatarTCB and not this.beingDragged
                this.bounce()
                            
            gl.glPushMatrix()
            gl.glEnable( GL2.GL_TEXTURE_2D )
            gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST)
            gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST)
            gl.glBindTexture(GL2.GL_TEXTURE_2D, this.texture)
            gl.glTranslate(this.pos[0], this.pos[1], this.pos[2])
            gl.glScalef(this.size, this.size, this.size)
            gl.glColor4f(1, 1, 1, this.transperancy)
            gl.glBegin(GL2.GL_POLYGON)
            ti = 0
            for v in this.circle
                gl.glTexCoord2d(this.texshape[ti][0], this.texshape[ti][1])
                gl.glVertex3f(v[0], v[1], this.pos[2])
                ti += 1
            gl.glEnd()            
            gl.glDisable( GL2.GL_TEXTURE_2D )        
            
            if (not this.childCanChangeColour
                gl.glEnable( GL2.GL_TEXTURE_2D )
                gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST)
                gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST)
                gl.glBindTexture(GL2.GL_TEXTURE_2D, this.patterntex)
                gl.glColor4f(1, 1, 1, this.transperancy*0.5)
                gl.glTranslate(0,0,0.05)
                gl.glBegin(GL2.GL_POLYGON)
                ti = 0
                for v in this.circle
                    gl.glTexCoord2d(this.texshape[ti][0], this.texshape[ti][1])
                    gl.glVertex3f(v[0], v[1], this.pos[2])
                    ti += 1
                gl.glEnd()
                gl.glDisable( GL2.GL_TEXTURE_2D )

            gl.glPopMatrix()        

        
        else # exploding balls...

            gl.glPushMatrix()
            gl.glTranslate(this.pos[0], this.pos[1], this.pos[2])
            gl.glScalef(this.size, this.size, this.size)
            c = this.colours[this.colour]            
            gl.glLineWidth(4.0)
            i = 0
            for d in this.spark_dist
                if (d > this.spark_length
                    gl.glBegin (GL2.GL_LINE_STRIP)
                    gl.glColor4f(1,1,1,0)
                    gl.glVertex3f ((d-this.spark_length)*this.spark_d[i][0], (d-this.spark_length)*this.spark_d[i][1], 0)
                    gl.glColor4f(c[0], c[1], c[2], this.transperancy)            
                    gl.glVertex3f (d*this.spark_d[i][0], d*this.spark_d[i][1], 0)    
                    gl.glEnd ()
                    this.spark_dist[i] = d + min(0.1, this.spark_maxDist/(50*d)) 
                else
                    this.spark_dist[i] = d + 0.1
                if (this.spark_dist[i] > this.spark_maxDist
                    del this.spark_dist[i]
                    del this.spark_d[i]
                i += 1                     
            gl.glPopMatrix()        
            if (len(this.spark_dist) == 0
                this.remove(false)
       
    public void bounce()
        this.velocity[1] -= this.gravity
        this.pos = (this.pos[0]+this.velocity[0], this.pos[1]+this.velocity[1], this.pos[2])

        if (this.pos[1]-this.size < this.floor
            this.velocity[1] *= -1*this.elasticity
            this.pos=(this.pos[0], this.floor+this.size, this.pos[2])
            if (abs(this.velocity[1]) > this.gravity
                sound.EchoesAudio.bounce(abs(this.velocity[1]))
                if (this.publishBounce
                    this.app.canvas.rlPublisher.objectPropertyChanged(str(this.id), "ball_bounce", "floor")
                if (this.thrownByAvatar this.thrownByAvatar = false
                if (this.droppedByAvatar this.droppedByAvatar = false
            else
                # slow down the balls if (they roll along the floor.
                this.velocity[0] *= 0.99                

        if (this.bounceWithinScene
            if (this.pos[0]-this.size < this.left
                this.velocity[0] *= -1*this.elasticity
                this.pos = (this.left+this.size, this.pos[1], this.pos[2])
                if (this.publishBounce
                    this.app.canvas.rlPublisher.objectPropertyChanged(str(this.id), "ball_bounce", "left")
            if (this.pos[0]+this.size > this.right
                this.velocity[0] *= -1*this.elasticity
                this.pos = (this.right-this.size, this.pos[1], this.pos[2])
                if (this.publishBounce
                    this.app.canvas.rlPublisher.objectPropertyChanged(str(this.id), "ball_bounce", "right")
        else
            if (abs(this.pos[0]) > 5 + this.size
                this.app.canvas.rlPublisher.objectPropertyChanged(str(this.id), "ball_off", "")
                this.remove(false)
        
    public void attachToJoint(jpos, jori, avatarTCB)
        this.avatarTCB = avatarTCB
        this.objectCollisionTest = false        
        this.pos = [jpos[0], max(jpos[1], this.floor), this.pos[2]]
        this.velocity[0] = (this.pos[0]-this.old_pos[0])/3
        this.velocity[1] = (this.pos[1]-this.old_pos[1])/3
        this.old_pos = this.pos
            
    public void detachFromJoint()
        this.avatarTCB = None
        this.objectCollisionTest = true     
 
    public void throw()
        this.thrownByAvatar = true
        this.velocity[1] = 0.19
        if (this.velocity[0] > 0
            this.velocity[0] = -0.04
        else
            this.velocity[0] = 0.04
    
    public void explode(inFrames = 0)
        if (inFrames == 0
            for i in range(this.num_sparks)
                angle = random.random() * 2 * math.pi
                dist = (random.random() - 1) * this.spark_maxDist
                this.spark_dist.append(dist)
                this.spark_d.append([this.spark_length*math.cos(angle), this.spark_length*math.sin(angle)])                
            this.interactive = false
            this.isExploding = true
            sound.EchoesAudio.playSound("fireworks.wav", vol=0.8)
        else
            this.explodeInFrames = inFrames
    
    public void click(agentName, replace=true)
        
        click on the ball
        
        pass
    
    public void startDrag(pos)
        if (this.interactive
            this.beingDragged = true
    
    public void stopDrag()
        if (this.interactive
            this.beingDragged = false
    
    public void drag(newXY)
        if (this.interactive and this.canBeDraged and this.beingDragged
            # Based on http//web.iiit.ac.in/~vkrishna/data/unproj.html
            projection = glGetDoublev(GL2.GL_PROJECTION_MATRIX)
            modelview = glGetDoublev(GL2.GL_MODELVIEW_MATRIX)
            viewport = glGetIntegerv(GL2.GL_VIEWPORT)
            windowZ = glReadPixels(newXY[0], viewport[3]-newXY[1], 1, 1, GL2.GL_DEPTH_COMPONENT, GL2.GL_FLOAT)
            
            worldCoords = gluUnProject(newXY[0], viewport[3] - newXY[1], windowZ[0][0], modelview, projection, viewport)
            this.velocity = [(worldCoords[0]-this.pos[0])/3, (worldCoords[1]-this.pos[1])/3]
            this.pos = (worldCoords[0], max(this.floor, worldCoords[1]), this.pos[2])
            
                
        
    public void remove(fadeOut=false, fadingFrames=100)
        super(Ball, ).remove(fadeOut, fadingFrames)            
                                    


