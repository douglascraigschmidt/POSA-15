package vandy.mooc.model;

import java.lang.ref.WeakReference;

import vandy.mooc.MVP;
import android.net.Uri;

/**
 * This class plays the "Model" role in the Model-View-Presenter (MVP)
 * pattern by defining an interface for providing data that will be
 * acted upon by the "Presenter" and "View" layers in the MVP pattern.
 * It implements the MVP.ProvidedModelOps so it can be created/managed
 * by the GenericModel framework.  This class plays the role of the
 * "Abstraction" in the Bridge pattern to decouple the interface of
 * the Model layer from the particular type of Service used to
 * implement this layer.
 */
public class ImageModel
       implements MVP.ProvidedModelOps {
    /**
     * Debugging tag used by the Android logger.
     */
    protected final static String TAG = 
        ImageModel.class.getSimpleName();

    /**
     * Indicates the desired type of Service.
     */
    public enum ServiceType {
        BOUND_SERVICE,   // Use a Bound Service to download an image.
        STARTED_SERVICE  // Use a Started Service to download an image.
    }

    /**
     * Type of Service (i.e., BOUND_SERVICE or STARTED_SERVICE) to use
     * for the ImageModel implementation.
     */
    private ServiceType mServiceType;

    /**
     * Reference to the selected implementation.  Play the role of the
     * "Impl" in the Bridge pattern.
     */
    private ImageModelImpl mImageModelImpl;

    /**
     * A WeakReference used to access methods in the Presenter layer.
     * The WeakReference enables garbage collection.
     */
    protected WeakReference<MVP.RequiredPresenterOps> mImagePresenter;

    /**
     * Hook method called when a new ImageModel instance is created.
     * Simply forward to the implementation.
     * 
     * @param presenter
     *            A reference to the Presenter layer.
     */
    @Override
    public void onCreate(MVP.RequiredPresenterOps presenter) {
        // Set the WeakReference.
        mImagePresenter = new WeakReference<>(presenter);

        // Create a BOUND_SERVICE by default.
        setServiceType(ServiceType.BOUND_SERVICE);
    }

    /**
     * Hook method called to shutdown the Presenter layer.
     */
    @Override
    public void onDestroy(boolean isChangingConfigurations) {
        // Forward the onDestroy().
        mImageModelImpl.onDestroy(isChangingConfigurations);
    }

    /**
     * Start a download.  When the download finishes its results are
     * passed up to the Presentation layer via the
     * onDownloadComplete() method defined in RequiredPresenterOps.
     *
     * @param url
     *        URL of the image to download.
     * @param directoryPathname
     *        Uri of the directory to store the downloaded image.
     */
    @Override
    public void startDownload(Uri url,
                              Uri directoryPathname) {
        mImageModelImpl.startDownload(url,
                                      directoryPathname);
    }

    /**
     * Set the type of Service to use for the ImageModel
     * implementation.
     *
     * @param serviceType 
     *            Type of Service, i.e., BOUND_SERVICE or STARTED_SERVICE.
     */
    public void setServiceType(ImageModel.ServiceType serviceType) {
        // Only set the new ServiceType if it's different than the one
        // that's already in place.
        if (mServiceType != serviceType) {
            mServiceType = serviceType;
            if (mImageModelImpl != null)
                // Destroy the existing implementation, if any.
                mImageModelImpl.onDestroy(false);

            switch (mServiceType) {
            case BOUND_SERVICE:
                // Create an implementation that uses a Bound Service.
                mImageModelImpl =
                    new ImageModelImplBoundService();
                break;

            case STARTED_SERVICE:
                // Create an implementation that uses a Started
                // Service.
                mImageModelImpl =
                    new ImageModelImplStartedService();
                break;
            }

            // Initialize the ImageModel implementation.
            mImageModelImpl.onCreate(mImagePresenter.get());
        }
    }
}
