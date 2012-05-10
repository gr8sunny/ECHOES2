//translation from Py May 10th 2012
package agents;

public class Andy extends EchoesAvatar
{
    public void Andy(autoAdd=true, props={"type" "Andy"}, callback=None)
    {    
        super(Andy, ).__init__(app, "agents/Andy/Andy", autoAdd, props, 0.055, callback)
        this.low = -0.8 - 1.2
        this.knee = -0.8 - 0.5
        this.waist = -0.8 + 0.3
        this.high = -0.8 + 1
    }
    public void startPostion()
    {    
    	this.orientation = Piavca.Quat(0, Piavca.Vec.ZAxis());
        this.setPosition((6,-0.8,-5));
    }   
    public void setDepthLayer(layer="front", action_id = -1)
    {
    	if (layer == "front")
    	{
    		this.scale = 0.055;
    	    this.setPosition((this.pos[0], -0.8, this.pos[2]), action_id);
    	}
        else if (layer == "back")
        {
        	this.scale = 0.035;
            this.setPosition((this.pos[0], 0.8, this.pos[2]), action_id);
        }
        //# these are affected by the scale of the avatar, so make sure they are re-computed
        this.floorheight = null;
        this.walkingDistance = this.getPlaneMotionDistance(this.walking, this.walking.getStartTime(), this.walking.getEndTime()); 
        this.stepDistance = this.getPlaneMotionDistance(this.step, this.walking.getStartTime(), this.walking.getEndTime());     
}