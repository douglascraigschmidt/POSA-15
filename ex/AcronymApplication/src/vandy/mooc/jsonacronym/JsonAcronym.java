package vandy.mooc.jsonacronym;

import java.util.ArrayList;
import java.util.List;

/**
 * This "Plain Ol' Java Object" (POJO) class represents data of
 * interest downloaded in Json from the Acronym Service.  We don't
 * care about all the data, just the fields defined in this class.
 */
public class JsonAcronym {
    /**
     * Various tags corresponding to data downloaded in Json from the
     * Acronym Service.
     */
    final public static String sf_JSON = "sf";
    final public static String lfs_JSON = "lfs";
    final public static String lf_JSON = "lf";
    final public static String freq_JSON = "freq";
    final public static String since_JSON = "since";

    /**
     * Various fields corresponding to data downloaded in Json from
     * the Acronym Service.
     */
    private String mLongForm;
    private int mFreq;
    private int mSince;

    /**
     * Constructor that initializes all the fields of interest.
     */
    public JsonAcronym(String longForm,
                       int freq,
                       int since) {
        mLongForm = longForm;
        mFreq = freq;
        mSince = since;
    }
    
    /**
     * No-op constructor.
     */
    public JsonAcronym() {
    }

    /**
     * @return The longForm
     */
    public String getLongForm() {
        return mLongForm;
    }

    /**
     * Set mLongForm.
     */
    public void setLongForm(String longForm) {
        mLongForm = longForm;
    }

    /**
     * @return The freq
     */
    public int getFreq() {
        return mFreq;
    }

    /**
     * Set mFreq.
     */
    public void setFreq(int freq) {
        mFreq = freq;
    }

    /**
     * @return The since
     */
    public int getSince() {
        return mSince;
    }

    /**
     * Set mSince.
     */
    public void setSince(int since) {
        mSince = since;
    }
}
