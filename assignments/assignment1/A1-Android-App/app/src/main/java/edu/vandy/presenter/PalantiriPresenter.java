package edu.vandy.presenter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import android.content.Intent;
import android.util.Log;
import edu.vandy.MVP;
import edu.vandy.common.GenericModel;
import edu.vandy.common.Utils;
import edu.vandy.model.PalantiriModel;
import edu.vandy.utils.Options;
import edu.vandy.view.DotArrayAdapter.DotColor;

/**
 * This class manages the Palantiri simulation.  The simulation begins
 * in the start() method, which is called by the UI Thread and is
 * provided a reference to MVP.RequiredViewOps, which is used to
 * manipulate the UI.  The Options singleton contains the number of
 * beings to simulate and the number of palantiri to simulate.
 * 
 * The simulation should run as follows: the correct number of
 * palantiri should be instantiated and added to the LeasePool in the
 * Model layer.  A Java thread should be created for each Being.  Each
 * Being thread should attempt to acquire a palantir a certain number
 * of times (defined via the GAZE_ATTEMPTS constant below).  As this
 * is happening, Being threads should call the appropriate methods in
 * MVP.RequiredViewOps to demonstrate which palantiri are being used
 * and which Beings currently own a palantir.
 *
 * This class plays the "Presenter" role in the Model-View-Presenter
 * (MVP) pattern by acting upon the Model and the View, i.e., it
 * retrieves data from the Model (e.g., PalantiriModel) and formats it
 * for display in the View (e.g., PalantiriActivity).  It expends the
 * GenericModel superclass and implements MVP.ProvidedPresenterOps and
 * MVP.RequiredModelOps so it can be created/managed by the
 * GenericModel framework.
 */
public class PalantiriPresenter 
       extends GenericModel<MVP.RequiredPresenterOps,
                            MVP.ProvidedModelOps,
                            PalantiriModel>
       implements MVP.ProvidedPresenterOps, 
                  MVP.RequiredPresenterOps {
    /**
     * Used for Android debugging.
     */
    private final static String TAG = 
        PalantiriPresenter.class.getName();

    /**
     * Keeps track of whether a runtime configuration change ever
     * occurred.
     */
    private boolean mConfigurationChangeOccurred;

    /**
     * Used to simplify actions performed by the UI, so the
     * application doesn't have to worry about it.
     */
    public WeakReference<MVP.RequiredViewOps> mView;

    /**
     * The list of Beings (implemented as concurrently executing Java
     * Threads) that are attempting to acquire Palantiri for gazing.
     */
    private List<BeingThread> mBeingsThreads;

    /**
     * The number of Beings that currently have a Palantir.
     */
    public AtomicLong mGazingThreads;

    /**
     * Tracks whether a simulation is currently running or not.
     */
    private boolean mRunning = false;

    /**
     * This List keeps track of how many palantiri we have and whether
     * they're in use or not.
     */
    private List<DotColor> mPalantiriColors =
        new ArrayList<>();
	
    /**
     * This List keeps track of how many beings we have and whether
     * they're gazing or not.
     */
    private List<DotColor> mBeingsColors =
        new ArrayList<>();

    /**
     * Default constructor that's needed by the GenericActivity
     * framework.
     */
    public PalantiriPresenter() {
    }

    /**
     * Hook method called when a new instance of PalantiriPresenter is
     * created.  One time initialization code goes here, e.g., storing
     * a WeakReference to the View layer and initializing the Model
     * layer.
     * 
     * @param view
     *            A reference to the View layer.
     */
    @Override
    public void onCreate(MVP.RequiredViewOps view) {
        // Set the WeakReference.
        mView =
            new WeakReference<>(view);

        // Invoke the special onCreate() method in GenericModel,
        // passing in the PalantiriModel class to instantiate/manage
        // and "this" to provide this MVP.RequiredModelOps instance.
        super.onCreate(PalantiriModel.class,
                       this);

        // Get the intent used to start the Activity.
        final Intent intent = view.getIntent();

        // Initialize the Options singleton using the extras contained
        // in the intent.
        if (Options.instance().parseArgs(view.getActivityContext(), 
                                         makeArgv(intent)) == false)
            Utils.showToast(view.getActivityContext(),
                            "Arguments were incorrect");

        // A runtime configuration change has not yet occurred.
        mConfigurationChangeOccurred = false;
    }

    /**
     * Hook method dispatched by the GenericActivity framework to
     * initialize the PalantiriPresenter object after it's been
     * created.
     *
     * @param view         
     *          The currently active MVP.RequiredViewOps.
     */
    @Override
    public void onConfigurationChange(MVP.RequiredViewOps view) {
        Log.d(TAG,
              "onConfigurationChange() called");

        // Reset the WeakReference.
        mView =
            new WeakReference<>(view);

        // A runtime configuration change occurred.
        mConfigurationChangeOccurred = true;
    }

    /**
     * Hook method called to shutdown the Model layer.
     *
     * @param isChangingConfigurations
     *        True if a runtime configuration triggered the onDestroy() call.
     */
    @Override
    public void onDestroy(boolean isChangingConfigurations) {
        // Destroy the model.
        getModel().onDestroy(isChangingConfigurations);
    }

    /**
     * Returns true if a configuration change has ever occurred, else
     * false.
     */
    @Override
    public boolean configurationChangeOccurred() {
        return mConfigurationChangeOccurred;
    }

    /**
     * Factory method that creates an Argv string containing the
     * options.
     */
    private String[] makeArgv(Intent intent) {
        // Create the list of arguments to pass to the Options
        // singleton.
        String argv[] = {
            "-b", // Number of Being threads.
            intent.getStringExtra("BEINGS"),
            "-p", // Number of Palantiri.
            intent.getStringExtra("PALANTIRI"),
            "-i", // Gazing iterations.
            intent.getStringExtra("GAZING_ITERATIONS"),
        };
        return argv;
    }

    /**
     * Returns true if the simulation is currently running, else false.
     */
    public boolean isRunning() {
        return mRunning;
    }

    /**
     * Sets whether the simulation is currently running or not.
     */
    public void setRunning(boolean running) {
        mRunning = running;
    }

    /**
     * Returns the List of Palantiri and whether they are gazing.
     */
    public List<DotColor> getPalantiriColors() {
        return mPalantiriColors;
    }

    /**
     * Returns the List of Beings and whether they are gazing.
     */
    public List<DotColor> getBeingsColors() {
        return mBeingsColors;
    }

    /**
     * This method is called when the user asks to start the
     * simulation in the context of the main UI Thread.  It creates
     * the designated number of Palantiri and adds them to the
     * PalantiriManager.  It then creates a Thread for each Being and
     * has each Being attempt to acquire a Palantir for gazing,
     * mediated by the PalantiriManager.  The BeingTheads call methods
     * from the MVP.RequiredViewOps interface to visualize what is
     * happening to the user.
     **/
    @Override
    public void start() {
        // Initialize the PalantiriManager.
        getModel().makePalantiri(Options.instance().numberOfPalantiri());

        // Initialize the count of the number of threads Beings use to
        // gaze.
        mGazingThreads = new AtomicLong(0);

        // Show the Beings on the UI.
        mView.get().showBeings();

        // Show the palantiri on the UI.
        mView.get().showPalantiri();

        // Create and start a BeingThread for each Being.
        beginBeingsThreads(Options.instance().numberOfBeings());

        // Start a thread to wait for all the Being threads to finish
        // and then inform the View layer that the simulation is done.
        waitForBeingsThreads();
    }

    /**
     * Create/start a List of BeingThreads that represent the Beings
     * in this simulation.  Each Thread is passed a BeingRunnable
     * parameter that performs the Being gazing logic.
     * 
     * @param beingCount
     *            Number of Being Threads to create.
     */
    private void beginBeingsThreads(int beingCount) {
        // Create an empty ArrayList, create new BeingThreads that
        // perform the BeingRunnable logic, add them to the ArrayList,
        // and then start all the BeingThreads in the ArrayList.
        // TODO - You fill in here.
    }

    /**
     * Start a thread to wait for all the Being threads to finish and
     * then inform the View layer that the simulation is done.
     */
    private void waitForBeingsThreads() {
        // Start a Java Thread that waits for all the BeingThreads to
        // finish and then calls mView.get().done() to inform the View
        // layer that the simulation is done.
        // @@ TODO -- you fill in here.
    }

    /**
     * This method is called if an unrecoverable exception occurs or
     * the user explicitly stops the simulation.  It shuts down all
     * the BeingThreads and notifies the View layer that
     */
    @Override
    public void shutdown() {
        synchronized(this) {
            // Shutdown all the BeingThreads.
            BeingThread.shutdown();

            // Inform the user that we're shutting down the
            // simulation.
            mView.get().shutdownOccurred(mBeingsThreads.size());
        }
    }
}
