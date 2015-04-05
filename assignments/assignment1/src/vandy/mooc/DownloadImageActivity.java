package vandy.mooc;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

/**
 * An Activity that downloads an image, stores it in a local file on the local device, and returns a Uri to the image
 * file.
 */
public class DownloadImageActivity extends Activity {

	public static final String IMAGE_PATH = "IMAGE_PATH";

	public static final String IMAGE_URL = "IMAGE_URL";

	/**
	 * Debugging tag used by the Android logger.
	 */
	private final String TAG = getClass().getSimpleName();

	/**
	 * Hook method called when a new instance of Activity is created. One time initialization code goes here, e.g., UI
	 * layout and some class scope variable initialization.
	 *
	 * @param savedInstanceState
	 *            object that contains saved state information.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// Always call super class for necessary
		// initialization/implementation.
		super.onCreate(savedInstanceState);

		// Get the URL associated with the Intent data.
		final Uri uri = getIntent().getParcelableExtra(IMAGE_URL);

		//  -- you fill in here using the Android "HaMeR"
		// concurrency framework.  Note that the finish() method
		// should be called in the UI thread, whereas the other
		// methods should be called in the background thread.  See
		// http://stackoverflow.com/questions/20412871/is-it-safe-to-finish-an-android-activity-from-a-background-thread
		// for more discussion about this topic.

		new Thread() {

			@Override
			public void run() {
				Uri path = DownloadUtils.downloadImage(DownloadImageActivity.this, uri);
				Intent result = new Intent();
				if (path != null) {
					result.putExtra(IMAGE_PATH, path);
					setResult(RESULT_OK, result);
				} else {
					setResult(RESULT_CANCELED, result);
				}
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						finish();
					}
				});
			}
		}.start();

	}
}
