package vandy.mooc.model.aidl;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class is a Plain Old Java Object (POJO) used for data transport within
 * the WeatherService app. It represents the response Json obtained from the
 * Open Weather Map API, e.g., a call to
 * http://api.openweathermap.org/data/2.5/weather?APPID=YOUR-APP-ID&q=Nashville,TN
 * might return
 * the following Json data:
 * 
 * { "coord":{ "lon":-86.78, "lat":36.17 }, "sys":{ "message":0.0138,
 * "country":"United States of America", "sunrise":1431427373,
 * "sunset":1431477841 }, "weather":[ { "id":802, "main":"Clouds",
 * "description":"scattered clouds", "icon":"03d" } ], "base":"stations",
 * "main":{ "temp":289.847, "temp_min":289.847, "temp_max":289.847,
 * "pressure":1010.71, "sea_level":1035.76, "grnd_level":1010.71, "humidity":76
 * }, "wind":{ "speed":2.42, "deg":310.002 }, "clouds":{ "all":36 },
 * "dt":1431435983, "id":4644585, "name":"Nashville", "cod":200 }
 *
 * The meaning of these Json fields is documented at
 * http://openweathermap.org/weather-data#current.
 */
public class WeatherData implements Parcelable {
    /*
     * These fields store the WeatherData's state.  
     */
    private String mName;
    private long mDate;
    private long mCod;
    private List<Weather> mWeathers = new ArrayList<Weather>();
    private Sys mSys;
    private Main mMain;
    private Wind mWind;
    private String mMessage;

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
    final public static String message_JSON = "message";

    /**
     * Constructor that initializes the POJO.
     */
    public WeatherData(String name,
                       long date,
                       long cod,
                       Sys sys,
                       Main main,
                       Wind wind,
                       List<Weather> weathers) {
	mName = name;
	mDate = date;
	mCod = cod;
	mSys = sys;
	mMain = main;
	mWind = wind;
	mWeathers = weathers;
    }
    
    /**
     * Default constructor that initializes the POJO.
     */
    public WeatherData() {
    }

    /*
     * Getter and setter methods for fields
     */

    /**
     * Getter method for the System info
     */
    public Sys getSys() {
	return mSys;
    }

    /**
     * Setter method for the System info
     * 
     * @param sys
     */
    public void setSys(Sys sys) {
	mSys = sys;
    }

    /**
     * Getter method for the Main info
     */
    public Main getMain() {
	return mMain;
    }

    /**
     * Setter method for the Main info
     * 
     * @param main
     */
    public void setMain(Main main) {
	mMain = main;
    }

    /**
     * Getter method for the Wind info
     */
    public Wind getWind() {
	return mWind;
    }

    /**
     * Setter method for the Wind info
     * 
     * @param wind
     */
    public void setWind(Wind wind) {
	mWind = wind;
    }

    /**
     * Getter method for location's name
     */
    public String getName() {
	return mName;
    }

    /**
     * Setter method for location's name
     * 
     * @param name
     */
    public void setName(String name) {
	mName = name;
    }

    /**
     * Getter method for the data's date
     */
    public long getDate() {
	return mDate;
    }

    /**
     * Setter method for the data's date
     * 
     * @param date
     */
    public void setDate(long date) {
	mDate = date;
    }

    /**
     * Getter method for the cod data
     */
    public long getCod() {
	return mCod;
    }

    /**
     * Setter method for the cod data
     * 
     * @param cod
     */
    public void setCod(long cod) {
	mCod = cod;
    }

    /**
     * Getter method for the Weather objects
     */
    public List<Weather> getWeathers() {
	return mWeathers;
    }

    /**
     * Setter method for the Weather objects
     * 
     * @param weathers
     */
    public void setWeathers(List<Weather> weathers) {
	mWeathers = weathers;
    }

    /**
     * Getter method for the Message.
     */
    public String getMessage() {
	return mMessage;
    }

    /**
     * Setter method for the Message.
     * 
     * @param message
     */
    public void setMessage(String message) {
	mMessage = message;
    }

    /**
     * Inner class representing a description of a current weather
     * condition.
     */
    public static class Weather {
        /*
         * These fields store the Weather's state.
         */
        private long mId;
        private String mMain;
        private String mDescription;
        private String mIcon;

        /**
         * Various tags corresponding to weather data downloaded in
         * Json from the Weather Service.
         */
        public final static String id_JSON = "id";
        public final static String main_JSON = "main";
        public final static String description_JSON = "description";
        public final static String icon_JSON = "icon";

        /**
         * Constructor sets the fields.
         */
	public Weather(long id,
                       String main,
                       String description,
                       String icon) {
	    mId = id;
	    mMain = main;
	    mDescription = description;
	    mIcon = icon;
	}

        /**
         * Default constructor.
         */
	public Weather() {
        }

	/*
	 * Getter and setting methods for fields.
	 */

	public long getId() {
	    return mId;
	}

	public void setId(long id) {
	    mId = id;
	}

	public String getMain() {
	    return mMain;
	}

	public void setMain(String main) {
	    mMain = main;
	}

	public String getDescription() {
	    return mDescription;
	}

	public void setDescription(String description) {
	    mDescription = description;
	}

	public String getIcon() {
	    return mIcon;
	}

	public void setIcon(String icon) {
	    mIcon = icon;
	}
    }

    /**
     * Inner class representing system data.
     */
    public static class Sys {
        /*
         * These fields store the Sys's state.
         */
        private long mSunrise;
        private long mSunset;
        private String mCountry;
        private double mMessage;

        /**
         * Various tags corresponding to system data downloaded in Json
         * from the Weather Service.
         */
        public final static String message_JSON = "message";
        public final static String country_JSON = "country";
        public final static String sunrise_JSON = "sunrise";
        public final static String sunset_JSON = "sunset";

        /**
         * Constructor sets the fields.
         */
	public Sys(long sunrise,
                   long sunset,
                   String country) {
	    mSunrise = sunrise;
	    mSunset = sunset;
	    mCountry = country;
	}

        /**
         * Default constructor.
         */
	public Sys() {
        }

	/*
	 * Getter and setter methods for fields
	 */

	public long getSunrise() {
	    return mSunrise;
	}

	public void setSunrise(long sunrise) {
	    mSunrise = sunrise;
	}

	public long getSunset() {
	    return mSunset;
	}

	public void setSunset(long sunset) {
	    mSunset = sunset;
	}

	public String getCountry() {
	    return mCountry;
	}

	public void setCountry(String country) {
	    mCountry = country;
	}

	public double getMessage() {
	    return mMessage;
	}

	public void setMessage(double message) {
	    mMessage = message;
	}
    }

    /**
     * Inner class representing the core weather data
     */
    public static class Main {
        /*
         * These fields store the Main's state.
         */
        private double mTemp;
        private long mHumidity;
        private double mPressure;

        /**
         * Various tags corresponding to temperature, pressure, and
         * humidity data downloaded in Json from the Weather Service.
         */
        public final static String temp_JSON = "temp";
        public final static String tempMin_JSON = "temp_min";
        public final static String tempMax_JSON = "temp_max";
        public final static String pressure_JSON = "pressure";
        public final static String seaLevel_JSON = "sea_level";
        public final static String grndLevel_JSON = "grnd_level";
        public final static String humidity_JSON = "humidity";

        /**
         * Constructor sets the fields.
         */
	public Main(double temp,
                    long humidity,
                    double pressure) {
	    mTemp = temp;
	    mHumidity = humidity;
	    mPressure = pressure;
	}

        /**
         * Default constructor.
         */
	public Main() {
        }

	/*
	 * Getter and setter methods for fields
	 */

	public double getPressure() {
	    return mPressure;
	}

	public void setPressure(double pressure) {
	    mPressure = pressure;
	}

	public double getTemp() {
	    return mTemp;
	}

	public void setTemp(double temp) {
	    mTemp = temp;
	}

	public long getHumidity() {
	    return mHumidity;
	}

	public void setHumidity(long humidity) {
	    mHumidity = humidity;
	}
    }

    /**
     * Inner class representing wind data
     */
    public static class Wind {
        /*
         * These fields store the Wind's state.
         */
        private double mSpeed;
        private double mDeg;

        /**
         * Various tags corresponding to wind data downloaded in Json
         * from the Weather Service.
         */
        public final static String deg_JSON = "deg";
        public final static String speed_JSON = "speed";

        /**
         * Constructor sets the fields.
         */
	public Wind(double speed,
                    double deg) {
	    mSpeed = speed;
	    mDeg = deg;
	}

        /**
         * Default constructor.
         */
	public Wind() {
        }

	/*
	 * Getter and setter methods for fields
	 */

	public double getSpeed() {
	    return mSpeed;
	}

	public void setSpeed(double speed) {
	    mSpeed = speed;
	}

	public double getDeg() {
	    return mDeg;
	}

	public void setDeg(double deg) {
	    mDeg = deg;
	}
    }

    /*
     * BELOW THIS is related to Parcelable Interface.
     */

    /**
     * A bitmask indicating the set of special object types marshaled
     * by the Parcelable.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Write this instance out to byte contiguous memory.
     */
    @Override
    public void writeToParcel(Parcel dest,
                              int flags) {
        dest.writeString(mName);
        dest.writeLong(mDate);
        dest.writeLong(mCod);
        final Weather weather = mWeathers.get(0);
        dest.writeLong(weather.getId());
        dest.writeString(weather.getMain());
        dest.writeString(weather.getDescription());
        dest.writeString(weather.getIcon());
        dest.writeLong(mSys.getSunrise());
        dest.writeLong(mSys.getSunset());
        dest.writeString(mSys.getCountry());
        dest.writeDouble(mMain.getTemp());
        dest.writeLong(mMain.getHumidity());
        dest.writeDouble(mMain.getPressure());
        dest.writeDouble(mWind.getSpeed());
        dest.writeDouble(mWind.getDeg());
    }

    /**
     * Private constructor provided for the CREATOR interface, which
     * is used to de-marshal an WeatherData from the Parcel of data.
     * <p>
     * The order of reading in variables HAS TO MATCH the order in
     * writeToParcel(Parcel, int)
     *
     * @param in
     */
    private WeatherData(Parcel in) {
        mName = in.readString();
        mDate = in.readLong();
        mCod = in.readLong();

        mWeathers.add(new Weather(in.readLong(),
                                  in.readString(),
                                  in.readString(),
                                  in.readString()));

        mSys = new Sys(in.readLong(),
                       in.readLong(),
                       in.readString());

        mMain = new Main(in.readDouble(),
                         in.readLong(),
                         in.readDouble());

        mWind = new Wind(in.readDouble(),
                         in.readDouble());
    }

    /**
     * public Parcelable.Creator for WeatherData, which is an
     * interface that must be implemented and provided as a public
     * CREATOR field that generates instances of your Parcelable class
     * from a Parcel.
     */
    public static final Parcelable.Creator<WeatherData> CREATOR =
        new Parcelable.Creator<WeatherData>() {
        public WeatherData createFromParcel(Parcel in) {
            return new WeatherData(in);
        }

        public WeatherData[] newArray(int size) {
            return new WeatherData[size];
        }
    };
}

