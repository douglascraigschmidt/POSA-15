package vandy.mooc;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

/**
 * This is the main Activity that the program uses to start the
 * ThreadedDownloads application.  It allows the user to input the URL
 * of an image and download that image using one of two different
 * Android Bound Service implementations: synchronous and
 * asynchronous.  The Activity starts the Service using bindService().
 * After the Service is started, its onBind() hook method returns an
 * implementation of an AIDL interface to the Activity by
 * asynchronously calling the onServiceConnected() hook method in the
 * Activity.  The AIDL interface object that's returned can then be
 * used to interact with the Service either synchronously or
 * asynchronously, depending on the type of AIDL interface requested.
 * 
 * This class is used the base class for the DownloadActivity.  It
 * instantiates the UI and handles displaying images and getting text
 * from the EditText object by making displayBitmap() and
 * getUrlString() available to subclasses.  This design separates
 * concerns by having DownloadBase handle UI functionality while
 * subclasses (such as DownloadActivity) handle any Service-related
 * communication with the GeoNames Web service.
 * 
 * GeoNamesBase is an example of the Template Method pattern since it
 * extends Activity and overrides its onCreate() hook method.  More
 * generally, any object that extends Activity and overrides its hook
 * methods, such as onStart() or onPause(), is also an example of the
 * Template Method pattern.
 *
 * Starting Bound Services to run synchronously in background Threads
 * from the asynchronous UI Thread is an example of the
 * Half-Sync/Half-Async Pattern.  Starting Bound Services using
 * Intents is an example of the Activator and Command Processor
 * patterns.  The DownloadActivity plays the role of the Creator and
 * creates a Command in the form of an Intent.  The Intent is received
 * by the Service process, which plays the role of the Executor.
 * 
 * The use of AIDL interfaces to pass information between two
 * different processes is an example of the Broker Pattern, in which
 * all communication-related functionality is encapsulated in the AIDL
 * interface and the underlying Android Binder framework, shielding
 * applications from tedious and error-prone aspects of inter-process
 * communication.
 */
public class DownloadImageActivity
       extends LifecycleLoggingActivity
       implements {
    /**
     * This is the reference to the text box that allows the user to
     * input a URL to an image for downloading.
     */
    private EditText mUrlEditText;

    /**
     * This is called when the Activity is initially created. This is
     * where we setup the UI for the activity and initialize any
     * objects that need to exist while the activity exists.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Use the Android framework to create a User Interface for
        // this activity.  The interface that should be created is
        // defined in activity_download.xml in the res/layout folder.
        setContentView(R.layout.download_activity);
        
        // Once the UI is created, get a reference to the instantiated
        // EditText and ImageView objects by providing their ids to
        // the Android framework.
        mUrlEditText = (EditText) findViewById(R.id.url);
    }

    /**
     * This method is called when a user presses a button (see
     * res/layout/download_activity.xml).
     */
    public void runService(View view) {
        Uri uri = Uri.parse(getUrlString());

        Utils.hideKeyboard(this,
                           mUrlEditText.getWindowToken());

    	switch(view.getId()) {
        case R.id.bound_sync_button:
            getOps().downloadImageSync();
            break;

        case R.id.bound_async_button:
            getOps().downloadImageAsync();
            break;
        }
    }
}
