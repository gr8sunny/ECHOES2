/**
 * 
 */
package pedagogicComponent;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JTextField;
import utils.Interfaces.*;
import utils.Enums.*;
import utils.Logger;
import pedagogicComponent.data.ChildAction;

/**
 * @author Mary Ellen Foster
 */
public class PCcontrolPanel extends JFrame {

	/**
   * 
   */
  private static final long serialVersionUID = 1L;
  PCcomponentHandler pCcompH;
	IDramaManager dmPrx;

	public PCcontrolPanel(final PCcomponentHandler pCcompH,
			final IDramaManager dmPrx) {
		super("Pedagogic Component Control Panel");

		// Shut down the whole app if the window is closed
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				try 
				{
					Logger.Log("info", "Getting admin interface, shutting down registry and communicator");

				} catch (NullPointerException e1) {
					System.exit(0);
				}
			}
		});

		this.pCcompH = pCcompH;
		this.dmPrx = dmPrx;

		setLocationByPlatform(true);

		final JTextField childName = new JTextField("Type in the child's name");
		childName.selectAll();
		JButton setIntroScene = new JButton(
				"Set Introduction scene with child name");
		setIntroScene.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pCcompH.getPCcs().agentH.setAgentGoal("wait");
				dmPrx.setIntroScene(EchoesScene.Intro, childName.getText());
			}
		});

		JButton startBubbleSceneButton = new JButton("start bubble scene");
		startBubbleSceneButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pCcompH.getPCcs().nonAgentSceneH
						.decideNonAgentSceneParameters(EchoesScene.Bubbles);
			}
		});

		JButton startGardenSceneButton = new JButton("start garden scene");
		startGardenSceneButton.addActionListener(new ActionListener() {

			// String[] engagementValues = new String[] { "disengaged",
			// "engaged" };
			// final JComboBox engagementCombo = new
			// JComboBox(engagementValues);
			// JButton setChildEnagementButton = new
			// JButton("set child engagement");
			// setChildEnagementButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// if (((String) engagementCombo.getSelectedItem())
				// .equals("engaged")) {
				// pCcompH.getPCcs().childStateH.loadInitialChildAttributes(
				// appropriateStartScene, bubbleComplexity,
				// displayScore, numRepetitions, isOpenToAgent,
				// goalAbilityMap, childProfileGoals, activityLikeMap,
				// objectLikeMap);

				// pCcompH.getPCcs().childStateH.setChildEngagement(true);
				// } else if (((String) engagementCombo.getSelectedItem())
				// .equals("disengaged")) {
                pCcompH.getPCcs().childStateH.loadInitialChildAttributes(childName.getText());

				dmPrx.setScene(EchoesScene.Garden);
				dmPrx.arrangeScene(EchoesScene.Garden, EchoesActivity.Explore,
						1, false);
				pCcompH.getPCcs().agentH.directAgentChangeInvolvement(false);

			}
			// }
		});

		JButton agentEnterButton = new JButton("get agent to enter and greet");
		agentEnterButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dmPrx.setScene(EchoesScene.GardenTask);
				pCcompH.getPCcs().agentH.setAgentGoal("enterECHOES");
			}
		});

		String[] agentFocus = new String[] { EchoesObjectType.Cloud.toString(),
				EchoesActivity.FlowerGrow.toString(),
				EchoesActivity.FlowerPickToBasket.toString(),
				EchoesActivity.PotStackRetrieveObject.toString(),
				EchoesActivity.FlowerTurnToBall.toString(),
				EchoesActivity.BallThrowing.toString(),
				EchoesActivity.BallSorting.toString(),
				EchoesActivity.TickleAndTree.toString(),
				EchoesActivity.ExploreWithAgent.toString(),
				EchoesActivity.FlowerTurnToBallContingent.toString(),
				EchoesActivity.BallThrowingContingent.toString(), };
		final JComboBox<?> agentFocusCombo = new JComboBox<Object>(agentFocus);
		JButton agentInvButton = new JButton("Set agent focus");
		agentInvButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String focus = (String) agentFocusCombo.getSelectedItem();
				EchoesObjectType object = null;
				EchoesActivity activity = null;
				if (EchoesObjectType.Cloud.toString().equals(focus)) {
					object = EchoesObjectType.Cloud;
					activity = EchoesActivity.CloudRain;
				} else if (EchoesActivity.FlowerGrow.toString().equals(focus)) {
					object = EchoesObjectType.Cloud;
					activity = EchoesActivity.FlowerGrow;
				} else if (EchoesActivity.FlowerPickToBasket.toString().equals(
						focus)) {
					object = EchoesObjectType.Flower;
					activity = EchoesActivity.FlowerPickToBasket;
				} else if (EchoesActivity.PotStackRetrieveObject.toString()
						.equals(focus)) {
					object = EchoesObjectType.Pot;
					activity = EchoesActivity.PotStackRetrieveObject;
				} else if (EchoesActivity.FlowerTurnToBall.toString().equals(
						focus)) {
					object = EchoesObjectType.Flower;
					activity = EchoesActivity.FlowerTurnToBall;
				} else if (EchoesActivity.BallThrowing.toString().equals(focus)) {
					object = EchoesObjectType.Ball;
					activity = EchoesActivity.BallThrowing;
				} else if (EchoesActivity.BallSorting.toString().equals(focus)) {
					object = EchoesObjectType.Ball;
					activity = EchoesActivity.BallSorting;
				} else if (EchoesActivity.TickleAndTree.toString()
						.equals(focus)) {
					// no object type relevant
					activity = EchoesActivity.TickleAndTree;
				} else if (EchoesActivity.ExploreWithAgent.toString().equals(
						focus)) {
					// no object type relevant
					activity = EchoesActivity.ExploreWithAgent;
				} else if (EchoesActivity.FlowerTurnToBallContingent.toString()
						.equals(focus)) {
					object = EchoesObjectType.Flower;
					activity = EchoesActivity.FlowerTurnToBall;
				} else if (EchoesActivity.BallThrowingContingent.toString()
						.equals(focus)) {
					object = EchoesObjectType.Ball;
					activity = EchoesActivity.BallThrowing;
				}

				if (focus.equals(EchoesActivity.BallThrowingContingent
						.toString())
						|| focus
								.equals(EchoesActivity.FlowerTurnToBallContingent
										.toString())) {
					pCcompH.getPCcs().agentH.setActivityContingent(true);
				} else {
					pCcompH.getPCcs().agentH.setActivityContingent(false);
				}

				pCcompH.getPCcs().agentH.setNextActivityAndObject(activity,
						object);
				pCcompH.getPCcs().agentH.setAgentGoal("walkOff");
			}
		});

		JButton childNoActionButton = new JButton(
				"child is not acting (agent will repeat bid)");
		childNoActionButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ArrayList<String> childActionDetails = new ArrayList<String>();
				childActionDetails.add("");
				pCcompH.getPCcs().childActionH.handleChildAction(
						ChildAction.noAction.getName(), childActionDetails);
				// pCcompH.getPCcs().agentH.setAgentGoal("dontWait");
			}
		});

		JButton stopAgentWaiting = new JButton(
				"press to get agent stop waiting (agent will take his turn)");
		stopAgentWaiting.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pCcompH.getPCcs().agentH.setAgentGoal("dontWait");
			}
		});

		JButton leaveButton = new JButton("press to to get Andy to leave");
		leaveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pCcompH.getPCcs().agentH.setAgentGoal("leave");
			}
		});

		String[] childModelChanges = new String[] { "engaged", "disengaged",
				"bored", "frustrated", "motivated",
				"childIsOpenToAgentInteraction",
				"childIsNotOpenToAgentInteraction" };

		final JComboBox<?> childModelCombo = new JComboBox<Object>(childModelChanges);

		JButton cmButton = new JButton("Update CM value");
		cmButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String childModelValue = (String) childModelCombo
						.getSelectedItem();
				if (childModelValue.equals("engaged")) {
					pCcompH.getPCcs().childStateH.setEngagedECHOES(true);
				} else if (childModelValue.equals("disengaged")) {
					pCcompH.getPCcs().childStateH.setEngagedECHOES(false);
				} else if (childModelValue.equals("bored")) {
					pCcompH.getPCcs().childStateH.setAffectiveState("bored");
				} else if (childModelValue.equals("frustrated")) {
					pCcompH.getPCcs().childStateH
							.setAffectiveState("frustrated");
				} else if (childModelValue.equals("motivated")) {
					pCcompH.getPCcs().childStateH
							.setAffectiveState("motivated");
				} else if (childModelValue
						.equals("childIsOpenToAgentInteraction")) {
				} else if (childModelValue
						.equals("childIsNotOpenToAgentInteraction")) {

				}
			}
		});

		Box agentInvBox = Box.createHorizontalBox();
		agentInvBox.add(agentFocusCombo);
		agentInvBox.add(agentInvButton);

		Box introSceneBox = Box.createHorizontalBox();
		introSceneBox.add(childName);
		introSceneBox.add(setIntroScene);

		Box bubbleSceneBox = Box.createHorizontalBox();
		bubbleSceneBox.add(startBubbleSceneButton);
		
		Box engagementBox = Box.createHorizontalBox();
		// engagementBox.add(engagementCombo);
		// engagementBox.add(setChildEnagementButton);
		engagementBox.add(startGardenSceneButton);

		Box addAgentBox = Box.createHorizontalBox();
		addAgentBox.add(agentEnterButton);

		Box leavingBox = Box.createHorizontalBox();
		leavingBox.add(leaveButton);

		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		add(Box.createVerticalStrut(10));
		add(introSceneBox);
		add(Box.createVerticalStrut(10));
		add(bubbleSceneBox);
		add(Box.createVerticalStrut(10));
		add(engagementBox);
		add(Box.createVerticalStrut(10));
		add(addAgentBox);

		add(Box.createVerticalStrut(10));
		add(agentInvBox);
		add(Box.createVerticalStrut(10));

		add(childNoActionButton);
		add(Box.createVerticalStrut(10));

		add(stopAgentWaiting);
		add(Box.createVerticalStrut(10));

		add(leavingBox);
		add(Box.createVerticalStrut(10));

		/*
		 * add(Box.createVerticalStrut(10)); add(activitiesBox);
		 * add(Box.createVerticalStrut(10)); add(practitionerBox);
		 * 
		 * add(Box.createVerticalStrut(10)); add(childActionPossibilities);
		 * add(Box.createVerticalStrut(10)); add(childActionBox);
		 * 
		 * add(Box.createVerticalStrut(10)); add(childStateChanges);
		 * add(Box.createVerticalStrut(10)); add(childModelBox);
		 * 
		 * add(Box.createVerticalStrut(10)); add(activityEnd);
		 * add(Box.createVerticalStrut(10)); add(newActivityButton);
		 * add(Box.createVerticalStrut(10));
		 */

		pack();

	}
	/*
	 * final JComboBox argCombo = new JComboBox(new String[] {"(none)"});
	 * argCombo.setBorder(new TitledBorder("Child Model change"));
	 * 
	 * final JComboBox objectIdCombo = new JComboBox(new String[] {"(none)"});
	 * for (String objId : objIds) { objectIdCombo.addItem(objId); }
	 * 
	 * JButton targetButton = new JButton("Make target");
	 * targetButton.addActionListener(new ActionListener() { public void
	 * actionPerformed(ActionEvent e) { String objId =
	 * objectIdCombo.getSelectedItem().toString(); if (!objId.equals("(none)"))
	 * { KnowledgeBase.GetInstance().Tell(new ComposedName("Paul",
	 * "targetFlower"), objId);
	 * Application.communicator().getLogger().trace("info",
	 * KnowledgeBase.GetInstance().toString()); } } });
	 * 
	 * JButton userButton = new JButton("Add User");
	 * userButton.addActionListener(new ActionListener() { public void
	 * actionPerformed(ActionEvent e) { agent.addUser(); } });
	 * 
	 * final JComboBox agentCombo = new JComboBox(new String[] {"User",
	 * "Paul"}); agentCombo.setBorder(new TitledBorder("Agent")); List actions =
	 * agent.getDeliberativeLayer().getEmotionalPlanner().GetOperators();
	 * List<String> actionNames = new LinkedList<String>(); for (Object obj :
	 * actions) { Step step = (Step)obj;
	 * actionNames.add(step.getName().GetFirstLiteral().getName()); }
	 * Collections.sort(actionNames); final JComboBox agentActionCombo = new
	 * JComboBox(actionNames.toArray());
	 * agentActionCombo.setSelectedItem("LookAtFace");
	 * agentActionCombo.setBorder(new TitledBorder("Agent Action")); final
	 * JTextField agentArgsField = new JTextField("Paul", 10);
	 * agentArgsField.setBorder(new TitledBorder("Agent Action Arg(s)"));
	 * 
	 * JButton agentActionButton = new JButton("Send agent action");
	 * agentActionButton.addActionListener(new ActionListener() { public void
	 * actionPerformed(ActionEvent e) { List<String> args =
	 * Arrays.asList(agentArgsField.getText().split(" +"));
	 * agentPublisher.agentActionCompleted
	 * (agentCombo.getSelectedItem().toString(),
	 * agentActionCombo.getSelectedItem().toString(), args); } });
	 * 
	 * Box agentActionBox = Box.createHorizontalBox();
	 * agentActionBox.add(agentCombo); agentActionBox.add(agentActionCombo);
	 * agentActionBox.add(agentArgsField);
	 * agentActionBox.add(agentActionButton);
	 * 
	 * List<Goal> goals = new LinkedList<Goal>(); for (Object obj :
	 * agent.getDeliberativeLayer().GetGoals()) { goals.add((Goal)obj); } //
	 * agent.getDeliberativeLayer().RemoveAllGoals();
	 * 
	 * final JComboBox goalCombo = new JComboBox(goals.toArray());
	 * 
	 * JButton addGoalButton = new JButton("Add Goal");
	 * addGoalButton.addActionListener(new ActionListener() { public void
	 * actionPerformed(ActionEvent e) { Goal goal =
	 * (Goal)goalCombo.getSelectedItem(); try {
	 * agent.getDeliberativeLayer().AddGoal(goal.GetName().toString(),
	 * goal.GetImportanceOfSuccess(), goal.GetImportanceOfFailure()); } catch
	 * (UnknownGoalException e1) { e1.printStackTrace(); }
	 * Application.communicator().getLogger().trace("info", "Goals now: " +
	 * agent.getDeliberativeLayer().GetGoals()); } });
	 * 
	 * JButton resetGoalButton = new JButton("Reset Goals");
	 * resetGoalButton.addActionListener(new ActionListener() { public void
	 * actionPerformed(ActionEvent e) {
	 * agent.getDeliberativeLayer().RemoveAllGoals();
	 * Application.communicator().getLogger().trace("info", "Goals now: " +
	 * agent.getDeliberativeLayer().GetGoals()); } });
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * Box goalBox = Box.createHorizontalBox(); goalBox.add(goalCombo);
	 * goalBox.add(addGoalButton); goalBox.add(Box.createHorizontalStrut(20));
	 * goalBox.add(resetGoalButton);
	 * 
	 * Box topBox = Box.createHorizontalBox(); topBox.add(objectIdCombo);
	 * topBox.add(targetButton); topBox.add(Box.createHorizontalGlue());
	 * 
	 * setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
	 * add(Box.createVerticalStrut(10)); add(topBox);
	 * add(Box.createVerticalStrut(10)); add(agentActionBox);
	 * add(Box.createVerticalStrut(10)); add(goalBox);
	 * add(Box.createVerticalStrut(10)); add(childModelBox);
	 * add(Box.createVerticalStrut(10));
	 * 
	 * pack(); }
	 * 
	 * public static void main(String[] args) { new PCcontrolPanel(null, null,
	 * new LinkedList<String>()).setVisible(true); }
	 * 
	 * private static Writer logWriter; public static void writeLog(String text)
	 * { if (logWriter == null) { try { logWriter = new
	 * FileWriter("ActionEngine.txt", false); } catch (IOException e) {
	 * e.printStackTrace(); } } if (logWriter != null) { try {
	 * logWriter.write(text + "\n"); logWriter.flush(); } catch (IOException e)
	 * { e.printStackTrace(); } } }
	 */

	public static void main(String[] args)
	{
	    new PCcontrolPanel(null, null).setVisible(true);
	}
}
