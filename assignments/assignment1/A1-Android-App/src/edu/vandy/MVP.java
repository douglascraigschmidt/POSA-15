package edu.vandy;

import java.util.List;

import android.content.Intent;
import edu.vandy.common.ContextView;
import edu.vandy.common.ModelOps;
import edu.vandy.common.PresenterOps;
import edu.vandy.model.Palantir;
import edu.vandy.view.DotArrayAdapter.DotColor;

/**
 * Defines the interfaces for the PalantirManager app that are
 * required and provided by the layers in the Model-View-Presenter
 * (MVP) pattern.  This design ensures loose coupling between the
 * layers in the app's MVP-based architecture.
 */
public interface MVP {
    /**
     * This interface allows the View layer to display Palantirs and
     * Beings on the UI without knowing any implementation details of
     * the UI.  Calls to this interface will be converted to Runnable
     * commands and run on the UI thread using the Android HaMeR
     * framework.
     */
    public interface RequiredViewOps
           extends ContextView {
        /**
         * Show the palantiri on the screen.  All palantiri will be
         * marked as unused by default.
         */
        void showPalantiri();
	
        /**
         * Mark a certain palantir as being used.
         */
        void markUsed(int index);
	
        /**
         * Mark a certain palantir as free.
         */
        void markFree(int index);
	
        /**
         * Show the Beings on the screen.  All beings will be marked
         * as not gazing by default.
         */
        void showBeings();
	
        /**
         * Mark a certain being as gazing at a palantir.
         */
        void markGazing(int index);

        /**
         * Mark a certain being as waiting for a Palantir palantir.
         */
        void markWaiting(int index);
	
        /**
         * Mark a certain being as idle (i.e. not gazing or waiting)
         */
        void markIdle(int index);
    
        /**
         * Tell the user that the simulation is done.
         */
        void done();
    
        /**
         * Called when a shutdown occurs.  Pops a toast to notify the
         * user.
         */
        void shutdownOccurred(int numberOfSimulationThreads);
    
        /**
         * Tell the user that a thread was shutdown.
         */
        void threadShutdown(int index);

        /**
         * Return the intent used to start the Activity that
         * implements the View.
         */
        Intent getIntent();
    }

    /**
     * This interface allows the View layer to call methods in the
     * Presenter layer without knowing any implementation details.
     */
    public interface ProvidedPresenterOps
           extends PresenterOps<MVP.RequiredViewOps> {
        /**
         * This method is called if an unrecoverable exception occurs
         * or if the user presses the "stop simulation" button.  It
         * interrupts all the other threads and notifies the UI.
         */
        void shutdown();

        /**
         * This method is called when the user asks to start the
         * simulation in the context of the main UI Thread.  It
         * creates the number of Palantiri designated in the Options
         * singleton and adds them to the PalantiriManager.  It then
         * creates a Thread for each Being and has each Being attempt
         * to acquire a Palantir for gazing, mediated by the
         * PalantiriManager.  The Being Theads call methods in the
         * RequiredViewOps interface to visualize what is happening to
         * the user.
         **/
        void start();

        /**
         * Returns true if the simulation is currently running, else
         * false.
         */
        boolean isRunning();

        /**
         * Sets whether the simulation is currently running or not.
         */
        void setRunning(boolean running);

        /**
         * Returns true if a configuration change has ever occurred, else
         * false.
         */
        boolean configurationChangeOccurred();

       /**
         * Returns the List of Palantiri and whether they are gazing.
         */
        List<DotColor> getPalantiriColors();

        /**
         * Returns the List of Beings and whether they are gazing.
         */
        List<DotColor> getBeingsColors();
    }

    /**
     * This interface is a no-op since the Model layer doesn't require
     * any methods from the Presenter layer.
     */
    public interface RequiredPresenterOps {
    }

    /**
     * This interface allows the Presenter layer to call methods in
     * the Model layer without knowing any implementation details.
     */
    public interface ProvidedModelOps 
           extends ModelOps<MVP.RequiredPresenterOps> {
        /**
         * Create a resource manager that contains the designated
         * number of Palantir with random gaze times between 1 and 5
         * milliseconds "Fair" semantics should be used to instantiate
         * the Semaphore.
         *
         * @param palantiriCount
         *            The number of Palantiri to add to the PalantirManager.
         */
        void makePalantiri(int palantiriCount);

        /**
         * Get the next available Palantir from the resource pool,
         * blocking until one is available.
         */
        Palantir acquirePalantir();

        /**
         * Releases the designated @code palantir so it's available
         * for other Beings to use.
         */
        void releasePalantir(final Palantir palantir);
    }
}
