## \mainpage The Rendering Engine
#
# \section intro_sec Introduction
#
# The rendering-engine project is part of the ECHOES system and communicates with other components of ECHOES through the middleware Ice.
# It implements four interfaces, which are defined by the corresponding slice files in the echoes-ice-interfaces component. The slice2py
# command generates a set of python bindings through which the Rendering Engine (or short RE) communicates. 
#
# The four interfaces of the RE are:
#
# -# interface.RenderingEngineImpl.RenderingEngineImpl implements calls the RE understands and acts upon
# -# interface.TouchListenerImpl.TouchListenerImpl implements a listener to the data sent by the touch-server
# -# RenderingListener is an Ice topic created to publish messages about the state of the objects in the RE
# -# AgentListener is an Ice topic created to publish messages about actions from the agent and the child
#
# Follow the links for the first two interfaces for more details and a specification of the commands the RE understands. 
# The later two are described below: 
#
# \section install_sec The RenderingListener
#
# The following messages are published by the RenderingListener (see the RenderingListener.slice file for the definition of the call)
# 
# - agentAdded(agentId, props)
# - agentRemoved(agentId)
# - userTouchedAgent(agentId)
# - agentPropertyChanged(agentId, "Visible", "True"/"False")
#
# - userStarted(name)
# - scenarioStarted (name)
# - scenarioEnded(name)
# - worldPropertyChanged("BubbleScore", score)
# - worldPropertyChanged("FlowerScore", score)
# - worldPropertyChanged("Score", score) \b Note: this is published at any time while the above are additional in certain scenes
# - userTouchedObject(objectId)
#
# - objectAdded(objectId, props)
# - objectRemoved(objectId)
# - objectPropertyChanged(objectId, "ScreenRegion", echoes.ScreenRegion)
# - objectPropertyChanged(objectId, "ScreenCoordinates", "[x,y]")
# - objectPropertyChanged(objectId, "WorldCoordinates", "[x,y,z]")
# - objectPropertyChanged(objectId, "HorizontalSlot", "0".."9")        
# - objectPropertyChanged(objectId, "atAgent", agentId) \b Note: ready to be acted upon
# - objectPropertyChanged(objectId, "nearAgent", agentId) \b Note: near, but not necessarily in front of agent
# - objectPropertyChanged(objectId, "overAgent", agentId) \b Note: whenever object is over agent
# - objectPropertyChanged(objectId, "draggedOverAgent", agentId) \b Note: whenever object is being dragged over agent by the child
# - objectPropertyChanged(objectId, "attachedToAgent", "True"/"False")
# - objectPropertyChanged(objectId, "touchedByAgent", agentId)
# - objectPropertyChanged(bubbleId, "bubble_pop", "")
# - objectPropertyChanged(bubbleId, "bubble_merge", "") 
# - objectPropertyChanged(cloudId, "cloud_rain", "True"/"False")
# - objectPropertyChanged(cloudId, "cloud_flower", flowerId) \b Note: when cloud grows flower
# - objectPropertyChanged(cloudId, "cloud_hitby", objectId) \b Note: when hit by an object handled in collisions
# - objectPropertyChanged(pondId, "pond_grow", size)
# - objectPropertyChanged(pondId, "pond_shrink", size)
# - objectPropertyChanged(flowerId, "flower_pot", potId/"None") \b Note: when flower is in pot
# - objectPropertyChanged(flowerId, "flower_basket", basketId/"None") \b Note: when flower is in pot
# - objectPropertyChanged(flowerId, "under_cloud", "True"/"False")\ b Note: when flower is under a cloud
# - objectPropertyChanged(flowerId, "is_growing", "True"/"False") \b Note: when flower is growing
# - objectPropertyChanged(flowerId, "flower_bubble", bubbleId) \b Note: when flower turns into a bubble
# - objectPropertyChanged(flowerId, "flower_ball", ballId) \b Note: when flower turns into a ball
# - objectPropertyChanged(potId, "pot_flower", flowerId/"None")
# - objectPropertyChanged(potId, "pot_stack", "True"/"False")
# - objectPropertyChanged(potId, "has_on_top", objectId/"None")
# - objectPropertyChanged(potId, "is_on_top_of", objectId/"None")
# - objectPropertyChanged(potId, "under_cloud", "True"/"False")
# - objectPropertyChanged(basketId, "basket_stack", "True"/"False")
# - objectPropertyChanged(basketId, "basket_flower", flowerId/"None")
# - objectPropertyChanged(basketId, "basket_numflowers", numFlowers)
# - objectPropertyChanged(leavesId, "leaves_flying", ""True"/"False"")
# - objectPropertyChanged(ballId, "ball_bounce", "left"/"right"/"floor")
# - objectPropertyChanged(ballId, "ball_colour", "red"/"green"/"blue")
# - objectPropertyChanged(ballId, "ball_off", "") \b Note: ball is off to the right/left and removed
# - objectPropertyChanged(ballId, "ball_container", containerId)
# - objectPropertyChanged(containerId, "container_colour", colour)
# - objectPropertyChanged(containerId, "container_reward", numBalls)
#
# \section install_sec The AgentListener
#
# The following messages are published by the AgentListener (see the AgentListener.slice file for the definition of the call)
#
# - agentActionCompleted("User", "drag", [objectId])
# - agentActionStarted("User", "touch_background", [x,y])
# - agentActionCompleted("User", "touch_background", [x,y]) \b Note: complementary to action started. x,y are the starting positions 
# - agentActionStarted('User', 'cloud_rain', [cloudId]) \b Note: agentActionCompleted published with same arguments
# - agentActionCompleted("User", "bubble_pop", [bubbleId])
# - agentActionCompleted('User', 'flower_pick', [flowerId])
# - agentActionCompleted('User', 'flower_bubble', [flowerId, bubbleId])
# - agentActionCompleted('User', 'flower_ball', [flowerId, ballId])
# - agentActionCompleted('User', 'flower_placeInPot', [potId, flowerId])
# - agentActionCompleted('User', 'flower_placeInBasket', [basketId, flowerid])
# - agentActionCompleted('User', 'stack_pot', [upperPotId, lowerPotId])
# - agentActionCompleted('User', 'unstack_pot', [potId])
# - agentActionCompleted('User', 'stack_basket', [upperBasketId, lowerPotId])
# - agentActionCompleted('User', 'unstack_basket', [basketId])
# - agentActionStarted('User', 'flower_grow', [potId, flowerId, pondId])\b Note: agentActionCompleted published with same arguments
# - agentActionCompleted('User', 'touch_leaves', [leavesId])
# - agentActionCompleted("User", "bubble_merge", [bubbleId, bubbleId])
# - agentActionCompleted("None", "bubble_merge", [bubbleId, bubbleId])
# - agentActionCompleted('User', 'container_ball', [containerId, ballId])
#
# - agentActionStarted(agentId, action, details) \b Note: these are published for all agent actions
# - agentActionCompleted(agentId, action, details)
# - agentActionFailed(agentId, action, details, reason)

## @package RenderingEngineIceApp
# The main application started by Ice.
# @date 16 September 2009
# @authors Chris, Mef

from EchoesApp import *
from objects.Bubbles import *
import objects.EchoesObject
import Ice, IceGrid, IceStorm
import random
import echoes
from interface.TouchListenerImpl import *
from interface.RenderingEngineImpl import *
from OpenGL.GLUT import *
import Logger

## A class derived from Ice.Application providing the main application entry point. 
#
class RenderingEngineIceApp(Ice.Application):
    ## The \a run method for the thread started by Ice. 
    #
    # Creates all Ice interfaces needed and starts the wxApp (an EchoesApp providing the main event loop).
    def run(self, args):
        #shutdown cleanly upon a signal
        self.shutdownOnInterrupt()
        # Create an adapter so that we shut down cleanly through IceGrid
        adapter = self.communicator().createObjectAdapter("Adapter")
        adapter.activate()
        
        # Connect to IceStorm and get the topic manager
        iceStormName = self.communicator().getProperties().getProperty("IceStorm.InstanceName")
        topicManager = IceStorm.TopicManagerPrx.checkedCast(self.communicator().stringToProxy(iceStormName + "/TopicManager"))
        
        # Create or retrieve the RenderingListener topic, and get a proxy for publishing
        rlTopic = None
        try:
            rlTopic = topicManager.retrieve(echoes.RenderingListener.ice_staticId())
        except IceStorm.NoSuchTopic:
            try:
                rlTopic = topicManager.create(echoes.RenderingListener.ice_staticId())
            except IceStorm.TopicExists:
                Logger.warning( "Couldn't retrieve or create RenderingListener topic")
        if (rlTopic):
            rlPublisher = echoes.RenderingListenerPrx.uncheckedCast(rlTopic.getPublisher())
            
        # Create or retrieve the RenderingListener topic, and get a proxy for publishing
        agentTopic = None
        try:
            agentTopic = topicManager.retrieve(echoes.AgentListener.ice_staticId())
        except IceStorm.NoSuchTopic:
            try:
                agentTopic = topicManager.create(echoes.AgentListener.ice_staticId())
            except IceStorm.TopicExists:
                Logger.warning("Couldn't retrieve or create AgentListener topic")
        if (agentTopic):
            agentPublisher = echoes.AgentListenerPrx.uncheckedCast(agentTopic.getPublisher())
            
        # Create or retrieve the TouchListener topic, create a TouchListener implementation,
        # and subscribe the implementation to the topic
        tlTopic = None
        try:
            tlTopic = topicManager.retrieve(echoes.TouchListener.ice_staticId())
        except IceStorm.NoSuchTopic:
            try:
                tlTopic = topicManager.create(echoes.TouchListener.ice_staticId())
            except IceStorm.TopicExists:
                Logger.warning("Couldn't retrieve or create TouchListener topic")
                
        # Get a proxy to the vision system
#        iceGridName = Ice.Application.communicator().getProperties().getProperty("IceGrid.InstanceName")
#        queryPrx = IceGrid.QueryPrx.checkedCast(Ice.Application.communicator().stringToProxy(iceGridName + "/Query"))
#        viPrx = echoes.VisionSystemPrx.checkedCast(queryPrx.findObjectByType(echoes.VisionSystem.ice_staticId()))
#        # test the connection for debug purposes 
#        Logger.trace("info", "testing getAttentionProbability" + str(viPrx.getAttentionProbability(0,0)))

        # Initialise the GUI
        wxApp = getWXApp()
        wxApp.showWindow((1000, 600))
        wxApp.canvas.cameraPos = (0, 0, 20)
        wxApp.canvas.aspectFourByThree = Ice.Application.communicator().getProperties().getPropertyWithDefault('RenderingEngine.AspectFourByThree', 'False') == 'True'
        wxApp.canvas.rlPublisher = rlPublisher
        wxApp.canvas.agentPublisher = agentPublisher
#        wxApp.canvas.viProxy = viPrx
        wxApp.fullscreen(True)
        glutInit(args)

        # Touch listener 
        tlImpl = TouchListenerImpl(wxApp)
        tlPrx = adapter.addWithUUID(tlImpl)
        tlTopic.subscribeAndGetPublisher({}, tlPrx)
                
        # Get a proxy to listen to messages sent directly to the RenderingEngine
        iceIdentity = self.communicator().stringToIdentity(self.communicator().getProperties().getProperty("Identity"))
        reImpl = RenderingEngineImpl(wxApp, rlPublisher)
        adapter.add(reImpl, iceIdentity)

        # Start the animation
        wxApp.MainLoop()
        
        wxApp.canvas.Destroy()
        wxApp.Destroy()
        
        # We need to shut some stuff down here, the communicator below waits for ages until all threads are done otherwise...
        # this is not gracefully shutting down and it leaves some logger output unprinted...
#        wx.Exit()
        
        # If we got here, we are shutting down, so we first have to unsubscribe from the TouchListener topic
        Logger.trace("info",  "Unsubscribing ...")
        try:
            tlTopic.unsubscribe(tlPrx)
        except:
            Logger.warning("Could not unsubscribe from TouchListener")
        wx.Exit() 
        self.communicator().destroy()
        return 0

print "Running"
## A handle to the main Ice application
app = RenderingEngineIceApp()
## Starting the main thread and waiting for the result
status = app.main(sys.argv)
sys.exit(status)