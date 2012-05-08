package touchListener;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class TouchPanel extends JPanel {
    
    private Map<Integer, Ellipse2D> points;
    private List<Ellipse2D> clicks;
    
    public TouchPanel() {
        this.points = Collections.synchronizedMap(new HashMap<Integer, Ellipse2D>());
        this.clicks = Collections.synchronizedList(new LinkedList<Ellipse2D>());
        setBackground(Color.WHITE);
    }
    
    private Point2D convertPoint (double centreX, double centreY, double width, double height) {
        // Step 1: screen coordinates to panel coordinates
        Point p = new Point((int)centreX, (int)centreY);
        SwingUtilities.convertPointFromScreen(p, this);
        
        // Step 2: we have the middle, we need the top left
        double topLeftX = p.x - (width/2);
        double topLeftY = p.y - (height/2);
        
        return new Point2D.Double(topLeftX, topLeftY);
    }
    
    public void addEllipse (int id, int centreX, int centreY, int width, int height) {
        // Translate the coordinates
        Point2D topLeft = convertPoint(centreX, centreY, width, height);
        Ellipse2D ellipse = new Ellipse2D.Double(topLeft.getX(), topLeft.getY(), width, height);
        points.put(id, ellipse);
        repaint();
    }
    
    public Ellipse2D removeEllipse (int id) {
        Ellipse2D ellipse = points.remove(id);
        repaint();
        return ellipse;
    }
    
    public void moveEllipse (int id, int centreX, int centreY, int width, int height) {
        Ellipse2D oldEllipse = points.get(id);
        if (oldEllipse != null) {
            Point2D topLeft = convertPoint(centreX, centreY, width, height);
            Ellipse2D newEllipse = new Ellipse2D.Double(topLeft.getX(), topLeft.getY(), width, height);
            points.put(id, newEllipse);
            repaint();
        }
    }

	public void addClick(double x, double y, double width, double height) {
        Point2D topLeft = convertPoint(x, y, width, height);
		Ellipse2D click = new Ellipse2D.Double(topLeft.getX(), topLeft.getY(), width, height);
		clicks.add(click);
		repaint();
	}

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        synchronized(clicks) {
	        g2.setColor(Color.RED.darker());
	        for (Ellipse2D ellipse : clicks) {
	        	g2.fill(ellipse);
	        }
        }
        g2.setColor(Color.GREEN.darker());
        synchronized(points) {
	        for (Ellipse2D ellipse : points.values()) {
	            g2.fill(ellipse);
	        }
        }
    }

}
