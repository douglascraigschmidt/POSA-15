package edu.vandy.presenter;

import java.util.concurrent.CountDownLatch;

import android.os.AsyncTask;
import android.util.Log;
import edu.vandy.common.Utils;
import edu.vandy.model.Palantir;
import edu.vandy.utils.Options;

/**
 * This class implements a BeingAsyncTask, which performs the Being
 * gazing logic and provides a means for canceling an AsyncTask.
 */
public class BeingAsyncTask
       extends AsyncTask<PalantiriPresenter,
                         Runnable,
                         Void> {
    /**
     * Used for Android debugging.
     */
    private final static String TAG = 
        BeingAsyncTask.class.getName();

    /**
     * The index of the Being in the list, which is used to properly
     * update the Being's status in the UI.
     */
    private final int mIndex;

    /**
     * A CountDownLatch that ensures all Threads exit as a group.
     */
    private final CountDownLatch mExitBarrier;

    /**
     * Constructor initializes the field.
     */
    BeingAsyncTask(int index,
                   CountDownLatch exitBarrier) {
        mIndex = index;
        mExitBarrier = exitBarrier;
    }

    /**
     * Perform the Being gazing logic.
     */
    @Override
    public Void doInBackground(PalantiriPresenter... presenters) {
        // Don't start the threads immediately.
        Utils.pauseThread(500);

        // Initialize local variables.
        int i = 0;
        Palantir palantir = null;
        final PalantiriPresenter presenter = presenters[0];

        // Try to gaze at a palantir the designated number of times.
        for (;
             i < Options.instance().gazingIterations();
             ++i)
            try {
                // Break out of the loop if the BeingAsyncTask has
                // been cancelled.
                // TODO -- you fill in here by replacing "false" with
                // the appropriate method call to an AsyncTask method.
                if (false) {
                    // If we've been instructed to stop gazing, notify
                    // the UI and exit gracefully.
                    presenter.mView.get().threadShutdown(mIndex);
                    break;
                }

                // Show that we're waiting on the screen.
                // TODO -- you fill in here with the appropriate
                // call to an AsyncTask method.

                // Get a Palantir - this call blocks if there are no
                // available Palantiri.
                palantir =
                    presenter.getModel().acquirePalantir();

                if (palantir == null)
                    Log.d(TAG,
                          "Received a null palantir in "
                          + Thread.currentThread().getId()
                          + " for Being "
                          + mIndex);

                // Make sure we were supposed to get a Palantir.
                if (!incrementGazingCountAndCheck(presenter))
                    break;

                // Mark it as used on the screen.
                // TODO -- you fill in here with the appropriate
                // call to an AsyncTask method.

                // Show that we're gazing on the screen.
                // TODO -- you fill in here with the appropriate
                // call to an AsyncTask method.

                // Gaze at my Palantir for the alloted time.
                palantir.gaze();

                // Show that we're no longer gazing.
                // TODO -- you fill in here with the appropriate
                // call to an AsyncTask method.
                Utils.pauseThread(500);

                // Mark the Palantir as being free.
                // TODO -- you fill in here with the appropriate call
                // to an AsyncTask method.
                Utils.pauseThread(500);

                // Tell the double-checker that we're about to
                // give up a Palantir.
                decrementGazingCount(presenter);
            } catch (Exception e) {
                Log.d(TAG,
                      "Exception caught in index "
                      + mIndex);

                // If we're interrupted by an exception, notify the UI and
                // exit gracefully.
                presenter.mView.get().threadShutdown(mIndex);
            } finally {
                // Give it back to the manager.
                presenter.getModel().releasePalantir(palantir);
            }

        Log.d(TAG,
              "Thread "
              + mIndex
              + " has finished "
              + i 
              + " of its "
              + Options.instance().gazingIterations()
              + " gazing iterations");
        return (Void) null;
    }

    /**
     * Hook method invoked by the AsyncTask framework when
     * doInBackground() calls publishProgress().  
     */
    @Override
    public void onProgressUpdate(Runnable ...runnableCommands) {
        // TODO -- you fill in here with the appropriate call to
        // the runnableCommands that will cause the progress
        // update to be displayed in the UI thread.
    }

    /**
     * Hook method invoked by the AsyncTask framework after
     * doInBackground() completes successfully.
     */
    @Override
    public void onPostExecute(Void v) {
        // Indicate to the waiter thread in PalantiriPresenter that
        // this AsyncTask is done.
        mExitBarrier.countDown();
    }

    /**
     * Hook method invoked by the AsyncTask framework if
     * doInBackground() is cancelled.
     */
    @Override
    public void onCancelled(Void v) {
        // Indicate to the waiter thread in PalantiriPresenter that
        // this AsyncTask is done.
        mExitBarrier.countDown();
    }

    /**
     * This method is called each time a Being acquires a Palantir.
     * Since each Being is a Java Thread, it will be called
     * concurrently from different threads.  This method increments
     * the number of threads gazing and checks that the number of
     * threads gazing does not exceed the number of Palantiri in the
     * simulation using an AtomicLong object instantiated above
     * (mGazingThreads).  If the number of gazing threads exceeds the
     * number of Palantiri, this thread will call shutdown and return
     * false.
     * 
     * @return false if the number of gazing threads is greater
     *         than the number of Palantiri, otherwise true.
     */
    private boolean incrementGazingCountAndCheck(PalantiriPresenter presenter) {
        final long numberOfGazingThreads =
            presenter.mGazingTasks.incrementAndGet();

        if (numberOfGazingThreads > Options.instance().numberOfPalantiri()) {
            presenter.shutdown();
            return false;
        } else
            return true;
    }

    /**
     * This method is called each time a Being is about to release a
     * Palantir.  It should simply decrement the number of gazing
     * threads in mGazingThreads.
     */
    private void decrementGazingCount(PalantiriPresenter presenter) {
        presenter.mGazingTasks.decrementAndGet();
    }
}
