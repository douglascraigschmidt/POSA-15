package vandy.mooc.common;

/**
 * The base interface that an operations ("Ops") class in the
 * Presenter layer must implement.
 */
public interface PresenterOps<RequiredViewOps> {
    /**
     * Hook method dispatched by the GenericActivity framework to
     * initialize an operations ("Ops") object after it's been
     * instantiated.
     *
     * @param view
     *        The currently active RequiredViewOps.
     */
    void onCreate(RequiredViewOps view);

    /**
     * Hook method called when an Ops object in the Presenter layer is
     * destroyed.
     *
     * @param isChangingConfigurations
     *        True if a runtime configuration triggered the onDestroy() call.
     */
    void onDestroy(boolean isChangingConfigurations);
}
