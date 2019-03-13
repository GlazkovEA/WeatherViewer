package homounikumus1.com.myweatherviewer.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static homounikumus1.com.myweatherviewer.WeatherApp.getAppContext;

public class LocationUtils implements LocationListener {
    /**
     * The device location saved in this variable
     */
    public static Location location;
    public static boolean isMock = false;

    public static void setIsMock(boolean isMock) {
        LocationUtils.isMock = isMock;
    }

    public static void SetUpLocationListener(Context context) {
        // init location service
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationUtils();

        assert lm != null;
        // get location from GPS
        boolean gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // get location from NETWORK
        boolean network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Location net_loc = null, gps_loc = null, finalLoc = null;

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        // request fr update location
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 10, locationListener);
        // if all enabled get it's all
        if (gps_enabled)
            gps_loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (network_enabled)
            net_loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        // get better
        if (gps_loc != null && net_loc != null) {
            if (gps_loc.getAccuracy() > net_loc.getAccuracy())
                finalLoc = net_loc;
            else
                finalLoc = gps_loc;
        } else {
            if (gps_loc != null) {
                finalLoc = gps_loc;
            } else if (net_loc != null) {
                finalLoc = net_loc;
            }
        }
        // save location in variable
        location = finalLoc;
    }

    private static double[] coordinates = null;

    public static double[] getCoordinates() {
        if (isMock==true) {
            return coordinates;
        } else {
            LocationUtils.SetUpLocationListener(getAppContext());

            double[] cords = new double[2];
            // get current location
            if (LocationUtils.location != null) {
                cords[0] = LocationUtils.location.getLatitude();
                cords[1] = LocationUtils.location.getLongitude();

                // rounds the latitude and longitude value to two decimal places as it uses the weatherAPI
                double latRound = new BigDecimal(cords[0]).setScale(3, RoundingMode.DOWN).doubleValue();
                double lonRound = new BigDecimal(cords[1]).setScale(3, RoundingMode.DOWN).doubleValue();

                if (latRound != 0.0 && lonRound != 0.0)
                    return new double[]{latRound, lonRound};
                else
                    return null;

            }
            // if for some reason we had'n data return - null
            return null;
        }
    }

    public static void setCoordinates(double[] coordinates) {
        LocationUtils.coordinates = coordinates;
    }

    // if locaion changed - update it
    @Override
    public void onLocationChanged(Location loc) {
        location = loc;
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
}
