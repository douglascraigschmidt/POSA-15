package vandy.mooc.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import vandy.mooc.aidl.AcronymData;
import vandy.mooc.jsonacronym.AcronymJSONParser;
import vandy.mooc.jsonacronym.JsonAcronym;
import android.app.Activity;
import android.content.Context;
import android.os.IBinder;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

/**
 * @class AcronymDownloadUtils
 *
 * @brief Handles the actual downloading of Acronym information from
 *        the Acronym web service.
 */
public class Utils {
    /**
     * Logging tag used by the debugger. 
     */
    private final static String TAG = Utils.class.getCanonicalName();

    /** 
     * URL to the Acronym web service.
     */
    private final static String sAcronym_Web_Service_URL =
        "http://www.nactem.ac.uk/software/acromine/dictionary.py?sf=";

    /**
     * Obtain the Acronym information.
     * 
     * @return The information that responds to your current acronym search.
     */
    public static List<AcronymData> getResults(final String acronym) {
        // Create a List that will return the AcronymData obtained
        // from the Acronym Service web service.
        final List<AcronymData> returnList = 
            new ArrayList<AcronymData>();
            
        // A List of JsonAcronym objects.
        List<JsonAcronym> jsonAcronyms = null;

        try {
            // Append the location to create the full URL.
            final URL url =
                new URL(sAcronym_Web_Service_URL
                        + acronym);

            // Opens a connection to the Acronym Service.
            HttpURLConnection urlConnection =
                (HttpURLConnection) url.openConnection();
            
            // Sends the GET request and reads the Json results.
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
        }

        // See if we parsed any valid data.
        if (jsonAcronyms != null && jsonAcronyms.size() > 0) {
            // Convert the JsonAcronym data objects to our AcronymData
            // object, which can be passed between processes.
            for (JsonAcronym jsonAcronym : jsonAcronyms)
                returnList.add(new AcronymData(jsonAcronym.getLongForm(),
                                               jsonAcronym.getFreq(),
                                               jsonAcronym.getSince()));
             // Return the List of AcronymData.
             return returnList;
        }  else
            return null;
    }

    /**
     * This method is used to hide a keyboard after a user has
     * finished typing the url.
     */
    public static void hideKeyboard(Activity activity,
                                    IBinder windowToken) {
        InputMethodManager mgr =
           (InputMethodManager) activity.getSystemService
            (Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken,
                                    0);
    }

    /**
     * Show a toast message.
     */
    public static void showToast(Context context,
                                 String message) {
        Toast.makeText(context,
                       message,
                       Toast.LENGTH_SHORT).show();
    }

    /**
     * Ensure this class is only used as a utility.
     */
    private Utils() {
        throw new AssertionError();
    } 
}
