package vandy.mooc.model;

import java.lang.ref.WeakReference;

import vandy.mooc.MVP;
import vandy.mooc.model.datamodel.ReplyMessage;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * This class defines the root of a hierachy for downloading images
 * via Bound or Started Services and plays the role of the
 * "Implementor" in the Bridge pattern.  It extends Handler so that
 * its handleMessage() method can be dispatched when
 * DownloadImage*Service has downloaded the image.
 */
public abstract class ImageModelImpl
       extends Handler {
    /**
     * Debugging tag used by the Android logger.
     */
    protected final static String TAG = 
        ImageModelImpl.class.getSimpleName();

    /**
     * Image-related operations.
     */
    public static enum OperationType {
        /**
         * Download an image.
         */
       DOWNLOAD_IMAGE
    }

    /**
     * A WeakReference used to access methods in the Presenter layer.
     * The WeakReference enables garbage collection.
     */
    protected WeakReference<MVP.RequiredPresenterOps> mImagePresenter;

    /**
     * Hook method called when a new ImageModel instance is created.
     * One time initialization code goes here, e.g., storing a
     * WeakReference to the Presenter and initializing the Bound
     * Service.
     * 
     * @param presenter
     *            A reference to the Presenter layer.
     */
    public void onCreate(MVP.RequiredPresenterOps presenter) {
        // Set the WeakReference.
        mImagePresenter =
            new WeakReference<>(presenter);

        // Bind to the Service.
        bindService();
    }

    /**
     * Hook method called to shutdown the Model layer.
     *
     * @param isChangeConfigurations
     *        True if a runtime configuration triggered the onDestroy() call.
     */
    public void onDestroy(boolean isChangingConfigurations) {
        if (isChangingConfigurations)
            Log.d(TAG,
                  "just a configuration change - unbindService() not called");
        else
            // Unbind from the Services only if onDestroy() is not
            // triggered by a runtime configuration change.
            unbindService();
    }

    /**
     * Start a download.  Plays the fole of the "Primitive Operation"
     * (a.k.a., "Abstract Hook Method") in the Template Method
     * pattern.
     *
     * @param url
     *        URL of the image to download.
     * @param directoryPathname
     *        Uri of the directory to store the downloaded image.
     */
    public abstract void startDownload(Uri url,
                                       Uri directoryPathname);

    /**
     * Initiate the protocol for binding the Services.
     */
    protected void bindService() {
        // No-op by default.
    }

    /**
     * Initiate the protocol for unbinding the Services.
     */
    protected void unbindService() {
        // No-op by default.
    }

    /**
     * Hook method dispatched in response to receiving the path to the
     * downloaded image file from a DownloadImages*Service.
     */
    @Override
    public void handleMessage(Message message) {
        Log.d(TAG,
              "handleMessage() called back");

        // Convert the Message into a ReplyMessage.
        final ReplyMessage replyMessage =
            ReplyMessage.makeReplyMessage(message);

        // Handle the results from the Service.
        mImagePresenter.get().onDownloadComplete
            (replyMessage);
    }
}
