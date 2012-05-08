/**
 * 
 */
package controlPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import utils.Enums.ListenerType;
import utils.Interfaces.*;
import utils.Logger;
import echoesEngine.ListenerManager;

/**
 * @author Mary Ellen Foster
 * 
 */
@SuppressWarnings("serial")
public class ControlPanel extends JFrame 
{
	boolean paused = false;

	public ControlPanel(IPedagogicComponent pcPrx, IHeadTracker htPrx, IChildModel cmPrx) 
	{
		super("ECHOES control panel (version 0.3)");
		setLocationByPlatform(true);

		// Shut down the whole app if the window is closed
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) 
			{
				Logger.Log("info","Calling communicator.shutdown()");
			}
		});

		// Publish to the pause listener
		ListenerManager listenerMgr = ListenerManager.GetInstance();
		final IPauseListener pausePublisher = (IPauseListener)listenerMgr.retrieve(ListenerType.pause);
		
		JTabbedPane tabPane = new JTabbedPane();

		tabPane.addTab("Setup", new SetupPanel2(cmPrx, htPrx));

		tabPane.addTab("Preferences", new PreferencesPanel());

		tabPane.addTab("Navigation", new NavigationPanel(pcPrx));

		setLayout(new BorderLayout());
		add(tabPane, BorderLayout.NORTH);

		final JButton pauseButton = new JButton("PAUSE SYSTEM");
		pauseButton.setBackground(Color.RED);
		pauseButton.setForeground(Color.WHITE);
		pauseButton.setFont(pauseButton.getFont().deriveFont(36.0f));

		pauseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				paused = !paused;
				pausePublisher.setPaused(paused);
				if (!paused) {
					pauseButton.setBackground(Color.RED);
					pauseButton.setText("PAUSE SYSTEM");
				} else {
					pauseButton.setBackground(Color.GRAY);
					pauseButton.setText("Resume");
				}
			}
		});

		JTextArea historyArea = new JTextArea(5, 0);
		historyArea.setBorder(new TitledBorder("Interaction History"));
		historyArea.setEditable(false);

		add(pauseButton, BorderLayout.CENTER);

		Box bottomBox = Box.createVerticalBox();
		bottomBox.add(historyArea);

		Box actionBox = Box.createHorizontalBox();
		actionBox.setBorder(new TitledBorder("Next system action"));
		actionBox.add(new JTextField(20));
		actionBox.add(new JButton("Confirm"));
		actionBox.add(new JButton("Change ..."));

		bottomBox.add(actionBox);

		add(bottomBox, BorderLayout.SOUTH);

		pack();
	}
}
