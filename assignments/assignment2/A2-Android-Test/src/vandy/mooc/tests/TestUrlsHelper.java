package vandy.mooc.tests;

import junit.framework.Assert;
import vandy.mooc.view.DisplayImagesActivity;
import vandy.mooc.view.DownloadImagesActivity;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.robotium.solo.Solo;

/**
 * Created by Monte Creasor on 2015-05-26.
 * Modify by Monte Creasor on 2015-06-13.
 */

/**
 * Helper class used by most tests to test downloading of image URLs.
 * The single doTest() member function supports the downloading of
 * single and multiple images URLs.
 */

public class TestUrlsHelper {
    /**
     * Some useful delay/timer values.
     */
    public static int SECOND = 1000;
    public static int shortDelay = 2 * SECOND;
    public static int mediumDelay = 5 * SECOND;
    public static int longDelay = 10 * SECOND;
    public static int veryLongDelay = 60 * SECOND;

    /**
     * Downloads and deletes the specified number of images from the
     * passed array of image URLs.
     *
     * @param solo         instance of solo
     * @param urlList      list of URLs to download
     * @param validCount   number of valid images in urlList
     * @param invalidCount number of invalid images in urlList
     * @param doRotations  if true, perform double screen rotation
     */
    public static void doTest(
            Solo solo,
            String[] urlList,
            int validCount,
            int invalidCount,
            boolean doRotations) {

        int count = validCount + invalidCount;

        Assert.assertTrue("Invocation Error: passed URL "
                        + "counts exceed passed URL array.",
                count <= urlList.length);

        // Wait for main activity.
        Assert.assertTrue(
                "Test failed: MainActivity did not load correctly.",
                solo.waitForActivity(DownloadImagesActivity.class));

        // Click on url text view.
        solo.clickOnView(solo.getView(vandy.mooc.R.id.url));

        for (int i = 0; i < count; i++) {
            String url = urlList[i];

            // Ensure that the edit view is clear.
            solo.clearEditText((android.widget.EditText)
                    solo.getView(vandy.mooc.R.id.url));

            // Add a url into the url text view.
            solo.enterText((android.widget.EditText)
                    solo.getView(vandy.mooc.R.id.url), url);

            // Ensure that the url was entered into the edit view.
            Assert.assertTrue("Test failed URL was not correctly "
                            + "entered in the URL text view.",
                    solo.searchText(url));

            // Hide the soft keyboard
            solo.hideSoftKeyboard();

            // Click on Add URL button to add the url to the list view.
            solo.clickOnView(solo.getView(vandy.mooc.R.id.button2));

            // Now check if the URL was added to the list view.
            Assert.assertTrue("Test failed: URL was not added to list.",
                    solo.waitForText(url));
        }

        // Now check if the proper number of URLs were added as
        // children to the linear layout.
        LinearLayout linearLayout = (LinearLayout)
                solo.getView(vandy.mooc.R.id.linearLayout);

        // There should be a total of count URLs added to the LinearLayout.
        Assert.assertTrue("Test failed: There should be "
                        + count + " URLs in the list.",
                linearLayout.getChildCount() == count);

        // Now click on the Download Images button.
        solo.clickOnView(solo.getView(vandy.mooc.R.id.downloadFabButton));

        if (doRotations) {
            // Rotate the screen
            solo.setActivityOrientation(Solo.LANDSCAPE);
            solo.sleep(shortDelay);
        }

        for (int i = 0; i < invalidCount; i++) {
            // Check for invalid URL toasts.
            Assert.assertTrue("Test failed: No Toast shown for "
                            + invalidCount + " invalid URL(s)",
                    solo.waitForText("failed to download!"));
        }

        if (validCount == 0) {
            // Ensure that the display images activity was started.
            Assert.assertFalse("Test failed: DownloadImageActivity should"
                            + " not start since there were no valid images.",
                    solo.waitForActivity(
                            DisplayImagesActivity.class, shortDelay));
        } else {
            // Ensure that the display images activity was started.
            Assert.assertTrue("Test failed: DisplayImagesActivity failed to start",
                    solo.waitForActivity(DisplayImagesActivity.class, veryLongDelay));

            // Now check if the proper number of images were
            // successfully downloaded.
            GridView view = (GridView)
                    solo.getView(vandy.mooc.R.id.imageGrid);

            // Ensure that all the appropriate number
            // of images are displayed in the grid view.
            Assert.assertTrue("Test failed: Grid is not displaying "
                            + validCount + " valid images.",
                    view.getCount() == validCount);

            /*
            // Allow user time to view the images before
            // closing (not really necessary on emulator).
            solo.sleep(shortDelay);
            */

            // Go directly back to the DownloadImagesActivity.
            // closing (not really necessary).
            solo.goBackToActivity("DownloadImagesActivity");

            /*
            // Invoke the back command to return to
            // the previous activity (MainActivity).
            // This method causes more problems than
            // calling goBackToActivity().
            solo.goBack();
            */

            Assert.assertTrue("Test failed: DownloadImagesActivity did not load"
                            + " after returning from DisplayImageActivity",
                    solo.waitForActivity(DownloadImagesActivity.class));
        }

        if (doRotations) {
            // Rotate the screen back to portrait.
            solo.setActivityOrientation(Solo.PORTRAIT);

            // Give the rotation time to settle.
            solo.sleep(shortDelay);

            // Wait for activity
            Assert.assertTrue("Test failed: DownloadImagesActivity did not"
                            + " correctly load after second rotation.",
                    solo.waitForActivity(DownloadImagesActivity.class));
        }

        if (validCount > 0) {
            // Finally, click on delete image
            // button to delete all the images.
            solo.clickOnView(solo.getView(vandy.mooc.R.id.clearFabButton));

            // Handles single image deletion toast string.
            Assert.assertTrue("Test failed: Unable to delete "
                            + validCount
                            + " downloaded image(s).",
                    solo.waitForText(validCount + " downloaded image"));
        }
    }
}
