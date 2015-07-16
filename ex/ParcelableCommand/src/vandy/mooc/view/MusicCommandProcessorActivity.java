package vandy.mooc.view;

import vandy.mooc.R;
import vandy.mooc.common.GenericActivity;
import vandy.mooc.presenter.MusicCommandProcessorPresenter;
import vandy.mooc.utils.ParcelableCommandMusic;
import vandy.mooc.utils.Utils;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.TextView;

/**
 * This Activity receives ParcelableCommandMusic objects stored as
 * "extras" in the Intent used to start the Activity.  It extracts
 * these commands and passes them to the Presenter layer to execute
 * them.
 */
public class MusicCommandProcessorActivity
       extends GenericActivity<MusicCommandProcessorActivity, 
                               MusicCommandProcessorPresenter, 
                               MusicCommandProcessorPresenter> {
    /**
     * Custom Action used by Implicit Intent to call this Activity.
     */
    public static final String ACTION_MUSIC_COMMAND_PROCESSOR =
        "vandy.mooc.intent.action.MUSIC_COMMAND_PROCESSOR";
	
    /** 
     * A colorful TextView that flashes the display.
     */
    private TextView mColorOutput;

    /**
     * MIME_TYPE of Songs Data
     */
    public static final String TYPE_SONGS =
        "parcelable/songs";
	
    /**
     * Key for the ParcelableCommand.
     */
    public static final String KEY_SONG_PLAYING_COMMAND =
        "song_playing_command";

    /**
     * Factory method that makes the implicit intent another Activity
     * uses to start this Activity.
     */
    public static Intent makeIntent(ParcelableCommandMusic parcelableCommand,
                                    Uri songUri) {
        // Create an Intent with a custom action.
        return new Intent(ACTION_MUSIC_COMMAND_PROCESSOR)
            // Set data and MIME_TYPE to display Songs.
            .setDataAndType(songUri, 
                            TYPE_SONGS)
            // Store the ParcelableCommand to send to the
            // CommandProcessorActivity when it starts.
            .putExtra(KEY_SONG_PLAYING_COMMAND, 
                      (Parcelable) parcelableCommand);
    }
    
    /**
     * Hook method called when a new instance of Activity is created.
     * One time initialization code goes here, e.g., runtime
     * configuration changes.
     *
     * @param Bundle object that contains saved state information.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Call the super class for necessary initialization.
        super.onCreate(savedInstanceState);
		
        // Get references to the UI components.
        setContentView(R.layout.music_command_processor_activity);

        // Initialize the View.
        mColorOutput =
            (TextView) findViewById(R.id.color_output);

        // Invoke the special onCreate() method in GenericActivity,
        // passing in the DownloadSongPresenter class to
        // instantiate/manage and "this" to provide
        // DownloadSoundPresenter with the DownloadSongActivity
        // instance as a callback target.
        super.onCreate(MusicCommandProcessorPresenter.class,
                       this);

        // Get the intent that started this Activity and execute the
        // ParcelableCommandMusic extra passed along with it.
        executeCommand(getIntent());
    }

    /**
     * Execute the command to play the song.
     */
    private void executeCommand(Intent intent) {
        // Check whether it is correct intent type.
        if (intent.getType().equals(TYPE_SONGS)) {
            // Get the ParcelableCommand from the Intent.
            final ParcelableCommandMusic parcelableCommandMusic =
                  intent.getParcelableExtra(KEY_SONG_PLAYING_COMMAND);
           
            // Execute the command.
            getPresenter().executeCommand
                (parcelableCommandMusic,
                 intent.getData(),
                 makeScreenFlasher());
        } else 
            // Show error message.
            Utils.showToast(this,
                            "Incorrect Data");
    }

    /**
     * Return a runnable that flashes the screen while the music
     * plays.
     */
    private Runnable makeScreenFlasher() {
        return new Runnable() {
            @Override
            public void run() {
                // Keep iterating until this Thread is interrupted.
                for (int counter = 0; ; ++counter) {
                    final int i = counter;

                    // Create a Runnable that will flash the screen.
                    final Runnable flasher = new Runnable() {
                        final int j = i;
                        @Override
                        public void run() {
                            // Change the background color every 500 milliseconds.
                            if ((j % 2) == 0) 
                                mColorOutput.setBackgroundColor(Color.WHITE);
                            else 
                                mColorOutput.setBackgroundColor(Color.BLACK);
                        }
                    };

                    // Post a Runnable whose run() method instructs
                    // the UI to print the output.
                    runOnUiThread(flasher);
				
                    // Wait 500 miiliseconds before handling the next
                    // message.
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        // Set the result of the Activity and finish
                        // it when this Thread is interrupted.
                        setResult(RESULT_OK,
                                  new Intent("",
                                             getIntent().getData()));
                        finish();
                        return;
                    }
                }
            }
        };
    }
}    


