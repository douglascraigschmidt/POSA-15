package vandy.mooc.services;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import vandy.mooc.aidl.AcronymData;
import vandy.mooc.jsonacronym.AcronymJSONParser;
import vandy.mooc.jsonacronym.JsonAcronym;
import vandy.mooc.utils.ExecutorServiceTimeoutCache;
import vandy.mooc.utils.GenericSingleton;
import android.util.Log;

/**
 * This is the super class for both AcronymServiceSync and
 * AcronymServiceAsync.  It factors out fields and methods that are
 * shared by both Service implementations.
 */
public class AcronymServiceBase extends LifecycleLoggingService {
    /**
     * URL to the Acronym Service web service.
     */
    private String sAcronym_Service_URL =
        "http://www.nactem.ac.uk/software/acromine/dictionary.py?sf=";

    /**
     * Default timeout is 10 seconds, after which the Cache data
     * expires.  In a production app this value should be much higher
     * (e.g., 10 minutes) - we keep it small here to help with
     * testing.
     */
    private int DEFAULT_CACHE_TIMEOUT = 10;

    /**
     * Define a class that will cache the AcronymData since it doesn't
     * change rapidly.  This class is passed to the
     * GenericSingleton.instance() method to retrieve the one and only
     * instance of the AcronymCache.
     */
    public static class AcronymCache 
           extends ExecutorServiceTimeoutCache<String, List<AcronymData>> {}

    /**
     * Hook method called when the Service is created.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        // Increment the reference count for the AcronymCache
        // singleton, which is shared by both Services.
        GenericSingleton.instance(AcronymCache.class).incrementRefCount();
    }

    /**
     * Hook method called when the last client unbinds from the
     * Service.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        // Decrement the reference count for the AcronymCache
        // singleton, which shuts it down when the count drops to 0.
        GenericSingleton.instance(AcronymCache.class).decrementRefCount();
    }

    /**
     * Contitionally queries the Acronym Service web service to obtain
     * a List of AcronymData corresponding to the @a acronym if it's
     * been more than 10 seconds since the last query to the Acronym
     * Service.  Otherwise, simply return the cached results.
     */
    protected List<AcronymData> getAcronymResults(String acronym) {
        Log.d(TAG,
              "Looking up results in the cache for "
              + acronym);

        // Try to get the results from the AcronymCache.
        List<AcronymData> results =
            GenericSingleton.instance(AcronymCache.class).get(acronym);

        if (results != null) {
            Log.d(TAG,
                  "Getting results from the cache for "
                  + acronym);
           
            // Return the results from the cache.
            return results;
        } else {
            Log.d(TAG,
                  "Getting results from the Acronym Service for "
                  + acronym);

            // The results weren't already in the cache or were
            // "stale", so obtain them from the Acronym Service.
            results = getResultsFromAcronymService(acronym);

            if (results != null)
                // Store the results into the cache for up to
                // DEFAULT_CACHE_TIMEOUT seconds based on the location
                // and return the results.
                GenericSingleton.instance(AcronymCache.class).put
                    (acronym,
                     results,
                     DEFAULT_CACHE_TIMEOUT);
            return results;
        }
    }

    /**
     * Actually query the Acronym Service web service to get the
     * current AcronymData.
     */
    private List<AcronymData> getResultsFromAcronymService(String acronym) {
        // Create a List that will return the AcronymData obtained
        // from the Acronym Service web service.
        final List<AcronymData> returnList = 
            new ArrayList<AcronymData>();
            
        // A List of JsonAcronym objects.
        List<JsonAcronym> jsonAcronyms = null;

        try {
            // Create a URL that points to desired location the
            // Acronym Service.
            final URL url = new URL(sAcronym_Service_URL
                                    + URLEncoder.encode(acronym,
                                                        "UTF-8"));
            // Opens a connection to the Acronym Service.
            HttpURLConnection urlConnection =
                (HttpURLConnection) url.openConnection();

            // Sends the GET request and returns a stream containing
            // the Json results.
            try (InputStream in =
                 new BufferedInputStream(urlConnection.getInputStream())) {
                    // Create the parser.
                 final AcronymJSONParser parser =
                     new AcronymJSONParser();
            
                // Parse the Json results and create JsonAcronym data
                // objects.
                jsonAcronyms = parser.parseJsonStream(in);
            } finally {
                urlConnection.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        // See if we parsed any valid data.
        if (jsonAcronyms != null 
            && jsonAcronyms.size() > 0) {
            // Convert the JsonAcronym data objects to our AcronymData
            // object, which can be passed between processes.
            for (JsonAcronym jsonAcronym : jsonAcronyms) 
                returnList.add(new AcronymData(jsonAcronym.getLongForm(),
                                               jsonAcronym.getFreq(),
                                               jsonAcronym.getSince()));
            
            // Return the List of AcronymData.
            return returnList;
        } else {
            return null;
        }
    }
}
