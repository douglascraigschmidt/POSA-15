package vandy.mooc.view;

import vandy.mooc.R;
import vandy.mooc.common.LifecycleLoggingActivity;
import vandy.mooc.common.Utils;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * This Activity allows a user to download a bitmap image from a
 * remote server using the following concurrency strategies from the
 * Android HaMeR and AsyncTask frameworks:
 *
 * . Download with Runnables (HaMeR framework)
 * . Download with Messages (HaMeR framework)
 * . Download with AsyncTask (AsyncTask framework)
 *        
 * After the image is downloaded and converted into a Bitmap it is
 * displayed on the user's screen.
 * 
 * This implementation doesn't handle runtime configuration changes
 * robustly.  See the ImageDownloads example for a framework that
 * handles these changes via the Model-View-Presenter (MVP) pattern.
 */
public class ImageDownloadsActivity
       extends LifecycleLoggingActivity {
    /**
     * Debug Tag for logging debug output to LogCat
     */
    private final static String TAG =
        ImageDownloadsActivity.class.getSimpleName();

    /**
     * Default URL to download
     */
    private final static String mDefaultUrl =
        "http://www.dre.vanderbilt.edu/~schmidt/ka.png";

    /**
     * User's selection of URL to download
     */
    private EditText mUrlEditText;

    /**
     * Image that's been downloaded
     */
    private ImageView mImageView;

    /**
     * Display progress of download
     */
    private ProgressDialog mProgressDialog;

    /**
     * Method that initializes the Activity when it is first created.
     * 
     * @param savedInstanceState
     *            Activity's previously frozen state, if there was one.
     */
    public void onCreate(Bundle savedInstanceState) {
        // Initialize the super class.
        super.onCreate(savedInstanceState);

        // Sets the content view specified in the
        // image_downloads_activity.xml file.
        setContentView(R.layout.image_downloads_activity);

        // Caches references to the EditText and ImageView objects in
        // data members to optimize subsequent access.
        mUrlEditText = (EditText) findViewById(R.id.mUrlEditText);
        mImageView = (ImageView) findViewById(R.id.mImageView);
    }

    /**
     * Called when a user clicks a button to download/display an image
     * with Runnables.
     * 
     * @param view
     *            The "Run Runnable" button
     */
    public void runRunnable(View view) {
        // Obtain the requested URL from the user input.
        final String url = getUrlString();

        Utils.hideKeyboard(this,
                           mUrlEditText.getWindowToken());

        // Inform the user that the download is starting.
        showDialog("downloading via Handlers and Runnables");
        
        // Create and start a new Thread to download an image in the
        // background via a Runnable.  The downloaded image is then
        // diplayed in the UI Thread by posting another Runnable via
        // the Activity's runOnUiThread() method, which uses an
        // internal Handler.
        new Thread(new DownloadWithRunnables(this,
                                             url)).start();
    }

    /**
     * Called when a user clicks a button to download an image with
     * a Runnable and Messages.
     * 
     * @param view
     *            The "Run Messages" button
     */
    public void runMessages(View view) {
        // Obtain the requested URL from the user input.
        final String url = getUrlString();

        Utils.hideKeyboard(this,
                           mUrlEditText.getWindowToken());

        // Create and start a new Thread to download an image in the
        // background and then use Messages and MessageHandler to
        // cause it to be displayed in the UI Thread.
        new Thread(new DownloadWithMessages(this,
                                            url)).start();
    }

    /**
     * Called when a user clicks a button to download an image with
     * AsyncTask.
     * 
     * @param view
     *            The "Run Async" button
     */
    public void runAsyncTask(View view) {
        // The the URL for the image to download.
        final String url = getUrlString();

        Utils.hideKeyboard(this,
                           mUrlEditText.getWindowToken());

        // Execute the download using an AsyncTask.
        new DownloadWithAsyncTask(this).execute(url);
    }

    /**
     * Download a bitmap image from the URL provided by the user.
     * 
     * @param url
     *            The url where a bitmap image is located
     * @return the image bitmap or null if there was an error
     */
    public Bitmap downloadBitmap(String url) {
        // Use the default URL if the user doesn't supply one.
        final String finalUrl = url.equals("") ? mDefaultUrl : url;

        final Bitmap bitmap =
            Utils.downloadAndDecodeImage(finalUrl);
        if (bitmap == null)
            // Post error reports to the UI Thread.
            runOnUiThread(new Runnable() {
                public void run() {
                    // Use a Toast to inform user that something
                    // has gone wrong.
                    Utils.showToast(ImageDownloadsActivity.this,
                                    "Error downloading image,"
                                    + " please recheck URL "
                                    + finalUrl);
                }
            });
        
        return bitmap;
    }

    /**
     * Display a downloaded bitmap image if it's non-null; otherwise,
     * it reports an error via a Toast.
     * 
     * @param image
     *            The bitmap image
     */
    void displayBitmap(Bitmap image) {   
        if (mImageView == null)
            Utils.showToast(this,
                            "Problem with Application,"
                            + " please contact the Developer.");
        else if (image != null)
            mImageView.setImageBitmap(image);
        else
            Utils.showToast(this,
                            "image is corrupted,"
                            + " please check the requested URL.");
    }

    /**
     * Called when a user clicks a button to reset an image to
     * default.
     * 
     * @param view
     *            The "Reset Image" button
     */
    public void resetImage(View view) {
        mImageView.setImageResource(R.drawable.default_image);
    }

    /**
     * Read the URL EditText and return the String it contains.
     * 
     * @return String value in mUrlEditText
     */
    String getUrlString() {
        return mUrlEditText.getText().toString();
    }

    /**
     * Display the Dialog to the User.
     * 
     * @param message 
     *          The String to display what download method was used.
     */
    public void showDialog(String message) {
        mProgressDialog =
            ProgressDialog.show(this,
                                "Download",
                                message);
    }
    
    /**
     * Dismiss the Dialog.
     */
    public void dismissDialog() {
        if (mProgressDialog != null)
            mProgressDialog.dismiss();
    }
}
