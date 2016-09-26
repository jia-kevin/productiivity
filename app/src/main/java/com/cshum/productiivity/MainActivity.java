package com.cshum.productiivity;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.hardware.SensorEventListener;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    public TextView label;
    public boolean go = false;
    public double timerValue;
    public MediaPlayer mediaGit;
    public MediaPlayer mediaRah;
    public int git = R.raw.git2;
    public int rah = R.raw.rah;
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private long lastUpdate = 0;
    private float last_x = 0, last_y = 0, last_z = 0;
    private static final int SHAKE_THRESHOLD = 250;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        gitCreate();
        rahCreate();

        rahStart();
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                AudioManager.FLAG_SHOW_UI);
    }

    protected void onPause() {
        super.onPause();
        senSensorManager.unregisterListener(this);
    }

    protected void onResume() {
        super.onResume();
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;
        if (go) {
            if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                long curTime = System.currentTimeMillis();

                if ((curTime - lastUpdate) > 20) {
                    long diffTime = (curTime - lastUpdate);
                    lastUpdate = curTime;

                    float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;

                    last_x = x;
                    last_y = y;
                    last_z = z;

                    while (speed > SHAKE_THRESHOLD) {
                        curTime = System.currentTimeMillis();
                        if ((curTime - lastUpdate) > 20) {
                            diffTime = (curTime - lastUpdate);
                            lastUpdate = curTime;
                            gitStart();
                            x = event.values[0];
                            y = event.values[1];
                            z = event.values[2];
                            speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;

                            last_x = x;
                            last_y = y;
                            last_z = z;
                        }

                    }
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    public void gitCreate() {
        mediaGit = MediaPlayer.create(this, git);
    }
    public void gitStart() {
        mediaGit.start();
    }

    public void rahCreate() {
        mediaRah = MediaPlayer.create(this, rah);
    }
    public void rahStart() {
        mediaRah.start();
    }

    public void startTimer(View view) {
        label = (TextView) findViewById(R.id.labelTxt);
        EditText time = (EditText) findViewById(R.id.editTxt);

        label.setText(time.getText().toString());
        timerValue = Double.parseDouble(time.getText().toString());
        go = true;

        new CountDownTimer(Long.parseLong(time.getText().toString()) * 60000, 1000) {
            public void onTick(long millisUntilFinished) {
                label.setText("seconds remaining: " + millisUntilFinished / 1000);
            }
            public void onFinish() {
                label.setText("Finished!");
                go = false;
            }
        }.start();
    }
}
