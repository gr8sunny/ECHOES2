/**
 * 
 */
package echoesEngine;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import FAtiMA.deliberativeLayer.goals.Goal;
import FAtiMA.deliberativeLayer.plan.Step;
import FAtiMA.exceptions.UnknownGoalException;
import FAtiMA.knowledgeBase.KnowledgeBase;
import FAtiMA.wellFormedNames.Name;
import echoesEngine.ListenerManager;
import utils.Enums.ListenerType;
import utils.Interfaces.*;
import utils.Logger;


/**
 * @author Mary Ellen Foster
 * 
 */
@SuppressWarnings("serial")
public class ControlPanel extends JFrame {

	private void log(String level, String message) {
		Logger.Log(level, message);
	}

	private void logInfo(String message) {
		log("info", message);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
  public ControlPanel(final IRenderingEngine rePrx, final EchoesAgent agent) {
		super("Action Engine Control Panel");
		setLocationByPlatform(true);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				try {
					logInfo("Calling communicator.shutdown()");
					
				} catch (NullPointerException e1) {
					System.exit(0);
				}
			}
		});

		ListenerManager listenerMgr = ListenerManager.GetInstance();
		final IAgentListener agentPublisher = (IAgentListener)listenerMgr.retrieve(ListenerType.agent);

		String[] KBparameters = new String[] { "childOpenToAgentInteraction()",
				"isInECHOES()", "hasGreetedChild()", "needToPerformAction()",
				"handsFree(Paul)", "stacking(isChosenActivity)",
				"flowerPicking(isChosenActivity)",
				"bidForChildAction(isChosenBidType)",
				"bidForTurn(isChosenBidType)",
				"requestObject(isChosenBidType)",
				"promptForInitiation(isChosenBidType)",
				"contactPoint(isChosenBidMethod)",
				"distalPoint(isChosenBidMethod)",
				"directionLevel1(isChosenBidMethod)",
				"directionLevel2(isChosenBidMethod)", "demo(isChosenPurpose)",
				"takeTurn(isChosenPurpose)",
				"justPerformAction(isChosenPurpose)" };
		String[] trueFalseValue = new String[] { "True", "False" };
		final JComboBox KBcombo = new JComboBox(KBparameters);
		final JComboBox trueFalseCombo = new JComboBox(trueFalseValue);
		JButton setKBbutton = new JButton("update KB");
		setKBbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				KnowledgeBase.GetInstance().Tell(
						Name.ParseName((String) KBcombo.getSelectedItem()),
						trueFalseCombo.getSelectedItem());
			}
		});

		String[] objects = new String[] { "Flower", "Pot" };
		final JComboBox objectCombo = new JComboBox(objects);
		String[] objIds = new String[] { "1", "2", "3", "4", "5", "6", "7" };
		final JComboBox objIdCombo = new JComboBox(objIds);
		JButton objectButton = new JButton("add object");
		objectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// String objId = rePrx.addObject((String) objectCombo
				// .getSelectedItem());
				String objId = (String) objIdCombo.getSelectedItem();
				KnowledgeBase.GetInstance().Tell(
						Name.ParseName(objId + "(type)"),
						(String) objectCombo.getSelectedItem());
				KnowledgeBase.GetInstance().Tell(
						Name.ParseName("hold(Paul," + objId + ")"), "False");
				KnowledgeBase.GetInstance().Tell(
						Name.ParseName("at(Paul," + objId + ")"), "False");
				if (objectCombo.getSelectedItem().equals("Pot")) {
					KnowledgeBase.GetInstance().Tell(
							Name.ParseName(objId + "(isStacked)"), "False");
				}
				if (objectCombo.getSelectedItem().equals("Flower")) {
					KnowledgeBase.GetInstance().Tell(
							Name.ParseName(objId + "(isInPot)"), "False");
				}
			}
		});
		final JComboBox objectTargetCombo = new JComboBox(trueFalseValue);
		JButton makeTargetButton = new JButton("make/remove target");
		makeTargetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String objId = (String) objIdCombo.getSelectedItem();
				String targetStatus = (String) objectTargetCombo
						.getSelectedItem();
				KnowledgeBase.GetInstance().Tell(
						Name.ParseName(objId + "(objectIsTarget)"),
						targetStatus);
			}
		});

		JButton userButton = new JButton("Add User");
		userButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				agent.addUser();
			}
		});

		final JComboBox agentCombo = new JComboBox(new String[] { "User",
				"Paul" });
		agentCombo.setBorder(new TitledBorder("Agent"));
		List actions = agent.getDeliberativeLayer().getEmotionalPlanner()
				.GetOperators();
		List<String> actionNames = new LinkedList<String>();
		for (Object obj : actions) {
			Step step = (Step) obj;
			actionNames.add(step.getName().GetFirstLiteral().getName());
		}
		Collections.sort(actionNames);
		final JComboBox agentActionCombo = new JComboBox(actionNames.toArray());
		agentActionCombo.setSelectedItem("LookAtFace");
		agentActionCombo.setBorder(new TitledBorder("Agent Action"));
		final JTextField agentArgsField = new JTextField("Paul", 10);
		agentArgsField.setBorder(new TitledBorder("Agent Action Arg(s)"));

		JButton agentActionButton = new JButton("Send agent action");
		agentActionButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				List<String> args = Arrays.asList(agentArgsField.getText()
						.split(" +"));
				agentPublisher.agentActionCompleted(agentCombo
						.getSelectedItem().toString(), agentActionCombo
						.getSelectedItem().toString(), args);
			}
		});

		Box agentActionBox = Box.createHorizontalBox();
		agentActionBox.add(agentCombo);
		agentActionBox.add(agentActionCombo);
		agentActionBox.add(agentArgsField);
		agentActionBox.add(agentActionButton);

		List<Goal> goals = new LinkedList<Goal>();
		for (Object obj : agent.getDeliberativeLayer().GetGoals()) {
			goals.add((Goal) obj);
		}
		// agent.getDeliberativeLayer().RemoveAllGoals();

		final JComboBox goalCombo = new JComboBox(goals.toArray());

		JButton addGoalButton = new JButton("Add Goal");
		addGoalButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Goal goal = (Goal) goalCombo.getSelectedItem();
				try {
					agent.getDeliberativeLayer().AddGoal(
							goal.GetName().toString(),
							goal.GetImportanceOfSuccess(),
							goal.GetImportanceOfFailure());
				} catch (UnknownGoalException e1) {
					e1.printStackTrace();
				}
				Logger.Log(	"info","Goals now: " + agent.getDeliberativeLayer().GetGoals());
			}
		});

		JButton resetGoalButton = new JButton("Reset Goals");
		resetGoalButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				agent.getDeliberativeLayer().RemoveAllGoals();
				Logger.Log("info","Goals now: " + agent.getDeliberativeLayer().GetGoals());
			}
		});

		// fake actions from the pedagogic component
		String[] pedComponentProps = new String[] { "PCactivityToPursue()",
				"PCrespondToUserRequest()", "PCneedToExplain()",
				"PCneedToMotivate()", "PCcontinueWithActivityIndefinitely()",
				"PCwaitLetUserAct()",
				"PCdontRequestTransformObjectStackable()",
				"PCdontRequestStackObject()", "PCdontRequestGiveObject()",
				"PCdontRequestExploreObjectProperties()",
				"PCassumeUserWontActWithoutPrompting()", "Paul(desiredObject)",
				"PCrequireEstablishAttentionToSelf()" };

		final JComboBox pedComponentCombo = new JComboBox(pedComponentProps);
		final JComboBox valueCombo = new JComboBox(new String[] { "True",
				"False", "(Remove)", "stackingActivity",
				"exploreObjectPropertiesActivity",
				"receiveTargetFlowerActivity", "whyTransformObjects",
				"stackObject", "giveObject", "dance", "object1", "object2",
				"object3", "object4", "redFlower", "blueFlower",
				"outOfReachObject" });
		JButton pcButton = new JButton("Update PC");
		pcButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Name name = Name.ParseName((String) pedComponentCombo
						.getSelectedItem());
				Object oldValue = KnowledgeBase.GetInstance().AskProperty(name);
				if (oldValue != null) {
					Logger.Log("info",
							"Removing existing value of " + name + " from KB");
					KnowledgeBase.GetInstance().Retract(name);
				}
				String newValue = (String) valueCombo.getSelectedItem();
				if (!newValue.equals("(Remove)")) {
					Logger.Log("info","Setting KB property " + name + " to " + newValue);
					KnowledgeBase.GetInstance().Tell(name, newValue);
				}
			}
		});

		Box updateKBBox = Box.createHorizontalBox();
		updateKBBox.add(KBcombo);
		updateKBBox.add(trueFalseCombo);
		updateKBBox.add(setKBbutton);

		Box addObjectsBox = Box.createHorizontalBox();
		addObjectsBox.add(objectCombo);
		addObjectsBox.add(objIdCombo);
		addObjectsBox.add(objectButton);
		addObjectsBox.add(objectTargetCombo);
		addObjectsBox.add(makeTargetButton);

		Box childModelBox = Box.createHorizontalBox();
		childModelBox.add(pedComponentCombo);
		childModelBox.add(valueCombo);
		childModelBox.add(Box.createHorizontalStrut(20));
		childModelBox.add(pcButton);

		Box goalBox = Box.createHorizontalBox();
		goalBox.add(goalCombo);
		goalBox.add(addGoalButton);
		goalBox.add(Box.createHorizontalStrut(20));
		goalBox.add(resetGoalButton);

		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		add(Box.createVerticalStrut(10));
		add(updateKBBox);
		add(Box.createVerticalStrut(10));
		add(addObjectsBox);
		add(Box.createVerticalStrut(10));
		add(agentActionBox);
		add(Box.createVerticalStrut(10));
		add(goalBox);
		add(Box.createVerticalStrut(10));
		add(childModelBox);
		add(Box.createVerticalStrut(10));

		pack();
	}

	public static void main(String[] args) {
		new ControlPanel(null, null).setVisible(true);
	}

	private static Writer logWriter;

	public static void writeLog(String text) {
		if (logWriter == null) {
			try {
				logWriter = new FileWriter("ActionEngine.txt", false);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (logWriter != null) {
			try {
				logWriter.write(text + "\n");
				logWriter.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
