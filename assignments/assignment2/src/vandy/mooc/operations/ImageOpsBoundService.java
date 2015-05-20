package vandy.mooc.operations;

import vandy.mooc.activities.MainActivity;
import vandy.mooc.services.DownloadImagesBoundService;
import vandy.mooc.utils.RequestMessage;
import vandy.mooc.utils.Utils;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.IBinder;
import android.os.Messenger;
import android.util.Log;

/**
 * This class implements all the image-related operations using an
 * Android Bound Service.  It plays the role of the "Concrete
 * Implementor" in the Bridge pattern and the "Concrete Class" in the
 * Template Method pattern.
 */
public class ImageOpsBoundService extends ImageOpsImpl {
    /**
     * Reference to the reply Messenger that's passed to the
     * DownloadImagesBoundService and used to return image results via
     * the ServiceResultHandler.
     */
    private Messenger mReplyMessenger = null;

    /**
     * Reference to the request Messenger that's implemented in the
     * DownloadImagesBoundService and used to send request messages to
     * the Service.
     */
    private Messenger mRequestMessengerRef = null;

    /** 
     * Used to receive a reference to the RequestMessenger after
     * binding to the DownloadImagesBoundService using bindService().
     */
    private ServiceConnection mServiceConnection = new ServiceConnection() {
            /**
             * Called by the Android Binder framework after the
             * DownloadImagesBoundService is connected to convey the
             * result returned from onBind().
             */
            public void onServiceConnected(ComponentName className,
                                           IBinder binder) {
                Log.d(TAG, "onServiceConnected() " + className);

                // Create a new Messenger that encapsulates the
                // returned IBinder object and store it for later use
                // in mRequestMessengerRef.
                // TODO -- you fill in here.
            }

            /**
             * Called if the Service crashes and is no longer
             * available.  The ServiceConnection will remain bound,
             * but the Service will not respond to any requests.
             */
            public void onServiceDisconnected(ComponentName className) {
                Log.d(TAG, "onServiceDisconnected ");
                // Reset the reference to the RequestMessenger to
                // null, thereby preventing send() calls until it's
                // reconnected.
                // TODO -- you fill in here.
            }
	};

    /**
     * Constructor initializes the Reply Messenger.
     */
    public ImageOpsBoundService(MainActivity activity) {
        super(activity);

        // Initialize the Reply Messenger.
        mReplyMessenger = 
            new Messenger(mServiceResultHandler);
    }        

    /**
     * Initiate the service binding protocol.
     */
    @Override
    public void bindService() {
        if (mRequestMessengerRef == null) {
            // Create a new intent to the DownloadImagesBoundService
            // that can download an image from the URL given by the
            // user.  
            // TODO - you fill in here.

            Log.d(TAG, "calling bindService()");

            // Bind to the Service associated with the Intent.
            // TODO -- you fill in here.
        }
    }

    /**
     * Initiate the service unbinding protocol.
     */
    @Override
    public void unbindService() {
        if (mRequestMessengerRef != null) {
            Log.d(TAG, "calling unbindService()");
            // Unbind from the Service.
            // TODO -- you fill in here.

            // Set this field to null to trigger a call to
            // bindService() next time bindService() is called.
            // TODO -- you fill in here.
        }
    }

    /**
     * Start a download.  Plays the fole of the "Primitive Operation"
     * (a.k.a., "Hook Method") in the Template Method pattern.
     */
    @Override
    protected void startDownload(Uri url) {
        if (mRequestMessengerRef == null) 
            Utils.showToast(mActivity.get(),
                            "not bound to the service");
        else {
            try {
                // Create a RequestMessage that indicates the
                // DownloadImagesBoundService should send the reply
                // back to ReplyHandler encapsulated by the Messenger.
                RequestMessage requestMessage =
                    RequestMessage.makeRequestMessage
                    (OperationType.DOWNLOAD_IMAGE.ordinal(),
                     url,
                     mDirectoryPathname,
                     mReplyMessenger);

                Log.d(TAG,
                      "sending a request message to DownloadImagesBoundService for "
                      + url.toString());

                // Send the request Message to the DownloadService.
                // TODO -- you fill in here.
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
