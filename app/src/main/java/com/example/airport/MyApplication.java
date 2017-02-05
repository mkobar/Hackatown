package com.example.airport;

import android.app.Application;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import java.util.Locale;
import java.util.UUID;


public class MyApplication extends Application implements TextToSpeech.OnInitListener {

    private BeaconManager beaconManager;
    private TextToSpeech tts;

    @Override
    public void onCreate() {
        super.onCreate();

        tts = new TextToSpeech(this, this);

        beaconManager = new BeaconManager(getApplicationContext());
/*
        beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
            @Override
            public void onEnteredRegion(Region region, List<Beacon> list) {
                // TODO: application response

                if (!list.isEmpty()){


                    Beacon closest = list.get(0);
                    double distance = Utils.computeAccuracy(closest);
                    int result =(int)Math.ceil(distance);

                    if (result < 3){
                        Log.d("BEACONS", "Distance: " + result);
//                    speakOut(Integer.toString(result) + " meters");
                        speakOut("You are at a red light");
                    }
//                Log.d("BEACONS", "Distance: " + result);
//                speakOut(Integer.toString(result) + " meters");

                    //EstimoteCloudBeaconDetails details = (EstimoteCloudBeaconDetails) closest;

                }

            }
            @Override
            public void onExitedRegion(Region region) {
                // could add an "exit" notification too if you want (-:
            }
        });
*/
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startMonitoring(new Region("monitored region",
                        UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6e"), null, null));
            }
        });
    }

    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.ENGLISH);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }
}