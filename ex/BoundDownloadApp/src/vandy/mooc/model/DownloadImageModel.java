package vandy.mooc.model;

import java.lang.ref.WeakReference;

import vandy.mooc.MVP;
import vandy.mooc.model.aidl.DownloadCall;
import vandy.mooc.model.aidl.DownloadRequest;
import vandy.mooc.model.aidl.DownloadResults;
import vandy.mooc.model.services.DownloadBoundServiceAsync;
import vandy.mooc.model.services.DownloadBoundServiceSync;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

/**
 * This class plays the "Model" role in the Model-View-Presenter (MVP)
 * pattern by defining an interface for providing data that will be
 * acted upon by the "Presenter" and "View" layers in the MVP pattern.
 * It implements the MVP.ProvidedModelOps so it can be created/managed
 * by the GenericPresenter framework.
 */
public class DownloadImageModel
       implements MVP.ProvidedModelOps {
    /**
     * Debugging tag used by the Android logger.
     */
    protected final static String TAG = 
        DownloadImageModel.class.getSimpleName();

    /**
     * A WeakReference used to access methods in the Presenter layer.
     * The WeakReference enables garbage collection.
     */
    protected WeakReference<MVP.RequiredPresenterOps> mPresenter;

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
    ServiceConnection mServiceConnectionSync =
        new ServiceConnection() {
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
    ServiceConnection mServiceConnectionAsync =
        new ServiceConnection() {
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
    DownloadResults.Stub mDownloadResults =
        new DownloadResults.Stub() {
            /**
             * Called when the DownloadBoundServiceAsync finishes
             * downloading the requested image.
             */
            @Override
            public void sendPath(final Uri pathToImageFile)
                throws RemoteException {
                mPresenter.get().displayImage(pathToImageFile);
            }
        };

    /**
     * Hook method called when a new ImageModel instance is created.
     * Simply forward to the implementation.
     * 
     * @param presenter
     *            A reference to the Presenter layer.
     */
    @Override
    public void onCreate(MVP.RequiredPresenterOps presenter) {
        // Set the WeakReference.
        mPresenter = new WeakReference<>(presenter);

    	// Bind this activity to the DownloadBoundService* Services if
    	// they aren't already bound Use mBoundSync/mBoundAsync
    	if (mDownloadCall == null) {
            mPresenter.get()
                      .getApplicationContext()
                      .bindService(DownloadBoundServiceSync.makeIntent
                                       (mPresenter.get()
                                                  .getActivityContext()), 
                                   mServiceConnectionSync,
                                   Context.BIND_AUTO_CREATE);
            Log.d(TAG,
                  "Calling bindService() on DownloadBoundServiceSync");
        }
    	if (mDownloadRequest == null) {
            mPresenter.get()
                      .getApplicationContext()
                      .bindService(DownloadBoundServiceAsync.makeIntent
                                       (mPresenter.get()
                                                  .getActivityContext()), 
                                   mServiceConnectionAsync,
                                   Context.BIND_AUTO_CREATE);
            Log.d(TAG,
                  "Calling bindService() on DownloadBoundServiceAsync");
        }
    }

    /**
     * Hook method called to shutdown the Presenter layer.
     */
    @Override
    public void onDestroy(boolean isChangingConfigurations) {
        // Don't bother unbinding the service if we're simply changing
        // configurations.
        if (isChangingConfigurations)
            Log.d(TAG,
                  "Simply changing configurations, no need to destroy the Service");
        else {
            // Unbind the Sync/Async Services if they are bound. Use
            // mBoundSync/mBoundAsync
            if (mDownloadCall != null) 
                mPresenter.get()
                          .getApplicationContext()
                          .unbindService(mServiceConnectionSync);
            if (mDownloadRequest != null) 
                mPresenter.get()
                          .getApplicationContext()
                          .unbindService(mServiceConnectionAsync);
        }
    }

    /**
     * Initiate the asynchronous image download.
     */
    public void downloadImageAsync(Uri uri) {
        if (mDownloadRequest != null) {
            try {
                Log.d(TAG,
                      "Calling oneway DownloadServiceAsync.downloadImage()");

                // Call downloadImage() on mDownloadRequest, passing
                // in the appropriate Uri and Results.
                mDownloadRequest.downloadImage(uri,
                                               mDownloadResults);
            } catch(RemoteException e) {
                e.printStackTrace();
            }
        } else
            Log.d(TAG,
                  "mDownloadRequest is null");
    }

    /**
     * Initiate the synchronous image download.
     */
    public void downloadImageSync(Uri uri) {
        if (mDownloadCall != null) {
            Log.d(TAG,
                  "Calling twoway DownloadServiceSync.downloadImage()");
            /** 
             * Define an AsyncTask instance to avoid blocking the UI Thread. 
             * */
            new AsyncTask<Uri, Void, Uri>() {
                /**
                 * Runs in a background thread.
                 */
                @Override
                protected Uri doInBackground(Uri... params) {
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
                protected void onPostExecute(Uri result) {
                    mPresenter.get().displayImage(result);
                }
            }.execute(uri);
        } else
            Log.d(TAG,
                  "mDownloadCall is null");
        
    }
}     
