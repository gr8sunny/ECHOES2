package pedagogicComponent;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import pedagogicComponent.InterventionOptions.Intervention;
import pedagogicComponent.Utilities.AbstractServerAction;
import pedagogicComponent.Utilities.AbstractToggleAction;
import pedagogicComponent.Utilities.Clock;
import pedagogicComponent.Utilities.ComboHandler;
import pedagogicComponent.Utilities.TitledPanel;
import pedagogicComponent.Utilities.TypedComboBox;

/**
 * A window for practitioners to use to control the system.
 * 
 * @author Elaine Farrow
 */
public class PractitionerWindow extends JFrame
{
    /**
   * 
   */
  private static final long serialVersionUID = 1L;

    /**
     * Do we want to use the pop-up interventions?
     */
    private static final boolean USE_POPUP_INTERVENTIONS = false;

    /**
     * The colour for controls for child actions.
     */
    private static final Color CHILD_COLOR = Color.CYAN;

    /**
     * The colour for controls for agent actions.
     */
    private static final Color AGENT_COLOR = Color.PINK;

    /**
     * The link to the rest of the system.
     */
    private final PractitionerServer server;

    /**
     * The field for typing the child's name.
     */
    private final JTextField childNameField = createChildNameField();

    /**
     * The initial learning activities.
     */
    private final TypedComboBox <Activity> initialActivityCombo = createInitialActivityCombo();

    /**
     * The later learning activities.
     */
    private final TypedComboBox <Activity> laterActivityCombo = createLaterActivityCombo();

    /**
     * The field for status messages.
     */
    private final JTextField statusField = Utilities.createStatusField();

    /**
     * The 'start introduction scene' action.
     */
    protected final Action startIntroSceneAction = createStartIntroSceneAction();

    /**
     * The 'start bubble scene' action.
     */
    protected final Action startBubbleSceneAction = createStartBubbleSceneAction();

    /**
     * The 'start garden scene' action.
     */
    protected final Action startGardenSceneAction = createStartGardenSceneAction();

    /**
     * The 'end session' action.
     */
    protected final Action endSessionAction = createEndSessionAction();

    /**
     * The 'tell the agent to prompt again' action.
     */
    protected final Action agentPromptAgainAction = createAgentPromptAgainAction();

    /**
     * The 'tell the agent to stop waiting' action.
     */
    protected final Action agentTakeTurnAction = createAgentTakeTurnAction();

    /**
     * The 'tell the agent to enter' action.
     */
    protected final Action agentEnterAction = createAgentEnterAction();

    /**
     * The 'tell the agent to leave' action.
     */
    protected final Action agentLeaveAction = createAgentLeaveAction();

    /**
     * The pause/ resume action.
     */
    private final ToggleAction pauseResumeAction = createPauseResumeAction();

    /**
     * The tickle control action.
     */
    private final ToggleAction tickleControlAction = createTickleControlAction();

    /**
     * The intervention controls.
     */
    private final InterventionOptions interventionOptions = new InterventionOptions(this);

    /**
     * The activity record.
     */
    private ActivityRecord activityRecord;

    /**
     * Create a new PractitionerWindow with the given server.
     * 
     * @param server
     * the PractitionerServer providing a link to the rest of the system.
     */
    public PractitionerWindow(final PractitionerServer server)
    {
        super("Practitioner Controls");

        this.server = server;

        // Shut down the whole app if the window is closed
        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                try
                {
                    server.shutdown();
                }
                catch (NullPointerException e1)
                {
                    System.exit(0);
                }
            }
        });

        setLocationByPlatform(true);
        setLayout(new BorderLayout());

        addPopupListeners();
        add(createSessionControlPanel(), BorderLayout.CENTER);
        add(createStatusPanel(), BorderLayout.PAGE_END);

        setSessionStarted(false);

        pack();
    }

    /**
     * Get the link to the rest of the system.
     * 
     * @return the link to the rest of the system.
     */
    protected PractitionerServer getServer()
    {
        return server;
    }

    /**
     * Update the status bar with the given text.
     * 
     * @param text
     * text to display in the status bar.
     */
    protected void updateStatus(String text)
    {
        if (text == null)
        {
            text = "";
        }

        statusField.setText(text);
    }

    /**
     * Get the contents of the child name field.
     * 
     * @return the child's name.
     */
    protected String getChildName()
    {
        return childNameField.getText();
    }

    /**
     * Get the current selection from the learning activity combo.
     * 
     * @return the activity.
     */
    protected Activity getInitialActivity()
    {
        return initialActivityCombo.getSelectedObject();
    }

    /**
     * Create the status panel.
     * 
     * @return the panel.
     */
    private JPanel createStatusPanel()
    {
        JPanel buttonPanel = new JPanel(new GridLayout(1, 0, 10, 10));
        buttonPanel.add(createPauseResumeButton());
        buttonPanel.add(createEndSessionButton());

        List <JComponent> components = new ArrayList <JComponent>();

        components.add(statusField);
        components.add(createClockDisplay());
        components.add(buttonPanel);

        JPanel panel = createPanel(components, BoxLayout.LINE_AXIS);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        return panel;
    }

    /**
     * Create the session control panel.
     * 
     * @return the panel.
     */
    private JPanel createSessionControlPanel()
    {
        JPanel panel = new JPanel(new GridLayout(1, 0));

        panel.add(createActivityOptionsPanel());
        panel.add(createAdvancedOptionsPanel());

        return panel;
    }

    /**
     * Create the activity options panel.
     * 
     * @return the panel.
     */
    private JPanel createActivityOptionsPanel()
    {
        List <JComponent> components = new ArrayList <JComponent>();

        components.add(createInitialActivityPanel());
        components.add(createLaterActivityPanel());

        return createPanel(components, BoxLayout.PAGE_AXIS);
    }

    /**
     * Create the initial activity panel.
     * 
     * @return the panel.
     */
    private JPanel createInitialActivityPanel()
    {
        List <JComponent> components = new ArrayList <JComponent>();

        components.add(Utilities.createPanel(initialActivityCombo));
        components.add(createChildNamePanel());

        return createPanel("Choose initial activity",
                           components,
                           BoxLayout.PAGE_AXIS);
    }

    /**
     * Create the later activity panel.
     * 
     * @return the panel.
     */
    private JPanel createLaterActivityPanel()
    {
        List <JComponent> components = new ArrayList <JComponent>();

        components.add(Utilities.createPanel(laterActivityCombo));
        components.add(Utilities.createButtonPanel(agentEnterAction));

        return createPanel("Choose next activity",
                           components,
                           BoxLayout.PAGE_AXIS);
    }

    /**
     * Create and show the activity record dialog.
     */
    public void showActivityRecord()
    {
        if (activityRecord == null)
        {
            activityRecord = new ActivityRecord(this);
        }

        activityRecord.setVisible(true);
    }

    /**
     * Hide the activity record dialog.
     */
    public void hideActivityRecord()
    {
        if (activityRecord != null)
        {
            activityRecord.close();
        }
    }

    /**
     * Create the clock display.
     * 
     * @return the clock display.
     */
    private JComponent createClockDisplay()
    {
        Clock clock = getServer().getClock();

        return Utilities.createClockDisplay(clock, "mm:ss", 500);
    }

    /**
     * Create the pause/ resume action.
     * 
     * @return the action.
     */
    private ToggleAction createPauseResumeAction()
    {
        return new ToggleAction("Pause", "Resume")
        {
            /**
           * 
           */
          private static final long serialVersionUID = 1L;

            public void toggleChanged(JComponent comp, boolean isAction1)
            {
                comp.setBackground(isAction1 ? Color.YELLOW : Color.GREEN);
            }

            public void serverActionPerformed(ActionEvent e, boolean isAction1)
            {
                if (isAction1)
                {
                    getServer().pause();
                }
                else
                {
                    getServer().resume();
                }
            }

            public String getDescription()
            {
                return super.getDescription() + " system";
            }
        };
    }

    /**
     * Create the pause/ resume button.
     * 
     * @return the button.
     */
    private JButton createPauseResumeButton()
    {
        JButton button = Utilities.createButton(pauseResumeAction);
        pauseResumeAction.register(button);

        return button;
    }

    /**
     * Create the tickle control action.
     * 
     * @return the action.
     */
    private ToggleAction createTickleControlAction()
    {
        return new ToggleAction("Make Andy respond to tickles",
                                "Make Andy ignore tickles")
        {
            /**
           * 
           */
          private static final long serialVersionUID = 1L;

            public void serverActionPerformed(ActionEvent e, boolean isAction1)
            {
                getServer().setCanTickleAgent(isAction1);
            }
        };
    }

    /**
     * Create the 'start introduction scene' action.
     * 
     * @return the action.
     */
    private Action createStartIntroSceneAction()
    {
        return new ServerAction("Play introduction scene")
        {
            /**
           * 
           */
          private static final long serialVersionUID = 1L;

            public void serverActionPerformed(ActionEvent e)
            {
                getServer().playIntroScene(getChildName(), getInitialActivity());
            }

            public String getDescription()
            {
                return "Play intro for " + getChildName();
            }

            public void finalClientAction(ActionEvent e, Exception ex)
            {
                super.finalClientAction(e, ex);

                if (ex == null || isTestRun())
                {
                    setSessionStarted(true);
                }
            }
        };
    }

    /**
     * Create the 'start bubble scene' action.
     * 
     * @return the action.
     */
    private Action createStartBubbleSceneAction()
    {
        return new ServerAction("Start the bubble scene")
        {
            /**
           * 
           */
          private static final long serialVersionUID = 1L;

            public void serverActionPerformed(ActionEvent e)
            {
                getServer().startBubbleScene();
            }

            public String getDescription()
            {
                return "Start bubble scene";
            }
        };
    }

    /**
     * Create the 'start garden scene' action.
     * 
     * @return the action.
     */
    private Action createStartGardenSceneAction()
    {
        return new ServerAction("Start the garden scene")
        {
            /**
           * 
           */
          private static final long serialVersionUID = 1L;

            public void serverActionPerformed(ActionEvent e)
            {
                getServer().startGardenScene();
            }

            public String getDescription()
            {
                return "Start garden scene";
            }
        };
    }

    /**
     * Create the end session action.
     * 
     * @return the action.
     */
    private Action createEndSessionAction()
    {
        return new ServerAction("End Session")
        {
            /**
           * 
           */
          private static final long serialVersionUID = 1L;

            public void serverActionPerformed(ActionEvent e)
            {
                getServer().endSession();
            }

            public String getDescription()
            {
                return "End session";
            }
        };
    }

    /**
     * Create the end session button.
     * 
     * @return the button.
     */
    private JButton createEndSessionButton()
    {
        JButton button = Utilities.createButton(endSessionAction);
        button.setBackground(Color.RED);

        return button;
    }

    /**
     * Create the 'tell the agent to prompt again' action.
     * 
     * @return the action.
     */
    private Action createAgentPromptAgainAction()
    {
        return new ServerAction("Tell Andy to prompt the child again")
        {
            /**
           * 
           */
          private static final long serialVersionUID = 1L;

            public void serverActionPerformed(ActionEvent e)
            {
                getServer().childIsNotActing();
            }

            public String getDescription()
            {
                return "Tell Andy to prompt the child again";
            }
        };
    }

    /**
     * Create the 'tell the agent to stop waiting' action.
     * 
     * @return the action.
     */
    private Action createAgentTakeTurnAction()
    {
        return new ServerAction("Tell Andy to stop waiting and take his turn")
        {
            /**
           * 
           */
          private static final long serialVersionUID = 1L;

            public void serverActionPerformed(ActionEvent e)
            {
                getServer().tellAgentToStopWaiting();
            }

            public String getDescription()
            {
                return "Tell Andy to take his turn";
            }
        };
    }

    /**
     * Create the 'tell the agent to enter' action.
     * 
     * @return the action.
     */
    private Action createAgentEnterAction()
    {
        return new ServerAction("Tell Andy to enter and greet the child")
        {
            /**
           * 
           */
          private static final long serialVersionUID = 1L;

            public void serverActionPerformed(ActionEvent e)
            {
                getServer().tellAgentToEnterAndGreet();
            }

            public String getDescription()
            {
                return "Tell Andy to enter and greet";
            }
        };
    }

    /**
     * Create the 'tell the agent to leave' action.
     * 
     * @return the action.
     */
    private Action createAgentLeaveAction()
    {
        return new ServerAction("Tell Andy to leave the scene")
        {
            /**
           * 
           */
          private static final long serialVersionUID = 1L;

            public void serverActionPerformed(ActionEvent e)
            {
                getServer().tellAgentToLeave();
            }

            public String getDescription()
            {
                return "Tell Andy to leave";
            }
        };
    }

    /**
     * Add listeners for the popup intervention controls.
     */
    private void addPopupListeners()
    {
        if (USE_POPUP_INTERVENTIONS)
        {
            server.addInterventionHandler(new Observer()
            {
                public void update(Observable o, Object arg)
                {
                    if (arg instanceof Intervention)
                    {
                        showPopupIntervention((Intervention) arg);
                    }
                }
            });
        }
    }

    /**
     * Show the popup intervention.
     * 
     * @param intervention
     * the type of intervention.
     */
    public void showPopupIntervention(Intervention intervention)
    {
        interventionOptions.popupConfirmAction(intervention);
    }

    /**
     * Test the popup intervention controls.
     */
    void testInterventionOptions()
    {
        final Random r = new Random();

        for (final Intervention intervention : Intervention.values())
        {
            final int timeout = 2000 * (r.nextInt(3));
            new Thread()
            {
                public void run()
                {
                    try
                    {
                        Thread.sleep(timeout);
                    }
                    catch (InterruptedException e)
                    {
                        // ignore
                    }

                    showPopupIntervention(intervention);
                }
            }.start();
        }
    }

    /**
     * Create the child name text field.
     * 
     * @return the field.
     */
    private JTextField createChildNameField()
    {
        JTextField field = Utilities.createTextField("", true);
        field.selectAll();

        int width = field.getPreferredSize().width;
        int height = field.getMaximumSize().height;
        field.setMaximumSize(new Dimension(width, height));

        return field;
    }

    /**
     * Create the child name panel.
     * 
     * @return the panel.
     */
    private JPanel createChildNamePanel()
    {
        childNameField.addActionListener(startIntroSceneAction);

        List <JComponent> components = new ArrayList <JComponent>();

        components.add(Utilities.createLabel("Child's name: "));
        components.add(childNameField);

        return createPanel(components, BoxLayout.LINE_AXIS);
    }

    /**
     * Create the advanced options panel.
     * 
     * @return the panel.
     */
    private JPanel createAdvancedOptionsPanel()
    {
        List <JComponent> components = new ArrayList <JComponent>();

        components.add(createActivityRecordPanel());
        components.add(Utilities.setPanelColor(createInterventionOptionsPanel(),
                                               AGENT_COLOR));

        return createPanel(components, BoxLayout.PAGE_AXIS);
    }

    /**
     * Create the activity record panel.
     * 
     * @return the panel.
     */
    private JPanel createActivityRecordPanel()
    {
        JPanel arPanel = Utilities.createButtonPanel(new AbstractAction("Show Activity Record")
        {
            /**
           * 
           */
          private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e)
            {
                showActivityRecord();
            }
        });

        JPanel testPanel = Utilities.createButtonPanel(new AbstractAction("Test Intervention Options")
        {
            /**
           * 
           */
          private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                testInterventionOptions();
            }
        });

        List <JComponent> components = new ArrayList <JComponent>();

        components.add(Utilities.setPanelColor(arPanel, CHILD_COLOR));
        components.add(Utilities.setVisible(testPanel, false));

        return createPanel("Activity record", components, BoxLayout.PAGE_AXIS);
    }

    /**
     * Create the intervention options panel.
     * 
     * @return the panel.
     */
    private JPanel createInterventionOptionsPanel()
    {
        List <JComponent> components = new ArrayList <JComponent>();

        components.add(Utilities.createButtonPanel(agentPromptAgainAction));
        components.add(Utilities.createButtonPanel(agentTakeTurnAction));
        components.add(Utilities.createButtonPanel(agentLeaveAction));
        components.add(Utilities.createButtonPanel(tickleControlAction));

        return createPanel("Intervention options",
                           components,
                           BoxLayout.PAGE_AXIS);
    }

    /**
     * Create and configure a combo box for activities, with the given activity
     * selected.
     * 
     * @param selectedActivity
     * the selected activity (optional).
     * 
     * @return the combo box.
     */
    private TypedComboBox <Activity> createActivityCombo(Activity selectedActivity)
    {
        Object selected = null;
        Map <Object, Activity> options = new LinkedHashMap <Object, Activity>();

        int index = 1;
        for (Activity activity : Activity.values())
        {
            String label = "" + index + ". " + activity;
            options.put(label, activity);

            if (activity.equals(selectedActivity))
            {
                selected = label;
            }

            index++;
        }

        TypedComboBox <Activity> box = Utilities.createComboBox(options);
        box.setSelectedItem(selected);

        return box;
    }

    /**
     * Create and configure a combo box for initial learning activities.
     * 
     * @return the combo box.
     */
    private TypedComboBox <Activity> createInitialActivityCombo()
    {
        return createActivityCombo(Activity.BUBBLES);
    }

    /**
     * Create and configure a combo box for later learning activities.
     * 
     * @return the combo box.
     */
    private TypedComboBox <Activity> createLaterActivityCombo()
    {
        ComboHandler <Activity> handler = new ComboHandler <Activity>()
        {
            public void handle(Activity activity)
            {
                activity.startActivity(getServer());
            }

            public String getDescription(Object activity)
            {
                return "Choose activity: " + activity;
            }
        };

        return addHandler(createActivityCombo(Activity.EXPLORE), handler);
    }

    /**
     * Create and configure a panel with the given components and axis of
     * alignment.
     * 
     * @param components
     * the components.
     * 
     * @param axis
     * one of BoxLayout.LINE_AXIS or BoxLayout.PAGE_AXIS.
     * 
     * @return the panel.
     */
    private JPanel createPanel(List <? extends JComponent> components, int axis)
    {
        return Utilities.createPanel(null, components, axis, null);
    }

    /**
     * Create and configure a panel with the given title, components and axis of
     * alignment.
     * 
     * @param title
     * the panel title.
     * 
     * @param components
     * the components.
     * 
     * @param axis
     * one of BoxLayout.LINE_AXIS or BoxLayout.PAGE_AXIS.
     * 
     * @return the panel.
     */
    private TitledPanel createPanel(String title,
                                    List <? extends JComponent> components,
                                    int axis)
    {
        return Utilities.createPanel(title, components, axis, null);
    }

    /**
     * Add the given handler as an action listener for the given combo box.
     * 
     * @param <T>
     * the type.
     * 
     * @param comboBox
     * the combo box.
     * 
     * @param handler
     * the handler.
     * 
     * @return the combo box.
     */
    private <T> TypedComboBox <T> addHandler(final TypedComboBox <T> comboBox,
                                             final ComboHandler <T> handler)
    {
        if (handler != null)
        {
            comboBox.addActionListener(new ServerAction()
            {
                /**
               * 
               */
              private static final long serialVersionUID = 1L;

                public void serverActionPerformed(ActionEvent e)
                {
                    handler.handle(comboBox.getSelectedObject());
                }

                public String getDescription()
                {
                    return handler.getDescription(comboBox.getSelectedItem());
                }
            });
        }

        return comboBox;
    }

    /**
     * Report the status of an action.
     * 
     * @param description
     * the description of the action.
     * 
     * @param ex
     * the Exception thrown by the action, if any; <code>null</code> otherwise.
     */
    protected void reportStatus(String description, Exception ex)
    {
        String status;
        if (ex == null)
        {
            status = "done";
        }
        else if ((ex instanceof UnsupportedOperationException)
                 || (ex.getCause() instanceof UnsupportedOperationException))
        {
            status = "operation not supported";
        }
        else
        {
            status = "failed";
            // status += " (" + ex.getMessage() + ")";
        }

        updateStatus(description + ": " + status);
    }

    /**
     * Update the window when the session has started.
     * 
     * @param sessionStarted
     * <code>true</code> if the session has started; <code>false</code>
     * otherwise.
     */
    protected void setSessionStarted(boolean sessionStarted)
    {
        initialActivityCombo.setEnabled(! sessionStarted);
        childNameField.setEnabled(! sessionStarted);
        laterActivityCombo.setEnabled(sessionStarted);

        agentEnterAction.setEnabled(sessionStarted);
        agentTakeTurnAction.setEnabled(sessionStarted);
        agentPromptAgainAction.setEnabled(sessionStarted);
        agentLeaveAction.setEnabled(sessionStarted);

        pauseResumeAction.setEnabled(sessionStarted);
    }

    /**
     * Is this a test run?
     * 
     * @return <code>true</code> if it is a test run; <code>false</code>
     * otherwise.
     */
    protected boolean isTestRun()
    {
        return false;
    }

    /**
     * An action that is performed on the server on a separate thread.
     * 
     * @author Elaine Farrow
     */
    private abstract class ServerAction extends AbstractServerAction
    {
        /**
       * 
       */
      private static final long serialVersionUID = 1L;

        public ServerAction()
        {
            super();
        }

        public ServerAction(String name)
        {
            super(name);
        }

        public void initialClientAction(ActionEvent e)
        {
            super.initialClientAction(e);

            updateStatus(getDescription() + "...");
        }

        public void finalClientAction(ActionEvent e, Exception ex)
        {
            super.finalClientAction(e, ex);

            reportStatus(getDescription(), ex);
        }
    }

    /**
     * An action that has two alternatives to be performed on the server on a
     * separate thread.
     * 
     * @author Elaine Farrow
     */
    private abstract class ToggleAction extends AbstractToggleAction
    {
        /**
       * 
       */
      private static final long serialVersionUID = 1L;

        /**
         * Create a new ToggleAction with the given two actions.
         * 
         * @param label1
         * the label for the first action.
         * 
         * @param label2
         * the label for the second action.
         */
        public ToggleAction(String label1, String label2)
        {
            super(label1, label2);
        }

        public void initialClientAction(ActionEvent e)
        {
            super.initialClientAction(e);

            updateStatus(getDescription() + "...");
        }

        public void finalClientAction(ActionEvent e, Exception ex)
        {
            reportStatus(getDescription(), ex);

            super.finalClientAction(e, ex);
        }
    }

    /**
     * Main method for testing.
     * 
     * @param args
     * the command-line arguments.
     */
    public static void main(String[] args)
    {
        PractitionerServer server = new PractitionerServer();
        PractitionerWindow window = new PractitionerWindow(server)
        {
            /**
           * 
           */
          private static final long serialVersionUID = 1L;

            protected boolean isTestRun()
            {
                return true;
            }
        };

        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }
}
