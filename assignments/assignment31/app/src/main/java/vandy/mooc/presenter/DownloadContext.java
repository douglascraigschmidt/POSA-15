package vandy.mooc.presenter;

/**
 * Created by praveen on 11/1/15.
 */

import android.graphics.Bitmap;
import android.net.Uri;

import java.lang.ref.WeakReference;

import vandy.mooc.MVP;
import vandy.mooc.model.ImageDownloadsModel;

/**
 * Class that defines methods shared by all the ButtonStrategy
 * implementations.  It plays the role of the "Context" in the
 * Strategy pattern.
 */
public class DownloadContext {

    private final WeakReference<MVP.RequiredPresenterOps> mPresenter;

    private final Uri muril;

    private final WeakReference<MVP.ProvidedModelOps> mModel;

    private final Runnable mCompletionCommand;

    private final Uri mFilePath;

    public DownloadContext(WeakReference<MVP.RequiredPresenterOps> mPresenter, Uri muril, WeakReference<MVP.ProvidedModelOps> mModel, Runnable mCompletionCommand, Uri mFilePath) {
        this.mFilePath = mFilePath;
        this.mPresenter = new WeakReference<MVP.RequiredPresenterOps>((MVP.RequiredPresenterOps) mPresenter);
        this.muril = muril;
        this.mModel = new WeakReference<MVP.ProvidedModelOps>((MVP.ProvidedModelOps) mModel);
        this.mCompletionCommand = mCompletionCommand;
    }

    public Uri getImageUri(){
        return muril;
    }

    public Uri downloadImage(Uri link){
        return  mModel.get().downloadImage(mPresenter.get().getApplicationContext(),link,mFilePath);
    }

}
