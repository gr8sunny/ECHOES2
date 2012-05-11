import java.util.Map;

//translation from Py May 11th
public class EchoesFlower
{    
    //public classdocs
	//__init__(autoAdd=true, props={"type" "Flower"}, fadeIn = false, fadingFrames = 100, callback=None)
    private float size = 0.4;
    private float maxSize = 0.6;
    private float [] pos = {0, 0, 0};
    private float [] rotate = {0, 0, 0};
    private boolean publishRegion = true;
    private boolean underCloud = false;
    private float amplitude = 0;
    private float swing = 0;
    private float[][] shape = {{-1, -1}, {1, -1}, {1, 1}, {-1, 1}};
    private float[][] texshape = {{0, 0}, {1, 0}, {1, 1}, {0, 1}};
    private boolean canTurnIntoBall = true;
    private boolean canTurnIntoBubble = true;
    private boolean childCanTurnIntoBubble = true;
    private boolean childCanTurnIntoBall = true;
    
	public EchoesFlower(boolean autoAdd, Map<String, String> properties, boolean fadeIn, int fadingFrames, Object callback)
    {        
        super(app, autoAdd, properties, fadeIn, fadingFrames, callback);
           
        if ("colour" in this.properties)
        {
        	this.colour = this.properties["colour"];
        }
        else
        {
        	this.colour = "red";
        }
        
        this.patterntex = this.setImage("visual/images/Circles.png");                    
        
        this.targetPos = None                                
        this.targetBasket = None
                
        this.pot = None 
        this.basket = None
        this.inCollision = None
        this.canGrow = true
        this.isGrowing = 0
        this.growToSize = None
        this.avatarTCB = None
        
    }
    public void setAttr(String item, String value)
    {
    	if (item == "size")
    	{
    		this.stemLength = value * 4;
            this.calcStemPoints();
            this.stemWidth = (int)(min(this.app.canvas.lineWidthRange[1] * 2 * value, 10));            
    	}
        else if (item == "growToSize")
        {
        	value = min(this.maxSize, value);
        }

        else if (item == "colour")
        {
        	if (value == "green")
                this.texture = this.setImage("visual/images/FlowerHead-01.png");
            else if (value == "blue")
                this.texture = this.setImage("visual/images/FlowerHead-03.png");
            else if (value == "yellow")
                this.texture = this.setImage("visual/images/FlowerHead-04.png");
            else //# red is the public voidault
                this.texture = this.setImage("visual/images/FlowerHead-02.png");
        }
        else if (item == "pos" && hasattr("pos") && hasattr("underCloud"))
        {
        	for oid, o in this.app.canvas.objects.items()
        	    if (isinstance(o, objects.Environment.Cloud))
                {
                	if (o.isUnder())
                        if (! this.underCloud) this.underCloud = true);
                    else
                        if (this.underCloud) this.underCloud = false;
                }
        }               
        else if (item == "pot")
        {
        	if (value == null)
                this.app.canvas.rlPublisher.objectPropertyChanged(str(this.id), "flower_pot", "None");
            else
                this.app.canvas.rlPublisher.objectPropertyChanged(str(this.id), "flower_pot", str(value.id));
        }    
        else if (item == "basket")
        {
        	if (value == None)
                this.app.canvas.rlPublisher.objectPropertyChanged(str(this.id), "flower_basket", "None");
            else
                this.app.canvas.rlPublisher.objectPropertyChanged(str(this.id), "flower_basket", str(value.id));
        }
        else if (item == "underCloud")
        {
        	this.app.canvas.rlPublisher.objectPropertyChanged(str(this.id), "under_cloud", str(value));
        }
            
        else if (hasattr("isGrowing") && item == "isGrowing")
        {
        	if (this.isGrowing > 0 && value == 0)
                this.app.canvas.rlPublisher.objectPropertyChanged(str(this.id), "is_growing", "false");
            if (this.isGrowing <= 0 and value > 0)
                this.app.canvas.rlPublisher.objectPropertyChanged(str(this.id), "is_growing", "true");
        }
        object.__setattr__(item, value);
    }                   
    public void findTargetBasket()
    {
    	for id, se in this.app.canvas.objects.items()
            if (isinstance(se, objects.Environment.Basket))
            {
            	this.targetBasket = se;
                break;
            }
    }       
    public void calcStemPoints()
    {
    	this.stemPoints = [];
        for i in range(4)
        {
        	if (i > 0 && i < 4)
                x = random.uniform(-0.2,0.2);
            else
                x = 0;
            this.stemPoints.append([x, -1*this.stemLength*float(i)/3.0, 0]);
        }
    }       
    public void renderObj()
    {     
     //   overwriting the render method to draw the flower
        
        if (! (hasattr("swing")))
        	return;
        
        if ((! (this.basket && this.basket.avatarTCB) &&
            ! (this.pot && this.pot.avatarTCB))
        {
        	if (!this.inCollision)
                if (this.basket)
                {
                	this.basket.removeFlower();
                    this.basket = null;
                }
                if (this.pot)
                {
                	this.pot.flower = null;
                    this.pot = null;
                }
            this.inCollision = None;             
        }
        if (this.isGrowing > 0)
            this.isGrowing -= 1;
            
        if (this.growToSize && this.canGrow)
        {
        	if (this.size < this.growToSize)
                this.grow();
            else
                this.growToSize = null;
        }
        if (this.targetPos)
        {
        	d = [0,0,0];
            for (int i=0; i<3 ;i++)
                d[i] = this.targetPos[i] - this.pos[i];
            this.pos = [this.pos[0] + d[0] / 20, this.pos[1] + d[1] / 20, this.pos[2] + d[2] / 20];
            if (abs(d[0]+d[1]+d[2]) < 0.05)
            {
            	this.pos = this.targetPos;
                this.targetPos = None;
                if (this.targetBasket)
                {
                	this.targetBasket.addFlower();
                    this.interactive = true;
                    this.targetBasket = None;
                }
            }
        }           
            
        if (! this.beingDragged)
        {
        	this.swing = (this.swing + 0.1) % (2*math.pi); //# animate the swinging stem
            this.amplitude = this.amplitude - 0.005;
            if (this.amplitude < 0 this.amplitude = 0;    
        }
        dx= -1.5*this.size * this.amplitude * math.sin(this.swing);
        dy= this.stemLength - math.sqrt(math.pow(this.stemLength, 2) - math.pow(dx, 2));        
        this.stemPoints[0]=(-1*dx,-1*dy,this.pos[2]);
                                         
        gl.gl.glPushMatrix()
        
            //# centre position
        gl.gl.glTranslate(this.pos[0], this.pos[1], this.pos[2]);  //make sure the head is in front of the stem
        gl.gl.glRotatef(this.rotate[2],0,0,1);
        
        //    # Stem
        if (! (hasattr("stemWidth")) || this.stemWidth == 0)
            this.stemWidth = 1;
        gl.gl.glLineWidth(this.stemWidth);
        gl.gl.glColor4f(0.229, 0.259, 0.326, this.transperancy);
        this.app.canvas.drawBezier(this.stemPoints, false);
        gl.gl.glLineWidth(1.0);
            //# touch area for better dragging
        gl.gl.glDisable(GL2.GL2.GL_DEPTH_TEST);
        gl.gl.glColor4f(1, 1, 1, 0.0);
        gl.gl.glBegin(GL2.GL2.GL_QUADS);
        gl.gl.glVertex3f(-this.size*0.7, 0, -0.1);
        gl.gl.glVertex3f(this.size*0.7, 0, -0.1);
        gl.gl.glVertex3f(this.size*0.7, -this.stemLength, -0.1);
        gl.gl.glVertex3f(-this.size*0.7, -this.stemLength, -0.1);
        gl.gl.glEnd();
        gl.gl.glEnable(GL2.GL2.GL_DEPTH_TEST);
            //# Head;
        gl.gl.glEnable( GL2.GL2.GL_ALPHA_TEST );
        gl.gl.glAlphaFunc( GL2.GL2.GL_GREATER, 0.1 );        
        gl.gl.glEnable( GL2.GL2.GL_TEXTURE_2D );
        gl.gl.glTexParameterf(GL2.GL2.GL_TEXTURE_2D, GL2.GL2.GL_TEXTURE_MAG_FILTER, GL2.GL2.GL_NEAREST);
        gl.gl.glTexParameterf(GL2.GL2.GL_TEXTURE_2D, GL2.GL2.GL_TEXTURE_MIN_FILTER, GL2.GL2.GL_NEAREST);
        gl.gl.glBindTexture(GL2.GL2.GL_TEXTURE_2D, this.texture);
        gl.gl.glTranslate(this.stemPoints[0][0], this.stemPoints[0][1], this.stemPoints[0][2]+0.05);
        gl.gl.glScalef(this.size, this.size, this.size);
        gl.gl.glColor4f(1, 1, 1, this.transperancy);
        gl.gl.glBegin(GL2.GL2.GL_QUADS);
        ti = 0;
        for v in this.shape
        {
        	gl.gl.glTexCoord2d(this.texshape[ti][0], this.texshape[ti][1]);
            gl.gl.glVertex3f(v[0], v[1], this.pos[2]);
            ti += 1;
        }
        gl.gl.glEnd();
        gl.gl.glDisable( GL2.GL2.GL_TEXTURE_2D );

        if (! this.childCanTurnIntoBall || !this.childCanTurnIntoBubble)
        {
        	gl.gl.glEnable( GL2.GL2.GL_TEXTURE_2D );
            gl.gl.glTexParameterf(GL2.GL2.GL_TEXTURE_2D, GL2.GL2.GL_TEXTURE_MAG_FILTER, GL2.GL2.GL_NEAREST);
            gl.gl.glTexParameterf(GL2.GL2.GL_TEXTURE_2D, GL2.GL2.GL_TEXTURE_MIN_FILTER, GL2.GL2.GL_NEAREST);
            gl.gl.glBindTexture(GL2.GL2.GL_TEXTURE_2D, this.patterntex);
            gl.gl.glColor4f(1, 1, 1, this.transperancy*0.5);
            gl.gl.glTranslate(0,0,0.05);
            gl.gl.glBegin(GL2.GL2.GL_QUADS);
            ti = 0;
            for v in this.shape
            {
            	gl.gl.glTexCoord2d(this.texshape[ti][0], this.texshape[ti][1]);
                gl.gl.glVertex3f(v[0], v[1], this.pos[2]);
                ti += 1;
            }
            gl.gl.glEnd();
            gl.gl.glDisable( GL2.GL2.GL_TEXTURE_2D );
        }
        gl.gl.glDisable( GL2.GL2.GL_ALPHA_TEST );
        
        gl.gl.glPopMatrix();
    }         
    public void shake(force)
    {   
     //   Shake the whole plant, stem rooted in the soil
        
       // pass
    }       
    public void grow()
    {   
     //   Grow the plant bigger to the set maximum
        
        if (this.size < this.maxSize)
        {
        	this.size += 0.001;
            this.pos = (this.pos[0], this.pos[1]+0.004, this.pos[2]);
            this.isGrowing = 5;// # number of frames that it will report growing
        }
        else
            this.canGrow=false; 
    }         
    public void moveToBasket(int id)//****I guess it should be int...have to verify
    {
    	if (id)
            this.targetBasket = this.app.canvas.objects[id];
        else
            this.findTargetBasket();
        if (this.targetBasket)
        {
        	this.interactive = false;
            if (this.basket == this.targetBasket)
                Logger.warning("Flower "  + str(this.id) + " is already in basket " + str(this.targetBasket.id));
            else
            {
            	Logger.trace("info", "moving flower " + str(this.id) + " to basket " + str(this.targetBasket.id));
                this.targetPos = [this.targetBasket.pos[0]+(0.4*random.random()-0.2), this.targetBasket.pos[1]+this.stemLength-this.targetBasket.size/2, this.targetBasket.pos[2]-0.5];
            }
        }
        else
            Logger.warning("Cannot move flower "  + str(this.id) + " to basket, no basket found in scene");
    }       
    public void attachToJoint(jpos, jori, avatarTCB)
    {
    	this.avatarTCB = avatarTCB;
        this.objectCollisionTest = false;
        rotz_r = math.pi - jori[2];
        if (jori[0] < 0)
        {
        	this.rotate[2] =  math.degrees(rotz_r);
            this.pos = [jpos[0]-this.stemLength/2*math.sin(rotz_r), jpos[1]+this.stemLength/2*math.cos(rotz_r), this.pos[2]];
        }
        else
        {
        	this.rotate[2] =  math.degrees(rotz_r) + 180;
            this.pos = [jpos[0]+this.stemLength/2*math.sin(rotz_r), jpos[1]-this.stemLength/2*math.cos(rotz_r), this.pos[2]];
        }
        this.old_jpos = jpos;
    }       
    public void detachFromJoint()
    {
    	this.avatarTCB = null;
        this.objectCollisionTest = true;
        this.pos = [this.old_jpos[0], this.old_jpos[1] + this.stemLength/2, this.old_jpos[2]];
        this.rotate = [0,0,0];
    }   

    public void click(agentName)
    {   
     //   pick
        
        this.app.canvas.agentPublisher.agentActionCompleted('User', 'flower_pick', [str(this.id)]);
//        pass
    } 
    public void startDrag(newXY)
    {
    	this.beingDragged = true;
     //   # Based on http//web.iiit.ac.in/~vkrishna/data/unproj.html
        projection = glGetDoublev(GL2.GL2.GL_PROJECTION_MATRIX);
        modelview = glGetDoublev(GL2.GL2.GL_MODELVIEW_MATRIX);
        viewport = glGetIntegerv(GL2.GL2.GL_VIEWPORT);
        windowZ = glReadPixels(newXY[0], viewport[3]-newXY[1], 1, 1, GL2.GL2.GL_DEPTH_COMPONENT, GL2.GL2.GL_FLOAT);
        worldCoords = gluUnProject(newXY[0], viewport[3] - newXY[1], windowZ[0][0], modelview, projection, viewport);
        this.worldDragOffset = [this.pos[0]-worldCoords[0], this.pos[1]-worldCoords[1], 0];
    } 
    public void stopDrag()
    {
    	this.beingDragged = false;
    }
    
    public void drag(newXY)
    {
    	if (! this.interactive) return;
        //# Based on http//web.iiit.ac.in/~vkrishna/data/unproj.html
        projection = glGetDoublev(GL2.GL2.GL_PROJECTION_MATRIX);
        modelview = glGetDoublev(GL2.GL2.GL_MODELVIEW_MATRIX);
        viewport = glGetIntegerv(GL2.GL2.GL_VIEWPORT);
        windowZ = glReadPixels(newXY[0], viewport[3]-newXY[1], 1, 1, GL2.GL2.GL_DEPTH_COMPONENT, GL2.GL2.GL_FLOAT);
        
        worldCoords = gluUnProject(newXY[0], viewport[3] - newXY[1], windowZ[0][0], modelview, projection, viewport);
           // # started drag outside the flower head
        if (this.worldDragOffset[1] > this.size)
        {
        	//# drag
            this.pos = [worldCoords[0]+this.worldDragOffset[0], worldCoords[1]+this.worldDragOffset[1], this.pos[2]];                
            this.locationChanged = true;
            if (this.avatarTCB)
                this.avatarTCB.detachObject();
            //# started drag in within the flowerhead
        }
        else
        {//        # into Bubble
            if (this.magic && this.childCanTurnIntoBubble && worldCoords[1] > (this.pos[1] + this.size/2))
            {
            	if (this.avatarTCB)
                   this.avatarTCB.detachObject();
                this.intoBubble(true);
                //# into Ball
            }
            else if (this.magic && this.childCanTurnIntoBall && worldCoords[1] < (this.pos[1] - this.size/2))
            {
            	if (this.avatarTCB)
            		this.avatarTCB.detachObject();
                this.intoBall(true);
             //   # swing
            }
            else
            {
            	this.swing = max(min((worldCoords[0] - this.pos[0]) / this.size, 1), -1);
                this.amplitude = Math.fabs(this.swing);
                this.swing = this.swing * Math.pi / 2;// # for max amplitude
            }
    }           
        //intoBubble(byUser=false)
    public void intoBubble(boolean byUser)
    {
    	if (this.canTurnIntoBubble)
    	{
    		bubble = Bubbles.EchoesBubble(this.app, true, fadeIn=true, fadingFrames=10);
    	    bubble.setStartPos(this.pos);
            bubble.size = this.size;
            bubble.willBeReplaced = false;
            if (this.pot)
                this.pot.flower = None;
            if (this.basket)
                this.basket.removeFlower();
            this.remove();
            if (byUser)
                this.app.canvas.agentPublisher.agentActionCompleted('User', 'flower_bubble', [str(this.id), str(bubble.id)]);
            this.app.canvas.rlPublisher.objectPropertyChanged(str(this.id), "flower_bubble", str(bubble.id));
    	}
    }
    //intoBall(byUser=false)
    public void intoBall(boolean byUser)
    {
    	if (this.canTurnIntoBall)
        {
    		ball = PlayObjects.Ball(this.app, true, fadeIn=true, fadingFrames=10);
            ball.pos = this.pos;
            ball.size = this.size;
            ball.colour = this.colour;
            if (this.pot)
                this.pot.flower = None;
            if (this.basket)
                this.basket.removeFlower();
            this.remove();       
            if (byUser)
                this.app.canvas.agentPublisher.agentActionCompleted('User', 'flower_ball', [str(this.id), str(ball.id)]);
            this.app.canvas.rlPublisher.objectPropertyChanged(str(this.id), "flower_ball", str(ball.id));
        }
    }   
    //remove(fadeOut = false, fadingFrames = 100)
    public void remove(boolean fadeOut, int fadingFrames)
    {
    	if (this.avatarTCB)
    		this.detachFromJoint();
        super(EchoesFlower, ).remove(fadeOut, fadingFrames);
    }   
}   