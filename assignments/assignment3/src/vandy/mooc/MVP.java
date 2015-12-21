package vandy.mooc;

import vandy.mooc.common.ContextView;
import vandy.mooc.common.ModelOps;
import vandy.mooc.common.PresenterOps;
import vandy.mooc.model.aidl.WeatherData;

/**
 * Defines the interfaces for the Download Weather Viewer application
 * that are required and provided by the layers in the
 * Model-View-Presenter (MVP) pattern.  This design ensures loose
 * coupling between the layers in the app's MVP-based architecture.
 */
public interface MVP {
    /**
     * This interface defines the minimum API needed by the
     * WeatherPresenter class in the Presenter layer to interact with
     * DownloadWeathersActivity in the View layer.  It extends the
     * ContextView interface so the Model layer can access Context's
     * defined in the View layer.
     */
    public interface RequiredViewOps
           extends ContextView {
        /**
         * Displays the weather data to the user.
         *
         * @param weatherData
         *            WeatherData to display
         * @param errorReason
         *            Reason that weatherData is null
         */
        void displayResults(WeatherData weatherData,
                            String errorMessage);
    }

    /**
     * This interface defines the minimum public API provided by the
     * WeatherPresenter class in the Presenter layer to the
     * DownloadWeathersActivity in the View layer.  It extends the
     * PresenterOps interface, which is instantiated by the
     * MVP.RequiredViewOps interface used to define the parameter
     * that's passed to the onConfigurationChange() method.
     */
    public interface ProvidedPresenterOps
           extends PresenterOps<MVP.RequiredViewOps> {
       /**
         * Initiate the asynchronous weather lookup when the user
         * presses the "Look Up Async" button.
         */
        boolean getWeatherAsync(String location);

        /**
         * Initiate the synchronous weather lookup when the user
         * presses the "Look Up Sync" button.
         */
        boolean getWeatherSync(String location);
    }

    /**
     * This interface defines the minimum API needed by the WeatherModel
     * class in the Model layer to interact with WeatherPresenter class
     * in the Presenter layer.  It extends the ContextView interface
     * so the Model layer can access Context's defined in the View
     * layer.
     */
    public interface RequiredPresenterOps
           extends ContextView {
        /**
         * Forwards to the View layer to displays the weather data to
         * the user.
         *
         * @param weatherData
         *            WeatherData to display
         * @param errorReason
         *            Reason that weatherData is null
         */
        public void displayResults(WeatherData weatherData,
                                   String errorMessage);
    }

    /**
     * This interface defines the minimum public API provided by the
     * WeatherModel class in the Model layer to the WeatherPresenter
     * class in the Presenter layer.  It extends the ModelOps
     * interface, which is parameterized by the
     * MVP.RequiredPresenterOps interface used to define the argument
     * passed to the onConfigurationChange() method.
     */
    public interface ProvidedModelOps
           extends ModelOps<MVP.RequiredPresenterOps> {
       /**
         * Initiate the asynchronous weather lookup when the user
         * presses the "Look Up Async" button.
         */
        boolean getWeatherAsync(String location);

        /**
         * Initiate the synchronous weather lookup when the user
         * presses the "Look Up Sync" button.
         */
        WeatherData getWeatherSync(String location);
     }
}
