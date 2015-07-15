package vandy.mooc.model.services;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import vandy.mooc.common.Utils;
import vandy.mooc.model.aidl.AcronymExpansion;
import vandy.mooc.model.aidl.AcronymRequest;
import vandy.mooc.model.aidl.AcronymResults;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

/**
 * @class AcronymServiceAsync
 * 
 * @brief This class uses asynchronous AIDL interactions to expand
 *        acronyms via an Acronym Web service.  The AcronymModel that
 *        binds to this Service will receive an IBinder that's an
 *        instance of AcronymRequest, which extends IBinder.  The
 *        AcronymModel can then interact with this Service by making
 *        one-way method calls on the AcronymRequest object asking
 *        this Service to lookup the Acronym's meaning, passing in an
 *        AcronymResults object and the Acronym string.  After the
 *        lookup is finished, this Service sends the Acronym results
 *        back to the AcronymModel by calling sendResults() on the
 *        AcronymResults object.
 * 
 *        AIDL is an example of the Broker Pattern, in which all
 *        interprocess communication details are hidden behind the
 *        AIDL interfaces.
 */
public class AcronymServiceAsync extends AcronymServiceBase {
    /**
     * Reference to the ExecutorService that manages a pool of
     * threads.  We need this feature since Android's Binder framework
     * executes oneway methods from a client in a single thread rather
     * than a pool of thread.
     */
    private ExecutorService mExecutorService;

    /**
     * Factory method that makes an Intent used to start the
     * AcronymServiceAsync when passed to bindService().
     * 
     * @param context
     *            The context of the calling component.
     */
    public static Intent makeIntent(Context context) {
        return new Intent(context,
                          AcronymServiceAsync.class);
    }

    /**
     * Called when a client (e.g., AcronymModel) calls bindService()
     * with the proper Intent.  Returns the implementation of
     * AcronymRequest, which is implicitly cast as an IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAcronymRequestImpl;
    }

    /**
     * Hook method called when the Service is created.
     */
    @Override
    public void onCreate() {
        // Call up to the super onCreate() method to perform its
        // initialization operations.
        super.onCreate();

        // Create an ExecutorService that manages a pool of threads.
        mExecutorService = Executors.newCachedThreadPool();
    }

    /**
     * Hook method called when the last client unbinds from the
     * Service.
     */
    @Override
    public void onDestroy() {
        // Immediately shutdown the ExecutorService.
        mExecutorService.shutdownNow(); 

        // Call up to the super onCreate() method to perform its
        // destruction operations.
        super.onDestroy();
    }

    /**
     * The concrete implementation of the AIDL AcronymRequest
     * interface, which extends the Stub class that implements
     * AcronymRequest, thereby allowing Android to handle calls across
     * process boundaries.  This method runs in a separate Thread as
     * part of the Android Binder framework.
     * 
     * This implementation plays the role of Invoker in the Broker
     * Pattern.
     */
    private final AcronymRequest.Stub mAcronymRequestImpl =
        new AcronymRequest.Stub() {
            /**
             * Implement the AIDL AcronymRequest expandAcronym()
             * method, which forwards to getAcronymResults() to obtain
             * the results and then sends these results back to the
             * AcronymModel via a callback.
             */
            @Override
            public void expandAcronym(final String acronym,
                                      final AcronymResults callback)
                throws RemoteException {
                final Runnable getCurrentAcronymRunnable = new Runnable() {
                    public void run() {
                        try {
                            // Call the Acronym Web service to get the
                            // list of possible expansions of the
                            // designated location.
                            final List<AcronymExpansion> acronymExpansions =
                                getAcronymExpansions(acronym);

                            if (acronymExpansions != null) {
                                Log.d(TAG, "" 
                                      + acronymExpansions.size() 
                                      + " result(s) for Acronym: "
                                      + acronym);
                                // Invoke a one-way callback to send
                                // list of Acronym expansions back to
                                // the client.
                                callback.sendResults(acronymExpansions);
                            } else {
                                Log.d(TAG, 
                                      "No expansion for \""
                                      + acronym
                                      + "\" found");

                                // Invoke a one-way callback to send
                                // an error message back to the
                                // client.es
                                callback.sendError("No expansion for \""
                                                   + acronym
                                                   + "\" found");
                            }
                        } catch (Exception e) {
                            Log.d(TAG,
                                  "getCurrentAcronym() "
                                  + e);
                        }
                    }

                };

                // Determine if we're on the UI thread or not.  
                if (Utils.runningOnUiThread())
                    // Execute getCurrentAcronymRunnable in a separate
                    // thread if this service has been configured to
                    // be collocated with an Activity.
                    mExecutorService.execute(getCurrentAcronymRunnable);
                else 
                    // Run the getCurrentAcronymRunnable in the pool
                    // thread if this service has been configured to
                    // run in its own process.
                    getCurrentAcronymRunnable.run();
            }
	};
}
