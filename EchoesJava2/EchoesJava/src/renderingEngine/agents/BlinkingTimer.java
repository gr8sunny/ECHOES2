//translation from Py May 10th

package renderingEngine.agents;


public class BlinkingTimer extends threading.Thread
{     
    public void BlinkingTimer(avatar)//****which class for avatar?
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
    		trackId = this.avatar.facialExpressions["ClosedEyes"];//****Dictionary problem
            this.avatar.facialExpMotion.setFloatValue(trackId, 1.0);
//#            Logger.trace("info", "Blinking - eyes closing")
            time.sleep(0.3);
            this.avatar.facialExpMotion.setFloatValue(trackId, 0.0);
//#            Logger.trace("info", "Blinking - eyes opening")
            time.sleep(random.randint(2,4));
    	}
    }
}