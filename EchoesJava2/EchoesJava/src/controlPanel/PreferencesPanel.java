/**
 * 
 */
package controlPanel;

import java.util.Dictionary;
import java.util.Hashtable;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;

/**
 * @author Mary Ellen Foster
 *
 */
@SuppressWarnings("serial")
public class PreferencesPanel extends JPanel {

    public PreferencesPanel() {
        JSlider volume = new JSlider(0, 10);
        volume.setMajorTickSpacing(1);
        Dictionary<Integer, JComponent> volumeLabels = new Hashtable<Integer, JComponent>();
        volumeLabels.put(0, new JLabel("Quiet"));
        volumeLabels.put(10, new JLabel("Loud"));
        volume.setLabelTable(volumeLabels);
        volume.setPaintLabels(true);
        volume.setPaintTicks(true);
        volume.setSnapToTicks(true);
        volume.setBorder(new TitledBorder("Volume"));

        JSlider brightness = new JSlider(0, 10);
        brightness.setMajorTickSpacing(1);
        Dictionary<Integer, JComponent> brightnessLabels = new Hashtable<Integer, JComponent>();
        brightnessLabels.put(0, new JLabel("Dark"));
        brightnessLabels.put(10, new JLabel("Bright"));
        brightness.setLabelTable(brightnessLabels);
        brightness.setPaintLabels(true);
        brightness.setPaintTicks(true);
        brightness.setSnapToTicks(true);
        brightness.setBorder(new TitledBorder("Brightness"));

        JSlider speed = new JSlider(0, 10);
        speed.setMajorTickSpacing(1);
        Dictionary<Integer, JComponent> speedLabels = new Hashtable<Integer, JComponent>();
        speedLabels.put(0, new JLabel("Slow"));
        speedLabels.put(10, new JLabel("Fast"));
        speed.setLabelTable(speedLabels);
        speed.setPaintLabels(true);
        speed.setPaintTicks(true);
        speed.setSnapToTicks(true);
        speed.setBorder(new TitledBorder("Response speed"));
        
        JSlider complexity = new JSlider(1, 2);
        Dictionary<Integer, JComponent> complexityLabels = new Hashtable<Integer, JComponent>();
        complexityLabels.put(1, new JLabel("Simple"));
        complexityLabels.put(2, new JLabel("Complex"));
        complexity.setLabelTable(complexityLabels);
        complexity.setPaintLabels(true);
        complexity.setBorder(new TitledBorder("Language level"));
        complexity.setSnapToTicks(true);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(volume);
        add(brightness);
        add(speed);
        add(complexity);
    }
}
