package utils;

public class Enums
{
  public enum EchoesActivity
  {
    BubbleActivity,
    FlowerPickToBasket,
    FlowerTurnToBall ,
    FlowerGrow,
    CloudRain,
    PotStackRetrieveObject ,
    AgentPoke,
    Explore,
    BallSorting,
    BallThrowing,
    TickleAndTree ,
    ExploreWithAgent,
    BallThrowingContingent,
    FlowerTurnToBallContingent
  }
 
  public enum EchoesObjectType 
  {
    Shed, 
    LifeTree, 
    IntroBubble, 
    Bubble, 
    Basket, 
    Flower, 
    Pot, 
    Cloud, 
    Pond, 
    MagicLeaves, 
    Ball, 
    Container
  }
  
  public enum EchoesScene 
  {
    NoScene, 
    Intro, 
    Bubbles, 
    Garden, 
    GardenTask, 
    GardenSocialGame
  }
  
  public enum ScertsGoal 
  {
    FollowRemotePoint, 
    FollowContactPoint,
    InitiateNonVerbalBid, 
    InitiateVerbalBid, 
    InitiateSocialGame,
    NonverballyRespondBid, 
    VerballyRespondBid, 
    BriefInteraction, 
    ExtendedInteraction, 
    TurnTaking, 
    SmilesToAgent,
    MonitorPartner, 
    SecureAttention, 
    ImitateIfElicited, 
    ImitateSpontaneously, 
    ImitateAtLaterTime,
    NonverballyInitiateJointAttention, 
    NonverballyRespondJointAttention,
    DescribeEmotions, 
    RespondToEmotions,
    LooksToAgent, 
    ShiftGaze, 
    LooksToObject,
    RequestObject, 
    ProtestObjectActivity,
    VerbalGreeting, 
    NonVerbalGreeting,
    RespondRequestObject,
    AnticipateAction
  }
  
  public enum ScreenRegion 
  {
    ScreenTopLeft,
    ScreenTopMiddle, 
    ScreenTopRight,
    ScreenMiddleLeft, 
    ScreenMiddleMiddle, 
    ScreenMiddleRight,
    ScreenBottomLeft, 
    ScreenBottomMiddle, 
    ScreenBottomRight,
    ScreenUnknown
  }
  
  public enum Engagement 
  {
    DisengTotal, 
    DisengMinus, 
    DisengPlus, 
    Eng, 
    EngPlus, 
    EngPlusPlus
  }

  public enum FacialExpression 
  {
    ExpressionSmile, 
    ExpressionUnknown
  }

  public enum HeadTrackerMode 
  {
    ModelMode, 
    TrackMode
  }
  
  public enum ListenerType
  {
    childModel,
    userHead,
    renderer,
    agent,
    event,
    touch,
    pause
  }
  
  public enum UserActionType 
  {
    UserRespondedToBid, 
    UserActivityRelevantAction, 
    UserGaveRequestedObject, 
    UserGaveUnrequestedObject, 
    UserUnrelatedAction, 
    UserNoAction, 
    UserInitiated, 
    UserTouchedAgent
  }
}
