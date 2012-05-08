package childModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import utils.Enums.*;
import utils.Interfaces.*;
import utils.Logger;

public class ChildModel implements IChildModel, Serializable 
{
	private static final Map<ScertsGoal, Integer> GOAL_LEVELS = new EnumMap<ScertsGoal, Integer>(ScertsGoal.class);
	static {
		for (ScertsGoal goal : ScertsGoal.values()) {
			switch (goal) {
			case BriefInteraction:
			case ExtendedInteraction:
				GOAL_LEVELS.put(goal, 2);
				break;
			default:
				GOAL_LEVELS.put(goal, 0);
			}
		}
	}
	private static final String MODEL_DIRECTORY = "share/child-model/models";
	private static final long serialVersionUID = 1;
	private EngagementClassifier eClassifier;
	private ChildModelDetails cmDetails;
	
	public ChildModel() 
	{
	  eClassifier = new EngagementClassifier();
    eClassifier.start();
	}

	private ChildModelDetails loadDetails (String fileName) {
		// Find the correct model with this name
		File file = new File(MODEL_DIRECTORY, fileName);
		try {
			ObjectInputStream stream = new ObjectInputStream(new FileInputStream(file));
			return (ChildModelDetails) stream.readObject();
		} catch (FileNotFoundException e) {
			Logger.LogWarning(
					"Unable to load model from " + file + ": " + e.getMessage());
		} catch (IOException e) {
			Logger.LogWarning(
					"Unable to load model from " + file + ": " + e.getMessage());
		} catch (ClassNotFoundException e) {
			Logger.LogWarning(
					"Unable to load model from " + file + ": " + e.getMessage());
		}
		return null;
	}
	
	public void createModel() 
	{
		cmDetails = new ChildModelDetails();
	}

	public void loadModel(String fileName) 
	{
		cmDetails = loadDetails (fileName);
		if (cmDetails != null) {
			Logger.Log("info", "Loaded child model for '" + cmDetails.name + "' from " + fileName);
		}
	}
	
	public String getChildNameForFile (String fileName) {
		ChildModelDetails details = loadDetails(fileName);
		if (details != null) {
			return details.name;
		} else {
			return null;
		}
	}

	public void saveModel() {
		if (cmDetails != null) {
			File directory = new File(MODEL_DIRECTORY);
			if (cmDetails.fileName == null) {
				// Choose a unique file name
				if (cmDetails.name == null) {
					cmDetails.name = "Nobody";
				}
				try {
					cmDetails.fileName = File.createTempFile(cmDetails.name, ".model", directory).getName();
					Logger.Log("info", "Selected file name is " + cmDetails.fileName);
				} catch (IOException e) {
					Logger.LogWarning("Unable to create file for model saving: " + e.getMessage());
				}
			}
			try {
				File file = new File(directory, cmDetails.fileName);
				ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(file));
				stream.writeObject(cmDetails);
				Logger.Log("info", "Saved model for " + cmDetails.name + " to " + file);
			} catch (IOException e) {
				Logger.LogWarning("Unable to save model: " + e.getMessage());
			}
		} 
		else 
			Logger.LogWarning("Not saving null child model");
	}
	
	public List<String> listModels () {
		File directory = new File(MODEL_DIRECTORY);
		List<String> models = new LinkedList<String>();
		for (File file : directory.listFiles()) {
			try {
				ObjectInputStream stream = new ObjectInputStream(new FileInputStream(file));
				@SuppressWarnings("unused")
				ChildModelDetails details = (ChildModelDetails)stream.readObject();
				models.add(file.getName());
			} catch (FileNotFoundException e) {
				Logger.LogWarning("Unable to load model from " + file + ": " + e.getMessage());
			} catch (IOException e) {
				Logger.LogWarning("Unable to load model from " + file + ": " + e.getMessage());
			} catch (ClassNotFoundException e) {
				Logger.LogWarning("Unable to load model from " + file + ": " + e.getMessage());
			}
		}
		Collections.sort(models);
		return models;
	}
	
	public void setTargetObject(String objId) {
		if (eClassifier != null) {
			Logger.Log("info", "Set target object to " + objId);
			eClassifier.setTargetObject(objId);
		}
	}

	public Engagement getEngagement() {
		return eClassifier.getEngagement();
	}
	
	public int getAbility(ScertsGoal goal) {
		return cmDetails.goalAbilityMap.get(goal);
	}
	
	public int getActivityValue(EchoesActivity activity) {
		return cmDetails.activityMap.get(activity);
	}

	public int getBubbleComplexity() {
		return cmDetails.bubbleComplexity;
	}

	public int getGoalLevelOfDirection(ScertsGoal goal) {
		return GOAL_LEVELS.get(goal);
	}
	
	public int getObjectValue(EchoesObjectType echoesObject) {
		return cmDetails.objectTypeMap.get(echoesObject);
	}

	public int getOverallLevelOfDirection() {
		return cmDetails.levelDirection;
	}

	public boolean isOpenToAgent() {
		return cmDetails.openToAgent;
	}

	public boolean displayScore() {
		return cmDetails.displayScore;
	}
	
	public int getAge() {
		return cmDetails.age;
	}

	public String getChildName() {
		return cmDetails.name;
	}

	public int getNumRepetitions() {
		return cmDetails.numRepetitions;
	}
	
	public void setAbility(ScertsGoal goal, int ability) {
		cmDetails.goalAbilityMap.put(goal, ability);
	}

	public void setActivityValue(EchoesActivity activity, int value) {
		cmDetails.activityMap.put(activity, value);
	}

	public void setBubbleComplexity(int complexity) {
		cmDetails.bubbleComplexity = complexity;
	}
	
	public void setDisplayScore(boolean display) {
		cmDetails.displayScore = display;
	}

	public void setNumRepetitions(int numRepetitions) {
		cmDetails.numRepetitions = numRepetitions;
	}

	public void setObjectValue(EchoesObjectType objectType, int value) {
		cmDetails.objectTypeMap.put(objectType, value);
	}

	public void setOpenToAgent(boolean open) {
		cmDetails.openToAgent = open;
	}
	
	public void setOverallLevelOfDirection(int level) {
		cmDetails.levelDirection = level;
	}

	public void setAge(int age) {
		cmDetails.age = age;
	}

	public void setChildName(String name) {
		cmDetails.name = name;
	}

	public String getSchool() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setSchool(String school) {
		// TODO Auto-generated method stub
	}
}