package vandy.mooc.presenter.strategies;

import java.lang.ref.WeakReference;

import vandy.mooc.MVP;
import vandy.mooc.common.Utils;
import android.graphics.Bitmap;

/**
 * Class that defines methods shared by all the ButtonStrategy
 * implementations.  It plays the role of the "Context" in the
 * Strategy pattern.
 */
public class DownloadContext {
    /**
     * Debug Tag for logging debug output to LogCat
     */
    private final String TAG =
        DownloadContext.class.getSimpleName();
    
    /**
     * URL to download.
     */
    private final String mUrl;
    
    /**
     * Reference to the Presenter layer.
     */
    private final WeakReference<MVP.RequiredPresenterOps> mPresenter;

    /**
     * Reference to the Model layer.
     */
    private final WeakReference<MVP.ProvidedModelOps> mModel;

    /**
     * The completion command called after the image has been
     * displayed.
     */
    private final Runnable mCompletionCommand;

    /**
     * Constructor sets the various data members used by concrete
     * ButtonStrategies.
     */
    public DownloadContext(String url,
                           MVP.RequiredPresenterOps presenter,
                           MVP.ProvidedModelOps model,
                           Runnable completionCommand) {
        mUrl = url;
        mPresenter = new WeakReference<MVP.RequiredPresenterOps>(presenter);
        mModel = new WeakReference<MVP.ProvidedModelOps>(model);
        mCompletionCommand = completionCommand; // new WeakReference<Runnable>(completionCommand);
    }

    /**
     * @return the URL to download.
     */
    public String getUrl() {
        return mUrl;
    }
    
    /**
     * Download a bitmap image from the URL provided by the user.
     * 
     * @param url
     *            The url where a bitmap image is located
     *
     * @return the image bitmap or null if there was an error
     */
    public Bitmap downloadBitmap(String url) {
        return mModel.get().downloadBitmap(url);
    }

    /**
     * Show a toast message.
     */
    public void showToast(String message) {
        Utils.showToast(mPresenter.get()
                                  .getActivityContext(),
                        message);
    }

    /**
     * Display a downloaded bitmap image if it's non-null; otherwise,
     * it reports an error via a Toast that's displayed on the UI
     * Thread.  This method can be called from either the UI Thread or
     * a background Thread.
     * 
     * @param image
     *            The bitmap image
     */
    public void displayBitmap(Bitmap image) {   
        mPresenter.get().displayBitmap(image,
                                       mCompletionCommand);
    }

    /**
     * Reset bitmap display on the user's screen to the default image.
     */
    public void resetBitmap() {
        mPresenter.get().resetBitmap();
    }
}
