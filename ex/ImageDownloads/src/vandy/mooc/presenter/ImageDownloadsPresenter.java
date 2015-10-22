package vandy.mooc.presenter;

import java.io.InputStream;
import java.lang.ref.WeakReference;

import vandy.mooc.MVP;
import vandy.mooc.R;
import vandy.mooc.common.GenericPresenter;
import vandy.mooc.model.ImageDownloadsModel;
import vandy.mooc.presenter.strategies.DownloadContext;
import vandy.mooc.presenter.strategies.DownloadWithAsyncTask;
import vandy.mooc.presenter.strategies.DownloadWithMessages;
import vandy.mooc.presenter.strategies.DownloadWithRunnables;
import vandy.mooc.presenter.strategies.ImageStrategy;
import vandy.mooc.presenter.strategies.ResetBitmap;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * This class plays the "Presenter" role in the Model-View-Presenter
 * (MVP) pattern by acting upon the Model and the View, i.e., it
 * retrieves data from the Model (e.g., ImageDownloadsModel) and
 * formats it for display in the View (e.g., ImageDownloadsActivity).
 * It expends the GenericPresenter superclass and implements
 * MVP.ProvidedPresenterOps and MVP.RequiredModelOps so it can be
 * created/managed by the GenericPresenter framework.
 */
public class ImageDownloadsPresenter
       extends GenericPresenter<MVP.RequiredPresenterOps,
                                MVP.ProvidedModelOps,
                                ImageDownloadsModel>
       implements MVP.ProvidedPresenterOps,
                  MVP.RequiredPresenterOps {
    /**
     * A WeakReference used to access methods in the View layer.  The
     * WeakReference enables garbage collection.
     */
    private WeakReference<MVP.RequiredViewOps> mView;

    /**
     * Maps buttons (represented via their resource ids) to
     * ImageStrategy implementations.
     */
    private ButtonToImageStrategyMapper mButtonToImageStrategyMapper;

    /**
     * The currently active ImageStrategy, which is used to keep track
     * of whether we need to cancel an ongoing download before
     * initiating a new one.
     */
    private ImageStrategy mActiveImageStrategy = null;

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
     * Default constructor that's needed by the GenericActivity
     * framework.
     */
    public ImageDownloadsPresenter() {
    }

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
        mView =
            new WeakReference<>(view);

        // Initialize the ImageStrategyMapper that efficiently maps
        // button ids to strategies for downloading and displaying
        // images concurrently.
        mButtonToImageStrategyMapper = 
            new ButtonToImageStrategyMapper
            (new int[] { 
                R.id.runnable_button,
                R.id.messages_button,
                R.id.async_task_button,
                R.id.reset_image_button },
             new ImageStrategy[] {
                new DownloadWithRunnables(),
                new DownloadWithMessages(),
                new DownloadWithAsyncTask(),
                new ResetBitmap()
             });


        // Extract the default image.
        final InputStream is = (InputStream)
            mView.get()
                 .getActivityContext()
                 .getResources()
                 .openRawResource(R.drawable.default_image);

        // Decode an InputStream into a Bitmap that
        // stores the default image.
        mDefaultImage = BitmapFactory.decodeStream(is);

        // Show the default image on the user's screen.
        resetBitmap();

        // Invoke the special onCreate() method in GenericPresenter,
        // passing in the ImageDownloadsModel class to
        // instantiate/manage and "this" to provide
        // ImageDownloadsModel with this MVP.RequiredModelOps
        // instance.
        super.onCreate(ImageDownloadsModel.class,
                       this);
    }

    /**
     * Hook method dispatched by the GenericActivity framework to
     * initialize the ImageDownloadsPresenter object after it's been
     * created.
     *
     * @param view         
     *          The currently active MVP.RequiredViewOps.
     */
    @Override
    public void onConfigurationChange(MVP.RequiredViewOps view) {
        Log.d(TAG,
              "onConfigurationChange() called");

        // Reset the WeakReference.
        mView =
            new WeakReference<>(view);

        // Set the default image.
        mView.get().displayBitmap(mCurrentImage,
                                  null);
    }

    /**
     * Hook method called to shutdown the Model layer.
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
     * Factory method that returns the DownloadContext associated with
     * this user request, which plays the role of the "Context" in the
     * Strategy pattern.
     * 
     * @param url
     *        URL to download.
     */
    private DownloadContext makeDownloadContext(String url) {
        // This command is called back after the image is displayed to
        // indicate there's no active ImageStrategy.
        final Runnable completionCommand =
            new Runnable() {
                public void run() {
                    // Indicate there's no active ImageStrategy.
                    mActiveImageStrategy = null;
                }
            };

        // Create a DownloadContext that stores references to the
        // MVP.ProvidedModelOps and completion hook objects in fields,
        // which are used by the various concrete ButtonStrategies to
        // download and display an image concurrently.
        return new DownloadContext(url,
                                   mView.get(),
                                   this,
                                   getModel(),
                                   completionCommand);
    }

    /**
     * Called when a user clicks a button to download an image.
     * 
     * @param buttonResId
     *            Indicates the button pressed by the user.
     * @param url
     *            URL givenby the user.
     */
    public void handleButtonClick(int buttonResId,
                                  String url) {
        // Create a DownloadContext object associated with this user
        // request, which plays the role of the "Context" in the
        // Strategy pattern.
        final DownloadContext downloadContext =
            makeDownloadContext(url);

        // Only one download/display is allowed at a time, so if an
        // operation is already in progress then cancel it first.
        if (mActiveImageStrategy != null) 
            mActiveImageStrategy.cancel(downloadContext);

        // Get the ImageStrategy associated with the buttonResId.
        mActiveImageStrategy = 
            mButtonToImageStrategyMapper.getImageStrategy(buttonResId);

        // Invoke the ImageStrategy to download and display the
        // image concurrently.
        mActiveImageStrategy.downloadAndDisplay(downloadContext);
    }

    /**
     * Set the current image.
     */
    public void setCurrentImage(Bitmap image) {
        mCurrentImage = image;
    }

    /**
     * Reset bitmap display on the user's screen to the default image.
     */
    public void resetBitmap() {
        // Store the current image for subsequent use in case of a
        // runtime configuration change.
        setCurrentImage(mDefaultImage);

        // Set the default image.
        mView.get().displayBitmap(mDefaultImage, null);
    }
}

