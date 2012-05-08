
Created on 8 Sep 2009



from EchoesObject import *
from OpenGL.GL import *
from OpenGL.GLU import *
from OpenGL.GLE import *
from OpenGL.GLUT import *
import PIL.Image
import random
import echoes
import math, numpy
import objects.Environment
import Ice
import Logger        
import Bubbles, PlayObjects
import Motions
        
public class EchoesFlower
    
    public classdocs
    
    public void __init__(autoAdd=true, props={"type" "Flower"}, fadeIn = false, fadingFrames = 100, callback=None)
        
        
        
        super(EchoesFlower, ).__init__(app, autoAdd, props, fadeIn, fadingFrames, callback)
        
        this.size = 0.4
        this.maxSize = 0.6
        this.pos = (0,0,0) 
        this.rotate = [0,0,0]
        
        this.publishRegion = true
        this.underCloud = false
        this.amplitude = 0
        this.swing = 0
        
        if ("colour" in this.props
            this.colour = this.props["colour"]
        else
            this.colour = "red"
        
        this.patterntex = this.setImage("visual/images/Circles.png")                    
                    
        this.shape = [(-1, -1), (1, -1), (1, 1), (-1, 1)]
        this.texshape = [(0, 0), (1, 0), (1, 1), (0, 1)]

        this.targetPos = None                                
        this.targetBasket = None
                
        this.pot = None 
        this.basket = None
        this.inCollision = None
        this.canGrow = true
        this.isGrowing = 0
        this.growToSize = None
        this.avatarTCB = None
        this.canTurnIntoBall = true
        this.canTurnIntoBubble = true
        this.childCanTurnIntoBubble = true
        this.childCanTurnIntoBall = true

    public void __setattr__(item, value)
        if (item == "size"
            this.stemLength = value * 4
            this.calcStemPoints()
            this.stemWidth = int(min(this.app.canvas.lineWidthRange[1] * 2 * value, 10))            

        else if (item == "growToSize"
            value = min(this.maxSize, value)

        else if (item == "colour"
            if (value == "green"
                this.texture = this.setImage('visual/images/FlowerHead-01.png')
            else if (value == "blue"
                this.texture = this.setImage('visual/images/FlowerHead-03.png')
            else if (value == "yellow"
                this.texture = this.setImage('visual/images/FlowerHead-04.png')
            else # red is the public voidault
                this.texture = this.setImage('visual/images/FlowerHead-02.png')

        else if (item == "pos" and hasattr("pos") and hasattr("underCloud")
            for oid, o in this.app.canvas.objects.items()
                if (isinstance(o, objects.Environment.Cloud)
                    if (o.isUnder()
                        if (not this.underCloud this.underCloud = true
                    else
                        if (this.underCloud this.underCloud = false
                        
        else if (item == "pot"
            if (value == None
                this.app.canvas.rlPublisher.objectPropertyChanged(str(this.id), "flower_pot", "None")
            else
                this.app.canvas.rlPublisher.objectPropertyChanged(str(this.id), "flower_pot", str(value.id))
                
        else if (item == "basket"
            if (value == None
                this.app.canvas.rlPublisher.objectPropertyChanged(str(this.id), "flower_basket", "None")
            else
                this.app.canvas.rlPublisher.objectPropertyChanged(str(this.id), "flower_basket", str(value.id))

        else if (item == "underCloud"
            this.app.canvas.rlPublisher.objectPropertyChanged(str(this.id), "under_cloud", str(value))
            
        else if (hasattr("isGrowing") and item == "isGrowing"
            if (this.isGrowing > 0 and value == 0
                this.app.canvas.rlPublisher.objectPropertyChanged(str(this.id), "is_growing", "false")
            if (this.isGrowing <= 0 and value > 0
                this.app.canvas.rlPublisher.objectPropertyChanged(str(this.id), "is_growing", "true")

        object.__setattr__(item, value)
                        
    public void findTargetBasket()
        for id, se in this.app.canvas.objects.items()
            if (isinstance(se, objects.Environment.Basket)
                this.targetBasket = se
                break
            
    public void calcStemPoints()
        this.stemPoints = []
        for i in range(4)
            if (i > 0 and i < 4
                x = random.uniform(-0.2,0.2)
            else
                x = 0
            this.stemPoints.append([x, -1*this.stemLength*float(i)/3.0, 0])
            
    public void renderObj()
         
        overwriting the render method to draw the flower
        
        if (not (hasattr("swing")) return
        
        if ((not (this.basket and this.basket.avatarTCB) and
            not (this.pot and this.pot.avatarTCB))
            if (not this.inCollision
                if (this.basket
                    this.basket.removeFlower()
                    this.basket = None
                if (this.pot
                    this.pot.flower = None
                    this.pot = None        
            this.inCollision = None             
        
        if (this.isGrowing > 0
            this.isGrowing -= 1
            
        if (this.growToSize and this.canGrow
            if (this.size < this.growToSize
                this.grow()
            else
                this.growToSize = None
        
        if (this.targetPos
            d = [0,0,0]
            for i in range(3)
                d[i] = this.targetPos[i] - this.pos[i]
            this.pos = [this.pos[0] + d[0] / 20, this.pos[1] + d[1] / 20, this.pos[2] + d[2] / 20]
            if (abs(d[0]+d[1]+d[2]) < 0.05
                this.pos = this.targetPos
                this.targetPos = None
                if (this.targetBasket
                    this.targetBasket.addFlower()
                    this.interactive = true
                    this.targetBasket = None
                    
            
        if (not this.beingDragged
            this.swing = (this.swing + 0.1) % (2*math.pi) # animate the swinging stem
            this.amplitude = this.amplitude - 0.005
            if (this.amplitude < 0 this.amplitude = 0    

        dx= -1.5*this.size * this.amplitude * math.sin(this.swing)
        dy= this.stemLength - math.sqrt(math.pow(this.stemLength, 2) - math.pow(dx, 2))        
        this.stemPoints[0]=(-1*dx,-1*dy,this.pos[2])
                                         
        gl.gl.glPushMatrix()
        
            # centre position
        gl.gl.glTranslate(this.pos[0], this.pos[1], this.pos[2])  #make sure the head is in front of the stem
        gl.gl.glRotatef(this.rotate[2],0,0,1)
        
            # Stem
        if (not (hasattr("stemWidth")) or this.stemWidth == 0
            this.stemWidth = 1
        gl.gl.glLineWidth(this.stemWidth)
        gl.gl.glColor4f(0.229, 0.259, 0.326, this.transperancy)
        this.app.canvas.drawBezier(this.stemPoints, false)
        gl.gl.glLineWidth(1.0)
            # touch area for better dragging
        gl.gl.glDisable(GL2.GL2.GL_DEPTH_TEST)
        gl.gl.glColor4f(1, 1, 1, 0.0)
        gl.gl.glBegin(GL2.GL2.GL_QUADS)
        gl.gl.glVertex3f(-this.size*0.7, 0, -0.1)
        gl.gl.glVertex3f(this.size*0.7, 0, -0.1)
        gl.gl.glVertex3f(this.size*0.7, -this.stemLength, -0.1)
        gl.gl.glVertex3f(-this.size*0.7, -this.stemLength, -0.1)
        gl.gl.glEnd()
        gl.gl.glEnable(GL2.GL2.GL_DEPTH_TEST)
            # Head
        gl.gl.glEnable( GL2.GL2.GL_ALPHA_TEST )
        gl.gl.glAlphaFunc( GL2.GL2.GL_GREATER, 0.1 )        
        gl.gl.glEnable( GL2.GL2.GL_TEXTURE_2D )
        gl.gl.glTexParameterf(GL2.GL2.GL_TEXTURE_2D, GL2.GL2.GL_TEXTURE_MAG_FILTER, GL2.GL2.GL_NEAREST)
        gl.gl.glTexParameterf(GL2.GL2.GL_TEXTURE_2D, GL2.GL2.GL_TEXTURE_MIN_FILTER, GL2.GL2.GL_NEAREST)
        gl.gl.glBindTexture(GL2.GL2.GL_TEXTURE_2D, this.texture)
        gl.gl.glTranslate(this.stemPoints[0][0], this.stemPoints[0][1], this.stemPoints[0][2]+0.05)
        gl.gl.glScalef(this.size, this.size, this.size)
        gl.gl.glColor4f(1, 1, 1, this.transperancy)
        gl.gl.glBegin(GL2.GL2.GL_QUADS)
        ti = 0
        for v in this.shape
            gl.gl.glTexCoord2d(this.texshape[ti][0], this.texshape[ti][1])
            gl.gl.glVertex3f(v[0], v[1], this.pos[2])
            ti += 1
        gl.gl.glEnd()
        gl.gl.glDisable( GL2.GL2.GL_TEXTURE_2D )

        if (not this.childCanTurnIntoBall or not this.childCanTurnIntoBubble
            gl.gl.glEnable( GL2.GL2.GL_TEXTURE_2D )
            gl.gl.glTexParameterf(GL2.GL2.GL_TEXTURE_2D, GL2.GL2.GL_TEXTURE_MAG_FILTER, GL2.GL2.GL_NEAREST)
            gl.gl.glTexParameterf(GL2.GL2.GL_TEXTURE_2D, GL2.GL2.GL_TEXTURE_MIN_FILTER, GL2.GL2.GL_NEAREST)
            gl.gl.glBindTexture(GL2.GL2.GL_TEXTURE_2D, this.patterntex)
            gl.gl.glColor4f(1, 1, 1, this.transperancy*0.5)
            gl.gl.glTranslate(0,0,0.05)
            gl.gl.glBegin(GL2.GL2.GL_QUADS)
            ti = 0
            for v in this.shape
                gl.gl.glTexCoord2d(this.texshape[ti][0], this.texshape[ti][1])
                gl.gl.glVertex3f(v[0], v[1], this.pos[2])
                ti += 1
            gl.gl.glEnd()
            gl.gl.glDisable( GL2.GL2.GL_TEXTURE_2D )

        gl.gl.glDisable( GL2.GL2.GL_ALPHA_TEST )
        
        gl.gl.glPopMatrix()
                
    public void shake(force)
        
        Shake the whole plant, stem rooted in the soil
        
        pass
                
    public void grow()
        
        Grow the plant bigger to the set maximum
        
        if (this.size < this.maxSize
            this.size += 0.001
            this.pos = (this.pos[0], this.pos[1]+0.004, this.pos[2])
            this.isGrowing = 5 # number of frames that it will report growing
        else
            this.canGrow=false 
                
    public void moveToBasket(id)
        if (id
            this.targetBasket = this.app.canvas.objects[id]
        else
            this.findTargetBasket()
        if (this.targetBasket
            this.interactive = false
            if (this.basket == this.targetBasket
                Logger.warning("Flower "  + str(this.id) + " is already in basket " + str(this.targetBasket.id))
            else
                Logger.trace("info", "moving flower " + str(this.id) + " to basket " + str(this.targetBasket.id))
                this.targetPos = [this.targetBasket.pos[0]+(0.4*random.random()-0.2), this.targetBasket.pos[1]+this.stemLength-this.targetBasket.size/2, this.targetBasket.pos[2]-0.5]
        else
            Logger.warning("Cannot move flower "  + str(this.id) + " to basket, no basket found in scene")
            
    public void attachToJoint(jpos, jori, avatarTCB)
        this.avatarTCB = avatarTCB
        this.objectCollisionTest = false
        rotz_r = math.pi - jori[2]
        if (jori[0] < 0
            this.rotate[2] =  math.degrees(rotz_r)
            this.pos = [jpos[0]-this.stemLength/2*math.sin(rotz_r), jpos[1]+this.stemLength/2*math.cos(rotz_r), this.pos[2]]
        else
            this.rotate[2] =  math.degrees(rotz_r) + 180
            this.pos = [jpos[0]+this.stemLength/2*math.sin(rotz_r), jpos[1]-this.stemLength/2*math.cos(rotz_r), this.pos[2]]
        this.old_jpos = jpos
            
    public void detachFromJoint()
        this.avatarTCB = None
        this.objectCollisionTest = true
        this.pos = [this.old_jpos[0], this.old_jpos[1] + this.stemLength/2, this.old_jpos[2]]
        this.rotate = [0,0,0]
        

    public void click(agentName)
        
        pick
        
        this.app.canvas.agentPublisher.agentActionCompleted('User', 'flower_pick', [str(this.id)])
        pass
        
    public void startDrag(newXY)
        this.beingDragged = true
        # Based on http//web.iiit.ac.in/~vkrishna/data/unproj.html
        projection = glGetDoublev(GL2.GL2.GL_PROJECTION_MATRIX)
        modelview = glGetDoublev(GL2.GL2.GL_MODELVIEW_MATRIX)
        viewport = glGetIntegerv(GL2.GL2.GL_VIEWPORT)
        windowZ = glReadPixels(newXY[0], viewport[3]-newXY[1], 1, 1, GL2.GL2.GL_DEPTH_COMPONENT, GL2.GL2.GL_FLOAT)
        worldCoords = gluUnProject(newXY[0], viewport[3] - newXY[1], windowZ[0][0], modelview, projection, viewport)
        this.worldDragOffset = [this.pos[0]-worldCoords[0], this.pos[1]-worldCoords[1], 0]
        
    public void stopDrag()
        this.beingDragged = false
    
    public void drag(newXY)
        if (not this.interactive return
        # Based on http//web.iiit.ac.in/~vkrishna/data/unproj.html
        projection = glGetDoublev(GL2.GL2.GL_PROJECTION_MATRIX)
        modelview = glGetDoublev(GL2.GL2.GL_MODELVIEW_MATRIX)
        viewport = glGetIntegerv(GL2.GL2.GL_VIEWPORT)
        windowZ = glReadPixels(newXY[0], viewport[3]-newXY[1], 1, 1, GL2.GL2.GL_DEPTH_COMPONENT, GL2.GL2.GL_FLOAT)
        
        worldCoords = gluUnProject(newXY[0], viewport[3] - newXY[1], windowZ[0][0], modelview, projection, viewport)
            # started drag outside the flower head
        if (this.worldDragOffset[1] > this.size
            # drag
            this.pos = [worldCoords[0]+this.worldDragOffset[0], worldCoords[1]+this.worldDragOffset[1], this.pos[2]]                
            this.locationChanged = true
            if (this.avatarTCB
                this.avatarTCB.detachObject()
            # started drag in within the flowerhead
        else
                # into Bubble
            if (this.magic and this.childCanTurnIntoBubble and worldCoords[1] > (this.pos[1] + this.size/2)
                if (this.avatarTCB
                    this.avatarTCB.detachObject()
                this.intoBubble(true)
                # into Ball
            else if (this.magic and this.childCanTurnIntoBall and worldCoords[1] < (this.pos[1] - this.size/2)
                if (this.avatarTCB
                    this.avatarTCB.detachObject()
                this.intoBall(true)
                # swing
            else
                this.swing = max(min((worldCoords[0] - this.pos[0]) / this.size, 1), -1)
                this.amplitude = math.fabs(this.swing)
                this.swing = this.swing * math.pi / 2 # for max amplitude
                
    public void intoBubble(byUser=false)
        if (this.canTurnIntoBubble
            bubble = Bubbles.EchoesBubble(this.app, true, fadeIn=true, fadingFrames=10)
            bubble.setStartPos(this.pos)
            bubble.size = this.size
            bubble.willBeReplaced = false
            if (this.pot
                this.pot.flower = None
            if (this.basket
                this.basket.removeFlower()
            this.remove()
            if (byUser
                this.app.canvas.agentPublisher.agentActionCompleted('User', 'flower_bubble', [str(this.id), str(bubble.id)])
            this.app.canvas.rlPublisher.objectPropertyChanged(str(this.id), "flower_bubble", str(bubble.id))

    public void intoBall(byUser=false)
        if (this.canTurnIntoBall
            ball = PlayObjects.Ball(this.app, true, fadeIn=true, fadingFrames=10)
            ball.pos = this.pos
            ball.size = this.size
            ball.colour = this.colour
            if (this.pot
                this.pot.flower = None
            if (this.basket
                this.basket.removeFlower()
            this.remove()       
            if (byUser
                this.app.canvas.agentPublisher.agentActionCompleted('User', 'flower_ball', [str(this.id), str(ball.id)])
            this.app.canvas.rlPublisher.objectPropertyChanged(str(this.id), "flower_ball", str(ball.id))
        
    public void remove(fadeOut = false, fadingFrames = 100)
        if (this.avatarTCB
            this.detachFromJoint()
        super(EchoesFlower, ).remove(fadeOut, fadingFrames)
        
        
public class Pot
    
    public classdocs
    
    public void __init__(autoAdd=true, props={"type" "Pot"}, fadeIn = false, fadingFrames = 100, callback=None)
        
        
        
        super(Pot, ).__init__(app, autoAdd, props, fadeIn, fadingFrames, callback)
        
        this.size = 0.3 + random.random()*0.2
        this.pos = [-1,-2.5,0.1] 
        this.publishRegion = true
        this.underCloud = false
        
        this.canBeDraged = true
        this.publishGrowStarted = false
        
        this.public voidaultHeight = this.app.canvas.getRegionCoords("ground")[0][1]
        this.fallTopublic voidaultHeight = true
        this.falling = false

        # basic shape in two strips [x,y, colour shade value]
        this.shape = [[[-1, 0.5, 1], [1, 0.5, 0.8], [1, 0.7, 0.8], [-1, 0.7, 1]],
                      [[-0.8, 0.5, 1], [-0.6, -0.7, 0.6], [0.6, -0.7, 0.6], [0.8, 0.5, 1]]]

        # a random neutral shade 
        this.neutralshade = ["neutral-1", "neutral-2", "neutral-3", "neutral-4", "neutral-5"][random.randint(0,4)]  

        # the flower growing out of the pot 
        this.flower = None
        this.stack = None
                                    
        if ("colour" in this.props
            this.colour = this.props["colour"]
            this.neutralshade = this.props["colour"]
        else
            this.colour = this.neutralshade
                    
        this.avatarTCB = None

    public void __setattr__(item, value)
        if (item == "colour"
            if (value == "dark"
                this.basecolour = [0.770, 0.371, 0.082, 1.0]
                this.linecolour = [0.3,0.1,0.1,1]
            else if (value == "neutral-1"                
                this.basecolour = [1.000, 0.609, 0.277, 1.000]
                this.linecolour = [0.3,0.1,0.1,1]
            else if (value == "neutral-2"                
                this.basecolour = [0.955, 0.878, 0.471, 1.000]
                this.linecolour = [0.3,0.1,0.1,1]
            else if (value == "neutral-3"                
                this.basecolour = [1.000, 0.796, 0.634, 1.000]
                this.linecolour = [0.3,0.1,0.1,1]
            else if (value == "neutral-4"                
                this.basecolour = [0.872, 0.655, 0.133, 1.000]
                this.linecolour = [0.3,0.1,0.1,1]
            else # neutral is the public voidault
                this.basecolour = [0.970, 0.571, 0.282, 1.0]
                this.linecolour = [1,0,0,1]
                
        else if (item == "flower" and isinstance(value, EchoesFlower)
            if (hasattr("hasOnTop") and this.hasOnTop 
                Logger.warning("Pot can't have flower in pot that has other pots on top of it")
                return
            this.app.canvas.rlPublisher.objectPropertyChanged(str(this.id), "pot_flower", str(value.id))
            value.pos = [this.pos[0], this.pos[1]+value.stemLength+this.size/2, this.pos[2]-0.01]
            value.inCollision = this.id
            value.pot = 
            
            Logger.trace("info", "Flower put into pot" + str(this.id) )
            if (value.beingDragged
                this.app.canvas.agentPublisher.agentActionCompleted('User', 'flower_placeInPot', [str(this.id), str(value.id)])
            
        else if (item == "flower" and value == None
            this.app.canvas.rlPublisher.objectPropertyChanged(str(this.id), "pot_flower", "None")
        
        else if (item == "pos" and hasattr("pos") 
            if (hasattr("stack") and this.stack and ((hasattr("beingDragged") and this.beingDragged) or (hasattr("avatarTCB") and this.avatarTCB))
                # if (the user did it, notify the rest of the system
                split = this.stack.split()
                if (split and hasattr("beingDragged") and this.beingDragged
                    this.app.canvas.agentPublisher.agentActionCompleted('User', 'unstack_pot', [str(this.id)])
                if (this.stack # the stack might be removed if (its the only pot left
                    for pot in this.stack.pots
                        if (pot !=  
                            dx = this.pos[0]-pot.pos[0]
                            dy = this.pos[1]-pot.pos[1]
                            pot.pos = [value[0]-dx, value[1]-dy, pot.pos[2]]   
            if (hasattr("flower") and this.flower
                this.flower.pos = [value[0], value[1]+this.flower.stemLength+this.size/2, value[2]-0.01]

            if (hasattr("underCloud")
                for oid, o in this.app.canvas.objects.items()
                    if (isinstance(o, objects.Environment.Cloud)
                        if (o.isUnder()
                            if (not this.underCloud this.underCloud = true
                        else
                            if (this.underCloud this.underCloud = false
                            
        else if (item == "stack"
            if (value == None
                this.hasOnTop = None
                this.isOnTopOf = None
                this.colour = this.neutralshade
                this.app.canvas.rlPublisher.objectPropertyChanged(str(this.id), "pot_stack", "false")
            else
                this.colour = "dark"
                this.app.canvas.rlPublisher.objectPropertyChanged(str(this.id), "pot_stack", "true")
                           
        else if (item == "underCloud"
            this.app.canvas.rlPublisher.objectPropertyChanged(str(this.id), "under_cloud", str(value))
  
        else if (item == "hasOnTop"
            this.app.canvas.rlPublisher.objectPropertyChanged(str(this.id), "has_on_top", str(value))

        else if (item == "isOnTopOf"
            this.app.canvas.rlPublisher.objectPropertyChanged(str(this.id), "is_on_top_of", str(value))

        object.__setattr__(item, value)
        
    public void renderObj()
         
        overwriting the render method to draw the pot
        
        if (not hasattr("stack") return # in case rendering is called before the object is fully built
        
        if (this.stack
            if (this.stack.pots[len(this.stack.pots)-1] == 
                if (this.hasOnTopthis.hasOnTop = None
            else
                if (not this.hasOnTop
                    i = this.stack.pots.index()
                    this.hasOnTop = this.stack.pots[i+1].id
            if (this.stack.pots[0] == 
                if (this.isOnTopOfthis.isOnTopOf = None
            else
                if (not this.isOnTopOf
                    i = this.stack.pots.index()
                    this.isOnTopOf = this.stack.pots[i-1].id                    
        
        if (this.fallTopublic voidaultHeight and not this.beingDragged and not this.avatarTCB
            hdiff = this.pos[1] - this.public voidaultHeight
            if (abs(hdiff) > 0.05
                if (not this.stack # no stack
                    this.pos = [this.pos[0], this.pos[1]-hdiff/10, this.pos[2]]
                    this.falling = true
                else if (==this.stack.pots[0] # lowest of stack        
                    for pot in this.stack.pots
                        pot.pos = [pot.pos[0], pot.pos[1]-hdiff/10, pot.pos[2]]
                        pot.falling = true
                else
                    this.falling = false
            else
                this.falling = false
                        
        gl.gl.glPushMatrix()
        gl.gl.glTranslate(this.pos[0], this.pos[1], this.pos[2])
        gl.gl.glScalef(this.size, this.size, this.size)
        c = this.basecolour
        for rectangle in this.shape
            gl.gl.glBegin( GL2.GL2.GL_QUADS )
            for v in rectangle
                gl.gl.glColor4f(c[0]*v[2], c[1]*v[2], c[2]*v[2], c[3]*this.transperancy)
                gl.gl.glVertex(v[0],v[1], this.pos[2])
            gl.gl.glEnd()
            gl.gl.glLineWidth(3.0)
            gl.gl.glBegin( GL2.GL2.GL_LINE_STRIP )
            gl.gl.glColor4f(this.linecolour[0], this.linecolour[1], this.linecolour[2], this.linecolour[3]*this.transperancy)            
            for v in rectangle
                gl.gl.glVertex(v[0],v[1], this.pos[2])
            gl.gl.glEnd()
            gl.gl.glLineWidth(1.0)
        gl.gl.glPopMatrix()
            
    public void growFlower()
        if (not this.hasOnTop
            if (not this.flower
                this.flower = EchoesFlower(this.app, true, fadeIn=true)
                this.flower.size = 0.1
                this.flower.pos = [this.pos[0], this.pos[1]+this.flower.stemLength+this.size/2, this.pos[2]-0.01]
            else
                this.flower.grow()
    
    public void click(agentName)
        
        pick
        
        pass
        
    public void startDrag(newXY)
        if (this.avatarTCB
            this.avatarTCB.detachObject()
        this.beingDragged = true
        # Based on http//web.iiit.ac.in/~vkrishna/data/unproj.html
        projection = glGetDoublev(GL2.GL2.GL_PROJECTION_MATRIX)
        modelview = glGetDoublev(GL2.GL2.GL_MODELVIEW_MATRIX)
        viewport = glGetIntegerv(GL2.GL2.GL_VIEWPORT)
        windowZ = glReadPixels(newXY[0], viewport[3]-newXY[1], 1, 1, GL2.GL2.GL_DEPTH_COMPONENT, GL2.GL2.GL_FLOAT)
        worldCoords = gluUnProject(newXY[0], viewport[3] - newXY[1], windowZ[0][0], modelview, projection, viewport)
        this.worldDragOffset = [this.pos[0]-worldCoords[0], this.pos[1]-worldCoords[1], 0] 
        
    public void stopDrag()
        this.beingDragged = false
        if (this.publishGrowStarted
            this.publishGrowStarted = false
            this.app.canvas.agentPublisher.agentActionCompleted('User', 'flower_grow', [str(this.id), str(this.flower.id), str(this.growPond)])

    public void drag(newXY)
        if (this.interactive and this.canBeDraged
            # Based on http//web.iiit.ac.in/~vkrishna/data/unproj.html
            projection = glGetDoublev(GL2.GL2.GL_PROJECTION_MATRIX)
            modelview = glGetDoublev(GL2.GL2.GL_MODELVIEW_MATRIX)
            viewport = glGetIntegerv(GL2.GL2.GL_VIEWPORT)
            windowZ = glReadPixels(newXY[0], viewport[3]-newXY[1], 1, 1, GL2.GL2.GL_DEPTH_COMPONENT, GL2.GL2.GL_FLOAT)
            
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
            y = max(jpos[1], this.public voidaultHeight)
        else
            y = jpos[1]
        this.pos = [jpos[0], y, this.pos[2]]
            
    public void detachFromJoint()
        this.avatarTCB = None
        this.objectCollisionTest = true        
        
    public void stackUp(pot)
        if (not this.stack and not pot.stack
            this.stack = pot.stack = Stack(this.app)
            this.stack.pots = [pot]
        else if (this.stack and pot.stack
            newstack = Stack(this.app)
            newstack.pots = this.stack.pots + pot.stack.pots
            for pot in newstack.pots
                pot.stack = newstack
        else if (this.stack or pot.stack
            if (pot.stack 
                pot.stack.pots = [] + pot.stack.pots
                this.stack = pot.stack
            else
                this.stack.pots = this.stack.pots + [pot]
                pot.stack = this.stack                    
        this.stack.checkAlignment()

    public void remove(fadeOut = false, fadingFrames = 100)
        if (not fadeOut and this.stack and  in this.stack.pots
            this.objectCollisionTest = false
            del this.stack.pots[this.stack.pots.index()]
            this.stack = None
        super(Pot, ).remove(fadeOut, fadingFrames)


public class Stack()
    
    public classdocs
    
    public void __init__(app)
        
        
        
        this.app = app        
        this.pots = []
        
        this.objectCollisionTest = false
        this.agentCollisionTest = false
        
    public void top()
        l = len(this.pots)
        if (l > 0
            return this.pots[l-1]
        else
            return None
    
    public void bottom()
        if (len(this.pots) > 0
            return this.pots[0]
        else
            return None
        
    public void split(pot)
        # if (pot is the lowest anyway
        if (this.pots[0] == pot return false
        #if (there are only two pots in the stack
        if (len(this.pots) == 2
            this.pots[0].stack = this.pots[1].stack = None
            this.pots = []
            return true
        # if (pot splits stack with one pot left
        if (this.pots[1] == pot
            this.pots[0].stack = None
            del this.pots[0]
            return true
        if (this.pots[len(this.pots)-1] == pot
            pot.stack = None
            del this.pots[len(this.pots)-1]
            return true
        # split stack into two stacks
        newStack = Stack(this.app)
        while this.pots[0] != pot
            newStack.pots.append(this.pots[0])
            this.pots[0].stack = newStack
            del this.pots[0]
        return true        
            
    public void checkAlignment()
        prevPot = None
        for pot in this.pots
            if (prevPot
                x, y, z = pot.pos
                if (abs(x - prevPot.pos[0]) > prevPot.size / 1.5
                    x = prevPot.pos[0] + random.uniform(-0.1,0.1)
                if (isinstance(pot, objects.Plants.Pot) and isinstance(prevPot, objects.Plants.Pot)
                    y = prevPot.pos[1] + prevPot.size + pot.size * 0.37
                else # the upper pot is really a basket
                    y = prevPot.pos[1] + prevPot.size + pot.size * 0.9
                z = prevPot.pos[2]-0.01
                pot.pos = [x,y,z]
            prevPot = pot 

    public void intoTree()
        Logger.trace("info", "replacing stack with tree") 
        tree = LifeTree(this.app, true, fadeIn=true)
        size = 0 
        for pot in this.pots
            size += pot.size
        size += 2.5
        tree.size = size
        lowest = this.pots[0]
        tree.pos = [lowest.pos[0], lowest.pos[1] + size/2, lowest.pos[2]]
        

public class LifeTree
    
    public classdocs
    
    public void __init__(autoAdd=true, props={"type" "LifeTree"}, fadeIn = false, fadingFrames = 100, callback=None)
        
        
        
        super(LifeTree, ).__init__(app, autoAdd, props, fadeIn, fadingFrames, callback)
        
        this.size = 3.5
        this.pos = (-2.5,-0.5,-1)         
        
        this.texture = this.setImage("visual/images/LifeTree.png")
        this.shape = [(-0.5, -0.5), (0.5, -0.5), (0.5, 0.5), (-0.5, 0.5)]
        this.texshape = [(0, 0), (1, 0), (1, 1), (0, 1)]
        
        this.leaves = [None, None, None, None]

    public void __setattr__(item, value)
        if (item == "pos"
            pass
                        
        object.__setattr__(item, value)
    
    public void getFreeBranch()
        branch = 0
        for leaf in this.leaves
            if (not leaf
                return branch
            branch += 1
        return -1
        
    public void renderObj()
         
        overwriting the render method to draw the flower
                    
        gl.gl.glPushMatrix()
        gl.gl.glEnable( GL2.GL2.GL_ALPHA_TEST )
        gl.gl.glAlphaFunc( GL2.GL2.GL_GREATER, 0.1 )
        
        gl.gl.glEnable( GL2.GL2.GL_TEXTURE_2D )
        gl.gl.glTexParameterf(GL2.GL2.GL_TEXTURE_2D, GL2.GL2.GL_TEXTURE_MAG_FILTER, GL2.GL2.GL_NEAREST)
        gl.gl.glTexParameterf(GL2.GL2.GL_TEXTURE_2D, GL2.GL2.GL_TEXTURE_MIN_FILTER, GL2.GL2.GL_NEAREST)
        gl.gl.glBindTexture(GL2.GL2.GL_TEXTURE_2D, this.texture)
        
        gl.gl.glTranslate(this.pos[0], this.pos[1], this.pos[2])  
        gl.gl.glScalef(this.size, this.size, this.size)
        gl.gl.glColor4f(1, 1, 1, this.transperancy)
        gl.gl.glBegin(GL2.GL2.GL_QUADS)
        ti = 0
        for v in this.shape
            gl.gl.glTexCoord2d(this.texshape[ti][0], this.texshape[ti][1])
            gl.gl.glVertex3f(v[0], v[1], this.pos[2])
            ti += 1
        gl.gl.glEnd()
        gl.gl.glDisable( GL2.GL2.GL_TEXTURE_2D )
        gl.gl.glDisable( GL2.GL2.GL_ALPHA_TEST )
        gl.gl.glPopMatrix()
        
public class MagicLeaves(EchoesObject, Motions.BezierMotion)
    
    public classdocs
    
    public void __init__(autoAdd=true, props={"type" "MagicLeaves"}, fadeIn = false, fadingFrames = 100, callback=None)
        
        
        
        super(MagicLeaves, ).__init__(app, autoAdd, props, fadeIn, fadingFrames, callback)
        super(MagicLeaves, ).initBezierVars()       
        
        this.size = 0.5
        this.pos = [0,0,0]
        this.orientation = 0
        this.speed = 0.04
    
        this.flying = true
        this.flyingXY = true

        this.newctrlpoints()
        this.drawCtrlPoints = false
        this.removeAtTargetPos = false

        this.flapamplitude = 45 # max opening angle when flapping in degrees 
        this.flap = 0
    
        this.energy = 1.0
    
        this.setImage()
        this.shape = [(0, 0), (1, 0), (1, 1), (0, 1)]
        this.texshape = [(0, 0), (1, 0), (1, 1), (0, 1)]
        
        this.tree = None
        this.putOnTree()

    public void __setattr__(item, value)
        if (item == "energy"
            this.flapamplitude = 45 * value
            if (value > 0.8
                this.boundingBox = this.app.canvas.getRegionCoords("v-top")
            else if (value > 0.6
                this.boundingBox = this.app.canvas.getRegionCoords("v-middle")
            else if (value > 0.3
                this.boundingBox = this.app.canvas.getRegionCoords("v-bottom")
            else
                this.boundingBox = this.app.canvas.getRegionCoords("ground")
            this.speed = 0.01 * value
                        
        if (item == "flying"
            this.app.canvas.rlPublisher.objectPropertyChanged(str(this.id), "leaves_flying", str(value))

        object.__setattr__(item, value)

    public void setImage()
        images = ['Leaf1.png', 'Leaf2.png']
        this.textures = glGenTextures(len(images))
        i = 0
        for image in images
            im = PIL.Image.open("visual/images/" + image)
            try                
                ix, iy, idata = im.size[0], im.size[1], im.tostring("raw", "RGBA", 0, -1)
            except SystemError
                ix, iy, idata = im.size[0], im.size[1], im.tostring("raw", "RGBX", 0, -1)        

            gl.gl.glPixelStorei(GL2.GL2.GL_UNPACK_ALIGNMENT,1)
            gl.gl.glBindTexture(GL2.GL2.GL_TEXTURE_2D, this.textures[i])
            gl.gl.glTexImage2D(GL2.GL2.GL_TEXTURE_2D, 0, 4, ix, iy, 0, GL2.GL2.GL_RGBA, GL2.GL2.GL_UNSIGNED_BYTE, idata)        
            i += 1
    
            
    public void renderObj()
         
        overwriting the render method to draw the flower
                    
        gl.gl.glPushMatrix()
        gl.gl.glEnable( GL2.GL2.GL_ALPHA_TEST )
        gl.gl.glAlphaFunc( GL2.GL2.GL_GREATER, 0.1 )
        
        if (this.energy > 0
            this.energy -= 0.0005
        else
            this.energy = 0
        
        if (this.flying and this.interactive
            oldpos = this.pos
            this.pos = this.nextBezierPos(this.flyingXY)
            if (this.pos[0]!=oldpos[0] or this.pos[1]!=oldpos[1] or this.pos[2]!=oldpos[2]
                this.orientation = math.atan2(this.pos[1]-oldpos[1], this.pos[0]-oldpos[0])  
            if (this.removeAtTargetPos and this.bezierIndex > 0.95
                this.remove()
            
            this.flap = (this.flap + 0.4) % (2*math.pi)
        
        gl.gl.glTranslate(this.pos[0], this.pos[1], this.pos[2])
        gl.gl.glScalef(this.size, this.size, this.size)
        gl.gl.glRotate(math.degrees(this.orientation), 0,0,1)
        
        angle =  this.flapamplitude * (1+math.sin(this.flap))

        if (this.flying or this.beingDragged
            gl.gl.glColor4f(0.584, 0.060, 0.025, this.transperancy)        
            gl.gl.glBegin(GL2.GL2.GL_QUADS)
            gl.gl.glVertex3f(0.5*this.size, 0.05*this.size, this.pos[2])
            gl.gl.glVertex3f(0.5*this.size, -0.05*this.size, this.pos[2])
            gl.gl.glVertex3f(-0.5*this.size, -0.05*this.size, this.pos[2])
            gl.gl.glVertex3f(-0.5*this.size, 0.05*this.size, this.pos[2])
            gl.gl.glEnd()
            
        i = 0
        olda = 0
        for texture in this.textures
            gl.gl.glEnable( GL2.GL2.GL_TEXTURE_2D )
            gl.gl.glTexParameterf(GL2.GL2.GL_TEXTURE_2D, GL2.GL2.GL_TEXTURE_MAG_FILTER, GL2.GL2.GL_NEAREST)
            gl.gl.glTexParameterf(GL2.GL2.GL_TEXTURE_2D, GL2.GL2.GL_TEXTURE_MIN_FILTER, GL2.GL2.GL_NEAREST)
            gl.gl.glBindTexture(GL2.GL2.GL_TEXTURE_2D, texture)
            a = math.pow(-1, i) * angle - olda
            olda = a 
            gl.gl.glRotate(a, 1,0,0.25)              
            gl.gl.glColor4f(1, 1, 1, this.transperancy)
            gl.gl.glBegin(GL2.GL2.GL_QUADS)
            ti = 0
            for v in this.shape
                gl.gl.glTexCoord2d(this.texshape[ti][0], this.texshape[ti][1])
                gl.gl.glVertex3f(v[0], v[1], 0)
                ti += 1
            gl.gl.glEnd()
            gl.gl.glDisable( GL2.GL2.GL_TEXTURE_2D )
            i += 1
            
        gl.gl.glDisable( GL2.GL2.GL_ALPHA_TEST )
        gl.gl.glPopMatrix()
                
    public void startDrag(pos=(0,0))
        this.app.canvas.agentPublisher.agentActionCompleted('User', 'touch_leaves', [str(this.id)])
        this.beingDragged = true
        this.energy = 0
        this.flying = false
        if (this.tree
            branch = 0
            for leaf in this.tree.leaves
                if (leaf ==  this.tree.leaves[branch] = None
                branch += 1
                
    public void stopDrag()
        this.beingDragged = false
        h = float(this.app.canvas.orthoCoordWidth / this.app.canvas.aspectRatio)
        this.energy = (this.pos[1] + h/2)/h
        this.newctrlpoints()
        this.flying = true
    
    public void drag(newXY)
        if (this.interactive
            # Based on http//web.iiit.ac.in/~vkrishna/data/unproj.html
            projection = glGetDoublev(GL2.GL2.GL_PROJECTION_MATRIX)
            modelview = glGetDoublev(GL2.GL2.GL_MODELVIEW_MATRIX)
            viewport = glGetIntegerv(GL2.GL2.GL_VIEWPORT)
            windowZ = glReadPixels(newXY[0], viewport[3]-newXY[1], 1, 1, GL2.GL2.GL_DEPTH_COMPONENT, GL2.GL2.GL_FLOAT)
            
            worldCoords = gluUnProject(newXY[0], viewport[3] - newXY[1], windowZ[0][0], modelview, projection, viewport)

            this.pos = (worldCoords[0], worldCoords[1], this.pos[2])

    public void touchLeaves(agent_id=None)
#        if (agent_id
#            this.app.canvas.agentPublisher.agentActionCompleted('Agent', 'touch_leaves', [str(this.id), str(agent_id)])

        if (this.tree
            branch = 0
            for leaf in this.tree.leaves
                if (leaf ==  this.tree.leaves[branch] = None
                branch += 1
        h = float(this.app.canvas.orthoCoordWidth / this.app.canvas.aspectRatio)
        this.energy = (this.pos[1] + h/2)/h
        this.newctrlpoints()
        this.flying = true
        
    public void putOnTree(id=None, branch=-1)
        if (not id
            for oid, se in this.app.canvas.objects.items()
                if (isinstance(se, LifeTree)
                    id = oid
                    break
        if (not id
            Logger.warning("No tree found to put magic leaves on")
            return
        tree = this.app.canvas.objects[id]
        if (branch==-1
            branch = tree.getFreeBranch()
        if (branch==-1
            Logger.warning("No free tree branch found to put magic leaves on")
            return

        this.energy = 0.0
        this.flying = false

        tree.leaves[branch] = 
        this.tree = tree
        if (branch == 0
            dx = -0.47
            dy = 0.35
            this.orientation = 1.5
        else if (branch == 1
            dx = -0.15
            dy = 0.49
            this.orientation = 0.2
        else if (branch == 2
            dx = 0.19
            dy = 0.47
            this.orientation = -0.2
        else
            dx = 0.47
            dy = 0.26
            this.orientation = -0.5
        this.pos = (tree.pos[0]+tree.size*dx, tree.pos[1]+tree.size*dy, tree.pos[2])
                        
    public void remove(fadeOut, fadingFrames)
        super(MagicLeaves, ).remove(fadeOut, fadingFrames)            

