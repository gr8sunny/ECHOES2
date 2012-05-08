package pedagogicComponent;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Observer;
import echoesEngine.*;
import utils.Interfaces.*;
import utils.Enums.*;
import utils.Logger;
import pedagogicComponent.Utilities.Clock;
import pedagogicComponent.data.ChildAction;

/**
 * Implementation of methods for the practitioner window.
 * 
 * @author Elaine Farrow
 */
public class PractitionerServer extends PCcomponentHandler
{
  private static final boolean USE_OWN_GOAL_MAP = true;
  private static final Map <String, Integer> goalMap = RuleBasedChildModel.createScertsGoalMap();

  /**
   * The clock for tracking elapsed time.
   */
  private final Clock clock = new Clock();

  private final IPauseListener pauseListener;

  /**
   * Create a PractitionerServer for testing only.
   */
  PractitionerServer()
  {
    this.dmPrx = null;
    this.pauseListener = null;
  }

  /**
   * Create a new PractitionerServer.
   * 
   * @param pCcompH
   * the PCcomponentHandler.
   */
  public PractitionerServer(PCcomponents pCc, IDramaManager dmPrx, IActionEngine aePrx)
  {
    super(pCc, dmPrx, aePrx);
    this.dmPrx = dmPrx;
    ListenerManager listenerMgr = ListenerManager.GetInstance();
    this.pauseListener = (IPauseListener)listenerMgr.retrieve(ListenerType.pause);
  }


  /**
   * Add an intervention handler.
   * 
   * @param handler
   * the intervention handler.
   */
  public void addInterventionHandler(Observer handler)
  {
    try
    {
      getPCcs().agentH.addInterventionHandler(handler);
    }
    catch (Exception e){}
  }

  /**
   * Shut down the whole system.
   */
  public void shutdown()
  {
    try
    {
      printScertsGoals();
    }
    catch (Exception e) {}
  }

  /**
   * Pause the system.
   */
  public void pause()
  {
    pauseListener.setPaused(true);
  }

  /**
   * Resume the system after pausing.
   */
  public void resume()
  {
    pauseListener.setPaused(false);
  }

  /**
   * End the current session.
   */
  public void endSession()
  {
    shutdown();
  }

  /**
   * Note that an activity has started.
   */
  public void activityStarted()
  {
    clock.start();
  }

  /**
   * Get the clock.
   * 
   * @return the clock.
   */
  public Clock getClock()
  {
    return clock;
  }

  /**
   * Play the introduction scene using the given name.
   * 
   * @param name
   * the name.
   * 
   * @param initialActivity
   * the initial activity.
   */
  public void playIntroScene(final String name, final Activity initialActivity)
  {
    getPCcs().childStateH.loadInitialChildAttributes(name);
    getPCcs().sceneH.setInitialActivity(initialActivity);

    getPCcs().agentH.setAgentGoal("wait");
    dmPrx.setIntroScene(EchoesScene.Intro, name);
  }

  /**
   * Start the bubble scene.
   */
  public void startBubbleScene()
  {
    if (getPCcs().agentH.getIsAgentInWorld())
    {
      getPCcs().agentH.setAgentGoal("walkOff");
      getPCcs().agentH.setSceneToStartAfterWalkingOut(EchoesScene.Bubbles);
    }
    else
    {
      getPCcs().nonAgentSceneH.decideNonAgentSceneParameters(EchoesScene.Bubbles);
    }
  }

  /**
   * Start the garden scene, using some default child attributes.
   */
  public void startGardenScene()
  {
    if (getPCcs().agentH.getIsAgentInWorld())
    {
      getPCcs().agentH.setAgentGoal("walkOff");
      getPCcs().agentH.setSceneToStartAfterWalkingOut(EchoesScene.Garden);
    }
    else
    {
      dmPrx.setScene(EchoesScene.Garden);
      dmPrx.arrangeScene(EchoesScene.Garden, EchoesActivity.Explore, 1, false);
      getPCcs().agentH.directAgentChangeInvolvement(false);
    }
  }

  /**
   * Tell the agent to enter the scene and greet the child.
   */
  public void tellAgentToEnterAndGreet()
  {
    dmPrx.setScene(EchoesScene.GardenTask);
    getPCcs().agentH.setAgentGoal("enterECHOES");
  }

  /**
   * Set the learning activity.
   * 
   * @param activity
   * the activity.
   * 
   * @param object
   * the object.
   * 
   * @param isContingent
   * <code>true</code> if the activity is contingent; <code>false</code>
   * otherwise.
   */
  public void setLearningActivity(final EchoesActivity activity,
                  final EchoesObjectType object,
                  final boolean isContingent)
  {
    getPCcs().agentH.setActivityContingent(isContingent);
    getPCcs().agentH.setNextActivityAndObject(activity, object);

    if (getPCcs().agentH.getIsAgentInWorld())
      getPCcs().agentH.setAgentGoal("walkOff");
    else
      getPCcs().agentH.startActivity();
  }

  /**
   * Tell the system that the child is not acting.
   */
  public void childIsNotActing()
  {
    getPCcs().childActionH.handleChildAction(ChildAction.noAction.getName(),
                         Collections.singletonList(""));
  }

  /**
   * Tell the agent to stop waiting and take his turn.
   */
  public void tellAgentToStopWaiting()
  {
    getPCcs().agentH.setAgentGoal("dontWait");
  }

  /**
   * Tell the agent to leave the scene.
   */
  public void tellAgentToLeave()
  {
    getPCcs().agentH.setAgentGoal("leave");
  }

  /**
   * Set whether the agent can be tickled by the child.
   * 
   * @param canTickle
   * <code>true</code> if the agent can be tickled; <code>false</code>
   * otherwise.
   */
  public void setCanTickleAgent(boolean canTickle)
  {
    getPCcs().childActionH.setTickle(canTickle);
  }

  /**
   * Tell the system that the child has greeted Andy.
   * 
   * @return the current count.
   */
  public int childGreetsAndy()
  {
    return incrementGoal(ScertsGoal.VerbalGreeting);
  }

  /**
   * Tell the system that the child has asked Andy to do the given action with
   * the given object.
   * 
   * @param action
   * the action.
   * 
   * @param object
   * the object.
   * 
   * @return the current count.
   */
  public int childAsksAndyToAct(final Object action,
                  final EchoesObjectType object)
  {
    return incrementGoal(ScertsGoal.InitiateVerbalBid);
  }

  /**
   * Tell the system that the child has requested the given object.
   * 
   * @param object
   * the object.
   * 
   * @return the current count.
   */
  public int childRequestsObject(final EchoesObjectType object)
  {
    return incrementGoal(ScertsGoal.RequestObject);
  }

  /**
   * Tell the system that the child has protested the given undesired action
   * or activity.
   * 
   * @param action
   * the action.
   * 
   * @return the current count.
   */
  public int childProtestsUndesiredActionActivity(final Object action)
  {
    return incrementGoal(ScertsGoal.ProtestObjectActivity);
  }

  /**
   * Tell the system that the child has protested the given undesired action.
   * 
   * @param action
   * the action.
   * 
   * @return the current count.
   */
  public int childProtestsUndesiredAction(final Object action)
  {
    return incrementGoal(ScertsGoal.ProtestObjectActivity);
  }

  /**
   * Tell the system that the child has protested the given undesired
   * activity.
   * 
   * @param activity
   * the activity.
   * 
   * @return the current count.
   */
  public int childProtestsUndesiredActivity(final EchoesActivity activity)
  {
    return incrementGoal(ScertsGoal.ProtestObjectActivity);
  }

  /**
   * Tell the system that the child has protested the given undesired object.
   * 
   * @param object
   * the object.
   * 
   * @return the current count.
   */
  public int childProtestsUndesiredObject(final EchoesObjectType object)
  {
    return incrementGoal(ScertsGoal.ProtestObjectActivity);
  }

  /**
   * Tell the system that the child has verbally responded to an interaction.
   * 
   * @return the current count.
   */
  public int childRespondsInteractionVerbal()
  {
    return incrementGoal(ScertsGoal.VerballyRespondBid);
  }

  /**
   * Tell the system that the child has taken part in a brief reciprocal
   * interaction.
   * 
   * @return the current count.
   */
  public int childReciprocalInteractionBrief()
  {
    return incrementGoal(ScertsGoal.BriefInteraction);
  }

  /**
   * Tell the system that the child has taken part in an extended reciprocal
   * interaction.
   * 
   * @return the current count.
   */
  public int childReciprocalInteractionExtended()
  {
    return incrementGoal(ScertsGoal.ExtendedInteraction);
  }

  /**
   * Tell the system that the child has attempted to secure Andy's attention.
   * 
   * @return the current count.
   */
  public int childSecuresAttention()
  {
    return incrementGoal(ScertsGoal.SecureAttention);
  }

  /**
   * Tell the system that the child has greeted Andy verbally.
   * 
   * @return the current count.
   */
  public int childGreetsAndyVerbal()
  {
    return incrementGoal(ScertsGoal.VerbalGreeting);
  }

  /**
   * Tell the system that the child has greeted Andy non-verbally.
   * 
   * @return the current count.
   */
  public int childGreetsAndyNonverbal()
  {
    return incrementGoal(ScertsGoal.NonVerbalGreeting);
  }

  /**
   * Tell the system that the child has exhibited verbal turn-taking.
   * 
   * @return the current count.
   */
  public int childTakesTurnsVerbally()
  {
    return incrementGoal(ScertsGoal.TurnTaking);
  }

  /**
   * Tell the system that the child has responded to another's emotion.
   * 
   * @return the current count.
   */
  public int childRespondsToEmotion()
  {
    return incrementGoal(ScertsGoal.RespondToEmotions);
  }

  /**
   * Tell the system that the child has used words to describe their own
   * emotion.
   * 
   * @param word
   * the word used.
   * 
   * @return the current count.
   */
  public int childUsesEmotionWords(final String word)
  {
    return incrementGoal(ScertsGoal.DescribeEmotions);
  }

  /**
   * Tell the system that the child has imitated an activity in response to
   * elicitation.
   * 
   * @param activity
   * the activity.
   * 
   * @return the current count.
   */
  public int childImitatesActionElicited(final EchoesActivity activity)
  {
    return incrementGoal(ScertsGoal.ImitateIfElicited);
  }

  /**
   * Tell the system that the child has imitated an activity spontaneously
   * immediately after seeing it.
   * 
   * @param activity
   * the activity.
   * 
   * @return the current count.
   */
  public int childImitatesActionImmediately(final EchoesActivity activity)
  {
    return incrementGoal(ScertsGoal.ImitateSpontaneously);
  }

  /**
   * Tell the system that the child has imitated an activity spontaneously
   * some time after seeing it.
   * 
   * @param activity
   * the activity.
   * 
   * @return the current count.
   */
  public int childImitatesActionLater(final EchoesActivity activity)
  {
    return incrementGoal(ScertsGoal.ImitateAtLaterTime);
  }

  /**
   * Tell the system that the child has imitated an action in response to
   * elicitation.
   * 
   * @param action
   * the action.
   * 
   * @return the current count.
   */
  public int childImitatesActionElicited(Object action)
  {
    return childImitatesActionElicited(Collections.singletonList(action));
  }

  /**
   * Tell the system that the child has imitated a sequence of actions in
   * response to elicitation.
   * 
   * @param actions
   * the actions.
   * 
   * @return the current count.
   */
  public int childImitatesActionElicited(final List <Object> actions)
  {
    return incrementGoal(ScertsGoal.ImitateIfElicited);
  }

  /**
   * Tell the system that the child has imitated an action spontaneously
   * immediately after seeing it.
   * 
   * @param action
   * the action.
   * 
   * @return the current count.
   */
  public int childImitatesActionImmediately(Object action)
  {
    return childImitatesActionImmediately(Collections.singletonList(action));
  }

  /**
   * Tell the system that the child has imitated a sequence of actions
   * spontaneously immediately after seeing it.
   * 
   * @param actions
   * the actions.
   * 
   * @return the current count.
   */
  public int childImitatesActionImmediately(final List <Object> actions)
  {
    return incrementGoal(ScertsGoal.ImitateSpontaneously);
  }

  /**
   * Tell the system that the child has imitated an action spontaneously some
   * time after seeing it.
   * 
   * @param action
   * the action.
   * 
   * @return the current count.
   */
  public int childImitatesActionLater(Object action)
  {
    return childImitatesActionLater(Collections.singletonList(action));
  }

  /**
   * Tell the system that the child has imitated a sequence of actions
   * spontaneously some time after seeing it.
   * 
   * @param actions
   * the actions.
   * 
   * @return the current count.
   */
  public int childImitatesActionLater(final List <Object> actions)
  {
    return incrementGoal(ScertsGoal.ImitateAtLaterTime);
  }

  /**
   * Tell the system that the child has initiated joint attention non
   * verbally.
   * 
   * @param actions
   * the actions.
   * 
   * @return the current count.
   */
  public int childInitiatesJointAttentionNonverbal()
  {
    return incrementGoal(ScertsGoal.NonverballyInitiateJointAttention);
  }

  /**
   * Tell the system that the child has responded to joint attention non
   * verbally.
   * 
   * @param actions
   * the actions.
   * 
   * @return the current count.
   */
  public int childRespondsJointAttentionNonverbal()
  {
    return incrementGoal(ScertsGoal.NonverballyRespondJointAttention);
  }

  /**
   * Tell the system whether the child is engaged with the system.
   * 
   * @param isEngaged
   * <code>true</code> if the child is engaged; <code>false</code> otherwise.
   */
  public void childEngagedWithSystem(final boolean isEngaged)
  {
    getPCcs().childStateH.setEngagedECHOES(isEngaged);
  }

  /**
   * Tell the system whether the child is engaged with the agent.
   * 
   * @param isEngaged
   * <code>true</code> if the child is engaged; <code>false</code> otherwise.
   */
  public void childEngagedWithAgent(final boolean isEngaged)
  {
    throwUnsupportedOperationException();
  }

  /**
   * Tell the system the child's affective state.
   * 
   * @param state
   * the state.
   */
  public void childAffectiveState(final String state)
  {
    getPCcs().childStateH.setAffectiveState(state);
  }

  /**
   * Tell the system that the child prefers the given object.
   * 
   * @param object
   * the object.
   */
  public void childPrefersObject(final EchoesObjectType object)
  {
    throwUnsupportedOperationException();
  }

  /**
   * Tell the system that the child has stated whether or not they like Andy.
   * 
   * @param likesAndy
   * <code>true</code> if the child likes Andy; <code>false</code> otherwise.
   */
  public void childLikesAndy(final boolean likesAndy)
  {
    throwUnsupportedOperationException();
  }

  /**
   * Tell the system that the child is looking towards the given object.
   * 
   * @param object
   * the object.
   * 
   * @return the current count.
   */
  public int childLooksTowardObject(final EchoesObjectType object)
  {
    return incrementGoal(ScertsGoal.LooksToObject);
  }

  /**
   * Tell the system that the child is looking where Andy is looking.
   * 
   * @return the current count.
   */
  public int childLooksWhereAndyLooks()
  {
    return incrementGoal(ScertsGoal.MonitorPartner);
  }

  /**
   * Tell the system that the child is smiling at Andy.
   * 
   * @return the current count.
   */
  public int childSmilesAtAndy()
  {
    return incrementGoal(ScertsGoal.SmilesToAgent);
  }

  /**
   * Tell the system that the child is looking towards Andy.
   * 
   * @return the current count.
   */
  public int childLooksTowardAndy()
  {
    return incrementGoal(ScertsGoal.LooksToAgent);
  }

  /**
   * Tell the system that the child is looking between people and the given
   * object.
   * 
   * @param object
   * the object.
   * 
   * @return the current count.
   */
  public int childLooksBetweenPeopleObjects(final EchoesObjectType object)
  {
    return incrementGoal(ScertsGoal.ShiftGaze);
  }

  /**
   * Increment the given goal and return the current count.
   * 
   * @param goal
   * the goal.
   * 
   * @return the current count.
   */
  private int incrementGoal(final ScertsGoal goal)
  {
    Map <String, Integer> map = USE_OWN_GOAL_MAP ? goalMap : getPCcs().childM.getScertsGoalSatisfactionMap();
    return RuleBasedChildModel.incrementScertsGoal(map, goal);
  }

  /**
   * Throw an {@link UnsupportedOperationException}.
   */
  private void throwUnsupportedOperationException()
  {
    throw new UnsupportedOperationException("Operation not implemented");
  }

  /**
   * Print the SCERTS goals to stdout.
   */
  private void printScertsGoals()
  {
    String ownGoals = "Practitioner SCERTS goal satisfaction map...\n"
            + RuleBasedChildModel.scertsGoalMapToString(goalMap);

    System.out.println(ownGoals);
    Logger.Log("info", ownGoals);

    String modelGoals = "Child Model SCERTS goal satisfaction map...\n"
              + RuleBasedChildModel.scertsGoalMapToString(getPCcs().childM.getScertsGoalSatisfactionMap());

    System.out.println(modelGoals);
    Logger.Log("info", modelGoals);

    System.out.flush();
  }
}
