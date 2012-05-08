'''
Created on 9 Dec, 2009

@author: cfabric
'''

from EchoesAgent import *
from OpenGL.GL import *
from OpenGL.GLU import *
from OpenGL.GLE import *
from OpenGL.GLUT import *
import random, math, time
import echoes
import sound.EchoesAudio
import Piavca
import Piavca.JointNames
from collections import deque
Piavca.JointNames.loadDefaults()
from Ice import Application
import objects.Plants
import objects.Environment
import Logger
import threading

class Bill(EchoesAgent):
    '''
    classdocs
    '''
    def __init__(self, app, autoAdd=True, props={"type": "Bill"}):
        '''
        Constructor
        '''
        super(Bill, self).__init__(app, autoAdd, props)
        self.avatar = Piavca.Avatar("agents/Bill/bill")
        
        self.collisionTest = False


    def render(self):
        ''' 
        will be rendered in Piavca Core loop
        '''
        pass
    
    def remove(self):
        super(Bill, self).remove()


class EchoesAvatar(EchoesAgent):
    '''
    classdocs
    '''
    def __init__(self, app, avatar="agents/Paul/Paul", autoAdd=True, props={"type": "Paul"}, scale=0.0275, callback=None):
        '''
        Constructor
        '''
        super(EchoesAvatar, self).__init__(app, autoAdd, props)
        
        self.collisionTest = True
        self.showBoundary = False
        self.publishVisibility = True
        self.visible = False

        self.app.canvas.renderPiavca = False #dont render Paul until I have positioned him at the start
        self.avatar = Piavca.Avatar(avatar)
        self.repositioner = Piavca.Reposition()
        self.repositioner.setRotateAboutUp(False)
        self.repositioner.setMaintainUp(True) # if False it locks the up position (presumably from the motion)
        self.repositioner.setUpDirection(Piavca.Vec(0,1,0))        
        
        self.jointBaseOrientations = dict()
        self.jointIdByName = dict()
        jid = self.avatar.begin()
        while jid != self.avatar.end():
            self.jointBaseOrientations[jid] = self.avatar.getJointOrientation(jid)
            self.jointIdByName[self.avatar.getJointName(jid)] = jid
            jid = self.avatar.next(jid)

        # those have to be listed in the avatars cfg file in this order
        expressionNames = ["Happy", "Sad", "Laugh", "OpenMouth", "ClosedEyes", "Blink", "Grin", "Aggressive"]
        self.facialExpressions = dict()
        self.expressionTargets = dict()  
        i = 0
        fid = self.avatar.beginExpression()
        # there is a bug in nextExpression that never produces the last number...
        while fid != self.avatar.endExpression() and fid != 10000 and i < 15:
            try:
                name = expressionNames[i]
            except:
                name = "Unknown" + str(i)
            self.facialExpressions[name] = fid
            self.expressionTargets[name] = 0.0
            fid = self.avatar.nextExpression(fid)
            i += 1
             
        # Current value motion to receive updates on facial expressions
        self.facialExpMotion = Piavca.CurrentValueMotion()

        self.playing = False # pre-recorded motions playing
        self.animating = False # manual animation
        self.speaking = False # using speech
        self.blinkingTimer = None # blinking
        
        # callback to determine end of motions and manually animate joint movements
        self.tcb = PiavcaTimeCallback(str(id(self)), self)
        self.avatar.registerCallback (self.tcb)
        
        # stores joints and target orientations for manual animation
        self.animationTargets = dict()  

        # queues for smoothly stacking up motions, animations and speech
        self.motionQueue = deque ()
        self.animationQueue = deque ()
        self.speechQueue = deque ()
        self.cancelMotions = 0
        
        self.scale = scale
        self.forwardOrientation = Piavca.Quat(-math.pi/2, Piavca.Vec.XAxis()) * Piavca.Quat(-math.pi/2, Piavca.Vec.ZAxis())
        self.zOffset = 3.0
        
        w = Piavca.getMotion("walking")
        self.walking = Piavca.SubMotion(w, 0.0, 4.2)
        self.startstep = Piavca.SubMotion(w, 0.0, 0.5)
        self.step = Piavca.SubMotion(w, 3.2, 4.2)
        self.walkingDistance = self.getPlaneMotionDistance(self.walking, self.walking.getStartTime(), self.walking.getEndTime()) 
        self.stepDistance = self.getPlaneMotionDistance(self.step, self.walking.getStartTime(), self.walking.getEndTime())
        self.relaxPosture = Piavca.MotionPosture(w)

        self.pos = (0,0,0)
        self.startPostion()
        self.floorheight = None
        self.blinking(True)

        self.app.canvas.rlPublisher.agentAdded(str(self.id), dict())
        if callback:
            callback.ice_response(str(self.id))
            
        self.app.canvas.renderPiavca = True

    def __setattr__(self, item, value):
        object.__setattr__(self, item, value)
        if item == "scale":
            self.avatar.setScale(value)
        if item == "pos":
            self.repositioner.setStartPosition(Piavca.Vec(value[0]/self.scale,value[1]/self.scale,(value[2]-self.zOffset)/self.scale))
        if item == "orientation" and hasattr(self, "forwardOrientation"):
            self.repositioner.setStartOrientation(self.forwardOrientation * value)
        
    def playSmoothAtPos(self, motion, t1=1, t2=0, window=1):
        self.repositioner.setMotion(Piavca.OverrideMotion(self.facialExpMotion, motion))
        posture = Piavca.AvatarPosture() 
        posture.getPostureFromAvatar(self.avatar)
        m = Piavca.MotionTransition(posture, self.repositioner, t1, t2, window)
        self.avatar.playMotionDirect(m)
        self.playing = True

    def playDirectAtPos(self, motion):
        self.repositioner.setMotion(Piavca.OverrideMotion(self.facialExpMotion, motion))
        self.avatar.playMotionDirect(self.repositioner)
        self.playing = True
            
    def updatePos(self, adjustY=True):
        rp = self.avatar.getRootPosition() * self.scale
        if adjustY:
            if not self.floorheight:
                self.floorheight = (self.avatar.getJointBasePosition(self.jointIdByName["Bip01 L Foot"], Piavca.WORLD_COORD) * self.scale)[1]
            currentFootHeight = (self.avatar.getJointBasePosition(self.jointIdByName["Bip01 L Foot"], Piavca.WORLD_COORD) * self.scale)[1]
            dy = self.floorheight - currentFootHeight
    #        print self.floorheight, currentFootHeight, "adjusting by", dy
            self.pos = [rp.X(), rp.Y() + dy, rp.Z() + self.zOffset] # make the avatar re-adjust its y position at the next motion
        else: 
            self.pos = [rp.X(), rp.Y(), rp.Z() + self.zOffset]
        self.orientation = self.forwardOrientation.inverse() * self.avatar.getRootOrientation()

    def startPostion(self):
        self.orientation = Piavca.Quat(0, Piavca.Vec.ZAxis())
        self.setPosition((-6,-0.5,-5))
        
    def setPosition(self, pos, action_id = -1):
        if len(pos) < 3: # assuming that it is xz, if y is not given (avatar remains at original height)
            pos = (pos[0], self.pos[1], pos[1])
        qi = QueueItem(self.relaxPosture, isFinal=True, action_id=action_id)
        qi.preCall = ("setPosition", pos)
        qi.playDirect = True
        self.motionQueue.append(qi)
        
    def setDepthLayer(self, layer="front", action_id = -1):
        pass
                
    def isMoving(self):
        m = self.repositioner.getMotion()
        return not m.finished()

    def walkTo(self, x, z, turnTo=None, action_id = -1):
        r = self.relaxPosture
        wi = QueueItem(None) # the motion is set up just before he actually needs to walk
        if turnTo:
            wi.callback = ("turnTo", turnTo)
        wi.preCall = ("walkTo", [x,z,wi,r,action_id])
        self.motionQueue.append(wi) 
        self.motionQueue.append(QueueItem(r, isFinal=True, action_id=action_id))
        return True 
            
    def walkToObject(self, id, distance = 1.5, action_id = -1):
        try:
            o = self.app.canvas.objects[int(id)]
        except:
            Logger.warning("Avatar: no valid object to go to")
            return False
        if self.isAt(id, distance): 
            # make sure actionCompleted is called shortly after this albeit not moving
            a = ActionTimer(0.2, self, action_id) 
            a.start()
            return True
        r = self.relaxPosture
        wi = QueueItem(None) # the motion is set up just before he actually needs to walk
        wi.preCall = ("walkTo", [o,distance,wi,r,action_id])
        self.motionQueue.append(wi) 
        self.motionQueue.append(QueueItem(r, isFinal=True, action_id=action_id))
        return True 
        
    def getDistance(self, id):
        try:
            o = self.app.canvas.objects[int(id)]
        except:
            Logger.warning("No object " + str(id) + " for distance computation")
            return 1000
        d = math.hypot(self.pos[0]-o.pos[0], self.pos[2]-o.pos[2])
        return abs(d)
    
    def isAt(self, id, distance = 1.5):
        try:
            o = self.app.canvas.objects[int(id)]
        except:
            Logger.warning("No object " + str(id) + " to check whether agent is at the object")
            return False
        turn = math.atan2(o.pos[0]-self.pos[0], o.pos[2]-self.pos[2])
        at = self.getDistance(id) <= distance + 0.3 and self.getDistance(id) >= distance - 0.3 and abs(turn+self.orientation.Zangle()) < 0.2
#        if not at:
#            Logger.trace("info", "Agent not at object " + str(id) + 
#                         " distance: " + str(self.getDistance(id)) + 
#                         " angle: " + str(math.degrees(turn+self.orientation.Zangle())))
        return at

    def isNear(self, id, distance = 1.7):
        try:
            o = self.app.canvas.objects[int(id)]
        except:
            Logger.warning("No object " + str(id) + " to check whether agent is at the object")
            return False
        return self.getDistance(id) <= distance

    def findMotionEndtime(self, motion, distance):
        motion.setStartTime(0.0)
        dt = time = motion.getMotionLength()
        d = self.getPlaneMotionDistance(motion, 0, time)
        if d < distance:
            return 0
        while (math.fabs(d-distance) > 0.1):
            dt = dt/2
            if d > distance: time -= dt 
            else: time += dt
            d = self.getPlaneMotionDistance(motion, 0, time)
        return time
    
    def getPlaneMotionDistance(self, motion, startTime, endTime):
        vec = motion.getVecValueAtTime(0, endTime) - motion.getVecValueAtTime(0, startTime)
        return math.hypot(vec.X(), vec.Y())*self.scale

    def turnTowardsDirect(self, x, z, relaxAfter = True, intermediate=False, action_id = -1):
        turn = math.atan2(x-self.pos[0], z-self.pos[2])
        orientation = Piavca.Quat(turn, Piavca.Vec.ZAxis())
        self.orientation = orientation
        if relaxAfter:
            self.motionQueue.append(QueueItem(self.startstep)) 
            self.motionQueue.append(QueueItem(self.relaxPosture, isFinal= not intermediate, action_id=action_id)) 

    def lookAtObject(self, targetId, speech=None, speed=1.0, hold=0.0, action_id = -1):
        if targetId in self.app.canvas.objects:
            target = self.app.canvas.objects[targetId]
            mh = Piavca.PointAt(self.jointIdByName['Bip01 Head'], Piavca.Vec())
            mh.setForwardDirection(Piavca.Vec.YAxis())
            mh.setLocal(True)
            mask = Piavca.MotionMask()
            mask.setAllMask(False)
            mask.setMask(self.jointIdByName['Bip01 Head'], True)
            masked_mh = Piavca.MaskedMotion(mh, mask)
            qi = QueueItem(Piavca.ScaleMotionSpeed(masked_mh, speed), isFinal=True, action_id=action_id)
            qi.speech = speech
            qi.preCall = ("lookatTarget", (mh, target, action_id))
            if hold > 0.0:
                qi.callback = ("hold_posture", [hold, qi])
            tqi = QueueItem(None, isFinal=False)
            tqi.preCall=("turn_towardsTarget", [tqi, target])
            self.motionQueue.append(tqi)
            self.motionQueue.append(qi)
            return True
        else:
            Logger.warning( "Avatar.lookAt: no such object id in scene - " + str(targetId))
            return False
        
    def lookAtChild(self, speech=None, speed=1.0, hold=0.0, action_id = -1):
        target = "child"
        mh = Piavca.PointAt(self.jointIdByName['Bip01 Head'], Piavca.Vec())
        mh.setForwardDirection(Piavca.Vec.YAxis())
        mh.setLocal(True)
        mask = Piavca.MotionMask()
        mask.setAllMask(False)
        mask.setMask(self.jointIdByName['Bip01 Head'], True)
        masked_mh = Piavca.MaskedMotion(mh, mask)
        qi = QueueItem(Piavca.ScaleMotionSpeed(masked_mh, speed), isFinal=True, action_id=action_id)
        qi.preCall = ("lookatTarget", (mh, target, action_id))
        if hold > 0.0:
            qi.callback = ("hold_posture", [hold, qi])        
        qi.speech = speech
        tqi = QueueItem(None, isFinal=False)
        tqi.preCall=("turn_towardsTarget", [tqi, target])
        self.motionQueue.append(tqi)
        self.motionQueue.append(qi)
        return True

    def lookAtPoint(self, x, y, z, speech=None, speed=1.0, hold=0.0, action_id = -1, intermediate=False):
        target = Piavca.Vec(x, y, z)
        mh = Piavca.PointAt(self.jointIdByName['Bip01 Head'], Piavca.Vec())
        mh.setForwardDirection(Piavca.Vec.YAxis())
        mh.setLocal(True)
        mask = Piavca.MotionMask()
        mask.setAllMask(False)
        mask.setMask(self.jointIdByName['Bip01 Head'], True)
        masked_mh = Piavca.MaskedMotion(mh, mask)
        qi = QueueItem(Piavca.ScaleMotionSpeed(masked_mh, speed), isFinal=not intermediate, action_id=action_id)
        qi.preCall = ("lookatTarget", (mh, target, action_id))
        if hold > 0.0:
            qi.callback = ("hold_posture", [hold, qi])        
        qi.speech = speech        
        tqi = QueueItem(None, isFinal=False)
        tqi.preCall=("turn_towardsTarget", [tqi, target])
        self.motionQueue.append(tqi)
        self.motionQueue.append(qi)
        return True
        
    def turnToChild(self, action_id = -1):
        qi = QueueItem(self.relaxPosture, isFinal=True, preCall = ("turnTo", ["child", action_id])) # the motion is set up just before he actually needs to walk
        qi.action_id = action_id
        self.motionQueue.append(qi) 
        return True 

    def turnTo(self, targetId, action_id = -1):
        target = None
        if targetId in self.app.canvas.objects:
            target = self.app.canvas.objects[targetId]
        else:
            Logger.warning( "TurnTo: no such object id in scene - " + str(targetId))
            return False
        qi = QueueItem(self.relaxPosture, isFinal=True, preCall = ("turnTo", [target, action_id])) # the motion is set up just before he actually needs to walk
        qi.action_id = action_id
        self.motionQueue.append(qi) 
        return True 
    
    def turnToPoint(self, x, z, action_id = -1):
        qi = QueueItem(self.relaxPosture, isFinal=True, preCall = ("turnTo", [x,z, action_id])) # the motion is set up just before he actually needs to walk
        qi.action_id = action_id
        self.motionQueue.append(qi) 
        return True 

    def popBubble(self, targetId, action_id = 1):
        return self.pointAt(targetId, action_id=action_id, postAction="popBubble")

    def pointAt(self, targetId, hand="right", action_id = -1, speed=1.0, hold=0.0, postAction=None):
        if targetId in self.app.canvas.objects:
            target = self.app.canvas.objects[targetId]
        else:
            Logger.warning( "pointAt: no such object id in scene - " + str(targetId))
            return False
        if hand=="right":
            jid = self.jointIdByName['Bip01 R UpperArm']
        else:
            jid = self.jointIdByName['Bip01 L UpperArm']
        mh = Piavca.PointAt(jid, Piavca.Vec(0,0,0))
        mh.setForwardDirection(Piavca.Vec.XAxis())
        mh.setLocal(False)
        mask = Piavca.MotionMask()
        mask.setAllMask(False)
        mask.setMask(jid, True)
        masked_mh = Piavca.MaskedMotion(mh, mask)        
        qi = QueueItem(Piavca.ScaleMotionSpeed(masked_mh, speed), isFinal=True, action_id=action_id)
        qi.preCall = ("pointatTarget", (mh, target, action_id))
        if postAction:
            qi.callback = (postAction, [target, action_id])
        elif hold > 0.0:
            qi.callback = ("hold_posture", [hold, qi])
        tqi = QueueItem(None, isFinal=False)
        tqi.preCall=("turn_towardsTarget", [tqi, target])
        self.motionQueue.append(tqi)
        self.motionQueue.append(qi)
        return True
        
    def pointAtPoint(self, x, y, z, hand="right", speed=1.0, hold=0.0, action_id = -1):          
        if hand=="right":
            jid = self.jointIdByName['Bip01 R UpperArm']
        else:
            jid = self.jointIdByName['Bip01 L UpperArm']
        target = Piavca.Vec(x, y, z)
        mh = Piavca.PointAt(jid, target)
        mh.setForwardDirection(Piavca.Vec.XAxis())
        mh.setLocal(False)
        mask = Piavca.MotionMask()
        mask.setAllMask(False)
        mask.setMask(jid, True)
        masked_mh = Piavca.MaskedMotion(mh, mask)                
        qi = QueueItem(Piavca.ScaleMotionSpeed(masked_mh, speed), isFinal=True, action_id=action_id)
        qi.preCall = ("pointatTarget", (mh, target))
        if hold > 0.0:
            qi.callback = ("hold_posture", [hold, qi])
        tqi = QueueItem(None, isFinal=False)
        tqi.preCall=("turn_towardsTarget", [tqi, target])
        self.motionQueue.append(tqi)
        self.motionQueue.append(qi)
        return True
        
    def resetPosture(self, action_id = -1):
        self.motionQueue.append(QueueItem(self.relaxPosture, isFinal=True, action_id=action_id))
        return True
            
    def gestureAnim(self, name, relaxAfter=True, orientation=None, speech=None, speed=1.0, hold=0.0, action_id = -1):
        if orientation:
            preCall = ("turnTo", orientation)
        else: 
            preCall = None
        m = QueueItem(Piavca.ScaleMotionSpeed(Piavca.getMotion(name), speed), preCall=preCall)
        m.speech = speech        
        if hold > 0.0: m.callback = ("hold_posture", [hold, m])
        if relaxAfter:
            self.motionQueue.append(m)
            self.motionQueue.append(QueueItem(self.relaxPosture, isFinal=True, action_id=action_id))
        else:
            m.isFinal = True
            m.action_id = action_id
            self.motionQueue.append(m)
        return True
            
    def gesture(self, type, relaxAfter=True, orientation=None, speech=None, speed=1.0, hold=0.0, action_id = -1):
        if orientation:
            preCall = ("turnTo", orientation)
        else: 
            preCall = None
        if type=="all": # going through all the joints, for debugging purposes only
            jid = self.avatar.begin()
            while (jid!=self.avatar.end()):
                original = dict()
                original[jid] = self.avatar.getJointOrientation(jid)
                animationTargets = dict()
                animationTargets[jid] = original[jid] * Piavca.Quat(-math.pi/2, Piavca.Vec.XAxis())
                self.animationQueue.append(QueueItem(animationTargets, preCall=preCall))
                self.animationQueue.append(QueueItem(original, isFinal=True, action_id=action_id))
                jid = self.avatar.next(jid)
        else:
            return self.gestureAnim(type, relaxAfter, orientation, speech, speed, hold, action_id)
        return True
    
    def sayPreRecorded(self, file, action_id = -1):
        if sound.EchoesAudio.soundPresent:
            duration = sound.EchoesAudio.getSoundDuration(file)
            if duration == 0: 
                Logger.warning("Requested sound file not present")
                return False
            at = ActionTimer(duration, self, action_id, speech=True)
            self.speechQueue.append(QueueItem(file, timer=at, action_id=action_id))
            return True
        Logger.warning("Could not say anything, sound is not available")
        return False

    def sayPreRecordedNow(self, file, action_id = -1):
        if sound.EchoesAudio.soundPresent:
            duration = sound.EchoesAudio.getSoundDuration(file)
            if duration == 0: 
                Logger.warning("Requested sound file not present")
                return False
            at = ActionTimer(duration, self, action_id, speech=True)
            self.speechQueue.appendleft(QueueItem(file, timer=at, action_id=action_id))
            return True
        Logger.warning("Could not say anything, sound is not available")
        return False
                    
    def setFacialExpression (self, expression="Neutral", weight=1.0, action_id = -1):
        if expression == "Neutral":
            for name in self.expressionTargets.keys():
                self.expressionTargets[name] = 0.0
        else:
            if expression in self.expressionTargets:
                self.expressionTargets[expression] = weight
            else:
                Logger.warning(expression + ": No such facial expression found")
                return False
        return True
        
    def pickFlower(self, id=None, speech=None, action_id = -1, walkTo=False):
        if not id:
            for tid, object in self.app.canvas.objects.items():
                if isinstance(object, objects.Plants.EchoesFlower):
                    id = tid
                    break
        if not id:
            Logger.warning("Avatar: no flower to pick, doing nothing")
            return False
        flower = self.app.canvas.objects[id]
        if walkTo and not (flower.overAgent and flower.beingDragged):
            self.walkToObject(id)
        elif not self.isAt(id)  and not (flower.overAgent and flower.beingDragged):
            Logger.warning("Avatar: flower too far away, doing nothing")
            return False
        mpick = Piavca.getMotion('pick_up_flower')
        mpick.setStartTime(0.0)
        endtime = mpick.getMotionLength()
        mdown = Piavca.SubMotion(mpick, 0.0, 0.7)
        mup = Piavca.SubMotion(mpick, 0.7, endtime)
        qidown = QueueItem(mdown, callback = ("attach_flowerR", flower))
        qidown.preCall = ("check_at", [id, action_id])
        qidown.speech = speech
        self.motionQueue.append(qidown)
        self.motionQueue.append(QueueItem(mup, isFinal=True, playDirect=True, action_id = action_id))
        return True 

    def touchObject(self, id=None, speech=None, action_id = -1, walkTo=False):
        if not id:
            Logger.warning("Avatar: no object provided to touch, doing nothing") 
            return False
        if walkTo:
            self.walkToObject(id)
        elif not(self.isAt(id)):
            Logger.warning("Avatar: flower too far away, doing nothing")
            return False
        object = self.app.canvas.objects[id]
        if object.pos[1] < self.low:
            mpick = Piavca.getMotion('touch_floor')
            cut = 0.5
        elif object.pos[1] < self.knee:
            mpick = Piavca.getMotion('touch_knee')
            cut = 0.5
        elif object.pos[1] < self.waist:
            mpick = Piavca.getMotion('touch_waist')
            cut = 0.5
        else:
            mpick = Piavca.getMotion('touch_head')
            cut = 0.5
        mpick.setStartTime(0.0)
        endtime = mpick.getMotionLength()
        mdown = Piavca.SubMotion(mpick, 0.0, endtime*cut)
        mup = Piavca.SubMotion(mpick, endtime*cut, endtime)
        qidown = QueueItem(mdown, callback = ("touch_object", object))        
        qidown.preCall = ("check_at", [id, action_id])
        qidown.speech = speech
        self.motionQueue.append(qidown)
        self.motionQueue.append(QueueItem(mup, isFinal=True, playDirect=True, action_id = action_id))
        return True         

    def touchFlower(self, id=None, target="Bubble", speech=None, action_id = -1):
        if not id:
            for tid, object in self.app.canvas.objects.items():
                if isinstance(object, objects.Plants.EchoesFlower):
                    id = tid
                    break
        if not id:
            Logger.warning("Avatar: no flower to touch, doing nothing") 
            return False
        flower = self.app.canvas.objects[id]
        mpick = Piavca.getMotion('spinning_flower_stepforward')
        mpick.setStartTime(0.0)
        endtime = mpick.getMotionLength()
        mdown = Piavca.SubMotion(mpick, 0.0, 1.3)
        mup = Piavca.SubMotion(mpick, 1.3, endtime)
        qidown = QueueItem(mdown, callback = ("touch_flower", [flower, target]))
        qidown.preCall = ("check_at", [id, action_id])
        qidown.speech = speech
        self.motionQueue.append(qidown)
        self.motionQueue.append(QueueItem(mup, isFinal=True, playDirect=True, action_id = action_id))
        return True         
        
    def putdownFlower(self, action_id = -1, walkTo=-1):
        flower = None
        for object in self.tcb.attachedObjects:
            if isinstance(object, objects.Plants.EchoesFlower):
                flower = object
                break
        if not flower: 
            Logger.warning("Avatar: not holding a flower, doing nothing") 
            return False      
        if walkTo > -1: # make sure the pot lands in the desired slot
            flowerx = walkTo - 4.5
            if flowerx > self.pos[0]:
                self.walkTo(flowerx - 1, self.pos[2])
            else:        
                self.walkTo(flowerx + 1, self.pos[2])
        mput = Piavca.getMotion('pick_up_flower')
        mput.setStartTime(0.0)
        endtime = mput.getMotionLength()
        mdown = Piavca.SubMotion(mput, 0.0, 0.7)
        mup = Piavca.SubMotion(mput, 0.7, endtime)
        self.motionQueue.append(QueueItem(mdown, callback = ("detach_flowerR", flower)))
        self.motionQueue.append(QueueItem(mup, isFinal=True, playDirect=True, action_id=action_id))
        return True         
        
    def putFlowerInPot(self, id=None, action_id = -1): 
        if not id:
            for tid, object in self.app.canvas.objects.items():
                if isinstance(object, objects.Plants.Pot):
                    id = tid
                    break
        if not id:
            Logger.warning("Avatar: no pot in scene, doing nothing") 
            return False
        flower = None
        for object in self.tcb.attachedObjects:
            if isinstance(object, objects.Plants.EchoesFlower):
                flower = object
                break
        if not flower: 
            Logger.warning("Avatar: not holding a flower, doing nothing") 
            return False
        if not(self.isAt(id)):
            Logger.warning("Avatar: Specified (or nearest) pot is too far away; not putting down flower")
            return False

        pot = self.app.canvas.objects[id]
        mput = Piavca.getMotion('pick_up_flower')
        mput.setStartTime(0.0)
        endtime = mput.getMotionLength()
        mdown = Piavca.SubMotion(mput, 0.0, 0.7)
        mup = Piavca.SubMotion(mput, 0.7, endtime)
        qidown = QueueItem(mdown, callback = ("detach_flowerInPotR", [pot, flower]))
        qidown.preCall = ("check_at", [id, action_id])
        self.motionQueue.append(qidown)
        self.motionQueue.append(QueueItem(mup, isFinal=True, playDirect=True, action_id=action_id))
        return True         
        
    def putFlowerInBasket(self, id=None, action_id = -1, walkTo=False): 
        if not id:
            for tid, object in self.app.canvas.objects.items():
                if isinstance(object, objects.Environment.Basket):
                    id = tid
                    break
        if not id:
            Logger.warning("Avatar: no basket in scene, doing nothing") 
            return False
        flower = None
        for object in self.tcb.attachedObjects:
            if isinstance(object, objects.Plants.EchoesFlower):
                flower = object
                break
        if not flower: 
            Logger.warning("Avatar: not holding a flower, doing nothing") 
            return False
        if walkTo:
            self.walkToObject(id)
        elif not(self.isAt(id)):
            Logger.warning("Avatar: Specified (or nearest) basket is too far away; not putting down flower")
            return False

        basket = self.app.canvas.objects[id]
        mput = Piavca.getMotion('pick_up_flower')
        mput.setStartTime(0.0)
        endtime = mput.getMotionLength()
        mdown = Piavca.SubMotion(mput, 0.0, 0.7)
        mup = Piavca.SubMotion(mput, 0.7, endtime)
        qidown = QueueItem(mdown, callback = ("detach_flowerInBasketR", [basket, flower]))
        qidown.preCall = ("check_at", [id, action_id])
        self.motionQueue.append(qidown)
        self.motionQueue.append(QueueItem(mup, isFinal=True, playDirect=True, action_id=action_id))
        return True         

    def stackPot(self, id=None, action_id = -1, walkTo = False): 
        pot = None
        for object in self.tcb.attachedObjects:
            if isinstance(object, objects.Plants.Pot):
                pot = object
                break
        if not pot: 
            Logger.warning("Avatar: not holding a pot, doing nothing") 
            return False
        d = 1000
        if not id:
            for tid, object in self.app.canvas.objects.items():
                if isinstance(object, objects.Plants.Pot) and object != pot and self.getDistance(tid) < d:
                    Logger.trace("info", "Found a pot " + str(tid) + " at distance " + str(self.getDistance(tid)))
                    id = tid
                    d = self.getDistance(tid)
        if not id:
            Logger.warning("Avatar: no other pot in scene, doing nothing") 
            return False

        if walkTo: 
            self.walkToObject(id)
        elif not(self.isAt(id)):
            Logger.warning("Avatar: Not close enough to target pot, not stacking")
            return False
        targetpot = self.app.canvas.objects[id]
        if targetpot.pos[1] < self.low:
            mpick = Piavca.getMotion('pot_down_floor')
            cut = 0.4
        elif targetpot.pos[1] < self.knee:
            mpick = Piavca.getMotion('pot_down_knee')
            cut = 0.4
        elif targetpot.pos[1] < self.waist:
            mpick = Piavca.getMotion('pot_down_waist')
            cut = 0.4
        else:
            mpick = Piavca.getMotion('pot_down_head')
            cut = 0.7
        mpick.setStartTime(0.0)
        endtime = mpick.getMotionLength()
        mdown = Piavca.SubMotion(mpick, 0.0, endtime*cut)
        mup = Piavca.SubMotion(mpick, endtime*cut, endtime)
        qidown = QueueItem(mdown, isFinal=False, callback=("detach_potOnStackR", [pot, targetpot]))
        qidown.preCall = ("check_at", [id, action_id])
        self.motionQueue.append(qidown)
        self.motionQueue.append(QueueItem(mup, isFinal=True, playDirect=True, action_id=action_id))        
        return True         
       
    def pickupPot(self, id=None, speech=None, action_id = -1, walkTo=False):
        d = 1000
        if not id:
            for tid, object in self.app.canvas.objects.items():
                if isinstance(object, objects.Plants.Pot) and self.getDistance(tid) < d:
                    print "Found a pot " + str(tid) + " at distance " + str(self.getDistance(tid))
                    id = tid
                    d = self.getDistance(tid)
        if not id:
            Logger.warning("Avatar: no pot in scene, doing nothing") 
            return False
        pot = self.app.canvas.objects[id]
        if walkTo and not (pot.overAgent and pot.beingDragged): 
            self.walkToObject(id)
        elif not self.isAt(id) and not (pot.overAgent and pot.beingDragged):
            Logger.warning("Avatar: Not close enough to target pot, not stacking")
            return False
        if pot.pos[1] < self.low:
            mpick = Piavca.getMotion('pot_up_floor')
            cut = 0.4
        elif pot.pos[1] < self.knee:
            mpick = Piavca.getMotion('pot_up_knee')
            cut = 0.4
        elif pot.pos[1] < self.waist:
            mpick = Piavca.getMotion('pot_up_waist')
            cut = 0.4
        else:
            mpick = Piavca.getMotion('pot_up_head')
            cut = 0.7
        mpick.setStartTime(0.0)
        endtime = mpick.getMotionLength()
        mdown = Piavca.SubMotion(mpick, 0.0, endtime*cut)
        mup = Piavca.SubMotion(mpick, endtime*cut, endtime)
        qidown = QueueItem(mdown, callback = ("attach_potR", pot))
        qidown.preCall = ("check_at", [id, action_id])
        qidown.speech = speech
        self.motionQueue.append(qidown)
        self.motionQueue.append(QueueItem(mup, isFinal=True, playDirect=True, action_id=action_id))
        return True        

    def putdownPot(self, action_id = -1, walkTo = -1):
        pot = None
        for object in self.tcb.attachedObjects:
            if isinstance(object, objects.Plants.Pot):
                pot = object
                break
        if not pot: 
            Logger.warning("Avatar: not holding a pot, doing nothing") 
            return False
        if walkTo > -1: # make sure the pot lands in the desired slot
            potx = walkTo - 4.5
            if potx > self.pos[0]:
                self.walkTo(potx - 1, self.pos[2])
            else:        
                self.walkTo(potx + 1, self.pos[2])
        mput = Piavca.getMotion('pot_down_floor')
        mput.setStartTime(0.0)
        endtime = mput.getMotionLength()
        mdown = Piavca.SubMotion(mput, 0.0, endtime*0.7)
        mup = Piavca.SubMotion(mput, endtime*0.7, endtime)
        self.motionQueue.append(QueueItem(mdown, callback = ("detach_potR", None)))
        self.motionQueue.append(QueueItem(mup, isFinal=True, playDirect=True, action_id=action_id))
        return True       

    def pickupBasket(self, id=None, speech=None, action_id = -1, walkTo=False):
        d = 1000
        if not id:
            for tid, object in self.app.canvas.objects.items():
                if isinstance(object, objects.Environment.Basket) and self.getDistance(tid) < d:
                    print "Found a basket " + str(tid) + " at distance " + str(self.getDistance(tid))
                    id = tid
                    d = self.getDistance(tid)
                    break
        if not id:
            Logger.warning("Avatar: no pot in scene, doing nothing") 
            return False
        basket = self.app.canvas.objects[id]
        if walkTo and not (basket.overAgent and basket.beingDragged): 
            self.walkToObject(id)
        elif not self.isAt(id) and not (basket.overAgent and basket.beingDragged):
            Logger.warning("Avatar: Not close enough to target pot, not stacking")
            return False
        if basket.pos[1] < self.low:
            mpick = Piavca.getMotion('pot_up_floor')
            cut = 0.4
        elif basket.pos[1] < self.knee:
            mpick = Piavca.getMotion('pot_up_knee')
            cut = 0.4
        elif basket.pos[1] < self.waist:
            mpick = Piavca.getMotion('pot_up_waist')
            cut = 0.4
        else:
            mpick = Piavca.getMotion('pot_up_head')
            cut = 0.7
        mpick.setStartTime(0.0)
        endtime = mpick.getMotionLength()
        mdown = Piavca.SubMotion(mpick, 0.0, endtime*cut)
        mup = Piavca.SubMotion(mpick, endtime*cut, endtime)
        qidown = QueueItem(mdown, callback = ("attach_potR", basket))
        qidown.preCall = ("check_at", [id, action_id])
        qidown.speech = speech        
        self.motionQueue.append(qidown)
        self.motionQueue.append(QueueItem(mup, isFinal=True, playDirect=True, action_id=action_id))
        return True        

    def putdownBasket(self, action_id = -1, walkTo = -1):
        basket = None
        for object in self.tcb.attachedObjects:
            if isinstance(object, objects.Environment.Basket):
                basket = object
                break
        if not basket: 
            Logger.warning("Avatar: not holding a basket, doing nothing") 
            return False
        if walkTo > -1: # make sure the pot lands in the desired slot
            basketx = walkTo - 4.5
            if basketx > self.pos[0]:
                self.walkTo(basketx - 1, self.pos[2])
            else:        
                self.walkTo(basketx + 1, self.pos[2])
        mput = Piavca.getMotion('pot_down_floor')
        mput.setStartTime(0.0)
        endtime = mput.getMotionLength()
        mdown = Piavca.SubMotion(mput, 0.0, endtime*0.7)
        mup = Piavca.SubMotion(mput, endtime*0.7, endtime)
        qidown = QueueItem(mdown, callback = ("detach_potR", None))
        self.motionQueue.append(qidown)
        self.motionQueue.append(QueueItem(mup, isFinal=True, playDirect=True, action_id=action_id))
        return True       

    def pickupBall(self, id=None, speech=None, action_id = -1, walkTo=False):
        d = 1000
        if not id:
            for tid, object in self.app.canvas.objects.items():
                if isinstance(object, objects.PlayObjects.Ball) and self.getDistance(tid) < d:
                    Logger.trace("info", "Found a ball " + str(tid) + " at distance " + str(self.getDistance(tid)))
                    id = tid
                    d = self.getDistance(tid)
        if not id:
            Logger.warning("Avatar: no ball in scene, doing nothing") 
            return False
        ball = self.app.canvas.objects[id]
        if walkTo and not (ball.overAgent and ball.beingDragged): 
            self.walkToObject(id)
        elif not self.isAt(id) and not (ball.overAgent and ball.beingDragged):
            Logger.warning("Avatar: Not close enough to target ball, not picking up")
            return False
        if ball.pos[1] < self.low:
            mpick = Piavca.getMotion('pot_up_floor')
            cut = 0.4
        elif ball.pos[1] < self.knee:
            mpick = Piavca.getMotion('pot_up_knee')
            cut = 0.4
        elif ball.pos[1] < self.waist:
            mpick = Piavca.getMotion('pot_up_waist')
            cut = 0.4
        else:
            mpick = Piavca.getMotion('pot_up_head')
            cut = 0.7
        mpick.setStartTime(0.0)
        endtime = mpick.getMotionLength()
        mdown = Piavca.SubMotion(mpick, 0.0, endtime*cut)
        mup = Piavca.SubMotion(mpick, endtime*cut, endtime)
        qidown = QueueItem(mdown, callback = ("attach_potR", ball))
        qidown.preCall = ("check_at", [id, action_id])
        qidown.speech = speech
        self.motionQueue.append(qidown)
        self.motionQueue.append(QueueItem(mup, isFinal=True, playDirect=True, action_id=action_id))
        return True        

    def putdownBall(self, action_id = -1, walkTo = -1):
        ball = None
        for object in self.tcb.attachedObjects:
            if isinstance(object, objects.PlayObjects.Ball):
                ball = object
                break
        if not ball: 
            Logger.warning("Avatar: not holding a ball, doing nothing") 
            return False
        if walkTo > -1: # make sure the pot lands in the desired slot
            ballx = walkTo - 4.5
            if ballx > self.pos[0]:
                self.walkTo(ballx - 1, self.pos[2])
            else:        
                self.walkTo(ballx + 1, self.pos[2])
        mput = Piavca.getMotion('pot_down_floor')
        mput.setStartTime(0.0)
        endtime = mput.getMotionLength()
        mdown = Piavca.SubMotion(mput, 0.0, endtime*0.7)
        mup = Piavca.SubMotion(mput, endtime*0.7, endtime)
        self.motionQueue.append(QueueItem(mdown, callback = ("detach_potR", None)))
        self.motionQueue.append(QueueItem(mup, isFinal=True, playDirect=True, action_id=action_id))
        return True       

    def putBallIntoContainer(self, id=None, action_id = -1, walkTo = False): 
        ball = None
        for object in self.tcb.attachedObjects:
            if isinstance(object, objects.PlayObjects.Ball):
                ball = object
                break
        if not ball: 
            Logger.warning("Avatar: not holding a ball, doing nothing") 
            return False
        if not id:
            for tid, object in self.app.canvas.objects.items():
                if isinstance(object, objects.Environment.Container):
                    Logger.trace("info", "Found a container " + str(tid))
                    id = tid
        if not id or not id in self.app.canvas.objects:
            Logger.warning("Avatar: no other pot in scene, doing nothing") 
            return False
        container = self.app.canvas.objects[id]
        if walkTo: 
            self.walkToObject(id)
        elif not self.isAt(id):
            Logger.warning("Avatar: Not close enough to target container")
            return False
        mpick = Piavca.getMotion('drop_ball')
        cut = 0.5
        mpick.setStartTime(0.0)
        endtime = mpick.getMotionLength()
        mdown = Piavca.SubMotion(mpick, 0.0, endtime*cut)
        mup = Piavca.SubMotion(mpick, endtime*cut, endtime)
        qidown = QueueItem(mdown, isFinal=False, callback=("put_ballContainer", [ball, container]))
        qidown.preCall = ("check_at", [id, action_id])
        self.motionQueue.append(qidown)
        self.motionQueue.append(QueueItem(mup, isFinal=True, playDirect=True, action_id=action_id))        
        return True         

    def throwBall(self, action_id = -1, cloudId = None):
        ball = None
        for object in self.tcb.attachedObjects:
            if isinstance(object, objects.PlayObjects.Ball):
                ball = object
                break
        if not ball: 
            Logger.warning("Avatar: not holding a ball, doing nothing") 
            return False
        if cloudId: # make sure the pot lands in the desired slot
            self.walkToObject(cloudId, distance=2.5)
        mpick = Piavca.getMotion('touch_above')
        mpick.setStartTime(0.0)
        endtime = mpick.getMotionLength()
        mup = Piavca.SubMotion(mpick, 0.0, endtime/2)
        mdown = Piavca.SubMotion(mpick, endtime/2, endtime)
        qiup = QueueItem(mup, callback = ("throw_ball", ball))
        self.motionQueue.append(qiup)
        self.motionQueue.append(QueueItem(mdown, isFinal=True, playDirect=True, action_id=action_id))
        return True    

    def makeRain(self, id=None, action_id = -1, walkTo = False):
        if not id:
            for tid, object in self.app.canvas.objects.items():
                if isinstance(object, objects.Environment.Cloud):
                    id = tid
                    break
        if not id:
            Logger.warning("Avatar: no cloud in scene, doing nothing") 
            return False
        
        if walkTo: 
            self.walkToObject(id)
        elif not(self.isAt(id)):
            Logger.warning("Avatar: specified (or nearest) cloud is too far away; not making rain")
            return False        
        cloud = self.app.canvas.objects[id]
        mpick = Piavca.getMotion('touch_above')
        mpick.setStartTime(0.0)
        endtime = mpick.getMotionLength()
        mup = Piavca.SubMotion(mpick, 0.0, endtime/2)
        mdown = Piavca.SubMotion(mpick, endtime/2, endtime)
        qiup = QueueItem(mup, callback = ("make_rain", cloud))
        qiup.preCall = ("check_at", [id, action_id])
        self.motionQueue.append(qiup)
        self.motionQueue.append(QueueItem(mdown, isFinal=True, playDirect=True, action_id=action_id))
        return True    
        
    def touchLeaves(self, id=None, action_id = -1):
        if not id:
            for tid, object in self.app.canvas.objects.items():
                if isinstance(object, objects.Plants.MagicLeaves):
                    id = tid
                    break
        if not id:
            Logger.warning("Avatar: no MagicLeaves in scene, doing nothing") 
            return False
        leaf = self.app.canvas.objects[id]
        if leaf.flying and leaf.energy > 0:
            Logger.warning("Avatar warning: MagicLeaves are moving, not attempting to touch them")
            return False        
        if not self.isAt(id):
            Logger.warning("Avatar warning: not at MagicLeaves to be able to touch them")
            return False        
        if leaf.pos[1] < self.pos[1]+1:
            Logger.warning("Avatar warning: MagicLeaves too low, doing nothing")
            return False        
        mpick = Piavca.getMotion('touch_above')
        mpick.setStartTime(0.0)
        endtime = mpick.getMotionLength()
        mup = Piavca.SubMotion(mpick, 0.0, endtime/2)
        mdown = Piavca.SubMotion(mpick, endtime/2, endtime)
        qiup = QueueItem(mup, callback = ("touch_leaves", leaf))
        qiup.preCall = ("check_at", [id, action_id])
        self.motionQueue.append(qiup)
        self.motionQueue.append(QueueItem(mdown, isFinal=True, playDirect=True, action_id=action_id))
        return True    

    def attachObjectToHand(self, object, right=True):
        if right:
            self.tcb.attachObjectToJoint(object, self.jointIdByName['Bip01 R Finger1'])
        else:
            self.tcb.attachObjectToJoint(object, self.jointIdByName['Bip01 L Finger1'])
        return True
    
    def detachObjectFromHand(self, right=True):
        if right:
            self.tcb.detachObjectFromJoint(self.jointIdByName['Bip01 R Finger1'])
        else:
            self.tcb.detachObjectFromJoint(self.jointIdByName['Bip01 L Finger1'])
        return True
    
    def attachCloud(self, cloudId=None, action_id = -1):
        if not cloudId:
            for tid, object in self.app.canvas.objects.items():
                if isinstance(object, objects.Environment.Cloud):
                    cloudId = tid
                    break
        if not cloudId:
            return False
        self.pointAt(cloudId)
        self.lookAtObject(cloudId)
        mask = Piavca.MotionMask()
        mask.setAllMask(False)
        motion = Piavca.MaskedMotion(Piavca.ZeroMotion(), mask)
        qi = QueueItem(motion, preCall = ("attach_cloud", cloudId))
        self.motionQueue.append(qi)
        return True

    def detachCloud(self, action_id = -1):
        qi = QueueItem(self.relaxPosture, preCall = ("detach_cloud", None))
        self.motionQueue.append(qi)
        return True
    
    def click(self, pos):
#        print "Avatar clicked at", pos
        self.app.canvas.rlPublisher.userTouchedAgent(str(self.id))
        
    def blinking(self, flag=True):
        if flag:
            if not self.blinkingTimer:
                self.blinkingTimer = BlinkingTimer(self)
                self.blinkingTimer.start()
        else:
            if self.blinkingTimer:
                self.blinkingTimer.running == False
                self.blinkingTimer = None
        return True
                    
    def getXYContour(self):
        bb = self.avatar.getBoundBox()
        bbox = [Piavca.Vec(bb.min.X(), bb.min.Y(), bb.min.Z()), 
                Piavca.Vec(bb.max.X(), bb.min.Y(), bb.min.Z()),
                Piavca.Vec(bb.min.X(), bb.max.Y(), bb.min.Z()),
                Piavca.Vec(bb.max.X(), bb.max.Y(), bb.min.Z()),
                Piavca.Vec(bb.min.X(), bb.min.Y(), bb.max.Z()),
                Piavca.Vec(bb.max.X(), bb.min.Y(), bb.max.Z()),
                Piavca.Vec(bb.min.X(), bb.max.Y(), bb.max.Z()),
                Piavca.Vec(bb.max.X(), bb.max.Y(), bb.max.Z())
                ]
        bbox_wc = []
        minx = miny = 1000
        maxx = maxy = -1000
        for vec in bbox:
            v = self.avatar.getRootOrientation().transform(vec * self.scale)
            bbox_wc.append(v)
            minx = min(minx, v.X())
            maxx = max(maxx, v.X())
            miny = min(miny, v.Y())
            maxy = max(maxy, v.Y())

        pos = self.avatar.getRootPosition() * self.scale
        minx += pos.X()
        maxx += pos.X()
        miny += pos.Y()
        maxy += pos.Y()
        # this is a hack, unsure why the bounding box is shifted by 2...
        return [(minx, miny-2), (minx, maxy-2), (maxx, maxy-2), (maxx, miny-2)]

    def render(self):
        ''' 
        will be rendered in Piavca Core loop
        '''
        if hasattr(self, "showBoundary") and self.showBoundary:
            contour = self.getXYContour()
            contour.append(contour[0])
            glPushMatrix()
            glLineWidth(1.0)
            glColor4f(1, 0, 0, 1.0)
            glBegin(GL_LINE_STRIP)
            for v in contour:
                glVertex3f(v[0], v[1], self.pos[2])                        
            glEnd()         
            glPopMatrix()   
            
        if hasattr(self, "publishVisibility") and self.publishVisibility:
            if self.visible and abs(self.pos[0]) > 4.5:
                self.visible = False 
                self.app.canvas.rlPublisher.agentPropertyChanged(str(self.id), "Visible", str(self.visible))
            if not self.visible and abs(self.pos[0]) < 4.5:
                self.visible = True 
                self.app.canvas.rlPublisher.agentPropertyChanged(str(self.id), "Visible", str(self.visible))
    
    def remove(self):
        # Piavca.Core.getCore().removeAvatar(self.avatar)
        super(EchoesAvatar, self).remove()
        
class QueueItem():
    
    def __init__ (self, item, isFinal=False, callback=None, preCall=None, playDirect=False, timer=None, speech=None, action_id = -1):
        self.item = item
        self.isFinal = isFinal
        self.callback = callback
        self.playDirect = playDirect
        self.action_id = action_id
        self.preCall = preCall
        self.timer = timer
        self.speech = speech
        
class ActionTimer(threading.Thread):
     
    def __init__(self, seconds, avatar, action_id=-1, speech=False):
        self.runTime = seconds
        self.app = avatar.app         
        self.avatar = avatar         
        self.action_id = action_id
        self.speech = speech
        threading.Thread.__init__(self)
         
    def run(self):
        if self.speech:
            weight = 0.0
            for i in range(int(2*self.runTime)):
                time.sleep(0.5)
                if weight == 0.0:
                    self.avatar.setFacialExpression("OpenMouth", 1.0)
                    weight = 1.0
                else:
                    self.avatar.setFacialExpression("OpenMouth", 0.0)
                    weight = 0.0
            self.avatar.setFacialExpression("OpenMouth", 0.0)
        else:
            time.sleep(self.runTime)
            
        if self.speech:
            self.avatar.speaking = False
        self.app.canvas.agentActionCompleted(self.action_id, True)
         
class BlinkingTimer(threading.Thread):
     
    def __init__(self, avatar):
        self.app = avatar.app         
        self.avatar = avatar         
        threading.Thread.__init__(self)
        self.running = True
         
    def run(self):
        while self.running: 
            trackId = self.avatar.facialExpressions["ClosedEyes"]
            self.avatar.facialExpMotion.setFloatValue(trackId, 1.0)
#            Logger.trace("info", "Blinking - eyes closing")
            time.sleep(0.3)
            self.avatar.facialExpMotion.setFloatValue(trackId, 0.0)
#            Logger.trace("info", "Blinking - eyes opening")
            time.sleep(random.randint(2,4)) 

class PiavcaTimeCallback (Piavca.AvatarTimeCallback):
    
    def __init__ (self, name, echoesAvatar):
        super(PiavcaTimeCallback, self).__init__(name)
        self.echoesAvatar = echoesAvatar
        self.currentQi = None
        self.animationSteppers = dict()  # storing jointId -> t (slerp interpolation variable [0,1])
        self.nextCallbackArgs = None
        self.attachedObjects = dict()
        self.attachedCloud = None
        self.action_id = -1
        self.expressionWeights = dict()
        for name, weight in echoesAvatar.expressionTargets.items():
            self.expressionWeights[name] = weight
        
    def init(self, avatar):
        pass  
    
    def attachObjectToJoint(self, object, id):
        self.attachedObjects[object] = id  
        
    def detachObjectFromJoint(self, id):
        for object, oid in self.attachedObjects.items():
            if oid == id:
                if hasattr(object, "detachFromJoint"):
                    object.detachFromJoint()
                del self.attachedObjects[object]

    def detachObject(self, object):
        for o, oid in self.attachedObjects.items():
            if o == object:
                if hasattr(object, "detachFromJoint"):
                    object.detachFromJoint()
                del self.attachedObjects[o]
                
    def attachCloud(self, cloud):
        self.attachedCloud = cloud

    def detachCloud(self):
        if self.attachedCloud:
            self.attachedCloud.detachFromAvatar()
            self.attachedCloud = None

    def timeStep (self, avatar, time):
        # get motion stack processed
        if self.echoesAvatar.playing and self.echoesAvatar.repositioner.getMotion().finished() and self.currentQi:
            self.echoesAvatar.playing = False
            self.echoesAvatar.avatar.stopMotion()
            self.echoesAvatar.updatePos(adjustY=self.currentQi.isFinal)
            Logger.trace("info", "Avatar position after after action " + str(self.currentQi.action_id) +  ": " + str(self.echoesAvatar.pos))
            if self.currentQi.callback:
                avatarCallback(self.echoesAvatar, self.currentQi.callback[0],self.currentQi.callback[1])
            if self.currentQi.isFinal:
                self.echoesAvatar.app.canvas.agentActionCompleted(self.currentQi.action_id, True)
            self.currentQi = None
        if not self.echoesAvatar.playing and not self.echoesAvatar.animating and len(self.echoesAvatar.motionQueue) > 0:
            self.currentQi = self.echoesAvatar.motionQueue.popleft()
            if self.currentQi.preCall:
                avatarCallback(self.echoesAvatar, self.currentQi.preCall[0], self.currentQi.preCall[1])
            if self.currentQi.speech:
                if "," in self.currentQi.speech:
                    allSpeech = self.currentQi.speech.split(",")
                    allSpeech.reverse()
                    for s in allSpeech:
                        self.echoesAvatar.sayPreRecordedNow(s)
                else:
                    self.echoesAvatar.sayPreRecordedNow(self.currentQi.speech)
            if self.echoesAvatar.cancelMotions == 0: 
                if self.currentQi.playDirect:
                    self.echoesAvatar.playDirectAtPos(self.currentQi.item)
                else:         
                    self.echoesAvatar.playSmoothAtPos(self.currentQi.item)
            else:
                self.echoesAvatar.cancelMotions -= 1
        # manual animation
        if not self.echoesAvatar.playing and not self.echoesAvatar.animating and len(self.echoesAvatar.animationQueue) > 0:
            self.currentQi = self.echoesAvatar.animationQueue.popleft()
            self.echoesAvatar.animationTargets = self.currentQi.item
            if self.currentQi.preCall:
                avatarCallback(self.echoesAvatar, self.currentQi.preCall[0], self.currentQi.preCall[1])
            self.echoesAvatar.avatar.stopMotion()
            self.echoesAvatar.animating = True
        if self.echoesAvatar.animating and self.currentQi:
            deletes = []
            for jointId, targetOrientation in self.echoesAvatar.animationTargets.iteritems():
                currentOrientation = self.echoesAvatar.avatar.getJointOrientation(jointId)
                if jointId not in self.animationSteppers:
                    self.animationSteppers[jointId] = 0.0
                newOrientation = Piavca.slerp(currentOrientation, targetOrientation, self.animationSteppers[jointId])
                self.echoesAvatar.avatar.setJointOrientation(jointId, newOrientation)
                self.animationSteppers[jointId] += 0.01
                if self.animationSteppers[jointId] > 1.0:
                    deletes.append(jointId)
            for id in deletes:
#                print "Finished animating joint", id, "(", self.echoesAvatar.avatar.getJointName(id), ")"
                del self.echoesAvatar.animationTargets[id]
                del self.animationSteppers[id]
            if len(self.animationSteppers) == 0:
                self.echoesAvatar.animating = False
                if self.currentQi.isFinal:
                    self.echoesAvatar.app.canvas.agentActionCompleted(self.action_id, True)
        # attached objects
        for object, id in self.attachedObjects.items():
            jpos = self.echoesAvatar.avatar.getJointBasePosition(id, Piavca.WORLD_COORD) * self.echoesAvatar.scale
            jori = self.echoesAvatar.avatar.getJointOrientation(id, Piavca.WORLD_COORD)
            if hasattr(object, "attachToJoint"):
                object.attachToJoint([jpos.X(), jpos.Y(), jpos.Z()+self.echoesAvatar.zOffset], [jori.Xangle(), jori.Yangle(), jori.Zangle()], self)
                
        if self.attachedCloud:
            jpos = self.echoesAvatar.avatar.getRootPosition() * self.echoesAvatar.scale
            jori = self.echoesAvatar.forwardOrientation.inverse() * self.echoesAvatar.avatar.getRootOrientation()
            self.attachedCloud.attachToAvatar([jpos.X(), jpos.Y(), jpos.Z()+self.echoesAvatar.zOffset], [jori.Xangle(), jori.Yangle(), jori.Zangle()], self)
                                
        # facial expressions
        for name, weight in self.expressionWeights.items():
            if weight != self.echoesAvatar.expressionTargets[name]:
                if not self.echoesAvatar.playing:
                    posture = Piavca.AvatarPosture()
                    posture.getPostureFromAvatar(self.echoesAvatar.avatar)
                    self.echoesAvatar.motionQueue.append(QueueItem(posture))
                if weight > self.echoesAvatar.expressionTargets[name]:
                    self.expressionWeights[name] -= 0.05
                    if self.expressionWeights[name] <= self.echoesAvatar.expressionTargets[name]: 
                        self.expressionWeights[name] = self.echoesAvatar.expressionTargets[name]            
                else:    
                    self.expressionWeights[name] += 0.05
                    if self.expressionWeights[name] >= self.echoesAvatar.expressionTargets[name]: 
                        self.expressionWeights[name] = self.echoesAvatar.expressionTargets[name]
                self.echoesAvatar.facialExpMotion.setFloatValue(self.echoesAvatar.facialExpressions[name], self.expressionWeights[name])
        
        # speech queue
        if not self.echoesAvatar.speaking and len(self.echoesAvatar.speechQueue) > 0:
            ai = self.echoesAvatar.speechQueue.popleft()
            sound.EchoesAudio.playSound(ai.item, action_id=ai.action_id)
            self.echoesAvatar.speaking = True
            ai.timer.start()

def avatarCallback(echoesAvatar, type, arg):
    if type == "turnTo":
        if len(arg) == 2 and isinstance(arg[0], (float, int)): #coordinates
            echoesAvatar.turnTowardsDirect(arg[0], arg[1], False)
        elif arg[0] == "child":
            echoesAvatar.turnTowardsDirect(echoesAvatar.pos[0], 10, False)
        elif isinstance(arg[0], objects.EchoesObject.EchoesObject):
            target, action_id = arg
            if target.id in echoesAvatar.app.canvas.objects:
                echoesAvatar.turnTowardsDirect(target.pos[0], target.pos[2], False)
            else:
                echoesAvatar.app.canvas.agentActionCompleted(action_id, False)
                echoesAvatar.cancelMotions = 1
    elif type == "walkTo":
        if len(arg) != 5: return
        withObject = False
        if isinstance(arg[0], objects.EchoesObject.EchoesObject):
            o, di, wi, r, action_id = arg
            wi.callback = ("turnTo", [o.pos[0], o.pos[2]])
            if echoesAvatar.pos[0] > o.pos[0]:
                x = o.pos[0] + di
            else: 
                x = o.pos[0] - di
            z = o.pos[2]  
            withObject = True      
        else:
            x, z, wi, r, action_id = arg

        distance = math.hypot(echoesAvatar.pos[0]-x,echoesAvatar.pos[2]-z)
        if distance > echoesAvatar.walkingDistance:
            Logger.warning("Avatar cannot walk that far in one go, walking full length of animation instead and inserting another walking motion")
            w = echoesAvatar.walking
            new_wi = QueueItem(None, action_id = action_id, isFinal=False)
            if withObject:
                new_wi.preCall = ("walkTo", [o,di,new_wi,echoesAvatar.relaxPosture,action_id])
            else:
                new_wi.preCall = ("walkTo", [x,z,new_wi,echoesAvatar.relaxPosture,action_id])
            wi.action_id = -1
            wi.isFinal = False
            echoesAvatar.motionQueue.appendleft(new_wi)
        else:
            w = Piavca.SubMotion(echoesAvatar.walking, 0, echoesAvatar.findMotionEndtime(echoesAvatar.walking, distance))
        echoesAvatar.turnTowardsDirect(x,z, relaxAfter=False, intermediate=True)
        object = None
        for o in echoesAvatar.tcb.attachedObjects:
            object = o 
            break            
        mask = Piavca.MotionMask()
        mask.setAllMask(True)
        if object:
            print "Attached object:", object
            mask.setMask(echoesAvatar.jointIdByName['Bip01 R Forearm'], False)
            mask.setMask(echoesAvatar.jointIdByName['Bip01 L Forearm'], False)
            w = Piavca.MaskedMotion(w, mask)
            r = Piavca.MaskedMotion(r, mask)
        if echoesAvatar.tcb.attachedCloud:
            print "Attached cloud:", echoesAvatar.tcb.attachedCloud
            mask.setMask(echoesAvatar.jointIdByName['Bip01 Head'], False)
            mask.setMask(echoesAvatar.jointIdByName['Bip01 R UpperArm'], False)
            w = Piavca.MaskedMotion(w, mask)
            r = Piavca.MaskedMotion(r, mask)
        wi.item = w
        
    elif type == "setOrientation":
        echoesAvatar.orientation = arg
    elif type == "setPosition":
        echoesAvatar.pos = arg
    elif type == "attach_flowerR" and isinstance(arg, objects.Plants.EchoesFlower):
        if arg.pot:
            arg.pot.flower = None
        if arg.basket:
            arg.basket.removeFlower(arg)
            arg.basket = None
        echoesAvatar.attachObjectToHand(arg, right=True)                
    elif type == "detach_flowerR":
        echoesAvatar.detachObjectFromHand(right=True)                
    elif type == "detach_flowerInPotR":
        echoesAvatar.detachObjectFromHand(right=True)
        arg[0].flower = arg[1]                
    elif type == "detach_flowerInBasketR":
        echoesAvatar.detachObjectFromHand(right=True)
        arg[0].addFlower(arg[1])                
    elif type == "attach_potR":
        echoesAvatar.attachObjectToHand(arg, right=True)                
    elif type == "detach_potR":
        echoesAvatar.detachObjectFromHand(right=True)                
    elif type == "detach_potOnStackR":
        echoesAvatar.detachObjectFromHand(right=True)   
        arg[1].stackUp(arg[0])    
    elif type == "put_ballContainer":
        ball, container = arg
        echoesAvatar.detachObjectFromHand(right=True)
        ball.droppedByAvatar = True   
    elif type == "touch_object":
        echoesAvatar.app.canvas.rlPublisher.objectPropertyChanged(str(arg.id), "touchedByAgent", str(echoesAvatar.id))
    elif type == "touch_flower" and isinstance(arg[0], objects.Plants.EchoesFlower):
        if arg[1] == "Bubble":
            arg[0].intoBubble()
        else:
            arg[0].intoBall()
    elif type == "make_rain" and isinstance(arg, objects.Environment.Cloud):
        arg.rain(50)
    elif type == "touch_leaves" and isinstance(arg, objects.Plants.MagicLeaves):
        arg.touchLeaves(echoesAvatar.id)
    elif type == "throw_ball" and isinstance(arg, objects.PlayObjects.Ball):
        echoesAvatar.detachObjectFromHand(right=True)   
        arg.throw()
    elif type == "turn_towardsTarget":
        qi, target = arg
        if isinstance(target, objects.EchoesObject.EchoesObject):
            if target.id in echoesAvatar.app.canvas.objects:
                target = Piavca.Vec(target.pos[0],target.pos[1],target.pos[2])
            else:
                echoesAvatar.app.canvas.agentActionCompleted(action_id, False)
                echoesAvatar.cancelMotions = 1
                return
        if not isinstance(target, Piavca.Vec) and target == "child":
            target = Piavca.Vec(echoesAvatar.pos[0], 0, 10)
        x, z = [target.X(), target.Z()]
        angle = math.atan2(x-echoesAvatar.pos[0], z-echoesAvatar.pos[2]) 
        # move whole body for more than 75 degrees
        time = 0.1
        a_ori = -1*echoesAvatar.orientation.Zangle()
        if abs(angle - a_ori) > math.radians(75):
            if angle < a_ori: angle += math.radians(45)
            else: angle -= math.radians(45)
            echoesAvatar.orientation = Piavca.Quat(angle, Piavca.Vec.ZAxis())
            time = 0.3 # give the motion some time to turn around...
        posture = Piavca.AvatarPosture()
        posture.getPostureFromAvatar(echoesAvatar.avatar)
        motion = Piavca.ChangeMotionLength(posture, time)    
        qi.item = posture
    elif type == "lookatTarget" and len(arg) == 3:
        motion, target, action_id = arg
        if isinstance(target, objects.EchoesObject.EchoesObject):
            if target.id in echoesAvatar.app.canvas.objects:
                target = Piavca.Vec(target.pos[0],target.pos[1],target.pos[2])
            else:
                echoesAvatar.app.canvas.agentActionCompleted(action_id, False)
                echoesAvatar.cancelMotions = 1
                return
        if not isinstance(target, Piavca.Vec) and target == "child":
            target = Piavca.Vec(echoesAvatar.pos[0], 0, 10)
        target -= Piavca.Vec(0,0,echoesAvatar.zOffset)        
        target = target/echoesAvatar.scale
        target -= echoesAvatar.avatar.getJointBasePosition(echoesAvatar.jointIdByName['Bip01 Head'], Piavca.WORLD_COORD)
        target = echoesAvatar.avatar.getRootOrientation().inverse().transform(target)
        target = echoesAvatar.forwardOrientation.inverse().transform(target)
        motion.setTarget(target)
    elif type == "pointatTarget" and len(arg) == 3:
        motion, target, action_id = arg
        if isinstance(target, objects.EchoesObject.EchoesObject):
            if target.id in echoesAvatar.app.canvas.objects:
                target = Piavca.Vec(target.pos[0],target.pos[1],target.pos[2]-echoesAvatar.zOffset)
            else:
                echoesAvatar.app.canvas.agentActionCompleted(action_id, False)
                echoesAvatar.cancelMotions = 1
                return            
        motion.setTarget(target)
    elif type == "popBubble":
        target, action_id = arg
        if target.id in echoesAvatar.app.canvas.objects and isinstance(target, objects.Bubbles.EchoesBubble):
            target.click("Agent", False)
        else:
            echoesAvatar.app.canvas.agentActionCompleted(action_id, False)
    elif type == "attach_cloud":
        echoesAvatar.tcb.attachCloud(echoesAvatar.app.canvas.objects[arg])
    elif type == "detach_cloud":
        echoesAvatar.tcb.detachCloud()
    elif type == "check_at":
        o_id, action_id = arg
        o = echoesAvatar.app.canvas.objects[o_id]
        if not echoesAvatar.isAt(o_id) and not (o.overAgent and o.beingDragged):
            Logger.warning("Avatar combined action: Avatar is not at object " + str(o_id) + ". Object has been moved, action failed")
            echoesAvatar.cancelMotions = 2
            echoesAvatar.app.canvas.agentActionCompleted(action_id, False)
    elif type == "hold_posture":
        time, old_qi = arg
        posture = Piavca.AvatarPosture()
        posture.getPostureFromAvatar(echoesAvatar.avatar)
        motion = Piavca.ChangeMotionLength(posture, time)
        qi = QueueItem(motion)
        if old_qi.isFinal:
            qi.isFinal = True
            old_qi.isFinal = False
        if old_qi.action_id != -1:
            qi.action_id = old_qi.action_id
            old_qi.action_id = -1
        echoesAvatar.motionQueue.appendleft(qi)
    elif type == "print_pos":
        Logger.trace("Debug", "real avatar position after motion" + str(echoesAvatar.avatar.getRootPosition()*echoesAvatar.scale))
        Logger.trace("Debug", "avatar position after motion" + str(echoesAvatar.pos))
        if len(arg) > 0:
            Logger.trace("info", "checking isAt object" + str(arg[0]))
            if not echoesAvatar.isAt(arg[0]):
                Logger.warning("Avatar combined action: Object has been moved, action failed")
                echoesAvatar.cancelMotions = 2
                echoesAvatar.app.canvas.agentActionCompleted(arg[1], False)
            

class Paul (EchoesAvatar):

    def __init__(self, app, autoAdd=True, props={"type": "Paul"}, callback=None):
        '''
        Constructor
        '''
        super(Paul, self).__init__(app, "agents/Paul/Paul", autoAdd, props, 0.0275, callback)
        self.low = -0.8 - 1
        self.knee = -0.8 - 0.5
        self.waist = -0.8 + 0.3
        self.high = -0.8 + 1        

class Andy (EchoesAvatar):

    def __init__(self, app, autoAdd=True, props={"type": "Andy"}, callback=None):
        '''
        Constructor
        '''
        super(Andy, self).__init__(app, "agents/Andy/Andy", autoAdd, props, 0.055, callback)
        self.low = -0.8 - 1.2
        self.knee = -0.8 - 0.5
        self.waist = -0.8 + 0.3
        self.high = -0.8 + 1

    def startPostion(self):
        self.orientation = Piavca.Quat(0, Piavca.Vec.ZAxis())
        self.setPosition((6,-0.8,-5))
        
    def setDepthLayer(self, layer="front", action_id = -1):
        if layer == "front":
            self.scale = 0.055
            self.setPosition((self.pos[0], -0.8, self.pos[2]), action_id)
        elif layer == "back":
            self.scale = 0.035
            self.setPosition((self.pos[0], 0.8, self.pos[2]), action_id)
        # these are affected by the scale of the avatar, so make sure they are re-computed
        self.floorheight = None
        self.walkingDistance = self.getPlaneMotionDistance(self.walking, self.walking.getStartTime(), self.walking.getEndTime()) 
        self.stepDistance = self.getPlaneMotionDistance(self.step, self.walking.getStartTime(), self.walking.getEndTime())            
