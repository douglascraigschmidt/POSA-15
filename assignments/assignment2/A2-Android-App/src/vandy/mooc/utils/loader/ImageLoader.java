package vandy.mooc.utils.loader;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.ReentrantLock;

import vandy.mooc.common.GenericAsyncTask;
import vandy.mooc.common.GenericAsyncTaskOps;
import vandy.mooc.utils.BitmapUtils;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

/**
 * This class loads and displays images in the background. It maintains 
 * a cache of bitmaps to make displaying the same image multiple times
 * more efficient. 
 */
public class ImageLoader 
    implements GenericAsyncTaskOps<ImageLoaderWorkOrder,
                                   Void,
                                   ImageLoaderWorkResult>{
    /**
     * Logcat Tag
     */
    public static final String TAG =
        ImageLoader.class.getCanonicalName();

    /**
     * Executor used to load the images from disk in the background.
     */
    private final Executor mDisplayThreadPoolExecutor =
        ImageLoaderThreadPool.MY_THREAD_POOL_EXECUTOR;

    /**
     * Map of ImageView's (using their hash codes) to the filepath of 
     * the image they are currently displaying. This is used to ensure 
     * that the ImageView is displaying the image that is being loaded 
     * for it. 
     */
    private final Map<Integer, String> mCacheKeysForImageView =
        new ConcurrentHashMap<>();

    /**
     * Map storing ReentrantLocks for each file.
     */
    private final Map<String, ReentrantLock> fileLocks =
        new WeakHashMap<String, ReentrantLock>();

    /**
     *  Drawable that is displayed while the image is loading
     */
    private final Drawable mLoadingDrawable;

    /**
     * Cache storing the bitmaps in memory.
     */
    private LruCache<String, Bitmap> mBitmapCache;

    /**
     * Constructor initializes the fields.
     */
    public ImageLoader(Drawable loadingDrawable) {
        mLoadingDrawable = loadingDrawable;

        initCache();
    }

    /**
     * Initialize the image cache.
     */
    private void initCache() {
        // Maximum memory allowed for this application in Mb.
        final int maxMemory =
            (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Maximum size of the cache - set to 1/4th of the memory
        // allowed for this application.
        final int cacheSize = maxMemory / 4;

        // Create the cache using the cache size above
        mBitmapCache = new LruCache<String, Bitmap>(cacheSize) {
            // Returns the size of an item in the cache.
            @Override
            protected int sizeOf(String key,
                                 Bitmap value) {
                return value.getAllocationByteCount() / 1024;
            }
        };
    }
    
    /**
     * Adds the bitmap to the image cache.
     */
    private void addBitmapToCache(String filename,
                                 Bitmap bitmap) {
        Log.d(TAG, "added BM to cache for position: " + filename);
        mBitmapCache.put(filename,
                         bitmap);
    }
    
    /**
     * Returns the ReentrantLock associated with the
     * file path parameter.
     */
    private ReentrantLock getFileLock(String filepath) {
        ReentrantLock lock =
            fileLocks.get(filepath);

        if (lock == null) {
            lock = new ReentrantLock();
            fileLocks.put(filepath, 
                          lock);
        }
        return lock;
    }
    
    /**
     * Sets the ImageView parameter to the bitmap at the filepath
     * parameter by loading it in a background thread. A cache is
     * maintained to make loading bitmaps multiple times more
     * efficient.
     */
    public void loadAndDisplayImage(ImageView view,
                String imageFilePath,
                int colWidth) {
        // Store the holder and its current filepath in the cache
        mCacheKeysForImageView.put(view.hashCode(),
                                   imageFilePath);

        Bitmap cachedBitmap =
            mBitmapCache.get(imageFilePath);

        if (cachedBitmap != null)
            // If the bitmap is in the cache, simply place it in the
            // ImageView.
            view.setImageBitmap(cachedBitmap);
        else {
            // If the bitmap isn't in the cache, set the ImageView to
            // display the loading drawable
            view.setImageDrawable(mLoadingDrawable);

            // Create the work order that contains the info
            // needed to load and display the image.
            ImageLoaderWorkOrder wo = 
                new ImageLoaderWorkOrder(new ImageViewHolder(view), 
                                         imageFilePath, 
                                         colWidth,
                                         colWidth);
            
            // Create a new GenericAsyncTask
            GenericAsyncTask<ImageLoaderWorkOrder, 
                             Void, 
                             ImageLoaderWorkResult, 
                             ImageLoader> imageLoaderTask = 
                new GenericAsyncTask<> (this);
            
            // Use the task to load the image in the background.
            imageLoaderTask
                .executeOnExecutor(mDisplayThreadPoolExecutor,
                                   wo);
        }
    }

    /**
     * Checks if the ImageView wrapped by the ImageViewHolder has
     * been garbage collected or reused to display a different
     * image. To ensure that the wrapped view is not GC'd while
     * this check is being performed, we first need to grab a
     * reference to that view.
     */
    private void checkImageView(ImageViewHolder imgView,
                                String filepath)
            throws ViewChangedException {
        // Grab a reference to the wrapped view so that
        // it won't be GC'd between the two checks below.
        ImageView view = imgView.getWrappedImageView();
        checkViewCollected(imgView);
        checkViewReused(imgView,
                        filepath);
    }

    /**
     * Checks if the view has been collected by 
     * the garbage collector
     */
    private void checkViewCollected(ImageViewHolder imgView)
        throws ViewChangedException {
            if (imgView.isCollected())
                throw new ViewChangedException();
    }

    /**
     * Checks if the view has been reused to display 
     * a different image.
     */
    private void checkViewReused(ImageViewHolder imgView,
                                 String filepath)
        throws ViewChangedException {
            if (isViewReused(imgView, filepath))
                throw new ViewChangedException();
    }

    /**
     * Checks if the view has been reused without
     * throwing an exception. If the wrapped image
     * view has been garbage collected, this method
     * will return true.
     */
    private boolean isViewReused(ImageViewHolder imgView,
                                 String filepath) {
        // Get a reference to the wrapped view to prevent
        // garbage collection.
        ImageView view = imgView.getWrappedImageView();
        if (view == null) {
            // Must have been collected.
            return true;
        }

        final String currCachedKey =
                mCacheKeysForImageView.get(view.hashCode());

        return currCachedKey != filepath;
    }

    /**
     * The Exception that is used to cancel the 
     * loading if the view has been collected or
     * reused.
     */
    @SuppressWarnings("serial")
    class ViewChangedException extends Exception {}
    
    /**
     * Hook method called by the GenericAsyncTask framework 
     * to perform the background processing. It loads the image
     * from disk while periodically checking if the view has been 
     * reused. 
     */
    @Override
    public ImageLoaderWorkResult doInBackground(ImageLoaderWorkOrder... param) {
        
        ImageLoaderWorkOrder wo = param[0];

        // Obtain a lock on this image file.
        ReentrantLock lock = getFileLock(wo.getmFilePath());
        lock.lock();

        Bitmap result = null;
        
        try {
            
            // Retrieve data from the work order
            ImageViewHolder holder = wo.getmImageViewHolder();
            String filepath = wo.getmFilePath();
            int width = wo.getmTargetWidth();
            int height = wo.getmTargetHeight();
            
            // Check that the view is still valid
            checkImageView(holder, filepath);
            
            // Load the bitmap
            result = 
                BitmapUtils
                    .decodeSampledBitmapFromFile(filepath,
                                                 width,
                                                 height);
            
            // re-check the view's validity
            checkImageView(holder, filepath);
            
        } catch (ViewChangedException e) {
            // Caught if the view is no longer valid. 
            // Halt loading the image.
            return null;
        } finally {
            lock.unlock();
        }

        return new ImageLoaderWorkResult
                    (wo.getmImageViewHolder(), 
                     wo.getmFilePath(), 
                     result);
    }
    
    /**
     * Hook method called by the GenericAsyncTask framework when the 
     * background processing has completed. This will check that the 
     * ImageView is still set to display the loaded bitmap, and display 
     * it if so. 
     */
    @Override
    public void onPostExecute(ImageLoaderWorkResult result) {
        if (result != null) {
            ImageViewHolder holder = result.getmImageViewHolder();
            String filepath = result.getmFilePath();

            // Check that the ImageView is still valid
            if (!holder.isCollected()
                && !isViewReused(holder, filepath)) {
                addBitmapToCache(filepath,
                                 result.getmBitmap());

                // Display the loaded bitmap
                holder.getWrappedImageView()
                    .setImageBitmap(result.getmBitmap());
            }
        }
    }
}
