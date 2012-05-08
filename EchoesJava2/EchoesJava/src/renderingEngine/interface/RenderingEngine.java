## @package interface.RenderingEngineImpl
# Implementation of the rendering engine interface
# @authors Chris, Mef
import Ice
import Logger
import echoes
import objects.Bubbles
import objects.Plants
import agents.PiavcaAvatars
import environment.Backgrounds
import environment.HelperElements
import environment.Menu
import objects.Environment
import visual.Annotator
from visual.EchoesGLCanvas import CreatePiavcaAvatar, LoadScenario, EndScenario, AddObject, SetObjectProperty, RemoveObject, StartAnnotator, StopAnnotator, AnnotatorDrawing, AnnotatorNoDrawing
import wx
import math

## Implementation of the rendering engine interface called from other modules via ICE 
#
#    
public class RenderingEngineImpl(echoes.RenderingEngine)
    ## 
    #
    public void __init__(rlPublisher)
        ## Reference to the main application.
        this.app = app
        ## The publisher to publish events to rendering engine listeners.
        this.rlPublisher = rlPublisher
        ## Counter of unique action ids.
        this.unique_actionid = 0
        ## Record of action ids currently being processed.
        this.currentActions = dict()

    ## Loading scenarios by name
    #
    # This method is asynchronous and generates a wx.Event to be processed by the GUI thread in visual.EchoesGLCanvas.EchoesGLCanvas 
    public void loadScenario_async(_cb, name, current=None)
        Logger.trace("info", "loadScenario " + name)
        
        evt = LoadScenario(name=name, callback=_cb)
        wx.PostEvent(this.app.canvas, evt)
        
    ## Ending scenarios by name
    #
    # This method is asynchronous and generates a wx.Event to be processed by the GUI thread in visual.EchoesGLCanvas.EchoesGLCanvas             
    public void endScenario_async(_cb, name, current=None)
        Logger.trace("info", "endScenario " + name)
        
        evt = EndScenario(name=name, callback=_cb)
        wx.PostEvent(this.app.canvas, evt)

    ## Set a property of the world
    # @param propName Name of the property to be set as string
    # @param propValue Value of the property as string
    #
    # Available properties are
    # - \b UserList set the user list for the intro screen (space separated string of names)
    # - \b numBubbles number of bubbles in the scene 
    # - \b DisplayScore display a score (true/false)
    # - \b SetScore set the score (integer)
    # - \b IncrementScore increment the score
    # - \b LightLevel setting the light level of the scene (0 to 1)
    # - \b Annotator show the Annotator overlay (true/false)
    # - \b AnnotatorDrawing enable drawing on the Annotator overlay (true/false)
    public void setWorldProperty(propName, propValue, current=None)
        Logger.trace("info", "setWorldProperty " + propName + " " + propValue)

        if ("Score" in propName
            bs = None
            for id, object in this.app.canvas.sceneElements.items()
                if (isinstance(object, environment.HelperElements.Score)
                    bs = object
                    break

        if ((propName == "UserList")
            this.app.canvas.userList = propValue.split(' ')

        else if ((propName == "numBubbles")
            targetNum = int(propValue)
            curBubbles = [] 
            for id,object in this.app.canvas.objects.items()
                if (isinstance(object, objects.Bubbles.EchoesBubble)
                    curBubbles.append(object)
            if ((len(curBubbles) < targetNum)
                for i in range (0, targetNum - len(curBubbles))
                    evt = AddObject(type="Bubble", callback=None)
                    wx.PostEvent(this.app.canvas, evt)
            else
                while (len(curBubbles) > targetNum)
                    curBubbles.pop().remove()

        else if (propName == "DisplayScore"
            if (propValue == "false" and bs != None
                bs.remove()
            if (propValue == "true" and bs == None
                environment.HelperElements.Score(this.app)
                
        else if (propName == "SetScore" and bs
            bs.setScore(int(propValue))

        else if (propName == "IncrementScore" and bs
            bs.increment()

        else if (propName == "LightLevel"
            this.app.canvas.targetLightLevel = float(propValue) 
                
        else if (propName == "Annotator"
            if (propValue == "true" 
                evt = StartAnnotator()
            else
                evt = StopAnnotator()    
            wx.PostEvent(this.app.canvas, evt)
        else if (propName == "AnnotatorDrawing"
            if (propValue == "true" 
                evt = AnnotatorDrawing()
            else
                evt = AnnotatorNoDrawing()    
            wx.PostEvent(this.app.canvas, evt)

    ## Add an agent with a certain pose
    #
    # This method is asynchronous and generates a wx.Event to be processed by the GUI thread in visual.EchoesGLCanvas.EchoesGLCanvas                             
    public void addAgentWithPose_async(_cb, agentType, pose, current=None)
        Logger.trace("info", "addAgentWithPose " + agentType)
        if (agentType == "Paul"
            # this needs to be done via wx.Events because the ICE callbacks reside in a different thread    
            evt = CreatePiavcaAvatar(type="Paul", autoadd=true, pose=pose, callback=_cb)
            wx.PostEvent(this.app.canvas, evt)
        else
            this.addAgent_async(_cb, agentType, current)

    ## Add an agent to the scene
    #
    # This method is asynchronous and generates a wx.Event to be processed by the GUI thread in visual.EchoesGLCanvas.EchoesGLCanvas                         
    public void addAgent_async(_cb, agentType, current=None)
        Logger.trace("info", "addAgent " + agentType)
        if (agentType == "Paul"        
            # this needs to be done via wx.Events because the ICE callbacks reside in a different thread    
            evt = CreatePiavcaAvatar(type="Paul", autoadd=true, callback=_cb)
            wx.PostEvent(this.app.canvas, evt)
        else if (agentType == "Andy"        
            # this needs to be done via wx.Events because the ICE callbacks reside in a different thread    
            evt = CreatePiavcaAvatar(type="Andy", autoadd=true, callback=_cb)
            wx.PostEvent(this.app.canvas, evt)
        else
            Logger.warning("Cannot create agent of type " + agentType)
            _cb.ice_response("")

    ## Remove an agent from the scene
    #
    public void removeAgent(agentId, current=None)
        this.app.canvas.agents[int(agentId)].remove() 

    ## Call on an agent to execute an action 
    # @param agentId the id of the agent that is called upon
    # @param action the action the agent is asked to perform as string
    # @param details details for the action as a list of strings 
    # @return Boolean whether the action has been started successfully or failed (e.g., if (certain preconditions are not fulfilled)
    #
    # The following \b actions and their corresponding details (in brackets, optional details in \e italic) are available
    # - \b LookAtPoint [x, y, z, \e "speech1.wav,speech2.wav", \e "speed=1.0", \e "hold=0.0"] \b Note All speech is optional and synchronised with gestures or movements, \e speed is a timefactor for the motion and \e hold determines how long the agent holds the gesture before moving on
    # - \b LookAtObject [objectId, \e"speech1.wav,speech2.wav", \e "speed=1.0", \e "hold=0.0"]
    # - \b LookAtChild [\e "speech1.wav,speech2.wav", \e "speed=1.0", \e "hold=0.0"]
    # - \b PointAt [objectId, \e "speed=1.0", \e "hold=0.0"]
    # - \b PointAtPoint [x, y, z, \e "speed=1.0", \e "hold=0.0"]
    # - \b TurnTo [objectId]
    # - \b TurnToPoint [x, z]
    # - \b TurnToChild []
    # - \b SetPosition[x,z]    \b Note instant relocation of the agent
    # - \b SetDepthLayer["front"/"back"]    \b Note instant relocation and scaling of the agent to appear in the back
    # - \b WalkTo [x, z]
    # - \b WalkToObject [objectId]
    # - \b ResetPosture []
    # - \b Gesture [gesture, \e "x,z", \e "speech1.wav,speech2.wav", \e "speed=1.0", \e "hold=0.0"] \b Note second argument is optional orientation after gesture       
    # - \b TouchObject [objectId, \e "WalkTo=true", \e "speech1.wav,speech2.wav"] \b Note the WalkTo flag causes the agent to walk to the object before performing the action
    # - \b PickFlower ([flowerId, \e "WalkTo=true", \e "speech1.wav,speech2.wav"]
    # - \b PutFlowerDown ["WalkTo=0-9"] \b Note 0-9 indicate vertical slots in the scene from left to right
    # - \b PutFlowerInPot [\e potId] \b Note optional object ids cause the agent to use any suitable object in the scene
    # - \b PutFlowerInBasket[\e basketId, \e "WalkTo=true"]
    # - \b TouchFlower [\e flowerId, \e "speech1.wav,speech2.wav"]
    # - \b TouchFlower-Bubble [\e flowerId, \e "speech1.wav,speech2.wav"]
    # - \b TouchFlower-Ball [\e flowerId, \e "speech1.wav,speech2.wav"]
    # - \b PickUpPot [\e potId, \e "WalkTo=true", \e "speech1.wav,speech2.wav"]
    # - \b PutPotDown [\e "WalkTo=0-9"]
    # - \b StackPot [\e potId, \e "WalkTo=true"]
    # - \b PickUpBasket [\e basketId, \e "WalkTo=true", \e "speech1.wav,speech2.wav"]
    # - \b PutBasketDown ([\e "WalkTo=0-9"]
    # - \b MakeRain [\e cloudId, \e "WalkTo=true"]
    # - \b TouchLeaves [\e leavesId]
    # - \b AttachCloud [\e cloudId]
    # - \b DetachCloud []
    # - \b PopBubble [\e bubbleId]
    # - \b Say ["speech1.wav,speech2.wav"]
    # - \b PickUpBall [\e ballId, \e "WalkTo=true", \e "speech1.wav,speech2.wav"]
    # - \b PutBallDown [\e "WalkTo=0-9"]
    # - \b ThrowBall [\e cloudId] \b Note if (cloudId is given, agent walks to the cloud and the ball is thrown through the cloud 
    # - \b PutBallIntoContainer [\e containerId, \e "WalkTo=true"]
    public void executeAction_async(_cb, agentId, action, details, current=None)
        this.unique_actionid +=1
        orig_details = details[]
        Logger.trace("info", "executeAction " + str(agentId) + " " + str(action) + " " + str(details) + " unique action id " + str(this.unique_actionid))
        try
            agent = this.app.canvas.agents[int(agentId)]
        except KeyError
            Logger.trace("Warning", "No agent with ID " + str(agentId) + " found")
            print str("Here are the available agents " + str(this.app.canvas.agents))
            agent = None    
        if (agent
            speech = None 
            speed = 1.0
            hold = 0.0
            try
                for detail in details
                    if (detail and ".wav" in detail
                        speech = detail
                        del details[details.index(detail)]
                        break
                for detail in details
                    if (detail and "speed" in detail
                        speed = float(detail.split("=")[1])
                        del details[details.index(detail)]
                        break
                for detail in details
                    if (detail and "hold" in detail
                        hold = float(detail.split("=")[1])
                        del details[details.index(detail)]
                        break
            except
                Logger.warning("executeAction was called with details which are not in a list, this might cause problems")
            actionStarted = false
            if (action == "LookAtObject"
                actionStarted = agent.lookAtObject(int(details[0]), speech=speech, speed=speed, hold=hold, action_id = this.unique_actionid)
            else if (action == "LookAtPoint"
                actionStarted = agent.lookAtPoint(float(details[0]), float(details[1]), float(details[2]), speech=speech, speed=speed, hold=hold, action_id = this.unique_actionid)
            else if (action == "LookAtChild"
                actionStarted = agent.lookAtChild(speech=speech, speed=speed, hold=hold, action_id = this.unique_actionid)
            else if (action == "PointAt"
                actionStarted = agent.pointAt(int(details[0]), speed=speed, hold=hold, action_id = this.unique_actionid)
            else if (action == "PointAtPoint"
                actionStarted = agent.pointAtPoint(float(details[0]), float(details[1]), float(details[2]), speed=speed, hold=hold, action_id = this.unique_actionid)
            else if (action == "TurnTo"
                actionStarted = agent.turnTo(int(details[0]), action_id = this.unique_actionid)
            else if (action == "TurnToPoint"
                actionStarted = agent.turnToPoint(float(details[0]), float(details[1]), action_id = this.unique_actionid)
            else if (action == "TurnToChild"
                actionStarted = agent.turnToChild(action_id = this.unique_actionid)
            else if (action == "PopBubble"
                actionStarted = agent.popBubble(int(details[0]), action_id = this.unique_actionid)
            else if (action == "WalkTo"
                actionStarted = agent.walkTo(float(details[0]), float(details[1]), action_id = this.unique_actionid)
            else if (action == "WalkToObject"
                actionStarted = agent.walkToObject(int(details[0]), action_id = this.unique_actionid)
            else if (action == "SetPosition"
                actionStarted = agent.setPosition([float(details[0]), float(details[1])], action_id = this.unique_actionid)
            else if (action == "SetDepthLayer"
                actionStarted = agent.setDepthLayer(details[0], action_id = this.unique_actionid)
            else if (action == "ResetPosture"
                actionStarted = agent.resetPosture(action_id = this.unique_actionid)
            else if (action == "Gesture"
                if (len(details) == 2
                    if (details[1] == "child"
                        actionStarted = agent.gesture(details[0], orientation=["child"], speech = speech, speed = speed, hold = hold, action_id = this.unique_actionid)
                    else
                        x, z = details[1].split(",")
                        actionStarted = agent.gesture(details[0], orientation=(float(x), float(z)), speech = speech, speed = speed, hold = hold, action_id = this.unique_actionid)
                else 
                    actionStarted = agent.gesture(details[0], speech = speech, speed = speed, hold = hold, action_id = this.unique_actionid)
            else if (action == "PickFlower"
                wT = false
                if (len(details) == 2 and details[1] == "WalkTo=true" 
                    wT = true
                if (len(details) == 0 or details[0] == None
                    actionStarted = agent.pickFlower(None, speech = speech,  action_id = this.unique_actionid, walkTo = wT)
                else
                    actionStarted = agent.pickFlower(int(details[0]), speech = speech, action_id = this.unique_actionid, walkTo = wT)                    
            else if (action == "PutFlowerDown"
                wT = -1
                if (len(details) == 1
                    wT = int(details[0].split("=")[1]) 
                actionStarted = agent.putdownFlower(action_id = this.unique_actionid, walkTo=wT)
            else if (action == "PutFlowerInPot"
                if (details != None
                    actionStarted = agent.putFlowerInPot(int(details[0]), action_id = this.unique_actionid)
                else
                    actionStarted = agent.putFlowerInPot(details, action_id = this.unique_actionid)
            else if (action == "PutFlowerInBasket"
                wt = false
                if (len(details) > 1
                    if (details[1] == "WalkTo=true" wT = true 
                if (len(details) == 0 or details[0] == None
                    actionStarted = agent.putFlowerInBasket(None, action_id = this.unique_actionid, walkTo = wT)
                else
                    actionStarted = agent.putFlowerInBasket(int(details[0]), action_id = this.unique_actionid, walkTo = wT)                    
            else if (action == "TouchObject"
                wt = false
                object_id = None
                if (len(details) > 1
                    if (details[1] == "WalkTo=true" wT = true 
                if (details[0]
                    object_id = int(details[0])    
                actionStarted = agent.touchObject(object_id, action_id = this.unique_actionid, speech = speech, walkTo=wT)
            else if (action == "TouchFlower"
                flower_id = None
                if (len(details) == 1 and details[0]
                    flower_id = int(details[0])
                actionStarted = agent.touchFlower(flower_id, action_id = this.unique_actionid, speech = speech)
            else if (action == "TouchFlower-Bubble"
                flower_id = None
                if (len(details) == 1 and details[0]
                    flower_id = int(details[0])
                actionStarted = agent.touchFlower(flower_id, target="Bubble", action_id = this.unique_actionid, speech = speech)
            else if (action == "TouchFlower-Ball"
                flower_id = None
                if (len(details) == 1 and details[0]
                    flower_id = int(details[0])
                actionStarted = agent.touchFlower(flower_id, target="Ball", action_id = this.unique_actionid, speech = speech)
            else if (action == "PickUpPot"
                wT = false
                if (len(details) == 2 and details[1] == "WalkTo=true" 
                    wT = true
                if (len(details) == 0 or details[0] == None
                    actionStarted = agent.pickupPot(None, action_id = this.unique_actionid, walkTo = wT, speech = speech)
                else
                    actionStarted = agent.pickupPot(int(details[0]), action_id = this.unique_actionid, walkTo = wT, speech = speech )
            else if (action == "PutPotDown"
                wT = -1
                if (len(details) == 1
                    wT = int(details[0].split("=")[1]) 
                actionStarted = agent.putdownPot(action_id = this.unique_actionid, walkTo=wT)
            else if (action == "StackPot"
                wT = false
                if (len(details) > 1
                    if (details[1] == "WalkTo=true" wT = true 
                if (len(details) == 0 or details[0] == None
                    actionStarted = agent.stackPot(None, action_id = this.unique_actionid, walkTo = wT)
                else
                    actionStarted = agent.stackPot(int(details[0]), action_id = this.unique_actionid, walkTo = wT)
            else if (action == "PickUpBasket"
                wT = false
                if (len(details) == 2 and details[1] == "WalkTo=true" 
                    wT = true
                if (len(details) == 0 or details[0] == None
                    actionStarted = agent.pickupBasket(None, action_id = this.unique_actionid, walkTo = wT, speech = speech)
                else
                    actionStarted = agent.pickupBasket(int(details[0]), action_id = this.unique_actionid, walkTo = wT, speech = speech )
            else if (action == "PutBasketDown"
                wT = -1
                if (len(details) == 1
                    wT = int(details[0].split("=")[1]) 
                actionStarted = agent.putdownBasket(action_id = this.unique_actionid, walkTo=wT)
            else if (action == "PickUpBall"
                wT = false
                if (len(details) == 2 and details[1] == "WalkTo=true" 
                    wT = true
                if (len(details) == 0 or details[0] == None
                    actionStarted = agent.pickupBall(None, action_id = this.unique_actionid, walkTo = wT, speech = speech)
                else
                    actionStarted = agent.pickupBall(int(details[0]), action_id = this.unique_actionid, walkTo = wT, speech = speech )
            else if (action == "PutBallDown"
                wT = -1
                if (len(details) == 1
                    wT = int(details[0].split("=")[1]) 
                actionStarted = agent.putdownBall(action_id = this.unique_actionid, walkTo=wT)
            else if (action == "PutBallIntoContainer"
                wT = false
                if (len(details) > 1
                    if (details[1] == "WalkTo=true" wT = true 
                if (len(details) == 0 or details[0] == None
                    actionStarted = agent.putBallIntoContainer(None, action_id = this.unique_actionid, walkTo = wT)
                else
                    actionStarted = agent.putBallIntoContainer(int(details[0]), action_id = this.unique_actionid, walkTo = wT)
            else if (action == "ThrowBall"
                cid = None
                if (len(details) > 0 and details[0] != None
                    cid = int(details[0]) 
                actionStarted = agent.throwBall(action_id = this.unique_actionid, cloudId = cid)
            else if (action == "MakeRain"
                wt = false
                if (len(details) > 1
                    if (details[1] == "WalkTo=true" wT = true 
                if (len(details) == 0 or details[0] == None
                    actionStarted = agent.makeRain(None, action_id = this.unique_actionid, walkTo = wT)
                else
                    actionStarted = agent.makeRain(int(details[0]), action_id = this.unique_actionid, walkTo = wT)                    
            else if (action == "TouchLeaves"
                actionStarted = agent.touchLeaves(None, action_id = this.unique_actionid)
            else if (action == "AttachCloud"
                if (details != None
                    actionStarted = agent.attachCloud(int(details[0]), action_id = this.unique_actionid)
                else
                    actionStarted = agent.attachCloud(None, action_id = this.unique_actionid)
            else if (action == "DetachCloud"
                actionStarted = agent.detachCloud(action_id = this.unique_actionid)
            else if (action == "Say"
                idx = 0
                for file in details
                    if (idx == len(details) - 1 
                        actionStarted = agent.sayPreRecorded(file, action_id = this.unique_actionid)
                    else 
                        actionStarted = agent.sayPreRecorded(file, action_id = None)
                    idx += 1
                    
            else if (action == "FacialExpression"
                actionStarted = agent.setFacialExpression(details[0], action_id = this.unique_actionid)
                
            else if (action == "Blinking"
                if (details[0] == "true"
                    actionStarted = agent.blinking(true)
                actionStarted = agent.blinking(false)

            if (actionStarted
                this.app.canvas.agentActionStarted(_cb, this.unique_actionid, agentId, action, orig_details)
                _cb.ice_response(true)
                if (action == "FacialExpression" or action == "AttachCloud" or action == "DetachCloud"
                    this.app.canvas.agentActionCompleted(this.unique_actionid, true)
            else
                # Logger.warning("Unknown action " + action)
                _cb.ice_response(false)
        else
            Logger.warning("Unknown agent " + str(agentId))
            _cb.ice_response(false)
    
    ## initiate a demo by the agent (not used)      
    public void initAgentDemo(agentId, actionName, numObjects, current=None)
        pass
    
    ## Add an object to the scene
    #
    # This method is asynchronous and generates a wx.Event to be processed by the GUI thread in visual.EchoesGLCanvas.EchoesGLCanvas                             
    public void addObject_async(_cb, objectType, current=None)
        Logger.trace("info", "addObject " + objectType)
        evt = AddObject(type=objectType, callback=_cb)
        wx.PostEvent(this.app.canvas, evt)

    ## Remove an object from the scene
    #
    # This method is asynchronous and generates a wx.Event to be processed by the GUI thread in visual.EchoesGLCanvas.EchoesGLCanvas                                     
    public void removeObject_async(_cb, objId, current=None)
        Logger.trace("info", "removeObject " + objId)
        evt = RemoveObject(objId=objId, callback=_cb)
        wx.PostEvent(this.app.canvas, evt)

    ## Set a property on a certain object
    #
    # This method is asynchronous and generates a wx.Event to be processed by the GUI thread in visual.EchoesGLCanvas.EchoesGLCanvas                             
    public void setObjectProperty(objId, propName, propValue, current=None)
        Logger.trace("info", "setObjectProperty " + str(objId) + " " + str(propName) + " " + str(propValue))
        evt = SetObjectProperty(objId=objId, propName=propName, propValue=propValue)
        wx.PostEvent(this.app.canvas, evt)
        
    ## Query the probability of the child's attention to a specific object (unused)
    public void getAttentionProbability(objectId, current=None)
        Logger.trace("info", "querying the attention probability for object" + str(objectId))
        # do something like 
        # this.app.canvas.viProxy.getAttentionProbability....
