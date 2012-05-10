//translation from Py May 10th 2012
package agents;

public class Paul (EchoesAvatar)
{
    public void __init__(autoAdd=true, props={"type" "Paul"}, callback=None)
    {    
        
        
        super(Paul, ).__init__(app, "agents/Paul/Paul", autoAdd, props, 0.0275, callback)
        this.low = -0.8 - 1
        this.knee = -0.8 - 0.5
        this.waist = -0.8 + 0.3
        this.high = -0.8 + 1        
    }
}
