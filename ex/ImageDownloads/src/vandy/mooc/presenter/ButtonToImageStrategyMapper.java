package vandy.mooc.presenter;

import vandy.mooc.presenter.strategies.ImageStrategy;
import android.util.SparseArray;

/**
 * Maps buttons (represented via their resource ids) to ImageStrategy
 * implementations.
 */
public class ButtonToImageStrategyMapper {
    /**
     * SparseArray maps ints representing buttonIds to ImageStrategy
     * objects.
     */
    private SparseArray<ImageStrategy> mImageStrategyArray =
        new SparseArray<ImageStrategy>();
            
    /**
     * Constructor initializes the field.
     */
    public ButtonToImageStrategyMapper(int[] buttonIds,
                                       ImageStrategy[] buttonStrategys) {
        // Map buttons pushed by the user to the requested type of
        // ImageStrategy.
        for (int i = 0; i < buttonIds.length; ++i)
            mImageStrategyArray.put(buttonIds[i],
                                     buttonStrategys[i]);
    }

    /**
     * Factory method that returns the request ImageStrategy
     * implementation.
     */
    public ImageStrategy getImageStrategy(int buttonId) {
        // Return the designated ImageStrategy.
        return mImageStrategyArray.get(buttonId);
    }
}

