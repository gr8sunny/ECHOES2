/**
 * 
 */
package uk.ac.hw.echoes.child_model;

import Ice.Application;
import Ice.Identity;
import Ice.ObjectAdapter;
import Ice.Util;

/**
 * @author Mary Ellen Foster
 *
 */
public class ChildModelApp extends Application {

	@Override
    public int run(String[] args) {
		// Create an adapter to communicate with the IceGrid manager (so we can
		// be started and stopped cleanly)
		ObjectAdapter adapter = communicator().createObjectAdapter("Adapter");

        final EngagementClassifier eClassifier = new EngagementClassifier(adapter);
        
        ChildModelImpl cmImpl = new ChildModelImpl(eClassifier);
		
		Identity id = Util.stringToIdentity(communicator().getProperties()
				.getProperty("Identity"));
		adapter.add(cmImpl, id);
                
		// We're ready to go!
		communicator().getLogger().trace("info", "Calling adapter.activate()");
		adapter.activate();
		
		eClassifier.start();
		
		// Process messages ... this will return only when we're shut down
		// through IceGrid
		communicator().getLogger().trace("info", "waitForShutdown() called");
		communicator().waitForShutdown();
		communicator().getLogger().trace("info",
				"waitForShutdown() returned; exiting");
		
		eClassifier.stop();
		
		return 0;
    }
    
	public static void main(String[] args) {
		ChildModelApp app = new ChildModelApp();
		System.exit(app.main("ChildModel", args));
	}

}
