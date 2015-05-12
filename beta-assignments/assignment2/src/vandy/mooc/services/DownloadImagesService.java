package vandy.mooc.services;

import vandy.mooc.utils.ReplyMessage;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

/**
 * A bound Service that concurrently downloads an image requested via
 * a Message passed to a Messenger, stores the image in a file on the
 * local device, and returns the URI to the downloaded image file back
 * to the Activity via the Messenger passed with the original Message.
 */
public class DownloadImagesService extends LifecycleLoggingService {
    /**
     * Debugging tag used by the Android logger.
     */
    private final String TAG = getClass().getSimpleName();

    /**
     * A RequestHandler that handles request Messages send from the
     * Activity.
     */
    private RequestHandler mRequestHandler = null;

    /**
     * A Messenger that encapsulates the RequestHandler used to handle
     * request Messages sent from the Activity.
     */
    private Messenger mRequestMessenger = null;

    /**
     * Factory method that returns an explicit Intent for downloading
     * an image.
     */
    public static Intent makeIntent(Context context) {
        // Create an intent that will download the image from the web.
    	// TODO -- you fill in here, replacing null with the proper
    	// code.
        return null;
    }

    /**
     * Hook method called when the Service is created.
     */
    @Override
    public void onCreate() {
        // Create a RequestHandler used to handle request Messages
        // sent from an Activity.
    	// TODO -- you fill in here.

        // Create a Messenger that encapsulates the RequestHandler.
    	// TODO -- you fill in here.
    }

    /**
     * Factory method that returns the underlying IBinder associated
     * with the request Messenger.
     */
    @Override
    public IBinder onBind(Intent intent) {
        super.onBind(intent);
        return mRequestMessenger.getBinder();
    }

    /**
     * Hook method called when the last client unbinds from the
     * Service.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();

        // Shutdown the RequestHandler.
        mRequestHandler.shutdown();
    }
}
