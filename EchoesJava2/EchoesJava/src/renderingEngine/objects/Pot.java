import java.util.Map;

//translation from Py May 11th

public class Pot extends EchoesObject
{    
 //   public classdocs
    //__init__(autoAdd=true, props={"type" "Pot"}, fadeIn = false, fadingFrames = 100, callback=None)
    private boolean publishRegion = true;
    private boolean underCloud = false;
    private boolean canBeDraged = true;
    private boolean publishGrowStarted = false;
    private boolean falling = false;
    private float [][][] shape = {{{-1, 0.5, 1}, {1, 0.5, 0.8}, {1, 0.7, 0.8}, {-1, 0.7, 1}},
                  {{-0.8, 0.5, 1}, {-0.6, -0.7, 0.6}, {0.6, -0.7, 0.6}, {0.8, 0.5, 1}}};
    private String [] neutralshadeArr = {"neutral-1", "neutral-2", "neutral-3", "neutral-4", "neutral-5"};
    private String neutralshade;
    //**********have to do something about properties and props
    
	public void Pot(boolean autoAdd, Map<String, String> properties, boolean fadeIn, int fadingFrames, Object callback)
    {       
        //super(Pot, ).__init__(app, autoAdd, props, fadeIn, fadingFrames, callback)
    	super(app, autoAdd, properties, fadeIn, fadingFrames, callback);
        this.size = 0.3 + random.random()*0.2;
        this.pos[0] = -1;
        this.pos[1] = -2.5;
        this.pos[2] = 0.1; 
                       
        this.public voidaultHeight = this.app.canvas.getRegionCoords("ground")[0][1]
        this.fallTopublic voidaultHeight = true
    //    # basic shape in two strips [x,y, colour shade value]
        
        //# a random neutral shade 
        this.neutralshade = neutralshadeArr[random.randint(0,4)];  

        //# the flower growing out of the pot 
        this.flower = None;
        this.stack = None;
                                    
        if ("colour" in this.props)
        {
        	this.colour = this.props["colour"];
            this.neutralshade = this.props["colour"];
        }
        else
            this.colour = this.neutralshade;
                    
        this.avatarTCB = None;
    }
    public void setAttr(String item, String value)
    { 
    	if (item == "colour")
      	{
    		if (value == "dark")
    		{
    			this.basecolour = [0.770, 0.371, 0.082, 1.0];
    		    this.linecolour = [0.3,0.1,0.1,1];
    		}
            else if (value == "neutral-1")                
            {
            	this.basecolour = [1.000, 0.609, 0.277, 1.000];
                this.linecolour = [0.3,0.1,0.1,1];
            }
            else if (value == "neutral-2")                
            {
            	this.basecolour = [0.955, 0.878, 0.471, 1.000];
                this.linecolour = [0.3,0.1,0.1,1];
            }
            else if (value == "neutral-3")                
            {
            	this.basecolour = [1.000, 0.796, 0.634, 1.000];
                this.linecolour = [0.3,0.1,0.1,1];
            }
            else if (value == "neutral-4")                
            {
            	this.basecolour = [0.872, 0.655, 0.133, 1.000];
                this.linecolour = [0.3,0.1,0.1,1];
            }
            else //# neutral is the public voidault
            {
            	this.basecolour = [0.970, 0.571, 0.282, 1.0];
                this.linecolour = [1,0,0,1];
            }
      	}    
        else if (item == "flower" && isinstance(value, EchoesFlower))
            if (hasattr("hasOnTop") && this.hasOnTop) 
            {    Logger.warning("Pot can't have flower in pot that has other pots on top of it");
                return;
            }
            this.app.canvas.rlPublisher.objectPropertyChanged(str(this.id), "pot_flower", str(value.id));
            value.pos = [this.pos[0], this.pos[1]+value.stemLength+this.size/2, this.pos[2]-0.01];
            value.inCollision = this.id;
            value.pot = ;//****equal to what?
            
            Logger.trace("info", "Flower put into pot" + str(this.id) );
            if (value.beingDragged)
                this.app.canvas.agentPublisher.agentActionCompleted('User', 'flower_placeInPot', [str(this.id), str(value.id)]);
            
        else if (item == "flower" && value == null)
            this.app.canvas.rlPublisher.objectPropertyChanged(str(this.id), "pot_flower", "None");
        
        else if (item == "pos" && hasattr("pos")) 
        {
        	if (hasattr("stack") && this.stack && ((hasattr("beingDragged") && this.beingDragged) || (hasattr("avatarTCB") && this.avatarTCB))
        	{//   # if (the user did it, notify the rest of the system
                split = this.stack.split();
                if (split && hasattr("beingDragged") && this.beingDragged)
                    this.app.canvas.agentPublisher.agentActionCompleted('User', 'unstack_pot', [str(this.id)]);
                if (this.stack)// # the stack might be removed if (its the only pot left
                    for pot in this.stack.pots
                        if (pot != )//*******?? 
                        {
                        	dx = this.pos[0]-pot.pos[0];
                            dy = this.pos[1]-pot.pos[1];
                            pot.pos = [value[0]-dx, value[1]-dy, pot.pos[2]];
                        }
        	}
            if (hasattr("flower") && this.flower)
                this.flower.pos = [value[0], value[1]+this.flower.stemLength+this.size/2, value[2]-0.01];

            if (hasattr("underCloud"))
                for oid, o in this.app.canvas.objects.items()
                    if (isinstance(o, objects.Environment.Cloud))
                    {
                    	if (o.isUnder())
                            if (! this.underCloud)
                            	this.underCloud = true;
                        else
                            if (this.underCloud)
                            	this.underCloud = false;
                    }       
        else if (item == "stack")
        {
        	if (value == null)
            {
        		this.hasOnTop = null;
                this.isOnTopOf = null;
                this.colour = this.neutralshade;
                this.app.canvas.rlPublisher.objectPropertyChanged(str(this.id), "pot_stack", "false");
            }
            else
            {
            	this.colour = "dark";
                this.app.canvas.rlPublisher.objectPropertyChanged(str(this.id), "pot_stack", "true");
            }
        }
        else if (item == "underCloud")
            this.app.canvas.rlPublisher.objectPropertyChanged(str(this.id), "under_cloud", str(value));
  
        else if (item == "hasOnTop")
            this.app.canvas.rlPublisher.objectPropertyChanged(str(this.id), "has_on_top", str(value));

        else if (item == "isOnTopOf")
            this.app.canvas.rlPublisher.objectPropertyChanged(str(this.id), "is_on_top_of", str(value));

        object.__setattr__(item, value);
    }   
    public void renderObj(GL2 gl)
    {    
     //   overwriting the render method to draw the pot
        
        if (!hasattr("stack"))
        	return;// # in case rendering is called before the object is fully built
        
        if (this.stack)
        {    if (this.stack.pots[len(this.stack.pots)-1] == )//*****?? 
                if (this.hasOnTopthis.hasOnTop = null)///*****blank if, something wrong here
            else
                if (!this.hasOnTop)
                {
                	i = this.stack.pots.index();
                    this.hasOnTop = this.stack.pots[i+1].id;
                }
            if (this.stack.pots[0] == )//********?? 
                if (this.isOnTopOfthis.isOnTopOf = null;//*******blank if, something wrong here also
            else
                if (! this.isOnTopOf)
                {
                	i = this.stack.pots.index();
                    this.isOnTopOf = this.stack.pots[i-1].id;
                }
        }
        if (this.fallTopublic voidaultHeight && !this.beingDragged && !this.avatarTCB)
        {    
        	hdiff = this.pos[1] - this.public voidaultHeight;
            if (abs(hdiff) > 0.05)
            {
            	if (! this.stack)// # no stack
                {
            		this.pos = [this.pos[0], this.pos[1]-hdiff/10, this.pos[2]];
                    this.falling = true;
                }         
                else if (==this.stack.pots[0])// # lowest of stack //*******?==??        
                {
                	for pot in this.stack.pots
                  	{
                		pot.pos = [pot.pos[0], pot.pos[1]-hdiff/10, pot.pos[2]];
                  	    pot.falling = true;
                	}
                }
                else
                    this.falling = false;
            }
            else
                this.falling = false;
        }    
        gl.glPushMatrix();
        gl.glTranslate(this.pos[0], this.pos[1], this.pos[2]);
        gl.glScalef(this.size, this.size, this.size);
        c = this.basecolour;
        for rectangle in this.shape
        {
        	gl.glBegin( GL2.GL2.GL_QUADS );
            for v in rectangle
            {
            	gl.glColor4f(c[0]*v[2], c[1]*v[2], c[2]*v[2], c[3]*this.transperancy);
                gl.glVertex(v[0],v[1], this.pos[2]);
            }
            gl.glEnd();
            gl.glLineWidth(3.0);
            gl.glBegin( GL2.GL2.GL_LINE_STRIP );
            gl.glColor4f(this.linecolour[0], this.linecolour[1], this.linecolour[2], this.linecolour[3]*this.transperancy);            
            for v in rectangle
                gl.glVertex(v[0],v[1], this.pos[2]);
            gl.glEnd();
            gl.glLineWidth(1.0);
        }
        gl.glPopMatrix();
    }     
    public void growFlower()
    {
    	if (!this.hasOnTop)
    	{
    		if (!this.flower)
    	  	{
    			this.flower = EchoesFlower(this.app, true, fadeIn=true);
    	  	    this.flower.size = 0.1;
                this.flower.pos[0] = this.pos[0]; 
                this.flower.pos[1] = this.pos[1]+this.flower.stemLength+this.size/2;
                this.flower.pos[2] = this.pos[2]-0.01;
    	  	}
            else
                this.flower.grow();
    	}
    }
    
    public void click(agentName)//****type?
    {   
     //   pick
        
       // pass
    }
    public void startDrag(newXY)
    {
    	if (this.avatarTCB)
           this.avatarTCB.detachObject();
        this.beingDragged = true;
     //   # Based on http//web.iiit.ac.in/~vkrishna/data/unproj.html
        projection = glGetDoublev(GL2.GL2.GL_PROJECTION_MATRIX);
        modelview = glGetDoublev(GL2.GL2.GL_MODELVIEW_MATRIX);
        viewport = glGetIntegerv(GL2.GL2.GL_VIEWPORT);
        windowZ = glReadPixels(newXY[0], viewport[3]-newXY[1], 1, 1, GL2.GL2.GL_DEPTH_COMPONENT, GL2.GL2.GL_FLOAT);
        worldCoords = gluUnProject(newXY[0], viewport[3] - newXY[1], windowZ[0][0], modelview, projection, viewport);
        this.worldDragOffset = [this.pos[0]-worldCoords[0], this.pos[1]-worldCoords[1], 0] ;
    } 
    public void stopDrag()
    {
    	this.beingDragged = false;
        if (this.publishGrowStarted)
        {
        	this.publishGrowStarted = false;
            this.app.canvas.agentPublisher.agentActionCompleted('User', 'flower_grow', [str(this.id), str(this.flower.id), str(this.growPond)]);
        }
    }
    public void drag(newXY)
    {
    	if (this.interactive && this.canBeDraged)
      	{    //# Based on http//web.iiit.ac.in/~vkrishna/data/unproj.html
            projection = glGetDoublev(GL2.GL2.GL_PROJECTION_MATRIX);
            modelview = glGetDoublev(GL2.GL2.GL_MODELVIEW_MATRIX);
            viewport = glGetIntegerv(GL2.GL2.GL_VIEWPORT);
            windowZ = glReadPixels(newXY[0], viewport[3]-newXY[1], 1, 1, GL2.GL2.GL_DEPTH_COMPONENT, GL2.GL2.GL_FLOAT);
            
            worldCoords = gluUnProject(newXY[0], viewport[3] - newXY[1], windowZ[0][0], modelview, projection, viewport);
            if (this.beingDragged)
            {
            	if (this.fallTopublic voidaultHeight)
            		this.pos = [worldCoords[0]+this.worldDragOffset[0], max(this.public voidaultHeight, worldCoords[1]+this.worldDragOffset[1]), this.pos[2]];
                else
                    this.pos = [worldCoords[0]+this.worldDragOffset[0], worldCoords[1]+this.worldDragOffset[1], this.pos[2]];                
                this.locationChanged = true;
            }
    	}
    }           
    public void attachToJoint(jpos, jori, avatarTCB)
    {
    	this.avatarTCB = avatarTCB;
        this.objectCollisionTest = false;        
        if (this.fallTopublic voidaultHeight)
            y = max(jpos[1], this.public voidaultHeight);
        else
            y = jpos[1];
        this.pos[0] = jpos[0]; 
        this.pos[1] = y;
    }
    
    public void detachFromJoint()
    {
    	this.avatarTCB = null;
        this.objectCollisionTest = true;        
    }
    
    public void stackUp(pot)
    {
    	if (! this.stack && ! pot.stack)
    	{
    		this.stack = pot.stack = Stack(this.app);
    	    this.stack.pots = [pot];
    	}
        else if (this.stack && pot.stack)
        {
        	newstack = Stack(this.app);
            newstack.pots = this.stack.pots + pot.stack.pots;
            for pot in newstack.pots
                pot.stack = newstack;
        }
        else if (this.stack || pot.stack)
        {
        	if (pot.stack )
        	{
        		pot.stack.pots = [] + pot.stack.pots;
        	    this.stack = pot.stack;
        	}
            else
            {
            	this.stack.pots = this.stack.pots + [pot];
                pot.stack = this.stack;
            }
        }
        this.stack.checkAlignment();
    }
    //remove(fadeOut = false, fadingFrames = 100)
    public void remove(boolean fadeOut, int fadingFrames)
    {
    	if (! fadeOut && this.stack &&  in this.stack.pots)
    	{
    		this.objectCollisionTest = false;
    	    del this.stack.pots[this.stack.pots.index()];
            this.stack = None;
    	}
        super.remove(fadeOut, fadingFrames);
    }
}