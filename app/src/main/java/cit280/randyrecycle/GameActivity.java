package cit280.randyrecycle;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import android.media.MediaPlayer;
import android.media.AudioManager;
import android.media.SoundPool;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;

import java.io.IOException;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Random;

import static cit280.randyrecycle.R.drawable.randy;
import static cit280.randyrecycle.R.id.collectedValue;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class GameActivity extends AppCompatActivity implements View.OnTouchListener {

    //loading in PNG files Sam created, Aaron/Graydon/Nick
    //Also getting screen size for spawning objects off screen
    private ImageView posbottle;
    private ImageView poscan;
    private ImageView posmilk;
    private ImageView posmag;
    private ImageView randyNew;
    private int frameHeight = 300;
    private int frameWidth;
    private int randySize;
    private int screenWidth;
    private int screenHeight;
    private int randyY = (450);
    private float randyX;
    private int posbottleX;
    private int posbottleY;
    private int poscanX;
    private int poscanY;
    private int posmagX;
    private int posmagY;
    private int posmilkX;
    private int posmilkY;
    private int score;
    private TextView collectedValue;
    MediaPlayer gameSong;  //create media player

    //instantiate variables to change speed of certain objects falling, Aaron/Graydon
    private int bottleSpeed;
    private int canSpeed;
    private int magSpeed;
    private int milkSpeed;

    SoundPool soundPool;// For sound FX
    int posID = -1;
    int negID = -1;
    int dieID = -1;
    int loseLifeID = -1;
    int pos2ID = -1;
    int pos3ID = -1;
    
    //flags to start game and see if screen has been touched Aaron
    private boolean action_flg = false;
    private boolean start_flg = false;

    //handler to handle runnables objects Aaron/Graydon, timer to start objects falling
    private Handler handler = new Handler();
    private Timer timer = new Timer();

    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        
         if( gameSong == null) {
            gameSong = MediaPlayer.create(GameActivity.this, R.raw.game_act); //song from raw folder
            gameSong.setLooping(true);
            gameSong.start();
        }
        
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        //Create and start countdown Timer Nick/Aaron
        new CountDownTimer(60000, 1000) {
            TextView timerText = (TextView) findViewById(R.id.timerValue);

            public void onTick(long millisUntilFinished) {
                timerText.setText(""+millisUntilFinished / 1000);
            }

            public void onFinish() {
                timerText.setText(""+0);
            }
        }.start();

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.frame_layout);

        //force landscape orientation Nick
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);


        // Set up the user interaction to manually show or hide the system UI. Nick
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.

        findViewById(R.id.randy).setOnTouchListener(mDelayHideTouchListener);

        //for onTouch Nick
        final View dragView = findViewById(R.id.randy);
        dragView.setOnTouchListener(this);

        //loading image views for falling objects Graydon/Aaron/Stuart/Sam
        //negbattery = (ImageView) findViewById(R.id.negbattery);
        //negcandy = (ImageView) findViewById(R.id.negcandy);
        //negcone = (ImageView) findViewById(R.id.negcone);
        //negflower = (ImageView) findViewById(R.id.negflower);
        posbottle = (ImageView) findViewById(R.id.posbottle);
        poscan = (ImageView) findViewById(R.id.poscan);
        posmilk = (ImageView) findViewById(R.id.posmilk);
        posmag = (ImageView) findViewById(R.id.posmag);
        collectedValue = (TextView) findViewById(R.id.collectedValue);

        //get screen sizes to prepare display Aaron/Graydon
        WindowManager wm = getWindowManager();
        Display disp = wm.getDefaultDisplay();
        Point size = new Point();
        disp.getSize(size);
        //set screen height/width base on above
        screenWidth = size.x;
        screenHeight = size.y;

        //set speed variables, change number (lower = faster), moves relative to
        // proportion of screen size Graydon/Aaron
        bottleSpeed = Math.round(screenHeight/100);
        canSpeed = Math.round(screenHeight/100);
        magSpeed = Math.round(screenHeight/100);
        milkSpeed = Math.round(screenHeight/100);

        //set start positions for objects being spawned when game starts
        //Graydon/Aaron
        posbottle.setX(500);
        posbottle.setY(0);
        poscan.setX(500);
        poscan.setY(-(10));
        posmag.setX(500);
        posmag.setY(-(10));
        posmilk.setX(500);
        posmilk.setY(-(10));

     soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);   // Load the sounds
        try {
            AssetManager assetManager = this.getAssets();
            AssetFileDescriptor descriptor;
            descriptor = assetManager.openFd("pos.wav");
            posID = soundPool.load(descriptor, 0);
            descriptor = assetManager.openFd("pos2.wav");
            pos2ID = soundPool.load(descriptor, 0);
            descriptor = assetManager.openFd("pos3.wav");
            pos3ID = soundPool.load(descriptor, 0);
            descriptor = assetManager.openFd("neg.wav");
            negID = soundPool.load(descriptor, 0);
            descriptor = assetManager.openFd("lose.wav");
            loseLifeID = soundPool.load(descriptor, 0);
            descriptor = assetManager.openFd("die.wav");
            dieID = soundPool.load(descriptor, 0);

        } catch (IOException e) {
            Log.e("error", "failed to load sound files"); // Print an error message
        }

    }


    public void changePos() {

        hitCheck();

        //positive items
        posbottleY += bottleSpeed;
        if (posbottleY > randyY) {
            posbottleY = -(80);
            posbottleX = (int) Math.floor(Math.random() * (screenWidth + posbottle.getWidth()));
        }
        posbottle.setX(posbottleX);
        posbottle.setY(posbottleY);

        poscanY += canSpeed;
        if (poscanY > randyY) {
            poscanY = -(20);
            poscanX = (int) Math.floor(Math.random() * (screenWidth + poscan.getWidth()));
        }
        poscan.setX(poscanX);
        poscan.setY(poscanY);

        posmagY += 2;
        if (posmagY > randyY) {
            posmagY = -(100);
            posmagX = (int) Math.floor(Math.random() * (screenWidth + posmag.getWidth()));
        }
        posmag.setX(posmagX);
        posmag.setY(posmagY);

        posmilkY += milkSpeed;
        if (posmilkY > randyY) {
            posmilkY = -(40);
            posmilkX = (int) Math.floor(Math.random() * (screenWidth + posmilk.getWidth()));
        }
        posmilk.setX(posmilkX);
        posmilk.setY(posmilkY);

        //update score
        collectedValue.setText("" + score);


    }


    public void hitCheck() {

        //if the center of the item touches randy, it hits
        //bottle hit check. This causes points to go up and items to despawn
        int bottleCenterX = posbottleX + posbottle.getWidth() / 2;
        int bottleCenterY = posbottleY + posbottle.getHeight() / 2;

        if(bottleCenterX == randyX){score += 1;
        posbottleX = 200;
        }

        if (0 <= bottleCenterX && bottleCenterX <= randyX + randySize && randyY <= bottleCenterY &&
                bottleCenterY <= randyY + randySize){
            score += 1;
            posbottleX = 100;
        }

        //can hitcheck
        int canCenterX = poscanX + poscan.getWidth() / 2;
        int canCenterY = poscanY + poscan.getHeight() / 2;
        if (0 <= canCenterX && canCenterX <= randySize &&
                randyY <= canCenterY && canCenterY <= randyY + randySize){
            soundPool.play(posID, 1, 1, 0, 0, 1);
            score += 2;
            poscanX = 100;
        }

        //mag hitcheck
        int magCenterX = posmagX + posmag.getWidth() / 2;
        int magCenterY = posmagY + posmag.getHeight() / 2;


        if (0 <= magCenterX && magCenterX <= randySize &&
                randyY <= magCenterY && magCenterY <= randyY + randySize){
            soundPool.play(pos2ID, 1, 1, 0, 0, 1);
            score += 3;
            posmagX = 100;
        }

        //milk hit check
        int milkCenterX = posmilkX + posmilk.getWidth() / 2;
        int milkCenterY = posmilkY + posmilk.getHeight() / 2;


        if (0 <= milkCenterY && milkCenterY <= randySize &&
                randyX <= milkCenterX && milkCenterX <= randyX + randySize){
            soundPool.play(pos3ID, 1, 1, 0, 0, 1);
            score += 4;
            posmilkX = 100;
        }


    }

        //URL=http://www.singhajit.com/android-draggable-view/
    //Move randy on x by dragging finger Nick
    float dX, dY;
    int lastAction;
    public boolean onTouch(View view, MotionEvent event) {
        //tap to start
        if (start_flg == false){
            start_flg = true;
            //starts the objects falling Graydon/Aaron
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            changePos();
                        }
                    });
                }
            }, 0, 20);
        }

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                //start object falling when screen is touched!!



                dX = view.getX() - event.getRawX();
                //dY = view.getY() - event.getRawY();
                lastAction = MotionEvent.ACTION_DOWN;
                break;

            case MotionEvent.ACTION_MOVE:
                //view.setY(event.getRawY() + dY);
                float newX = event.getRawX() + dX;
                if(newX>-885 && newX<1015){
                    view.setX(newX);
                    randyX = newX;
                }
                double testx =event.getRawX() + dX;
                System.out.println(testx);
                lastAction = MotionEvent.ACTION_MOVE;
                break;

            case MotionEvent.ACTION_UP:
                if (lastAction == MotionEvent.ACTION_DOWN)
                break;

            default:
                return false;
        }
        return true;


    }



    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button.
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
