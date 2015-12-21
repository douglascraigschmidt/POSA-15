package vandy.mooc.presenter;

import java.lang.ref.WeakReference;

import vandy.mooc.MVP;
import vandy.mooc.common.GenericAsyncTask;
import vandy.mooc.common.GenericAsyncTaskOps;
import vandy.mooc.common.GenericPresenter;
import vandy.mooc.model.WeatherModel;
import vandy.mooc.model.aidl.WeatherData;
import android.content.Context;
import android.os.Handler;

/**
 * This class implements all the weather-related operations defined in
 * the WeatherPresenter interface.  It implements the various Ops
 * interfaces so it can be created/managed by the GenericActivity
 * framework.  It plays the role of the "Presenter" in the
 * Model-View-Presenter pattern.
 */
public class WeatherPresenter
       extends GenericPresenter<MVP.RequiredPresenterOps,
                                MVP.ProvidedModelOps,
                                WeatherModel>
       implements GenericAsyncTaskOps<String, Void, WeatherData>,
                  MVP.ProvidedPresenterOps,
                  MVP.RequiredPresenterOps {
    /**
     * Debugging tag used by the Android logger.
     */
    protected final static String TAG = 
        WeatherPresenter.class.getSimpleName();

    /**
     * Used to enable garbage collection.
     */
    protected WeakReference<MVP.RequiredViewOps> mView;
    	
    /**
     * The GenericAsyncTask used to obtain the weather in a background
     * thread via the Weather Service web service.
     */
    private GenericAsyncTask<String,
                             Void,
                             WeatherData,
                             WeatherPresenter> mAsyncTask;

    /**
     * This Handler is used to post Runnables to the UI from the
     * mWeatherResults callback methods to avoid a dependency on the
     * Activity, which may be destroyed in the UI Thread during a
     * runtime configuration change.
     */
    private final Handler mDisplayHandler = new Handler();

    /**
     * Location we're trying to get current weather for.
     */
    private String mLocation;

    /**
     * Default constructor.
     */
    public WeatherPresenter() {
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

        // Invoke the special onCreate() method in GenericPresenter,
        // passing in the WeatherModel class to instantiate/manage and
        // "this" to provide WeatherModel with this
        // MVP.RequiredModelOps instance.
        super.onCreate(WeatherModel.class,
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
    }

    /**
     * Hook method called to shutdown the Presenter layer.
     *
     * @param isChangeConfigurations
     *        True if a runtime configuration triggered the onDestroy() call.
     */
    @Override
    public void onDestroy(boolean isChangingConfigurations) {
        // Destroy the model.
        getModel().onDestroy(isChangingConfigurations);
    }

    /**
     * Initiate the asynchronous weather lookup when the user presses
     * the "Look Up Async" button.
     */
    public boolean getWeatherAsync(String location) {
        return getModel().getWeatherAsync(location);
    }

    /**
     * Initiate the synchronous weather lookup when the user presses
     * the "Look Up Sync" button.
     */
    public boolean getWeatherSync(String location) {
        // Check to see if there's already a call in progress.
        if (mAsyncTask != null) 
            return false;
        else {
            // Create and execute the AsyncTask to expand the weather
            // without blocking the caller.
            mAsyncTask = new GenericAsyncTask<>(this);
            mAsyncTask.execute(location);
            return true;
        }
    }

    /**
     * Retrieve the expanded weather results via a synchronous two-way
     * method call, which runs in a background thread to avoid
     * blocking the UI thread.
     */
    public WeatherData doInBackground(String... locations) {
        mLocation = locations[0];
        return getModel().getWeatherSync(mLocation);
    }

    /**
     * Display the results in the UI Thread.
     */
    public void onPostExecute(WeatherData weatherData) {
        mView.get().displayResults(weatherData,
                                   "No weather data for location \""
                                   + mLocation
                                   + "\" found");
        mAsyncTask = null;
    }

    /**
     * Forwards to the View layer to displays the weather data to the
     * user.
     *
     * @param weatherData
     *            WeatherData to display
     */
    public void displayResults(final WeatherData weatherData,
                               final String reason) {
        // Since the Android Binder framework dispatches this method
        // in a separate Thread we need to explicitly post a runnable
        // containing the results to the UI Thread, where it's
        // displayed.
        mDisplayHandler.post(new Runnable() {
                public void run() {
                    mView.get().displayResults(weatherData,
                                               reason);
                }
            });
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
