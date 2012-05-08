'''
Created on 8 Sep 2009

@author: cfabric
'''

from EchoesObject import *
from OpenGL.GL import *
from OpenGL.GLU import *
from OpenGL.GLUT import *
import random, time, math
import echoes
import sound.EchoesAudio
import Motions
import environment
import PIL.Image

class EchoesBubble(EchoesObject, Motions.BezierMotion):
    '''
    classdocs
    '''

    def __init__(self, app, autoAdd=True, props = {"type": "Bubble"}, fadeIn = False, fadingFrames = 100, randomSize = True, callback=None):
        '''
        Constructor
        '''
        super(EchoesBubble, self).__init__(app, autoAdd, props, fadeIn, fadingFrames, callback)
        super(EchoesBubble, self).initBezierVars()
        
        if randomSize:
            self.size = 0.2 + random.random() * 0.2
        else:
            self.size = 0.3 
        self.maxSize = 1.5
        self.speed = 0.002
        self.moving = True
        self.floatingXY = True
        self.floatingSound = False
        self.canBeClicked = True
        self.canBeDraged = True
        self.canMerge = True
        self.willBeReplaced = True
        self.mergedByChild = False
        
        if "colour" in self.props:
            self.colour = self.props["colour"]
        else:
            self.colour = "neutral"
            
        self.shape = [(-1, -1), (1, -1), (1, 1), (-1, 1)]
        self.texshape = [(0, 0), (1, 0), (1, 1), (0, 1)]
        
        self.newstartpos()
        self.newctrlpoints()
                    
    def __setattr__(self, item, value):
        if item == "colour":
            if value == "green":
                self.texture = self.setImage('visual/images/Bubble-green.png')
            elif value == "red":
                self.texture = self.setImage('visual/images/Bubble-red.png')
            else: # "neutral":
                self.texture = self.setImage('visual/images/Bubble-neutral.png')
                            
        object.__setattr__(self, item, value)
                             
    def renderObj(self):
        ''' 
        overwriting the render method to draw the bubble
        '''
        oldpos = self.pos
        if self.moving and not self.beingDragged:
            self.pos = self.nextBezierPos(self.floatingXY)
            if self.removeAtTargetPos and self.bezierIndex > 0.95:
                self.remove(True)
            
        if self.size > self.maxSize:
            self.click("None")
 
        if self.mergedByChild: # remove in first rendering cycle after the child might have merged the bubbles
            self.mergedByChild = False
        
        glPushMatrix()
        glEnable( GL_ALPHA_TEST )
        glAlphaFunc( GL_GREATER, 0.1 )        
        glEnable( GL_TEXTURE_2D )
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glBindTexture(GL_TEXTURE_2D, self.texture)
        glTranslate(self.pos[0], self.pos[1], self.pos[2])
        glScalef(self.size, self.size, self.size)
        glColor4f(1, 1, 1, self.transperancy)
        glBegin(GL_QUADS)
        ti = 0
        for v in self.shape:
            glTexCoord2d(self.texshape[ti][0], self.texshape[ti][1])
            glVertex3f(v[0], v[1], self.pos[2])
            ti += 1
        glEnd()
        glDisable( GL_TEXTURE_2D )
        glDisable( GL_ALPHA_TEST )
        glPopMatrix()
        
        if self.showCtrlPoints:
            glPushMatrix()
            glPointSize (4.0)
            glColor4f(1.0,0,0,1.0)
            glBegin (GL_POINTS)
            for i,p in self.ctrlpoints.iteritems():
                glVertex3f (p[0], p[1], p[2])
            glEnd ()
            glPointSize (1.0)
            glPopMatrix()

    def grow(self):
        print "Growing bubble " + str(self.id)
        if self.size < self.maxSize:
            self.size = self.size * 1.1        
                
    def newstartpos(self):
        x = self.app.canvas.orthoCoordWidth/2 - (random.random() * self.app.canvas.orthoCoordWidth)
        y = random.choice([-1,1])*self.app.canvas.orthoCoordWidth/2/self.app.canvas.aspectRatio + self.size * 1.1
        self.pos = (x,y,0)
        
    def click(self, agentName, replace=True):
        '''
        pop the bubble when clicked and re-introduce it from the back to keep the number
        '''
        if self.interactive and (self.canBeClicked or agentName=="None"):

            if self.mergedByChild or agentName == "User":  # only count if it was inflicted by the child
                if self.app.canvas.scenario == "BubbleWorld":
                    bs = None
                    for id, object in self.app.canvas.sceneElements.items():
                        if isinstance(object, environment.HelperElements.Score):
                            bs = object
                    if bs:
                        bs.increment()
                        if self.app.canvas.publishScore:
                            self.app.canvas.rlPublisher.worldPropertyChanged("BubbleScore", str(bs.score))
                
                self.app.canvas.agentPublisher.agentActionCompleted("User", "bubble_pop", [str(self.id)])
                    
            if sound.EchoesAudio.soundPresent:
                sound.EchoesAudio.bubblePop(self.size)
            
            if self.willBeReplaced and replace:
                newB = EchoesBubble(self.app, True, self.props, self.fadingIn)

            self.app.canvas.rlPublisher.objectPropertyChanged(str(self.id), "bubble_pop", "")
            self.remove()
    
    def startDrag(self, pos):
        if self.interactive and self.canBeDraged:
            self.beingDragged = True
            self.locationChanged = False
            self.dragStartXY = pos
            self.dragStartWorld = self.pos
        
    def stopDrag(self):
        if self.interactive and self.canBeDraged:
            self.beingDragged = False
            self.locationChanged = False
            self.newctrlpoints()
    
    def drag(self, newXY):
        if self.interactive and self.canBeDraged:
            # Based on http://web.iiit.ac.in/~vkrishna/data/unproj.html
            projection = glGetDoublev(GL_PROJECTION_MATRIX)
            modelview = glGetDoublev(GL_MODELVIEW_MATRIX)
            viewport = glGetIntegerv(GL_VIEWPORT)
            windowZ = glReadPixels(newXY[0], viewport[3]-newXY[1], 1, 1, GL_DEPTH_COMPONENT, GL_FLOAT)
            
            worldCoords = gluUnProject(newXY[0], viewport[3] - newXY[1], windowZ[0][0], modelview, projection, viewport)
            if self.floatingXY:
                self.pos = (worldCoords[0], worldCoords[1], self.pos[2])
            else:
                self.pos = (worldCoords[0], worldCoords[1], worldCoords[2])
                
            projection = glGetDoublev(GL_PROJECTION_MATRIX)
            modelview = glGetDoublev(GL_MODELVIEW_MATRIX)
            viewport = glGetIntegerv(GL_VIEWPORT)
            
            self.locationChanged = True
        
    def remove(self, fadeOut=False, fadingFrames=100):
        super(EchoesBubble, self).remove(fadeOut, fadingFrames)            
                                    


