package suntime.swindroid.travelcalculator.model;

/**
 * Created by srikaram on 15-Oct-16.
 */
public class Location {

    private String locationName;

    private double latitude;

    private double longitude;

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
