package vandy.mooc.view;

import vandy.mooc.R;
import vandy.mooc.common.GenericActivity;
import vandy.mooc.presenter.DownloadSongPresenter;
import vandy.mooc.utils.ParcelableCommandMusic;
import vandy.mooc.utils.ParcelableCommandMusicAsync;
import vandy.mooc.utils.ParcelableCommandMusicService;
import vandy.mooc.utils.Utils;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

/**
 * This Activity downloads a song and passes it to the
 * MusicCommandProcessorActivity, which plays the song.
 */
public class DownloadSongActivity 
       extends GenericActivity<DownloadSongActivity,
                               DownloadSongPresenter,
                               DownloadSongPresenter> {
    /**
     * Song to download and play.
     */
    private static String DEFAULT_SONG = 
        "http://www.dre.vanderbilt.edu/~schmidt/braincandy.m4a";

    /**
     * Display progress to the user.
     */
    private ProgressBar mLoadingProgressBar;

    /**
     * Request the ParcelableCommandMusicAsync player.
     */
    private static int ASYNC_PLAYER = 0;

    /**
     * Request the ParcelableCommandMusicService player.
     */
    private static int SERVICE_PLAYER = 1;

    /**
     * Hook method called when a new instance of Activity is created.
     * One time initialization code goes here, e.g., runtime
     * configuration changes.
     *
     * @param Bundle object that contains saved state information.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Call up to initialize superclass.
        super.onCreate(savedInstanceState);

        // Get references to the UI components.
        setContentView(R.layout.download_song_activity);

        // Store the ProgressBar in a field for fast access.
        mLoadingProgressBar =
            (ProgressBar) findViewById(R.id.progressBar_loading);

        // Invoke the special onCreate() method in GenericActivity,
        // passing in the DownloadSongPresenter class to
        // instantiate/manage and "this" to provide
        // DownloadSoundPresenter with the DownloadSongActivity
        // instance as a callback target.
        super.onCreate(DownloadSongPresenter.class,
                       this);

        // Make the ProgressBar visible.
        mLoadingProgressBar.setVisibility(View.VISIBLE);

        // Explain which song we're downloading.
        Utils.showToast(this,
                        "Downloading the song at URL "
                        + DEFAULT_SONG);

        // Download the requested song, which runs asynchronously and
        // dispatches onDownloadComplete() when it's finished.
        getPresenter().downloadSong(Uri.parse(DEFAULT_SONG));
    }

    /**
     * Send the @a songUri to the CommandProcessActivity along with
     * the command to execute to play the song.
     */
    public void onDownloadComplete(Uri songUri) {
        // Make the ProgressBar invisible.
        mLoadingProgressBar.setVisibility(View.INVISIBLE);

        if (songUri == null)
            Utils.showToast(this,
                            "songUri not downloaded properly");
        else 
            // Start the Activity using the Async player.
            startMusicCommandProcessorActivity
                (ASYNC_PLAYER,
                 "Async",
                 songUri);
    }

    /**
     * Start the MusicCommandProcessorActivity with the given @a
     * playerType to play the given @a songUri.
     */
    private void startMusicCommandProcessorActivity(int playerType,
                                                    String playerName,
                                                    Uri songUri) {
        // Create an intent that will start an Activity to play a song
        // for the user using the given type of player.
        final Intent intent =
            MusicCommandProcessorActivity.makeIntent
               (makeParcelableCommandMusic(playerType),
                songUri);
       
        // Verify that the intent will resolve to an Activity.
        if (intent.resolveActivity(getPackageManager()) != null) {
            Utils.showToast(this,
                            "Playing song with the "
                            + playerName
                            + " player");

            // Start the MusicCommandProcessorActivity with this
            // implicit intent.
            startActivityForResult(intent,
                                   playerType);
        } else
            Utils.showToast(this,
                            "No Activity found to play songs");
    }

    /**
     * Hook method called back by the Android Activity framework when
     * an Activity that's been launched exits, giving the requestCode
     * it was started with, the resultCode it returned, and any
     * additional data from it.
     */
    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {
        // Check if the started Activity completed successfully.
        if (resultCode == Activity.RESULT_OK) {
            // Check if the request code is what we're expecting.
            if (requestCode == ASYNC_PLAYER) {
                // Start the Activity using the Service player.
                startMusicCommandProcessorActivity
                    (SERVICE_PLAYER,
                     "Service",
                     data.getData());
            } else if (requestCode == SERVICE_PLAYER) {
                Utils.showToast(this,
                                "Shutting down");
                finish();
            }
        }
    }

    /**
     * Factory method that makes the appropriate type of parcelable command.
     */
    private ParcelableCommandMusic makeParcelableCommandMusic(int type) {
        if (type == ASYNC_PLAYER)
            return new ParcelableCommandMusicAsync();
        else if (type == SERVICE_PLAYER)
            return new ParcelableCommandMusicService();
        else 
            throw new IllegalArgumentException();
    }
}
