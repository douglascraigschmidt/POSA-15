package vandy.mooc.view;

import vandy.mooc.R;
import vandy.mooc.MVP;
import vandy.mooc.common.GenericActivity;
import vandy.mooc.common.Utils;
import vandy.mooc.presenter.DownloadImagePresenter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * This is the main Activity that the program uses to start the
 * DownloadImage app.  It plays the role of the "View" in the
 * Model-View-Presenter (MVP) pattern.  It extends GenericActivity
 * that provides a framework to automatically handle runtime
 * configuration changes of an WeatherPresenter object, which plays
 * the role of the "Presenter" in the MVP pattern.  The
 * MPV.RequiredViewOps and MVP.ProvidedPresenterOps interfaces are
 * used to minimize dependencies between the View and Presenter
 * layers.
 */
public class DownloadImageActivity
       extends GenericActivity<MVP.RequiredViewOps,
                               MVP.ProvidedPresenterOps,
                               DownloadImagePresenter>
       implements MVP.RequiredViewOps {
    /**
     * This is the reference to the text box that allows the user to
     * input a URL to an image for downloading.
     */
    private EditText mUrlEditText;

    /**
     * Default URL.
     */
    private String mDefaultUrl = 
        "http://www.dre.vanderbilt.edu/~schmidt/ka.png";

    /**
     * Image that's been downloaded
     */
    private ImageView mImageView;

    /**
     * This is called when the Activity is initially created. This is
     * where we setup the UI for the activity and initialize any
     * objects that need to exist while the activity exists.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Perform first part of initializing the super class.
        super.onCreate(savedInstanceState);
        
        // Use the Android framework to create a User Interface for
        // this activity.  The interface that should be created is
        // defined in download_image_activity.xml in the res/layout
        // folder.
        setContentView(R.layout.download_image_activity);
        
        // Once the UI is created, get a reference to the instantiated
        // EditText and ImageView objects by providing their ids to
        // the Android framework.
        mUrlEditText = (EditText) findViewById(R.id.url);
        mImageView = (ImageView) findViewById(R.id.imageView1);

        // Perform second part of initializing the super class,
        // passing in the DownloadImagePresenter class to
        // instantiate/manage and "this" to provide the
        // MVP.RequiredViewOps instance.
        super.onCreate(DownloadImagePresenter.class,
                       this);
    }

    /**
     * This method is called when a user presses a button (see
     * res/layout/download_image_activity.xml).
     */
    public void downloadImage(View view) {
        Uri uri = Uri.parse(getUrlString());

        Utils.hideKeyboard(this,
                           mUrlEditText.getWindowToken());

    	switch(view.getId()) {
        case R.id.bound_sync_button:
            if (getPresenter().downloadImageSync(uri) == false)
                // Show error message to user.
                Utils.showToast(this,
                                "Download already in progress");

            break;

        case R.id.bound_async_button:
            if (getPresenter().downloadImageAsync(uri) == false)
                // Show error message to user.
                Utils.showToast(this,
                                "Download already in progress");
            break;
        }
    }

    /**
     * Called when a user clicks a button to reset an image to
     * default.
     * 
     * @param view
     *            The "Reset Image" button
     */
    public void resetImage(View view) {
        getPresenter().resetImage();
    }

    /**
     * Display a downloaded bitmap image if it's non-null; otherwise,
     * it reports an error via a Toast.
     * 
     * @param image
     *            The bitmap image to display
     */
    public void displayImage(final Bitmap image) {
        if (Utils.runningOnUiThread()) {
            if (image == null)
                Utils.showToast(this,
                                "image is corrupted,"
                                + " please check the requested URL.");
            else 
                mImageView.setImageBitmap(image);
        } else {
            // Create a new Runnable whose run() method calls
            // displayImage() in the UI Thread.
            final Runnable displayRunnable = new Runnable() {
                    public void run() {
                        displayImage(image);
                    }
                };

            runOnUiThread(displayRunnable);
        }
    }

    /**
     * Read the URL EditText and return the String it contains.
     * 
     * @return String value in mUrlEditText
     */
    private String getUrlString() {
        String s = mUrlEditText.getText().toString();
        if (s.equals(""))
            s = mDefaultUrl;
        return s;
    }
}
