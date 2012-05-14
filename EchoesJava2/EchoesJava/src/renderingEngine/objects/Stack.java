import java.util.*;

//translation from Py May 11th

public class Stack
{    
 //   public classdocs
	private boolean objectCollisionTest = false;
    private boolean agentCollisionTest = false;
    Vector pots = new Vector(); //*****is pots of the type Pot?
    
    public Stack(app)
    {       
        this.app = app;        
        //this.pots = [];
    }
    public Object top()//****return type?
    {
    	int l = this.pots.size();
        if (l > 0)
            return this.pots.get(l-1);
        else
            return null;
    }
    public Object bottom()
    {
    	if (this.pots.size() > 0)
            return this.pots.get(0);
        else
            return null;
    }   
    public boolean split(Object pot)
    {//   # if (pot is the lowest anyway
        if (this.pots.get(0) == pot)
        	return false;
        //#if (there are only two pots in the stack
        if (this.pots.size() == 2)
        {
        	this.pots.get(0).stack = this.pots.get(1).stack = null;
            this.pots.removeAllElements();
            return true;
        }
        //# if (pot splits stack with one pot left
        if (this.pots.get(1) == pot)
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
                if (isinstance(pot, objects.Plants.Pot) && isinstance(prevPot, objects.Plants.Pot))
                    y = prevPot.pos[1] + prevPot.size + pot.size * 0.37;
                else// # the upper pot is really a basket
                    y = prevPot.pos[1] + prevPot.size + pot.size * 0.9;
                z = prevPot.pos[2]-0.01;
                pot.pos[0] = x;
                pot.pos[1] = y;
                pot.pos[2] = z;
        	}
            prevPot = pot;
        }
    }
    public void intoTree()
    {
    	Logger.trace("info", "replacing stack with tree"); 
        tree = LifeTree(this.app, true, fadeIn=true);//****set default params
        int size = 0 ;
        for pot in this.pots
            size += pot.size;
        size += 2.5;
        tree.size = size;
        lowest = this.pots[0];
        tree.pos[0] = lowest.pos[0];
        tree.pos[1] = lowest.pos[1] + size/2;
        tree.pos[2] = lowest.pos[2];
    }
}
