package vandy.mooc;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

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
    private Uri resultUri;
    
    /**
     * Hook method called when a new instance of Activity is created.
     * One time initialization code goes here, e.g., UI layout and
     * some class scope variable initialization.
     *
     * @param savedInstanceState object that contains saved state information.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Always call super class for necessary
        // initialization/implementation.
        // @@ TODO -- you fill in here.
    	super.onCreate(savedInstanceState);
        // Get the URL associated with the Intent data.
        // @@ TODO -- you fill in here.
    	url = getIntent().getData();
        // Download the image in the background, create an Intent that
        // contains the path to the image file, and set this as the
        // result of the Activity.	
    	
    	new Thread(new Runnable() {
			
			@Override
			public void run() {
				resultUri = DownloadUtils.downloadImage(getApplicationContext(), url);
				handler.sendEmptyMessage(0);
			}
			
		}).start();
    	
        // @@ TODO -- you fill in here using the Android "HaMeR"
        // concurrency framework.  Note that the finish() method
        // should be called in the UI thread, whereas the other
        // methods should be called in the background thread.  See
        // http://stackoverflow.com/questions/20412871/is-it-safe-to-finish-an-android-activity-from-a-background-thread
        // for more discussion about this topic.
    }
    
    private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			Intent intent = new Intent();
			intent.setData(resultUri);
			setResult(RESULT_OK, intent);
			finish();
		}
		
	};
}
