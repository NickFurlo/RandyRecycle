package cit280.randyrecycle;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import android.widget.FrameLayout;
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
import android.util.Log;
import java.io.IOException;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Random;

import static cit280.randyrecycle.R.id.initalTimer;
import static cit280.randyrecycle.R.id.textView;
import static cit280.randyrecycle.R.id.timerValue;

//import static cit280.randyrecycle.R.drawable.randy;
//import static cit280.randyrecycle.R.id.collectedValue;
//import static cit280.randyrecycle.R.id.timerValue;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class GameActivity extends AppCompatActivity implements View.OnTouchListener {

    //loading in PNG files Sam created, Aaron/Graydon/Nick
    //Also getting screen size for spawning objects off screen
    private boolean canStart = false;
    private ImageView posbottle;
    private ImageView poscan;
    private ImageView posmilk;
    private ImageView posmag;
    private float newX;
    private int randySize = 90;
    private int screenWidth;
    private int screenHeight;
    private int randyY = 1000;
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
    //starting health value, change for levels Graydon
    private int health = 20;
    private TextView collectedValue;
    private TextView healthValue;
    private TextView timerValue;
    MediaPlayer gameSong;  //create media player Sam

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

        //Countdown to start Nick
        new CountDownTimer(4000, 1000) {
            TextView initalTimer = (TextView)findViewById(R.id.initalTimer);
            public void onTick(long millisUntilFinished) {
                initalTimer.setText(Long.toString(millisUntilFinished / 1000));
                if(millisUntilFinished <1000){}
            }
            public void onFinish() {
                initalTimer.setVisibility(View.GONE);
                canStart = true;
            }
        }.start();
        

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

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

        //when gameview is created, put images in Y-position for random respawn Graydon
        posbottle = (ImageView) findViewById(R.id.posbottle);
        posbottleY = 1;
        poscan = (ImageView) findViewById(R.id.poscan);
        poscanY = 1;
        posmilk = (ImageView) findViewById(R.id.posmilk);
        posmilkY = 1;
        posmag = (ImageView) findViewById(R.id.posmag);
        posmagY = 1;
        //timer value for stopping/starting level below
        timerValue = (TextView) findViewById(R.id.timerValue);

        //label textviews for score and health
        collectedValue = (TextView) findViewById(R.id.collectedValue);
        healthValue = (TextView) findViewById(R.id.healthValue);

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
        bottleSpeed = Math.round(screenHeight / 160);
        canSpeed = Math.round(screenHeight / 140);
        magSpeed = Math.round(screenHeight / 130);
        milkSpeed = Math.round(screenHeight / 120);



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
        //calling hit and miss checks while positions are changed
        hitCheck();
        missCheck();

        //positive items
        posbottleY += bottleSpeed;
        if (posbottleY > randyY + 99) {
            posbottleY = -(200);
            posbottleX = (int) Math.floor(Math.random() * (screenWidth + posbottle.getWidth()));
        }
        posbottle.setX(posbottleX);
        posbottle.setY(posbottleY);

        poscanY += canSpeed;
        if (poscanY > randyY + 99) {
            poscanY = -(290);
            poscanX = (int) Math.floor(Math.random() * (screenWidth + poscan.getWidth()));
        }
        poscan.setX(poscanX);
        poscan.setY(poscanY);

        posmagY += magSpeed;
        if (posmagY > randyY + 99) {
            posmagY = -(240);
            posmagX = (int) Math.floor(Math.random() * (screenWidth + posmag.getWidth()));
        }
        posmag.setX(posmagX);
        posmag.setY(posmagY);

        posmilkY += milkSpeed;
        if (posmilkY > randyY + 99) {
            posmilkY = -(320);
            posmilkX = (int) Math.floor(Math.random() * (screenWidth + posmilk.getWidth()));
        }
        posmilk.setX(posmilkX);
        posmilk.setY(posmilkY);

    }

    //checking for hit detection to add score and despawn objects caught Graydon/Aaron
    public void hitCheck() {

        //bottle hit check, This causes points to go up and items to respawn
        //catching bottles, once caught retrigger random spawn of bottle image
        int bottleCenterX = posbottleX + posbottle.getWidth() / 2;
        int bottleCenterY = posbottleY + posbottle.getHeight() / 2;
        if (bottleCenterX >= randyX - randySize && bottleCenterX <= randyX + randySize && randyY - 50 <= bottleCenterY ) {
            soundPool.play(posID, 1, 1, 0, 0, 1);
            posbottleY = 1200;
            score += 1;
        }

        //can hitcheck, once triggered reset random can positon
        int canCenterX = poscanX + poscan.getWidth() / 2;
        int canCenterY = poscanY + poscan.getHeight() / 2;
        if (canCenterX >= randyX - randySize && canCenterX <= randyX + randySize && randyY - 50 <= canCenterY){
            soundPool.play(pos2ID, 1, 1, 0, 0, 1);
            poscanY = 1200;
            score += 1;
        }

        //mag hitcheck
        int magCenterX = posmagX + posmag.getWidth() / 2;
        int magCenterY = posmagY + posmag.getHeight() / 2;
        if (magCenterX >= randyX - randySize && magCenterX <= randyX + randySize && randyY - 50 <= magCenterY ) {
            soundPool.play(pos3ID, 1, 1, 0, 0, 1);
            posmagY = 1200;
            score += 1;
        }

        //milk hit check
        int milkCenterX = posmilkX + posmilk.getWidth() / 2;
        int milkCenterY = posmilkY + posmilk.getHeight() / 2;
        if (milkCenterX >= randyX - randySize && milkCenterX <= randyX + randySize && randyY - 50 <= milkCenterY ) {
            soundPool.play(posID, 1, 1, 0, 0, 1);
            posmilkY = 1200;
            score += 1;
        }

        //update score  on hitCheck() Aaron/Nick/Graydon
        collectedValue.setText("" + score);
    }

    //once an item passes Randy's Y value and reaches y=490, decrease health by 1
    //Graydon
    public void missCheck() {
        //checking for bottle miss
        int bottleCenterY = posbottleY + posbottle.getHeight() / 2;
        if (bottleCenterY >= 1010 && bottleCenterY <= 1080){
            health = health - 1;
            posbottleY = 1200;
        }

        //checking for can miss
        int canCenterY = poscanY + poscan.getHeight() / 2;
        if (canCenterY >= 1010 && canCenterY <= 1080){
            health = health - 1;
            poscanY = 1200;
        }

        //checking for magazine miss
        int magCenterY = posmagY + posmag.getHeight() / 2;
        if (magCenterY >= 1010 && magCenterY <= 1080){
            health = health - 1;
            posmagY = 1200;
        }

        //checking for milk miss
        int milkCenterY = posmilkY + posmilk.getHeight() / 2;
        if (milkCenterY >= 1010 && milkCenterY <= 1080){
            health = health - 1;
            posmilkY = 1200;
        }

        //update health value on missCheck() Graydon
        healthValue.setText("" + health);
    }

    //URL=http://www.singhajit.com/android-draggable-view/
    //Move randy on x by dragging finger Nick
    float dX, dY;
    int lastAction;
    public boolean onTouch(View view, MotionEvent event) {
        //tap to start
        if (start_flg == false){
            //game song By: Sam
            if (gameSong == null) {
                gameSong = MediaPlayer.create(GameActivity.this, R.raw.game_act); //song from raw folder
                //gameSong.setLooping(true);
                gameSong.start();
            }
            //Create and start countdown Timer Nick/Aaron
            //TODO: CHANGE BACK TO 60 SECONDS OR WHATEVER, lowered for testing
            new CountDownTimer(10000, 1000) {
                //TextView timerText = (TextView) findViewById(R.id.timerValue);

                public void onTick(long millisUntilFinished){
                    //timerText.setText(String.valueOf(millisUntilFinished / 1000));
                    timerValue.setText(String.valueOf(millisUntilFinished/1000));
                }
                public void onFinish() {
                    //TODO: Create if statement checking high scores when time runs out
                    //if (score >= highscore1){
                    // highscore1 = score;
                    // highscoreName = playerName;
                    // }
                    gameSong.stop();
                    Intent intent = new Intent(GameActivity.this, factScreen.class);
                    startActivity(intent);
                }
            }.start();

            //starts the objects falling Graydon/Aaron/Nick
            start_flg = true;
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("canStart: " + canStart);

                            if(canStart)
                                changePos();
                        }
                    });
                }
            }, 0, 20);
        }

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                dX = view.getX() - event.getRawX();
                //dY = view.getY() - event.getRawY();
                //monitor randy X position for catching objects, while touching screen
                randyX = event.getRawX();
                lastAction = MotionEvent.ACTION_DOWN;
                break;

            case MotionEvent.ACTION_MOVE:
                //view.setY(event.getRawY() + dY);
                //monitor randy X position for catching objects, while moving randy on screen
                randyX = event.getRawX();
                newX = event.getRawX() + dX;
                if(newX>-885 && newX<1015){
                    view.setX(newX);
                }
                double testx = event.getRawX() + dX;
                System.out.println("testX = " + testx);
                lastAction = MotionEvent.ACTION_MOVE;
                break;

            case MotionEvent.ACTION_UP:
                action_flg = false;
                //monitor randy X position for catching objects, while NOT moving randy on screen
                randyX = event.getRawX();
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
