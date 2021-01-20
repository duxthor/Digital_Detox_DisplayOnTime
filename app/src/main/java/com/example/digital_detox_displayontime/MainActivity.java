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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
//        chronometer.setFormat("Time: %s");
//        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener(){
//            @Override
//            public void onChronometerTick(Chronometer c) {
//                long time = SystemClock.elapsedRealtime() - c.getBase();
//                int h   = (int)(time /3600000);
//                int m = (int)(time - h*3600000)/60000;
//                int s= (int)(time - h*3600000- m*60000)/1000 ;
//                String hh = h < 10 ? "0"+h: h+"";
//                String mm = m < 10 ? "0"+m: m+"";
//                String ss = s < 10 ? "0"+s: s+"";
//                c.setText(hh + ":" + mm + ":" + ss);
//            }
//        });
        chronometer.setBase(SystemClock.elapsedRealtime());

        // Start Service
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        serviceIntent.putExtra("notification", "Die App läuft im Hintergrund.");
        startService(serviceIntent);

//        // Prüft, ob Display an oder aus.
//        if (stopServiceClicked != true) {
//            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
//                DisplayManager displayManager = (DisplayManager) getSystemService(DISPLAY_SERVICE);
//                for (Display display : displayManager.getDisplays()) {
//                    if (display.getState() != Display.STATE_OFF) {
//                        if (isScreenOn != true) {
//                            Log.d(TAG, "DisplayManager_isScreenOn: true");
//                            isScreenOn = true;
//                            if (!running) {
//                                chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
//                                chronometer.start();
//                                running = true;
//                            }
//                        }
//                    } else if (display.getState() == Display.STATE_OFF) {
//                        if (isScreenOn != false) {
//                            Log.d(TAG, "DisplayManager_isScreenOn: false");
//                            isScreenOn = false;
//                            if (running) {
//                                chronometer.stop();
//                                pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
//                                running = false;
//                            }
//                        }
//                    }
//                }
//            } else {
//                PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
//                if (powerManager.isScreenOn() == true) {
//                    if (isScreenOn != true) {
//                        Log.d(TAG, "PowerManager_isScreenOn: true");
//                        isScreenOn = true;
//                        if (!running) {
//                            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
//                            chronometer.start();
//                            running = true;
//                        }
//                    }
//                } else if (powerManager.isScreenOn() == false) {
//                    if (isScreenOn != false) {
//                        Log.d(TAG, "PowerManager_isScreenOn: false");
//                        isScreenOn = false;
//                        if (running) {
//                            chronometer.stop();
//                            pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
//                            running = false;
//                        }
//                    }
//                } else {
//                    Log.d(TAG, "PowerManager: Irgendetwas lief schief");
//                }
//            }
//        }
        checkDisplayOnOrOff();
    }


    //Beendet den Service
    public void stopService(View v) {
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        stopService(serviceIntent);
        stopServiceClicked = true;
    }

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

    // Prüft, ob Display an oder aus.
    public void checkDisplayOnOrOff() {
        if (stopServiceClicked != true) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                DisplayManager displayManager = (DisplayManager) getSystemService(DISPLAY_SERVICE);
                for (Display display : displayManager.getDisplays()) {
                    if (display.getState() != Display.STATE_OFF) {
                        //if (isScreenOn != true) {
                        Log.d(TAG, "DisplayManager_isScreenOn: true");
                        isScreenOn = true;
                        if (!running) {
                            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
                            chronometer.start();
                            running = true;
                        }
                        //}
                        timeout();
                        checkDisplayOnOrOff();
                    } else if (display.getState() == Display.STATE_OFF) {
                        //if (isScreenOn != false) {
                        Log.d(TAG, "DisplayManager_isScreenOn: false");
                        isScreenOn = false;
                        if (running) {
                            chronometer.stop();
                            pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
                            running = false;
                        }
                        //}
                        timeout();
                        checkDisplayOnOrOff();
                    }
                }
            } else {
                PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
                if (powerManager.isScreenOn() == true) {
                    //if (isScreenOn != true) {
                    Log.d(TAG, "PowerManager_isScreenOn: true");
                    isScreenOn = true;
                    if (!running) {
                        chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
                        chronometer.start();
                        running = true;
                    }
                    //}
                    timeout();
                    checkDisplayOnOrOff();
                } else if (powerManager.isScreenOn() == false) {
                    //if (isScreenOn != false) {
                    Log.d(TAG, "PowerManager_isScreenOn: false");
                    isScreenOn = false;
                    if (running) {
                        chronometer.stop();
                        pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
                        running = false;
                    }
                    //}
                    timeout();
                    checkDisplayOnOrOff();
                } else {
                    Log.d(TAG, "PowerManager: Irgendetwas lief schief");
                    //timeout();
                    checkDisplayOnOrOff();
                }
            }
        }
    }

    public void timeout() {
        final long startTime = System.currentTimeMillis();
        // Log.d(TAG, "calling runWithTimeout!");
        try {
            TimeLimitedCodeBlock.runWithTimeout(new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.d(TAG, "starting sleep!");
                        Thread.sleep(10000);
                        // Log.d(TAG, "woke up!");
                    } catch (InterruptedException e) {
                        // Log.d(TAG, "was interrupted!");
                    }
                }
            }, 5, TimeUnit.SECONDS);
        } catch (Exception e) {
            // Log.d(TAG, "got timeout!");
        }
        // Log.d(TAG, "end of main method!");
    }
}