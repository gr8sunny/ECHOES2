'''
Created on 7 Oct 2009

@author: cfabric
'''
from EchoesObject import *
from OpenGL.GL import *
from OpenGL.GLU import *
from OpenGL.GLUT import *
import echoes
import environment
import objects.Plants
import PIL.Image
import sound.EchoesAudio
import Bubbles, PlayObjects
import Motions
import Logger

class Cloud(EchoesObject):
    '''
    classdocs
    '''
    def __init__(self, app, autoAdd=True, props={"type": "Cloud"}, fadeIn = False, fadingFrames = 100, callback=None):
        '''
        Constructor
        '''
        super(Cloud, self).__init__(app, autoAdd, props, fadeIn, fadingFrames, callback)
       
        self.size = 0.3
        self.pos = (-0.39, 2.25, 0)
        self.colour = "white"
        self.shape = [(0, -1)]
        self.shape += [(2.5+math.cos(math.radians(deg)), math.sin(math.radians(deg))) for deg in xrange(-90, 135, 10)]
        self.shape += [(1.8*math.cos(math.radians(deg)), 1+math.sin(math.radians(deg))) for deg in xrange(-45, 225, 10)]
        self.shape += [(math.cos(math.radians(deg))-2.5, math.sin(math.radians(deg))) for deg in xrange(45, 270, 10)]
        self.shape += [(0,-1)]
        
        self.objectCollisionTest = True
        self.agentCollisionTest = False
        
        self.canRain = True
        self.raining = False
        self.userRain = False
        self.shakeAmplitude = 0
        self.shake = 0
        
        self.dimSun = True

        self.diffPos = (0,0)
        self.prevDiffPos = (0,0)
        self.curDirChangesPF = 0   # change in directions per 20 frames when dragged
        self.avDirChangesPF = 0
        self.fcounter = 0
        
        self.hitBy = None
        self.hitByFCounter = 0
        self.b_colours = ["yellow", "blue", "green"]
        self.b_nextcolour = 0
        
        self.avatarTCB = None
        self.avatarRain = False
        
        self.setImage()
        
        if sound.EchoesAudio.soundPresent:
            self.rainSound = sound.EchoesAudio.playSound("rain.wav", True, 0.0)
        else:
            self.rainSound = None
        
    def __setattr__(self, item, value):
        if item == "pos" and hasattr(self, "pos"): # make sure this is only done when the object is fully built
            self.objectsUnderCloud() # notify the objects that come under the cloud
        
        if item == "hitBy" and hasattr(self, "hitBy"):
            if value != self.hitBy and value != None:
                self.app.canvas.rlPublisher.objectPropertyChanged(str(self.id), "cloud_hitby", str(value.id))
                self.hitByFCounter = 30
                if not value.thrownByAvatar:
                    self.app.canvas.agentPublisher.agentActionCompleted('User', 'cloud_ball', [str(self.id), str(value.id)])         

        if item == "colour":
            if value == "red": self.cv = [0.735,0.197,0.286]
            elif value == "green": self.cv = [0.439,0.633,0.245]
            elif value == "blue": self.cv = [0.220,0.481,0.628]
            elif value == "yellow": self.cv = [0.921,0.832,0.217]
            else: self.cv = [0.96,0.95,1]

        object.__setattr__(self, item, value)

    def setImage(self, file='visual/images/Rain-drop.png'):
        im = PIL.Image.open(file) # .jpg, .bmp, etc. also work
        try:
            ix, iy, image = im.size[0], im.size[1], im.tostring("raw", "RGBA", 0, -1)
        except SystemError:
            ix, iy, image = im.size[0], im.size[1], im.tostring("raw", "RGBX", 0, -1)        

        self.dropTexture = glGenTextures(1)
        glPixelStorei(GL_UNPACK_ALIGNMENT,1)
        glBindTexture(GL_TEXTURE_2D, self.dropTexture)
        glTexImage2D(GL_TEXTURE_2D, 0, 4, ix, iy, 0, GL_RGBA, GL_UNSIGNED_BYTE, image)
        self.dropSize = (ix, iy)
               
    def renderObj(self):
        glPushMatrix()
        glDisable(GL_DEPTH_TEST)

        self.fcounter += 1
        if self.fcounter > 40:
            self.avDirChangesPF = self.curDirChangesPF
            self.curDirChangesPF = 0
            self.fcounter = 0
                          
        if self.canRain and self.avDirChangesPF > 2:
            if not self.raining:
                self.app.canvas.rlPublisher.objectPropertyChanged(str(self.id), "cloud_rain", "True")
                self.raining = True
                if self.rainSound:
                    self.rainSound.set("mul", 0.8, 0.05)
                if not self.avatarRain:
                    self.app.canvas.agentPublisher.agentActionStarted('User', 'cloud_rain', [str(self.id)])
                    
            for i in xrange(20):
                self.drawRainDrop((self.pos[0] + (random.random()-0.5) * self.size *2.5, self.pos[1] - random.random() * 5,0), random.random() * 0.3)
            
            foundObject = False
            for i, object in self.app.canvas.objects.items():
                if self.isUnder(object):
                    if isinstance(object, Pond) and object.canGrow: 
                        object.grow()
                        foundObject = True
                        break
                    elif isinstance(object, objects.Plants.Pot):
                        if ((not object.flower or object.flower.canGrow) and   
                            (not object.stack or object.stack.top() == object)):
                            object.growFlower()
                            foundObject = True
                            break
                    elif isinstance(object, objects.Environment.Basket):
                        object.growFlowers()
                        foundObject = True
                        break
                    elif isinstance(object, objects.Plants.EchoesFlower) and object.canGrow:
                        object.grow()
                        foundObject = True
                        break
            if not foundObject:
                flower = objects.Plants.EchoesFlower(self.app, True, fadeIn=True)
                flower.size = 0.1
                flower.pos = [self.pos[0], self.app.canvas.getRegionCoords("ground")[1][1], self.pos[2]]
                self.app.canvas.rlPublisher.objectPropertyChanged(str(self.id), "cloud_flower", str(flower.id))
                if not self.avatarRain:
                    bs = None
                    for id, object in self.app.canvas.sceneElements.items():
                        if isinstance(object, environment.HelperElements.Score):
                            bs = object
                    if bs:
                        bs.increment()
                        if self.app.canvas.publishScore:
                            self.app.canvas.rlPublisher.worldPropertyChanged("FlowerScore", str(bs.score))

                
        else:
            if self.raining:
                if self.rainSound:
                    self.rainSound.set("mul", 0.0, 0.1)
                if not self.avatarRain:
                    self.app.canvas.agentPublisher.agentActionCompleted('User', 'cloud_rain', [str(self.id)])
                self.app.canvas.rlPublisher.objectPropertyChanged(str(self.id), "cloud_rain", "False")
                self.raining = False
                self.avatarRain = False
                
        if self.shakeAmplitude > 0:
            shakeX = math.sin(self.shake) * self.shakeAmplitude
            self.shakeAmplitude -= 0.01
            self.shake = (self.shake + 0.7) % (2*math.pi)
        else: 
            shakeX = 0

        if self.colour != "white":
            for i in range(3):
                self.cv[i] = min(1, self.cv[i]+0.005)
            if self.cv[0] == self.cv[1] == self.cv[2] == 1:
                self.colour = "white"
        # reset hits in 30 frames
        if self.hitBy and self.hitByFCounter > 0:
            self.hitByFCounter -= 1
            if self.hitByFCounter <= 0:
                self.hitBy = None
        
        glTranslate(self.pos[0] + shakeX, self.pos[1], self.pos[2])
        glScale(self.size, self.size, self.size)
        glColor4f(self.cv[0], self.cv[1], self.cv[2], self.transperancy)
        glBegin(GL_TRIANGLE_FAN)
        glVertex2f(0,0)
        for v in self.shape:
            glVertex3f(v[0], v[1], self.pos[2])
        glEnd()
        glLineWidth(3.0)
        glColor4f(0.385, 0.691, 1.0, self.transperancy)
        glBegin(GL_LINE_STRIP)
        for v in self.shape:
            glVertex3f(v[0], v[1], self.pos[2]+0.1)
        glEnd()
        glLineWidth(1.0)

        glEnable(GL_DEPTH_TEST)
        glPopMatrix()
        
    def rain(self, frames=40):
        self.shakeAmplitude = 0.3
        self.fcounter = -1*(frames-40)
        self.avDirChangesPF = 3
        self.avatarRain = True
        
    def drawRainDrop(self, pos, size=1):

        glPushMatrix()
        glBlendFunc(GL_ONE, GL_ONE)        
        glEnable( GL_TEXTURE_2D )
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glBindTexture(GL_TEXTURE_2D, self.dropTexture)
        glTranslate(pos[0], pos[1], pos[2])
        glScalef(size, size, size)
        glColor4f(1, 1, 1, self.transperancy)
        dropRatio = self.dropSize[1]/self.dropSize[0]
        glBegin(GL_QUADS)
        glTexCoord2d(0.0,0.0)
        glVertex2d(0,0)
        glTexCoord2d(1.0,0.0)
        glVertex2d(1,0)
        glTexCoord2d(1.0,1.0)
        glVertex2d(1,dropRatio)
        glTexCoord2d(0.0,1.0)
        glVertex2d(0,dropRatio)
        glEnd()
        glDisable( GL_TEXTURE_2D )        
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        glPopMatrix()


    def startDrag(self, pos):
        if self.interactive:
            self.beingDragged = True
            self.locationChanged = False
            self.dragStartWorld = self.pos
        
    def stopDrag(self):
        if self.interactive:
            self.beingDragged = False
            self.locationChanged = False
    
    def drag(self, newXY):
        if self.interactive:
            # Based on http://web.iiit.ac.in/~vkrishna/data/unproj.html
            projection = glGetDoublev(GL_PROJECTION_MATRIX)
            modelview = glGetDoublev(GL_MODELVIEW_MATRIX)
            viewport = glGetIntegerv(GL_VIEWPORT)
            windowZ = glReadPixels(newXY[0], viewport[3]-newXY[1], 1, 1, GL_DEPTH_COMPONENT, GL_FLOAT)
            
            worldCoords = gluUnProject(newXY[0], viewport[3] - newXY[1], windowZ[0][0], modelview, projection, viewport)

            self.prevDiffPos = self.diffPos
            self.diffPos = (worldCoords[0]-self.pos[0], worldCoords[1]-self.pos[1])
            # check if any of the directional changes has changed sign
            if (self.prevDiffPos[0]*self.diffPos[0]) < 0 or (self.prevDiffPos[1]*self.diffPos[1]) < 0:
                self.curDirChangesPF +=1
            
            sky = self.app.canvas.getRegionCoords("sky")
            self.pos = (worldCoords[0], max(worldCoords[1], sky[1][1]), self.pos[2])            
                                
            self.locationChanged = True
        
            if self.dimSun:
                for id,object in self.app.canvas.objects.items():
                    if isinstance(object, Sun):
                        distance = (math.hypot(self.pos[0]-object.pos[0], self.pos[1]-object.pos[1]))
                        self.app.canvas.setLight(min(0.8,distance/2.0))
                        break
    
    def objectsUnderCloud(self):
        for oid, object in self.app.canvas.objects.items():
            if hasattr(object, "underCloud"):
                if self.isUnder(object):
                    if not object.underCloud: object.underCloud = True
                else:
                    if object.underCloud: object.underCloud = False
                    
    def isUnder(self, object):
        if object.pos[0] > (self.pos[0] - 3.5*self.size) and object.pos[0] < (self.pos[0] + 3.5*self.size):
            return True            
        else:
            return False
        
    def attachToAvatar(self, apos, aori, avatarTCB=None):
        if not self.avatarTCB:
            self.avatarTCB = avatarTCB
            self.xoffset = abs(self.pos[0] - apos[0])
        xoff = self.xoffset * math.sin(-aori[2])
        self.pos = [apos[0]+xoff, self.pos[1], self.pos[2]]
            
    def detachFromAvatar(self):
        self.avatarTCB = None
        

class Pond(EchoesObject):
    '''
    classdocs
    '''
    def __init__(self, app, autoAdd=True, props={"type": "Pond"}, fadeIn = False, fadingFrames = 100, callback = None):
        '''
        Constructor
        '''
        super(Pond, self).__init__(app, autoAdd, props, fadeIn, fadingFrames, callback)
        self.pos = (0, -2.5, 0)
        self.size = 0.2
        self.maxSize = 1
        self.maxSize = 0.1
        self.shape = [(math.cos(math.radians(deg)), math.sin(math.radians(deg))) for deg in xrange(-180, 1, 10)]
        self.shape += [(1+ 0.5*math.cos(math.radians(deg)), 0.5+0.5*math.sin(math.radians(deg))) for deg in xrange(-90, 91, 10)]
        self.shape += [(1+ 0.2*math.cos(math.radians(deg)), 1.2+0.2*math.sin(math.radians(deg))) for deg in xrange(-90, 91, 10)]
        self.shape += [(math.cos(math.radians(deg)), 1.4+0.5*math.sin(math.radians(deg))) for deg in xrange(0, 181, 10)]
        self.shape += [(-1+ 0.2*math.cos(math.radians(deg)), 1.2+0.2*math.sin(math.radians(deg))) for deg in xrange(90, 271, 10)]
        self.shape += [(-1+ 0.5*math.cos(math.radians(deg)), 0.5+0.5*math.sin(math.radians(deg))) for deg in xrange(90, 271, 10)]
        
        self.canGrow = True
        self.canShrink = True
              
    def renderObj(self):
        
        glPushMatrix()
        glDisable(GL_DEPTH_TEST)

        glTranslate(self.pos[0],self.pos[1],self.pos[2])
        glScale(self.size, self.size, self.size)
        
        glRotate(70.0,1.0,0.0,0.0)
        glRotate(35.0,0.0,0.0,-1.0)
        glColor4f(0.576, 0.918, 1.0, self.transperancy)
        glBegin(GL_LINE_STRIP)
        for v in self.shape:
            glVertex2f(v[0], v[1])
        glEnd()
        glVertex2f(self.shape[0][0], self.shape[0][1])
        glColor4f(0.376, 0.718, 1.0, self.transperancy)
        glBegin(GL_TRIANGLE_FAN)
        glVertex2f(0,0)
        for v in self.shape:
            glVertex2f(v[0], v[1])
        glEnd()

        
        glEnable(GL_DEPTH_TEST)
        glPopMatrix()        
        
    def grow(self):
        if self.size < self.maxSize:
            self.size += 0.005
            self.app.canvas.rlPublisher.objectPropertyChanged(str(self.id), "pond_grow", str(self.size))
        else:
            self.canGrow = False
            self.canShrink = True

    def shrink(self):
        if self.size > self.minSize:
            self.size -= 0.005
            self.app.canvas.rlPublisher.objectPropertyChanged(str(self.id), "pond_shrink", str(self.size))
        else: 
            self.canGrow = True
            self.canShrink = False
        
               
class Basket(EchoesObject):
    '''
    classdocs
    '''
    def __init__(self, app, autoAdd=True, props={"type": "Basket"}, fadeIn = False, fadingFrames = 100, callback = None):
        '''
        Constructor
        '''
        super(Basket, self).__init__(app, autoAdd, props, fadeIn, fadingFrames, callback)
       
        self.size = 0.6
        self.pos = (0,0,0)   
        self.publishRegion = True
        
        self.canBeDraged = True        
        self.defaultHeight = self.app.canvas.getRegionCoords("ground")[0][1]
        self.fallToDefaultHeight = True
        self.falling = False
        self.avatarTCB = None
        
        self.stack = None
        self.flowers = []
        self.numflowers = 0

        self.player = None

        self.textures = []
        self.sizes = []
        self.shapes = []
        self.texshape = [(0, 0), (1, 0), (1, 1), (0, 1)]        
        self.setImage('visual/images/basket-top.png')
        self.setImage('visual/images/basket-bottom.png')
        oy = (self.sizes[0][1] + self.sizes[1][1]) * 0.96
        w = self.sizes[0][0] / oy
        h = 1.0-2.0*self.sizes[0][1]/oy
        self.shapes.append([(-w, h), (w, h), (w, 1), (-w, 1)])
        h = -1.0+2.0*self.sizes[1][1]/oy
        self.shapes.append([(-w, -1), (w, -1), (w, h), (-w, h)])

    def __setattr__(self, item, value):
        if item == "pos" and hasattr(self, "flowers"):
            for f in self.flowers:
                f.pos = [value[0], value[1]+f.stemLength-self.size/2, value[2]]
            if hasattr(self, "stack") and self.stack and ((hasattr(self, "beingDragged") and self.beingDragged) or (hasattr(self, "avatarTCB") and self.avatarTCB)):
                # If the user did it, notify the rest of the system
                split = self.stack.split(self)
                if split and hasattr(self, "beingDragged") and self.beingDragged:
                    self.app.canvas.agentPublisher.agentActionCompleted('User', 'unstack_basket', [str(self.id)])

        elif item == "stack":
            if value == None:
                self.app.canvas.rlPublisher.objectPropertyChanged(str(self.id), "basket_stack", "False")
            else:
                self.app.canvas.rlPublisher.objectPropertyChanged(str(self.id), "basket_stack", "True")

    
        object.__setattr__(self, item, value)

    def setImage(self, file):
        im = PIL.Image.open(file) # .jpg, .bmp, etc. also work
        try:
            ix, iy, image = im.size[0], im.size[1], im.tostring("raw", "RGBA", 0, -1)
        except SystemError:
            ix, iy, image = im.size[0], im.size[1], im.tostring("raw", "RGBX", 0, -1)        

        tex = glGenTextures(1)
        glPixelStorei(GL_UNPACK_ALIGNMENT,1)
        glBindTexture(GL_TEXTURE_2D, tex)
        glTexImage2D(GL_TEXTURE_2D, 0, 4, ix, iy, 0, GL_RGBA, GL_UNSIGNED_BYTE, image)
        self.textures.append(tex)
        self.sizes.append([ix,iy])        
                       
    def renderObj(self):
        if not hasattr(self, "shapes"): return
        
        if self.numflowers != len(self.flowers):
            self.numflowers = len(self.flowers)
            self.app.canvas.rlPublisher.objectPropertyChanged(str(self.id), "basket_numflowers", str(self.numflowers))
            
        if self.fallToDefaultHeight and not self.beingDragged and not self.avatarTCB:
            hdiff = self.pos[1] - self.defaultHeight
            if abs(hdiff) > 0.05:
                if not self.stack: # no stack
                    self.pos = [self.pos[0], self.pos[1]-hdiff/10, self.pos[2]]
                    self.falling = True
                else:
                    self.falling = False
            else:
                self.falling = False
                        
        glPushMatrix()
        glTranslate(self.pos[0], self.pos[1], self.pos[2])
        glScalef(self.size, self.size, self.size)
        i = 0
        for texture in self.textures: 
            glEnable( GL_ALPHA_TEST )
            glAlphaFunc( GL_GREATER, 0.1 )        
            glEnable( GL_TEXTURE_2D )
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
            glBindTexture(GL_TEXTURE_2D, texture)
            glColor4f(1, 1, 1, self.transperancy)
            glBegin(GL_QUADS)
            ti = 0
            for v in self.shapes[i]:
                glTexCoord2d(self.texshape[ti][0], self.texshape[ti][1])
                glVertex3f(v[0], v[1], -0.1 + i*0.2)
                ti += 1
            glEnd()
            glDisable( GL_TEXTURE_2D )
            glDisable( GL_ALPHA_TEST )
            i += 1
        glPopMatrix()
               
    def addFlower(self, flower):
        self.flowers.append(flower)
        flower.basket = self
        flower.pos = [self.pos[0], self.pos[1]+flower.stemLength-self.size/2, self.pos[2]]
        flower.inCollision = self.id
        self.app.canvas.rlPublisher.objectPropertyChanged(str(self.id), "basket_flower", str(flower.id))
        if flower.beingDragged: 
            self.app.canvas.agentPublisher.agentActionCompleted('User', 'flower_placeInBasket', [str(self.id), str(flower.id)])

    def growFlowers(self):
        if len(self.flowers) == 0:
            flower = objects.Plants.EchoesFlower(self.app)
            flower.size = 0.1
            self.addFlower(flower)
        for f in self.flowers:
            f.grow()
        
    def removeFlower(self, flower):
        try: 
            i = self.flowers.index(flower)
            del self.flowers[i]
        except ValueError:
            Logger.warning("Basket: trying to remove flower that is not in the basket, id=" + str(flower.id)) 
        if len(self.flowers) == 0:
            self.app.canvas.rlPublisher.objectPropertyChanged(str(self.id), "basket_flower", "None")

    def playFanfare(self):
        if sound.EchoesAudio.soundPresent:
            if not self.player: 
                fanfar = "fanfar" + str(random.randint(1,3)) + ".wav"
                self.player = sound.EchoesAudio.playSound(fanfar, vol=0.3)
                sound.EchoesAudio.SoundCallback(fanfar, self.resetPlayer).start()
            else:
                Logger.warning("Basket already plays the fanfare, not triggering new one.")

    def resetPlayer(self):
        self.player = None
                       
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
            y = max(jpos[1]+self.size/3, self.defaultHeight)
        else:
            y = jpos[1]
        self.pos = [jpos[0], y, self.pos[2]]
            
    def detachFromJoint(self):
        self.avatarTCB = None
        self.objectCollisionTest = True        
        
    def remove(self, fadeOut = False, fadingFrames = 100):
        if not fadeOut and self.stack and self in self.stack.pots:
            self.objectCollisionTest = False
            del self.stack.pots[self.stack.pots.index(self)]
            self.stack = None
        super(Basket, self).remove(fadeOut, fadingFrames)
        

class Container(EchoesObject):
    '''
    classdocs
    '''
    def __init__(self, app, autoAdd=True, props={"type": "Container"}, fadeIn = False, fadingFrames = 100, callback = None):
        '''
        Constructor
        '''
        super(Container, self).__init__(app, autoAdd, props, fadeIn, fadingFrames, callback)
       
        self.size = 0.6
        self.pos = [0,self.app.canvas.getRegionCoords("ground")[1][1],0]   
        
        self.colours = { "red": (0.735, 0.197, 0.286), "yellow": (0.921,0.832,0.217), "blue":(0.220,0.481,0.628), "green":(0.439,0.633,0.245)}
        self.colour = "red"
        self.shape = [[-1,1,0], [-1,-1,0],[1,-1,0],[1,1,0]]

        self.publishRegion = True
        self.canBeDraged = False        
        
        self.balls = []
        
    def __setattr__(self, item, value):
        if item == "colour":
            if not value in self.colours: value = "red" 
            self.app.canvas.rlPublisher.objectPropertyChanged(str(self.id), "container_colour", str(value))      
    
        object.__setattr__(self, item, value)
                       
    def renderObj(self):
        glPushMatrix()
        glDisable(GL_DEPTH_TEST)
        glTranslate(self.pos[0], self.pos[1], self.pos[2])
        glScalef(self.size, self.size, self.size)
        c = self.colours[self.colour]
        glColor4f(c[0], c[1], c[2], self.transperancy*0.4)
        glBegin(GL_QUADS)
        for v in self.shape:
            glVertex3f(v[0],v[1],v[2])
        glEnd()
        glColor4f(c[0], c[1], c[2], self.transperancy)
        glBegin(GL_QUADS)
        glVertex3f(self.shape[0][0]*1.1, self.shape[0][1], 0)
        glVertex3f(self.shape[0][0]*0.9, self.shape[0][1], 0)
        glVertex3f(self.shape[1][0]*0.9, self.shape[1][1], 0)
        glVertex3f(self.shape[1][0]*1.1, self.shape[1][1], 0)
        glEnd()
        glBegin(GL_QUADS)
        glVertex3f(self.shape[1][0]*1.1, self.shape[1][1]*0.9, 0)
        glVertex3f(self.shape[1][0]*1.1, self.shape[1][1]*1.1, 0)
        glVertex3f(self.shape[2][0]*1.1, self.shape[2][1]*1.1, 0)
        glVertex3f(self.shape[2][0]*1.1, self.shape[2][1]*0.9, 0)
        glEnd()
        glBegin(GL_QUADS)
        glVertex3f(self.shape[3][0]*1.1, self.shape[3][1], 0)
        glVertex3f(self.shape[3][0]*0.9, self.shape[3][1], 0)
        glVertex3f(self.shape[2][0]*0.9, self.shape[2][1], 0)
        glVertex3f(self.shape[2][0]*1.1, self.shape[2][1], 0)
        glEnd()
        glEnable(GL_DEPTH_TEST)        
        glPopMatrix()
                              
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
                
    def addBall(self, ball):
        self.balls.append(ball)
        ball.container = self
        ball.floor = self.pos[1]-self.size
        ball.left = self.pos[0]-self.size
        ball.right = self.pos[0]+self.size
        ball.bounceWithinScene = True
        ball.size = max(self.size/2.5, ball.size/3)
        ball.stopDrag()
        ball.pos = [ball.pos[0], ball.pos[1], -10]
        ball.interactive = False
        ball.publishBounce = False
        
                       
    def reward(self, type="Bubbles"):
        num_balls = 0
        if type == "Bubbles":
            for b in self.balls:
                bubble = Bubbles.EchoesBubble(self.app, True, fadeIn=True, fadingFrames=10)
                bubble.setStartPos(b.pos)
                bubble.willBeReplaced = False 
                bubble.canMerge = False           
                b.remove(False)
                num_balls += 1
            self.remove(True)               
        elif type == "Fireworks":
            for b in self.balls:
                b.explode(inFrames=random.randint(0,100))
                num_balls += 1
            self.remove(True)
        elif type == "Bees":
            for b in self.balls:
                bee = Bee(self.app, True, fadeIn=True, fadingFrames=10)
                bee.setStartPos(b.pos)
                bee.setTargetPos([random.randint(-5,5), 5])
                bee.removeAtTargetPos = True
                b.remove(False)
                num_balls += 1
            self.remove(True)
        self.app.canvas.rlPublisher.objectPropertyChanged(str(self.id), "container_reward", str(num_balls))
        
    def remove(self, fadeOut = False, fadingFrames = 100):
        super(Container, self).remove(fadeOut, fadingFrames)
               
               
        
        
class Sun(EchoesObject):
    '''
    classdocs
    '''
    def __init__(self, app, autoAdd=True, props={"type": "Sun"}, fadeIn = False, fadingFrames = 100, callback=None):
        '''
        Constructor
        '''
        super(Sun, self).__init__(app, autoAdd, props, fadeIn, fadingFrames, callback)
        self.pos = (3, 2.25, -0.5)
        self.size = 0.5
        self.shape = [(math.cos(math.radians(deg)), math.sin(math.radians(deg))) for deg in xrange(0, 370, 10)]
        
        self.objectCollisionTest = False
        self.agentCollisionTest = False
        
               
    def renderObj(self):
        
        glPushMatrix()
        glDisable(GL_DEPTH_TEST)

        glTranslate(self.pos[0],self.pos[1],self.pos[2])
        glColor4f(1,1,0, self.transperancy)
        glBegin(GL_TRIANGLE_FAN)
        glVertex2f(0,0)
        glColor4f(0.741, 0.878, 0.929, 0)
        for v in self.shape:
            glVertex2f(v[0], v[1])
        glEnd()

        glEnable(GL_DEPTH_TEST)
        glPopMatrix()        
        
class Shed(EchoesObject):
    '''
    classdocs
    '''
    def __init__(self, app, autoAdd=True, props={"type": "Shed"}, fadeIn = False, fadingFrames = 100, callback=None):
        '''
        Constructor
        '''
        super(Shed, self).__init__(app, autoAdd, props, fadeIn, fadingFrames, callback)
        self.pos = (-2, -0.5, -3)
        self.size = 2.5
        
        self.objectCollisionTest = False
        self.agentCollisionTest = False
        
        self.texture = self.setImage('visual/images/Shed.png')
        self.shape = [(-0.5, -0.5), (0.5, -0.5), (0.5, 0.5), (-0.5, 0.5)]
        self.texshape = [(0, 0), (1, 0), (1, 1), (0, 1)]

    def __setattr__(self, item, value):
        if item == "pos":
            pass
                        
        object.__setattr__(self, item, value)
            
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
        
        
class Bee(EchoesObject, Motions.BezierMotion):
    '''
    classdocs
    '''

    def __init__(self, app, autoAdd=True, props = {"type": "Bee"}, fadeIn = False, fadingFrames = 100, randomSize = True, callback=None):
        '''
        Constructor
        '''
        super(Bee, self).__init__(app, autoAdd, props, fadeIn, fadingFrames, callback)
        super(Bee, self).initBezierVars()
        
        if randomSize:
            self.size = 0.15 + random.random() * 0.1
        else:
            self.size = 0.3 
        self.maxSize = 1.5
        self.speed = 0.002
        self.moving = True
        self.floatingXY = True
        self.floatingSound = False
        self.canBeClicked = True
        self.canBeDraged = True

        self.texture = self.setImage('visual/images/bee.png')        
            
        self.shape = [(-1, -1), (1, -1), (1, 1), (-1, 1)]
        self.texshape = [(1, 0), (1, 1), (0, 1), (0, 0)]
        
        self.newstartpos()
        self.newctrlpoints()
        
        if sound.EchoesAudio.soundPresent:
            self.buzz = sound.EchoesAudio.playSound("buzz.wav", loop=True, vol=0.0)
        else:
            self.buzz = None
                    
    def __setattr__(self, item, value):
                            
        object.__setattr__(self, item, value)
                             
    def renderObj(self):
        ''' 
        overwriting the render method to draw the bubble
        '''
        oldpos = self.pos
        if self.moving and not self.beingDragged:
            self.pos = self.nextBezierPos(self.floatingXY)
            self.orientation = (self.pos[0]-oldpos[0], self.pos[1]-oldpos[1], self.pos[2]-oldpos[2])              
            if self.removeAtTargetPos and self.bezierIndex > 0.95:
                self.remove(True)
        
        if self.buzz and not self.fadingOut:
            vel = math.hypot(self.orientation[0], self.orientation[1])
            self.buzz.mul = min(0.8, vel*200)
            self.buzz.speed =  1 + (vel*10)
                    
        glPushMatrix()
        glEnable( GL_ALPHA_TEST )
        glAlphaFunc( GL_GREATER, 0.1 )        
        glEnable( GL_TEXTURE_2D )
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glBindTexture(GL_TEXTURE_2D, self.texture)
        glTranslate(self.pos[0], self.pos[1], self.pos[2])
        glRotate(math.degrees(math.atan2(self.orientation[1], self.orientation[0])), 0,0,1)
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
                
    def newstartpos(self):
        x = self.app.canvas.orthoCoordWidth/2 - (random.random() * self.app.canvas.orthoCoordWidth)
        y = random.choice([-1,1])*self.app.canvas.orthoCoordWidth/2/self.app.canvas.aspectRatio + self.size * 1.1
        self.pos = (x,y,0)
        
    def click(self, agentName, replace=True):
        '''
        click
        '''
    
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
                            
            self.locationChanged = True
        
    def remove(self, fadeOut=False, fadingFrames=100):
        if self.buzz:
            if fadeOut: 
                self.buzz.mul = self.buzz.mul / 2
            else:
                self.buzz.stop()
        super(Bee, self).remove(fadeOut, fadingFrames)            
                                    
        