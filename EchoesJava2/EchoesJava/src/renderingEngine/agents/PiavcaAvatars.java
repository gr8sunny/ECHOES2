	//translation from Py May 10th
	//*******function is not in a class
package renderingEngine.agents;
	
public class Utils
{
	public void avatarCallback(EchoesAvatar echoesAvatar, String type, String [] arg)
	{
		if (type == "turnTo")
		{
			if (arg.length == 2 && isinstance(arg[0], (float, int))) //#coordinates
	            echoesAvatar.turnTowardsDirect(arg[0], arg[1], false);
	        else if (arg[0] == "child")
	            echoesAvatar.turnTowardsDirect(echoesAvatar.pos[0], 10, false);
	        else if (isinstance(arg[0], objects.EchoesObject.EchoesObject))
	        {
	        	target, action_id = arg;
	            if (canvas.objects.contains(target.id))
	                echoesAvatar.turnTowardsDirect(target.pos[0], target.pos[2], false);
	            else
	            {
	            	echoesAvatar.app.canvas.agentActionCompleted(action_id, false);
	                echoesAvatar.cancelMotions = 1;
	            }
	        }
		}
	    else if (type == "walkTo")
	    {    if (arg.length != 5) 
	    		return;
	        boolean withObject = false;
	        if (isinstance(arg[0], objects.EchoesObject.EchoesObject))
	        {
	        	o, di, wi, r, action_id = arg;
	            wi.callback = ("turnTo", [o.pos[0], o.pos[2]]);
	            if (echoesAvatar.pos[0] > o.pos[0])
	                x = o.pos[0] + di;
	            else 
	                x = o.pos[0] - di;
	            z = o.pos[2]  ;
	            withObject = true;
	        }
	        else
	            x, z, wi, r, action_id = arg;
	
	        distance = math.hypot(echoesAvatar.pos[0]-x,echoesAvatar.pos[2]-z);
	        if (distance > echoesAvatar.walkingDistance)
	        {
	        	Logger.warning("Avatar cannot walk that far in one go, walking full length of animation instead and inserting another walking motion");
	            w = echoesAvatar.walking;
	            new_wi = QueueItem(None, action_id = action_id, isFinal=false);
	            if (withObject)
	                new_wi.preCall = ("walkTo", [o,di,new_wi,echoesAvatar.relaxPosture,action_id]);
	            else
	                new_wi.preCall = ("walkTo", [x,z,new_wi,echoesAvatar.relaxPosture,action_id]);
	            wi.action_id = -1;
	            wi.isFinal = false;
	            echoesAvatar.motionQueue.appendleft(new_wi);
	        }
	        else
	            w = Piavca.SubMotion(echoesAvatar.walking, 0, echoesAvatar.findMotionEndtime(echoesAvatar.walking, distance));
	        echoesAvatar.turnTowardsDirect(x,z, relaxAfter=false, intermediate=true);
	        object = None;
	        Set set = echoesAvatar.tcb.attachedObjects.keySet(); // get set-view of keys
	        // get iterator
	        Iterator itr = set.iterator();
	        String o;
	        while(itr.hasNext()) {
	        	o = (String) itr.next(); 
	        //for o in echoesAvatar.tcb.attachedObjects
	        //{
	        	object = o; 
	            break;
	        }
	        mask = Piavca.MotionMask();
	        mask.setAllMask(true);
	        if (object)
	        {
	        	print "Attached object", object;
	            mask.setMask(echoesAvatar.jointIdByName.get("Bip01 R Forearm"), false);
	            mask.setMask(echoesAvatar.jointIdByName.get("Bip01 L Forearm"), false);
	            w = Piavca.MaskedMotion(w, mask);
	            r = Piavca.MaskedMotion(r, mask);
	        }
	        if (echoesAvatar.tcb.attachedCloud)
	        {
	        	print "Attached cloud", echoesAvatar.tcb.attachedCloud;
	            mask.setMask(echoesAvatar.jointIdByName.get("Bip01 Head"), false);
	            mask.setMask(echoesAvatar.jointIdByName.get("Bip01 R UpperArm"), false);
	            w = Piavca.MaskedMotion(w, mask);
	            r = Piavca.MaskedMotion(r, mask);
	        }
	        wi.item = w;
	    }
	    else if (type == "setOrientation")
	        echoesAvatar.orientation = arg;
	    else if (type == "setPosition")
	        echoesAvatar.pos = arg;
	    else if (type == "attach_flowerR" && isinstance(arg, objects.Plants.EchoesFlower))
	    {    if (arg.pot)
	            arg.pot.flower = None;
	        if (arg.basket)
	            arg.basket.removeFlower(arg);
	            arg.basket = None;
	        echoesAvatar.attachObjectToHand(arg, right=true);
	    }
	    else if (type == "detach_flowerR")
	        echoesAvatar.detachObjectFromHand(right=true);                
	    else if (type == "detach_flowerInPotR")
	    {    echoesAvatar.detachObjectFromHand(right=true);
	        arg[0].flower = arg[1];
	    }
	    else if (type == "detach_flowerInBasketR")
	    {    echoesAvatar.detachObjectFromHand(right=true);
	        arg[0].addFlower(arg[1]);
	    }
	    else if (type == "attach_potR")
	        echoesAvatar.attachObjectToHand(arg, right=true);                
	    else if (type == "detach_potR")
	        echoesAvatar.detachObjectFromHand(right=true);                
	    else if (type == "detach_potOnStackR")
	    {    echoesAvatar.detachObjectFromHand(right=true);   
	        arg[1].stackUp(arg[0]);
	    }
	    else if (type == "put_ballContainer")
	    {    ball, container = arg;
	        echoesAvatar.detachObjectFromHand(right=true);
	        ball.droppedByAvatar = true;
	    }
	    else if (type == "touch_object")
	        echoesAvatar.app.canvas.rlPublisher.objectPropertyChanged(str(arg.id), "touchedByAgent", str(echoesAvatar.id));
	    else if (type == "touch_flower" && isinstance(arg[0], objects.Plants.EchoesFlower))
	    {
	    	if (arg[1] == "Bubble")
	            arg[0].intoBubble();
	        else
	            arg[0].intoBall();
	    }
	    else if (type == "make_rain" && isinstance(arg, objects.Environment.Cloud))
	        arg.rain(50);
	    else if (type == "touch_leaves" && isinstance(arg, objects.Plants.MagicLeaves))
	        arg.touchLeaves(echoesAvatar.id);
	    else if (type == "throw_ball" && isinstance(arg, objects.PlayObjects.Ball))
	    {    echoesAvatar.detachObjectFromHand(right=true);
	        arg.throw();
	    }
	    else if (type == "turn_towardsTarget")
	    {
	    	qi, target = arg;
	        if (isinstance(target, objects.EchoesObject.EchoesObject))
	        {
	        	if (target.id in echoesAvatar.app.canvas.objects)
	                target = Piavca.Vec(target.pos[0],target.pos[1],target.pos[2]);
	            else
	            {
	            	echoesAvatar.app.canvas.agentActionCompleted(action_id, false);
	                echoesAvatar.cancelMotions = 1;
	                return;
	            }
	        }
	        if (! isinstance(target, Piavca.Vec) && target == "child")
	            target = Piavca.Vec(echoesAvatar.pos[0], 0, 10);
	        x = target.X();
	        z = target.Z();
	        angle = Math.atan2(x-echoesAvatar.pos[0], z-echoesAvatar.pos[2]); 
	        //# move whole body for more than 75 degrees
	        time = 0.1;
	        a_ori = -1*echoesAvatar.orientation.Zangle();
	        if (Math.abs(angle - a_ori) > Math.toRadians(75))
	        {
	        	if (angle < a_ori)
	        		angle += math.radians(45);
	            else 
	            	angle -= math.radians(45);
	            echoesAvatar.orientation = Piavca.Quat(angle, Piavca.Vec.ZAxis());
	            time = 0.3;// # give the motion some time to turn around...
	        }
	        posture = Piavca.AvatarPosture();
	        posture.getPostureFromAvatar(echoesAvatar.avatar);
	        motion = Piavca.ChangeMotionLength(posture, time) ;   
	        qi.item = posture;
	    }
	    else if (type == "lookatTarget" && arg.length == 3)
	    { 
	    	motion, target, action_id = arg;
	        if (isinstance(target, objects.EchoesObject.EchoesObject))
	        {
	        	if (target.id in echoesAvatar.app.canvas.objects)
	                target = Piavca.Vec(target.pos[0],target.pos[1],target.pos[2]);
	            else
	            {
	            	echoesAvatar.app.canvas.agentActionCompleted(action_id, false);
	                echoesAvatar.cancelMotions = 1;
	                return;
	            }
	        }
	        if (! isinstance(target, Piavca.Vec) && target == "child")
	            target = Piavca.Vec(echoesAvatar.pos[0], 0, 10);
	        target -= Piavca.Vec(0,0,echoesAvatar.zOffset);        
	        target = target/echoesAvatar.scale;
	        target -= echoesAvatar.avatar.getJointBasePosition(echoesAvatar.jointIdByName['Bip01 Head'], Piavca.WORLD_COORD);
	        target = echoesAvatar.avatar.getRootOrientation().inverse().transform(target);
	        target = echoesAvatar.forwardOrientation.inverse().transform(target);
	        motion.setTarget(target);
	    }
	    else if (type == "pointatTarget" && arg.length == 3)
	    {
	    	motion, target, action_id = arg;
	        if (isinstance(target, objects.EchoesObject.EchoesObject))
	        {
	        	if (target.id in echoesAvatar.app.canvas.objects)
	                target = Piavca.Vec(target.pos[0],target.pos[1],target.pos[2]-echoesAvatar.zOffset);
	            else
	            {    echoesAvatar.app.canvas.agentActionCompleted(action_id, false);
	                echoesAvatar.cancelMotions = 1;
	                return;
	            }
	        }
	        motion.setTarget(target);
	    }
	    else if (type == "popBubble")
	    {
	    	target, action_id = arg;
	        if (target.id in echoesAvatar.app.canvas.objects and isinstance(target, objects.Bubbles.EchoesBubble))
	            target.click("Agent", false);
	        else
	            echoesAvatar.app.canvas.agentActionCompleted(action_id, false);
	    }
	    else if (type == "attach_cloud")
	        echoesAvatar.tcb.attachCloud(echoesAvatar.app.canvas.objects[arg]);
	    else if (type == "detach_cloud")
	        echoesAvatar.tcb.detachCloud();
	    else if (type == "check_at")
	    {
	    	o_id, action_id = arg;
	        o = echoesAvatar.app.canvas.objects[o_id];
	        if (!echoesAvatar.isAt(o_id) && !(o.overAgent && o.beingDragged))
	        {    Logger.warning("Avatar combined action Avatar is not at object " + str(o_id) + ". Object has been moved, action failed");
	            echoesAvatar.cancelMotions = 2;
	            echoesAvatar.app.canvas.agentActionCompleted(action_id, false);
	        }
	    }
	    else if (type == "hold_posture")
	    {
	    	time, old_qi = arg;
	        posture = Piavca.AvatarPosture();
	        posture.getPostureFromAvatar(echoesAvatar.avatar);
	        motion = Piavca.ChangeMotionLength(posture, time);
	        qi = QueueItem(motion);
	        if (old_qi.isFinal)
	        {
	        	qi.isFinal = true;
	            old_qi.isFinal = false;
	        }
	        if (old_qi.action_id != -1)
	        {
	        	qi.action_id = old_qi.action_id;
	            old_qi.action_id = -1;
	        }
	        echoesAvatar.motionQueue.appendleft(qi);
	    }
	    else if (type == "print_pos")
	    {
	    	Logger.trace("Debug", "real avatar position after motion" + str(echoesAvatar.avatar.getRootPosition()*echoesAvatar.scale));
	        Logger.trace("Debug", "avatar position after motion" + str(echoesAvatar.pos));
	        if (arg.length > 0)
	        {
	        	Logger.trace("info", "checking isAt object" + str(arg[0]));
	            if (! echoesAvatar.isAt(arg[0]))
	            { 
	            	Logger.warning("Avatar combined action Object has been moved, action failed");
	                echoesAvatar.cancelMotions = 2;
	                echoesAvatar.app.canvas.agentActionCompleted(arg[1], false);
	            }
	        }
	    }
	}
}