package com.example.systemy_wbudowane;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class StepDetector implements SensorEventListener {

    private Sensor stepSensor;
    private int PERMISSION_REQUEST_CODE = 1;

    public StepDetector(SensorManager sensorManager, Context context, AppCompatActivity appCompatActivity) {
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        if (stepSensor == null) {
            Toast.makeText(context, "no step detector", Toast.LENGTH_SHORT).show();
        }

        requestStepDetectorPermission(appCompatActivity, context);
    }

    public Sensor getStepSensor() {
        return stepSensor;
    }

    public void requestStepDetectorPermission(AppCompatActivity appCompatActivity, Context context) {
          if(ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(appCompatActivity, new  String[]{Manifest.permission.ACTIVITY_RECOGNITION}, PERMISSION_REQUEST_CODE);
          }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.values[0] == 1) {
            MainActivity.view.game.score = MainActivity.view.game.score + 1;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
