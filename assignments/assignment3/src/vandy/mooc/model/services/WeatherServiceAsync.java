package vandy.mooc.model.services;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import vandy.mooc.model.aidl.WeatherData;
import vandy.mooc.model.aidl.WeatherRequest;
import vandy.mooc.model.aidl.WeatherResults;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

/**
 * This class uses asynchronous AIDL interactions to expand weather
 * via a Weather Web service.  The client that binds to this Service
 * will receive an IBinder that's an instance of WeatherRequest, which
 * extends IBinder.  The client can then interact with this Service by
 * making one-way method calls on the WeatherRequest object asking
 * this Service to lookup the current Weather for a designated
 * location, passing in an WeatherResults object and the weather
 * location as a string.  After the lookup finishes successfully, this
 * Service sends the Weather results back to the client by invoking
 * sendResults() on the WeatherResults object.  An unsuccessful lookup
 * will invoke sendError() on the WeatherResults object.
 * 
 * AIDL is an example of the Broker Pattern, in which all interprocess
 * communication details are hidden behind the AIDL interfaces.
 */
public class WeatherServiceAsync 
       extends WeatherServiceBase {
    /**
     * Reference to the ExecutorService that manages a pool of
     * threads.  We need this feature since Android's Binder framework
     * apparently executes oneway methods from a client in a single
     * thread rather than a pool of thread.
     */
    private ExecutorService mExecutorService;

    /**
     * Factory method that makes an explicit intent used to start the
     * WeatherServiceAsync when passed to bindService().
     * 
     * @param context
     *            The context of the calling component.
     */
    public static Intent makeIntent(Context context) {
        // TODO -- you fill in here.
    }
    
    /**
     * Hook method called when the Service is created.
     */
    @Override
    public void onCreate() {
        // Call up to the super onCreate() method to perform its
        // initialization operations.
        super.onCreate();

        // Create an ExecutorService that manages a pool of threads.
        mExecutorService = Executors.newCachedThreadPool();
    }

    /**
     * Hook method called when the last client unbinds from the
     * Service.
     */
    @Override
    public void onDestroy() {
        // Immediately shutdown the ExecutorService.
        mExecutorService.shutdownNow(); 

        // Call up to the super onCreate() method to perform its
        // destruction operations.
        super.onDestroy();
    }

    /**
     * Called when a client calls bindService() with the proper
     * Intent.  Returns the implementation of WeatherRequest, which is
     * implicitly cast as an IBinder.
     */
    @Override
     public IBinder onBind(Intent intent) {
        return mWeatherRequestImpl;
    }

    /**
     * The concrete implementation of the AIDL Interface
     * WeatherRequest, which extends the Stub class that implements
     * WeatherRequest, thereby allowing Android to handle calls across
     * process boundaries.  This method runs in a separate Thread as
     * part of the Android Binder framework.
     * 
     * This implementation plays the role of Invoker in the Broker
     * Pattern.
     */
    private final WeatherRequest.Stub mWeatherRequestImpl =
        new WeatherRequest.Stub() {
            /**
             * Implement the AIDL WeatherRequest getCurrentWeather()
             * method, which forwards to getWeatherResults() to obtain
             * the results and then sends these results back to the
             * client via the callback.
             */
            @Override
            public void getCurrentWeather(final String location,
                                          final WeatherResults callback) {
                // TODO -- you fill in here.
            }
        };
}
