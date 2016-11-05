package suntime.swindroid.travelcalculator.astronomy;

import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

class TimeZoneListStore {
    private List<TZLocation> timeZones = new LinkedList<>();

    public void insert(TZLocation loc) {
        timeZones.add(loc);
    }

    public TimeZone nearestTimeZone(TZLocation node) {
        double bestDistance = Double.MAX_VALUE;
        TZLocation bestGuess = timeZones.get(0);

        for (TZLocation current : timeZones.subList(1, timeZones.size())) {
            double newDistance = distanceInKilometers(node, current);

            if (newDistance < bestDistance) {
                bestDistance = newDistance;
                bestGuess = current;
            }
        }

        return TimeZone.getTimeZone(bestGuess.getZone());
    }

    private double distanceInKilometers(final double latFrom, final double lonFrom, final double latTo, final double lonTo) {
        final double meridianLength = 111.1;
        return meridianLength * centralAngle(latFrom, lonFrom, latTo, lonTo);
    }

    private double centralAngle(final double latFrom, final double lonFrom, final double latTo, final double lonTo) {
        final double latFromRad = toRadians(latFrom),
                lonFromRad = toRadians(lonFrom),
                latToRad   = toRadians(latTo),
                lonToRad   = toRadians(lonTo);

        final double centralAngle = toDegrees(acos(sin(latFromRad) * sin(latToRad) + cos(latFromRad) * cos(latToRad) * cos(lonToRad - lonFromRad)));

        return centralAngle <= 180.0 ? centralAngle : (360.0 - centralAngle);
    }

    private double distanceInKilometers(final TZLocation from, final TZLocation to) {
        return distanceInKilometers(from.getLatitude(), from.getLongitude(), to.getLatitude(), to.getLongitude());
    }
}
