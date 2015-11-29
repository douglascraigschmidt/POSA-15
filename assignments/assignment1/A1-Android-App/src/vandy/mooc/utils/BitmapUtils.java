package vandy.mooc.utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * BitmapUtils
 * 
 * This helper class encapsulates Bitmap-specific processing methods.
 */
public class BitmapUtils {
    /**
     * Constants used in the image's filenames when saving
     */
    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";

    /**
     * Creates a new file to store an image in.
     * 
     * @return A File created to store a new image.
     * @throws IOException
     */
    public static File createImageFile(File albumDir,
                                       String prefix,
                                       String suffix) throws IOException {
        // Create an image file name.
        String currentTimeStamp =
            new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = prefix + currentTimeStamp;
        File imageFile =
            new File(albumDir.getAbsolutePath() + "/" + imageFileName + suffix);
        imageFile.createNewFile();

        return imageFile;
    }

    /**
     * Dispatches an intent notifying the device's camera app to take
     * a new picture, and updates the current photo path to reflect
     * where the photo will be saved
     * 
     * @param actionCode
     */
    public static Intent getTakePictureIntent(File albumDir) {
        Intent takePictureIntent =
            new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File f = null;

        try {
            f = BitmapUtils.createImageFile(albumDir,
                                            JPEG_FILE_PREFIX,
                                            JPEG_FILE_SUFFIX);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                       Uri.fromFile(f));
        } catch (IOException e) {
            e.printStackTrace();
            f = null;
        }
        return takePictureIntent;
    }

    /**
     * This returns the sample size that should be used when 
     * down-sampling the image. This ensures that the image is scaled
     * appropriately with respect to it's final display size.
     */
    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth,
                                            int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a
            // power of 2 and keeps both
            // height and width larger than the requested height and
            // width.
            while ((halfHeight / inSampleSize) > reqHeight
                   && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /**
     * This will return a bitmap that is loaded and appropriately scaled
     * from the filepath parameter.  
     */
    public static Bitmap decodeSampledBitmapFromFile(String filepath,
                                                     int reqWidth,
                                                     int reqHeight) {
        // First decode with inJustDecodeBounds=true to check
        // dimensions.
        final BitmapFactory.Options options =
            new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filepath,
                                 options);

        // Calculate inSampleSize
        options.inSampleSize =
            calculateInSampleSize(options,
                                  reqWidth,
                                  reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filepath,
                                        options);
    }
    
    /**
     * Helper method used to determine an appropriate name for a
     * processed file.
     */
    public static String getNewFileName(String orgFile) {
        String newFilePath =
            orgFile.replace(JPEG_FILE_SUFFIX, 
                            "edit1" + JPEG_FILE_SUFFIX);
        File processedImgFile = new File(newFilePath);

        int editNum = 1;

        // Increase the file's edit number until there isn't a
        // conflict
        while (processedImgFile.exists())
            processedImgFile = 
                (new File(newFilePath = newFilePath.replace
                          (editNum
                           + JPEG_FILE_SUFFIX,
                           ++editNum 
                           + JPEG_FILE_SUFFIX)));

        return processedImgFile.getAbsolutePath();
    }

    /**
     * Ensure this class is only used as a utility.
     */
    private BitmapUtils() {
        throw new AssertionError();
    } 
}
