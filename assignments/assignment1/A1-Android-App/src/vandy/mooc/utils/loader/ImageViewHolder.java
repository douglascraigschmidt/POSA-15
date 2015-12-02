package vandy.mooc.utils.loader;

import java.lang.ref.WeakReference;

import android.widget.ImageView;

/**
 * Container that holds an image view in a weak reference.
 */
public class ImageViewHolder {
    /**
     * WeakReference to an ImageView object to enable garbage
     * collection.
     */
    protected WeakReference<ImageView> mImgView;

    /**
     * Constructor initializes the field.
     */
    public ImageViewHolder(ImageView imgView) {
        mImgView = new WeakReference<ImageView>(imgView);
    }

    /**
     * Getter for the wrapped ImageView. isCollected() should be
     * called before calling this method.
     */
    public ImageView getWrappedImageView() {
        return mImgView.get();
    }
    
    /**
     * Returns true if the wrapped ImageView has been garbage
     * collected, false otherwise.
     */
    public boolean isCollected() {
        return mImgView.get() == null;
    }
}
