package edu.vandy.common;

import android.util.Log;

/**
 * This Activity provides a framework that enables a Presenter to
 * access an object residing in the Model layer in the
 * Model-View-Presenter (MVP) pattern.
 */
public class GenericPresenter<RequiredPresenterOps,
                              ProvidedModelOps,
                              ModelType extends ModelOps<RequiredPresenterOps>> {
    /**
     * Debugging tag used by the Android logger.
     */
    protected final String TAG = 
        getClass().getSimpleName();

    /**
     * Instance of the operations ("Ops") type.
     */
    private ModelType mOpsInstance;

    /**
     * Lifecycle hook method that's called when the GenericPresenteris
     * created.
     *
     * @param opsType 
     *            Class object that's used to create an model
     *            object.  
     * @param presenter
     *            Reference to the RequiredPresenterOps in the Presenter layer.
     */
    public void onCreate(Class<ModelType> opsType,
                         RequiredPresenterOps presenter) {
        try {
            // Initialize the GenericPresenter fields.
            initialize(opsType,
                       presenter);
        } catch (InstantiationException
                 | IllegalAccessException e) {
            Log.d(TAG, 
                  "handleConfiguration " 
                  + e);
            // Propagate this as a runtime exception.
            throw new RuntimeException(e);
        }
    }

    /**
     * Initialize the GenericPresenter fields.
     * @throws IllegalAccessException 
     * @throws InstantiationException 
     */
    private void initialize(Class<ModelType> opsType,
                            RequiredPresenterOps presenter)
            throws InstantiationException, IllegalAccessException {
        // Create the ModelType object.
        mOpsInstance = opsType.newInstance();

        // Perform the first initialization.
        mOpsInstance.onCreate(presenter);
    }

    /**
     * Return the initialized ProvidedOps instance for use by the
     * application.
     */
    @SuppressWarnings("unchecked")
    public ProvidedModelOps getModel() {
        return (ProvidedModelOps) mOpsInstance;
    }
}

