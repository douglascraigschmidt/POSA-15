package vandy.mooc;

import java.net.URI;

import vandy.mooc.common.ContextView;
import vandy.mooc.common.ModelOps;
import vandy.mooc.common.PresenterOps;
import android.graphics.Bitmap;

/**
 * Defines the interfaces for the ThreadedDownloads application that
 * are required and provided by the layers in the Model-View-Presenter
 * (MVP) pattern.  This design ensures loose coupling between the
 * layers in the app's MVP-based architecture.
 */
public interface MVP {
    /**
     * This interface defines the minimum API needed by the
     * ImageDownloadsPresenter class in the Presenter layer to
     * interact with ImageDownloadsActivity in the View layer.  It
     * extends the ContextView interface so the Presentation layer can
     * access Context's defined in the View layer.
     */
    public interface RequiredViewOps
           extends ContextView {
        /**
         * Display a downloaded bitmap image if it's non-null;
         * otherwise, it reports an error via a Toast that's displayed
         * on the UI Thread.  This method can be called from either
         * the UI Thread or a background Thread.
         * 
         * @param image
         *            The bitmap image
         * @param completionCommand
         *            Command whose run() hook method is called after the
         *            image is displayed.
         */
        void displayBitmap(final Bitmap image,
                           Runnable completionCommand);
    }

    /**
     * This interface defines the minimum public API provided by the
     * ImageDownloadsPresenter class in the Presenter layer to the
     * AcronymExpansionActivity in the View layer.  It extends the
     * PresenterOps interface, which is instantiated by the
     * MVP.RequiredViewOps interface used to define the parameter
     * that's passed to the onConfigurationChange() method.
     */
    public interface ProvidedPresenterOps
           extends PresenterOps<MVP.RequiredViewOps> {
        /**
         * Called when a user clicks a button to download an image.
         * 
         * @param buttonResId
         *            Indicates the button pressed by the user.
         * @param url
         *            URL givenby the user.
         */
        void handleButtonClick(int buttonResId,
                               String url);
    }

    /**
     * This interface defines the minimum API needed by the
     * ImageDownloadsModel class in the Model layer to interact with
     * ImageDownloadsPresenter class in the Presenter layer.
     */
    public interface RequiredPresenterOps            
           extends RequiredViewOps {
        /**
         * Set the current image.
         */
        void setCurrentImage(Bitmap image);

        /**
         * Reset bitmap display on the user's screen to the default image.
         */
        void resetBitmap();

        /**
         * Display a downloaded bitmap image if it's non-null;
         * otherwise, it reports an error via a Toast that's displayed
         * on the UI Thread.  This method can be called from either
         * the UI Thread or a background Thread.
         * 
         * @param image
         *            The bitmap image
         * @param completionCommand
         *            Command whose run() hook method is called after the
         *            image is displayed.
         */
        void displayBitmap(final Bitmap image,
                           Runnable completionCommand);
    }

    /**
     * This interface defines the minimum public API provided by the
     * ImageDownloadsModel class in the Model layer to the
     * ImageDownloadsPresenter class in the Presenter layer.  It
     * extends the ModelOps interface, which is parameterized by the
     * MVP.RequiredPresenterOps interface used to define the argument
     * passed to the onConfigurationChange() method.
     */
    public interface ProvidedModelOps
           extends ModelOps<MVP.RequiredPresenterOps> {
        /**
         * Download a bitmap image from the URL provided by the user.
         * 
         * @param url
         *            The url where a bitmap image is located
         *
         * @return the image bitmap or null if there was an error
         */
        Bitmap downloadBitmap(String url);
    }
}
