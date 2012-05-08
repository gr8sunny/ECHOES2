package uk.ac.hw.echoes.child_model;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;

import echoes.EchoesActivity;
import echoes.EchoesObjectType;
import echoes.ScertsGoal;

public class ChildModelDetails implements Serializable {
	private static final long serialVersionUID = 1L;

	// Basic (static) information
	public String name;
	public String fileName;
	public int age;
	
	// Other (dynamic) information
	public int bubbleComplexity;
	public boolean displayScore;
	public boolean openToAgent;
	public int levelDirection;
	public int numRepetitions;
	public Map<ScertsGoal, Integer> goalAbilityMap;
	public Map<EchoesActivity, Integer> activityMap;
	public Map<EchoesObjectType, Integer> objectTypeMap;

	public ChildModelDetails() {
		// Initialise this based on what's in the pedagogic component at the moment
		bubbleComplexity = 3;
		displayScore = true;
		openToAgent = false;
		levelDirection = 1;
		numRepetitions = 5;
		
		goalAbilityMap = new EnumMap<ScertsGoal, Integer>(ScertsGoal.class);
		for (ScertsGoal goal : ScertsGoal.values()) {
			goalAbilityMap.put(goal, 2);
		}
		activityMap = new EnumMap<EchoesActivity, Integer>(EchoesActivity.class);
		for (EchoesActivity activity : EchoesActivity.values()) {
				activityMap.put(activity, 0);
		}
		objectTypeMap = new EnumMap<EchoesObjectType, Integer>(EchoesObjectType.class);
		for (EchoesObjectType type : EchoesObjectType.values()) {
			objectTypeMap.put(type, 0);
		}
	}

}
