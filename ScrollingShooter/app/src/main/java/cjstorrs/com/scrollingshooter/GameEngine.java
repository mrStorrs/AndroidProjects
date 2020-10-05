package cjstorrs.com.scrollingshooter;

import android.graphics.Point;
import android.graphics.PointF;
import android.provider.Contacts;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.content.Context;

import java.lang.reflect.Array;
import java.util.ArrayList;

class GameEngine extends SurfaceView implements Runnable, GameStarter, GameEngineBroadcaster,
                        ,PlayerLaserSpawner{
    private Thread mThread = null;
    private long mFPS;

    private ArrayList<InputObserver> inputObservers = new ArrayList();

    UIController mUIController;

    private GameState mGameState;
    private SoundEngine mSoundEngine;
    HUD mHUD;
    Renderer mRenderer;
    ParticleSystem mParticleSystem;
    PhysicsEngine mPhysicsEngine;

    //constructor
    public GameEngine(Context context, Point size){
        super(context);

        mUIController = new UIController(this);
        mGameState = new GameState(this, context);
        mSoundEngine = new SoundEngine(context);
        mHUD = new HUD(size);
        mRenderer = new Renderer(this);
        mParticleSystem = new ParticleSystem();
        mPhysicsEngine = new PhysicsEngine();

        //choose the amount of particles
        mParticleSystem.init(1000);
    }

    //for te game engine broadcaster interface
    public void addObserver(InputObserver o ){
        inputObservers.add(o);
    }

    @Override
    public void run() {

        while (mGameState.getThreadRunning()) {
            long frameStartTime = System.currentTimeMillis();

            if (!mGameState.getPaused()) {
                //update all te game objs here.

                //this call to update will evolve with proj
                if(mPhysicsEngine.update(mFPS, mParticleSystem)){
                    //player hit
                    deSpawnReSpawn();
                }
            }

            //draw all the game objs here
            mRenderer.draw(mGameState, mHUD, mParticleSystem);

            //measure the FPS
            long timeThisFrame = System.currentTimeMillis() - frameStartTime;
            if (timeThisFrame >= 1) {
                final int MILLIS_IN_SECOND = 1000;
                mFPS = MILLIS_IN_SECOND / timeThisFrame;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent){
        //handle the players input here
        for (InputObserver o : inputObservers){
            o.handleInput(motionEvent, mGameState, mHUD.getControls());
        }

        mSoundEngine.playShoot();
        return true;
    }

    public void stopThread(){
        //new code here soon
        mGameState.stopEverything();
        try{
            mThread.join();
        } catch (InterruptedException e){
            Log.e("Exception", "stopThread()" + e.getMessage());
        }
    }

    public void startThread(){
        //new code here soon
        mGameState.startThread();

        mThread = new Thread(this);
        mThread.start();
    }

    public void deSpawnReSpawn(){
        //eventually this will despawn and respawn all game obj
    }

    @Override
    public boolean spawnPlayerLaser(Transform transform) {
        return false;
    }
}
