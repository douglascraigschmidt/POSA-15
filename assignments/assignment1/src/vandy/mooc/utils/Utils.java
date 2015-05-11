package vandy.mooc.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Locale;

import vandy.mooc.R;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
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
     * Display a @a bitmapImage on an @a imageView.
     */
    public static Bitmap decodeImageFromPath(Uri pathToImageFile) {
        try (InputStream inputStream =
             new FileInputStream(pathToImageFile.toString())) {
                return BitmapFactory.decodeStream(inputStream);
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
            decodeImageFromPath(pathToImageFile);

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
            	// by checking if the alpha is 0
                if (hasTransparent 
                    && ((grayScaleImage.getPixel(j, i) & 0xff000000) >> 24) == 0) {
                    continue;
                }
                
                // Convert the pixel to grayscale.
                int pixel = grayScaleImage.getPixel(j, i);
                int grayScale = 
                    (int) (Color.red(pixel) * .299 
                           + Color.green(pixel) * .587
                           + Color.blue(pixel) * .114);
                grayScaleImage.setPixel(j, i, 
                                     Color.rgb(grayScale, grayScale, grayScale)
                                     );
            }
        }

        return Utils.createDirectoryAndSaveFile
            (context, 
             grayScaleImage,
             // Name of the image file that we're filtering.
             pathToImageFile.toString(),
             directoryPathname.toString());
    }
    
    /**
     * Download the image located at the provided Internet url using
     * the URL class, store it on the android file system using a
     * FileOutputStream, and return the path to the image file on
     * disk.
     *
     * @param context	the context in which to write the file.
     * @param url       the web url.
     * 
     * @return          the absolute path to the downloaded image file on the file system.
     */
    public static Uri downloadImage(Context context,
                                    Uri url,
                                    String directoryPathname) {
        if (!isExternalStorageWritable()) {
            Log.d(TAG,
                  "external storage is not writable");
            return null;
        }

        // If we're offline, open the image in our resources.
        if (DOWNLOAD_OFFLINE) {
            // Get a stream from the image resource.
            try (InputStream inputStream =
                 context.getResources().openRawResource(OFFLINE_TEST_IMAGE)) {
                    // Create an output file and save the image into it.
                    return Utils.createDirectoryAndSaveFile
                        (context,
                         // Decode the InputStream into a Bitmap image.
                         BitmapFactory.decodeStream(inputStream),
                         OFFLINE_FILENAME,
                         directoryPathname);
            } catch (Exception e) {
                Log.e(TAG,
                      "Exception getting resources."
                      + e.toString());
                return null;
            }
        } 
        // Otherwise, download the file requested by the user.
        else {
            // Download the contents at the URL, which should
            // reference an image.
            try (InputStream inputStream = 
                 (InputStream) new URL(url.toString()).getContent()) {
                    // Create an output file and save the image into it.
                    return Utils.createDirectoryAndSaveFile
                        (context,
                         // Decode the InputStream into a Bitmap image.
                         BitmapFactory.decodeStream(inputStream),
                         url.toString(),
                         directoryPathname);
             } catch (Exception e) {
                Log.e(TAG,
                      "Exception while downloading -- returning null."
                      + e.toString());
                return null;
            }
        }
    }
        
    /**
     * Decode an InputStream into a Bitmap and store it in a file on
     * the device.
     *
     * @param context	   the context in which to write the file.
     * @param inputStream  the Input Stream.
     * @param fileName     name of the file.
     * 
     * @return          the absolute path to the downloaded image file on the file system.
     */
    private static Uri createDirectoryAndSaveFile(Context context,
                                                  Bitmap imageToSave,
                                                  String fileName,
                                                  String directoryPathname) {
        // Bail out of we get an invalid bitmap.
        if (imageToSave == null)
            return null;

        // Try to open a directory.
        File directory =
            new File(directoryPathname);

        // If the directory doesn't exist already then create it.
        if (!directory.exists()) {
            // File newDirectory =
            // new File(directory.getAbsolutePath());
            directory.mkdirs();
        }

        File file = new File(directory, 
                             getTemporaryFilename(fileName));
        // Delete the file if it already exists.
        if (file.exists())
            file.delete();

        // Save the image to the output file.
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            imageToSave.compress(Bitmap.CompressFormat.JPEG,
                                 100,
                                 outputStream);
            outputStream.flush();
        } catch (Exception e) {
            // Indicate a failure.
            return null;
        }

        // Get the absolute path of the image.
        String absolutePathToImage = file.getAbsolutePath();

        // Provide metadata so the downloaded image is viewable in the
        // Gallery.
        ContentValues values =
            new ContentValues();
        values.put(Images.Media.TITLE,
                   fileName);
        values.put(Images.Media.DESCRIPTION,
                   fileName);
        values.put(Images.Media.DATE_TAKEN,
                   System.currentTimeMillis ());
        values.put(Images.ImageColumns.BUCKET_DISPLAY_NAME,
                   file.getName().toLowerCase(Locale.US));
        values.put("_data",
                   absolutePathToImage);
        
        ContentResolver cr = 
            context.getContentResolver();

        // Store the metadata for the image into the Gallery.
        cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                  values);

        Log.d(TAG,
              "absolute path to image file is " 
              + absolutePathToImage);
            
        return Uri.parse(absolutePathToImage);
    }

    /**
     * This method checks if we can write image to external storage
     * 
     * @return true if an image can be written, and false otherwise
     */
    private static boolean isExternalStorageWritable() {
        return Environment.MEDIA_MOUNTED.equals
            (Environment.getExternalStorageState());
    }

    /**
     * Create a temporary filename to store the result of a download.
     * 
     * @param url Name of the URL.
     * @return String containing the temporary filename.
     */
    static private String getTemporaryFilename(final String url) {
        // This is what you'd normally call to get a unique temporary
        // filename, but for testing purposes we always name the file
        // the same to avoid filling up student phones with numerous
        // files!
        //
        // return Base64.encodeToString((url.toString() 
        //                              + System.currentTimeMillis()).getBytes(),
        //                              Base64.NO_WRAP);
        return Base64.encodeToString(url.getBytes(),
                                     Base64.NO_WRAP);
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
     * Ensure this class is only used as a utility.
     */
    private Utils() {
        throw new AssertionError();
    }
}
