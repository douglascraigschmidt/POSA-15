package vandy.mooc.operations;

import android.content.res.Configuration;
import android.view.View;

/**
 * This class defines all the acronym-related operations.
 */
public interface AcronymOps {
    /**
     * Initiate the service binding protocol.
     */
    public void bindService();

    /**
     * Initiate the service unbinding protocol.
     */
    public void unbindService();

    /*
     * Initiate the synchronous acronym lookup when the user presses
     * the "Look Up Sync" button.
     */
    public void expandAcronymSync(View v);

    /*
     * Initiate the asynchronous acronym lookup when the user presses
     * the "Look Up Async" button.
     */
    public void expandAcronymAsync(View v);

    /**
     * Called after a runtime configuration change occurs.
     */
    public void onConfigurationChanged(Configuration newConfig);
}
