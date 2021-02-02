package com.example.digital_detox_displayontime;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import android.view.Display;
import android.widget.Chronometer;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import static com.example.digital_detox_displayontime.App.CHANNEL_ID;

public class ForegroundService extends Service {

//    final class LoopThreadClass implements Runnable {
//        private static final String TAG = "ForegroundService";
//        int service_id;
//
//        LoopThreadClass(int service_id) {
//            this.service_id = service_id;
//        }
//
//        @Override
//        public void run() {
//            MainActivity mainActivity;
//            mainActivity = new MainActivity();
//
//            // Loop this Service
//            int i = 0;
//            synchronized (this) {
//                while (i < 10) {
//
//                    // if (stopServiceClicked != true) {
//                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
//                        DisplayManager displayManager = (DisplayManager) getSystemService(DISPLAY_SERVICE);
//                        for (Display display : displayManager.getDisplays()) {
//                            if (display.getState() != Display.STATE_OFF) {
//                                mainActivity.startChronometerImmediately();
//                            } else if (display.getState() == Display.STATE_OFF) {
//                                mainActivity.stopChronometerImmediately();
//                            }
//                        }
//                    } else {
//                        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
//                        if (powerManager.isScreenOn() == true) {
//                            mainActivity.startChronometerImmediately();
//                        } else if (powerManager.isScreenOn() == false) {
//                            mainActivity.stopChronometerImmediately();
//                        } else {
//                            Log.d(TAG, "PowerManager: Irgendetwas lief schief");
//                        }
//                    }
//
//
//                    try {
//                        wait(1500);
//                        i++;
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//                stopSelf(service_id);
//            }
//        }
//    }


    @Override
    public void onCreate() {
        super.onCreate();
    }

    //Wird jedes Mal ausgeführt, wenn der Service (startService) genutzt wird
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service Started.", Toast.LENGTH_LONG).show();

        //String aus MainActivity.startService() erhalten
        String input = intent.getStringExtra("notification");

        //Startet App, wenn die Benachrichtigung angeklickt wurde
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Foreground Service")
                .setContentText(input)
                .setSmallIcon(R.drawable.ic_android)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);

//        Thread thread = new Thread(new LoopThreadClass(startId));
//        thread.start();

        //Was passiert, wenn das System den Service killt
        // START_NOT_STICKY = Service beginnt, wird aber nicht erneut ausgeführt
        // START_STICKY = System startet sobald wie möglich den Service (Intent bleibt aber NULL
        // START_REDELIVER_INTENT = System startet den Service neu und übermittelt das letzte Intent
//        return START_STICKY;
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service Destroyed.", Toast.LENGTH_LONG).show();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
