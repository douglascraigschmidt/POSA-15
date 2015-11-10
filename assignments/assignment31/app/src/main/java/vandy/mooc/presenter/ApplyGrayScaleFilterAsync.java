package vandy.mooc.presenter;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;

import vandy.mooc.model.ImageDownloadsModel;

/**
 * Created by praveen on 11/9/15.
 */
public class ApplyGrayScaleFilterAsync extends AsyncTask<Uri,Void,Uri>{

    private static final String TAG = ApplyGrayScaleFilterAsync.class.getName();

    private ImagePresenter mImagePresenter;

    private Uri mDirectoryPath ;

    public ApplyGrayScaleFilterAsync(ImagePresenter mImagePresenter, Uri dirPath) {
        this.mImagePresenter = mImagePresenter;
        this.mDirectoryPath = dirPath;
    }

    @Override
    protected Uri doInBackground(Uri... uris) {
        Log.i(TAG,"Came in gray scale");
        Bitmap origImage = null;
        try {
            origImage = MediaStore.Images.Media.getBitmap(mImagePresenter.getActivityContext().getContentResolver(),uris[0]);
        } catch (IOException e) {
            Log.e(TAG,"Issue creating bitamp from uri");
            e.printStackTrace();
        }

        Bitmap grayScaleImage = createGreyScaleImage(origImage);

        if(grayScaleImage != null){
            Log.i(TAG, "greyscale image created sucessfully");
//            mImagePresenter.getApplicationContext().getContentResolver().delete(uris[0], null, null);
            File destDir = new File(mDirectoryPath.toString());
            if(destDir.exists()){
                Log.i(TAG,"path exists");
            }else{
                Log.i(TAG,"Path does not exist");
            }
            String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
            String imageName = "IMG_"+timeStamp+".jpg";
            File greyFilterImageFile = new File(destDir+File.separator+imageName);
            ;
            if(ImageDownloadsModel.saveImageToDir(greyFilterImageFile, grayScaleImage)){
                return Uri.fromFile(greyFilterImageFile);
            }else{
                Log.e(TAG,"Error occured in saving filtered image to dir");
                return null;
            }
        }else{
            Log.e(TAG,"Error occured when creating grey scale image");
            return null;
        }
    }

    @Override
    protected void onPostExecute(Uri greyScaleImage) {
//        super.onPostExecute(uri);
        mImagePresenter.onProcessingComplete(mDirectoryPath,greyScaleImage);
    }

    public Bitmap createGreyScaleImage(Bitmap origImage){
        int width, height;
        height = origImage.getHeight();
        width = origImage.getWidth();

        Bitmap grayScale = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(grayScale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter cmf = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(cmf);
        c.drawBitmap(origImage,0,0,paint);

        return grayScale;
    }
}
