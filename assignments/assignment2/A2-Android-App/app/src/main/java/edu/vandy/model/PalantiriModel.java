package edu.vandy.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.vandy.MVP;
import edu.vandy.utils.Options;

/**
 * This class is a proxy that provides access to the PalantiriManager.
 * It plays the "Model" role in the Model-View-Presenter (MVP) pattern
 * by acting upon requests from the Presenter, i.e., it implements the
 * methods in MVP.ProvidedModelOps that acquire and release Palantiri
 * from the PalantiriManager and returns the Palantiri to the
 * Presenter.
 */
public class PalantiriModel
       implements MVP.ProvidedModelOps {
    /**
     * Used for Android debugging.
     */
    private final static String TAG = 
        PalantiriModel.class.getName();

    /**
     * Mediates concurrent access of multiple Middle-Earth Beings to a
     * (smaller) fixed number of available Palantiri.
     */
    private PalantiriManager mPalantiriManager;

    /**
     * Hook method called when a new instance of PalantiriModel is
     * created.  One time initialization code goes here, e.g., storing
     * a WeakReference to the Presenter.
     * 
     * @param presenter
     *            A reference to the Presenter layer.
     */
    @Override
    public void onCreate(MVP.RequiredPresenterOps presenter) {
        // No-op
    }

    /**
     * Hook method called to shutdown the Model layer.
     *
     * @param isChangeConfigurations
     *        True if a runtime configuration triggered the onDestroy() call.
     */
    @Override
    public void onDestroy(boolean isChangingConfigurations) {
        // No-op
    }

    /**
     * Create a PalantiriManager that contains the designated number
     * of Palantir with random gaze times between 1 and 5 milliseconds
     *
     * @param palantiriCount
     *            The number of Palantiri to add to the PalantiriManager.
     */
    @Override
    public void makePalantiri(int palantiriCount) {
    	// Create a list to hold the generated Palantiri.
        final List<Palantir> palantiri =
            new ArrayList<Palantir>(palantiriCount);

        // Create a new Random number generator.
        final Random random = new Random();

        // Create and add each new Palantir into the list.  The id of
        // each Palantir is its position in the list.
        for (int i = 0; i < palantiriCount; ++i) 
            palantiri.add(new Palantir(i,
                                       random));

        // Create a PalantiriManager that is used to mediate
        // concurrent access to the List of Palantiri.
        mPalantiriManager = new PalantiriManager(palantiri);
    }

    /**
     * Get the next available Palantir from the resource pool,
     * blocking until one is available.
     */
    @Override
    public Palantir acquirePalantir() {
        return mPalantiriManager.acquire();
    }

    /**
     * Releases the designated @code palantir so it's available for
     * other Beings to use.
     */
    @Override
    public void releasePalantir(final Palantir palantir) {
        mPalantiriManager.release(palantir);
    }
}
