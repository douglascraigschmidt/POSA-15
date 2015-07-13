package vandy.mooc.model.aidl;

import java.util.List;
import vandy.mooc.model.aidl.AcronymExpansion;

/**
 * Interface defining the method that receives callbacks from the
 * AcronymServiceAsync.
 */
interface AcronymResults {
    /**
     * This one-way (non-blocking) method allows AcyronymServiceAsync
     * to return AcronymData results associated with a one-way
     * AcronymRequest.callAcronymRequest() call.
     */
    oneway void sendResults(in List<AcronymExpansion> results);

    /**
     * This one-way (non-blocking) method allows AcyronymServiceAsync
     * to return an error String if the Service fails for some reason.
     */
    oneway void sendError(in String reason);
}
