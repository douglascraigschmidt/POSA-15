package vandy.mooc.activities;

import java.util.List;

import vandy.mooc.R;
import vandy.mooc.aidl.AcronymData;
import vandy.mooc.operations.AcronymOps;
import vandy.mooc.operations.AcronymOpsImpl;
import vandy.mooc.utils.AcronymDataArrayAdapter;
import vandy.mooc.utils.RetainedFragmentManager;
import vandy.mooc.utils.Utils;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

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
     * The ListView that will display the results to the user.
     */
    protected ListView mListView;

    /**
     * Acronym entered by the user.
     */
    protected EditText mEditText;

    /**
     * A custom ArrayAdapter used to display the list of AcronymData
     * objects.
     */
    protected AcronymDataArrayAdapter mAdapter;

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

        // Get references to the UI components.
        setContentView(R.layout.main_activity);

        // Store the EditText that holds the urls entered by the user
        // (if any).
        mEditText = (EditText) findViewById(R.id.editText1);

        // Store the ListView for displaying the results entered.
        mListView = (ListView) findViewById(R.id.listView1);

        // Create a local instance of our custom Adapter for our
        // ListView.
        mAdapter = new AcronymDataArrayAdapter(this);

        // Set the adapter to the ListView.
        mListView.setAdapter(mAdapter);

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
        // Get the acronym entered by the user.
        final String acronym =
            mEditText.getText().toString();
        
        // Reset the display for the next acronym expansion.
        resetDisplay();

        // Asynchronously expand the acronym. 
        mAcronymOps.expandAcronymSync(acronym);
    }

    /*
     * Initiate the asynchronous acronym lookup when the user presses
     * the "Look Up Async" button.
     */
    public void expandAcronymAsync(View v) {
        // Get the acronym entered by the user.
        final String acronym =
            mEditText.getText().toString();
        
        // Reset the display for the next acronym expansion.
        resetDisplay();
        
        // Asynchronously expand the acronym. 
        mAcronymOps.expandAcronymAsync(acronym);
    }

    /**
     * Display the results to the screen.
     * 
     * @param results
     *            List of Results to be displayed.
     */
    public void displayResults(List<AcronymData> results,
                               String errorMessage) {
        if (results == null || results.size() == 0)
            Utils.showToast(this,
                            errorMessage);
        else {
            Log.d(TAG,
                  "displayResults() with number of acronyms = "
                  + results.size());

            // Set/change data set.
            mAdapter.clear();
            mAdapter.addAll(results);
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Reset the display prior to attempting to expand a new acronym.
     */
    private void resetDisplay() {
        Utils.hideKeyboard(this,
                           mEditText.getWindowToken());
        mAdapter.clear();
        mAdapter.notifyDataSetChanged();
    }
}
