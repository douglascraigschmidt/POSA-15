package vandy.mooc.presenter;

import java.lang.ref.WeakReference;
import java.util.List;

import vandy.mooc.MVP;
import vandy.mooc.common.GenericAsyncTask;
import vandy.mooc.common.GenericAsyncTaskOps;
import vandy.mooc.model.AcronymModel;
import vandy.mooc.model.aidl.AcronymExpansion;
import vandy.mooc.model.aidl.AcronymResults;
import android.content.Context;
import android.content.res.Configuration;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

/**
 * This class plays the "Presenter" role in the Model-View-Presenter
 * (MVP) pattern by acting upon the Model and the View, i.e., it
 * retrieves data from the Model (e.g., AcronymModel) and formats it
 * for display in the View (e.g., DisplayExpansionActivity).  It
 * implements GenericAsyncTaskOps so its doInBackground() method runs
 * in a background task.  It implements MVP.ProvidedPresenterOps and
 * MVP.RequiredModelOps to decouple the MVP layers.  It implements
 * AcronymResults so it can be the target of asynchronous callback
 * methods from the Model layer.
 */
public class AcronymPresenter
    implements GenericAsyncTaskOps<String, Void, List<AcronymExpansion>>,
               MVP.ProvidedPresenterOps,
               MVP.RequiredPresenterOps,
               AcronymResults {
    /**
     * Debugging tag used by the Android logger.
     */
    protected final static String TAG =
        AcronymPresenter.class.getSimpleName();

    /**
     * Used to enable garbage collection.
     */
    private WeakReference<MVP.RequiredViewOps> mAcronymView;
    
    /**
     * Reference to the AcronymModel.
     */
    private AcronymModel mAcronymModel;
    
       /**
     * The GenericAsyncTask used to expand an acronym in a background
     * thread via the Acronym web service.
     */
    private GenericAsyncTask<String, 
        Void,
        List<AcronymExpansion>,
        AcronymPresenter> mAsyncTask;

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
     * Constructor initializes the fields.
     */
    public void onCreate(MVP.RequiredViewOps view) {
        // Initialize the WeakReference.
        mAcronymView = new WeakReference<>(view);

        // Create the AcronymModel.
        mAcronymModel = new AcronymModel();
        mAcronymModel.onCreate(this);
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
     * Hook method called to shutdown the Model layer.
     *
     * @param isChangeConfigurations
     *        True if a runtime configuration triggered the onDestroy() call.
     */
    @Override
    public void onDestroy(boolean isChangingConfigurations) {
        // Destroy the model.
        mAcronymModel.onDestroy(isChangingConfigurations);
    }

    /**
     * Initiate the synchronous acronym lookup when the user presses
     * the "Lookup Acronym Sync" button.  It uses an AsyncTask to
     * avoid blocking the UI thread.
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
        // Get the acronym expansions synchronously.
        return mAcronymModel.getAcronymExpansions(acronyms[0]);
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

            // Get the acronym expansions asynchronously.  The results
            // are returned via the sendResults() and sendError() hook
            // methods below.
            mAcronymModel.getAcronymExpansions(acronym,
                                               this);
            return true;
        }
    }

    /**
     * This hook method is called back by the Model layer and returns
     * AcronymExpansion results back to the View layer.
     */
    public void sendResults(final List<AcronymExpansion> acronymExpansions)
        throws RemoteException {
        displayResults(acronymExpansions,
                       (String) null);
    }

    /**
     * This hook method is called back by the Model layer and returns
     * error results back to the View layer.
     */
    public void sendError(final String reason)
        throws RemoteException {
        displayResults(null,
                       reason);
    }

    /**
     * Call back to the View layer to display the results.
     */
    @Override
    public void displayResults(List<AcronymExpansion> results,
                               String failureReason) {
        // Display the results.
        mAcronymView.get().displayResults(results,
                                          failureReason);

        // Call is no longer in progress.
        mCallInProgress = false;
    }

    /**
     * Return the Activity context.
     */
    @Override
    public Context getActivityContext() {
        return mAcronymView.get().getActivityContext();
    }
    
    /**
     * Return the Application context.
     */
    @Override
    public Context getApplicationContext() {
        return mAcronymView.get().getApplicationContext();
    }

    /**
     * A no-op needed to make the compiler happy since we implement
     * the AcronymResult interface.
     */
    @Override
    public IBinder asBinder() {
        return null;
    }
}
