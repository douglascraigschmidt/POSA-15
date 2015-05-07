package vandy.mooc.operations;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import vandy.mooc.R;
import vandy.mooc.activities.DisplayImagesActivity;
import vandy.mooc.activities.MainActivity;
import vandy.mooc.services.DownloadImageService;
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
 * This class provides all the image-related operations.
 */
public class ImageOps {
    /**
     * Debugging tag used by the Android logger.
     */
    private final String TAG = getClass().getSimpleName();

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
    WeakReference<MainActivity> mActivity;
    	
    /**
     * EditText field for entering the desired URL to an image.
     */
    private EditText mUrlEditText;

    /**
     * Linear layout to store TextViews displaying URLs.
     */
    private LinearLayout mLinearLayout;

    /**
     * Display progress to the user.
     */
    protected ProgressBar mLoadingProgressBar;

    /**
     * Stores an instance of ServiceResultHandler.
     */
    public Handler mServiceResultHandler = null;
        
    /**
     * Stores the running total number of images downloaded that must
     * be handled by ServiceResultHandler.
     */
    public int mNumImagesToHandle = 0;
    
    /**
     * Stores the running total number of images that have been
     * handled by the ServiceResultHandler.
     */
    public int mNumImagesHandled = 0;
    
    /**
     * Stores the directory to be used for all downloaded images.
     */
    public String mDirectoryPathname = null;
    
    /**
     * Array of Strings that represent the valid URLs that have
     * been entered.
     */
    public ArrayList<String> mUrlList;

    /**
     * Constructor.
     */
    public ImageOps(MainActivity activity) {
        // Initialize the WeakReference.
        mActivity = new WeakReference<>(activity);

        // Initialize the downloadHandler.
        // @@ TODO -- you fill in here.
        mServiceResultHandler = new ServiceResultHandler(mActivity.get());
                
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

        // Store the ProgressBar in a field for fast access.
        mLoadingProgressBar = (ProgressBar)
            mActivity.get().findViewById(R.id.progressBar_loading);
            
        // Cache the EditText that holds the urls entered by the user
        // (if any).
        mUrlEditText = (EditText) mActivity.get().findViewById(R.id.url);

        // Store the linear layout displaying URLs entered.
        mLinearLayout = 
            (LinearLayout) mActivity.get().findViewById(R.id.linearLayout);
    }        

    /**
     * Start all the downloads.
     */
    public void startDownloads() {
        // Hide the keyboard.
        Utils.hideKeyboard(mActivity.get(),
                           mUrlEditText.getWindowToken());

        if (mUrlList.isEmpty())
            Utils.showToast(mActivity.get(),
                            "no images provided");
        else {
            // Make the progress bar visible.
            mLoadingProgressBar.setVisibility(View.VISIBLE);

            // Iterate over each URL and start the download.
            for (String urlString : mUrlList) 
                startDownload(Uri.parse(urlString));
        }
    }

    /**
     * Start a download.
     */
    private void startDownload(Uri url) {
        // Create an intent to download the image.
        Intent intent =
            DownloadImageService.makeIntent(mActivity.get(),
                                            OperationType.DOWNLOAD_IMAGE.ordinal(),
                                            url,
                                            mDirectoryPathname,
                                            mServiceResultHandler);
        // Keep track of number of images downloaded that must
        // be displayed.
        ++mNumImagesToHandle;

        Log.d(TAG,
              "starting the DownloadImageService for "
              + url.toString());

        // Start the service.
        mActivity.get().startService(intent);
    }

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

        if (resultCode == Activity.RESULT_OK) {
            Log.d(TAG,
                  "received image at URI "
                  + DownloadImageService.getImagePathname(data));
                
            // Display all the images that were received successfully.
            downloadSuccess(data);
        } else /* if (resultCode == Activity.RESULT_CANCELED) */ {
            downloadFailure(data);
        }
    }

    /**
     * Launch an Activity to display all the images that were received
     * successfully.
     */
    public void downloadSuccess(Bundle data) {
        // If this is last image handled, display images via
        // DisplayImagesActivity.
        if (allDownloadsComplete()) {
            // Dismiss the progress bar.
            mLoadingProgressBar.setVisibility(View.INVISIBLE);

            // Reset URL list for next time.
            resetUrls();
	
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

    /**
     * Handle failure to download an image.
     */
    public void downloadFailure(Bundle data) {
        // Extract the URL from the message.
        final String url =
            DownloadImageService.getImageURL(data);
            
        Utils.showToast(mActivity.get(),
                        "image at " 
                        + url
                        + " failed to download!");

        // Remove the URL that failed from the UI.
        removeUrl(url);

        if (allDownloadsComplete()) {
            // Dismiss the progress bar.
            mLoadingProgressBar.setVisibility(View.INVISIBLE);
        }
    }

   /**
     * Add whatever URL has been entered into the text field if that
     * URL is valid when user presses the "Add URL" button in UI.
     */
    public void addUrl() {
        // Get the user input (if any).
        final String url = mUrlEditText.getText().toString(); 

    	if (URLUtil.isValidUrl(url)) {
            // Add valid URL to running list for download.
            mUrlList.add
                (mUrlEditText.getText().toString());

            // (Re)display all the URLs.
            displayUrls();
    	} else 
            Utils.showToast(mActivity.get(),
                            "Invalid URL" 
                            + url);
    }

    /**
     * Remove a URL that couldn't be downloaded.
     */
    public void removeUrl(String url) {
        // Remove the invalid URL from the list.
        mUrlList.remove(url);

        // If there are no more downloads pending dismiss the progress
        // bar.
        if (allDownloadsComplete())
            mLoadingProgressBar.setVisibility(View.INVISIBLE);

        // (Re)display the URLs provided by the user thus far.
        displayUrls();
    }

    /**
     * Display the URLs provided by the user thus far.
     */
    private void displayUrls() {
        // First remove all URL views in the parent LinearLayout
        mLinearLayout.removeAllViews();

        // Add a each URL list entry as a text view child of the parent LinearLayout.
        for (String url: mUrlList) {
            TextView urlTextView = new TextView(mActivity.get());
            urlTextView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT));
            urlTextView.setText(url);
            mLinearLayout.addView(urlTextView);
        }

        // Clear the URL input view.
        mUrlEditText.setText("");
    }

    /**
     * Reset the URLs.
     */
    private void resetUrls() {
        mUrlList.clear();
        displayUrls();
    }

    /**
     * Delete all the downloaded images.
     */
    public void deleteDownloadedImages() {
        // Delete all the downloaded image.
        Integer fileCount =
            deleteFiles(mDirectoryPathname, 
                        Integer.valueOf(0));

        // Indicate how many files were deleted.
        Utils.showToast(mActivity.get(),
                        fileCount
                        + " downloaded image(s) were deleted.");

        // Reset the URLs for the next time.
        resetUrls();
    }

    /**
     * A helper method that recursively deletes files in a specified
     * directory.
     */
    private Integer deleteFiles(String directoryPathname,
                                Integer fileCount) {
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
     * Called after a runtime configuration change occurs.
     */
    public void reconfigurationComplete(MainActivity activity) {
        // Reset the mActivity WeakReference.
        mActivity = new WeakReference<>(activity);

        // Store the ProgressBar in a field for fast access.
        mLoadingProgressBar = (ProgressBar)
            mActivity.get().findViewById(R.id.progressBar_loading);
            
        // If the content is non-null then we're done, so set the
        // result of the Activity and finish it.
        if (allDownloadsComplete()) {
            mLoadingProgressBar.setVisibility(View.INVISIBLE);
            Log.d(TAG,
                  "All images have finished downloading");
        } else {
            mLoadingProgressBar.setVisibility(View.VISIBLE);
            Log.d(TAG,
                  "Not all images have finished downloading");
        }
    }

    /**
     * Returns true if all the downloads have completed, else false.
     */
    public boolean allDownloadsComplete() {
        return mNumImagesHandled == mNumImagesToHandle
            && mNumImagesHandled > 0;
    }
}
