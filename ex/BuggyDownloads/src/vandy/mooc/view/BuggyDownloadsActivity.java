package vandy.mooc.view;

import vandy.mooc.R;
import vandy.mooc.common.LifecycleLoggingActivity;
import vandy.mooc.common.Utils;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * This Activity allows a user to download a bitmap image from a
 * remote server using Java Thrads.  After the image is downloaded and
 * converted into a Bitmap it is displayed on the user's screen.
 * 
 * This implementation is buggy!
 */
public class BuggyDownloadsActivity
       extends LifecycleLoggingActivity {
    /**
     * Debug Tag for logging debug output to LogCat
     */
    private final static String TAG =
        BuggyDownloadsActivity.class.getSimpleName();

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
        // Initialize the superclass.
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
     * Called when a user clicks the "buggy1" button to
     * download/display an image.
     * 
     * @param view
     *            The "buggy1" button
     */
    public void buggy1(View view) {
        // Obtain the requested URL from the user input.
        final String url = getUrlString();

 
        Utils.hideKeyboard(this,
                           mUrlEditText.getWindowToken());

        // Inform the user that the download is starting.
        showDialog("downloading via buggy1() method");

        try {
            // Download the image.
            final Bitmap image = downloadBitmap(url);

            // Display the downloaded image to the user.
            displayBitmap(image);
        } catch (Exception e) {
            Utils.showToast(this,
                            "Exception " 
                            + e
                            + " caught in buggy1()");
        } finally {
            // Dismiss the progress dialog.
            dismissDialog();
        }
    }

    /**
     * Called when a user clicks the "buggy2" button to download an
     * image.
     * 
     * @param view
     *            The "buggy2" button
     */
    public void buggy2(View view) {
        // Obtain the requested URL from the user input.
        final String url = getUrlString();

        Utils.hideKeyboard(this,
                           mUrlEditText.getWindowToken());

        // Create and start a new Thread to download an image in the
        // background and then use Messages and MessageHandler to
        // cause it to be displayed in the UI Thread.
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Download the image.
                    final Bitmap image = downloadBitmap(url);

                    // Display the downloaded image to the user.
                    displayBitmap(image);
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Utils.showToast(BuggyDownloadsActivity.this,
                                        "Exception " 
                                        + e
                                        + " caught in buggy1()");
                        }
                    });
                } finally {
                    // Dismiss the progress dialog.
                    dismissDialog();
                }
            }}).start();
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
                    Utils.showToast(BuggyDownloadsActivity.this,
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
