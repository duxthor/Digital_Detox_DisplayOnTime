package com.example.digital_detox_displayontime;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AppOpsManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.View;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

//Version 1.0 (04-01-2021)
//Robert Lange and Daniela Scheling

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Chronometer chronometer;
    private long pauseOffset;
    private boolean running;
    private boolean isScreenOn;
    private EditText editTextInput;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    public static String SCREENONTIME_COUNTER ="Screen on time Counter";
    private TextView screenOnTime_view;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextInput = findViewById(R.id.edit_text_input);
        chronometer = findViewById(R.id.chronometer);
//      chronometer.setFormat("Time: %s");
        chronometer.setBase(SystemClock.elapsedRealtime());

        sharedPreferences = getSharedPreferences("@strings/app_name", MODE_PRIVATE);
        if (!checkUsageStatsAllowedOrNot()) {
            Intent usageAccessIntent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            usageAccessIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(usageAccessIntent);
            if (checkUsageStatsAllowedOrNot()) {
                startService(new Intent(MainActivity.this, BackgroundService.class));
            } else {
                Toast.makeText(getApplicationContext(), "Berechtigungen werden benötigt.", Toast.LENGTH_SHORT).show();
            }
        } else {
            startService(new Intent(MainActivity.this, BackgroundService.class));
        }
        screenOnTime_view = findViewById(R.id.screen_on_time);
        TimerTask updateView = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        long screen_on_time = sharedPreferences.getLong(SCREENONTIME_COUNTER,0);
                        long second = (screen_on_time/1000)%60;
                        long minute = (screen_on_time/(1000*60))%60;
                        long hour = (screen_on_time/(1000*60*60));
                        String screenOnTime_value = hour + " h " + minute + " m " + second +  " s ";
                        screenOnTime_view.setText(screenOnTime_value);
                    }
                });
            }
        };
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(updateView,0, 1000);


//        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
//        new Timer().scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                if (powerManager.isScreenOn() == true) {
//                    Log.d(TAG, "isScreenOn: true");
//                    if (!running) {
//                        chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
//                        chronometer.start();
//                        running = true;
//                    }
//                } else if (powerManager.isScreenOn() == false) {
//                    Log.d(TAG, "isScreenOn: false");
//                    if (running) {
//                        chronometer.stop();
//                        pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
//                        running = false;
//                    }
//                } else {
//                    Log.d(TAG, "isScreenOn: Irgendetwas lief schief");
//                }
//            }
//        }, 0, 5000); //put here time 1000 milliseconds=1 second


//        //Wird jede Sekunde ausgeführt
//        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
//            @Override
//            public void onChronometerTick(Chronometer chronometer) {
//                //Nach 10 Sekunden wird der Timer zurück gesetz
//                if (SystemClock.elapsedRealtime() - chronometer.getBase() >= 10000) {
//                    chronometer.setBase(SystemClock.elapsedRealtime());
//                    Toast.makeText(MainActivity.this, "10 sec. over", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

    }

    //Übermittelt einen String, der als Benachrichtigung angezeigt wird. Es verhindert
    //das Beenden der App durch das System (ForegroundService)
    public void startService(View v) {
        String input = editTextInput.getText().toString();
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        serviceIntent.putExtra("notification", input);
        startService(serviceIntent);
    }

    //Beendet den Service
    public void stopService(View v) {
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        stopService(serviceIntent);
    }

    //Das onClick Attribut des Buttons versucht ein View zur Methode zu Schicken (vllt ohne onClick versuchen)
    public void startChronometer(View v) {
        if (!running) {
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            chronometer.start();
            running = true;
        }
    }

    public void pauseChronometer(View v) {
        if (running) {
            chronometer.stop();
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
            running = false;
        }
    }

    public void resetChronometer(View v) {
        chronometer.setBase(SystemClock.elapsedRealtime());
        pauseOffset = 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public boolean checkUsageStatsAllowedOrNot() {
        try{
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager)getSystemService(APP_OPS_SERVICE);
            int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
            return (mode == AppOpsManager.MODE_ALLOWED);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Fehler, es konnten keine Nutzungsdaten Manager gefunden werden.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onDestroy() {
        if (checkUsageStatsAllowedOrNot()) {
            startService(new Intent(MainActivity.this, BackgroundService.class));
        }
        super.onDestroy();
    }
}