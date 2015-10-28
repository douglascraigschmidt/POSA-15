package vandy.mooc.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.os.Looper;
import android.util.Base64;
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
    public static boolean interruptibleCopy(InputStream inputStream,
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
                  "Error downloading image");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Returns a open File if @a directoryPath points to a valid
     * directory, else null.
     */
    public static File openDirectory(Uri directoryPathname) {
        File d = new File(directoryPathname.toString());
        if (!d.exists()
            && !d.mkdir())
            return null;
        else
            return d;
    }

    /**
     * Download store a song into a file on the device.
     *
     * @param context
     *            The context in which to write the file.
     * @param url
     *            URL to the resource (e.g., local or remote file).
     * @param fileName
     *            Name of the file.
     * @param directoryPathname
     *            Pathname of the directory to write the file.
     * 
     * @return Absolute path to the downloaded song file on the file
     *         system.
     */
    public static Uri createDirectoryAndSaveFile(Context context,
                                                 URL url,
                                                 Uri fileName,
                                                 Uri directoryPathname) {
        try {
            // Bail out of we get an invalid bitmap.
            if (url == null 
                || fileName == null) 
                return null;

            // Create a directory path.
            File directoryPath =
                new File(directoryPathname.toString());

            // If the directory doesn't exist already then create it.
            if (!directoryPath.exists())
                directoryPath.mkdirs();

            // Create a filePath within the directoryPath.
            File file =
                new File(directoryPath,
                         getUniqueFilename(fileName));

            // Delete the file if it already exists.
            if (file.exists())
                file.delete();

            // Get the content of the resource at the url and save it
            // to an output file.
            try (InputStream is = (InputStream) url.getContent();
                 OutputStream os = new FileOutputStream(file)) {
                // Copy input to output.
                interruptibleCopy(is,
                                  os);

                // Set the modified date to enable cancellation.
                file.setLastModified(System.currentTimeMillis());
            } 
            catch (InterruptedIOException iioe)
			{
            	iioe.printStackTrace();
			}

            // Get the absolute path of the song.
            String absolutePathTosong = file.getAbsolutePath();

            return Uri.parse(absolutePathTosong);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This method checks if we can write song to external storage.
     * 
     * @return true if an song can be written, and false otherwise
     */
    public static boolean isExternalStorageWritable() {
        return Environment.MEDIA_MOUNTED.equals
            (Environment.getExternalStorageState());
    }

    /**
     * Create a filename that contains a timestamp which makes it
     * unique.
     * 
     * @param filename
     *            The name of a file that we'd like to make unique.
     * @return String containing the unique temporary filename.
     */
    public static String getUniqueFilename(final Uri filename) {
        return Base64.encodeToString((filename.toString()
                                      + System.currentTimeMillis() 
                                      + Thread.currentThread().getName()).getBytes(),
                                     Base64.NO_WRAP);
        // Use this implementation if you don't want to keep filling
        // up your file system with temp files..
        //
        // return Base64.encodeToString(filename.getBytes(),
        // Base64.NO_WRAP);
    }

    /**
     * Ensure this class is only used as a utility.
     */
    private Utils() {
        throw new AssertionError();
    } 
}
