package vandy.mooc.presenter;

import java.io.InputStream;
import java.lang.ref.WeakReference;

import vandy.mooc.MVP;
import vandy.mooc.R;
import vandy.mooc.common.GenericPresenter;
import vandy.mooc.model.DownloadImageModel;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

/**
 * This class implements all the operations defined in the
 * DownloadImagePresenter.  It implements the various Ops interfaces
 * so it can be created/managed by the GenericActivity framework.  It
 * plays the role of the "Presenter" in the Model-View-Presenter
 * pattern.
 */
public class DownloadImagePresenter
       extends GenericPresenter<MVP.RequiredPresenterOps,
                                MVP.ProvidedModelOps,
                                DownloadImageModel>
       implements MVP.ProvidedPresenterOps,
                  MVP.RequiredPresenterOps {
    /**
     * Debugging tag used by the Android logger.
     */
    private final static String TAG = 
        DownloadImagePresenter.class.getSimpleName();

    /**
     * Used to enable garbage collection.
     */
    private WeakReference<MVP.RequiredViewOps> mView;

    /**
     * Used to keep track of whether a call is already in progress.
     */
    private boolean mCallInProgress;

    /**
     * Stores the current image to redisplay after a runtime
     * configuration change.
     */
    private Bitmap mCurrentImage = null;

    /**
     * Stores the default image to display when the "reset image"
     * button is pushed.
     */
    private Bitmap mDefaultImage = null;

    /**
     * Hook method called when a new instance of AcronymPresenter is
     * created.  One time initialization code goes here, e.g., storing
     * a WeakReference to the View layer and initializing the Model
     * layer.
     * 
     * @param view
     *            A reference to the View layer.
     */
    @Override
    public void onCreate(MVP.RequiredViewOps view) {
        // Set the WeakReference.
        mView = new WeakReference<>(view);

        // Extract the default image.
        final InputStream is = (InputStream)
            mView.get()
                 .getActivityContext()
                 .getResources()
                 .openRawResource(R.drawable.default_image);

        // Decode an InputStream into a Bitmap that
        // stores the default image.
        mDefaultImage = BitmapFactory.decodeStream(is);

        // Invoke the special onCreate() method in GenericPresenter,
        // passing in the DownloadImageModel class to instantiate/manage and
        // "this" to provide DownloadImageModel with this
        // MVP.RequiredModelOps instance.
        super.onCreate(DownloadImageModel.class,
                       this);
    }

    /**
     * Hook method dispatched by the GenericActivity framework to
     * initialize the ImagePresenter object after a runtime
     * configuration change.
     *
     * @param view         The currently active ImagePresenter.View.
     */
    @Override
    public void onConfigurationChange(MVP.RequiredViewOps view) {
        // Reset the mView WeakReference.
        mView = new WeakReference<>(view);

        // Set the default image.
        mView.get().displayImage(mCurrentImage);
    }

    /**
     * Hook method called to shutdown the Presenter layer.
     *
     * @param isChangeConfigurations
     *        True if a runtime configuration triggered the onDestroy() call.
     */
    @Override
    public void onDestroy(boolean isChangingConfigurations) {
        // Destroy the model.
        getModel().onDestroy(isChangingConfigurations);
    }

    /**
     * Initiate the asynchronous image download.
     */
    public boolean downloadImageAsync(Uri uri) {
        if (mCallInProgress)
            return false;
        else {
            mCallInProgress = true;
            getModel().downloadImageAsync(uri);
            mCallInProgress = false;
            return true;
        }
    }

    /**
     * Initiate the synchronous image download.
     */
    public boolean downloadImageSync(Uri uri) {
        if (mCallInProgress)
            return false;
        else {
            mCallInProgress = true;
            getModel().downloadImageSync(uri);
            mCallInProgress = false;
            return true;
        }
    }

    /**
     * Reset the image to the default.
     */
    public void resetImage() {
        // Store the current image for subsequent use in case of a
        // runtime configuration change.
        mCurrentImage = mDefaultImage;

        // Set the default image.
        mView.get().displayImage(mDefaultImage);
    }

    /**
     * Forwards to the View layer to display an image.
     * 
     * @param pathToImageFile
     *            The path to the file where the image is stored.
     */
    public void displayImage(Uri pathToImageFile) {   
        // Convert the file into a bitmap image and store it for
        // subsequent use after a rotation.
        mCurrentImage =
            BitmapFactory.decodeFile(pathToImageFile.toString());

        // Display the image to the user.
        mView.get().displayImage(mCurrentImage);
    }

    /**
     * Return the Activity context.
     */
    @Override
    public Context getActivityContext() {
        return mView.get().getActivityContext();
    }
    
    /**
     * Return the Application context.
     */
    @Override
    public Context getApplicationContext() {
        return mView.get().getApplicationContext();
    }
}
