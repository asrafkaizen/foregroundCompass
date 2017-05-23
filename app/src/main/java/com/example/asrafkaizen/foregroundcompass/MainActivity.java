package com.example.asrafkaizen.foregroundcompass;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final String STARTSERVICE = "StartForegroundService";
    public static final String STOPSERVICE = "StopForegroundService";
    public static final String MAINACTION = "MainAction";
    Button btn;

    public static final int NOTIFICATION_ID_FOREGROUND_SERVICE = 32;

    //listens to whether service is running

    private boolean serviceRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (ForegroundService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = (Button) findViewById(R.id.btn);
    }

    public void onClick(View v) {
        if (serviceRunning(getApplicationContext())) {
            startService();
        } else {
            stopService();
        }
    }

    public void startService() {
        btn.setText("START SERVICE");
        Intent startIntent = new Intent(MainActivity.this, ForegroundService.class);
        startIntent.setAction(STOPSERVICE);
        startService(startIntent);
    }

    public void stopService() {
        btn.setText("STOP SERVICE");
        Intent startIntent = new Intent(MainActivity.this, ForegroundService.class);
        startIntent.setAction(STARTSERVICE);
        startService(startIntent);
    }
}
