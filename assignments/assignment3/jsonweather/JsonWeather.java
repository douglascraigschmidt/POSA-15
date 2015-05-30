package vandy.mooc.jsonweather;

import java.util.ArrayList;
import java.util.List;


/**
 * This "Plain Ol' Java Object" (POJO) class represents data of
 * interest downloaded in Json from the Weather Service.  We don't
 * care about all the data, just the fields defined in this class.
 */
public class JsonWeather {
    /**
     * Various tags corresponding to data downloaded in Json from the
     * Weather Service.
     */
    final public static String cod_JSON = "cod";
    final public static String name_JSON = "name";
    final public static String id_JSON = "id";
    final public static String dt_JSON = "dt";
    final public static String wind_JSON = "wind";
    final public static String main_JSON = "main";
    final public static String base_JSON = "base";
    final public static String weather_JSON = "weather";
    final public static String sys_JSON = "sys";

    /**
     * Various fields corresponding to data downloaded in Json from
     * the Weather Service.
     */
    private Sys mSys;
    private String mBase;
    private Main mMain;
    private List<Weather> mWeather = new ArrayList<Weather>();
    private Wind mWind;
    private long mDt;
    private long mId;
    private String mName;
    private long mCod;

    /**
     * Constructor that initializes all the fields of interest.
     */
    public JsonWeather(Sys sys,
                       String base,
                       Main main,
                       List<Weather> weather,
                       Wind wind,
                       long dt,
                       long id,
                       String name,
                       long cod) {
        mSys = sys;
        mBase = base;
        mMain = main;
        mWeather = weather;
        mWind = wind;
        mDt = dt;
        mId = id;
        mName = name;
        mCod = cod;
    }

    /**
     * No-op constructor
     */
    public JsonWeather() {
    }

    /**
     * @return The sys
     */
    public Sys getSys() {
        return mSys;
    }

    /**
     * @param sys
     *            The sys
     */
    public void setSys(Sys sys) {
        mSys = sys;
    }

    /**
     * @return The base
     */
    public String getBase() {
        return mBase;
    }

    /**
     * @param base
     *            The base
     */
    public void setBase(String base) {
        mBase = base;
    }

    /**
     * @return The main
     */
    public Main getMain() {
        return mMain;
    }

    /**
     * @param main
     *            The main
     */
    public void setMain(Main main) {
        mMain = main;
    }

    
    /**
     * 
     * @return The weather
     */
    public List<Weather> getWeather() {
        return mWeather;
    }

    /**
     * 
     * @param weather
     *            The weather
     */
    public void setWeather(List<Weather> weather) {
        mWeather = weather;
    }


    /**
     * @return The wind
     */
    public Wind getWind() {
        return mWind;
    }

    /**
     * 
     * @param wind
     *            The wind
     */
    public void setWind(Wind wind) {
        mWind = wind;
    }

    /**
     * @return The dt
     */
    public long getDt() {
        return mDt;
    }

    /**
     * @param dt
     *            The dt
     */
    public void setDt(long dt) {
        mDt = dt;
    }

    /**
     * @return The id
     */
    public long getId() {
        return mId;
    }

    /**
     * @param id
     *            The id
     */
    public void setId(long id) {
        mId = id;
    }

    /**
     * @return The name
     */
    public String getName() {
        return mName;
    }

    /**
     * @param name
     *            The name
     */
    public void setName(String name) {
        mName = name;
    }

    /**
     * @return The cod
     */
    public long getCod() {
        return mCod;
    }

    /**
     * @param cod
     *            The cod
     */
    public void setCod(long cod) {
        mCod = cod;
    }
}
