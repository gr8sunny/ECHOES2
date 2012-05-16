//translation from Py May 10th 2012
package agents;

import java.util.Map;

public class Andy extends EchoesAvatar
{
	//Andy(autoAdd=true, props={"type" "Andy"}, callback=None)
	float low = (float) (-0.8 - 1.2);
    float knee = (float) (-0.8 - 0.5);
    float waist = (float) (-0.8 + 0.3);
    float high = (float) (-0.8 + 1);
    float walkingDistance;
    float stepDistance;
	private double scale;
	private Object floorheight;
	public Andy(boolean autoAdd, Map<String, String> props, Object callback)
    {    
        super("agents/Andy/Andy", autoAdd, props, (float) 0.055, callback);
    }
    public void startPostion()
    {    
    	this.orientation = Piavca.Quat(0, Piavca.Vec.ZAxis());
        this.setPosition((6,-0.8,-5));
    }   
    //setDepthLayer(layer="front", action_id = -1)
    public void setDepthLayer(String layer, int action_id)
    {
    	if (layer == "front")
    	{
    		this.scale = 0.055;
    		float [] tempArray = {this.pos[0], -0.8, this.pos[2]};
    	    this.setPosition(tempArray, action_id);
    	}
        else if (layer == "back")
        {
        	this.scale = 0.035;
        	float [] tempArray = {this.pos[0], 0.8, this.pos[2]};
            this.setPosition(tempArray, action_id);
        }
        //# these are affected by the scale of the avatar, so make sure they are re-computed
        this.floorheight = null;
        this.walkingDistance = this.getPlaneMotionDistance(this.walking, this.walking.getStartTime(), this.walking.getEndTime()); 
        this.stepDistance = this.getPlaneMotionDistance(this.step, this.walking.getStartTime(), this.walking.getEndTime());
    }
}