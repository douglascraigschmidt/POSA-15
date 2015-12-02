package edu.vandy.model;

import android.util.Log;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Defines a mechanism that mediates concurrent access to a fixed
 * number of available Palantiri.  This class uses a "fair" Semaphore,
 * a HashMap, and synchronized statements to mediate concurrent access
 * to the Palantiri.  This class implements a variant of the "Pooling"
 * pattern (kircher-schwanninger.de/michael/publications/Pooling.pdf).
 */
public class PalantiriManager {
    /**
     * Debugging tag used by the Android logger.
     */
    protected final static String TAG = 
        PalantiriManager.class.getSimpleName();

    /**
     * A counting Semaphore that limits concurrent access to the fixed
     * number of available palantiri managed by the PalantiriManager.
     */
    private final Semaphore mAvailablePalantiri;

    /**
     * A map that associates the @a Palantiri key to the @a boolean
     * values that keep track of whether the key is available.
     */
    protected final ConcurrentHashMap<Palantir, Boolean> mPalantiriMap;

    /**
     * Constructor creates a PalantiriManager for the List of @a
     * palantiri passed as a parameter and initializes the fields.
     */
    public PalantiriManager(List<Palantir> palantiri) {
        // Create a new ConcurrentHashMap, iterate through the List of
        // Palantiri and initialize each key in the HashMap with
        // "true" to indicate it's available, and initialize the
        // Semaphore to use a "fair" implementation that mediates
        // concurrent access to the given Palantiri.
        // TODO -- you fill in here.
        mPalantiriMap = new ConcurrentHashMap<Palantir,Boolean>();

        for (Palantir eachPalantir : palantiri) {
            mPalantiriMap.put(eachPalantir,true);
        }

        mAvailablePalantiri = new Semaphore(palantiri.size(),true);

        Log.i(TAG,"Size of sempahore:"+mAvailablePalantiri.availablePermits());

    }

    /**
     * Get a Palantir from the PalantiriManager, blocking until one is
     * available.
     */
    public Palantir acquire() {
        // Acquire the Semaphore uninterruptibly and then iterate
        // through the ConcurrentHashMap to find the first key in the
        // HashMap whose value is "true" (which indicates it's
        // available for use) and atomically replace the value of this
        // key with "false" to indicate the Palantir isn't available
        // and then return that palantir to the client.  There should
        // be *no* synchronized statements in this method.
        // TODO -- you fill in here.

        Log.i(TAG,"Size of sempahore before acquire:"+mAvailablePalantiri.availablePermits());


        mAvailablePalantiri.acquireUninterruptibly();

        Log.i(TAG, "Size of sempahore after acquire:" + mAvailablePalantiri.availablePermits());


        for(Palantir eachPalantiri : mPalantiriMap.keySet()){
            if(mPalantiriMap.get(eachPalantiri) == true){
                AtomicBoolean lockPalantiri = new AtomicBoolean(false);
                mPalantiriMap.put(eachPalantiri,lockPalantiri.get());
                return eachPalantiri;
            }
        }

        // This shouldn't happen, but we need this here to make the
        // compiler happy.
        return null; 
    }

    /**
     * Returns the designated @code palantir to the PalantiriManager
     * so that it's available for other Threads to use.
     */
    public void release(final Palantir palantir) {
        // Put the "true" value back into ConcurrentHashMap for the
        // palantir key and release the Semaphore if all works
        // properly.  There should be *no* synchronized statements in
        // this method.
        // TODO -- you fill in here.

        Log.i(TAG, "Size of sempahore in release:" + mAvailablePalantiri.availablePermits());
        Log.i(TAG, "Palintiri being released:" + palantir.getId());

        AtomicBoolean unlockPalintiri = new AtomicBoolean(true);
        mPalantiriMap.put(palantir,unlockPalintiri.get());

        mAvailablePalantiri.release();
    }

    /*
     * The following method is just intended for use by the regression
     * tests, not by applications.
     */

    /**
     * Returns the number of available permits on the semaphore.
     */
    public int availablePermits() {
        return mAvailablePalantiri.availablePermits();
    }
}
