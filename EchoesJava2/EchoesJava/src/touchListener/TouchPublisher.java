package touchListener;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;
import utils.Interfaces.ITouchListener;

@SuppressWarnings("serial")
public class TouchPublisher extends JFrame {
  
  @SuppressWarnings("rawtypes")
  public TouchPublisher(final ITouchListener publisher) {
      super("Touch publisher");
      setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
      setLocationByPlatform(true);
      addWindowListener(new WindowAdapter() {
          @Override
          public void windowClosing(WindowEvent e) {
          }
      });

      final Map<Integer, Point> points = new HashMap<Integer, Point>();
      
      // Set up the window ...
      @SuppressWarnings("unchecked")
      final JComboBox idCombo = new JComboBox(new Integer[] { 0, 1, 2, 3, 4 });
      
      final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      
      SpinnerModel xModel = new SpinnerNumberModel(screenSize.width / 2, 0, screenSize.width, 25);
      final JSpinner xSpinner = new JSpinner(xModel);
      SpinnerModel yModel = new SpinnerNumberModel(screenSize.height / 2, 0, screenSize.height, 25);
      final JSpinner ySpinner = new JSpinner(yModel);
      
      final JButton clickButton = new JButton("Click");
      final JButton updateButton = new JButton("Add point");
      final JButton deleteButton = new JButton("Delete point");
      deleteButton.setEnabled(false);
      
      idCombo.addActionListener(new ActionListener() {
      
          public void actionPerformed(ActionEvent arg0) {
              Integer id = (Integer)idCombo.getSelectedItem();
              Point point = points.get(id);
              if (point != null) {
                  xSpinner.setValue(point.x);
                  ySpinner.setValue(point.y);
                  updateButton.setText("Update point");
                  deleteButton.setEnabled(true);
              } else {
                  xSpinner.setValue(screenSize.width / 2);
                  ySpinner.setValue(screenSize.height / 2);
                  updateButton.setText("Add point");
                  deleteButton.setEnabled(false);
              }
          }
      });
      
      clickButton.addActionListener(new ActionListener() {
      
          public void actionPerformed(ActionEvent e) {
              Point point = new Point((Integer)xSpinner.getValue(), (Integer)ySpinner.getValue());
              publisher.click(point.x, point.y, 40, 40);
          }
      });
      
      updateButton.addActionListener(new ActionListener() {
      
          public void actionPerformed(ActionEvent e) {
              Integer id = (Integer)idCombo.getSelectedItem();
              Point point = points.get(id);
              Point newPoint = new Point((Integer)xSpinner.getValue(), (Integer)ySpinner.getValue());
              System.out.println("point " + point + "; newPoint " + newPoint);
              if (point == null || (point != null && !point.equals(newPoint))) {
                  System.out.println("pointDown");
                  publisher.pointDown(id, newPoint.x, newPoint.y, 40, 40);
              } else {
                  System.out.println("pointMoved");
                  publisher.pointMoved(id, newPoint.x, newPoint.y, 40, 40);
              }
              points.put(id, newPoint);
              updateButton.setText("Update point");
              deleteButton.setEnabled(true);
          }
      });
      
      deleteButton.addActionListener(new ActionListener() {
      
          public void actionPerformed(ActionEvent e) {
              Integer id = (Integer)idCombo.getSelectedItem();
              Point point = points.get(id);
              if (point != null) {
                  publisher.pointUp(id);
                  points.remove(id);
                  updateButton.setText("Add point");
                  deleteButton.setEnabled(false);
              }
          }
      });
      
      Box propsBox = Box.createHorizontalBox();
      propsBox.add(new JLabel("Point ID"));
      propsBox.add(idCombo);
      propsBox.add(Box.createHorizontalStrut(10));
      propsBox.add(new JLabel("Coords ("));
      propsBox.add(xSpinner);
      propsBox.add(new JLabel(","));
      propsBox.add(ySpinner);
      propsBox.add(new JLabel(")"));
      
      Box buttonBox = Box.createHorizontalBox();
      buttonBox.add(clickButton);
      buttonBox.add(updateButton);
      buttonBox.add(deleteButton);

      getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
      add(propsBox);
      add(buttonBox);
      
      pack();
  }
}
