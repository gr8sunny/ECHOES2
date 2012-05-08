/**
 * 
 */
package controlPanel;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import utils.Interfaces.*;

/**
 * @author Mary Ellen Foster
 *
 */
@SuppressWarnings("serial")
public class SetupPanel extends JPanel 
{
    public SetupPanel(final IPedagogicComponent pcPrx, final IHeadTracker htPrx, final IChildModel cmPrx) 
    {
        List<JLabel> childLabels = new LinkedList<JLabel>();
        childLabels.add(new JLabel("Name:", SwingConstants.RIGHT));
        childLabels.add(new JLabel("Age:", SwingConstants.RIGHT));

        List<JComponent> childComponents = new LinkedList<JComponent>();
        final JComboBox<?> nameCombo = new JComboBox<Object>(cmPrx.listModels().toArray());
        nameCombo.setEditable(true);
        childComponents.add(nameCombo);

        JComboBox<?> ageCombo = new JComboBox<Object>(new Object[] { "Under 5", 5, 6, 7, 8, 9, 10, 11, 12, 13, "Over 13" });
        childComponents.add(ageCombo);

        // Compute some sizes now
        Box compBox = Box.createVerticalBox();
        int maxHeight = 0, maxWidth = 0;
        for (JComponent comp : childComponents) {
            Dimension prefSize = comp.getPreferredSize();
            if (prefSize.height > maxHeight)
                maxHeight = prefSize.height;
            if (prefSize.width > maxWidth)
                maxWidth = prefSize.width;
        }
        for (JComponent comp : childComponents) {
            comp.setPreferredSize(new Dimension(maxWidth, maxHeight));
            comp.setMaximumSize(new Dimension(1000, maxHeight));
            compBox.add(comp);
        }
        compBox.add(Box.createVerticalGlue());

        Box labelBox = Box.createVerticalBox();
        int maxLabelWidth = 0;
        for (JLabel label : childLabels) {
            Dimension prefSize = label.getPreferredSize();
            if (prefSize.width > maxLabelWidth)
                maxLabelWidth = prefSize.width;
        }
        for (JLabel label : childLabels) {
            label.setPreferredSize(new Dimension(maxLabelWidth, maxHeight));
            label.setMaximumSize(label.getPreferredSize());
            label.setMinimumSize(label.getPreferredSize());
            labelBox.add(label);
        }
        labelBox.add(Box.createVerticalGlue());

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        for (int i = 0; i < childLabels.size(); i++) {
            Box box = Box.createHorizontalBox();
            box.add(Box.createHorizontalStrut(10));
            box.add(childLabels.get(i));
            box.add(Box.createHorizontalStrut(10));
            box.add(childComponents.get(i));
            box.add(Box.createHorizontalStrut(10));
            add(box);
        }
        add(Box.createVerticalGlue());

        JButton startButton = new JButton("Start new user");
        startButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				pcPrx.loadChildProfile((String)nameCombo.getSelectedItem());
				cmPrx.loadModel((String)nameCombo.getSelectedItem());
			}
		});
        
        JButton saveModelButton = new JButton("Save CM");
        saveModelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				cmPrx.saveModel();
			}
		});
        
    JButton restartButton = new JButton("Restart ECHOES");
    restartButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0)
      {}});
        
    JButton trackerButton = new JButton("(Re)Start head tracker");
    final JButton skipButton = new JButton("Skip 3D model");
    final JButton modelButton = new JButton("Build 3D model");

    trackerButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0)
      {}});
			
        
    modelButton.setEnabled(htPrx != null);
    modelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (htPrx != null) {
					if (!htPrx.buildHeadModel()) {
						JOptionPane.showMessageDialog(SetupPanel.this, "Model building failed", "Model building failed", JOptionPane.WARNING_MESSAGE);
					} else {
						modelButton.setEnabled(false);
						skipButton.setEnabled(false);
					}
				}
			}
		});
        
        skipButton.setEnabled(htPrx != null);
        skipButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (htPrx != null) {
					htPrx.skipHeadModel();
					modelButton.setEnabled(false);
					skipButton.setEnabled(false);
				}
			}
		});
        
        Box startBox = Box.createHorizontalBox();
        startBox.add(Box.createHorizontalGlue());
        startBox.add(restartButton);
        startBox.add(Box.createHorizontalGlue());
        startBox.add(trackerButton);
        startBox.add(Box.createHorizontalGlue());
        startBox.add(modelButton);
        startBox.add(Box.createHorizontalGlue());
        startBox.add(skipButton);
        startBox.add(Box.createHorizontalGlue());
        startBox.add(startButton);
        startBox.add(Box.createHorizontalGlue());
        startBox.add(saveModelButton);
        startBox.add(Box.createHorizontalGlue());
        add(startBox);
        
        add(Box.createVerticalStrut(10));
    }
}
