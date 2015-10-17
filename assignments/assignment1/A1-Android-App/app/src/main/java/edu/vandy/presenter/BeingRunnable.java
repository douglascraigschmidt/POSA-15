package edu.vandy.presenter;

import android.util.Log;
import edu.vandy.common.Utils;
import edu.vandy.model.Palantir;
import edu.vandy.utils.Options;

/**
 * This class implements the gazing logic of a BeingThread.  Since
 * Beings are identified by their indices in the list they must be
 * supplied with an index when created.
 */
public class BeingRunnable
       implements Runnable {
    /**
     * Used for Android debugging.
     */
    private final static String TAG = 
        BeingRunnable.class.getName();

    /**
     * The index of the Being in the list, which is used to properly
     * update the Being's status in the UI.
     */
    private final int mIndex;

    /**
     * Reference to the enclosing Presenter.
     */
    private final PalantiriPresenter mPresenter;

    /**
     * Constructor initializes the field.
     */
    BeingRunnable(int index,
                  PalantiriPresenter presenter) {
        mIndex = index;
        mPresenter = presenter;
    }

    /**
     * Perform the Being gazing logic.
     */
    @Override
    public void run() {
        // Don't start the threads immediately.
        Utils.pauseThread(500);

        // Define local variables.
        int i = 0;
        Palantir palantir = null;

        // Get the underlying BeingThread.
        final BeingThread beingThread = 
            (BeingThread) Thread.currentThread();

        // Try to gaze at a palantir the designated number of
        // times.
        for (;
             i < Options.instance().gazingIterations();
             ++i) {
            try {
                // Break out of the loop if we've been instructed to
                // stop gazing.
                if (BeingThread.isShutdown()) {
                    Log.d(TAG,
                          "isShutdown() is true for Being "
                          + mIndex
                          + " in Thread "
                          + beingThread.getId());

                    // If we've been instructed to stop gazing, notify
                    // the UI and exit gracefully.
                    mPresenter.mView.get().threadShutdown(mIndex);
                    break;
                }

                // Show that we're waiting on the screen.
                mPresenter.mView.get().markWaiting(mIndex);
						
                // Get a Palantir - this call can block if there are
                // no available Palantiri.
                palantir =
                    mPresenter.getModel().acquirePalantir();

                // Make sure we were supposed to get a
                // Palantir.
                if (!incrementGazingCountAndCheck(palantir))
                    break;

                // Mark it as used on the screen.
                mPresenter.mView.get().markUsed(palantir.getId());

                // Show that we're gazing on the screen.
                mPresenter.mView.get().markGazing(mIndex);

                // Gaze at my Palantir for the alloted time.
                palantir.gaze();

                // Show that we're no longer gazing.
                mPresenter.mView.get().markIdle(mIndex);
                Utils.pauseThread(500);

                // Mark the Palantir as being free.
                mPresenter.mView.get().markFree(palantir.getId());
                Utils.pauseThread(500);

                // Tell the double-checker that we're about to
                // give up a Palantir.
                decrementGazingCount();
            } catch (Exception e) {
                Log.d(TAG,
                      "Exception caught in index "
                      + mIndex);

                // If we're interrupted by an exception, notify the UI and
                // exit gracefully.
                mPresenter.mView.get().threadShutdown(mIndex);
            } finally {
                // Always return Palantir back to PalantiriManager.
                mPresenter.getModel().releasePalantir(palantir);
            }
        }

        Log.d(TAG,
              "BeingThread "
              + mIndex
              + " has finished "
              + i 
              + " of its "
              + Options.instance().gazingIterations()
              + " gazing iterations");
    }

    /**
     * This method is called each time a BeingThread acquires a
     * Palantir, so it is called concurrently from different threads.
     * This method increments the number of threads gazing and checks
     * that the number of threads gazing does not exceed the number of
     * Palantiri in the simulation using an AtomicLong object
     * instantiated above (mGazingThreads).  If the number of gazing
     * threads exceeds the number of Palantiri, this thread will call
     * shutdown and return false.
     * 
     * @param palantir
     *         The Palantir that was just acquired.
     *         
     * @return false if the number of gazing threads is greater
     *         than the number of Palantiri, otherwise true.
     */
    private boolean incrementGazingCountAndCheck(Palantir palantir) {
        final long numberOfGazingThreads =
            mPresenter.mGazingThreads.incrementAndGet();

        if ((numberOfGazingThreads > Options.instance().numberOfPalantiri())
            && !BeingThread.isShutdown()) {
            Log.d(TAG,
                  "ERROR, Being "
                  + mIndex
                  + " shouldn't have acquired Palantir "
                  + palantir.getId());

            // Shutdown the app.
            mPresenter.shutdown();
            return false;
        } else
            return true;
    }

    /**
     * This method is called each time a Being is about to release a
     * Palantir.  It should simply decrement the number of gazing
     * threads in mGazingThreads.
     */
    private void decrementGazingCount() {
        mPresenter.mGazingThreads.decrementAndGet();
    }
}
