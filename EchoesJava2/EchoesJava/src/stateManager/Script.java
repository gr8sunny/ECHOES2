package stateManager;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import utils.Interfaces.IRenderingEngine;

public class Script implements Iterator<TrialDesc> {
	private String scriptName;
	private String childFirst;
	private String childLast;
	@SuppressWarnings("unused")
	private String initials;
	private String fileName;
	private int[] trialIds;
	
	private int curTrial;
	private boolean playedIntermediate;
	
	private static final SortedMap<String, Script> scripts = new TreeMap<String, Script>();
	public static Collection<String> getScriptDescs() {
		List<String> descs = new LinkedList<String>();
		for (Script script : scripts.values()) {
			descs.add(script.getDesc());
		}
		return descs;
	}
	public String getChildName() {
		return childFirst;
	}
	public String getDesc() {
		String desc = scriptName + " (";
		if (!childFirst.isEmpty()) {
			desc += childFirst;
		}
		if (!childLast.isEmpty()) {
			desc += " " + childLast;
		}
		desc += ")";
		return desc;
	}
	public static Script getScript(String scriptName) {
		return scripts.get(scriptName.split(" ")[0]);
	}
	
	public Script(String line) {
		String[] fields = line.split(",");
		int count = 0;
		scriptName = fields[count++];
		fileName = fields[count++];
		childFirst = fields[count++];
		childLast = fields[count++];
		initials = fields[count++];
		trialIds = new int[fields.length-count];
		for (int i = count; i < fields.length; i++) {
			trialIds[i-count] = Integer.parseInt(fields[i]);
		}
		scripts.put(scriptName, this);
		curTrial = 0;
	}
	
	public boolean hasNext() {
		return curTrial < trialIds.length;
	}
	
	public TrialDesc next() {
		return TrialDesc.getTrialDesc(trialIds[curTrial++]);
	}
	
	public TrialDesc peek() {
		return TrialDesc.getTrialDesc(trialIds[curTrial]);
	}
	
	public int getCurTrialNum() {
		return curTrial + 1;
	}
	
	public void resetCounter() {
		curTrial = 0;
		playedIntermediate = false;
	}
	
	public boolean checkIntermediate() {
		if (curTrial > 0 && curTrial % 12 == 0) {
			// In theory we should do it unless we've already done it
			return !playedIntermediate;
		} else {
			playedIntermediate = false;
			return false;
		}
	}
	
	public void setCounter(int trialNum) {
		assert(trialNum > 0 && trialNum <= trialIds.length);
		curTrial = trialNum - 1;
	}
	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	public void playIntro(IRenderingEngine rePrx) {
		String helloFile = "hello.wav";
		File childFile = new File("bin/rendering-engine/sound/sounds/" + fileName);
		ActionController.addHistory("Looking for file " + childFile);
		if (childFile.exists()) {
			ActionController.addHistory("Found it; using custom hello");
			helloFile = fileName;
		} else {
			ActionController.addHistory("Didn't find it; using generic hello");
		}
		
		rePrx.loadScenario("SensoryGarden");
		String agentId = rePrx.addAgent("Paul");

		List<String> args = new LinkedList<String>();
		args.add(helloFile);
		rePrx.executeAction(agentId, "Say", args);
		
		args.set(0, "mynameis2.wav");
		rePrx.executeAction(agentId, "Say", args);
		
		args.set(0, "welcome.wav");
		rePrx.executeAction(agentId, "Say", args);

		args.set(0, "name.wav");
		rePrx.executeAction(agentId, "Say", args);

		rePrx.endScenario("SensoryGarden");
		rePrx.loadScenario("Intro");
		
		// Wait until the scenario is finished ...
	}
	
	public void playIntroPart2(IRenderingEngine rePrx, ActionController controller) {
		rePrx.loadScenario("SensoryGarden");
		String agentId = rePrx.addAgent("Paul");
		
		List<String> args = new LinkedList<String>();
		args.add("thankyou.wav");
		rePrx.executeAction(agentId, "Say", args);
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		rePrx.endScenario("SensoryGarden");
		controller.trialDone();
	}
	
	public void playEnding(IRenderingEngine rePrx, ActionController controller) {
		rePrx.loadScenario("SensoryGarden");
		String agentId = rePrx.addAgent("Paul");

		List<String> args = new LinkedList<String>();
		args.add("welldone.wav");
		rePrx.executeAction(agentId, "Say", args);

		args.set(0, "tada.wav");
		rePrx.executeAction(agentId, "Say", args);

		args.set(0, "finish.wav");
		rePrx.executeAction(agentId, "Say", args);
		
		args.set(0, "goodbye.wav");
		rePrx.executeAction(agentId, "Say", args);
		
		rePrx.executeAction(agentId, "Wave", args);
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		rePrx.endScenario("SensoryGarden");
		controller.trialDone();
	}

	public void playIntermediate(IRenderingEngine rePrx, ActionController controller) {
		playedIntermediate = true;
		
		rePrx.loadScenario("SensoryGarden");
		String agentId = rePrx.addAgent("Paul");
		
		List<String> args = new LinkedList<String>();
		args.add(curTrial == 12 ? "after12.wav" : "after24.wav");
		rePrx.executeAction(agentId, "Say", args);

		args.set(0, "tada.wav");
		rePrx.executeAction(agentId, "Say", args);

		args.set(0, "welldone.wav");
		rePrx.executeAction(agentId, "Say", args);
		
		args.set(0, "pickmore.wav");
		rePrx.executeAction(agentId, "Say", args);
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// playedIntermediate = true;
		rePrx.endScenario("SensoryGarden");
		controller.trialDone();
	}
	public void setChildName(String name) {
		childFirst = name;
		childLast = "";
		fileName = name.toLowerCase() + ".wav";
	}
}
