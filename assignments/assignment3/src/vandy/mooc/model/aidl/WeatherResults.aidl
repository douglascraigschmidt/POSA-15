package vandy.mooc.model.aidl;

import vandy.mooc.model.aidl.WeatherData;
import java.util.List;

/**
 * Interface defining the method that receives callbacks from the
 * WeatherServiceAsync.  This method should be implemented by the
 * WeatherActivity.
 */
interface WeatherResults {
    /**
     * This one-way (non-blocking) method allows WeatherServiceAsync
     * to return the WeatherData results associated with a one-way
     * WeatherRequest.getCurrentWeather() call.
     */
    oneway void sendResults(in WeatherData results);

    /**
     * This one-way (non-blocking) method allows WeatherServiceAsync
     * to return an error String if the Service fails for some reason.
     */
    oneway void sendError(in String reason);
}
