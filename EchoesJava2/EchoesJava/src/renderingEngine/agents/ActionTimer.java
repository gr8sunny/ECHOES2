//translation from Py May 10th

package renderingEngine.agents;

public class ActionTimer(threading.Thread)
{     
	//ActionTimer(seconds, avatar, int action_id=-1, speech=false)
	//*****avatar, seconds...which class?
    boolean speech;	
	public ActionTimer(seconds, avatar, int action_id, boolean speech)
    {    
    	this.runTime = seconds;
        this.app = avatar.app;       
        this.avatar = avatar;        
        this.action_id = action_id;
        this.speech = speech;
        //****call constructor of Thread class?
        threading.Thread.__init__();
    }
    
    public void run()
    {
    	if (this.speech)
    	{
    		float weight = 0.0;
    	    for(int i=0;i < (int)(2*this.runTime);i++)
    	    {    
    	    	time.sleep(0.5);
    	        if (weight == 0.0)
    	        {
    	        	this.avatar.setFacialExpression("OpenMouth", 1.0);
    	            weight = 1.0;
    	        }
                else
                {
                	this.avatar.setFacialExpression("OpenMouth", 0.0);
                    weight = 0.0;
                }
    	    }
            this.avatar.setFacialExpression("OpenMouth", 0.0);
    	}
        else
        {
        	time.sleep(this.runTime);
        }
            
        if (this.speech)
        {
        	this.avatar.speaking = false;
        }
        this.app.canvas.agentActionCompleted(this.action_id, true);
    }
}
