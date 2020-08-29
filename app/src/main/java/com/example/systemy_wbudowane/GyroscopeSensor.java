package com.example.systemy_wbudowane;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.Toast;

public class GyroscopeSensor implements SensorEventListener {

    private Sensor gyroscopeSensor;
    private boolean ready = true;

    public GyroscopeSensor(SensorManager sensorManager, Context context) {
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        if (gyroscopeSensor == null) {
            Toast.makeText(context, "no gyroscope sensor", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.values[0] < 0.7f && event.values[0] > -0.7f && event.values[1] < 0.7f && event.values[1] > -0.7f) {
            ready = false;
        }
        if (!ready) {
            if (event.values[0] > 7.5f) {
                MainActivity.view.game.move(Direction.DOWN);
                ready = false;
            } else if (event.values[0] < -7.5f) {
                MainActivity.view.game.move(Direction.UP);
                ready = false;
            } else if (event.values[2] < -7.5f) {
                MainActivity.view.game.move(Direction.RIGHT);
                ready = false;
            } else if (event.values[2] > 7.5f) {
                MainActivity.view.game.move(Direction.LEFT);
                ready = false;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public Sensor getGyroscopeSensor() {
        return gyroscopeSensor;
    }
}
