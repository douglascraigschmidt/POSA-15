package vandy.mooc.tests;

/**
 * Created by Monte Creasor on 2015-05-26.
 */

/**
 * Single Invalid URL Test:
 *
 * ACTIONS: Tests the download handling of a single non-existent image URL.
 * EXPECTED RESULTS: A single entry in the URL list and no image displayed
 * in the DisplayImageActivity grid view.
 */

public class Test2_SingleInvalidUrl extends DownloadImagesActivityBaseTest {

    public void testRun() {
        TestUrlsHelper.doTest(mSolo, mInvalidUrlList, 0, 1, false);
    }
}
