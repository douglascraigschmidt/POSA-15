package vandy.mooc.presenter;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;

import vandy.mooc.common.GenericAsyncTask;
import vandy.mooc.common.GenericAsyncTaskOps;
import vandy.mooc.common.PresenterOps;
import vandy.mooc.utils.Utils;
import vandy.mooc.view.DownloadSongActivity;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

/**
 * This class downloads a song and stores it in a local directory on
 * the device.  It plays role of the "Presenter" in the
 * Model-View-Presenter pattern and can communicate with the
 * DownloadSongActivity in the "View" layer.
 */
public class DownloadSongPresenter 
       implements GenericAsyncTaskOps<Uri, Void, Uri>,
                  PresenterOps<DownloadSongActivity> {
    /**
     * Logging tag.
     */
    private final static String TAG =
        DownloadSongPresenter.class.getCanonicalName();

    /**
     * Used to enable garbage collection.
     */
    private WeakReference<DownloadSongActivity> mView;

    /**
     * The GenericAsyncTask used to obtain the weather in a background
     * thread via the Weather Service web service.
     */
    private GenericAsyncTask<Uri,
                             Void,
                             Uri,
                             DownloadSongPresenter> mAsyncTask;

    /**
     * Stores the directory to be used for all downloaded images.
     */
    private Uri mDirectoryPathname = null;

    /**
     * Default constructor is needed by the GenericActivity framework.
     */
    public DownloadSongPresenter() {
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
    public void onCreate(DownloadSongActivity view) {
        // Set the WeakReference.
        mView = new WeakReference<>(view);

        // Create a timestamp that will be unique.
        final String timestamp =
            new SimpleDateFormat("yyyyMMdd'_'HHmm").format(new Date());

        // Use the timestamp to create a pathname for the
        // directory that stores downloaded images.
        mDirectoryPathname = 
            Uri.parse(Environment.getExternalStoragePublicDirectory
                      (Environment.DIRECTORY_DCIM)
                      + "/" 
                      + timestamp 
                      + "/");
    }

    /**
     * Hook method dispatched by the GenericActivity framework to
     * initialize the DownloadSongPresenter object after a runtime
     * configuration change.
     *
     * @param view         The currently active View.
     */
    @Override
    public void onConfigurationChange(DownloadSongActivity view) {
        // Reset the WeakReference.
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
        // No op.
    }

    /**
     * Download the requested song, which runs asynchronously and
     * dispatches onDownloadComplete() when it's finished.
     */
    public void downloadSong(Uri songUri) {
        mAsyncTask = new GenericAsyncTask<>(this);
        mAsyncTask.execute(songUri, 
                           mDirectoryPathname);
    }

    /**
     * Perform the download in background thread.
     */
    public Uri doInBackground(Uri... params) {
        final Uri songUri = params[0];
        final Uri directoryPathname = params[1];

        // Download the requested song in a background thread.
        return Utils.downloadSong
            (mView.get().getApplicationContext(),
             songUri,
             directoryPathname);
    }

    /**
     * Send the result of background calculations to the
     * DownloadSongActivity in the UI thread.
     */
    public void onPostExecute(Uri pathToSongFile) {
        // Return the song path back to the View layer.
        mView.get().onDownloadComplete(pathToSongFile);
    }
}
