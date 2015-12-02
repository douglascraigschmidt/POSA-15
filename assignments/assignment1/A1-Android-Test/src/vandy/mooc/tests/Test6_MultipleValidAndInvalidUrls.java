package vandy.mooc.tests;

/**
 * Created by Monte Creasor on 2015-05-26.
 */

/**
 * Multiple Valid and Invalid URLs Test]:
 *
 * ACTIONS: Add, download, display, and deletion of a multiple valid and
 * invalid image URLs.
 * EXPECT RESULTS: Multiple entries in URL list and only the valid image URLs
 * displayed images in the  DisplayImageActivity grid view, and only the valid
 * images deleted from the delete action.
 */

public class Test6_MultipleValidAndInvalidUrls extends DownloadImagesActivityBaseTest {
    public void testRun() {
        TestUrlsHelper.doTest(
                mSolo,
                mMixedUrlList,
                VALID_MIXED_IMAGE_COUNT,
                INVALID_MIXED_IMAGE_COUNT,
                false);
    }
}
