/**
 * 
 */
package stateManager;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JOptionPane;
import org.jdom.Element;
import utils.Interfaces.*;
import utils.Logger;

/**
 * @author Mary Ellen Foster
 *
 */
public class ScriptedAction implements Runnable {
    
    /** The internal ID of this action */
    private String actionId;
    
    /** List of all actions, for constructor purposes */
    private static final Map<String, ScriptedAction> actions = new HashMap<String, ScriptedAction>();
    
    public static ScriptedAction getAction(String id) {
        return actions.get(id);
    }
    
    /** Possible world actions to take */
    private enum WorldActionType {
        // Actions for the agent to do (some composite)
        LookAround, LookAtObject, LookAtChild, PointAt, LookAndPointAt, PointAgain, LookAgain, Say, Wave, StandAndSay, DoNothing,
        
        // Actions to change the state of the world
        PickFlower, EndTrial,
        
        // Special action
        TeacherPrompt, ResetPosture
    }
    
    /** The action represented by this step */
    private class WorldAction {
        WorldActionType type;
        String arg;
        
        public String toString() {
            return type.toString() + "(" + arg + ")";
        }
    }
    
    private WorldAction worldAction;
    
    /** Things that the user might be expected to do. */
    private enum UserActionType {
        LookAt, Touch, Timeout
    }
    
    private class UserAction {
        UserActionType type;
        String arg;
        
        public UserAction(String spec) {
            String[] pieces = spec.split("[()]");
            type = UserActionType.valueOf(pieces[0]);
            if (pieces.length > 1) {
                arg = pieces[1];
            }
        }

        public UserAction(UserActionType type, String arg) {
            this.type = type;
            this.arg = arg;
        }

        @Override
        public boolean equals (Object obj) {
            if (obj == null) return false;
            try {
                UserAction a2 = (UserAction)obj;
                return a2.type == type && (arg == null ? a2.arg == null : arg.equals(a2.arg));
            } catch (ClassCastException ex) {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return toString().hashCode();
        }
        
        public String toString() {
            return type + "(" + arg + ")";
        }
    }
  
  /** How long to wait before moving to the timeout action. */
  private long timeout;
  /** The object that we want them to look at/touch/etc */
  private static String targetId;
  private static String agentId;
	private static String childName;
  private Map<UserAction, String> nextActions;
  private Timer timer;
  private Random rand;
  private AtomicBoolean active;
  private IRenderingEngine rePrx;
  private ActionController controller;
	private TimerTask timeoutTask;
    
    public ScriptedAction(String fileName, Element element, IRenderingEngine rePrx, ActionController controller) 
    {
        this.rePrx = rePrx;
        this.controller = controller;
        this.active = new AtomicBoolean();
        this.rand = new Random();
        
        // Get the ID
        actionId = fileName + "#" + element.getAttributeValue("id");
        synchronized(actions) {
            actions.put(actionId, this);
        }
        
        // Parse the rest of the stuff
        worldAction = new WorldAction();
        worldAction.type = WorldActionType.valueOf(element.getAttributeValue("type"));
        if (element.getAttribute("details") != null) {
            worldAction.arg = element.getAttributeValue("details");
        }
        timeout = Long.parseLong(element.getAttributeValue("timeout"));
        
        nextActions = new HashMap<UserAction, String>();
        for (Object obj : element.getChildren("response")) {
            Element child = (Element)obj;
            UserAction ua = new UserAction(child.getAttributeValue("context"));
            String responseId = fileName + child.getAttributeValue("action");
            nextActions.put(ua, responseId);
        }
    }
    
    public static void cancelAll() {
    	for (ScriptedAction action : actions.values()) {
			action.cancel();
    	}
    }
    
    private void cancel() {
    	if (active.getAndSet(false)) {
    		ActionController.addHistory("Cancelled " + this);
    	}
    }
    
    private void log (String content) {
        // Application.communicator().getLogger().trace("info", content);
        ActionController.addHistory(content);
    }
    
    private void warning (String content) {
        Logger.LogWarning(content);
        ActionController.addHistory("WARNING: " + content);
    }
    
    @Override
    public String toString() {
        return this.worldAction.toString();
    }
    
    public static void setTargetId(String _targetId) {
        targetId = _targetId;
    }
    
    public static void setAgentId(String _agentId) {
        agentId = _agentId;
    }
    
    public static void setChildName(String _childName) {
    	childName = _childName;
    }
    
    public synchronized void processAction (UserAction action) {
    	if (active.get()) {
	        log("Processing user action " + action);
	        String nextId = nextActions.get(action);
	        if (nextId != null) {
	            ScriptedAction nextAction = actions.get(nextId);
	            if (nextAction == null) {
	                warning("Action " + nextId + " not found!");
	                return;
	            }
	            if (active.getAndSet(false)) 
	            {
	                //if (eventTopic != null) 
	                //    eventTopic.unsubscribe(eventListenerPrx);
	                
	                if (timeoutTask != null) {
	                	timeoutTask.cancel();
	                }
	                new Thread(nextAction).start();
	            }
	        } else if (this.worldAction.type == WorldActionType.EndTrial) {
	        	// Unsubscribe regardless
	        	active.set(false);
	        	//eventTopic.unsubscribe(eventListenerPrx);
	        }
    	}
    }
    
    public synchronized void run() 
    {
        log("Executing action " + this);
        active.set(true);
        
        /* 
        // Listen for interesting user gaze and touch events from fusion
        IEventListener eventListener = new IEventListener() {

            public void userGazeEvent(String details, long msec) {
            	controller.showGaze(details);
                processAction (new UserAction(UserActionType.LookAt, processDetails (details)));
            }

            public void userTouchEvent(String objId) {
                processAction (new UserAction (UserActionType.Touch, processDetails (objId)));
            }
            
            private String processDetails (String details) {
                if (targetId != null && details != null && !details.equals("AGENT") && !details.equals("NONE")) {
                    return targetId.equals(details) ? "TARGET" : "OTHER";
                } else {
                    return details;
                }
            }

    			@Override
    			public void userAction(UserActionType action) {
    				// TODO Auto-generated method stub
    				
			}
        };
       
        if (adapter != null && eventTopic != null) {
            // Subscribe to the event topic
            eventListenerPrx = adapter.addWithUUID(eventListener);
            try {
                eventTopic.subscribeAndGetPublisher(new HashMap<String, String>(), eventListenerPrx);
            } catch (AlreadySubscribed e) {
                e.printStackTrace();
            } catch (BadQoS e) {
                e.printStackTrace();
            }
        }
       */ 
        // Now do the world action -- only respond to actions that start during or after this action
        List<String> args = new LinkedList<String>();
        if (worldAction.arg != null) {
        	String newArg = worldAction.arg.replaceAll("CHILD", childName);
        	if (newArg.equals("thankyou.wav") && rand.nextBoolean()) {
        		newArg = "welldone.wav";
        	}
        	if (newArg.equals("TARGET")) {
        		args.add(targetId);
        	} else {
        		args.add(newArg);
        	}
        }

        switch (worldAction.type) {
        case DoNothing:
        	break;
        	
        case LookAtObject:
        case PointAt:
        case LookAtChild:
        case Say:
        case Wave:
            rePrx.executeAction (agentId, worldAction.type.toString(), args);
            break;
            
        case PickFlower:
        	/*
        	rePrx.executeAction(agentId, "ResetPosture", new LinkedList<String>());
        	pause();
        	*/
        	rePrx.executeAction(agentId, "PickFlower", args);
        	pause();
        	if (active.get()) {
        		List<String> vaseArg = new LinkedList<String>();
        		vaseArg.add("VASE");
        		rePrx.executeAction(agentId, "LookAtObject", vaseArg);
        	}
        	break;
            
        case LookAround:
        	// A few looks around
        	List<String> whistleArg = new LinkedList<String>();
        	whistleArg.add("True");
        	// rePrx.executeAction(agentId, "Whistle", whistleArg);
        	List<String> coords = new LinkedList<String>();
        	coords.add("");
        	coords.add("3");
        	coords.add("0");
        	for (int i = 0; i < 2; i++) {
        		coords.set(0, String.valueOf(5 - rand.nextInt(10)));
            	rePrx.executeAction(agentId, "LookAtPoint", coords);
            	try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
        	}
        	whistleArg.set(0, "False");
        	// rePrx.executeAction(agentId, "Whistle", whistleArg);
        	break;
            
        case LookAndPointAt:
            rePrx.executeAction(agentId, "LookAtObject", args);
            pause();
            if (active.get()) 
            	rePrx.executeAction(agentId, "PointAt", args);
            break;
            
        case PointAgain:
            rePrx.executeAction(agentId, "ResetPosture", new LinkedList<String>());
            pause();
            if (active.get())
            	rePrx.executeAction(agentId, "LookAtObject", args);
            pause();
            if (active.get())
            	rePrx.executeAction(agentId, "PointAt", args);
            break;
            
        case LookAgain:
            rePrx.executeAction(agentId, "ResetPosture", new LinkedList<String>());
            pause();
            if (active.get())
            	rePrx.executeAction(agentId, "LookAtObject", args);
            break;
            
        case ResetPosture:
        	rePrx.executeAction(agentId, "ResetPosture", new LinkedList<String>());
        	break;
        	
        case StandAndSay:
        	rePrx.executeAction(agentId, "ResetPosture", new LinkedList<String>());
            pause();
            if (active.get())
            	rePrx.executeAction(agentId, "Say", args);
            if (args.get(0).equals("notthatone.wav")) {
            	controller.trialDone();
            }
            break;
            
        case EndTrial:
        	if (active.get())
        		rePrx.endScenario("SensoryGarden");
            controller.trialDone();
            break;
            
        case TeacherPrompt:
            JOptionPane.showMessageDialog(controller, "Please remind the child to touch the flower", "Prompt the child", JOptionPane.WARNING_MESSAGE);
            break;
        }
        
        // Now wait for the prescribed time until the user does something (unless it's been interrupted)
        if (active.get()) {
	        timer = new Timer();
	        timeoutTask = new TimerTask() {
	            @Override
	            public void run() {
	            	if (active.get()) {
		                log("Timeout of " + timeout + "ms expired for action " + actionId);
		                processAction(new UserAction(UserActionType.Timeout, null));
	            	}
	            }
	        };
	        
	        log("Waiting " + timeout + "ms for something to happen ...");
	        timer.schedule(timeoutTask, timeout);
        }
    }

	private void pause() 
	{
		try {
			Thread.sleep(100);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}
}
