package vandy.mooc.model;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import vandy.mooc.MVP;
import vandy.mooc.common.Utils;

import android.app.Activity;
import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

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
     * @param isChangingConfigurations
     *        True if a runtime configuration triggered the onDestroy() call.
     */
    @Override
    public void onDestroy(boolean isChangingConfigurations) {
        // No-op.
    }

    /**
     * Download the image located at the provided Internet url using
     * the URL class, store it on the android file system using a
     * FileOutputStream, and return the path to the image file on
     * disk.
     *
     * @param context
     *          The context in which to write the file.
     * @param url 
     *          The URL of the image to download.
     * @param directoryPathname 
     *          Pathname of the directory to write the file.
     * U
     * @return 
     *        Absolute path to the downloaded image file on the file
     *        system.
     */
    public Uri downloadImage(Context context,
                             Uri url,
                             Uri directoryPathname) {
        // @@ TODO -- You fill in here, replacing "null" with the appropriate code.
        Log.i(TAG,"Downlaoding image for url :" + url);
        Log.i(TAG, "file name :" + url.getLastPathSegment());
        Bitmap downloadedImage = Utils.downloadAndDecodeImage(url.toString());
        if(downloadedImage == null){
            Log.e(TAG,"Could not donwload image for url"+url.toString());
            return null;
        }
        File dirPath = new File(directoryPathname.toString());
        dirPath.mkdir();
        if(dirPath.exists()){
            Log.i(TAG,"path created");
        }else{
            Log.i(TAG,"No Go");
        }
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        String imageName = url.getLastPathSegment();
        Log.i(TAG,"image Name"+ imageName);
        File imageFile = new File(directoryPathname+File.separator+imageName);
        Log.i(TAG,"image path"+imageFile.getAbsolutePath());
        OutputStream outputStream = null;
        try{
            outputStream =new BufferedOutputStream(new FileOutputStream(imageFile));

            downloadedImage.compress(Bitmap.CompressFormat.PNG,90,outputStream);
        } catch (FileNotFoundException e) {
            Log.i(TAG,"file not found"+imageFile);
            e.printStackTrace();
            return null;
        }

        return Uri.fromFile(imageFile);
    }
}
