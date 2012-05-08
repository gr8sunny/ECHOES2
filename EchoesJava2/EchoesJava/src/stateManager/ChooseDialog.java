package stateManager;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ChooseDialog extends JDialog 
{
  private static final long serialVersionUID = -6717450087507617827L;
  private JTextField childNameField;
	private JComboBox<?> startNumCombo;
	private JComboBox<?> scriptCombo;
	private boolean wasCancelled;
	
	public ChooseDialog(ActionController controller) {
		super(controller, "Setup Experiment", true);

		Integer[] startNumbers = new Integer[36];
		for (int i = 0; i < startNumbers.length; i++) {
			startNumbers[i] = i+1;
		}
		startNumCombo = new JComboBox<Object>(startNumbers);
		childNameField = new JTextField(20);
		scriptCombo = new JComboBox<Object>(Script.getScriptDescs().toArray());
		
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new GridLayout(0, 2));
		topPanel.add(new JLabel("Script"));
		topPanel.add(scriptCombo);
		topPanel.add(new JLabel("Child name (optional)"));
		topPanel.add(childNameField);
		topPanel.add(new JLabel("Start number (optional)"));
		topPanel.add(startNumCombo);
		
		wasCancelled = false;
		
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				wasCancelled = false;
				setVisible(false);
			}
		});
		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				wasCancelled = true;
				setVisible(false);
			}
		});
		
		Box buttonBox = Box.createHorizontalBox();
		buttonBox.add(Box.createHorizontalGlue());
		buttonBox.add(okButton);
		buttonBox.add(Box.createHorizontalStrut(10));
		buttonBox.add(cancelButton);
		buttonBox.add(Box.createHorizontalGlue());
		
		setLayout(new BorderLayout());
		add(topPanel, BorderLayout.CENTER);
		add(buttonBox, BorderLayout.SOUTH);
		pack();
	}
	
	public Script getSelectedScript() {
		Script script = Script.getScript(scriptCombo.getSelectedItem().toString());
		if(!childNameField.getText().isEmpty()) {
			script.setChildName(childNameField.getText().trim());
		}
		script.resetCounter();
		int startNum = (Integer)startNumCombo.getSelectedItem();
		if (startNum > 1) {
			script.setCounter(startNum);
		}
		return script;
	}
	public boolean wasCancelled() {
		return wasCancelled;
	}
	
	@Override
	public void setVisible(boolean visible) {
		if (visible) {
			startNumCombo.setSelectedIndex(0);
		}
		super.setVisible(visible);
	}
}
