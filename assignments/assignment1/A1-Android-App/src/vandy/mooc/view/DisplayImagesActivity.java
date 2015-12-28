package vandy.mooc.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;

import vandy.mooc.R;
import vandy.mooc.common.LifecycleLoggingActivity;
import vandy.mooc.utils.loader.ImageLoader;

/**
 * An Activity that displays all the images that have been downloaded
 * and processed.
 */
public class DisplayImagesActivity
       extends LifecycleLoggingActivity {
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
     * The adapter responsible for loading the results into the GridView.
     */
    private ImageAdapter imageAdapter;

    /**
     * The file path in external storage storing images to display
     */
    private String mFilePath;
    
    /**
     * ImageLoader used to load images in the background
     */
    private ImageLoader mLoader;

    /**
     * Creates the activity and generates a button for each filter
     * applied to the images. These buttons load change the
     * imageAdapter's source to a new directory, from which it will
     * load images into the GridView.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_images_activity);

        // Configure the GridView adapter and dimensions.
        imageAdapter = new ImageAdapter(this);
        GridView imageGrid = (GridView) findViewById(R.id.imageGrid);
        imageGrid.setAdapter(imageAdapter);
        configureGridView(imageGrid);
        
        // Initialize the image loader
        mLoader = new ImageLoader(getResources()
                                    .getDrawable
                                       (R.drawable.loading));

        // Retrieve the file path to the directory containing the
        // images to display from the intent.
        mFilePath = getIntent().getData().getPath();

        // Find the directory and load the directory as the source of
        // the imageAdapter.
        imageAdapter.setBitmaps(mFilePath);

        // Implement onItemClick to start a SwipeListDisplay at
        // the current image.
        imageGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                startActivity(ViewPagerActivity.makeIntent
                        (mFilePath,
                                position));
            }
        });
    }

    /**
     * Factory method that returns an implicit Intent for displaying
     * images.
     */
    public static Intent makeIntent(Uri directoryPathname) {
        return new Intent(ACTION_DISPLAY_IMAGES)
                .setDataAndType(
                        Uri.parse(directoryPathname.getPath())
                                .buildUpon()
                                .scheme("file")
                                .build(),
                        "image/*");
    }

    /**
     * Configures the GridView with an appropriate column number and
     * width based on the screen size.
     */
    private void configureGridView(GridView imageGrid) {
        // Retrieve the Screen dimensions.
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        // Calculate appropriate values.
        mNumCols = size.x / COL_WIDTH;
        mColWidth = size.x / mNumCols;

        // Configure the GridView with dynamic values.
        imageGrid.setColumnWidth(mColWidth);
        imageGrid.setNumColumns(mNumCols);

        ((ImageAdapter) imageGrid.getAdapter()).setColWidth(mColWidth);
    }

    /**
     * @class ImageAdapter
     *
     * @brief The Adapter that loads the Images into the Layout's GridView.
     */
    public class ImageAdapter extends BaseAdapter {
    	/**
    	 * File path of the directory holding the images to display
    	 */
        private String mFilePath = null;

        /**
         * The Context of the application
         */
        private Context mContext;

        /**
         * The padding each image will have around it
         */
        private int mPadding = 0;

        /**
         * The image files being displayed
         */
        private File[] mBitmapFiles;
        
        /**
         * Creates the ImageAdapter in the given context.
         */
        public ImageAdapter(Context c) {
            mContext = c;
            mBitmapFiles = new File[] {};
        }
        
        /**
         * Returns the count of bitmaps in the list.
         */
        @Override
        public int getCount() {
            return mBitmapFiles.length;
        }

        /**
         * Returns the bitmap at the given position.
         */
        @Override
        public Object getItem(int position) {
            return mBitmapFiles[position];
        }

        /**
         * Returns the given position as the Id of the bitmap. This works
         * because the bitmaps are stored in a sequential manner.
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
        public View getView(final int position,
                            View convertView,
                            ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(mContext);

                // Set configuration properties of the ImageView
                imageView.setLayoutParams(new GridView.LayoutParams(mColWidth,
                                                                    mColWidth));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(mPadding,
                                     mPadding,
                                     mPadding,
                                     mPadding);
            } else
                imageView = (ImageView) convertView;

            // Load the image in the background
            mLoader.loadAndDisplayImage(imageView, 
                                        mBitmapFiles[position]
                                            .getAbsolutePath(), 
                                        mColWidth);
            return imageView;
        }

        /**
         * Maximum width of a column.
         */
        private int mColWidth = 100;

        /**
         * Set the maximum width of a column.
         */
        public void setColWidth(int w) {
            if (w > 0)
                mColWidth = w;
        }
        /**
         * Resets the bitmaps of the GridView to the ones found at the
         * given filterPath.
         */
        private void setBitmaps(String filterPath) {
            mFilePath = filterPath;
            mBitmapFiles = new File(filterPath).listFiles();

            notifyDataSetChanged();
        }
    }
}
