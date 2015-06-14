package vandy.mooc.operations;

import java.lang.ref.WeakReference;
import java.util.List;

import vandy.mooc.R;
import vandy.mooc.activities.MainActivity;
import vandy.mooc.aidl.AcronymCall;
import vandy.mooc.aidl.AcronymData;
import vandy.mooc.aidl.AcronymRequest;
import vandy.mooc.aidl.AcronymResults;
import vandy.mooc.services.AcronymServiceAsync;
import vandy.mooc.services.AcronymServiceSync;
import vandy.mooc.utils.AcronymDataArrayAdapter;
import vandy.mooc.utils.GenericServiceConnection;
import vandy.mooc.utils.Utils;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

/**
 * This class implements all the acronym-related operations defined in
 * the AcronymOps interface.
 */
public class AcronymOpsImpl implements AcronymOps {
    /**
     * Debugging tag used by the Android logger.
     */
    protected final String TAG = getClass().getSimpleName();

    /**
     * Used to enable garbage collection.
     */
    protected WeakReference<MainActivity> mActivity;
    	
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
     * List of results to display (if any).
     */
    protected List<AcronymData> mResults;

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
             * This method is invoked by the AcronymServiceAsync to
             * return the results back to the AcronymActivity.
             */
            @Override
            public void sendResults(final List<AcronymData> acronymDataList)
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
                            mResults = acronymDataList;
                            mActivity.get().displayResults
                                (acronymDataList,
                                 null);
                        }
                    });
            }

            /**
             * This method is invoked by the AcronymServiceAsync to
             * return error results back to the AcronymActivity.
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
                            mActivity.get().displayResults(null,
                                                           reason);
                        }
                    });
            }
	};

    /**
     * Constructor initializes the fields.
     */
    public AcronymOpsImpl(MainActivity activity) {
        // Initialize the WeakReference.
        mActivity = new WeakReference<>(activity);

        // Initialize the GenericServiceConnection objects.
        mServiceConnectionSync = 
            new GenericServiceConnection<AcronymCall>(AcronymCall.class);

        mServiceConnectionAsync =
            new GenericServiceConnection<AcronymRequest>(AcronymRequest.class);
    }

    /**
     * Called after a runtime configuration change occurs to finish
     * the initialization steps.
     */
    public void onConfigurationChange(MainActivity activity) {
        Log.d(TAG,
              "onConfigurationChange() called");

        // Reset the mActivity WeakReference.
        mActivity = new WeakReference<>(activity);

        updateResultsDisplay();
    }

    /**
     * Display results if any (due to runtime configuration change).
     */
    private void updateResultsDisplay() {
        if (mResults != null)
            mActivity.get().displayResults(mResults, 
                                           null);
    }

    /**
     * Initiate the service binding protocol.
     */
    @Override
    public void bindService() {
        Log.d(TAG,
              "calling bindService()");

        // Launch the Acronym Bound Services if they aren't already
        // running via a call to bindService(), which binds this
        // activity to the AcronymService* if they aren't already
        // bound.
        if (mServiceConnectionSync.getInterface() == null) 
            mActivity.get().getApplicationContext().bindService
                (AcronymServiceSync.makeIntent(mActivity.get()),
                 mServiceConnectionSync,
                 Context.BIND_AUTO_CREATE);

        if (mServiceConnectionAsync.getInterface() == null) 
            mActivity.get().getApplicationContext().bindService
                (AcronymServiceAsync.makeIntent(mActivity.get()),
                 mServiceConnectionAsync,
                 Context.BIND_AUTO_CREATE);
    }

    /**
     * Initiate the service unbinding protocol.
     */
    @Override
    public void unbindService() {
        if (mActivity.get().isChangingConfigurations()) 
            Log.d(TAG,
                  "just a configuration change - unbindService() not called");
        else {
            Log.d(TAG,
                  "calling unbindService()");

            // Unbind the Async Service if it is connected.
            if (mServiceConnectionAsync.getInterface() != null)
                mActivity.get().getApplicationContext().unbindService
                    (mServiceConnectionAsync);

            // Unbind the Sync Service if it is connected.
            if (mServiceConnectionSync.getInterface() != null)
                mActivity.get().getApplicationContext().unbindService
                    (mServiceConnectionSync);
        }
    }

    /*
     * Initiate the asynchronous acronym lookup when the user presses
     * the "Look Up Async" button.
     */
    public void expandAcronymAsync(String acronym) {
        final AcronymRequest acronymRequest = 
            mServiceConnectionAsync.getInterface();

        if (acronymRequest != null) {
            try {
                // Invoke a one-way AIDL call, which does not block
                // the client.  The results are returned via the
                // sendResults() method of the mAcronymResults
                // callback object, which runs in a Thread from the
                // Thread pool managed by the Binder framework.
                acronymRequest.expandAcronym(acronym,
                                             mAcronymResults);
            } catch (RemoteException e) {
                Log.e(TAG,
                      "RemoteException:" 
                      + e.getMessage());
            }
        } else {
            Log.d(TAG,
                  "acronymRequest was null.");
        }
    }

    /*
     * Initiate the synchronous acronym lookup when the user presses
     * the "Look Up Sync" button.
     */
    public void expandAcronymSync(String acronym) {
        final AcronymCall acronymCall = 
            mServiceConnectionSync.getInterface();

        if (acronymCall != null) {
            // Use an anonymous AsyncTask to download the Acronym data
            // in a separate thread and then display any results in
            // the UI thread.
            new AsyncTask<String, Void, List<AcronymData>> () {
                /**
                 * Acronym we're trying to expand.
                 */
                private String mAcronym;

                /**
                 * Retrieve the expanded acronym results via a
                 * synchronous two-way method call, which runs in a
                 * background thread to avoid blocking the UI thread.
                 */
                protected List<AcronymData> doInBackground(String... acronyms) {
                    try {
                        mAcronym = acronyms[0];
                        return acronymCall.expandAcronym(mAcronym);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                /**
                 * Display the results in the UI Thread.
                 */
                protected void onPostExecute(List<AcronymData> acronymDataList) {
                    mResults = acronymDataList;
                    mActivity.get().displayResults(acronymDataList,
                                                   "no expansions for "
                                                   + mAcronym
                                                   + " found");
                }
                // Execute the AsyncTask to expand the acronym without
                // blocking the caller.
            }.execute(acronym);
        } else {
            Log.d(TAG, "mAcronymCall was null.");
        }
    }
}
