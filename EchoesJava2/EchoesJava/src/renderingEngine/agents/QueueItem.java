//translation from Py May 10th

package renderingEngine.agents;

import java.util.HashMap;


public class QueueItem
{
	//__init__ (item, isFinal=false, callback=None, preCall=None, playDirect=false, timer=None, speech=None, action_id = -1)
	//****callback, preCall etc. objects of which class?
	//HashMap<String, HashMap<String, Integer>> preCall = new HashMap();
    public void QueueItem(item, boolean isFinal, Object callback, Object preCall, boolean playDirect, timer=None, Object speech, int action_id)
    {
    	this.item = item;
        this.isFinal = isFinal;
        this.callback = callback;
        this.playDirect = playDirect;
        this.action_id = action_id;
        this.preCall = preCall;
        this.timer = timer;
        this.speech = speech;
    }
}
