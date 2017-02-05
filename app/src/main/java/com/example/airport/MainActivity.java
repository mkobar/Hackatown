package com.example.airport;

        import android.os.Bundle;
        import android.speech.tts.TextToSpeech;
        import android.support.v4.view.GestureDetectorCompat;
        import android.support.v7.app.AppCompatActivity;
        import android.util.Log;
        import android.view.GestureDetector;
        import android.view.MotionEvent;

        import com.estimote.sdk.Beacon;
        import com.estimote.sdk.BeaconManager;
        import com.estimote.sdk.Region;
        import com.estimote.sdk.SystemRequirementsChecker;
        import com.estimote.sdk.Utils;
    //  import com.N2ChaoGmailCom.Eyebe.estimote.EstimoteCloudBeaconDetails;
//        import com.N2ChaoGmailCom.EyebeaconDwh.estimote.EstimoteCloudBeaconDetailsFactory;
        import java.util.List;
        import java.util.Locale;
        import java.util.Random;
        import java.util.UUID;
        import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener,GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener {

    private BeaconManager beaconManager;
    private Region region;
    private TextToSpeech tts;

    private static final String DEBUG_TAG = "Gestures";
    private GestureDetectorCompat mDetector;

    private boolean running = false;
    private int currentDistance;
    private String beaconName = "";

    private String [] trafficLights =  {"red","red","red","red","green","green","green","green","yellow","yellow"};

    private static int previous = 0 ;
    private static int index = 1;
    Random ran;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tts = new TextToSpeech(this, this);

        beaconManager = new BeaconManager(this);

//        beaconManager.setBackgroundScanPeriod(2000, 3000);
        beaconManager.setForegroundScanPeriod(3000, 0);
        region = new Region("ranged region", UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6e"),null,null);

        mDetector = new GestureDetectorCompat(this,this);
        // Set the gesture detector as the double tap
        // listener.
        mDetector.setOnDoubleTapListener(this);

        ran = new Random();


    }


    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            for (int i =0; i<8000; i++){}
//            try {
//                Thread.sleep(5000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            tutorial();

            int result = tts.setLanguage(Locale.ENGLISH);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");


            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        running = true;
        SystemRequirementsChecker.checkWithDefaultDialogs(this);

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(region);
            }
        });
    }

    @Override
    protected void onPause() {
        beaconManager.stopRanging(region);
        running = false;
        super.onPause();
    }


    private void speakOut(String test) {
        tts.speak(test, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        this.mDetector.onTouchEvent(event);
        // Be sure to call the superclass implementation
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        if (running) {
            onPause();
            tutorial();
            onResume();
        }else{
            tutorial();
        }

        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {

        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
//        Log.d("Hey", "Long press " + currentDistance);
        if(!beaconName.equals("") && running)
            speakOut("About "+ currentDistance + " meters from " +beaconName);


    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if(e1.getY()-e2.getY() > 0) {
            speakOut("Starting");
            running = true;
            onResume();



            beaconManager.setRangingListener(new BeaconManager.RangingListener() {
                @Override
                public void onBeaconsDiscovered(Region region, List<Beacon> list) {


                    if (!list.isEmpty()) {


                        Beacon closest = list.get(0);
                        int major = closest.getMajor();

                        if (major == 61097){
                            beaconName = "Pink Street";
                        }else if (major == 61981){
                            beaconName = "Yellow Street";
                        } else if (major == 23632){
                            beaconName = "Purple Street";
                        }



                        if(previous == 0){
                            previous = major;
                        }
                        else if(previous == major){
                            index = index+1;
                            if(index==10){
                                index = 0;
                            }
                        }else if (previous != major){
                            index = ran.nextInt(9);
                            previous = major;
                        }

                        double distance = Utils.computeAccuracy(closest);
                        int result = (int) Math.ceil(distance);
                        Log.d("BEACONS", "Distance: " + result);
                        currentDistance = result;
                        if (result < 3) {
                           // Log.d("BEACONS", "Distance: " + result);
//                    speakOut(Integer.toString(result) + " meters");
                            speakOut(trafficLights[index]+" Light at "+beaconName);
                        }
                    }
                }
            });
        }else{
            if(running){
                onPause();
                speakOut("Pause");
            }

        }

        return false;
    }

    public void tutorial(){
        speakOut("Swipe up to start, down to pause, long press for distance, double tap for instruction");

    }
}