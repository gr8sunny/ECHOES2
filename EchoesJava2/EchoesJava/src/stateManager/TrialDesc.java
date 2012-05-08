package stateManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import utils.Interfaces.IRenderingEngine;

public class TrialDesc {
	private boolean engagement;
	private boolean gesture;
	private boolean contingent;
	private String[] flowerColours;
	private int targetFlower;
	private int trialId;
	
	private static final Map<Integer, TrialDesc> trialDescs = new HashMap<Integer, TrialDesc>();
	public static TrialDesc getTrialDesc(int trialId) {
		return trialDescs.get(trialId);
	}
	
	public TrialDesc(String trialDesc) {
		String[] fields = trialDesc.split(",");
		trialId = Integer.valueOf(fields[0]);
		engagement = Boolean.valueOf(fields[1]);
		gesture = Boolean.valueOf(fields[2]);
		flowerColours = new String[3];
		System.arraycopy(fields, 3, flowerColours, 0, 3);
		targetFlower = Integer.valueOf(fields[6]);
		if (fields.length >= 8) {
		    contingent = Boolean.valueOf(fields[7]);
		} else {
		    contingent = false;
		}
		trialDescs.put(trialId, this);
	}
	
	@Override
	public String toString() {
		return trialId + ": en=" + engagement + " ge=" + gesture + " con=" + contingent + " flowers=" + Arrays.toString(flowerColours) + " target=" + targetFlower;
	}
	
	public void start(int seqNo, IRenderingEngine rePrx) {
		ActionController.addHistory(seqNo + ": Starting trial " + this.toString());
        
        // Load the correct scenario and add the agent
        ActionController.addHistory("Loading scenario ...");
        rePrx.loadScenario("SensoryGarden");

        String agentId = rePrx.addAgent("Paul");
		ActionController.addHistory("Agent ID is " + agentId);

        rePrx.setWorldProperty("DisplayScore", "True");
		rePrx.setWorldProperty("Score", String.valueOf(seqNo));
        
        // Add three flowers
		String targetId = null;
        for (int i = 0; i < flowerColours.length; i++) {
            String id = rePrx.addObject("Flower");
            if (i == targetFlower-1) {
            	targetId = id;
            }
            double xPos = -1 * (3.3 - i * 3); 
            rePrx.setObjectProperty(id, "Pos", "(" + xPos + ",-2,0)");
            rePrx.setObjectProperty(id, "Colour", flowerColours[i]);
        }
        
        ScriptedAction.setTargetId(targetId);
        ScriptedAction.setAgentId(agentId);
        
        // Wait a short amount of time before starting just so that everything is happy on screen
        /*
        try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		*/
        
        // Load the initial scripted action and start things going
        ScriptedAction startAction = ActionController.getScript(engagement, gesture, contingent);
        ActionController.addHistory("Calling start action: " + startAction);
        new Thread(startAction).start();
	}
}
