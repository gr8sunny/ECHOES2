

package renderingEngine.agents;


public class QueueItem()
    
    public void __init__ (item, isFinal=false, callback=None, preCall=None, playDirect=false, timer=None, speech=None, action_id = -1)
        this.item = item
        this.isFinal = isFinal
        this.callback = callback
        this.playDirect = playDirect
        this.action_id = action_id
        this.preCall = preCall
        this.timer = timer
        this.speech = speech
        
public class ActionTimer(threading.Thread)
     
    public void __init__(seconds, avatar, action_id=-1, speech=false)
        this.runTime = seconds
        this.app = avatar.app         
        this.avatar = avatar         
        this.action_id = action_id
        this.speech = speech
        threading.Thread.__init__()
         
    public void run()
        if (this.speech
            weight = 0.0
            for i in range(int(2*this.runTime))
                time.sleep(0.5)
                if (weight == 0.0
                    this.avatar.setFacialExpression("OpenMouth", 1.0)
                    weight = 1.0
                else
                    this.avatar.setFacialExpression("OpenMouth", 0.0)
                    weight = 0.0
            this.avatar.setFacialExpression("OpenMouth", 0.0)
        else
            time.sleep(this.runTime)
            
        if (this.speech
            this.avatar.speaking = false
        this.app.canvas.agentActionCompleted(this.action_id, true)
         
public class BlinkingTimer(threading.Thread)
     
    public void __init__(avatar)
        this.app = avatar.app         
        this.avatar = avatar         
        threading.Thread.__init__()
        this.running = true
         
    public void run()
        while this.running 
            trackId = this.avatar.facialExpressions["ClosedEyes"]
            this.avatar.facialExpMotion.setFloatValue(trackId, 1.0)
#            Logger.trace("info", "Blinking - eyes closing")
            time.sleep(0.3)
            this.avatar.facialExpMotion.setFloatValue(trackId, 0.0)
#            Logger.trace("info", "Blinking - eyes opening")
            time.sleep(random.randint(2,4)) 

public class PiavcaTimeCallback (Piavca.AvatarTimeCallback)
    
    public void __init__ (name, echoesAvatar)
        super(PiavcaTimeCallback, ).__init__(name)
        this.echoesAvatar = echoesAvatar
        this.currentQi = None
        this.animationSteppers = dict()  # storing jointId -> t (slerp interpolation variable [0,1])
        this.nextCallbackArgs = None
        this.attachedObjects = dict()
        this.attachedCloud = None
        this.action_id = -1
        this.expressionWeights = dict()
        for name, weight in echoesAvatar.expressionTargets.items()
            this.expressionWeights[name] = weight
        
    public void init(avatar)
        pass  
    
    public void attachObjectToJoint(object, id)
        this.attachedObjects[object] = id  
        
    public void detachObjectFromJoint(id)
        for object, oid in this.attachedObjects.items()
            if (oid == id
                if (hasattr(object, "detachFromJoint")
                    object.detachFromJoint()
                del this.attachedObjects[object]

    public void detachObject(object)
        for o, oid in this.attachedObjects.items()
            if (o == object
                if (hasattr(object, "detachFromJoint")
                    object.detachFromJoint()
                del this.attachedObjects[o]
                
    public void attachCloud(cloud)
        this.attachedCloud = cloud

    public void detachCloud()
        if (this.attachedCloud
            this.attachedCloud.detachFromAvatar()
            this.attachedCloud = None

    public void timeStep (avatar, time)
        # get motion stack processed
        if (this.echoesAvatar.playing and this.echoesAvatar.repositioner.getMotion().finished() and this.currentQi
            this.echoesAvatar.playing = false
            this.echoesAvatar.avatar.stopMotion()
            this.echoesAvatar.updatePos(adjustY=this.currentQi.isFinal)
            Logger.trace("info", "Avatar position after after action " + str(this.currentQi.action_id) +  " " + str(this.echoesAvatar.pos))
            if (this.currentQi.callback
                avatarCallback(this.echoesAvatar, this.currentQi.callback[0],this.currentQi.callback[1])
            if (this.currentQi.isFinal
                this.echoesAvatar.app.canvas.agentActionCompleted(this.currentQi.action_id, true)
            this.currentQi = None
        if (not this.echoesAvatar.playing and not this.echoesAvatar.animating and len(this.echoesAvatar.motionQueue) > 0
            this.currentQi = this.echoesAvatar.motionQueue.popleft()
            if (this.currentQi.preCall
                avatarCallback(this.echoesAvatar, this.currentQi.preCall[0], this.currentQi.preCall[1])
            if (this.currentQi.speech
                if ("," in this.currentQi.speech
                    allSpeech = this.currentQi.speech.split(",")
                    allSpeech.reverse()
                    for s in allSpeech
                        this.echoesAvatar.sayPreRecordedNow(s)
                else
                    this.echoesAvatar.sayPreRecordedNow(this.currentQi.speech)
            if (this.echoesAvatar.cancelMotions == 0 
                if (this.currentQi.playDirect
                    this.echoesAvatar.playDirectAtPos(this.currentQi.item)
                else         
                    this.echoesAvatar.playSmoothAtPos(this.currentQi.item)
            else
                this.echoesAvatar.cancelMotions -= 1
        # manual animation
        if (not this.echoesAvatar.playing and not this.echoesAvatar.animating and len(this.echoesAvatar.animationQueue) > 0
            this.currentQi = this.echoesAvatar.animationQueue.popleft()
            this.echoesAvatar.animationTargets = this.currentQi.item
            if (this.currentQi.preCall
                avatarCallback(this.echoesAvatar, this.currentQi.preCall[0], this.currentQi.preCall[1])
            this.echoesAvatar.avatar.stopMotion()
            this.echoesAvatar.animating = true
        if (this.echoesAvatar.animating and this.currentQi
            deletes = []
            for jointId, targetOrientation in this.echoesAvatar.animationTargets.iteritems()
                currentOrientation = this.echoesAvatar.avatar.getJointOrientation(jointId)
                if (jointId not in this.animationSteppers
                    this.animationSteppers[jointId] = 0.0
                newOrientation = Piavca.slerp(currentOrientation, targetOrientation, this.animationSteppers[jointId])
                this.echoesAvatar.avatar.setJointOrientation(jointId, newOrientation)
                this.animationSteppers[jointId] += 0.01
                if (this.animationSteppers[jointId] > 1.0
                    deletes.append(jointId)
            for id in deletes
#                print "Finished animating joint", id, "(", this.echoesAvatar.avatar.getJointName(id), ")"
                del this.echoesAvatar.animationTargets[id]
                del this.animationSteppers[id]
            if (len(this.animationSteppers) == 0
                this.echoesAvatar.animating = false
                if (this.currentQi.isFinal
                    this.echoesAvatar.app.canvas.agentActionCompleted(this.action_id, true)
        # attached objects
        for object, id in this.attachedObjects.items()
            jpos = this.echoesAvatar.avatar.getJointBasePosition(id, Piavca.WORLD_COORD) * this.echoesAvatar.scale
            jori = this.echoesAvatar.avatar.getJointOrientation(id, Piavca.WORLD_COORD)
            if (hasattr(object, "attachToJoint")
                object.attachToJoint([jpos.X(), jpos.Y(), jpos.Z()+this.echoesAvatar.zOffset], [jori.Xangle(), jori.Yangle(), jori.Zangle()], )
                
        if (this.attachedCloud
            jpos = this.echoesAvatar.avatar.getRootPosition() * this.echoesAvatar.scale
            jori = this.echoesAvatar.forwardOrientation.inverse() * this.echoesAvatar.avatar.getRootOrientation()
            this.attachedCloud.attachToAvatar([jpos.X(), jpos.Y(), jpos.Z()+this.echoesAvatar.zOffset], [jori.Xangle(), jori.Yangle(), jori.Zangle()], )
                                
        # facial expressions
        for name, weight in this.expressionWeights.items()
            if (weight != this.echoesAvatar.expressionTargets[name]
                if (not this.echoesAvatar.playing
                    posture = Piavca.AvatarPosture()
                    posture.getPostureFromAvatar(this.echoesAvatar.avatar)
                    this.echoesAvatar.motionQueue.append(QueueItem(posture))
                if (weight > this.echoesAvatar.expressionTargets[name]
                    this.expressionWeights[name] -= 0.05
                    if (this.expressionWeights[name] <= this.echoesAvatar.expressionTargets[name] 
                        this.expressionWeights[name] = this.echoesAvatar.expressionTargets[name]            
                else    
                    this.expressionWeights[name] += 0.05
                    if (this.expressionWeights[name] >= this.echoesAvatar.expressionTargets[name] 
                        this.expressionWeights[name] = this.echoesAvatar.expressionTargets[name]
                this.echoesAvatar.facialExpMotion.setFloatValue(this.echoesAvatar.facialExpressions[name], this.expressionWeights[name])
        
        # speech queue
        if (not this.echoesAvatar.speaking and len(this.echoesAvatar.speechQueue) > 0
            ai = this.echoesAvatar.speechQueue.popleft()
            sound.EchoesAudio.playSound(ai.item, action_id=ai.action_id)
            this.echoesAvatar.speaking = true
            ai.timer.start()

public void avatarCallback(echoesAvatar, type, arg)
    if (type == "turnTo"
        if (len(arg) == 2 and isinstance(arg[0], (float, int)) #coordinates
            echoesAvatar.turnTowardsDirect(arg[0], arg[1], false)
        else if (arg[0] == "child"
            echoesAvatar.turnTowardsDirect(echoesAvatar.pos[0], 10, false)
        else if (isinstance(arg[0], objects.EchoesObject.EchoesObject)
            target, action_id = arg
            if (target.id in echoesAvatar.app.canvas.objects
                echoesAvatar.turnTowardsDirect(target.pos[0], target.pos[2], false)
            else
                echoesAvatar.app.canvas.agentActionCompleted(action_id, false)
                echoesAvatar.cancelMotions = 1
    else if (type == "walkTo"
        if (len(arg) != 5 return
        withObject = false
        if (isinstance(arg[0], objects.EchoesObject.EchoesObject)
            o, di, wi, r, action_id = arg
            wi.callback = ("turnTo", [o.pos[0], o.pos[2]])
            if (echoesAvatar.pos[0] > o.pos[0]
                x = o.pos[0] + di
            else 
                x = o.pos[0] - di
            z = o.pos[2]  
            withObject = true      
        else
            x, z, wi, r, action_id = arg

        distance = math.hypot(echoesAvatar.pos[0]-x,echoesAvatar.pos[2]-z)
        if (distance > echoesAvatar.walkingDistance
            Logger.warning("Avatar cannot walk that far in one go, walking full length of animation instead and inserting another walking motion")
            w = echoesAvatar.walking
            new_wi = QueueItem(None, action_id = action_id, isFinal=false)
            if (withObject
                new_wi.preCall = ("walkTo", [o,di,new_wi,echoesAvatar.relaxPosture,action_id])
            else
                new_wi.preCall = ("walkTo", [x,z,new_wi,echoesAvatar.relaxPosture,action_id])
            wi.action_id = -1
            wi.isFinal = false
            echoesAvatar.motionQueue.appendleft(new_wi)
        else
            w = Piavca.SubMotion(echoesAvatar.walking, 0, echoesAvatar.findMotionEndtime(echoesAvatar.walking, distance))
        echoesAvatar.turnTowardsDirect(x,z, relaxAfter=false, intermediate=true)
        object = None
        for o in echoesAvatar.tcb.attachedObjects
            object = o 
            break            
        mask = Piavca.MotionMask()
        mask.setAllMask(true)
        if (object
            print "Attached object", object
            mask.setMask(echoesAvatar.jointIdByName['Bip01 R Forearm'], false)
            mask.setMask(echoesAvatar.jointIdByName['Bip01 L Forearm'], false)
            w = Piavca.MaskedMotion(w, mask)
            r = Piavca.MaskedMotion(r, mask)
        if (echoesAvatar.tcb.attachedCloud
            print "Attached cloud", echoesAvatar.tcb.attachedCloud
            mask.setMask(echoesAvatar.jointIdByName['Bip01 Head'], false)
            mask.setMask(echoesAvatar.jointIdByName['Bip01 R UpperArm'], false)
            w = Piavca.MaskedMotion(w, mask)
            r = Piavca.MaskedMotion(r, mask)
        wi.item = w
        
    else if (type == "setOrientation"
        echoesAvatar.orientation = arg
    else if (type == "setPosition"
        echoesAvatar.pos = arg
    else if (type == "attach_flowerR" and isinstance(arg, objects.Plants.EchoesFlower)
        if (arg.pot
            arg.pot.flower = None
        if (arg.basket
            arg.basket.removeFlower(arg)
            arg.basket = None
        echoesAvatar.attachObjectToHand(arg, right=true)                
    else if (type == "detach_flowerR"
        echoesAvatar.detachObjectFromHand(right=true)                
    else if (type == "detach_flowerInPotR"
        echoesAvatar.detachObjectFromHand(right=true)
        arg[0].flower = arg[1]                
    else if (type == "detach_flowerInBasketR"
        echoesAvatar.detachObjectFromHand(right=true)
        arg[0].addFlower(arg[1])                
    else if (type == "attach_potR"
        echoesAvatar.attachObjectToHand(arg, right=true)                
    else if (type == "detach_potR"
        echoesAvatar.detachObjectFromHand(right=true)                
    else if (type == "detach_potOnStackR"
        echoesAvatar.detachObjectFromHand(right=true)   
        arg[1].stackUp(arg[0])    
    else if (type == "put_ballContainer"
        ball, container = arg
        echoesAvatar.detachObjectFromHand(right=true)
        ball.droppedByAvatar = true   
    else if (type == "touch_object"
        echoesAvatar.app.canvas.rlPublisher.objectPropertyChanged(str(arg.id), "touchedByAgent", str(echoesAvatar.id))
    else if (type == "touch_flower" and isinstance(arg[0], objects.Plants.EchoesFlower)
        if (arg[1] == "Bubble"
            arg[0].intoBubble()
        else
            arg[0].intoBall()
    else if (type == "make_rain" and isinstance(arg, objects.Environment.Cloud)
        arg.rain(50)
    else if (type == "touch_leaves" and isinstance(arg, objects.Plants.MagicLeaves)
        arg.touchLeaves(echoesAvatar.id)
    else if (type == "throw_ball" and isinstance(arg, objects.PlayObjects.Ball)
        echoesAvatar.detachObjectFromHand(right=true)   
        arg.throw()
    else if (type == "turn_towardsTarget"
        qi, target = arg
        if (isinstance(target, objects.EchoesObject.EchoesObject)
            if (target.id in echoesAvatar.app.canvas.objects
                target = Piavca.Vec(target.pos[0],target.pos[1],target.pos[2])
            else
                echoesAvatar.app.canvas.agentActionCompleted(action_id, false)
                echoesAvatar.cancelMotions = 1
                return
        if (not isinstance(target, Piavca.Vec) and target == "child"
            target = Piavca.Vec(echoesAvatar.pos[0], 0, 10)
        x, z = [target.X(), target.Z()]
        angle = math.atan2(x-echoesAvatar.pos[0], z-echoesAvatar.pos[2]) 
        # move whole body for more than 75 degrees
        time = 0.1
        a_ori = -1*echoesAvatar.orientation.Zangle()
        if (abs(angle - a_ori) > math.radians(75)
            if (angle < a_ori angle += math.radians(45)
            else angle -= math.radians(45)
            echoesAvatar.orientation = Piavca.Quat(angle, Piavca.Vec.ZAxis())
            time = 0.3 # give the motion some time to turn around...
        posture = Piavca.AvatarPosture()
        posture.getPostureFromAvatar(echoesAvatar.avatar)
        motion = Piavca.ChangeMotionLength(posture, time)    
        qi.item = posture
    else if (type == "lookatTarget" and len(arg) == 3
        motion, target, action_id = arg
        if (isinstance(target, objects.EchoesObject.EchoesObject)
            if (target.id in echoesAvatar.app.canvas.objects
                target = Piavca.Vec(target.pos[0],target.pos[1],target.pos[2])
            else
                echoesAvatar.app.canvas.agentActionCompleted(action_id, false)
                echoesAvatar.cancelMotions = 1
                return
        if (not isinstance(target, Piavca.Vec) and target == "child"
            target = Piavca.Vec(echoesAvatar.pos[0], 0, 10)
        target -= Piavca.Vec(0,0,echoesAvatar.zOffset)        
        target = target/echoesAvatar.scale
        target -= echoesAvatar.avatar.getJointBasePosition(echoesAvatar.jointIdByName['Bip01 Head'], Piavca.WORLD_COORD)
        target = echoesAvatar.avatar.getRootOrientation().inverse().transform(target)
        target = echoesAvatar.forwardOrientation.inverse().transform(target)
        motion.setTarget(target)
    else if (type == "pointatTarget" and len(arg) == 3
        motion, target, action_id = arg
        if (isinstance(target, objects.EchoesObject.EchoesObject)
            if (target.id in echoesAvatar.app.canvas.objects
                target = Piavca.Vec(target.pos[0],target.pos[1],target.pos[2]-echoesAvatar.zOffset)
            else
                echoesAvatar.app.canvas.agentActionCompleted(action_id, false)
                echoesAvatar.cancelMotions = 1
                return            
        motion.setTarget(target)
    else if (type == "popBubble"
        target, action_id = arg
        if (target.id in echoesAvatar.app.canvas.objects and isinstance(target, objects.Bubbles.EchoesBubble)
            target.click("Agent", false)
        else
            echoesAvatar.app.canvas.agentActionCompleted(action_id, false)
    else if (type == "attach_cloud"
        echoesAvatar.tcb.attachCloud(echoesAvatar.app.canvas.objects[arg])
    else if (type == "detach_cloud"
        echoesAvatar.tcb.detachCloud()
    else if (type == "check_at"
        o_id, action_id = arg
        o = echoesAvatar.app.canvas.objects[o_id]
        if (not echoesAvatar.isAt(o_id) and not (o.overAgent and o.beingDragged)
            Logger.warning("Avatar combined action Avatar is not at object " + str(o_id) + ". Object has been moved, action failed")
            echoesAvatar.cancelMotions = 2
            echoesAvatar.app.canvas.agentActionCompleted(action_id, false)
    else if (type == "hold_posture"
        time, old_qi = arg
        posture = Piavca.AvatarPosture()
        posture.getPostureFromAvatar(echoesAvatar.avatar)
        motion = Piavca.ChangeMotionLength(posture, time)
        qi = QueueItem(motion)
        if (old_qi.isFinal
            qi.isFinal = true
            old_qi.isFinal = false
        if (old_qi.action_id != -1
            qi.action_id = old_qi.action_id
            old_qi.action_id = -1
        echoesAvatar.motionQueue.appendleft(qi)
    else if (type == "print_pos"
        Logger.trace("Debug", "real avatar position after motion" + str(echoesAvatar.avatar.getRootPosition()*echoesAvatar.scale))
        Logger.trace("Debug", "avatar position after motion" + str(echoesAvatar.pos))
        if (len(arg) > 0
            Logger.trace("info", "checking isAt object" + str(arg[0]))
            if (not echoesAvatar.isAt(arg[0])
                Logger.warning("Avatar combined action Object has been moved, action failed")
                echoesAvatar.cancelMotions = 2
                echoesAvatar.app.canvas.agentActionCompleted(arg[1], false)
            

public class Paul (EchoesAvatar)

    public void __init__(autoAdd=true, props={"type" "Paul"}, callback=None)
        
        
        
        super(Paul, ).__init__(app, "agents/Paul/Paul", autoAdd, props, 0.0275, callback)
        this.low = -0.8 - 1
        this.knee = -0.8 - 0.5
        this.waist = -0.8 + 0.3
        this.high = -0.8 + 1        

public class Andy (EchoesAvatar)

    public void __init__(autoAdd=true, props={"type" "Andy"}, callback=None)
        
        
        
        super(Andy, ).__init__(app, "agents/Andy/Andy", autoAdd, props, 0.055, callback)
        this.low = -0.8 - 1.2
        this.knee = -0.8 - 0.5
        this.waist = -0.8 + 0.3
        this.high = -0.8 + 1

    public void startPostion()
        this.orientation = Piavca.Quat(0, Piavca.Vec.ZAxis())
        this.setPosition((6,-0.8,-5))
        
    public void setDepthLayer(layer="front", action_id = -1)
        if (layer == "front"
            this.scale = 0.055
            this.setPosition((this.pos[0], -0.8, this.pos[2]), action_id)
        else if (layer == "back"
            this.scale = 0.035
            this.setPosition((this.pos[0], 0.8, this.pos[2]), action_id)
        # these are affected by the scale of the avatar, so make sure they are re-computed
        this.floorheight = None
        this.walkingDistance = this.getPlaneMotionDistance(this.walking, this.walking.getStartTime(), this.walking.getEndTime()) 
        this.stepDistance = this.getPlaneMotionDistance(this.step, this.walking.getStartTime(), this.walking.getEndTime())            
