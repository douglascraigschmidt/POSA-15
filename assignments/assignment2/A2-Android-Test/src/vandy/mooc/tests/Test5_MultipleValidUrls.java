package vandy.mooc.tests;

/**
 * Created by Monte Creasor on 2015-05-26.
 */

/**
 * Multiple Valid URLs Test:
 *
 * ACTIONS: Add, download, display, and deletion of a multiple valid image URLs.
 * EXPECT RESULTS: Multiple entries in URL list and the same number of
 * displayed images in the DisplayImageActivity grid view, and the same number
 * of images deleted from the delete action.
 */
public class Test5_MultipleValidUrls extends DownloadImagesActivityBaseTest {
    public void testRun() {
        TestUrlsHelper.doTest(mSolo,
                              mValidUrlList,
                              mValidUrlList.length,
                              0,
                              false);
    }
}

