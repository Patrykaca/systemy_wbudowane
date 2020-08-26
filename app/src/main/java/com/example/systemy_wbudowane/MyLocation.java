package com.example.systemy_wbudowane;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;

public class MyLocation extends MainActivity implements LocationListener {

    private double latitude;
    private double longitude;

    private LocationManager locationManager;

    private LocationRequest locationRequest;
    private LocationSettingsRequest.Builder locationBuilder;

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    private Location location;

    public MyLocation(Context context, final Activity activity) {

        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        locationRequest = new LocationRequest()
                .setFastestInterval(1000)
                .setInterval(2000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationBuilder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        final Task<LocationSettingsResponse> resultSettings =
                LocationServices.getSettingsClient(context).checkLocationSettings(locationBuilder.build());

        resultSettings.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    task.getResult(ApiException.class);
                } catch (ApiException e) {
                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(activity, PERMISSION_REQUEST_COARSE_LOCATION);
                            } catch (IntentSender.SendIntentException ex) {
                                ex.printStackTrace();
                            } catch (ClassCastException ex) {

                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE: {
                            break;
                        }
                    }
                }
            }
        });
//
      //  if (isProviderEnabled()) {
      //      //Toast.makeText(this, "ok", Toast.LENGTH_SHORT).show();
      //      if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
      //              != PackageManager.PERMISSION_GRANTED &&
      //              ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
      //                      != PackageManager.PERMISSION_GRANTED) {
      //          // TODO: Consider calling
      //          //    ActivityCompat#requestPermissions
      //          // here to request the missing permissions, and then overriding
      //          //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
      //          //                                          int[] grantResults)
      //          // to handle the case where the user grants the permission. See the documentation
      //          // for ActivityCompat#requestPermissions for more details.
      //          //Toast.makeText(this, "PERMISSION DENIED", Toast.LENGTH_SHORT).show();
      //          final AlertDialog.Builder builder = new AlertDialog.Builder(this);
      //          builder.setTitle("Dostęp do lokalizacji");
      //          builder.setMessage("W celu zapewnienia pełnej funkcjonalności zezwól aplikacji na udostępnianie lokalizacji.");
      //          builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
      //              @Override
      //              public void onClick(DialogInterface dialogInterface, int i) {
//
      //                  requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
      //              }
      //          });
      //          builder.setNegativeButton(android.R.string.no, null);
      //          builder.show();
      //      }
//
      //      do {
      //          locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
      //          location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
      //      } while (location == null);
      //  }
    }

    public LocationManager getLocationManager() {
        return locationManager;
    }

   // public void getCurrentLocation(Location location, LocationListener locationListener) {
   //     do {
   //         locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
   //     } while (location == null)
   // }

    public boolean isProviderEnabled() {
        try {
            boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            return (gpsEnabled && networkEnabled);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getCity(Context context, Location location) {
        try {
            Geocoder geocoder = new Geocoder(context);
            List<Address> addresses;
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            return addresses.get(0).getLocality();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public void onLocationChanged(android.location.Location location) {
        longitude = location.getLongitude();
        latitude = location.getLatitude();
        //Toast.makeText(MainActivity.this, latitude + " " + longitude, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
