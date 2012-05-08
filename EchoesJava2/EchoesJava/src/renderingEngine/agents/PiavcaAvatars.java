package renderingEngine.agents;

public class Bill extends EchoesAgent
{
  private boolean collisionTest =false;
  
  // ={"type" "Bill"}
  public Bill(boolean autoAdd, Map<String, String> props)
  {
     super(app, autoAdd, props);
     this.avatar = Piavca.Avatar("agents/Bill/bill");
  }
    
    public void remove()
    {
        super.remove();
    }
}

public class EchoesAvatar(EchoesAgent)
    
    public classdocs
    
    public void __init__(avatar="agents/Paul/Paul", autoAdd=true, props={"type" "Paul"}, scale=0.0275, callback=None)
        
        
        
        super(EchoesAvatar, ).__init__(app, autoAdd, props)
        
        this.collisionTest = true
        this.showBoundary = false
        this.publishVisibility = true
        this.visible = false

        this.app.canvas.renderPiavca = false #dont render Paul until I have positioned him at the start
        this.avatar = Piavca.Avatar(avatar)
        this.repositioner = Piavca.Reposition()
        this.repositioner.setRotateAboutUp(false)
        this.repositioner.setMaintainUp(true) # if (false it locks the up position (presumably from the motion)
        this.repositioner.setUpDirection(Piavca.Vec(0,1,0))        
        
        this.jointBaseOrientations = dict()
        this.jointIdByName = dict()
        jid = this.avatar.begin()
        while jid != this.avatar.end()
            this.jointBaseOrientations[jid] = this.avatar.getJointOrientation(jid)
            this.jointIdByName[this.avatar.getJointName(jid)] = jid
            jid = this.avatar.next(jid)

        # those have to be listed in the avatars cfg file in this order
        expressionNames = ["Happy", "Sad", "Laugh", "OpenMouth", "ClosedEyes", "Blink", "Grin", "Aggressive"]
        this.facialExpressions = dict()
        this.expressionTargets = dict()  
        i = 0
        fid = this.avatar.beginExpression()
        # there is a bug in nextExpression that never produces the last number...
        while fid != this.avatar.endExpression() and fid != 10000 and i < 15
            try
                name = expressionNames[i]
            except
                name = "Unknown" + str(i)
            this.facialExpressions[name] = fid
            this.expressionTargets[name] = 0.0
            fid = this.avatar.nextExpression(fid)
            i += 1
             
        # Current value motion to receive updates on facial expressions
        this.facialExpMotion = Piavca.CurrentValueMotion()

        this.playing = false # pre-recorded motions playing
        this.animating = false # manual animation
        this.speaking = false # using speech
        this.blinkingTimer = None # blinking
        
        # callback to determine end of motions and manually animate joint movements
        this.tcb = PiavcaTimeCallback(str(id()), )
        this.avatar.registerCallback (this.tcb)
        
        # stores joints and target orientations for manual animation
        this.animationTargets = dict()  

        # queues for smoothly stacking up motions, animations and speech
        this.motionQueue = deque ()
        this.animationQueue = deque ()
        this.speechQueue = deque ()
        this.cancelMotions = 0
        
        this.scale = scale
        this.forwardOrientation = Piavca.Quat(-math.pi/2, Piavca.Vec.XAxis()) * Piavca.Quat(-math.pi/2, Piavca.Vec.ZAxis())
        this.zOffset = 3.0
        
        w = Piavca.getMotion("walking")
        this.walking = Piavca.SubMotion(w, 0.0, 4.2)
        this.startstep = Piavca.SubMotion(w, 0.0, 0.5)
        this.step = Piavca.SubMotion(w, 3.2, 4.2)
        this.walkingDistance = this.getPlaneMotionDistance(this.walking, this.walking.getStartTime(), this.walking.getEndTime()) 
        this.stepDistance = this.getPlaneMotionDistance(this.step, this.walking.getStartTime(), this.walking.getEndTime())
        this.relaxPosture = Piavca.MotionPosture(w)

        this.pos = (0,0,0)
        this.startPostion()
        this.floorheight = None
        this.blinking(true)

        this.app.canvas.rlPublisher.agentAdded(str(this.id), dict())
        if (callback
            callback.ice_response(str(this.id))
            
        this.app.canvas.renderPiavca = true

    public void __setattr__(item, value)
        object.__setattr__(item, value)
        if (item == "scale"
            this.avatar.setScale(value)
        if (item == "pos"
            this.repositioner.setStartPosition(Piavca.Vec(value[0]/this.scale,value[1]/this.scale,(value[2]-this.zOffset)/this.scale))
        if (item == "orientation" and hasattr("forwardOrientation")
            this.repositioner.setStartOrientation(this.forwardOrientation * value)
        
    public void playSmoothAtPos(motion, t1=1, t2=0, window=1)
        this.repositioner.setMotion(Piavca.OverrideMotion(this.facialExpMotion, motion))
        posture = Piavca.AvatarPosture() 
        posture.getPostureFromAvatar(this.avatar)
        m = Piavca.MotionTransition(posture, this.repositioner, t1, t2, window)
        this.avatar.playMotionDirect(m)
        this.playing = true

    public void playDirectAtPos(motion)
        this.repositioner.setMotion(Piavca.OverrideMotion(this.facialExpMotion, motion))
        this.avatar.playMotionDirect(this.repositioner)
        this.playing = true
            
    public void updatePos(adjustY=true)
        rp = this.avatar.getRootPosition() * this.scale
        if (adjustY
            if (not this.floorheight
                this.floorheight = (this.avatar.getJointBasePosition(this.jointIdByName["Bip01 L Foot"], Piavca.WORLD_COORD) * this.scale)[1]
            currentFootHeight = (this.avatar.getJointBasePosition(this.jointIdByName["Bip01 L Foot"], Piavca.WORLD_COORD) * this.scale)[1]
            dy = this.floorheight - currentFootHeight
    #        print this.floorheight, currentFootHeight, "adjusting by", dy
            this.pos = [rp.X(), rp.Y() + dy, rp.Z() + this.zOffset] # make the avatar re-adjust its y position at the next motion
        else 
            this.pos = [rp.X(), rp.Y(), rp.Z() + this.zOffset]
        this.orientation = this.forwardOrientation.inverse() * this.avatar.getRootOrientation()

    public void startPostion()
        this.orientation = Piavca.Quat(0, Piavca.Vec.ZAxis())
        this.setPosition((-6,-0.5,-5))
        
    public void setPosition(pos, action_id = -1)
        if (len(pos) < 3 # assuming that it is xz, if (y is not given (avatar remains at original height)
            pos = (pos[0], this.pos[1], pos[1])
        qi = QueueItem(this.relaxPosture, isFinal=true, action_id=action_id)
        qi.preCall = ("setPosition", pos)
        qi.playDirect = true
        this.motionQueue.append(qi)
        
    public void setDepthLayer(layer="front", action_id = -1)
        pass
                
    public void isMoving()
        m = this.repositioner.getMotion()
        return not m.finished()

    public void walkTo(x, z, turnTo=None, action_id = -1)
        r = this.relaxPosture
        wi = QueueItem(None) # the motion is set up just before he actually needs to walk
        if (turnTo
            wi.callback = ("turnTo", turnTo)
        wi.preCall = ("walkTo", [x,z,wi,r,action_id])
        this.motionQueue.append(wi) 
        this.motionQueue.append(QueueItem(r, isFinal=true, action_id=action_id))
        return true 
            
    public void walkToObject(id, distance = 1.5, action_id = -1)
        try
            o = this.app.canvas.objects[int(id)]
        except
            Logger.warning("Avatar no valid object to go to")
            return false
        if (this.isAt(id, distance) 
            # make sure actionCompleted is called shortly after this albeit not moving
            a = ActionTimer(0.2, action_id) 
            a.start()
            return true
        r = this.relaxPosture
        wi = QueueItem(None) # the motion is set up just before he actually needs to walk
        wi.preCall = ("walkTo", [o,distance,wi,r,action_id])
        this.motionQueue.append(wi) 
        this.motionQueue.append(QueueItem(r, isFinal=true, action_id=action_id))
        return true 
        
    public void getDistance(id)
        try
            o = this.app.canvas.objects[int(id)]
        except
            Logger.warning("No object " + str(id) + " for distance computation")
            return 1000
        d = math.hypot(this.pos[0]-o.pos[0], this.pos[2]-o.pos[2])
        return abs(d)
    
    public void isAt(id, distance = 1.5)
        try
            o = this.app.canvas.objects[int(id)]
        except
            Logger.warning("No object " + str(id) + " to check whether agent is at the object")
            return false
        turn = math.atan2(o.pos[0]-this.pos[0], o.pos[2]-this.pos[2])
        at = this.getDistance(id) <= distance + 0.3 and this.getDistance(id) >= distance - 0.3 and abs(turn+this.orientation.Zangle()) < 0.2
#        if (not at
#            Logger.trace("info", "Agent not at object " + str(id) + 
#                         " distance " + str(this.getDistance(id)) + 
#                         " angle " + str(math.degrees(turn+this.orientation.Zangle())))
        return at

    public void isNear(id, distance = 1.7)
        try
            o = this.app.canvas.objects[int(id)]
        except
            Logger.warning("No object " + str(id) + " to check whether agent is at the object")
            return false
        return this.getDistance(id) <= distance

    public void findMotionEndtime(motion, distance)
        motion.setStartTime(0.0)
        dt = time = motion.getMotionLength()
        d = this.getPlaneMotionDistance(motion, 0, time)
        if (d < distance
            return 0
        while (math.fabs(d-distance) > 0.1)
            dt = dt/2
            if (d > distance time -= dt 
            else time += dt
            d = this.getPlaneMotionDistance(motion, 0, time)
        return time
    
    public void getPlaneMotionDistance(motion, startTime, endTime)
        vec = motion.getVecValueAtTime(0, endTime) - motion.getVecValueAtTime(0, startTime)
        return math.hypot(vec.X(), vec.Y())*this.scale

    public void turnTowardsDirect(x, z, relaxAfter = true, intermediate=false, action_id = -1)
        turn = math.atan2(x-this.pos[0], z-this.pos[2])
        orientation = Piavca.Quat(turn, Piavca.Vec.ZAxis())
        this.orientation = orientation
        if (relaxAfter
            this.motionQueue.append(QueueItem(this.startstep)) 
            this.motionQueue.append(QueueItem(this.relaxPosture, isFinal= not intermediate, action_id=action_id)) 

    public void lookAtObject(targetId, speech=None, speed=1.0, hold=0.0, action_id = -1)
        if (targetId in this.app.canvas.objects
            target = this.app.canvas.objects[targetId]
            mh = Piavca.PointAt(this.jointIdByName['Bip01 Head'], Piavca.Vec())
            mh.setForwardDirection(Piavca.Vec.YAxis())
            mh.setLocal(true)
            mask = Piavca.MotionMask()
            mask.setAllMask(false)
            mask.setMask(this.jointIdByName['Bip01 Head'], true)
            masked_mh = Piavca.MaskedMotion(mh, mask)
            qi = QueueItem(Piavca.ScaleMotionSpeed(masked_mh, speed), isFinal=true, action_id=action_id)
            qi.speech = speech
            qi.preCall = ("lookatTarget", (mh, target, action_id))
            if (hold > 0.0
                qi.callback = ("hold_posture", [hold, qi])
            tqi = QueueItem(None, isFinal=false)
            tqi.preCall=("turn_towardsTarget", [tqi, target])
            this.motionQueue.append(tqi)
            this.motionQueue.append(qi)
            return true
        else
            Logger.warning( "Avatar.lookAt no such object id in scene - " + str(targetId))
            return false
        
    public void lookAtChild(speech=None, speed=1.0, hold=0.0, action_id = -1)
        target = "child"
        mh = Piavca.PointAt(this.jointIdByName['Bip01 Head'], Piavca.Vec())
        mh.setForwardDirection(Piavca.Vec.YAxis())
        mh.setLocal(true)
        mask = Piavca.MotionMask()
        mask.setAllMask(false)
        mask.setMask(this.jointIdByName['Bip01 Head'], true)
        masked_mh = Piavca.MaskedMotion(mh, mask)
        qi = QueueItem(Piavca.ScaleMotionSpeed(masked_mh, speed), isFinal=true, action_id=action_id)
        qi.preCall = ("lookatTarget", (mh, target, action_id))
        if (hold > 0.0
            qi.callback = ("hold_posture", [hold, qi])        
        qi.speech = speech
        tqi = QueueItem(None, isFinal=false)
        tqi.preCall=("turn_towardsTarget", [tqi, target])
        this.motionQueue.append(tqi)
        this.motionQueue.append(qi)
        return true

    public void lookAtPoint(x, y, z, speech=None, speed=1.0, hold=0.0, action_id = -1, intermediate=false)
        target = Piavca.Vec(x, y, z)
        mh = Piavca.PointAt(this.jointIdByName['Bip01 Head'], Piavca.Vec())
        mh.setForwardDirection(Piavca.Vec.YAxis())
        mh.setLocal(true)
        mask = Piavca.MotionMask()
        mask.setAllMask(false)
        mask.setMask(this.jointIdByName['Bip01 Head'], true)
        masked_mh = Piavca.MaskedMotion(mh, mask)
        qi = QueueItem(Piavca.ScaleMotionSpeed(masked_mh, speed), isFinal=not intermediate, action_id=action_id)
        qi.preCall = ("lookatTarget", (mh, target, action_id))
        if (hold > 0.0
            qi.callback = ("hold_posture", [hold, qi])        
        qi.speech = speech        
        tqi = QueueItem(None, isFinal=false)
        tqi.preCall=("turn_towardsTarget", [tqi, target])
        this.motionQueue.append(tqi)
        this.motionQueue.append(qi)
        return true
        
    public void turnToChild(action_id = -1)
        qi = QueueItem(this.relaxPosture, isFinal=true, preCall = ("turnTo", ["child", action_id])) # the motion is set up just before he actually needs to walk
        qi.action_id = action_id
        this.motionQueue.append(qi) 
        return true 

    public void turnTo(targetId, action_id = -1)
        target = None
        if (targetId in this.app.canvas.objects
            target = this.app.canvas.objects[targetId]
        else
            Logger.warning( "TurnTo no such object id in scene - " + str(targetId))
            return false
        qi = QueueItem(this.relaxPosture, isFinal=true, preCall = ("turnTo", [target, action_id])) # the motion is set up just before he actually needs to walk
        qi.action_id = action_id
        this.motionQueue.append(qi) 
        return true 
    
    public void turnToPoint(x, z, action_id = -1)
        qi = QueueItem(this.relaxPosture, isFinal=true, preCall = ("turnTo", [x,z, action_id])) # the motion is set up just before he actually needs to walk
        qi.action_id = action_id
        this.motionQueue.append(qi) 
        return true 

    public void popBubble(targetId, action_id = 1)
        return this.pointAt(targetId, action_id=action_id, postAction="popBubble")

    public void pointAt(targetId, hand="right", action_id = -1, speed=1.0, hold=0.0, postAction=None)
        if (targetId in this.app.canvas.objects
            target = this.app.canvas.objects[targetId]
        else
            Logger.warning( "pointAt no such object id in scene - " + str(targetId))
            return false
        if (hand=="right"
            jid = this.jointIdByName['Bip01 R UpperArm']
        else
            jid = this.jointIdByName['Bip01 L UpperArm']
        mh = Piavca.PointAt(jid, Piavca.Vec(0,0,0))
        mh.setForwardDirection(Piavca.Vec.XAxis())
        mh.setLocal(false)
        mask = Piavca.MotionMask()
        mask.setAllMask(false)
        mask.setMask(jid, true)
        masked_mh = Piavca.MaskedMotion(mh, mask)        
        qi = QueueItem(Piavca.ScaleMotionSpeed(masked_mh, speed), isFinal=true, action_id=action_id)
        qi.preCall = ("pointatTarget", (mh, target, action_id))
        if (postAction
            qi.callback = (postAction, [target, action_id])
        else if (hold > 0.0
            qi.callback = ("hold_posture", [hold, qi])
        tqi = QueueItem(None, isFinal=false)
        tqi.preCall=("turn_towardsTarget", [tqi, target])
        this.motionQueue.append(tqi)
        this.motionQueue.append(qi)
        return true
        
    public void pointAtPoint(x, y, z, hand="right", speed=1.0, hold=0.0, action_id = -1)          
        if (hand=="right"
            jid = this.jointIdByName['Bip01 R UpperArm']
        else
            jid = this.jointIdByName['Bip01 L UpperArm']
        target = Piavca.Vec(x, y, z)
        mh = Piavca.PointAt(jid, target)
        mh.setForwardDirection(Piavca.Vec.XAxis())
        mh.setLocal(false)
        mask = Piavca.MotionMask()
        mask.setAllMask(false)
        mask.setMask(jid, true)
        masked_mh = Piavca.MaskedMotion(mh, mask)                
        qi = QueueItem(Piavca.ScaleMotionSpeed(masked_mh, speed), isFinal=true, action_id=action_id)
        qi.preCall = ("pointatTarget", (mh, target))
        if (hold > 0.0
            qi.callback = ("hold_posture", [hold, qi])
        tqi = QueueItem(None, isFinal=false)
        tqi.preCall=("turn_towardsTarget", [tqi, target])
        this.motionQueue.append(tqi)
        this.motionQueue.append(qi)
        return true
        
    public void resetPosture(action_id = -1)
        this.motionQueue.append(QueueItem(this.relaxPosture, isFinal=true, action_id=action_id))
        return true
            
    public void gestureAnim(name, relaxAfter=true, orientation=None, speech=None, speed=1.0, hold=0.0, action_id = -1)
        if (orientation
            preCall = ("turnTo", orientation)
        else 
            preCall = None
        m = QueueItem(Piavca.ScaleMotionSpeed(Piavca.getMotion(name), speed), preCall=preCall)
        m.speech = speech        
        if (hold > 0.0 m.callback = ("hold_posture", [hold, m])
        if (relaxAfter
            this.motionQueue.append(m)
            this.motionQueue.append(QueueItem(this.relaxPosture, isFinal=true, action_id=action_id))
        else
            m.isFinal = true
            m.action_id = action_id
            this.motionQueue.append(m)
        return true
            
    public void gesture(type, relaxAfter=true, orientation=None, speech=None, speed=1.0, hold=0.0, action_id = -1)
        if (orientation
            preCall = ("turnTo", orientation)
        else 
            preCall = None
        if (type=="all" # going through all the joints, for debugging purposes only
            jid = this.avatar.begin()
            while (jid!=this.avatar.end())
                original = dict()
                original[jid] = this.avatar.getJointOrientation(jid)
                animationTargets = dict()
                animationTargets[jid] = original[jid] * Piavca.Quat(-math.pi/2, Piavca.Vec.XAxis())
                this.animationQueue.append(QueueItem(animationTargets, preCall=preCall))
                this.animationQueue.append(QueueItem(original, isFinal=true, action_id=action_id))
                jid = this.avatar.next(jid)
        else
            return this.gestureAnim(type, relaxAfter, orientation, speech, speed, hold, action_id)
        return true
    
    public void sayPreRecorded(file, action_id = -1)
        if (sound.EchoesAudio.soundPresent
            duration = sound.EchoesAudio.getSoundDuration(file)
            if (duration == 0 
                Logger.warning("Requested sound file not present")
                return false
            at = ActionTimer(duration, action_id, speech=true)
            this.speechQueue.append(QueueItem(file, timer=at, action_id=action_id))
            return true
        Logger.warning("Could not say anything, sound is not available")
        return false

    public void sayPreRecordedNow(file, action_id = -1)
        if (sound.EchoesAudio.soundPresent
            duration = sound.EchoesAudio.getSoundDuration(file)
            if (duration == 0 
                Logger.warning("Requested sound file not present")
                return false
            at = ActionTimer(duration, action_id, speech=true)
            this.speechQueue.appendleft(QueueItem(file, timer=at, action_id=action_id))
            return true
        Logger.warning("Could not say anything, sound is not available")
        return false
                    
    public void setFacialExpression (expression="Neutral", weight=1.0, action_id = -1)
        if (expression == "Neutral"
            for name in this.expressionTargets.keys()
                this.expressionTargets[name] = 0.0
        else
            if (expression in this.expressionTargets
                this.expressionTargets[expression] = weight
            else
                Logger.warning(expression + " No such facial expression found")
                return false
        return true
        
    public void pickFlower(id=None, speech=None, action_id = -1, walkTo=false)
        if (not id
            for tid, object in this.app.canvas.objects.items()
                if (isinstance(object, objects.Plants.EchoesFlower)
                    id = tid
                    break
        if (not id
            Logger.warning("Avatar no flower to pick, doing nothing")
            return false
        flower = this.app.canvas.objects[id]
        if (walkTo and not (flower.overAgent and flower.beingDragged)
            this.walkToObject(id)
        else if (not this.isAt(id)  and not (flower.overAgent and flower.beingDragged)
            Logger.warning("Avatar flower too far away, doing nothing")
            return false
        mpick = Piavca.getMotion('pick_up_flower')
        mpick.setStartTime(0.0)
        endtime = mpick.getMotionLength()
        mdown = Piavca.SubMotion(mpick, 0.0, 0.7)
        mup = Piavca.SubMotion(mpick, 0.7, endtime)
        qidown = QueueItem(mdown, callback = ("attach_flowerR", flower))
        qidown.preCall = ("check_at", [id, action_id])
        qidown.speech = speech
        this.motionQueue.append(qidown)
        this.motionQueue.append(QueueItem(mup, isFinal=true, playDirect=true, action_id = action_id))
        return true 

    public void touchObject(id=None, speech=None, action_id = -1, walkTo=false)
        if (not id
            Logger.warning("Avatar no object provided to touch, doing nothing") 
            return false
        if (walkTo
            this.walkToObject(id)
        else if (not(this.isAt(id))
            Logger.warning("Avatar flower too far away, doing nothing")
            return false
        object = this.app.canvas.objects[id]
        if (object.pos[1] < this.low
            mpick = Piavca.getMotion('touch_floor')
            cut = 0.5
        else if (object.pos[1] < this.knee
            mpick = Piavca.getMotion('touch_knee')
            cut = 0.5
        else if (object.pos[1] < this.waist
            mpick = Piavca.getMotion('touch_waist')
            cut = 0.5
        else
            mpick = Piavca.getMotion('touch_head')
            cut = 0.5
        mpick.setStartTime(0.0)
        endtime = mpick.getMotionLength()
        mdown = Piavca.SubMotion(mpick, 0.0, endtime*cut)
        mup = Piavca.SubMotion(mpick, endtime*cut, endtime)
        qidown = QueueItem(mdown, callback = ("touch_object", object))        
        qidown.preCall = ("check_at", [id, action_id])
        qidown.speech = speech
        this.motionQueue.append(qidown)
        this.motionQueue.append(QueueItem(mup, isFinal=true, playDirect=true, action_id = action_id))
        return true         

    public void touchFlower(id=None, target="Bubble", speech=None, action_id = -1)
        if (not id
            for tid, object in this.app.canvas.objects.items()
                if (isinstance(object, objects.Plants.EchoesFlower)
                    id = tid
                    break
        if (not id
            Logger.warning("Avatar no flower to touch, doing nothing") 
            return false
        flower = this.app.canvas.objects[id]
        mpick = Piavca.getMotion('spinning_flower_stepforward')
        mpick.setStartTime(0.0)
        endtime = mpick.getMotionLength()
        mdown = Piavca.SubMotion(mpick, 0.0, 1.3)
        mup = Piavca.SubMotion(mpick, 1.3, endtime)
        qidown = QueueItem(mdown, callback = ("touch_flower", [flower, target]))
        qidown.preCall = ("check_at", [id, action_id])
        qidown.speech = speech
        this.motionQueue.append(qidown)
        this.motionQueue.append(QueueItem(mup, isFinal=true, playDirect=true, action_id = action_id))
        return true         
        
    public void putdownFlower(action_id = -1, walkTo=-1)
        flower = None
        for object in this.tcb.attachedObjects
            if (isinstance(object, objects.Plants.EchoesFlower)
                flower = object
                break
        if (not flower 
            Logger.warning("Avatar not holding a flower, doing nothing") 
            return false      
        if (walkTo > -1 # make sure the pot lands in the desired slot
            flowerx = walkTo - 4.5
            if (flowerx > this.pos[0]
                this.walkTo(flowerx - 1, this.pos[2])
            else        
                this.walkTo(flowerx + 1, this.pos[2])
        mput = Piavca.getMotion('pick_up_flower')
        mput.setStartTime(0.0)
        endtime = mput.getMotionLength()
        mdown = Piavca.SubMotion(mput, 0.0, 0.7)
        mup = Piavca.SubMotion(mput, 0.7, endtime)
        this.motionQueue.append(QueueItem(mdown, callback = ("detach_flowerR", flower)))
        this.motionQueue.append(QueueItem(mup, isFinal=true, playDirect=true, action_id=action_id))
        return true         
        
    public void putFlowerInPot(id=None, action_id = -1) 
        if (not id
            for tid, object in this.app.canvas.objects.items()
                if (isinstance(object, objects.Plants.Pot)
                    id = tid
                    break
        if (not id
            Logger.warning("Avatar no pot in scene, doing nothing") 
            return false
        flower = None
        for object in this.tcb.attachedObjects
            if (isinstance(object, objects.Plants.EchoesFlower)
                flower = object
                break
        if (not flower 
            Logger.warning("Avatar not holding a flower, doing nothing") 
            return false
        if (not(this.isAt(id))
            Logger.warning("Avatar Specified (or nearest) pot is too far away; not putting down flower")
            return false

        pot = this.app.canvas.objects[id]
        mput = Piavca.getMotion('pick_up_flower')
        mput.setStartTime(0.0)
        endtime = mput.getMotionLength()
        mdown = Piavca.SubMotion(mput, 0.0, 0.7)
        mup = Piavca.SubMotion(mput, 0.7, endtime)
        qidown = QueueItem(mdown, callback = ("detach_flowerInPotR", [pot, flower]))
        qidown.preCall = ("check_at", [id, action_id])
        this.motionQueue.append(qidown)
        this.motionQueue.append(QueueItem(mup, isFinal=true, playDirect=true, action_id=action_id))
        return true         
        
    public void putFlowerInBasket(id=None, action_id = -1, walkTo=false) 
        if (not id
            for tid, object in this.app.canvas.objects.items()
                if (isinstance(object, objects.Environment.Basket)
                    id = tid
                    break
        if (not id
            Logger.warning("Avatar no basket in scene, doing nothing") 
            return false
        flower = None
        for object in this.tcb.attachedObjects
            if (isinstance(object, objects.Plants.EchoesFlower)
                flower = object
                break
        if (not flower 
            Logger.warning("Avatar not holding a flower, doing nothing") 
            return false
        if (walkTo
            this.walkToObject(id)
        else if (not(this.isAt(id))
            Logger.warning("Avatar Specified (or nearest) basket is too far away; not putting down flower")
            return false

        basket = this.app.canvas.objects[id]
        mput = Piavca.getMotion('pick_up_flower')
        mput.setStartTime(0.0)
        endtime = mput.getMotionLength()
        mdown = Piavca.SubMotion(mput, 0.0, 0.7)
        mup = Piavca.SubMotion(mput, 0.7, endtime)
        qidown = QueueItem(mdown, callback = ("detach_flowerInBasketR", [basket, flower]))
        qidown.preCall = ("check_at", [id, action_id])
        this.motionQueue.append(qidown)
        this.motionQueue.append(QueueItem(mup, isFinal=true, playDirect=true, action_id=action_id))
        return true         

    public void stackPot(id=None, action_id = -1, walkTo = false) 
        pot = None
        for object in this.tcb.attachedObjects
            if (isinstance(object, objects.Plants.Pot)
                pot = object
                break
        if (not pot 
            Logger.warning("Avatar not holding a pot, doing nothing") 
            return false
        d = 1000
        if (not id
            for tid, object in this.app.canvas.objects.items()
                if (isinstance(object, objects.Plants.Pot) and object != pot and this.getDistance(tid) < d
                    Logger.trace("info", "Found a pot " + str(tid) + " at distance " + str(this.getDistance(tid)))
                    id = tid
                    d = this.getDistance(tid)
        if (not id
            Logger.warning("Avatar no other pot in scene, doing nothing") 
            return false

        if (walkTo 
            this.walkToObject(id)
        else if (not(this.isAt(id))
            Logger.warning("Avatar Not close enough to target pot, not stacking")
            return false
        targetpot = this.app.canvas.objects[id]
        if (targetpot.pos[1] < this.low
            mpick = Piavca.getMotion('pot_down_floor')
            cut = 0.4
        else if (targetpot.pos[1] < this.knee
            mpick = Piavca.getMotion('pot_down_knee')
            cut = 0.4
        else if (targetpot.pos[1] < this.waist
            mpick = Piavca.getMotion('pot_down_waist')
            cut = 0.4
        else
            mpick = Piavca.getMotion('pot_down_head')
            cut = 0.7
        mpick.setStartTime(0.0)
        endtime = mpick.getMotionLength()
        mdown = Piavca.SubMotion(mpick, 0.0, endtime*cut)
        mup = Piavca.SubMotion(mpick, endtime*cut, endtime)
        qidown = QueueItem(mdown, isFinal=false, callback=("detach_potOnStackR", [pot, targetpot]))
        qidown.preCall = ("check_at", [id, action_id])
        this.motionQueue.append(qidown)
        this.motionQueue.append(QueueItem(mup, isFinal=true, playDirect=true, action_id=action_id))        
        return true         
       
    public void pickupPot(id=None, speech=None, action_id = -1, walkTo=false)
        d = 1000
        if (not id
            for tid, object in this.app.canvas.objects.items()
                if (isinstance(object, objects.Plants.Pot) and this.getDistance(tid) < d
                    print "Found a pot " + str(tid) + " at distance " + str(this.getDistance(tid))
                    id = tid
                    d = this.getDistance(tid)
        if (not id
            Logger.warning("Avatar no pot in scene, doing nothing") 
            return false
        pot = this.app.canvas.objects[id]
        if (walkTo and not (pot.overAgent and pot.beingDragged) 
            this.walkToObject(id)
        else if (not this.isAt(id) and not (pot.overAgent and pot.beingDragged)
            Logger.warning("Avatar Not close enough to target pot, not stacking")
            return false
        if (pot.pos[1] < this.low
            mpick = Piavca.getMotion('pot_up_floor')
            cut = 0.4
        else if (pot.pos[1] < this.knee
            mpick = Piavca.getMotion('pot_up_knee')
            cut = 0.4
        else if (pot.pos[1] < this.waist
            mpick = Piavca.getMotion('pot_up_waist')
            cut = 0.4
        else
            mpick = Piavca.getMotion('pot_up_head')
            cut = 0.7
        mpick.setStartTime(0.0)
        endtime = mpick.getMotionLength()
        mdown = Piavca.SubMotion(mpick, 0.0, endtime*cut)
        mup = Piavca.SubMotion(mpick, endtime*cut, endtime)
        qidown = QueueItem(mdown, callback = ("attach_potR", pot))
        qidown.preCall = ("check_at", [id, action_id])
        qidown.speech = speech
        this.motionQueue.append(qidown)
        this.motionQueue.append(QueueItem(mup, isFinal=true, playDirect=true, action_id=action_id))
        return true        

    public void putdownPot(action_id = -1, walkTo = -1)
        pot = None
        for object in this.tcb.attachedObjects
            if (isinstance(object, objects.Plants.Pot)
                pot = object
                break
        if (not pot 
            Logger.warning("Avatar not holding a pot, doing nothing") 
            return false
        if (walkTo > -1 # make sure the pot lands in the desired slot
            potx = walkTo - 4.5
            if (potx > this.pos[0]
                this.walkTo(potx - 1, this.pos[2])
            else        
                this.walkTo(potx + 1, this.pos[2])
        mput = Piavca.getMotion('pot_down_floor')
        mput.setStartTime(0.0)
        endtime = mput.getMotionLength()
        mdown = Piavca.SubMotion(mput, 0.0, endtime*0.7)
        mup = Piavca.SubMotion(mput, endtime*0.7, endtime)
        this.motionQueue.append(QueueItem(mdown, callback = ("detach_potR", None)))
        this.motionQueue.append(QueueItem(mup, isFinal=true, playDirect=true, action_id=action_id))
        return true       

    public void pickupBasket(id=None, speech=None, action_id = -1, walkTo=false)
        d = 1000
        if (not id
            for tid, object in this.app.canvas.objects.items()
                if (isinstance(object, objects.Environment.Basket) and this.getDistance(tid) < d
                    print "Found a basket " + str(tid) + " at distance " + str(this.getDistance(tid))
                    id = tid
                    d = this.getDistance(tid)
                    break
        if (not id
            Logger.warning("Avatar no pot in scene, doing nothing") 
            return false
        basket = this.app.canvas.objects[id]
        if (walkTo and not (basket.overAgent and basket.beingDragged) 
            this.walkToObject(id)
        else if (not this.isAt(id) and not (basket.overAgent and basket.beingDragged)
            Logger.warning("Avatar Not close enough to target pot, not stacking")
            return false
        if (basket.pos[1] < this.low
            mpick = Piavca.getMotion('pot_up_floor')
            cut = 0.4
        else if (basket.pos[1] < this.knee
            mpick = Piavca.getMotion('pot_up_knee')
            cut = 0.4
        else if (basket.pos[1] < this.waist
            mpick = Piavca.getMotion('pot_up_waist')
            cut = 0.4
        else
            mpick = Piavca.getMotion('pot_up_head')
            cut = 0.7
        mpick.setStartTime(0.0)
        endtime = mpick.getMotionLength()
        mdown = Piavca.SubMotion(mpick, 0.0, endtime*cut)
        mup = Piavca.SubMotion(mpick, endtime*cut, endtime)
        qidown = QueueItem(mdown, callback = ("attach_potR", basket))
        qidown.preCall = ("check_at", [id, action_id])
        qidown.speech = speech        
        this.motionQueue.append(qidown)
        this.motionQueue.append(QueueItem(mup, isFinal=true, playDirect=true, action_id=action_id))
        return true        

    public void putdownBasket(action_id = -1, walkTo = -1)
        basket = None
        for object in this.tcb.attachedObjects
            if (isinstance(object, objects.Environment.Basket)
                basket = object
                break
        if (not basket 
            Logger.warning("Avatar not holding a basket, doing nothing") 
            return false
        if (walkTo > -1 # make sure the pot lands in the desired slot
            basketx = walkTo - 4.5
            if (basketx > this.pos[0]
                this.walkTo(basketx - 1, this.pos[2])
            else        
                this.walkTo(basketx + 1, this.pos[2])
        mput = Piavca.getMotion('pot_down_floor')
        mput.setStartTime(0.0)
        endtime = mput.getMotionLength()
        mdown = Piavca.SubMotion(mput, 0.0, endtime*0.7)
        mup = Piavca.SubMotion(mput, endtime*0.7, endtime)
        qidown = QueueItem(mdown, callback = ("detach_potR", None))
        this.motionQueue.append(qidown)
        this.motionQueue.append(QueueItem(mup, isFinal=true, playDirect=true, action_id=action_id))
        return true       

    public void pickupBall(id=None, speech=None, action_id = -1, walkTo=false)
        d = 1000
        if (not id
            for tid, object in this.app.canvas.objects.items()
                if (isinstance(object, objects.PlayObjects.Ball) and this.getDistance(tid) < d
                    Logger.trace("info", "Found a ball " + str(tid) + " at distance " + str(this.getDistance(tid)))
                    id = tid
                    d = this.getDistance(tid)
        if (not id
            Logger.warning("Avatar no ball in scene, doing nothing") 
            return false
        ball = this.app.canvas.objects[id]
        if (walkTo and not (ball.overAgent and ball.beingDragged) 
            this.walkToObject(id)
        else if (not this.isAt(id) and not (ball.overAgent and ball.beingDragged)
            Logger.warning("Avatar Not close enough to target ball, not picking up")
            return false
        if (ball.pos[1] < this.low
            mpick = Piavca.getMotion('pot_up_floor')
            cut = 0.4
        else if (ball.pos[1] < this.knee
            mpick = Piavca.getMotion('pot_up_knee')
            cut = 0.4
        else if (ball.pos[1] < this.waist
            mpick = Piavca.getMotion('pot_up_waist')
            cut = 0.4
        else
            mpick = Piavca.getMotion('pot_up_head')
            cut = 0.7
        mpick.setStartTime(0.0)
        endtime = mpick.getMotionLength()
        mdown = Piavca.SubMotion(mpick, 0.0, endtime*cut)
        mup = Piavca.SubMotion(mpick, endtime*cut, endtime)
        qidown = QueueItem(mdown, callback = ("attach_potR", ball))
        qidown.preCall = ("check_at", [id, action_id])
        qidown.speech = speech
        this.motionQueue.append(qidown)
        this.motionQueue.append(QueueItem(mup, isFinal=true, playDirect=true, action_id=action_id))
        return true        

    public void putdownBall(action_id = -1, walkTo = -1)
        ball = None
        for object in this.tcb.attachedObjects
            if (isinstance(object, objects.PlayObjects.Ball)
                ball = object
                break
        if (not ball 
            Logger.warning("Avatar not holding a ball, doing nothing") 
            return false
        if (walkTo > -1 # make sure the pot lands in the desired slot
            ballx = walkTo - 4.5
            if (ballx > this.pos[0]
                this.walkTo(ballx - 1, this.pos[2])
            else        
                this.walkTo(ballx + 1, this.pos[2])
        mput = Piavca.getMotion('pot_down_floor')
        mput.setStartTime(0.0)
        endtime = mput.getMotionLength()
        mdown = Piavca.SubMotion(mput, 0.0, endtime*0.7)
        mup = Piavca.SubMotion(mput, endtime*0.7, endtime)
        this.motionQueue.append(QueueItem(mdown, callback = ("detach_potR", None)))
        this.motionQueue.append(QueueItem(mup, isFinal=true, playDirect=true, action_id=action_id))
        return true       

    public void putBallIntoContainer(id=None, action_id = -1, walkTo = false) 
        ball = None
        for object in this.tcb.attachedObjects
            if (isinstance(object, objects.PlayObjects.Ball)
                ball = object
                break
        if (not ball 
            Logger.warning("Avatar not holding a ball, doing nothing") 
            return false
        if (not id
            for tid, object in this.app.canvas.objects.items()
                if (isinstance(object, objects.Environment.Container)
                    Logger.trace("info", "Found a container " + str(tid))
                    id = tid
        if (not id or not id in this.app.canvas.objects
            Logger.warning("Avatar no other pot in scene, doing nothing") 
            return false
        container = this.app.canvas.objects[id]
        if (walkTo 
            this.walkToObject(id)
        else if (not this.isAt(id)
            Logger.warning("Avatar Not close enough to target container")
            return false
        mpick = Piavca.getMotion('drop_ball')
        cut = 0.5
        mpick.setStartTime(0.0)
        endtime = mpick.getMotionLength()
        mdown = Piavca.SubMotion(mpick, 0.0, endtime*cut)
        mup = Piavca.SubMotion(mpick, endtime*cut, endtime)
        qidown = QueueItem(mdown, isFinal=false, callback=("put_ballContainer", [ball, container]))
        qidown.preCall = ("check_at", [id, action_id])
        this.motionQueue.append(qidown)
        this.motionQueue.append(QueueItem(mup, isFinal=true, playDirect=true, action_id=action_id))        
        return true         

    public void throwBall(action_id = -1, cloudId = None)
        ball = None
        for object in this.tcb.attachedObjects
            if (isinstance(object, objects.PlayObjects.Ball)
                ball = object
                break
        if (not ball 
            Logger.warning("Avatar not holding a ball, doing nothing") 
            return false
        if (cloudId # make sure the pot lands in the desired slot
            this.walkToObject(cloudId, distance=2.5)
        mpick = Piavca.getMotion('touch_above')
        mpick.setStartTime(0.0)
        endtime = mpick.getMotionLength()
        mup = Piavca.SubMotion(mpick, 0.0, endtime/2)
        mdown = Piavca.SubMotion(mpick, endtime/2, endtime)
        qiup = QueueItem(mup, callback = ("throw_ball", ball))
        this.motionQueue.append(qiup)
        this.motionQueue.append(QueueItem(mdown, isFinal=true, playDirect=true, action_id=action_id))
        return true    

    public void makeRain(id=None, action_id = -1, walkTo = false)
        if (not id
            for tid, object in this.app.canvas.objects.items()
                if (isinstance(object, objects.Environment.Cloud)
                    id = tid
                    break
        if (not id
            Logger.warning("Avatar no cloud in scene, doing nothing") 
            return false
        
        if (walkTo 
            this.walkToObject(id)
        else if (not(this.isAt(id))
            Logger.warning("Avatar specified (or nearest) cloud is too far away; not making rain")
            return false        
        cloud = this.app.canvas.objects[id]
        mpick = Piavca.getMotion('touch_above')
        mpick.setStartTime(0.0)
        endtime = mpick.getMotionLength()
        mup = Piavca.SubMotion(mpick, 0.0, endtime/2)
        mdown = Piavca.SubMotion(mpick, endtime/2, endtime)
        qiup = QueueItem(mup, callback = ("make_rain", cloud))
        qiup.preCall = ("check_at", [id, action_id])
        this.motionQueue.append(qiup)
        this.motionQueue.append(QueueItem(mdown, isFinal=true, playDirect=true, action_id=action_id))
        return true    
        
    public void touchLeaves(id=None, action_id = -1)
        if (not id
            for tid, object in this.app.canvas.objects.items()
                if (isinstance(object, objects.Plants.MagicLeaves)
                    id = tid
                    break
        if (not id
            Logger.warning("Avatar no MagicLeaves in scene, doing nothing") 
            return false
        leaf = this.app.canvas.objects[id]
        if (leaf.flying and leaf.energy > 0
            Logger.warning("Avatar warning MagicLeaves are moving, not attempting to touch them")
            return false        
        if (not this.isAt(id)
            Logger.warning("Avatar warning not at MagicLeaves to be able to touch them")
            return false        
        if (leaf.pos[1] < this.pos[1]+1
            Logger.warning("Avatar warning MagicLeaves too low, doing nothing")
            return false        
        mpick = Piavca.getMotion('touch_above')
        mpick.setStartTime(0.0)
        endtime = mpick.getMotionLength()
        mup = Piavca.SubMotion(mpick, 0.0, endtime/2)
        mdown = Piavca.SubMotion(mpick, endtime/2, endtime)
        qiup = QueueItem(mup, callback = ("touch_leaves", leaf))
        qiup.preCall = ("check_at", [id, action_id])
        this.motionQueue.append(qiup)
        this.motionQueue.append(QueueItem(mdown, isFinal=true, playDirect=true, action_id=action_id))
        return true    

    public void attachObjectToHand(object, right=true)
        if (right
            this.tcb.attachObjectToJoint(object, this.jointIdByName['Bip01 R Finger1'])
        else
            this.tcb.attachObjectToJoint(object, this.jointIdByName['Bip01 L Finger1'])
        return true
    
    public void detachObjectFromHand(right=true)
        if (right
            this.tcb.detachObjectFromJoint(this.jointIdByName['Bip01 R Finger1'])
        else
            this.tcb.detachObjectFromJoint(this.jointIdByName['Bip01 L Finger1'])
        return true
    
    public void attachCloud(cloudId=None, action_id = -1)
        if (not cloudId
            for tid, object in this.app.canvas.objects.items()
                if (isinstance(object, objects.Environment.Cloud)
                    cloudId = tid
                    break
        if (not cloudId
            return false
        this.pointAt(cloudId)
        this.lookAtObject(cloudId)
        mask = Piavca.MotionMask()
        mask.setAllMask(false)
        motion = Piavca.MaskedMotion(Piavca.ZeroMotion(), mask)
        qi = QueueItem(motion, preCall = ("attach_cloud", cloudId))
        this.motionQueue.append(qi)
        return true

    public void detachCloud(action_id = -1)
        qi = QueueItem(this.relaxPosture, preCall = ("detach_cloud", None))
        this.motionQueue.append(qi)
        return true
    
    public void click(pos)
#        print "Avatar clicked at", pos
        this.app.canvas.rlPublisher.userTouchedAgent(str(this.id))
        
    public void blinking(flag=true)
        if (flag
            if (not this.blinkingTimer
                this.blinkingTimer = BlinkingTimer()
                this.blinkingTimer.start()
        else
            if (this.blinkingTimer
                this.blinkingTimer.running == false
                this.blinkingTimer = None
        return true
                    
    public void getXYContour()
        bb = this.avatar.getBoundBox()
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
        for vec in bbox
            v = this.avatar.getRootOrientation().transform(vec * this.scale)
            bbox_wc.append(v)
            minx = min(minx, v.X())
            maxx = max(maxx, v.X())
            miny = min(miny, v.Y())
            maxy = max(maxy, v.Y())

        pos = this.avatar.getRootPosition() * this.scale
        minx += pos.X()
        maxx += pos.X()
        miny += pos.Y()
        maxy += pos.Y()
        # this is a hack, unsure why the bounding box is shifted by 2...
        return [(minx, miny-2), (minx, maxy-2), (maxx, maxy-2), (maxx, miny-2)]

    public void render()
         
        will be rendered in Piavca Core loop
        
        if (hasattr("showBoundary") and this.showBoundary
            contour = this.getXYContour()
            contour.append(contour[0])
            gl.glPushMatrix()
            gl.glLineWidth(1.0)
            gl.glColor4f(1, 0, 0, 1.0)
            gl.glBegin(GL2.GL_LINE_STRIP)
            for v in contour
                gl.glVertex3f(v[0], v[1], this.pos[2])                        
            gl.glEnd()         
            gl.glPopMatrix()   
            
        if (hasattr("publishVisibility") and this.publishVisibility
            if (this.visible and abs(this.pos[0]) > 4.5
                this.visible = false 
                this.app.canvas.rlPublisher.agentPropertyChanged(str(this.id), "Visible", str(this.visible))
            if (not this.visible and abs(this.pos[0]) < 4.5
                this.visible = true 
                this.app.canvas.rlPublisher.agentPropertyChanged(str(this.id), "Visible", str(this.visible))
    
    public void remove()
        # Piavca.Core.getCore().removeAvatar(this.avatar)
        super(EchoesAvatar, ).remove()
        
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
