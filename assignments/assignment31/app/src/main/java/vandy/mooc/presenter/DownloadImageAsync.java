package vandy.mooc.presenter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;

import vandy.mooc.MVP;
import vandy.mooc.common.Utils;
import vandy.mooc.model.ImageDownloadsModel;

/**
 * Created by praveen on 11/1/15.
 */
public class DownloadImageAsync extends AsyncTask<Uri,Void,Uri> {

    private static final String TAG = DownloadImageAsync.class.getName();

    private Uri mDirectoryPath;

    private ImagePresenter mImagePresenter;

    public DownloadImageAsync(Uri dirPath, ImagePresenter imagePresenter) {
        this.mDirectoryPath = dirPath;
        this.mImagePresenter = imagePresenter;
    }

    @Override
    protected Uri doInBackground(Uri... uris) {
        Log.i(TAG, "Image being downloaded " + uris[0]);
        ImageDownloadsModel im = new ImageDownloadsModel();
        Uri imagePath = im.downloadImage(mImagePresenter.getApplicationContext(), uris[0], mDirectoryPath);
        return imagePath;
    }

    @Override
    protected void onPostExecute(Uri imagePath) {
        Utils.showToast(mImagePresenter.getApplicationContext(),"Donwloaded image");
          mImagePresenter.onProcessingComplete(mDirectoryPath,imagePath);
//        mImagePresenter.onProcessingComplete();
    }
}

