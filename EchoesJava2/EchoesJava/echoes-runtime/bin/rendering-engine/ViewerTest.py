'''
Created on 26 Aug 2009

@author: cfabric
'''

from EchoesApp import *
import objects.EchoesObject
import objects.Plants
import objects.Environment
import environment.Backgrounds
import environment.HelperElements
import visual.Annotator
import agents.PiavcaAvatars
import Piavca, math, random
import wx
from OpenGL.GLUT import *
from interface.RenderingEngineImpl import RenderingEngineImpl

global avatar
avatar = None

global reAPI
reAPI = None

global iceCB
iceCB = None

class FakeRLPublisher():
    
    def objectAdded(self, objId, props, current=None):
        print "objectAdded", objId, props
    def objectRemoved(self, objId, current=None):
        print "objectRemoved", objId
    def objectPropertyChanged(self, objId, propName, propValue, current=None):
        print "objectPropertyChanged", objId, propName, propValue
    def agentAdded(self, agentId, props, current=None):
        pass
    def agentRemoved(self, agentId, current=None):
        pass
    def agentPropertyChanged(self, agentId, propName, propValue, current=None):
        print "agentPropertyChanged", agentId, propName, propValue
    def actionExecuted(self, agentId, action, objIds, current=None):
        pass
    def userStarted(self, userName, current=None):
        pass
    def scenarioStarted(self, name, current=None):
        print "scenarioStarted", name
    def scenarioEnded(self, name, current=None):
        print "scenarioEnded", name
    def userTouchedObject(self, name, current=None):
        pass
    def userTouchedAgent(self, name, current=None):
        print "userTouchedAgent", name
        pass
    def worldPropertyChanged(self, name, value, current=None):
        print "worldPropertyChanged", name, value
        pass    

class FakeAgentPublisher():
    
    def agentActionStarted(self, agent, action, details, current=None):
        print "agentActionStarted " + str(agent) + " " + str(action) + " " + str(details)
    def agentActionCompleted(self, agent, action, details, current=None):
        print "agentActionCompleted " + str(agent) + " " + str(action) + " " + str(details)
    def agentActionFailed(self, agent, action, details, reason, current=None):
        print "agentActionFailed " + str(agent) + " " + str(action) + " " + str(details) + " " + str(reason)
    
class FakeIceCallback():
    def ice_response(self, msg=None):
        print "Ice callback triggered", msg
            
def OnKeyboard(evt):
    try:
        if type(evt.KeyCode) == int:
            char = chr(evt.KeyCode)
        else:
            char = chr(evt.KeyCode())
    except ValueError:
        print "KeyInput.KeyPressedCB not ASCII"
        return

    if char == 'f':
        app.fullscreen(True)

    if char == 'l':
        app.canvas.setLight()

    print "KeyInput.KeyPressedCB in ViewwerTest", char
        
def makeControlPanel(app):
    cp = wx.Panel(app.controlpanel, wx.ID_ANY)
    vbox = wx.BoxSizer(wx.VERTICAL)
    
    font = wx.SystemSettings_GetFont(wx.SYS_SYSTEM_FONT)
    font.SetPointSize(9)

    p1box = wx.StaticBoxSizer(wx.StaticBox(cp, -1, 'Intro'), orient=wx.HORIZONTAL)
    p1grid = wx.GridSizer(1, 3, 5, 5)
    for label in ['Load', 'End']:        
        if label!=None:
            b = wx.Button(cp, -1, label, size=(120, -1))
            b.Bind(wx.EVT_BUTTON, intro)
            p1grid.Add(b)
        else: p1grid.Add((-1,-1), wx.EXPAND)
    p1box.Add(p1grid, 0, wx.ALIGN_CENTER)
    vbox.Add(p1box, 0, wx.EXPAND | wx.ALL, 10) 

    p2box = wx.StaticBoxSizer(wx.StaticBox(cp, -1, 'Bubble World'), orient=wx.HORIZONTAL)
    p2grid = wx.GridSizer(1, 5, 5, 5)
    for label in ['Load','3 Bubbles', 'Display Score', 'End']:        
        if label!=None:
            b = wx.Button(cp, -1, label, size=(120, -1))
            b.Bind(wx.EVT_BUTTON, bubbleworld)
            p2grid.Add(b)
        else: p2grid.Add((-1,-1), wx.EXPAND)
    p2box.Add(p2grid, 0, wx.ALIGN_CENTER)
    vbox.Add(p2box, 0, wx.EXPAND | wx.ALL, 10) 

    p3box = wx.StaticBoxSizer(wx.StaticBox(cp, -1, 'Garden'), orient=wx.HORIZONTAL)
    p3grid = wx.GridSizer(4,6,5,5)
    for label in ['Load', 'End', 'Intro Bubble', "MagicLeaves", "Basket", "Container",
                  'Pot', 'Pond', 'Flower', 'Cloud', 'LifeTree', 'Shed', 
                  'ContainerReward', 'Ball']:
        if label!=None:
            b = wx.Button(cp, -1, label, size=(120, -1))
            b.Bind(wx.EVT_BUTTON, garden)
            p3grid.Add(b)
        else: p3grid.Add((-1,-1), wx.EXPAND)
    p3grid.Add(wx.StaticText(cp, -1, "LightLevel:"))
    light = wx.TextCtrl(cp,-1, size=(120, -1), style=wx.TE_PROCESS_ENTER, name="light")
    light.Bind(wx.EVT_TEXT_ENTER, garden)
    p3grid.Add(light)        
    p3box.Add(p3grid, 0, wx.EXPAND | wx.ALIGN_CENTER)
    vbox.Add(p3box, 0, wx.EXPAND | wx.ALL, 10) 
    
    p4box = wx.StaticBoxSizer(wx.StaticBox(cp, -1, 'Agent'), orient=wx.HORIZONTAL)
    p4grid = wx.GridSizer(6,6,5,5)
    for label in ['Add Paul', 'Add Andy', 'Walk in', 'Wave', 'Thumbs Up', 'Giggle', 
                  'Shrug', 'Touch H', 'Touch A', 'Spin flower', 'Kick', "Bid Pron",
                  'Point above L', 'Point above R', 'Point down', 'Point down R', 'Request object', 'Request object 2', 
                  'Pick up object F',  'Pick up object W', 'Pick up flower', 'Place object F', 'Place object W', 'Place object H', 
                  'Sit on heels', 'Sit on heels face', 'Thinking', 'All done 1', 'All done 2', 'Say Hello']:
        if label!=None:
            b = wx.Button(cp, -1, label, size=(120, -1))
            b.Bind(wx.EVT_BUTTON, agent)
            p4grid.Add(b)
        else: p4grid.Add((-1,-1), wx.EXPAND)        
    p4grid.Add(wx.StaticText(cp, -1, "Goto:"))
    to = wx.TextCtrl(cp,-1, size=(120, -1), style=wx.TE_PROCESS_ENTER, name="goto")
    to.Bind(wx.EVT_TEXT_ENTER, agent)
    p4grid.Add(to)        
    p4grid.Add(wx.StaticText(cp, -1, "Look At:"))
    lookat = wx.TextCtrl(cp,-1, size=(120, -1), style=wx.TE_PROCESS_ENTER, name="lookat")
    lookat.Bind(wx.EVT_TEXT_ENTER, agent)
    p4grid.Add(lookat)        
    p4grid.Add(wx.StaticText(cp, -1, "Point At:"))
    pointat = wx.TextCtrl(cp,-1, size=(120, -1), style=wx.TE_PROCESS_ENTER, name="pointat")
    pointat.Bind(wx.EVT_TEXT_ENTER, agent)
    p4grid.Add(pointat)        
    p4box.Add(p4grid, 0, wx.EXPAND | wx.ALIGN_CENTER)
    vbox.Add(p4box, 0, wx.EXPAND | wx.ALL, 10) 

    p5box = wx.StaticBoxSizer(wx.StaticBox(cp, -1, 'Agent - Combined Actions'), orient=wx.HORIZONTAL)
    p5grid = wx.GridSizer(2,6,5,5)
    for label in ['Pick flower', 'Touch flower', 'Put flower down', 'Pick up pot', 'Put pot down', 'Put F>B', 
                  'Make rain', 'Put F>P', 'Stack pot', 'Touch leaves', 'AttachCloud', 'DetachCloud']:
        if label!=None:
            b = wx.Button(cp, -1, label, size=(120, -1))
            b.Bind(wx.EVT_BUTTON, agentC)
            p5grid.Add(b)
        else: p5grid.Add((-1,-1), wx.EXPAND)
    p5box.Add(p5grid, 0, wx.EXPAND | wx.ALIGN_CENTER)
    vbox.Add(p5box, 0, wx.EXPAND | wx.ALL, 10) 

    p6box = wx.StaticBoxSizer(wx.StaticBox(cp, -1, 'Agent - Facial expressions'), orient=wx.HORIZONTAL)
    p6grid = wx.GridSizer(2,6,5,5)
    for label in ['Neutral', 'Happy', 'Sad', 'Laugh', 'OpenMouth', 'ClosedEyes',
                  'Grin', 'Blink', 'Aggressive', None, None, None]:
        if label!=None:
            b = wx.Button(cp, -1, label, size=(120, -1))
            b.Bind(wx.EVT_BUTTON, agentF)
            p6grid.Add(b)
        else: p6grid.Add((-1,-1), wx.EXPAND)
    p6box.Add(p6grid, 0, wx.EXPAND | wx.ALIGN_CENTER)
    vbox.Add(p6box, 0, wx.EXPAND | wx.ALL, 10) 
    
    pabox = wx.StaticBoxSizer(wx.StaticBox(cp, -1, 'Annotate'), orient=wx.HORIZONTAL)
    pagrid = wx.GridSizer(1,4,5,5)
    for label in ['Start', 'Stop','Drawing off', 'Drawing on']:
        if label!=None:
            b = wx.Button(cp, -1, label, size=(120, -1))
            b.Bind(wx.EVT_BUTTON, annotate)
            pagrid.Add(b)
        else: pagrid.Add((-1,-1), wx.EXPAND)
    pabox.Add(pagrid, 0, wx.EXPAND | wx.ALIGN_CENTER)
    vbox.Add(pabox, 0, wx.EXPAND | wx.ALL, 10) 

    cp.SetSizer(vbox)
    cp.Center()
    app.controlpanel.SetSize(wx.Size(800,840))
    
def intro(evt):
    o = evt.GetEventObject()
    if o.GetLabel() == "Load":
        reAPI.setWorldProperty("UserList", "Louis")
        reAPI.loadScenario_async(iceCB,"Intro", None)
    if o.GetLabel() == "End":
        reAPI.endScenario_async(iceCB,"Intro", None)

def bubbleworld(evt):
    o = evt.GetEventObject()
    if o.GetLabel() == "Load":
        reAPI.loadScenario_async(iceCB,"BubbleWorld", None)
        reAPI.addObject_async(iceCB, "IntroBubble")
        reAPI.setWorldProperty("numBubbles", "3")
#        reAPI.setWorldProperty("DisplayScore", "True")
    if o.GetLabel() == "3 Bubbles":
        reAPI.setWorldProperty("numBubbles", "3")
    if o.GetLabel() == "Display Score":
        reAPI.setWorldProperty("DisplayScore", "True")
    if o.GetLabel() == "End":
        reAPI.endScenario_async(iceCB,"BubbleWorld", None)

def garden(evt):
    o = evt.GetEventObject()
    if o.GetLabel() == "Load":
        reAPI.loadScenario_async(iceCB,"GardenVeg", None)
        reAPI.addObject_async(iceCB, "IntroBubble")        
    if o.GetLabel() == "Intro Bubble":
        reAPI.addObject_async(iceCB, "IntroBubble")
    if o.GetLabel() == "End":
        reAPI.endScenario_async(iceCB,"Garden", None)
    if o.GetLabel() == "Flower":
        reAPI.addObject_async(iceCB,"Flower", None)
    if o.GetLabel() == "Pot":
        reAPI.addObject_async(iceCB,"Pot", None)
    if o.GetLabel() == "Cloud":
        reAPI.addObject_async(iceCB,"Cloud", None)
    if o.GetLabel() == "LifeTree":
        reAPI.addObject_async(iceCB,"LifeTree", None)
    if o.GetLabel() == "Shed":
        reAPI.addObject_async(iceCB,"Shed", None)
    if o.GetLabel() == "Pond":
        reAPI.addObject_async(iceCB,"Pond", None)
    if o.GetLabel() == "MagicLeaves":
        reAPI.addObject_async(iceCB,"MagicLeaves", None)
    if o.GetLabel() == "Basket":
        reAPI.addObject_async(iceCB,"Basket", None)
    if o.GetLabel() == "Ball":
        reAPI.addObject_async(iceCB,"Ball", None)
    if o.GetLabel() == "Container":
        reAPI.addObject_async(iceCB,"Container", None)
    if o.GetLabel() == "ContainerReward":
        reAPI.setObjectProperty("2","Reward", "Bees")
    if isinstance(o, wx.TextCtrl):
        name = o.GetName()
        if name == "light":
            value = o.GetValue()
            reAPI.setWorldProperty("LightLevel", value)        
        

def agent(evt):
    o = evt.GetEventObject()    
    if o.GetLabel() == "Add Paul":
        reAPI.addAgent_async(iceCB, "Paul")    
    if o.GetLabel() == "Add Andy":
        reAPI.addAgent_async(iceCB, "Andy")    
    if o.GetLabel() == "Walk in":
        reAPI.executeAction_async(iceCB, 1, "WalkTo", ["3", "-0.5"])    
        reAPI.executeAction_async(iceCB, 1, "Gesture", ["wave", "child"])
        reAPI.executeAction_async(iceCB, 1, "LookAtChild", ["hello.wav,christopher.wav"])    
    if o.GetLabel() == "Wave":
        reAPI.executeAction_async(iceCB, 1, "LookAtChild", [])    
        reAPI.executeAction_async(iceCB, 1, "Gesture", ["wave", "hello.wav"])   
    if o.GetLabel() == "Shrug":
        reAPI.executeAction_async(iceCB, 1, "Gesture", ["shrug", "-5,0"])    
    if o.GetLabel() == "Sit on heels":
        reAPI.executeAction_async(iceCB, 1, "Gesture", ["sitting_heels"])    
    if o.GetLabel() == "Sit on heels face":
        reAPI.executeAction_async(iceCB, 1, "Gesture", ["sitting_heels_coveringface"])    
    if o.GetLabel() == "Thinking":
        reAPI.executeAction_async(iceCB, 1, "Gesture", ["thinking_scratchinghead"])    
    if o.GetLabel() == "Touch H":
        reAPI.executeAction_async(iceCB, 1, "Gesture", ["touch_headheight"])    
    if o.GetLabel() == "Touch A":
        reAPI.executeAction_async(iceCB, 1, "Gesture", ["touch_above"])    
    if o.GetLabel() == "Kick":
        reAPI.executeAction_async(iceCB, 1, "Gesture", ["kick_object"])    
    if o.GetLabel() == "Spin flower":
        reAPI.executeAction_async(iceCB, 1, "Gesture", ["spinning_flower_stepforward"])    
    if o.GetLabel() == "Request object":
        reAPI.executeAction_async(iceCB, 1, "Gesture", ["request_object"])    
    if o.GetLabel() == "Request object 2":
        reAPI.executeAction_async(iceCB, 1, "Gesture", ["request_twohands"])    
    if o.GetLabel() == "Point down":
        reAPI.executeAction_async(iceCB, 1, "Gesture", ["point_down"])    
    if o.GetLabel() == "Point down R":
        reAPI.executeAction_async(iceCB, 1, "Gesture", ["point_down_headturn"])    
    if o.GetLabel() == "Point above R":
        reAPI.executeAction_async(iceCB, 1, "Gesture", ["point_above_headturnR"])    
    if o.GetLabel() == "Point above L":
        reAPI.executeAction_async(iceCB, 1, "Gesture", ["point_above_headturnL"])    
    if o.GetLabel() == "Place object F":
        reAPI.executeAction_async(iceCB, 1, "Gesture", ["place_object_floor"])    
    if o.GetLabel() == "Place object W":
        reAPI.executeAction_async(iceCB, 1, "Gesture", ["place_object_waistheight"])    
    if o.GetLabel() == "Place object H":
        reAPI.executeAction_async(iceCB, 1, "Gesture", ["place_object_headheight"])    
    if o.GetLabel() == "Pick up object F":
        reAPI.executeAction_async(iceCB, 1, "Gesture", ["pick_up_object_floor"])    
    if o.GetLabel() == "Pick up object W":
        reAPI.executeAction_async(iceCB, 1, "Gesture", ["pick_up_object_waistheight"])    
    if o.GetLabel() == "Pick up flower":
        reAPI.executeAction_async(iceCB, 1, "Gesture", ["pick_up_flower"])    
    if o.GetLabel() == "Thumbs Up":
        reAPI.executeAction_async(iceCB, 1, "Gesture", ["thumbs_up", "hello.wav", "speed=0.5", "hold=5.0"])    
    if o.GetLabel() == "All done 1":
        reAPI.executeAction_async(iceCB, 1, "Gesture", ["all_done1"])    
    if o.GetLabel() == "All done 2":
        reAPI.executeAction_async(iceCB, 1, "Gesture", ["all_done2"])    
    if o.GetLabel() == "Bid Pron":
        reAPI.executeAction_async(iceCB, 1, "Gesture", ["bid_turn_pronounced"])    
    if o.GetLabel() == "Giggle":
        reAPI.executeAction_async(iceCB, 1, "Gesture", ["giggle"])    

    if o.GetLabel() == "Say Hello":
        reAPI.executeAction_async(iceCB, 1, "LookAtChild", [])
        reAPI.executeAction_async(iceCB, 1, "FacialExpression", ["Neutral"])
        reAPI.executeAction_async(iceCB, 1, "LookAtObject", ["2"])
        reAPI.executeAction_async(iceCB, 1, "FacialExpression", ["Happy"])
        reAPI.executeAction_async(iceCB, 1, "PointAt", ["2"])    
        
    if isinstance(o, wx.TextCtrl):
        name = o.GetName()
        if name == "goto":
            value = o.GetValue()
            try:
                valueid = int(value) 
                reAPI.executeAction_async(iceCB, 1, "WalkToObject", [value])
            except:
                try:
                    x,z = value.split(",")
                    reAPI.executeAction_async(iceCB, 1, "WalkTo", [x,z])
                except:
                    reAPI.executeAction_async(iceCB, 1, "SetDepthLayer", [value])
        if name == "lookat":
            value = o.GetValue()
            try:
                valueid = int(value) 
                reAPI.executeAction_async(iceCB, 1, "LookAtObject", [value])
            except:
                x,y,z = value.split(",")
                reAPI.executeAction_async(iceCB, 1, "LookAtPoint", [x,y,z])
        if name == "pointat":
            value = o.GetValue()
            try:
                valueid = int(value) 
                reAPI.executeAction_async(iceCB, 1, "PointAt", [value])
            except:
                x,y,z = value.split(",")
                reAPI.executeAction_async(iceCB, 1, "PointAtPoint", [x,y,z])
        
def agentC(evt):
    o = evt.GetEventObject()
    if o.GetLabel() == "Pick flower":
        reAPI.executeAction_async(iceCB, 1, "PickFlower", [None, "WalkTo=True", "hello.wav"])            
    if o.GetLabel() == "Touch flower":
        reAPI.executeAction_async(iceCB, 1, "TouchObject", ["2", "WalkTo=True", "hello.wav"])            
    if o.GetLabel() == "Make rain":
        reAPI.executeAction_async(iceCB, 1, "MakeRain", [None, "WalkTo=True"])   
    if o.GetLabel() == 'Pick up pot':
        reAPI.executeAction_async(iceCB, 1, "PickUpBall", [None, "WalkTo=True", "hello.wav"])   
    if o.GetLabel() == 'Put pot down':
        reAPI.executeAction_async(iceCB, 1, "PutBallDown", ["WalkTo=7"])
        reAPI.executeAction_async(iceCB, 1, "Say", ['wonder-what-pots-for.wav'])
        reAPI.executeAction_async(iceCB, 1, "FacialExpression", ['Neutral'])   
    if o.GetLabel() == 'Put flower down':
        reAPI.executeAction_async(iceCB, 1, "PutFlowerDown", ["WalkTo=2"])   
    if o.GetLabel() == 'Put F>P':
        reAPI.executeAction_async(iceCB, 1, "PutFlowerInPot", None)   
    if o.GetLabel() == 'Put F>B':
        reAPI.executeAction_async(iceCB, 1, "PutFlowerInBasket", [None, "WalkTo=True"])   
    if o.GetLabel() == 'Stack pot':
        reAPI.executeAction_async(iceCB, 1, "PutBallIntoContainer", [None, "WalkTo=True"])   
    if o.GetLabel() == 'Touch leaves':
        reAPI.executeAction_async(iceCB, 1, "ThrowBall", ["2"])   
    if o.GetLabel() == 'AttachCloud':
        reAPI.executeAction_async(iceCB, 1, "AttachCloud", [None])   
    if o.GetLabel() == 'DetachCloud':
        reAPI.executeAction_async(iceCB, 1, "DetachCloud", ["None"])   

def agentF(evt):
    o = evt.GetEventObject()
    reAPI.executeAction_async(iceCB, 1, "FacialExpression", [o.GetLabel()])            
            
def annotate(evt):
    o = evt.GetEventObject()
    if o.GetLabel() == "Start":
        app.canvas.Annotator = visual.Annotator.Annotator(app)
    if o.GetLabel() == "Stop":
        app.canvas.Annotator.done()
        del app.canvas.Annotator
        app.canvas.Annotator = None
    if o.GetLabel() == "Drawing off":
        if app.canvas.Annotator:
            app.canvas.Annotator.drawingFeature(False)
    if o.GetLabel() == "Drawing on":
        if app.canvas.Annotator:
            app.canvas.Annotator.drawingFeature(True)
        
            
if __name__ == '__main__':
    app = EchoesApp_NoIce('C:\\Program Files (x86)\\SuperCollider', 'ASIO : ASIO4ALL v2')     # call with sc_path to enable sound
#    app = EchoesApp_NoIce('/usr/local/src/SuperCollider/build')
    app.showWindow((1000,600))
    app.canvas.cameraPos = (0,0,20)
    app.canvas.printFPS = False
    app.canvas.rlPublisher = FakeRLPublisher()
    app.canvas.agentPublisher = FakeAgentPublisher()
    app.canvas.aspectFourByThree = False
    app.fullscreen(False)
    app.canvas.Bind(wx.EVT_CHAR, OnKeyboard)
#    environment.HelperElements.Axis(app)

    reAPI = RenderingEngineImpl(app, app.canvas.rlPublisher)
    iceCB = FakeIceCallback()
    
    makeControlPanel(app)
        
    glutInit()
    
    app.MainLoop()
