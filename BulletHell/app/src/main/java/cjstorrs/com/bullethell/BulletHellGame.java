package cjstorrs.com.bullethell;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.Random;

class BulletHellGame extends SurfaceView implements Runnable {

    //Are we currently debugging
    boolean mDebugging = true;

    //objects for the game loop/thread
    private Thread mGameThread = null;
    private volatile boolean mPlaying;
    private boolean mPaused = true;

    //Objects for drawing
    private SurfaceHolder mOurHolder;
    private Canvas mCanvas;
    private Paint mPaint;

    //Keep track of the frame rate
    private long mFPS;
    private final int MILLIS_IN_SECOND = 1000;

    //holds res of screen
    private int mScreenX;
    private int mScreenY;

    //how big will tthe text be
    private int mFontSize;
    private int mFontMargin;

    //the are for the sound
    private SoundPool mSP;
    private int mBeepID = -1;
    private int mTeleportID = -1;

    //up to 10000 bullets
    private Bullet[] mBullets = new Bullet[10000];
    private int mNumBullets = 0;
    private int mSpawnRate = 1;

    private Random mRandomX = new Random();
    private Random mRandomY = new Random();

    private Bob mBob;
    private boolean mHit = false;
    private int mNumHits;
    private int mShield = 10;

    //used to time the game
    private long mStartGameTime;
    private long mBestGameTime;
    private long mTotalGameTime;

    //constructor method that gets called from BulletHellActivity
    public BulletHellGame(Context context, int x, int y){
        super(context);

        mScreenX = x;
        mScreenY = y;

        //set font to 5% screen width
        mFontSize = mScreenX / 20;
        //set margin to 2% of screen width
        mFontMargin = mScreenX / 50;

        mOurHolder = getHolder();
        mPaint = new Paint();

        //initialize the sound pool
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
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

        try{
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            descriptor = assetManager.openFd("beep.ogg");
            mBeepID = mSP.load(descriptor, 0);

            descriptor = assetManager.openFd("teleport.ogg");
            mTeleportID = mSP.load(descriptor, 0);
        } catch(IOException e) {
            Log.e("error", "failed to load sound files");
        }

        //initiallize bullets
        for(int i = 0; i<mBullets.length; i++ ){
            mBullets[i] = new Bullet(mScreenX);
        }

        mBob = new Bob(context, mScreenX, mScreenY);
        startGame();
    }

    //start a new game
    public void startGame(){
        mNumHits = 0;
        mNumBullets = 0;
        mHit = false;

        //did the player survive longer than previously
        if(mTotalGameTime > mBestGameTime){
            mBestGameTime = mTotalGameTime;
        }
    }

    //spawns ANOTHER bullet
    private void spawnBullet(){
        mNumBullets++;

        //where to spawn the bullet next and which direction it will travel
        int spawnX;
        int spawnY;
        int velocityX;
        int velocityY;

        //Make sure the bullet does not spawn too close to bob.
        if (mBob.getRect().centerX() < mScreenX / 2) {
            //bob is on left so spawn to right
            spawnX = mRandomX.nextInt(mScreenX / 2) + mScreenX / 2;
            //set bullet heading to the right
            velocityX = 1;
        } else {
            //bob is on the right so spawn on the left
            spawnX = mRandomX.nextInt(mScreenX / 2);
            //set bullet heading to the left
            velocityX = -1;
        }
        //don't spawn to close to bob
        if (mBob.getRect().centerY() < mScreenY / 2) {
            //bob is on top, spawn bullet on bottom
            spawnY = mRandomY.nextInt(mScreenY / 2) + mScreenY / 2;
            velocityY = 1; //heading down
        } else {
            //bob is on the bottom, spawn bullet on top
            spawnY = mRandomY.nextInt(mScreenY / 2);
            velocityY = -1; //heading up
        }
        //spawn the bullet
        mBullets[mNumBullets - 1].spawn(spawnX, spawnY, velocityX, velocityY);
    }

    //handles the game loop
    @Override
    public void run(){
        while (mPlaying) {
            long frameStartTime = System.currentTimeMillis();

            if (!mPaused) {
                update();
                // now all the bullets have been moved, we then can detect collisions
                detectCollisions();
            }

            draw();

            long timeThisFrame = System.currentTimeMillis() - frameStartTime;
            if (timeThisFrame >= 1) {
                mFPS = MILLIS_IN_SECOND / timeThisFrame;
            }
        }
    }

    //update all the game obj's
    private void update(){
        for(int i=0; i<mNumBullets; i++){
            mBullets[i].update(mFPS);
        }
    }

    private void detectCollisions(){
        //check if the bullet has collided with the wall.
        //loop through each active bullet in turn
        for(int i=0; i<mNumBullets; i++){
            if(mBullets[i].getRect().bottom > mScreenY){
                mBullets[i].reverseYVelocity();
            } else if (mBullets[i].getRect().top < 0){
                mBullets[i].reverseYVelocity();
            } else if (mBullets[i].getRect().left < 0){
                mBullets[i].reverseXVelocity();
            } else if (mBullets[i].getRect().right >mScreenX){
                mBullets[i].reverseXVelocity();
            }
        }

        //check if a bullet has hit bob
        //check each bullet for an intersection with bob's RectF
        for (int i=0; i<mNumBullets; i++){
            if (RectF.intersects(mBullets[i].getRect(),mBob.getRect())){
                //bob has been hit
                mSP.play(mBeepID, 1, 1,0, 0, 1);

                //This flags that a hit occurred so that the draw mehtod "knows" as well
                mHit = true;

                //rebound the bullet that collided
                mBullets[i].reverseXVelocity();
                mBullets[i].reverseYVelocity();

                //keep track of the number of hits
                mNumHits++;

                if (mNumHits == mShield){
                    mPaused = true;
                    mTotalGameTime = System.currentTimeMillis() - mStartGameTime;

                    startGame();
                }
            }
        }
    }

    private void draw() {
        if (mOurHolder.getSurface().isValid()) {
            mCanvas = mOurHolder.lockCanvas();
            mCanvas.drawColor(Color.argb(255, 253, 111, 36));
            mPaint.setColor(Color.argb(255,255,255,255));

            //all the drawing code will go here
            for(int i=0; i<mNumBullets; i++){
                mCanvas.drawRect(mBullets[i].getRect(), mPaint);
            }

            mCanvas.drawBitmap(mBob.getBitmap(),mBob.getRect().left, mBob.getRect().top, mPaint);

            mPaint.setTextSize(mFontSize);
            mCanvas.drawText("Bullets: " + mNumBullets
                    + " Shield: " + (mShield - mNumHits)
                    + " Best Time: " + mBestGameTime / MILLIS_IN_SECOND
                    , mFontMargin, mFontSize, mPaint);

            //Don't draw the current time when paused
            if(!mPaused){
                mCanvas.drawText("Seconds Survived: " + ((System.currentTimeMillis()
                        - mStartGameTime) / MILLIS_IN_SECOND), mFontMargin, mFontMargin * 30, mPaint);
            }
            if(mDebugging) {
                printDebuggingText();
            }

            mOurHolder.unlockCanvasAndPost(mCanvas);
        }
    }

    @Override
    public boolean onTouchEvent (MotionEvent motionEvent){

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_DOWN:
                if (mPaused){
                    mStartGameTime = System.currentTimeMillis();
                    mPaused = false;
                }

                if(mBob.teleport(motionEvent.getX(), motionEvent.getY())){
                    mSP.play(mTeleportID, 1, 1, 0, 0, 1);
                }
                break;
            case MotionEvent.ACTION_UP:
                mBob.setTeleportAvailable();
                spawnBullet();
                break;
        }
        return true;
    }

    public void pause() {
        mPlaying = false;
        try {
            mGameThread.join();
        } catch (InterruptedException e){
            Log.e("Error:", "joining thread");
        }
    }

    public void resume() {
        mPlaying = true;
        mGameThread = new Thread(this);
        mGameThread.start();
    }

    private void printDebuggingText() {
        int debugSize = 35;
        int debugStart = 150;
        mPaint.setTextSize(debugSize);

        mCanvas.drawText("FPS: " + mFPS, 10, debugStart + debugSize, mPaint);
        mCanvas.drawText("Bob left: " + mBob.getRect().left, 10, debugStart + debugSize * 2, mPaint);
        mCanvas.drawText("Bob top: " + mBob.getRect().top, 10, debugStart + debugSize * 3, mPaint);
        mCanvas.drawText("Bob right: " + mBob.getRect().right, 10, debugStart + debugSize * 4, mPaint);
        mCanvas.drawText("Bob bottom: " + mBob.getRect().bottom, 10, debugStart + debugSize * 5, mPaint);
        mCanvas.drawText("Bob centerX: " + mBob.getRect().centerX(), 10, debugStart + debugSize * 6, mPaint);
        mCanvas.drawText("Bob centerY: " + mBob.getRect().centerY(), 10, debugStart + debugSize * 7, mPaint);

    }
}
//353