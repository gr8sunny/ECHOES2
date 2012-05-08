import java.util.Map;

//Translation from Py May 8 2012

/*
import environment
import objects.Plants
import PIL.Image
import sound.EchoesAudio
import Bubbles, PlayObjects
import Motions
import Logger
*/
/*Not sure if Cloud extends EchoesObject but seeing the super() call I guessed that should be it
 * 
 */
public class Cloud extends EchoesObject
{   
	private float shape = new float[40][2];
	private float[] pos = {-0.39, 2.25, 0};
	private float size = 0.3;
	private String colour = "white";
	private boolean objectCollisionTest = true;
	private boolean agentCollisionTest = false;
	private boolean canRain = true;
	private boolean raining = false;
	private boolean userRain = false;
	private boolean dimSun = false;
	private float shakeAmplitude = 0;
    private float shake = 0;
    private float [] diffPos = {0,0};
    private float [] prevDiffPos = {0,0};
    private float curDirChangesPF = 0;   // change in directions per 20 frames when dragged
    private float avDirChangesPF = 0;
    private int fcounter = 0;
    
    this.hitBy = None; //Which class?
    private int hitByFCounter = 0;
    private String [] b_colours = {"yellow", "blue", "green"};
    private float b_nextcolour = 0;
    
    this.avatarTCB = None;//Which class?
    private boolean avatarRain = false;
    //props={"type" "Cloud"}
	public Cloud(boolean autoAdd, Map<String, String> properties, boolean fadeIn, int fadingFrames, Object callback)
	{
		super(autoAdd, properties, fadeIn, fadingFrames, callback);

		// Create the shape
		shape[0][0]= 0;
		shape[0][1] = -1;
		int pointIndex = 1;
		for (float deg = -90; deg < 135; deg+=10)
		{
			shape[pointIndex][0] = (float)(2.5+Math.cos(Math.toRadians(deg)));
			shape[pointIndex][1] = (float)Math.sin(Math.toRadians(deg);
			pointIndex++;
		}
		//this.shape += [(1.8*math.cos(math.radians(deg)), 1+math.sin(math.radians(deg))) for deg in xrange(-45, 225, 10)]
		for(float deg=-45; deg < 225; deg+=10)
		{
			shape[pointIndex][0]=(float)(1.8*Math.cos(Math.toRadians(deg)));
			shape[pointIndex][1] = (float)(1+Math.sin(Math.toRadians(deg));
			pointIndex++;
		}
		//this.shape += [(math.cos(math.radians(deg))-2.5, math.sin(math.radians(deg))) for deg in xrange(45, 270, 10)]
		for(float deg=45; deg < 270; deg+=10)
		{
			shape[pointIndex][0]=(float)(Math.cos(Math.toRadians(deg))-2.5);
			shape[pointIndex][1] = (float)(Math.sin(Math.toRadians(deg));
			pointIndex++;
		}
		//this.shape += [(0,-1)]
		shape[pointIndex][0]= 0;
		shape[pointIndex][1] = -1;
        
        this.shakeAmplitude = 0;
        this.shake = 0;

        this.diffPos = (0,0);
        this.prevDiffPos = (0,0);
        this.curDirChangesPF = 0;   // change in directions per 20 frames when dragged
        this.avDirChangesPF = 0;
        this.fcounter = 0;
        
        this.hitBy = None
        this.hitByFCounter = 0
        this.b_colours = ["yellow", "blue", "green"]
        this.b_nextcolour = 0
        
        this.avatarTCB = None
        this.avatarRain = false
        
        this.setImage()
        
        if (sound.EchoesAudio.soundPresent
            this.rainSound = sound.EchoesAudio.playSound("rain.wav", true, 0.0)
        else
            this.rainSound = None
	}
        
    //public void __setattr__(item, value)
    public void setAttr(String item, String value)
    {
        if (item == "pos" && hasattr("pos")) // make sure this is only done when the object is fully built
        {
        	this.objectsUnderCloud(); // notify the objects that come under the cloud
        }
        
        if (item == "hitBy" && hasattr("hitBy"))
        {    if (value != this.hitBy && value != None)
             {   
        		this.app.canvas.rlPublisher.objectPropertyChanged(str(this.id), "cloud_hitby", str(value.id));
                this.hitByFCounter = 30;
                if (value.thrownByAvatar==false)
                {
                	//this.app.canvas.agentPublisher.agentActionCompleted("User", "cloud_ball", [str(this.id), str(value.id)]);
                	/* Here [str(this.id), str(value.id)] is a list with two string elements
                	 * So I am trying to declare a String array of two elements
                	 * Lets see if this works
                	 */
                	String valueParams[] = {str(this.id), str(value.id)};
                	this.app.canvas.agentPublisher.agentActionCompleted("User", "cloud_ball", valueParams);  
                }
             }
        }
        if (item == "colour")
        {   
        	/*
        	 * Here also there were many lists
        	 * so I have declared an integer array called intArray
        	 */
        	int intArray[]=new intArray[3];
           	if (value == "red")
        	{ 
        		intArray[0]=0.735;
        		intArray[1]=0.197;
        		intArray[2]=0.286;
        	}
            else if (value == "green")
            {
            	intArray[0]=0.439;
        		intArray[1]=0.633;
        		intArray[2]=0.245;
            }
            else if (value == "blue")
            {
            	intArray[0]=0.220;
        		intArray[1]=0.481;
        		intArray[2]=0.628;
            }
            else if (value == "yellow")
            {
            	intArray[0]=0.921;
        		intArray[1]=0.832;
        		intArray[2]=0.217;
            }
            else 
            {
            	intArray[0]=0.96;
        		intArray[1]=0.95;
        		intArray[2]=1;
            }
        	this.cv = intArray;
        }
        setAttr(item, value);
    }
    public void setImage(file='visual/images/Rain-drop.png')
    {
    	im = PIL.Image.open(file); // .jpg, .bmp, etc. also work
        /*
         * Whats the point of doing a try, catch here? Same code in both
         */
        try
            ix, iy, image = im.size[0], im.size[1], im.tostring("raw", "RGBA", 0, -1)
        except SystemError
            ix, iy, image = im.size[0], im.size[1], im.tostring("raw", "RGBX", 0, -1)        

        this.dropTexture = glGenTextures(1);
        gl.glPixelStorei(GL2.GL_UNPACK_ALIGNMENT,1);
        gl.glBindTexture(GL2.GL_TEXTURE_2D, this.dropTexture);
        gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, 4, ix, iy, 0, GL2.GL_RGBA, GL2.GL_UNSIGNED_BYTE, image);
        this.dropSize = (ix, iy);
    }           
    public void renderObj()
    {    
      	gl.glPushMatrix();
        gl.glDisable(GL2.GL_DEPTH_TEST);
        this.fcounter += 1;
        if (this.fcounter > 40)
        { 
        	this.avDirChangesPF = this.curDirChangesPF;
            this.curDirChangesPF = 0;
            this.fcounter = 0;
        }
        if (this.canRain && this.avDirChangesPF > 2)
        {
        	if (! this.raining)
            {
        		this.app.canvas.rlPublisher.objectPropertyChanged(str(this.id), "cloud_rain", "true");
                this.raining = true;
                if (this.rainSound)
                {
                	this.rainSound.set("mul", 0.8, 0.05);
                }
                if (! this.avatarRain)
                {
                	//Don't know what to do with this
                	this.app.canvas.agentPublisher.agentActionStarted('User', 'cloud_rain', [str(this.id)]);
            	}      
             } 
            for i in xrange(20)
            {
            	this.drawRainDrop((this.pos[0] + (random.random()-0.5) * this.size *2.5, this.pos[1] - random.random() * 5,0), random.random() * 0.3);
            }
            
            foundObject = false;
            for i, object in this.app.canvas.objects.items()
            {    
            	if (this.isUnder(object))
            	{
                    if (isinstance(object, Pond) && object.canGrow) 
                    {
                    	object.grow();
                        foundObject = true;
                        break;
                    }
                    else if (isinstance(object, objects.Plants.Pot))
                    {   
                    	if (((! object.flower or object.flower.canGrow) && (! object.stack or object.stack.top() == object))
                    	{
                    		object.growFlower();
                    	    foundObject = true;
                            break;
                    	}
                    }
                    else if (isinstance(object, objects.Environment.Basket))
                    {
                    	object.growFlowers();
                        foundObject = true;
                        break;
                    }
                    else if (isinstance(object, objects.Plants.EchoesFlower) and object.canGrow)
                    {
                    	object.grow();
                        foundObject = true;
                        break;
                    }
            	}
            }
            if (! foundObject)
            {
            	flower = objects.Plants.EchoesFlower(this.app, true, fadeIn=true);
                flower.size = 0.1;
                flower.pos = [this.pos[0], this.app.canvas.getRegionCoords("ground")[1][1], this.pos[2]];
                this.app.canvas.rlPublisher.objectPropertyChanged(str(this.id), "cloud_flower", str(flower.id));
                if (! this.avatarRain)
                {
                	bs = None;
                    for id, object in this.app.canvas.sceneElements.items()
                    {
                    	if (isinstance(object, environment.HelperElements.Score))
                    	{
                    		bs = object;
                    	}
                    }
                    if (bs)
                    {
                    	bs.increment();
                        if (this.app.canvas.publishScore)
                        {
                        	this.app.canvas.rlPublisher.worldPropertyChanged("FlowerScore", str(bs.score));
                        }
                    }
                }
            }

                
        else
        {
        	if (this.raining)
            {  
        		if (this.rainSound)
                {
        			this.rainSound.set("mul", 0.0, 0.1);
                }
                if (! this.avatarRain)
                {
                	this.app.canvas.agentPublisher.agentActionCompleted("User", "cloud_rain", [str(this.id)]);
                }
                this.app.canvas.rlPublisher.objectPropertyChanged(str(this.id), "cloud_rain", "false");
                this.raining = false;
                this.avatarRain = false;
            }
        }
        if (this.shakeAmplitude > 0)
        {
        	shakeX = math.sin(this.shake) * this.shakeAmplitude;
            this.shakeAmplitude -= 0.01;
            this.shake = (this.shake + 0.7) % (2*math.pi);
        }
        else 
        {
        	shakeX = 0;
        }
               
        if (this.colour != "white")
        {
        	for i in range(3)
            {
        		this.cv[i] = min(1, this.cv[i]+0.005);
            }
            if (this.cv[0] == this.cv[1] == this.cv[2] == 1)
            {
            	this.colour = "white";
            }
         }
        // reset hits in 30 frames
        if (this.hitBy and this.hitByFCounter > 0
        {    
        	this.hitByFCounter -= 1;
            if (this.hitByFCounter <= 0)
            {
            	this.hitBy = None;
            }
        }
        gl.glTranslate(this.pos[0] + shakeX, this.pos[1], this.pos[2]);
        gl.glScale(this.size, this.size, this.size);
        gl.glColor4f(this.cv[0], this.cv[1], this.cv[2], this.transperancy);
        gl.glBegin(GL2.GL_TRIANGLE_FAN);
        gl.glVertex2f(0,0);
        for v in this.shape
        {
        	gl.glVertex3f(v[0], v[1], this.pos[2]);
        }
        gl.glEnd();
        gl.glLineWidth(3.0);
        gl.glColor4f(0.385, 0.691, 1.0, this.transperancy);
        gl.glBegin(GL2.GL_LINE_STRIP);
        for v in this.shape
        {
        	gl.glVertex3f(v[0], v[1], this.pos[2]+0.1);
        }
        gl.glEnd();
        gl.glLineWidth(1.0);

        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glPopMatrix();
        }
    }
    public void rain(frames=40)
    {
    	this.shakeAmplitude = 0.3
        this.fcounter = -1*(frames-40)
        this.avDirChangesPF = 3
        this.avatarRain = true
    }
    public void drawRainDrop(pos, size=1)
    {
        gl.glPushMatrix();
        gl.glBlendFunc(GL2.GL_ONE, GL2.GL_ONE);        
        gl.glEnable( GL2.GL_TEXTURE_2D );
        gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
        gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
        gl.glBindTexture(GL2.GL_TEXTURE_2D, this.dropTexture);
        gl.glTranslate(pos[0], pos[1], pos[2]);
        gl.glScalef(size, size, size);
        gl.glColor4f(1, 1, 1, this.transperancy);
        dropRatio = this.dropSize[1]/this.dropSize[0];
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2d(0.0,0.0);
        gl.glVertex2d(0,0);
        gl.glTexCoord2d(1.0,0.0);
        gl.glVertex2d(1,0);
        gl.glTexCoord2d(1.0,1.0);
        gl.glVertex2d(1,dropRatio);
        gl.glTexCoord2d(0.0,1.0);
        gl.glVertex2d(0,dropRatio);
        gl.glEnd();
        gl.glDisable( GL2.GL_TEXTURE_2D );        
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
        gl.glPopMatrix();
    }

    public void startDrag(pos)
    {    
    	if (this.interactive)
        {    
        	this.beingDragged = true;
            this.locationChanged = false;
            this.dragStartWorld = this.pos;
        }
    }   
    public void stopDrag()
    {
    	if (this.interactive)
    	{
    		this.beingDragged = false;
    	    this.locationChanged = false;
    	}
    }
    
    public void drag(newXY)
    {    if (this.interactive)
            // Based on http//web.iiit.ac.in/~vkrishna/data/unproj.html
    		/*
    		 * Wow...this is a reference from my College :)
    		 */
            projection = glGetDoublev(GL2.GL_PROJECTION_MATRIX);
            modelview = glGetDoublev(GL2.GL_MODELVIEW_MATRIX);
            viewport = glGetIntegerv(GL2.GL_VIEWPORT);
            windowZ = glReadPixels(newXY[0], viewport[3]-newXY[1], 1, 1, GL2.GL_DEPTH_COMPONENT, GL2.GL_FLOAT);
            
            worldCoords = gluUnProject(newXY[0], viewport[3] - newXY[1], windowZ[0][0], modelview, projection, viewport);

            this.prevDiffPos = this.diffPos;
            this.diffPos = (worldCoords[0]-this.pos[0], worldCoords[1]-this.pos[1]);
            // check if any of the directional changes has changed sign
            if ((this.prevDiffPos[0]*this.diffPos[0]) < 0 || (this.prevDiffPos[1]*this.diffPos[1]) < 0)
            {
            	this.curDirChangesPF +=1;
            }
            
            sky = this.app.canvas.getRegionCoords("sky");
            this.pos = (worldCoords[0], max(worldCoords[1], sky[1][1]), this.pos[2]);      
                                
            this.locationChanged = true;
        
            if (this.dimSun)
            {
            	for id,object in this.app.canvas.objects.items()
              	{ 
                	if (isinstance(object, Sun)
            	   	{
                		distance = (math.hypot(this.pos[0]-object.pos[0], this.pos[1]-object.pos[1]));
            	   	    this.app.canvas.setLight(min(0.8,distance/2.0));
                        break;
            	   	}
            	}
            }
    
    public void objectsUnderCloud()
    {
    	for oid, object in this.app.canvas.objects.items()
    	{
    		if (hasattr(object, "underCloud")
    		{
    			if (this.isUnder(object)
    			{
    				if (!object.underCloud)
    				{
    					object.underCloud = true;
    				}
    			}
    			else
                {
    			  	if (object.underCloud)
    			   	{
    			   		object.underCloud = false;
    			   	}
                }
    		}
    	}
    }
                    
    public void isUnder(object)
    {    if (object.pos[0] > (this.pos[0] - 3.5*this.size) && object.pos[0] < (this.pos[0] + 3.5*this.size))
    	{
    		return true;            
    	}
        else
        {
        	return false;
        }
    }
        
    public void attachToAvatar(apos, aori, avatarTCB=None)
    {  
    	if (! this.avatarTCB)
      	{ 
    		this.avatarTCB = avatarTCB;
      	    this.xoffset = abs(this.pos[0] - apos[0]);
      	}
        xoff = this.xoffset * math.sin(-aori[2]);
        this.pos = [apos[0]+xoff, this.pos[1], this.pos[2]];
    }       
    public void detachFromAvatar()
    {
    	this.avatarTCB = None;
    }
        
    
public class Pond
    
    public classdocs
    
    public void __init__(autoAdd=true, props={"type" "Pond"}, fadeIn = false, fadingFrames = 100, callback = None)
        
        
        
        super(Pond, ).__init__(app, autoAdd, props, fadeIn, fadingFrames, callback)
        this.pos = (0, -2.5, 0)
        this.size = 0.2
        this.maxSize = 1
        this.maxSize = 0.1
        this.shape = [(math.cos(math.radians(deg)), math.sin(math.radians(deg))) for deg in xrange(-180, 1, 10)]
        this.shape += [(1+ 0.5*math.cos(math.radians(deg)), 0.5+0.5*math.sin(math.radians(deg))) for deg in xrange(-90, 91, 10)]
        this.shape += [(1+ 0.2*math.cos(math.radians(deg)), 1.2+0.2*math.sin(math.radians(deg))) for deg in xrange(-90, 91, 10)]
        this.shape += [(math.cos(math.radians(deg)), 1.4+0.5*math.sin(math.radians(deg))) for deg in xrange(0, 181, 10)]
        this.shape += [(-1+ 0.2*math.cos(math.radians(deg)), 1.2+0.2*math.sin(math.radians(deg))) for deg in xrange(90, 271, 10)]
        this.shape += [(-1+ 0.5*math.cos(math.radians(deg)), 0.5+0.5*math.sin(math.radians(deg))) for deg in xrange(90, 271, 10)]
        
        this.canGrow = true
        this.canShrink = true
              
    public void renderObj()
        
        gl.glPushMatrix()
        gl.glDisable(GL2.GL_DEPTH_TEST)

        gl.glTranslate(this.pos[0],this.pos[1],this.pos[2])
        gl.glScale(this.size, this.size, this.size)
        
        gl.glRotate(70.0,1.0,0.0,0.0)
        gl.glRotate(35.0,0.0,0.0,-1.0)
        gl.glColor4f(0.576, 0.918, 1.0, this.transperancy)
        gl.glBegin(GL2.GL_LINE_STRIP)
        for v in this.shape
            gl.glVertex2f(v[0], v[1])
        gl.glEnd()
        gl.glVertex2f(this.shape[0][0], this.shape[0][1])
        gl.glColor4f(0.376, 0.718, 1.0, this.transperancy)
        gl.glBegin(GL2.GL_TRIANGLE_FAN)
        gl.glVertex2f(0,0)
        for v in this.shape
            gl.glVertex2f(v[0], v[1])
        gl.glEnd()

        
        gl.glEnable(GL2.GL_DEPTH_TEST)
        gl.glPopMatrix()        
        
    public void grow()
        if (this.size < this.maxSize
            this.size += 0.005
            this.app.canvas.rlPublisher.objectPropertyChanged(str(this.id), "pond_grow", str(this.size))
        else
            this.canGrow = false
            this.canShrink = true

    public void shrink()
        if (this.size > this.minSize
            this.size -= 0.005
            this.app.canvas.rlPublisher.objectPropertyChanged(str(this.id), "pond_shrink", str(this.size))
        else 
            this.canGrow = true
            this.canShrink = false
        
               
public class Basket
    
    public classdocs
    
    public void __init__(autoAdd=true, props={"type" "Basket"}, fadeIn = false, fadingFrames = 100, callback = None)
        
        
        
        super(Basket, ).__init__(app, autoAdd, props, fadeIn, fadingFrames, callback)
       
        this.size = 0.6
        this.pos = (0,0,0)   
        this.publishRegion = true
        
        this.canBeDraged = true        
        this.public voidaultHeight = this.app.canvas.getRegionCoords("ground")[0][1]
        this.fallTopublic voidaultHeight = true
        this.falling = false
        this.avatarTCB = None
        
        this.stack = None
        this.flowers = []
        this.numflowers = 0

        this.player = None

        this.textures = []
        this.sizes = []
        this.shapes = []
        this.texshape = [(0, 0), (1, 0), (1, 1), (0, 1)]        
        this.setImage('visual/images/basket-top.png')
        this.setImage('visual/images/basket-bottom.png')
        oy = (this.sizes[0][1] + this.sizes[1][1]) * 0.96
        w = this.sizes[0][0] / oy
        h = 1.0-2.0*this.sizes[0][1]/oy
        this.shapes.append([(-w, h), (w, h), (w, 1), (-w, 1)])
        h = -1.0+2.0*this.sizes[1][1]/oy
        this.shapes.append([(-w, -1), (w, -1), (w, h), (-w, h)])

    public void __setattr__(item, value)
        if (item == "pos" and hasattr("flowers")
            for f in this.flowers
                f.pos = [value[0], value[1]+f.stemLength-this.size/2, value[2]]
            if (hasattr("stack") and this.stack and ((hasattr("beingDragged") and this.beingDragged) or (hasattr("avatarTCB") and this.avatarTCB))
                # if (the user did it, notify the rest of the system
                split = this.stack.split()
                if (split and hasattr("beingDragged") and this.beingDragged
                    this.app.canvas.agentPublisher.agentActionCompleted('User', 'unstack_basket', [str(this.id)])

        else if (item == "stack"
            if (value == None
                this.app.canvas.rlPublisher.objectPropertyChanged(str(this.id), "basket_stack", "false")
            else
                this.app.canvas.rlPublisher.objectPropertyChanged(str(this.id), "basket_stack", "true")

    
        object.__setattr__(item, value)

    public void setImage(file)
        im = PIL.Image.open(file) # .jpg, .bmp, etc. also work
        try
            ix, iy, image = im.size[0], im.size[1], im.tostring("raw", "RGBA", 0, -1)
        except SystemError
            ix, iy, image = im.size[0], im.size[1], im.tostring("raw", "RGBX", 0, -1)        

        tex = glGenTextures(1)
        gl.glPixelStorei(GL2.GL_UNPACK_ALIGNMENT,1)
        gl.glBindTexture(GL2.GL_TEXTURE_2D, tex)
        gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, 4, ix, iy, 0, GL2.GL_RGBA, GL2.GL_UNSIGNED_BYTE, image)
        this.textures.append(tex)
        this.sizes.append([ix,iy])        
                       
    public void renderObj()
        if (not hasattr("shapes") return
        
        if (this.numflowers != len(this.flowers)
            this.numflowers = len(this.flowers)
            this.app.canvas.rlPublisher.objectPropertyChanged(str(this.id), "basket_numflowers", str(this.numflowers))
            
        if (this.fallTopublic voidaultHeight and not this.beingDragged and not this.avatarTCB
            hdiff = this.pos[1] - this.public voidaultHeight
            if (abs(hdiff) > 0.05
                if (not this.stack # no stack
                    this.pos = [this.pos[0], this.pos[1]-hdiff/10, this.pos[2]]
                    this.falling = true
                else
                    this.falling = false
            else
                this.falling = false
                        
        gl.glPushMatrix()
        gl.glTranslate(this.pos[0], this.pos[1], this.pos[2])
        gl.glScalef(this.size, this.size, this.size)
        i = 0
        for texture in this.textures 
            gl.glEnable( GL2.GL_ALPHA_TEST )
            gl.glAlphaFunc( GL2.GL_GREATER, 0.1 )        
            gl.glEnable( GL2.GL_TEXTURE_2D )
            gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST)
            gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST)
            gl.glBindTexture(GL2.GL_TEXTURE_2D, texture)
            gl.glColor4f(1, 1, 1, this.transperancy)
            gl.glBegin(GL2.GL_QUADS)
            ti = 0
            for v in this.shapes[i]
                gl.glTexCoord2d(this.texshape[ti][0], this.texshape[ti][1])
                gl.glVertex3f(v[0], v[1], -0.1 + i*0.2)
                ti += 1
            gl.glEnd()
            gl.glDisable( GL2.GL_TEXTURE_2D )
            gl.glDisable( GL2.GL_ALPHA_TEST )
            i += 1
        gl.glPopMatrix()
               
    public void addFlower(flower)
        this.flowers.append(flower)
        flower.basket = 
        flower.pos = [this.pos[0], this.pos[1]+flower.stemLength-this.size/2, this.pos[2]]
        flower.inCollision = this.id
        this.app.canvas.rlPublisher.objectPropertyChanged(str(this.id), "basket_flower", str(flower.id))
        if (flower.beingDragged 
            this.app.canvas.agentPublisher.agentActionCompleted('User', 'flower_placeInBasket', [str(this.id), str(flower.id)])

    public void growFlowers()
        if (len(this.flowers) == 0
            flower = objects.Plants.EchoesFlower(this.app)
            flower.size = 0.1
            this.addFlower(flower)
        for f in this.flowers
            f.grow()
        
    public void removeFlower(flower)
        try 
            i = this.flowers.index(flower)
            del this.flowers[i]
        except ValueError
            Logger.warning("Basket trying to remove flower that is not in the basket, id=" + str(flower.id)) 
        if (len(this.flowers) == 0
            this.app.canvas.rlPublisher.objectPropertyChanged(str(this.id), "basket_flower", "None")

    public void playFanfare()
        if (sound.EchoesAudio.soundPresent
            if (not this.player 
                fanfar = "fanfar" + str(random.randint(1,3)) + ".wav"
                this.player = sound.EchoesAudio.playSound(fanfar, vol=0.3)
                sound.EchoesAudio.SoundCallback(fanfar, this.resetPlayer).start()
            else
                Logger.warning("Basket already plays the fanfare, not triggering new one.")

    public void resetPlayer()
        this.player = None
                       
    public void startDrag(newXY)
        if (this.avatarTCB
            this.avatarTCB.detachObject()
        this.beingDragged = true
        # Based on http//web.iiit.ac.in/~vkrishna/data/unproj.html
        projection = glGetDoublev(GL2.GL_PROJECTION_MATRIX)
        modelview = glGetDoublev(GL2.GL_MODELVIEW_MATRIX)
        viewport = glGetIntegerv(GL2.GL_VIEWPORT)
        windowZ = glReadPixels(newXY[0], viewport[3]-newXY[1], 1, 1, GL2.GL_DEPTH_COMPONENT, GL2.GL_FLOAT)
        worldCoords = gluUnProject(newXY[0], viewport[3] - newXY[1], windowZ[0][0], modelview, projection, viewport)
        this.worldDragOffset = [this.pos[0]-worldCoords[0], this.pos[1]-worldCoords[1], 0] 
        
    public void stopDrag()
        this.beingDragged = false

    public void drag(newXY)
        if (this.interactive and this.canBeDraged
            # Based on http//web.iiit.ac.in/~vkrishna/data/unproj.html
            projection = glGetDoublev(GL2.GL_PROJECTION_MATRIX)
            modelview = glGetDoublev(GL2.GL_MODELVIEW_MATRIX)
            viewport = glGetIntegerv(GL2.GL_VIEWPORT)
            windowZ = glReadPixels(newXY[0], viewport[3]-newXY[1], 1, 1, GL2.GL_DEPTH_COMPONENT, GL2.GL_FLOAT)
            
            worldCoords = gluUnProject(newXY[0], viewport[3] - newXY[1], windowZ[0][0], modelview, projection, viewport)
            if (this.beingDragged
                if (this.fallTopublic voidaultHeight
                    this.pos = [worldCoords[0]+this.worldDragOffset[0], max(this.public voidaultHeight, worldCoords[1]+this.worldDragOffset[1]), this.pos[2]]
                else
                    this.pos = [worldCoords[0]+this.worldDragOffset[0], worldCoords[1]+this.worldDragOffset[1], this.pos[2]]                
                this.locationChanged = true
               
    public void attachToJoint(jpos, jori, avatarTCB)
        this.avatarTCB = avatarTCB
        this.objectCollisionTest = false        
        if (this.fallTopublic voidaultHeight
            y = max(jpos[1]+this.size/3, this.public voidaultHeight)
        else
            y = jpos[1]
        this.pos = [jpos[0], y, this.pos[2]]
            
    public void detachFromJoint()
        this.avatarTCB = None
        this.objectCollisionTest = true        
        
    public void remove(fadeOut = false, fadingFrames = 100)
        if (not fadeOut and this.stack and  in this.stack.pots
            this.objectCollisionTest = false
            del this.stack.pots[this.stack.pots.index()]
            this.stack = None
        super(Basket, ).remove(fadeOut, fadingFrames)
        

public class Container
    
    public classdocs
    
    public void __init__(autoAdd=true, props={"type" "Container"}, fadeIn = false, fadingFrames = 100, callback = None)
        
        
        
        super(Container, ).__init__(app, autoAdd, props, fadeIn, fadingFrames, callback)
       
        this.size = 0.6
        this.pos = [0,this.app.canvas.getRegionCoords("ground")[1][1],0]   
        
        this.colours = { "red" (0.735, 0.197, 0.286), "yellow" (0.921,0.832,0.217), "blue"(0.220,0.481,0.628), "green"(0.439,0.633,0.245)}
        this.colour = "red"
        this.shape = [[-1,1,0], [-1,-1,0],[1,-1,0],[1,1,0]]

        this.publishRegion = true
        this.canBeDraged = false        
        
        this.balls = []
        
    public void __setattr__(item, value)
        if (item == "colour"
            if (not value in this.colours value = "red" 
            this.app.canvas.rlPublisher.objectPropertyChanged(str(this.id), "container_colour", str(value))      
    
        object.__setattr__(item, value)
                       
    public void renderObj()
        gl.glPushMatrix()
        gl.glDisable(GL2.GL_DEPTH_TEST)
        gl.glTranslate(this.pos[0], this.pos[1], this.pos[2])
        gl.glScalef(this.size, this.size, this.size)
        c = this.colours[this.colour]
        gl.glColor4f(c[0], c[1], c[2], this.transperancy*0.4)
        gl.glBegin(GL2.GL_QUADS)
        for v in this.shape
            gl.glVertex3f(v[0],v[1],v[2])
        gl.glEnd()
        gl.glColor4f(c[0], c[1], c[2], this.transperancy)
        gl.glBegin(GL2.GL_QUADS)
        gl.glVertex3f(this.shape[0][0]*1.1, this.shape[0][1], 0)
        gl.glVertex3f(this.shape[0][0]*0.9, this.shape[0][1], 0)
        gl.glVertex3f(this.shape[1][0]*0.9, this.shape[1][1], 0)
        gl.glVertex3f(this.shape[1][0]*1.1, this.shape[1][1], 0)
        gl.glEnd()
        gl.glBegin(GL2.GL_QUADS)
        gl.glVertex3f(this.shape[1][0]*1.1, this.shape[1][1]*0.9, 0)
        gl.glVertex3f(this.shape[1][0]*1.1, this.shape[1][1]*1.1, 0)
        gl.glVertex3f(this.shape[2][0]*1.1, this.shape[2][1]*1.1, 0)
        gl.glVertex3f(this.shape[2][0]*1.1, this.shape[2][1]*0.9, 0)
        gl.glEnd()
        gl.glBegin(GL2.GL_QUADS)
        gl.glVertex3f(this.shape[3][0]*1.1, this.shape[3][1], 0)
        gl.glVertex3f(this.shape[3][0]*0.9, this.shape[3][1], 0)
        gl.glVertex3f(this.shape[2][0]*0.9, this.shape[2][1], 0)
        gl.glVertex3f(this.shape[2][0]*1.1, this.shape[2][1], 0)
        gl.glEnd()
        gl.glEnable(GL2.GL_DEPTH_TEST)        
        gl.glPopMatrix()
                              
    public void startDrag(newXY)
        this.beingDragged = true
        # Based on http//web.iiit.ac.in/~vkrishna/data/unproj.html
        projection = glGetDoublev(GL2.GL_PROJECTION_MATRIX)
        modelview = glGetDoublev(GL2.GL_MODELVIEW_MATRIX)
        viewport = glGetIntegerv(GL2.GL_VIEWPORT)
        windowZ = glReadPixels(newXY[0], viewport[3]-newXY[1], 1, 1, GL2.GL_DEPTH_COMPONENT, GL2.GL_FLOAT)
        worldCoords = gluUnProject(newXY[0], viewport[3] - newXY[1], windowZ[0][0], modelview, projection, viewport)
        this.worldDragOffset = [this.pos[0]-worldCoords[0], this.pos[1]-worldCoords[1], 0] 
        
    public void stopDrag()
        this.beingDragged = false

    public void drag(newXY)
        if (this.interactive and this.canBeDraged
            # Based on http//web.iiit.ac.in/~vkrishna/data/unproj.html
            projection = glGetDoublev(GL2.GL_PROJECTION_MATRIX)
            modelview = glGetDoublev(GL2.GL_MODELVIEW_MATRIX)
            viewport = glGetIntegerv(GL2.GL_VIEWPORT)
            windowZ = glReadPixels(newXY[0], viewport[3]-newXY[1], 1, 1, GL2.GL_DEPTH_COMPONENT, GL2.GL_FLOAT)
            
            worldCoords = gluUnProject(newXY[0], viewport[3] - newXY[1], windowZ[0][0], modelview, projection, viewport)
            if (this.beingDragged
                if (this.fallTopublic voidaultHeight
                    this.pos = [worldCoords[0]+this.worldDragOffset[0], max(this.public voidaultHeight, worldCoords[1]+this.worldDragOffset[1]), this.pos[2]]
                else
                    this.pos = [worldCoords[0]+this.worldDragOffset[0], worldCoords[1]+this.worldDragOffset[1], this.pos[2]]                
                this.locationChanged = true
                
    public void addBall(ball)
        this.balls.append(ball)
        ball.container = 
        ball.floor = this.pos[1]-this.size
        ball.left = this.pos[0]-this.size
        ball.right = this.pos[0]+this.size
        ball.bounceWithinScene = true
        ball.size = max(this.size/2.5, ball.size/3)
        ball.stopDrag()
        ball.pos = [ball.pos[0], ball.pos[1], -10]
        ball.interactive = false
        ball.publishBounce = false
        
                       
    public void reward(type="Bubbles")
        num_balls = 0
        if (type == "Bubbles"
            for b in this.balls
                bubble = Bubbles.EchoesBubble(this.app, true, fadeIn=true, fadingFrames=10)
                bubble.setStartPos(b.pos)
                bubble.willBeReplaced = false 
                bubble.canMerge = false           
                b.remove(false)
                num_balls += 1
            this.remove(true)               
        else if (type == "Fireworks"
            for b in this.balls
                b.explode(inFrames=random.randint(0,100))
                num_balls += 1
            this.remove(true)
        else if (type == "Bees"
            for b in this.balls
                bee = Bee(this.app, true, fadeIn=true, fadingFrames=10)
                bee.setStartPos(b.pos)
                bee.setTargetPos([random.randint(-5,5), 5])
                bee.removeAtTargetPos = true
                b.remove(false)
                num_balls += 1
            this.remove(true)
        this.app.canvas.rlPublisher.objectPropertyChanged(str(this.id), "container_reward", str(num_balls))
        
    public void remove(fadeOut = false, fadingFrames = 100)
        super(Container, ).remove(fadeOut, fadingFrames)
               
               
        
        
public class Sun
    
    public classdocs
    
    public void __init__(autoAdd=true, props={"type" "Sun"}, fadeIn = false, fadingFrames = 100, callback=None)
        
        
        
        super(Sun, ).__init__(app, autoAdd, props, fadeIn, fadingFrames, callback)
        this.pos = (3, 2.25, -0.5)
        this.size = 0.5
        this.shape = [(math.cos(math.radians(deg)), math.sin(math.radians(deg))) for deg in xrange(0, 370, 10)]
        
        this.objectCollisionTest = false
        this.agentCollisionTest = false
        
               
    public void renderObj()
        
        gl.glPushMatrix()
        gl.glDisable(GL2.GL_DEPTH_TEST)

        gl.glTranslate(this.pos[0],this.pos[1],this.pos[2])
        gl.glColor4f(1,1,0, this.transperancy)
        gl.glBegin(GL2.GL_TRIANGLE_FAN)
        gl.glVertex2f(0,0)
        gl.glColor4f(0.741, 0.878, 0.929, 0)
        for v in this.shape
            gl.glVertex2f(v[0], v[1])
        gl.glEnd()

        gl.glEnable(GL2.GL_DEPTH_TEST)
        gl.glPopMatrix()        
        
public class Shed
    
    public classdocs
    
    public void __init__(autoAdd=true, props={"type" "Shed"}, fadeIn = false, fadingFrames = 100, callback=None)
        
        
        
        super(Shed, ).__init__(app, autoAdd, props, fadeIn, fadingFrames, callback)
        this.pos = (-2, -0.5, -3)
        this.size = 2.5
        
        this.objectCollisionTest = false
        this.agentCollisionTest = false
        
        this.texture = this.setImage('visual/images/Shed.png')
        this.shape = [(-0.5, -0.5), (0.5, -0.5), (0.5, 0.5), (-0.5, 0.5)]
        this.texshape = [(0, 0), (1, 0), (1, 1), (0, 1)]

    public void __setattr__(item, value)
        if (item == "pos"
            pass
                        
        object.__setattr__(item, value)
            
    public void renderObj()
         
        overwriting the render method to draw the flower
                    
        gl.glPushMatrix()
        gl.glEnable( GL2.GL_ALPHA_TEST )
        gl.glAlphaFunc( GL2.GL_GREATER, 0.1 )
        
        gl.glEnable( GL2.GL_TEXTURE_2D )
        gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST)
        gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST)
        gl.glBindTexture(GL2.GL_TEXTURE_2D, this.texture)
        
        gl.glTranslate(this.pos[0], this.pos[1], this.pos[2])  
        gl.glScalef(this.size, this.size, this.size)
        gl.glColor4f(1, 1, 1, this.transperancy)
        gl.glBegin(GL2.GL_QUADS)
        ti = 0
        for v in this.shape
            gl.glTexCoord2d(this.texshape[ti][0], this.texshape[ti][1])
            gl.glVertex3f(v[0], v[1], this.pos[2])
            ti += 1
        gl.glEnd()
        gl.glDisable( GL2.GL_TEXTURE_2D )
        gl.glDisable( GL2.GL_ALPHA_TEST )
        gl.glPopMatrix()
        
        
public class Bee(EchoesObject, Motions.BezierMotion)
    
    public classdocs
    

    public void __init__(autoAdd=true, props = {"type" "Bee"}, fadeIn = false, fadingFrames = 100, randomSize = true, callback=None)
        
        
        
        super(Bee, ).__init__(app, autoAdd, props, fadeIn, fadingFrames, callback)
        super(Bee, ).initBezierVars()
        
        if (randomSize
            this.size = 0.15 + random.random() * 0.1
        else
            this.size = 0.3 
        this.maxSize = 1.5
        this.speed = 0.002
        this.moving = true
        this.floatingXY = true
        this.floatingSound = false
        this.canBeClicked = true
        this.canBeDraged = true

        this.texture = this.setImage('visual/images/bee.png')        
            
        this.shape = [(-1, -1), (1, -1), (1, 1), (-1, 1)]
        this.texshape = [(1, 0), (1, 1), (0, 1), (0, 0)]
        
        this.newstartpos()
        this.newctrlpoints()
        
        if (sound.EchoesAudio.soundPresent
            this.buzz = sound.EchoesAudio.playSound("buzz.wav", loop=true, vol=0.0)
        else
            this.buzz = None
                    
    public void __setattr__(item, value)
                            
        object.__setattr__(item, value)
                             
    public void renderObj()
         
        overwriting the render method to draw the bubble
        
        oldpos = this.pos
        if (this.moving and not this.beingDragged
            this.pos = this.nextBezierPos(this.floatingXY)
            this.orientation = (this.pos[0]-oldpos[0], this.pos[1]-oldpos[1], this.pos[2]-oldpos[2])              
            if (this.removeAtTargetPos and this.bezierIndex > 0.95
                this.remove(true)
        
        if (this.buzz and not this.fadingOut
            vel = math.hypot(this.orientation[0], this.orientation[1])
            this.buzz.mul = min(0.8, vel*200)
            this.buzz.speed =  1 + (vel*10)
                    
        gl.glPushMatrix()
        gl.glEnable( GL2.GL_ALPHA_TEST )
        gl.glAlphaFunc( GL2.GL_GREATER, 0.1 )        
        gl.glEnable( GL2.GL_TEXTURE_2D )
        gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST)
        gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST)
        gl.glBindTexture(GL2.GL_TEXTURE_2D, this.texture)
        gl.glTranslate(this.pos[0], this.pos[1], this.pos[2])
        gl.glRotate(math.degrees(math.atan2(this.orientation[1], this.orientation[0])), 0,0,1)
        gl.glScalef(this.size, this.size, this.size)
        gl.glColor4f(1, 1, 1, this.transperancy)
        gl.glBegin(GL2.GL_QUADS)
        ti = 0
        for v in this.shape
            gl.glTexCoord2d(this.texshape[ti][0], this.texshape[ti][1])
            gl.glVertex3f(v[0], v[1], this.pos[2])
            ti += 1
        gl.glEnd()
        gl.glDisable( GL2.GL_TEXTURE_2D )
        gl.glDisable( GL2.GL_ALPHA_TEST )
        gl.glPopMatrix()
        
        if (this.showCtrlPoints
            gl.glPushMatrix()
            gl.glPointSize (4.0)
            gl.glColor4f(1.0,0,0,1.0)
            gl.glBegin (GL2.GL_POINTS)
            for i,p in this.ctrlpoints.iteritems()
                gl.glVertex3f (p[0], p[1], p[2])
            gl.glEnd ()
            gl.glPointSize (1.0)
            gl.glPopMatrix()
                
    public void newstartpos()
        x = this.app.canvas.orthoCoordWidth/2 - (random.random() * this.app.canvas.orthoCoordWidth)
        y = random.choice([-1,1])*this.app.canvas.orthoCoordWidth/2/this.app.canvas.aspectRatio + this.size * 1.1
        this.pos = (x,y,0)
        
    public void click(agentName, replace=true)
        
        click
        
    
    public void startDrag(pos)
        if (this.interactive and this.canBeDraged
            this.beingDragged = true
            this.locationChanged = false
            this.dragStartXY = pos
            this.dragStartWorld = this.pos
        
    public void stopDrag()
        if (this.interactive and this.canBeDraged
            this.beingDragged = false
            this.locationChanged = false
            this.newctrlpoints()
    
    public void drag(newXY)
        if (this.interactive and this.canBeDraged
            # Based on http//web.iiit.ac.in/~vkrishna/data/unproj.html
            projection = glGetDoublev(GL2.GL_PROJECTION_MATRIX)
            modelview = glGetDoublev(GL2.GL_MODELVIEW_MATRIX)
            viewport = glGetIntegerv(GL2.GL_VIEWPORT)
            windowZ = glReadPixels(newXY[0], viewport[3]-newXY[1], 1, 1, GL2.GL_DEPTH_COMPONENT, GL2.GL_FLOAT)
            
            worldCoords = gluUnProject(newXY[0], viewport[3] - newXY[1], windowZ[0][0], modelview, projection, viewport)
            if (this.floatingXY
                this.pos = (worldCoords[0], worldCoords[1], this.pos[2])
            else
                this.pos = (worldCoords[0], worldCoords[1], worldCoords[2])
                            
            this.locationChanged = true
        
    public void remove(fadeOut=false, fadingFrames=100)
        if (this.buzz
            if (fadeOut 
                this.buzz.mul = this.buzz.mul / 2
            else
                this.buzz.stop()
        super(Bee, ).remove(fadeOut, fadingFrames)            
                                    
        