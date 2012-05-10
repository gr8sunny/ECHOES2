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
//import java.util.Map;

public class Cloud extends EchoesObject
{   
	private float shape = new float[40][2];
	private double[] pos = {-0.39, 2.25, 0};
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
        
        if (sound.EchoesAudio.soundPresent)
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
    {    
    	if (this.interactive)
        {   // Based on http//web.iiit.ac.in/~vkrishna/data/unproj.html
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
    
    
