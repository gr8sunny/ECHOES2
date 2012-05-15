package agents;
//translation from Py May 15th 2012

public class AgentAction
{
  public Object callback;
  public String agentId;
  public String action;
  public String details;
  
  public AgentAction(Object callback, String agentId, String action, String details)
  {
    this.callback = callback;
    this.agentId = agentId;
    this.action = action;
    this.details = details;
  }
}