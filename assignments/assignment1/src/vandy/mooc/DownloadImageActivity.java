package vandy.mooc;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

/**
 * An Activity that downloads an image, stores it in a local file on
 * the local device, and returns a Uri to the image file.
 */
public class DownloadImageActivity extends Activity {
    /**
     * Debugging tag used by the Android logger.
     */
    private final String TAG = getClass().getSimpleName();

    private Uri url;
    private Uri downloadedImage;

    /**
     * Hook method called when a new instance of Activity is created.
     * One time initialization code goes here, e.g., UI layout and
     * some class scope variable initialization.
     *
     * @param Bundle object that contains saved state information.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Always call super class for necessary
        // initialization/implementation.
        super.onCreate(savedInstanceState);

        // Get the URL associated with the Intent data.
        url = getIntent().getData();
        Log.d(TAG, "onCreate url: " + url.toString());

        // Download the image in the background, create an Intent that
        // contains the path to the image file, and set this as the
        // result of the Activity.

        // Note that the finish() method
        // should be called in the UI thread, whereas the other
        // methods should be called in the background thread.
        Runnable downloadRunnable = new Runnable() {

            @Override
            public void run() {
                Log.d(TAG, "DownloadImageActivity Runnable run url: " + url);
                // Download the image and save in a file
                downloadedImage = DownloadUtils.downloadImage(getApplicationContext(), url);

                runOnUiThread(new Runnable() {
                    public void run() {
                        Log.d(TAG, "Image downloaded");
                        int result = (downloadedImage != null) ? Activity.RESULT_OK : Activity.RESULT_CANCELED;

                        // Set the activity result
                        Intent resultIntent = new Intent();
                        resultIntent.setData(downloadedImage);
                        setResult(result, resultIntent);

                        // Finish activity
                        finish();
                    }});
            }
        };

        Thread downloadThread = new Thread(downloadRunnable);
        downloadThread.start();


    }
}
