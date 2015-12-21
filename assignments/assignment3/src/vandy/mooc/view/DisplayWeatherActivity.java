package vandy.mooc.view;

import vandy.mooc.R;
import vandy.mooc.common.LifecycleLoggingActivity;
import vandy.mooc.common.Utils;
import vandy.mooc.model.aidl.WeatherData;
import vandy.mooc.utils.WeatherUtils;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * This Activity shows the details of weather for a location provided
 * by the user.  It expects the intent used to start the Activity to
 * contain an extra that holds List of WeatherData objects under the
 * key "KEY_WEATHER_DATA".  Extends LifecycleLoggingActivity so its
 * its lifecycle hook methods are logged automatically.
 */
public class DisplayWeatherActivity
       extends LifecycleLoggingActivity {
    /**
     * Custom Action used by Implicit Intent
     *  to call this Activity.
     */
    public static final String ACTION_DISPLAY_WEATHER = 
        "vandy.mooc.intent.action.DISPLAY_WEATHER";
	
    /**
     * MIME_TYPE of Weather Data
     */
    public static final String TYPE_WEATHER =
        "parcelable/weather";
	
    /**
     * Key for the List of Weather Data to be displayed
     */
    public static final String KEY_WEATHER_DATA =
        "weatherList";
	
    /**
     * Views to hold the Weather Data from Open Weather Map API call.
     */
    TextView mDateView;
    TextView mFriendlyDateView;
    TextView mLocationName;
    TextView mDescriptionView;
    TextView mCelsiusTempView;
    TextView mFarhenheitTempView;
    TextView mHumidityView;
    TextView mWindView;
    TextView mSunriseView;
    TextView mSunsetView;
    ImageView mIconView;
    
    /**
     * Factory method that makes the implicit intent another Activity
     * uses to call this Activity.
     *
     * @param weatherList
     *            List of WeatherData to be displayed.
     */
    public static Intent makeIntent(WeatherData weatherData) {
        // Create an Intent with a custom action to display
        // WeatherData.
        return new Intent(ACTION_DISPLAY_WEATHER)
            // Set MIME_TYPE to display Weather.
            .setType(TYPE_WEATHER)
            // Store the list of WeatherData to send to the
            // DisplayWeatherActivity.
            .putExtra(KEY_WEATHER_DATA, 
                      weatherData);
    }
    
    /**
     * Hook method called when a new instance of Activity is created.
     * One time initialization code goes here, e.g., runtime
     * configuration changes.
     *
     * @param Bundle object that contains saved state information.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Always call super class for necessary
        // initialization/implementation.
        super.onCreate(savedInstanceState);
		
        // Set the content view.
        setContentView(R.layout.display_weather_activity);
		
        // Initialize all the View fields.
        initializeViewFields();

        // Get the intent that started this activity 
        final Intent intent = getIntent();
        
        // Check whether it is correct intent type.
        if (intent.getType().equals(TYPE_WEATHER)) {
            // Get the Weather Data from the Intent.
            final WeatherData weatherData =
                  intent.getParcelableExtra(KEY_WEATHER_DATA);
           
            // The WeatherData is located in the first element of the
            // ArrayList.
            setViewFields(weatherData);
        } else 
            // Show error message.
            Utils.showToast(this,
                            "Incorrect Data");
    }
    
    /**
     * Initialize all the View fields.
     */
    private void initializeViewFields() {
        mIconView =
            (ImageView) findViewById(R.id.detail_icon);
        mDateView =
            (TextView) findViewById(R.id.detail_date_textview);
        mFriendlyDateView =
            (TextView)findViewById(R.id.detail_day_textview);
        mLocationName =
            (TextView)findViewById(R.id.detail_locationName);
        mDescriptionView =
            (TextView)findViewById(R.id.detail_forecast_textview);
        mCelsiusTempView =
            (TextView) findViewById(R.id.detail_high_textview);
        mFarhenheitTempView=
            (TextView) findViewById(R.id.detail_low_textview);
        mHumidityView =
            (TextView)findViewById(R.id.detail_humidity_textview);
        mWindView =
            (TextView) findViewById(R.id.detail_wind_textview);
        mSunriseView =
            (TextView)findViewById(R.id.detail_sunrise_textview);
        mSunsetView =
            (TextView)findViewById(R.id.detail_sunset_textview);
    }

    /**
     * Set all the View fields from the @a weatherData.
     */
    private void setViewFields(WeatherData weatherData) {
        // Get the City and Country Name
        final String locationName = 
            weatherData.getName()
            + ", "
            + weatherData.getSys().getCountry();
            
        // Update view for Location Name
        mLocationName.setText(locationName);
    		
        // Use weather art image given by its weatherId.
        int weatherId = (int) weatherData.getWeathers().get(0).getId();
        mIconView.setImageResource
            (WeatherUtils.getArtResourceForWeatherCondition(weatherId));

        // Get user-friendly date text.
        final String dateText =
            WeatherUtils.formatCurrentDate();

        // Update views for day of week and date.
        mFriendlyDateView.setText("Today");
        mDateView.setText(dateText);
            
        // Read description and update the view.
        final String description =
            weatherData.getWeathers().get(0).getDescription();
        mDescriptionView.setText(description);
           
        // For accessibility, add a content description to the icon
        // field.
        mIconView.setContentDescription(description);
            
        // Read Sunrise time and update the view.
        final String sunriseText = 
            "Sunrise:  " + 
            WeatherUtils.formatTime(weatherData.getSys().getSunrise());
        mSunriseView.setText(sunriseText);
            
        // Read Sunset time and update the view.
        final String sunsetText = 
            "Sunset:  " 
            +
            WeatherUtils.formatTime(weatherData.getSys().getSunset());
        mSunsetView.setText(sunsetText);
             		
        // Read Temperature in Celsius and Farhenheit 
        final double temp = weatherData.getMain().getTemp();
        final String tempCelsius =
            WeatherUtils.formatTemperature(getApplicationContext(),
                                           temp,
                                           false) 
            + "C";
        final String tempFarhenheit = 
            WeatherUtils.formatTemperature(getApplicationContext(),
                                           temp, 
                                           true)
            + "F";

        // Update the Views to display Celsius and Farhenheit
        // Temperature
        mCelsiusTempView.setText(tempCelsius);
        mFarhenheitTempView.setText(tempFarhenheit);
            
        // Read humidity and update the view.
        final float humidity = weatherData.getMain().getHumidity();
        mHumidityView.setText
            (getString(R.string.format_humidity,
                       humidity));

        // Read wind speed and direction and update the view.
        final double windSpeedStr =
            weatherData.getWind().getSpeed();
        final double windDirStr =
            weatherData.getWind().getDeg();
        mWindView.setText
            (WeatherUtils.getFormattedWind(this,
                                           windSpeedStr,
                                           windDirStr));
    }
}
