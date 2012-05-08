'''
Created on 26 Aug 2009

@author: cfabric
'''
import wx
from wx.lib.dialogs import *
from wx import glcanvas
import array

try:
    from OpenGL.GL import *
    from OpenGL import platform, constants, constant, arrays
    from OpenGL.GLUT import *
    from OpenGL.GLU import *
    from OpenGL.platform import *
    glPresent = True
except ImportError:
    glPresent = False

from Collisions import *
import echoes
import environment.Menu
import environment.HelperElements
import environment.Backgrounds
import objects.Environment
import objects.Plants
import objects.PlayObjects
import agents.PiavcaAvatars
import agents.EchoesAgent
import time, random
import Piavca
import thread
import Logger
import Annotator
import datetime, os
import PIL

from interface.TouchListenerImpl import *
import wx.lib.newevent
CreatePiavcaAvatar, EVT_CREATE_PIAVCA_AVATAR = wx.lib.newevent.NewEvent()
LoadScenario, EVT_LOAD_SCENARIO = wx.lib.newevent.NewEvent()
EndScenario, EVT_END_SCENARIO = wx.lib.newevent.NewEvent()
AddObject, EVT_ADD_OBJECT = wx.lib.newevent.NewEvent()
SetObjectProperty, EVT_SET_OBJECT_PROPERTY = wx.lib.newevent.NewEvent()
RemoveObject, EVT_REMOVE_OBJECT = wx.lib.newevent.NewEvent()
StartAnnotator, EVT_START_ANNOTATOR = wx.lib.newevent.NewEvent()
StopAnnotator, EVT_STOP_ANNOTATOR = wx.lib.newevent.NewEvent()
AnnotatorDrawing, EVT_ANNOTATOR_DRAWING = wx.lib.newevent.NewEvent()
AnnotatorNoDrawing, EVT_ANNOTATOR_NO_DRAWING = wx.lib.newevent.NewEvent()

if glPresent:
    
    class EchoesGLCanvas(glcanvas.GLCanvas):
        
        def __init__(self, parent):
            
            Logger.trace("info",  "init canvas")
            attribList = (glcanvas.WX_GL_RGBA, # RGBA
                      glcanvas.WX_GL_DOUBLEBUFFER, # Double Buffered
                      glcanvas.WX_GL_DEPTH_SIZE, 24) # 24 bit
            
            glcanvas.GLCanvas.__init__(self, parent, -1, attribList=attribList)
            self.init = False
            # initial mouse position
            self.size = None
            self.Bind(wx.EVT_ERASE_BACKGROUND, self.OnEraseBackground)
            self.Bind(wx.EVT_SIZE, self.OnSize)
            self.Bind(wx.EVT_PAINT, self.OnPaint)
            self.Bind(wx.EVT_LEFT_DOWN, self.OnMouseDown)
            self.Bind(wx.EVT_LEFT_UP, self.OnMouseUp)
            self.Bind(wx.EVT_LEFT_DCLICK, self.OnMouseDoubleClick)
            self.Bind(wx.EVT_RIGHT_DOWN, self.OnMouseDown)
            self.Bind(wx.EVT_RIGHT_UP, self.OnMouseUp)
            self.Bind(wx.EVT_MIDDLE_DOWN, self.OnMouseDown)
            self.Bind(wx.EVT_MIDDLE_UP, self.OnMouseUp)
            self.Bind(wx.EVT_MOTION, self.OnMouseMotion)
            self.Bind(wx.EVT_CHAR, self.OnKeyboard)
            self.Bind(wx.EVT_IDLE, self.OnIdle)
            
            # Listen for the events from the touch-server too
            self.Bind(EVT_ECHOES_CLICK_EVENT, self.OnEchoesClick)
            self.Bind(EVT_ECHOES_POINT_DOWN_EVENT, self.OnEchoesPointDown)
            self.Bind(EVT_ECHOES_POINT_MOVED_EVENT, self.OnEchoesPointMoved)
            self.Bind(EVT_ECHOES_POINT_UP_EVENT, self.OnEchoesPointUp)
            
            self.Bind(EVT_CREATE_PIAVCA_AVATAR, self.OnCreatePiavcaAvatar)
            self.Bind(EVT_LOAD_SCENARIO, self.OnLoadScenario)
            self.Bind(EVT_END_SCENARIO, self.OnEndScenario)
            self.Bind(EVT_ADD_OBJECT, self.OnAddObject)
            self.Bind(EVT_SET_OBJECT_PROPERTY, self.OnSetObjectProperty)
            self.Bind(EVT_REMOVE_OBJECT, self.OnRemoveObject)
            self.Bind(EVT_START_ANNOTATOR, self.OnStartAnnotator)
            self.Bind(EVT_STOP_ANNOTATOR, self.OnStopAnnotator)
            self.Bind(EVT_ANNOTATOR_DRAWING, self.OnAnnotatorDrawing)
            self.Bind(EVT_ANNOTATOR_NO_DRAWING, self.OnAnnotatorNoDrawing)
            
            self.scaleBias = 1.0
            self.tracking = 1
            
            self.cameraPos = (100.0, 100.0, 100.0)
            self.orthoCoordWidth = 10
            self.orthoCoordDepth = 100
            self.aspectRatio = 1.0
            self.aspectFourByThree = True
            
            self.dragging = False
                                    
            self.clear_colour = (0,0,0,0)
            
            self.currentScene = None
            self.sceneElementCount = 0
            self.sceneElements = dict()
            self.objectCount = 0
            self.objects = dict()
            self.agentCount = 0
            self.agents = dict()
            self.drag = dict()      #id's of drag events on objects 
            self.bgtouch = dict()   #id's of drag events on background
            
            self.userList = []
            
            self.agentActions = dict()
            self.actionLock = thread.allocate_lock()
            self.piavcaAvatars = dict()
            
            self.touchEnabled = False
            self.Annotator = None
            
            self.scenario = ""
            self.publishScore = True
            self.targetLightLevel = self.lightLevel = 0.8

            self.frame = parent
            self.app = parent.app
            
            self.last_time = time.time()
            self.frameCounter = 0
            self.printFPS = False
            
            self.renderPiavca = False
            self.InitGL()
            
        def setCurrent(self):
            if self.GetContext():
                self.SetCurrent()                
            
        def setClearColour(self, r, g, b, a):
            self.clear_colour = (r,g,b,a)

        def OnEraseBackground(self, event):
            pass # Do nothing, to avoid flashing.

        def projection(self):
#            gluPerspective( 45.0, self.aspectRatio, 0.5, 50.0 ); 
            glOrtho(-1*self.orthoCoordWidth/2, self.orthoCoordWidth/2, -1*self.orthoCoordWidth/2/self.aspectRatio, self.orthoCoordWidth/2/self.aspectRatio, -1*self.orthoCoordDepth/2, self.orthoCoordDepth/2)

        def OnSize(self, event):
            size = self.size = self.GetClientSize()
            if size.width >=0 :
                width = size.width
            else:
                width = 0
            if size.height >=0 :
                height = size.height
            else:
                height = 0
            if self.GetContext():
                self.SetCurrent()
                glViewport(0, 0, width, height)
                if width > 0 and height > 0:
                    self.aspectRatio = float(width)/float(height)
                    if self.aspectFourByThree:
                        self.aspectRatio = self.aspectRatio * 4/3
                    Logger.trace("info",  "setting perspective and viewport with size " + str(width) + " x " + str(height) + "aspect ratio " + str(self.aspectRatio))
                    glMatrixMode(GL_PROJECTION)
                    glLoadIdentity()
                    self.projection()
            event.Skip()

        def getRegionCoords(self, key):
            w = float(self.orthoCoordWidth)
            h = float(self.orthoCoordWidth / self.aspectRatio)
            d = float(self.orthoCoordDepth)
            return {
                    "all": [(-1*w/2,-1*h/2,-1*d/2), (w/2,h/2, d/2)], 
                    "all80": [(-0.8*w/2,-0.8*h/2,-0.8*d/2), (0.8*w/2,0.8*h/2, 0.8*d/2)], 
                    "all70": [(-0.7*w/2,-0.7*h/2,-0.7*d/2), (0.7*w/2,0.7*h/2, 0.7*d/2)], 
                    "all60": [(-0.6*w/2,-0.6*h/2,-0.6*d/2), (0.6*w/2,0.6*h/2, 0.6*d/2)], 
                    "all50": [(-0.5*w/2,-0.5*h/2,-0.5*d/2), (0.5*w/2,0.5*h/2, 0.5*d/2)], 
                    "left": [(-1*w/2,-1*h/2,-1*d/2), (-1*w/6, h/2, d/2)],
                    "middle": [(-1*w/6, -1*h/2, -1*d/2), (w/6, h/2, d/2)],
                    "right": [(w/6, -1*h/2, -1*d/2), (w/2, h/2, d/2)],
                    "v-top": [(-1*w/2,0.3*h/2,-1*d/2), (w/2,h/2, d/2)], 
                    "v-middle": [(-1*w/2,-0.3*h/2,-1*d/2), (w/2,0.3*h/2, d/2)], 
                    "v-bottom": [(-1*w/2,-1*h/2,-1*d/2), (w/2,-0.3*h/2, d/2)], 
                    "3x3": [[(-1*w/2, h/6, -1*d/2), (-1*w/6, h/2, d/2)],        # top-left
                            [(-1*w/6, h/6, -1*d/2), (w/6, h/2, d/2)],           # top-middle
                            [(w/6, h/6, -1*d/2), (w/2, h/2, d/2)],              # top-right
                            [(-1*w/2, -1*h/6, -1*d/2), (-1*w/6, h/6, d/2)],     # middle-left
                            [(-1*w/6, -1*h/6, -1*d/2), (w/6, h/6, d/2)],        # middle-middle
                            [(w/6, -1*h/6, -1*d/2), (w/2, h/6, d/2)],           # middle-right
                            [(-1*w/2, -1*h/2, -1*d/2), (-1*w/6, -1*h/6, d/2)],  # bottom-left
                            [(-1*w/6, -1*h/2, -1*d/2), (w/6, -1*h/6, d/2)],     # bottom-middle
                            [(w/6, -1*h/2, -1*d/2), (w/2, -1*h/6, d/2)]         # bottom-right
                            ],
                    "ground": [(-1*w/2,-0.7*h/2,-1*d/2), (w/2,-0.8*h/2, d/2)],
                    "middle-ground": [(-1*w/2,-0.2*h/2,-1*d/2), (w/2,-0.7*h/2, d/2)],
                    "sky": [(-1*w/2,0.95*h/2,-1*d/2), (w/2,0.8*h/2, d/2)]
                    
            }[key]
            
        def get3x3Neighbours(self, region, distance):
            if distance == 0:
                return [region]
            else:
                return [
                        [[1,4,3], [2,5,8,7,6]],
                        [[0,2,3,4,5], [6,7,8]],
                        [[1,4,5], [0,3,6,7,8]],
                        [[0,1,4,7,6], [2,5,8]],
                        [[0,1,2,3,5,6,7,8], []],
                        [[1,2,4,7,8], [0,3,6]],
                        [[3,4,7], [0,1,2,5,8]],
                        [[6,3,4,5,8], [0,1,2]],
                        [[7,4,5], [0,1,2,3,6]]
                        ][region][distance-1]

        def resize(self, size):
            pass

        def OnPaint(self, event):
            dc = wx.PaintDC(self)
            self.SetCurrent()
            if not self.init:
                self.InitGL()
                self.init = True
            self.OnDraw()

        def OnMouseDown(self, evt):
            # self.CaptureMouse()
            x, y = evt.GetPosition()

            if (not(self.touchEnabled)):
                if self.Annotator:
                    self.Annotator.startDrag((x,y))
                else:
                    id = self.getObjectAtPosition(x, y)
                    evt.id = 0
                    if (id > -1):
                        # print "Touching object " + str(id), " in region ", self.objects[id].currentRegion
                        self.rlPublisher.userTouchedObject(str(id))
                        self.drag[evt.id] = id
                        self.objects[id].startDrag((x, y))

        def OnMouseUp(self, evt):
            self.dragging = False
            
            if (not(self.touchEnabled)):
                if self.Annotator:
                    self.Annotator.stopDrag()
                else:
                    evt.id = 0
                    if(evt.id in self.drag and self.drag[evt.id] in self.objects):
                        if(self.objects[self.drag[evt.id]].locationChanged):
                            self.agentPublisher.agentActionCompleted("User", "drag", [str(self.drag[evt.id])])
                        self.objects[self.drag[evt.id]].stopDrag()
                    if(evt.id in self.drag):
                        del self.drag[evt.id]

        def OnMouseMotion(self, evt):
            if evt.Dragging():
                x, y = evt.GetPosition()
                self.Refresh(False)
                
                if (not(self.touchEnabled)):
                    if self.Annotator:
                        self.Annotator.drag((x,y))
                    else:
                        evt.id = 0
                        if(evt.id in self.drag and self.drag[evt.id] in self.objects):
                            self.objects[self.drag[evt.id]].drag([x, y])                        

        def OnMouseDoubleClick(self, evt):
            x, y = evt.GetPosition()
            self.processClick(x, y)            
                    
        def processClick(self, x, y):
            if self.Annotator:
                self.Annotator.click((x,y))
            else:
                id = self.getObjectAtPosition(x, y)
                if (id > -1):
                    Logger.trace("info",  "clicked object is " + str(id))
                    self.objects[id].click("User")
                    self.rlPublisher.userTouchedObject(str(id))
                else:
                    id = self.getAgentAtPosition(x,y)
                    if (id > -1):
                        Logger.trace("info",  "clicked agent is " + str(id))
                        self.agents[id].click(self.getWorldCoord((x,y,0)))
                        self.rlPublisher.userTouchedAgent(str(id))
                menus = self.getMenus()
                for menu in menus:
                    menu.click(self.getWorldCoord((x,y,0)))
                        
        def OnEchoesClick(self, evt):
            Logger.trace("info",  "Click from ECHOES: x = " + str(evt.x) + "; y = " + str(evt.y))
            framePos = self.ScreenToClient([evt.x, evt.y])
            Logger.trace("info",  "Location on screen: " + str(framePos))
            self.processClick(framePos[0], framePos[1])

        def OnEchoesPointDown(self, evt):
            Logger.trace("info",  "Point down from ECHOES: id = " + str(evt.id) + "; x = " + str(evt.x) + "; y = " + str(evt.y))
            framePos = self.ScreenToClient([evt.x, evt.y])
            Logger.trace("info",  "Location on screen: " + str(framePos))
            if self.Annotator:
                self.Annotator.startDrag(framePos)
            else:
                id = self.getObjectAtPosition(framePos[0], framePos[1])
                if (id > -1):
                    Logger.trace("info",  "Touching object " + str(id) + " with gesture #" + str(evt.id))
                    self.rlPublisher.userTouchedObject(str(id))
                    self.drag[evt.id] = id
                    self.objects[id].startDrag(framePos)
                else:
                    self.bgtouch[evt.id] = framePos
                    self.agentPublisher.agentActionStarted("User", "touch_background", [str(framePos[0]), str(framePos[1])])

        def OnEchoesPointMoved(self, evt):
            framePos = self.ScreenToClient([evt.x, evt.y])
            if self.Annotator:
                self.Annotator.drag(framePos)
            else:
                if(evt.id in self.drag and self.drag[evt.id] in self.objects):
                    self.objects[self.drag[evt.id]].drag(framePos)

        def OnEchoesPointUp(self, evt):
            Logger.trace("info",  "Point up from ECHOES: id = " + str(evt.id))
            if self.Annotator:
                self.Annotator.stopDrag()
            else:            
                if(evt.id in self.drag and self.drag[evt.id] in self.objects):
                    if(self.objects[self.drag[evt.id]].locationChanged):
                        self.agentPublisher.agentActionCompleted("User", "drag", [str(self.drag[evt.id])])
                    self.objects[self.drag[evt.id]].stopDrag()
                if(evt.id in self.drag):
                    del self.drag[evt.id]
                if(evt.id in self.bgtouch):
                    self.agentPublisher.agentActionCompleted("User", "touch_background", [str(self.bgtouch[evt.id][0]), str(self.bgtouch[evt.id][1])])
                    del self.bgtouch[evt.id]
                
        def OnKeyboard(self, evt):
            #print evt.KeyCode
            try:
                if type(evt.KeyCode) == int:
                    char = chr(evt.KeyCode)
                else:
                    char = chr(evt.KeyCode())
            except ValueError:
                # print "KeyInput.KeyPressedCB not ASCII"
                return

            # print "KeyInput.KeyPressedCB", char
            
            if char == 'f':
                self.app.fullscreen(not self.app.fullscreenFlag)
            
            if char == 'a':
                removed = False
                for id, object in self.sceneElements.items():
                    if isinstance(object, environment.HelperElements.Axis):
                        object.remove()
                        removed = True
                if not removed:
                    environment.HelperElements.Axis(self.app)
                                    
            if char == 'g':
                removed = False
                for id, object in self.sceneElements.items():
                    if isinstance(object, environment.HelperElements.Grid3x3):
                        object.remove()
                        removed = True
                if not removed:
                    environment.HelperElements.Grid3x3(self.app)              

        def OnCreatePiavcaAvatar(self, evt):
            if (evt.type in self.piavcaAvatars):
                # Re-add it
                self.piavcaAvatars[evt.type].id = self.addAgent(self.piavcaAvatars[evt.type], dict())
                self.piavcaAvatars[evt.type].startPostion()
                if hasattr(evt, "pose"):
                    print "Looking at point ..."
                    self.piavcaAvatars[evt.type].lookAtPoint(0, 3, 0)
                self.rlPublisher.agentAdded(str(self.agentCount), dict())
                evt.callback.ice_response(str(self.piavcaAvatars[evt.type].id))
            else:
                # Create and auto-add the avatar
                if evt.type == "Paul":
                    self.piavcaAvatars[evt.type] = agents.PiavcaAvatars.Paul(self.app, evt.autoadd, callback=evt.callback)
                elif evt.type == "Andy":
                    self.piavcaAvatars[evt.type] = agents.PiavcaAvatars.Andy(self.app, evt.autoadd, callback=evt.callback)
                else:
                    Logger.warning("Unknown avatar type: " + evt.name)
                    evt.callback.ice_response("")
                
        def OnLoadScenario(self, evt):
            self.scenario = evt.name
            if evt.name == "Intro":
                self.renderPiavca = False
                environment.Backgrounds.Sky(self.app)
                bubble = objects.Bubbles.EchoesBubble(self.app, True, fadeIn=True)
                bubble.colour = "green"
                m = self.getRegionCoords("middle")
                bubble.setStartPos((m[0][0], 0,0))
                bubble.interactive = False
                bubble.moving = False
                bubble.size = 0.6                
                userList = self.userList
                if Ice.Application.communicator() and (not(userList) or (len(userList) == 0)):
                    userList = Ice.Application.communicator().getProperties().getPropertyAsListWithDefault('RenderingEngine.UserList', ['Tim', 'Tom', 'Jake', 'Sam'])
                environment.Menu.UserMenu(self.app, True, True,
                                          userlist=userList, 
                                          pos=(0,0,0))   
            
            elif evt.name == "BubbleWorld":
                self.renderPiavca = False
                self.score = 0
                environment.Backgrounds.Sky(self.app, True, True)
    
            elif "Garden" in evt.name:
                bg = environment.Backgrounds.Garden(self.app, fadeIn=True)
                self.renderPiavca = True
                self.score = 0
                # default is visual/images/GardenBackExplore.png
                if evt.name == "GardenTask":
                    bg.setImage("visual/images/GardenBackTask.png")
                elif evt.name == "GardenSocialGame":
                    bg.setImage("visual/images/GardenBackSocialGame.png")
                elif evt.name == "GardenVeg":
                    bg.setImage("visual/images/VegBackground.png")
                    
            self.currentScene = evt.name
            self.rlPublisher.scenarioStarted (evt.name)
            evt.callback.ice_response()
            
        def OnEndScenario(self, evt):
            self.scenario = None
            if evt.name == "Intro" or evt.name == "BubbleWorld" or "Garden" in evt.name:

                # Introduce a new transition bubble except in the Intro scene
                trans_bubble = None
                if evt.name == "Intro":
                    for id,object in self.objects.items():
                        if isinstance(object, objects.Bubbles.EchoesBubble):
                            trans_bubble=object
                            break
                    for id, object in self.sceneElements.items():
                        if isinstance(object, environment.Menu.UserMenu):
                            object.remove(False)                    
                                            
                if not trans_bubble:
                    trans_bubble = objects.Bubbles.EchoesBubble(self.app, True, fadeIn=True, fadingFrames=100)
                    trans_bubble.setStartPos((0,0,0))
                
                trans_bubble.interactive = False
                trans_bubble.colour = "red"
                trans_bubble.moving = True
                trans_bubble.setTargetPos((self.orthoCoordWidth, self.orthoCoordWidth / self.aspectRatio, self.orthoCoordDepth))
                trans_bubble.removeAtTargetPos = True
                trans_bubble.removeAction = "PublishScenarioEnded"
                trans_bubble.callback = evt.callback
                trans_bubble.removeActionArgs = evt.name                                

            else:
                Logger.warning("Unknown scenario in endScenario: " + evt.name)
                evt.callback.ice_response()
                
            self.renderPiavca = False
            self.currentScene = None
            
        def OnAddObject(self, evt):
            if evt.type == "Flower":
                objects.Plants.EchoesFlower(self.app, True, fadeIn = False, callback=evt.callback)
            elif evt.type == "Bubble":
                objects.Bubbles.EchoesBubble(self.app, True, fadeIn = True, callback=evt.callback)
            elif evt.type == "Ball":
                objects.PlayObjects.Ball(self.app, True, fadeIn = True, callback=evt.callback)
            elif evt.type == "IntroBubble":
                b = objects.Bubbles.EchoesBubble(self.app, True, fadeIn = True, callback=evt.callback)
                b.colour = "green"
                if self.currentScene == "BubbleWorld":
                    b.willBeReplaced = True
                else:
                    b.willBeReplaced = False
                b.setStartPos((0,5,0.5))
            elif evt.type == "Pot":
                objects.Plants.Pot(self.app, True, fadeIn = True, callback=evt.callback)
            elif evt.type == "Ball":
                objects.PlayObjects.Ball(self.app, True, fadeIn = True, callback=evt.callback)
            elif evt.type == "Pond":
                objects.Environment.Pond(self.app, True, fadeIn = True, callback=evt.callback)
            elif evt.type == "Cloud":
                objects.Environment.Cloud(self.app, True, fadeIn = True, callback=evt.callback)
            elif evt.type == "Container":
                objects.Environment.Container(self.app, True, fadeIn = True, callback=evt.callback)
            elif evt.type == "Sun":
                objects.Environment.Sun(self.app, True, fadeIn = True, callback=evt.callback)
            elif evt.type == "LifeTree":
                objects.Plants.LifeTree(self.app, True, fadeIn = True, callback=evt.callback)
            elif evt.type == "MagicLeaves":
                objects.Plants.MagicLeaves(self.app, True, fadeIn = True, callback=evt.callback)
            elif evt.type == "Basket":
                objects.Environment.Basket(self.app, True, fadeIn = True, callback=evt.callback)
            elif evt.type == "Shed":
                objects.Environment.Shed(self.app, True, fadeIn = True, callback=evt.callback)
            else:
                Logger.warning("Cannot create object of type " + evt.type)
                evt.callback.ice_response("")
        
        def OnSetObjectProperty(self, evt):
            if int(evt.objId) in self.app.canvas.objects:
                o = self.app.canvas.objects[int(evt.objId)]
                # Generic properties
                if evt.propName == "Pos":
                    pos = str(evt.propValue)
                    if pos.startswith("("): pos = pos[1:]
                    if pos.endswith(")"): pos = pos[:-1]
                    pos = pos.split(",")
                    try:
                        x = float(pos[0])
                    except ValueError:
                        Logger.warning("setObjectProperty: Invalid coordinate for x")
                        return
                    try:
                        y = float(pos[1])
                    except ValueError:
                        try:
                            f = self.getRegionCoords(pos[1].strip())
                            y = f[1][1]
                        except ValueError:
                            Logger.warning("setObjectProperty: Invalid coordinate for y")
                            return
                    try:
                        z = float(pos[2])
                    except ValueError:
                        if pos[2] == "front": z = 1
                        elif pos[2] == "back": z = -1
                        else: z = 0

                    o.pos = [x,y,z]
                
                if evt.propName == "Size" and hasattr(o, "size"):
                    o.size = float(evt.propValue)                
                
                if evt.propName == "Colour" and hasattr(o, "colour"):
                    o.colour = str(evt.propValue)
                    
                if evt.propName == "Interactive":
                    o.interactive = (evt.propValue == "True")
                            
                #Properties for Bubbles exposed to API
                if isinstance(o, objects.Bubbles.EchoesBubble):
                    if evt.propName == "Size": 
                        if evt.propValue == "Bigger":
                            o.grow()
                    elif evt.propName == "Replace": 
                        o.willBeReplaced = (evt.propValue == "True")
                #Properties for Flowers exposed to API    
                elif isinstance(o, objects.Plants.EchoesFlower):
                    if evt.propName == "MoveToBasket":
                        try: 
                            id = int(evt.propValue)
                        except: 
                            id = None 
                        o.moveToBasket(id)
                    elif evt.propName == "IntoBubble":
                        o.intoBubble()
                    elif evt.propName == "IntoBall":
                        o.intoBall()
                    elif evt.propName == "CanTurnIntoBall": 
                        o.canTurnIntoBall = (evt.propValue == "True")
                    elif evt.propName == "CanTurnIntoBubble": 
                        o.canTurnIntoBubble = (evt.propValue == "True")
                    elif evt.propName == "ChildCanTurnIntoBall": 
                        o.childCanTurnIntoBall = (evt.propValue == "True")
                    elif evt.propName == "ChildCanTurnIntoBubble": 
                        o.childCanTurnIntoBubble = (evt.propValue == "True")
                    elif evt.propName == "GrowToSize": 
                        if evt.propValue == "Max":
                            o.growToSize = o.maxSize
                        else:
                             o.growToSize = float(evt.propValue)
                #Properties for Pots exposed to API
                elif isinstance(o, objects.Plants.Pot):
                    if evt.propName == "GrowFlower":
                        o.growFlower()
                    if evt.propName == "StackIntoTree" and o.stack:
                        o.stack.intoTree()
                #Properties for Stacks exposed to API
                elif isinstance(o, objects.Plants.Stack):
                    if evt.propName == "StackIntoTree":
                        o.intoTree()
                #Properties for Ball exposed to API    
                elif isinstance(o, objects.PlayObjects.Ball):
                    if evt.propName == "BounceWithinScene":
                        o.bounceWithinScene = (evt.propValue == "True")
                    if evt.propName == "ChildCanChangeColour":
                        o.childCanChangeColour = (evt.propValue == "True")
                #Properties for Container exposed to API    
                elif isinstance(o, objects.Environment.Container):
                    if evt.propName == "Reward":
                        o.reward(evt.propValue)
                #Properties for Basket exposed to API    
                elif isinstance(o, objects.Environment.Basket):
                    if evt.propName == "PlayFanfare":
                        o.playFanfare()
                #Properties for Cloud exposed to API    
                elif isinstance(o, objects.Environment.Cloud):
                    if evt.propName == "CanRain":
                        o.canRain = (evt.propValue == "True")
            else:
                Logger.warning("setObjectProperty was called with object which is not in the objects list")

                
        def OnRemoveObject(self, evt):
            if int(evt.objId) in self.app.canvas.objects:
                o = self.app.canvas.objects[int(evt.objId)]
                o.remove()
            else:
                Logger.warning("No object " + evt.objId + " in world, not removing")
            evt.callback.ice_response()
        
        def OnStartAnnotator(self, evt):
            if not self.Annotator:
                self.Annotator = Annotator.Annotator(self.app)

        def OnStopAnnotator(self, evt):
            if self.Annotator:
                self.Annotator.done()
                del self.Annotator
                self.Annotator = None

        def OnAnnotatorDrawing(self, evt):
            if self.Annotator:
                self.Annotator.drawingFeature(True)
                
        def OnAnnotatorNoDrawing(self, evt):
            if self.Annotator:
                self.Annotator.drawingFeature(False)
                
        def OnDraw(self):
            self.draw()

        def OnIdle(self, evt):
            self.draw()
            evt.RequestMore()

        def InitGL(self):
            self.custom_init()
            Logger.trace("info",  "EchoesGLCanvas.InitGL()")

            # set viewing projection 
            # done in OnSize

            # model projection mode
            glMatrixMode(GL_MODELVIEW)
            glClearDepth(1.0)
            
            glEnable(GL_DEPTH_TEST)
            glEnable(GL_NORMALIZE)

            glEnable(GL_COLOR_MATERIAL)
            glEnable(GL_BLEND)
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

            glShadeModel(GL_SMOOTH)
            glHint(GL_LINE_SMOOTH_HINT, GL_NICEST)
            glEnable(GL_LINE_SMOOTH)

            glEnable(GL_LIGHTING)
            glEnable(GL_LIGHT0)

            self.setLight(0.8)
            self.targetLightLevel = 0.8
                        
            self.lineWidthRange = glGetIntegerv(GL_LINE_WIDTH_RANGE)
            try:
                self.lineWidthRange[1]
            except IndexError:
                Logger.warning( "*** HACK *** setting lineWidthRange manually")
                self.lineWidthRange = [1, 10]
                                               
        def setLight(self, brightness=1.0):
            
            self.lightLevel = brightness
             
            # Create light components
            al = brightness
            dl = max(0, brightness-0.2)
            sl = max(0, brightness-0.8)
            ambientLight = [ al, al, al, 1.0 ]
            diffuseLight = [ dl, dl, dl, 1.0 ]
            specularLight = [ sl, sl, sl, 1.0 ]
             
            # Assign created components to GL_LIGHT0
            glLightfv(GL_LIGHT0, GL_AMBIENT, ambientLight)
            glLightfv(GL_LIGHT0, GL_DIFFUSE, diffuseLight)
            glLightfv(GL_LIGHT0, GL_SPECULAR, specularLight)

                                                               
        def custom_init(self):
            pass
                                    
        def clearScene(self, quick=False):
            if hasattr(self, "background") and self.background:
                self.background.remove()
            for id,object in self.sceneElements.items():
                object.remove(not quick)
            for id,object in self.objects.items():
                object.interactive = False
                object.remove(not quick)
            # for id,agent in self.agents.items():
                # agent.remove()                            
                                                
        def draw(self):
            if self.printFPS:
                self.frameCounter += 1
                if time.time() - self.last_time >= 1:
                    current_fps = self.frameCounter / (time.time() - self.last_time)
                    print current_fps, 'fps'
                    self.frameCounter = 0
                    self.last_time = time.time()
            if self.targetLightLevel != self.lightLevel:
                newlight = self.lightLevel + (self.targetLightLevel - self.lightLevel)/100
                if abs(newlight-self.targetLightLevel) < 0.01: newlight = self.targetLightLevel
                self.setLight(newlight)

            # clear color and depth buffers
            glClearColor(self.clear_colour[0], self.clear_colour[1], self.clear_colour[2], self.clear_colour[3])
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
            
            glMatrixMode(GL_MODELVIEW)
            glLoadIdentity()
            gluLookAt (self.cameraPos[0], self.cameraPos[1], self.cameraPos[2], 0.0, 0.0, 0.0, 0.0, 1.0, 0.0)

            # position of the light needs to be set after the projection
            glLightfv(GL_LIGHT0, GL_POSITION, [-4, 2.0, 10.0, 1.0 ])
            
            self.renderBackground ()
            self.renderEnvironment()            
            self.renderObjects()
            self.renderAgents()

            if self.renderPiavca:
                Piavca.Core.getCore().timeStep(); 
                Piavca.Core.getCore().prerender();
                Piavca.Core.getCore().render();

            if self.Annotator:
                self.Annotator.render()

            self.SwapBuffers()
            
        def addBackground(self, object):
            self.background = object
            
        def renderBackground(self):
            if hasattr(self, "background") and self.background and hasattr(self.background, "render"):
                self.background.render()
                
        def removeBackground(self):
            self.background = None

        def addSceneElement(self, object):
            self.sceneElementCount = self.sceneElementCount + 1
            self.sceneElements[self.sceneElementCount] = object
            return self.sceneElementCount
            
        def removeSceneElement(self, id):
            del self.sceneElements[id]

        def renderEnvironment(self):
            for id in self.sceneElements.keys():
                object = self.sceneElements[id]
                if hasattr(object, "render"):
                    glPushName (int(id))
                    object.render()
                    glPopName ()
            
        def getMenus(self):
            menus = []
            for id, object in self.sceneElements.items():
                if isinstance(object, environment.Menu.UserMenu):
                    menus.append(object)
            return menus
            
        def addObject(self, object, props):
            self.objectCount = self.objectCount + 1
            self.objects[self.objectCount] = object
            self.rlPublisher.objectAdded(str(self.objectCount), props)
            return self.objectCount
            
        def removeObject(self, id):
            del self.objects[id]
            self.rlPublisher.objectRemoved(str(id))

        def renderObjects(self, hitTest=False):
            for id in self.objects.keys():
                object = self.objects[id]
                if hasattr(object, "render"):
                    glPushName (int(id))
                    object.render(hitTest)
                    glPopName ()
            objectsToTest = dict(filter(lambda item: hasattr(item[1], "objectCollisionTest") and item[1].objectCollisionTest==True, self.objects.iteritems()))
            collisions = self.hitTest(objectsToTest)  
                            
            for pair in collisions:                 
                objectCollision(pair[0], pair[1], self.app)
            
        def addAgent(self, agent, props):
            # self.renderPiavca = True
            self.agentCount = self.agentCount + 1
            self.agents[self.agentCount] = agent
            # self.rlPublisher.agentAdded(str(self.agentCount), agent.props)
            return self.agentCount

        def removeAgent(self, id):
            del self.agents[id]
            # if len(self.agents) == 0:
                # self.renderPiavca = False
            self.rlPublisher.agentRemoved(str(id))
            
        def renderAgents(self):
            for id in self.agents.keys():
                agent = self.agents[id]
                if hasattr(agent, "render"):
                    glPushName (int(id))
                    agent.render()
                    glPopName ()
            
            objectsToTest = dict(filter(lambda item: hasattr(item[1], "agentCollisionTest") and item[1].agentCollisionTest==True, self.objects.iteritems()))
            agentsToTest = dict(filter(lambda item: hasattr(item[1], "collisionTest") and item[1].collisionTest==True, self.agents.iteritems()))
            collisions = self.agentHitTest(agentsToTest, objectsToTest)       
                            
            for pair in collisions:                 
                agentObjectCollision(pair[0], pair[1], self.app)

        def agentActionStarted(self, callback, unique_actionid, agentId, action, details):
            self.actionLock.acquire()
            # pass
            Logger.trace("info",  "agentActionStarted " + str(action) + " " + str(callback))
            if hasattr(self, "agentPublisher"):
                self.agentPublisher.agentActionStarted(agentId, action, details)

            self.agentActions[unique_actionid] = agents.EchoesAgent.AgentAction(callback, agentId, action, details)
            self.actionLock.release()
            
        def agentActionCompleted(self, unique_actionid, success=True):
            self.actionLock.acquire()
            # pass
            Logger.trace("info",  "agentActionCompleted (" + str(unique_actionid) + "): " + str(success))
            if hasattr(self, "agentPublisher") and unique_actionid in self.agentActions:
                # if self.agentActions[unique_actionid].callback:
                    # Logger.trace("info",  "calling ice_response on callback " + str(self.agentActions[unique_actionid].callback)) 
                    # self.agentActions[unique_actionid].callback.ice_response(success)
                try:
                    if success:
                        self.agentPublisher.agentActionCompleted(self.agentActions[unique_actionid].agentId, self.agentActions[unique_actionid].action, self.agentActions[unique_actionid].details)
                    else:
                        self.agentPublisher.agentActionFailed(self.agentActions[unique_actionid].agentId, self.agentActions[unique_actionid].action, self.agentActions[unique_actionid].details, "Probably a combined action failed, because the object was moved while the agent was walking there")
                except:
                    Logger.warning("Incomplete information in completed agent action")
                del self.agentActions[unique_actionid]
            else:
                Logger.warning("Agent action completed was called with non-existing id " + str(unique_actionid))
            self.actionLock.release()
                    
        def getObjectIds(self):
            return self.objects.keys()
                
        def getObjectAtPosition(self, x, y):
            Logger.trace("info",  "Looking for objects at " + str(x) + "," + str(y))

            # Based on code from http://nehe.gamedev.net/data/lessons/lesson.asp?lesson=32
            
            # Get the current viewport
            viewport = glGetIntegerv(GL_VIEWPORT)
            
            # Prepare a buffer to hold the results
            glSelectBuffer (100)

            # Put OpenGL into selection mode, and reset the name stack
            glRenderMode(GL_SELECT)
            glInitNames()
            
            # Only draw in the area under the mouse click
            glMatrixMode(GL_PROJECTION)
            glPushMatrix()
            glLoadIdentity()
            gluPickMatrix(x, viewport[3] - y, 1.0, 1.0, viewport)
            
            # Multiply the perspective matrix by the pick matrix to restrict the drawing area
            self.projection()
            
            # Switch to normal mode, render the target to the buffer, and do some further mapping
            glMatrixMode(GL_MODELVIEW)
            self.renderObjects(True)
            glMatrixMode(GL_PROJECTION)
            glPopMatrix()
            glMatrixMode(GL_MODELVIEW)

            # Switch back to normal mode and see whether we hit anything
            records = glRenderMode(GL_RENDER)
            hitObject = -1
            # Changed mode: always take the "top" object instead of the "nearest" one (unless it's the shed)
#            if len(records) > 0:
#                hitObject = records[len(records)-1].names[0]
#                if self.objects[hitObject].props['type'] == "Shed" and len(records) > 1:
#                    hitObject = records[len(records)-2].names[0]
            distance = 1000
            for record in records:
                if (record.near < distance):
                    distance = record.near
                    hitObject = record.names[0]
                    
            return hitObject
        
        def getAgentAtPosition(self, x, y):
            Logger.trace("info",  "Looking for agents at " + str(x) + "," + str(y))
            rx, ry, rz = self.getWorldCoord((x,y))
            for id in self.agents.keys():
                agent = self.agents[id]
                if hasattr(agent, "avatar"):
                    c = agent.getXYContour()
                    if c[0][0] < rx and c[2][0] > rx and c[0][1] < ry and c[1][1] > ry:
                        return id 
            return -1

        def getScreenCoord(self, pos):
            model = glGetDoublev(GL_MODELVIEW_MATRIX)
            projection = glGetDoublev(GL_PROJECTION_MATRIX)
            viewport = glGetIntegerv(GL_VIEWPORT)
            # not sure the y value is correct here... look below
            return gluProject(pos[0], pos[1], pos[2], model, projection, viewport)
                    
        def getWorldCoord(self, pos):
            model = glGetDoublev(GL_MODELVIEW_MATRIX)
            projection = glGetDoublev(GL_PROJECTION_MATRIX)
            viewport = glGetIntegerv(GL_VIEWPORT)            
            wz = glReadPixels(pos[0],pos[0],1,1,GL_DEPTH_COMPONENT,GL_FLOAT)[0][0]
            unprojected = gluUnProject(pos[0], viewport[3]-pos[1], wz, model, projection, viewport)
            return unprojected

        def drawBezier(self, ctrlPoints, drawPoints=False, numStrips=30.0):
            glMap1f(GL_MAP1_VERTEX_3, 0.0, 1.0, ctrlPoints)
            glEnable(GL_MAP1_VERTEX_3)
            glBegin(GL_LINE_STRIP)
            for i in range(0,int(numStrips)):
                glEvalCoord1f(i/float(numStrips))
            glEnd()
            if drawPoints:
                glPointSize(5.0)
                glColor3f(1.0, 1.0, 0.0)
                glBegin(GL_POINTS)
                for point in ctrlPoints: 
                    glVertex3fv(point)
                glEnd()
                
                
        def hitTest(self, things, otherThings=None):
            collisions = []
            if not otherThings:
                for id1 in things.keys():
                    for id2 in things.keys():
                        if (id2 < id1):
                            continue
                        if (id1 != id2):
                            o1 = things[id1]
                            o2 = things[id2]
                            deltaX = o2.pos[0] - o1.pos[0]
                            deltaY = o2.pos[1] - o1.pos[1]
                            deltaZ = o2.pos[2] - o1.pos[2]
                            if isinstance(o1, objects.Plants.EchoesFlower) or isinstance(o2, objects.Plants.EchoesFlower):
                                if isinstance(o1, objects.Plants.EchoesFlower):
                                    flower = o1
                                    other = o2
                                else:
                                    flower = o2
                                    other = o1
                                deltaY = flower.pos[1] - other.pos[1]
                                if (abs(deltaX) <= other.size and 
                                    deltaY < (other.size + flower.stemLength) and deltaY > 0):
                                    collisions.append([o1, o2])                                    
                            else:
                                distanceSquared = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ
                                minDistance = o1.size + o2.size
                                if (distanceSquared < minDistance * minDistance):
                                    collisions.append([o1,o2])
            else:
                for o1 in things:
                    for o2 in otherThings:
                        deltaX = o2.pos[0] - o1.pos[0]
                        deltaY = o2.pos[1] - o1.pos[1]
                        deltaZ = o2.pos[2] - o1.pos[2]
                        distanceSquared = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ
                        minDistance = o1.size + o2.size
                        if (distanceSquared < minDistance * minDistance):
                            collisions.append([o1,o2])
                
            return collisions
        
        
        def agentHitTest(self, agentsToTest, objectsToTest):
            noAvatars = dict()
            collisions = []
            for aid, agent in agentsToTest.items():
                if isinstance(agent, agents.PiavcaAvatars.EchoesAvatar):
                    bb = agent.getXYContour()
                    for oid, object in objectsToTest.items():
                        if object.pos[0] > bb[0][0] and object.pos[0] < bb[2][0] and object.pos[1] > bb[0][1] and object.pos[1] < bb[1][1]:
                            if object.beingDragged: object.draggedOverAgent = agent.id
                            else: object.draggedOverAgent = None
                            object.overAgent = agent.id
                            collisions.append([agent, object])
                        else:
                            object.draggedOverAgent = None
                            object.overAgent = None                            
                else:
                    noAvatars[aid] = agent
            if len(noAvatars) > 0:
                collisions += self.hitTest(noAvatars, objectsToTest)
            return collisions                              
        
        def saveScreenshot(self, name=None, path=None):
            """ Read in the screen information in the area specified """
            glFinish()
            glPixelStorei(GL_PACK_ALIGNMENT, 4)
            glPixelStorei(GL_PACK_ROW_LENGTH, 0)
            glPixelStorei(GL_PACK_SKIP_ROWS, 0)
            glPixelStorei(GL_PACK_SKIP_PIXELS, 0)

            data = glReadPixels(0, 0, self.size[0], self.size[1], GL_RGBA, GL_UNSIGNED_BYTE)
            
            if not name:
                name = datetime.datetime.now().strftime("%Y-%m-%d_%H.%M")
            if not path:
                path = os.getcwd()

            im = PIL.Image.fromstring("RGBA", self.size, data)
            im.rotate(180).transpose(PIL.Image.FLIP_LEFT_RIGHT).save(path + "/" + name + ".png","PNG")

else:
    EchoesGLCanvas = None
        