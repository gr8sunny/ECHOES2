/**
 * 
 */
package stateManager;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.TitledBorder;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import utils.Interfaces.*;
import utils.Logger;

/**
 * @author Mary Ellen Foster
 *
 */
public class ActionController extends JFrame 
{
  private static final long serialVersionUID = 1L;
  private static Map<AgentActions, ScriptedAction> startActions;
  private static SortedMap<String, List<TrialDesc>> trialDescs;
  private static DateFormat dateFormat;
  private static JTextArea statusArea;
	private JButton scriptButton;
	private Script curScript;
	private JButton nextButton;
	private JLabel gazeLabel;
	private JButton endButton;
    
	private static class AgentActions 
	{
		boolean engagement;
		boolean gesture;
		boolean contingent;
		public AgentActions(boolean engagement, boolean gesture, boolean contingent) {
		this.engagement = engagement;
		this.gesture = gesture; 
		this.contingent = contingent;
	}
	
	@Override
	public int hashCode() {
		return toString().hashCode();
	}
	
	@Override
	public String toString() {
		return "en=" + engagement + ",ge=" + gesture + ",con=" + contingent;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof AgentActions && ((AgentActions)obj).engagement == engagement
			&& ((AgentActions)obj).gesture == gesture && ((AgentActions)obj).contingent == contingent;
	}
	}
	
	public ActionController(final IRenderingEngine rePrx, final StateManager smImpl)
	{
    super("Experiment controller");
		setLocationByPlatform(true);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Logger.Log("info",
                        "Calling communicator.shutdown()");
            }
        });
        
        dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.UK);
        
        // Load the scripts
        startActions = new HashMap<AgentActions, ScriptedAction>();
        trialDescs = new TreeMap<String, List<TrialDesc>>();
        final SAXBuilder builder = new SAXBuilder();
        File parentDir = new File("share/scripts");
        for (File file : parentDir.listFiles()) {
        	if (file.toString().endsWith(".xml")) {
        		if (file.toString().contains("test")) continue;
	            try {
	                Document doc = builder.build(file);
	                boolean engagement = Boolean.valueOf(doc.getRootElement().getAttributeValue("engage"));
	                boolean gestures = Boolean.valueOf(doc.getRootElement().getAttributeValue("gesture"));
	                boolean contingent = Boolean.valueOf(doc.getRootElement().getAttributeValue("contingent"));
	                for (Object obj : doc.getRootElement().getChildren("action")) {
	                    new ScriptedAction(file.getName(), (Element)obj, rePrx, this);
	                }
	                String parentId = file.getName() + doc.getRootElement().getAttributeValue("start");
	                ScriptedAction rootAction = ScriptedAction.getAction(parentId);
	                startActions.put(new AgentActions(engagement, gestures, contingent), rootAction);
	            } catch (JDOMException e) {
	                e.printStackTrace();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
        	} else if (file.getName().equals("trial-descs.csv")) {
        		try {
					BufferedReader reader = new BufferedReader(new FileReader(file));
					String line;
					while ((line = reader.readLine()) != null) {
						new TrialDesc(line);
					}
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
        	} else if (file.getName().equals("scripts-final.csv")) {
        		try {
					BufferedReader reader = new BufferedReader(new FileReader(file));
					String line;
					while ((line = reader.readLine()) != null) {
						new Script(line);
					}
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
        	}
        }
        
        trialLabel = new JLabel();

        final ChooseDialog chooseDialog = new ChooseDialog(this);
        scriptButton = new JButton("Choose script");
        scriptButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chooseDialog.setVisible(true);
				if (!chooseDialog.wasCancelled()) {
					curScript = chooseDialog.getSelectedScript();
					rePrx.setWorldProperty("UserList", curScript.getChildName());
					updateUI();
				}
				/*
				String[] scripts = Script.getScriptDescs().toArray(new String[0]);
				String scriptDesc = (String)JOptionPane.showInputDialog(ActionController.this, "Choose a script", "Choose a script", JOptionPane.QUESTION_MESSAGE, null, scripts, scripts[0]);
				if (scriptDesc != null) {
					curScript = Script.getScript(scriptDesc);
					curScript.resetCounter();
					updateUI();
				}
				*/
			}
		});
        
      introButton = new JButton("Play welcome from Paul");
      introButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {
					public void run() {
						addHistory("Showing introduction");
						curScript.playIntro(rePrx);
					}
				}).start();
			}
		});
        
        nextButton = new JButton("Start next trial");
        nextButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (curScript.hasNext()) {
					ScriptedAction.cancelAll();
					final int nextIndex = curScript.getCurTrialNum();
					if (curScript.checkIntermediate()) {
						new Thread(new Runnable() {
							public void run() {
								trialLabel.setText(curScript.getDesc() + " - now playing intermediate stuff");
								curScript.playIntermediate(rePrx, ActionController.this);
							}
						}).start();
					} else {
						TrialDesc nextTrial = curScript.next();
						nextTrial.start(nextIndex, rePrx);
						trialLabel.setText(curScript.getDesc() + " - now executing: #" + nextIndex + " (" + nextTrial + ")");
						nextButton.setEnabled(false);
					}
				}
			}
		});
        
        endButton = new JButton("End experiment");
        endButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addHistory("Ending experiment");
				new Thread(new Runnable() {
					public void run() {
						curScript.playEnding(rePrx, ActionController.this);
						curScript = null;
						updateUI();
					}
				}).start();
			}
		});
        
        Box buttonBox = Box.createHorizontalBox();
        buttonBox.add(Box.createHorizontalGlue());
        buttonBox.add(scriptButton);
        buttonBox.add(Box.createHorizontalStrut(10));
        buttonBox.add(introButton);
        buttonBox.add(Box.createHorizontalStrut(10));
        // buttonBox.add(nextButton);
        // buttonBox.add(Box.createHorizontalGlue());
        buttonBox.add(endButton);
        buttonBox.add(Box.createHorizontalGlue());
        
        gazeLabel = new JLabel("Gaze: ");
        
        statusArea = new JTextArea(20, 60);
        statusArea.setEditable(false);
        JScrollPane statusScroll = new JScrollPane(statusArea);
        statusScroll.setBorder(new TitledBorder("History"));
        statusScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        
        updateUI();
        
        trialLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        gazeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nextButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        add(trialLabel);
        add(buttonBox);
        add(Box.createVerticalStrut(10));
        add(nextButton);
        add(Box.createVerticalStrut(10));
        add(gazeLabel);
        add(statusScroll);
        
        //_RenderingListenerDisp rl = new _RenderingListenerDisp();
	}

	public void userStarted(String name) 
	{
		Logger.Log("info", "user started: " + name);
		/*
		new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				rePrx.endScenario("Intro");
				curScript.playIntroPart2(rePrx, ActionController.this);
			}
		}).start();
		*/
	}

	public void worldPropertyChanged(String propName, String propValue) {}
  public void userTouchedObject(String objId) {}
	public void scenarioStarted(String name) {}	
	public void scenarioEnded(String name) {}	
	public void objectRemoved(String objId) {}	
	public void objectPropertyChanged(String objId, String propName)	{}
	public void objectAdded(String objId, Map<String, String> props) {}
	public void agentRemoved(String agentId) {}
	public void agentPropertyChanged(String agentId, String propName,String propValue) {}
	public void agentAdded(String agentId, Map<String, String> props) {}
	public void userTouchedAgent(String agentId) {}

	
	public void shutdown() {
		//renderingTopic.unsubscribe(rlPrx);
	}
	
	public static ScriptedAction getScript(boolean engagement, boolean gesture, boolean contingent) {
		return startActions.get(new AgentActions(engagement, gesture, contingent));
	}
	
	public void showGaze(String details) {
		gazeLabel.setText("Gaze: " + details);
	}
    
    private void updateUI() {
    	if (curScript == null) {
    		trialLabel.setText("No script loaded");
    		nextButton.setEnabled(false);
    		introButton.setEnabled(false);
    		endButton.setEnabled(false);
    	} else {
    		introButton.setEnabled(true);
    		endButton.setEnabled(true);
	    	synchronized(curScript) {
	    		if (curScript.hasNext()) {
	    			trialLabel.setText(curScript.getDesc() + " - next trial: #" + curScript.getCurTrialNum() + "(" + curScript.peek() + ")");
	    			nextButton.setEnabled(true);
	    		} else {
	    			trialLabel.setText("No more trials");
	    			nextButton.setEnabled(false);
	    		}
    		}
    	}
    }
    
	public void setVisible(boolean visible) {
		super.setVisible(visible);
	}

  public static void addHistory(String history) {
  	Logger.Log("info", history);
      statusArea.setText(statusArea.getText() + "[" + dateFormat.format(new Date()) + "] " + history + "\n\n");
      statusArea.setCaretPosition(statusArea.getDocument().getLength());
  }
    
	private JLabel trialLabel;
	private JButton introButton;

	public void trialDone() 
	{
    addHistory("Trial is done!");
    updateUI();
  }
	
	public static Collection<String> getTrialDescNames() {
		return trialDescs.keySet();
	}
}
