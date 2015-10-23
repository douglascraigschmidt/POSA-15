package vandy.mooc.presenter.strategies;

import java.lang.ref.WeakReference;

import vandy.mooc.common.Utils;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

/**
 * Use the Handlers and Messages defined by the HaMeR framework to
 * download a bitmap image in a background thread and then display it
 * in the UI thread.  This class plays the role of the "Concrete
 * Strategy" in the Strategy pattern.
 */
public class DownloadWithMessages 
       implements ImageStrategy {
    /**
     * Thread object that's used for the download.
     */
    private Thread mThread = null;

    /**
     * Types of Messages that can be passed from a background Thread
     * to the UI Thread to specify which processing to perform.
     */
    public static final int SHOW_TOAST = 1;
    public static final int DISPLAY_IMAGE = 2;

    /**
     * Creates and starts a Thread that downloads an image in the
     * background and then use Messages to display the image in the UI
     * Thread.
     */
    @Override
    public void downloadAndDisplay(final DownloadContext downloadContext) {
        // Handler whose handleMessage() hook method processes
        // Messages sent to it from a background Thread.
        final Handler messageHandler =
            new Handler() {
                /**
                 * Process the specified Messages passed to the
                 * Handler in the UI Thread.  These Messages instruct
                 * the Handler to start showing the progress dialog,
                 * dismiss it, or display the designated bitmap image
                 * via the ImageView.
                 */
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                    case SHOW_TOAST:
                        downloadContext.showToast("downloading via Handlers and Messages");
                        break;

                    case DISPLAY_IMAGE:
                        // Display the downloaded image to the user.
                        downloadContext.displayBitmap((Bitmap) msg.obj);
                        break;
                    }
                }
            };

        final Runnable downloadRunnable = new Runnable() {
            /**
             * Download a bitmap image in a background Thread by
             * sending Messages to the mHandler running in the UI
             * Thread.
             */
            @Override
            public void run() {
                // Factory creates a Message that instructs the
                // MessageHandler to post the toast to the user.
                Message msg =
                    messageHandler.obtainMessage(SHOW_TOAST);

                // Send the Message to show the toast.
                messageHandler.sendMessage(msg);

                // Download the image.
                final Bitmap image = 
                    downloadContext.downloadBitmap(downloadContext.getUrl());

                // Factory creates a Message that instructs the
                // MessageHandler to display the image to the user.
                msg =
                    messageHandler.obtainMessage(DISPLAY_IMAGE,
                                                 image);

                // Send the Message to instruct the UI Thread to
                // display the image.
                messageHandler.sendMessage(msg);
            }
        };

        // Create and Start a new Thread to perform the download and
        // display the results to the user.
        mThread = new Thread(downloadRunnable);
        mThread.start();
    }

    /**
     * Cancel the download/display.
     */
    @Override
    public void cancel(DownloadContext downloadContext) {
        // Let the user know this download/display is being canceled.
        downloadContext.showToast("Canceling DownloadWithMessages in progress");

        // Interrupt the Thread so it will stop the download/display.
        mThread.interrupt();
    }
}
