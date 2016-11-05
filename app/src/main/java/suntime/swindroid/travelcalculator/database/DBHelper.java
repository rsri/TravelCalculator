package suntime.swindroid.travelcalculator.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import suntime.swindroid.travelcalculator.util.Constants;
import suntime.swindroid.travelcalculator.util.Util;

import static suntime.swindroid.travelcalculator.util.Constants.COL_LATITUDE;
import static suntime.swindroid.travelcalculator.util.Constants.COL_LOCATION;
import static suntime.swindroid.travelcalculator.util.Constants.COL_LONGITUDE;
import static suntime.swindroid.travelcalculator.util.Constants.DB_NAME;
import static suntime.swindroid.travelcalculator.util.Constants.TABLE_NAME;
import static suntime.swindroid.travelcalculator.util.Constants.VERSION;

/**
 * Created by srikaram on 15-Oct-16.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String CREATE_QUERY = "CREATE TABLE %s (%s TEXT, %s REAL, %s REAL)";

    private final Context context;
    public DBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(String.format(CREATE_QUERY, TABLE_NAME, COL_LOCATION, COL_LATITUDE, COL_LONGITUDE));
        insertPrimaryData(db);
    }

    private void insertPrimaryData(SQLiteDatabase db) {
        InputStream assetStream = null;
        try {
            assetStream = context.getAssets().open(Constants.ASSET_LOCATIONS);
            CSVParser csvParser = new CSVParser(new InputStreamReader(assetStream), CSVFormat.DEFAULT);
            List<CSVRecord> csvRecords = csvParser.getRecords();
            ContentValues contentValues = new ContentValues();
            for (CSVRecord record : csvRecords) {
                contentValues.clear();
                for (int i = 0 ; i < record.size() ; i++) {
                    String data = record.get(i);
                    switch (i) {
                        case 0:
                            contentValues.put(COL_LOCATION, data);
                            break;
                        case 1:
                            contentValues.put(COL_LATITUDE, Double.parseDouble(data));
                            break;
                        case 2:
                            contentValues.put(COL_LONGITUDE, Double.parseDouble(data));
                            break;
                    }
                }
                insertLocation(contentValues, db);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Util.closeQuietly(assetStream);
        }
    }

    private boolean insertLocation(ContentValues locationValues, SQLiteDatabase db) {
        long rowVal = db.insert(TABLE_NAME, null, locationValues);
        return rowVal != -1;
    }

    public boolean insertLocation(ContentValues locationValues) {
        SQLiteDatabase db = getWritableDatabase();
        return insertLocation(locationValues, db);
    }

    public boolean deleteLocation(String location) {
        SQLiteDatabase db = getWritableDatabase();
        int numOfRows = db.delete(TABLE_NAME, COL_LOCATION + "=?", new String[]{location});
        return numOfRows != 0;
    }

    public List<String> getLocationNames() {
        String query = "select %s from %s";
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(String.format(query, COL_LOCATION, TABLE_NAME), null);
        List<String> locations = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            locations.add(cursor.getString(cursor.getColumnIndex(COL_LOCATION)));
            cursor.moveToNext();
        }
        cursor.close();
        return locations;
    }

    public ContentValues getLocation(String locationName) {
        String query = "select * from %s where %s=?";
        return captureLocations(String.format(query, TABLE_NAME, COL_LOCATION), new String[] {locationName}).get(0);
    }

    public List<ContentValues> getLocations() {
        String query = "select * from %s";
        return captureLocations(String.format(query, TABLE_NAME), null);
    }

    private List<ContentValues> captureLocations(String query, String[] selectionArgs) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, selectionArgs);
        cursor.moveToFirst();
        List<ContentValues> locations = new ArrayList<>();
        while (!cursor.isAfterLast()) {
            ContentValues location = new ContentValues();
            location.put(COL_LOCATION, cursor.getString(cursor.getColumnIndex(COL_LOCATION)));
            location.put(COL_LONGITUDE, cursor.getDouble(cursor.getColumnIndex(COL_LONGITUDE)));
            location.put(COL_LATITUDE, cursor.getDouble(cursor.getColumnIndex(COL_LATITUDE)));
            locations.add(location);
            cursor.moveToNext();
        }
        cursor.close();
        return locations;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
