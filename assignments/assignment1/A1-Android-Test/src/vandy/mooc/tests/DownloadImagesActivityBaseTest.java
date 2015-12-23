package vandy.mooc.tests;

import vandy.mooc.view.DownloadImagesActivity;
import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;


/**
 * Created by Monte Creasor on 2015-05-26.
 */

/**
 * This class is used a base class for all DownloadImagesActivity tests.  It
 * contains arrays of valid, invalid, and combined valid/invalid URLs
 * used by each test. It also handles the setup and tear down
 * sequences for the instrumentation runner along with an instance of
 * the Robotium solo class object.
 */

public class DownloadImagesActivityBaseTest extends ActivityInstrumentationTestCase2<DownloadImagesActivity> {
    /**
     * Malformed URL string used to test for toast message.
     */
    protected static final String mInvalidUrl = "http//www.microsoft.com";

    /**
     * A list of valid URLs to test the download of a valid images.
     * This array is used to test the download of a single valid image
     * as well as again to test the download of multiple valid images.
     * These images are ordered from largest to smallest in size.
     */
    protected static final String mValidUrlList[] =
         // 163 KB
        {"http://s1.ibtimes.com/sites/www.ibtimes.com/files/2015/06/11/game-thrones-coloring-book.jpg",
         // 151 KB
         "http://colleges.usnews.rankingsandreviews.com/img/college-photo_313._445x280-zmm.jpg",
         // 144 KB
         "http://static.tumblr.com/a5fa27964cfba9a928dd51f93d648375/24rnppg/LeFndriav/tumblr_static_876b2abic1kwkcw884s40cgw0.jpg",
         // 84 KB
         "http://www.dre.vanderbilt.edu/~schmidt/gifs/dougs-xsmall.jpg",
         // 48 KB
         "http://acidcow.com/pics/20110920/famous_actors_who_got_hit_with_the_ugly_stick_19.jpg",
         // 28 KB
         "http://funny-pics-fun.com/wp-content/uploads/Very-Funny-Baby-Faces-13.jpg",
        };

    /**
     * A list of invalid URLs to test the handling of invalid images.
     * This array is used to test the handling of a single invalid image
     * as well as again to test the handling of multiple invalid images.
     */
    protected static final String mInvalidUrlList[] =
        {"http://www.microsoft.com/NO_IMAGE.jpg",
         "http://www.coursera.com/NO_IMAGE.jpg",
        };

    /**
     * Mixed list of valid and invalid URLs. The list contains 6 valid
     * URLs and 3 invalid ones. The invalid ones contain the string
     * NO_IMAGE as a path component.
     */
    protected static final int VALID_MIXED_IMAGE_COUNT = 2;
    protected static final int INVALID_MIXED_IMAGE_COUNT = 3;
    protected static final String mMixedUrlList[] =
        {"http://www.microsoft.com/NO_IMAGE.jpg",
         "http://www.dre.vanderbilt.edu/~schmidt/gifs/dougs-xsmall.jpg",
         "http://www.google.com/NO_IMAGE.jpg",
         "http://funny-pics-fun.com/wp-content/uploads/Very-Funny-Baby-Faces-13.jpg",
         "http://www.coursera.com/NO_IMAGE.jpg"
        };

    /**
     * Solo instance for automated tests.
     */
    protected Solo mSolo;

    public DownloadImagesActivityBaseTest() {
        super(DownloadImagesActivity.class);
    }

    /**
     * Called by the framework before invoking test functions.
     */
    protected void setUp() throws Exception {
        mSolo = new Solo(getInstrumentation(), getActivity());
    }

    /**
     * Called by the framework when the first test fails,
     * or when all tests have completed successfully.
     *
     * @throws Exception
     */
    protected void tearDown() throws Exception {
        mSolo.finishOpenedActivities();
    }

}
