package agents;
//Translation from Py May 9th
//class file created out of PiavcaAvatars.java
public class EchoesAvatar extends EchoesAgent
{    
    //public classdocs
    //EchoesAvatar(avatar="agents/Paul/Paul", autoAdd=true, props={"type" "Paul"}, scale=0.0275, callback=None
    public void EchoesAvatar(String avatar, boolean autoAdd=true, Map<String, String> properties ,float scale, Object callback)
    {    
        super(autoAdd, properties)
        
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
        while( jid != this.avatar.end())
        {
        	this.jointBaseOrientations[jid] = this.avatar.getJointOrientation(jid);
            this.jointIdByName[this.avatar.getJointName(jid)] = jid;
            jid = this.avatar.next(jid);
        }
        // those have to be listed in the avatars cfg file in this order
        String [] expressionNames = {"Happy", "Sad", "Laugh", "OpenMouth", "ClosedEyes", "Blink", "Grin", "Aggressive"};
        this.facialExpressions = dict()
        this.expressionTargets = dict()  
        i = 0
        fid = this.avatar.beginExpression()
        // there is a bug in nextExpression that never produces the last number...
        while fid != this.avatar.endExpression() and fid != 10000 and i < 15
            try
                name = expressionNames[i]
            except
                name = "Unknown" + str(i)
            this.facialExpressions[name] = fid
            this.expressionTargets[name] = 0.0
            fid = this.avatar.nextExpression(fid)
            i += 1
             
        // Current value motion to receive updates on facial expressions
        this.facialExpMotion = Piavca.CurrentValueMotion()

        this.playing = false # pre-recorded motions playing
        this.animating = false # manual animation
        this.speaking = false # using speech
        this.blinkingTimer = None # blinking
        
        // callback to determine end of motions and manually animate joint movements
        this.tcb = PiavcaTimeCallback(str(id()), )
        this.avatar.registerCallback (this.tcb)
        
        // stores joints and target orientations for manual animation
        this.animationTargets = dict()  

        // queues for smoothly stacking up motions, animations and speech
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
        //******************this is using ice :O
        if (callback)
        {
        	callback.ice_response(str(this.id));
        }
            
        this.app.canvas.renderPiavca = true;
    }
    //public void __setattr__(item, value)
    public void setAttr(String item, String value)
    {
        object.__setattr__(item, value); //********object?
        if (item == "scale")
        {
        		this.avatar.setScale(value);
        }
        if (item == "pos")
        {
        		this.repositioner.setStartPosition(Piavca.Vec(value[0]/this.scale,value[1]/this.scale,(value[2]-this.zOffset)/this.scale));
        }
        if (item == "orientation" and hasattr("forwardOrientation"))
        {
        	this.repositioner.setStartOrientation(this.forwardOrientation * value);
        }
    }    
    //playSmoothAtPos(motion, t1=1, t2=0, window=1)
    public void playSmoothAtPos(motion, t1=1, t2=0, window=1)
    {
    	this.repositioner.setMotion(Piavca.OverrideMotion(this.facialExpMotion, motion));
        posture = Piavca.AvatarPosture() ;
        posture.getPostureFromAvatar(this.avatar);
        m = Piavca.MotionTransition(posture, this.repositioner, t1, t2, window);
        this.avatar.playMotionDirect(m);
        this.playing = true;
    } 
    public void playDirectAtPos(motion)
    {
    	this.repositioner.setMotion(Piavca.OverrideMotion(this.facialExpMotion, motion));
        this.avatar.playMotionDirect(this.repositioner);
        this.playing = true;
    }       
    public void updatePos(adjustY=true)
    {
    	rp = this.avatar.getRootPosition() * this.scale;
        if (adjustY)
        {
        	if (! this.floorheight)
            {
            	this.floorheight = (this.avatar.getJointBasePosition(this.jointIdByName["Bip01 L Foot"], Piavca.WORLD_COORD) * this.scale)[1]
            }
            currentFootHeight = (this.avatar.getJointBasePosition(this.jointIdByName["Bip01 L Foot"], Piavca.WORLD_COORD) * this.scale)[1]
            dy = this.floorheight - currentFootHeight
    //        print this.floorheight, currentFootHeight, "adjusting by", dy
            this.pos = [rp.X(), rp.Y() + dy, rp.Z() + this.zOffset] # make the avatar re-adjust its y position at the next motion
        }
        else 
        {
        	this.pos = [rp.X(), rp.Y(), rp.Z() + this.zOffset];
        }
        this.orientation = this.forwardOrientation.inverse() * this.avatar.getRootOrientation();
    }
    public void startPostion()
    {
    	this.orientation = Piavca.Quat(0, Piavca.Vec.ZAxis());
        this.setPosition((-6,-0.5,-5));
    }    
    public void setPosition(pos, action_id = -1)
    {
    	if (len(pos) < 3) // assuming that it is xz, if (y is not given (avatar remains at original height)
        {
        	pos = (pos[0], this.pos[1], pos[1])
        }
        qi = QueueItem(this.relaxPosture, isFinal=true, action_id=action_id);
        qi.preCall = ("setPosition", pos);
        qi.playDirect = true;
        this.motionQueue.append(qi);
    }    
    //setDepthLayer(String layer="front", int action_id = -1)
    public void setDepthLayer(String layer, int action_id)
    {
    	//pass
    }
                
    public void isMoving()
    {
    	m = this.repositioner.getMotion()
        return !m.finished()
    }
    
    public void walkTo(x, z, turnTo=None, action_id = -1)
    {
    	r = this.relaxPosture;
        wi = QueueItem(None); // the motion is set up just before he actually needs to walk
        if (turnTo)
        {
        	wi.callback = ("turnTo", turnTo);
        }
        wi.preCall = ("walkTo", [x,z,wi,r,action_id])
        this.motionQueue.append(wi) 
        this.motionQueue.append(QueueItem(r, isFinal=true, action_id=action_id))
        return true 
    }  
    public void walkToObject(id, distance = 1.5, action_id = -1)
    {
    	try
    	{
    		o = this.app.canvas.objects[int(id)];
    	}
        catch ()//****which exception?
        {
            Logger.warning("Avatar no valid object to go to")
            return false
        }
        if (this.isAt(id, distance)) 
        {    // make sure actionCompleted is called shortly after this albeit not moving
            a = ActionTimer(0.2, action_id); 
            a.start();
            return true;
        }
        r = this.relaxPosture;
        wi = QueueItem(None); // the motion is set up just before he actually needs to walk
        wi.preCall = ("walkTo", [o,distance,wi,r,action_id])
        /*
         * Here motionQueue is a list and so append will work...but how to do it in Java? 
         */
        this.motionQueue.append(wi);//***********How to handle queues? 
        this.motionQueue.append(QueueItem(r, isFinal=true, action_id=action_id))
        return true; 
    }
    public double getDistance(int id)
    {
    	try
    	{
    		o = this.app.canvas.objects[int(id)];
    	}
        catch () //****which exception?
        {
        	Logger.warning("No object " + str(id) + " for distance computation");
            return 1000;
        }
        d = Math.hypot(this.pos[0]-o.pos[0], this.pos[2]-o.pos[2]);
        return abs(d);
    }
    //isAt(id, distance = 1.5)
    public boolean isAt(int id, float distance)
    {
    	try
    	{
    		o = this.app.canvas.objects[int(id)];
    	}
        catch() //***Which exception?
        {   
        	Logger.warning("No object " + str(id) + " to check whether agent is at the object");
            return false;
        }
        turn = Math.atan2(o.pos[0]-this.pos[0], o.pos[2]-this.pos[2]);
        at = this.getDistance(id) <= distance + 0.3 && this.getDistance(id) >= distance - 0.3 && abs(turn+this.orientation.Zangle()) < 0.2;
//        if (not at
//            Logger.trace("info", "Agent not at object " + str(id) + 
//                         " distance " + str(this.getDistance(id)) + 
//                         " angle " + str(math.degrees(turn+this.orientation.Zangle())))
        return at;
    }
    //isNear(int id, float distance = 1.7)
    public boolean isNear(int id, float distance)
    {
    	try
    	{
    		o = this.app.canvas.objects[int(id)];
    	}
        catch () //***Which exception?
        {
        	Logger.warning("No object " + str(id) + " to check whether agent is at the object");
            return false;
        }
        return this.getDistance(id) <= distance;
    }
    
    public float findMotionEndtime(motion, distance)
    {
    	motion.setStartTime(0.0);
        dt = time = motion.getMotionLength();
        d = this.getPlaneMotionDistance(motion, 0, time);
        if (d < distance)
        {
        	return 0;
        }
        while (Math.fabs(d-distance) > 0.1)
        {
        	dt = dt/2;
            if (d > distance)
            {
            	time -= dt; 
            }
            else
            {
            	time += dt;
            }
            d = this.getPlaneMotionDistance(motion, 0, time);
        }
        return time;
    }
    public float getPlaneMotionDistance(motion, startTime, endTime)
    {    
    	vec = motion.getVecValueAtTime(0, endTime) - motion.getVecValueAtTime(0, startTime);
        return Math.hypot(vec.X(), vec.Y())*this.scale;
    }
    //turnTowardsDirect(x, z, relaxAfter = true, intermediate=false, action_id = -1)
    public void turnTowardsDirect(float x, float z, boolean relaxAfter, boolean intermediate, int action_id )
    {
    	turn = Math.atan2(x-this.pos[0], z-this.pos[2]);
        orientation = Piavca.Quat(turn, Piavca.Vec.ZAxis());
        this.orientation = orientation;
        if (relaxAfter)
        {    
        	this.motionQueue.append(QueueItem(this.startstep)); 
            this.motionQueue.append(QueueItem(this.relaxPosture, isFinal= not intermediate, action_id=action_id)); 
        }
    }
    //lookAtObject(int targetId, Object speech=None, speed=1.0, hold=0.0, action_id = -1)
    public boolean lookAtObject(int targetId, Object speech, float speed, float hold, int action_id)
    {
    	//if (targetId in this.app.canvas.objects
      	/*
      	 * will have to do it using a loop and a flag called targetIdinObjects
      	 */
    	boolean targetIdinObjects=false;
    	
    	if (targetId in this.app.canvas.objects)
            target = this.app.canvas.objects[targetId]
            //*****jointIdByName is dictionary->convert to HashTable http://www.java-samples.com/showtutorial.php?tutorialid=375
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
            if (hold > 0.0)
                qi.callback = ("hold_posture", [hold, qi])
            tqi = QueueItem(None, isFinal=false)
            tqi.preCall=("turn_towardsTarget", [tqi, target])
            this.motionQueue.append(tqi)
            this.motionQueue.append(qi)
            return true
        else
            Logger.warning( "Avatar.lookAt no such object id in scene - " + str(targetId))
            return false
    }
    //lookAtChild(speech=None, speed=1.0, hold=0.0, action_id = -1)
    public void lookAtChild(Object speech, float speed, float hold, int action_id)
    {
    	String target = "child";
        mh = Piavca.PointAt(this.jointIdByName['Bip01 Head'], Piavca.Vec());//*****dictionary here also
        mh.setForwardDirection(Piavca.Vec.YAxis());
        mh.setLocal(true);
        mask = Piavca.MotionMask();
        mask.setAllMask(false);
        mask.setMask(this.jointIdByName['Bip01 Head'], true);
        masked_mh = Piavca.MaskedMotion(mh, mask);
        qi = QueueItem(Piavca.ScaleMotionSpeed(masked_mh, speed), isFinal=true, action_id=action_id);
        qi.preCall = ("lookatTarget", (mh, target, action_id));
        if (hold > 0.0)
        {
        	qi.callback = ("hold_posture", [hold, qi]);        
        }
        qi.speech = speech;
        tqi = QueueItem(None, isFinal=false);
        tqi.preCall=("turn_towardsTarget", [tqi, target]);
        this.motionQueue.append(tqi);//****do something about append in queue
        this.motionQueue.append(qi);
        return true;
    }
    //lookAtPoint(x, y, z, speech=None, speed=1.0, hold=0.0, action_id = -1, intermediate=false)
    public boolean lookAtPoint(float x, float y, float z, Object speech, float speed, float hold, int action_id, boolean intermediate)
    {
    	target = Piavca.Vec(x, y, z);
        mh = Piavca.PointAt(this.jointIdByName['Bip01 Head'], Piavca.Vec());//****dictionary here also
        mh.setForwardDirection(Piavca.Vec.YAxis());
        mh.setLocal(true);
        mask = Piavca.MotionMask();
        mask.setAllMask(false);
        mask.setMask(this.jointIdByName['Bip01 Head'], true);//*****dictionary
        masked_mh = Piavca.MaskedMotion(mh, mask);
        qi = QueueItem(Piavca.ScaleMotionSpeed(masked_mh, speed), isFinal=not intermediate, action_id=action_id);
        qi.preCall = ("lookatTarget", (mh, target, action_id));
        if (hold > 0.0)
        {
        	qi.callback = ("hold_posture", [hold, qi]);        
        }
        qi.speech = speech;        
        tqi = QueueItem(None, isFinal=false);
        tqi.preCall=("turn_towardsTarget", [tqi, target]);
        this.motionQueue.append(tqi);
        this.motionQueue.append(qi);
        return true;
    }
    //turnToChild(action_id = -1)
    public boolean turnToChild(int action_id)
    {
    	//*******preCall has a tuple and a list...what to do?
    	qi = QueueItem(this.relaxPosture, isFinal=true, preCall = ("turnTo", ["child", action_id])); // the motion is set up just before he actually needs to walk
        qi.action_id = action_id;
        this.motionQueue.append(qi);//do something about append 
        return true; 
    }
    //turnTo(targetId, action_id = -1)
    public boolean turnTo(int targetId, int action_id)
    {    
    	//target = None
        Object target;//****Don't know for sure if this will solve it
    	if (targetId in this.app.canvas.objects)//****put a loop again for this?
    	{
    		target = this.app.canvas.objects[targetId];
    	}
        else
        {
        	Logger.warning( "TurnTo no such object id in scene - " + str(targetId));
            return false;
        }
        //****again preCall has a tuple and list
    	qi = QueueItem(this.relaxPosture, isFinal=true, preCall = ("turnTo", [target, action_id])) // the motion is set up just before he actually needs to walk
        qi.action_id = action_id;
        this.motionQueue.append(qi); 
        return true ;
    }
    //turnToPoint(x, z, action_id = -1)
    public boolean turnToPoint(float x, float z, int action_id)
    {
    	//****preCall problem
    	qi = QueueItem(this.relaxPosture, isFinal=true, preCall = ("turnTo", [x,z, action_id])) # the motion is set up just before he actually needs to walk
        qi.action_id = action_id;
        //*****something about the append
    	this.motionQueue.append(qi); 
        return true ;
    }
    //popBubble(targetId, action_id = 1)
    public void popBubble(int targetId, int action_id)//****change the return type
    {
    	return this.pointAt(targetId, action_id=action_id, postAction="popBubble");
    }
    //pointAt(targetId, hand="right", action_id = -1, speed=1.0, hold=0.0, postAction=None)
    public void pointAt(int targetId, String hand, int action_id, float speed, float hold, Object postAction)
    {
    	if (targetId in this.app.canvas.objects //*******the In problem
    	{
    		target = this.app.canvas.objects[targetId];
    	}
        else
        {
        	Logger.warning( "pointAt no such object id in scene - " + str(targetId));
            return false;
        }
        if (hand=="right")
        {
        	jid = this.jointIdByName['Bip01 R UpperArm'];//****the dictionary problem
        }
        else
        {
        	jid = this.jointIdByName['Bip01 L UpperArm']; //****dictionary prob
        }
        mh = Piavca.PointAt(jid, Piavca.Vec(0,0,0));
        mh.setForwardDirection(Piavca.Vec.XAxis());
        mh.setLocal(false);
        mask = Piavca.MotionMask();
        mask.setAllMask(false);
        mask.setMask(jid, true);
        masked_mh = Piavca.MaskedMotion(mh, mask);     
        qi = QueueItem(Piavca.ScaleMotionSpeed(masked_mh, speed), isFinal=true, action_id=action_id);
        //****preCall has a list of I guess different objects
        qi.preCall = ("pointatTarget", (mh, target, action_id));
        if (postAction)
        {
        	//****has a list
        	qi.callback = (postAction, [target, action_id]);
        }
        else if (hold > 0.0)
        {
        	//****has a list
         	qi.callback = ("hold_posture", [hold, qi]);
        }
        tqi = QueueItem(None, isFinal=false);
        //******the preCall problem
        tqi.preCall=("turn_towardsTarget", [tqi, target]);
        this.motionQueue.append(tqi);//*****append problem
        this.motionQueue.append(qi);
        return true;
    }
    //pointAtPoint(x, y, z, hand="right", speed=1.0, hold=0.0, action_id = -1)
    public void pointAtPoint(float x, float y, float z, String hand, float speed, float hold, int action_id)          
    {
    	if (hand=="right")
    	{
    		jid = this.jointIdByName['Bip01 R UpperArm'];//****dictionary problem
    	}
        else
        {
        	jid = this.jointIdByName['Bip01 L UpperArm'];//*****dictionary prob
        }
        target = Piavca.Vec(x, y, z);
        mh = Piavca.PointAt(jid, target);
        mh.setForwardDirection(Piavca.Vec.XAxis());
        mh.setLocal(false);
        mask = Piavca.MotionMask();
        mask.setAllMask(false);
        mask.setMask(jid, true);
        masked_mh = Piavca.MaskedMotion(mh, mask);                
        qi = QueueItem(Piavca.ScaleMotionSpeed(masked_mh, speed), isFinal=true, action_id=action_id);
        //*****preCall problem
        qi.preCall = ("pointatTarget", (mh, target));
        if (hold > 0.0)
        {
        	//***callback problem
        	qi.callback = ("hold_posture", [hold, qi]);
        }
        tqi = QueueItem(None, isFinal=false);
        //***********preCall prob
        tqi.preCall=("turn_towardsTarget", [tqi, target]);
        //*****append problem
        this.motionQueue.append(tqi);
        this.motionQueue.append(qi);
        return true;
    }
    //resetPosture(action_id = -1)
    public boolean resetPosture(int action_id)
    {
    	this.motionQueue.append(QueueItem(this.relaxPosture, isFinal=true, action_id=action_id));
        return true;
    }
    //gestureAnim(name, relaxAfter=true, orientation=None, speech=None, speed=1.0, hold=0.0, action_id = -1)
    //********datatype of name
    public boolean gestureAnim(name, boolean relaxAfter, Object orientation, Object speech, float speed, float hold, int action_id)
    {
    	if (orientation)
    	{
    		//******preCall is a tuple
    		preCall = ("turnTo", orientation);
    	}
        else 
        {
        	preCall = None;//****
        }
        m = QueueItem(Piavca.ScaleMotionSpeed(Piavca.getMotion(name), speed), preCall=preCall);
        m.speech = speech;        
        if (hold > 0.0)
        {
        	//********callback problem-has a list
        	m.callback = ("hold_posture", [hold, m]);
        }
        if (relaxAfter)
        {
        	this.motionQueue.append(m);
            this.motionQueue.append(QueueItem(this.relaxPosture, isFinal=true, action_id=action_id));
        }
        else
        {    
        	m.isFinal = true;
            m.action_id = action_id;
            this.motionQueue.append(m);
        }
        return true;
    }
    //gesture(type, relaxAfter=true, orientation=None, speech=None, speed=1.0, hold=0.0, action_id = -1)
    //****datatype of type?
    public void gesture(type, boolean relaxAfter, Object orientation, Object speech, float speed, float hold, int action_id)
    {
    	if (orientation)
    	{
    		//****preCall problem
    		preCall = ("turnTo", orientation);
    	}
        else 
        {
        	preCall = None;//****None?
        }
        if (type=="all") // going through all the joints, for debugging purposes only
        {    
        	jid = this.avatar.begin();
            while (jid!=this.avatar.end())
            {
            	original = dict();//*****dictionary problem
                //****original is a dictionary
            	original[jid] = this.avatar.getJointOrientation(jid);
                animationTargets = dict();//****animationTargets is a dictionary
                animationTargets[jid] = original[jid] * Piavca.Quat(-math.pi/2, Piavca.Vec.XAxis());
                this.animationQueue.append(QueueItem(animationTargets, preCall=preCall));
                //***append prob
                this.animationQueue.append(QueueItem(original, isFinal=true, action_id=action_id));
                jid = this.avatar.next(jid);
            }
        }
        else
        {
        	return this.gestureAnim(type, relaxAfter, orientation, speech, speed, hold, action_id);
        }
        return true;
    }
    //sayPreRecorded(file, action_id = -1)
    public boolean sayPreRecorded(String file, int action_id)
    {
    	if (sound.EchoesAudio.soundPresent)
    	{   
    		duration = sound.EchoesAudio.getSoundDuration(file);
            if (duration == 0) 
            {    
            	Logger.warning("Requested sound file not present");
                return false;
            }
            at = ActionTimer(duration, action_id, speech=true);
            this.speechQueue.append(QueueItem(file, timer=at, action_id=action_id));
            return true;
    	}
        Logger.warning("Could not say anything, sound is not available");
        return false;
    }
    //sayPreRecordedNow(file, action_id = -1)
    public void sayPreRecordedNow(file, action_id)
    {   
    	if (sound.EchoesAudio.soundPresent)
    	{   
    		duration = sound.EchoesAudio.getSoundDuration(file);
            if (duration == 0) 
        	{ 
            	Logger.warning("Requested sound file not present");
                return false;
        	}
            at = ActionTimer(duration, action_id, speech=true);
            //*****queue
            this.speechQueue.appendleft(QueueItem(file, timer=at, action_id=action_id));
            return true;
    	}
        Logger.warning("Could not say anything, sound is not available");
        return false;
    }               
    //setFacialExpression (expression="Neutral", weight=1.0, action_id = -1)
    public boolean setFacialExpression (String expression, float weight, int action_id)
    {    
    	if (expression == "Neutral")
      	{   
    		//***in problem
    		for name in this.expressionTargets.keys()
            {
      			this.expressionTargets[name] = 0.0;
            }
      	}
        else
        {   
        	if (expression in this.expressionTargets)
        	{
        		this.expressionTargets[expression] = weight;
        	}
            else
            {    
            	Logger.warning(expression + " No such facial expression found");
                return false;
            }
        }
        return true;
    }
    //pickFlower(id=None, speech=None, action_id = -1, walkTo=false)
    public boolean pickFlower(id=None, speech=None, int action_id, boolean walkTo)
    {   
    	if (!id)
      	{   
    		for tid, object in this.app.canvas.objects.items()
    		{    
    			if (isinstance(object, objects.Plants.EchoesFlower))
    		    {
    				id = tid;
                    break;
                }
    		}
    	}
        if (!id)
        {    
        	Logger.warning("Avatar no flower to pick, doing nothing");
            return false;
        }
        flower = this.app.canvas.objects[id];
        if (walkTo && !(flower.overAgent && flower.beingDragged))
        {    
        	this.walkToObject(id);
        }
        else if (not this.isAt(id)  and not (flower.overAgent and flower.beingDragged))
        {
        	Logger.warning("Avatar flower too far away, doing nothing");
            return false;
        }
        mpick = Piavca.getMotion('pick_up_flower');
        mpick.setStartTime(0.0);
        endtime = mpick.getMotionLength();
        mdown = Piavca.SubMotion(mpick, 0.0, 0.7);
        mup = Piavca.SubMotion(mpick, 0.7, endtime);
        qidown = QueueItem(mdown, callback = ("attach_flowerR", flower));
        qidown.preCall = ("check_at", [id, action_id]);
        qidown.speech = speech;
        this.motionQueue.append(qidown);
        this.motionQueue.append(QueueItem(mup, isFinal=true, playDirect=true, action_id = action_id));
        return true; 
    }
    //touchObject(id=None, speech=None, action_id = -1, walkTo=false)
    public void touchObject(id=None, speech=None, action_id = -1, walkTo=false)
    {    
    	if (!id)
       	{   
    		Logger.warning("Avatar no object provided to touch, doing nothing"); 
       	    return false;
        }
        if (walkTo)
        {
        	this.walkToObject(id);
        }
        else if (!(this.isAt(id)))
   		{
        	Logger.warning("Avatar flower too far away, doing nothing");
            return false;
   		}
        object = this.app.canvas.objects[id];
        if (object.pos[1] < this.low)
        {
        	mpick = Piavca.getMotion('touch_floor');
            cut = 0.5;
        }
        else if (object.pos[1] < this.knee)
        {
        	mpick = Piavca.getMotion('touch_knee');
            cut = 0.5;
        }
        else if (object.pos[1] < this.waist)
        {
        	mpick = Piavca.getMotion('touch_waist');
            cut = 0.5;
        }
        else
        {
        	mpick = Piavca.getMotion('touch_head');
            cut = 0.5;
        }
        mpick.setStartTime(0.0);
        endtime = mpick.getMotionLength();
        mdown = Piavca.SubMotion(mpick, 0.0, endtime*cut);
        mup = Piavca.SubMotion(mpick, endtime*cut, endtime);
        qidown = QueueItem(mdown, callback = ("touch_object", object));        
        qidown.preCall = ("check_at", [id, action_id]);
        qidown.speech = speech;
        this.motionQueue.append(qidown);
        this.motionQueue.append(QueueItem(mup, isFinal=true, playDirect=true, action_id = action_id));
        return true;         
    }
    //touchFlower(id=None, target="Bubble", speech=None, action_id = -1)
    public void touchFlower(id=None, target="Bubble", speech=None, action_id = -1)
    {
    	if (!id)
    	{
    		for tid, object in this.app.canvas.objects.items()
    		{
    			if (isinstance(object, objects.Plants.EchoesFlower))
    			{
    				id = tid;
    				break;
    			}
    		}
    	}
    	if (!id)
    	{
    		Logger.warning("Avatar no flower to touch, doing nothing"); 
    	    return false;
    	}
        flower = this.app.canvas.objects[id];
        mpick = Piavca.getMotion('spinning_flower_stepforward');
        mpick.setStartTime(0.0);
        endtime = mpick.getMotionLength();
        mdown = Piavca.SubMotion(mpick, 0.0, 1.3);
        mup = Piavca.SubMotion(mpick, 1.3, endtime);
        qidown = QueueItem(mdown, callback = ("touch_flower", [flower, target]));
        qidown.preCall = ("check_at", [id, action_id]);
        qidown.speech = speech;
        this.motionQueue.append(qidown);
        this.motionQueue.append(QueueItem(mup, isFinal=true, playDirect=true, action_id = action_id));
        return true;
    }
    //putdownFlower(action_id = -1, walkTo=-1)
    public boolean putdownFlower(int action_id, int walkTo)
    {
    	flower = null;
        for object in this.tcb.attachedObjects
        {   
        	if (isinstance(object, objects.Plants.EchoesFlower))
          	{
        		flower = object;
        		break;
          	}
        }
        if (! flower) 
        { 
        	Logger.warning("Avatar not holding a flower, doing nothing"); 
            return false;
        }      
        if (walkTo > -1) // make sure the pot lands in the desired slot)
        {  
        	flowerx = walkTo - 4.5;
            if (flowerx > this.pos[0])
            {
            	this.walkTo(flowerx - 1, this.pos[2]);
            }
            else        
            {
            	this.walkTo(flowerx + 1, this.pos[2]);
            }
        }
        mput = Piavca.getMotion('pick_up_flower');
        mput.setStartTime(0.0);
        endtime = mput.getMotionLength();
        mdown = Piavca.SubMotion(mput, 0.0, 0.7);
        mup = Piavca.SubMotion(mput, 0.7, endtime);
        this.motionQueue.append(QueueItem(mdown, callback = ("detach_flowerR", flower)));
        this.motionQueue.append(QueueItem(mup, isFinal=true, playDirect=true, action_id=action_id));
        return true;
    }
    
    //putFlowerInPot(id=None, action_id = -1)
    public boolean putFlowerInPot(id=None, action_id = -1) 
    {
    	if (!id)
    	{
    		for tid, object in this.app.canvas.objects.items()
            {
            	if (isinstance(object, objects.Plants.Pot))
                {
                	id = tid;
                    break;
                }
            }
    	}
        if (!id)
        {
        	Logger.warning("Avatar no pot in scene, doing nothing"); 
            return false;
        }
        flower = null;
        for object in this.tcb.attachedObjects
        {
        	if (isinstance(object, objects.Plants.EchoesFlower))
        	{
        		flower = object;
                break;
        	}
        }
        if (! flower) 
        {
        	Logger.warning("Avatar not holding a flower, doing nothing"); 
            return false;
        }
        if (!(this.isAt(id)))
        {
        	Logger.warning("Avatar Specified (or nearest) pot is too far away; not putting down flower");
            return false;
        }

        pot = this.app.canvas.objects[id];
        mput = Piavca.getMotion('pick_up_flower')
        mput.setStartTime(0.0);
        endtime = mput.getMotionLength();
        mdown = Piavca.SubMotion(mput, 0.0, 0.7);
        mup = Piavca.SubMotion(mput, 0.7, endtime);
        qidown = QueueItem(mdown, callback = ("detach_flowerInPotR", [pot, flower]));
        qidown.preCall = ("check_at", [id, action_id]);
        this.motionQueue.append(qidown);
        this.motionQueue.append(QueueItem(mup, isFinal=true, playDirect=true, action_id=action_id));
        return true;         
    }
    //putFlowerInBasket(id=None, action_id = -1, walkTo=false)
    public boolean putFlowerInBasket(id=None, action_id = -1, walkTo=false) 
    {
    	if (!id)
    	{    for tid, object in this.app.canvas.objects.items()
    		{
    			if (isinstance(object, objects.Environment.Basket))
    			{
    				id = tid;
    				break;
    			}
    		}
    	}
        if (!id)
        {
        	Logger.warning("Avatar no basket in scene, doing nothing"); 
            return false;
        }
        flower = null;
        for object in this.tcb.attachedObjects
        {
        	if (isinstance(object, objects.Plants.EchoesFlower))
        	{   flower = object;
                break;
        	}
        }
        if (!flower) 
        {
        	Logger.warning("Avatar not holding a flower, doing nothing"); 
            return false;
        }
        if (walkTo)
        {
        	this.walkToObject(id);
        }
        else if (!(this.isAt(id)))
        {
        	Logger.warning("Avatar Specified (or nearest) basket is too far away; not putting down flower");
            return false;
        }
        basket = this.app.canvas.objects[id];
        mput = Piavca.getMotion('pick_up_flower');
        mput.setStartTime(0.0);
        endtime = mput.getMotionLength();
        mdown = Piavca.SubMotion(mput, 0.0, 0.7);
        mup = Piavca.SubMotion(mput, 0.7, endtime);
        qidown = QueueItem(mdown, callback = ("detach_flowerInBasketR", [basket, flower]));
        qidown.preCall = ("check_at", [id, action_id]);
        this.motionQueue.append(qidown);
        this.motionQueue.append(QueueItem(mup, isFinal=true, playDirect=true, action_id=action_id));
        return true;         
    }
    //stackPot(id=None, action_id = -1, walkTo = false)
    public void stackPot(id=None, action_id = -1, walkTo = false) 
    {    
    	pot = null;
        for object in this.tcb.attachedObjects
        {
        	if (isinstance(object, objects.Plants.Pot))
          	{   pot = object;
                break;
        	}
        }
        if (!pot) 
        {
        	Logger.warning("Avatar not holding a pot, doing nothing"); 
            return false;
        }
        d = 1000;
        if (!id)
        {
        	for tid, object in this.app.canvas.objects.items()
          	{
        		if (isinstance(object, objects.Plants.Pot) and object != pot and this.getDistance(tid) < d)
        		{
        			Logger.trace("info", "Found a pot " + str(tid) + " at distance " + str(this.getDistance(tid)));
        			id = tid;
                    d = this.getDistance(tid);
        		}
        	}
        }
        if (!id)
        {
        	Logger.warning("Avatar no other pot in scene, doing nothing"); 
            return false;
        }
        if (walkTo) 
        {
        	this.walkToObject(id);
        }
        else if (!(this.isAt(id)))
        {    
        	Logger.warning("Avatar Not close enough to target pot, not stacking");
            return false;
        }
        targetpot = this.app.canvas.objects[id];
        if (targetpot.pos[1] < this.low)
        {
        	mpick = Piavca.getMotion('pot_down_floor');
            cut = 0.4;
        }
        else if (targetpot.pos[1] < this.knee)
        {
        	mpick = Piavca.getMotion('pot_down_knee');
            cut = 0.4;
        }
        else if (targetpot.pos[1] < this.waist)
        {
        	mpick = Piavca.getMotion('pot_down_waist');
            cut = 0.4;
        }
        else
        {
        	mpick = Piavca.getMotion('pot_down_head');
            cut = 0.7;
        }
        mpick.setStartTime(0.0);
        endtime = mpick.getMotionLength();
        mdown = Piavca.SubMotion(mpick, 0.0, endtime*cut);
        mup = Piavca.SubMotion(mpick, endtime*cut, endtime);
        qidown = QueueItem(mdown, isFinal=false, callback=("detach_potOnStackR", [pot, targetpot]));
        qidown.preCall = ("check_at", [id, action_id]);
        this.motionQueue.append(qidown);
        this.motionQueue.append(QueueItem(mup, isFinal=true, playDirect=true, action_id=action_id));        
        return true;         
    }
    //pickupPot(id=None, speech=None, action_id = -1, walkTo=false)
    public void pickupPot(id=None, speech=None, action_id = -1, walkTo=false)
    {    
    	d = 1000;
        if (!id)
        {
        	for tid, object in this.app.canvas.objects.items()
        	{
        		if (isinstance(object, objects.Plants.Pot) and this.getDistance(tid) < d)
        		{
        			print "Found a pot " + str(tid) + " at distance " + str(this.getDistance(tid));
        		    id = tid;
                    d = this.getDistance(tid);
        		}
        	}
        }
        if (! id)
        {
        	Logger.warning("Avatar no pot in scene, doing nothing"); 
            return false;
        }
        pot = this.app.canvas.objects[id];
        if (walkTo && not (pot.overAgent and pot.beingDragged)) 
        {
        	this.walkToObject(id);
        }
        else if (! this.isAt(id) && !(pot.overAgent and pot.beingDragged))
        {
        	Logger.warning("Avatar Not close enough to target pot, not stacking");
            return false;
        }
        if (pot.pos[1] < this.low)
        {
        	mpick = Piavca.getMotion('pot_up_floor');
            cut = 0.4;
        }
        else if (pot.pos[1] < this.knee)
        {
        	mpick = Piavca.getMotion('pot_up_knee');
            cut = 0.4;
        }
        else if (pot.pos[1] < this.waist)
        {
        	mpick = Piavca.getMotion('pot_up_waist');
            cut = 0.4;
        }
        else
        {
        	mpick = Piavca.getMotion('pot_up_head');
            cut = 0.7;
        }
        mpick.setStartTime(0.0);
        endtime = mpick.getMotionLength();
        mdown = Piavca.SubMotion(mpick, 0.0, endtime*cut);
        mup = Piavca.SubMotion(mpick, endtime*cut, endtime);
        qidown = QueueItem(mdown, callback = ("attach_potR", pot));
        qidown.preCall = ("check_at", [id, action_id]);
        qidown.speech = speech;
        this.motionQueue.append(qidown);
        this.motionQueue.append(QueueItem(mup, isFinal=true, playDirect=true, action_id=action_id));
        return true ;       
    }
    //putdownPot(action_id = -1, walkTo = -1)
    public void putdownPot(action_id = -1, walkTo = -1)
    {    
    	pot = null;
        for object in this.tcb.attachedObjects
        {
        	if (isinstance(object, objects.Plants.Pot))
        	{
        		pot = object;
        	    break;
        	}
        }
        if (!pot) 
        {
        	Logger.warning("Avatar not holding a pot, doing nothing"); 
            return false;
        }
        if (walkTo > -1) //make sure the pot lands in the desired slot
        {
        	potx = walkTo - 4.5;
            if (potx > this.pos[0])
            {
            	this.walkTo(potx - 1, this.pos[2]);
            }
            else        
            {
            	this.walkTo(potx + 1, this.pos[2]);
            }
        }
        mput = Piavca.getMotion("pot_down_floor");
        mput.setStartTime(0.0);
        endtime = mput.getMotionLength();
        mdown = Piavca.SubMotion(mput, 0.0, endtime*0.7);
        mup = Piavca.SubMotion(mput, endtime*0.7, endtime);
        this.motionQueue.append(QueueItem(mdown, callback = ("detach_potR", None)));
        this.motionQueue.append(QueueItem(mup, isFinal=true, playDirect=true, action_id=action_id));
        return true;       
    }
    //pickupBasket(id=None, speech=None, action_id = -1, walkTo=false)
    public void pickupBasket(id=None, speech=None, action_id = -1, walkTo=false)
    {
    	d = 1000;
        if (!id)
        {
        	for tid, object in this.app.canvas.objects.items()
        	{
        		if (isinstance(object, objects.Environment.Basket) and this.getDistance(tid) < d)
        		{
        			print "Found a basket " + str(tid) + " at distance " + str(this.getDistance(tid));
        		    id = tid;
                    d = this.getDistance(tid);
                    break;
        		}
        	}
        }
        if (!id)
        {
        	Logger.warning("Avatar no pot in scene, doing nothing"); 
            return false;
        }
        basket = this.app.canvas.objects[id];
        if (walkTo &&  !(basket.overAgent and basket.beingDragged)) 
        {
        	this.walkToObject(id);
        }
        else if (! this.isAt(id) && !(basket.overAgent && basket.beingDragged))
        {
        	Logger.warning("Avatar Not close enough to target pot, not stacking");
            return false;
        }
        if (basket.pos[1] < this.low)
        {
        	mpick = Piavca.getMotion('pot_up_floor');
            cut = 0.4;
        }
        else if (basket.pos[1] < this.knee)
        {
        	mpick = Piavca.getMotion('pot_up_knee');
            cut = 0.4;
        }
        else if (basket.pos[1] < this.waist)
        {
        	mpick = Piavca.getMotion('pot_up_waist');
            cut = 0.4;
        }
        else
        {
        	mpick = Piavca.getMotion('pot_up_head');
            cut = 0.7;
        }
        mpick.setStartTime(0.0);
        endtime = mpick.getMotionLength();
        mdown = Piavca.SubMotion(mpick, 0.0, endtime*cut);
        mup = Piavca.SubMotion(mpick, endtime*cut, endtime);
        qidown = QueueItem(mdown, callback = ("attach_potR", basket));
        qidown.preCall = ("check_at", [id, action_id]);
        qidown.speech = speech;        
        this.motionQueue.append(qidown);
        this.motionQueue.append(QueueItem(mup, isFinal=true, playDirect=true, action_id=action_id));
        return true;        
    }
    //putdownBasket(action_id = -1, walkTo = -1)
    public void putdownBasket(action_id = -1, walkTo = -1)
    {
    	basket = null;
        for object in this.tcb.attachedObjects
        {   
        	if (isinstance(object, objects.Environment.Basket))
        	{
        		basket = object;
        	    break;
        	}
        }
        if (!basket) 
        {
        	Logger.warning("Avatar not holding a basket, doing nothing"); 
            return false;
        }
        if (walkTo > -1) // make sure the pot lands in the desired slot
        {
        	basketx = walkTo - 4.5;
            if (basketx > this.pos[0])
            {
            	this.walkTo(basketx - 1, this.pos[2]);
            }
            else        
            {
            	this.walkTo(basketx + 1, this.pos[2]);
            }
        }
        mput = Piavca.getMotion('pot_down_floor');
        mput.setStartTime(0.0);
        endtime = mput.getMotionLength();
        mdown = Piavca.SubMotion(mput, 0.0, endtime*0.7);
        mup = Piavca.SubMotion(mput, endtime*0.7, endtime);
        qidown = QueueItem(mdown, callback = ("detach_potR", None));
        this.motionQueue.append(qidown);
        this.motionQueue.append(QueueItem(mup, isFinal=true, playDirect=true, action_id=action_id));
        return true;       
    }
    //pickupBall(id=None, speech=None, action_id = -1, walkTo=false)
    public void pickupBall(id=None, speech=None, action_id = -1, walkTo=false)
    {    
    	d = 1000;
        if (!id)
        {
        	for tid, object in this.app.canvas.objects.items()
          	{
        		if (isinstance(object, objects.PlayObjects.Ball) and this.getDistance(tid) < d)
        		{
        			Logger.trace("info", "Found a ball " + str(tid) + " at distance " + str(this.getDistance(tid)));
        		    id = tid;
                    d = this.getDistance(tid);
        		}
          	}
        }
        if (!id)
        {
        	Logger.warning("Avatar no ball in scene, doing nothing"); 
            return false;
        }
        ball = this.app.canvas.objects[id];
        if (walkTo && !(ball.overAgent && ball.beingDragged)) 
        {
        	this.walkToObject(id);
        }
        else if (! this.isAt(id) && !(ball.overAgent && ball.beingDragged))
        {
        	Logger.warning("Avatar Not close enough to target ball, not picking up");
            return false;
        }
        if (ball.pos[1] < this.low)
        {
        	mpick = Piavca.getMotion('pot_up_floor');
            cut = 0.4;
        }
        else if (ball.pos[1] < this.knee)
        {
        	mpick = Piavca.getMotion('pot_up_knee');
        	cut = 0.4;
        }
        else if (ball.pos[1] < this.waist)
        {
        	mpick = Piavca.getMotion('pot_up_waist');
            cut = 0.4;
        }
        else
        {
        	mpick = Piavca.getMotion('pot_up_head');
            cut = 0.7;
        }
        mpick.setStartTime(0.0);
        endtime = mpick.getMotionLength();
        mdown = Piavca.SubMotion(mpick, 0.0, endtime*cut);
        mup = Piavca.SubMotion(mpick, endtime*cut, endtime);
        qidown = QueueItem(mdown, callback = ("attach_potR", ball));
        qidown.preCall = ("check_at", [id, action_id]);
        qidown.speech = speech;
        this.motionQueue.append(qidown);
        this.motionQueue.append(QueueItem(mup, isFinal=true, playDirect=true, action_id=action_id));
        return true;        
    }
    //putdownBall(action_id = -1, walkTo = -1)
    public void putdownBall(action_id = -1, walkTo = -1)
    {
    	ball = null;
        for object in this.tcb.attachedObjects
        {
        	if (isinstance(object, objects.PlayObjects.Ball))
        	{
        		ball = object;
        	    break;
        	}
        }
        if (!ball) 
        {
        	Logger.warning("Avatar not holding a ball, doing nothing"); 
        	return false;
        }
        if (walkTo > -1) // make sure the pot lands in the desired slot
        {
        	ballx = walkTo - 4.5;
            if (ballx > this.pos[0])
            {
            	this.walkTo(ballx - 1, this.pos[2]);
            }
            else        
            {
            	this.walkTo(ballx + 1, this.pos[2]);
            }
        }
        mput = Piavca.getMotion('pot_down_floor');
        mput.setStartTime(0.0);
        endtime = mput.getMotionLength();
        mdown = Piavca.SubMotion(mput, 0.0, endtime*0.7);
        mup = Piavca.SubMotion(mput, endtime*0.7, endtime);
        this.motionQueue.append(QueueItem(mdown, callback = ("detach_potR", None)));
        this.motionQueue.append(QueueItem(mup, isFinal=true, playDirect=true, action_id=action_id));
        return true;       
    }
    //putBallIntoContainer(id=None, action_id = -1, walkTo = false)
    public void putBallIntoContainer(id=None, action_id = -1, walkTo = false) 
    {
    	ball = null;
        for object in this.tcb.attachedObjects
        {
        	if (isinstance(object, objects.PlayObjects.Ball))
        	{
        		ball = object;
        	    break;
        	}
        }
        if (!ball) 
        {
        	Logger.warning("Avatar not holding a ball, doing nothing"); 
        	return false;
        }
        if (!id)
        {
        	for tid, object in this.app.canvas.objects.items()
        	{
        		if (isinstance(object, objects.Environment.Container))
        		{
        			Logger.trace("info", "Found a container " + str(tid));
        			id = tid;
        		}
        	}
        }
        if (!id || !id in this.app.canvas.objects)
        {
        	Logger.warning("Avatar no other pot in scene, doing nothing"); 
        	return false;
        }
        container = this.app.canvas.objects[id];
        if (walkTo) 
        {
        	this.walkToObject(id);
        }
        else if (!this.isAt(id))
        {
        	Logger.warning("Avatar Not close enough to target container");
            return false;
        }
        mpick = Piavca.getMotion('drop_ball');
        cut = 0.5;
        mpick.setStartTime(0.0);
        endtime = mpick.getMotionLength();
        mdown = Piavca.SubMotion(mpick, 0.0, endtime*cut);
        mup = Piavca.SubMotion(mpick, endtime*cut, endtime);
        qidown = QueueItem(mdown, isFinal=false, callback=("put_ballContainer", [ball, container]));
        qidown.preCall = ("check_at", [id, action_id]);
        this.motionQueue.append(qidown);
        this.motionQueue.append(QueueItem(mup, isFinal=true, playDirect=true, action_id=action_id));        
        return true;         
    }
    //throwBall(action_id = -1, cloudId = None)
    public void throwBall(action_id = -1, cloudId = None)
    {
    	ball = null;
        for object in this.tcb.attachedObjects
        {
        	if (isinstance(object, objects.PlayObjects.Ball))
        	{
        		ball = object;
        	    break;
        	}
        }
        if (!ball) 
        {
        	Logger.warning("Avatar not holding a ball, doing nothing"); 
            return false;
        }
        if (cloudId) // make sure the pot lands in the desired slot
        {
        	this.walkToObject(cloudId, distance=2.5);
        }
        mpick = Piavca.getMotion('touch_above');
        mpick.setStartTime(0.0);
        endtime = mpick.getMotionLength();
        mup = Piavca.SubMotion(mpick, 0.0, endtime/2);
        mdown = Piavca.SubMotion(mpick, endtime/2, endtime);
        qiup = QueueItem(mup, callback = ("throw_ball", ball));
        this.motionQueue.append(qiup);
        this.motionQueue.append(QueueItem(mdown, isFinal=true, playDirect=true, action_id=action_id));
        return true;    
    }
    //makeRain(id=None, action_id = -1, walkTo = false)
    public void makeRain(id=None, action_id = -1, walkTo = false)
    {  
    	if (!id)
      	{
    		for tid, object in this.app.canvas.objects.items()
    		{
    			if (isinstance(object, objects.Environment.Cloud))
    			{
    				id = tid;
    				break;
    			}
    		}
      	}
        if (!id)
        {
        	Logger.warning("Avatar no cloud in scene, doing nothing"); 
            return false;
        }
        
        if (walkTo) 
        {
        	this.walkToObject(id);
        }
        else if (!(this.isAt(id)))
        {
        	Logger.warning("Avatar specified (or nearest) cloud is too far away; not making rain");
        	return false;        
        }
        cloud = this.app.canvas.objects[id];
        mpick = Piavca.getMotion('touch_above');
        mpick.setStartTime(0.0);
        endtime = mpick.getMotionLength();
        mup = Piavca.SubMotion(mpick, 0.0, endtime/2);
        mdown = Piavca.SubMotion(mpick, endtime/2, endtime);
        qiup = QueueItem(mup, callback = ("make_rain", cloud));
        qiup.preCall = ("check_at", [id, action_id]);
        this.motionQueue.append(qiup);
        this.motionQueue.append(QueueItem(mdown, isFinal=true, playDirect=true, action_id=action_id));
        return true;   
    }
    //touchLeaves(id=None, action_id = -1)
    public void touchLeaves(id=None, action_id = -1)
    {
    	if (!id)
    	{
    		for tid, object in this.app.canvas.objects.items()
    		{
    			if (isinstance(object, objects.Plants.MagicLeaves))
    			{
    				id = tid;
    				break;
    			}
    		}
    	}
        if (!id)
        {
        	Logger.warning("Avatar no MagicLeaves in scene, doing nothing"); 
        	return false;
        }
        leaf = this.app.canvas.objects[id];
        if (leaf.flying and leaf.energy > 0)
        {
        	Logger.warning("Avatar warning MagicLeaves are moving, not attempting to touch them");
            return false;
        }
        if (!this.isAt(id))
        {
        	Logger.warning("Avatar warning not at MagicLeaves to be able to touch them");
            return false;        
        }
        if (leaf.pos[1] < this.pos[1]+1)
        {
        	Logger.warning("Avatar warning MagicLeaves too low, doing nothing");
        	return false;
        }
        mpick = Piavca.getMotion('touch_above');
        mpick.setStartTime(0.0);
        endtime = mpick.getMotionLength();
        mup = Piavca.SubMotion(mpick, 0.0, endtime/2);
        mdown = Piavca.SubMotion(mpick, endtime/2, endtime);
        qiup = QueueItem(mup, callback = ("touch_leaves", leaf));
        qiup.preCall = ("check_at", [id, action_id]);
        this.motionQueue.append(qiup);
        this.motionQueue.append(QueueItem(mdown, isFinal=true, playDirect=true, action_id=action_id));
        return true;    
    }
    //attachObjectToHand(object, right=true)
    public void attachObjectToHand(object, right=true)
    {  
    	if (right)
    	{
    		this.tcb.attachObjectToJoint(object, this.jointIdByName['Bip01 R Finger1']);
    	}
        else
        {
        	this.tcb.attachObjectToJoint(object, this.jointIdByName['Bip01 L Finger1']);
        }
        return true;
    }
    //detachObjectFromHand(right=true)
    public void detachObjectFromHand(boolean right)
    {
    	if (right)
    	{
    		this.tcb.detachObjectFromJoint(this.jointIdByName['Bip01 R Finger1']);
    	}
        else
        {
        	this.tcb.detachObjectFromJoint(this.jointIdByName['Bip01 L Finger1']);
        }
        return true;
    }
    public void attachCloud(cloudId=None, action_id = -1)
    {
    	if (not cloudId)
    	{
    		for tid, object in this.app.canvas.objects.items()
    		{
    			if (isinstance(object, objects.Environment.Cloud))
    			{
    				cloudId = tid;
    			    break;
    			}
    		}
    	}
        if (!cloudId)
        {
        	return false;
        }
        this.pointAt(cloudId);
        this.lookAtObject(cloudId);
        mask = Piavca.MotionMask();
        mask.setAllMask(false);
        motion = Piavca.MaskedMotion(Piavca.ZeroMotion(), mask);
        qi = QueueItem(motion, preCall = ("attach_cloud", cloudId));
        this.motionQueue.append(qi);
        return true;
    }
    
    //detachCloud(action_id = -1)
    public void detachCloud(action_id = -1)
    {
    	qi = QueueItem(this.relaxPosture, preCall = ("detach_cloud", None));
        this.motionQueue.append(qi);
        return true;
    }
    //click(pos)
    public void click(pos)
    {//#        print "Avatar clicked at", pos
        this.app.canvas.rlPublisher.userTouchedAgent(str(this.id))
    }   
    //blinking(flag=true)
    public void blinking(flag=true)
    {
    	if (flag)
    	{
    		if (!this.blinkingTimer)
    		{
                this.blinkingTimer = BlinkingTimer();
                this.blinkingTimer.start();
    		}
    	}
        else
        {
        	if (this.blinkingTimer)
        	{
        		this.blinkingTimer.running == false;
        		this.blinkingTimer = None;
        	}
        }
        return true;
    }
                    
    public void getXYContour()
    {
    	bb = this.avatar.getBoundBox();
        bbox = [Piavca.Vec(bb.min.X(), bb.min.Y(), bb.min.Z()), 
                Piavca.Vec(bb.max.X(), bb.min.Y(), bb.min.Z()),
                Piavca.Vec(bb.min.X(), bb.max.Y(), bb.min.Z()),
                Piavca.Vec(bb.max.X(), bb.max.Y(), bb.min.Z()),
                Piavca.Vec(bb.min.X(), bb.min.Y(), bb.max.Z()),
                Piavca.Vec(bb.max.X(), bb.min.Y(), bb.max.Z()),
                Piavca.Vec(bb.min.X(), bb.max.Y(), bb.max.Z()),
                Piavca.Vec(bb.max.X(), bb.max.Y(), bb.max.Z())
                ];
        bbox_wc = [];
        minx = miny = 1000;
        maxx = maxy = -1000;
        for vec in bbox
        {
        	v = this.avatar.getRootOrientation().transform(vec * this.scale);
        	bbox_wc.append(v);
            minx = min(minx, v.X());
            maxx = max(maxx, v.X());
            miny = min(miny, v.Y());
            maxy = max(maxy, v.Y());
        }
        pos = this.avatar.getRootPosition() * this.scale;
        minx += pos.X();
        maxx += pos.X();
        miny += pos.Y();
        maxy += pos.Y();
        // this is a hack, unsure why the bounding box is shifted by 2...
        return [(minx, miny-2), (minx, maxy-2), (maxx, maxy-2), (maxx, miny-2)];
    }
    public void render()
    {     
        //will be rendered in Piavca Core loop
        
        if (hasattr("showBoundary") && this.showBoundary)
        {
        	contour = this.getXYContour();
            contour.append(contour[0]);
            gl.glPushMatrix();
            gl.glLineWidth(1.0);
            gl.glColor4f(1, 0, 0, 1.0);
            gl.glBegin(GL2.GL_LINE_STRIP);
            for v in contour
            {
            	gl.glVertex3f(v[0], v[1], this.pos[2]);                        
            }
            gl.glEnd();
            gl.glPopMatrix();   
        }
        if (hasattr("publishVisibility") && this.publishVisibility)
        {
        	if (this.visible and abs(this.pos[0]) > 4.5)
        	{    
        		this.visible = false; 
        	    this.app.canvas.rlPublisher.agentPropertyChanged(str(this.id), "Visible", str(this.visible));
        	}
        	if (not this.visible and abs(this.pos[0]) < 4.5)
        	{
        		this.visible = true ;
        	    this.app.canvas.rlPublisher.agentPropertyChanged(str(this.id), "Visible", str(this.visible));
        	}
        }
    }
    public void remove()
    {
        // Piavca.Core.getCore().removeAvatar(this.avatar)
        super(EchoesAvatar, ).remove();
    }
}

