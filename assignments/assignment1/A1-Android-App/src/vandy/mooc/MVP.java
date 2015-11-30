package vandy.mooc;

import java.util.ArrayList;

import vandy.mooc.common.ContextView;
import vandy.mooc.common.ModelOps;
import vandy.mooc.common.PresenterOps;
import vandy.mooc.model.ImageModel;
import vandy.mooc.model.datamodel.ReplyMessage;
import android.net.Uri;

/**
 * Defines the interfaces for the Download Image Viewer application
 * that are required and provided by the layers in the
 * Model-View-Presenter (MVP) pattern.  This design ensures loose
 * coupling between the layers in the app's MVP-based architecture.
 */
public interface MVP {
    /**
     * This interface defines the minimum API needed by the
     * ImagePresenter class in the Presenter layer to interact with
     * DownloadImagesActivity in the View layer.  It extends the
     * ContextView interface so the Model layer can access Context's
     * defined in the View layer.
     */
    public interface RequiredViewOps
           extends ContextView {
        /**
         * Make the ProgressBar visible.
         */
        void displayProgressBar();

        /**
         * Make the ProgressBar invisible.
         */
        void dismissProgressBar();

        /**
         * Display the URLs provided by the user thus far.
         */
        void displayUrls();

        /**
         * Handle failure to download an image at @a url.
         */
        void reportDownloadFailure(Uri url,
                                   boolean downloadsComplete);

        /**
         * Start the DisplayImagesActivity to display the results of
         * the download to the user.
         */
        void displayResults(Uri directoryPathname);
    }

    /**
     * This interface defines the minimum public API provided by the
     * ImagePresenter class in the Presenter layer to the
     * DownloadImagesActivity in the View layer.  It extends the
     * PresenterOps interface, which is instantiated by the
     * MVP.RequiredViewOps interface used to define the parameter
     * that's passed to the onConfigurationChange() method.
     */
    public interface ProvidedPresenterOps
           extends PresenterOps<MVP.RequiredViewOps> {
        /**
         * Get the list of URLs.
         */
        ArrayList<Uri> getUrlList();

        /**
         * Start all the downloads and processing. 
         */
        void startProcessing();

        /**
         * Delete all the downloaded images.
         */
        void deleteDownloadedImages();

        /**
         * Return the initialized ProvidedModelOps instance for use by
         * the application.
         */
        MVP.ProvidedModelOps getModel();
    }

    /**
     * This interface defines the minimum API needed by the ImageModel
     * class in the Model layer to interact with ImagePresenter class
     * in the Presenter layer.  It extends the ContextView interface
     * so the Model layer can access Context's defined in the View
     * layer.
     */
    public interface RequiredPresenterOps
           extends ContextView {
        /**
         * Interact with the View layer to display the downloaded
         * images when they are all returned from the Model.
         */
        void onDownloadComplete(ReplyMessage replyMessage);
    }

    /**
     * This interface defines the minimum public API provided by the
     * ImageModel class in the Model layer to the ImagePresenter class
     * in the Presenter layer.  It extends the ModelOps interface,
     * which is parameterized by the MVP.RequiredPresenterOps
     * interface used to define the argument passed to the
     * onConfigurationChange() method.
     */
    public interface ProvidedModelOps
           extends ModelOps<MVP.RequiredPresenterOps> {
        /**
         * Start a download.  When the download finishes its results
         * are passed up to the Presentation layer via the
         * onDownloadComplete() method defined in
         * RequiredPresenterOps.
         *
         * @param url
         *        URL of the image to download.
         * @param directoryPathname
         *        Uri of the directory to store the downloaded image.
         */
        void startDownload(Uri url,
                           Uri directoryPathname);

        /**
         * Set the type of Service to use for the ImageModel
         * implementation.
         *
         * @param serviceType 
         *            Type of Service, i.e., STARTED_SERVICE
         */
        void setServiceType(ImageModel.ServiceType serviceType);
    }
}
