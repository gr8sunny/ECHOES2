'''
Created on 22 Januar 2011

@author: chris
'''

import Ice, IceGrid, IceStorm
import echoes
from OpenGL.GLUT import *
import Logger
import wx, time

class PDCritiqueApp(wx.App):
    def __init__(self, rePrx):
        wx.App.__init__(self, redirect=False)
        self.rePrx = rePrx

    def OnInit(self):
        self.keepGoing = True
        self.controlpanel = wx.Frame(None, wx.ID_ANY, "Control Panel")
        self.controlpanel.Show(True)
        return True
            
    # Replacement main loop adapted from samples/mainloop/mainloop.py
    def MainLoop(self):

        print "into main loop"
        # Create an event loop and make it active.  If you are
        # only going to temporarily have a nested event loop then
        # you should get a reference to the old one and set it as
        # the active event loop when you are done with this one...
        evtloop = wx.EventLoop()
        old = wx.EventLoop.GetActive()
        wx.EventLoop.SetActive(evtloop)
        
        # This outer loop determines when to exit the application,
        # for this example we let the main frame reset this flag
        # when it closes.
        while self.keepGoing:
                            
            # Process Ice events until we're shut down externally
            if Ice.Application.communicator().isShutdown():
                break
            
            # This inner loop will process any GUI events
            # until there are no more waiting.
            while evtloop.Pending():
                evtloop.Dispatch()

            # Send idle events to idle handlers.  You may want to
            # throttle this back a bit somehow so there is not too
            # much CPU time spent in the idle handlers.  For this
            # example, I'll just snooze a little...
            time.sleep(0.01)
            self.ProcessIdle()

        wx.EventLoop.SetActive(old)
        print "exit main loop"


    def makeControlPanel(self):
        cp = wx.Panel(self.controlpanel, wx.ID_ANY)
        vbox = wx.BoxSizer(wx.VERTICAL)
        
        font = wx.SystemSettings_GetFont(wx.SYS_SYSTEM_FONT)
        font.SetPointSize(9)
    
        p1box = wx.StaticBoxSizer(wx.StaticBox(cp, -1, 'Intro Scene'), orient=wx.HORIZONTAL)
        p1grid = wx.GridSizer(1, 3, 5, 5)
        for label in ['Load', 'End']:        
            if label!=None:
                b = wx.Button(cp, -1, label, size=(120, -1))
                b.Bind(wx.EVT_BUTTON, self.intro)
                p1grid.Add(b)
            else: p1grid.Add((-1,-1), wx.EXPAND)
        p1box.Add(p1grid, 0, wx.ALIGN_CENTER)
        vbox.Add(p1box, 0, wx.EXPAND | wx.ALL, 10) 
    
        p2box = wx.StaticBoxSizer(wx.StaticBox(cp, -1, 'Bubble World'), orient=wx.HORIZONTAL)
        p2grid = wx.GridSizer(1, 5, 5, 5)
        for label in ['Load','3 Bubbles', 'Display Score', 'End']:        
            if label!=None:
                b = wx.Button(cp, -1, label, size=(120, -1))
                b.Bind(wx.EVT_BUTTON, self.bubbleworld)
                p2grid.Add(b)
            else: p2grid.Add((-1,-1), wx.EXPAND)
        p2box.Add(p2grid, 0, wx.ALIGN_CENTER)
        vbox.Add(p2box, 0, wx.EXPAND | wx.ALL, 10) 
    
        p3box = wx.StaticBoxSizer(wx.StaticBox(cp, -1, 'Garden Scene'), orient=wx.HORIZONTAL)
        p3grid = wx.GridSizer(4,6,5,5)
        for label in ['Load', 'End', 'Intro Bubble', 'Shed', None, None,
                      'Pot', 'Pond', 'Flower', 'Cloud', 'LifeTree', 'MagicLeaves']:
            if label!=None:
                b = wx.Button(cp, -1, label, size=(120, -1))
                b.Bind(wx.EVT_BUTTON, self.garden)
                p3grid.Add(b)
            else: p3grid.Add((-1,-1), wx.EXPAND)
        p3box.Add(p3grid, 0, wx.EXPAND | wx.ALIGN_CENTER)
        vbox.Add(p3box, 0, wx.EXPAND | wx.ALL, 10) 
        
        p4box = wx.StaticBoxSizer(wx.StaticBox(cp, -1, 'Agent and Animations'), orient=wx.HORIZONTAL)
        p4grid = wx.GridSizer(6,6,5,5)
        for label in ['Add Andy', 'Walk in', 'Say Hello', 'Wave',  'Giggle', None, 
                      'Shrug', 'Touch H', 'Touch A', 'Spin flower', 'Kick', "Bid Pron",
                      'Point above L', 'Point above R', 'Point down', 'Point down R', 'Request object', 'Request object 2', 
                      'Pick up object F',  'Pick up object W', 'Pick up flower', 'Place object F', 'Place object W', 'Place object H', 
                      'Sit on heels', 'Sit on heels face', 'Thinking', 'Thumbs Up', 'All done 1', 'All done 2']:
            if label!=None:
                b = wx.Button(cp, -1, label, size=(120, -1))
                b.Bind(wx.EVT_BUTTON, self.agent)
                p4grid.Add(b)
            else: p4grid.Add((-1,-1), wx.EXPAND)        
        p4grid.Add(wx.StaticText(cp, -1, "Goto:"))
        to = wx.TextCtrl(cp,-1, size=(120, -1), style=wx.TE_PROCESS_ENTER, name="goto")
        to.Bind(wx.EVT_TEXT_ENTER, self.agent)
        p4grid.Add(to)        
        p4grid.Add(wx.StaticText(cp, -1, "Look At:"))
        lookat = wx.TextCtrl(cp,-1, size=(120, -1), style=wx.TE_PROCESS_ENTER, name="lookat")
        lookat.Bind(wx.EVT_TEXT_ENTER, self.agent)
        p4grid.Add(lookat)        
        p4grid.Add(wx.StaticText(cp, -1, "Point At:"))
        pointat = wx.TextCtrl(cp,-1, size=(120, -1), style=wx.TE_PROCESS_ENTER, name="pointat")
        pointat.Bind(wx.EVT_TEXT_ENTER, self.agent)
        p4grid.Add(pointat)        
        p4box.Add(p4grid, 0, wx.EXPAND | wx.ALIGN_CENTER)
        vbox.Add(p4box, 0, wx.EXPAND | wx.ALL, 10) 
    
        p5box = wx.StaticBoxSizer(wx.StaticBox(cp, -1, 'Agent actions on objects'), orient=wx.HORIZONTAL)
        p5grid = wx.GridSizer(2,6,5,5)
        for label in ['Pick flower', 'Touch flower', 'Put flower down', 'Pick up pot', 'Put pot down', None, 
                      'Make rain', 'Put F>P', 'Stack pot', 'Touch leaves', None, None]:
            if label!=None:
                b = wx.Button(cp, -1, label, size=(120, -1))
                b.Bind(wx.EVT_BUTTON, self.agentC)
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
                b.Bind(wx.EVT_BUTTON, self.agentF)
                p6grid.Add(b)
            else: p6grid.Add((-1,-1), wx.EXPAND)
        p6box.Add(p6grid, 0, wx.EXPAND | wx.ALIGN_CENTER)
        vbox.Add(p6box, 0, wx.EXPAND | wx.ALL, 10) 
        
        pabox = wx.StaticBoxSizer(wx.StaticBox(cp, -1, 'Annotate'), orient=wx.HORIZONTAL)
        pagrid = wx.GridSizer(1,5,5,5)
        for label in ['Start', "Start ND",'Stop','Drawing off', 'Drawing on']:
            if label!=None:
                b = wx.Button(cp, -1, label, size=(120, -1))
                b.Bind(wx.EVT_BUTTON, self.annotate)
                pagrid.Add(b)
            else: pagrid.Add((-1,-1), wx.EXPAND)
        pabox.Add(pagrid, 0, wx.EXPAND | wx.ALIGN_CENTER)
        vbox.Add(pabox, 0, wx.EXPAND | wx.ALL, 10) 
    
        cp.SetSizer(vbox)
        cp.Center()
        self.controlpanel.SetSize(wx.Size(800,840))
        
    def intro(self, evt):
        o = evt.GetEventObject()
        if o.GetLabel() == "Load":
            self.rePrx.setWorldProperty("UserList", "Louis Zoe Mary-Ellen Katerina Chris")
            self.rePrx.loadScenario("Intro")
        if o.GetLabel() == "End":
            self.rePrx.endScenario("Intro")
    
    def bubbleworld(self, evt):
        o = evt.GetEventObject()
        if o.GetLabel() == "Load":
            self.rePrx.loadScenario("BubbleWorld")
        if o.GetLabel() == "3 Bubbles":
            self.rePrx.setWorldProperty("numBubbles", "3")
        if o.GetLabel() == "Display Score":
            self.rePrx.setWorldProperty("DisplayScore", "True")
        if o.GetLabel() == "End":
            self.rePrx.endScenario("BubbleWorld")
    
    def garden(self, evt):
        o = evt.GetEventObject()
        if o.GetLabel() == "Load":
            self.rePrx.loadScenario("Garden")
        if o.GetLabel() == "Intro Bubble":
            self.rePrx.addObject( "IntroBubble")
        if o.GetLabel() == "End":
            self.rePrx.endScenario("Garden")
        if o.GetLabel() == "Flower":
            self.rePrx.addObject("Flower")
        if o.GetLabel() == "Pot":
            self.rePrx.addObject("Pot")
        if o.GetLabel() == "Cloud":
            self.rePrx.addObject("Cloud")
        if o.GetLabel() == "LifeTree":
            self.rePrx.addObject("LifeTree")
        if o.GetLabel() == "Shed":
            self.rePrx.addObject("Shed")
        if o.GetLabel() == "Pond":
            self.rePrx.addObject("Pond")
        if o.GetLabel() == "MagicLeaves":
            self.rePrx.addObject("MagicLeaves")
            
    
    def agent(self, evt):
        o = evt.GetEventObject()    
        if o.GetLabel() == "Add Andy":
            self.rePrx.addAgent("Andy")    
        if o.GetLabel() == "Walk in":
            self.rePrx.executeAction( "1", "WalkTo", ["3", "-0.5"])    
            self.rePrx.executeAction( "1", "LookAtPoint", ["0","0","10"])
        if o.GetLabel() == "Wave":
            self.rePrx.executeAction( "1", "Gesture", ["wave"])   
        if o.GetLabel() == "Shrug":
            self.rePrx.executeAction( "1", "Gesture", ["shrug", "-5,0"])    
        if o.GetLabel() == "Sit on heels":
            self.rePrx.executeAction( "1", "Gesture", ["sitting_heels"])    
        if o.GetLabel() == "Sit on heels face":
            self.rePrx.executeAction( "1", "Gesture", ["sitting_heels_coveringface"])    
        if o.GetLabel() == "Thinking":
            self.rePrx.executeAction( "1", "Gesture", ["thinking_scratchinghead"])    
        if o.GetLabel() == "Touch H":
            self.rePrx.executeAction( "1", "Gesture", ["touch_headheight"])    
        if o.GetLabel() == "Touch A":
            self.rePrx.executeAction( "1", "Gesture", ["touch_above"])    
        if o.GetLabel() == "Kick":
            self.rePrx.executeAction( "1", "Gesture", ["kick_object"])    
        if o.GetLabel() == "Spin flower":
            self.rePrx.executeAction( "1", "Gesture", ["spinning_flower_stepforward"])    
        if o.GetLabel() == "Request object":
            self.rePrx.executeAction( "1", "Gesture", ["request_object"])    
        if o.GetLabel() == "Request object 2":
            self.rePrx.executeAction( "1", "Gesture", ["request_twohands"])    
        if o.GetLabel() == "Point down":
            self.rePrx.executeAction( "1", "Gesture", ["point_down"])    
        if o.GetLabel() == "Point down R":
            self.rePrx.executeAction( "1", "Gesture", ["point_down_headturn"])    
        if o.GetLabel() == "Point above R":
            self.rePrx.executeAction( "1", "Gesture", ["point_above_headturnR"])    
        if o.GetLabel() == "Point above L":
            self.rePrx.executeAction( "1", "Gesture", ["point_above_headturnL"])    
        if o.GetLabel() == "Place object F":
            self.rePrx.executeAction( "1", "Gesture", ["place_object_floor"])    
        if o.GetLabel() == "Place object W":
            self.rePrx.executeAction( "1", "Gesture", ["place_object_waistheight"])    
        if o.GetLabel() == "Place object H":
            self.rePrx.executeAction( "1", "Gesture", ["place_object_headheight"])    
        if o.GetLabel() == "Pick up object F":
            self.rePrx.executeAction( "1", "Gesture", ["pick_up_object_floor"])    
        if o.GetLabel() == "Pick up object W":
            self.rePrx.executeAction( "1", "Gesture", ["pick_up_object_waistheight"])    
        if o.GetLabel() == "Pick up flower":
            self.rePrx.executeAction( "1", "Gesture", ["pick_up_flower"])    
        if o.GetLabel() == "Thumbs Up":
            self.rePrx.executeAction( "1", "Gesture", ["thumbs_up"])    
        if o.GetLabel() == "All done 1":
            self.rePrx.executeAction( "1", "Gesture", ["all_done1"])    
        if o.GetLabel() == "All done 2":
            self.rePrx.executeAction( "1", "Gesture", ["all_done2"])    
        if o.GetLabel() == "Bid Pron":
            self.rePrx.executeAction( "1", "Gesture", ["bid_turn_pronounced"])    
        if o.GetLabel() == "Giggle":
            self.rePrx.executeAction( "1", "Gesture", ["giggle"])    
    
        if o.GetLabel() == "Say Hello":
            self.rePrx.executeAction( "1", "Say", ["hello.wav", "goodbye.wav", "christopher.wav"])    
            
        if isinstance(o, wx.TextCtrl):
            name = o.GetName()
            if name == "goto":
                value = o.GetValue()
                try:
                    valueid = int(value) 
                    self.rePrx.executeAction( "1", "WalkToObject", [value])
                except:
                    x,z = value.split(",")
                    self.rePrx.executeAction( "1", "WalkTo", [x,z])
            if name == "lookat":
                value = o.GetValue()
                try:
                    valueid = int(value) 
                    self.rePrx.executeAction( "1", "LookAtObject", [value])
                except:
                    x,y,z = value.split(",")
                    self.rePrx.executeAction( "1", "LookAtPoint", [x,y,z])
            if name == "pointat":
                value = o.GetValue()
                try:
                    valueid = int(value) 
                    self.rePrx.executeAction( "1", "PointAt", [value])
                except:
                    x,y,z = value.split(",")
                    self.rePrx.executeAction( "1", "PointAtPoint", [x,y,z])
            
    def agentC(self, evt):
        o = evt.GetEventObject()
        if o.GetLabel() == "Pick flower":
            self.rePrx.executeAction( "1", "PickFlower", None)            
        if o.GetLabel() == "Touch flower":
            self.rePrx.executeAction( "1", "TouchFlower-Ball", None)            
        if o.GetLabel() == "Make rain":
            self.rePrx.executeAction( "1", "MakeRain", None)   
        if o.GetLabel() == 'Pick up pot':
            self.rePrx.executeAction( "1", "PickUpPot", None)   
        if o.GetLabel() == 'Put pot down':
            self.rePrx.executeAction( "1", "PutPotDown", None)   
        if o.GetLabel() == 'Put flower down':
            self.rePrx.executeAction( "1", "PutFlowerDown", None)   
        if o.GetLabel() == 'Put F>P':
            self.rePrx.executeAction( "1", "PutFlowerInPot", None)   
        if o.GetLabel() == 'Stack pot':
            self.rePrx.executeAction( "1", "StackPot", None)   
        if o.GetLabel() == 'Touch leaves':
            self.rePrx.executeAction( "1", "TouchLeaves", None)   
    
    def agentF(self, evt):
        o = evt.GetEventObject()
        self.rePrx.executeAction( "1", "FacialExpression", [o.GetLabel()])            
                
    def annotate(self, evt):
        o = evt.GetEventObject()
        if o.GetLabel() == "Start":
            self.rePrx.setWorldProperty("Annotator", "True")
        if o.GetLabel() == "Start ND":
            self.rePrx.setWorldProperty("Annotator", "True")
            self.rePrx.setWorldProperty("AnnotatorDrawing", "False")
        if o.GetLabel() == "Stop":
            self.rePrx.setWorldProperty("Annotator", "False")
        if o.GetLabel() == "Drawing off":
            self.rePrx.setWorldProperty("AnnotatorDrawing", "False")
        if o.GetLabel() == "Drawing on":
            self.rePrx.setWorldProperty("AnnotatorDrawing", "True")
            

    def shutdown(self):
        self.keepGoing = False
        self.Destroy()
        wx.Exit()


class PDCritiqueIceApp(Ice.Application):

    def run(self, args):
        # Create an adapter so that we shut down cleanly through IceGrid
        adapter = self.communicator().createObjectAdapter("Adapter")
        adapter.activate()
        
        # Connect to IceStorm and get the topic manager
        iceStormName = self.communicator().getProperties().getProperty("IceStorm.InstanceName")
        topicManager = IceStorm.TopicManagerPrx.checkedCast(self.communicator().stringToProxy(iceStormName + "/TopicManager"))
        iceGridName = Ice.Application.communicator().getProperties().getProperty("IceGrid.InstanceName")


        queryPrx = IceGrid.QueryPrx.checkedCast(Ice.Application.communicator().stringToProxy(iceGridName + "/Query"))
        print echoes.RenderingEngine.ice_staticId()
        rePrx = echoes.RenderingEnginePrx.checkedCast(queryPrx.findObjectByType(echoes.RenderingEngine.ice_staticId()))
        print rePrx
        
        # Initialise the GUI
        wxApp = PDCritiqueApp(rePrx)
        wxApp.makeControlPanel()
        
        # Start the animation
        wxApp.MainLoop()

        # If we got here, we are shutting down, so we first have to unsubscribe from the TouchListener topic
        Logger.trace("info",  "Unsubscribing ...")
        self.communicator().destroy()

        return 0

Logger.trace("info", "Starting up PDCritique")
app = PDCritiqueIceApp()
status = app.main(sys.argv)
sys.exit(status)
Logger.trace("info", "Exiting PDCritique")
