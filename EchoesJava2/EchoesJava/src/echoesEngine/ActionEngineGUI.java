/**
 * 
 */
package echoesEngine;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import utils.Interfaces.IRenderingEngine;

/**
 * @author mef
 * 
 */
@SuppressWarnings("serial")
public class ActionEngineGUI extends JFrame {

    public ActionEngineGUI(final IRenderingEngine rePrx) {
        super("Action engine controller");
        setLocationByPlatform(true);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
            }
        });
        Box controlsBox = Box.createVerticalBox();

        final JSpinner bubbleSpinner = new JSpinner();
        JButton bubbleButton = new JButton("Send");
        Box bubbleBox = Box.createHorizontalBox();
        bubbleBox.add(new JLabel("numBubbles"));
        bubbleBox.add(bubbleSpinner);
        bubbleBox.add(bubbleButton);

        bubbleButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                rePrx.setWorldProperty("numBubbles", String.valueOf(bubbleSpinner.getValue()));
            }
        });
        controlsBox.add(bubbleBox);

        final JSpinner flowerSpinner = new JSpinner();
        JButton flowerButton = new JButton("Send");
        Box flowerBox = Box.createHorizontalBox();
        flowerBox.add(new JLabel("numFlowers"));
        flowerBox.add(flowerSpinner);
        flowerBox.add(flowerButton);

        flowerButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                rePrx.setWorldProperty("numFlowers", String.valueOf(flowerSpinner.getValue()));
            }
        });
        controlsBox.add(flowerBox);

        JToggleButton floorButton = new JToggleButton("Toggle Floor");
        floorButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                JToggleButton bt = (JToggleButton) arg0.getSource();
                rePrx.setWorldProperty("floorFlag", String.valueOf(bt.isSelected()));
            }
        });
        controlsBox.add(floorButton);

        JToggleButton sunButton = new JToggleButton("Toggle Sun");
        sunButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                JToggleButton bt = (JToggleButton) arg0.getSource();
                rePrx.setWorldProperty("sunFlag", String.valueOf(bt.isSelected()));
            }
        });
        controlsBox.add(sunButton);

        JToggleButton beeButton = new JToggleButton("Toggle Bee");
        beeButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                JToggleButton bt = (JToggleButton) arg0.getSource();
                rePrx.setWorldProperty("beeFlag", String.valueOf(bt.isSelected()));
            }
        });
        controlsBox.add(beeButton);

        JToggleButton axisButton = new JToggleButton("Toggle Axis");
        axisButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                JToggleButton bt = (JToggleButton) arg0.getSource();
                rePrx.setWorldProperty("axisFlag", String.valueOf(bt.isSelected()));
            }
        });
        controlsBox.add(axisButton);

        add(controlsBox);
        pack();
    }

}
