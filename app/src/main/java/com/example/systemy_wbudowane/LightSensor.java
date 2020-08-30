package com.example.systemy_wbudowane;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.View;
import android.widget.Toast;

import java.util.EventListener;

public class LightSensor implements SensorEventListener {

    private Sensor lightSensor;

    public LightSensor(SensorManager sensorManager, Context context) {
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        if (lightSensor == null) {
            Toast.makeText(context, "no light sensor", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.values[0] == 0) {
            new AlertDialog.Builder(MainActivity.view.getContext())
                    .setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.view.game.NewGame();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .setTitle("Reset game?")
                    .setMessage("Are you sure you wish to reset the game?")
                    .show();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public Sensor getLightSensor() {
        return lightSensor;
    }
}
