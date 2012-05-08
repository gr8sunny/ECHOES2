'''
Created on 24 Sep 2009

@author: cfabric
'''

import objects.EchoesObject
import agents.EchoesAgent
import objects.Bubbles
import objects.Plants
import objects.Environment
import echoes
import math
import Logger

def objectCollision(o1, o2, app):
    
    if not (hasattr(o1,"id") and hasattr(o2,"id")):
        raise TypeError, "objectCollision: not called with valid objects"
    if not (o1.id in app.canvas.objects and o2.id in app.canvas.objects):
        raise TypeError, "objectCollision: objects not yet in object list of canvas, aborting collision"

#    print "Object Collision:", o1.id, " X " , o2.id

    if not hasattr(app, "canvas"):
        raise TypeError, "objectCollision: not called with valid app instance"  
    
    # two bubbles are merged when colliding
    if isinstance(o1, objects.Bubbles.EchoesBubble) and isinstance(o2, objects.Bubbles.EchoesBubble) and o1.canMerge and o2.canMerge and o1.interactive and o2.interactive:                           
        newPos = ((o1.pos[0] + o2.pos[0]) / 2, (o1.pos[1] + o2.pos[1]) / 2, (o1.pos[2] + o2.pos[2]) / 2 )
        bubble = objects.Bubbles.EchoesBubble(app, False, fadeIn=True, fadingFrames=10)
        bubble.currentRegion = o1.currentRegion # in o1 and o2 region
        bubble.size = max(o1.size, o2.size) * 1.2
        bubble.setStartPos(newPos)
        bubble.floatingXY = o1.floatingXY and o2.floatingXY
        bubble.willBeReplaced = o1.willBeReplaced or o2.willBeReplaced
        bubble.mergedByChild = o1.beingDragged or o2.beingDragged
        bubble.id = app.canvas.addObject(bubble, {"type": "Bubble"})
        if o1.attachedToAgentId < 0 and o2.attachedToAgentId < 0:
            if o1.beingDragged or o2.beingDragged:
                app.canvas.agentPublisher.agentActionCompleted("User", "bubble_merge", [str(o1.id), str(o2.id)])
            else:
                app.canvas.agentPublisher.agentActionCompleted("None", "bubble_merge", [str(o1.id), str(o2.id)])
        else:
            app.canvas.agentPublisher.agentActionCompleted("Agent", "bubble_merge", [str(o1.id), str(o2.id)])
            if o1.attachedToAgentId < 0:
                agentId = o2.attachedToAgentId
            else:
                agentId = o1.attachedToAgentId
            app.canvas.agents[agentId].attachedObjectId = bubble.id
            bubble.attachedToAgentId = app.canvas.agents[agentId].id
            bubble.interactive = False
            bubble.moving = False
            app.canvas.agents[agentId].numBubblesMerged += 1

        if o1.willBeReplaced or o2.willBeReplaced:
            newBubble = objects.Bubbles.EchoesBubble(app, True)
            newBubble.floatingXY = True
        
        app.canvas.rlPublisher.objectPropertyChanged(str(o1.id), "bubble_merge", "")                 
        app.canvas.rlPublisher.objectPropertyChanged(str(o2.id), "bubble_merge", "")                 
        o1.remove()
        o2.remove()
    
    elif (isinstance(o1, objects.Environment.Pond) and isinstance(o2, objects.Plants.Pot)) or (isinstance(o1, objects.Plants.Pot) and isinstance(o2, objects.Environment.Pond)):
        if isinstance(o1, objects.Environment.Pond):
            pond = o1
            pot = o2
        else:
            pond = o2
            pot = o1
        if pond.canShrink: 
            pot.growFlower()                       
            if pot.flower and pot.flower.canGrow:
                pond.shrink()
                if pot.beingDragged and not(pot.publishGrowStarted):
                    app.canvas.agentPublisher.agentActionStarted('User', 'flower_grow', [str(pot.id), str(pot.flower.id), str(pond.id)])
                    pot.publishGrowStarted = True
                    pot.growPond = pond.id

    elif ((isinstance(o1, objects.Plants.Pot) and isinstance(o2, objects.Plants.Pot)) or 
        (isinstance(o1, objects.Environment.Basket) and isinstance(o2, objects.Plants.Pot)) or 
        (isinstance(o2, objects.Environment.Basket) and isinstance(o1, objects.Plants.Pot))):

        # return pots are not yet fully formed
        if not (hasattr(o1,"stack") and hasattr(o2,"stack")): return
        # return if in the same stack
        if o1.stack and o2.stack and o1.stack == o2.stack: return        
        # return if attached to avatar (stacking is invoked explicitly when caused by the avatar)
        if o1.avatarTCB or o2.avatarTCB: return
        # return if exactly in the same position (as with adding many and moving them around later)
        if o1.pos[0]==o2.pos[0] and o1.pos[1]==o2.pos[1] and o1.pos[2]==o2.pos[2]: return
        # take into account that they are not as tall as wide
        if (isinstance(o1, objects.Plants.Pot) and isinstance(o2, objects.Plants.Pot) and
            abs(o1.pos[1]-o2.pos[1]) > 0.65 * (o1.size + o2.size)): return 

        if o1.pos[1] > o2.pos[1]: 
            upper = o1 
            lower = o2
        else:
            upper = o2 
            lower = o1

        moving = False
        if o1.beingDragged or o1.falling:
            dx = o1.pos[0] - o2.pos[0]
            dy = o1.pos[1] - o2.pos[1]
            hratio = (o1.size + o2.size + 0.01)/math.hypot(dx, dy)
            newo1x = o2.pos[0] + dx*hratio
            newo1y = o2.pos[1] + dy*hratio
            o1.pos = [newo1x, newo1y, o1.pos[2]]
            moving = True
            o1.stopDrag()            
        if o2.beingDragged or o2.falling:
            if not moving:
                dx = o2.pos[0] - o1.pos[0]
                dy = o2.pos[1] - o1.pos[1]
                hratio = (o2.size + o1.size + 0.01)/math.hypot(dx, dy)
                newo2x = o1.pos[0] + dx*hratio
                newo2y = o1.pos[1] + dy*hratio
                o2.pos = [newo2x, newo2y, o2.pos[2]]
            moving = True
            o2.stopDrag() 

        if moving:
            # stacking - only if dragged by the user
            if abs(o1.pos[0] - o2.pos[0]) < (lower.size + upper.size) / 1.5:
                if isinstance(lower, objects.Plants.Pot) and not lower.flower:
                    lower.stackUp(upper)
                    # The user did it!
                    # with the introduction of falling, this is not certain anymore...
                    if isinstance(upper, objects.Plants.Pot):
                        app.canvas.agentPublisher.agentActionCompleted('User', 'stack_pot', [str(upper.id), str(lower.id)])
                    else:
                        app.canvas.agentPublisher.agentActionCompleted('User', 'stack_basket', [str(upper.id), str(lower.id)])

    elif isinstance(o1, objects.Plants.EchoesFlower) or isinstance(o2, objects.Plants.EchoesFlower):
        if isinstance(o1, objects.Plants.EchoesFlower) and isinstance(o2, objects.Plants.Pot):
            o1.inCollision = o2
            if not o1.beingDragged: return
            if o2.flower == o1: return
            o2.flower = o1
            if o1.basket: 
                o1.basket.removeFlower(o1)
                o1.basket = None
        elif isinstance(o2, objects.Plants.EchoesFlower) and isinstance(o1, objects.Plants.Pot):
            o2.inCollision = o1
            if not o2.beingDragged: return
            if o1.flower == o2: return
            o1.flower = o2
            if o2.basket: 
                o2.basket.removeFlower(o2)
                o2.basket = None
        elif isinstance(o1, objects.Plants.EchoesFlower) and isinstance(o2, objects.Environment.Basket):
            o1.inCollision = o2
            if not o1.beingDragged: return
            if o1 in o2.flowers: return
            o2.addFlower(o1)
            if o1.pot:
                o1.pot.flower = None
                o1.pot = None
        elif isinstance(o2, objects.Plants.EchoesFlower) and isinstance(o1, objects.Environment.Basket):
            o2.inCollision = o1
            if not o2.beingDragged: return
            if o2 in o1.flowers: return
            o1.addFlower(o2)
            if o2.pot:
                o2.pot.flower = None
                o2.pot = None
                
    elif ((isinstance(o1, objects.PlayObjects.Ball) and isinstance(o2, objects.Environment.Cloud)) or
          (isinstance(o2, objects.PlayObjects.Ball) and isinstance(o1, objects.Environment.Cloud))):
        if isinstance(o1, objects.PlayObjects.Ball):
            ball = o1
            cloud = o2
        else:
            ball = o2
            cloud = o1
        # set the collision object for the cloud 
        if cloud.hitBy == ball: return
        cloud.hitBy = ball
        # if the ball is dragged into the cloud by the child, stop the drag
        if ball.beingDragged:
            ball.stopDrag()
        # process colour change only when thrown by avatar or if flag is set that child can trigger the change of colour
        if not (ball.thrownByAvatar or ball.childCanChangeColour): return 
        cloud.colour = ball.colour
        ball.colour = cloud.b_colours[cloud.b_nextcolour]
        cloud.b_nextcolour = (cloud.b_nextcolour+1)%len(cloud.b_colours)

    elif ((isinstance(o1, objects.PlayObjects.Ball) and isinstance(o2, objects.Environment.Container)) or
          (isinstance(o2, objects.PlayObjects.Ball) and isinstance(o1, objects.Environment.Container))):
        if isinstance(o1, objects.PlayObjects.Ball):
            ball = o1
            container = o2
        else:
            ball = o2
            container = o1
        if ball in container.balls: return  # nothing to be done if the ball is already in the container
        if ball.avatarTCB: return           # don't process collision if ball is carried by avatar
        bx, by, bz = ball.pos
        cx, cy, cz = container.pos
        # bounce off
        if bx > (cx + container.size) or bx < (cx - container.size):
            if by > (cy + container.size):
                ball.velocity[1] *= -1*ball.elasticity
            ball.velocity[0] *= -1*ball.elasticity
            if ball.velocity[0] < ball.gravity:
                if bx > cx: ball.velocity[0] = 0.1
                else: ball.velocity[0] = -0.1 
            ball.stopDrag()
            return
        if by < (cy - container.size):
            ball.velocity[1] *= -1*ball.elasticity
            ball.stopDrag()
            return
        # if different colour, bounce off the top too        
        if container.colour != ball.colour:
            ball.velocity[1] *= -1*ball.elasticity
            if abs(ball.velocity[0]) < 0.01:
                ball.velocity[0] = 0.01
            ball.stopDrag()
            if abs(ball.velocity[1]) < ball.gravity:
                ball.velocity[1] = ball.gravity
        # otherwise, into container
        else:
            container.addBall(ball)
            if not ball.droppedByAvatar:
                app.canvas.agentPublisher.agentActionCompleted('User', 'container_ball', [str(container.id), str(ball.id)])
        
    elif isinstance(o1, objects.PlayObjects.Ball) and isinstance(o2, objects.PlayObjects.Ball):
        if o1.container and o2.container and o1.container == o2.container: return
        vo = (abs(o1.velocity[0]) + abs(o1.velocity[1]) + abs(o2.velocity[0]) + abs(o2.velocity[1]))/2 
        
        angle12 = math.atan2(o1.pos[1]-o2.pos[1], o1.pos[0]-o2.pos[0])
        o1.velocity[0] = vo*o1.elasticity*math.cos(angle12)
        o1.velocity[1] = -vo*o1.elasticity*math.sin(angle12) 
        o2.velocity[0] = -vo*o2.elasticity*math.cos(angle12)
        o2.velocity[1] = vo*o2.elasticity*math.sin(angle12)
        
        # introduce some x velocity if they are head on vertically
        if o1.velocity[0] == 0 and o2.velocity[0] == 0 and o1.velocity[1] != 0 and o2.velocity[1] != 0:
            o1.velocity[0] = -0.1
            o2.velocity[0] = 0.1
        
        o1.stopDrag()
        o2.stopDrag() 
          

        
                 
def agentObjectCollision(agent, object, app):

    if not (hasattr(agent,"id") and hasattr(object,"id")):
        raise TypeError, "agentObjectCollision: not called with valid objects/agents"

#    Logger.trace("info",  "Agent " + str(agent.id) + " Collision with Object " + str(object.id))

    if isinstance(agent, agents.PiavcaAvatars.EchoesAvatar):
        pass
