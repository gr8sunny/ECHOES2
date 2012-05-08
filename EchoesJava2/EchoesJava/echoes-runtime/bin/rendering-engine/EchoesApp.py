'''
Created on 26 Aug 2009

@author: cfabric
'''
try:
    
    wxPresent = True
except ImportError:
    wxPresent = False
    
import time
import sound.EchoesAudio

from Ice import Application
from IceGrid import QueryPrx
from echoes import *
import Logger

if wxPresent:    
    from visual.EchoesGLCanvas import EchoesGLCanvas
    from interface.TouchListenerImpl import *
            
    class EchoesAppGLFrame(wx.Frame):
        def __init__(
                self, app, parent, ID, title, pos=wx.DefaultPosition,
                size=(1280, 960), style=wx.DEFAULT_FRAME_STYLE
                ):
    
            wx.Frame.__init__(self, parent, ID, title, pos, size, style)
            self.app = app
            self.canvas = EchoesGLCanvas(self)
            self.Bind(wx.EVT_CLOSE, self.OnCloseWindow)
                
        def OnCloseWindow(self, event):
            Logger.trace("info",  "closing window")
            # Stop the event loop
            self.app.shutdown()
            
    class EchoesApp(wx.App):
        def __init__(self):
            wx.App.__init__(self, redirect=False)
            self.experimentalConditions = {}

        def OnInit(self):
            self.keepGoing = True
            self.fullscreenFlag = True
            soundOn = Ice.Application.communicator().getProperties().getPropertyWithDefault('RenderingEngine.Sound', "True")
            if soundOn == "True" and not sound.EchoesAudio.soundPresent:
                sound.EchoesAudio.soundInit()
            return True
        
        def startServer(self, queryPrx, type):
            try:
                obj = queryPrx.findObjectByType(type)
                if obj:
                    obj.ice_ping()
            except Ice.NoEndpointException:
                pass
            except Ice.ObjectNotExistException:
                # This is not a problem; happens for pure clients (no object on the adapter)
                pass
            
        # Replacement main loop adapted from samples/mainloop/mainloop.py
        def MainLoop(self):

            # Create an event loop and make it active.  If you are
            # only going to temporarily have a nested event loop then
            # you should get a reference to the old one and set it as
            # the active event loop when you are done with this one...
            evtloop = wx.EventLoop()
            old = wx.EventLoop.GetActive()
            wx.EventLoop.SetActive(evtloop)
            
            first = True
    
            # This outer loop determines when to exit the application,
            # for this example we let the main frame reset this flag
            # when it closes.
            while self.keepGoing:
                
                if first:
                    # Start up everything else
                    iceGridName = Application.communicator().getProperties().getProperty("IceGrid.InstanceName")
                    queryPrx = QueryPrx.checkedCast(Application.communicator().stringToProxy(iceGridName + "/Query"))
                    
                    # DEADLOCK!
                    self.startServer(queryPrx, "::echoes::TouchServer")
                    self.startServer(queryPrx, "::echoes::PedagogicComponent")
                    
                    first = False
                                    
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
                
                # Process Ice events until we're shut down externally
                if Application.communicator().isShutdown():
                    Logger.trace("info", "Shutdown initiated, waiting until done and then cleaning up RE")
                    Application.communicator().waitForShutdown()
                    sound.EchoesAudio.soundShutdown()
                    self.keepGoing = False
    
#            wx.EventLoop.SetActive(old)

        def showWindow(self, i_size=(800,800)):
            print "wxApp.showwindows"
            if EchoesGLCanvas == None:
                raise ImportError("Can't start a because PyOpenGL is not available on this system")
            self.frame = EchoesAppGLFrame(self, None, -1, "Echoes Viewer", size=i_size)
            self.SetTopWindow(self.frame)
            self.frame.Show(True)
            self.frame.canvas.setCurrent()
            self.canvas = self.frame.canvas 
            self.canvas.app = self
            self.size = i_size
            return self.frame.canvas
        
        def fullscreen(self, fs):
            self.frame.ShowFullScreen(fs, style=wx.FULLSCREEN_ALL)
            self.fullscreenFlag = fs
            
        def clear(self):
            if self.canvas:
                self.canvas.clear()
            
        def setCanvas(self, canvas):    
            self.canvas = canvas
        
        def getCanvas(self):
            return self.canvas
        
        def shutdown(self):
            sound.EchoesAudio.soundShutdown()
            self.keepGoing = False
            self.canvas.Destroy()
            self.Destroy()
            wx.Exit()
    
    class EchoesApp_NoIce(wx.App):
        
        def __init__(self, sc_path=None, sc_device=''):
            self.sc_path = sc_path
            self.sc_device = sc_device
            wx.App.__init__(self, redirect=False)

        def OnInit(self):
            self.fullscreenFlag = False
            sound.EchoesAudio.soundInit()            
            return True
    
        def showWindow(self, i_size=(800,800)):
            print "wxApp.showwindows"
            self.controlpanel = wx.Frame(None, wx.ID_ANY, "Control Panel")
            self.controlpanel.Show(True)
            if EchoesGLCanvas == None:
                raise ImportError("Can't start a because PyOpenGL is not available on this system")
            self.frame = EchoesAppGLFrame(self, None, -1, "Echoes Viewer", size=i_size)
            self.SetTopWindow(self.frame)
            self.frame.Show(True)
            self.frame.canvas.setCurrent()
            self.canvas = self.frame.canvas 
            self.canvas.app = self
            self.size = i_size
            return self.frame.canvas
        
        def fullscreen(self, fs):
            self.frame.ShowFullScreen(fs, style=wx.FULLSCREEN_ALL)
            self.fullscreenFlag = fs
            
        def clear(self):
            if self.canvas:
                self.canvas.clear()
            
        def setCanvas(self, canvas):    
            self.canvas = canvas
        
        def getCanvas(self):
            return self.canvas
        
        def shutdown(self):
            sound.EchoesAudio.soundShutdown()
            self.canvas.Destroy()
            self.Destroy()
            wx.Exit()

_wxApp = None

def getWXApp():
    global _wxApp
    if wxPresent and _wxApp == None:
        _wxApp = EchoesApp()
    return _wxApp

def setWXApp(app):
    global _wxApp
    _wxApp = app