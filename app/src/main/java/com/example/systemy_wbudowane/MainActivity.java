package com.example.systemy_wbudowane;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


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
    public static String CITY = null;
    private boolean batteryStatusLOW;
    private GPS gps;

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

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        view.hasSaveState = settings.getBoolean("save_state", false);

        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean("hasState")) {
                load();
            }
        }
        setContentView(view);

        gps = new GPS(this, this, this);

        gps.requestLocationPermission(this);

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

    public void setCityBar(String str) {
        getSupportActionBar().setTitle(str);
    }

}