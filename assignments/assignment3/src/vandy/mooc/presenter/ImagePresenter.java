package vandy.mooc.presenter;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import vandy.mooc.MVP;
import vandy.mooc.common.GenericPresenter;
import vandy.mooc.common.Utils;
import vandy.mooc.model.ImageDownloadsModel;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

/**
 * This class defines all the image-related operations.  It implements
 * the various Ops interfaces so it can be created/managed by the
 * GenericActivity framework.  It plays the role of the "Abstraction"
 * in Bridge pattern and the role of the "Presenter" in the
 * Model-View-Presenter pattern.
 */
public class ImagePresenter 
       extends GenericPresenter<MVP.RequiredPresenterOps,
                                MVP.ProvidedModelOps,
                                ImageDownloadsModel>
       implements MVP.ProvidedPresenterOps,
                  MVP.RequiredPresenterOps {
    /**
     * Used to enable garbage collection.
     */
    private WeakReference<MVP.RequiredViewOps> mView;
    	
    /**
     * Stores the running total number of images downloaded that must
     * be handled by ServiceResultHandler.
     */
    private int mNumImagesToHandle;
    
    /**
     * Stores the running total number of images that have been
     * handled by the ServiceResultHandler.
     */
    private int mNumImagesHandled;
    
    /**
     * Stores the directory to be used for all downloaded images.
     */
    private Uri mDirectoryPathname = null;
    
    /**
     * Array of Strings that represent the valid URLs that have
     * been entered.
     */
    private ArrayList<Uri> mUrlList;

    /**
     * Constructor will choose either the Started Service or Bound
     * Service implementation of ImagePresenter.
     */
    public ImagePresenter() {
    }

    /**
     * Hook method called when a new instance of AcronymPresenter is
     * created.  One time initialization code goes here, e.g., storing
     * a WeakReference to the View layer and initializing the Model
     * layer.
     * 
     * @param view
     *            A reference to the View layer.
     */
    @Override
    public void onCreate(MVP.RequiredViewOps view) {
        // Set the WeakReference.
        mView = new WeakReference<>(view);

        // Create a timestamp that will be unique.
        final String timestamp =
            new SimpleDateFormat("yyyyMMdd'_'HHmm").format(new Date());

        // Use the timestamp to create a pathname for the
        // directory that stores downloaded images.
        mDirectoryPathname = 
            Uri.parse(Environment.getExternalStoragePublicDirectory
                      (Environment.DIRECTORY_DCIM)
                      + "/" 
                      + timestamp 
                      + "/");
        
        // Initialize the list of URLs.
        mUrlList = new ArrayList<Uri>();

        // Finish the initialization steps.
        resetFields();

        // Invoke the special onCreate() method in GenericPresenter,
        // passing in the ImageDownloadsModel class to instantiate/manage and
        // "this" to provide ImageDownloadsModel with this MVP.RequiredModelOps
        // instance.
        super.onCreate(ImageDownloadsModel.class,
                       this);
    }

    /**
     * Hook method dispatched by the GenericActivity framework to
     * initialize the ImagePresenter object after a runtime
     * configuration change.
     *
     * @param view         The currently active ImagePresenter.View.
     */
    @Override
    public void onConfigurationChange(MVP.RequiredViewOps view) {
        // Reset the mView WeakReference.
        mView = new WeakReference<>(view);

        // If the content is non-null then we're done, so set the
        // result of the Activity and finish it.
        if (allDownloadsComplete()) {
            // Hide the progress bar.
            mView.get().dismissProgressBar();
            Log.d(TAG,
                  "All images have finished downloading");
        } else if (downloadsInProgress()) {
            // Display the progress bar.
            mView.get().displayProgressBar();

            Log.d(TAG,
                  "Not all images have finished downloading");
        }

        // (Re)display the URLs.
        mView.get().displayUrls();
    }

    /**
     * Hook method called to shutdown the Presenter layer.
     *
     * @param isChangingConfigurations
     *        True if a runtime configuration triggered the onDestroy() call.
     */
    @Override
    public void onDestroy(boolean isChangingConfigurations) {
        // Destroy the model.
        getModel().onDestroy(isChangingConfigurations);
    }

    /**
     * Get the list of URLs.
     */
    @Override
    public ArrayList<Uri> getUrlList() {
        return mUrlList;
    }

    /**
     * Reset the URL and counter fields and redisplay linear layout.
     */
    private void resetFields() {
        // Reset the number of images to handle and which have been
        // handled.
        mNumImagesHandled = 0;
        mNumImagesToHandle = 0;

        // Clear the URL list.
        mUrlList.clear();
        
        // Redisplay the URLs, which should now be empty.
        mView.get().displayUrls();
    }
        
    /**
     * Perform the download and filter processing.  
     */
    @Override
    public void startProcessing() {
        if (mUrlList.isEmpty())
            Utils.showToast(mView.get().getActivityContext(),
                            "no images provided");
        else {
            // Make the progress bar visible.
            mView.get().displayProgressBar();

            // Keep track of number of images to download that must be
            // displayed.
            mNumImagesToHandle = mUrlList.size();

            // Iterate over all the URLs, start each download in an
            // AsyncTask, apply a grayscale filter to each image
            // that's downloaded successfully, and finally call
            // onProcessingComplete() when all is done.  The
            // downloading and filtering should be performed in
            // separate AsyncTask instances, which should run
            // concurrently via the AsyncTask.THREAD_POOL_EXECUTOR and
            // executeOnExecutor().

            // TODO -- you fill in here.
        }
    }

    /**
     * Interact with the View layer to display the downloaded images
     * when they are all returned from the Model.
     */
    @Override
    public void onProcessingComplete(Uri url,
                                     Uri pathToImageFile) {
        // Increment the number of images handled regardless of
        // whether this result succeeded or failed to download and
        // image.
        ++mNumImagesHandled;

        if (pathToImageFile == null)
            // Handle a failed download.
            mView.get().reportDownloadFailure
                (url,
                 allDownloadsComplete());
        else /* replyMessage.getResultCode() == Activity.RESULT_OK) */
            // Handle a successful download.
            Log.d(TAG,
                  "received image at URI "
                  + pathToImageFile.toString());
                
        // Try to display all images received successfully.
        tryToDisplayImages();
    }

    /**
     * Launch an Activity to display all the images that were received
     * successfully if all downloads are complete.
     */
    private void tryToDisplayImages() {
        // If this is last image handled, display images via
        // DisplayImagesActivity.
        if (allDownloadsComplete()) {
            // Dismiss the progress bar.
            mView.get().dismissProgressBar();

            // Initialize state for the next run.
            resetFields();

            // Only start the DisplayImageActivity if the image folder
            // exists and also contains at least 1 image to display.
            // Note that if the directory is empty, File.listFiles()
            // returns null.
            File file = new File(mDirectoryPathname.toString());
            if (file.isDirectory() 
                && file.listFiles() != null 
                && file.listFiles().length > 0) {
                // Display the results.
                mView.get().displayResults(mDirectoryPathname);
            }
        }
    }  

    /**
     * Returns true if all the downloads have completed, else false.
     */
    private boolean allDownloadsComplete() {
        return mNumImagesHandled == mNumImagesToHandle
            && mNumImagesHandled > 0;
    }

    /**
     * Returns true if there are any downloads in progress, else false.
     */
    private boolean downloadsInProgress() {
        return mNumImagesToHandle > 0;
    }

    /**
     * Delete all the downloaded images.
     */
    @Override
    public void deleteDownloadedImages() {
        // Delete all the downloaded image.
        int fileCount = deleteFiles(mDirectoryPathname, 
                                    0);

        // Indicate how many files were deleted.
        Utils.showToast(mView.get().getActivityContext(),
                        fileCount
                        + " downloaded image"
                        + (fileCount == 1 ? " was" : "s were")
                        + " deleted.");

        // Reset the fields for the next run.
        resetFields();
    }

    /**
     * A helper method that recursively deletes files in a specified
     * directory.
     */
    private Integer deleteFiles(Uri directoryPathname,
                                int fileCount) {
        File imageDirectory =
            new File(directoryPathname.toString());        
        File files[] = imageDirectory.listFiles();

        if (files == null) 
            return fileCount;

        // Android does not allow you to delete a directory with child
        // files, so we need to write code that handles this
        // recursively.
        for (final File f : files) {  
            final Uri fileOrDirectoryName = Uri.parse(f.toString());
            if (f.isDirectory()) 
                fileCount += deleteFiles(fileOrDirectoryName, 
                                         fileCount);
            Log.d(TAG,
                  "deleting file "
                  + fileOrDirectoryName.toString()
                  + " with count "
                  + fileCount);
            ++fileCount;
            f.delete();
        }

        imageDirectory.delete();
        return fileCount;
    }

    /**
     * Return the Activity context.
     */
    @Override
    public Context getActivityContext() {
        return mView.get().getActivityContext();
    }
    
    /**
     * Return the Application context.
     */
    @Override
    public Context getApplicationContext() {
        return mView.get().getApplicationContext();
    }
}
