package vandy.mooc.model;

import java.lang.ref.WeakReference;
import java.util.List;

import vandy.mooc.MVP;
import vandy.mooc.common.GenericServiceConnection;
import vandy.mooc.model.aidl.AcronymCall;
import vandy.mooc.model.aidl.AcronymExpansion;
import vandy.mooc.model.aidl.AcronymRequest;
import vandy.mooc.model.aidl.AcronymResults;
import vandy.mooc.model.services.AcronymServiceAsync;
import vandy.mooc.model.services.AcronymServiceSync;
import android.content.Context;
import android.os.RemoteException;
import android.util.Log;

/**
 * This class plays the "Model" role in the Model-View-Presenter (MVP)
 * pattern by defining an interface for providing data that will be
 * acted upon by the "Presenter" and "View" layers in the MVP pattern.
 * It implements the MVP.ProvidedModelOps so it can be created/managed
 * by the GenericModel framework.
 */
public class AcronymModel 
       implements MVP.ProvidedModelOps {
    /**
     * Debugging tag used by the Android logger.
     */
    protected final static String TAG = 
        AcronymModel.class.getSimpleName();

    /**
     * A WeakReference used to access methods in the Presenter layer.
     * The WeakReference enables garbage collection.
     */
    private WeakReference<MVP.RequiredPresenterOps> mAcronymPresenter;

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
     * Hook method called when a new instance of AcronymModel is
     * created.  One time initialization code goes here, e.g., storing
     * a WeakReference to the Presenter and initializing the sync and
     * async Services.
     * 
     * @param presenter
     *            A reference to the Presenter layer.
     */
    public void onCreate(MVP.RequiredPresenterOps presenter) {
        // Set the WeakReference.
        mAcronymPresenter =
            new WeakReference<>(presenter);

        // Initialize the GenericServiceConnection objects.
        mServiceConnectionSync = 
            new GenericServiceConnection<AcronymCall>(AcronymCall.class);
        mServiceConnectionAsync =
            new GenericServiceConnection<AcronymRequest>(AcronymRequest.class);
            
        // Bind to the sync and async Services.
        bindServices();
    }

    /**
     * Hook method called to shutdown the Model layer.
     *
     * @param isChangeConfigurations
     *        True if a runtime configuration triggered the onDestroy() call.
     */
    @Override
    public void onDestroy(boolean isChangingConfigurations) {
        if (isChangingConfigurations)
            Log.d(TAG,
                  "just a configuration change - unbindService() not called");
        else
            // Unbind from the Services only if onDestroy() is not
            // triggered by a runtime configuration change.
            unbindServices();
    }

    /**
     * Initiate the protocol for binding the Services.
     */
    private void bindServices() {
        Log.d(TAG,
              "calling bindService()");

        // Launch the Acronym Bound Services if they aren't already
        // running via a call to bindService(), which binds this
        // activity to the AcronymService* if they aren't already
        // bound.
        if (mServiceConnectionSync.getInterface() == null) 
            mAcronymPresenter.get()
                             .getApplicationContext()
                             .bindService
                (AcronymServiceSync.makeIntent
                     (mAcronymPresenter.get()
                                       .getActivityContext()),
                 mServiceConnectionSync,
                 Context.BIND_AUTO_CREATE);

        if (mServiceConnectionAsync.getInterface() == null) 
            mAcronymPresenter.get()
                             .getApplicationContext()
                             .bindService
                (AcronymServiceAsync.makeIntent
                     (mAcronymPresenter.get().getActivityContext()),
                 mServiceConnectionAsync,
                 Context.BIND_AUTO_CREATE);
    }

    /**
     * Initiate the protocol for unbinding the Services.
     */
    private void unbindServices() {
        Log.d(TAG,
              "calling unbindService()");

        // Unbind the Async Service if it is connected.
        if (mServiceConnectionAsync.getInterface() != null)
            mAcronymPresenter.get()
                .getApplicationContext()
                .unbindService
                (mServiceConnectionAsync);

        // Unbind the Sync Service if it is connected.
        if (mServiceConnectionSync.getInterface() != null)
            mAcronymPresenter.get()
                .getApplicationContext()
                .unbindService
                (mServiceConnectionSync);
    }

    /**
     * Use a two-way synchronous AIDL call to expand the @a acronym
     * parameter.  Must be called in a background thread (e.g., via
     * AsyncTask).
     */
    @Override
    public List<AcronymExpansion> getAcronymExpansions(String acronym) {
        try {
            final AcronymCall acronymCall = 
                mServiceConnectionSync.getInterface();

            if (acronymCall != null) 
                // Invoke a two-way AIDL call, which blocks the
                // caller.
                return acronymCall.expandAcronym(acronym);
            else 
                Log.d(TAG, "mAcronymCall was null.");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Use a two-way asynchronous AIDL call to expand the @a acronym
     * parameter.  Need not be called in a background thread since the
     * caller isn't blocked.
     */
    public void getAcronymExpansions(String acronym,
                                     AcronymResults results) {
        // Get a reference to the AcronymRequest interface.
        final AcronymRequest acronymRequest = 
            mServiceConnectionAsync.getInterface();

        if (acronymRequest != null) {
            try {
                // Invoke a one-way AIDL call that doesn't block the
                // caller.  Results are returned via the sendResults()
                // or sendError() methods of the AsyncResultsImpl
                // callback object, which runs in a Thread from the
                // Thread pool managed by the Binder framework.
                acronymRequest.expandAcronym(acronym,
                                             new AsyncResultsImpl(results));
            } catch (RemoteException e) {
                Log.e(TAG,
                      "RemoteException:" 
                      + e.getMessage());
            }
        } else 
            Log.d(TAG,
                  "acronymRequest was null.");
    }

    /**
     * The implementation of the AcronymResults AIDL Interface, which
     * will be passed to the Acronym Web service using the
     * AcronymRequest.expandAcronym() method.  Instances of this class
     * play the role of Invoker in the Broker Pattern since it
     * dispatches the upcall to sendResults().
     */
    private static class AsyncResultsImpl
            extends AcronymResults.Stub {
        /**
         * A WeakReference to the AcronymResults object.
         */
        private WeakReference<AcronymResults> mAcronymResults;

        /**
         * Constructor initializes the field.
         */
        public AsyncResultsImpl(AcronymResults acronymResults) {
            mAcronymResults = new WeakReference<>(acronymResults);
        }

        /**
         * This method is invoked by AcronymServiceAsync to trigger
         * returning the results back to AcronymExpansionActivity.
         */
        @Override
        public void sendResults(final List<AcronymExpansion> acronymExpansions)
            throws RemoteException {
            mAcronymResults.get().sendResults(acronymExpansions);
        }

        /**
         * This method is invoked by AcronymServiceAsync to trigger
         * returning error results back to AcronymExpansionActivity.
         */
        @Override
        public void sendError(final String reason)
            throws RemoteException {
            mAcronymResults.get().sendError(reason);
        }
    }
}
