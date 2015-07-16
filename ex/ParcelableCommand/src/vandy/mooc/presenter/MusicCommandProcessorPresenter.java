package vandy.mooc.presenter;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

import vandy.mooc.common.PresenterOps;
import vandy.mooc.utils.ParcelableCommandMusic;
import vandy.mooc.view.MusicCommandProcessorActivity;
import android.net.Uri;

/**
 * This class executes a ParcelableCommandMusic to play a song.  It
 * plays role of the "Presenter" in the Model-View-Presenter pattern
 * and can communicate with the MusicCommandProcessorActivity in the
 * "View" layer.
 */
public class MusicCommandProcessorPresenter 
       implements PresenterOps<MusicCommandProcessorActivity> {
    /**
     * Debugging tag used by the Android logger.
     */
    private String TAG = getClass().getSimpleName();

    /**
     * Used to enable garbage collection.
     */
    private WeakReference<MusicCommandProcessorActivity> mView;

    /**
     * Default constructor is needed by the GenericActivity framework.
     */
    public MusicCommandProcessorPresenter() {
    }

    /**
     * Hook method called when a new instance of AcronymPresenter is
     * created.  One time initialization code goes here, e.g., storing
     * a WeakReference to the View layer and initializing the Model
     * layer.
     * 
     * @param view
     *            A reference to the View layer.
     */
    @Override
    public void onCreate(MusicCommandProcessorActivity view) {
        // Set the WeakReference.
        mView = new WeakReference<>(view);
    }

    /**
     * Hook method dispatched by the GenericActivity framework to
     * initialize the DownloadSongPresenter object after a runtime
     * configuration change.
     *
     * @param view         The currently active View.
     */
    @Override
    public void onConfigurationChange(MusicCommandProcessorActivity view) {
        // Reset the mImageView WeakReference.
        mView = new WeakReference<>(view);
    }

    /**
     * Hook method called to shutdown the Presenter layer.
     *
     * @param isChangeConfigurations
     *        True if a runtime configuration triggered the onDestroy() call.
     */
    @Override
    public void onDestroy(boolean isChangingConfigurations) {
        // Stop playing the song.
    }

    /**
     * Execute the @a parcelableCommandMusic to play the @a songUri.
     */
    public void executeCommand
        (final ParcelableCommandMusic parcelableCommandMusic,
         Uri songUri,
         Runnable screenFlasher) {
        // Execute the command to starting playing the song.
        parcelableCommandMusic.execute
            (mView.get().getActivityContext(),
             parcelableCommandMusic.makeArgs(songUri));

        // Start a Thread to flash the screen.
        final Thread thread = new Thread(screenFlasher);
        thread.start();

        // Schedule a timer to shut everything down after 4 seconds.
        new Timer().schedule
            (new TimerTask() { 
                    public void run() {
                        // Unexecute the command to stop playing the
                        // song.
                        parcelableCommandMusic.unexecute
                            (mView.get().getActivityContext());

                        // Interrupt the screenFlasher Thread.
                       thread.interrupt();
                    }
                },
                4000);
    }
}
