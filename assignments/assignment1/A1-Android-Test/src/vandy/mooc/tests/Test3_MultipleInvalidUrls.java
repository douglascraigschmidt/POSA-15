package vandy.mooc.tests;

/**
 * Created by Monte Creasor on 2015-05-26.
 */

/**
 * Multiple Invalid URLs Test:
 *
 * ACTIONS: Add, download, display, and deletion of a multiple invalid image URLs.
 * EXPECT RESULTS: Multiple entries in URL list and no  displayed images in the
 * DisplayImageActivity grid view, and no images deleted from the delete action.
 */

public class Test3_MultipleInvalidUrls extends DownloadImagesActivityBaseTest {

    public void testRun() {
        TestUrlsHelper.doTest(mSolo, mInvalidUrlList, 0, mInvalidUrlList.length, false);
    }
}
