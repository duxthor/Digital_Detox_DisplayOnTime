package com.example.digital_detox_displayontime;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Chronometer;
import android.widget.EditText;

//Version 1.0 (04-01-2021)
//Robert Lange and Daniela Scheling

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Chronometer chronometer;
    private long pauseOffset;
    private boolean running;
    private boolean isScreenOn;
    private EditText editTextInput;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextInput = findViewById(R.id.edit_text_input);
        chronometer = findViewById(R.id.chronometer);
//      chronometer.setFormat("Time: %s");
        chronometer.setBase(SystemClock.elapsedRealtime());



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


}