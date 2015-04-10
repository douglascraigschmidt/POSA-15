package coursera.gavin;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.renderscript.RenderScript;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import vandy.mooc.Utils;

public class MyActivity extends Activity {
    private static final String TAG = "MyActivity";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final ImageView iv = (ImageView) findViewById(R.id.imageView);

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.raw.dougs);
        iv.setImageBitmap(bm);

        final RenderScript renderScript = RenderScript.create(this);

        Utils.prepareRS(renderScript);

        Button btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bitmap bm2 = BitmapFactory.decodeResource(getResources(), R.raw.dougs);

                Bitmap newBmp = Bitmap.createBitmap(bm2.getWidth(), bm2.getHeight(), Bitmap.Config.ARGB_8888);
                // Create a canvas  for new bitmap
                Canvas c = new Canvas(newBmp);

                // Draw your old bitmap on it.
                c.drawBitmap(bm2, 0, 0, new Paint());

                Log.i(TAG, "Start Convert");
                //Bitmap convertToGreyScale = Utils.convertToGreyScale(newBmp);
                Bitmap convertToGreyScale = Utils.convertToGreyScaleRS(newBmp);
                Log.i(TAG, "Done Convert");

                iv.setImageBitmap(convertToGreyScale);
            }
        });
    }
}
