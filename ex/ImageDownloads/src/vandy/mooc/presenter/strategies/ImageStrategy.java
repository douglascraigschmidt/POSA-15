package vandy.mooc.presenter.strategies;

/**
 * Implement this interface to define the strategy used to download
 * and display an image when the user clicks a button.  This interface
 * plays the role of the "Strategy" in the Strategy pattern.
 */
public interface ImageStrategy {
    /**
     * Download and display a bitmap image, which is guided by the
     * DownloadContext.
     */
    void downloadAndDisplay(DownloadContext downloadContext);

    /**
     * Cancel the download/display of an image.
     */
    void cancel(DownloadContext downloadContext);
}
