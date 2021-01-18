package com.example.digital_detox_displayontime;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AppOpsManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

//Version 1.0 (04-01-2021)
//Robert Lange and Daniela Scheling

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Chronometer chronometer;
    private long pauseOffset;
    private boolean running;
    public boolean stopServiceClicked = false;
    public boolean isScreenOn = false;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chronometer = findViewById(R.id.chronometer);
//      chronometer.setFormat("Time: %s");
        chronometer.setBase(SystemClock.elapsedRealtime());

        // Start Service
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        serviceIntent.putExtra("notification", "Die App läuft im Hintergrund.");
        startService(serviceIntent);

//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//
//            }
//        }, 0, 1000);
//        timer.cancel();


        //while (stopServiceClicked != true) {
            // Prüft, ob Display an oder aus.
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                DisplayManager displayManager = (DisplayManager) getSystemService(DISPLAY_SERVICE);
                for (Display display : displayManager.getDisplays()) {
                    if (display.getState() != Display.STATE_OFF) {
                        if (isScreenOn != true) {
                            Log.d(TAG, "DisplayManager_isScreenOn: true");
                            isScreenOn = true;
                            if (!running) {
                                chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
                                chronometer.start();
                                running = true;
                            }
                        }
                    } else if (display.getState() == Display.STATE_OFF) {
                        if (isScreenOn != false) {
                            Log.d(TAG, "DisplayManager_isScreenOn: false");
                            isScreenOn = false;
                            if (running) {
                                chronometer.stop();
                                pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
                                running = false;
                            }
                        }
                    }
                }
            } else {
                PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
                if (powerManager.isScreenOn() == true) {
                    if (isScreenOn != true) {
                        Log.d(TAG, "PowerManager_isScreenOn: true");
                        isScreenOn = true;
                        if (!running) {
                            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
                            chronometer.start();
                            running = true;
                        }
                    }
                } else if (powerManager.isScreenOn() == false) {
                    if (isScreenOn != false) {
                        Log.d(TAG, "PowerManager_isScreenOn: false");
                        isScreenOn = false;
                        if (running) {
                            chronometer.stop();
                            pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
                            running = false;
                        }
                    }
                } else {
                    Log.d(TAG, "PowerManager: Irgendetwas lief schief");
                }
            }
        }
    //}


    //Beendet den Service
    public void stopService(View v) {
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        stopService(serviceIntent);
        stopServiceClicked = true;
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
}