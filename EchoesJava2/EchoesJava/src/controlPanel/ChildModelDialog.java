package controlPanel;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import utils.Interfaces.IChildModel;
import utils.Enums.*;

public class ChildModelDialog extends JDialog 
{
  private static final long serialVersionUID = 1L;

  public ChildModelDialog (JFrame parent, final IChildModel cmPrx) 
	{
		super(parent, "Child properties", true);
		
		JPanel demoPanel = new JPanel();
		demoPanel.setBorder(new TitledBorder("Demographics"));
		demoPanel.setLayout(new GridLayout(0, 2));
		
		// Basic information
		JLabel childNameLabel = new JLabel("Child name");
		final JTextField childNameField = new JTextField(cmPrx.getChildName());
		demoPanel.add(childNameLabel);
		demoPanel.add(childNameField);
		
		JLabel childAgeLabel = new JLabel("Child age");
		SpinnerModel ageSpinnerModel = new SpinnerNumberModel(cmPrx.getAge(), 5, 15, 1);
		final JSpinner childAgeSpinner = new JSpinner(ageSpinnerModel);
		demoPanel.add(childAgeLabel);
		demoPanel.add(childAgeSpinner);
		

		JPanel infoPanel = new JPanel();
		infoPanel.setBorder(new TitledBorder("Basic information"));
		infoPanel.setLayout(new GridLayout(0, 2));
		
		// Boolean information
		final JCheckBox openCheckBox = new JCheckBox("Open to agent", cmPrx.isOpenToAgent());
		final JCheckBox scoreCheckBox = new JCheckBox("Display score", cmPrx.displayScore());
		infoPanel.add (openCheckBox);
		infoPanel.add (scoreCheckBox);
		
		// Integer information
		JLabel bubbleLabel = new JLabel("Number of bubbles");
		SpinnerModel bubbleSpinnerModel = new SpinnerNumberModel(cmPrx.getBubbleComplexity(), 1, 6, 1);
		final JSpinner bubbleSpinner = new JSpinner(bubbleSpinnerModel);
		infoPanel.add(bubbleLabel);
		infoPanel.add(bubbleSpinner);
		
		JLabel repLabel = new JLabel("Number of repetitions");
		SpinnerModel repSpinnerModel = new SpinnerNumberModel(cmPrx.getNumRepetitions(), 0, 10, 1);
		final JSpinner repSpinner = new JSpinner(repSpinnerModel);
		infoPanel.add(repLabel);
		infoPanel.add(repSpinner);
		
		JLabel directionLabel = new JLabel("Level of direction");
		SpinnerModel directionSpinnerModel = new SpinnerNumberModel(cmPrx.getOverallLevelOfDirection(), 0, 2, 1);
		final JSpinner directionSpinner = new JSpinner(directionSpinnerModel);
		infoPanel.add(directionLabel);
		infoPanel.add(directionSpinner);
		

		JPanel abilityPanel = new JPanel();
		abilityPanel.setLayout(new GridLayout(0, 2));
		
		JLabel label = new JLabel ("Goal");
		label.setFont(label.getFont().deriveFont(16.0f).deriveFont(Font.BOLD));
		Font font = label.getFont();
		abilityPanel.add(label);
		label = new JLabel("Ability");
		label.setFont(font);
		abilityPanel.add(label);
		
		final Map<ScertsGoal, ButtonGroup> scertsGroups = new HashMap<ScertsGoal, ButtonGroup>();
		for (ScertsGoal goal : ScertsGoal.values()) {
			abilityPanel.add(new JLabel(goal.toString()));
			ButtonGroup group = new ButtonGroup();
			JPanel buttonPanel = new JPanel(new GridLayout(1, 0));
			for (int i = 0; i <= 2; i++) {
				JRadioButton rb = new JRadioButton(String.valueOf(i));
				rb.setActionCommand(String.valueOf(i));
				rb.setSelected(cmPrx.getAbility(goal) == i); 
				group.add(rb);
				buttonPanel.add(rb);
			}
			abilityPanel.add(buttonPanel);
			scertsGroups.put(goal, group);
		}
		
		JPanel activityPanel = new JPanel(new GridLayout(0, 2));
		
		label = new JLabel("Activity");
		label.setFont(font);
		activityPanel.add(label);
		label = new JLabel("Value");
		label.setFont(font);
		activityPanel.add(label);
		
		final Map<EchoesActivity, ButtonGroup> activityGroups = new HashMap<EchoesActivity, ButtonGroup>();
		for (EchoesActivity activity : EchoesActivity.values()) {
			activityPanel.add(new JLabel(activity.toString()));
			ButtonGroup group = new ButtonGroup();
			JPanel buttonPanel = new JPanel(new GridLayout(1, 0));
			for (int i = 0; i <= 2; i++) {
				JRadioButton rb = new JRadioButton(String.valueOf(i));
				rb.setActionCommand(String.valueOf(i));
				rb.setSelected(cmPrx.getActivityValue(activity) == i); 
				group.add(rb);
				buttonPanel.add(rb);
			}
			activityPanel.add(buttonPanel);
			activityGroups.put(activity, group);
		}
		
		JPanel objectPanel = new JPanel(new GridLayout(0, 2));
		
		label = new JLabel("Object type");
		label.setFont(font);
		objectPanel.add(label);
		label = new JLabel("Value");
		label.setFont(font);
		objectPanel.add(label);
		
		final Map<EchoesObjectType, ButtonGroup> objectGroups = new HashMap<EchoesObjectType, ButtonGroup>();
		for (EchoesObjectType object : EchoesObjectType.values()) {
			objectPanel.add(new JLabel(object.toString()));
			ButtonGroup group = new ButtonGroup();
			JPanel buttonPanel = new JPanel(new GridLayout(1, 0));
			for (int i = 0; i <= 2; i++) {
				JRadioButton rb = new JRadioButton(String.valueOf(i));
				rb.setActionCommand(String.valueOf(i));
				rb.setSelected(cmPrx.getObjectValue(object) == i); 
				group.add(rb);
				buttonPanel.add(rb);
			}
			objectPanel.add(buttonPanel);
			objectGroups.put(object, group);
		}
		
		JTabbedPane tabPane = new JTabbedPane();
		tabPane.add("SCERTS goals", abilityPanel);
		tabPane.add("Activities", activityPanel);
		tabPane.add("Objects", objectPanel);
		
		JButton saveButton = new JButton("OK");
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cmPrx.setChildName(childNameField.getText());
				cmPrx.setAge((Integer)childAgeSpinner.getValue());
				cmPrx.setOpenToAgent(openCheckBox.isSelected());
				cmPrx.setDisplayScore(scoreCheckBox.isSelected());
				cmPrx.setBubbleComplexity((Integer)bubbleSpinner.getValue());
				cmPrx.setNumRepetitions((Integer)repSpinner.getValue());
				cmPrx.setOverallLevelOfDirection((Integer)directionSpinner.getValue());
				
				for (ScertsGoal goal : ScertsGoal.values()) {
					int value = Integer.valueOf(scertsGroups.get(goal).getSelection().getActionCommand());
					cmPrx.setAbility(goal, value);
				}
				for (EchoesActivity activity : EchoesActivity.values()) {
					int value = Integer.valueOf(activityGroups.get(activity).getSelection().getActionCommand());
					cmPrx.setActivityValue(activity, value);
				}
				for (EchoesObjectType objectType : EchoesObjectType.values()) {
					int value = Integer.valueOf(objectGroups.get(objectType).getSelection().getActionCommand());
					cmPrx.setObjectValue(objectType, value);
				}
				
				dispose();
			}
		});
		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		Box buttonBox = Box.createHorizontalBox();
		buttonBox.add(Box.createHorizontalGlue());
		buttonBox.add(saveButton);
		buttonBox.add(Box.createHorizontalStrut(10));
		buttonBox.add(cancelButton);
		buttonBox.add(Box.createHorizontalGlue());
		
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		add(demoPanel);
		add(infoPanel);
		add(tabPane);
		add(buttonBox);
		
		pack();
	}

}
