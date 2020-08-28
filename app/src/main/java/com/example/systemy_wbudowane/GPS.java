package com.example.systemy_wbudowane;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;

public class GPS  {

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationSettingsRequest.Builder locationBuilder;
    protected Task<LocationSettingsResponse> resultSettings;
    private LocationCallback locationCallback;
    private int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    public String CITY = "chuj";
    private String city = "null";

    public GPS(final MainActivity activity, final Context context) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //Toast.makeText(this, "PERMISSION DENIED", Toast.LENGTH_SHORT).show();
            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Dostęp do lokalizacji");
            builder.setMessage("W celu zapewnienia pełnej funkcjonalności zezwól aplikacji na udostępnianie lokalizacji.");
            builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                   // requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_COARSE_LOCATION);
                   // requestPermissions(new String[]{Manifest.permission.INTERNET}, PERMISSION_REQUEST_COARSE_LOCATION);
                   // requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                   // requestPermissions(new String[]{Manifest.permission.LOCATION_HARDWARE}, PERMISSION_REQUEST_COARSE_LOCATION);


                }
            });
            builder.setNegativeButton(android.R.string.no, null);
            builder.show();
        }

        locationRequest = new LocationRequest()
                .setFastestInterval(300)
                .setInterval(300)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationBuilder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        resultSettings =
                LocationServices.getSettingsClient(context).checkLocationSettings(locationBuilder.build());


        resultSettings.addOnFailureListener(activity, new OnFailureListener() {


            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    try {
                        ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                        resolvableApiException.startResolutionForResult(activity, PERMISSION_REQUEST_COARSE_LOCATION);
                    } catch (IntentSender.SendIntentException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationRequest == null) {
                    Toast.makeText(context, "nulll kurwa", Toast.LENGTH_SHORT).show();
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Toast.makeText(MainActivity.this, String.valueOf(location.getLatitude()), Toast.LENGTH_SHORT).show();
                    //getCity(location.getLatitude(), location.getLongitude());
                    getCity(location.getLatitude(), location.getLongitude(), context);
                    //Toast.makeText(context, CITY, Toast.LENGTH_SHORT).show();
                    activity.setCityBar(CITY);
                  // MainActivity.view.invalidate();
                  // MainActivity.view.setCitySize(CITY, CITY.length());
                  // MainActivity.view.drawCity();
                }
            }
        };

    }

    public void getCity(double latitude, double longitude, Context context) {
        try {
            Geocoder geocoder = new Geocoder(context);
            List<Address> addresses;
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            city = addresses.get(0).getLocality();
            CITY = city;
            MainActivity.CITY = city;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FusedLocationProviderClient getFusedLocationProviderClient() {
        return fusedLocationProviderClient;
    }

    public LocationCallback getLocationCallback() {
        return locationCallback;
    }

    public String getCity() {
        return city;
    }

    public void startLocationUpdates(Activity activity) {
        if (fusedLocationProviderClient != null) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, activity.getMainLooper());
        }


    }
}
