## @package interface.TouchListenerImpl
# Implementation for listening to data published by the touch-server
# @authors Mef, Chris
import Ice
import echoes

# Define the WX events corresponding to the events from the touch server
import wx.lib.newevent
## wx.Event for clicks from the touch server
EchoesClickEvent, EVT_ECHOES_CLICK_EVENT = wx.lib.newevent.NewEvent()
## wx.Event for point down from the touch server
EchoesPointDownEvent, EVT_ECHOES_POINT_DOWN_EVENT = wx.lib.newevent.NewEvent()
## wx.Event for point moved from the touch server
EchoesPointMovedEvent, EVT_ECHOES_POINT_MOVED_EVENT = wx.lib.newevent.NewEvent()
## wx.Event for point up from the touch server
EchoesPointUpEvent, EVT_ECHOES_POINT_UP_EVENT = wx.lib.newevent.NewEvent()
    
## Implementation of the Ice interface for listening to the touch-server data
class TouchListenerImpl(echoes.TouchListener):
    ## Constructor
    def __init__(self, app):
        self.app = app

    ## Processing clicks with coordinates and touch surface
    def click(self, x, y, width, height, current=None):
        evt = EchoesClickEvent(x=x, y=y, width=width, height=height)
        wx.PostEvent(self.app.canvas, evt)
    ## Processing a point down with coordinates and touch surface
    # @param id unique touch id to follow multi-touch events correctly
    def pointDown(self, id, x, y, width, height, current=None):
        evt = EchoesPointDownEvent(id=id, x=x, y=y, width=width, height=height)
        wx.PostEvent(self.app.canvas, evt)
    ## Processing a move of a touch with coordinates and touch surface
    # @param id unique touch id to follow multi-touch events correctly        
    def pointMoved(self, id, newX, newY, newWidth, newHeight, current=None):
        evt = EchoesPointMovedEvent(id=id, x=newX, y=newY, width=newWidth, height=newHeight)
        wx.PostEvent(self.app.canvas, evt)
    ## Processing a point up
    # @param id unique touch id to follow multi-touch events correctly        
    def pointUp(self, id, current=None):
        evt = EchoesPointUpEvent(id=id)
        wx.PostEvent(self.app.canvas, evt)
