package suntime.swindroid.travelcalculator.util;

/**
 * Created by srikaram on 15-Oct-16.
 */
public class Constants {

    public static final int VERSION = 1;

    public static final String TIMEZONES_LIST = "timezones.csv";
    public static final String ASSET_LOCATIONS = "au_locations.txt";

    public static final String DB_NAME = "locations.db";
    public static final String TABLE_NAME = "locations";
    public static final String COL_LOCATION = "location";
    public static final String COL_LATITUDE = "latitude";
    public static final String COL_LONGITUDE = "longitude";

    public static final String DATE_FORMAT = "%d/%d/%d";
    public static final String DATE_SEPARATOR = "/";

    public static final int REQ_CODE_WRITE_PERMISSION_SAVE = 0;
    public static final int REQ_CODE_WRITE_PERMISSION_SHARE = 1;
    public static final int REQ_CODE_ACCESS_INTERNET = 2;
    public static final int REQ_CODE_ACCESS_LOCATION = 3;
    public static final int REQ_CODE_ACCESS_LOCATION_MAP = 4;
}
