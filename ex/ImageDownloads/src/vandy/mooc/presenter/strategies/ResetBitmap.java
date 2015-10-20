package vandy.mooc.presenter.strategies;

import vandy.mooc.R;

/**
 * This class resets the image displayed to user with default image.
 */
public class ResetBitmap
       implements ImageStrategy {
    /**
     * Replace the current image with the default image.
     */
    public void downloadAndDisplay(DownloadContext downloadContext) {
        downloadContext.showToast("Resetting image to the default");
        downloadContext.resetBitmap();
    }

    /**
     * "Cancel" a download.
     */
    @Override
    public void cancel(DownloadContext downloadContext) {
        // No-op.
    }
}
