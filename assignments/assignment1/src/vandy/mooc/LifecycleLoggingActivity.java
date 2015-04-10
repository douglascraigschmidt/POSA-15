package vandy.mooc;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

/**
 * This abstract class extends the Activity class and overrides
 * lifecycle callbacks for logging various lifecycle events.
 */
public abstract class LifecycleLoggingActivity extends Activity {
    /**
     * Debugging tag used by the Android logger.
     */
    private final String TAG = getClass().getSimpleName();

    /**
     * Hook method called when a new instance of Activity is created.
     * One time initialization code should go here e.g. UI layout,
     * some class scope variable initialization.  if finish() is
     * called from onCreate no other lifecycle callbacks are called
     * except for onDestroy().
     *
     * @param savedInstanceState object that contains saved state information.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState){
        // Always call super class for necessary
        // initialization/implementation.
        super.onCreate(savedInstanceState);
		
        if(savedInstanceState != null) {
            // The activity is being re-created. Use the
            // savedInstanceState bundle for initializations either
            // during onCreate or onRestoreInstanceState().
            Log.d(TAG,
                  "onCreate(): activity re-created from savedInstanceState");
						
        } else {
            // Activity is being created anew.  No prior saved
            // instance state information available in Bundle object.
            Log.d(TAG,
                  "onCreate(): activity created anew");
        }
		
    }
	
    /**
     * Hook method called after onCreate() or after onRestart() (when
     * the activity is being restarted from stopped state).  Should
     * re-acquire resources relinquished when activity was stopped
     * (onStop()) or acquire those resources for the first time after
     * onCreate().
     */	
    @Override
    protected void onStart(){
        // Always call super class for necessary
        // initialization/implementation.
        // TODO - you fill in here.
    	super.onStart();
    	Log.d(TAG, "onStart()");
    }
	
    /**
     * Hook method called after onRestoreStateInstance(Bundle) only if
     * there is a prior saved instance state in Bundle object.
     * onResume() is called immediately after onStart().  onResume()
     * is called when user resumes activity from paused state
     * (onPause()) User can begin interacting with activity.  Place to
     * start animations, acquire exclusive resources, such as the
     * camera.
     */
    @Override
    protected void onResume(){
        // Always call super class for necessary
        // initialization/implementation and then log which lifecycle
        // hook method is being called.
        // TODO - you fill in here.
    	super.onResume();
    	Log.d(TAG, "onResume()");
    }
	
    /**
     * Hook method called when an Activity loses focus but is still
     * visible in background. May be followed by onStop() or
     * onResume().  Delegate more CPU intensive operation to onStop
     * for seamless transition to next activity.  Save persistent
     * state (onSaveInstanceState()) in case app is killed.  Often
     * used to release exclusive resources.
     */
    @Override
    protected void onPause(){
        // Always call super class for necessary
        // initialization/implementation and then log which lifecycle
        // hook method is being called.
        // TODO - you fill in here.
    	super.onPause();
    	Log.d(TAG, "onPause()");
    }
	
    /**
     * Called when Activity is no longer visible.  Release resources
     * that may cause memory leak. Save instance state
     * (onSaveInstanceState()) in case activity is killed.
     */
    @Override
    protected void onStop(){
        // Always call super class for necessary
        // initialization/implementation and then log which lifecycle
        // hook method is being called.
        // TODO - you fill in here.
    	super.onStop();
    	Log.d(TAG, "onStop()");
    }
	
    /**
     * Hook method called when user restarts a stopped activity.  Is
     * followed by a call to onStart() and onResume().
     */	
    @Override
    protected void onRestart(){
        // Always call super class for necessary
        // initialization/implementation and then log which lifecycle
        // hook method is being called.
        // TODO - you fill in here.
    	super.onRestart();
    	Log.d(TAG, "onRestart()");
    }
	
    /**
     * Hook method that gives a final chance to release resources and
     * stop spawned threads.  onDestroy() may not always be
     * called-when system kills hosting process
     */
    @Override
    protected void onDestroy(){
        // Always call super class for necessary
        // initialization/implementation and then log which lifecycle
        // hook method is being called.
        // TODO - you fill in here.
    	super.onDestroy();
    	Log.d(TAG, "onDestroy()");
    }
}
