package edu.vandy.view;

import java.util.ArrayList;
import java.util.List;

import edu.vandy.R;
import edu.vandy.common.LifecycleLoggingActivity;
import edu.vandy.model.aidl.AcronymExpansion;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

/**
 * This activity shows the acronym expansions for an acronym provided
 * by the user. It expects the intent used to start the activity to
 * contain an extra that holds acronym under the key
 * "KEY_ACRONYM_DATA". Extends LifecycleLoggingActivity so its
 * lifecycle hook methods are logged automatically.
 */
public class DisplayExpansionActivity
       extends LifecycleLoggingActivity {
    /**
     * Custom Action used by Implicit Intent to call this Activity.
     */
    public static final String ACTION_DISPLAY_ACRONYM_EXPANSIONS =
        "edu.vandy.intent.action.ACRONYMS";

    /**
     * Key for the List of Acronym Data to be displayed
     */
    public static final String KEY_ACRONYM_DATA = "acronym";

    /**
     * The ListView that will display the results to the user.
     */
    private ListView mListView;

    /**
     * A custom ArrayAdapter used to display the list of AcronymData
     * objects.
     */
    private AcronymExpansionArrayAdapter mAdapter;

    /**
     * Factory method that makes the implicit intent another Activity
     * uses to call this Activity.
     * 
     * @param results
     *            acronym entered by user.
     */
    public static Intent makeIntent(List<AcronymExpansion> results) {
        // Create an Intent with a custom action to display Acronym
        // Expansions.
        return new Intent(ACTION_DISPLAY_ACRONYM_EXPANSIONS)
            .putParcelableArrayListExtra
                (KEY_ACRONYM_DATA,
                 (ArrayList<AcronymExpansion>) results);
    }

    /**
     * Hook method called when a new instance of Activity is
     * created. One time initialization code goes here, e.g., runtime
     * configuration changes.
     * 
     * @param savedInstanceState
     *            object that contains saved state information.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Call to the superclass.
        super.onCreate(savedInstanceState);

        // Initialize the default layout.
        setContentView(R.layout.display_expansion_activity);

        // Initialize all the View fields.
        initializeViewFields();

        // Get the intent that started this activity and get the extra
        // data from it.
        final List<AcronymExpansion> result = 
            getIntent().getParcelableArrayListExtra(KEY_ACRONYM_DATA);
   
        // Add the results to the Adapter and notify changes.
        mAdapter.addAll(result);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Initialize all the View fields.
     */
    private void initializeViewFields() {
        // Store the ListView for displaying the results entered.
        mListView = (ListView) findViewById(R.id.listView1);

        // Create a local instance of our custom Adapter for our
        // ListView.
        mAdapter = new AcronymExpansionArrayAdapter(this);

        // Set the adapter to the ListView.
        mListView.setAdapter(mAdapter);
    }
}
