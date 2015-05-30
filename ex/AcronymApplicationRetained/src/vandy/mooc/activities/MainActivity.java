package vandy.mooc.activities;

import vandy.mooc.operations.AcronymOps;
import vandy.mooc.operations.AcronymOpsImpl;
import vandy.mooc.utils.RetainedFragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

/**
 * The main Activity that prompts the user for Acronyms to expand via
 * various implementations of AcronymServiceSync and
 * AcronymServiceAsync and view via the results.  Extends
 * LifecycleLoggingActivity so its lifecycle hook methods are logged
 * automatically.
 */
public class MainActivity extends LifecycleLoggingActivity {
    /**
     * Used to retain the ImageOps state between runtime configuration
     * changes.
     */
    protected final RetainedFragmentManager mRetainedFragmentManager = 
        new RetainedFragmentManager(this.getFragmentManager(),
                                    TAG);

    /**
     * Provides acronym-related operations.
     */
    private AcronymOps mAcronymOps;

    /**
     * Hook method called when a new instance of Activity is created.
     * One time initialization code goes here, e.g., runtime
     * configuration changes.
     *
     * @param Bundle object that contains saved state information.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Always call super class for necessary
        // initialization/implementation.
        super.onCreate(savedInstanceState);

        // Create the AcronymOps object one time.
        mAcronymOps = new AcronymOpsImpl(this);

        // Handle any configuration change.
        handleConfigurationChanges();
    }

    /**
     * Hook method called by Android when this Activity is
     * destroyed.
     */
    @Override
    protected void onDestroy() {
        // Unbind from the Service.
        mAcronymOps.unbindService();

        // Always call super class for necessary operations when an
        // Activity is destroyed.
        super.onDestroy();
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

            // Create the AcronymOps object one time.  The "true"
            // parameter instructs AcronymOps to use the
            // DownloadImagesBoundService.
            mAcronymOps = new AcronymOpsImpl(this);

            // Store the AcronymOps into the RetainedFragmentManager.
            mRetainedFragmentManager.put("ACRONYM_OPS_STATE",
                                         mAcronymOps);
            
            // Initiate the service binding protocol (which may be a
            // no-op, depending on which type of DownloadImages*Service is
            // used).
            mAcronymOps.bindService();
        } else {
            // The RetainedFragmentManager was previously initialized,
            // which means that a runtime configuration change
            // occured.

            Log.d(TAG,
                  "Second or subsequent onCreate() call");

            // Obtain the AcronymOps object from the
            // RetainedFragmentManager.
            mAcronymOps = 
                mRetainedFragmentManager.get("ACRONYM_OPS_STATE");

            // This check shouldn't be necessary under normal
            // circumtances, but it's better to lose state than to
            // crash!
            if (mAcronymOps == null) {
                // Create the AcronymOps object one time.  The "true"
                // parameter instructs AcronymOps to use the
                // DownloadImagesBoundService.
                mAcronymOps = new AcronymOpsImpl(this);

                // Store the AcronymOps into the RetainedFragmentManager.
                mRetainedFragmentManager.put("ACRONYM_OPS_STATE",
                                             mAcronymOps);

                // Initiate the service binding protocol (which may be
                // a no-op, depending on which type of
                // DownloadImages*Service is used).
                mAcronymOps.bindService();
            } else
                // Inform it that the runtime configuration change has
                // completed.
                mAcronymOps.onConfigurationChange(this);
        }
    }

    /*
     * Initiate the synchronous acronym lookup when the user presses
     * the "Look Up Sync" button.
     */
    public void expandAcronymSync(View v) {
        mAcronymOps.expandAcronymSync(v);
    }

    /*
     * Initiate the asynchronous acronym lookup when the user presses
     * the "Look Up Async" button.
     */
    public void expandAcronymAsync(View v) {
        mAcronymOps.expandAcronymAsync(v);
    }
}
