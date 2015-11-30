package vandy.mooc.view;

import vandy.mooc.MVP;
import vandy.mooc.R;
import vandy.mooc.common.GenericActivity;
import vandy.mooc.common.Utils;
import vandy.mooc.presenter.ImagePresenter;
import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * This Activity prompts the user for URLs of images to download
 * concurrently via the ImagePresenter and view via the
 * DisplayImagesActivity.  It plays the role of the "View" in the
 * Model-View-Presenter (MVP) pattern.  It extends GenericActivity
 * that provides a framework to automatically handle runtime
 * configuration changes of an ImagePresenter object, which plays the
 * role of the "Presenter" in the MVP pattern.  The
 * MPV.RequiredViewOps and MVP.ProvidedPresenterOps interfaces are
 * used to minimize dependencies between the View and Presenter
 * layers.
 */
public class DownloadImagesActivity 
       extends GenericActivity<MVP.RequiredViewOps,
                               MVP.ProvidedPresenterOps,
                               ImagePresenter>
       implements MVP.RequiredViewOps {
    /**
     * EditText field for entering the desired URL to an image.
     */
    protected EditText mUrlEditText;

    /**
     * Linear layout to store TextViews displaying URLs.
     */
    protected LinearLayout mLinearLayout;

    /**
     * Display progress to the user.
     */
    protected ProgressBar mLoadingProgressBar;
    
    /**
     * Menu on main screen
     */
    protected Menu mServiceMenu;

    /**
     * Hook method called when a new instance of Activity is created.
     * One time initialization code goes here, e.g., UI layout
     * initialization and initializing the GenericActivity framework.
     *
     * @param savedInstanceState
     *            Object that contains saved state information.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Perform first part of initializing the super class.
        super.onCreate(savedInstanceState);

        // Set the default layout.
        setContentView(R.layout.download_images_activity);

        // (Re)initialize all the View fields.
        initializeViewFields();

        // Perform second part of initializing the super class,
        // passing in the ImagePresenter class to instantiate/manage
        // and "this" to provide ImagePresenter with the
        // MVP.RequiredViewOps instance.
        super.onCreate(ImagePresenter.class,
                       this);
    }

    /**
     * Hook method called by Android when this Activity becomes
     * invisible.
     */
    @Override
    protected void onDestroy() {
        // Destroy the presenter layer, passing in whether this is
        // triggered by a runtime configuration or not.
        getPresenter().onDestroy(isChangingConfigurations());

        // Always call super class for necessary operations when
        // stopping.
        super.onDestroy();
    }

    /**
     * Initialize the View fields.
     */
    private void initializeViewFields() {
        // Store the ProgressBar in a field for fast access.
        mLoadingProgressBar =
            (ProgressBar) findViewById(R.id.progressBar_loading);
            
        // Store the EditText that holds the urls entered by the user
        // (if any).
        mUrlEditText =
            (EditText) findViewById(R.id.url);

        // Store the linear layout displaying URLs entered.
        mLinearLayout =
            (LinearLayout) findViewById(R.id.linearLayout);
    }

    /**
     * Called by the Android Activity framework when the user presses
     * the "Download" button in the UI.
     *
     * @param view The view.
     */
    public void downloadImages(View view) {
        getPresenter().startProcessing();
    }

    /**
     * Add whatever URL has been entered into the text field if that
     * URL is valid when user presses the "Add URL" button in UI.
     */
    public void addUrl(View view) {
        // Get the user input (if any).
        final String url =
            mUrlEditText.getText().toString();

        // Do sanity check for syntactic validity of the URL.
        if (URLUtil.isValidUrl(url)) {
            // Add valid URL to running list for download.
            getPresenter().getUrlList().add(Uri.parse(url));

            // (Re)display all the URLs.
            displayUrls();
    	} else 
            Utils.showToast(this,
                            "Invalid URL "
                            + url);
    }

    /**
     * Delete the previously downloaded pictures and directories when
     * user presses the "Delete Downloaded Image(s)" button in the UI.
     */
    public void deleteDownloadedImages(View view) {
        getPresenter().deleteDownloadedImages();
    }
	
    /**
     * Make the ProgressBar visible.
     */
    @Override
    public void displayProgressBar() {
        mLoadingProgressBar.setVisibility(View.VISIBLE);
    }

    /**
     * Make the ProgressBar invisible.
     */
    @Override
    public void dismissProgressBar() {
        mLoadingProgressBar.setVisibility(View.INVISIBLE);
    }

    /**
     * Handle failure to download an image at @a url.
     */
    @Override
    public void reportDownloadFailure(Uri url,
                                      boolean downloadsComplete) {
        Utils.showToast(this,
                        "Invalid URL: image at " 
                        + url.toString()
                        + " failed to download!");

        // Remove the URL that failed from the UI.
        removeUrl(url,
                  downloadsComplete);

        if (downloadsComplete)
            // Dismiss the progress bar.
            mLoadingProgressBar.setVisibility(View.INVISIBLE);
    }

    /**
     * Remove a URL that couldn't be downloaded.
     */
    private void removeUrl(Uri url,
                           boolean downloadsComplete) {
        // Check if passed URL is in the list of URLs.
        if (getPresenter().getUrlList().contains(url)) {
            // Remove the invalid URL from the list.
            getPresenter().getUrlList().remove(url);
        } else {
            // Warn caller that URL was not in the list.
            Log.w(TAG, 
                  "RemoveUrl() - passed URL ("
                  + (url == null ? "null" : url.toString())
                  + ") is not in URL list.");
        }

        // If there are no more downloads pending dismiss the progress
        // bar.
        if (downloadsComplete)
            mLoadingProgressBar.setVisibility(View.INVISIBLE);

        // (Re)display the URLs provided by the user thus far.
        displayUrls();
    }

    /**
     * Display the URLs provided by the user thus far.
     */
    @Override
    public void displayUrls() {
        // First remove all URL views in the parent LinearLayout
        mLinearLayout.removeAllViews();

        // Add a each URL list entry as a text view child of the
        // parent LinearLayout.
        for (Uri url: getPresenter().getUrlList()) {
            TextView urlTextView = new TextView(this);
            urlTextView.setLayoutParams
                (new LayoutParams(LayoutParams.WRAP_CONTENT,
                                  LayoutParams.WRAP_CONTENT));
            urlTextView.setText(url.toString());
            mLinearLayout.addView(urlTextView);
        }

        // Clear the URL input view.
        mUrlEditText.setText("");
    }

    /**
     * Start the DisplayImagesActivity to display the results of the
     * download to the user.
     */
    @Override
    public void displayResults(Uri directoryPathname) {
        // Create an Activity for displaying the images.
        final Intent intent =
            DisplayImagesActivity.makeIntent
            (directoryPathname);

        Log.d(TAG,
              "starting DisplayImageActivity at "
              + directoryPathname.toString());

        // Verify that the intent will resolve to an Activity.
        if (intent.resolveActivity(getPackageManager()) != null) 
            // Launch Activity to display the results.
            startActivity(intent);
    }
}
