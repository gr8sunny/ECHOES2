package FAtiMA.Display;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import FAtiMA.Agent;


public class TestAction implements ActionListener {
	
	Agent _ag;

	public TestAction(Agent a)
	{	
		_ag = a;
	}

	public void actionPerformed(ActionEvent arg0) {
		_ag.SaveAgentState(_ag.name());
	}
	
}
