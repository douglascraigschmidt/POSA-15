package vandy.mooc;

import java.util.List;

import vandy.mooc.common.ContextView;
import vandy.mooc.common.ModelOps;
import vandy.mooc.common.PresenterOps;
import vandy.mooc.model.aidl.AcronymExpansion;
import vandy.mooc.model.aidl.AcronymResults;

/**
 * Defines the interfaces for the Acronym application that are
 * required and provided by the layers in the Model-View-Presenter
 * (MVP) pattern.  This design ensures loose coupling between the
 * layers in the app's MVP-based architecture.
 */
public interface MVP {
    /**
     * This interface defines the minimum API needed by the
     * AcronymPresenter class in the Presenter layer to interact with
     * AcronymExpansionActivity in the View layer.  It extends the
     * ContextView interface so the Presentation layer can access
     * Context's defined in the View layer.
     */
    public interface RequiredViewOps
           extends ContextView {
        /**
         * Start a new Activity that displays the Acronym Expansions
         * to the user.
         * 
         * @param results
         *            List of AcronymExpansions to display.
         */
        void displayResults(List<AcronymExpansion> results,
                            String failureReason);
    }

    /**
     * This interface defines the minimum public API provided by the
     * AcronymPresenter class in the Presenter layer to the
     * AcronymExpansionActivity in the View layer.  It extends the
     * PresenterOps interface, which is instantiated by the
     * MVP.RequiredViewOps interface used to define the parameter
     * that's passed to the onConfigurationChange() method.
     */
    public interface ProvidedPresenterOps
           extends PresenterOps<MVP.RequiredViewOps> {
        /**
         * Initiate the synchronous acronym lookup when the user
         * presses the "Lookup Acronym Async" button.
         *
         * @return false if a call is already in progress, else true.
         */
        boolean expandAcronymAsync(String acronym);

        /**
         * Initiate the synchronous acronym lookup when the user
         * presses the "Lookup Acronym Sync" button.  It uses an
         * AsyncTask.
         *
         * @return false if a call is already in progress, else true.
         */
        boolean expandAcronymSync(String acronym);
    }

    /**
     * This interface defines the minimum API needed by the
     * AcronymModel class in the Model layer to interact with
     * AcronymPresenter class in the Presenter layer.  Since this
     * interface is identical to the one used by the RequiredViewOps
     * interface it simply extends it.
     */
    public interface RequiredPresenterOps
           extends RequiredViewOps {
    }

    /**
     * This interface defines the minimum public API provided by the
     * AcronymModel class in the Model layer to the AcronymPresenter
     * class in the Presenter layer.  It extends the ModelOps
     * interface, which is parameterized by the
     * MVP.RequiredPresenterOps interface used to define the argument
     * passed to the onConfigurationChange() method.
     */
    public interface ProvidedModelOps
           extends ModelOps<MVP.RequiredPresenterOps> {
        /**
         * Use a two-way synchronous AIDL call to expand the @a
         * acronym parameter.  Must be called in a background thread
         * (e.g., via AsyncTask).
         */
        List<AcronymExpansion> getAcronymExpansions(String acronym);

        /**
         * Use a two-way asynchronous AIDL call to expand the @a
         * acronym parameter.  Need not be called in a background
         * thread since the caller isn't blocked.
         */
        void getAcronymExpansions(String acronym,
                                  AcronymResults presenter);
    }
}
