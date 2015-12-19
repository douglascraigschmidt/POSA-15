package vandy.mooc.model.services;

import java.util.ArrayList;
import java.util.List;

import vandy.mooc.model.aidl.WeatherCall;
import vandy.mooc.model.aidl.WeatherData;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

/**
 * This class uses synchronous AIDL interactions to expand Weathers
 * via an Weather Web service.  The client that binds to this Service
 * will receive an IBinder that's an instance of WeatherRequest, which
 * extends IBinder.  The client can then interact with this Service by
 * making two-way method calls on the WeatherCall object asking this
 * to lookup the current Weather for a designated location.  After the
 * lookup is finishes successfully, this Service sends the Weather
 * results back to the Activity by returning a List of WeatherData.
 * An unsuccessful lookup will return a zero-sized List of
 * WeatherData.
 * 
 * AIDL is an example of the Broker Pattern, in which all interprocess
 * communication details are hidden behind the AIDL interfaces.
 */
public class WeatherServiceSync 
       extends WeatherServiceBase {
    /**
     * Factory method that makes an explicit intent used to start the
     * WeatherServiceSync when passed to bindService().
     * 
     * @param context
     *            The context of the calling component.
     */
    public static Intent makeIntent(Context context) {
        // TODO -- you fill in here.
    }

    /**
     * Called when a client calls bindService() with the proper
     * Intent.  Returns the implementation of WeatherCall, which is
     * implicitly cast as an IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mWeatherCallImpl;
    }

    /**
     * The concrete implementation of the AIDL Interface WeatherCall,
     * which extends the Stub class that implements WeatherCall,
     * thereby allowing Android to handle calls across process
     * boundaries.  This method runs in a separate Thread as part of
     * the Android Binder framework.
     * 
     * This implementation plays the role of Invoker in the Broker
     * Pattern.
     */
    private final WeatherCall.Stub mWeatherCallImpl =
        new WeatherCall.Stub() {
            /**
             * Implement the AIDL WeatherCall getCurrentWeather()
             * method, which forwards to getWeatherResults() to obtain
             * results and then returns the results to the client.
             */
            @Override
            public List<WeatherData> getCurrentWeather(String location)
                throws RemoteException {

                // TODO -- you fill in here.
        };
}
