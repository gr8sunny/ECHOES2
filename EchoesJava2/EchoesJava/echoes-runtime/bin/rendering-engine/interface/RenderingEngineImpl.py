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
class RenderingEngineImpl(echoes.RenderingEngine):
    ## Constructor
    #
    def __init__(self, app, rlPublisher):
        ## Reference to the main application.
        self.app = app
        ## The publisher to publish events to rendering engine listeners.
        self.rlPublisher = rlPublisher
        ## Counter of unique action ids.
        self.unique_actionid = 0
        ## Record of action ids currently being processed.
        self.currentActions = dict()

    ## Loading scenarios by name
    #
    # This method is asynchronous and generates a wx.Event to be processed by the GUI thread in visual.EchoesGLCanvas.EchoesGLCanvas 
    def loadScenario_async(self, _cb, name, current=None):
        Logger.trace("info", "loadScenario " + name)
        
        evt = LoadScenario(name=name, callback=_cb)
        wx.PostEvent(self.app.canvas, evt)
        
    ## Ending scenarios by name
    #
    # This method is asynchronous and generates a wx.Event to be processed by the GUI thread in visual.EchoesGLCanvas.EchoesGLCanvas             
    def endScenario_async(self, _cb, name, current=None):
        Logger.trace("info", "endScenario " + name)
        
        evt = EndScenario(name=name, callback=_cb)
        wx.PostEvent(self.app.canvas, evt)

    ## Set a property of the world
    # @param propName Name of the property to be set as string
    # @param propValue Value of the property as string
    #
    # Available properties are:
    # - \b UserList: set the user list for the intro screen (space separated string of names)
    # - \b numBubbles: number of bubbles in the scene 
    # - \b DisplayScore: display a score (True/False)
    # - \b SetScore: set the score (integer)
    # - \b IncrementScore: increment the score
    # - \b LightLevel: setting the light level of the scene (0 to 1)
    # - \b Annotator: show the Annotator overlay (True/False)
    # - \b AnnotatorDrawing: enable drawing on the Annotator overlay (True/False)
    def setWorldProperty(self, propName, propValue, current=None):
        Logger.trace("info", "setWorldProperty " + propName + " " + propValue)

        if "Score" in propName:
            bs = None
            for id, object in self.app.canvas.sceneElements.items():
                if isinstance(object, environment.HelperElements.Score):
                    bs = object
                    break

        if (propName == "UserList"):
            self.app.canvas.userList = propValue.split(' ')

        elif (propName == "numBubbles"):
            targetNum = int(propValue)
            curBubbles = [] 
            for id,object in self.app.canvas.objects.items():
                if isinstance(object, objects.Bubbles.EchoesBubble):
                    curBubbles.append(object)
            if (len(curBubbles) < targetNum):
                for i in range (0, targetNum - len(curBubbles)):
                    evt = AddObject(type="Bubble", callback=None)
                    wx.PostEvent(self.app.canvas, evt)
            else:
                while (len(curBubbles) > targetNum):
                    curBubbles.pop().remove()

        elif propName == "DisplayScore":
            if propValue == "False" and bs != None:
                bs.remove()
            if propValue == "True" and bs == None:
                environment.HelperElements.Score(self.app)
                
        elif propName == "SetScore" and bs:
            bs.setScore(int(propValue))

        elif propName == "IncrementScore" and bs:
            bs.increment()

        elif propName == "LightLevel":
            self.app.canvas.targetLightLevel = float(propValue) 
                
        elif propName == "Annotator":
            if propValue == "True": 
                evt = StartAnnotator()
            else:
                evt = StopAnnotator()    
            wx.PostEvent(self.app.canvas, evt)
        elif propName == "AnnotatorDrawing":
            if propValue == "True": 
                evt = AnnotatorDrawing()
            else:
                evt = AnnotatorNoDrawing()    
            wx.PostEvent(self.app.canvas, evt)

    ## Add an agent with a certain pose
    #
    # This method is asynchronous and generates a wx.Event to be processed by the GUI thread in visual.EchoesGLCanvas.EchoesGLCanvas                             
    def addAgentWithPose_async(self, _cb, agentType, pose, current=None):
        Logger.trace("info", "addAgentWithPose " + agentType)
        if agentType == "Paul":
            # this needs to be done via wx.Events because the ICE callbacks reside in a different thread    
            evt = CreatePiavcaAvatar(type="Paul", autoadd=True, pose=pose, callback=_cb)
            wx.PostEvent(self.app.canvas, evt)
        else:
            self.addAgent_async(_cb, agentType, current)

    ## Add an agent to the scene
    #
    # This method is asynchronous and generates a wx.Event to be processed by the GUI thread in visual.EchoesGLCanvas.EchoesGLCanvas                         
    def addAgent_async(self, _cb, agentType, current=None):
        Logger.trace("info", "addAgent " + agentType)
        if agentType == "Paul":        
            # this needs to be done via wx.Events because the ICE callbacks reside in a different thread    
            evt = CreatePiavcaAvatar(type="Paul", autoadd=True, callback=_cb)
            wx.PostEvent(self.app.canvas, evt)
        elif agentType == "Andy":        
            # this needs to be done via wx.Events because the ICE callbacks reside in a different thread    
            evt = CreatePiavcaAvatar(type="Andy", autoadd=True, callback=_cb)
            wx.PostEvent(self.app.canvas, evt)
        else:
            Logger.warning("Cannot create agent of type " + agentType)
            _cb.ice_response("")

    ## Remove an agent from the scene
    #
    def removeAgent(self, agentId, current=None):
        self.app.canvas.agents[int(agentId)].remove() 

    ## Call on an agent to execute an action 
    # @param agentId the id of the agent that is called upon
    # @param action the action the agent is asked to perform as string
    # @param details details for the action as a list of strings 
    # @return Boolean whether the action has been started successfully or failed (e.g., if certain preconditions are not fulfilled)
    #
    # The following \b actions and their corresponding details (in brackets, optional details in \e italic) are available:
    # - \b LookAtPoint [x, y, z, \e "speech1.wav,speech2.wav", \e "speed=1.0", \e "hold=0.0"] \b Note: All speech is optional and synchronised with gestures or movements, \e speed is a timefactor for the motion and \e hold determines how long the agent holds the gesture before moving on
    # - \b LookAtObject [objectId, \e"speech1.wav,speech2.wav", \e "speed=1.0", \e "hold=0.0"]
    # - \b LookAtChild [\e "speech1.wav,speech2.wav", \e "speed=1.0", \e "hold=0.0"]
    # - \b PointAt [objectId, \e "speed=1.0", \e "hold=0.0"]
    # - \b PointAtPoint [x, y, z, \e "speed=1.0", \e "hold=0.0"]
    # - \b TurnTo [objectId]
    # - \b TurnToPoint [x, z]
    # - \b TurnToChild []
    # - \b SetPosition[x,z]    \b Note: instant relocation of the agent
    # - \b SetDepthLayer["front"/"back"]    \b Note: instant relocation and scaling of the agent to appear in the back
    # - \b WalkTo [x, z]
    # - \b WalkToObject [objectId]
    # - \b ResetPosture []
    # - \b Gesture [gesture, \e "x,z", \e "speech1.wav,speech2.wav", \e "speed=1.0", \e "hold=0.0"] \b Note: second argument is optional orientation after gesture       
    # - \b TouchObject [objectId, \e "WalkTo=True", \e "speech1.wav,speech2.wav"] \b Note: the WalkTo flag causes the agent to walk to the object before performing the action
    # - \b PickFlower ([flowerId, \e "WalkTo=True", \e "speech1.wav,speech2.wav"]
    # - \b PutFlowerDown ["WalkTo=0-9"] \b Note: 0-9 indicate vertical slots in the scene from left to right
    # - \b PutFlowerInPot [\e potId] \b Note: optional object ids cause the agent to use any suitable object in the scene
    # - \b PutFlowerInBasket[\e basketId, \e "WalkTo=True"]
    # - \b TouchFlower [\e flowerId, \e "speech1.wav,speech2.wav"]
    # - \b TouchFlower-Bubble [\e flowerId, \e "speech1.wav,speech2.wav"]
    # - \b TouchFlower-Ball [\e flowerId, \e "speech1.wav,speech2.wav"]
    # - \b PickUpPot [\e potId, \e "WalkTo=True", \e "speech1.wav,speech2.wav"]
    # - \b PutPotDown [\e "WalkTo=0-9"]
    # - \b StackPot [\e potId, \e "WalkTo=True"]
    # - \b PickUpBasket [\e basketId, \e "WalkTo=True", \e "speech1.wav,speech2.wav"]
    # - \b PutBasketDown ([\e "WalkTo=0-9"]
    # - \b MakeRain [\e cloudId, \e "WalkTo=True"]
    # - \b TouchLeaves [\e leavesId]
    # - \b AttachCloud [\e cloudId]
    # - \b DetachCloud []
    # - \b PopBubble [\e bubbleId]
    # - \b Say ["speech1.wav,speech2.wav"]
    # - \b PickUpBall [\e ballId, \e "WalkTo=True", \e "speech1.wav,speech2.wav"]
    # - \b PutBallDown [\e "WalkTo=0-9"]
    # - \b ThrowBall [\e cloudId] \b Note: if cloudId is given, agent walks to the cloud and the ball is thrown through the cloud 
    # - \b PutBallIntoContainer [\e containerId, \e "WalkTo=True"]
    def executeAction_async(self, _cb, agentId, action, details, current=None):
        self.unique_actionid +=1
        orig_details = details[:]
        Logger.trace("info", "executeAction " + str(agentId) + " " + str(action) + " " + str(details) + " unique action id: " + str(self.unique_actionid))
        try:
            agent = self.app.canvas.agents[int(agentId)]
        except KeyError:
            Logger.trace("Warning", "No agent with ID " + str(agentId) + " found")
            print str("Here are the available agents: " + str(self.app.canvas.agents))
            agent = None    
        if agent:
            speech = None 
            speed = 1.0
            hold = 0.0
            try:
                for detail in details:
                    if detail and ".wav" in detail:
                        speech = detail
                        del details[details.index(detail)]
                        break
                for detail in details:
                    if detail and "speed" in detail:
                        speed = float(detail.split("=")[1])
                        del details[details.index(detail)]
                        break
                for detail in details:
                    if detail and "hold" in detail:
                        hold = float(detail.split("=")[1])
                        del details[details.index(detail)]
                        break
            except:
                Logger.warning("executeAction was called with details which are not in a list, this might cause problems")
            actionStarted = False
            if action == "LookAtObject":
                actionStarted = agent.lookAtObject(int(details[0]), speech=speech, speed=speed, hold=hold, action_id = self.unique_actionid)
            elif action == "LookAtPoint":
                actionStarted = agent.lookAtPoint(float(details[0]), float(details[1]), float(details[2]), speech=speech, speed=speed, hold=hold, action_id = self.unique_actionid)
            elif action == "LookAtChild":
                actionStarted = agent.lookAtChild(speech=speech, speed=speed, hold=hold, action_id = self.unique_actionid)
            elif action == "PointAt":
                actionStarted = agent.pointAt(int(details[0]), speed=speed, hold=hold, action_id = self.unique_actionid)
            elif action == "PointAtPoint":
                actionStarted = agent.pointAtPoint(float(details[0]), float(details[1]), float(details[2]), speed=speed, hold=hold, action_id = self.unique_actionid)
            elif action == "TurnTo":
                actionStarted = agent.turnTo(int(details[0]), action_id = self.unique_actionid)
            elif action == "TurnToPoint":
                actionStarted = agent.turnToPoint(float(details[0]), float(details[1]), action_id = self.unique_actionid)
            elif action == "TurnToChild":
                actionStarted = agent.turnToChild(action_id = self.unique_actionid)
            elif action == "PopBubble":
                actionStarted = agent.popBubble(int(details[0]), action_id = self.unique_actionid)
            elif action == "WalkTo":
                actionStarted = agent.walkTo(float(details[0]), float(details[1]), action_id = self.unique_actionid)
            elif action == "WalkToObject":
                actionStarted = agent.walkToObject(int(details[0]), action_id = self.unique_actionid)
            elif action == "SetPosition":
                actionStarted = agent.setPosition([float(details[0]), float(details[1])], action_id = self.unique_actionid)
            elif action == "SetDepthLayer":
                actionStarted = agent.setDepthLayer(details[0], action_id = self.unique_actionid)
            elif action == "ResetPosture":
                actionStarted = agent.resetPosture(action_id = self.unique_actionid)
            elif action == "Gesture":
                if len(details) == 2:
                    if details[1] == "child":
                        actionStarted = agent.gesture(details[0], orientation=["child"], speech = speech, speed = speed, hold = hold, action_id = self.unique_actionid)
                    else:
                        x, z = details[1].split(",")
                        actionStarted = agent.gesture(details[0], orientation=(float(x), float(z)), speech = speech, speed = speed, hold = hold, action_id = self.unique_actionid)
                else: 
                    actionStarted = agent.gesture(details[0], speech = speech, speed = speed, hold = hold, action_id = self.unique_actionid)
            elif action == "PickFlower":
                wT = False
                if len(details) == 2 and details[1] == "WalkTo=True": 
                    wT = True
                if len(details) == 0 or details[0] == None:
                    actionStarted = agent.pickFlower(None, speech = speech,  action_id = self.unique_actionid, walkTo = wT)
                else:
                    actionStarted = agent.pickFlower(int(details[0]), speech = speech, action_id = self.unique_actionid, walkTo = wT)                    
            elif action == "PutFlowerDown":
                wT = -1
                if len(details) == 1:
                    wT = int(details[0].split("=")[1]) 
                actionStarted = agent.putdownFlower(action_id = self.unique_actionid, walkTo=wT)
            elif action == "PutFlowerInPot":
                if details != None:
                    actionStarted = agent.putFlowerInPot(int(details[0]), action_id = self.unique_actionid)
                else:
                    actionStarted = agent.putFlowerInPot(details, action_id = self.unique_actionid)
            elif action == "PutFlowerInBasket":
                wt = False
                if len(details) > 1:
                    if details[1] == "WalkTo=True": wT = True 
                if len(details) == 0 or details[0] == None:
                    actionStarted = agent.putFlowerInBasket(None, action_id = self.unique_actionid, walkTo = wT)
                else:
                    actionStarted = agent.putFlowerInBasket(int(details[0]), action_id = self.unique_actionid, walkTo = wT)                    
            elif action == "TouchObject":
                wt = False
                object_id = None
                if len(details) > 1:
                    if details[1] == "WalkTo=True": wT = True 
                if details[0]:
                    object_id = int(details[0])    
                actionStarted = agent.touchObject(object_id, action_id = self.unique_actionid, speech = speech, walkTo=wT)
            elif action == "TouchFlower":
                flower_id = None
                if len(details) == 1 and details[0]:
                    flower_id = int(details[0])
                actionStarted = agent.touchFlower(flower_id, action_id = self.unique_actionid, speech = speech)
            elif action == "TouchFlower-Bubble":
                flower_id = None
                if len(details) == 1 and details[0]:
                    flower_id = int(details[0])
                actionStarted = agent.touchFlower(flower_id, target="Bubble", action_id = self.unique_actionid, speech = speech)
            elif action == "TouchFlower-Ball":
                flower_id = None
                if len(details) == 1 and details[0]:
                    flower_id = int(details[0])
                actionStarted = agent.touchFlower(flower_id, target="Ball", action_id = self.unique_actionid, speech = speech)
            elif action == "PickUpPot":
                wT = False
                if len(details) == 2 and details[1] == "WalkTo=True": 
                    wT = True
                if len(details) == 0 or details[0] == None:
                    actionStarted = agent.pickupPot(None, action_id = self.unique_actionid, walkTo = wT, speech = speech)
                else:
                    actionStarted = agent.pickupPot(int(details[0]), action_id = self.unique_actionid, walkTo = wT, speech = speech )
            elif action == "PutPotDown":
                wT = -1
                if len(details) == 1:
                    wT = int(details[0].split("=")[1]) 
                actionStarted = agent.putdownPot(action_id = self.unique_actionid, walkTo=wT)
            elif action == "StackPot":
                wT = False
                if len(details) > 1:
                    if details[1] == "WalkTo=True": wT = True 
                if len(details) == 0 or details[0] == None:
                    actionStarted = agent.stackPot(None, action_id = self.unique_actionid, walkTo = wT)
                else:
                    actionStarted = agent.stackPot(int(details[0]), action_id = self.unique_actionid, walkTo = wT)
            elif action == "PickUpBasket":
                wT = False
                if len(details) == 2 and details[1] == "WalkTo=True": 
                    wT = True
                if len(details) == 0 or details[0] == None:
                    actionStarted = agent.pickupBasket(None, action_id = self.unique_actionid, walkTo = wT, speech = speech)
                else:
                    actionStarted = agent.pickupBasket(int(details[0]), action_id = self.unique_actionid, walkTo = wT, speech = speech )
            elif action == "PutBasketDown":
                wT = -1
                if len(details) == 1:
                    wT = int(details[0].split("=")[1]) 
                actionStarted = agent.putdownBasket(action_id = self.unique_actionid, walkTo=wT)
            elif action == "PickUpBall":
                wT = False
                if len(details) == 2 and details[1] == "WalkTo=True": 
                    wT = True
                if len(details) == 0 or details[0] == None:
                    actionStarted = agent.pickupBall(None, action_id = self.unique_actionid, walkTo = wT, speech = speech)
                else:
                    actionStarted = agent.pickupBall(int(details[0]), action_id = self.unique_actionid, walkTo = wT, speech = speech )
            elif action == "PutBallDown":
                wT = -1
                if len(details) == 1:
                    wT = int(details[0].split("=")[1]) 
                actionStarted = agent.putdownBall(action_id = self.unique_actionid, walkTo=wT)
            elif action == "PutBallIntoContainer":
                wT = False
                if len(details) > 1:
                    if details[1] == "WalkTo=True": wT = True 
                if len(details) == 0 or details[0] == None:
                    actionStarted = agent.putBallIntoContainer(None, action_id = self.unique_actionid, walkTo = wT)
                else:
                    actionStarted = agent.putBallIntoContainer(int(details[0]), action_id = self.unique_actionid, walkTo = wT)
            elif action == "ThrowBall":
                cid = None
                if len(details) > 0 and details[0] != None:
                    cid = int(details[0]) 
                actionStarted = agent.throwBall(action_id = self.unique_actionid, cloudId = cid)
            elif action == "MakeRain":
                wt = False
                if len(details) > 1:
                    if details[1] == "WalkTo=True": wT = True 
                if len(details) == 0 or details[0] == None:
                    actionStarted = agent.makeRain(None, action_id = self.unique_actionid, walkTo = wT)
                else:
                    actionStarted = agent.makeRain(int(details[0]), action_id = self.unique_actionid, walkTo = wT)                    
            elif action == "TouchLeaves":
                actionStarted = agent.touchLeaves(None, action_id = self.unique_actionid)
            elif action == "AttachCloud":
                if details != None:
                    actionStarted = agent.attachCloud(int(details[0]), action_id = self.unique_actionid)
                else:
                    actionStarted = agent.attachCloud(None, action_id = self.unique_actionid)
            elif action == "DetachCloud":
                actionStarted = agent.detachCloud(action_id = self.unique_actionid)
            elif action == "Say":
                idx = 0
                for file in details:
                    if idx == len(details) - 1: 
                        actionStarted = agent.sayPreRecorded(file, action_id = self.unique_actionid)
                    else: 
                        actionStarted = agent.sayPreRecorded(file, action_id = None)
                    idx += 1
                    
            elif action == "FacialExpression":
                actionStarted = agent.setFacialExpression(details[0], action_id = self.unique_actionid)
                
            elif action == "Blinking":
                if details[0] == "True":
                    actionStarted = agent.blinking(True)
                actionStarted = agent.blinking(False)

            if actionStarted:
                self.app.canvas.agentActionStarted(_cb, self.unique_actionid, agentId, action, orig_details)
                _cb.ice_response(True)
                if action == "FacialExpression" or action == "AttachCloud" or action == "DetachCloud":
                    self.app.canvas.agentActionCompleted(self.unique_actionid, True)
            else:
                # Logger.warning("Unknown action: " + action)
                _cb.ice_response(False)
        else:
            Logger.warning("Unknown agent: " + str(agentId))
            _cb.ice_response(False)
    
    ## initiate a demo by the agent (not used)      
    def initAgentDemo(self, agentId, actionName, numObjects, current=None):
        pass
    
    ## Add an object to the scene
    #
    # This method is asynchronous and generates a wx.Event to be processed by the GUI thread in visual.EchoesGLCanvas.EchoesGLCanvas                             
    def addObject_async(self, _cb, objectType, current=None):
        Logger.trace("info", "addObject " + objectType)
        evt = AddObject(type=objectType, callback=_cb)
        wx.PostEvent(self.app.canvas, evt)

    ## Remove an object from the scene
    #
    # This method is asynchronous and generates a wx.Event to be processed by the GUI thread in visual.EchoesGLCanvas.EchoesGLCanvas                                     
    def removeObject_async(self, _cb, objId, current=None):
        Logger.trace("info", "removeObject " + objId)
        evt = RemoveObject(objId=objId, callback=_cb)
        wx.PostEvent(self.app.canvas, evt)

    ## Set a property on a certain object
    #
    # This method is asynchronous and generates a wx.Event to be processed by the GUI thread in visual.EchoesGLCanvas.EchoesGLCanvas                             
    def setObjectProperty(self, objId, propName, propValue, current=None):
        Logger.trace("info", "setObjectProperty " + str(objId) + " " + str(propName) + " " + str(propValue))
        evt = SetObjectProperty(objId=objId, propName=propName, propValue=propValue)
        wx.PostEvent(self.app.canvas, evt)
        
    ## Query the probability of the child's attention to a specific object (unused)
    def getAttentionProbability(self, objectId, current=None):
        Logger.trace("info", "querying the attention probability for object" + str(objectId))
        # do something like 
        # self.app.canvas.viProxy.getAttentionProbability....