package edu.vandy.common;

import android.content.Context;
import android.util.Log;

/**
 * This Activity provides a framework for mediating access to a object
 * residing in the Presenter layer in the Model-View-Presenter (MVP)
 * pattern.  It automatically handles runtime configuration changes in
 * conjunction with an instance of PresenterType, which must implement
 * the PresenterOps interface.  It extends LifecycleLoggingActivity so
 * that all lifecycle hook method calls are automatically logged.  It
 * also implements the ContextView interface that provides access to
 * the Activity and Application contexts in the View layer.
 *
 * The three types used by a GenericActivity are the following:
 * <ol>
 *     <li><code>RequiredViewOps</code>, the class or interface that
 *     defines the methods available to the Presenter object from the
 *     View layer.</li> 
 *     <li><code>ProvidedPresenterOps</code>, the class or interface
 *     that defines the methods available to the View layer from the
 *     Presenter object.</li>
 *     <li><code>PresenterType</code>, the class created/used by the
 *     GenericActivity framework to implement an Presenter object.</li>
 * </ol>
 */
public abstract class GenericActivity<RequiredViewOps,
                                      ProvidedPresenterOps,
                                      PresenterType extends PresenterOps<RequiredViewOps>>
       extends LifecycleLoggingActivity
       implements ContextView {
    /**
     * Used to retain the ProvidedPresenterOps state between runtime
     * configuration changes.
     */
    private final RetainedFragmentManager mRetainedFragmentManager 
        = new RetainedFragmentManager(this.getFragmentManager(),
                                      TAG);
 
    /**
     * Instance of the Presenter type.
     */
    private PresenterType mPresenterInstance;

    /**
     * Initialize or reinitialize the Presenter layer.  This must be
     * called *after* the onCreate(Bundle saveInstanceState) method.
     *
     * @param opsType 
     *            Class object that's used to create a Presenter object.
     * @param view
     *            Reference to the RequiredViewOps object in the View layer.
     */
    public void onCreate(Class<PresenterType> opsType,
                         RequiredViewOps view) {
        // Handle configuration-related events, including the initial
        // creation of an Activity and any subsequent runtime
        // configuration changes.
        try {
            // If this method returns true it's the first time the
            // Activity has been created.
            if (mRetainedFragmentManager.firstTimeIn()) {
                Log.d(TAG,
                      "First time calling onCreate()");

                // Initialize the GenericActivity fields.
                initialize(opsType,
                           view);
            } else {
                Log.d(TAG,
                      "Second (or subsequent) time calling onCreate()");

                // The RetainedFragmentManager was previously
                // initialized, which means that a runtime
                // configuration change occurred.
                reinitialize(opsType,
                             view);
            }
        } catch (InstantiationException
                 | IllegalAccessException e) {
            Log.d(TAG, 
                  "onCreate() " 
                  + e);
            // Propagate this as a runtime exception.
            throw new RuntimeException(e);
        }
    }

    /**
     * Return the initialized ProvidedPresenterOps instance for use by
     * application logic in the View layer.
     */
    @SuppressWarnings("unchecked")
    public ProvidedPresenterOps getPresenter() {
        return (ProvidedPresenterOps) mPresenterInstance;
    }

    /**
     * Return the RetainedFragmentManager.
     */
    public RetainedFragmentManager getRetainedFragmentManager() {
        return mRetainedFragmentManager;
    }

    /**
     * Initialize the GenericActivity fields.
     * @throws IllegalAccessException 
     * @throws InstantiationException 
     */
    private void initialize(Class<PresenterType> opsType,
                            RequiredViewOps view)
            throws InstantiationException, IllegalAccessException {
        // Create the PresenterType object.
        mPresenterInstance = opsType.newInstance();

        // Put the PresenterInstance into the RetainedFragmentManager under
        // the simple name.
        mRetainedFragmentManager.put(opsType.getSimpleName(),
                                     mPresenterInstance);

        // Perform the first initialization.
        mPresenterInstance.onCreate(view);
    }

    /**
     * Reinitialize the GenericActivity fields after a runtime
     * configuration change.
     * @throws IllegalAccessException 
     * @throws InstantiationException 
     */
    private void reinitialize(Class<PresenterType> opsType,
                              RequiredViewOps view)
            throws InstantiationException, IllegalAccessException {
        // Try to obtain the PresenterType instance from the
        // RetainedFragmentManager.
        mPresenterInstance =
            mRetainedFragmentManager.get(opsType.getSimpleName());

        // This check shouldn't be necessary under normal
        // circumstances, but it's better to lose state than to
        // crash!
        if (mPresenterInstance == null) 
            // Initialize the GenericActivity fields.
            initialize(opsType,
                       view);
        else
            // Inform it that the runtime configuration change has
            // completed.
            mPresenterInstance.onConfigurationChange(view);
    }

    /**
     * Return the Activity context.
     */
    @Override
    public Context getActivityContext() {
        return this;
    }
    
    /**
     * Return the Application context.
     */
    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }
}
