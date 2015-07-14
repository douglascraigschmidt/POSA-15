package vandy.mooc.view;

import java.util.List;

import vandy.mooc.MVP;
import vandy.mooc.R;
import vandy.mooc.common.GenericActivity;
import vandy.mooc.common.Utils;
import vandy.mooc.model.aidl.AcronymExpansion;
import vandy.mooc.presenter.AcronymPresenter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

/**
 * This Activity prompts the user for acronyms to expand via Android
 * Bound Services and display the results via DisplayAcronymActivity.
 * It plays the role of the "View" in the Model-View-Presenter (MVP)
 * pattern.  It extends that GenericActivity framework that
 * automatically handles runtime configuration changes of an
 * AcronymPresenter object, which plays the role of the "Presenter" in
 * the MVP pattern.  The MPV.RequiredViewOps and
 * MVP.ProvidedPresenterOps interfaces are used to minimize
 * dependencies between the View and Presenter layers.
 */
public class AcronymExpansionActivity
       extends GenericActivity<MVP.RequiredViewOps, MVP.ProvidedPresenterOps, AcronymPresenter>
       implements MVP.RequiredViewOps {
    /**
     * Acronym entered by the user.
     */
    protected EditText mEditText;
	
    /**
     * Hook method called when a new instance of Activity is created.
     * One-time initialization code goes here, e.g., storing Views and
     * initializing the Presenter layer.
     * 
     * @param savedInstanceState
     *            object that contains saved state information.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Perform first part of initializing the super class.
        super.onCreate(savedInstanceState);

        // Get references to the UI components.
        setContentView(R.layout.acronym_expansion_activity);

        // Store the EditText that holds the urls entered by the user
        // (if any).
        mEditText = (EditText) findViewById(R.id.editText1);

        // Perform second part of initializing the super class,
        // passing in the AcronymPresenter class to instantiate/manage
        // and "this" to provide AcronymPresenter with the
        // MVP.RequiredViewOps instance.
        super.onCreate(AcronymPresenter.class,
                       this);
    }

    /**
     * Hook method called by Android when this Activity becomes is
     * destroyed.
     */
    @Override
    protected void onDestroy() {
        // Destroy the presenter layer, passing in whether this is
        // triggered by a runtime configuration or not.
        getPresenter().onDestroy(isChangingConfigurations());

    	// Call super class for necessary operations when stopping.
        super.onDestroy();
    }

    /**
     * Initiate the synchronous acronym lookup when the user presses
     * the "Lookup Acronym Async" button.
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
            if (getPresenter().expandAcronymAsync(acronym) == false)
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
     * Initiate the synchronous acronym lookup when the user presses
     * the "Lookup Acronym Sync" button.
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
            if (getPresenter().expandAcronymSync(acronym) == false)
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
}
