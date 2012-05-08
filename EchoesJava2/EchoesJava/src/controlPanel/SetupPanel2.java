package controlPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import utils.Interfaces.*;

@SuppressWarnings("serial")
public class SetupPanel2 extends JPanel {
	
	private class ModelInfo {
		public String childName;
		public String fileName;
		
		public ModelInfo(String childName, String fileName) {
			this.childName = childName;
			this.fileName = fileName;
		}

		public String toString() {
			return childName + " (" + fileName + ")";
		}
	}
	
	public SetupPanel2(final IChildModel cmPrx, final IHeadTracker htPrx) {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		JButton newCMButton = new JButton("Create new model");
		newCMButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String name = JOptionPane.showInputDialog(SetupPanel2.this, "Enter child's name");
				if (name == null) {
					return;
				}
				Integer[] ages = new Integer[] { 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };
				Integer age = (Integer) JOptionPane.showInputDialog(SetupPanel2.this,
						"Enter child's age", "Enter child's age", JOptionPane.QUESTION_MESSAGE,
						null, ages, 10);
				if (age == null) {
					return;
				}
				cmPrx.createModel();
				cmPrx.setChildName(name);
				cmPrx.setAge(age);
			}
		});
		
		JButton loadCMButton = new JButton("Load existing model");
		loadCMButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				List<ModelInfo> modelList = new LinkedList<ModelInfo>();
				for (String fileName : cmPrx.listModels()) {
					modelList.add(new ModelInfo(cmPrx.getChildNameForFile(fileName), fileName));
				}
				if (modelList.isEmpty()) {
					JOptionPane.showMessageDialog(SetupPanel2.this, "No models to load", "No models to load", JOptionPane.WARNING_MESSAGE);
				} else {
					ModelInfo[] modelArray = modelList.toArray(new ModelInfo[modelList.size()]);
					ModelInfo selection = (ModelInfo)JOptionPane.showInputDialog(SetupPanel2.this, "Choose a model",
							"Choose a model", JOptionPane.QUESTION_MESSAGE, null, modelArray, modelArray[0]);
					if (selection != null) {
						cmPrx.loadModel(selection.fileName);
					}
				}
			}
		});
		
		JButton saveCMButton = new JButton("Save current model");
		saveCMButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				cmPrx.saveModel();
			}
		});
		
		JButton editCMButton = new JButton("Edit current model");
		editCMButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new ChildModelDialog(null, cmPrx).setVisible(true);
			}
		});
		
		Box cmBox = Box.createVerticalBox();
		cmBox.setBorder(new TitledBorder("Child model"));
		
		cmBox.add(newCMButton);
		cmBox.add(Box.createVerticalStrut(10));
		cmBox.add(loadCMButton);
		cmBox.add(Box.createVerticalStrut(10));
		cmBox.add(editCMButton);
		cmBox.add(Box.createVerticalStrut(10));
		cmBox.add(saveCMButton);
		cmBox.add(Box.createVerticalGlue());
		
		add(cmBox);

		JButton trackerButton = new JButton("Restart head tracker");
        final JButton skipButton = new JButton("Skip 3D model");
        final JButton modelButton = new JButton("Build 3D model");

        trackerButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			}});
        
        modelButton.setEnabled(htPrx != null);
        modelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (htPrx != null) {
					if (htPrx.buildHeadModel()) {
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
        
    Box trackerBox = Box.createVerticalBox();
    trackerBox.setBorder(new TitledBorder("Head tracker"));
    trackerBox.add(trackerButton);
    trackerBox.add(Box.createVerticalStrut(10));
    trackerBox.add(modelButton);
    trackerBox.add(Box.createVerticalStrut(10));
    trackerBox.add(skipButton);
		trackerBox.add(Box.createVerticalGlue());

    add(trackerBox);
    
    JButton restartButton = new JButton("Restart ECHOES");
    restartButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			}});
        
        Box generalBox = Box.createVerticalBox();
        generalBox.setBorder(new TitledBorder("General controls"));
        generalBox.add(restartButton);
        generalBox.add(Box.createVerticalGlue());
        add(generalBox);
	}
}
