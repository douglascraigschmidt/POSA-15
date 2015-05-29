package vandy.mooc.operations;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import vandy.mooc.R;
import vandy.mooc.activities.DisplayImagesActivity;
import vandy.mooc.activities.MainActivity;
import vandy.mooc.utils.ReplyMessage;
import vandy.mooc.utils.ServiceResultHandler;
import vandy.mooc.utils.Utils;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * This abstract class defines all the image-related operations.  It
 * plays the role of the "Implementor" in Bridge pattern and the role
 * of the "Abstract Class" in the Template Method pattern.
 */
public abstract class ImageOpsImpl {
    /**
     * Debugging tag used by the Android logger.
     */
    protected final String TAG = getClass().getSimpleName();

    /**
     * Image-related operations.
     */
    enum OperationType {
        /**
         * Download an image.
         */
        DOWNLOAD_IMAGE
    }

    /**
     * Used to enable garbage collection.
     */
    protected WeakReference<MainActivity> mActivity;
    	
    /**
     * EditText field for entering the desired URL to an image.
     */
    protected WeakReference<EditText> mUrlEditText;

    /**
     * Linear layout to store TextViews displaying URLs.
     */
    protected WeakReference<LinearLayout> mLinearLayout;

    /**
     * Display progress to the user.
     */
    protected WeakReference<ProgressBar> mLoadingProgressBar;

    /**
     * Stores the running total number of images downloaded that must
     * be handled by ServiceResultHandler.
     */
    protected int mNumImagesToHandle;
    
    /**
     * Stores the running total number of images that have been
     * handled by the ServiceResultHandler.
     */
    protected int mNumImagesHandled;
    
    /**
     * Stores the directory to be used for all downloaded images.
     */
    protected String mDirectoryPathname = null;
    
    /**
     * Array of Strings that represent the valid URLs that have
     * been entered.
     */
    protected ArrayList<String> mUrlList;

    /**
     * Stores an instance of ServiceResultHandler.
     */
    protected Handler mServiceResultHandler = null;

    /**
     * Constructor initializes the fields.
     */
    public ImageOpsImpl(MainActivity activity) {
        // Initialize the WeakReference.
        mActivity = new WeakReference<>(activity);

        // Initialize the ServiceResultHandler.
        mServiceResultHandler = 
            new ServiceResultHandler(mActivity.get());

        // Create a timestamp that will be unique.
        final String timestamp =
            new SimpleDateFormat("yyyyMMdd'_'HHmm").format(new Date());

        // Use the timestamp to create a pathname for the directory
        // that stores downloaded images.
        mDirectoryPathname = Environment.getExternalStoragePublicDirectory
            (Environment.DIRECTORY_DCIM)
            + "/" + timestamp + "/";
        
        // Initialize the list of URLs.
        mUrlList = new ArrayList<String>();

        // Finish the initialization steps.
        initializeViewFields();
        resetNonViewFields();
    }

    /**
     * Initialize the View fields, which are all stored as
     * WeakReferences to enable garbage collection.
     */
    private void initializeViewFields() {
        // Store the ProgressBar in a field for fast access.
        mLoadingProgressBar = new WeakReference<> 
            ((ProgressBar) mActivity.get().findViewById(R.id.progressBar_loading));
            
        // Store the EditText that holds the urls entered by the user
        // (if any).
        mUrlEditText = new WeakReference<> 
            ((EditText) mActivity.get().findViewById(R.id.url));

        // Store the linear layout displaying URLs entered.
        mLinearLayout = new WeakReference<> 
            ((LinearLayout) mActivity.get().findViewById(R.id.linearLayout));
    }

    /**
     * Reset the non-view fields (e.g., URLs and counters) and
     * redisplay linear layout.
     */
    private void resetNonViewFields() {
        // Reset the number of images to handle and which have been
        // handled.
        mNumImagesHandled = 0;
        mNumImagesToHandle = 0;

        // Clear the URL list.
        mUrlList.clear();
        
        // Redisplay the URLs, which should now be empty.
        displayUrls();
    }
        
    /**
     * Initiate the service binding protocol.
     */
    public void bindService() { /* no op */ }

    /**
     * Initiate the service unbinding protocol.
     */
    public void unbindService() { /* no op */ }

   /**
     * Add whatever URL has been entered into the text field if that
     * URL is valid when user presses the "Add URL" button in the UI.
     */
    public void addUrl() {
        // Get the user input (if any).
        final String url =
            mUrlEditText.get().getText().toString();

        if (URLUtil.isValidUrl(url)) {
            // Add valid URL to running list for download.
            mUrlList.add
                (mUrlEditText.get().getText().toString());

            // (Re)display all the URLs.
            displayUrls();
    	} else 
            Utils.showToast(mActivity.get(),
                            "Invalid URL "
                            + url);
    }

    /**
     * Remove a URL that couldn't be downloaded.
     */
    private void removeUrl(String url) {
        // Check if passed URL is in the list of URLs.
        if (mUrlList.contains(url)) {
            // Remove the invalid URL from the list.
            mUrlList.remove(url);
        } else {
            // Warn caller that URL was not in the list.
            Log.w(TAG, "RemoveUrl() - passed URL ("
                    + (url == null ? "null" : url.toString())
                    + ") is not in URL list.");
        }

        // If there are no more downloads pending dismiss the progress
        // bar.
        if (allDownloadsComplete())
            mLoadingProgressBar.get().setVisibility(View.INVISIBLE);

        // (Re)display the URLs provided by the user thus far.
        displayUrls();
    }

    /**
     * Display the URLs provided by the user thus far.
     */
    private void displayUrls() {
        // First remove all URL views in the parent LinearLayout
        mLinearLayout.get().removeAllViews();

        // Add a each URL list entry as a text view child of the
        // parent LinearLayout.
        for (String url: mUrlList) {
            TextView urlTextView = new TextView(mActivity.get());
            urlTextView.setLayoutParams
                (new LayoutParams(LayoutParams.WRAP_CONTENT,
                                  LayoutParams.WRAP_CONTENT));
            urlTextView.setText(url);
            mLinearLayout.get().addView(urlTextView);
        }

        // Clear the URL input view.
        mUrlEditText.get().setText("");
    }

    /**
     * Start all the downloads.  Plays the role of the "Template
     * Method" in the Template Method pattern.
     */
    public void startDownloads() {
        // Hide the keyboard.
        Utils.hideKeyboard(mActivity.get(),
                           mUrlEditText.get().getWindowToken());

        if (mUrlList.isEmpty())
            Utils.showToast(mActivity.get(),
                            "no images provided");
        else {
            // Make the progress bar visible.
            mLoadingProgressBar.get().setVisibility(View.VISIBLE);

            // Keep track of number of images to download that must be
            // displayed.
            mNumImagesToHandle = mUrlList.size();

            // Iterate over each URL and start the download.
            for (String urlString : mUrlList) 
                startDownload(Uri.parse(urlString));
        }
    }

    /**
     * Start a download.  Plays the role of a "Primitive Operation"
     * (aka "Hook Method") in the Template Method pattern, which is
     * needed since the means for passing requests to a Started
     * Service are different than for a Bound Service.
     */
    protected abstract void startDownload(Uri url);

    /**
     * Handle the results returned from the Service.
     */
    public void doResult(int requestCode,
                         int resultCode,
                         Bundle data) {
        // Increment the number of images handled regardless of
        // whether this result succeeded or failed to download and
        // image.
        ++mNumImagesHandled;

        if (resultCode == Activity.RESULT_CANCELED) 
            // Handle a failed download.
            handleDownloadFailure(data);
        else /* resultCode == Activity.RESULT_OK) */
            // Handle a successful download.
            Log.d(TAG,
                  "received image at URI "
                  + ReplyMessage.getImagePathname(data));
                
        // Try to display all images received successfully.
        tryToDisplayImages(data);
    }

    /**
     * Launch an Activity to display all the images that were received
     * successfully if all downloads are complete.
     */
    private void tryToDisplayImages(Bundle data) {
        // If this is last image handled, display images via
        // DisplayImagesActivity.
        if (allDownloadsComplete()) {
            // Dismiss the progress bar.
            mLoadingProgressBar.get().setVisibility(View.INVISIBLE);

            // Initialize state for the next run.
            resetNonViewFields();

            // Only start the DisplayImageActivity if the image folder
            // exists and also contains at least 1 image to display.
            // Note that if the directory is empty, File.listFiles()
            // returns null.
            File file = new File(mDirectoryPathname);
            if (file.isDirectory() 
                && file.listFiles() != null 
                && file.listFiles().length > 0) {
                // Create an Activity for displaying the images.
                final Intent intent =
                        DisplayImagesActivity.makeIntent
                                (mDirectoryPathname);

                Log.d(TAG,
                        "starting DisplayImageActivity at "
                                + mDirectoryPathname);

                // Launch Activity to display the results.
                mActivity.get().startActivity(intent);
            }
        }
    }  

    /**
     * Handle failure to download an image.
     */
    private void handleDownloadFailure(Bundle data) {
        // Extract the URL from the message.
        final Uri url =
            ReplyMessage.getImageURL(data);
            
        Utils.showToast(mActivity.get(),
                        "image at " 
                        + url.toString()
                        + " failed to download!");

        // Remove the URL that failed from the UI.
        removeUrl(url.toString());

        if (allDownloadsComplete()) {
            // Dismiss the progress bar.
            mLoadingProgressBar.get().setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Returns true if all the downloads have completed, else false.
     */
    public boolean allDownloadsComplete() {
        return mNumImagesHandled == mNumImagesToHandle
            && mNumImagesHandled > 0;
    }

    /**
     * Returns true if there are any downloads in progress, else false.
     */
    public boolean downloadsInProgress() {
        return mNumImagesToHandle > 0;
    }

    /**
     * Delete all the downloaded images.
     */
    public void deleteDownloadedImages() {
        // Delete all the downloaded image.
        int fileCount = deleteFiles(mDirectoryPathname, 
                                    0);

        // Indicate how many files were deleted.
        Utils.showToast(mActivity.get(),
                        fileCount
                        + " downloaded image"
                        + (fileCount == 1 ? " was" : "s were")
                        + " deleted.");

        // Reset the non-view fields for the next run.
        resetNonViewFields();
    }

    /**
     * A helper method that recursively deletes files in a specified
     * directory.
     */
    private Integer deleteFiles(String directoryPathname,
                                int fileCount) {
        File imageDirectory = new File(directoryPathname);        
        File files[] = imageDirectory.listFiles();

        if (files == null) 
            return fileCount;

        // Android does not allow you to delete a directory with child
        // files, so we need to write code that handles this
        // recursively.
        for (File f : files) {          
            if (f.isDirectory()) 
                fileCount += deleteFiles(f.toString(), 
                                         fileCount);
            Log.d(TAG,
                  "deleting file "
                  + f.toString()
                  + " with count "
                  + fileCount);
            ++fileCount;
            f.delete();
        }

        imageDirectory.delete();
        return fileCount;
    }

    /**
     * Called by the ImageOps constructor and after a runtime
     * configuration change occurs to finish the initialization steps.
     */
    public void onConfigurationChange(MainActivity activity) {
        // Reset the mActivity WeakReference.
        mActivity = new WeakReference<>(activity);

        // If we have a currently active service result handler, allow
        // the handler to update its outdated weak reference to the
        // ServiceResult callback implementation instance.
        if (mServiceResultHandler != null) {
            ((ServiceResultHandler) mServiceResultHandler)
                    .onConfigurationChange(mActivity.get());
        }

        // (Re)initialize all the View fields.
        initializeViewFields();

        // If the content is non-null then we're done, so set the
        // result of the Activity and finish it.
        if (allDownloadsComplete()) {
            // Hide the progress bar.
            mLoadingProgressBar.get().setVisibility(View.INVISIBLE);
            Log.d(TAG,
                  "All images have finished downloading");
        } else if (downloadsInProgress()) {
            // Display the progress bar.
            mLoadingProgressBar.get().setVisibility(View.VISIBLE);

            Log.d(TAG,
                  "Not all images have finished downloading");
        }

        // (Re)display the URLs.
        displayUrls();
    }
}
