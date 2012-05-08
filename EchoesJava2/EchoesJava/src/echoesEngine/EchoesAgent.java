package echoesEngine;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import FAtiMA.Agent;
import FAtiMA.AgentSimulationTime;
import FAtiMA.DialogManager;
import FAtiMA.Display.AgentDisplay;
import FAtiMA.autobiographicalMemory.AutobiographicalMemory;
import FAtiMA.deliberativeLayer.DeliberativeProcess;
import FAtiMA.deliberativeLayer.EmotionalPlanner;
import FAtiMA.deliberativeLayer.goals.GoalLibrary;
import FAtiMA.knowledgeBase.KnowledgeBase;
import FAtiMA.reactiveLayer.ReactiveProcess;
import FAtiMA.util.enumerables.EmotionType;
import FAtiMA.util.parsers.AgentLoaderHandler;
import FAtiMA.wellFormedNames.ComposedName;
import utils.Logger;
import utils.Interfaces.IPedagogicComponent;
import utils.Interfaces.IRenderingEngine;

/**
 * @author mef3
 * 
 */
public class EchoesAgent extends Agent 
{
    private static final String DATA_PATH = "share/action-engine/";
  private static EchoesAgent _instance;
  
  public static EchoesAgent getInstance() {
      return _instance;
  }

  public void addUser() {
      KnowledgeBase.GetInstance().Tell(new ComposedName("User", "type"), "user");
      // Logger.Log("info", "Knowledge base now " + KnowledgeBase.GetInstance());
  }
  
  @SuppressWarnings("rawtypes")
  public EchoesAgent(String name, String displayName, String agentId, HashMap<String, String> properties,
                    IRenderingEngine rePrx, IPedagogicComponent pcPrx) 
  {
      if (_instance != null) 
        Logger.LogWarning("Creating another agent!");
      
      _instance = this;
      _shutdown = false;
      _numberOfCycles = 0;
      _self = name;
      _role = "Paul";
      _sex = "M";
      _displayName = displayName;
      _showStateWindow = true;
      _currentEmotion = EmotionType.NEUTRAL; 
      _actionsForExecution = new ArrayList();
      _perceivedEvents = new ArrayList();

      properties.put("name", _self);
      properties.put("role", _role);
      properties.put("sex", _sex);
      
      Logger.Log("info", properties.toString());
      for(Object key : properties.keySet()) 
          KnowledgeBase.GetInstance().Tell(new ComposedName(_self, key.toString()), properties.get(key));
      
      _dialogManager = new DialogManager();

      AutobiographicalMemory.GetInstance().setSelf(_self);

      // Load language engine
      try {
          // Load Plan Operators
          EmotionalPlanner planner = new EmotionalPlanner(DATA_PATH + "Actions.xml", _self);

          // Load GoalLibrary
          GoalLibrary goalLibrary = new GoalLibrary(DATA_PATH + "GoalLibrary.xml", _self);
          String personalityFile = DATA_PATH + "Paul.xml";
          Logger.Log("info", "LOADING Personality: " + personalityFile);
          
          // For efficiency reasons these two are not real processes

          _reactiveLayer = new ReactiveProcess(_self);

          _deliberativeLayer = new DeliberativeProcess(_self, goalLibrary, planner);
          
          _deliberativeLayer.RemoveAllGoals();
          
          AgentLoaderHandler c = new AgentLoaderHandler(_self,_reactiveLayer,_deliberativeLayer);
          SAXParserFactory factory = SAXParserFactory.newInstance();
          SAXParser parser = factory.newSAXParser();
          parser.parse(new File(personalityFile), c);

          _remoteAgent = new EchoesRemoteAgent(this, agentId, properties, _deliberativeLayer, rePrx, pcPrx);

      } catch (Exception e) {
          e.printStackTrace();
          // System.exit(-1);
      }
      
      AgentSimulationTime.GetInstance();
  }

  public void start() {
      /*
       * This call will initialize the timer for the agent's simulation time
       */
      AgentSimulationTime.GetInstance();

      _remoteAgent.start();

      if (_showStateWindow)
          _agentDisplay = new AgentDisplay(this);

      new Thread(new Runnable() {
          public void run() {
              Logger.Log("info", "Calling EchoesAgent.Run()");
              EchoesAgent.this.Run();
          }
      }).start();
  }

  public void stop() {
      Logger.Log("info", "Calling EchoesAgent.stop()");
      _deliberativeLayer.ShutDown();
      _reactiveLayer.ShutDown();
      _remoteAgent.ShutDown();
      if (_showStateWindow && _agentDisplay != null)
          _agentDisplay.dispose();

  }
}
