package vandy.mooc.utils.loader;


/**
 * POJO that stores the information for a single 
 * ImageLoaderTask's processing
 */
public class ImageLoaderWorkOrder {
    
    /**
     * ImageViewHolder wrapping the ImageView that 
     * will display the image
     */
    private ImageViewHolder mImageViewHolder;
    
    /**
     * The filepath of the image to load
     */
    private String mFilePath;
    
    /**
     * The target width
     */
    private int mTargetWidth;
    
    /**
     * The target height
     */
    private int mTargetHeight;
    
    /**
     * Constructor initializes fields
     */
    public ImageLoaderWorkOrder(ImageViewHolder imageViewHolder,
                                String filepath,
                                int targetWidth,
                                int targetHeight) {
        mImageViewHolder = imageViewHolder;
        mFilePath = filepath;
        mTargetWidth = targetWidth;
        mTargetHeight = targetHeight;
    }

    /*
     * Getters and setters
     */
    
    public ImageViewHolder getmImageViewHolder() {
        return mImageViewHolder;
    }

    public void setmImageViewHolder(ImageViewHolder mImageViewHolder) {
        this.mImageViewHolder = mImageViewHolder;
    }

    public String getmFilePath() {
        return mFilePath;
    }

    public void setmFilePath(String mFilePath) {
        this.mFilePath = mFilePath;
    }

    public int getmTargetWidth() {
        return mTargetWidth;
    }

    public void setmTargetWidth(int mTargetWidth) {
        this.mTargetWidth = mTargetWidth;
    }

    public int getmTargetHeight() {
        return mTargetHeight;
    }

    public void setmTargetHeight(int mTargetHeight) {
        this.mTargetHeight = mTargetHeight;
    }
}
