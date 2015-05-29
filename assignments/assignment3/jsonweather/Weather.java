package vandy.mooc.jsonweather;

/**
 * This "Plain Ol' Java Object" (POJO) class represents data related
 * to weather downloaded in Json from the Weather Service.
 */
public class Weather {
    /**
     * Various tags corresponding to weather data downloaded in Json
     * from the Weather Service.
     */
    public final static String id_JSON = "id";
    public final static String main_JSON = "main";
    public final static String description_JSON = "description";
    public final static String icon_JSON = "icon";

    /**
     * Various fields corresponding to weather data downloaded in Json
     * from the Weather Service.
     */
    private long id;
    private String main;
    private String description;
    private String icon;

    /**
     * 
     * @return The id
     */
    public long getId() {
        return id;
    }

    /**
     * 
     * @param id
     *            The id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * 
     * @return The main
     */
    public String getMain() {
        return main;
    }

    /**
     * 
     * @param main
     *            The main
     */
    public void setMain(String main) {
        this.main = main;
    }

    /**
     * 
     * @return The description
     */
    public String getDescription() {
        return description;
    }

    /**
     * 
     * @param description
     *            The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 
     * @return The icon
     */
    public String getIcon() {
        return icon;
    }

    /**
     * 
     * @param icon
     *            The icon
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }
}
