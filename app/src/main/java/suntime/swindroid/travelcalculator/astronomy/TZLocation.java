package suntime.swindroid.travelcalculator.astronomy;

import java.util.Arrays;

/**
 * @author artemgapchenko
 * Created on 22.04.14.
 */
class TZLocation {
    private double[] coordinates;
    private String zone;

    public TZLocation(double[] coordinates) {
        this.coordinates = coordinates;
    }

    public TZLocation(final double latitude, final double longitude, final String zone) {
        this.coordinates = new double[] { latitude, longitude };
        this.zone = zone;
    }

    public double[] getCoordinates() {
        return this.coordinates;
    }

    public double getLatitude() {
        return this.coordinates[0];
    }

    public double getLongitude() {
        return this.coordinates[1];
    }

    public String getZone() {
        return zone;
    }

    @Override
    public boolean equals(Object obj) {
        if (!obj.getClass().isAssignableFrom(TZLocation.class)) throw new IllegalArgumentException("provided obj is: " + obj);
        return Arrays.equals(coordinates, ((TZLocation) obj).getCoordinates());
    }
}
