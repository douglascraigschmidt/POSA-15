package vandy.mooc.presenter;

import java.lang.ref.WeakReference;
import java.util.List;

import vandy.mooc.common.ContextView;
import vandy.mooc.common.GenericAsyncTask;
import vandy.mooc.common.GenericAsyncTaskOps;
import vandy.mooc.common.GenericServiceConnection;
import vandy.mooc.model.aidl.AcronymCall;
import vandy.mooc.model.aidl.AcronymExpansion;
import vandy.mooc.model.aidl.AcronymRequest;
import vandy.mooc.model.aidl.AcronymResults;
import vandy.mooc.model.services.AcronymServiceAsync;
import vandy.mooc.model.services.AcronymServiceSync;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;

/**
 * This class implements all the acronym-related operations defined in
 * the AcronymOps interface.
 */
public class AcronymOps
    implements GenericAsyncTaskOps<String, Void, List<AcronymExpansion>> {
    /**
     * Debugging tag used by the Android logger.
     */
    protected final static String TAG =
        AcronymOps.class.getSimpleName();

    /**
     * This interface defines the minimum interface needed by the
     * AcronymOps class in the "Presenter" layer to interact with the
     * AcronymActivity in the "View" layer.
     */
    public interface View extends ContextView {
        /**
         * Start a new Activity that displays the Acronym Expansions
         * to the user.
         * 
         * @param results
         *            List of AcronymExpansions to display.
         */
        void displayResults(List<AcronymExpansion> results,
                            String errorMessage);
    }

    /**
     * Used to enable garbage collection.
     */
    private WeakReference<AcronymOps.View> mAcronymView;
    	
    /**
     * This GenericServiceConnection is used to receive results after
     * binding to the AcronymServiceSync Service using bindService().
     */
    private GenericServiceConnection<AcronymCall> mServiceConnectionSync;

    /**
     * This GenericServiceConnection is used to receive results after
     * binding to the AcronymServiceAsync Service using bindService().
     */
    private GenericServiceConnection<AcronymRequest> mServiceConnectionAsync;

    /**
     * The GenericAsyncTask used to expand an acronym in a background
     * thread via the Acronym web service.
     */
    private GenericAsyncTask<String, 
        Void,
        List<AcronymExpansion>,
        AcronymOps> mAsyncTask;

    /**
     * Keeps track of whether a call is already in progress and
     * ignores subsequent calls until the first call is done.
     */
    private boolean mCallInProgress;

    /**
     * Store Acronym for error reporting purposes.
     */
    private String mAcronym;

    /**
     * This Handler is used to post Runnables to the UI from the
     * mWeatherResults callback methods to avoid a dependency on the
     * Activity, which may be destroyed in the UI Thread during a
     * runtime configuration change.
     */
    private final Handler mDisplayHandler = new Handler();

    /**
     * The implementation of the AcronymResults AIDL Interface, which
     * will be passed to the Acronym Web service using the
     * AcronymRequest.expandAcronym() method.
     * 
     * This implementation of AcronymResults.Stub plays the role of
     * Invoker in the Broker Pattern since it dispatches the upcall to
     * sendResults().
     */
    private final AcronymResults.Stub mAcronymResults =
        new AcronymResults.Stub() {
            /**
             * This method is invoked by AcronymServiceAsync to return
             * the results back to the AcronymExpansionActivity.
             */
            @Override
            public void sendResults(final List<AcronymExpansion> acronymExpansions)
                throws RemoteException {
                // Since the Android Binder framework dispatches this
                // method in a background Thread we need to explicitly
                // post a runnable containing the results to the UI
                // Thread, where it's displayed.  We use the
                // mDisplayHandler to avoid a dependency on the
                // Activity, which may be destroyed in the UI Thread
                // during a runtime configuration change.
                mDisplayHandler.post(new Runnable() {
                        public void run() {
                            displayResults(acronymExpansions,
                                           null);
                        }
                    });
            }

            /**
             * This method is invoked by AcronymServiceAsync to return
             * error results back to the AcronymExpansionActivity.
             */
            @Override
            public void sendError(final String reason)
                throws RemoteException {
                // Since the Android Binder framework dispatches this
                // method in a background Thread we need to explicitly
                // post a runnable containing the results to the UI
                // Thread, where it's displayed.  We use the
                // mDisplayHandler to avoid a dependency on the
                // Activity, which may be destroyed in the UI Thread
                // during a runtime configuration change.
                mDisplayHandler.post(new Runnable() {
                        public void run() {
                            displayResults(null,
                                           reason);
                        }
                    });
            }
	};

    /**
     * Constructor initializes the fields.
     */
    public AcronymOps(AcronymOps.View view) {
        // Initialize the WeakReference.
        mAcronymView = new WeakReference<>(view);

        // Initialize the GenericServiceConnection objects.
        mServiceConnectionSync = 
            new GenericServiceConnection<AcronymCall>(AcronymCall.class);

        mServiceConnectionAsync =
            new GenericServiceConnection<AcronymRequest>(AcronymRequest.class);
    }

    /**
     * Called after a runtime configuration change occurs.
     */
    public void onConfigurationChanged(Configuration newConfig) {
        // Checks the orientation of the screen.
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) 
            Log.d(TAG,
                  "Now running in landscape mode");
        else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
            Log.d(TAG,
                  "Now running in portrait mode");
    }

    /**
     * Initiate the service binding protocol.
     */
    public void bindService() {
        Log.d(TAG,
              "calling bindService()");

        // Launch the Acronym Bound Services if they aren't already
        // running via a call to bindService(), which binds this
        // activity to the AcronymService* if they aren't already
        // bound.
        if (mServiceConnectionSync.getInterface() == null) 
            mAcronymView.get().getActivityContext().bindService
                (AcronymServiceSync.makeIntent(mAcronymView.get().getActivityContext()),
                 mServiceConnectionSync,
                 Context.BIND_AUTO_CREATE);

        if (mServiceConnectionAsync.getInterface() == null) 
            mAcronymView.get().getActivityContext().bindService
                (AcronymServiceAsync.makeIntent(mAcronymView.get().getActivityContext()),
                 mServiceConnectionAsync,
                 Context.BIND_AUTO_CREATE);
    }

    /**
     * Initiate the service unbinding protocol.
     */
    public void unbindService() {
        Log.d(TAG,
              "calling unbindService()");

        // Unbind the Async Service if it is connected.
        if (mServiceConnectionAsync.getInterface() != null)
            mAcronymView.get().getActivityContext().unbindService
                (mServiceConnectionAsync);

        // Unbind the Sync Service if it is connected.
        if (mServiceConnectionSync.getInterface() != null)
            mAcronymView.get().getActivityContext().unbindService
                (mServiceConnectionSync);
    }

    /**
     * Initiate the synchronous acronym lookup when the user presses
     * the "Lookup Acronym Async" button.
     *
     * @return false if a call is already in progress, else true.
     */
    public boolean expandAcronymAsync(String acronym) {
        if (mCallInProgress)
            return false;
        else {
            // Don't allow concurrent calls to get the weather.
            mCallInProgress = true;

            // Store this for error reporting purposes.
            mAcronym = acronym;

            // Get a reference to the AcronymRequest interface.
            final AcronymRequest acronymRequest = 
                mServiceConnectionAsync.getInterface();

            if (acronymRequest != null) {
                try {
                    // Invoke a one-way AIDL call, which doesn't block
                    // the client.  Results are returned via the
                    // sendResults() method of the mAcronymResults
                    // callback object, which runs in a Thread from
                    // the Thread pool managed by the Binder
                    // framework.
                    acronymRequest.expandAcronym(acronym,
                                                 mAcronymResults);
                } catch (RemoteException e) {
                    Log.e(TAG, "RemoteException:" + e.getMessage());
                }
            } else 
                Log.d(TAG, "acronymRequest was null.");
            return true;
        }
    }

    /**
     * Initiate the synchronous acronym lookup when the user presses
     * the "Lookup Acronym Sync" button.  It uses an AsyncTask.
     *
     * @return false if a call is already in progress, else true.
     */
    public boolean expandAcronymSync(String acronym) {
        if (mCallInProgress)
            return false;
        else {
            // Don't allow concurrent calls to get the weather.
            mCallInProgress = true;
            
            // Store this for error reporting purposes.
            mAcronym = acronym;

            // Execute the AsyncTask to expand the acronym without
            // blocking the caller.
            mAsyncTask = new GenericAsyncTask<>(this);
            mAsyncTask.execute(mAcronym);
            return true;
        }
    }

    /**
     * Retrieve the expanded acronym results via a synchronous two-way
     * method call, which runs in a background thread to avoid
     * blocking the UI thread.
     */
    @Override
    public List<AcronymExpansion> doInBackground(String... acronyms) {
        try {
            final AcronymCall acronymCall = 
                mServiceConnectionSync.getInterface();

            if (acronymCall != null) {
                mAcronym = acronyms[0];
                return acronymCall.expandAcronym(mAcronym);
            } else 
                Log.d(TAG, "mAcronymCall was null.");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Display the results in the UI Thread.
     */
    @Override
    public void onPostExecute(List<AcronymExpansion> acronymExpansions) {
        displayResults(acronymExpansions,
                       "no expansions for "
                       + mAcronym
                       + " found");
    }

    /**
     * Call back to the View layer to display the results.
     */
    private void displayResults(List<AcronymExpansion> results,
                                String failureReason) {
        // Display the results.
        mAcronymView.get().displayResults(results,
                                          failureReason);

        // Call is no longer in progress.
        mCallInProgress = false;
    }
}
