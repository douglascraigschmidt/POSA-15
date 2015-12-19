package vandy.mooc;

import vandy.mooc.common.ContextView;
import vandy.mooc.common.ModelOps;
import vandy.mooc.common.PresenterOps;
import android.graphics.Bitmap;
import android.net.Uri;

/**
 * Defines the interfaces for the BoundDownload application that are
 * required and provided by the layers in the Model-View-Presenter
 * (MVP) pattern.  This design ensures loose coupling between the
 * layers in the app's MVP-based architecture.
 */
public interface MVP {
    /**
     * This interface defines the minimum API needed by the
     * DownloadPresenter class in the Presenter layer to interact with
     * DownloadImageActivity in the View layer.  It extends the
     * ContextView interface so the Model layer can access Context's
     * defined in the View layer.
     */
    public interface RequiredViewOps
           extends ContextView {
        /**
         * Display an image to the user.
         * 
         * @param image
         *            The bitmap image to display
         */
        void displayImage(Bitmap image);
    }

    /**
     * This interface defines the minimum public API provided by the
     * DownloadPresenter class in the Presenter layer to the
     * DownloadImageActivity in the View layer.  It extends the
     * PresenterOps interface, which is instantiated by the
     * MVP.RequiredViewOps interface used to define the parameter
     * that's passed to the onConfigurationChange() method.
     */
    public interface ProvidedPresenterOps
           extends PresenterOps<MVP.RequiredViewOps> {
        /**
         * Initiate the asynchronous image download.
         */
        boolean downloadImageAsync(Uri uri);

        /**
         * Initiate the synchronous image download.
         */
        boolean downloadImageSync(Uri uri);

        /**
         * Reset the image to the default.
         */
        void resetImage();
    }

    /**
     * This interface defines the minimum API needed by the DownloadModel
     * class in the Model layer to interact with DownloadPresenter class
     * in the Presenter layer.  It extends the ContextView interface
     * so the Model layer can access Context's defined in the View
     * layer.
     */
    public interface RequiredPresenterOps
           extends ContextView {
        /**
         * Forwards to the View layer to display an image.
         * 
         * @param pathToImageFile
         *            The path to the file where the image is stored.
         */
        void displayImage(Uri pathToImageFile);
    }

    /**
     * This interface defines the minimum public API provided by the
     * DownloadModel class in the Model layer to the DownloadPresenter
     * class in the Presenter layer.  It extends the ModelOps
     * interface, which is parameterized by the
     * MVP.RequiredPresenterOps interface used to define the argument
     * passed to the onConfigurationChange() method.
     */
    public interface ProvidedModelOps
           extends ModelOps<MVP.RequiredPresenterOps> {
        /**
         * Initiate the asynchronous image download.
         */
        void downloadImageAsync(Uri uri);

        /**
         * Initiate the synchronous image download.
         */
        void downloadImageSync(Uri uri);
    }
}
