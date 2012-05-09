package renderingEngine.visual;

import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.*;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.awt.GLCanvas;

import renderingEngine.objects.EchoesObject;
import echoesEngine.ListenerManager;
import utils.Interfaces.*;

public class EchoesGLCanvas extends GLCanvas
{   
  public static EchoesGLCanvas theCanvas;
  public float orthoCoordWidth = 10;
  public float orthoCoordDepth = 100;
  public float aspectRatio = 1;
  public String scenario = "";
  public float[] frameSize; 
  public boolean renderPiavca = false;
  public Map<String, Object> piavcaAvatars = new HashMap<String, Object>();     
  
  private float scaleBias = 1;
  private float tracking = 1;
  private float[] cameraPos = {100, 100, 100};
  private boolean aspectFourByThree = true;
  private boolean dragging = false;                            
  private float[] clear_colour = {0,0,0,0};      
  private Object currentScene = null;
  private int sceneElementCount = 0;
  private Map<String, EchoesObject> sceneElements = new HashMap<String, EchoesObject>();
  private Integer objectCount = 0;
  private Map<String, EchoesObject> objects = new HashMap<String, EchoesObject>();
  private Integer agentCount = 0;
  private Map<String, Object> agents = new HashMap<String, Object>(); // EchoesAgents ?
  private Map<Integer, EchoesObject> drag = new HashMap<Integer, EchoesObject>();     //id's of drag events on objects 
  private Map<Integer, EchoesObject> bgtouch = new HashMap<Integer, EchoesObject>();   //id's of drag events on background  
  private float[] userList;          
  private Map<String, Object> agentActions =new HashMap<String, Object>(); 
  //private float actionLock = thread.allocate_lock(;)
  private boolean touchEnabled = false;
  private Annotator annotator = null;           
  private boolean publishScore = true;
  private float targetLightLevel  = (float)0.8;
  private float lightLevel = (float)0.8;
  private Frame frame;    
  private int frameCounter = 0;
  private boolean printFPS = false;
  
  private GL2 gl;
  private IRenderingListener rlPublisher;
  private IAgentListener agentPublisher;
  private  Graphics context;
  private int[] attribList = {GL.GL_RGBA, GL2.GL_DOUBLEBUFFER, 24};
  
  
  public void EchoesGLCanvas(Frame parent)
  {
    super(parent, -1, attribList);
    
    frame = parent;
    rlPublisher = (IRenderingListener)ListenerManager.GetInstance();
    agentPublisher = (IAgentListener)ListenerManager.GetInstance();
    
    enableEvents( AWTEvent.MOUSE_MOTION_EVENT_MASK | AWTEvent.MOUSE_EVENT_MASK |   AWTEvent.WINDOW_EVENT_MASK
        | AWTEvent.WINDOW_STATE_EVENT_MASK | AWTEvent.PAINT_EVENT_MASK  | AWTEvent.KEY_EVENT_MASK);   
   
    /*
    Bind(EVT_ERASE_BACKGROUND,OnEraseBackground)
    Bind(wx.EVT_SIZE,OnSize)
    Bind(wx.EVT_PAINT,OnPaint)
    Bind(wx.EVT_LEFT_DOWN,OnMouseDown)
    Bind(wx.EVT_LEFT_UP,OnMouseUp)
    Bind(wx.EVT_LEFT_DCLICK,OnMouseDoubleClick)
    Bind(wx.EVT_RIGHT_DOWN,OnMouseDown)
    Bind(wx.EVT_RIGHT_UP,OnMouseUp)
    Bind(wx.EVT_MIDDLE_DOWN,OnMouseDown)
    Bind(wx.EVT_MIDDLE_UP,OnMouseUp)
    Bind(wx.EVT_MOTION,OnMouseMotion)
    Bind(wx.EVT_CHAR,OnKeyboard)
    Bind(wx.EVT_IDLE,OnIdle)
      
     // Listen for the events from the touch-server too
    Bind(EVT_ECHOES_CLICK_EVENT,OnEchoesClick)
    Bind(EVT_ECHOES_POINT_DOWN_EVENT,OnEchoesPointDown)
    Bind(EVT_ECHOES_POINT_MOVED_EVENT,OnEchoesPointMoved)
    Bind(EVT_ECHOES_POINT_UP_EVENT,OnEchoesPointUp)
      
    Bind(EVT_CREATE_PIAVCA_AVATAR,OnCreatePiavcaAvatar)
    Bind(EVT_LOAD_SCENARIO,OnLoadScenario)
    Bind(EVT_END_SCENARIO,OnEndScenario)
    Bind(EVT_ADD_OBJECT,OnAddObject)
    Bind(EVT_SET_OBJECT_PROPERTY,OnSetObjectProperty)
    Bind(EVT_REMOVE_OBJECT,OnRemoveObject)
    Bind(EVT_START_ANNOTATOR,OnStartAnnotator)
    Bind(EVT_STOP_ANNOTATOR,OnStopAnnotator)
    Bind(EVT_ANNOTATOR_DRAWING,OnAnnotatorDrawing)
    Bind(EVT_ANNOTATOR_NO_DRAWING,OnAnnotatorNoDrawing)
     */
    InitGL();
  }
  
  public void addNotify() 
  { 
    super.addNotify();   
    context = this.getGraphics().create();
  } 
  
  public void setCurrent()
  {
    if (this.getContext() != null)
      setCurrent();                
  }
        
  public void setClearColour(float r, float g, float b, float a)
  {
    //clear_colour = (r,g,b,a);
  }
  
  public void projection()
  {
//    gl.gluPerspective( 45.0,aspectRatio, 0.5, 50.0 ); 
      gl.glOrtho(-orthoCoordWidth/2,orthoCoordWidth/2, 
          -orthoCoordWidth/2/aspectRatio, orthoCoordWidth/2/aspectRatio, 
          -orthoCoordDepth/2, orthoCoordDepth/2);
  }

  public void OnEraseBackground(PaintEvent event)
  {
      // Do nothing, to avoid flashing.
  }
        
  public void OnSize(Event event)
  {
    Dimension size = getSize();
    int width = (size.width >= 0) ? size.width : 0;
    int height = (size.height >= 0) ? size.height : 0;
    if (getContext() != null)
    {
      setCurrent();
      gl.glViewport(0, 0, width, height);
      if (width > 0 && height > 0)
      {
        aspectRatio = (float)(width/height);
        if (aspectFourByThree)
           aspectRatio =aspectRatio * 4/3;
        //Logger.trace("info",  "setting perspective and viewport with size " + str(width) + " x " + str(height) + "aspect ratio " + str(this.aspectRatio))
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        projection();
      }
    }
  }
  
  protected void processMouseEvent(MouseEvent event)
  {
    if (event.getID() == MouseEvent.MOUSE_PRESSED) 
      OnMouseDown(event);
    else if (event.getID() == MouseEvent.MOUSE_RELEASED) 
      OnMouseUp(event);
    //else if (event.getID() == MouseEvent.MOUSE_MOVED) 
   //   OnMouseMove(event);
  }
  
  protected void processMouseMotionEvent(MouseEvent event)
  {
    OnMouseMove(event);
  }
  
  public void OnMouseDown(MouseEvent event)
  {
    int x = event.getX();
    int y = event.getY();

    if (!this.touchEnabled)
    {
      if (annotator != null)
         annotator.startDrag(x, y);
      else
      {
        String id = getObjectAtPosition(x, y);
        if (id != null)
        {
           drag.put(event.getID(), id);
           objects.get(id).startDrag(x, y);
        }
      }
    }
  }

  public void OnMouseUp(MouseEvent event)
  {
   dragging = false;
    
    if (!touchEnabled)
    {
      if (annotator != null)
         annotator.stopDrag();
      else
      {
        if (drag.containsKey(event.getID()) && objects.containsKey(drag.get(event.getID())))
        {
          if (this.objects[this.drag[event.getID()]].locationChanged)
             agentPublisher.agentActionCompleted("User", "drag", drag[event.getID()].toString());
          objects[drag[event.getID()]].stopDrag();
        }
        if (drag.containskey(event.getID()))
            drag.remove(event.getID());
      }
    }
  }
  
  public void OnMouseMove(MouseEvent event)
  {
    if (event.Dragging())
    {
      int x;
      int y;
      //y = event.GetPosition();

      refresh(false);
      
      if (!touchEnabled)
      {
        if (this.Annotator != null)
           Annotator.drag((x,y))
        else
        {
          event.getID() = 0;
          if (drag.containsKey(event.getID())  && objects.containsKey(drag[event.getID()])
             objects[drag[event.getID()]].drag([x, y]);
        }
      }
    }
  }
  
  public void OnMouseDoubleClick(Event event)
  {
     int x, y = event.GetPosition();
     processClick(x, y);
  }
              
  public void processClick(int x, int y)
  {
    if (this.Annotator
       Annotator.click(x,y);
    else
    {
      id = getObjectAtPosition(x, y)
      if (id > -1)
      {
         objects[id].click("User");
         rlPublisher.userTouchedObject(str(id));
      }
      else
      {
        int id  = getAgentAtPosition(x,y);
        if (id > -1)
        {
           agents[id].click(this.getWorldCoord((x,y,0)));
           rlPublisher.userTouchedAgent(str(id));
        }
      }
      //menus =getMenus()
      //for menu in menus
      //    menu.click(this.getWorldCoord((x,y,0)));
    }
  }
  
  public float[] getRegionCoords(String key)
  {
    float w = orthoCoordWidth;
    float h = orthoCoordWidth /aspectRatio;
    float d = orthoCoordDepth;
 
    if ("all" == key) return {(-1*w/2,-1*h/2,-1*d/2), (w/2,h/2, d/2)};
    if ("all80" == key) return {(-0.8*w/2,-0.8*h/2,-0.8*d/2), (0.8*w/2,0.8*h/2, 0.8*d/2)}; 
    if ("all70" == key) return {(-0.7*w/2,-0.7*h/2,-0.7*d/2), (0.7*w/2,0.7*h/2, 0.7*d/2)}; 
    if ("all60" == key) return {(-0.6*w/2,-0.6*h/2,-0.6*d/2), (0.6*w/2,0.6*h/2, 0.6*d/2)}; 
    if ("all50" == key) return {(-0.5*w/2,-0.5*h/2,-0.5*d/2), (0.5*w/2,0.5*h/2, 0.5*d/2)}; 
    if ("left" == key) return {(-1*w/2,-1*h/2,-1*d/2), (-1*w/6, h/2, d/2)};
    if ("middle" == key) return {(-1*w/6, -1*h/2, -1*d/2), (w/6, h/2, d/2)};
    if ("right" == key) return {(w/6, -1*h/2, -1*d/2), (w/2, h/2, d/2)};
    if ("v-top" == key) return {(-1*w/2,0.3*h/2,-1*d/2), (w/2,h/2, d/2)}; 
    if ("v-middle" == key) return {(-1*w/2,-0.3*h/2,-1*d/2), (w/2,0.3*h/2, d/2)};
    if ("v-bottom" == key) return {(-1*w/2,-1*h/2,-1*d/2), (w/2,-0.3*h/2, d/2)};
    /*
    if ("3x3" == key) return {{(-1*w/2, h/6, -1*d/2), (-1*w/6, h/2, d/2)}        // top-left
            {(-1*w/6, h/6, -1*d/2), (w/6, h/2, d/2)},           // top-middle
            {(w/6, h/6, -1*d/2), (w/2, h/2, d/2)},              // top-right
            {(-1*w/2, -1*h/6, -1*d/2), (-1*w/6, h/6, d/2)},     // middle-left
            {(-1*w/6, -1*h/6, -1*d/2), (w/6, h/6, d/2)},        // middle-middle
            {(w/6, -1*h/6, -1*d/2), (w/2, h/6, d/2)},           // middle-right
            {(-1*w/2, -1*h/2, -1*d/2), (-1*w/6, -1*h/6, d/2)},  // bottom-left
            {(-1*w/6, -1*h/2, -1*d/2), (w/6, -1*h/6, d/2)},     // bottom-middle
            {(w/6, -1*h/2, -1*d/2), (w/2, -1*h/6, d/2)}};         // bottom-right        */
    if ("ground" == key) return {(-1*w/2,-0.7*h/2,-1*d/2), (w/2,-0.8*h/2, d/2)};
    if ("middle-ground" == key) return {(-1*w/2,-0.2*h/2,-1*d/2), (w/2,-0.7*h/2, d/2)};
    if ("sky" == key) return {(-1*w/2,0.95*h/2,-1*d/2), (w/2,0.8*h/2, d/2)};        
  }
            
  public void get3x3Neighbours(float [] region, float distance)
  {/*
    if (distance == 0)
        return region
    else
        return {{{1,4,3}, {2,5,8,7,6}},
                {{0,2,3,4,5}, {6,7,8}},
                {{1,4,5}, {0,3,6,7,8}},
                {{0,1,4,7,6}, {2,5,8}},
                {{0,1,2,3,5,6,7,8}, {}},
                {{1,2,4,7,8}, {0,3,6}},
                {{3,4,7}, {0,1,2,5,8}},
                {{6,3,4,5,8}, {0,1,2}},
                {{7,4,5}, {0,1,2,3,6}}}     */     
  }

  @Override
  public void paint(Graphics g)
  {
    setCurrent();
    OnDraw();
  }

  
      
  public void OnEchoesClick(Event event)
  {
    //Logger.trace("info",  "Click from ECHOES x = " + str(event.x) + "; y = " + str(event.y))
    int[] framePos = ScreenToClient(event.x, event.y);
    //Logger.trace("info",  "Location on screen " + str(framePos))
    processClick(framePos[0], framePos[1]);
  }

  public void OnEchoesPointDown(Event event)
  {
    Logger.trace("info",  "Point down from ECHOES id = " + str(event.getID()) + "; x = " + str(event.x) + "; y = " + str(event.y))
    framePos =ScreenToClient([event.x, event.y])
    Logger.trace("info",  "Location on screen " + str(framePos))
    if (this.Annotator != null)
       Annotator.startDrag(framePos)
    else
    {
      int id = getObjectAtPosition(framePos[0], framePos[1]);
      if (id > -1)
      {
        //Logger.trace("info",  "Touching object " + str(id) + " with gesture //" + str(event.getID()))
        rlPublisher.userTouchedObject(id.toString());
        drag[event.getID()] = id;
        objects[id].startDrag(framePos);
      }
      else
      {
        bgtouch[event.getID()] = framePos;
        agentPublisher.agentActionStarted("User", "touch_background", {framePos[0].toString(), framePos[1].toString()});
      }
    }
  }
  
  public void OnEchoesPointMoved(Event event)
  {
    int[] framePos = ScreenToClient(event.x, event.y);
    if (this.Annotator != null)
       Annotator.drag(framePos);
    else
    {
      if (event.getID() indrag anddrag[event.getID()] inobjects)
        objects[this.drag[event.getID()]].drag(framePos);
    }
  }
      
  public void OnEchoesPointUp(Event event)
  {
    Logger.trace("info",  "Point up from ECHOES id = " + str(event.getID()))
    if (this.Annotator
       Annotator.stopDrag()
    else        
    {
      if (event.getID() indrag anddrag[event.getID()] inobjects)
      {
        if (this.objects[this.drag[event.getID()]].locationChanged)
          agentPublisher.agentActionCompleted("User", "drag", {drag[event.getID()].toString()});
        objects[this.drag[event.getID()]].stopDrag();
      }
      if (event.getID() indrag)
          deldrag[event.getID()]
      if (event.getID() inbgtouch)
      {
         agentPublisher.agentActionCompleted("User", "touch_background", [str(this.bgtouch[event.getID()][0]), str(this.bgtouch[event.getID()][1])])
         delbgtouch[event.getID()];
      }
    }
  }
      
  public void OnKeyboard(Event event)
  {/*
    char c = event.KeyCode();
    
    if (c == 'f')
       fullscreen(notapp.fullscreenFlag);
    else if (c == 'a')
    {
      removed = false;
      for (id, object in sceneElements.items())
      {
        if (isinstance(object, environment.HelperElements.Axis)
        {
          object.remove();
          removed = true;
        }
      }
    }                 
    else if (c == 'g')
    {
      removed = false;
      for (id, object in sceneElements.items())
      {
        if (isinstance(object, environment.HelperElements.Grid3x3)
        {
          object.remove();
          removed = true;
        }
      }
      if (!removed)
        environment.HelperElements.Grid3x3(this.app);     
    }*/
  }
      
  public void OnCreatePiavcaAvatar(Event event)
  {/*
    if (event.type in piavcaAvatars)
    {
       piavcaAvatars[event.type].id = addAgent(this.piavcaAvatars[event.type], null);
       piavcaAvatars[event.type].startPostion();
       if (hasattr(evt, "pose"))
         piavcaAvatars[event.type].lookAtPoint(0, 3, 0);
       rlPublisher.agentAdded(str(this.agentCount), null);
    }
    else
    {
      // Create and auto-add the avatar
      if (event.type == "Paul")
         piavcaAvatars[event.type] = agents.PiavcaAvatars.Paul(event.autoadd, event.callback);
      else if (event.type == "Andy")
         piavcaAvatars[event.type] = agents.PiavcaAvatars.Andy(event.autoadd, event.callback);
      else
      {
        Logger.warning("Unknown avatar type " + event.name);
      }
    }*/
  }
      
  public void OnLoadScenario(Event event)
  {
    scenario = event.name;
    if (event.name == "Intro")
    {
      renderPiavca = false;
      environment.Backgrounds.Sky(this.app);
      EchoesBubble bubble = objects.Bubbles.EchoesBubble(true, fadeIn=true);
      bubble.colour = "green";
      float[]  m = getRegionCoords("middle");
      bubble.setStartPos(m[0], 0, 0);
      bubble.interactive = false;
      bubble.moving = false;
      bubble.size = 0.6  ;              
        //environment.Menu.UserMenu(true, true, userlist, pos=(0,0,0));  
    }
    else if (event.name == "BubbleWorld")
    {
       renderPiavca = false;
       score = 0;
       environment.Backgrounds.Sky(true, true);
    }
    else if (event.name.contains("Garden"))
    {
       bg = environment.Backgrounds.Garden(true);
       renderPiavca = true;
       score = 0;
       if (event.name == "GardenTask")
         bg.setImage("visual/images/GardenBackTask.png");
       else if (event.name == "GardenSocialGame")
         bg.setImage("visual/images/GardenBackSocialGame.png");
       else if (event.name == "GardenVeg")
         bg.setImage("visual/images/VegBackground.png");
    }
              
     currentScene = event.name;
     rlPublisher.scenarioStarted(event.name);
  }
      
  public void OnEndScenario(Event event)
  {
     scenario = "";
     if (event.name == "Intro" || event.name == "BubbleWorld" ||  event.name.contains("Garden"))
     {
        // Introduce a new transition bubble except in the Intro scene
        trans_bubble = null;
        if (event.name == "Intro")
        {
          for (Object o : objects.items())
          {
            if (o instanceof objects.Bubbles.EchoesBubble)
            {
              trans_bubbl e= o;
              break;
            }
          }
          for (Object oo : sceneElements.items())
          {
            if (ob instanceof environment.Menu.UserMenu)
              ob.remove(false);
          }
        }
                                    
        if (null == trans_bubble)
        {
          trans_bubble = objects.Bubbles.EchoesBubble(true, true, 100);
          trans_bubble.setStartPos(0,0,0);
        }
        trans_bubble.interactive = false;
        trans_bubble.colour = "red";
        trans_bubble.moving = true;
        trans_bubble.setTargetPos(this.orthoCoordWidth,orthoCoordWidth /aspectRatio,orthoCoordDepth);
        trans_bubble.removeAtTargetPos = true;
        trans_bubble.removeAction = "PublishScenarioEnded";
        trans_bubble.callback = event.callback;
        trans_bubble.removeActionArgs = event.name;                                
     }
          
     renderPiavca = false;
     currentScene = null;
  }
      
  public void OnAddObject(Event event)
  {
    if (event.type == "Flower")
      objects.Plants.EchoesFlower(true, false, event.callback);
    else if (event.type == "Bubble")
      objects.Bubbles.EchoesBubble(true, true, event.callback);
    else if (event.type == "Ball")
      objects.PlayObjects.Ball(true, true, event.callback);
    else if (event.type == "IntroBubble")
    {
      EchoesBubble b = objects.Bubbles.EchoesBubble(true, true, event.callback);
      b.colour = "green";
      if (this.currentScene == "BubbleWorld")
          b.willBeReplaced = true;
      else
          b.willBeReplaced = false;
      b.setStartPos(0,5,0.5);
    }
    else if (event.type == "Pot")
      objects.Plants.Pot(true, true, event.callback);
    else if (event.type == "Ball")
      objects.PlayObjects.Ball(true, true, event.callback);
    else if (event.type == "Pond")
      objects.Environment.Pond(true, true, event.callback);
    else if (event.type == "Cloud")
      objects.Environment.Cloud(true, true, event.callback);
    else if (event.type == "Container")
      objects.Environment.Container(true, true, event.callback);
    else if (event.type == "Sun")
      objects.Environment.Sun(true, true, event.callback);
    else if (event.type == "LifeTree")
      objects.Plants.LifeTree(true, true, event.callback);
    else if (event.type == "MagicLeaves")
      objects.Plants.MagicLeaves(true, true, event.callback);
    else if (event.type == "Basket")
      objects.Environment.Basket(true, true, event.callback);
    else if (event.type == "Shed")
      objects.Environment.Shed(true, true, event.callback);
  }
      
  public void OnSetObjectProperty(Event event)
  {/*
      if (int(event.objId) inapp.canvas.objects
          o =app.canvas.objects[int(event.objId)]
          // Generic properties
          if (event.propName == "Pos"
              pos = str(event.propValue)
              if (pos.startswith("(") pos = pos[1]
              if (pos.endswith(")") pos = pos[-1]
              pos = pos.split(",")
              try
                  x = float(pos[0])
              except ValueError
                  Logger.warning("setObjectProperty Invalid coordinate for x")
                  return
              try
                  y = float(pos[1])
              except ValueError
                  try
                      f =getRegionCoords(pos[1].strip())
                      y = f[1][1]
                  except ValueError
                      Logger.warning("setObjectProperty Invalid coordinate for y")
                      return
              try
                  z = float(pos[2])
              except ValueError
                  if (pos[2] == "front" z = 1
                  else if (pos[2] == "back" z = -1
                  else z = 0

              o.pos = [x,y,z]
          
          if (event.propName == "Size" and hasattr(o, "size")
              o.size = float(event.propValue)                
          
          if (event.propName == "Colour" and hasattr(o, "colour")
              o.colour = str(event.propValue)
              
          if (event.propName == "Interactive"
              o.interactive = (event.propValue == "true")
                      
          //Properties for Bubbles exposed to API
          if (isinstance(o, objects.Bubbles.EchoesBubble)
              if (event.propName == "Size" 
                  if (event.propValue == "Bigger"
                      o.grow()
              else if (event.propName == "Replace" 
                  o.willBeReplaced = (event.propValue == "true")
          //Properties for Flowers exposed to API    
          else if (isinstance(o, objects.Plants.EchoesFlower)
              if (event.propName == "MoveToBasket"
                  try 
                      id = int(event.propValue)
                  except 
                      id = None 
                  o.moveToBasket(id)
              else if (event.propName == "IntoBubble"
                  o.intoBubble()
              else if (event.propName == "IntoBall"
                  o.intoBall()
              else if (event.propName == "CanTurnIntoBall" 
                  o.canTurnIntoBall = (event.propValue == "true")
              else if (event.propName == "CanTurnIntoBubble" 
                  o.canTurnIntoBubble = (event.propValue == "true")
              else if (event.propName == "ChildCanTurnIntoBall" 
                  o.childCanTurnIntoBall = (event.propValue == "true")
              else if (event.propName == "ChildCanTurnIntoBubble" 
                  o.childCanTurnIntoBubble = (event.propValue == "true")
              else if (event.propName == "GrowToSize" 
                  if (event.propValue == "Max"
                      o.growToSize = o.maxSize
                  else
                       o.growToSize = float(event.propValue)
          //Properties for Pots exposed to API
          else if (isinstance(o, objects.Plants.Pot)
              if (event.propName == "GrowFlower"
                  o.growFlower()
              if (event.propName == "StackIntoTree" and o.stack
                  o.stack.intoTree()
          //Properties for Stacks exposed to API
          else if (isinstance(o, objects.Plants.Stack)
              if (event.propName == "StackIntoTree"
                  o.intoTree()
          //Properties for Ball exposed to API    
          else if (isinstance(o, objects.PlayObjects.Ball)
              if (event.propName == "BounceWithinScene"
                  o.bounceWithinScene = (event.propValue == "true")
              if (event.propName == "ChildCanChangeColour"
                  o.childCanChangeColour = (event.propValue == "true")
          //Properties for Container exposed to API    
          else if (isinstance(o, objects.Environment.Container)
              if (event.propName == "Reward"
                  o.reward(event.propValue)
          //Properties for Basket exposed to API    
          else if (isinstance(o, objects.Environment.Basket)
              if (event.propName == "PlayFanfare"
                  o.playFanfare()
          //Properties for Cloud exposed to API    
          else if (isinstance(o, objects.Environment.Cloud)
              if (event.propName == "CanRain"
                  o.canRain = (event.propValue == "true")
      else
          Logger.warning("setObjectProperty was called with object which is not in the objects list")*/
  }
              
  public void OnRemoveObject(Event event)
  {
    if (objects.containsKey(event.id))
    {
        Object o = objects.get(event.id);
        //o.remove();
    }
  }
  
  public void OnStartAnnotator(Event event)
  {
    if (null == annotator)
       annotator = new Annotator();
  }
      
  public void OnStopAnnotator(Event event)
  {
    if (Annotator != null)
    {
       Annotator.done();
       Annotator = null;
    }
  }
    
  public void OnAnnotatorDrawing(Event event)
  {
    if (Annotator != null)
       Annotator.drawingFeature(true);
  }
      
  public void OnAnnotatorNoDrawing(Event event)
  {
    if (Annotator != null)
       Annotator.drawingFeature(false);
  }
      
  public void OnDraw()
  {
   draw();
  }
  
  public void OnIdle(Event event)
  {
    draw();
    event.RequestMore();
  }

  public void InitGL()
  {
    gl.glMatrixMode(GL2.GL_MODELVIEW);
    gl.glClearDepth(1.0);
    
    gl.glEnable(GL2.GL_DEPTH_TEST);
    gl.glEnable(GL2.GL_NORMALIZE);

    gl.glEnable(GL2.GL_COLOR_MATERIAL);
    gl.glEnable(GL2.GL_BLEND);
    gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

    gl.glShadeModel(GL2.GL_SMOOTH);
    gl.glHint(GL2.GL_LINE_SMOOTH_HINT, GL2.GL_NICEST);
    gl.glEnable(GL2.GL_LINE_SMOOTH);

    gl.glEnable(GL2.GL_LIGHTING);
    gl.glEnable(GL2.GL_LIGHT0);

    setLight(0.8);
    targetLightLevel = 0.8;
                  
    lineWidthRange = glGetIntegerv(GL2.GL_LINE_WIDTH_RANGE);
  }
                                             
  public void setLight(float brightness)
  {
     lightLevel = brightness;
       
     // Create light components
     float al = brightness;
     float dl = max(0, brightness-0.2);
     float sl = max(0, brightness-0.8);
     ambientLight = [ al, al, al, 1.0 ];
     diffuseLight = [ dl, dl, dl, 1.0 ];
     specularLight = [ sl, sl, sl, 1.0 ];
       
     // Assign created components to GL2.GL_LIGHT0
     gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, ambientLight);
     gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, diffuseLight);
     gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, specularLight);
  }
             
  public void clearScene(boolean quick)
  {
    if (hasattr("background") andbackground
       background.remove();
    for (id,object in sceneElements.items())
      object.remove(!quick);
    for(id,object in objects.items())
    {
      object.interactive = false;
      object.remove(!quick);
    }
  }
  
  public void draw()
  {
    if (this.printFPS)
    {
       frameCounter += 1
      if (time.time() -last_time >= 1)
      {
         float current_fps = frameCounter / (time.time() -last_time);
          //print current_fps, 'fps'
         frameCounter = 0;
         last_time = time.time();
      }
    }
    if (this.targetLightLevel != lightLevel)
    {
       float newlight = lightLevel + (this.targetLightLevel -lightLevel)/100;
       if (Math.abs(newlight-this.targetLightLevel) < 0.01)
         newlight = targetLightLevel);
       setLight(newlight);
    }

    // clear color and depth buffers
    gl.glClearColor(this.clear_colour[0],clear_colour[1],clear_colour[2],clear_colour[3]);
    gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
    
    gl.glMatrixMode(GL2.GL_MODELVIEW);
    gl.glLoadIdentity();
    gl.gluLookAt (this.cameraPos[0],cameraPos[1],cameraPos[2], 0.0, 0.0, 0.0, 0.0, 1.0, 0.0);

    // position of the light needs to be set after the projection
    gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, [-4, 2.0, 10.0, 1.0 ]);
    
   renderBackground ();
   renderEnvironment();          
   renderObjects();
   renderAgents();

    if (this.renderPiavca)
    {
      Piavca.Core.getCore().timeStep(); 
      Piavca.Core.getCore().prerender();
      Piavca.Core.getCore().render();
    }
    
    if (this.Annotator)
     Annotator.render();

    SwapBuffers();
  }
      
  public void addBackground(Object object)
  {
     background = object;
  }
          
  public void renderBackground()
  {
    if (hasattr("background") andbackground && hasattr(this.background, "render"))
       background.render();
  }
              
  public void removeBackground()
  {
     background = null;
  }

  public void addSceneElement(Object object)
  {
    sceneElementCount =sceneElementCount + 1;
    sceneElements[this.sceneElementCount] = object;
    returnsceneElementCount;
  }
          
  public void removeSceneElement(String id)
  {
    sceneElements.remove(id);
  }

  public void renderEnvironment()
  {
    for (String id : sceneElements.keys())
    {
      Object object = sceneElements[id];
      if (hasattr(object, "render"))
      {
          gl.glPushName (int(id));
          object.render();
          gl.glPopName ();
      }
    }
  }
  
  public void getMenus()
  {/*
      menus = []
      for id, object insceneElements.items()
          if (isinstance(object, environment.Menu.UserMenu)
              menus.append(object)
      return menus;*/
  }
      
  public int addObject(Object object, Map<String, String> props)
  {
     objectCount++;
     objects.put(objectCount.toString(), object);
     rlPublisher.objectAdded(objectCount.toString(), props);
     return objectCount;
  }
      
  public void removeObject(String id)
  {
     objects.remove(id);
     rlPublisher.objectRemoved(id);
  }

  public void renderObjects(boolean hitTest)
  {
    for (String id : objects.keySet())
    {
      Object object = objects.get(id);
      if (hasAttr(object, "render"))
      {
        gl.glPushName (Integer.parseInt(id));
        object.render(hitTest);
        gl.glPopName ();
      }
    }
    
      //objectsToTest = dict(filter(lambda item hasattr(item[1], "objectCollisionTest") and item[1].objectCollisionTest==true,objects.iteritems()))
      //collisions = hitTest(objectsToTest)               
      //for (pair in collisions)                
      //  objectCollision(pair[0], pair[1],app);
  }
  
  public int addAgent(Object agent, Map<String, String> props)
  {
    //renderPiavca = true
    agentCount++;
    agents.put(agentCount.toString(), agent);
    rlPublisher.agentAdded(agentCount.toString(), agent.props);
    return agentCount;
  }
      
  public void removeAgent(String id)
  {
    agents.remove(id);
    // if (len(this.agents) == 0
        //renderPiavca = false
    rlPublisher.agentRemoved(id);
  }
          
  public void renderAgents()
  {/*
    for id inagents.keys()
        agent =agents[id]
        if (hasattr(agent, "render")
            gl.glPushName (int(id))
            agent.render()
            gl.glPopName ()
    
    objectsToTest = dict(filter(lambda item hasattr(item[1], "agentCollisionTest") and item[1].agentCollisionTest==true,objects.iteritems()))
    agentsToTest = dict(filter(lambda item hasattr(item[1], "collisionTest") and item[1].collisionTest==true,agents.iteritems()))
    collisions =agentHitTest(agentsToTest, objectsToTest)       
                    
    for pair in collisions                 
        agentObjectCollision(pair[0], pair[1],app)*/
  }

  /*
  public void agentActionStarted(Object callback, String unique_actionid, String agentId, action, details)
  {
     actionLock.acquire()
      // pass
      Logger.trace("info",  "agentActionStarted " + str(action) + " " + str(callback))
      if (hasattr("agentPublisher")
         agentPublisher.agentActionStarted(agentId, action, details)

     agentActions[unique_actionid] = agents.EchoesAgent.AgentAction(callback, agentId, action, details)
     actionLock.release()
  }
  
  public void agentActionCompleted(String unique_actionid, boolean success)
  {
     actionLock.acquire()
      // pass
      Logger.trace("info",  "agentActionCompleted (" + str(unique_actionid) + ") " + str(success))
      if (hasattr("agentPublisher") and unique_actionid inagentActions
          // if (this.agentActions[unique_actionid].callback
              // Logger.trace("info",  "calling ice_response on callback " + str(this.agentActions[unique_actionid].callback)) 
              //agentActions[unique_actionid].callback.ice_response(success)
          try
              if (success
                 agentPublisher.agentActionCompleted(this.agentActions[unique_actionid].agentId,agentActions[unique_actionid].action,agentActions[unique_actionid].details)
              else
                 agentPublisher.agentActionFailed(this.agentActions[unique_actionid].agentId,agentActions[unique_actionid].action,agentActions[unique_actionid].details, "Probably a combined action failed, because the object was moved while the agent was walking there")
          except
              Logger.warning("Incomplete information in completed agent action")
          delagentActions[unique_actionid]
      else
          Logger.warning("Agent action completed was called with non-existing id " + str(unique_actionid))
     actionLock.release();
  }
  */
  
  public Set<String> getObjectIds()
  {
    return objects.keySet();
  }
          
  public String getObjectAtPosition(int x, int y)
  {
    //Logger.trace("info",  "Looking for objects at " + x + "," + y)

    // Based on code from http//nehe.gamedev.net/data/lessons/lesson.asp?lesson=32
    
    // Get the current viewport
    viewport = glGetIntegerv(GL2.GL_VIEWPORT);
    
    // Prepare a buffer to hold the results
    gl.glSelectBuffer (100);

    // Put OpenGL into selection mode, and reset the name stack
    gl.glRenderMode(GL2.GL_SELECT);
    gl.glInitNames();
    
    // Only draw in the area under the mouse click
    gl.glMatrixMode(GL2.GL_PROJECTION);
    gl.glPushMatrix();
    gl.glLoadIdentity();
    gl.gluPickMatrix(x, viewport[3] - y, 1.0, 1.0, viewport);
    
    // Multiply the perspective matrix by the pick matrix to restrict the drawing area
   projection();
    
    // Switch to normal mode, render the target to the buffer, and do some further mapping
    gl.glMatrixMode(GL2.GL_MODELVIEW);
   renderObjects(true);
    gl.glMatrixMode(GL2.GL_PROJECTION);
    gl.glPopMatrix();;
    gl.glMatrixMode(GL2.GL_MODELVIEW);

    // Switch back to normal mode and see whether we hit anything
    records = glRenderMode(GL2.GL_RENDER);
    String hitObject = null;
    // Changed mode always take the "top" object instead of the "nearest" one (unless it's the shed)
//            if (len(records) > 0
//                hitObject = records[len(records)-1].names[0]
//                if (this.objects[hitObject].props['type'] == "Shed" and len(records) > 1
//                    hitObject = records[len(records)-2].names[0]
    distance = 1000;
    for ( record : records)
    {
        if (record.near < distance)
        {
            distance = record.near;
            hitObject = record.names[0];
        }
    }
            
    return hitObject;
  }
      
  public String getAgentAtPosition(int x, int y)
  {/*
      Logger.trace("info",  "Looking for agents at " + str(x) + "," + str(y))
      rx, ry, rz =getWorldCoord((x,y))
      for id inagents.keys()
          agent =agents[id]
          if (hasattr(agent, "avatar")
              c = agent.getXYContour()
              if (c[0][0] < rx and c[2][0] > rx and c[0][1] < ry and c[1][1] > ry
                  return id */
      return "";
  }
      
  public float [] getScreenCoord(float[] pos)
  {
    GL2 gl = getGL().getGL2();
    int[] viewport = new int[4];
    double[] modelview = new double[16];
    double[] projection = new double[16];
    
    gl.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, modelview, 0);
    gl.glGetDoublev(GL2.GL_PROJECTION_MATRIX, projection, 0);
    gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport, 0);

    //return getGLU().gluProject(pos[0], pos[1], pos[2], model, projection, viewport);
    return null;
  }
                  
  public void getWorldCoord(float[] pos)
  {
    GL2 gl = getGL().getGL2();
    int[] viewport = new int[4];
    double[] modelview = new double[16];
    double[] projection = new double[16];
    
    gl.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, modelview, 0);
    gl.glGetDoublev(GL2.GL_PROJECTION_MATRIX, projection, 0);
    gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport, 0);
    
    //wz = glReadPixels(pos[0],pos[0],1,1,GL2.GL_DEPTH_COMPONENT,GL2.GL_FLOAT)[0][0]
    //unprojected = gluUnProject(pos[0], viewport[3]-pos[1], wz, model, projection, viewport);;
    //return unprojected;
  }

  public void drawBezier(float[][]ctrlPoints, boolean drawPoints, int numStrips)
  {
    GL2 gl = getGL().getGL2();
    gl.glMap1f(GL2.GL_MAP1_VERTEX_3, 0, 1, 0, 0, ctrlPoints, 0);
    gl.glEnable(GL2.GL_MAP1_VERTEX_3);
    gl.glBegin(GL2.GL_LINE_STRIP);
    for (int i = 0; i < numStrips; i++)
        gl.glEvalCoord1f((float)i/numStrips);
    gl.glEnd();
    if (drawPoints)
    {
      gl.glPointSize(5);
      gl.glColor3f(1, 1, 0);
      gl.glBegin(GL2.GL_POINTS);
      for (float [] point : ctrlPoints)
        gl.glVertex3f(point[0], point[1], point[2]);
      gl.glEnd();
    }
  }     
              
  public Map<Object, Object> hitTest(Map<String, Object> things, Map<String, Object> otherThings)
  {
    Map<Object, Object> collisions= new HashMap<Object, Object>();
    if (null == otherThings)
    {
      for (String id1 : things.keySet())
      {
        for (String id2 : things.keySet())
        {
          if (id2.compareToIgnoreCase(id1) < 0)
              continue;
          if (id1 != id2)
          {
            Object o1 = things.get(id1);
            Object o2 = things.get(id2);
            float deltaX = o2.pos[0] - o1.pos[0];
            float deltaY = o2.pos[1] - o1.pos[1];
            float deltaZ = o2.pos[2] - o1.pos[2];
            if (isinstance(o1, objects.Plants.EchoesFlower) || isinstance(o2, objects.Plants.EchoesFlower))
            {
              if (isinstance(o1, objects.Plants.EchoesFlower))
              {
                flower = o1;
                other = o2;
              }
              else
              {
                flower = o2;
                other = o1;
              }
              deltaY = flower.pos[1] - other.pos[1];
              if (abs(deltaX) <= other.size && deltaY < (other.size + flower.stemLength) && deltaY > 0)
                collisions.add(o1, o2);
            }
            else
            {
              distanceSquared = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
              minDistance = o1.size + o2.size;
              if (distanceSquared < minDistance * minDistance)
                collisions.add(o1,o2);
            }
          }
        }
      }
    }
    else
    {
      for (Object o1 : things)
      {
        for (Object o2 : otherThings)
        {
          deltaX = o2.pos[0] - o1.pos[0];
          deltaY = o2.pos[1] - o1.pos[1];
          deltaZ = o2.pos[2] - o1.pos[2];
          distanceSquared = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
          minDistance = o1.size + o2.size;
          if (distanceSquared < minDistance * minDistance)
              collisions.add(o1,o2);
        }
      }
    }
    return collisions;
  }
      
  public void agentHitTest(Map<String, Object> agentsToTest, Map<String, Object> objectsToTest)
  {/*
      noAvatars = dict()
      collisions = []
      for aid, agent in agentsToTest.items()
          if (isinstance(agent, agents.PiavcaAvatars.EchoesAvatar)
              bb = agent.getXYContour()
              for oid, object in objectsToTest.items()
                  if (object.pos[0] > bb[0][0] and object.pos[0] < bb[2][0] and object.pos[1] > bb[0][1] and object.pos[1] < bb[1][1]
                      if (object.beingDragged object.draggedOverAgent = agent.id
                      else object.draggedOverAgent = None
                      object.overAgent = agent.id
                      collisions.append([agent, object])
                  else
                      object.draggedOverAgent = None
                      object.overAgent = None                            
          else
              noAvatars[aid] = agent
      if (len(noAvatars) > 0
          collisions +=hitTest(noAvatars, objectsToTest)
      return collisions      */                        
  }
      
  public void saveScreenshot(String name, String path)
  {/*
     // Read in the screen information in the area specified """
      gl.glFinish();
      gl.glPixelStorei(GL2.GL_PACK_ALIGNMENT, 4);
      gl.glPixelStorei(GL2.GL_PACK_ROW_LENGTH, 0);
      gl.glPixelStorei(GL2.GL_PACK_SKIP_ROWS, 0);
      gl.glPixelStorei(GL2.GL_PACK_SKIP_PIXELS, 0);
  
      data = glReadPixels(0, 0,size[0],size[1], GL2.GL_RGBA, GL2.GL_UNSIGNED_BYTE);
      
      if (null == name)
        name = datetime.datetime.now().strftime("%Y-%m-%d_%H.%M");
      if (null ==  path)
        path = os.getcwd();
  
      im = PIL.Image.fromstring("RGBA",size, data);
      im.rotate(180).transpose(PIL.Image.FLIP_LEFT_RIGHT).save(path + "/" + name + ".png","PNG");*/
  }
}
        