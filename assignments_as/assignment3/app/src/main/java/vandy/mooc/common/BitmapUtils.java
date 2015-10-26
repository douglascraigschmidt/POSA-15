package vandy.mooc.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import vandy.mooc.common.Utils;
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
import android.provider.MediaStore;
import android.widget.ImageView;

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
     * Display a @a bitmapImage on an @a imageView.
     */
    public static void displayImage(Context context, 
                                    Bitmap bitmapImage,
                                    ImageView imageView) {
        if (bitmapImage != null || imageView != null)
            imageView.setImageBitmap(bitmapImage);
        else
            Utils.showToast(context,
                      "image or ImageView is corrupted");
    }

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

        try {
            return BitmapFactory.decodeFile(pathToImageFile.toString(),
                                            options);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Apply a grayscale filter to the @a pathToImageFile and return a
     * Uri to the filtered image.
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
            new File(Utils.openDirectory(directoryPathname),
                     Utils.getUniqueFilename
                         (Uri.parse(pathToImageFile.getLastPathSegment())));

        try (FileOutputStream fileOutputStream =
             new FileOutputStream(filePath)) {
            grayScaleImage.compress(CompressFormat.JPEG, 
                                    100,
                                    fileOutputStream);

            // Create a URI from the file.
            return Uri.fromFile(filePath);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Ensure this class is only used as a utility.
     */
    private BitmapUtils() {
        throw new AssertionError();
    } 
}
