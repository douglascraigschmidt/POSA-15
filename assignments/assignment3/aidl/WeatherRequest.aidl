package vandy.mooc.aidl;

import vandy.mooc.aidl.WeatherResults;

/**
 * Interface defining the method implemented within
 * WeatherServiceAsync that provides asynchronous access to the
 * Weather Service web service.
 */
interface WeatherRequest {
   /**
    * A one-way (non-blocking) call to the WeatherServiceAsync that
    * retrieves information about the current weather from the Weather
    * Service web service.  WeatherServiceAsync subsequently uses the
    * WeatherResults parameter to return a List of WeatherData
    * containing the results from the Weather Service web service back
    * to the WeatherActivity via the one-way sendResults() method.
    */
    oneway void getCurrentWeather(in String Weather,
                                  in WeatherResults results); 
}
