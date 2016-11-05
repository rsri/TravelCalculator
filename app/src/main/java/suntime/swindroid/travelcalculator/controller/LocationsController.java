package suntime.swindroid.travelcalculator.controller;

import android.content.ContentValues;
import android.content.Context;
import android.util.Pair;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import suntime.swindroid.travelcalculator.MainActivity;
import suntime.swindroid.travelcalculator.astronomy.AstronomicalCalendar;
import suntime.swindroid.travelcalculator.astronomy.Converter;
import suntime.swindroid.travelcalculator.astronomy.GeoLocation;
import suntime.swindroid.travelcalculator.database.DBHelper;
import suntime.swindroid.travelcalculator.model.Location;
import suntime.swindroid.travelcalculator.util.Constants;

/**
 * Created by srikaram on 15-Oct-16.
 */
public class LocationsController {

    private final MainActivity activity;
    private final DBHelper dbHelper;

    public LocationsController(MainActivity activity) {
        this.activity = activity;
        dbHelper = new DBHelper(activity);
    }

    public List<String> getLocationNames() {
        return dbHelper.getLocationNames();
    }

    public List<Location> getLocations() {
        List<Location> locations = new ArrayList<>();
        List<ContentValues> contentValues = dbHelper.getLocations();
        for (ContentValues values : contentValues) {
            Location location = new Location();
            location.setLocationName(values.getAsString(Constants.COL_LOCATION));
            location.setLatitude(values.getAsDouble(Constants.COL_LATITUDE));
            location.setLongitude(values.getAsDouble(Constants.COL_LONGITUDE));
            locations.add(location);
        }
        return locations;
    }

    public boolean removeLocation(String locationName) {
        return dbHelper.deleteLocation(locationName);
    }

    public boolean insertLocation(Location location) {
        ContentValues values = new ContentValues();
        values.put(Constants.COL_LOCATION, location.getLocationName());
        values.put(Constants.COL_LATITUDE, location.getLatitude());
        values.put(Constants.COL_LONGITUDE, location.getLongitude());
        return dbHelper.insertLocation(values);
    }

    public Pair<String, String> getSunRiseSet(Context context, Location location, int year, int monthOfYear, int dayOfMonth) {
        Converter converter = Converter.getInstance(context);
        TimeZone timeZone = converter.getTimeZone(location.getLatitude(), location.getLongitude());

        GeoLocation geoLocation = convertMetrics(location, timeZone);
        AstronomicalCalendar ac = new AstronomicalCalendar(geoLocation);
        ac.getCalendar().set(year, monthOfYear, dayOfMonth);
        Date srise = ac.getSunrise();
        Date sset = ac.getSunset();

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return Pair.create(sdf.format(srise), sdf.format(sset));
    }

    private GeoLocation convertMetrics(Location location, TimeZone timeZone) {
        GeoLocation geoLocation = new GeoLocation();
        geoLocation.setLocationName(location.getLocationName());
        geoLocation.setLatitude(location.getLatitude());
        geoLocation.setLongitude(location.getLongitude());
        geoLocation.setTimeZone(timeZone);
        return geoLocation;
    }

    public List<String[]> getSunriseSetBetweenDates(Context context, Location location, Calendar fromDate, Calendar toDate) {
        List<String[]> data = new ArrayList<>();
        while (fromDate.compareTo(toDate) <= 0) {
            int year = fromDate.get(Calendar.YEAR);
            int month = fromDate.get(Calendar.MONTH);
            int dayOfMonth = fromDate.get(Calendar.DAY_OF_MONTH);
            Pair<String, String> sunRiseSet = getSunRiseSet(context, location, year, month, dayOfMonth);
            String date = String.format(Locale.getDefault(), Constants.DATE_FORMAT, dayOfMonth, month, year);
            String[] entry = new String[] {date, sunRiseSet.first, sunRiseSet.second};
            data.add(entry);
            fromDate.add(Calendar.DAY_OF_MONTH, 1);
        }
        return data;
    }

}
