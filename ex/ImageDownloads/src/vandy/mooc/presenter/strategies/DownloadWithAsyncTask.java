package vandy.mooc.presenter.strategies;

import android.graphics.Bitmap;
import android.os.AsyncTask;

/**
 * Use the AsyncTask framework to download a bitmap image in a
 * background Thread and display it to the user in the UI Thread.
 * This class plays the role of the "Concrete Strategy" in the
 * Strategy pattern.
 */
public class DownloadWithAsyncTask 
       implements ImageStrategy {
    /**
     * AsyncTask that's used to download and display a bitmap image
     * requested by the user.
     */
    private AsyncTask<String, Void, Bitmap> mDownloader = null;

    /**
     * Create and execute an AsyncTask that downloads the image in a
     * Thread in the pool of Threads.
     */
    @Override
    public void downloadAndDisplay(final DownloadContext downloadContext) {
        // Create an AsyncTask to download an image in the background
        // and display it to the user in the UI Thread.
        mDownloader = 
            new AsyncTask<String, Void, Bitmap>() {
            /**
             * Called by the AsyncTask framework in the UI Thread to
             * perform initialization actions.
             */
            protected void onPreExecute() {
                // Show the toast before starting the download in a
                // background Thread.
                downloadContext.showToast("downloading via AsyncTask");
            }

            /**
             * Download a bitmap image in a background thread.
             * 
             * @param params
             *            The url of a bitmap image
             *
             @ @return The Bitmap representation of the downloaded image.
            */
            protected Bitmap doInBackground(String... urls) {
                // Download the image, which can block since we're in
                // a background thread.
                return downloadContext.downloadBitmap(urls[0]);
            }

            /**
             * Called after an operation executing in the background
             * completes to set the bitmap image to an image view and
             * dismiss the progress dialog.
             * 
             * @param image
             *            The bitmap image
             */
            protected void onPostExecute(Bitmap image) {
                // Display the downloaded image to the user.
                downloadContext.displayBitmap(image);
            }
        }.execute(downloadContext.getUrl());
    }

    /**
     * Cancel a download/display.
     */
    @Override
    public void cancel(DownloadContext downloadContext) {
        // Let the user know this download/display is being canceled.
        downloadContext.showToast("Canceling DownloadWithAsyncTask in progress");

        // Cancel the AsyncTask immediately.
        mDownloader.cancel(true);
    }
}
