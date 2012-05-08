package pedagogicComponent;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.CellRendererPane;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolTip;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicToolTipUI;
import utils.Enums.EchoesActivity;
import utils.Enums.EchoesObjectType;

/**
 * Utility classes and methods.
 * 
 * @author Elaine Farrow
 */
public class Utilities
{
    /**
     * Choose whether to show the help popups.
     */
    private static final boolean SHOW_HELP_POPUPS = true;

    /**
     * Key for popup help text.
     */
    protected static final String POPUP_HELP = "PopupHelpText";

    private Utilities()
    {
        // no instances
    }

    /**
     * Return a string describing the given object.
     * 
     * @param object
     * the object.
     * 
     * @return the string.
     */
    public static String toString(EchoesObjectType object)
    {
        switch (object)
        {
            case Ball:
                return "ball";
            case Basket:
                return "basket";
            case Bubble:
                return "bubble";
            case Cloud:
                return "cloud";
            case Container:
                return "container";
            case Flower:
                return "flower";
            case IntroBubble:
                return "intro bubble";
            case LifeTree:
                return "tree";
            case MagicLeaves:
                return "leaves";
            case Pond:
                return "pond";
            case Pot:
                return "pot";
            case Shed:
                return "shed";
            default:
                return object.toString();
        }
    }

    /**
     * Return a string describing the given activity.
     * 
     * @param activity
     * the activity.
     * 
     * @return the string.
     */
    public static String toString(EchoesActivity activity)
    {
        switch (activity)
        {
            case AgentPoke:
                return "poke Andy";
            case BallSorting:
                return "sort the balls";
            case BallThrowing:
                return "throw the balls";
            case BallThrowingContingent:
                return "throw the balls with Andy";
            case CloudRain:
                return "make the cloud rain";
            case Explore:
                return "explore";
            case ExploreWithAgent:
                return "explore with Andy";
            case FlowerGrow:
                return "grow the flowers";
            case FlowerPickToBasket:
                return "pick the flowers";
            case FlowerTurnToBall:
                return "turn the flowers into balls";
            case FlowerTurnToBallContingent:
                return "turn the flowers into balls with Andy";
            case PotStackRetrieveObject:
                return "stack the pots";
            case TickleAndTree:
                return "tickle Andy";
            default:
                return activity.toString();
        }
    }

    /**
     * Add the given components to the given panel using the given axis of
     * alignment. Add a button for the optional help text last.
     * 
     * @param panel
     * the panel panel.
     * 
     * @param components
     * the components.
     * 
     * @param axis
     * one of BoxLayout.LINE_AXIS or BoxLayout.PAGE_AXIS.
     * 
     * @param helpText
     * the help text (optional).
     * 
     * @return the panel.
     */
    public static <T extends JPanel> T addComponents(T panel,
                                                     List <? extends JComponent> components,
                                                     int axis,
                                                     String helpText)
    {
        boolean isVertical = (axis == BoxLayout.PAGE_AXIS);
        Dimension d = isVertical ? new Dimension(0, 10) : new Dimension(10, 0);

        panel.setLayout(new BoxLayout(panel, axis));

        JComponent lastComp = null;
        for (JComponent comp : components)
        {
            if (lastComp != null)
            {
                panel.add(setVisible(Box.createRigidArea(d), comp.isVisible()));
            }

            panel.add(comp);
            lastComp = comp;
        }

        panel.add(isVertical ? Box.createVerticalGlue() : Box.createHorizontalGlue());

        if (SHOW_HELP_POPUPS && helpText != null)
        {
            panel.add(Box.createRigidArea(d));
            panel.add(createHelp(helpText));
        }

        setMaximumHeight(panel);

        return panel;
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
    public static void addTab(JTabbedPane pane, TitledPanel panel)
    {
        pane.addTab(panel.getTitle(), panel);
    }

    /**
     * Create and configure a JButton using the given Action.
     * 
     * @param action
     * the action.
     * 
     * @return the button.
     */
    public static JButton createButton(Action action)
    {
        if (action.getValue(Action.NAME) == null)
        {
            action.putValue(Action.NAME, "Goal is satisfied");
        }

        return increaseMaximumHeight(new JButton(action));
    }

    /**
     * Create and configure a panel containing a JButton configured using the
     * given Action.
     * 
     * @param action
     * the action.
     * 
     * @return the panel.
     */
    public static JPanel createButtonPanel(Action action)
    {
        return createPanel(createButton(action),
                           (String) action.getValue(POPUP_HELP));
    }

    /**
     * Create and configure a check box with the given text and initial selected
     * state.
     * 
     * @param text
     * the text.
     * 
     * @param isSelected
     * <code>true</code> if the box should be selected; <code>false</code>
     * otherwise.
     * 
     * @return the check box.
     */
    public static JCheckBox createCheckBox(String text, boolean isSelected)
    {
        return setAlignment(new JCheckBox(text, isSelected));
    }

    /**
     * Create and configure a combo box with the given options. The options are
     * sorted alphabetically.
     * 
     * @param options
     * the options.
     * 
     * @return the combo box.
     */
    public static <T> TypedComboBox <T> createComboBox(Map <Object, T> options)
    {
        return initComboBox(new TypedComboBox <T>(options));
    }

    /**
     * Create and configure an editable combo box with the given options. The
     * options are sorted alphabetically.
     * 
     * @param options
     * the options.
     * 
     * @return the combo box.
     */
    public static <T> TypedComboBox <T> createComboBoxEditable(T[] options)
    {
        sortAlphabetically(options);

        TypedComboBox <T> comboBox = new TypedComboBox <T>(options);
        comboBox.setEditable(true);

        return initComboBox(comboBox);
    }

    /**
     * Configure the given combo box.
     * 
     * @param comboBox
     * the combo box.
     * 
     * @return the combo box.
     */
    private static <T> TypedComboBox <T> initComboBox(TypedComboBox <T> comboBox)
    {
        return increaseMaximumHeight(setMaximumWidth(setAlignment(comboBox)));
    }

    /**
     * Create and configure a combo box with the given options. The options are
     * not sorted.
     * 
     * @param options
     * the options.
     * 
     * @return the combo box.
     */
    public static <T> TypedComboBox <T> createComboBoxUnsorted(T[] options)
    {
        return initComboBox(new TypedComboBox <T>(options));
    }

    /**
     * Create help text with the given text.
     * 
     * @param helpText
     * the text.
     * 
     * @return the help.
     */
    public static JLabel createHelp(String helpText)
    {
        if (! SHOW_HELP_POPUPS)
        {
            return new JLabel();
        }

        JLabel label = new JLabel("?")
        {
            /**
           * 
           */
          private static final long serialVersionUID = 1L;

            public JToolTip createToolTip()
            {
                return Utilities.createToolTip(25, new Color(255, 255, 155));
            }
        };

        setAlignment(label);
        label.setOpaque(true);
        label.setBackground(Color.BLUE);
        label.setForeground(Color.WHITE);
        label.setToolTipText(helpText);
        label.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        setMaximumWidth(label);
        setMaximumHeight(label);

        return label;
    }

    /**
     * Create and configure a JLabel using the given text.
     * 
     * @param text
     * the text.
     * 
     * @return the label.
     */
    public static JLabel createLabel(String text)
    {
        return setAlignment(new JLabel(text));
    }

    /**
     * Create and configure a horizontal panel with the given component.
     * 
     * @param component
     * the component.
     * 
     * @return the panel.
     */
    public static JPanel createPanel(JComponent component)
    {
        return createPanel(component, null);
    }

    /**
     * Create and configure a horizontal panel with the given component and
     * optional help text.
     * 
     * @param component
     * the component.
     * 
     * @param helpText
     * the help text (optional).
     * 
     * @return the panel.
     */
    public static JPanel createPanel(JComponent component, String helpText)
    {
        return createPanel(null,
                           Collections.singletonList(component),
                           BoxLayout.LINE_AXIS,
                           helpText);
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
    public static TitledPanel createPanel(String title,
                                          List <? extends JComponent> components,
                                          int axis)
    {
        return createPanel(title, components, axis, null);
    }

    /**
     * Create and configure a panel with the given title, components and axis of
     * alignment. If help text is provided, add it at the end.
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
     * @param helpText
     * the help text (optional).
     * 
     * @return the panel.
     */
    public static TitledPanel createPanel(String title,
                                          List <? extends JComponent> components,
                                          int axis,
                                          String helpText)
    {
        TitledPanel panel = setAlignment(new TitledPanel(title));

        return addComponents(panel, components, axis, helpText);
    }

    /**
     * Create the status field.
     * 
     * @return the field.
     */
    public static JTextField createStatusField()
    {
        return createTextField("", false);
    }

    /**
     * Create and configure a JTextArea using the given text.
     * 
     * @param text
     * the text.
     * 
     * @return the area.
     */
    public static JTextArea createTextArea(String text)
    {
        JTextArea area = new JTextArea(text);
        area.setEditable(false);
        area.setFocusable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);

        JLabel label = new JLabel();
        area.setBackground(label.getBackground());
        area.setForeground(label.getForeground());
        area.setFont(label.getFont());

        return setAlignment(area);
    }

    /**
     * Create and configure a JTextField using the given text and initial
     * editable state.
     * 
     * @param text
     * the text.
     * 
     * @param isEditable
     * <code>true</code> if the text is editable; <code>false</code> otherwise.
     * 
     * @return the field.
     */
    public static JTextField createTextField(String text, boolean isEditable)
    {
        JTextField field = new JTextField(text, 20);
        field.setEditable(isEditable);
        field.setFocusable(isEditable);

        return increaseMaximumHeight(setAlignment(field));
    }

    /**
     * Create a popup yes/no dialog with the given options; if the user chooses
     * 'yes', execute the given runnable on the Swing thread. The dialog is
     * non-modal.
     * 
     * @param parentComponent
     * the parent component.
     * 
     * @param message
     * the message.
     * 
     * @param title
     * the title.
     * 
     * @param runnable
     * the runnable.
     * 
     * @return the dialog.
     */
    public static JDialog createConfirmDialog(Component parentComponent,
                                              Object message,
                                              String title,
                                              final Runnable runnable)
    {
        final JOptionPane pane = new JOptionPane(message,
                                                 JOptionPane.QUESTION_MESSAGE,
                                                 JOptionPane.YES_NO_OPTION);

        final JDialog dialog = pane.createDialog(parentComponent, title);

        dialog.setModal(false);
        dialog.addComponentListener(new ComponentAdapter()
        {
            public void componentHidden(ComponentEvent e)
            {
                Object selectedValue = pane.getValue();
                if (selectedValue instanceof Integer)
                {
                    int result = ((Integer) selectedValue).intValue();
                    if (JOptionPane.YES_OPTION == result)
                    {
                        SwingUtilities.invokeLater(runnable);
                    }
                }
            }
        });

        return dialog;
    }

    /**
     * Set the alignment of the given component to centre-left.
     * 
     * @param comp
     * the component.
     * 
     * @return the component.
     */
    public static <T extends JComponent> T setAlignment(T comp)
    {
        comp.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        comp.setAlignmentY(JComponent.CENTER_ALIGNMENT);

        return comp;
    }

    /**
     * Increase the preferred height of the given component.
     * 
     * @param comp
     * the component.
     * 
     * @return the component.
     */
    private static <T extends JComponent> T increasePreferredHeight(T comp)
    {
        Dimension pSize = comp.getPreferredSize();
        pSize.height *= 2;
        comp.setPreferredSize(pSize);

        return comp;
    }

    /**
     * Increase the maximum height of the given component.
     * 
     * @param comp
     * the component.
     * 
     * @return the component.
     */
    private static <T extends JComponent> T increaseMaximumHeight(T comp)
    {
        return setMaximumHeight(increasePreferredHeight(comp));
    }

    /**
     * Set the maximum height of the given component to its preferred height.
     * 
     * @param comp
     * the component.
     * 
     * @return the component.
     */
    public static <T extends Component> T setMaximumHeight(T comp)
    {
        Dimension pSize = comp.getPreferredSize();
        Dimension mSize = comp.getMaximumSize();
        comp.setMaximumSize(new Dimension(mSize.width, pSize.height));

        return comp;
    }

    /**
     * Set the maximum width of the given component to its preferred width.
     * 
     * @param comp
     * the component.
     * 
     * @return the component.
     */
    public static <T extends Component> T setMaximumWidth(T comp)
    {
        Dimension pSize = comp.getPreferredSize();
        Dimension mSize = comp.getMaximumSize();
        comp.setMaximumSize(new Dimension(pSize.width, mSize.height));

        return comp;
    }

    /**
     * Set the colour of the components in the given panel to the given colour.
     * 
     * @param panel
     * the panel.
     * 
     * @param color
     * the colour.
     * 
     * @return the panel.
     */
    public static <T extends JPanel> T setPanelColor(T panel, Color color)
    {
        for (Component comp : panel.getComponents())
        {
            if (comp instanceof JPanel)
            {
                setPanelColor((JPanel) comp, color);
            }
            else
            {
                comp.setBackground(color);
            }
        }

        return panel;
    }

    /**
     * Set the background colour of the given panel to the given colour.
     * 
     * @param panel
     * the panel.
     * 
     * @param color
     * the colour.
     * 
     * @return the panel.
     */
    public static <T extends JPanel> T setPanelBg(T panel, Color color)
    {
        panel.setBackground(color);
        for (Component comp : panel.getComponents())
        {
            if (comp instanceof JPanel)
            {
                setPanelBg((JPanel) comp, color);
            }
        }

        return panel;
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
    public static <T extends Component> T setVisible(T comp, boolean visible)
    {
        comp.setVisible(visible);

        return comp;
    }

    /**
     * Create a component to display the given clock.
     * 
     * @param clock
     * the clock.
     * 
     * @param format
     * the display format.
     * 
     * @param refreshRate
     * the refresh rate in milliseconds.
     * 
     * @return the component.
     */
    public static JComponent createClockDisplay(final Clock clock,
                                                String format,
                                                int refreshRate)
    {
        final Format formatter = new SimpleDateFormat(format);

        final JLabel display = new JLabel();

        // make the font big and bold
        Font font = display.getFont().deriveFont(Font.BOLD);
        font = font.deriveFont(font.getSize() * 2.0f);

        display.setFont(font);
        display.setOpaque(true);

        Timer timer = new Timer(refreshRate, new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                display.setText(formatter.format(new Date(clock.get())));
            }
        });

        timer.setInitialDelay(0);
        timer.start();

        return display;
    }

    /**
     * A clock to keep track of elapsed time.
     * 
     * @author Elaine Farrow
     */
    public static final class Clock
    {
        /**
         * The start time.
         */
        private long start = 0;

        /**
         * The elapsed time.
         */
        private long elapsed = 0;

        /**
         * Get the current elapsed time (in milliseconds).
         * 
         * @return the elapsed time.
         */
        public synchronized long get()
        {
            if (start > 0)
            {
                // clock is running so update elapsed time
                elapsed = System.currentTimeMillis() - start;
            }

            return elapsed;
        }

        /**
         * Start (or restart) the clock.
         */
        public synchronized void start()
        {
            if (start == 0)
            {
                start = System.currentTimeMillis() - elapsed;
            }
        }

        /**
         * Pause the clock.
         */
        public synchronized void pause()
        {
            // remember current elapsed time
            get();

            start = 0;
        }

        /**
         * Reset the clock to zero.
         */
        public synchronized void reset()
        {
            start = 0;
            elapsed = 0;
        }
    }

    /**
     * A panel that has a title.
     * 
     * @author Elaine Farrow
     */
    public static final class TitledPanel extends JPanel
    {
        /**
       * 
       */
      private static final long serialVersionUID = 1L;
        private final String title;

        public TitledPanel(String title)
        {
            this.title = title;

            if (title != null)
            {
                setBorder(new TitledBorder(title));
            }
        }

        public String getTitle()
        {
            return title;
        }
    }

    /**
     * A typed combo box.
     * 
     * @author Elaine Farrow
     */
    @SuppressWarnings("rawtypes")
    public static final class TypedComboBox <T> extends JComboBox
    {
        /**
       * 
       */
      private static final long serialVersionUID = 1L;
        private final Map <Object, T> map = new HashMap <Object, T>();

        @SuppressWarnings("unchecked")
        public TypedComboBox(T[] items)
        {
            super(items);

            for (T item : items)
            {
                map.put(item, item);
            }
        }

        @SuppressWarnings("unchecked")
        public TypedComboBox(Vector <T> items)
        {
            super(items);

            for (T item : items)
            {
                map.put(item, item);
            }
        }

        @SuppressWarnings("unchecked")
        public TypedComboBox(Map <Object, T> items)
        {
            super(getKeys(items));

            map.putAll(items);
        }

        public T getSelectedObject()
        {
            return map.get(getSelectedItem());
        }

        private static Vector <Object> getKeys(Map <Object, ?> items)
        {
            Vector <Object> keys = new Vector <Object>(items.keySet());

            if (items instanceof LinkedHashMap)
            {
                return keys;
            }

            return sortAlphabetically(keys);
        }
    }

    /**
     * A handler for a combo box.
     * 
     * @author Elaine Farrow
     */
    public abstract static class ComboHandler <T>
    {
        /**
         * Handle the selected item.
         * 
         * @param selection
         * the selected item.
         */
        public abstract void handle(T selection);

        /**
         * Get a description of the action performed for the selected item.
         * 
         * @param label
         * the name of the selected item.
         */
        public abstract String getDescription(Object label);
    }

    /**
     * An action that is performed on the server on a separate thread.
     * 
     * @author Elaine Farrow
     */
    public abstract static class AbstractServerAction extends AbstractAction
    {
        /**
       * 
       */
      private static final long serialVersionUID = 1L;

        public AbstractServerAction()
        {
            super();
        }

        public AbstractServerAction(String name)
        {
            super(name);
        }

        /**
         * Perform an initial action on the GUI thread in response to the given
         * action event.
         * 
         * @param e
         * the action event.
         */
        public void initialClientAction(ActionEvent e)
        {
            // do nothing here -- can override
        }

        /**
         * Perform an action on a new server thread in response to the given
         * action event.
         * 
         * @param e
         * the action event.
         */
        public abstract void serverActionPerformed(ActionEvent e);

        /**
         * Get a description of the action performed.
         */
        public abstract String getDescription();

        /**
         * Perform a final action on the GUI thread in response to the given
         * action event.
         * 
         * @param e
         * the action event.
         * 
         * @param ex
         * the exception thrown by the server action, if any; <code>null</code>
         * otherwise.
         */
        public void finalClientAction(ActionEvent e, Exception ex)
        {
            // do nothing here -- can override
        }

        @Override
        public final void actionPerformed(final ActionEvent e)
        {
            initialClientAction(e);

            new SwingWorker <Boolean, Object>()
            {
                @Override
                protected Boolean doInBackground()
                    throws Exception
                {
                    log(getDescription() + "... ");

                    try
                    {
                        serverActionPerformed(e);
                        logSuccess();
                        return true;
                    }
                    catch (Exception ex)
                    {
                        logFailure(ex);
                        throw (ex);
                    }
                }

                @Override
                protected void done()
                {
                    Exception exception = null;

                    try
                    {
                        get();
                    }
                    catch (Exception ex)
                    {
                        exception = ex;
                    }

                    finalClientAction(e, exception);
                }
            }.execute();
        }
    }

    /**
     * An action that has two alternatives to be performed on the server on a
     * separate thread.
     * 
     * @author Elaine Farrow
     */
    public abstract static class AbstractToggleAction extends
        AbstractServerAction
    {
        /**
       * 
       */
      private static final long serialVersionUID = 1L;
        private final String label1;
        private final String label2;

        /**
         * Create a new ToggleAction with the given two actions.
         * 
         * @param label1
         * the label for the first action.
         * 
         * @param label2
         * the label for the second action.
         */
        public AbstractToggleAction(String label1, String label2)
        {
            super(label1);

            this.label1 = label1;
            this.label2 = label2;
        }

        /**
         * Notify the given component that the selected action has changed.
         * 
         * @param comp
         * the component.
         * 
         * @param isAction1
         * <code>true</code> if the first action was performed;
         * <code>false</code> if it was the second.
         */
        public void toggleChanged(JComponent comp, boolean isAction1)
        {
            // do nothing
        }

        /**
         * Perform an action on a new server thread in response to the given
         * action event.
         * 
         * @param e
         * the action event.
         * 
         * @param isAction1
         * <code>true</code> if the first action was performed;
         * <code>false</code> if it was the second.
         */
        public abstract void serverActionPerformed(ActionEvent e,
                                                   boolean isAction1);

        public final void serverActionPerformed(ActionEvent e)
        {
            serverActionPerformed(e, isAction1());
        }

        public void finalClientAction(ActionEvent e, Exception ex)
        {
            if (ex == null)
            {
                toggleAction();
            }
        }

        public String getDescription()
        {
            return String.valueOf(getValue(Action.NAME));
        }

        /**
         * Register the given component to be notified when the selected action
         * has changed.
         * 
         * @param comp
         * the component.
         */
        public void register(final JComponent comp)
        {
            addPropertyChangeListener(new PropertyChangeListener()
            {
                public void propertyChange(PropertyChangeEvent evt)
                {
                    toggleChanged(comp, isAction1());
                }
            });

            // initialise the component
            toggleChanged(comp, isAction1());
        }

        /**
         * Toggle the action.
         */
        protected void toggleAction()
        {
            putValue(NAME, isAction1() ? label2 : label1);
        }

        /**
         * Is the first action current?
         * 
         * @return <code>true</code> if the first action is current;
         * <code>false</code> if it is the second.
         */
        boolean isAction1()
        {
            return label1.equals(getValue(NAME));
        }
    }

    /**
     * Sort the given options alphabetically by toString() value.
     * 
     * @param <T>
     * the type.
     * 
     * @param options
     * the options.
     * 
     * @return the sorted options.
     */
    public static <T> T[] sortAlphabetically(T[] options)
    {
        Arrays.sort(options, new Comparator <T>()
        {
            @Override
            public int compare(T o1, T o2)
            {
                String s1 = o1.toString();
                String s2 = o2.toString();

                return s1.compareTo(s2);
            }
        });

        return options;
    }

    /**
     * Sort the given options alphabetically by toString() value.
     * 
     * @param <T>
     * the type.
     * 
     * @param options
     * the options.
     * 
     * @return the sorted options.
     */
    public static <T> Vector <T> sortAlphabetically(Vector <T> options)
    {
        Collections.sort(options, new Comparator <T>()
        {
            @Override
            public int compare(T o1, T o2)
            {
                String s1 = o1.toString();
                String s2 = o2.toString();

                return s1.compareTo(s2);
            }
        });

        return options;
    }

    public static JToolTip createToolTip(int columns, Color bgColor)
    {
        JMultiLineToolTip toolTip = new JMultiLineToolTip();

        toolTip.setColumns(columns);
        toolTip.setBackground(bgColor);
        toolTip.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        return toolTip;
    }

    /**
     * Found online at http://www.codeguru.com/java/articles/122.shtml
     * 
     * @author Zafir Anjum
     */
    public static class JMultiLineToolTip extends JToolTip
    {

      private static final long serialVersionUID = 1L;
      protected int columns = 0;
      protected int fixedwidth = 0;

      public JMultiLineToolTip()
      {
          updateUI();
      }

      public void updateUI()
      {
          setUI(MultiLineToolTipUI.createUI(this));
      }

      public void setColumns(int columns)
      {
          this.columns = columns;
          this.fixedwidth = 0;
      }

      public int getColumns()
      {
          return columns;
      }

      public void setFixedWidth(int width)
      {
          this.fixedwidth = width;
          this.columns = 0;
      }

      public int getFixedWidth()
      {
          return fixedwidth;
      }
    }

    private static class MultiLineToolTipUI extends BasicToolTipUI
    {
        static MultiLineToolTipUI sharedInstance = new MultiLineToolTipUI();
        @SuppressWarnings("unused")
        static JToolTip tip;
        protected CellRendererPane rendererPane;

        private static JTextArea textArea;

        public static ComponentUI createUI(JComponent c)
        {
            return sharedInstance;
        }

        public MultiLineToolTipUI()
        {
            super();
        }

        public void installUI(JComponent c)
        {
            super.installUI(c);
            tip = (JToolTip) c;
            rendererPane = new CellRendererPane();
            c.add(rendererPane);
        }

        public void uninstallUI(JComponent c)
        {
            super.uninstallUI(c);

            c.remove(rendererPane);
            rendererPane = null;
        }

        public void paint(Graphics g, JComponent c)
        {
            Dimension size = c.getSize();
            textArea.setBackground(c.getBackground());
            rendererPane.paintComponent(g, textArea, c, 1, 1, size.width - 1, size.height - 1, true);
        }

        public Dimension getPreferredSize(JComponent c)
        {
            String tipText = ((JToolTip) c).getTipText();
            if (tipText == null)
                return new Dimension(0, 0);
            textArea = new JTextArea(tipText);
            rendererPane.removeAll();
            rendererPane.add(textArea);
            textArea.setWrapStyleWord(true);
            int width = ((JMultiLineToolTip) c).getFixedWidth();
            int columns = ((JMultiLineToolTip) c).getColumns();

            if (columns > 0)
            {
                textArea.setColumns(columns);
                textArea.setSize(0, 0);
                textArea.setLineWrap(true);
                textArea.setSize(textArea.getPreferredSize());
            }
            else if (width > 0)
            {
                textArea.setLineWrap(true);
                Dimension d = textArea.getPreferredSize();
                d.width = width;
                d.height++;
                textArea.setSize(d);
            }
            else
                textArea.setLineWrap(false);

            Dimension dim = textArea.getPreferredSize();

            dim.height += 1;
            dim.width += 1;
            return dim;
        }

        public Dimension getMinimumSize(JComponent c)
        {
            return getPreferredSize(c);
        }

        public Dimension getMaximumSize(JComponent c)
        {
            return getPreferredSize(c);
        }
    }

    /**
     * Log the given string to stdout.
     * 
     * @param s
     * the string.
     */
    static void log(String s)
    {
        System.out.println(s);
    }

    /**
     * Log a success message to stdout.
     */
    static void logSuccess()
    {
        log("  succeeded");
    }

    /**
     * Log the given exceptions to stdout.
     * 
     * @param ex
     * the exception.
     */
    static void logFailure(Exception ex)
    {
        if (ex instanceof UnsupportedOperationException)
        {
            log("  operation not supported");
        }
        else
        {
            log("  failed");
            ex.printStackTrace(System.out);
        }
    }
}
