//translation from Py May 10th

package renderingEngine.agents;


public class BlinkingTimer extends threading.Thread
{     
    private boolean running;
	private Object avatar;
	public BlinkingTimer(avatar)//****which class for avatar?
    {
    	this.app = avatar.app;         
        this.avatar = avatar;
        //****calling constructor of Threads?
        threading.Thread.__init__();
        this.running = true;
    }    
    public void run()
    {
    	while(this.running) 
    	{
    		Object trackId = this.avatar.facialExpressions.get("ClosedEyes");//****Dictionary problem
            this.avatar.facialExpMotion.setFloatValue(trackId, 1.0);
//#            Logger.trace("info", "Blinking - eyes closing")
            time.sleep(0.3);
            this.avatar.facialExpMotion.setFloatValue(trackId, 0.0);
//#            Logger.trace("info", "Blinking - eyes opening")
            int randomNum = 2 + (int)(Math.random()*4);//generating a random integer in range(2,4)
            time.sleep(randomNum);
    	}
    }
}