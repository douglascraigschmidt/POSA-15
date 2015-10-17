package edu.vandy.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import edu.vandy.MVP;
import edu.vandy.R;
import edu.vandy.common.GenericActivity;
import edu.vandy.common.Utils;
import edu.vandy.presenter.PalantiriPresenter;
import edu.vandy.utils.Options;
import edu.vandy.view.DotArrayAdapter.DotColor;

/**
 * This Activity runs the palantiri simulation.  The number of Beings,
 * Palantiri, and gazing iterations are "extra" parameters to the
 * intent used to start this Activity.  They are used to initialize an
 * Options singleton and are available to Presenter layer when the
 * PalantiriPresenter's start() hook method is called.
 * 		
 * This Activity plays the "View" role in the Model-View-Presenter
 * (MVP) pattern.  It extends that GenericActivity framework that
 * automatically handles runtime configuration changes of a
 * PalantiriPresenter object, which plays the "Presenter" role in the
 * MVP pattern.  The MPV.RequiredViewOps and MVP.ProvidedPresenterOps
 * interfaces are used to minimize dependencies between the View and
 * Presenter layers, e.g., the Presenter layer can display data to the
 * user via the View layer without knowing any implementation details
 * about the Activity.
 */
public class GazingSimulationActivity
       extends GenericActivity<MVP.RequiredViewOps,
                               MVP.ProvidedPresenterOps,
                               PalantiriPresenter>
       implements MVP.RequiredViewOps {
    /**
     * Name of the Intent action that wills start this Activity.
     */
    private static String ACTION_GAZING_SIMULATION =
        "android.intent.action.GAZING_SIMULATION";

    /**
     * The list views that will display our Palantiri and Beings.
     */
    private ListView mPalantirListView;
    private ListView mBeingListView;
	
    /**
     * The array adapters that will convert our booleans to 
     * various colored dots.
     */
    private ArrayAdapter<DotColor> mPalantiriAdapter;
    private ArrayAdapter<DotColor> mBeingAdapter;
    
    /**
     * The Start/Stop Simulation button.
     */
    private Button mSimulationButton;
	
    /**
     * Default parameters.
     */
    private final static String DEFAULT_BEINGS = "6";
    private final static String DEFAULT_PALANTIRI = "4";
    private final static String DEFAULT_GAZING_ITERATIONS = "5";

    /**
     * Factory method that returns an intent that will start the
     * GazingSimulationActivity.
     */
    public static Intent makeIntent(String beings,
                                    String palantiri,
                                    String gazingIterations) {
        if (beings.isEmpty())
            beings = DEFAULT_BEINGS;
        if (palantiri.isEmpty())
            palantiri = DEFAULT_PALANTIRI;
        if (gazingIterations.isEmpty())
            gazingIterations = DEFAULT_GAZING_ITERATIONS;
        return new Intent(ACTION_GAZING_SIMULATION)
            .putExtra("BEINGS", beings)
            .putExtra("PALANTIRI", palantiri)
            .putExtra("GAZING_ITERATIONS", gazingIterations);
    }

    /**
     * This hook method is called when the Activity is instantiated.
     * We must get references to the UI objects that the framework has
     * created based on our gazing_simulation_activity.xml file and
     * store them for later use.
     */ 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Call up to initialize the superclass.
        super.onCreate(savedInstanceState);

        // Set the default content view.
        setContentView(R.layout.gazing_simulation_activity);

        // Invoke the special onCreate() method in GenericActivity,
        // passing in the PalantiriPresenter class to
        // instantiate/manage and "this" to provide PalantiriPresenter
        // with this MVP.RequiredViewOps instance.
        super.onCreate(PalantiriPresenter.class,
                       this);

        // Initialize the Views.
        initializeViews();

        // Run the simulation, passing a parameter that indicates
        // whether this is the first time the Activity has been called
        // or a subsequent call after a runtime configuration has
        // occurred.
        runSimulation(getPresenter().configurationChangeOccurred());
    }

    /**
     * Initialize the Views and GUI widgets.
     */
    private void initializeViews() {
        // Initialize various Views and buttons.
        mPalantirListView =
            (ListView) findViewById(R.id.list_view_palantiri);
        mBeingListView =
            (ListView) findViewById(R.id.list_view_beings);
        mSimulationButton =
            (Button) findViewById(R.id.button_simulation);

        // These adapters convert the colors of the Palantiri and
        // Beings into UI elements.
        mPalantiriAdapter =
            new DotArrayAdapter(this,
                                R.layout.palantir_list_element,
                                getPresenter().getPalantiriColors());
        mBeingAdapter =
            new DotArrayAdapter(this,
                                R.layout.being_list_element,
                                getPresenter().getBeingsColors());
	
        // These ListViews get the UI elements from our adapters.
        mPalantirListView.setAdapter(mPalantiriAdapter);
        mBeingListView.setAdapter(mBeingAdapter);
    }

    /**
     * Try to run the simulation if the conditions are right.
     */
    private void runSimulation(boolean configurationChangeOccurred) {
        // Determine whether the simulation is running or not.
        final boolean simulationRunning = 
            getPresenter().isRunning();

        // If nothing is running and the configuration hasn't changed, 
        // start everything off.
        if (simulationRunning == false
            && configurationChangeOccurred == false) {
            getPresenter().setRunning(true);
			
            // Change the button to say "Stop Simulation."
            mSimulationButton.setText("Stop Simulation");

            // Start the simulation in the Presenter layer.
            getPresenter().start();
        }
        // If the simulation is running and the screen-orientation
        // changed then simply inform the user the simulation is
        // continuing and return.
        else if (configurationChangeOccurred == true
            && simulationRunning) {
            // Change the button to say "Stop Simulation."
            mSimulationButton.setText("Stop Simulation");

            Utils.showToast(this,
                            "Continuing simulation");
        } else
            // Change the button to say "Start Simulation."
            mSimulationButton.setText("Start Simulation");

    }

    /**
     * This hook method is called when the user hits the "Start
     * Simulate" button.
     */ 
    public void simulationButtonPressed(View v) { 
        if (getPresenter().isRunning()) 
            getPresenter().shutdown();
        else
            runSimulation(false);
    }

    /**
     * Shows the Beings on the screen.  By default, all beings are not
     * gazing (yellow).
     */ 
    @Override
    public void showBeings() {
        final Runnable runnable = new Runnable () {
            @Override
            public void run() {
                // Clear the previous list.
                mBeingAdapter.clear();
			
                // Add the new beings.
                for (int i = 0;
                     i < Options.instance().numberOfBeings();
                     ++i) 
                    getPresenter().getBeingsColors().add(DotColor.YELLOW);
			
                // Update the list view.
                mBeingAdapter.notifyDataSetChanged();
            }
        };

        // Use the HaMeR framework to run runnable on the UI Thread.
        runOnUiThread(runnable);
    }

    /**
     * This method shows the palantiri on the screen.  By default, all
     * palantiri are unused (gray).
     */ 
    @Override
    public void showPalantiri() {
        Runnable runnable = new Runnable () {
            @Override
            public void run() {
                // Clear the previous list.
                mPalantiriAdapter.clear();
				
                // Add the new palantiri.
                for (int i = 0;
                     i < Options.instance().numberOfPalantiri();
                     ++i)
                    getPresenter().getPalantiriColors().add(DotColor.GRAY);
				
                // Update the list view.
                mPalantiriAdapter.notifyDataSetChanged();
            }
        };

        // Example of the HaMeR framework to run on the UI Thread.
        runOnUiThread(runnable);
    }

    /**
     * Mark a Palantir at location @a index the given @a color.
     */
    private void markPalantir(final int index,
                              final DotColor color) {
        final Runnable runnable = new Runnable () {
            @Override
            public void run() {
                // Set the appropriate value
                getPresenter().getPalantiriColors().set(index,
                                                        color);
				
                // Update the list view.
                mPalantiriAdapter.notifyDataSetChanged();
            }
        };

        // Example of the HaMeR framework to run on the UI Thread.
        runOnUiThread(runnable);
    }

    /**
     * Mark a Being at location @a index the given @a color.
     */
    private void markBeing(final int index,
                           final DotColor color) {
        final Runnable runnable = new Runnable () {
            @Override
            public void run() {
                // Set the appropriate value.
                getPresenter().getBeingsColors().set(index, 
                                                     color);
				
                // Update the list view.
                mBeingAdapter.notifyDataSetChanged();
            }
        };

        // Example of the HaMeR framework to run on the UI Thread.
        runOnUiThread(runnable);
    }

    /**
     * Mark a specific palantir as being available for use (makes the
     * dot green).
     */ 
    @Override
    public void markFree(final int index) {
        markPalantir(index,
                     DotColor.GREEN);
    }

    /**
     *	Mark a specific palantir as being currently used (makes the
     *	dot red).
     */ 
    @Override
    public void markUsed(final int index) {
        markPalantir(index,
                     DotColor.RED);
    }

    /**
     * Marks a certain being as idle (makes the dot yellow). 
     */ 
    @Override
    public void markIdle(int index) {
        markBeing(index, 
                  DotColor.YELLOW);
    }
    
    /**
     * Mark a certain being as gazing at a palantir (makes the dot
     * green).
     */ 
    @Override
    public void markGazing(final int index) {
        markBeing(index,
                  DotColor.GREEN);
    }

    /**
     * Marks a certain being as not gazing at a palantir (makes the
     * dot red).
     */ 
    @Override
    public void markWaiting(final int index) {
        markBeing(index,
                  DotColor.RED);
    }
	
    /**
     * Called when all threads are done working.  Pops a toast to
     * notify the user.
     */ 
    @Override
    public void done() {
        final Runnable runnable = new Runnable () {
            @Override
            public void run() {
                // Make the Palantiri gray again.
                showPalantiri();

                // Make the Beings yellow again.
                showBeings();

                Utils.showToast(GazingSimulationActivity.this,
                               "Simulation complete");

                // Indicate the current simulation is no longer
                // running.
                getPresenter().setRunning(false);

                // Allow the simulation to start again.
                mSimulationButton.setText("Start Simulation");
            }
        };

        // Example of the HaMeR framework to run on the UI Thread.
        runOnUiThread(runnable);
    }

    /**
     * Called when a shutdown occurs.  Pops a toast to notify the
     * user.
     */ 
    @Override
    public void shutdownOccurred(final int numberOfSimulationThreads) {
        final Runnable runnable = new Runnable () {
            @Override
            public void run() {
                Utils.showToast(GazingSimulationActivity.this,
                                "Exception was thrown or stop button was pressed, so "
                                + numberOfSimulationThreads
                                + " simulation threads are being halted");
            }
        };

        // Example of the HaMeR framework to run on the UI Thread.
        runOnUiThread(runnable);
    }
    
    /**
     * Called when a thread is shutdown.  Pops a toast to notify the
     * user.
     */ 
    @Override
    public void threadShutdown(final int index) {
        final Runnable runnable = new Runnable () {
            @Override
            public void run() {
                Log.d(TAG,
                      "Being " 
                      + index 
                      + " was shutdown");

                // Mark the Being as idle (yellow).
                getPresenter().getBeingsColors().set(index, 
                                                     DotColor.YELLOW);
				
                // Update the list view.
                mBeingAdapter.notifyDataSetChanged();

                // Indicate the current simulation is no longer
                // running.
                getPresenter().setRunning(false);
            }
        };

        // Example of the HaMeR framework to run on the UI Thread.
        runOnUiThread(runnable);
    }
}
