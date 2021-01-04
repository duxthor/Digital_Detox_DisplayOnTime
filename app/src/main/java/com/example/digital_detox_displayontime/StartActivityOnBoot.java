package com.example.digital_detox_displayontime;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StartActivityOnBoot extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //Wird gestartet nach dem Boot
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent i = new Intent(context, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //Startet unsere Activity außerhalb der App
            context.startActivity(i);
        }
    }
}
