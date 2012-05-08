package pedagogicComponent;

import echoesEngine.ListenerManager;
import utils.Enums.ListenerType;
import utils.Interfaces.*;

/**
 * Contains all the Pedagogic Component components
 * 
 * @author katerina avramides
 * 
 */
public class PCcomponents {
	// is responsible for all communication with the drama manager and action
	// engine
	DMandAEdirector director;
	// is responsible for monitoring the activity - not sure is needed
	ActivityDurationHandler activityDurationH;
	// is responsible for setting the initial scene
	SceneHandler sceneH;
	// is responsible for decisions in relation to the child's engagement
	EngagementHandler engagementH;
	// is responsible for the scenes that do not have an agent (currently only
	// the bubble scene)
	NonAgentSceneHandler nonAgentSceneH;
	// is responsible for decisions relating to whether the agent engages with
	// the child
	AgentInvolvementHandler agentInvolvementH;
	// is responsible for deciding which goal the agent will try to support in
	// his/her next action given the current activity
	GoalsForActivityReasoner goalsActivityR;
	// is responsible for decisions relating to choice of SCERTS goal
	SCERTSgoalHandler goalH;
	// is responsible for decisions relating to activity and object choice
	ActivityAndObjectHandler activityObjectH;
	// is responsible for decisions relating to the agent's behaviour within an
	// activity
	AgentBehaviourHandler agentH;
	// is responsible for handling the child's actions
	ChildActionHandler childActionH;
	// is responsible for responding to changes in the child's state
	ChildStateHandler childStateH;

	CurrentSystemState currentState;
	// initialises the maps between scenes and available objects and activities
	// within them
	// the goals available in echoes (for which we need info on the child's
	// ability)
	// which activities are appropriate for each scerts goal
	// methods for reengaging the learner
	// agent behaviours when not engaging with the learner but present in the
	// ECHOES world
	ECHOESworldDataInitialiser initialiser;

	RuleBasedChildModel childM;
	
	PractitionerServer ps;

	// timer
	Reminder reminder;

	public PCcomponents(IDramaManager dmPrx, IActionEngine aePrx, IChildModel cmPrx, IStateManager smPrx) 
	{
	  ListenerManager listenerMgr = ListenerManager.GetInstance();
	  IEventListener eventPublisher = (IEventListener)listenerMgr.retrieve(ListenerType.event);
	  
		initialiser = new ECHOESworldDataInitialiser(dmPrx);
		director = new DMandAEdirector(this, dmPrx, aePrx);
		sceneH = new SceneHandler(this, dmPrx, aePrx);
		engagementH = new EngagementHandler(this, dmPrx, aePrx);
		nonAgentSceneH = new NonAgentSceneHandler(this, dmPrx, aePrx);
		agentInvolvementH = new AgentInvolvementHandler(this, dmPrx, aePrx);
		goalH = new SCERTSgoalHandler(this, dmPrx, aePrx);
		activityObjectH = new ActivityAndObjectHandler(this, dmPrx, aePrx);
		agentH = new AgentBehaviourHandler(this, dmPrx, aePrx);
		goalsActivityR = new GoalsForActivityReasoner(this, dmPrx, aePrx);
		childActionH = new ChildActionHandler(this, dmPrx, aePrx, cmPrx, smPrx, eventPublisher);
		childStateH = new ChildStateHandler(this, dmPrx, aePrx);
		// not sure needed - just communicate with dm?
		currentState = new CurrentSystemState(dmPrx);
		activityDurationH = new ActivityDurationHandler(this, dmPrx, aePrx);
		reminder = new Reminder(this, dmPrx, aePrx);
		childM = new RuleBasedChildModel(this, dmPrx, aePrx);
		ps = new PractitionerServer(this, dmPrx, aePrx);
	}
}
