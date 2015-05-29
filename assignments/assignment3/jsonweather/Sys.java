package vandy.mooc.jsonweather;

/**
 * This "Plain Ol' Java Object" (POJO) class represents system data
 * downloaded in Json from the Weather Service.
 */
public class Sys {
    /**
     * Various tags corresponding to system data downloaded in Json
     * from the Weather Service.
     */
    public final static String message_JSON = "message";
    public final static String country_JSON = "country";
    public final static String sunrise_JSON = "sunrise";
    public final static String sunset_JSON = "sunset";

    /**
     * Various fields corresponding to system data downloaded in Json
     * from the Weather Service.
     */
    private double mMessage;
    private String mCountry;
    private long mSunrise;
    private long mSunset;

    /**
     * @return The message
     */
    public double getMessage() {
        return mMessage;
    }

    /**
     * @param message
     *            The message
     */
    public void setMessage(double message) {
        mMessage = message;
    }

    /**
     * @return The country
     */
    public String getCountry() {
        return mCountry;
    }

    /**
     * @param country
     *            The country
     */
    public void setCountry(String country) {
        mCountry = country;
    }

    /**
     * @return The sunrise
     */
    public long getSunrise() {
        return mSunrise;
    }

    /**
     * @param sunrise
     *            The sunrise
     */
    public void setSunrise(long sunrise) {
        mSunrise = sunrise;
    }

    /**
     * @return The sunset
     */
    public long getSunset() {
        return mSunset;
    }

    /**
     * @param sunset
     *            The sunset
     */
    public void setSunset(long sunset) {
        mSunset = sunset;
    }
}
