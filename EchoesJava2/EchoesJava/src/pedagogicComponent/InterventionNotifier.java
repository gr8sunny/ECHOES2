package pedagogicComponent;

import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

import pedagogicComponent.InterventionOptions.Intervention;

/**
 * Register to be notified about interventions.
 * 
 * @author Elaine Farrow
 */
public class InterventionNotifier extends Observable
{
    /**
     * The last timer, if any.
     */
    private Timer timer;

    /**
     * Cancel the last intervention.
     */
    public synchronized void cancelIntervention()
    {
        if (timer != null)
        {
            timer.cancel();
        }
    }

    /**
     * Schedule the given intervention to take place after the given delay.
     * 
     * @param intervention
     * the intervention.
     * 
     * @param delay
     * the delay in milliseconds.
     */
    public synchronized void scheduleIntervention(final Intervention intervention,
                                                  long delay)
    {
        cancelIntervention();
        setChanged();

        timer = new Timer();
        timer.schedule(new TimerTask()
        {
            public void run()
            {
                notifyObservers(intervention);
            }
        }, delay);
    }
}
