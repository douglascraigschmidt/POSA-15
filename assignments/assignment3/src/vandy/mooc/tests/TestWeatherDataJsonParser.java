package vandy.mooc.tests;

import android.test.AndroidTestCase;
import junit.framework.Assert;
import vandy.mooc.model.aidl.WeatherData;
import vandy.mooc.model.aidl.WeatherDataJsonParser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

/**
 * Test case for testing the WeatherDataJsonParser class methods.
 * The data member validation is only performed on the subset of
 * data members that the skeleton application actually uses. For
 * clarity, all data members required by the skeleton are marked
 * with a "required" comment.
 * <p/>
 * These tests have broken the data into strings that could be
 * used to test each of the individual methods in the parser.
 * However, student implementations may vary in terms of the
 * state of the JsonReader that is passed to each of these
 * functions which will likely make function specific testing
 * fail. For example, some students may decide to call
 * reader.beginArray() and reader.endArray() outside
 * the scope of an array parsing function, while others may
 * call these functions inside the function.
 * <p/>
 * NOTE: If any student implementation uses more than this the
 * minimum number of data members required by the skeleton, these
 * values will not be tested by this test case.
 */
public class TestWeatherDataJsonParser extends AndroidTestCase {

    /**
     * Sys data
     */
    public static final String mSysData =
            "\"sys\": {"
                    + "\"message\": 0.0138,"
                    + "\"country\": \"United States of America\"," /* required */
                    + "\"sunrise\": 1431427373," /* required */
                    + "\"sunset\": 1431477841" /* required */
                    + "}";

    /**
     * Weathers data (only 1 list entry).
     */
    public static final String mWeathersData =
            "\"weather\": [{"
                    + "\"id\": 802," /* required */
                    + "\"main\": \"Clouds\"," /* parcelized but not used */
                    + "\"description\": \"scattered clouds\"," /* required */
                    + "\"icon\": \"03d\"" /* parcelized but not used */
                    + "}]";

    /**
     * Main data.
     */
    public static final String mMainData =
            "\"main\": {"
                    + "\"temp\": 289.847," /* required */
                    + "\"temp_min\": 289.847,"
                    + "\"temp_max\": 289.847,"
                    + "\"pressure\": 1010.71," /* parcelized but not used */
                    + "\"sea_level\": 1035.76,"
                    + "\"grnd_level\": 1010.71,"
                    + "\"humidity\": 76" + "}"; /* required */

    /**
     * Wind data.
     */
    public static final String mWindData =
            "\"wind\": {"
                    + "\"speed\": 2.42," /* required */
                    + "\"deg\": 310.002" /* required */
                    + "}";

    /*
     * Weather Data (with string replacement stubs for subclasses).
     */
    public static final String mData = "{"
            + "\"coord\": { \"lon\": -86.78, \"lat\": 36.17 },"
            + "_SYS_DATA" + ","
            + "_WEATHER_DATA" + ","
            + "\"base\": \"stations\","
            + "_MAIN_DATA" + ","
            + "_WIND_DATA" + ","
            + "\"clouds\": {" + "\"all\": 36" + "},"
            + "\"dt\": 1431435983," /* parcelized but not used */
            + "\"id\": 4644585,"
            + "\"name\": \"Nashville\"," /* required */
            + "\"cod\": 200" /* parcelized but not used */
            + "}";

    /**
     * Test case runner for testing the
     * WeatherDataJsonParser class methods.
     *
     * @throws Throwable
     */
    public void testParseJSONStream() throws Throwable {
        // Build the input string to mock the JSON output
        // from openweathermap.org.
        String data = buildJSONData();

        // Convert JSON string to an InputStream.
        InputStream inputStream = new ByteArrayInputStream(data.getBytes());

        // Test the WeatherDataJsonParser main entry point.
        WeatherDataJsonParser parser = new WeatherDataJsonParser();

        try {
            // Test the parser.
            List<WeatherData> weatherDataList = parser.parseJsonStream(inputStream);

            // Now verify the results from the parser.
            validateWeatherDataList(weatherDataList);
        } catch (Exception e) {
            fail("Unable to parse weather data: " + e);
        }
    }

    public void validateWeatherDataList(List<WeatherData> weatherDataList) throws Throwable {
        Assert.assertNotNull(weatherDataList != null);
        Assert.assertNotNull(weatherDataList.size() == 1);

        WeatherData weatherData = weatherDataList.get(0);
        Assert.assertNotNull(weatherData); // Probably not necessary.

        // Validate weatherData members.
        validateData(weatherData);

        // Validate sys data values.
        validateSys(weatherData);

        // Validate weathers data values.
        validateWeathers(weatherData);

        // Validate main data values.
        validateMain(weatherData);

        // Validate wind data values.
        validateWind(weatherData);
    }

    public void validateData(WeatherData weatherData) {
        Assert.assertNotNull(weatherData);
        Assert.assertEquals("getName() is invalid",
                weatherData.getName(), "Nashville");
    }

    public void validateWeathers(WeatherData weatherData) {
        Assert.assertNotNull(weatherData.getWeathers());
        Assert.assertEquals("Expected 1 weathers array object",
                1, weatherData.getWeathers().size());

        Assert.assertEquals("getWeathers().get(0).getDescription() value is invalid",
                weatherData.getWeathers().get(0).getDescription(), "scattered clouds");
        Assert.assertEquals("getWeathers().get(0).getId() value is invalid",
                weatherData.getWeathers().get(0).getId(), 802);
    }

    public void validateSys(WeatherData weatherData) {
        Assert.assertNotNull(weatherData.getSys());
        Assert.assertEquals("getSys().getCountry() is invalid",
                weatherData.getSys().getCountry(), "United States of America");
        Assert.assertEquals("getSys().getSunrise() is invalid",
                weatherData.getSys().getSunrise(), 1431427373);
        Assert.assertEquals("getSys().getSunset() is invalid",
                weatherData.getSys().getSunset(), 1431477841);
    }

    public void validateMain(WeatherData weatherData) {
        Assert.assertNotNull(weatherData.getMain());

        Assert.assertEquals("getSys().getHumidity() is invalid",
                weatherData.getMain().getHumidity(), 76);
        Assert.assertEquals("getSys().getTemp() is invalid",
                weatherData.getMain().getTemp(), 289.847);
    }

    public void validateWind(WeatherData weatherData) {
        Assert.assertNotNull(weatherData.getWind());

        Assert.assertEquals("getWind().getDeg() is invalid",
                weatherData.getWind().getDeg(), 310.002);
        Assert.assertEquals("getWind().getSpeed() is invalid",
                weatherData.getWind().getSpeed(), 2.42);
    }

    private String buildJSONData() {
        String data = new String(mData);

        data = data.replace("_SYS_DATA", mSysData);
        data = data.replace("_MAIN_DATA", mMainData);
        data = data.replace("_WEATHER_DATA", mWeathersData);
        data = data.replace("_WIND_DATA", mWindData);

        return data;
    }
}
