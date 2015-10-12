package vandy.mooc.model;

import java.lang.ref.WeakReference;

import vandy.mooc.MVP;
import vandy.mooc.common.Utils;
import android.graphics.Bitmap;

/**
 * This class plays the "Model" role in the Model-View-Presenter (MVP)
 * pattern by defining an interface for providing data that will be
 * acted upon by the "Presenter" and "View" layers in the MVP pattern.
 * It implements the MVP.ProvidedModelOps so it can be created/managed
 * by the GenericPresenter framework.
 */
public class ImageDownloadsModel 
       implements MVP.ProvidedModelOps {
    /**
     * Debugging tag used by the Android logger.
     */
    protected final static String TAG = 
        ImageDownloadsModel.class.getSimpleName();

    /**
     * Default URL to download.
     */
    private final String mDefaultURL = 
        "http://www.dre.vanderbilt.edu/~schmidt/ka.png";

    /**
     * A WeakReference used to access methods in the Presenter layer.
     * The WeakReference enables garbage collection.
     */
    private WeakReference<MVP.RequiredPresenterOps> mPresenter;

    /**
     * Hook method called when a new instance of AcronymModel is
     * created.  One time initialization code goes here, e.g., storing
     * a WeakReference to the Presenter and initializing the sync and
     * async Services.
     * 
     * @param presenter
     *            A reference to the Presenter layer.
     */
    @Override
    public void onCreate(MVP.RequiredPresenterOps presenter) {
        // Set the WeakReference.
        mPresenter =
            new WeakReference<>(presenter);
    }

    /**
     * Hook method called to shutdown the Model layer.
     *
     * @param isChangeConfigurations
     *        True if a runtime configuration triggered the onDestroy() call.
     */
    @Override
    public void onDestroy(boolean isChangingConfigurations) {
        // No-op.
    }

    /**
     * Download an image file from the URL provided by the user and
     * decode into a Bitmap.
     * 
     * @param url
     *            The url where a bitmap image is located
     *
     * @return the image bitmap or null if there was an error
     */
    public Bitmap downloadBitmap(String url) {
        //  Use the default URL if the user doesn't supply one.
        if (url.equals(""))
            url = mDefaultURL;
        return Utils.downloadAndDecodeImage(url);
    }
}
