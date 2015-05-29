package vandy.mooc.utils;

import java.lang.ref.WeakReference;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * This class inherits from Handler and uses its handleMessage() hook
 * method to forward relevant data from Messages sent from the
 * DownloadImageService back to the MainActivity.
 */
public class ServiceResultHandler extends Handler {
    /**
     * Debugging tag used by the Android logger.
     */
    private final String TAG = getClass().getSimpleName();
    
    /**
     * Used to enable garbage collection.
     */
    private WeakReference<ServiceResult> mResult;
    
    /**
     * Constructor stores @a serviceResult into a WeakReference.
     */
    public ServiceResultHandler(ServiceResult serviceResult) {
        mResult = new WeakReference<>(serviceResult);
    }
    
    /**
     * Called to reset ServiceResult callback instance (e.g.,
     * MainActivity) after a configuration change, which will have
     * caused the garbage collector to destroy the Service object
     * associated with the mResult WeakReference.
     */
    public void onConfigurationChange(ServiceResult serviceResult) {
        mResult = new WeakReference<>(serviceResult);
    }

    /**
     * This hook method is dispatched in response to receiving the
     * path to the image file from the DownloadImageService.
     */
    @Override
    public void handleMessage(Message message) {
        Log.d(TAG,
              "handleMessage() called back");

        // Convert the Message into a ReplyMessage.
        ReplyMessage replyMessage =
            ReplyMessage.makeReplyMessage(message);

        // Extract the necessary fields from the ReplyMessage.
        final int requestCode =
            replyMessage.getRequestCode();
        final int resultCode = replyMessage.getResultCode();
        final Bundle data = replyMessage.getData();

        if (mResult.get() == null) {
            // Warn programmer that mResult callback reference has
            // been lost without being restored after a configuration
            // change.
            Log.w(TAG, "Configuration change handling not implemented correctly;"
                    + " lost weak reference to ServiceResult callback)");
        } else {
            // Forward result to ServiceResult callback
            // implementation.
            mResult.get().onServiceResult(requestCode,
                                          resultCode,
                                          data);
        }
    }
}
