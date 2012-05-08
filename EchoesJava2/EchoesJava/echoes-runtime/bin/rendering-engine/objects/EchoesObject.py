'''
Created on 4 Sep 2009

@author: cfabric
'''
import echoes
import math, random
import PIL.Image
from OpenGL.GL import *
from OpenGL.GLU import *
from OpenGL.GLE import *
from OpenGL.GLUT import *

class EchoesObject(object):
    '''
    classdocs
    '''  

    def __init__(self, app, autoAdd=True, props={"type": "None"}, fadeIn = False, fadingFrames = 100, callback=None):
        '''
        Constructor
        '''
        self.app = app
        self.props = props
        if autoAdd:
            self.id = app.canvas.addObject(self, props)
        else:
            self.id = -1
        
        self.publishRegion = True
        self.publishCoords = False
        self.publishHSlot = True
        self.publishCounter = 0
        self.publishFreq = 20 # publish region information every X frames
        self.currentRegion = -1
        self.currentHSlot = -1
        self.publishNearness = True
        self.atAgent = False
        self.nearAgent = False
        self.attachedToAgent = False
        self.attachedToAgentId = -1
        self.publishOverAgent = True
        self.overAgent = None
        self.currentOverAgent = None
        self.draggedOverAgent = None
        self.currentDraggedOverAgent = None
                
        # basic geometry and dragging   
        self.beingDragged = False
        self.locationChanged = False
        self.removeAtTargetPos = False        
        self.removeAction = None
        self.removeActionArgs = None
        self.interactive = True
        self.magic = True
        
        self.objectCollisionTest = True
        self.agentCollisionTest = True

        
        self.pos = (0,0,0)
        self.size = 1
        self.circle = [(math.cos(math.radians(deg)), math.sin(math.radians(deg))) for deg in xrange(0, 360, 10)]

        self.showBoundary = False
        self.showId = False

        
        self.fadingOut = False
        self.fadingIn = fadeIn
        self.fadingFrames = fadingFrames
        if fadeIn:
            self.transperancy = 0.0
        else:
            self.transperancy = 1.0
            
        self.objectCollisionTest = True
        self.agentCollisionTest = True
        
        if callback:
            callback.ice_response(str(self.id))

        
    
    def render(self, hitTest=False):
        '''
        All objects are added to the scene as plugins (see EchoesGLCanvas) 
        and need to provide a render function if they want to draw anything 
        '''
        # do not do anything when rendered into normal mode to perform the hit test
        # this double check is necessary as render is sometimes called before the object is fully built            
        if not hitTest and hasattr(self, "publishCounter") and hasattr(self, "publishFreq"): 
            # perform region update every publishFreq frames for efficiency
            if math.modf(self.publishCounter/self.publishFreq)[0] == 0:
                self.publishCounter = 0
                screenpos = self.app.canvas.getScreenCoord(self.pos)
                screensize = self.app.frame.GetSize()
                if hasattr(self, "publishRegion") and self.publishRegion:
                    region = -1
                    if screenpos[0] < screensize[0]/3:
                        if screenpos[1] < screensize[1]/3:
                            region = 6
                        elif screenpos[1] < 2*screensize[1]/3:
                            region = 3
                        else:
                            region = 0
                    elif screenpos[0] < 2*screensize[0]/3:
                        if screenpos[1] < screensize[1]/3:
                            region = 7
                        elif screenpos[1] < 2*screensize[1]/3:
                            region = 4
                        else:
                            region = 1
                    else:
                        if screenpos[1] < screensize[1]/3:
                            region = 8
                        elif screenpos[1] < 2*screensize[1]/3:
                            region = 5
                        else:
                            region = 2
                        
                    if self.currentRegion != region:
                        self.currentRegion = region
                        self.app.canvas.rlPublisher.objectPropertyChanged(str(self.id), "ScreenRegion", str(echoes.ScreenRegion(self.currentRegion)))
        
                if hasattr(self, "publishCoords") and self.publishCoords:
                    self.app.canvas.rlPublisher.objectPropertyChanged(str(self.id), "ScreenCoordinates", str(screenpos))
                    self.app.canvas.rlPublisher.objectPropertyChanged(str(self.id), "WorldCoordinates", str(self.pos))
                    
                if hasattr(self, "publishHSlot") and self.publishHSlot:
                    hSlot = int(math.floor(self.pos[0]) + 5)
                    if hSlot != self.currentHSlot:
                        self.app.canvas.rlPublisher.objectPropertyChanged(str(self.id), "HorizontalSlot", str(hSlot))
                        self.currentHSlot = hSlot

                if hasattr(self, "publishNearness") and self.publishNearness and self.app.canvas.renderPiavca and self.app.canvas.piavcaAvatars.values():
                    for agentid in self.app.canvas.piavcaAvatars.keys():
                        at = self.app.canvas.piavcaAvatars[agentid].isAt(self.id)
#                        if at: self.showBoundary = True 
#                        else: self.showBoundary= False
                        if self.atAgent != at:
                            self.atAgent = at
                            self.app.canvas.rlPublisher.objectPropertyChanged(str(self.id), "atAgent", str(at))
                        near = self.app.canvas.piavcaAvatars[agentid].isNear(self.id)
                        if self.nearAgent != near:
                            self.nearAgent = near
                            self.app.canvas.rlPublisher.objectPropertyChanged(str(self.id), "nearAgent", str(near))
                        if hasattr(self, "avatarTCB"):
                            if self.attachedToAgent and not self.avatarTCB:
                                self.attachedToAgent = False
                                self.app.canvas.rlPublisher.objectPropertyChanged(str(self.id), "attachedToAgent", "False")
                            if not self.attachedToAgent and self.avatarTCB:
                                self.attachedToAgent = True
                                self.app.canvas.rlPublisher.objectPropertyChanged(str(self.id), "attachedToAgent", "True")
                                
                if hasattr(self, "publishOverAgent") and self.publishOverAgent:
                    if self.currentOverAgent != self.overAgent:
                        self.currentOverAgent = self.overAgent
                        self.app.canvas.rlPublisher.objectPropertyChanged(str(self.id), "overAgent", str(self.overAgent))
                    if self.currentDraggedOverAgent != self.draggedOverAgent:
                        self.currentDraggedOverAgent = self.draggedOverAgent
                        self.app.canvas.rlPublisher.objectPropertyChanged(str(self.id), "draggedOverAgent", str(self.draggedOverAgent))
    
            self.publishCounter += 1
    
            if hasattr(self, "fadingIn") and self.fadingIn:
                self.transperancy += 1.0/self.fadingFrames
                if self.transperancy >= 1.0:
                    self.transperancy = 1.0
                    self.fadingIn = False
    
            elif hasattr(self, "fadingOut") and self.fadingOut:
                self.transperancy -= 1.0/self.fadingFrames
                if self.transperancy <= 0.0:
                    self.remove(False)
        
        if self.showBoundary:
                    glPushMatrix()
                    glLineWidth(1.0)
                    glTranslate(self.pos[0], self.pos[1], self.pos[2])
                    glScalef(self.size, self.size, self.size)
                    glColor4f(1, 0, 0, self.transperancy)
                    glBegin(GL_LINE_STRIP)
                    for v in self.circle:
                        glVertex3f(v[0], v[1], self.pos[2])                        
                    glEnd()         
                    glPopMatrix()   

        if self.showId:
                    glPushMatrix()
                    glLineWidth(3.0)
                    glTranslate(self.pos[0]-self.size*0.15, self.pos[1]-self.size*0.15, self.pos[2]+0.5)
                    glScalef(self.size/200, self.size/200, self.size/200)
                    glColor4f(1, 0, 0, 0.8*self.transperancy)
                    for letter in str(self.id):
                        glutStrokeCharacter(GLUT_STROKE_ROMAN, ord(letter))
                    glPopMatrix()                    
                         
        self.renderObj()

    def renderObj(self):
        '''
        The actual rendering of the object as called by self.render 
        '''
        print "EchoesObject: overwrite this rendering method!"
        
    def remove(self, fadeOut = False, fadingFrames = 100):
        # Remove this object from the canvas
        if fadeOut:
            if not self.fadingOut:
                self.fadingOut = True
                self.fadingFrames = fadingFrames
                self.transperancy = 1.0
                if self.removeAction:
                    if self.removeAction == "PublishScenarioEnded":
                        self.app.canvas.clearScene()
        else:        
            self.app.canvas.removeObject(self.id)
            if self.removeAction:
                if self.removeAction == "PublishScenarioEnded":
                    self.app.canvas.clearScene(quick=True)
                    self.app.canvas.rlPublisher.scenarioEnded(self.removeActionArgs)
                    if self.callback:
                        self.callback.ice_response()
                        self.callback = None
        
    def startDrag(self, pos):
        print "EchoesObject.startDrag: overwrite this method!"
        pass
    
    def stopDrag(self):
        print "EchoesObject.stopDrag: overwrite this method!"
        pass
    
    def drag(self, newXY):
        print "EchoesObject.drag: overwrite this method!"
        pass

    def click(self, agentName):
        print "EchoesObject.click: overwrite this method!"
        pass
    
    def setImage(self, file=None):
        if not file: return
        im = PIL.Image.open(file) # .jpg, .bmp, etc. also work
        try:
            ix, iy, image = im.size[0], im.size[1], im.tostring("raw", "RGBA", 0, -1)
        except SystemError:
            ix, iy, image = im.size[0], im.size[1], im.tostring("raw", "RGBX", 0, -1)        

        tex = glGenTextures(1)
        glPixelStorei(GL_UNPACK_ALIGNMENT,1)
        glBindTexture(GL_TEXTURE_2D, tex)
        glTexImage2D(GL_TEXTURE_2D, 0, 4, ix, iy, 0, GL_RGBA, GL_UNSIGNED_BYTE, image)
        return tex        
   
    
