package vandy.mooc.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

/**
 * This helper class encapsulates various static methods that are used
 * to download various types of files (such as songs and images) from
 * the Internet.
 *
 * @@ Vikas, let's chat about how to bling out this class a bit so
 * that it can serve as a general-purpose utility for downloading
 * images, songs, movies, etc.
 */
public class DownloadUtils {
    /**
     * Used for debugging.
     */
    private final static String TAG = 
        DownloadUtils.class.getSimpleName();

    /**
     * If you have access to a stable Internet connection for testing
     * purposes, feel free to change this variable to false so it
     * actually downloads the song from a remote server.
     */
    static final boolean DOWNLOAD_OFFLINE = false;

    /**
     * The resource that we write to the file system in offline mode.
     */
    static final int OFFLINE_TEST_IMAGE = 0;
    
    /**
     * The resource that we write to the file system in offline mode.
     */
    static final int OFFLINE_TEST_SONG = 0;

    /**
     * The file name that we should use to store the song in offline
     * mode.
     */
    static final String OFFLINE_FILENAME = "braincandy.m4a";

    /**
     * Size of each file I/O operation.
     */
    private static final int BUFLEN = 1024;

    /**
     * Download the image located at the provided Internet url using
     * the URL class, store it on the android file system using a
     * FileOutputStream, and return the path to the image file on
     * disk.
     *
     * @param context
     *          The context in which to write the file.
     * @param url 
     *          The URL of the image to download.
     * @param directoryPathname 
     *          Pathname of the directory to write the file.
     * 
     * @return 
     *        Absolute path to the downloaded image file on the file
     *        system.
     */
    public static Uri downloadImage(Context context,
                                    Uri url,
                                    Uri directoryPathname) {
            try  {
                if (!Utils.isExternalStorageWritable()) {
                    Log.d(TAG,
                          "external storage is not writable");
                    return null;
                }
        
                // If we're offline, open the image in our resources.
                if (DOWNLOAD_OFFLINE) {
                    // Create a filePath to a temporary file.
                    File filePath = 
                        new File(Utils.openDirectory(directoryPathname),
                                 Utils.getUniqueFilename(Uri.parse(OFFLINE_FILENAME)));

                    // Get a stream from the image resource and copy it into a
                    // temporary file.
                    try (InputStream is = (InputStream)
                         context.getResources().openRawResource(OFFLINE_TEST_IMAGE);
                         OutputStream os = new FileOutputStream(filePath)) {
                         Utils.interruptibleCopy(is, os);
                    } catch (Exception e) {
                        return null; // Indicate a failure.
                    }

                    // Create an output file and save the image into it.
                    return Utils.createDirectoryAndSaveFile
                        (context,
                         new URL(Uri.fromFile(filePath).toString()),
                         Uri.parse(OFFLINE_FILENAME),
                         directoryPathname);
                } 
                // Otherwise, download the file requested by the user.
                else {
                    // Create an output file and save the image referenced
                    // at the URL into it.
                    return Utils.createDirectoryAndSaveFile
                        (context,
                         new URL(url.toString()),
                         Uri.parse(url.getLastPathSegment()),
                         directoryPathname);
                }
            } catch (Exception e) {
                Log.e(TAG,
                      "Exception while downloading -- returning null."
                      + e.toString());
                return null;
            }
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
            if (!Utils.isExternalStorageWritable()) 
                return null;

            // If we're offline, open the song in our resources.
            if (DOWNLOAD_OFFLINE) {
                // Create a file to a temporary file.
                File file =
                    new File(Utils.openDirectory(directoryPathname),
                             Utils.getUniqueFilename(Uri.parse(OFFLINE_FILENAME)));

                // Get a stream from the song resource and copy it
                // into a temporary file.
                try (InputStream is = (InputStream) 
                         context.getResources().openRawResource(OFFLINE_TEST_SONG);
                     OutputStream os = new FileOutputStream(file)) {
                    // Copy input to output.
                    Utils.interruptibleCopy(is,
                                            os);

                    // Set the modified date to enable cancellation.
                    file.setLastModified(System.currentTimeMillis());
                } catch (Exception e) {
                    return null; // Indicate a failure.
                }

                // Create an output file and save the song into it.
                return Utils.createDirectoryAndSaveFile
                    (context,
                     new URL(Uri.fromFile(file).toString()),
                     Uri.parse(OFFLINE_FILENAME),
                     directoryPathname);
            }
            // Otherwise, download the file requested by the user.
            else {
                // Check to see if this thread has been interrupted.
                if(!Thread.currentThread().isInterrupted()){
                    // Create an output file and save the song referenced
                    // at the URL into it.
                    return Utils.createDirectoryAndSaveFile
                        (context,
                         new URL(url.toString()),
                         Uri.parse(url.getLastPathSegment()),
                         directoryPathname);
                }
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Ensure this class is only used as a utility.
     */
    private DownloadUtils() {
        throw new AssertionError();
    }
}
