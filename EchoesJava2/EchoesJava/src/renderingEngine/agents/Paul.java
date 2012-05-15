//translation from Py May 10th 2012
package agents;

import java.util.Map;

public class Paul extends EchoesAvatar
{
	//__init__(autoAdd=true, props={"type" "Paul"}, callback=None)
	float low = (float) (-0.8 - 1);
	float knee = (float) (-0.8 - 0.5);
	float waist = (float) (-0.8 + 0.3);
	float high = (float) (-0.8 + 1);
    public Paul(boolean autoAdd, Map<String, String> props, Object callback)
    {    
        super("agents/Paul/Paul", autoAdd, props, (float) 0.0275, callback);
                
    }
}
