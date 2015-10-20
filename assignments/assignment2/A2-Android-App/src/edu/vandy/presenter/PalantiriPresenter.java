package edu.vandy.presenter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import edu.vandy.MVP;
import edu.vandy.common.Utils;
import edu.vandy.common.GenericPresenter;
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
       extends GenericPresenter<MVP.RequiredPresenterOps,
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
    private List<BeingAsyncTask> mBeingsTasks;

    /**
     * The number of Beings that currently have a Palantir.
     */
    public AtomicLong mGazingTasks;

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
     * A CountDownLatch that ensures all Threads exit as a group.
     */
    private CountDownLatch mExitBarrier;

    /**
     * A ThreadFactory object that spawns an appropriately named
     * Thread for each Being.
     */
    private ThreadFactory mThreadFactory =
        new ThreadFactory() {
            /**
             * Give each Being a uniquely numbered name.
             */
            private final AtomicInteger mBeingCount =
                new AtomicInteger(1);

            /**
             * Construct a new Thread.
             */
            public Thread newThread(Runnable runnable) {
                // Create a new BeingThread whose name uniquely
                // identifies each Being.
                // TODO -- you fill in here by replacing "return null"
                // with the appropriate code.
                return null;
            }
        };

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

        // Invoke the special onCreate() method in GenericPresenter,
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
     * @param isChangeConfigurations
     *        True if a runtime configuration triggered the onDestroy() call.
     */
    @Override
    public void onDestroy(boolean isChangingConfigurations) {
        // Destroy the model.
        // getModel().onDestroy(isChangingConfigurations);
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
     * This method is called if an unrecoverable exception occurs or
     * the user explicitly stops the simulation.  It interrupts all
     * the other threads and notifies the UI.
     */
    @Override
    public void shutdown() {
        synchronized(this) {
            // Inform the user that we're shutting down the
            // simulation.
            mView.get().shutdownOccurred(mBeingsTasks.size());

            // Cancel all the BeingTasks.
            for (BeingAsyncTask bat : mBeingsTasks) 
                // Cancel the task.
                bat.cancel(true);
        }
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
        // Initialize the Palantiri.
        getModel().makePalantiri(Options.instance().numberOfPalantiri());

        // Initialize the count of the number of threads Beings use to
        // gaze.
        mGazingTasks = new AtomicLong(0);

        // Show the Beings on the UI.
        mView.get().showBeings();

        // Show the palantiri on the UI.
        mView.get().showPalantiri();

        // Spawn a thread that waits for all the Being threads to
        // finish.
        joinBeingsTasks();

        // Create and execute an AsyncBeingTask for each Being.
        createAndExecuteBeingsTasks(Options.instance().numberOfBeings());
    }

    /**
     * Spawn a thread to wait for all the Being threads to finish.
     */
    private void joinBeingsTasks() {
        // First, initialize mExitBarrier that's used as an exit
        // barrier to ensure the waiter thread doesn't finish until
        // all the BeingTasks finish.
        mExitBarrier =
            new CountDownLatch(Options.instance().numberOfBeings());

        // Create/start a waiter thread that uses mExitBarrier to wait
        // for all the BeingTasks to finish.  After they are all
        // finished then tell the UI thread this simulation is done.
        new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // Wait for all BeingTasks to stop gazing.
                        mExitBarrier.await();
                    } catch (Exception e) {
                        Log.d(TAG,
                              "joinBeingTasks() received exception");
                        // If we get interrupted while waiting, stop
                        // everything.
                        shutdown();
                    } finally {
                        // Tell the UI thread this simulation is done.
                        mView.get().done();
                    }
                }}).start();
    }

    /**
     * Create a List of Threads that will be used to represent the
     * Beings in this simulation.  Each Thread is passed a
     * BeingRunnable parameter that takes the index of the Being in
     * the list as a parameter.
     * 
     * @param beingCount
     *            Number of Being Threads to create.
     */
    private void createAndExecuteBeingsTasks(int beingCount) {
        // First, create a new BeingsTasks ArrayList, iterate through
        // all the Beings, create a new BeingAsyncTask that performs
        // the Being logic, and add the BeingAsyncTask to the List.
        // Next, create a ThreadPoolExecutor that contains (1) a
        // fixed-size pool of BeingThreads corresponding to the number
        // of Beings, (2) a LinkedBlockingQueue, and (3) the
        // ThreadFactory instance.  Finally, iterate through all the
        // BeingTasks and execute them on the threadPoolExecutor.
        // TODO - You fill in here.
    }

    /**
     * Return the Activity context.
     */
    @Override
    public Context getActivityContext() {
        return mView.get().getActivityContext();
    }
    
    /**
     * Return the Application context.
     */
    @Override
    public Context getApplicationContext() {
        return mView.get().getApplicationContext();
    }
}
