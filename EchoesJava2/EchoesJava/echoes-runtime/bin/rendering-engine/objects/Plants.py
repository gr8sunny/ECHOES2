'''
Created on 8 Sep 2009

@author: cfabric
'''
from EchoesObject import *
from OpenGL.GL import *
from OpenGL.GLU import *
from OpenGL.GLE import *
from OpenGL.GLUT import *
import PIL.Image
import random
import echoes
import math, numpy
import objects.Environment
import Ice
import Logger        
import Bubbles, PlayObjects
import Motions
        
class EchoesFlower(EchoesObject):
    '''
    classdocs
    '''
    def __init__(self, app, autoAdd=True, props={"type": "Flower"}, fadeIn = False, fadingFrames = 100, callback=None):
        '''
        Constructor
        '''
        super(EchoesFlower, self).__init__(app, autoAdd, props, fadeIn, fadingFrames, callback)
        
        self.size = 0.4
        self.maxSize = 0.6
        self.pos = (0,0,0) 
        self.rotate = [0,0,0]
        
        self.publishRegion = True
        self.underCloud = False
        self.amplitude = 0
        self.swing = 0
        
        if "colour" in self.props:
            self.colour = self.props["colour"]
        else:
            self.colour = "red"
        
        self.patterntex = self.setImage("visual/images/Circles.png")                    
                    
        self.shape = [(-1, -1), (1, -1), (1, 1), (-1, 1)]
        self.texshape = [(0, 0), (1, 0), (1, 1), (0, 1)]

        self.targetPos = None                                
        self.targetBasket = None
                
        self.pot = None 
        self.basket = None
        self.inCollision = None
        self.canGrow = True
        self.isGrowing = 0
        self.growToSize = None
        self.avatarTCB = None
        self.canTurnIntoBall = True
        self.canTurnIntoBubble = True
        self.childCanTurnIntoBubble = True
        self.childCanTurnIntoBall = True

    def __setattr__(self, item, value):
        if item == "size":
            self.stemLength = value * 4
            self.calcStemPoints()
            self.stemWidth = int(min(self.app.canvas.lineWidthRange[1] * 2 * value, 10))            

        elif item == "growToSize":
            value = min(self.maxSize, value)

        elif item == "colour":
            if value == "green":
                self.texture = self.setImage('visual/images/FlowerHead-01.png')
            elif value == "blue":
                self.texture = self.setImage('visual/images/FlowerHead-03.png')
            elif value == "yellow":
                self.texture = self.setImage('visual/images/FlowerHead-04.png')
            else: # red is the default
                self.texture = self.setImage('visual/images/FlowerHead-02.png')

        elif item == "pos" and hasattr(self, "pos") and hasattr(self, "underCloud"):
            for oid, o in self.app.canvas.objects.items():
                if isinstance(o, objects.Environment.Cloud):
                    if o.isUnder(self):
                        if not self.underCloud: self.underCloud = True
                    else:
                        if self.underCloud: self.underCloud = False
                        
        elif item == "pot":
            if value == None:
                self.app.canvas.rlPublisher.objectPropertyChanged(str(self.id), "flower_pot", "None")
            else:
                self.app.canvas.rlPublisher.objectPropertyChanged(str(self.id), "flower_pot", str(value.id))
                
        elif item == "basket":
            if value == None:
                self.app.canvas.rlPublisher.objectPropertyChanged(str(self.id), "flower_basket", "None")
            else:
                self.app.canvas.rlPublisher.objectPropertyChanged(str(self.id), "flower_basket", str(value.id))

        elif item == "underCloud":
            self.app.canvas.rlPublisher.objectPropertyChanged(str(self.id), "under_cloud", str(value))
            
        elif hasattr(self, "isGrowing") and item == "isGrowing":
            if self.isGrowing > 0 and value == 0:
                self.app.canvas.rlPublisher.objectPropertyChanged(str(self.id), "is_growing", "False")
            if self.isGrowing <= 0 and value > 0:
                self.app.canvas.rlPublisher.objectPropertyChanged(str(self.id), "is_growing", "True")

        object.__setattr__(self, item, value)
                        
    def findTargetBasket(self):
        for id, se in self.app.canvas.objects.items():
            if isinstance(se, objects.Environment.Basket):
                self.targetBasket = se
                break
            
    def calcStemPoints(self):
        self.stemPoints = []
        for i in range(4):
            if i > 0 and i < 4:
                x = random.uniform(-0.2,0.2)
            else:
                x = 0
            self.stemPoints.append([x, -1*self.stemLength*float(i)/3.0, 0])
            
    def renderObj(self):
        ''' 
        overwriting the render method to draw the flower
        '''
        if not (hasattr(self, "swing")): return
        
        if (not (self.basket and self.basket.avatarTCB) and
            not (self.pot and self.pot.avatarTCB)):
            if not self.inCollision:
                if self.basket:
                    self.basket.removeFlower(self)
                    self.basket = None
                if self.pot:
                    self.pot.flower = None
                    self.pot = None        
            self.inCollision = None             
        
        if self.isGrowing > 0:
            self.isGrowing -= 1
            
        if self.growToSize and self.canGrow:
            if self.size < self.growToSize:
                self.grow()
            else:
                self.growToSize = None
        
        if self.targetPos:
            d = [0,0,0]
            for i in range(3):
                d[i] = self.targetPos[i] - self.pos[i]
            self.pos = [self.pos[0] + d[0] / 20, self.pos[1] + d[1] / 20, self.pos[2] + d[2] / 20]
            if abs(d[0]+d[1]+d[2]) < 0.05:
                self.pos = self.targetPos
                self.targetPos = None
                if self.targetBasket:
                    self.targetBasket.addFlower(self)
                    self.interactive = True
                    self.targetBasket = None
                    
            
        if not self.beingDragged:
            self.swing = (self.swing + 0.1) % (2*math.pi) # animate the swinging stem
            self.amplitude = self.amplitude - 0.005
            if self.amplitude < 0: self.amplitude = 0    

        dx= -1.5*self.size * self.amplitude * math.sin(self.swing)
        dy= self.stemLength - math.sqrt(math.pow(self.stemLength, 2) - math.pow(dx, 2))        
        self.stemPoints[0]=(-1*dx,-1*dy,self.pos[2])
                                         
        glPushMatrix()
        
            # centre position
        glTranslate(self.pos[0], self.pos[1], self.pos[2])  #make sure the head is in front of the stem
        glRotatef(self.rotate[2],0,0,1)
        
            # Stem
        if not (hasattr(self, "stemWidth")) or self.stemWidth == 0:
            self.stemWidth = 1
        glLineWidth(self.stemWidth)
        glColor4f(0.229, 0.259, 0.326, self.transperancy)
        self.app.canvas.drawBezier(self.stemPoints, False)
        glLineWidth(1.0)
            # touch area for better dragging
        glDisable(GL_DEPTH_TEST)
        glColor4f(1, 1, 1, 0.0)
        glBegin(GL_QUADS)
        glVertex3f(-self.size*0.7, 0, -0.1)
        glVertex3f(self.size*0.7, 0, -0.1)
        glVertex3f(self.size*0.7, -self.stemLength, -0.1)
        glVertex3f(-self.size*0.7, -self.stemLength, -0.1)
        glEnd()
        glEnable(GL_DEPTH_TEST)
            # Head
        glEnable( GL_ALPHA_TEST )
        glAlphaFunc( GL_GREATER, 0.1 )        
        glEnable( GL_TEXTURE_2D )
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glBindTexture(GL_TEXTURE_2D, self.texture)
        glTranslate(self.stemPoints[0][0], self.stemPoints[0][1], self.stemPoints[0][2]+0.05)
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

        if not self.childCanTurnIntoBall or not self.childCanTurnIntoBubble:
            glEnable( GL_TEXTURE_2D )
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
            glBindTexture(GL_TEXTURE_2D, self.patterntex)
            glColor4f(1, 1, 1, self.transperancy*0.5)
            glTranslate(0,0,0.05)
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
                
    def shake(self, force):
        '''
        Shake the whole plant, stem rooted in the soil
        '''
        pass
                
    def grow(self):
        '''
        Grow the plant bigger to the set maximum
        '''
        if self.size < self.maxSize:
            self.size += 0.001
            self.pos = (self.pos[0], self.pos[1]+0.004, self.pos[2])
            self.isGrowing = 5 # number of frames that it will report growing
        else:
            self.canGrow=False 
                
    def moveToBasket(self, id):
        if id:
            self.targetBasket = self.app.canvas.objects[id]
        else:
            self.findTargetBasket()
        if self.targetBasket:
            self.interactive = False
            if self.basket == self.targetBasket:
                Logger.warning("Flower "  + str(self.id) + " is already in basket " + str(self.targetBasket.id))
            else:
                Logger.trace("info", "moving flower " + str(self.id) + " to basket " + str(self.targetBasket.id))
                self.targetPos = [self.targetBasket.pos[0]+(0.4*random.random()-0.2), self.targetBasket.pos[1]+self.stemLength-self.targetBasket.size/2, self.targetBasket.pos[2]-0.5]
        else:
            Logger.warning("Cannot move flower "  + str(self.id) + " to basket, no basket found in scene")
            
    def attachToJoint(self, jpos, jori, avatarTCB):
        self.avatarTCB = avatarTCB
        self.objectCollisionTest = False
        rotz_r = math.pi - jori[2]
        if jori[0] < 0:
            self.rotate[2] =  math.degrees(rotz_r)
            self.pos = [jpos[0]-self.stemLength/2*math.sin(rotz_r), jpos[1]+self.stemLength/2*math.cos(rotz_r), self.pos[2]]
        else:
            self.rotate[2] =  math.degrees(rotz_r) + 180
            self.pos = [jpos[0]+self.stemLength/2*math.sin(rotz_r), jpos[1]-self.stemLength/2*math.cos(rotz_r), self.pos[2]]
        self.old_jpos = jpos
            
    def detachFromJoint(self):
        self.avatarTCB = None
        self.objectCollisionTest = True
        self.pos = [self.old_jpos[0], self.old_jpos[1] + self.stemLength/2, self.old_jpos[2]]
        self.rotate = [0,0,0]
        

    def click(self, agentName):
        '''
        pick
        '''
        self.app.canvas.agentPublisher.agentActionCompleted('User', 'flower_pick', [str(self.id)])
        pass
        
    def startDrag(self, newXY):
        self.beingDragged = True
        # Based on http://web.iiit.ac.in/~vkrishna/data/unproj.html
        projection = glGetDoublev(GL_PROJECTION_MATRIX)
        modelview = glGetDoublev(GL_MODELVIEW_MATRIX)
        viewport = glGetIntegerv(GL_VIEWPORT)
        windowZ = glReadPixels(newXY[0], viewport[3]-newXY[1], 1, 1, GL_DEPTH_COMPONENT, GL_FLOAT)
        worldCoords = gluUnProject(newXY[0], viewport[3] - newXY[1], windowZ[0][0], modelview, projection, viewport)
        self.worldDragOffset = [self.pos[0]-worldCoords[0], self.pos[1]-worldCoords[1], 0]
        
    def stopDrag(self):
        self.beingDragged = False
    
    def drag(self, newXY):
        if not self.interactive: return
        # Based on http://web.iiit.ac.in/~vkrishna/data/unproj.html
        projection = glGetDoublev(GL_PROJECTION_MATRIX)
        modelview = glGetDoublev(GL_MODELVIEW_MATRIX)
        viewport = glGetIntegerv(GL_VIEWPORT)
        windowZ = glReadPixels(newXY[0], viewport[3]-newXY[1], 1, 1, GL_DEPTH_COMPONENT, GL_FLOAT)
        
        worldCoords = gluUnProject(newXY[0], viewport[3] - newXY[1], windowZ[0][0], modelview, projection, viewport)
            # started drag outside the flower head
        if self.worldDragOffset[1] > self.size:
            # drag
            self.pos = [worldCoords[0]+self.worldDragOffset[0], worldCoords[1]+self.worldDragOffset[1], self.pos[2]]                
            self.locationChanged = True
            if self.avatarTCB:
                self.avatarTCB.detachObject(self)
            # started drag in within the flowerhead
        else:
                # into Bubble
            if self.magic and self.childCanTurnIntoBubble and worldCoords[1] > (self.pos[1] + self.size/2):
                if self.avatarTCB:
                    self.avatarTCB.detachObject(self)
                self.intoBubble(True)
                # into Ball
            elif self.magic and self.childCanTurnIntoBall and worldCoords[1] < (self.pos[1] - self.size/2):
                if self.avatarTCB:
                    self.avatarTCB.detachObject(self)
                self.intoBall(True)
                # swing
            else:
                self.swing = max(min((worldCoords[0] - self.pos[0]) / self.size, 1), -1)
                self.amplitude = math.fabs(self.swing)
                self.swing = self.swing * math.pi / 2 # for max amplitude
                
    def intoBubble(self, byUser=False):
        if self.canTurnIntoBubble:
            bubble = Bubbles.EchoesBubble(self.app, True, fadeIn=True, fadingFrames=10)
            bubble.setStartPos(self.pos)
            bubble.size = self.size
            bubble.willBeReplaced = False
            if self.pot:
                self.pot.flower = None
            if self.basket:
                self.basket.removeFlower(self)
            self.remove()
            if byUser:
                self.app.canvas.agentPublisher.agentActionCompleted('User', 'flower_bubble', [str(self.id), str(bubble.id)])
            self.app.canvas.rlPublisher.objectPropertyChanged(str(self.id), "flower_bubble", str(bubble.id))

    def intoBall(self, byUser=False):
        if self.canTurnIntoBall:
            ball = PlayObjects.Ball(self.app, True, fadeIn=True, fadingFrames=10)
            ball.pos = self.pos
            ball.size = self.size
            ball.colour = self.colour
            if self.pot:
                self.pot.flower = None
            if self.basket:
                self.basket.removeFlower(self)
            self.remove()       
            if byUser:
                self.app.canvas.agentPublisher.agentActionCompleted('User', 'flower_ball', [str(self.id), str(ball.id)])
            self.app.canvas.rlPublisher.objectPropertyChanged(str(self.id), "flower_ball", str(ball.id))
        
    def remove(self, fadeOut = False, fadingFrames = 100):
        if self.avatarTCB:
            self.detachFromJoint()
        super(EchoesFlower, self).remove(fadeOut, fadingFrames)
        
        
class Pot(EchoesObject):
    '''
    classdocs
    '''
    def __init__(self, app, autoAdd=True, props={"type": "Pot"}, fadeIn = False, fadingFrames = 100, callback=None):
        '''
        Constructor
        '''
        super(Pot, self).__init__(app, autoAdd, props, fadeIn, fadingFrames, callback)
        
        self.size = 0.3 + random.random()*0.2
        self.pos = [-1,-2.5,0.1] 
        self.publishRegion = True
        self.underCloud = False
        
        self.canBeDraged = True
        self.publishGrowStarted = False
        
        self.defaultHeight = self.app.canvas.getRegionCoords("ground")[0][1]
        self.fallToDefaultHeight = True
        self.falling = False

        # basic shape in two strips [x,y, colour shade value]
        self.shape = [[[-1, 0.5, 1], [1, 0.5, 0.8], [1, 0.7, 0.8], [-1, 0.7, 1]],
                      [[-0.8, 0.5, 1], [-0.6, -0.7, 0.6], [0.6, -0.7, 0.6], [0.8, 0.5, 1]]]

        # a random neutral shade 
        self.neutralshade = ["neutral-1", "neutral-2", "neutral-3", "neutral-4", "neutral-5"][random.randint(0,4)]  

        # the flower growing out of the pot 
        self.flower = None
        self.stack = None
                                    
        if "colour" in self.props:
            self.colour = self.props["colour"]
            self.neutralshade = self.props["colour"]
        else:
            self.colour = self.neutralshade
                    
        self.avatarTCB = None

    def __setattr__(self, item, value):
        if item == "colour":
            if value == "dark":
                self.basecolour = [0.770, 0.371, 0.082, 1.0]
                self.linecolour = [0.3,0.1,0.1,1]
            elif value == "neutral-1":                
                self.basecolour = [1.000, 0.609, 0.277, 1.000]
                self.linecolour = [0.3,0.1,0.1,1]
            elif value == "neutral-2":                
                self.basecolour = [0.955, 0.878, 0.471, 1.000]
                self.linecolour = [0.3,0.1,0.1,1]
            elif value == "neutral-3":                
                self.basecolour = [1.000, 0.796, 0.634, 1.000]
                self.linecolour = [0.3,0.1,0.1,1]
            elif value == "neutral-4":                
                self.basecolour = [0.872, 0.655, 0.133, 1.000]
                self.linecolour = [0.3,0.1,0.1,1]
            else: # neutral is the default
                self.basecolour = [0.970, 0.571, 0.282, 1.0]
                self.linecolour = [1,0,0,1]
                
        elif item == "flower" and isinstance(value, EchoesFlower):
            if hasattr(self, "hasOnTop") and self.hasOnTop: 
                Logger.warning("Pot: can't have flower in pot that has other pots on top of it")
                return
            self.app.canvas.rlPublisher.objectPropertyChanged(str(self.id), "pot_flower", str(value.id))
            value.pos = [self.pos[0], self.pos[1]+value.stemLength+self.size/2, self.pos[2]-0.01]
            value.inCollision = self.id
            value.pot = self
            
            Logger.trace("info", "Flower put into pot" + str(self.id) )
            if value.beingDragged:
                self.app.canvas.agentPublisher.agentActionCompleted('User', 'flower_placeInPot', [str(self.id), str(value.id)])
            
        elif item == "flower" and value == None:
            self.app.canvas.rlPublisher.objectPropertyChanged(str(self.id), "pot_flower", "None")
        
        elif item == "pos" and hasattr(self, "pos"): 
            if hasattr(self, "stack") and self.stack and ((hasattr(self, "beingDragged") and self.beingDragged) or (hasattr(self, "avatarTCB") and self.avatarTCB)):
                # If the user did it, notify the rest of the system
                split = self.stack.split(self)
                if split and hasattr(self, "beingDragged") and self.beingDragged:
                    self.app.canvas.agentPublisher.agentActionCompleted('User', 'unstack_pot', [str(self.id)])
                if self.stack: # the stack might be removed if its the only pot left
                    for pot in self.stack.pots:
                        if pot != self: 
                            dx = self.pos[0]-pot.pos[0]
                            dy = self.pos[1]-pot.pos[1]
                            pot.pos = [value[0]-dx, value[1]-dy, pot.pos[2]]   
            if hasattr(self, "flower") and self.flower:
                self.flower.pos = [value[0], value[1]+self.flower.stemLength+self.size/2, value[2]-0.01]

            if hasattr(self, "underCloud"):
                for oid, o in self.app.canvas.objects.items():
                    if isinstance(o, objects.Environment.Cloud):
                        if o.isUnder(self):
                            if not self.underCloud: self.underCloud = True
                        else:
                            if self.underCloud: self.underCloud = False
                            
        elif item == "stack":
            if value == None:
                self.hasOnTop = None
                self.isOnTopOf = None
                self.colour = self.neutralshade
                self.app.canvas.rlPublisher.objectPropertyChanged(str(self.id), "pot_stack", "False")
            else:
                self.colour = "dark"
                self.app.canvas.rlPublisher.objectPropertyChanged(str(self.id), "pot_stack", "True")
                           
        elif item == "underCloud":
            self.app.canvas.rlPublisher.objectPropertyChanged(str(self.id), "under_cloud", str(value))
  
        elif item == "hasOnTop":
            self.app.canvas.rlPublisher.objectPropertyChanged(str(self.id), "has_on_top", str(value))

        elif item == "isOnTopOf":
            self.app.canvas.rlPublisher.objectPropertyChanged(str(self.id), "is_on_top_of", str(value))

        object.__setattr__(self, item, value)
        
    def renderObj(self):
        ''' 
        overwriting the render method to draw the pot
        '''
        if not hasattr(self, "stack"): return # in case rendering is called before the object is fully built
        
        if self.stack:
            if self.stack.pots[len(self.stack.pots)-1] == self:
                if self.hasOnTop:self.hasOnTop = None
            else:
                if not self.hasOnTop:
                    i = self.stack.pots.index(self)
                    self.hasOnTop = self.stack.pots[i+1].id
            if self.stack.pots[0] == self:
                if self.isOnTopOf:self.isOnTopOf = None
            else:
                if not self.isOnTopOf:
                    i = self.stack.pots.index(self)
                    self.isOnTopOf = self.stack.pots[i-1].id                    
        
        if self.fallToDefaultHeight and not self.beingDragged and not self.avatarTCB:
            hdiff = self.pos[1] - self.defaultHeight
            if abs(hdiff) > 0.05:
                if not self.stack: # no stack
                    self.pos = [self.pos[0], self.pos[1]-hdiff/10, self.pos[2]]
                    self.falling = True
                elif self==self.stack.pots[0]: # lowest of stack        
                    for pot in self.stack.pots:
                        pot.pos = [pot.pos[0], pot.pos[1]-hdiff/10, pot.pos[2]]
                        pot.falling = True
                else:
                    self.falling = False
            else:
                self.falling = False
                        
        glPushMatrix()
        glTranslate(self.pos[0], self.pos[1], self.pos[2])
        glScalef(self.size, self.size, self.size)
        c = self.basecolour
        for rectangle in self.shape:
            glBegin( GL_QUADS )
            for v in rectangle:
                glColor4f(c[0]*v[2], c[1]*v[2], c[2]*v[2], c[3]*self.transperancy)
                glVertex(v[0],v[1], self.pos[2])
            glEnd()
            glLineWidth(3.0)
            glBegin( GL_LINE_STRIP )
            glColor4f(self.linecolour[0], self.linecolour[1], self.linecolour[2], self.linecolour[3]*self.transperancy)            
            for v in rectangle:
                glVertex(v[0],v[1], self.pos[2])
            glEnd()
            glLineWidth(1.0)
        glPopMatrix()
            
    def growFlower(self):
        if not self.hasOnTop:
            if not self.flower:
                self.flower = EchoesFlower(self.app, True, fadeIn=True)
                self.flower.size = 0.1
                self.flower.pos = [self.pos[0], self.pos[1]+self.flower.stemLength+self.size/2, self.pos[2]-0.01]
            else:
                self.flower.grow()
    
    def click(self, agentName):
        '''
        pick
        '''
        pass
        
    def startDrag(self, newXY):
        if self.avatarTCB:
            self.avatarTCB.detachObject(self)
        self.beingDragged = True
        # Based on http://web.iiit.ac.in/~vkrishna/data/unproj.html
        projection = glGetDoublev(GL_PROJECTION_MATRIX)
        modelview = glGetDoublev(GL_MODELVIEW_MATRIX)
        viewport = glGetIntegerv(GL_VIEWPORT)
        windowZ = glReadPixels(newXY[0], viewport[3]-newXY[1], 1, 1, GL_DEPTH_COMPONENT, GL_FLOAT)
        worldCoords = gluUnProject(newXY[0], viewport[3] - newXY[1], windowZ[0][0], modelview, projection, viewport)
        self.worldDragOffset = [self.pos[0]-worldCoords[0], self.pos[1]-worldCoords[1], 0] 
        
    def stopDrag(self):
        self.beingDragged = False
        if self.publishGrowStarted:
            self.publishGrowStarted = False
            self.app.canvas.agentPublisher.agentActionCompleted('User', 'flower_grow', [str(self.id), str(self.flower.id), str(self.growPond)])

    def drag(self, newXY):
        if self.interactive and self.canBeDraged:
            # Based on http://web.iiit.ac.in/~vkrishna/data/unproj.html
            projection = glGetDoublev(GL_PROJECTION_MATRIX)
            modelview = glGetDoublev(GL_MODELVIEW_MATRIX)
            viewport = glGetIntegerv(GL_VIEWPORT)
            windowZ = glReadPixels(newXY[0], viewport[3]-newXY[1], 1, 1, GL_DEPTH_COMPONENT, GL_FLOAT)
            
            worldCoords = gluUnProject(newXY[0], viewport[3] - newXY[1], windowZ[0][0], modelview, projection, viewport)
            if self.beingDragged:
                if self.fallToDefaultHeight:
                    self.pos = [worldCoords[0]+self.worldDragOffset[0], max(self.defaultHeight, worldCoords[1]+self.worldDragOffset[1]), self.pos[2]]
                else:
                    self.pos = [worldCoords[0]+self.worldDragOffset[0], worldCoords[1]+self.worldDragOffset[1], self.pos[2]]                
                self.locationChanged = True
                
    def attachToJoint(self, jpos, jori, avatarTCB):
        self.avatarTCB = avatarTCB
        self.objectCollisionTest = False        
        if self.fallToDefaultHeight:
            y = max(jpos[1], self.defaultHeight)
        else:
            y = jpos[1]
        self.pos = [jpos[0], y, self.pos[2]]
            
    def detachFromJoint(self):
        self.avatarTCB = None
        self.objectCollisionTest = True        
        
    def stackUp(self, pot):
        if not self.stack and not pot.stack:
            self.stack = pot.stack = Stack(self.app)
            self.stack.pots = [self, pot]
        elif self.stack and pot.stack:
            newstack = Stack(self.app)
            newstack.pots = self.stack.pots + pot.stack.pots
            for pot in newstack.pots:
                pot.stack = newstack
        elif self.stack or pot.stack:
            if pot.stack: 
                pot.stack.pots = [self] + pot.stack.pots
                self.stack = pot.stack
            else:
                self.stack.pots = self.stack.pots + [pot]
                pot.stack = self.stack                    
        self.stack.checkAlignment()

    def remove(self, fadeOut = False, fadingFrames = 100):
        if not fadeOut and self.stack and self in self.stack.pots:
            self.objectCollisionTest = False
            del self.stack.pots[self.stack.pots.index(self)]
            self.stack = None
        super(Pot, self).remove(fadeOut, fadingFrames)


class Stack():
    '''
    classdocs
    '''
    def __init__(self, app):
        '''
        Constructor
        '''
        self.app = app        
        self.pots = []
        
        self.objectCollisionTest = False
        self.agentCollisionTest = False
        
    def top(self):
        l = len(self.pots)
        if l > 0:
            return self.pots[l-1]
        else:
            return None
    
    def bottom(self):
        if len(self.pots) > 0:
            return self.pots[0]
        else:
            return None
        
    def split(self, pot):
        # if pot is the lowest anyway
        if self.pots[0] == pot: return False
        #if there are only two pots in the stack
        if len(self.pots) == 2:
            self.pots[0].stack = self.pots[1].stack = None
            self.pots = []
            return True
        # if pot splits stack with one pot left
        if self.pots[1] == pot:
            self.pots[0].stack = None
            del self.pots[0]
            return True
        if self.pots[len(self.pots)-1] == pot:
            pot.stack = None
            del self.pots[len(self.pots)-1]
            return True
        # split stack into two stacks
        newStack = Stack(self.app)
        while self.pots[0] != pot:
            newStack.pots.append(self.pots[0])
            self.pots[0].stack = newStack
            del self.pots[0]
        return True        
            
    def checkAlignment(self):
        prevPot = None
        for pot in self.pots:
            if prevPot:
                x, y, z = pot.pos
                if abs(x - prevPot.pos[0]) > prevPot.size / 1.5:
                    x = prevPot.pos[0] + random.uniform(-0.1,0.1)
                if isinstance(pot, objects.Plants.Pot) and isinstance(prevPot, objects.Plants.Pot):
                    y = prevPot.pos[1] + prevPot.size + pot.size * 0.37
                else: # the upper pot is really a basket
                    y = prevPot.pos[1] + prevPot.size + pot.size * 0.9
                z = prevPot.pos[2]-0.01
                pot.pos = [x,y,z]
            prevPot = pot 

    def intoTree(self):
        Logger.trace("info", "replacing stack with tree") 
        tree = LifeTree(self.app, True, fadeIn=True)
        size = 0 
        for pot in self.pots:
            size += pot.size
        size += 2.5
        tree.size = size
        lowest = self.pots[0]
        tree.pos = [lowest.pos[0], lowest.pos[1] + size/2, lowest.pos[2]]
        

class LifeTree(EchoesObject):
    '''
    classdocs
    '''
    def __init__(self, app, autoAdd=True, props={"type": "LifeTree"}, fadeIn = False, fadingFrames = 100, callback=None):
        '''
        Constructor
        '''
        super(LifeTree, self).__init__(app, autoAdd, props, fadeIn, fadingFrames, callback)
        
        self.size = 3.5
        self.pos = (-2.5,-0.5,-1)         
        
        self.texture = self.setImage("visual/images/LifeTree.png")
        self.shape = [(-0.5, -0.5), (0.5, -0.5), (0.5, 0.5), (-0.5, 0.5)]
        self.texshape = [(0, 0), (1, 0), (1, 1), (0, 1)]
        
        self.leaves = [None, None, None, None]

    def __setattr__(self, item, value):
        if item == "pos":
            pass
                        
        object.__setattr__(self, item, value)
    
    def getFreeBranch(self):
        branch = 0
        for leaf in self.leaves:
            if not leaf:
                return branch
            branch += 1
        return -1
        
    def renderObj(self):
        ''' 
        overwriting the render method to draw the flower
        '''            
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
        
class MagicLeaves(EchoesObject, Motions.BezierMotion):
    '''
    classdocs
    '''
    def __init__(self, app, autoAdd=True, props={"type": "MagicLeaves"}, fadeIn = False, fadingFrames = 100, callback=None):
        '''
        Constructor
        '''
        super(MagicLeaves, self).__init__(app, autoAdd, props, fadeIn, fadingFrames, callback)
        super(MagicLeaves, self).initBezierVars()       
        
        self.size = 0.5
        self.pos = [0,0,0]
        self.orientation = 0
        self.speed = 0.04
    
        self.flying = True
        self.flyingXY = True

        self.newctrlpoints()
        self.drawCtrlPoints = False
        self.removeAtTargetPos = False

        self.flapamplitude = 45 # max opening angle when flapping in degrees 
        self.flap = 0
    
        self.energy = 1.0
    
        self.setImage()
        self.shape = [(0, 0), (1, 0), (1, 1), (0, 1)]
        self.texshape = [(0, 0), (1, 0), (1, 1), (0, 1)]
        
        self.tree = None
        self.putOnTree()

    def __setattr__(self, item, value):
        if item == "energy":
            self.flapamplitude = 45 * value
            if value > 0.8:
                self.boundingBox = self.app.canvas.getRegionCoords("v-top")
            elif value > 0.6:
                self.boundingBox = self.app.canvas.getRegionCoords("v-middle")
            elif value > 0.3:
                self.boundingBox = self.app.canvas.getRegionCoords("v-bottom")
            else:
                self.boundingBox = self.app.canvas.getRegionCoords("ground")
            self.speed = 0.01 * value
                        
        if item == "flying":
            self.app.canvas.rlPublisher.objectPropertyChanged(str(self.id), "leaves_flying", str(value))

        object.__setattr__(self, item, value)

    def setImage(self):
        images = ['Leaf1.png', 'Leaf2.png']
        self.textures = glGenTextures(len(images))
        i = 0
        for image in images:
            im = PIL.Image.open("visual/images/" + image)
            try:                
                ix, iy, idata = im.size[0], im.size[1], im.tostring("raw", "RGBA", 0, -1)
            except SystemError:
                ix, iy, idata = im.size[0], im.size[1], im.tostring("raw", "RGBX", 0, -1)        

            glPixelStorei(GL_UNPACK_ALIGNMENT,1)
            glBindTexture(GL_TEXTURE_2D, self.textures[i])
            glTexImage2D(GL_TEXTURE_2D, 0, 4, ix, iy, 0, GL_RGBA, GL_UNSIGNED_BYTE, idata)        
            i += 1
    
            
    def renderObj(self):
        ''' 
        overwriting the render method to draw the flower
        '''            
        glPushMatrix()
        glEnable( GL_ALPHA_TEST )
        glAlphaFunc( GL_GREATER, 0.1 )
        
        if self.energy > 0:
            self.energy -= 0.0005
        else:
            self.energy = 0
        
        if self.flying and self.interactive:
            oldpos = self.pos
            self.pos = self.nextBezierPos(self.flyingXY)
            if self.pos[0]!=oldpos[0] or self.pos[1]!=oldpos[1] or self.pos[2]!=oldpos[2]:
                self.orientation = math.atan2(self.pos[1]-oldpos[1], self.pos[0]-oldpos[0])  
            if self.removeAtTargetPos and self.bezierIndex > 0.95:
                self.remove()
            
            self.flap = (self.flap + 0.4) % (2*math.pi)
        
        glTranslate(self.pos[0], self.pos[1], self.pos[2])
        glScalef(self.size, self.size, self.size)
        glRotate(math.degrees(self.orientation), 0,0,1)
        
        angle =  self.flapamplitude * (1+math.sin(self.flap))

        if self.flying or self.beingDragged:
            glColor4f(0.584, 0.060, 0.025, self.transperancy)        
            glBegin(GL_QUADS)
            glVertex3f(0.5*self.size, 0.05*self.size, self.pos[2])
            glVertex3f(0.5*self.size, -0.05*self.size, self.pos[2])
            glVertex3f(-0.5*self.size, -0.05*self.size, self.pos[2])
            glVertex3f(-0.5*self.size, 0.05*self.size, self.pos[2])
            glEnd()
            
        i = 0
        olda = 0
        for texture in self.textures:
            glEnable( GL_TEXTURE_2D )
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
            glBindTexture(GL_TEXTURE_2D, texture)
            a = math.pow(-1, i) * angle - olda
            olda = a 
            glRotate(a, 1,0,0.25)              
            glColor4f(1, 1, 1, self.transperancy)
            glBegin(GL_QUADS)
            ti = 0
            for v in self.shape:
                glTexCoord2d(self.texshape[ti][0], self.texshape[ti][1])
                glVertex3f(v[0], v[1], 0)
                ti += 1
            glEnd()
            glDisable( GL_TEXTURE_2D )
            i += 1
            
        glDisable( GL_ALPHA_TEST )
        glPopMatrix()
                
    def startDrag(self, pos=(0,0)):
        self.app.canvas.agentPublisher.agentActionCompleted('User', 'touch_leaves', [str(self.id)])
        self.beingDragged = True
        self.energy = 0
        self.flying = False
        if self.tree:
            branch = 0
            for leaf in self.tree.leaves:
                if leaf == self: self.tree.leaves[branch] = None
                branch += 1
                
    def stopDrag(self):
        self.beingDragged = False
        h = float(self.app.canvas.orthoCoordWidth / self.app.canvas.aspectRatio)
        self.energy = (self.pos[1] + h/2)/h
        self.newctrlpoints()
        self.flying = True
    
    def drag(self, newXY):
        if self.interactive:
            # Based on http://web.iiit.ac.in/~vkrishna/data/unproj.html
            projection = glGetDoublev(GL_PROJECTION_MATRIX)
            modelview = glGetDoublev(GL_MODELVIEW_MATRIX)
            viewport = glGetIntegerv(GL_VIEWPORT)
            windowZ = glReadPixels(newXY[0], viewport[3]-newXY[1], 1, 1, GL_DEPTH_COMPONENT, GL_FLOAT)
            
            worldCoords = gluUnProject(newXY[0], viewport[3] - newXY[1], windowZ[0][0], modelview, projection, viewport)

            self.pos = (worldCoords[0], worldCoords[1], self.pos[2])

    def touchLeaves(self, agent_id=None):
#        if agent_id:
#            self.app.canvas.agentPublisher.agentActionCompleted('Agent', 'touch_leaves', [str(self.id), str(agent_id)])

        if self.tree:
            branch = 0
            for leaf in self.tree.leaves:
                if leaf == self: self.tree.leaves[branch] = None
                branch += 1
        h = float(self.app.canvas.orthoCoordWidth / self.app.canvas.aspectRatio)
        self.energy = (self.pos[1] + h/2)/h
        self.newctrlpoints()
        self.flying = True
        
    def putOnTree(self, id=None, branch=-1):
        if not id:
            for oid, se in self.app.canvas.objects.items():
                if isinstance(se, LifeTree):
                    id = oid
                    break
        if not id:
            Logger.warning("No tree found to put magic leaves on")
            return
        tree = self.app.canvas.objects[id]
        if branch==-1:
            branch = tree.getFreeBranch()
        if branch==-1:
            Logger.warning("No free tree branch found to put magic leaves on")
            return

        self.energy = 0.0
        self.flying = False

        tree.leaves[branch] = self
        self.tree = tree
        if branch == 0:
            dx = -0.47
            dy = 0.35
            self.orientation = 1.5
        elif branch == 1:
            dx = -0.15
            dy = 0.49
            self.orientation = 0.2
        elif branch == 2:
            dx = 0.19
            dy = 0.47
            self.orientation = -0.2
        else:
            dx = 0.47
            dy = 0.26
            self.orientation = -0.5
        self.pos = (tree.pos[0]+tree.size*dx, tree.pos[1]+tree.size*dy, tree.pos[2])
                        
    def remove(self, fadeOut=False, fadingFrames=100):
        super(MagicLeaves, self).remove(fadeOut, fadingFrames)            

