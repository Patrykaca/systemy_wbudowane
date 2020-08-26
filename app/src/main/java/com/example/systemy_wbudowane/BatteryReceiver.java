package com.example.systemy_wbudowane;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.widget.Toast;

public class BatteryReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if(action != null && action.equals(Intent.ACTION_BATTERY_CHANGED)){
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
                MainActivity.view.setLightTheme();
            } else {
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

                int batteryPrc = level * 100 / scale;

                if ( batteryPrc < 20) {
                    MainActivity.view.setDarkTheme();
                } else {
                    MainActivity.view.setLightTheme();
                }
            }
        }
        MainActivity.view.invalidate();
    }
}
