package vandy.mooc.tests;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import junit.framework.Assert;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by Monte on 2015-12-22.
 */
public class TestTimeTuningManager {
    /**
     * Students should adjust this value to a value that will
     * ensure that the largest test image can be successfully
     * downloaded. To determine this value, run the
     * Test4_singleValidUrl which downloads and displays the
     * largest image used in this test suite.
     */
    private static final int MAX_DOWNLOAD_SECONDS_PER_IMAGE = 200;
    private static final int MIN_DOWNLOAD_SECONDS_PER_IMAGE = 5;

    /**
     * Shared preference key for saving/restoring the calculated
     * download seconds parameter. This value is calculated
     * automatically when each time Test4_SingleValidUrl is run
     * and then is used to calculate the wait time for downloading
     * multiple images.
     */
    public static final String SHARED_PREF_KEY = "DownloadSecs";

    /**
     * Keeps track of the start time when attempting to
     * determine how long it takes to download the largest
     * image in the test dataset.
     */
    private static long mStartTime;

    /**
     * Since the wait time for the DownloadImageActivity is
     * dependant not only on the connection speed, but also
     * on how many images are being downloaded (and their
     * sizes), this method estimates the download wait time
     * based on the number of expected images that are being
     * downloaded. It does not take into account the size of
     * each image, so the DOWNLOAD_SECONDS_PER_IMAGE should be
     * set to some value greater than the estimated time to
     * download and display the largest image.
     * @param numImages
     * @return
     */
    public static int getDownloadWaitTime(Context context, int numImages) {
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(context);

        int downloadSecsPerImage =
                prefs.getInt(SHARED_PREF_KEY, MAX_DOWNLOAD_SECONDS_PER_IMAGE);

        int secs = downloadSecsPerImage * numImages;

        Log.w("DOWNLOAD TUNING MESSAGE",
                "Estimating download of " + numImages
                        + " images will take " + secs + " seconds.");

        return (int)TimeUnit.SECONDS.toMillis(secs);
    }

    /**
     * Called to get a ridiculously long wait time when downloading
     * the single valid image (Test4_SingleValidImage) the purpose of
     * which is to ensure that the operation can complete even on the
     * slowest of connections. The resulting calculated time can then
     * be used to determine the appropriate wait time for downloading
     * multiple images.
     *
     * @return a very very long wait time, guaranteed to not timeout.
     */
    public static int getMaxDownloadWaitTime() {
        return (int)TimeUnit.SECONDS.toMillis(MAX_DOWNLOAD_SECONDS_PER_IMAGE);
    }

    /**
     * Starts a time before downloading the largest image during
     * the Test4_SingleValidUrl test.
     *
     * @return the start time of the timing operation.
     */
    public static long startTimer() {
        mStartTime = Calendar.getInstance().getTimeInMillis();
        return mStartTime;
    }

    /**
     * Determines the total number of seconds to it took to
     * download the largest image from Test4_SingleValidUrl and
     * saves this value in the registry which can the be retrieved
     * by calling getDownloadWaitTime() used for downloading
     * multiple images.
     *
     * @param context for accessing shared preferences
     * @return the number of seconds it too to download the image.
     */
    public static long stopTimerAndSaveResults(Context context) {
        long stopTime = Calendar.getInstance().getTimeInMillis();
        long duration = stopTime - mStartTime;
        int secs = (int)TimeUnit.MILLISECONDS.toSeconds(duration);
        secs += secs * 0.20;
        secs = Math.max(secs, MIN_DOWNLOAD_SECONDS_PER_IMAGE);

        Log.w("DOWNLOAD TUNING MESSAGE",
                "TestWaitTimeTuningParameter.java automatically adjusting "
                        + "estimated download time for each image to be "
                        + secs + " seconds.");
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(SHARED_PREF_KEY, secs);
        Assert.assertTrue(editor.commit());

        return secs;
    }
}

