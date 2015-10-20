package vandy.mooc.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

/**
 * @class Utils
 *
 * @brief Helper methods shared by various Activities.
 */
public class Utils {
    /**
     * Debugging tag.
     */
    private static final String TAG =
        Utils.class.getCanonicalName();

    /**
     * Return an uppercase version of the input or null if user gave
     * no input.  If user gave no input and @a showToast is true a
     * toast is displayed to this effect.
     */
    public static String uppercaseInput(Context context, 
                                        String input,
                                        boolean showToast) {
        if (input.isEmpty()) {
            if (showToast)
                Utils.showToast(context,
                                "no input provided");
            return null;
        } else
            // Convert the input entered by the user so it's in
            // uppercase.
            return input.toUpperCase(Locale.ENGLISH);
    }

    /**
     * Show a toast message.
     */
    public static void showToast(Context context,
                                 String message) {
        Toast.makeText(context,
                       message,
                       Toast.LENGTH_SHORT).show();
    }

    /**
     * This method is used to hide a keyboard after a user has
     * finished typing the url.
     */
    public static void hideKeyboard(Activity activity,
                                    IBinder windowToken) {
        InputMethodManager mgr =
            (InputMethodManager) activity.getSystemService
            (Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken, 0);
    }
        
    /**
     * Set the result of the Activity to indicate whether the
     * operation on the content succeeded or not.
     * 
     * @param activity
     *          The Activity whose result is being set.
     * @param pathToContent
     *          The pathname to the content file.
     * @param failureReason
     *          String to add to add as an extra to the Intent passed
     *          back to the originating Activity if the @a
     *          pathToContent is null.
     */
    public static void setActivityResult(Activity activity,
                                         Uri pathToContent,
                                         String failureReason) {
        if (pathToContent == null)
            // Indicate why the operation on the content was
            // unsuccessful or was cancelled.
            activity.setResult
                (Activity.RESULT_CANCELED,
                 new Intent("").putExtra("reason",
                                         failureReason));
        else
            // Set the result of the Activity to designate the path to
            // the content file resulting from a successful operation.
            activity.setResult(Activity.RESULT_OK,
                               new Intent("",
                                          pathToContent));
    }

    /**
     * Set the result of the Activity to indicate whether the
     * operation on the content succeeded or not.
     * 
     * @param activity
     *          The Activity whose result is being set.
     * @param resultCode
     *          The result of the Activity, i.e., RESULT_CANCELED or
     *          RESULT_OK. 
     * @param failureReason
     *          String to add to add as an extra to the Intent passed
     *          back to the originating Activity if the result of the
     *          Activity is RESULT_CANCELED. 
     */
    public static void setActivityResult(Activity activity,
                                         int resultCode,
                                         String failureReason) {
        if (resultCode == Activity.RESULT_CANCELED)
            // Indicate why the operation on the content was
            // unsuccessful or was cancelled.
            activity.setResult(Activity.RESULT_CANCELED,
                 new Intent("").putExtra("reason",
                                         failureReason));
        else 
            // Everything is ok.
            activity.setResult(Activity.RESULT_OK);
    }

    /**
     * @return True if the caller is running on the UI thread, else
     * false.
     */
    public static boolean runningOnUiThread() {
        return Thread.currentThread() == Looper.getMainLooper().getThread();
    }

    /**
     * Copy the contents of the @a inputStream to the @a outputStream
     * in a manner that can be interrupted properly.
     * 
     * @return true if copy completed without being interrupted, else false
     *
     * @throws IOException
     */
    private static boolean interruptibleCopy(InputStream inputStream,
                                             OutputStream outputStream)
        throws IOException {
        final byte[] buffer = new byte[1024];

        try {
            // Keep looping until the input stream is finished or the
            // thread is interrupted.
            for (int n; 
                 (n = inputStream.read(buffer)) >= 0;
                 ) {
                if (Thread.interrupted())
                    return false;

                // Write the bytes to the output stream.
                outputStream.write(buffer,
                                   0,
                                   n);
    
            }
        } finally {
            // Flush the contents of the output stream.
            outputStream.flush();
        }
        return true;
    }

    /**
     * Download an image file from the URL provided by the user and
     * decode into a Bitmap.
     * 
     * @param url
     *            The url where a bitmap image is located
     *
     * @return the image bitmap or null if there was an error
     */
    public static Bitmap downloadAndDecodeImage(String url) {
        try {
            // Check to see if this thread has been interrupted.
            if (Thread.interrupted())
                return null;

            // Connect to a remote server, download the contents of
            // the image, and provide access to it via an Input
            // Stream.
            InputStream is =
                (InputStream) new URL(url).getContent();

            // Check to see if this thread has been interrupted.
            if (Thread.interrupted())
                return null;
            else
                // Decode an InputStream into a Bitmap.
                return BitmapFactory.decodeStream(is);
        } catch (Exception e) {
            Log.e(TAG,
                  "Exception "
                  + e
                  + " received in downloadAndDecodeImage()");
            throw new RuntimeException(e);
        }
    }

    /**
     * Ensure this class is only used as a utility.
     */
    private Utils() {
        throw new AssertionError();
    } 
}
