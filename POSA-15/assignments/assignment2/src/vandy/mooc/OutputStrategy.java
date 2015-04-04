package vandy.mooc;

import java.lang.ref.WeakReference;

import android.util.Log;

/**
 * Implements a API for outputting data to Android UI thread and
 * synchronizing on thread completion in the ping-pong application.
 */
public class OutputStrategy {
    /** 
     * Define a WeakReference to avoid memory leaks.  See
     * www.androiddesignpatterns.com/2013/01/inner-class-handler-memory-leak.html
     * for an explanation of why we need this WeakReference.
     */
    private final WeakReference<MainActivity> mOuterClass;

    /**
     * Constructor initializes the field.
     */
    public OutputStrategy(final MainActivity activity) {
        // Keep track of the MainActivity.
        mOuterClass =
            new WeakReference<MainActivity> (activity);
    }

    /** 
     * Output the string from a background thread to the Android
     * display managed by the UI thread.
     */
    public void print(final String outputString) {
        // Call the MainActivity.print() method, which create a
        // Runnable that's ultimately posted to the UI Thread via
        // another Thread that sleeps for 0.5 seconds to let the user
        // see what's going on.
        // @@ TODO - you fill in here.
    }

    /**
     * Error log formats the message and displays it for the debugging
     * purposes.
     */
    public void errorLog(String javaFile,
                         String errorMessage) {
        Log.e(javaFile, errorMessage);
    }
}
