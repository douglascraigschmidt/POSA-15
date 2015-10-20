package vandy.mooc.view;

import vandy.mooc.MVP;
import vandy.mooc.R;
import vandy.mooc.common.GenericActivity;
import vandy.mooc.common.Utils;
import vandy.mooc.presenter.ImageDownloadsPresenter;
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
 * This Activity plays the role of the "View" in the
 * Model-View-Presenter (MVP) pattern.  It extends that
 * GenericActivity framework that automatically handles runtime
 * configuration changes of an ImageDownloadsPresenter object, which
 * plays the role of the "Presenter" in the MVP pattern.  The
 * MPV.RequiredViewOps and MVP.ProvidedPresenterOps interfaces are
 * used to minimize dependencies between the View and Presenter
 * layers.
 */
public class ImageDownloadsActivity 
       extends GenericActivity<MVP.RequiredViewOps,
                               MVP.ProvidedPresenterOps,
                               ImageDownloadsPresenter>
       implements MVP.RequiredViewOps {
    /**
     * Debug Tag for logging debug output to LogCat.
     */
    protected final static String TAG =
        ImageDownloadsActivity.class.getSimpleName();

    /**
     * User's selection of URL to download.
     */
    private EditText mUrlEditText;

    /**
     * Image that will be displayed to the user.
     */
    private ImageView mImageView;

    /**
     * Lifecycle hook method that initializes the Activity when it is
     * first created.
     * 
     * @param savedInstanceState
     *            Activity's previously frozen state, if there was one.
     */
    public void onCreate(Bundle savedInstanceState) {
        // Perform first part of initializing the super class.
        super.onCreate(savedInstanceState);

        // Sets the content view specified in the main.xml file.
        setContentView(R.layout.image_downloads_activity);

        // Assign View fields.
        mUrlEditText = (EditText) 
            findViewById(R.id.mUrlEditText);
        mImageView = (ImageView)
            findViewById(R.id.mImageView);

        // Perform second part of initializing the super class,
        // passing in the ImageDownloadsPresenter class to
        // instantiate/manage and "this" to provide
        // ImageDownloadsPresenter with the MVP.RequiredViewOps
        // instance.
        super.onCreate(ImageDownloadsPresenter.class,
                       this);
    }
    
    /**
     * Called when a user clicks a button to download an image.
     * 
     * @param view
     *            Indicates the button pressed by the user.
     */
    public void handleButtonClick(View view) {
        // Hide the keyboard.
        Utils.hideKeyboard(this,
                           mUrlEditText.getWindowToken());

        // Forward to the Presenter layer.
        getPresenter().handleButtonClick(view.getId(),
                                         getUrlString());
    }

    /**
     * Display a downloaded bitmap image if it's non-null; otherwise,
     * it reports an error via a Toast that's displayed on the UI
     * Thread.  This method can be called from either the UI Thread or
     * a background Thread.
     * 
     * @param image
     *            The bitmap image
     * @param completionCommand
     *            Command whose run() hook method is called after the
     *            image is displayed.
     */
    public void displayBitmap(final Bitmap image,
                              final Runnable completionCommand) {
        // If this method is run in the UI Thread then display the
        // image.
        if (Utils.runningOnUiThread()) {
            if (image == null)
                Utils.showToast(this,
                                "image is corrupted,"
                                + " please check the requested URL.");
            else {
                // Display the image on the user's screen.
                mImageView.setImageBitmap(image);

                if (completionCommand != null)
                    // Indicate we're done with this image.  This call
                    // runs in the UI Thread, so we don't need to
                    // synchronize it.
                    completionCommand.run();
            }
        } 

        // Otherwise, create a new Runnable command that's posted to
        // the UI Thread to display the image.
        else {
            runOnUiThread(new Runnable() {
                    public void run() {
                        // Display the downloaded image to the user.
                        displayBitmap(image,
                                      completionCommand);
                    }});
        }
    }

    /**
     * Read the URL EditText and return the String it contains.
     * 
     * @return String value in mUrlEditText
     */
    public String getUrlString() {
        return mUrlEditText.getText().toString();
    }
}
