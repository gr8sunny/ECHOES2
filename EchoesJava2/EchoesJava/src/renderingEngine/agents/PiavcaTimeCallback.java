//translation from Py May 10th

package renderingEngine.agents;

public class PiavcaTimeCallback extends Piavca.AvatarTimeCallback
{    
    public void PiavcaTimeCallback (name, echoesAvatar)
    {    super(PiavcaTimeCallback, ).__init__(name)
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
    }   
    public void init(avatar)
    { //   pass  
    }
    public void attachObjectToJoint(object, id)
    {
    	this.attachedObjects[object] = id;  
    }
        
    public void detachObjectFromJoint(id)
    {    
    	for object, oid in this.attachedObjects.items()
      	{
    		if (oid == id)
    		{
                if (hasattr(object, "detachFromJoint"))
                {
                	object.detachFromJoint();
                }
                del this.attachedObjects[object];//*****is attachedObjects a Dictionary?
    		}
      	}
    }
    public void detachObject(object)
    {
    	for o, oid in this.attachedObjects.items()
    	{
    		if (o == object)
    		{
                if (hasattr(object, "detachFromJoint"))
                {
                	object.detachFromJoint();
                }
                del this.attachedObjects[o];
    		}
    	}
    }           
    public void attachCloud(cloud)
    {
    	this.attachedCloud = cloud;
    }

    public void detachCloud()
    {
    	if (this.attachedCloud)
    	{
    		this.attachedCloud.detachFromAvatar();
    	    this.attachedCloud = None;
    	}
    }
    public void timeStep (avatar, time)
    {  //  # get motion stack processed
        if (this.echoesAvatar.playing and this.echoesAvatar.repositioner.getMotion().finished() and this.currentQi)
        {
        	this.echoesAvatar.playing = false;
            this.echoesAvatar.avatar.stopMotion();
            this.echoesAvatar.updatePos(adjustY=this.currentQi.isFinal);
            Logger.trace("info", "Avatar position after after action " + str(this.currentQi.action_id) +  " " + str(this.echoesAvatar.pos));
            if (this.currentQi.callback)
            {
            	avatarCallback(this.echoesAvatar, this.currentQi.callback[0],this.currentQi.callback[1]);
            }
            if (this.currentQi.isFinal)
            {
            	this.echoesAvatar.app.canvas.agentActionCompleted(this.currentQi.action_id, true);
            }
            this.currentQi = null;
        }
        if (! this.echoesAvatar.playing && !this.echoesAvatar.animating && len(this.echoesAvatar.motionQueue) > 0)
        {
        	this.currentQi = this.echoesAvatar.motionQueue.popleft();
            if (this.currentQi.preCall)
            {
            	avatarCallback(this.echoesAvatar, this.currentQi.preCall[0], this.currentQi.preCall[1]);
            }
            if (this.currentQi.speech)
            {    
            	if ("," in this.currentQi.speech)
            	{
            		allSpeech = this.currentQi.speech.split(",");
            	    allSpeech.reverse();
                    for s in allSpeech
                    {
                    	this.echoesAvatar.sayPreRecordedNow(s);
                    }
            	}
                else
                {
                	this.echoesAvatar.sayPreRecordedNow(this.currentQi.speech);
                }
            }
            if (this.echoesAvatar.cancelMotions == 0) 
            {
            	if (this.currentQi.playDirect)
            	{
            		this.echoesAvatar.playDirectAtPos(this.currentQi.item);
            	}
                else         
                {
                	this.echoesAvatar.playSmoothAtPos(this.currentQi.item);
                }
            }
            else
            {
            	this.echoesAvatar.cancelMotions -= 1;
            }
        }
        //# manual animation
        if (! this.echoesAvatar.playing && !this.echoesAvatar.animating && len(this.echoesAvatar.animationQueue) > 0)
        {
        	this.currentQi = this.echoesAvatar.animationQueue.popleft();
        	this.echoesAvatar.animationTargets = this.currentQi.item;
            if (this.currentQi.preCall)
            {
            	avatarCallback(this.echoesAvatar, this.currentQi.preCall[0], this.currentQi.preCall[1]);
            }
            this.echoesAvatar.avatar.stopMotion();
            this.echoesAvatar.animating = true;
        }
        if (this.echoesAvatar.animating and this.currentQi)
        {    
        	deletes = [];
        	for jointId, targetOrientation in this.echoesAvatar.animationTargets.iteritems()
        	{
        		currentOrientation = this.echoesAvatar.avatar.getJointOrientation(jointId);
        	    if (jointId not in this.animationSteppers)
        	    {
        	    	this.animationSteppers[jointId] = 0.0;
        	    }
                newOrientation = Piavca.slerp(currentOrientation, targetOrientation, this.animationSteppers[jointId]);
                this.echoesAvatar.avatar.setJointOrientation(jointId, newOrientation);
                this.animationSteppers[jointId] += 0.01;//*********this may be a dictionary
                if (this.animationSteppers[jointId] > 1.0)
                {
                	deletes.append(jointId);
                }
        	}
            for id in deletes
//#                print "Finished animating joint", id, "(", this.echoesAvatar.avatar.getJointName(id), ")"
            {   
            	del this.echoesAvatar.animationTargets[id];
                del this.animationSteppers[id];
            }
            if (len(this.animationSteppers) == 0)
            {
            	this.echoesAvatar.animating = false;
                if (this.currentQi.isFinal)
                {
                	this.echoesAvatar.app.canvas.agentActionCompleted(this.action_id, true);
                }
            }
        }
        //# attached objects
        for object, id in this.attachedObjects.items()
        {
        	jpos = this.echoesAvatar.avatar.getJointBasePosition(id, Piavca.WORLD_COORD) * this.echoesAvatar.scale;
            jori = this.echoesAvatar.avatar.getJointOrientation(id, Piavca.WORLD_COORD);
            if (hasattr(object, "attachToJoint"))
            {
            	//**********list problem
            	object.attachToJoint([jpos.X(), jpos.Y(), jpos.Z()+this.echoesAvatar.zOffset], [jori.Xangle(), jori.Yangle(), jori.Zangle()], );
            }
        }
        if (this.attachedCloud)
        {
        	jpos = this.echoesAvatar.avatar.getRootPosition() * this.echoesAvatar.scale;
            jori = this.echoesAvatar.forwardOrientation.inverse() * this.echoesAvatar.avatar.getRootOrientation();
            this.attachedCloud.attachToAvatar([jpos.X(), jpos.Y(), jpos.Z()+this.echoesAvatar.zOffset], [jori.Xangle(), jori.Yangle(), jori.Zangle()], );
        }                        
        //# facial expressions
        for name, weight in this.expressionWeights.items()
        {
        	if (weight != this.echoesAvatar.expressionTargets[name])
        	{
        		if (not this.echoesAvatar.playing)
        		{		
                    posture = Piavca.AvatarPosture();
                    posture.getPostureFromAvatar(this.echoesAvatar.avatar);
                    this.echoesAvatar.motionQueue.append(QueueItem(posture));
        		}
                if (weight > this.echoesAvatar.expressionTargets[name])
                {
                	this.expressionWeights[name] -= 0.05;
                    if (this.expressionWeights[name] <= this.echoesAvatar.expressionTargets[name]) 
                    {
                    	this.expressionWeights[name] = this.echoesAvatar.expressionTargets[name];            
                    }
                }
                else    
                {
                	this.expressionWeights[name] += 0.05;
                    if (this.expressionWeights[name] >= this.echoesAvatar.expressionTargets[name]) 
                    {
                    	this.expressionWeights[name] = this.echoesAvatar.expressionTargets[name];
                    }
                }
                this.echoesAvatar.facialExpMotion.setFloatValue(this.echoesAvatar.facialExpressions[name], this.expressionWeights[name]);
        	}
        }
        //# speech queue
        if (! this.echoesAvatar.speaking && len(this.echoesAvatar.speechQueue) > 0)
        {
        	ai = this.echoesAvatar.speechQueue.popleft();
            sound.EchoesAudio.playSound(ai.item, action_id=ai.action_id);
            this.echoesAvatar.speaking = true;
            ai.timer.start();
        }
    }
}