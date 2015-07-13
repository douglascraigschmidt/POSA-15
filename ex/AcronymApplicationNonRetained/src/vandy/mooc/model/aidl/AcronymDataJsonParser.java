package vandy.mooc.model.aidl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.util.JsonReader;
import android.util.JsonToken;

/**
 * Parses the Json acronym data returned from the Acronym Services API
 * and returns a List of JsonAcronym objects that contain this data.
 */
public class AcronymDataJsonParser {
    /**
     * Used for logging purposes.
     */
    private final String TAG =
        this.getClass().getCanonicalName();

    /**
     * Parse the @a inputStream and convert it into a List of AcronymData
     * objects.
     */
    public List<AcronymExpansion> parseJsonStream(InputStream inputStream)
        throws IOException {

        // Create a JsonReader for the inputStream.
        try (JsonReader reader =
             new JsonReader(new InputStreamReader(inputStream,
                                                  "UTF-8"))) {
            // Log.d(TAG, "Parsing the results returned as an array");

            // Handle the array returned from the Acronym Service.
            return parseAcronymWebServiceResults(reader);
        }
    }

    /**
     * Parse a Json stream and convert it into a List of AcronymData
     * objects.
     */
    public List<AcronymExpansion> parseAcronymWebServiceResults(JsonReader reader)
        throws IOException {

        reader.beginArray();
        try {
            // If the acronym wasn't expanded return null;
            if (reader.peek() == JsonToken.END_ARRAY)
                return null;

            // Create a AcronymData object for each element in the
            // Json array.
            return parseAcronymData(reader);
        } finally {
            reader.endArray();
        }
    }

    public List<AcronymExpansion> parseAcronymData(JsonReader reader)
        throws IOException {

        List<AcronymExpansion> acronymExpansion = null;
        reader.beginObject();

        try {
            outerloop:
            while (reader.hasNext()) {
                String name = reader.nextName();
                switch (name) {
                case AcronymData.sf_JSON:
                    // Log.d(TAG, "reading sf field");
                    reader.nextString();
                    break;
                case AcronymData.lfs_JSON:
                    // Log.d(TAG, "reading lfs field");
                    if (reader.peek() == JsonToken.BEGIN_ARRAY)
                        acronymExpansion = parseAcronymLongFormArray(reader);
                    break outerloop;
                default:
		    reader.skipValue();
                    // Log.d(TAG, "weird problem with " + name + " field");
                    break;
                }
            }
        } finally {
                reader.endObject();
        }
        return acronymExpansion;
    }

    /**
     * Parse a Json stream and convert it into a List of AcronymData
     * objects.
     */
    public List<AcronymExpansion> parseAcronymLongFormArray(JsonReader reader)
        throws IOException {

        // Log.d(TAG, "reading lfs elements");

        reader.beginArray();

        try {
            List<AcronymExpansion> acronyms = new ArrayList<AcronymExpansion>();

            while (reader.hasNext()) 
                acronyms.add(parseAcronymExpansion(reader));
            
            return acronyms;
        } finally {
            reader.endArray();
        }
    }

    /**
     * Parse a Json stream and return a AcronymExpansion object.
     */
    public AcronymExpansion parseAcronymExpansion(JsonReader reader) 
        throws IOException {

        reader.beginObject();

        AcronymExpansion acronymExpansion = new AcronymExpansion();
        try {
            while (reader.hasNext()) {
                String name = reader.nextName();
                switch (name) {
                case AcronymExpansion.lf_JSON:
                    acronymExpansion.setLf(reader.nextString());
                    // Log.d(TAG, "reading lf " + acronym.getLongForm());
                    break;
                case AcronymExpansion.freq_JSON:
                    acronymExpansion.setFreq(reader.nextInt());
                    // Log.d(TAG, "reading freq " + acronym.getFreq());
                    break;
                case AcronymExpansion.since_JSON:
                    acronymExpansion.setSince(reader.nextInt());
                    // Log.d(TAG, "reading since " + acronym.getSince());
                    break;
                default:
                    reader.skipValue();
                    // Log.d(TAG, "ignoring " + name);
                    break;
                }
            } 
        } finally {
                reader.endObject();
        }
        return acronymExpansion;
    }
}
