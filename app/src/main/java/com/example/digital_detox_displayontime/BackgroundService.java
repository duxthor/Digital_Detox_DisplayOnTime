package com.example.digital_detox_displayontime;

import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import static com.example.digital_detox_displayontime.MainActivity.SCREENONTIME_COUNTER;

public class BackgroundService extends Service {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    public BackgroundService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sharedPreferences = getSharedPreferences("@strings/app_name", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        TimerTask detectApp = new TimerTask() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                sharedPreferences = getSharedPreferences("@strings/app_name", MODE_PRIVATE);
                editor = sharedPreferences.edit();
                UsageStatsManager usageStatsManager = (UsageStatsManager)getSystemService(USAGE_STATS_SERVICE);
                long endTime = System.currentTimeMillis();
                long beginTime = endTime - (1000);
                List<UsageStats> usageStatsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, beginTime, endTime);
                if (usageStatsList != null) {
                    for (UsageStats usageStat:usageStatsList) {
                        if (usageStat.getPackageName().toLowerCase().contains("com.example.digital_detox_displayontime")) {
                            editor.putLong(SCREENONTIME_COUNTER, usageStat.getTotalTimeInForeground());
                        }
                        editor.apply();
                    }
                }
            }
        };
        Timer detectAppTime = new Timer();
        detectAppTime.scheduleAtFixedRate(detectApp, 0, 1000);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
