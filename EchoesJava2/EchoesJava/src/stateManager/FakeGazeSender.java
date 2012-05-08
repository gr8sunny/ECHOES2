package stateManager;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import echoesEngine.ListenerManager;
import utils.Interfaces.IUserHeadListener;
import utils.Enums.*;

public class FakeGazeSender extends JFrame 
{
  private static final long serialVersionUID = 1L;

  public FakeGazeSender(final FusionImpl fusionImpl) 
	{
		super("Fake gaze sender");

		setLocationByPlatform(true);
		
		final JComboBox<?> regionCombo = new JComboBox<Object>(ScreenRegion.values());
		JButton sendButton = new JButton("Send");

		ListenerManager listenerMgr = ListenerManager.GetInstance();
    final IUserHeadListener gazePublisher = (IUserHeadListener)listenerMgr.retrieve(ListenerType.userHead);
        
    sendButton.addActionListener(new ActionListener() {
			
		public void actionPerformed(ActionEvent e) {
			gazePublisher.gaze((ScreenRegion) regionCombo.getSelectedItem());
			}
		});
        
    final JSlider thresholdSlider = new JSlider(0, 1000, 200);
    Box sliderBox = Box.createHorizontalBox();
    sliderBox.add(new JLabel("Threshold"));
    sliderBox.add(thresholdSlider);
    
    thresholdSlider.setMajorTickSpacing(200);
    thresholdSlider.setMinorTickSpacing(100);
    thresholdSlider.setPaintTicks(true);
    thresholdSlider.setPaintLabels(true);
    
    thresholdSlider.addChangeListener(new ChangeListener() {
			
		public void stateChanged(ChangeEvent e) {
			if (!thresholdSlider.getValueIsAdjusting()) {
				int threshold = thresholdSlider.getValue();
				fusionImpl.setGazeTimeout(threshold);
			}
		}
		});

    setLayout(new FlowLayout());
    add(sliderBox);
    add(regionCombo);
    add(sendButton);
    
    pack();
	}
}
