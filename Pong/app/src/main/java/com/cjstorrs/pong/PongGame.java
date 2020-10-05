package com.cjstorrs.pong;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.AudioManager;

import java.io.IOException;


class PongGame extends SurfaceView implements Runnable {
    //debugging check. declared final because we never want to change during game.
    private final boolean DEBUGGING = true;

    //objects that are need to do the drawing
    private SurfaceHolder mOurHolder;
    private Canvas mCanvas;
    private Paint mPaint;

    //How many frames per second did we get
    private long mFPS;
    //# of milliseconds in a second
    private final int MILLIS_IN_SECOND = 1000;

    //Holds res of screen
    private int mScreenX;
    private int mScreenY;

    //size of text
    private int mFontSize;
    private int mFontMargin;

    //game objects
    private Bat mBat;
    private Ball mBall;

    //current score and lives remaining
    private int mScore;
    private int mLives;

    //here is the thread and control variables
    private Thread mGameThread = null;
    //this volatile variable can be accessed from inside and outside the thread
    private volatile boolean mPlaying;
    private boolean mPaused = true;

    //these are for playing sounds
    private SoundPool mSP;
    private int mBeepID = -1;
    private int mBoopID = -1;
    private int mBopID = -1;
    private int mMissID = -1;


    // Contstructor, for when calling mPongGame = new PongGame (from pong activity) this line
    // is creating a new instance of the PongGame class.
    public PongGame(Context context, int x, int y){
        //This calls the parent class constructor of SurfaceView provided by android
        super(context);

        //initializing screen size values.
        mScreenX = x;
        mScreenY = y;

        mFontSize = mScreenX / 20;  //set font to 1/20th of the screen width
        mFontMargin = mScreenX / 75;    //margin to 1/75th of screeen width

        //initialize objects ready for drawing with getHolder. Getholder is a method of SurfaceView
        mOurHolder = getHolder();
        mPaint = new Paint();

        //initialize the bat and ball
        mBall = new Ball(mScreenX);
        mBat = new Bat(mScreenX, mScreenY);

        //prepare the SoundPool instance
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            AudioAttributes audioAttributes =
                    new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build();
            mSP = new SoundPool.Builder()
                    .setMaxStreams(5)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            mSP = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        }

        //open each of the sound files in turn and leoad them into RAM ready to play
        //the Try-Catch blocks handle when this fails and is required.
        try{
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            descriptor = assetManager.openFd("beep.ogg");
            mBeepID = mSP.load(descriptor, 0);

            descriptor = assetManager.openFd("boop.ogg");
            mBoopID = mSP.load(descriptor, 0);

            descriptor = assetManager.openFd("bop.ogg");
            mBopID = mSP.load(descriptor, 0);

            descriptor = assetManager.openFd("miss.ogg");
            mMissID = mSP.load(descriptor, 0);

        } catch(IOException e){
            Log.d("error", "failed to load sound files");
        }

        //start game
        startNewGame();
    }

    //Handle all the screen touches
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK){
            //player has put their finger on the screen
            case MotionEvent.ACTION_DOWN:
                //if game is paused, unpause
                mPaused = false;
                //where did the touch happen
                if(motionEvent.getX() > mScreenX / 2){
                    //on right side
                    mBat.setMovementState(mBat.RIGHT);
                } else {
                    //on left side
                    mBat.setMovementState(mBat.LEFT);
                }
                break;
            //Player liffed thieer finger from anywhere on screen. currently if player uses multiple
            //fingers there will be bugs so this code will just be for single finger.
            case MotionEvent.ACTION_UP:
                //stop bat from moving
                mBat.setMovementState(mBat.STOPPED);
                break;
        }
        return true;
    }

    //player has lost or is starting a new game
    private void startNewGame(){
        //Put the ball back to the starting position
        mBall.reset(mScreenX, mScreenY);
        //reset the score and the player's chances
        mScore = 0;
        mLives = 3;
    }

    @Override
    public void run(){
        /*mPlaying gives us finer control rather than just relying on the calls to run. mPlaying
        must be true AND the thread running for the main loop to execute */
        while (mPlaying){
            long frameStartTime = System.currentTimeMillis(); //check time @ start of loop

            if(!mPaused){ //if game isn't paused call update method
                update();
                /* now that the ball and bat are in their new positions we can see if there have
                been any collisions */
                detectCollisions();
            }
            /* the movement has been handled and collisions detected now we can draw the scene*/
            draw();
            long timeThisFrame = System.currentTimeMillis() - frameStartTime; //how long did frame take

            if(timeThisFrame > 0){ //dividing by 0 will crash game. this ensures that doesn't happen
                /*Store the current frame rate in mFPS ready to pass to the update mehtods of mBat
                and mBall next frame loop*/
                mFPS = MILLIS_IN_SECOND / timeThisFrame;
            }
        }
    }

    private void update(){
        //update the bat and ball
        mBall.update(mFPS);
        mBat.update(mFPS);
    }

    private void detectCollisions(){
        // Has the bat hit the ball?
        if(RectF.intersects(mBat.getRect(), mBall.getRect())) {
            //realistic-ish bounce
            mBall.batBounce(mBat.getRect());
            mBall.increaseVelocity();
            mScore++;
            mSP.play(mBeepID, 1, 1, 0 , 0, 1);
        }
        //has the ball hit the edge of the screen

        //bottom
        if(mBall.getRect().bottom < 0){
            mBall.reverseYVelocity();

            mLives--;
            mSP.play(mMissID, 1 , 1, 0,0 , 1);

            if(mLives == 0){
                mPaused = true;
                startNewGame();
            }
        }
        //top
        if(mBall.getRect().top < 0){
            mBall.reverseYVelocity();
            mSP.play(mBoopID, 1, 1, 0, 0, 1);
        }
        //left
        if(mBall.getRect().left < 0){
            mBall.reverseXVelocity();
            mSP.play(mBopID, 1, 1, 0, 0, 1);
        }
        //right
        if(mBall.getRect().right > mScreenX){
            mBall.reverseXVelocity();
            mSP.play(mBopID, 1, 1, 0, 0, 1);
        }
    }

    //called when player quits game
    public void pause(){
        mPlaying = false;
        try {
            mGameThread.join(); //stopping thread is not always instant.
        } catch (InterruptedException e){
            Log.e("Error:", "joining thread");
        }
    }

    //called when player starts a game
    public void resume(){
        mPlaying = true;
        mGameThread = new Thread(this); //initiallize instance of thread

        mGameThread.start(); //start the thread.
    }

    //draw the game objects and the HUD
    private void draw(){
//        void draw() {

            if (mOurHolder.getSurface().isValid()) {
                mCanvas = mOurHolder.lockCanvas();//lock the canvas (graphics memory ready to draw
                mCanvas.drawColor(Color.argb(255, 26, 128, 182)); //fill screen with solid color
                mPaint.setColor(Color.argb(255, 255, 255, 255)); //choose paint color

                //draw bat and ball
                mCanvas.drawRect(mBall.getRect(), mPaint);
                mCanvas.drawRect(mBat.getRect(), mPaint);

                mPaint.setTextSize(mFontSize); //set fontsize
                //draw HUD
                mCanvas.drawText("Score: " + mScore + "  Lives: " + mLives, mFontMargin, mFontSize, mPaint);

                if(DEBUGGING){
                    printDebuggingText();
                }

                //Display the drawing on the screen. unlock is a mehtod of surfaceview
                mOurHolder.unlockCanvasAndPost(mCanvas);
            }
//        }
    }

    private void printDebuggingText(){
        int debugSize = mFontSize / 2;
        int debugStart = 150;
        mPaint.setTextSize(debugSize);
        mCanvas.drawText("FPS: " + mFPS, 10, debugStart + debugSize, mPaint);
    }

}
