package vandy.mooc.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import vandy.mooc.R;
import android.content.Context;

/**
 * WeatherUtils contains helper methods that properly format the WeatherData
 * POJO and display it to the user.
 */
public class WeatherUtils {
    /**
     * Logging tag.
     */
    private final static String TAG =
        WeatherUtils.class.getCanonicalName();

    /**
     * Helper method to format the Temprature returned by the
     * OpenWeatherMap call. The default is Celsius.
     *
     * @param Application Context
     * @param temprature from OpenWeatherMap API response
     * @param isFarhenheit
     * @return formatted Temprature
     */
    public static String formatTemperature(Context context,
                                           double temperature,
                                           boolean isFarhenheit) {
        if (isFarhenheit) 
            // Conversion of Kelvin to Frahenheit temperature.
            temperature = 1.8*(temperature - 273) + 32;
        else 
            // Conversion of Kelvin to Celsius temperature.
            temperature = temperature - 273;
	    	
        return String.format(context.getString(R.string.format_temperature),
                             temperature);
    }

    /**
     * Helper method to format the Date returned by the OpenWeatherMap call.
     * @return formatted Current Date String
     */
    public static String formatCurrentDate() {
        SimpleDateFormat sdf =
            new SimpleDateFormat("MMM  dd ");	
        Calendar c = Calendar.getInstance();
        return sdf.format(c.getTime());
    }
    
    /**
     * Helper method to format the Sunsrise and Sunset time returned
     * by the OpenWeatherMap call.
     *
     * @param Time in Seconds.
     * @return formatted Time String
     */
    public static String formatTime(long time) {
        SimpleDateFormat sdf =
        		new SimpleDateFormat("h:mm a");	
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time*1000);
        return sdf.format(c.getTime());
    }
    
    /**
     * Helper method to format the Wind returned by the OpenWeatherMap call.
     *
     * @param Application Context
     * @param Wind Speed from OpenWeatherMap API response
     * @param Wind Direction from OpenWeatherMap API response
     * @return formatted String of Wind Data
     */
    public static String getFormattedWind(Context context,
                                          double windSpeedStr,
                                          double windDirStr) {
        int windFormat;
        windFormat = R.string.format_wind_kmh;
	        
        // From wind direction in degrees, determine compass direction
        // as a string (e.g., NW).
        String direction = "Unknown";
        if (windDirStr >= 337.5 || windDirStr < 22.5) 
            direction = "N";
        else if (windDirStr >= 22.5 && windDirStr < 67.5) 
            direction = "NE";
        else if (windDirStr >= 67.5 && windDirStr < 112.5) 
            direction = "E";
        else if (windDirStr >= 112.5 && windDirStr < 157.5) 
            direction = "SE";
        else if (windDirStr >= 157.5 && windDirStr < 202.5) 
            direction = "S";
        else if (windDirStr >= 202.5 && windDirStr < 247.5)
            direction = "SW";
        else if (windDirStr >= 247.5 && windDirStr < 292.5) 
            direction = "W";
        else if (windDirStr >= 292.5 && windDirStr < 337.5) 
            direction = "NW";

        return String.format(context.getString(windFormat),
                             windSpeedStr,
                             direction);
    }

    /**
     * Helper method to provide the art resource id according to the
     * weather condition id returned by the OpenWeatherMap call.
     *
     * @param weatherId from OpenWeatherMap API response
     * @return resource id for the corresponding icon. -1 if no relation is found.
     */
    public static int getArtResourceForWeatherCondition(int weatherId) {
        // Based on weather code data found at:
        // http://bugs.openweathermap.org/projects/api/wiki/Weather_Condition_Codes
        if (weatherId >= 200 && weatherId <= 232) 
            return R.drawable.art_storm;
        else if (weatherId >= 300 && weatherId <= 321) 
            return R.drawable.art_light_rain;
        else if (weatherId >= 500 && weatherId <= 504) 
            return R.drawable.art_rain;
        else if (weatherId == 511) 
            return R.drawable.art_snow;
        else if (weatherId >= 520 && weatherId <= 531) 
            return R.drawable.art_rain;
        else if (weatherId >= 600 && weatherId <= 622) 
            return R.drawable.art_snow;
        else if (weatherId >= 701 && weatherId <= 761) 
            return R.drawable.art_fog;
        else if (weatherId == 761 || weatherId == 781) 
            return R.drawable.art_storm;
        else if (weatherId == 800) 
            return R.drawable.art_clear;
        else if (weatherId == 801) 
            return R.drawable.art_light_clouds;
        else if (weatherId >= 802 && weatherId <= 804) 
            return R.drawable.art_clouds;

        return -1;
    }
    
    /**
     * Make WeatherUtils a utility class by preventing instantiation.
     */
    private WeatherUtils() {
        throw new AssertionError();
    }
}


