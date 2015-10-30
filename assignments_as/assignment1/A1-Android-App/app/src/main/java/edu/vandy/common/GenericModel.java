package edu.vandy.common;

import android.util.Log;

/**
 * This class provides a framework for mediating access to the Model
 * layer in the Model-View-Presenter pattern.
 */
public class GenericModel<RequiredModelOps,
                          ProvidedModelOps,
                          OpsType extends ModelOps<RequiredModelOps>> {
    /**
     * Debugging tag used by the Android logger.
     */
    protected final String TAG = 
        getClass().getSimpleName();

    /**
     * Instance of the operations ("Ops") type.
     */
    private OpsType mOpsInstance;

    /**
     * Lifecycle hook method that's called when the GenericModelis
     * created.
     *
     * @param opsType 
     *            Class object that's used to create an model
     *            object.  
     * @param presenter
     *            Reference to the RequiredModelOps in the Presenter layer.
     */
    public void onCreate(Class<OpsType> opsType,
                         RequiredModelOps presenter) {
        try {
            // Initialize the GenericModel fields.
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
     * Initialize the GenericModel fields.
     * @throws IllegalAccessException 
     * @throws InstantiationException 
     */
    private void initialize(Class<OpsType> opsType,
                            RequiredModelOps presenter)
            throws InstantiationException, IllegalAccessException {
        // Create the OpsType object.
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

