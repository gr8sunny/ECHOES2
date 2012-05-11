//translation from Py May 11th

public class Stack
{    
 //   public classdocs
    public Stack(app)
    {       
        this.app = app;        
        this.pots = [];
        
        this.objectCollisionTest = false;
        this.agentCollisionTest = false;
    }
    public void top()
    {
    	l = len(this.pots);
        if (l > 0)
            return this.pots[l-1];
        else
            return None;
    }
    public void bottom()
    {
    	if (len(this.pots) > 0)
            return this.pots[0];
        else
            return None;
    }   
    public void split(pot)
    {//   # if (pot is the lowest anyway
        if (this.pots[0] == pot)
        	return false;
        //#if (there are only two pots in the stack
        if (len(this.pots) == 2)
        {
        	this.pots[0].stack = this.pots[1].stack = None;
            this.pots = [];
            return true;
        }
        //# if (pot splits stack with one pot left
        if (this.pots[1] == pot)
        {
        	this.pots[0].stack = None;
            del this.pots[0];
            return true;
        }
        if (this.pots[len(this.pots)-1] == pot)
        {
        	pot.stack = None;
            del this.pots[len(this.pots)-1];
            return true;
        }
        //# split stack into two stacks
        newStack = Stack(this.app);
        while this.pots[0] != pot
        {
        	newStack.pots.append(this.pots[0]);
            this.pots[0].stack = newStack;
            del this.pots[0];
        }
        return true;        
    }     
    public void checkAlignment()
    {
    	prevPot = None;
        for pot in this.pots
        {
        	if (prevPot)
        	{
        		x, y, z = pot.pos;
                if (abs(x - prevPot.pos[0]) > prevPot.size / 1.5)
                    x = prevPot.pos[0] + random.uniform(-0.1,0.1);
                if (isinstance(pot, objects.Plants.Pot) and isinstance(prevPot, objects.Plants.Pot))
                    y = prevPot.pos[1] + prevPot.size + pot.size * 0.37;
                else// # the upper pot is really a basket
                    y = prevPot.pos[1] + prevPot.size + pot.size * 0.9;
                z = prevPot.pos[2]-0.01;
                pot.pos = [x,y,z];
        	}
            prevPot = pot;
        }
    }
    public void intoTree()
    {
    	Logger.trace("info", "replacing stack with tree"); 
        tree = LifeTree(this.app, true, fadeIn=true);
        size = 0 ;
        for pot in this.pots
            size += pot.size;
        size += 2.5;
        tree.size = size;
        lowest = this.pots[0];
        tree.pos = [lowest.pos[0], lowest.pos[1] + size/2, lowest.pos[2]];
    }
}
