package suntime.swindroid.travelcalculator.astronomy;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.TimeZone;

import suntime.swindroid.travelcalculator.util.Constants;

public class Converter {
    private final TimeZoneListStore tzStore;
    private static Converter instance = null;

    private Converter(Context context) {
        tzStore = new TimeZoneListStore();
        try {
            loadData(context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Converter getInstance(Context context) {
        if (instance == null) {
            instance = new Converter(context);
        }
        return instance;
    }

    public TimeZone getTimeZone(final double lat, final double lon) {
        return tzStore.nearestTimeZone(new TZLocation(new double[]{lat, lon}));
    }

    private void loadData(Context context) throws IOException {
        BufferedReader br = new BufferedReader(
                new InputStreamReader(context.getAssets().open(Constants.TIMEZONES_LIST))
        );

        try {
            String line;
            String[] location;

            while ((line = br.readLine()) != null) {
                location = line.split(";");
                tzStore.insert(new TZLocation(Double.valueOf(location[1]), Double.valueOf(location[2]), location[0]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}