package vandy.mooc.view;

import java.lang.ref.WeakReference;

import android.graphics.Bitmap;
import android.os.AsyncTask;

/**
 * This class downloads a bitmap image in the background
 *        using AsyncTask.
 */
public class DownloadWithAsyncTask 
       extends AsyncTask<String, Integer, Bitmap> {
    /**
     * WeakReference enable garbage collection of Activity.
     */
    WeakReference<ImageDownloadsActivity> mActivity;

    /**
     * Constructor initializes the field.
     */
    DownloadWithAsyncTask(ImageDownloadsActivity activity) {
        mActivity = 
            new WeakReference<>(activity);
    }

    /**
     * Called by the AsyncTask framework in the UI Thread to perform
     * initialization actions.
     */
    protected void onPreExecute() {
        // Show the progress dialog before starting the download
        // in a Background Thread.
        mActivity.get().showDialog("downloading via AsyncTask");
    }

    /**
     * Downloads bitmap in an AsyncTask background thread.
     * 
     * @param params
     *            The url of a bitmap image
     */
    protected Bitmap doInBackground(String... urls) {
        return mActivity.get().downloadBitmap(urls[0]);
    }

    /**
     * Called after an operation executing in the background is
     * completed. It sets the bitmap image to an image view and
     * dismisses the progress dialog.
     * 
     * @param image
     *            The bitmap image
     */
    protected void onPostExecute(Bitmap image) {
        // Dismiss the progress dialog.
        mActivity.get().dismissDialog();

        // Display the downloaded image to the user.
        mActivity.get().displayBitmap(image);
    }
}

