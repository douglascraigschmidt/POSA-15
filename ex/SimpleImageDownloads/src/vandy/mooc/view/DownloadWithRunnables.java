package vandy.mooc.view;

import java.lang.ref.WeakReference;

import android.graphics.Bitmap;

/**
 * This class downloads/displays a bitmap image using Runnables.
 */
public class DownloadWithRunnables
       implements Runnable {
    // The URL to download. 
    final private String mUrl;

    /**
     * WeakReference enable garbage collection of Activity.
     */
    final WeakReference<ImageDownloadsActivity> mActivity;

    /**
     * Class initializes the fields.
     * 
     * @param activity
     *            The enclosing Activity
     * @param url
     *            The bitmap image url
     */
    DownloadWithRunnables(ImageDownloadsActivity activity,
                          String url) {
        mUrl = url;
        mActivity = 
            new WeakReference<>(activity);
    }

    /**
     * Download a bitmap image in the background.  It also displays
     * the image and dismisses the progress dialog.
     */
    public void run() {
        // Download the bitmap image.
        final Bitmap image =
            mActivity.get().downloadBitmap(mUrl);

        // Display the image in the UI thread.
        mActivity.get().runOnUiThread(new Runnable() {
            public void run() {
                // Dismiss the progress dialog.
                mActivity.get().dismissDialog();
                   
                // Display the downloaded image to the user.
                mActivity.get().displayBitmap(image);
            }
        });
    }
}

