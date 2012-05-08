/**
 * 
 */
package controlPanel;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerModel;
import utils.Enums.*;
import utils.Enums.EchoesActivity;
import utils.Interfaces.IPedagogicComponent;


/**
 * @author Mary Ellen Foster
 * 
 */
@SuppressWarnings("serial")
public class NavigationPanel extends JPanel {

	public NavigationPanel(final IPedagogicComponent pcPrx) {
		final JComboBox<EchoesActivity> activityCombo = new JComboBox<EchoesActivity>();
		activityCombo.setMaximumSize(new Dimension(1000, activityCombo
				.getPreferredSize().height));

		final JComboBox<?> scertsCombo = new JComboBox<Object>(ScertsGoal.values());
		scertsCombo.setSelectedItem(ScertsGoal.ExtendedInteraction);
		scertsCombo.setMaximumSize(new Dimension(1000, activityCombo
				.getPreferredSize().height));
		scertsCombo.setSelectedIndex(-1);

		scertsCombo.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ScertsGoal goal = (ScertsGoal) scertsCombo.getSelectedItem();
				activityCombo.removeAllItems();
				// Based on Katerina's documentation
				switch (goal) {
				case BriefInteraction:
					activityCombo.addItem(EchoesActivity.FlowerTurnToBall);
					activityCombo.addItem(EchoesActivity.FlowerGrow);
					activityCombo.addItem(EchoesActivity.CloudRain);
					break;

				case ExtendedInteraction:
					activityCombo.addItem(EchoesActivity.FlowerPickToBasket);
					activityCombo
							.addItem(EchoesActivity.PotStackRetrieveObject);
					break;



				case InitiateSocialGame:
					activityCombo.addItem(EchoesActivity.FlowerPickToBasket);
					activityCombo.addItem(EchoesActivity.FlowerTurnToBall);
					activityCombo.addItem(EchoesActivity.FlowerGrow);
					activityCombo.addItem(EchoesActivity.CloudRain);
					activityCombo
							.addItem(EchoesActivity.PotStackRetrieveObject);
					activityCombo.addItem(EchoesActivity.AgentPoke);
                    activityCombo.addItem(EchoesActivity.TickleAndTree);
					break;
				}
			}
		});

		SpinnerModel spinnerModel = new SpinnerListModel(new Object[] { 1, 2,
				3, 4, 5, 6, 7, 8, 9, 10, 15, 20, "Unlimited" });
		final JSpinner numRepetitions = new JSpinner(spinnerModel);
		numRepetitions.setValue(5);
		numRepetitions.setMaximumSize(new Dimension(1000, numRepetitions
				.getPreferredSize().height));

		Box scertsBox = Box.createHorizontalBox();
		scertsBox.add(new JLabel("SCERTS goal:"));
		scertsBox.add(Box.createHorizontalStrut(10));
		scertsBox.add(scertsCombo);

		Box activityBox = Box.createHorizontalBox();
		activityBox.add(new JLabel("Activity:"));
		activityBox.add(Box.createHorizontalStrut(10));
		activityBox.add(activityCombo);

		Box repBox = Box.createHorizontalBox();
		repBox.add(new JLabel("Repetitions:"));
		repBox.add(Box.createHorizontalStrut(10));
		repBox.add(numRepetitions);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(scertsBox);
		add(activityBox);
		add(repBox);

		JButton changeButton = new JButton("Update activity");
		changeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// can't directly set this
			}
		});

		Box updateBox = Box.createHorizontalBox();
		updateBox.add(Box.createHorizontalGlue());
		updateBox.add(changeButton);
		updateBox.add(Box.createHorizontalGlue());

		add(updateBox);

		add(Box.createVerticalGlue());
	}

}
