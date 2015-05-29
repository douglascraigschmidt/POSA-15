package vandy.mooc.activities;

import vandy.mooc.R;
import vandy.mooc.operations.ImageOps;
import vandy.mooc.utils.RetainedFragmentManager;
import vandy.mooc.utils.ServiceResult;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

/**
 * The main Activity that prompts the user for URLs of images to
 * download concurrently via the DownloadImageService and view via the
 * DisplayImagesActivity.  Also allows the user to delete downloaded
 * images.  Extends LifecycleLoggingActivity so its lifecycle hook
 * methods are logged automatically.  Also implements ServiceResult so
 * that the onServiceResult() hook method is called back when an image
 * has been downloaded.  This implementation uses the
 * RetainedFragmentManager class to handle runtime reconfigurations
 * robustly.
 */
public class MainActivity extends LifecycleLoggingActivity
                          implements ServiceResult {
    /**
     * Used to retain the ImageOps state between runtime configuration
     * changes.
     */
    protected final RetainedFragmentManager mRetainedFragmentManager = 
        new RetainedFragmentManager(this.getFragmentManager(),
                                    TAG);

    /**
     * Provides image-related operations.
     */
    private ImageOps mImageOps;

    /**
     * Hook method called when a new instance of Activity is created.
     * One time initialization code goes here, e.g., UI layout
     * initialization and runtime configuration changes.
     *
     * @param Bundle object that contains saved state information.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Always call super class for necessary
        // initialization/implementation.
        super.onCreate(savedInstanceState);

        // Set the default layout.
        setContentView(R.layout.main_activity);

        // Handle any configuration change.
        handleConfigurationChanges();
    }

    /**
     * Handle hardware reconfigurations, such as rotating the display.
     */
    protected void handleConfigurationChanges() {
        // If this method returns true then this is the first time the
        // Activity has been created.
        if (mRetainedFragmentManager.firstTimeIn()) {
            Log.d(TAG,
                  "First time onCreate() call");

            // Create the ImageOps object one time.
            mImageOps = new ImageOps(this);

            // Store the ImageOps into the RetainedFragmentManager.
            mRetainedFragmentManager.put("IMAGE_OPS_STATE",
                                         mImageOps);
            
        } else {
            Log.d(TAG,
                  "Second or subsequent onCreate() call");

            // The RetainedFragmentManager was previously initialized,
            // which means that a runtime configuration change
            // occured, so obtain the ImageOps object and inform it
            // that the runtime configuration change has completed.
            mImageOps = 
                mRetainedFragmentManager.get("IMAGE_OPS_STATE");

            // This check shouldn't be necessary under normal
            // circumtances, but it's better to lose state than to
            // crash!
            if (mImageOps == null) {
                // Create the ImageOps object one time.
                mImageOps = new ImageOps(this);

                // Store the ImageOps into the
                // RetainedFragmentManager.
                mRetainedFragmentManager.put("IMAGE_OPS_STATE",
                                             mImageOps);
            }            
            else 
                // Inform it that the runtime configuration change has
                // completed.
                mImageOps.onConfigurationChange(this);
        }
    }

    /**
     * Called by the Android Activity framework when the user preses
     * the "Download and Display Image(s)" button in the UI.
     *
     * @param view The view.
     */
    public void downloadImages(View view) {
        mImageOps.startDownloads();
    }

    /**
     * Add whatever URL has been entered into the text field if that
     * URL is valid when user presses the "Add URL" button in UI.
     */
    public void addUrl(View view) {
        mImageOps.addUrl();
    }

    /**
     * Delete the previously downloaded pictures and directories when
     * user presses the "Delete Downloaded Image(s)" button in the UI.
     */
    public void deleteDownloadedImages(View view) {
        mImageOps.deleteDownloadedImages();
    }
	
    /**
     * Hook method called back by the ServiceResultHandler when a
     * Service that's been launched finishes, giving the requestCode
     * it was started with, the resultCode it returned, and any
     * additional result data returned from the service.
     */
    @Override
    public void onServiceResult(int requestCode,
                                int resultCode,
                                Bundle data) {
        // Handle the results.
        mImageOps.doResult(requestCode,
                           resultCode,
                           data);
    }
}
