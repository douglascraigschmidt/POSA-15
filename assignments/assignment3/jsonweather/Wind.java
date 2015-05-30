package vandy.mooc.jsonweather;

/**
 * This "Plain Ol' Java Object" (POJO) class represents data related
 * to wind downloaded in Json from the Weather Service.
 */
public class Wind {
    /**
     * Various tags corresponding to wind data downloaded in Json from
     * the Weather Service.
     */
    public final static String deg_JSON = "deg";
    public final static String speed_JSON = "speed";

    /**
     * Various fields corresponding to wind data downloaded in Json
     * from the Weather Service.
     */
    private double mSpeed;
    private double mDeg;

    /**
     * @return The speed
     */
    public double getSpeed() {
        return mSpeed;
    }

    /**
     * @param speed
     *            The speed
     */
    public void setSpeed(double speed) {
        mSpeed = speed;
    }

    /**
     * @return The deg
     */
    public double getDeg() {
        return mDeg;
    }

    /**
     * @param deg
     *            The deg
     */
    public void setDeg(double deg) {
        mDeg = deg;
    }
}
