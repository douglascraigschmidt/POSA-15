package vandy.mooc.model.aidl;

import vandy.mooc.model.aidl.WeatherResults;

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
    oneway void getCurrentWeather(in String location,
                                  in WeatherResults results); 
}
