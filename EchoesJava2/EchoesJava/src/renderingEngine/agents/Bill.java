//Translation from Py May 9th
//class file created out of PiavcaAvatars.java
package agents;
import java.util.Map;

import agents.$missing$;
import agents.EchoesAgent;
import agents.action_id;
import agents.and;
import agents.bbox_wc;
import agents.before;
import agents.expression;
import agents.file;
import agents.flower;
import agents.id;
import agents.in;
import agents.motion;
import agents.name;
import agents.needs;
import agents.pos;
import agents.pot;
import agents.presumably;
import agents.set;
import agents.targetId;
import agents.the;
import agents.type;
import agents.x;

public class Bill extends EchoesAgent
{
	private boolean collisionTest =false;
  
  // ={"type" "Bill"}
	public Bill(boolean autoAdd, Map<String, String> props)
	{
		super(autoAdd, props);
		this.avatar = Piavca.Avatar("agents/Bill/bill");
	}
    
    public void remove()
    {
        super.remove();
    }
}
