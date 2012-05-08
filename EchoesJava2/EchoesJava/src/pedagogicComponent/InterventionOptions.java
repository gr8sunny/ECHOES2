package pedagogicComponent;

import javax.swing.Action;
import javax.swing.JDialog;

/**
 * Options for when the child is not responding to a bid for interaction.
 * 
 * @author Elaine Farrow
 */
public class InterventionOptions
{
    /**
     * The time (in milliseconds) before an intervention popup disappears by
     * itself.
     */
    public static final long POPUP_TIMEOUT = 30000;

    /**
     * The parent window.
     */
    private final PractitionerWindow parent;

    /**
     * The currently visible popup dialog, if any.
     */
    private JDialog visibleDialog = null;

    public enum Intervention
    {
        AGENT_PROMPT_AGAIN("Echoes has detected that the child is not interacting. Do you want Andy to prompt the child again?",
                           "Tell Andy to prompt the child again?"),
        AGENT_TAKE_TURN("Do you want Andy to take his turn?",
                        "Tell Andy to take his turn?"),
        AGENT_ENTER("Echoes has detected that the child is not interacting. Do you want Andy to enter the scene?",
                    "Tell Andy to enter?"),
        AGENT_LEAVE("Do you want Andy to leave the scene?",
                    "Tell Andy to leave?"),
        END_SESSION("Do you want to end the session?", "End the session?");

        /**
         * The message.
         */
        final String message;

        /**
         * The title.
         */
        final String title;

        private Intervention(String message, String title)
        {
            this.message = message;
            this.title = title;
        }
    }

    /**
     * Create a new InterventionOptions as a child of the given
     * PractitionerWindow.
     * 
     * @param parent
     * the PractitionerWindow which is a parent of this one.
     */
    public InterventionOptions(final PractitionerWindow parent)
    {
        this.parent = parent;
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
     * Show a popup yes/no dialog for the given intervention; if the user
     * chooses 'yes', perform the relevant action.
     * 
     * @param intervention
     * the intervention.
     */
    public void popupConfirmAction(Intervention intervention)
    {
        final JDialog dialog = createConfirmDialog(intervention);

        new Thread()
        {
            public void run()
            {
                showDialog(dialog);

                try
                {
                    sleep(POPUP_TIMEOUT);
                }
                catch (InterruptedException e)
                {
                    // ignore
                }
                finally
                {
                    hideDialog(dialog);
                }
            }
        }.start();
    }

    /**
     * Create a popup yes/no dialog for the given intervention; if the user
     * chooses 'yes', perform the relevant action.
     * 
     * @param intervention
     * the intervention.
     */
    private JDialog createConfirmDialog(Intervention intervention)
    {
        final Action action = getAction(intervention);

        Runnable runnable = new Runnable()
        {
            public void run()
            {
                action.actionPerformed(null);
            }
        };

        return Utilities.createConfirmDialog(parent,
                                             intervention.message,
                                             intervention.title,
                                             runnable);
    }

    /**
     * Show the given dialog, and remember it for later.
     * 
     * @param dialog
     * the dialog.
     */
    void showDialog(JDialog dialog)
    {
        synchronized (this)
        {
            // if there's another dialog visible, hide it first
            hideDialog(visibleDialog);

            // remember this one instead
            visibleDialog = dialog;
        }

        synchronized (this)
        {
            if (visibleDialog != null)
            {
                visibleDialog.setVisible(true);
            }
        }
    }

    /**
     * Hide the given dialog. If it is the last one we remembered, forget it
     * now.
     * 
     * @param dialog
     * the dialog.
     */
    void hideDialog(JDialog dialog)
    {
        synchronized (this)
        {
            if (visibleDialog == dialog)
            {
                visibleDialog = null;
            }
        }

        if (dialog != null)
        {
            // hide this one
            dialog.setVisible(false);
            dialog.dispose();
        }
    }

    /**
     * Get the action for the given intervention.
     * 
     * @param intervention
     * the intervention.
     * 
     * @return the action.
     */
    private Action getAction(Intervention intervention)
    {
        switch (intervention)
        {
            case AGENT_ENTER:
                return parent.agentEnterAction;
            case AGENT_LEAVE:
                return parent.agentLeaveAction;
            case AGENT_PROMPT_AGAIN:
                return parent.agentPromptAgainAction;
            case AGENT_TAKE_TURN:
                return parent.agentTakeTurnAction;
            case END_SESSION:
                return parent.endSessionAction;
        }

        throw new IllegalArgumentException("Unknown intervention "
                                           + intervention);
    }
}
