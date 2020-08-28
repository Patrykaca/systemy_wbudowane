package com.example.systemy_wbudowane;

import android.Manifest;
import android.app.usage.UsageEvents;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.EventLog;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Lifecycle;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String WIDTH = "width";
    private static final String HEIGHT = "height";
    private static final String SCORE = "score";
    private static final String HIGH_SCORE = "high score temp";
    private static final String UNDO_SCORE = "undo score";
    private static final String CAN_UNDO = "can undo";
    private static final String UNDO_GRID = "undo";
    private static final String GAME_STATE = "game state";
    private static final String UNDO_GAME_STATE = "undo game state";
    public  static MainView view;
    private SensorManager sensorManager;
    private LightSensor lightSensor;
    private GyroscopeSensor gyroscopeSensor;
    private ProximitySensor proximitySensor;
    private BatteryReceiver batteryReceiver;
    private IntentFilter intentFilter;
    public static Sounds sound;
    public static Vibrator vibro;
    private Sensor stepSensor;
    private SensorEventListener stepEventListener;
    private float stepValue;
    private String city = null;
    public static String CITY = "chuj";
    private boolean batteryStatusLOW;
    private GPS gps;
  //  private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
  //  private LocationRequest locationRequest;
  //  private LocationSettingsRequest.Builder locationBuilder;
  //  private LocationCallback locationCallback;
  //  private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_game:
                Intent inte1 = new Intent(getBaseContext(), MainActivity.class);
                startActivity(inte1);
                return true;
            case R.id.nav_compass:
                setContentView(R.layout.compass);
                Intent inte2 = new Intent(getBaseContext(), Compass.class);
                startActivity(inte2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = new MainView(this, true);
        sound = new Sounds(this);
        vibro = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        //fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        view.hasSaveState = settings.getBoolean("save_state", false);

        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean("hasState")) {
                load();
            }
        }
        setContentView(view);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        lightSensor = new LightSensor(sensorManager, this);

        gyroscopeSensor = new GyroscopeSensor(sensorManager, this);

        proximitySensor = new ProximitySensor(sensorManager, this);

        intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

        batteryReceiver = new BatteryReceiver();


        //TODO nie działa usunąć lub naprawić
        //
        if (stepSensor == null) {
            Toast.makeText(this, "no step sensor", Toast.LENGTH_SHORT).show();
        }


        stepEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                stepValue = event.values[0];
                if (stepValue != 0)
                    view.game.score = view.game.score + 1;
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };

        gps = new GPS(this, this);

        // Location
       // locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    //    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
    //            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
    //        // TODO: Consider calling
    //        //    ActivityCompat#requestPermissions
    //        // here to request the missing permissions, and then overriding
    //        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
    //        //                                          int[] grantResults)
    //        // to handle the case where the user grants the permission. See the documentation
    //        // for ActivityCompat#requestPermissions for more details.
    //        //Toast.makeText(this, "PERMISSION DENIED", Toast.LENGTH_SHORT).show();
    //        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
    //        builder.setTitle("Dostęp do lokalizacji");
    //        builder.setMessage("W celu zapewnienia pełnej funkcjonalności zezwól aplikacji na udostępnianie lokalizacji.");
    //        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
    //            @Override
    //            public void onClick(DialogInterface dialogInterface, int i) {
//
    //                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_COARSE_LOCATION);
    //                requestPermissions(new String[]{Manifest.permission.INTERNET}, PERMISSION_REQUEST_COARSE_LOCATION);
    //                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
    //                requestPermissions(new String[]{Manifest.permission.LOCATION_HARDWARE}, PERMISSION_REQUEST_COARSE_LOCATION);
//
//
    //            }
    //        });
    //        builder.setNegativeButton(android.R.string.no, null);
    //        builder.show();
    //    }
//
//
    //    locationRequest = new LocationRequest()
    //            .setFastestInterval(300)
    //            .setInterval(300)
    //            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//
//
    //    locationBuilder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
//
    //    Task<LocationSettingsResponse> resultSettings =
    //            LocationServices.getSettingsClient(this).checkLocationSettings(locationBuilder.build());
//
    //    resultSettings.addOnFailureListener(this, new OnFailureListener() {
    //        @Override
    //        public void onFailure(@NonNull Exception e) {
    //            if (e instanceof ResolvableApiException) {
    //                try {
    //                    ResolvableApiException resolvableApiException = (ResolvableApiException) e;
    //                    resolvableApiException.startResolutionForResult(MainActivity.this, PERMISSION_REQUEST_COARSE_LOCATION);
    //                } catch (IntentSender.SendIntentException ex) {
    //                    ex.printStackTrace();
    //                }
    //            }
    //        }
    //    });
//
      //  locationCallback = new LocationCallback() {
      //      @Override
      //      public void onLocationResult(LocationResult locationResult) {
      //          if (locationRequest == null) {
      //              Toast.makeText(MainActivity.this, "nulll kurwa", Toast.LENGTH_SHORT).show();
      //              return;
      //          }
      //          for (Location location : locationResult.getLocations()) {
      //             // Toast.makeText(MainActivity.this, String.valueOf(location.getLatitude()), Toast.LENGTH_SHORT).show();
      //              getCity(location.getLatitude(), location.getLongitude());
      //              getSupportActionBar().setTitle(CITY);
      //          }
      //      }
      //  };

    // Location
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                return true;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                view.game.move(Direction.DOWN);
                return true;
            case KeyEvent.KEYCODE_DPAD_UP:
                view.game.move(Direction.UP);
                return true;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                view.game.move(Direction.LEFT);
                return true;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                view.game.move(Direction.RIGHT);
                return true;
            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean("hasState", true);
        save();
    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(lightSensor);  //for light sensor
        sensorManager.unregisterListener(gyroscopeSensor);  //for gyroscope
        sensorManager.unregisterListener(stepEventListener); // for pedometer
        sensorManager.unregisterListener(proximitySensor);
        unregisterReceiver(batteryReceiver);
        //fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        gps.getFusedLocationProviderClient().removeLocationUpdates(gps.getLocationCallback());
        save();
    }

    protected void onResume() {
        super.onResume();
       // startLocationUpdates();
        gps.startLocationUpdates(this);
        sensorManager.registerListener(lightSensor, lightSensor.getLightSensor(), SensorManager.SENSOR_DELAY_FASTEST);  //for light sensor
        sensorManager.registerListener(gyroscopeSensor, gyroscopeSensor.getGyroscopeSensor(), SensorManager.SENSOR_DELAY_UI); // for gyroscope
        sensorManager.registerListener(stepEventListener, stepSensor, SensorManager.SENSOR_DELAY_UI );// for pedometer
        sensorManager.registerListener(proximitySensor, proximitySensor.getProximitySensor(), SensorManager.SENSOR_DELAY_UI);
        registerReceiver(batteryReceiver, intentFilter);

        load();
    }

    private void save() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = settings.edit();
        Tile[][] field = view.game.grid.field;
        Tile[][] undoField = view.game.grid.undoField;
        editor.putInt(WIDTH, field.length);
        editor.putInt(HEIGHT, field.length);
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[0].length; j++) {
                if (field[i][j] != null) {
                    editor.putInt(i + " " + j, field[i][j].getValue());
                } else {
                    editor.putInt(i + " " + j, 0);
                }

                if (undoField[i][j] != null) {
                    editor.putInt(UNDO_GRID + i + " " + j, undoField[i][j].getValue());
                } else {
                    editor.putInt(UNDO_GRID + i + " " + j, 0);
                }
            }
        }
        editor.putLong(SCORE, view.game.score);
        editor.putLong(HIGH_SCORE, view.game.highScore);
        editor.putLong(UNDO_SCORE, view.game.lastScore);
        editor.putBoolean(CAN_UNDO, view.game.canUndo);
        editor.putInt(GAME_STATE, view.game.gameState);
        editor.putInt(UNDO_GAME_STATE, view.game.lastGameState);
        editor.commit();
    }

    private void load() {
        //Stopping all animations

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        for (int i = 0; i < view.game.grid.field.length; i++) {
            for (int j = 0; j < view.game.grid.field[0].length; j++) {
                int value = settings.getInt(i + " " + j, -1);
                if (value > 0) {
                    view.game.grid.field[i][j] = new Tile(i, j, value);
                } else if (value == 0) {
                    view.game.grid.field[i][j] = null;
                }

                int undoValue = settings.getInt(UNDO_GRID + i + " " + j, -1);
                if (undoValue > 0) {
                    view.game.grid.undoField[i][j] = new Tile(i, j, undoValue);
                } else if (value == 0) {
                    view.game.grid.undoField[i][j] = null;
                }
            }
        }

        view.game.score = settings.getLong(SCORE, view.game.score);
        view.game.highScore = settings.getLong(HIGH_SCORE, view.game.highScore);
        view.game.lastScore = settings.getLong(UNDO_SCORE, view.game.lastScore);
        view.game.canUndo = settings.getBoolean(CAN_UNDO, view.game.canUndo);
        view.game.gameState = settings.getInt(GAME_STATE, view.game.gameState);
        view.game.lastGameState = settings.getInt(UNDO_GAME_STATE, view.game.lastGameState);
    }

  //  private void () {
  //      if (fusedLocationProviderClient != null) {
  //              fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, getMainLooper());
  //          }
  //      }

    public void setCityBar(String str) {
        getSupportActionBar().setTitle(str);
    }


}