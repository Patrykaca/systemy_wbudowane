package com.example.systemy_wbudowane;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.Toast;

public class ProximitySensor implements SensorEventListener {

    private Sensor proximitySensor;

    public ProximitySensor(SensorManager sensorManager, Context context) {
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        if (proximitySensor == null) {
            Toast.makeText(context, "no proximity sensor", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.values[0] < 3) {
            MainActivity.view.game.revertUndoState();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public Sensor getProximitySensor() {
        return proximitySensor;
    }
}
