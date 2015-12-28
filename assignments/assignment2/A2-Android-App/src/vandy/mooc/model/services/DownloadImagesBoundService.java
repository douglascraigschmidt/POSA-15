package vandy.mooc.model.services;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import vandy.mooc.common.LifecycleLoggingService;
import vandy.mooc.model.datamodel.RequestMessage;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

/**
 * A Bound Service that concurrently downloads an image requested via
 * a Message passed to a Request Messenger, stores the image in a file
 * on the local device, and returns the URI to the downloaded image
 * file back to the Activity via the Reply Messenger passed with the
 * original Message.
 */
public class DownloadImagesBoundService 
       extends LifecycleLoggingService {
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
     * This class handles messages sent from an Activity in a pool of
     * threads managed by the Java ExecutorService.
     */
    private static class RequestHandler extends Handler {
	/**
	 * Debugging tag used by the Android logger.
	 */
	private final String TAG = getClass().getSimpleName();

	/**
	 * Store a WeakReference to the Service to enable garbage
	 * collection.
	 */
	WeakReference<DownloadImagesBoundService> mService;
    
	/**
	 * Reference to the ExecutorService that manages a pool of
	 * threads.
	 */
	private ExecutorService mExecutorService;

	/**
	 * Constructor initializes the WeakReference and ExecutorService.
	 */
	public RequestHandler(DownloadImagesBoundService service) {
	    // Store a WeakReference to the DownloadImageService.
	    mService = new WeakReference<>(service);

	    // Create an ExecutorService that manages a pool of threads.
	    mExecutorService = Executors.newCachedThreadPool();
	}

	/**
	 * Hook method called back when a request message arrives from
	 * an Activity.  The Message it receives contains the
	 * Messenger used to reply to the Activity and the URL of the
	 * image to download.  This image is stored in a local file on
	 * the local device and image file's URI is sent back to the
	 * MainActivity via the Messenger passed with the message.
	 */
	public void handleMessage(Message message) {
	    // Convert the Message into a RequestMessage.
	    final RequestMessage requestMessage =
		RequestMessage.makeRequestMessage(message);

	    // Get the reply Messenger.
	    // TODO -- you fill in here.

	    // Get the URL associated with the Message.
	    // TODO -- you fill in here.

	    // Get the directory pathname where the image will be
	    // stored.
	    // TODO -- you fill in here.

	    // Get the requestCode for the operation that was invoked
	    // by the Activity.
	    // TODO -- you fill in here.

	    // A Runnable that downloads the image, stores it in a
	    // file, and sends the path to the file back to the
	    // Activity.
	    final Runnable downloadImageAndReply = 
		new Runnable() {
		    /**
		     * This method runs in a background Thread.
		     */
		    @Override
                    public void run() {
	
			// Download and store the requested image.
			// TODO -- you fill in here.

			// Send the path to the image file, url, and
			// requestCode back to the Activity via the
			// replyMessenger.
			// TODO -- you fill in here.
		    }
		};

	    // Execute the downloadImageAndReply Runnable to download
	    // the image and reply.
	    // TODO -- you fill in here.
	}

	/**
	 * Send the @a pathToImageFile, @a url, and @a requestCode back to
	 * the Activity via the @a messenger.
	 */
	public void sendPath(Messenger messenger, 
			     Uri pathToImageFile,
			     Uri url,
			     int requestCode) {
	    // Call the makeReplyMessage() factory method to create
	    // Message.
	    // TODO -- you fill in here.
	    try {
		Log.d(TAG,
		      "sending "
		      + pathToImageFile
		      + " back to the MainActivity");

		// Send the replyMessage back to the Activity.
		// TODO -- you fill in here.
	    } catch (RemoteException e) {
		Log.e(getClass().getName(),
		      "Exception while sending reply message back to Activity.",
		      e);
	    }
	}

	/**
	 * Shutdown the ExecutorService immediately.
	 */
	public void shutdown() {
	    // Immediately shutdown the ExecutorService.
	    // TODO -- you fill in here.        
	}
    }

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
     * with the Request Messenger.
     */
    @Override
    public IBinder onBind(Intent intent) {
        super.onBind(intent);
    
        // Return the IBinder associated with the Request Messenger.
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
    	// TODO -- you fill in here.
    }
}
