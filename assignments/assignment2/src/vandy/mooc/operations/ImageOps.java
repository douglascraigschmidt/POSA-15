package vandy.mooc.operations;

import vandy.mooc.activities.MainActivity;
import android.os.Bundle;

/**
 * This class defines all the image-related operations.  It plays the
 * role of the "Abstraction" in Bridge pattern.
 */
public class ImageOps {
    /**
     * Reference to the designed Concrete Implementor (i.e., either
     * ImageOpsBoundService or ImageOpsStartedService).
     */
    private ImageOpsImpl mImageOpsImpl;

    /**
     * Constructor will choose either the Started Service or Bound
     * Service implementation of ImageOps.
     */
    public ImageOps(MainActivity activity,
                    boolean useBoundService) {
        if (useBoundService)
            mImageOpsImpl = new ImageOpsBoundService(activity);
        else
            mImageOpsImpl = null; // Placeholder for now...
    }

    /**
     * Initiate the service binding protocol.
     */
    public void bindService() { 
        mImageOpsImpl.bindService();
    }

    /**
     * Initiate the service unbinding protocol.
     */
    public void unbindService() { 
        mImageOpsImpl.unbindService();        
    }

   /**
     * Add whatever URL has been entered into the text field if that
     * URL is valid when user presses the "Add URL" button in the UI.
     */
    public void addUrl() {
        mImageOpsImpl.addUrl();        
    }

    /**
     * Start all the downloads.
     */
    public void startDownloads() {
        mImageOpsImpl.startDownloads();
    }

    /**
     * Delete all the downloaded images.
     */
    public void deleteDownloadedImages() {
        mImageOpsImpl.deleteDownloadedImages();
    }

    /**
     * Handle the results returned from the Service.
     */
    public void doResult(int requestCode,
                         int resultCode,
                         Bundle data) {
        mImageOpsImpl.doResult(requestCode,
                               resultCode,
                               data);
    }

    /**
     * Called by the ImageOps constructor and after a runtime
     * configuration change occurs to finish the initialization steps.
     */
    public void onConfigurationChange(MainActivity activity) {
        mImageOpsImpl.onConfigurationChange(activity);
    }
}
