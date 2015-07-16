package vandy.mooc.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.util.Base64;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

/**
 * Utils contains helper methods that properly format the WeatherData
 * POJO and display it to the user.
 */
public class Utils {
    /**
     * Logging tag.
     */
    private final static String TAG =
        Utils.class.getCanonicalName();

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
        mgr.hideSoftInputFromWindow(windowToken,
                                    0);
    }
    
    /**
     * Download the song located at the provided Internet url using
     * the URL class, store it on the android file system using a
     * FileOutputStream, and return the path to the song file on disk.
     *
     * @param context
     *            The context in which to write the file.
     * @param url
     *            The URL of the song to download.
     * @param directoryPathname
     *            Pathname of the directory to write the file.
     * 
     * @return Absolute path to the downloaded song file on the file
     *         system.
     */
    public static Uri downloadSong(Context context,
                                   Uri url,
                                   Uri directoryPathname) {
        try {
            if (!isExternalStorageWritable()) {
                Log.d(TAG, "external storage is not writable");
                return null;
            }

            Log.d(TAG, "starting download of song at "
                  + url.toString() 
                  + " into directory " 
                  + directoryPathname);

            // Check to see if this thread has been interrupted.
            if (!Thread.currentThread().isInterrupted()) {
                // Create an output file and save the song referenced
                // at the URL into it.
                return Utils.downloadAndSaveFile
                    (context,
                     new URL(url.toString()),
                     Uri.parse(url.getLastPathSegment()),
                     directoryPathname);
            } else
                return null;
        } catch (Exception e) {
            Log.e(TAG, 
                  "Exception while downloading -- returning null."
                  + e.toString());
            return null;
        }
    }

    /**
     * Returns a open File if @a directoryPath points to a valid
     * directory, else null.
     */
    private static File openDirectory(Uri directoryPathname) {
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
    private static Uri downloadAndSaveFile(Context context,
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
                         Utils.getUniqueFilename(fileName));

            // Delete the file if it already exists.
            if (file.exists())
                file.delete();

            // Get the content of the resource at the url and save it
            // to an output file.
            try (InputStream is = (InputStream) url.getContent();
                 OutputStream os = new FileOutputStream(file)) {
                // Copy input to output.
                copy(is, os);

                // Set the modified date to enable cancellation.
                file.setLastModified(System.currentTimeMillis());
            } catch (Exception e) {
                return null; // Indicate a failure.
            }

            // Get the absolute path of the song.
            final String absolutePathToSong = file.getAbsolutePath();
            return Uri.parse(absolutePathToSong);
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
    private static boolean isExternalStorageWritable() {
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
    static private String getUniqueFilename(final Uri filename) {
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
     * Copy the contents of the @a inputStream to the @a outputStream.
     * 
     * @throws IOException
     */
    private static void copy(InputStream inputStream,
                             OutputStream outputStream)
        throws IOException {
        // Size of each file I/O operation.
        final int BUFLEN = 1024;
        byte[] buffer = new byte[BUFLEN];

        // Keep looping until the input stream is finished or the
        // thread is interrupted.
        for (int n; 
             (n = inputStream.read(buffer)) >= 0 
                 && !Thread.currentThread().isInterrupted();
             ) 
            // Write the bytes to the output stream.
            outputStream.write(buffer,
                               0,
                               n);

        // Flush the contents of the output stream.
        outputStream.flush();
    }

    /**
     * Make UtilGUI a utility class by preventing instantiation.
     */
    private Utils() {
        throw new AssertionError();
    }
}


