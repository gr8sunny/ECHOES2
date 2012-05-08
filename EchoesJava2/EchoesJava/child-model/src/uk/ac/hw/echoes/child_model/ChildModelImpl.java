/**
 * 
 */
package uk.ac.hw.echoes.child_model;

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

import Ice.Application;
import Ice.Current;
import echoes.EchoesActivity;
import echoes.EchoesObjectType;
import echoes.Engagement;
import echoes.ScertsGoal;
import echoes._ChildModelDisp;

public class ChildModelImpl extends _ChildModelDisp implements Serializable {
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
	
	private EngagementClassifier eClassifier;
	private ChildModelDetails cmDetails;
	
	public ChildModelImpl(EngagementClassifier eClassifier) {
		this.eClassifier = eClassifier;
	}

	private ChildModelDetails loadDetails (String fileName) {
		// Find the correct model with this name
		File file = new File(MODEL_DIRECTORY, fileName);
		try {
			ObjectInputStream stream = new ObjectInputStream(new FileInputStream(file));
			return (ChildModelDetails) stream.readObject();
		} catch (FileNotFoundException e) {
			Application.communicator().getLogger().warning(
					"Unable to load model from " + file + ": " + e.getMessage());
		} catch (IOException e) {
			Application.communicator().getLogger().warning(
					"Unable to load model from " + file + ": " + e.getMessage());
		} catch (ClassNotFoundException e) {
			Application.communicator().getLogger().warning(
					"Unable to load model from " + file + ": " + e.getMessage());
		}
		return null;
	}
	
	@Override
	public void createModel(Current current) {
		if (cmDetails != null) {
			saveModel(current);
		}
		cmDetails = new ChildModelDetails();
	}

	@Override
	public void loadModel(String fileName, Current current) {
		if (cmDetails != null) {
			saveModel(current);
		}
		cmDetails = loadDetails (fileName);
		if (cmDetails != null) {
			Application.communicator().getLogger().trace("info", "Loaded child model for '" + cmDetails.name + "' from " + fileName);
		}
	}
	
	@Override
	public String getChildNameForFile (String fileName, Current current) {
		ChildModelDetails details = loadDetails(fileName);
		if (details != null) {
			return details.name;
		} else {
			return null;
		}
	}

	@Override
	public void saveModel(Current current) {
		if (cmDetails != null) {
			File directory = new File(MODEL_DIRECTORY);
			if (cmDetails.fileName == null) {
				// Choose a unique file name
				if (cmDetails.name == null) {
					cmDetails.name = "Nobody";
				}
				try {
					cmDetails.fileName = File.createTempFile(cmDetails.name, ".model", directory).getName();
					Application.communicator().getLogger().trace("info", "Selected file name is " + cmDetails.fileName);
				} catch (IOException e) {
					Application.communicator().getLogger().warning("Unable to create file for model saving: " + e.getMessage());
				}
			}
			try {
				File file = new File(directory, cmDetails.fileName);
				ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(file));
				stream.writeObject(cmDetails);
				Application.communicator().getLogger().trace("info", "Saved model for " + cmDetails.name + " to " + file);
			} catch (IOException e) {
				Application.communicator().getLogger().warning("Unable to save model: " + e.getMessage());
			}
		} else {
			Application.communicator().getLogger().warning("Not saving null child model");
		}
	}
	
	@Override
	public List<String> listModels (Current current) {
		File directory = new File(MODEL_DIRECTORY);
		List<String> models = new LinkedList<String>();
		for (File file : directory.listFiles()) {
			try {
				ObjectInputStream stream = new ObjectInputStream(new FileInputStream(file));
				@SuppressWarnings("unused")
				ChildModelDetails details = (ChildModelDetails)stream.readObject();
				models.add(file.getName());
			} catch (FileNotFoundException e) {
				Application.communicator().getLogger().warning("Unable to load model from " + file + ": " + e.getMessage());
			} catch (IOException e) {
				Application.communicator().getLogger().warning("Unable to load model from " + file + ": " + e.getMessage());
			} catch (ClassNotFoundException e) {
				Application.communicator().getLogger().warning("Unable to load model from " + file + ": " + e.getMessage());
			}
		}
		Collections.sort(models);
		return models;
	}

	@Override
	public void setTargetObject(String objId, Current current) {
		if (eClassifier != null) {
			ChildModelApp.communicator().getLogger().trace("info", "Set target object to " + objId);
			eClassifier.setTargetObject(objId);
		}
	}

	@Override
	public Engagement getEngagement(Current current) {
		return eClassifier.getEngagement();
	}

	@Override
	public int getAbility(ScertsGoal goal, Current current) {
		return cmDetails.goalAbilityMap.get(goal);
	}

	@Override
	public int getActivityValue(EchoesActivity activity, Current current) {
		return cmDetails.activityMap.get(activity);
	}

	@Override
	public int getBubbleComplexity(Current current) {
		return cmDetails.bubbleComplexity;
	}

	@Override
	public int getGoalLevelOfDirection(ScertsGoal goal, Current current) {
		return GOAL_LEVELS.get(goal);
	}

	@Override
	public int getObjectValue(EchoesObjectType echoesObject, Current current) {
		return cmDetails.objectTypeMap.get(echoesObject);
	}

	@Override
	public int getOverallLevelOfDirection(Current current) {
		return cmDetails.levelDirection;
	}

	@Override
	public boolean isOpenToAgent(Current current) {
		return cmDetails.openToAgent;
	}

	@Override
	public boolean displayScore(Current current) {
		return cmDetails.displayScore;
	}

	@Override
	public int getAge(Current current) {
		return cmDetails.age;
	}

	@Override
	public String getChildName(Current current) {
		return cmDetails.name;
	}

	@Override
	public int getNumRepetitions(Current current) {
		return cmDetails.numRepetitions;
	}

	@Override
	public void setAbility(ScertsGoal goal, int ability, Current current) {
		cmDetails.goalAbilityMap.put(goal, ability);
	}

	@Override
	public void setActivityValue(EchoesActivity activity, int value,
			Current current) {
		cmDetails.activityMap.put(activity, value);
	}

	@Override
	public void setBubbleComplexity(int complexity, Current current) {
		cmDetails.bubbleComplexity = complexity;
	}

	@Override
	public void setDisplayScore(boolean display, Current current) {
		cmDetails.displayScore = display;
	}

	@Override
	public void setNumRepetitions(int numRepetitions, Current current) {
		cmDetails.numRepetitions = numRepetitions;
	}

	@Override
	public void setObjectValue(EchoesObjectType objectType, int value,
			Current current) {
		cmDetails.objectTypeMap.put(objectType, value);
	}

	@Override
	public void setOpenToAgent(boolean open, Current current) {
		cmDetails.openToAgent = open;
	}

	@Override
	public void setOverallLevelOfDirection(int level, Current current) {
		cmDetails.levelDirection = level;
	}

	@Override
	public void setAge(int age, Current current) {
		cmDetails.age = age;
	}

	@Override
	public void setChildName(String name, Current current) {
		cmDetails.name = name;
	}

	@Override
	public String getSchool(Current current) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSchool(String school, Current current) {
		// TODO Auto-generated method stub
		
	}
}