package pedagogicComponent;

import java.util.Timer;
import java.util.TimerTask;

import utils.Interfaces.IActionEngine;
import utils.Interfaces.IDramaManager;

public class Reminder extends PCcomponentHandler {
	Timer timer;
	String purpose;

	public Reminder(PCcomponents pCc, IDramaManager dmPrx,
			IActionEngine aePrx) {
		super(pCc, dmPrx, aePrx);
	}

	public void startTime(int milliseconds, String purpose) {
		this.purpose = purpose;
		timer = new Timer();
		timer.schedule(new RemindTask(), milliseconds);
	}

	class RemindTask extends TimerTask {
		public void run() {
			if (purpose.equals("assessGive")) {
				getPCcs().agentH.checkGiveStatus();
			} else if (purpose.equals("madeBid")) {
				System.out.println("practitioner feedback");
				// pop up window for practitioner - did child respond? window
				// should time out too
			}
			timer.cancel(); // Terminate the timer thread
		}
	}
}
