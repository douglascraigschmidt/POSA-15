package vandy.mooc.view;

import vandy.mooc.R;
import vandy.mooc.MVP;
import vandy.mooc.common.GenericActivity;
import vandy.mooc.common.Utils;
import vandy.mooc.model.aidl.WeatherData;
import vandy.mooc.presenter.WeatherPresenter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

/**
 * The main Activity that prompts the user for a location and then
 * displays WeatherData about this location via either retrieving the
 * WeatherData from a ContentProvider-based cache or from the Weather
 * Service web service via the use of Retrofit.  It plays the role of
 * the "View" in the Model-View-Presenter (MVP) pattern.  It extends
 * GenericActivity that provides a framework to automatically handle
 * runtime configuration changes of an WeatherPresenter object, which
 * plays the role of the "Presenter" in the MVP pattern.  The
 * MPV.RequiredViewOps and MVP.ProvidedPresenterOps interfaces are
 * used to minimize dependencies between the View and Presenter
 * layers.
 */
public class DownloadWeatherActivity
       extends GenericActivity<MVP.RequiredViewOps,
                               MVP.ProvidedPresenterOps,
                               WeatherPresenter>
       implements MVP.RequiredViewOps {
    /**
     * Weather location entered by the user.
     */
    protected EditText mEditText;

    /**
     * Hook method called when a new instance of Activity is created.
     * One time initialization code goes here, e.g., initializing
     * views.
     *
     * @param Bundle object that contains saved state information.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Perform first part of initializing the super class.
        super.onCreate(savedInstanceState);

        // Get references to the UI components.
        setContentView(R.layout.download_weather_activity);

        // Store the EditText that holds the urls entered by the user
        // (if any).
        mEditText = ((EditText) findViewById(R.id.locationQuery));
        
        // Perform second part of initializing the super class,
        // passing in the WeatherPresenter class to instantiate/manage
        // and "this" to provide WeatherPresenter with the
        // MVP.RequiredViewOps instance.
        super.onCreate(WeatherPresenter.class,
                       this);
    }

    /**
     * Hook method called by Android when this Activity becomes is
     * destroyed.
     */
    @Override
    protected void onDestroy() {
        // Destroy the presenter layer, passing in whether this is
        // triggered by a runtime configuration or not.
        getPresenter().onDestroy(isChangingConfigurations());

        // Always call super class for necessary operations when
        // stopping.
        super.onDestroy();
    }

    /*
     * Initiate the synchronous weather lookup when the user presses
     * the "Get Weather Sync" button.
     */
    public void getWeatherSync(View v) {
        // Hide the keyboard.
        Utils.hideKeyboard(this,
                           mEditText.getWindowToken());

        // Get the location entered by the user.
        final String location =
            Utils.uppercaseInput(this,
                                 mEditText.getText().toString().trim(),
                                 true);
        if (location != null) {
            // Synchronously get the weather for the location.
            if (getPresenter().getWeatherSync(location) == false)
                // Show error message to user.
                Utils.showToast(this,
                                "Call already in progress");

            // Return focus to edit box and select all text in it
            // after query.
            mEditText.requestFocus();
            mEditText.selectAll();
        }
    }

    /*
     * Initiate the asynchronous weather lookup when the user presses
     * the "Get Weather Async" button.
     */
    public void getWeatherAsync(View v) {
        // Hide the keyboard.
        Utils.hideKeyboard(this,
                           mEditText.getWindowToken());

        // Get the location entered by the user.
        final String location =
            Utils.uppercaseInput(this,
                                 mEditText.getText().toString().trim(),
                                 true);
        if (location != null) {
            // Asynchronously get the weather for the location.
            if (getPresenter().getWeatherAsync(location) == false)
                // Show error message to user.
                Utils.showToast(this,
                                "Call already in progress");

            // Return focus to edit box and select all text in it
            // after query.
            mEditText.requestFocus();
            mEditText.selectAll();
        }
    }

    /**
     * Displays the weather data to the user.
     *
     * @param weatherData
     *            WeatherData to display
     * @param errorReason
     *            Reason that weatherData is null
     */
    public void displayResults(WeatherData weatherData,
                               String errorMessage) {
        // Only display the results if we got valid WeatherData.
        if (weatherData == null) 
            Utils.showToast(this,
                            errorMessage);
        else {
            // Create an intent that will start an Activity to display
            // the WeatherData to the user.
            final Intent intent = DisplayWeatherActivity.makeIntent
                ((WeatherData) weatherData);
       
            // Verify that the intent will resolve to an Activity.
            if (intent.resolveActivity(getPackageManager()) != null)
                // Start the DisplayWeatherActivity with this implicit
                // intent.
                startActivity(intent);
            else
                // Show error message to user.
                Utils.showToast(this,
                                "No Activity found to display Weather Data");
        }
    }
}
