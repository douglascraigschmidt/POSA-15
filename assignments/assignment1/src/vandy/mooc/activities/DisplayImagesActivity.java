package vandy.mooc.activities;

import java.io.File;
import java.util.ArrayList;

import vandy.mooc.R;
import vandy.mooc.utils.Utils;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * An Activity that Displays an image.
 */
public class DisplayImagesActivity extends LifecycleLoggingActivity {
    /**
     * Name of the Intent Action that wills start this Activity.
     */
    public static String ACTION_DISPLAY_IMAGES =
        "android.intent.action.DISPLAY_IMAGES";

    /**
     * The column width to use for the GridView.
     */
    private int mColWidth;
    
    /**
     * The number of columns to use in the GridView.
     */
    private int mNumCols;
    
    /**
     * A reasonable column width.
     */
    private final int COL_WIDTH = 300;

    /**
     * The adapter responsible for loading the results into the
     * GridView.
     */
    private ImageAdapter imageAdapter;

    /**
     * The file path in external storage storing images to display
     */
    private String mFilePath;
	
    /**
     * Creates the activity and generates a button for each filter
     * applied to the images. These buttons load change the
     * imageAdapter's source to a new directory, from which it will
     * load images into the GridView.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        
        // Configure the GridView adapter and dimensions.
        imageAdapter = new ImageAdapter(this);
        GridView imageGrid = (GridView) findViewById(R.id.imageGrid);
        imageGrid.setAdapter(imageAdapter);
        configureGridView(imageGrid);
        
        mFilePath =
            getIntent().getDataString();
        
        // Find the directory and load the directory as the source of
        // the imageAdapter.
        imageAdapter.setBitmaps
            (mFilePath);
    }
    
    /**
     * Factory method that returns an Intent for displaying images.
     */
    public static Intent makeIntent(String directoryPathname) {
        return new Intent(ACTION_DISPLAY_IMAGES)
                          .setDataAndType(Uri.parse(directoryPathname),
                                          "image/*");
    }

    /**
     * Configures the GridView with an appropriate column number and
     * width based on the screen size.
     */
    private void configureGridView(GridView imageGrid) {
    	// Retrieve the Screen dimensions.
        Display display = 
            getWindowManager().getDefaultDisplay();
    	Point size = new Point();
    	display.getSize(size);
    	
    	// Calculate appropriate values.
    	mNumCols = size.x/COL_WIDTH;
    	mColWidth = size.x/mNumCols;
    	
    	// Configure the GridView with dynamic values.
    	imageGrid.setColumnWidth(mColWidth);
    	imageGrid.setNumColumns(mNumCols);
    	
    	((ImageAdapter)imageGrid.getAdapter()).setColWidth(mColWidth);
    }

    /**
     * @class ImageAdapter
     *
     * @brief The Adapter that loads the Images into the Layout's
     *        GridView.
     */
    public class ImageAdapter extends BaseAdapter {
        /**
         * The Context of the application
         */
        private Context mContext;
        
        /**
         * The padding each image will have around it
         */
        private int mPadding = 8;

        /**
         * The ArrayList of bitmaps that hold the thumbnail images.
         */
        private ArrayList<Bitmap> mBitmaps;

        /**
         * Creates the ImageAdapter in the given context.
         */
        public ImageAdapter(Context c) {
            mContext = c;
            mBitmaps = new ArrayList<Bitmap>();
        }

        /**
         * Returns the count of bitmaps in the list.
         */
        @Override
            public int getCount() {
            return mBitmaps.size();
        }

        /**
         * Returns the bitmap at the given position.
         */
        @Override
            public Object getItem(int position) {
            return mBitmaps.get(position);
        }

        /**
         * Returns the given position as the Id of the bitmap.  This
         * works because the bitmaps are stored in a sequential
         * manner.
         */
        @Override
            public long getItemId(int position) {
            return position;
        }

        /**
         * Returns the view. This method is necessary for filling the
         * GridView appropriately.
         */
        @Override
        public View getView(int position,
                            View convertView,
                            ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(mContext);
                
                // Set configuration properties of the ImageView
                imageView.setLayoutParams(
                		new GridView.LayoutParams(mColWidth, mColWidth));
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setPadding(mPadding, mPadding, mPadding, mPadding);
            } else {
                imageView = (ImageView) convertView;
            }
            imageView.setImageBitmap(mBitmaps.get(position));
            return imageView;
        }

        
        private int mColWidth = 100;
        
        public void setColWidth(int w ) {
        	if (w > 0 )
        		mColWidth = w;
        }

        /**
         * Convert the @a bitmap parameter into a scaled Bitmap to 
         * avoid out-of-memory exceptions with large images.
         */
        private Bitmap getScaledBitmap(File bitmap) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(bitmap.getAbsolutePath(), options);
        	
            int sizeRatio = options.outWidth /mColWidth;
        	
            options.inJustDecodeBounds = false;
            options.inSampleSize = sizeRatio;	
            return BitmapFactory.decodeFile(bitmap.getAbsolutePath(), options);
        }

        /**
         * Resets the bitmaps of the GridView to the ones found at the
         * given filterPath.
         */
        private void setBitmaps(String filterPath) {
            File[] bitmaps = new File(filterPath).listFiles();
            mBitmaps = new ArrayList<Bitmap>();

            // If there are some image files to display, load, and
            // store their bitmaps in the bitmap array.
            if (bitmaps != null) {
                for (File bitmap : bitmaps) {
                    if (bitmap != null) {
                        try {
                            mBitmaps.add
                                // Scale the bitmap to avoid
                                // out-of-memory exceptions with large
                                // images.
                                (getScaledBitmap(bitmap));
                        } catch (Exception | Error e) {
                            Log.e(TAG,"Error displaying image:", e);
                            Utils.showToast(DisplayImagesActivity.this,
                                            "Error displaying image at "
                                            + bitmap.getAbsolutePath());
                        }
                    }
                }
            }

            notifyDataSetChanged();
        }
    }
}
