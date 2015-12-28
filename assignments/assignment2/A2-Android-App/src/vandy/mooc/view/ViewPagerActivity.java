package vandy.mooc.view;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import vandy.mooc.R;
import vandy.mooc.utils.loader.ImageLoader;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Displays a directory of Images in a ViewPager. Each image is
 * displayed fullscreen and the user can swipe to navigate to other
 * images.
 * 
 * @author Geoffrey
 */
public class ViewPagerActivity extends FragmentActivity {
    /*
     * Constants
     */

    /**
     * Class tag for Debugging
     */
    private static final String TAG =
        ViewPagerActivity.class.getCanonicalName();

    /**
     * Constant used to access the position of the current image
     * passed through the intent.
     */
    public static final String CURRENT_IMAGE_POSITION = "img";

    /**
     * Name of the intent action that will start this Activity.
     */
    public static String ACTION_DISPLAY_IMAGES_SWIPE =
        "android.intent.action.DISPLAY_IMAGES_SWIPE";

    /*
     * Data members.
     */

    /**
     * The FragmentStatePagerAdapter responsible for creating the
     * fragments holding each bitmap that are used by the ViewPager.
     */
    ImagePagerAdapter mImagePagerAdapter;

    /**
     * The UI element responsible for displaying each fragment and
     * handling the swipe navigation.
     */
    ViewPager mViewPager;

    /**
     * The position in the directory of the currently displayed image.
     */
    public int mCurrentImage;

    /**
     * The file path to the directory of images.
     */
    private String mFilePath;
    
    /**
     * ImageLoader used to load the images in the background
     */
    private ImageLoader mLoader;
    
    /**
     * The screen width that is used to scale the loaded bitmaps
     */
    private static int mWidth;

    /**
     * Factory method that returns an Intent for displaying Images in
     * a ViewPager
     * 
     * @param directoryPathname
     *            Filepath storing images to display
     * @param pos
     *            Position of starting image
     * @return
     */
    public static Intent makeIntent(String directoryPathname,
                                    int position) {
        return new Intent(ACTION_DISPLAY_IMAGES_SWIPE)
                .setDataAndType(
                        Uri.parse(directoryPathname)
                                .buildUpon()
                                .scheme("file")
                                .build(),
                        "image/*")
                .putExtra(ViewPagerActivity.CURRENT_IMAGE_POSITION,
                        position);
    }

    /**
     * This Creates the activity, the Viewpager, and its adapter.
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Extract intent's data.
        Intent intent = getIntent();

        Log.v(TAG,
              "2" 
              + intent);
        Log.v(TAG,
              "2" 
              + intent.getData());

        // If the intent exists and contains the filepath, extract its
        // data
        if (intent != null && intent.getData() != null) {
            mCurrentImage =
                intent.getIntExtra(CURRENT_IMAGE_POSITION,
                                   0);
            mFilePath = intent.getData().getPath();
        }
        Log.v(TAG,
              "mFilePath" 
              + mFilePath);

        // Set the content view.
        setContentView(R.layout.image_detail_swipe_activity);
        
        // Find the screen's width
        DisplayMetrics displaymetrics =
            new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        mWidth = displaymetrics.widthPixels;

        // Initialize the pager adapter.
        File dirFile = new File(mFilePath);
        mImagePagerAdapter =
            new ImagePagerAdapter(getFragmentManager(),
                                  dirFile.listFiles());

        // Initialize the ViewPager using the pager adapter
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mImagePagerAdapter);
        mViewPager.setCurrentItem(mCurrentImage);
        Log.v(TAG, 
              "first image #: " 
              + mCurrentImage);
        
        // Initialize the ImageLoader
        mLoader = new ImageLoader(getResources().getDrawable(R.drawable.loading));
    }
    
    /**
     * Accesses the ImageLoader used by this activity.
     */
    public ImageLoader getImageLoader () {
        return mLoader;
    }

    /**
     * FragmentStatePagerAdapter that returns fragments containing a
     * fullscreen representation of the images stored in the directory
     * passed to the setBitmaps method
     */
    private class ImagePagerAdapter extends FragmentStatePagerAdapter {
        /**
         * The ArrayList of Files holding the images to be displayed.
         */
        private ArrayList<File> mBitmapFiles;

        /**
         * Constructor initializes data members.
         */
        public ImagePagerAdapter(FragmentManager fm,
                                 File[] files) {
            super(fm);
            Log.v(TAG,
                  "ImagePagerAdapter constructor()");
            mBitmapFiles =
                new ArrayList<>(Arrays.asList(files));
        }

        /**
         * Returns a fragment that displays the Bitmap at the
         * requested position in the directory.
         */
        @Override
        public Fragment getItem(int position) {
            Log.v(TAG,
                  "getItem" 
                  + position);

            mCurrentImage = position;

            // Create a new ImageDetailFragment holding the Image
            // stored at the File in the requested position.
            return ImageDetailFragment.newInstance
                (mBitmapFiles.get(position));
        }

        /**
         * Returns the number of Images in the directory.
         */
        @Override
        public int getCount() {
            Log.v(TAG,
                  "ImagePagerAdapter getCount()");
            return mBitmapFiles.size();
        }
    }

    /**
     * Fragment representing a single Bitmap object in the collection.
     */
    public static class ImageDetailFragment extends Fragment {
        /**
         * The file path of the image to be displayed
         */
        String mImageFilePath;

        /**
         * Empty constructor, which is needed for Fragments.
         */
        public ImageDetailFragment() {
            /* no-op */
        }

        /**
         * Create a new instance of ImageDetailFragment, providing an
         * image file to be displayed.
         */
        static ImageDetailFragment newInstance(File imageFile) {
            ImageDetailFragment f = new ImageDetailFragment();

            // Supply the file's path as an argument.
            Bundle args = new Bundle();
            args.putString("file",
                           imageFile.getPath());
            f.setArguments(args);
            return f;
        }

        /**
         * When creating, retrieve this instance's image's file path
         * from its arguments.
         */
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Retrieve the file to display from the fragment's
            // set arguments if it exists (i.e. the fragment has 
            // undergone an orientation change, or the newInstance()
            // method was used to create the fragment
            mImageFilePath =
                getArguments() != null 
                ? getArguments().getString("file")
                : null;
        }

        /**
         * Returns a view displaying the image found at the file path
         * full screen.
         */
        @Override
        public View onCreateView(LayoutInflater inflater,
                                 ViewGroup container,
                                 Bundle savedInstanceState) {
            // Create the fragment's view from its corresponding layout file
            View rootView =
                inflater.inflate(R.layout.fragment_collection_detail,
                                 container,
                                 false);

            // Use the ImageLoader to load and display the bitmap
            ((ViewPagerActivity)getActivity())
                .getImageLoader()
                    .loadAndDisplayImage((ImageView)rootView.findViewById(R.id.img_view), 
                                         mImageFilePath, 
                                         mWidth);
            return rootView;
        }
    }
}
