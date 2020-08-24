package com.example.systemy_wbudowane;

import android.app.VoiceInteractor;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private static final String WIDTH = "width";
    private static final String HEIGHT = "height";
    private static final String SCORE = "score";
    private static final String HIGH_SCORE = "high score temp";
    private static final String UNDO_SCORE = "undo score";
    private static final String CAN_UNDO = "can undo";
    private static final String UNDO_GRID = "undo";
    private static final String GAME_STATE = "game state";
    private static final String UNDO_GAME_STATE = "undo game state";
    private MainView view;
    private SensorManager sensorManager;
    private Sensor lightSensor;
    private Sensor gyroscopeSensor;
    private Sensor proximitySensor;
    private SensorEventListener lightEventListener;
    private SensorEventListener gyroscopeEventListener;
    private SensorEventListener proximityEventListener;
    public float lightValue;
    boolean ready = true;
    public static Sounds sound;
    public static Vibrator vibro;
    private Sensor stepSensor;
    private SensorEventListener stepEventListener;
    private float stepValue;
    double latitude;
    double longitude;


    private LocationManager locationManager;

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
        view = new MainView(this);
        sound = new Sounds(this);
        vibro = (Vibrator) getSystemService(VIBRATOR_SERVICE);


        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        view.hasSaveState = settings.getBoolean("save_state", false);

        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean("hasState")) {
                load();
            }
        }
        setContentView(view);

        //light sensor
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        stepValue = 0;
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        stepValue=0;


        if (stepSensor == null) {
            Toast.makeText(this, "no step sensor", Toast.LENGTH_SHORT).show();
        }
        if (gyroscopeSensor == null) {
            Toast.makeText(this, "no gyroscope sensor", Toast.LENGTH_SHORT).show();
        }

        if (lightSensor == null) {
            Toast.makeText(this, "no light sensor", Toast.LENGTH_SHORT).show();
        }

        if (proximitySensor == null) {
            Toast.makeText(this, "no proximity sensor", Toast.LENGTH_SHORT).show();
        } else {
            sensorManager.registerListener(proximityEventListener, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
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
        lightEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                lightValue = event.values[0];
                //getSupportActionBar().setTitle("light " + lightValue);  //show light value
                if (lightValue == 0) {
                    view.game.revertUndoState();
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };

        gyroscopeEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {

                if (event.values[0] < 0.7f && event.values[0] > -0.7f && event.values[1] < 0.7f && event.values[1] > -0.7f) {
                    ready = false;
                }
                if (!ready) {
                    //getSupportActionBar().setTitle("gyroscope " + event.values[0]);  //show light value

                    if (event.values[0] > 7.5f) {
                        view.game.move(Direction.DOWN);
                        ready = false;
                    } else if (event.values[0] < -7.5f) {
                        view.game.move(Direction.UP);
                        ready = false;
                    } else if (event.values[2] < -7.5f) {
                        view.game.move(Direction.RIGHT);
                        ready = false;
                    } else if (event.values[2] > 7.5f) {
                        view.game.move(Direction.LEFT);
                        ready = false;
                    }
                }

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };


        // Location

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(this, "shit" , Toast.LENGTH_SHORT).show();
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        };

        proximityEventListener = new SensorEventListener() {
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }

        onLocationChanged(location);

        // Location
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_PROXIMITY)
                    if (event.values[0] < 4)
                        Toast.makeText(getApplicationContext(), "nie za blisko ?", Toast.LENGTH_SHORT).show();
            }
        };


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
        sensorManager.unregisterListener(lightEventListener);  //for light sensor
        sensorManager.unregisterListener(gyroscopeEventListener);  //for gyroscope
        sensorManager.unregisterListener(stepEventListener); // for pedometer
        sensorManager.unregisterListener(proximityEventListener);
        save();
    }

    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(lightEventListener, lightSensor, SensorManager.SENSOR_DELAY_FASTEST);  //for light sensor
        sensorManager.registerListener(gyroscopeEventListener, gyroscopeSensor, SensorManager.SENSOR_DELAY_UI); // for gyroscope
        sensorManager.registerListener(stepEventListener, stepSensor, SensorManager.SENSOR_DELAY_UI );// for pedometer
        sensorManager.registerListener(proximityEventListener, proximitySensor, SensorManager.SENSOR_DELAY_UI);
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

    @Override
    public void onLocationChanged(Location location) {
        longitude = location.getLongitude();
        latitude = location.getLatitude();
        Toast.makeText(this, latitude + " " + longitude, Toast.LENGTH_SHORT).show();
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