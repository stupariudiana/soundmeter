package com.example.soundmeter;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.random;


public class MainActivity extends AppCompatActivity {
    TextView soundDB;
    TextView soundPa;
    TextView reference;
    MediaRecorder sound;

    ProgressBar soundBar;

    private static double ambience = 0.0;
    static final private double filter = 0.6;

    public static final int RECORD_AUDIO = 0;

    Thread runner;

    final Runnable updater = new Runnable() {
        @Override
        public void run() {
            updateAudio();
        }
    };

    final Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        soundDB = findViewById(R.id.valoareDb);
        reference = findViewById(R.id.reference);
        soundBar = findViewById(R.id.progressBar2);

        if (runner == null)
        {
            runner = new Thread() {public void run (){
                while (runner != null)
                {
                    try{
                        Thread.sleep(4000);
                        mHandler.post(updater);
                        Log.i("Noise", "Tock" + runner);

                    }catch (InterruptedException e){}
                    Log.d("test", "test " );
                   // mHandler.post(updater);
                }
            }};
            runner.start();
            Log.d("Noise", "start runner()");
        }

    }

    public void onResume() {
        super.onResume();
        updateAudio();
    }

    public void onPause() {
        super.onPause();
        stopAudio();
    }


    public void stopAudio(){
        if (sound != null){
            sound.stop();
            sound.release();
            sound = null;
        }
    }

    @SuppressLint("SetTextI18n")
    public void updateAudio(){
        if (sound == null)
        {
            Log.d("Noise", "AICI");
            sound = new MediaRecorder();
            sound.setAudioSource(MediaRecorder.AudioSource.MIC);
            sound.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            sound.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            sound.setOutputFile("/dev/null");

            try{
                sound.prepare();
            }catch (java.io.IOException ioe){
                android.util.Log.e("[MS]", "IOException: " +
                        android.util.Log.getStackTraceString(ioe));
            }catch (java.lang.SecurityException e){
                android.util.Log.e("[MS]", "SecurityException: " +
                        android.util.Log.getStackTraceString(e));
            }
            try{
                sound.start();

            }catch (java.lang.SecurityException e){
                android.util.Log.e("[MS MAI JOS]", "SecurityException: " +
                        android.util.Log.getStackTraceString(e));
            }
        }
        Log.d("Noise", "updateAAAAAAAAAAAAA");

        DecimalFormat dfl = new DecimalFormat("####.0");
        double decib = soundDb(20);
        String decibl = dfl.format(decib);

        soundBar.setProgress((int) decib);
        soundDB.setText(decibl + "dB");
        Log.d("Noise", "updateAAAAAAAAAAAAA " + decib);
        if (decib <10)
        {
            reference.setText("Almost Silent.");
            soundDB.setTextColor(Color.rgb(0,132,194));
        }
        else  if (decib <20)
        {
            reference.setText("Clock Sound... Tik Tok.");
            soundDB.setTextColor(Color.rgb(0,190,194));
        }
        else  if (decib <30)
        {
            reference.setText("Someone is whispering...");
            soundDB.setTextColor(Color.rgb(0,194,194));
        }
        else  if (decib <40)
        {
            reference.setText("We are in a library now!");
            soundDB.setTextColor(Color.rgb(0,194,63));
        }
        else  if (decib <50)
        {
            reference.setText("Outside is raining.");
            soundDB.setTextColor(Color.rgb(34,194,0));
        }
        else  if (decib <60)
        {
            reference.setText("This is a normal conversation.");
            soundDB.setTextColor(Color.rgb(114,194,5));
        }
        else  if (decib <70)
        {
            reference.setText("High Traffic or Vacuum Cleaner.");
            soundDB.setTextColor(Color.rgb(194,187,0));
        }
        else  if (decib <80)
        {
            reference.setText("High Volume Music.");
            soundDB.setTextColor(Color.rgb(233,130,0));
        }
        else  if (decib <90)
        {
            reference.setText("Lawn Mower or Diesel Motor.");
            soundDB.setTextColor(Color.rgb(233,78,0));
        }
        else  if (decib <100)
        {
            reference.setText("Concert.");
            soundDB.setTextColor(Color.rgb(233,27,0));
        }
        else
        {
            reference.setText("Pain.");
            soundDB.setTextColor(Color.rgb(233,27,0));
        }

    }

    public double soundDb (double ampl){

        double dbSPL = 20 * Math.log10(soundPressure()/ampl);
        Log.d("aici in debug idkkdkdkdjd", "valoare:  " + dbSPL);
        if (dbSPL < 0) {
            return 0;
        }
        else {
            return dbSPL;
        }
    }

    public double soundPressure(){
        double amp = soundAmbience();
        ambience = filter * amp * (1.0 - filter) * ambience;
        Log.d("soundPrssure" , "valoare " + ambience);
        return ambience;
    }

    public double soundAmbience(){
        Log.d("soundAMB" , "valoare " + sound);
        if (sound != null){
            return (sound.getMaxAmplitude()/2700.0);
        }
        else
            return 0;
    }
}
