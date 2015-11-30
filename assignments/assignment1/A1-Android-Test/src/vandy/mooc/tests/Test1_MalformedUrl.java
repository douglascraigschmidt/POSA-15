package vandy.mooc.tests;

import vandy.mooc.R;
import vandy.mooc.view.DownloadImagesActivity;
import android.widget.LinearLayout;

/**
 * Created by Monte Creasor on 2015-05-26.
 */

/**
 * Malformed URL Test:
 *
 * ACTIONS: Tests the handling of single malformed URL.
 * EXPECTED RESULTS: A warning toast message and an empty URL list.
 *
 * This test enters a malformed URL into the URL edit view and
 * then clicks on the add URL button to attempt to add it to the
 * URL list. Since there is no way to be sure that all students
 * printed the same Toast error message string, it checks for
 * proper malformed URL handling by ensuring that the URL does
 * not get added to the URL list.
 */

public class Test1_MalformedUrl extends DownloadImagesActivityBaseTest {
    public void testRun() {
        // Wait for main activity.
        assertTrue("Test 1 failed: MainActivity did not load correctly.",
                   mSolo.waitForActivity(DownloadImagesActivity.class,
                                         TestUrlsHelper.longDelay));

        // Click on url text view.
        mSolo.clickOnView(mSolo.getView(R.id.url));

        // Ensure that the edit view is clear.
        mSolo.clearEditText((android.widget.EditText) mSolo.getView(R.id.url));

        // Add a url into the url text view.
        mSolo.enterText((android.widget.EditText) mSolo.getView(R.id.url),
                        mInvalidUrl);

        // Click on Add URL button to add the url to the list view.
        mSolo.clickOnView(mSolo.getView(R.id.button2));

        // Check for a toast error message.
        assertTrue("Test failed: Section One: No toast for Invalid URL",
                   mSolo.waitForText("Invalid",
                                     1,
                                     TestUrlsHelper.shortDelay));

        // Now check if the URL was added to the list view. This can be verified
        // by ensuring that the linear layout does not have an children.
        LinearLayout linearLayout = 
            (LinearLayout) mSolo.getView(R.id.linearLayout);

        // There should be a total of VALID_IMAGE_COUNT URLs added to
        // the LinearLayout.
        assertTrue("Add URLs failed: There should be no URLs in the URL list",
                   linearLayout.getChildCount() == 0);
    }
}
