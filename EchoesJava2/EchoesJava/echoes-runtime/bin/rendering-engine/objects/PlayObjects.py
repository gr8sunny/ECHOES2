'''
Created on 26 Oct 2010

@author: cfabric
'''

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

class Ball(EchoesObject):
    '''
    classdocs
    '''
    def __init__(self, app, autoAdd=True, props = {"type": "Ball"}, fadeIn = False, fadingFrames = 100, randomSize = True, callback=None):
        '''
        Constructor
        '''
        super(Ball, self).__init__(app, autoAdd, props, fadeIn, fadingFrames, callback)
        
        if randomSize:
            self.size = 0.2 + random.random() * 0.2
        else:
            self.size = 0.3 
        self.maxSize = 1.5
        self.moving = True
        self.canBeClicked = True
        self.canBeDraged = True
        self.bounceWithinScene = False
        self.publishBounce = True
        self.container = None 
        self.thrownByAvatar = False
        self.childCanChangeColour = True
        self.droppedByAvatar = False
        
        self.avatarTCB = None
        self.old_pos = self.pos
        
        self.velocity = [0,0] # this is (x,y) velocity
        self.spin = 0
        self.gravity = 0.01
        self.elasticity = 0.8
        
        self.isExploding = False
        self.explodeInFrames = 0
        
        self.left = -1*self.app.canvas.orthoCoordWidth/2
        self.right = -self.left
        self.floor = -1*self.app.canvas.orthoCoordWidth/2/self.app.canvas.aspectRatio * 0.8
        
        
        if "colour" in self.props:
            self.colour = self.props["colour"]
        else:
            self.colour = "red"
        self.colours = { "red": (0.735, 0.197, 0.286), "yellow": (0.921,0.832,0.217), "blue":(0.220,0.481,0.628), "green":(0.439,0.633,0.245)}
        self.patterntex = self.setImage("visual/images/Circles.png")                    

        self.circle = [(math.cos(math.radians(deg)), math.sin(math.radians(deg))) for deg in xrange(0, 360, 5)]
        self.texshape = [((math.cos(math.radians(deg))+1)/2, (math.sin(math.radians(deg))+1)/2) for deg in xrange(0, 360, 5)]        
        
        self.num_sparks = 50
        self.spark_dist = []
        self.spark_d = []
        self.spark_length = 2
        self.spark_maxDist = 7
                
        
    def __setattr__(self, item, value):
        if item == "colour":
            if value == "green":
                self.texture = self.setImage('visual/images/Ball-01.png')
            elif value == "blue":
                self.texture = self.setImage('visual/images/Ball-03.png')
            elif value == "yellow":
                self.texture = self.setImage('visual/images/Ball-04.png')
            else: # red is the default
                self.texture = self.setImage('visual/images/Ball-02.png')
            self.app.canvas.rlPublisher.objectPropertyChanged(str(self.id), "ball_colour", value)
        
        elif item == "container":
            if value == None:
                self.app.canvas.rlPublisher.objectPropertyChanged(str(self.id), "ball_container", "None")
            else:
                self.app.canvas.rlPublisher.objectPropertyChanged(str(self.id), "ball_container", str(value.id))
                
        object.__setattr__(self, item, value)
                 
    def renderObj(self):
        ''' 
        overwriting the render method to draw the bubble
        '''    
        if not hasattr(self, "explodeInFrames"): return
        if self.explodeInFrames > 0:
            self.explodeInFrames -= 1
            if self.explodeInFrames == 0: self.explode()
            
        if not self.isExploding:
            if not self.avatarTCB and not self.beingDragged:
                self.bounce()
                            
            glPushMatrix()
            glEnable( GL_TEXTURE_2D )
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
            glBindTexture(GL_TEXTURE_2D, self.texture)
            glTranslate(self.pos[0], self.pos[1], self.pos[2])
            glScalef(self.size, self.size, self.size)
            glColor4f(1, 1, 1, self.transperancy)
            glBegin(GL_POLYGON)
            ti = 0
            for v in self.circle:
                glTexCoord2d(self.texshape[ti][0], self.texshape[ti][1])
                glVertex3f(v[0], v[1], self.pos[2])
                ti += 1
            glEnd()            
            glDisable( GL_TEXTURE_2D )        
            
            if not self.childCanChangeColour:
                glEnable( GL_TEXTURE_2D )
                glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
                glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
                glBindTexture(GL_TEXTURE_2D, self.patterntex)
                glColor4f(1, 1, 1, self.transperancy*0.5)
                glTranslate(0,0,0.05)
                glBegin(GL_POLYGON)
                ti = 0
                for v in self.circle:
                    glTexCoord2d(self.texshape[ti][0], self.texshape[ti][1])
                    glVertex3f(v[0], v[1], self.pos[2])
                    ti += 1
                glEnd()
                glDisable( GL_TEXTURE_2D )

            glPopMatrix()        

        
        else: # exploding balls...

            glPushMatrix()
            glTranslate(self.pos[0], self.pos[1], self.pos[2])
            glScalef(self.size, self.size, self.size)
            c = self.colours[self.colour]            
            glLineWidth(4.0)
            i = 0
            for d in self.spark_dist:
                if d > self.spark_length:
                    glBegin (GL_LINE_STRIP)
                    glColor4f(1,1,1,0)
                    glVertex3f ((d-self.spark_length)*self.spark_d[i][0], (d-self.spark_length)*self.spark_d[i][1], 0)
                    glColor4f(c[0], c[1], c[2], self.transperancy)            
                    glVertex3f (d*self.spark_d[i][0], d*self.spark_d[i][1], 0)    
                    glEnd ()
                    self.spark_dist[i] = d + min(0.1, self.spark_maxDist/(50*d)) 
                else:
                    self.spark_dist[i] = d + 0.1
                if self.spark_dist[i] > self.spark_maxDist:
                    del self.spark_dist[i]
                    del self.spark_d[i]
                i += 1                     
            glPopMatrix()        
            if len(self.spark_dist) == 0:
                self.remove(False)
       
    def bounce(self):
        self.velocity[1] -= self.gravity
        self.pos = (self.pos[0]+self.velocity[0], self.pos[1]+self.velocity[1], self.pos[2])

        if self.pos[1]-self.size < self.floor:
            self.velocity[1] *= -1*self.elasticity
            self.pos=(self.pos[0], self.floor+self.size, self.pos[2])
            if abs(self.velocity[1]) > self.gravity:
                sound.EchoesAudio.bounce(abs(self.velocity[1]))
                if self.publishBounce:
                    self.app.canvas.rlPublisher.objectPropertyChanged(str(self.id), "ball_bounce", "floor")
                if self.thrownByAvatar: self.thrownByAvatar = False
                if self.droppedByAvatar: self.droppedByAvatar = False
            else:
                # slow down the balls if they roll along the floor.
                self.velocity[0] *= 0.99                

        if self.bounceWithinScene:
            if self.pos[0]-self.size < self.left:
                self.velocity[0] *= -1*self.elasticity
                self.pos = (self.left+self.size, self.pos[1], self.pos[2])
                if self.publishBounce:
                    self.app.canvas.rlPublisher.objectPropertyChanged(str(self.id), "ball_bounce", "left")
            if self.pos[0]+self.size > self.right:
                self.velocity[0] *= -1*self.elasticity
                self.pos = (self.right-self.size, self.pos[1], self.pos[2])
                if self.publishBounce:
                    self.app.canvas.rlPublisher.objectPropertyChanged(str(self.id), "ball_bounce", "right")
        else:
            if abs(self.pos[0]) > 5 + self.size:
                self.app.canvas.rlPublisher.objectPropertyChanged(str(self.id), "ball_off", "")
                self.remove(False)
        
    def attachToJoint(self, jpos, jori, avatarTCB):
        self.avatarTCB = avatarTCB
        self.objectCollisionTest = False        
        self.pos = [jpos[0], max(jpos[1], self.floor), self.pos[2]]
        self.velocity[0] = (self.pos[0]-self.old_pos[0])/3
        self.velocity[1] = (self.pos[1]-self.old_pos[1])/3
        self.old_pos = self.pos
            
    def detachFromJoint(self):
        self.avatarTCB = None
        self.objectCollisionTest = True     
 
    def throw(self):
        self.thrownByAvatar = True
        self.velocity[1] = 0.19
        if self.velocity[0] > 0:
            self.velocity[0] = -0.04
        else:
            self.velocity[0] = 0.04
    
    def explode(self, inFrames = 0):
        if inFrames == 0:
            for i in range(self.num_sparks):
                angle = random.random() * 2 * math.pi
                dist = (random.random() - 1) * self.spark_maxDist
                self.spark_dist.append(dist)
                self.spark_d.append([self.spark_length*math.cos(angle), self.spark_length*math.sin(angle)])                
            self.interactive = False
            self.isExploding = True
            sound.EchoesAudio.playSound("fireworks.wav", vol=0.8)
        else:
            self.explodeInFrames = inFrames
    
    def click(self, agentName, replace=True):
        '''
        click on the ball
        '''
        pass
    
    def startDrag(self, pos):
        if self.interactive:
            self.beingDragged = True
    
    def stopDrag(self):
        if self.interactive:
            self.beingDragged = False
    
    def drag(self, newXY):
        if self.interactive and self.canBeDraged and self.beingDragged:
            # Based on http://web.iiit.ac.in/~vkrishna/data/unproj.html
            projection = glGetDoublev(GL_PROJECTION_MATRIX)
            modelview = glGetDoublev(GL_MODELVIEW_MATRIX)
            viewport = glGetIntegerv(GL_VIEWPORT)
            windowZ = glReadPixels(newXY[0], viewport[3]-newXY[1], 1, 1, GL_DEPTH_COMPONENT, GL_FLOAT)
            
            worldCoords = gluUnProject(newXY[0], viewport[3] - newXY[1], windowZ[0][0], modelview, projection, viewport)
            self.velocity = [(worldCoords[0]-self.pos[0])/3, (worldCoords[1]-self.pos[1])/3]
            self.pos = (worldCoords[0], max(self.floor, worldCoords[1]), self.pos[2])
            
                
        
    def remove(self, fadeOut=False, fadingFrames=100):
        super(Ball, self).remove(fadeOut, fadingFrames)            
                                    


