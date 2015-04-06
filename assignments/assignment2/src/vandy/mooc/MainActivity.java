package vandy.mooc;

import java.util.Locale;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Main Activity for the Android implementation of the concurrent
 * ping-pong application.
 */
public class MainActivity extends Activity {
    /** 
     * A plain TextView that PingPong will be "played" upon. 
     */
    private TextView mPingPongTextViewLog;

    /** 
     * A ScrollView that contains the PingPong results.
     */
    private ScrollView mPingPongScrollView;

    /** 
     * A more colorful TextView that prints "Ping" or "Pong" to the
     * display.
     */
    private TextView mPingPongColorOutput;

    /** 
     * Button that allows playing and resetting the concurrent
     * ping/pong algorithm.
     */
    private Button mPlayButton;
    
    /**
     * Possible states that the ping/pong program can be in.
     */
    enum ProgramState {
        /**
         * The program is ready to run.
         */
        RUN,
        /**
        * The program is running but can be reset.
        */
        RESET
     };

    /**
     * The program's current state.
     */
    private ProgramState mProgramState = ProgramState.RUN;

    /**
     * Defines the strategy for outputting text to the display.
     */
    private OutputStrategy mOutputStrategy;

    /**
     * Maximum number of times to play ping-pong.
     */
    private int mMaxIterations = 10;

    /**
     * PlayPingPong object.
     */
    private PlayPingPong mPlayPingPong;

    /**
     * Hook method called when the Activity is first launched.
     */
    protected void onCreate(Bundle savedInstanceState) {
        // Call up to the super class to perform initializations.
        super.onCreate(savedInstanceState);

        // Sets the content view to the xml file, activity_ping_pong.
        setContentView(R.layout.activity_ping_pong);

        // Cache various TextView and Button widgets used to interact
        // with the user.
        mPingPongTextViewLog =
            (TextView) findViewById(R.id.pingpong_text_output);
        mPingPongScrollView =
            (ScrollView) findViewById(R.id.scrollview_text_output);
        mPingPongColorOutput =
            (TextView) findViewById(R.id.pingpong_color_output);
        mPlayButton =
            (Button) findViewById(R.id.play_button);

        // Create a new OutputStrategy that displays the ping and pong
        // output to the user.
        mOutputStrategy = new OutputStrategy(this);
    }

    /** 
     * Sets the action of the button on click state. 
     */
    public void playButtonClicked(View view) {
        if (mProgramState == null) {
            // Notify the player that something has gone wrong and
            // reset.
            mOutputStrategy.errorLog("MainActivity",
                                     "encountered a null state, "
                                     + "which was then set to RESET");
            mProgramState = ProgramState.RESET;
        }

        switch(mProgramState) {
        case RUN:
            // Create the object that plays ping-pong.
            mPlayPingPong = new PlayPingPong(mMaxIterations,
                                             mOutputStrategy);

            // Create and start a background thread that uses the
            // Android HaMeR concurrency framework to run calls to
            // print() on the UI thread after a short 0.5 sec delay.
            mDelayedOutputThread =
                new DelayedOutputThread(mPlayPingPong);
            mDelayedOutputThread.start();

            mPlayButton.setText(R.string.reset_button);
            mPingPongScrollView.fullScroll(ScrollView.FOCUS_UP);
            mProgramState = ProgramState.RESET;
            break;
        case RESET:
            // Stop the thread that handles calls to print();
            mDelayedOutputThread.interrupt();

            // Reset the color output.
            mPingPongColorOutput.setText("");
            mPingPongColorOutput.setBackgroundColor(Color.TRANSPARENT);
        	
            // Empty TextView and prepare the UI to start another run
            // of the concurrent ping/pong algorithm.
            mPingPongTextViewLog.setText(R.string.empty_string);
            mPlayButton.setText(R.string.play_button);
            mProgramState = ProgramState.RUN;
            break;
        }
    }

    /** 
     * Instance of DelayedOutputThread that's described below.
     */
    private DelayedOutputThread mDelayedOutputThread;
    
    /*
     * Defines a HandlerThread that waits 0.5 seconds between handling
     * messages so the "ping" and "pong" output is visually
     * discernable by the user.
     */
    class DelayedOutputThread extends HandlerThread {
        /**
         * Handler that's used to post Runnables to the
         * HandlerThread's Looper.
         */
        private volatile Handler mDelayedOutputHandler;

        /**
         * Completion hook that's dispatched once the Handler is
         * initialized.
         */
        private final Runnable mCompletionHook;

        /**
         * Constructor initializes the super class and completion hook.
         */
        DelayedOutputThread(Runnable completionHook) {
            super ("DelayedOutputThread");
            mCompletionHook = completionHook;
        }
            
        /**
         * Hook method called back by HandlerThread.run() after the
         * Looper is initialized.
         */
        protected void onLooperPrepared() {
            // Create the Handler in the context of the
            // HandlerThread's Looper.
            mDelayedOutputHandler = new Handler();

            // Start a Thread to run the mRunnable.
            new Thread(mCompletionHook).start();
        }

        /**
         * Run the specified command in the context of the
         * HandlerThread's Looper.
         */
        public void runOnDelayedOutputThread(Runnable command) {
            mDelayedOutputHandler.post(command);
        }
    }

    /**
     * Prints the output string to the text log on screen. If the
     * string contains "ping" (case-insensitive) then a large Ping!
     * will be shown on screen with a certain color. The same goes for
     * strings containing "pong".
     * 
     * This method is called from a background thread and will not
     * block the caller. However, the code to display this output will
     * be posted to the UI thread in such a way that any changes to
     * the UI will be spaced out by 0.5 seconds, thereby giving the
     * user an appropriate amount of time to appreciate the ping'ing
     * and the pong'ing that is happening.
     */
    public void print(final String output) {
        // Post a Runnable task that prints the output with a 0.5
        // second delay between displaying the output.
    	mDelayedOutputThread.runOnDelayedOutputThread(new Runnable() {
            @Override
            public void run() {
                // Post a Runnable whose run() method instructs the UI
                // to print the output.
                runOnUiThread(new Runnable() {	
                    @Override
                    public void run() {
                        mPingPongTextViewLog.append(output);
                        mPingPongScrollView.fullScroll(ScrollView.FOCUS_DOWN);
				        
                        // If we encounter a ping, throw it up on the
                        // screen in color.
                        if (output.toLowerCase(Locale.US).contains("ping")) {
                            mPingPongColorOutput.setBackgroundColor(Color.WHITE);
                            mPingPongColorOutput.setTextColor(Color.BLACK);
                            mPingPongColorOutput.setText("PING");
                        }
                        else if (output.toLowerCase(Locale.US).contains("pong")) {
                            mPingPongColorOutput.setBackgroundColor(Color.BLACK);
                            mPingPongColorOutput.setTextColor(Color.WHITE);
                            mPingPongColorOutput.setText("PONG");
                        }
                    }
                });
				
                // Wait 0.5 seconds before handling the next message.
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    // If we get interrupted, stop the looper.
                    Looper.myLooper().quit();
                }
            }
        });
    }
}
