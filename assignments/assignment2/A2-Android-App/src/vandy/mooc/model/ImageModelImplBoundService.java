package vandy.mooc.model;

import vandy.mooc.common.Utils;
import vandy.mooc.model.datamodel.RequestMessage;
import vandy.mooc.model.services.DownloadImagesBoundService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.IBinder;
import android.os.Messenger;
import android.util.Log;

/**
 * This class implements the image downloading operations using an
 * Android Bound Service.  It plays the role of the "Concrete
 * Implementor" in the Bridge pattern.
 */
public class ImageModelImplBoundService 
       extends ImageModelImpl {
    /**
     * Reference to the reply Messenger that's passed to the
     * DownloadImagesBoundService and used to return image results via
     * the Handler.
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
    private ServiceConnection mServiceConnection = 
        new ServiceConnection() {
            /**
             * Called by the Android Binder framework after the
             * DownloadImagesBoundService is connected to convey the
             * result returned from onBind().
             */
            public void onServiceConnected(ComponentName className,
                                           IBinder binder) {
                Log.d(TAG,
                      "onServiceConnected() " 
                      + className);

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
                Log.d(TAG,
                      "onServiceDisconnected() "
                      + className);

                // Reset the reference to the RequestMessenger to
                // null, thereby preventing send() calls until it's
                // reconnected.
                // TODO -- you fill in here.
            }
	};

    /**
     * Constructor initializes the Reply Messenger.
     */
    public ImageModelImplBoundService() {
        // Initialize the Reply Messenger.
        mReplyMessenger = 
            new Messenger(this);
    }        

    /**
     * Initiate the protocol for binding the Services.
     */
    @Override
    protected void bindService() {
        Log.d(TAG,
              "calling ImageModelImplBoundService.bindService()");

        if (mRequestMessengerRef == null) {
            // Create a new intent to the DownloadImagesBoundService
            // that can download an image from the URL given by the
            // user.
            // TODO - you fill in here.

            Log.d(TAG,
                  "calling Context.bindService()");

            // Bind to the Service associated with the Intent.
            // TODO -- you fill in here.
        }
    }

    /**
     * Initiate the protocol for unbinding the Services.
     */
    @Override
    protected void unbindService() {
        Log.d(TAG,
              "calling ImageModelImplBoundService.unbindService()");

        if (mRequestMessengerRef != null) {
            // Unbind from the Service.
            // TODO -- you fill in here.

            Log.d(TAG,
                  "calling Context.unbindService()");

            // Set this field to null to trigger a call to
            // bindService() next time bindService() is called.
            // TODO -- you fill in here.
        } 
    }

    /**
     * Start a download.  When the download finishes its results are
     * passed up to the Presentation layer via the
     * onDownloadComplete() method defined in RequiredPresenterOps.
     * This method plays the fole of the "Primitive Operation"
     * (a.k.a., "Abstract Hook Method") in the Template Method
     * pattern.
     *
     * @param url
     *        URL of the image to download.
     * @param directoryPathname
     *        Uri of the directory to store the downloaded image.
     */
    @Override
    public void startDownload(Uri url,
                              Uri directoryPathname) {
        if (mRequestMessengerRef == null) 
            Utils.showToast(mImagePresenter.get().getActivityContext(),
                            "Not bound to the service");
        else {
            try {
                // Create a RequestMessage that indicates the
                // DownloadImagesBoundService should send the reply
                // back to ReplyHandler encapsulated by the Messenger.
                final RequestMessage requestMessage =
                    RequestMessage.makeRequestMessage
                    (OperationType.DOWNLOAD_IMAGE.ordinal(),
                     url,
                     directoryPathname,
                     mReplyMessenger);

                Log.d(TAG,
                      "sending a request message to DownloadImagesBoundService for "
                      + url.toString());

                // Send the request Message to the
                // DownloadImagesBoundService.
                // TODO -- you fill in here.
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
