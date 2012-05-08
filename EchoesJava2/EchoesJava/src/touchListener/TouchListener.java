package touchListener;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import utils.Interfaces.ITouchListener;
import utils.Logger;

public class TouchListener implements ITouchListener
{
  private TouchPanel touchPanel;
  
  public TouchListener() {
      JFrame frame = new JFrame("Touch demo");
      frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
      frame.addWindowListener(new WindowAdapter() {
          @Override
          public void windowClosing(WindowEvent e) {
          }
      });
      
      touchPanel = new TouchPanel();
      frame.add(touchPanel);
      
      /*
      GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
      GraphicsDevice gs = ge.getDefaultScreenDevice();
      gs.setFullScreenWindow(frame);
      */
      frame.pack();
      frame.setVisible(true);
      frame.setExtendedState(Frame.MAXIMIZED_BOTH);
  }

  public synchronized void pointDown(int id, int x, int y, int width, int height) {
      touchPanel.addEllipse(id, x, y, width, height);
      Logger.Log("INFO", "Point " + id + " down at (" + x + "," + y + "); size " + width + "x" + height);
  }

  public synchronized void pointMoved(int id, int newX, int newY, int newWidth, int newHeight) {
      touchPanel.moveEllipse(id, newX, newY, newWidth, newHeight);
      Logger.Log("INFO", "Point " + id + " moved to (" + newX + "," + newY + "); size " + newWidth + "x" + newHeight);
  }

  public synchronized void pointUp(int id) {
      touchPanel.removeEllipse(id);
      Logger.Log("INFO", "Point " + id + " up");
  }
  
  public void click (int x, int y, int width, int height) {
  	touchPanel.addClick (x, y, width, height);
      Logger.Log("INFO", "Click at (" + x + "," + y + ") " + width + "x" + height);
  }
}
