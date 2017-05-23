package com.example.asrafkaizen.foregroundcompass;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

/**
 * Created by asrafkaizen on 5/16/2017.
 */

public class ForegroundService extends Service implements SensorEventListener {

    private static final String TAG = "ForegroundService";

    // device sensor manager
    private SensorManager mSensorManager;
    public float currentDegree = 0f;
    public PendingIntent pi;
    private float oldDegree = 90;

    @Override
    public void onCreate(){
        super.onCreate();
    }

    public int onStartCommand(Intent intent, int flags, int startID){
        if (intent.getAction().equals(MainActivity.STARTSERVICE)){
            Log.i(TAG, "Received Start Service Intent ");

            compass();

            Intent notificationIntent = new Intent(this, MainActivity.class);
            notificationIntent.setAction(MainActivity.MAINACTION);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                    notificationIntent, 0);

            buildNoti(pendingIntent);
            pendingIntent = pi;

        } else if (intent.getAction().equals(
                MainActivity.STOPSERVICE)) {
            Log.i(TAG, "Received Stop Foreground Intent");
            stopForeground(true);
            stopSelf();
        }
        return START_STICKY;
    }

    private void compass(){
        Log.e(TAG, "Line 76 : compass method caleled");
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        // for the system's orientation sensor registered listeners
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // get the angle around the z-axis rotated
        currentDegree = Math.round(event.values[0]);

        //make sure oldDegree does not become negative
        if ( oldDegree < 0.0 ){
            oldDegree += 360.0;
        }else if (oldDegree > 360.0){
            oldDegree -= 360.0;
        }

        if ( currentDegree > (oldDegree + 15.0) || currentDegree < (oldDegree - 15.0)) {
            oldDegree = currentDegree;
            buildNoti(pi);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //not in use
    }

    @Override
    public void onDestroy(){
        Log.e(TAG, "Service is destroyed");
        mSensorManager.unregisterListener(this);
        super.onDestroy();
    }

    public void buildNoti(PendingIntent pendingIntent){
        Log.e(TAG, "Line 98, noti made");

        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.compass);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Compass")
                .setTicker("Compass ticker")
                .setContentText(Float.toString(currentDegree))
                .setSmallIcon(R.mipmap.compass)
                .setLargeIcon(
                        Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setAutoCancel(true)
                .build();

        startForeground(MainActivity.NOTIFICATION_ID_FOREGROUND_SERVICE,
                notification);
    }

}
