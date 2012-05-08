package pedagogicComponent;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import utils.Enums.EchoesActivity;
import utils.Enums.EchoesObjectType;
import pedagogicComponent.Utilities.*;


/**
 * The activity record, used by practitioners to give input to the child model.
 * 
 * @author Elaine Farrow
 */
public class ActivityRecord extends JFrame
{
    /**
   * 
   */
  private static final long serialVersionUID = 1L;

    /**
     * The parent window.
     */
    private final PractitionerWindow parent;

    /**
     * The field for status messages.
     */
    private final JTextField statusField = Utilities.createStatusField();

    /**
     * Components that are only relevant if we are using the interface as a
     * substitute for the vision system.
     */
    private final List <JComponent> visionSubstitutes = new ArrayList <JComponent>();

    /**
     * Create a new ActivityRecord as a child of the given PractitionerWindow.
     * 
     * @param owner
     * the PractitionerWindow which owns this one.
     */
    public ActivityRecord(final PractitionerWindow owner)
    {
        super("Activity Record");

        this.parent = owner;
        setIsVisionSubstitute(true);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationByPlatform(true);
        setLayout(new BorderLayout());

        add(createStatusPanel(), BorderLayout.PAGE_END);
        add(createActivityRecordPanel(), BorderLayout.CENTER);

        pack();
    }

    /**
     * Close this window.
     */
    public void close()
    {
        setVisible(false);
        dispose();
    }

    /**
     * Get the link to the rest of the system.
     * 
     * @return the link to the rest of the system.
     */
    protected PractitionerServer getServer()
    {
        return parent.getServer();
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
     * If this panel is not being used as a substitute for the vision system,
     * hide various components.
     * 
     * @param isVisionSubstitute
     * <code>true</code> if this panel is being used as a substitute for the
     * vision system; <code>false</code> otherwise.
     */
    protected void setIsVisionSubstitute(boolean isVisionSubstitute)
    {
        for (JComponent comp : visionSubstitutes)
        {
            setVisible(comp, isVisionSubstitute);
        }

        validate();
    }

    /**
     * Create the status panel.
     * 
     * @return the panel.
     */
    private JPanel createStatusPanel()
    {
        JPanel buttonPanel = new JPanel(new GridLayout(1, 0, 10, 10));
        buttonPanel.add(createCloseButton());

        List <JComponent> components = new ArrayList <JComponent>();

        components.add(statusField);
        components.add(buttonPanel);

        JPanel panel = createPanel(components, BoxLayout.LINE_AXIS);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        return panel;
    }

    /**
     * Create the close button.
     * 
     * @return the button.
     */
    private JButton createCloseButton()
    {
        return Utilities.createButton(new AbstractAction("Close")
        {
            /**
           * 
           */
          private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e)
            {
                close();
            }
        });
    }

    /**
     * Create the activity record panel.
     * 
     * @return the panel.
     */
    private JPanel createActivityRecordPanel()
    {
        Color c1 = new Color(255, 204, 204);
        Color c2 = new Color(255, 255, 204);
        Color c3 = new Color(204, 255, 255);
        Color c4 = new Color(204, 204, 255);

        JPanel top = new JPanel(new GridLayout(1, 0));
        JPanel bottom = new JPanel(new GridLayout(1, 0));

        top.add(Utilities.setPanelBg(createVerbalBehaviourPanel(), c1));
        top.add(Utilities.setPanelBg(createAffectsPanel(), c2));

        bottom.add(Utilities.setPanelBg(createReciprocalInteractionPanel(), c3));
        bottom.add(Utilities.setPanelBg(createVisionPanel(), c4));

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.add(top);
        panel.add(bottom);

        return panel;
    }

    /**
     * Create the activity record pane.
     * 
     * @return the pane.
     */
    @SuppressWarnings("unused")
    private JTabbedPane createActivityRecordPane()
    {
        JTabbedPane pane = new JTabbedPane(JTabbedPane.LEFT,
                                           JTabbedPane.SCROLL_TAB_LAYOUT);
        pane.setOpaque(true);

        addTab(pane, createInitiateJAPanel());
        addTab(pane, createRespondJAPanel());
        addTab(pane, createReciprocalPanel());
        addTab(pane, createMonitorPartnerPanel());
        addTab(pane, createSocialBehavioursPanel());
        addTab(pane, createEmotionalExpressionPanel());
        addTab(pane, createImitativeBehavioursPanel());
        addTab(pane, createChildModelPanel());
        addTab(pane, createVisionSystemPanel());

        return pane;
    }

    /**
     * Add the given panel to the given tabbed pane.
     * 
     * @param pane
     * the pane.
     * 
     * @param panel
     * the panel.
     */
    private void addTab(JTabbedPane pane, TitledPanel panel)
    {
        Utilities.addTab(pane, panel);
    }

    /**
     * Create the 'verbal behaviour' panel.
     * 
     * @return the panel.
     */
    private TitledPanel createVerbalBehaviourPanel()
    {
        List <JComponent> components = new ArrayList <JComponent>();

        components.add(createGreetsAndyVerballyPanel());
        components.add(createRequestsAndyToActPanel());
        components.add(createRequestsObjectPanel());
        components.add(createChildProtestsPanel());

        components.add(createRespondsVerballyPanel());
        components.add(createGreetsAndyPanel());
        components.add(createTakesTurnsVerballyPanel());

        components.add(createSecuresAndysAttentionPanel());

        return createPanel("Verbal behaviour", components, BoxLayout.PAGE_AXIS);
    }

    /**
     * Create the 'reciprocal interaction' panel.
     * 
     * @return the panel.
     */
    private TitledPanel createReciprocalInteractionPanel()
    {
        List <JComponent> components = new ArrayList <JComponent>();

        components.add(createBriefReciprocalInteractionPanel());
        components.add(createExtendedReciprocalInteractionPanel());
        components.add(createImitateActionElicitedPanel());
        components.add(createImitatesActionImmediatelyPanel());
        components.add(createImitatesActionLaterPanel());
        components.add(createInitiatesJointAttentionNonverbalPanel());
        components.add(createRespondsJointAttentionNonverbalPanel());

        return createPanel("Reciprocal Interaction",
                           components,
                           BoxLayout.PAGE_AXIS);
    }

    /**
     * Create the 'affects' panel.
     * 
     * @return the panel.
     */
    private TitledPanel createAffectsPanel()
    {
        List <JComponent> components = new ArrayList <JComponent>();

        components.add(createRespondsToEmotionPanel());
        components.add(createDescribesEmotionPanel());
        components.add(createAffectiveStatePanel());
        components.add(createLikesDislikesPanel());

        return createPanel("Affects", components, BoxLayout.PAGE_AXIS);
    }

    /**
     * Create the 'vision' panel.
     * 
     * @return the panel.
     */
    private TitledPanel createVisionPanel()
    {
        List <JComponent> components = new ArrayList <JComponent>();

        components.add(tagVisionSubstitute(createLooksTowardAndyPanel()));
        components.add(tagVisionSubstitute(createShiftsGazePanel()));
        components.add(tagVisionSubstitute(createFollowsAndysGazePanel()));
        components.add(tagVisionSubstitute(createLooksTowardObjectPanel()));

        components.add(createPrefersObjectPanel());

        return createPanel("Vision", components, BoxLayout.PAGE_AXIS);
    }

    /**
     * Create the 'initiate joint action' panel.
     * 
     * @return the panel.
     */
    private TitledPanel createInitiateJAPanel()
    {
        List <JComponent> components = new ArrayList <JComponent>();

        components.add(createPanelSP_JA1_2());
        components.add(createPanelSP_JA4_1());
        components.add(createPanelSP_JA4_4());

        return createPanel(createTitle(ScertsGoalType.INITIATE_JA),
                           components,
                           BoxLayout.PAGE_AXIS);
    }

    /**
     * Create the 'respond to joint action' panel.
     * 
     * @return the panel.
     */
    private TitledPanel createRespondJAPanel()
    {
        return createPanel(createTitle(ScertsGoalType.RESPOND_JA),
                           Collections.singletonList(createPanelSP_JA1_1()),
                           BoxLayout.PAGE_AXIS);
    }

    /**
     * Create the reciprocal action panel.
     * 
     * @return the panel.
     */
    private TitledPanel createReciprocalPanel()
    {
        List <JComponent> components = new ArrayList <JComponent>();

        components.add(createPanelSP_JA1_3());
        components.add(createPanelSP_JA1_4());

        return createPanel(createTitle(ScertsGoalType.RECIPROCAL_INTERACTION),
                           components,
                           BoxLayout.PAGE_AXIS);
    }

    /**
     * Create the 'monitor social partner' panel.
     * 
     * @return the panel.
     */
    private TitledPanel createMonitorPartnerPanel()
    {
        List <JComponent> components = new ArrayList <JComponent>();

        components.add(createPanelLP_JA2_4());
        components.add(tagVisionSubstitute(createPanelLP_JA2_3()));

        return createPanel(createTitle(ScertsGoalType.MONITOR_PARTNER),
                           components,
                           BoxLayout.PAGE_AXIS);
    }

    /**
     * Create the 'social behaviours' panel.
     * 
     * @return the panel.
     */
    private TitledPanel createSocialBehavioursPanel()
    {
        List <JComponent> components = new ArrayList <JComponent>();

        components.add(createPanelSP_JA5_4());
        components.add(createPanelSP_JA5_3());

        components.add(tagVisionSubstitute(createPanelSP_JA2_1()));
        components.add(tagVisionSubstitute(createPanelSP_JA2_2()));

        return createPanel(createTitle(ScertsGoalType.SOCIAL_BEHAVIOURS),
                           components,
                           BoxLayout.PAGE_AXIS);
    }

    /**
     * Create the 'emotional expression' panel.
     * 
     * @return the panel.
     */
    private TitledPanel createEmotionalExpressionPanel()
    {
        List <JComponent> components = new ArrayList <JComponent>();

        components.add(createPanelE1());
        components.add(createPanelE2());

        return createPanel(createTitle(ScertsGoalType.EMOTIONAL_EXPRESSION),
                           components,
                           BoxLayout.PAGE_AXIS);
    }

    /**
     * Create the 'imitative behaviours' panel.
     * 
     * @return the panel.
     */
    private TitledPanel createImitativeBehavioursPanel()
    {
        List <JComponent> components = new ArrayList <JComponent>();

        components.add(createPanelSP_SU1_2());
        components.add(createPanelSP_SU1_3());
        components.add(createPanelSP_SU1_4());

        return createPanel(createTitle(ScertsGoalType.IMITATIVE_BEHAVIOURS),
                           components,
                           BoxLayout.PAGE_AXIS);
    }

    /**
     * Create the child model panel.
     * 
     * @return the panel.
     */
    private TitledPanel createChildModelPanel()
    {
        List <JComponent> components = new ArrayList <JComponent>();

        components.add(createAffectiveStatePanel());
        components.add(setVisible(createEngagementStatePanel(), false));
        components.add(setVisible(createFocusOfAttentionPanel(), false));

        return createPanel("Child model", components, BoxLayout.PAGE_AXIS);
    }

    /**
     * Create the vision system panel.
     * 
     * @return the panel.
     */
    private TitledPanel createVisionSystemPanel()
    {
        List <JComponent> components = new ArrayList <JComponent>();

        components.add(createVisionCheckboxPanel());
        components.add(tagVisionSubstitute(createLooksTowardObjectPanel()));

        return createPanel("Vision system", components, BoxLayout.PAGE_AXIS);
    }

    /**
     * Create the E1 panel.
     * 
     * @return the panel.
     */
    private JPanel createPanelE1()
    {
        return createPanel(createTitle(Feature.E1),
                           Collections.singletonList(createRespondsToEmotionPanel()),
                           BoxLayout.PAGE_AXIS);
    }

    /**
     * Create the E2 panel.
     * 
     * @return the panel.
     */
    private JPanel createPanelE2()
    {
        return createPanel(createTitle(Feature.E2),
                           Collections.singletonList(createDescribesEmotionPanel()),
                           BoxLayout.PAGE_AXIS);
    }

    /**
     * Create the LP-JA2.3 panel.
     * 
     * @return the panel.
     */
    private JPanel createPanelLP_JA2_3()
    {
        return createPanel(createTitle(Feature.LP_JA2_3),
                           Collections.singletonList(createFollowsAndysGazePanel()),
                           BoxLayout.PAGE_AXIS);
    }

    /**
     * Create the LP-JA2.4 panel.
     * 
     * @return the panel.
     */
    private JPanel createPanelLP_JA2_4()
    {
        return createPanel(createTitle(Feature.LP_JA2_4),
                           Collections.singletonList(createSecuresAndysAttentionPanel()),
                           BoxLayout.PAGE_AXIS);
    }

    /**
     * Create the SP-JA1.1 panel.
     * 
     * @return the panel.
     */
    private JPanel createPanelSP_JA1_1()
    {
        return createPanel(createTitle(Feature.SP_JA1_1),
                           Collections.singletonList(createRespondsVerballyPanel()),
                           BoxLayout.PAGE_AXIS);
    }

    /**
     * Create the SP-JA1.2 panel.
     * 
     * @return the panel.
     */
    private JPanel createPanelSP_JA1_2()
    {
        List <JComponent> components = new ArrayList <JComponent>();

        components.add(createGreetsAndyVerballyPanel());
        components.add(createRequestsAndyToActPanel());

        String title = createTitle(Feature.SP_JA1_2);

        return createPanel(title, components, BoxLayout.PAGE_AXIS);
    }

    /**
     * Create the SP-JA1.3 panel.
     * 
     * @return the panel.
     */
    private JPanel createPanelSP_JA1_3()
    {
        return createPanel(createTitle(Feature.SP_JA1_3),
                           Collections.singletonList(createBriefReciprocalInteractionPanel()),
                           BoxLayout.PAGE_AXIS);
    }

    /**
     * Create the SP-JA1.4 panel.
     * 
     * @return the panel.
     */
    private JPanel createPanelSP_JA1_4()
    {
        return createPanel(createTitle(Feature.SP_JA1_4),
                           Collections.singletonList(createExtendedReciprocalInteractionPanel()),
                           BoxLayout.PAGE_AXIS);
    }

    /**
     * Create the SP-JA2.1 panel.
     * 
     * @return the panel.
     */
    private JPanel createPanelSP_JA2_1()
    {
        return createPanel(createTitle(Feature.SP_JA2_1),
                           Collections.singletonList(createLooksTowardAndyPanel()),
                           BoxLayout.PAGE_AXIS);
    }

    /**
     * Create the SP-JA2.2 panel.
     * 
     * @return the panel.
     */
    private JPanel createPanelSP_JA2_2()
    {
        return createPanel(createTitle(Feature.SP_JA2_2),
                           Collections.singletonList(createShiftsGazePanel()),
                           BoxLayout.PAGE_AXIS);
    }

    /**
     * Create the SP-JA4.1 panel.
     * 
     * @return the panel.
     */
    private JPanel createPanelSP_JA4_1()
    {
        return createPanel(createTitle(Feature.SP_JA4_1),
                           Collections.singletonList(createRequestsObjectPanel()),
                           BoxLayout.PAGE_AXIS);
    }

    /**
     * Create the SP-JA4.4 panel.
     * 
     * @return the panel.
     */
    private JPanel createPanelSP_JA4_4()
    {
        List <JComponent> components = new ArrayList <JComponent>();

        components.add(createChildProtestsObjectPanel());
        components.add(createChildProtestsActionPanel());
        components.add(createChildProtestsActivityPanel());

        return createPanel(createTitle(Feature.SP_JA4_4),
                           components,
                           BoxLayout.PAGE_AXIS);
    }

    /**
     * Create the SP-JA5.3 panel.
     * 
     * @return the panel.
     */
    private JPanel createPanelSP_JA5_3()
    {
        return createPanel(createTitle(Feature.SP_JA5_3),
                           Collections.singletonList(createTakesTurnsVerballyPanel()),
                           BoxLayout.PAGE_AXIS);
    }

    /**
     * Create the SP-JA5.4 panel.
     * 
     * @return the panel.
     */
    private JPanel createPanelSP_JA5_4()
    {
        return createPanel(createTitle(Feature.SP_JA5_4),
                           Collections.singletonList(createGreetsAndyPanel()),
                           BoxLayout.PAGE_AXIS);
    }

    /**
     * Create the SP-SU1.2 panel.
     * 
     * @return the panel.
     */
    private JPanel createPanelSP_SU1_2()
    {
        return createPanel(createTitle(Feature.SP_SU1_2),
                           Collections.singletonList(createImitateActionElicitedPanel()),
                           BoxLayout.PAGE_AXIS);
    }

    /**
     * Create the SP-SU1.3 panel.
     * 
     * @return the panel.
     */
    private JPanel createPanelSP_SU1_3()
    {
        return createPanel(createTitle(Feature.SP_SU1_3),
                           Collections.singletonList(createImitatesActionImmediatelyPanel()),
                           BoxLayout.PAGE_AXIS);
    }

    /**
     * Create the SP-SU1.4 panel.
     * 
     * @return the panel.
     */
    private JPanel createPanelSP_SU1_4()
    {
        return createPanel(createTitle(Feature.SP_SU1_4),
                           Collections.singletonList(createImitatesActionLaterPanel()),
                           BoxLayout.PAGE_AXIS);
    }

    /**
     * Create the 'responds to emotion' panel.
     * 
     * @return the panel.
     */
    private JPanel createRespondsToEmotionPanel()
    {
        List <JComponent> components = new ArrayList <JComponent>();

        components.add(Utilities.createButton(new ServerAction("Responds to another's emotional state")
        {
            /**
           * 
           */
          private static final long serialVersionUID = 1L;

            public void serverActionPerformed(ActionEvent e)
            {
                getServer().childRespondsToEmotion();
            }

            public String getDescription()
            {
                return "The child indicates understanding or response to another's emotional state";
            }
        }));

        String helpText = "The child responds to a partner’s clear emotional state or change in emotional state by changing his or her own behaviour (e.g. pausing, focusing on partner’s face, moving toward or away, stopping or resuming play). Success in this category may (but does not need to) include mirroring that partner’s emotional tone. Emotional states may be positive or negative.";

        return createPanelHorizontal(components, helpText);
    }

    /**
     * Create the 'describes emotion' panel.
     * 
     * @return the panel.
     */
    private JPanel createDescribesEmotionPanel()
    {
        List <JComponent> components = new ArrayList <JComponent>();

        components.add(Utilities.createLabel("Describes emotional state: "));
        components.add(createEmotionCombo(new ComboHandler <String>()
        {
            public void handle(String word)
            {
                getServer().childUsesEmotionWords(word);
            }

            public String getDescription(Object word)
            {
                return "The child uses emotion words to describe own emotional state: "
                       + word;
            }
        }));

        String helpText = "Child uses words, symbols, pictures, or some other strategy to refer to/comment on their own or another’s emotional state. Emotional states may be positive or negative.";

        return createPanelHorizontal(components, helpText);
    }

    /**
     * Create the 'follows Andy's gaze' panel.
     * 
     * @return the panel.
     */
    private JPanel createFollowsAndysGazePanel()
    {
        List <JComponent> components = new ArrayList <JComponent>();

        components.add(Utilities.createButton(new ServerAction("Follows what Andy is doing or looking at")
        {
            /**
           * 
           */
          private static final long serialVersionUID = 1L;

            public void serverActionPerformed(ActionEvent e)
            {
                getServer().childLooksWhereAndyLooks();
            }

            public String getDescription()
            {
                return "The child looks at what Andy is doing or paying attention to";
            }
        }));

        String helpText = "The child spontaneously follows the reference of another person’s attentional focus during an ongoing activity. Evidence includes the child following the reference of another person’s gesture, looking at what someone else is paying attention to, or communicating about what someone else is doing.";

        return createPanelHorizontal(components, helpText);
    }

    /**
     * Create the 'secures Andy's attention' panel.
     * 
     * @return the panel.
     */
    private JPanel createSecuresAndysAttentionPanel()
    {
        List <JComponent> components = new ArrayList <JComponent>();

        components.add(Utilities.createButton(new ServerAction("Catches Andy's attention")
        {
            /**
           * 
           */
          private static final long serialVersionUID = 1L;

            public void serverActionPerformed(ActionEvent e)
            {
                getServer().childSecuresAttention();
            }

            public String getDescription()
            {
                return "The child calls Andy by saying his name or talking to him to catch his attention";
            }
        }));

        String helpText = "The child secures the attention of a social partner by calling nonverbally (e.g. tapping on shoulder or arm) or verbally (e.g.; saying partner’s name, signing partner’s name, holding up a picture) prior to expressing communicative intentions (e.g. requesting, commenting).";

        return createPanelHorizontal(components, helpText);
    }

    /**
     * Create the 'responds verbally' panel.
     * 
     * @return the panel.
     */
    private JPanel createRespondsVerballyPanel()
    {
        List <JComponent> components = new ArrayList <JComponent>();

        components.add(Utilities.createButton(new ServerAction("Responds verbally")
        {
            /**
           * 
           */
          private static final long serialVersionUID = 1L;

            public void serverActionPerformed(ActionEvent e)
            {
                getServer().childRespondsInteractionVerbal();
            }

            public String getDescription()
            {
                return "The child responds verbally to a bid for interaction";
            }
        }));

        String helpText = "The child verbally responds to a familiar person’s bid for interaction. The child’s response must be immediate (i.e. displayed within 5 seconds following the other person’s bid) and contingent (i.e. maintains the focus of attention or topic). The child’s response does not need to demonstrate comprehension of the original verbal bid.";

        return createPanelHorizontal(components, helpText);
    }

    /**
     * Create the 'greets Andy verbally' panel.
     * 
     * @return the panel.
     */
    private JPanel createGreetsAndyVerballyPanel()
    {
        List <JComponent> components = new ArrayList <JComponent>();

        components.add(Utilities.createButton(new ServerAction("Greets Andy verbally")
        {
            /**
           * 
           */
          private static final long serialVersionUID = 1L;

            public void serverActionPerformed(ActionEvent e)
            {
                getServer().childGreetsAndy();
            }

            public String getDescription()
            {
                return "The child greets Andy verbally";
            }
        }));

        String helpText = "The child initiates a bid for interaction through verbal means. The behaviour must be directed to another person. The behaviour must be initiated by the child, meaning that it is not a response to another person’s behaviour.";

        return createPanelHorizontal(components, helpText);
    }

    /**
     * Create the 'requests Andy to act' panel.
     * 
     * @return the panel.
     */
    private JPanel createRequestsAndyToActPanel()
    {
        List <JComponent> components = new ArrayList <JComponent>();

        final JTextField actionField = createActionField(null);

        components.add(Utilities.createLabel("Asks Andy to"));
        components.add(actionField);
        components.add(Utilities.createLabel("the"));
        components.add(createObjectCombo(new ComboHandler <EchoesObjectType>()
        {
            public void handle(EchoesObjectType object)
            {
                Object action = actionField.getText();
                getServer().childAsksAndyToAct(action, object);
            }

            public String getDescription(Object object)
            {
                Object action = actionField.getText();

                return "The child asks Andy to " + action + " the " + object;
            }
        }));

        String helpText = "The child initiates a bid for interaction through verbal means. The behaviour must be directed to another person. The behaviour must be initiated by the child, meaning that it is not a response to another person’s behaviour.";

        return createPanelHorizontal(components, helpText);
    }

    /**
     * Create the 'brief reciprocal interaction' panel.
     * 
     * @return the panel.
     */
    private JPanel createBriefReciprocalInteractionPanel()
    {
        List <JComponent> components = new ArrayList <JComponent>();

        components.add(Utilities.createButton(new ServerAction("Engages in brief reciprocal interaction sequence")
        {
            /**
           * 
           */
          private static final long serialVersionUID = 1L;

            public void serverActionPerformed(ActionEvent e)
            {
                getServer().childReciprocalInteractionBrief();
            }

            public String getDescription()
            {
                return "The child engages in a brief reciprocal interaction sequence";
            }
        }));

        String helpText = "The child initiates and responds to bids for interaction with a familiar person for at least two consecutive exchanges. An exchange consists of a turn for the child and a turn for the partner. At least one the exchanges must be initiated by the child.";

        return createPanelHorizontal(components, helpText);
    }

    /**
     * Create the 'extended reciprocal interaction' panel.
     * 
     * @return the panel.
     */
    private JPanel createExtendedReciprocalInteractionPanel()
    {
        List <JComponent> components = new ArrayList <JComponent>();

        components.add(Utilities.createButton(new ServerAction("Engages in extended reciprocal interaction sequence")
        {
            /**
           * 
           */
          private static final long serialVersionUID = 1L;

            public void serverActionPerformed(ActionEvent e)
            {
                getServer().childReciprocalInteractionExtended();
            }

            public String getDescription()
            {
                return "The child engages in an extended reciprocal interaction sequence";
            }
        }));

        String helpText = "The child initiates and responds to bids for interaction with another person for at least four consecutive exchanges between the child and partner. An exchange consists of a turn for the child and a turn for the partner. At least one the exchanges must be initiated by the child.";

        return createPanelHorizontal(components, helpText);
    }

    /**
     * Create the 'looks toward Andy' panel.
     * 
     * @return the panel.
     */
    private JPanel createLooksTowardAndyPanel()
    {
        List <JComponent> components = new ArrayList <JComponent>();

        components.add(Utilities.createButton(new ServerAction("Looks toward Andy")
        {
            /**
           * 
           */
          private static final long serialVersionUID = 1L;

            public void serverActionPerformed(ActionEvent e)
            {
                getServer().childLooksTowardAndy();
            }

            public String getDescription()
            {
                return "The child looks toward Andy";
            }
        }));

        components.add(tagVisionSubstitute(Utilities.createButton(new ServerAction("Smiles to Andy")
        {
            /**
           * 
           */
          private static final long serialVersionUID = 1L;

            public void serverActionPerformed(ActionEvent e)
            {
                getServer().childSmilesAtAndy();
            }

            public String getDescription()
            {
                return "The child smiles to Andy";
            }
        })));

        String helpText = "The child directs gaze spontaneously (without prompting) toward another person’s face. Looking toward people may occur without a communicative signal or may support communication.";

        return createPanelHorizontal(components, helpText);
    }

    /**
     * Create the 'child looks between people and objects' panel.
     * 
     * @return the panel.
     */
    private JPanel createShiftsGazePanel()
    {
        List <JComponent> components = new ArrayList <JComponent>();

        components.add(Utilities.createLabel("Looks between people and the"));
        components.add(createObjectCombo(new ComboHandler <EchoesObjectType>()
        {
            public void handle(EchoesObjectType object)
            {
                getServer().childLooksBetweenPeopleObjects(object);
            }

            public String getDescription(Object object)
            {
                return "The child looks between people and " + object;
            }
        }));

        String helpText = "The child shifts or alternates gaze spontaneously (without prompting) between a person and an object and back at least three times. The gaze must be directed to another person’s face. Gaze shifts may occur without a gesture or vocalization or may support communication. The shift must be smooth and immediate (i.e. the entire sequence should occur within 2 seconds). The gaze shift must be three point or four point. A three-point gaze shift may be either a person-object-person gaze shift (i.e. when the child is looking at a person, shifts gaze to an object, and then immediately shifts back to the person) or an object-person-object gaze shift (i.e., the child is looking at an object, shifts gaze to a person, and then immediately shifts gaze back to the object). A four-point gaze shift is an object-Person A-Person B-object gaze shift (i.e., the child is looking at an object, shifts gaze to Person A, then immediately shifts gaze to Person B, and then immediately shifts back to the object).";

        return createPanelHorizontal(components, helpText);
    }

    /**
     * Create the 'child requests object' panel.
     * 
     * @return the panel.
     */
    private JPanel createRequestsObjectPanel()
    {
        List <JComponent> components = new ArrayList <JComponent>();

        components.add(Utilities.createLabel("Requests the"));
        components.add(createObjectCombo(new ComboHandler <EchoesObjectType>()
        {
            public void handle(EchoesObjectType object)
            {
                getServer().childRequestsObject(object);
            }

            public String getDescription(Object object)
            {
                return "The child requests the " + object;
            }
        }));

        String helpText = "The child directs verbal or nonverbal signals (e.g. reaches toward an object, bangs, and looks toward an object out of reach) to get another person to give a desired object.";

        return createPanelHorizontal(components, helpText);
    }

    /**
     * Create the 'child protests undesired action/ activity' panel.
     * 
     * @return the panel.
     */
    private JPanel createChildProtestsPanel()
    {
        List <JComponent> components = new ArrayList <JComponent>();

        components.add(Utilities.createLabel("Child protests undesired action / activity"));
        components.add(createActionField(new ComboHandler <Object>()
        {
            public void handle(Object action)
            {
                getServer().childProtestsUndesiredActionActivity(action);
            }

            public String getDescription(Object action)
            {
                return "The child protests undesired action/ activity: "
                       + action;
            }
        }));

        String helpText = "The child directs nonverbal or vocal signals (e.g. pushes away, cries paired with gaze) to get another person to get another person to cease an undesirable action or get out of an undesirable activity.";

        return createPanelHorizontal(components, helpText);
    }

    /**
     * Create the 'child protests undesired object' panel.
     * 
     * @return the panel.
     */
    private JPanel createChildProtestsObjectPanel()
    {
        List <JComponent> components = new ArrayList <JComponent>();

        components.add(Utilities.createLabel("Child protests undesired object"));
        components.add(createObjectCombo(new ComboHandler <EchoesObjectType>()
        {
            public void handle(EchoesObjectType object)
            {
                getServer().childProtestsUndesiredObject(object);
            }

            public String getDescription(Object object)
            {
                return "The child protests undesired object: " + object;
            }
        }));

        String helpText = "The child directs nonverbal or vocal signals (e.g. pushes away, cries paired with gaze) to get another person to get another person to cease an undesirable action or get out of an undesirable activity.";

        return createPanelHorizontal(components, helpText);
    }

    /**
     * Create the 'child protests undesired action' panel.
     * 
     * @return the panel.
     */
    private JPanel createChildProtestsActionPanel()
    {
        List <JComponent> components = new ArrayList <JComponent>();

        components.add(Utilities.createLabel("Child protests undesired action"));
        components.add(createActionField(new ComboHandler <Object>()
        {
            public void handle(Object action)
            {
                getServer().childProtestsUndesiredAction(action);
            }

            public String getDescription(Object action)
            {
                return "The child protests undesired action: " + action;
            }
        }));

        String helpText = "The child directs nonverbal or vocal signals (e.g. pushes away, cries paired with gaze) to get another person to get another person to cease an undesirable action or get out of an undesirable activity.";

        return createPanelHorizontal(components, helpText);
    }

    /**
     * Create the 'child protests undesired activity' panel.
     * 
     * @return the panel.
     */
    private JPanel createChildProtestsActivityPanel()
    {
        List <JComponent> components = new ArrayList <JComponent>();

        components.add(Utilities.createLabel("Child protests undesired activity"));
        components.add(createActivityCombo(new ComboHandler <EchoesActivity>()
        {
            public void handle(EchoesActivity activity)
            {
                getServer().childProtestsUndesiredActivity(activity);
            }

            public String getDescription(Object activity)
            {
                return "The child protests undesired activity: " + activity;
            }
        }));

        String helpText = "The child directs nonverbal or vocal signals (e.g. pushes away, cries paired with gaze) to get another person to get another person to cease an undesirable action or get out of an undesirable activity.";

        return createPanelHorizontal(components, helpText);
    }

    /**
     * Create the 'child takes turns verbally' panel.
     * 
     * @return the panel.
     */
    private JPanel createTakesTurnsVerballyPanel()
    {
        List <JComponent> components = new ArrayList <JComponent>();

        components.add(Utilities.createButton(new ServerAction("Takes turns verbally")
        {
            /**
           * 
           */
          private static final long serialVersionUID = 1L;

            public void serverActionPerformed(ActionEvent e)
            {
                getServer().childTakesTurnsVerbally();
            }

            public String getDescription()
            {
                return "The child takes turns verbally";
            }
        }));

        String helpText = "The child directs nonverbal or vocal signals as a turn filler to keep a cooperative social exchange going at least two times. This involves waiting for the partner to take a turn.";

        return createPanelHorizontal(components, helpText);
    }

    /**
     * Create the 'child greets Andy' panel.
     * 
     * @return the panel.
     */
    private JPanel createGreetsAndyPanel()
    {
        List <JComponent> components = new ArrayList <JComponent>();

        components.add(Utilities.createButton(new ServerAction("Greets Andy verbally")
        {
            /**
           * 
           */
          private static final long serialVersionUID = 1L;

            public void serverActionPerformed(ActionEvent e)
            {
                getServer().childGreetsAndyVerbal();
            }

            public String getDescription()
            {
                return "The child greets Andy verbally";
            }
        }));

        components.add(tagVisionSubstitute(Utilities.createButton(new ServerAction("Greets Andy non-verbally")
        {
            /**
           * 
           */
          private static final long serialVersionUID = 1L;

            public void serverActionPerformed(ActionEvent e)
            {
                getServer().childGreetsAndyNonverbal();
            }

            public String getDescription()
            {
                return "The child greets Andy non-verbally";
            }
        })));

        String helpText = "The child directs nonverbal or vocal signals to indicate notice of a person or object entering or leaving the immediate situation or to mark the initiation or termination of an interaction (e.g. waves).";

        return createPanelHorizontal(components, helpText);
    }

    /**
     * Create the 'child imitates elicited action' panel.
     * 
     * @return the panel.
     */
    private JPanel createImitateActionElicitedPanel()
    {
        List <JComponent> components = new ArrayList <JComponent>();

        components.add(Utilities.createLabel("Imitates action"));
        components.add(createActionField(new ComboHandler <Object>()
        {
            public void handle(Object action)
            {
                getServer().childImitatesActionElicited(action);
            }

            public String getDescription(Object action)
            {
                return "The child imitates familiar actions elicited immediately after model: "
                       + action;
            }
        }));
        components.add(Utilities.createLabel("when elicited"));

        String helpText = "The child accurately imitates or closely approximates a familiar action or sound immediately after a partner directs the child (e.g. “Do this,” “Say this”).";

        return createPanelHorizontal(components, helpText);
    }

    /**
     * Create the 'child spontaneously imitates action immediately' panel.
     * 
     * @return the panel.
     */
    private JPanel createImitatesActionImmediatelyPanel()
    {
        List <JComponent> components = new ArrayList <JComponent>();

        components.add(Utilities.createLabel("Spontaneously imitates action"));
        components.add(createActionField(new ComboHandler <Object>()
        {
            public void handle(Object action)
            {
                getServer().childImitatesActionImmediately(action);
            }

            public String getDescription(Object action)
            {
                return "The child spontaneously imitates familiar actions immediately after model: "
                       + action;
            }
        }));
        components.add(Utilities.createLabel("immediately"));

        String helpText = "The child accurately imitates or closely approximates a familiar action or sound spontaneously (i.e. without direction to do so) immediately after a partner models the behaviour.";

        return createPanelHorizontal(components, helpText);
    }

    /**
     * Create the 'child spontaneously imitates action later' panel.
     * 
     * @return the panel.
     */
    private JPanel createImitatesActionLaterPanel()
    {
        List <JComponent> components = new ArrayList <JComponent>();

        components.add(Utilities.createLabel("Spontaneously imitates action"));
        components.add(createActionField(new ComboHandler <Object>()
        {
            public void handle(Object action)
            {
                getServer().childImitatesActionLater(action);
            }

            public String getDescription(Object action)
            {
                return "The child spontaneously imitates familiar actions at a later time: "
                       + action;
            }
        }));
        components.add(Utilities.createLabel("later"));

        String helpText = "The child accurately imitates or closely approximates a familiar action or sound spontaneously (i.e. without direction to do so) at a later time after a partner has modelled the behaviour. At a later time is at least three turns or at least a minute after the model but can be within the same activity or at a much later time.";

        return createPanelHorizontal(components, helpText);
    }

    /**
     * Create the 'child initiates joint attention non verbally' panel.
     * 
     * @return the panel.
     */
    private JPanel createInitiatesJointAttentionNonverbalPanel()
    {
        List <JComponent> components = new ArrayList <JComponent>();

        components.add(Utilities.createButton(new ServerAction("Initiates joint attention nonverbally")
        {
            /**
           * 
           */
          private static final long serialVersionUID = 1L;

            public void serverActionPerformed(ActionEvent e)
            {
                getServer().childInitiatesJointAttentionNonverbal();
            }

            public String getDescription()
            {
                return "The child initiates joint attention nonverbally";
            }
        }));

        String helpText = null;

        return createPanelHorizontal(components, helpText);
    }

    /**
     * Create the 'child responds to joint attention non verbally' panel.
     * 
     * @return the panel.
     */
    private JPanel createRespondsJointAttentionNonverbalPanel()
    {
        List <JComponent> components = new ArrayList <JComponent>();

        components.add(Utilities.createButton(new ServerAction("Responds to joint attention nonverbally")
        {
            /**
           * 
           */
          private static final long serialVersionUID = 1L;

            public void serverActionPerformed(ActionEvent e)
            {
                getServer().childRespondsJointAttentionNonverbal();
            }

            public String getDescription()
            {
                return "The child responds to joint attention nonverbally";
            }
        }));

        String helpText = null;

        return createPanelHorizontal(components, helpText);
    }

    /**
     * Create the 'affective state' panel.
     * 
     * @return the panel.
     */
    private JPanel createAffectiveStatePanel()
    {
        List <JComponent> components = new ArrayList <JComponent>();

        components.add(Utilities.createLabel("Affective state: "));
        components.add(createAffectiveCombo(new ComboHandler <String>()
        {
            public void handle(String state)
            {
                getServer().childAffectiveState(state);
            }

            public String getDescription(Object state)
            {
                return "The child's affective state is " + state;
            }
        }));

        String helpText = "The affective states of the child. You can type a new value here.";

        return createPanelHorizontal(components, helpText);
    }

    /**
     * Create the 'engagement state' panel.
     * 
     * @return the panel.
     */
    private JPanel createEngagementStatePanel()
    {
        List <JComponent> components = new ArrayList <JComponent>();

        components.add(createEngagementCombo(new ComboHandler <String>()
        {
            public void handle(String state)
            {
                Boolean isEngaged = null;

                isEngaged = isEngagedSystem(state);
                if (isEngaged != null)
                {
                    getServer().childEngagedWithSystem(isEngaged);
                }
                else
                {
                    isEngaged = isEngagedAndy(state);
                    if (isEngaged != null)
                    {
                        getServer().childEngagedWithAgent(isEngaged);
                    }
                }
            }

            public String getDescription(Object state)
            {
                return "The child is " + state;
            }

            private Boolean isEngagedSystem(String state)
            {
                if ("engaged with system".equals(state))
                {
                    return true;
                }
                else if ("disengaged with system".equals(state))
                {
                    return false;
                }

                return null;
            }

            private Boolean isEngagedAndy(String state)
            {
                if ("engaged with Andy".equals(state))
                {
                    return true;
                }
                else if ("disengaged with Andy".equals(state))
                {
                    return false;
                }

                return null;
            }
        }));

        String helpText = "Possible engagement states for the child.";

        return createPanelHorizontal("Engagement", components, helpText);
    }

    /**
     * Create the 'focus of attention' panel.
     * 
     * @return the panel.
     */
    private JPanel createFocusOfAttentionPanel()
    {
        List <JComponent> components = new ArrayList <JComponent>();

        components.add(createPrefersObjectPanel());
        components.add(createLikesDislikesPanel());

        String title = "Focus of attention";

        return createPanel(title, components, BoxLayout.PAGE_AXIS);
    }

    /**
     * Create the 'looks toward object' panel.
     * 
     * @return the panel.
     */
    private JPanel createLooksTowardObjectPanel()
    {
        List <JComponent> components = new ArrayList <JComponent>();

        components.add(Utilities.createLabel("Looks toward the"));
        components.add(createObjectCombo(new ComboHandler <EchoesObjectType>()
        {
            public void handle(EchoesObjectType object)
            {
                getServer().childLooksTowardObject(object);
            }

            public String getDescription(Object object)
            {
                return "The child looks toward " + object;
            }
        }));

        String helpText = "The child is looking towards an object in the current scene";

        return createPanelHorizontal(components, helpText);
    }

    /**
     * Create the 'prefers object' panel.
     * 
     * @return the panel.
     */
    private JPanel createPrefersObjectPanel()
    {
        List <JComponent> components = new ArrayList <JComponent>();

        components.add(Utilities.createLabel("Prefers the"));
        components.add(createObjectCombo(new ComboHandler <EchoesObjectType>()
        {
            public void handle(EchoesObjectType object)
            {
                getServer().childPrefersObject(object);
            }

            public String getDescription(Object object)
            {
                return "The child prefers the " + object;
            }
        }));

        String helpText = "What objects the child prefers, based on what the child verbally says about objects during the interaction";

        return createPanelHorizontal(components, helpText);
    }

    /**
     * Create the 'likes/ dislikes Andy' panel.
     * 
     * @return the panel.
     */
    private JPanel createLikesDislikesPanel()
    {
        List <JComponent> components = new ArrayList <JComponent>();

        components.add(Utilities.createButton(new ServerAction("Child likes Andy")
        {
            /**
           * 
           */
          private static final long serialVersionUID = 1L;

            public void serverActionPerformed(ActionEvent e)
            {
                getServer().childLikesAndy(true);
            }

            public String getDescription()
            {
                return "The child likes Andy";
            }
        }));

        components.add(Utilities.createButton(new ServerAction("Child dislikes Andy")
        {
            /**
           * 
           */
          private static final long serialVersionUID = 1L;

            public void serverActionPerformed(ActionEvent e)
            {
                getServer().childLikesAndy(false);
            }

            public String getDescription()
            {
                return "The child dislikes Andy";
            }
        }));

        String helpText = "Based on what the child verbally says about Andy during the interaction, if the child talks to Andy or ignores him, if the child openly says he likes/ dislikes Andy.";

        return createPanelHorizontal(components, helpText);
    }

    /**
     * Create the vision check box panel.
     * 
     * @return the panel.
     */
    private JPanel createVisionCheckboxPanel()
    {
        final JCheckBox box = Utilities.createCheckBox("Use interface as substitute for vision system",
                                                       true);
        box.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                setIsVisionSubstitute(box.isSelected());
            }
        });

        List <JComponent> components = new ArrayList <JComponent>();

        components.add(box);

        String helpText = "We might want to drop some features in part or in full when the vision is available";

        return createPanelHorizontal(components, helpText);
    }

    /**
     * Create and configure a combo box for activities using the given handler.
     * 
     * @param handler
     * the handler.
     * 
     * @return the combo box.
     */
    private TypedComboBox <EchoesActivity> createActivityCombo(final ComboHandler <EchoesActivity> handler)
    {
        Map <Object, EchoesActivity> options = new HashMap <Object, EchoesActivity>();
        for (EchoesActivity activity : EchoesActivity.values())
        {
            options.put(Utilities.toString(activity), activity);
        }

        return addHandler(Utilities.createComboBox(options), handler);
    }

    /**
     * Create and configure a combo box for objects using the given handler.
     * 
     * @param handler
     * the handler.
     * 
     * @return the combo box.
     */
    private TypedComboBox <EchoesObjectType> createObjectCombo(final ComboHandler <EchoesObjectType> handler)
    {
        Map <Object, EchoesObjectType> options = new HashMap <Object, EchoesObjectType>();
        for (EchoesObjectType object : EchoesObjectType.values())
        {
            switch (object)
            {
                case IntroBubble:
                case Pond:
                case Shed:
                    continue;
            }

            options.put(Utilities.toString(object), object);
        }

        return addHandler(Utilities.createComboBox(options), handler);
    }

    /**
     * Create and configure a horizontal panel with the given components and no
     * title. If help text is provided, add it at the end.
     * 
     * @param components
     * the components.
     * 
     * @param helpText
     * the help text (optional).
     * 
     * @return the panel.
     */
    private JPanel createPanelHorizontal(List <? extends JComponent> components,
                                         String helpText)
    {
        return createPanelHorizontal(null, components, helpText);
    }

    /**
     * Create and configure a horizontal panel with the given title and
     * components. If help text is provided, add it at the end.
     * 
     * @param title
     * the panel title.
     * 
     * @param components
     * the components.
     * 
     * @param helpText
     * the help text (optional).
     * 
     * @return the panel.
     */
    private JPanel createPanelHorizontal(String title,
                                         List <? extends JComponent> components,
                                         String helpText)
    {
        return Utilities.createPanel(title,
                                     components,
                                     BoxLayout.LINE_AXIS,
                                     helpText);
    }

    /**
     * Create and configure a panel with the given components and axis of
     * alignment and no title.
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
        return Utilities.createPanel(title, components, axis);
    }

    /**
     * Create and configure a text field for actions using the given handler.
     * 
     * @param handler
     * the handler.
     * 
     * @return the text field.
     */
    private JTextField createActionField(final ComboHandler <Object> handler)
    {
        JTextField field = Utilities.createTextField("", true);
        field.setColumns(15);

        return addHandler(Utilities.setMaximumWidth(Utilities.setMaximumHeight(field)),
                          handler);
    }

    /**
     * Create and configure a combo box for affective states using the given
     * handler.
     * 
     * @param handler
     * the handler.
     * 
     * @return the combo box.
     */
    private TypedComboBox <String> createAffectiveCombo(final ComboHandler <String> handler)
    {
        String[] options = new String[]
        {
            "happy", "excited", "upset", "frustrated", "bored"
        };

        return addHandler(Utilities.createComboBoxEditable(options), handler);
    }

    /**
     * Create and configure a combo box for emotions using the given handler.
     * 
     * @param handler
     * the handler.
     * 
     * @return the combo box.
     */
    private TypedComboBox <String> createEmotionCombo(final ComboHandler <String> handler)
    {
        String[] options = new String[]
        {
            "engaged", "disengaged", "bored", "frustrated", "motivated"
        };

        return addHandler(Utilities.createComboBoxEditable(options), handler);
    }

    /**
     * Create and configure a combo box for engagement using the given handler.
     * 
     * @param handler
     * the handler.
     * 
     * @return the combo box.
     */
    private TypedComboBox <String> createEngagementCombo(final ComboHandler <String> handler)
    {
        String[] options = new String[]
        {
            "engaged with system", "disengaged with system",
            "engaged with Andy", "disengaged with Andy"
        };

        TypedComboBox <String> comboBox = Utilities.createComboBoxUnsorted(options);

        return addHandler(comboBox, handler);
    }

    /**
     * Create a title string from the given ScertsGoal.
     * 
     * @param goal
     * the goal.
     * 
     * @return the title.
     */
    private String createTitle(ScertsGoalType goal)
    {
        return goal.getLabel();
    }

    /**
     * Create a title string from the given Feature.
     * 
     * @param feature
     * the feature.
     * 
     * @return the title.
     */
    private String createTitle(Feature feature)
    {
        String title = feature.getLabel();
        if (feature.isOptional())
        {
            // title += " (optional)";
        }

        return title;
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
     * Add the given handler as an action listener for the given text field.
     * 
     * @param <T>
     * the type.
     * 
     * @param textField
     * the text field.
     * 
     * @param handler
     * the handler.
     * 
     * @return the text field.
     */
    private JTextField addHandler(final JTextField textField,
                                  final ComboHandler <Object> handler)
    {
        if (handler != null)
        {
            textField.addActionListener(new ServerAction()
            {
                /**
               * 
               */
              private static final long serialVersionUID = 1L;

                public void serverActionPerformed(ActionEvent e)
                {
                    handler.handle(textField.getText());
                }

                public String getDescription()
                {
                    return handler.getDescription(textField.getText());
                }
            });
        }

        return textField;
    }

    /**
     * Set the visibility of the given component to the given value.
     * 
     * @param comp
     * the component.
     * 
     * @param visible
     * <code>true</code> to make the component visible; <code>false</code>
     * otherwise.
     * 
     * @return the component.
     */
    private <T extends JComponent> T setVisible(T comp, boolean visible)
    {
        return Utilities.setVisible(comp, visible);
    }

    /**
     * Tag the given component as a vision-substitute (whose visibility is
     * controlled by the isVisionSubstitute check box).
     * 
     * @param comp
     * the component.
     * 
     * @return the component.
     */
    private <T extends JComponent> T tagVisionSubstitute(T comp)
    {
        visionSubstitutes.add(comp);

        return comp;
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
     * The Scerts Goal types.
     * 
     * @author Elaine Farrow
     */
    private enum ScertsGoalType
    {
        INITIATE_JA("Initiates joint attention or interaction"),
        RESPOND_JA("Responds to joint attention or interaction"),
        RECIPROCAL_INTERACTION("Reciprocal interaction"),
        MONITOR_PARTNER("Monitors social partner"),
        SOCIAL_BEHAVIOURS("Social behaviours"),
        EMOTIONAL_EXPRESSION("Emotional expression"),
        IMITATIVE_BEHAVIOURS("Imitative behaviours");

        private final String label;

        ScertsGoalType(String label)
        {
            this.label = label;
        }

        public String getLabel()
        {
            return label;
        }
    }

    /**
     * The Features.
     * 
     * @author Elaine Farrow
     */
    private enum Feature
    {
        E1("E1",
           "Indicates understanding or appropriate response (verbal or nonverbal) to another's emotional state"),
        E2("E2", "Uses emotion words or signs to describe own emotional state"),

        LP_JA2_3("LP-JA2.3", "Monitors attentional focus of a social partner"),
        LP_JA2_4("LP-JA2.4",
                 "Secures attention to oneself prior to expressing intentions"),

        SP_JA1_1("SP-JA1.1", "Verbally responds to bid for interaction"),
        SP_JA1_2("SP-JA1.2", "Initiates verbal bids for interaction"),
        SP_JA1_3("SP-JA1.3", "Engages in brief reciprocal interaction sequence"),
        SP_JA1_4("SP-JA1.4",
                 "Engages in extended reciprocal interaction sequence"),
        SP_JA2_1("SP-JA2.1", "Looks towards agent"),
        SP_JA2_2("SP-JA2.2", "Shifts gaze between people and objects"),
        SP_JA4_1("SP-JA4.1", "Requests desired objects"),
        SP_JA4_4("SP-JA4.4", "Protests undesired actions/ activities"),
        SP_JA5_3("SP-JA5.3", "Turn taking"),
        SP_JA5_4("SP-JA5.4", "Verbal greets"),
        SP_JA5_4_2("SP-JA5.4", "Non-verbal greets"),

        SP_SU1_2("SP-SU1.2",
                 "Imitates familiar actions when elicited immediately after a model"),
        SP_SU1_3("SP-SU1.3",
                 "Spontaneously imitates familiar actions immediately after a model"),
        SP_SU1_4("SP-SU1.4",
                 "Spontaneously imitates familiar actions/ sounds at a later time");

        public String getLabel()
        {
            return id + ": " + description;
        }

        /**
         * Is this Feature optional?
         * 
         * @return <code>true</code> if it is optional; <code>false</code>
         * otherwise.
         */
        public boolean isOptional()
        {
            switch (this)
            {
                case E1:
                case LP_JA2_3:
                case SP_JA1_3:
                case SP_JA1_4:
                case SP_JA2_1:
                case SP_JA2_2:
                case SP_SU1_2:
                case SP_SU1_3:
                    return true;

                case E2:
                case LP_JA2_4:
                case SP_JA1_1:
                case SP_JA1_2:
                case SP_JA4_1:
                case SP_JA4_4:
                case SP_JA5_3:
                case SP_JA5_4:
                case SP_SU1_4:
                    return false;
            }

            return false;
        }

        private final String id;
        private final String description;

        private Feature(String id, String description)
        {
            this.id = id;
            this.description = description;
        }
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

        public void finalClientAction(ActionEvent e, Exception ex)
        {
            reportStatus(getDescription(), ex);
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
        new ActivityRecord(null).setVisible(true);
    }
}
