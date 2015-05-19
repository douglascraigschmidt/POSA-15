/**
 * This software is intended for educational purposes and there is no
 * warranty, so use at your own risk.
 */
package vandy.mooc.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import vandy.mooc.R;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.util.Base64;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * This helper class encapsulates several static methods that are used
 * to download image files.
 */
public class Utils {
    /**
     * Used for debugging.
     */
    private final static String TAG = "Utils";
    
    /**
     * If you have access to a stable Internet connection for testing
     * purposes, feel free to change this variable to false so it
     * actually downloads the image from a remote server.
     */
    static final boolean DOWNLOAD_OFFLINE = false;
    
    /**
     * The resource that we write to the file system in offline
     * mode. 
     */
    static final int OFFLINE_TEST_IMAGE = R.raw.dougs;

    /**
     * The file name that we should use to store the image in offline
     * mode.
     */
    static final String OFFLINE_FILENAME = "dougs.jpg";

    /**
     * Size of each file I/O operation.
     */
    private static final int BUFLEN = 1024;
    
    /**
     * Display a @a bitmapImage on an @a imageView.
     */
    public static void displayImage(Context context, 
                                    Bitmap bitmapImage,
                                    ImageView imageView) {
        if (bitmapImage != null || imageView != null)
            imageView.setImageBitmap(bitmapImage);
        else
            showToast(context,
                      "image or ImageView is corrupted");
    }

    /**
     * Decode an image located at @a pathToImageFile and return a
     * Bitmap to the image.  This method scales the image to avoid
     * out-of-memory exceptions when decoding large images.
     */
    public static Bitmap decodeImageFromPath(Context context,
                                             Uri pathToImageFile) {
    	ActivityManager mgr = (ActivityManager) context
            .getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo info = new ActivityManager.MemoryInfo();
        mgr.getMemoryInfo(info);
        BitmapFactory.Options options =
            new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inPreferredConfig = Config.ARGB_8888;

        BitmapFactory.decodeFile(pathToImageFile.toString(),
                                 options);
        int ratio = 
            (int) (4 * (long) options.outHeight
                   * (long) options.outWidth * (long) 4 
                   / (info.availMem + 1));

        options.inSampleSize = ratio;
        options.inJustDecodeBounds = false;

        try (InputStream inputStream =
             new FileInputStream(pathToImageFile.toString())) {
                return BitmapFactory.decodeFile(pathToImageFile.toString(),
                                                options);
            } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Apply a grayscale filter to the @a imageEntity and return it.
     */
    public static Uri grayScaleFilter(Context context,
                                      Uri pathToImageFile,
                                      Uri directoryPathname) {
        Bitmap originalImage =
            decodeImageFromPath(context,
                                pathToImageFile);

        // Bail out if something is wrong with the image.
        if (originalImage == null)
            return null;

        Bitmap grayScaleImage = 
            originalImage.copy(originalImage.getConfig(),
                               true);

        boolean hasTransparent = grayScaleImage.hasAlpha();
        int width = grayScaleImage.getWidth();
        int height = grayScaleImage.getHeight();

        // A common pixel-by-pixel grayscale conversion algorithm
        // using values obtained from en.wikipedia.org/wiki/Grayscale.
        for (int i = 0; i < height; ++i) {
            // Break out if we've been interrupted.
            if (Thread.interrupted())
                return null;

            for (int j = 0; j < width; ++j) {
            	// Check if the pixel is transparent in the original
            	// by checking if the alpha is 0.
                if (hasTransparent 
                    && ((grayScaleImage.getPixel(j, i) 
                         & 0xff000000) >> 24) == 0) 
                    continue;
                
                // Convert the pixel to grayscale.
                int pixel = grayScaleImage.getPixel(j, i);
                int grayScale = 
                    (int) (Color.red(pixel) * .299 
                           + Color.green(pixel) * .587
                           + Color.blue(pixel) * .114);
                grayScaleImage.setPixel(j, i, 
                                        Color.rgb(grayScale,
                                                  grayScale,
                                                  grayScale));
            }
        }

        // Create a filePath to a temporary file.
        File filePath = 
            new File(openDirectory(directoryPathname),
                     getUniqueFilename(pathToImageFile.getLastPathSegment()));

        try (FileOutputStream fileOutputStream =
             new FileOutputStream(filePath)) {
            grayScaleImage.compress(CompressFormat.JPEG, 100,
                                    fileOutputStream);

            // Create a URI from the file.
            Uri uri = Uri.fromFile(filePath);

            return Utils.createDirectoryAndSaveFile
                (context, 
                 new URL(uri.toString()),
                 uri.getLastPathSegment(),
                 directoryPathname.toString()); 
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
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
                                    String directoryPathname) {
            try  {
                if (!isExternalStorageWritable()) {
                    Log.d(TAG,
                          "external storage is not writable");
                    return null;
                }
        
                // If we're offline, open the image in our resources.
                if (DOWNLOAD_OFFLINE) {
                    // Create a filePath to a temporary file.
                    File filePath = 
                        new File(openDirectory(Uri.parse(directoryPathname)),
                                 getUniqueFilename(OFFLINE_FILENAME));

                    // Get a stream from the image resource and copy it into a
                    // temporary file.
                    try (InputStream is = (InputStream)
                         context.getResources().openRawResource(OFFLINE_TEST_IMAGE);
                         OutputStream os = new FileOutputStream(filePath)) {
                         copyFile(is, os);
                    } catch (Exception e) {
                        return null; // Indicate a failure.
                    }

                    // Create an output file and save the image into it.
                    return Utils.createDirectoryAndSaveFile
                        (context,
                         new URL(Uri.fromFile(filePath).toString()),
                         OFFLINE_FILENAME,
                         directoryPathname);
                } 
                // Otherwise, download the file requested by the user.
                else {
                    // Create an output file and save the image referenced
                    // at the URL into it.
                    return Utils.createDirectoryAndSaveFile
                        (context,
                         new URL(url.toString()),
                         url.getLastPathSegment(),
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
     * Returns a open File if @a directoryPath points to a valid
     * directory, else null.
     */
    private static File openDirectory(Uri directoryPathname) {
        File d = new File(directoryPathname.toString());
        if (!d.exists() && !d.mkdir())
            return null;
        else 
            return d;
    }

    /**
     * Decode an InputStream into a Bitmap and store it in a file on
     * the device.
     *
     * @param context
     *           The context in which to write the file.
     * @param url               
     *           URL to the resource (e.g., local or remote file).
     * @param fileName          
     *           Name of the file.
     * @param directoryPathname
     *           Pathname of the directory to write the file.
     * 
     * @return 
     *     Absolute path to the downloaded image file on the file
     *     system.
     */
    private static Uri createDirectoryAndSaveFile(Context context,
                                                  URL url,
                                                  String fileName,
                                                  String directoryPathname) {
        try {
            // Bail out of we get an invalid bitmap.
            if (url == null)
                return null;
            
            // Bail if the fileName is null as well.
            if (fileName == null) {
            	return null;
            }

            // Create a directory path.
            File directoryPath = new File(directoryPathname);

            // If the directory doesn't exist already then create it.
            if (!directoryPath.exists()) 
                directoryPath.mkdirs();

            // Create a filePath within the directoryPath.
            File filePath =
                new File(directoryPath,
                         getUniqueFilename(fileName));

            // Delete the file if it already exists.
            if (filePath.exists())
                filePath.delete();
                
            // Pre-validate file.
            try (InputStream is = (InputStream) url.getContent()) {
                 BitmapFactory.Options options = 
                     new BitmapFactory.Options();
                 options.inJustDecodeBounds = true;
                 BitmapFactory.decodeStream(is, null, options);
                 if (options.outMimeType == null)
                     return null;
            } catch (Exception e) {
               	return null; // Indicate a failure.
            }
            
            // Get the content of the resource at the url and save it
            // to an output file.
            try (InputStream is = (InputStream) url.getContent();
                 OutputStream os = new FileOutputStream(filePath)) {
                 copyFile(is, os);
            } catch (Exception e) {
            	return null; // Indicate a failure.
            }

            // Get the absolute path of the image.
            String absolutePathToImage = filePath.getAbsolutePath();

            Log.d(TAG,
                  "absolute path to image file is " 
                  + absolutePathToImage);

            // Return the absolute path to the image file.
            return Uri.parse(absolutePathToImage);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This method checks if we can write image to external storage.
     * 
     * @return true if an image can be written, and false otherwise
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
     *          The name of a file that we'd like to make unique.
     * @return 
     *          String containing the unique temporary filename.
     */
    static private String getUniqueFilename(final String filename) {
    	
        return Base64.encodeToString((filename
                                      + System.currentTimeMillis()
                                      + Thread.currentThread().getName()).getBytes(),
                                      Base64.NO_WRAP);
        // Use this implementation if you don't want to keep filling
        // up your file system with temp files..
        // 
        // return Base64.encodeToString(filename.getBytes(), Base64.NO_WRAP);
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
     * Copy the contents of the @a inputStream to the @a outputStream.
     * @throws IOException 
     */
    private static void copyFile(InputStream inputStream,
                                 OutputStream outputStream) 
                        throws IOException {
        byte[] buffer = new byte[BUFLEN];

        for (int n; (n = inputStream.read(buffer)) >= 0; ) 
            outputStream.write(buffer, 0, n);

        outputStream.flush();
    }

    /**
     * Ensure this class is only used as a utility.
     */
    private Utils() {
        throw new AssertionError();
    } 
}
