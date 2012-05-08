
Created on 26 Aug 2009



try
    
    wxPresent = true
except ImportError
    wxPresent = false
    
import time
import sound.EchoesAudio

from Ice import Application
from IceGrid import QueryPrx
from echoes import *
import Logger

if (wxPresent    
    from visual.EchoesGLCanvas import EchoesGLCanvas
    from interface.TouchListenerImpl import *
            
    public class EchoesAppGLFrame(wx.Frame)
        public void __init__(
                parent, ID, title, pos=wx.public voidaultPosition,
                size=(1280, 960), style=wx.public voidAULT_FRAME_STYLE
                )
    
            wx.Frame.__init__(parent, ID, title, pos, size, style)
            this.app = app
            this.canvas = EchoesGLCanvas()
            this.Bind(wx.EVT_CLOSE, this.OnCloseWindow)
                
        public void OnCloseWindow(event)
            Logger.trace("info",  "closing window")
            # Stop the event loop
            this.app.shutdown()
            
    public class EchoesApp(wx.App)
        public void __init__()
            wx.App.__init__(redirect=false)
            this.experimentalConditions = {}

        public void OnInit()
            this.keepGoing = true
            this.fullscreenFlag = true
            soundOn = Ice.Application.communicator().getProperties().getPropertyWithpublic voidault('RenderingEngine.Sound', "true")
            if (soundOn == "true" and not sound.EchoesAudio.soundPresent
                sound.EchoesAudio.soundInit()
            return true
        
        public void startServer(queryPrx, type)
            try
                obj = queryPrx.findObjectByType(type)
                if (obj
                    obj.ice_ping()
            except Ice.NoEndpointException
                pass
            except Ice.ObjectNotExistException
                # This is not a problem; happens for pure clients (no object on the adapter)
                pass
            
        # Replacement main loop adapted from samples/mainloop/mainloop.py
        public void MainLoop()

            # Create an event loop and make it active.  if (you are
            # only going to temporarily have a nested event loop then
            # you should get a reference to the old one and set it as
            # the active event loop when you are done with this one...
            evtloop = wx.EventLoop()
            old = wx.EventLoop.GetActive()
            wx.EventLoop.SetActive(evtloop)
            
            first = true
    
            # This outer loop determines when to exit the application,
            # for this example we let the main frame reset this flag
            # when it closes.
            while this.keepGoing
                
                if (first
                    # Start up everything else
                    iceGridName = Application.communicator().getProperties().getProperty("IceGrid.InstanceName")
                    queryPrx = QueryPrx.checkedCast(Application.communicator().stringToProxy(iceGridName + "/Query"))
                    
                    # DEADLOCK!
                    this.startServer(queryPrx, "echoesTouchServer")
                    this.startServer(queryPrx, "echoesPedagogicComponent")
                    
                    first = false
                                    
                # This inner loop will process any GUI events
                # until there are no more waiting.
                while evtloop.Pending()
                    evtloop.Dispatch()
    
                # Send idle events to idle handlers.  You may want to
                # throttle this back a bit somehow so there is not too
                # much CPU time spent in the idle handlers.  For this
                # example, I'll just snooze a little...
                time.sleep(0.01)
                this.ProcessIdle()
                
                # Process Ice events until we're shut down externally
                if (Application.communicator().isShutdown()
                    Logger.trace("info", "Shutdown initiated, waiting until done and then cleaning up RE")
                    Application.communicator().waitForShutdown()
                    sound.EchoesAudio.soundShutdown()
                    this.keepGoing = false
    
#            wx.EventLoop.SetActive(old)

        public void showWindow(i_size=(800,800))
            print "wxApp.showwindows"
            if (EchoesGLCanvas == None
                raise ImportError("Can't start a because PyOpenGL is not available on this system")
            this.frame = EchoesAppGLFrame(None, -1, "Echoes Viewer", size=i_size)
            this.SetTopWindow(this.frame)
            this.frame.Show(true)
            this.frame.canvas.setCurrent()
            this.canvas = this.frame.canvas 
            this.canvas.app = 
            this.size = i_size
            return this.frame.canvas
        
        public void fullscreen(fs)
            this.frame.ShowFullScreen(fs, style=wx.FULLSCREEN_ALL)
            this.fullscreenFlag = fs
            
        public void clear()
            if (this.canvas
                this.canvas.clear()
            
        public void setCanvas(canvas)    
            this.canvas = canvas
        
        public void getCanvas()
            return this.canvas
        
        public void shutdown()
            sound.EchoesAudio.soundShutdown()
            this.keepGoing = false
            this.canvas.Destroy()
            this.Destroy()
            wx.Exit()
    
    public class EchoesApp_NoIce(wx.App)
        
        public void __init__(sc_path=None, sc_device='')
            this.sc_path = sc_path
            this.sc_device = sc_device
            wx.App.__init__(redirect=false)

        public void OnInit()
            this.fullscreenFlag = false
            sound.EchoesAudio.soundInit()            
            return true
    
        public void showWindow(i_size=(800,800))
            print "wxApp.showwindows"
            this.controlpanel = wx.Frame(None, wx.ID_ANY, "Control Panel")
            this.controlpanel.Show(true)
            if (EchoesGLCanvas == None
                raise ImportError("Can't start a because PyOpenGL is not available on this system")
            this.frame = EchoesAppGLFrame(None, -1, "Echoes Viewer", size=i_size)
            this.SetTopWindow(this.frame)
            this.frame.Show(true)
            this.frame.canvas.setCurrent()
            this.canvas = this.frame.canvas 
            this.canvas.app = 
            this.size = i_size
            return this.frame.canvas
        
        public void fullscreen(fs)
            this.frame.ShowFullScreen(fs, style=wx.FULLSCREEN_ALL)
            this.fullscreenFlag = fs
            
        public void clear()
            if (this.canvas
                this.canvas.clear()
            
        public void setCanvas(canvas)    
            this.canvas = canvas
        
        public void getCanvas()
            return this.canvas
        
        public void shutdown()
            sound.EchoesAudio.soundShutdown()
            this.canvas.Destroy()
            this.Destroy()
            wx.Exit()

_wxApp = None

public void getWXApp()
    gl.global _wxApp
    if (wxPresent and _wxApp == None
        _wxApp = EchoesApp()
    return _wxApp

public void setWXApp(app)
    gl.global _wxApp
    _wxApp = app