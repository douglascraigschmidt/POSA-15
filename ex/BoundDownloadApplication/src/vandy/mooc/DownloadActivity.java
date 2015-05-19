package vandy.mooc;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

/**
 * This is the main Activity that the program uses to start the
 * ThreadedDownloads application.  It allows the user to input the URL
 * of an image and download that image using one of two different
 * Android Bound Service implementations: synchronous and
 * asynchronous.  The Activity starts the Service using bindService().
 * After the Service is started, its onBind() hook method returns an
 * implementation of an AIDL interface to the Activity by
 * asynchronously calling the onServiceConnected() hook method in the
 * Activity.  The AIDL interface object that's returned can then be
 * used to interact with the Service either synchronously or
 * asynchronously, depending on the type of AIDL interface requested.
 * 
 * Starting Bound Services to run synchronously in background Threads
 * from the asynchronous UI Thread is an example of the
 * Half-Sync/Half-Async Pattern.  Starting Bound Services using
 * Intents is an example of the Activator and Command Processor
 * patterns.  The DownloadActivity plays the role of the Creator and
 * creates a Command in the form of an Intent.  The Intent is received
 * by the Service process, which plays the role of the Executor.
 * 
 * The use of AIDL interfaces to pass information between two
 * different processes is an example of the Broker Pattern, in which
 * all communication-related functionality is encapsulated in the AIDL
 * interface and the underlying Android Binder framework, shielding
 * applications from tedious and error-prone aspects of inter-process
 * communication.
 */
public class DownloadActivity extends DownloadActivityBase {
    /**
     * The AIDL Interface that's used to make twoway calls to the
     * DownloadServiceSync Service.  This object plays the role of
     * Requestor in the Broker Pattern.  If it's null then there's no
     * connection to the Service.
     */
    DownloadCall mDownloadCall;
     
    /**
     * The AIDL Interface that we will use to make oneway calls to the
     * DownloadServiceAsync Service.  This plays the role of Requestor
     * in the Broker Pattern.  If it's null then there's no connection
     * to the Service.
     */
    DownloadRequest mDownloadRequest;
     
    /** 
     * This ServiceConnection is used to receive results after binding
     * to the DownloadServiceSync Service using bindService().
     */
    ServiceConnection mServiceConnectionSync = new ServiceConnection() {
            /**
             * Cast the returned IBinder object to the DownloadCall
             * AIDL Interface and store it for later use in
             * mDownloadCall.
             */
            @Override
            public void onServiceConnected(ComponentName name,
                                           IBinder service) {
            	Log.d(TAG, "ComponentName: " + name);
                // Call the generated stub method to convert the
                // service parameter into an interface that can be
                // used to make RPC calls to the Service.
                mDownloadCall =
                    DownloadCall.Stub.asInterface(service);
            }

            /**
             * Called if the remote service crashes and is no longer
             * available.  The ServiceConnection will remain bound,
             * but the service will not respond to any requests.
             */
            @Override
            public void onServiceDisconnected(ComponentName name) {
                mDownloadCall = null;
            }
        };
     
    /** 
     * This ServiceConnection is used to receive results after binding
     * to the DownloadServiceAsync Service using bindService().
     */
    ServiceConnection mServiceConnectionAsync = new ServiceConnection() {
            /**
             * Cast the returned IBinder object to the DownloadRequest
             * AIDL Interface and store it for later use in
             * mDownloadRequest.
             */
            @Override
		public void onServiceConnected(ComponentName name,
                                               IBinder service) {
                // Call the generated stub method to convert the
                // service parameter into an interface that can be
                // used to make RPC calls to the Service.
                mDownloadRequest =
                    DownloadRequest.Stub.asInterface(service);
            }

            /**
             * Called if the remote service crashes and is no longer
             * available.  The ServiceConnection will remain bound,
             * but the service will not respond to any requests.
             */
            @Override
		public void onServiceDisconnected(ComponentName name) {
                mDownloadRequest = null;
            }
        };
     
    /**
     * The implementation of the DownloadResults AIDL
     * Interface. Should be passed to the DownloadBoundServiceAsync
     * Service using the DownloadRequest.downloadImage() method.
     * 
     * This implementation of DownloadResults.Stub plays the role of
     * Invoker in the Broker Pattern.
     */
    DownloadResults.Stub mDownloadResults = new DownloadResults.Stub() {
            /**
             * Called when the DownloadServiceAsync finishes obtaining
             * the results from the GeoNames Web service.  Use the
             * provided String to display the results in a TextView.
             */
            @Override
            public void sendPath(final String imagePathname) throws RemoteException {
                // Create a new Runnable whose run() method displays
                // the bitmap image whose pathname is passed as a
                // parameter to sendPath().  Please use
                // displayBitmap() defined in DownloadBase.
                final Runnable displayRunnable = new Runnable() {
                        public void run() {
                            displayBitmap(imagePathname);
                        }
                    };

                runOnUiThread(displayRunnable);
            }
        };
     
    /**
     * This method is called when a user presses a button(see
     * res/layout/activity_download.xml)
     */
    public void runService(View view) {
        Uri uri = Uri.parse(getUrlString());

        hideKeyboard();

    	switch(view.getId()) {
        case R.id.bound_sync_button:
            if (mDownloadCall != null) {
                Log.d(TAG,
                      "Calling twoway DownloadServiceSync.downloadImage()");
                /** 
                 * Define an AsyncTask instance to avoid blocking the UI Thread. 
                 * */
		new AsyncTask<Uri, Void, String>() {
                    /**
                     * Runs in a background thread.
                     */
                    @Override
                    protected String doInBackground(Uri... params) {
                        try {
                            return mDownloadCall.downloadImage(params[0]);
                        } catch(RemoteException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    /**
                     * Runs in the UI Thread.
                     */
                    @Override
                    protected void onPostExecute(String result) {
                        if (result != null) 
                            displayBitmap(result);
                    }
                }.execute(uri);
            }
            break;

        case R.id.bound_async_button:
            if(mDownloadRequest != null) {
                try {
                    Log.d(TAG,
                          "Calling oneway DownloadServiceAsync.downloadImage()");

                    // Call downloadImage() on mDownloadRequest, passing in
                    // the appropriate Uri and Results.
                    mDownloadRequest.downloadImage(uri,
                                                   mDownloadResults);
                } catch(RemoteException e) {
                    e.printStackTrace();
                }
            }
            break;
        }
    }

    /**
     * Hook method called when the DownloadActivity becomes visible to
     * bind the Activity to the Services.
     */
    @Override
    public void onStart() {
    	super.onStart();
    	
    	// Bind this activity to the DownloadBoundService* Services if
    	// they aren't already bound Use mBoundSync/mBoundAsync
    	if(mDownloadCall == null) 
            bindService(DownloadBoundServiceSync.makeIntent(this), 
                        mServiceConnectionSync, 
                        BIND_AUTO_CREATE);
    	if(mDownloadRequest == null)
            bindService(DownloadBoundServiceAsync.makeIntent(this), 
                        mServiceConnectionAsync, 
                        BIND_AUTO_CREATE);
    }
    
    /**
     * Hook method called when the DownloadActivity becomes completely
     * hidden to unbind the Activity from the Services.
     */
    @Override
    public void onStop() {
    	super.onStop();
    	
    	// Unbind the Sync/Async Services if they are bound. Use
    	// mBoundSync/mBoundAsync
    	if(mDownloadCall != null) 
            unbindService(mServiceConnectionSync);
    	if(mDownloadRequest != null) 
            unbindService(mServiceConnectionAsync);
    }
    
    // Public accessor method for testing purposes
    public DownloadCall getDownloadCall() {
    	return mDownloadCall;
    }
    
    // Public accessor method for testing purposes
    public DownloadRequest getDownloadRequest() {
    	return mDownloadRequest;
    }
    
    // Public accessor method for testing purposes
    public DownloadResults getDownloadResults() {
    	return mDownloadResults;
    }
    
    // Public accessor method for testing purposes
    public boolean isBoundToSync() {
    	return mDownloadCall != null;
    }
    
    // Public accessor method for testing purposes
    public boolean isBoundToAsync() {
    	return mDownloadRequest != null;
    }     
}
