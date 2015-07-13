package vandy.mooc.model.services;

import java.util.ArrayList;
import java.util.List;

import vandy.mooc.model.aidl.AcronymCall;
import vandy.mooc.model.aidl.AcronymExpansion;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

/**
 * @class AcronymServiceSync
 * 
 * @brief This class uses synchronous AIDL interactions to expand
 *        acronyms via an Acronym Web service.  The AcronymModel that
 *        binds to this Service will receive an IBinder that's an
 *        instance of AcronymCall, which extends IBinder.  The
 *        AcronymModel can then interact with this Service by making
 *        two-way method calls on the AcronymCall object asking this
 *        Service to lookup the meaning of the Acronym string.  After
 *        the lookup is finished, this Service sends the Acronym
 *        results back to the AcronymModel by returning a List of
 *        AcronymData.
 * 
 *        AIDL is an example of the Broker Pattern, in which all
 *        interprocess communication details are hidden behind the
 *        AIDL interfaces.
 */
public class AcronymServiceSync
       extends AcronymServiceBase {
    /**
     * Factory method that makes an Intent used to start the
     * AcronymServiceSync when passed to bindService().
     * 
     * @param context
     *            The context of the calling component.
     */
    public static Intent makeIntent(Context context) {
        return new Intent(context,
                          AcronymServiceSync.class);
    }

    /**
     * Called when a client (e.g., AcronymModel) calls bindService()
     * with the proper Intent.  Returns the implementation of
     * AcronymCall, which is implicitly cast as an IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAcronymCallImpl;
    }

    /**
     * The concrete implementation of the AIDL Interface AcronymCall,
     * which extends the Stub class that implements AcronymCall,
     * thereby allowing Android to handle calls across process
     * boundaries.  This method runs in a separate Thread as part of
     * the Android Binder framework.
     * 
     * This implementation plays the role of Invoker in the Broker
     * Pattern.
     */
    private final AcronymCall.Stub mAcronymCallImpl =
        new AcronymCall.Stub() {
            /**
             * Implement the AIDL AcronymCall expandAcronym() method,
             * which forwards to getAcronymResults() to obtain the
             * results and then returns these results back to the
             * AcronymModel.
             */
            @Override
            public List<AcronymExpansion> expandAcronym(String acronym)
                throws RemoteException {

                // Call the Acronym Web service to get the list of
                // possible expansions of the designated acronym.
                final List<AcronymExpansion> acronymExpansions = 
                    getAcronymExpansions(acronym);

                if (acronymExpansions != null) {
                    Log.d(TAG, "" 
                          + acronymExpansions.size() 
                          + " results for acronym: " 
                          + acronym);

                    // Return the list of acronym expansions back to
                    // the AcronymModel.
                    return acronymExpansions;
                } else 
                    // Create a zero-sized acronymResults object to
                    // indicate to the caller that the acronym had no
                    // expansions.
                    return new ArrayList<AcronymExpansion>();
            }
	};
}
