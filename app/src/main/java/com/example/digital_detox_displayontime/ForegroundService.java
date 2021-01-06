package com.example.digital_detox_displayontime;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import static com.example.digital_detox_displayontime.App.CHANNEL_ID;

public class ForegroundService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    //Wird jedes Mal ausgeführt, wenn der Service (startService) genutzt wird
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
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
        //Was passiert, wenn das System den Service killt
        // START_NOT_STICKY = Service beginnt, wird aber nicht erneut ausgeführt
        // START_STICKY = System startet sobald wie möglich den Service (Intent bleibt aber NULL
        // START_REDELIVER_INTENT = System startet den Service neu und übermittelt das letzte Intent
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
