import java.util.Map;

//translation from Py May 11th
public class MagicLeaves extends EchoesObject
{   
 //   public classdocs
	private BezierMotion bezierMotion = new BezierMotion();
	private float [][] shape = {{0, 0}, {1, 0}, {1, 1}, {0, 1}};
	private float [][] texshape = {{0, 0}, {1, 0}, {1, 1}, {0, 1}};
	//__init__(autoAdd=true, props={"type" "MagicLeaves"}, fadeIn = false, fadingFrames = 100, callback=None)
    public MagicLeaves(boolean autoAdd, Map<String, String> props, boolean fadeIn, int fadingFrames, Object callback)
    {       
        super(autoAdd, props, fadeIn, fadingFrames, callback);
        //super(MagicLeaves, ).initBezierVars()       
        
        this.size = (float) 0.5;
        this.pos[0] = 0;
        this.pos[1] = 0;
        this.pos[2] = 0;
        this.orientation = 0;
        this.speed = 0.04;
    
        this.flying = true;
        this.flyingXY = true;

        bezierMotion.newCtrlPoints(null);
        this.drawCtrlPoints = false;
        this.removeAtTargetPos = false;

        this.flapamplitude = 45;// # max opening angle when flapping in degrees 
        this.flap = 0;
    
        this.energy = 1.0;
    
        this.setImage();
         
        this.tree = None;
        this.putOnTree();
    }
    public void setAttr(String item, String value)
    {
    	if (item == "energy")
    	{
    		this.flapamplitude = 45 * value;
            if (value > 0.8)
                this.boundingBox = this.app.canvas.getRegionCoords("v-top");
            else if (value > 0.6)
                this.boundingBox = this.app.canvas.getRegionCoords("v-middle");
            else if (value > 0.3)
                this.boundingBox = this.app.canvas.getRegionCoords("v-bottom");
            else
                this.boundingBox = this.app.canvas.getRegionCoords("ground");
            this.speed = 0.01 * value;
    	}               
        if (item == "flying")
            this.app.canvas.rlPublisher.objectPropertyChanged(str(this.id), "leaves_flying", str(value));

        setAttr(item, value);
    }
    public void setImage()
    {
    	String [] images = {"Leaf1.png", "Leaf2.png"};
        this.textures = glGenTextures(images.length);
        int i = 0;
        for (String image : images)
        {
        	im = PIL.Image.open("visual/images/" + image);
            try                
            {
            	ix, iy, idata = im.size[0], im.size[1], im.tostring("raw", "RGBA", 0, -1);
            }
            catch( SystemError e)
            {
            	ix, iy, idata = im.size[0], im.size[1], im.tostring("raw", "RGBX", 0, -1);        
            }

            gl.glPixelStorei(GL2.GL_UNPACK_ALIGNMENT,1);
            gl.glBindTexture(GL2.GL_TEXTURE_2D, this.textures[i]);
            gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, 4, ix, iy, 0, GL2.GL_RGBA, GL2.GL_UNSIGNED_BYTE, idata);        
            i += 1;
        }
    }
            
    public void renderObj(GL2 gl)
    {    
     //   overwriting the render method to draw the flower
                    
        gl.glPushMatrix();
        gl.glEnable( GL2.GL_ALPHA_TEST );
        gl.glAlphaFunc( GL2.GL_GREATER, 0.1 );
        
        if (this.energy > 0)
            this.energy -= 0.0005;
        else
            this.energy = 0;
        
        if (this.flying && this.interactive)
        {
        	oldpos = this.pos;
            this.pos = this.nextBezierPos(this.flyingXY);
            if (this.pos[0]!=oldpos[0] || this.pos[1]!=oldpos[1] || this.pos[2]!=oldpos[2])
                this.orientation = math.atan2(this.pos[1]-oldpos[1], this.pos[0]-oldpos[0]);  
            if (this.removeAtTargetPos && this.bezierIndex > 0.95)
                this.remove();
            
            this.flap = (this.flap + 0.4) % (2*math.pi);
        }
        gl.glTranslate(this.pos[0], this.pos[1], this.pos[2]);
        gl.glScalef(this.size, this.size, this.size);
        gl.glRotate(math.degrees(this.orientation), 0,0,1);
        
        angle =  this.flapamplitude * (1+math.sin(this.flap));

        if (this.flying || this.beingDragged)
        {
        	gl.glColor4f(0.584, 0.060, 0.025, this.transperancy);        
            gl.glBegin(GL2.GL2.GL_QUADS);
            gl.glVertex3f(0.5*this.size, 0.05*this.size, this.pos[2]);
            gl.glVertex3f(0.5*this.size, -0.05*this.size, this.pos[2]);
            gl.glVertex3f(-0.5*this.size, -0.05*this.size, this.pos[2]);
            gl.glVertex3f(-0.5*this.size, 0.05*this.size, this.pos[2]);
            gl.glEnd();
        }
        i = 0;
        olda = 0;
        for texture in this.textures
        {
        	gl.glEnable( GL2.GL_TEXTURE_2D );
            gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
            gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
            gl.glBindTexture(GL2.GL_TEXTURE_2D, texture);
            a = math.pow(-1, i) * angle - olda;
            olda = a; 
            gl.glRotate(a, 1,0,0.25);              
            gl.glColor4f(1, 1, 1, this.transperancy);
            gl.glBegin(GL2.GL2.GL_QUADS);
            ti = 0;
            for(float[] v : this.shape)
            {
            	gl.glTexCoord2d(this.texshape[ti][0], this.texshape[ti][1]);
                gl.glVertex3f(v[0], v[1], 0);
                ti += 1;
            }
            gl.glEnd();
            gl.glDisable( GL2.GL_TEXTURE_2D );
            i += 1;
        }
        gl.glDisable( GL2.GL_ALPHA_TEST );
        gl.glPopMatrix();
    }         
    //startDrag(pos=(0,0))
    public void startDrag(pos=(0,0))
    {
    	this.app.canvas.agentPublisher.agentActionCompleted('User', 'touch_leaves', [str(this.id)]);
        this.beingDragged = true;
        this.energy = 0;
        this.flying = false;
        if (this.tree)
        {
        	branch = 0;
            for leaf in this.tree.leaves
                if (leaf ==  this.tree.leaves[branch])
                	= None;//********8what = none?
                branch += 1;
        }
    }           
    public void stopDrag()
    {
    	this.beingDragged = false;
        h = (float)(this.app.canvas.orthoCoordWidth / this.app.canvas.aspectRatio);
        this.energy = (this.pos[1] + h/2)/h;
        this.newctrlpoints();
        this.flying = true;
    }
    public void drag(float [] newXY)
    {
    	if (this.interactive)
    	{ //   # Based on http//web.iiit.ac.in/~vkrishna/data/unproj.html
            projection = glGetDoublev(GL2.GL_PROJECTION_MATRIX);
            modelview = glGetDoublev(GL2.GL_MODELVIEW_MATRIX);
            viewport = glGetIntegerv(GL2.GL_VIEWPORT);
            windowZ = glReadPixels(newXY[0], viewport[3]-newXY[1], 1, 1, GL2.GL_DEPTH_COMPONENT, GL2.GL_FLOAT);
            
            worldCoords = gluUnProject(newXY[0], viewport[3] - newXY[1], windowZ[0][0], modelview, projection, viewport);

            this.pos = (worldCoords[0], worldCoords[1], this.pos[2]);
    	}
    }
    public void touchLeaves(agent_id=None)
    {//#        if (agent_id
    //#            this.app.canvas.agentPublisher.agentActionCompleted('Agent', 'touch_leaves', [str(this.id), str(agent_id)])

        if (this.tree)
        {
        	branch = 0;
            for leaf in this.tree.leaves
            {
            	if (leaf ==  this.tree.leaves[branch])
                	= None;//****something equal to none
                branch += 1;
        	}
        }
        h = float(this.app.canvas.orthoCoordWidth / this.app.canvas.aspectRatio);
        this.energy = (this.pos[1] + h/2)/h;
        this.newctrlpoints();
        this.flying = true;
    }    
    public void putOnTree(id=None, branch=-1)
    {
    	if (!id)
    	{
    		for oid, se in this.app.canvas.objects.items()
                if (isinstance(se, LifeTree))
                {
                	id = oid;
                	break;
                }
    	}
        if (!id)
        {
        	Logger.warning("No tree found to put magic leaves on");
            return;
        }
        tree = this.app.canvas.objects[id];
        if (branch==-1)
            branch = tree.getFreeBranch();
        if (branch==-1)
        {
        	Logger.warning("No free tree branch found to put magic leaves on");
            return;
        }

        this.energy = 0.0;
        this.flying = false;

        tree.leaves[branch] = ;//*****equsal to? 
        this.tree = tree;
        if (branch == 0)
        {
        	dx = -0.47;
            dy = 0.35;
            this.orientation = 1.5;
        }
        else if (branch == 1)
        {
        	dx = -0.15;
            dy = 0.49;
            this.orientation = 0.2;
        }   
        else if (branch == 2)
        {
        	dx = 0.19;
            dy = 0.47;
            this.orientation = -0.2;
        }
        else
        {
        	dx = 0.47;
            dy = 0.26;
            this.orientation = -0.5;
        }
        this.pos = (tree.pos[0]+tree.size*dx, tree.pos[1]+tree.size*dy, tree.pos[2]);
    }                   
    public void remove(fadeOut, fadingFrames)
    {
    	super.remove(fadeOut, fadingFrames);            
    }
}