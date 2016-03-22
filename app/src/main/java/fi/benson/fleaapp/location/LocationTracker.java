package fi.benson.fleaapp.location;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;


/**
 * Created by bkamau on 3/22/16.
 */

public class LocationTracker extends Service implements LocationListener {

    //declaring Context variable
    private final Context con;

    //flag for gps
    public boolean isGPSOn = false;

    //flag for network location
    public boolean isNetWorkEnabled = false;

    //flag to getlocation
    public boolean isLocationEnabled = false;

    //minimum distance to request for location update
    private static final long MIN_DISTANCE_TO_REQUEST_LOCATION = 1; // in meters

    // minimum time to request location updates
    private static final long MIN_TIME_FOR_UPDATES = 1000 * 1; // 1 sec

    //location
    Location location;
    //latitude and longitude
    double latitude, longitude;

    //Declaring a LocationManager
    LocationManager locationManager;

    public LocationTracker(Context context) {
        this.con = context;
        checkIfLocationAvailable();
    }

    public Location checkIfLocationAvailable() {
        try {
            locationManager = (LocationManager) con.getSystemService(LOCATION_SERVICE);
            //check for gps availability
            isGPSOn = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            //check for network availablity
            isNetWorkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSOn && !isNetWorkEnabled) {
                isLocationEnabled = false;
                // no location provider is available show toast to user
                Toast.makeText(con, "No Location Provider is Available", Toast.LENGTH_SHORT).show();
            } else {
                isLocationEnabled = true;

                // if network location is available request location update
                if (isNetWorkEnabled) {

                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_FOR_UPDATES, MIN_DISTANCE_TO_REQUEST_LOCATION, this);
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();

                        }
                    }
                }

                if (isGPSOn) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_FOR_UPDATES, MIN_DISTANCE_TO_REQUEST_LOCATION, this);

                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
            }

        } catch (Exception e) {

        }

        return location;
    }

    // call this to stop using location
    public void stopUsingLocation() {
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.removeUpdates(LocationTracker.this);
        }
    }
    // call this to getLatitude
    public double getLatitude()
    {
        if(location!=null)
        {
            latitude=location.getLatitude();
        }
        return latitude;
    }
    //call this to getLongitude
    public double getLongitude()
    {
        if(location!=null)
        {
            longitude=location.getLongitude();
        }
        return longitude;
    }

    public boolean isLocationEnabled() {
        return this.isLocationEnabled;
    }

    //call to open settings and ask to enable Location
    public void askToOnLocation()
    {
        AlertDialog.Builder dialog=new AlertDialog.Builder(con);

        //set title
        dialog.setTitle("Settings");
        //set Message
        dialog.setMessage("Location is not Enabled.Do you want to go to settings to enable it?");
        // on pressing this will be called
        dialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                con.startActivity(intent);
            }
        });

        //on Pressing cancel
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        // show Dialog box
        dialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {

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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}