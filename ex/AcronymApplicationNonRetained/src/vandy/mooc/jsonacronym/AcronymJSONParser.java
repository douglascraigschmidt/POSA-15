package vandy.mooc.jsonacronym;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

/**
 * Parses the Json acronym data returned from the Acronym Services API
 * and returns a List of JsonAcronym objects that contain this data.
 */
public class AcronymJSONParser {
    /**
     * Used for logging purposes.
     */
    private final String TAG =
        this.getClass().getCanonicalName();

    /**
     * Parse the @a inputStream and convert it into a List of JsonAcronym
     * objects.
     */
    public List<JsonAcronym> parseJsonStream(InputStream inputStream)
        throws IOException {

        // Create a JsonReader for the inputStream.
        try (JsonReader reader =
             new JsonReader(new InputStreamReader(inputStream,
                                                  "UTF-8"))) {
            // Log.d(TAG, "Parsing the results returned as an array");

            // Handle the array returned from the Acronym Service.
            return parseAcronymServiceResults(reader);
        }
    }

    /**
     * Parse a Json stream and convert it into a List of JsonAcronym
     * objects.
     */
    public List<JsonAcronym> parseAcronymServiceResults(JsonReader reader)
        throws IOException {

        reader.beginArray();
        try {
            // If the acronym wasn't expanded return null;
            if (reader.peek() == JsonToken.END_ARRAY)
                return null;

            // Create a JsonAcronym object for each element in the
            // Json array.
            return parseAcronymMessage(reader);
        } finally {
            reader.endArray();
        }
    }

    public List<JsonAcronym> parseAcronymMessage(JsonReader reader)
        throws IOException {

        List<JsonAcronym> acronyms = null;
        reader.beginObject();

        try {
            outerloop:
            while (reader.hasNext()) {
                String name = reader.nextName();
                switch (name) {
                case JsonAcronym.sf_JSON:
                    // Log.d(TAG, "reading sf field");
                    reader.nextString();
                    break;
                case JsonAcronym.lfs_JSON:
                    // Log.d(TAG, "reading lfs field");
                    if (reader.peek() == JsonToken.BEGIN_ARRAY)
                        acronyms = parseAcronymLongFormArray(reader);
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
        return acronyms;
    }

    /**
     * Parse a Json stream and convert it into a List of JsonAcronym
     * objects.
     */
    public List<JsonAcronym> parseAcronymLongFormArray(JsonReader reader)
        throws IOException {

        // Log.d(TAG, "reading lfs elements");

        reader.beginArray();

        try {
            List<JsonAcronym> acronyms = new ArrayList<JsonAcronym>();

            while (reader.hasNext()) 
                acronyms.add(parseAcronym(reader));
            
            return acronyms;
        } finally {
            reader.endArray();
        }
    }

    /**
     * Parse a Json stream and return a JsonAcronym object.
     */
    public JsonAcronym parseAcronym(JsonReader reader) 
        throws IOException {

        reader.beginObject();

        JsonAcronym acronym = new JsonAcronym();
        try {
            while (reader.hasNext()) {
                String name = reader.nextName();
                switch (name) {
                case JsonAcronym.lf_JSON:
                    acronym.setLongForm(reader.nextString());
                    // Log.d(TAG, "reading lf " + acronym.getLongForm());
                    break;
                case JsonAcronym.freq_JSON:
                    acronym.setFreq(reader.nextInt());
                    // Log.d(TAG, "reading freq " + acronym.getFreq());
                    break;
                case JsonAcronym.since_JSON:
                    acronym.setSince(reader.nextInt());
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
        return acronym;
    }
}
