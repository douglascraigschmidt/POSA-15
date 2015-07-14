package vandy.mooc.view;

import java.util.List;

import vandy.mooc.R;
import vandy.mooc.common.LifecycleLoggingActivity;
import vandy.mooc.common.Utils;
import vandy.mooc.model.aidl.AcronymExpansion;
import vandy.mooc.MVP;
import vandy.mooc.presenter.AcronymPresenter;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

/**
 * The main Activity that prompts the user for Acronyms to expand via
 * AcronymServiceSync and AcronymServiceAsync and view the results.
 * Extends LifecycleLoggingActivity so its lifecycle hook methods are
 * logged automatically.
 */
public class AcronymExpansionActivity
       extends LifecycleLoggingActivity
       implements MVP.RequiredViewOps {
    /**
     * Provides acronym-related operations.
     */
    private AcronymPresenter mAcronymPresenter;

    /**
     * Acronym entered by the user.
     */
    protected EditText mEditText;

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
        setContentView(R.layout.acronym_expansion_activity);

        // Store the EditText that holds the urls entered by the user
        // (if any).
        mEditText = ((EditText) findViewById(R.id.editText1));

        // Create the AcronymPresenter object one time.
        mAcronymPresenter = new AcronymPresenter();
        mAcronymPresenter.onCreate(this);
    }

    /**
     * Hook method called by Android when this Activity is
     * destroyed.
     */
    @Override
    protected void onDestroy() {
        // Destroy the presenter layer, passing in whether this is
        // triggered by a runtime configuration or not.
        mAcronymPresenter.onDestroy(isChangingConfigurations());

        // Always call super class for necessary operations when an
        // Activity is destroyed.
        super.onDestroy();
    }

    /**
     * Hook method invoked when the screen orientation changes.
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Forward to the configuration changed method.
        mAcronymPresenter.onConfigurationChanged(newConfig);
    }

    /*
     * Initiate the synchronous acronym lookup when the user presses
     * the "Look Up Sync" button.
     */
    public void expandAcronymSync(View v) {
        // Hide the keyboard.
        Utils.hideKeyboard(this, 
                           mEditText.getWindowToken());

        // Try to get an acronym entered by the user.
        final String acronym =
            Utils.uppercaseInput(this,
                                 mEditText.getText().toString().trim(),
                                 true);

        if (acronym != null) {
            Log.d(TAG,
                  "calling expandAcronymSync() for "
                  + acronym);

            // Synchronously expand the acronym.
            if (mAcronymPresenter.expandAcronymSync(acronym) == false)
                // Show error message to user.
                Utils.showToast(this,
                                "Call already in progress");

            // Return focus to edit box and select all text in it
            // after query.
            mEditText.requestFocus();
            mEditText.selectAll();
        }
    }

    /*
     * Initiate the asynchronous acronym lookup when the user presses
     * the "Look Up Async" button.
     */
    public void expandAcronymAsync(View v) {
        // Hide the keyboard.
        Utils.hideKeyboard(this, 
                           mEditText.getWindowToken());

        // Try to get an acronym entered by the user.
        final String acronym =
            Utils.uppercaseInput(this,
                                 mEditText.getText().toString().trim(),
                                 true);

        if (acronym != null) {
            Log.d(TAG,
                  "calling expandAcronymAsync() for "
                  + acronym);

            // Synchronously expand the acronym.
            if (mAcronymPresenter.expandAcronymAsync(acronym) == false)
                // Show error message to user.
                Utils.showToast(this,
                                "Call already in progress");

            // Return focus to edit box and select all text in it
            // after query.
            mEditText.requestFocus();
            mEditText.selectAll();
        }
    }

    /**
     * Start a new Activity that displays the Acronym Expansions to
     * the user.
     * 
     * @param results
     *            List of AcronymExpansions to display.
     */
    @Override
    public void displayResults(List<AcronymExpansion> results,
                               String errorMessage) {
        if (results == null)
            Utils.showToast(this,
                            errorMessage);
        else {
            Log.d(TAG,
                  "displayResults() with number of acronyms = "
                  + results.size());

            // Create an intent that will start an Activity to display
            // the Acronym Expansions to the user.
            final Intent intent =
                DisplayExpansionActivity.makeIntent(results);

            // Verify that the intent will resolve to an Activity.
            if (intent.resolveActivity(getPackageManager()) != null)
                // Start the DisplayAcronymExpansionsActivity with
                // this implicit intent.
                startActivity(intent);
            else
                // Show error message to user.
                Utils.showToast(this,
                                "No Activity found to display Acronym Expansions");
        }
    }

    /**
     * Return the Activity context.
     */
    @Override
    public Context getActivityContext() {
        return this;
    }

    /**
     * If the activity is being torn down in order to be 
     * recreated with a new configuration, returns true; 
     * else returns false.
     */
    @Override
    public boolean isChangingConfigurations() {
        return super.isChangingConfigurations();
    }
}
