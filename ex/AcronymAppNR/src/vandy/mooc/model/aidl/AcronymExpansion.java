package vandy.mooc.model.aidl;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Inner class that contains data for each Acronym Expansion.
 * This POJO implements the Parcelable interface to pass data
 * between AcronymActivity and DisplayAcronymExpansionActivity.
 * Parcelable defines an interface for marshaling/de-marshaling
 * https://en.wikipedia.org/wiki/Marshalling_(computer_science)
 * to/from a format that Android uses to allow data transport
 * between processes on a device.  Discussion of the details of
 * Parcelable is outside the scope of this assignment, but you can
 * read more at
 * https://developer.android.com/reference/android/os/Parcelable.html.
 */
public class AcronymExpansion        
       implements Parcelable {
    /**
     * Various tags corresponding to data downloaded in Json from the
     * Acronym Service.
     */
    final public static String lf_JSON = "lf";
    final public static String freq_JSON = "freq";
    final public static String since_JSON = "since";

    /*
     * These data members are the local variables that will store
     * the AcronymExpansion's state.
     */

    /**
     * The long form of the acronym (spelled out version).
     */
    private String lf;

    /**
     * The relative frequency of usage in print, of this meaning
     * of the acronym.
     */
    private int freq;

    /**
     * The year the acronym was added to this database of
     * acronyms, or was originally termed.
     */
    private int since;
        
    /**
     * Default constructor.
     */
    public AcronymExpansion() {
            
    }
        
    /**
     * Constructor that initialises an AcronymExpansion object
     * from its parameters.
     */
    public AcronymExpansion(String lf,
                            int freq,
                            int since) {
        this.lf = lf;
        this.freq = freq;
        this.since = since;
    }

    /**
     * Private constructor provided for the CREATOR interface,
     * which is used to de-marshal an AcronymExpansion from the
     * Parcel of data.
     */
    private AcronymExpansion(Parcel in) {
        lf = in.readString();
        freq = in.readInt();
        since = in.readInt();
    }

    /*
     * Getters and setters to access AcronymExpansion.
     */

    public String getLf() {
        return lf;
    }

    public void setLf(String lf) {
        this.lf = lf;
    }

    public int getFreq() {
        return freq;
    }

    public void setFreq(int freq) {
        this.freq = freq;
    }

    public int getSince() {
        return since;
    }

    public void setSince(int since) {
        this.since = since;
    }

    /**
     * The toString() custom implementation.
     */
    @Override
        public String toString() {
        return "AcronymExpansion [lf=" 
            + lf 
            + ", freq=" 
            + freq 
            + ", since=" 
            + since
            + "]";
    }

    /*
     * Parcelable related methods.
     */

    /**
     * A bitmask indicating the set of special object types
     * marshaled by the Parcelable.
     */
    @Override
        public int describeContents() {
        return 0;
    }

    /**
     * Marshal this ParcelableAcronymExpansion to the target Parcel.
     */
    @Override
        public void writeToParcel(Parcel dest,
                                  int flags) {
        dest.writeString(lf);
        dest.writeInt(freq);
        dest.writeInt(since);
    }

    /**
     * public Parcelable.Creator for AcronymExpansion,
     * which is an interface that must be implemented and provided
     * as a public CREATOR field that generates instances of your
     * Parcelable class from a Parcel.
     */
    public static final Parcelable.Creator<AcronymExpansion> CREATOR =
        new Parcelable.Creator<AcronymExpansion>() {
        public AcronymExpansion createFromParcel(Parcel in) {
            return new AcronymExpansion(in);
        }

        public AcronymExpansion[] newArray(int size) {
            return new AcronymExpansion[size];
        }
    };
}
