package cjstorrs.com.scrollingshooter;

import android.content.Context;
import android.content.SharedPreferences;

final class GameState {
    private static volatile  boolean mThreadRunning = false;
    private static volatile  boolean mPaused = true;
    private static volatile  boolean mGameOver = true;
    private static volatile  boolean mDrawing = false;

    //this obj will have acecss to the deSpawnReSpawn method in GameEngine once it is initiallized
    private GameStarter gameStarter;

    private int mScore;
    private int mHighScore;
    private int mNumShips;

    //this will be used for persistant high scores
    private SharedPreferences.Editor mEditor;

    //constructor
    GameState(GameStarter gs, Context context){
       //intit the gameStarter reference
       gameStarter = gs;

       //get current high sore
        SharedPreferences prefs;
        prefs = context.getSharedPreferences("HiScore", Context.MODE_PRIVATE);

        //init the mEditor ready
        mEditor = prefs.edit();

        //load high score from a entry in the file, labeled "hiscore"
        //if none avail , set to 0
        mHighScore = prefs.getInt("hi_score", 0 );
    }

    private void endGame(){
        mGameOver = true;
        mPaused = true;
        if(mScore > mHighScore){
            mHighScore = mScore;
            //save the high score
            mEditor.putInt("hi_score", mHighScore);
            mEditor.commit();
        }
    }

    void startNewGame(){
        mScore = 0;
        mNumShips = 3;
        //dont want to be drawing objects while deSpawnReSpan is clearning them and respawning.
        stopDrawing();
        gameStarter.deSpawnReSpawn();
        resume();

        //now start to draw again
        startDrawing();
    }

    void lostLife(SoundEngine se){
        mNumShips--;
        se.playPlayerExplode();
        if(mNumShips == 0){
            pause();
            endGame();
        }
    }

    int getNumShips(){
        return mNumShips;
    }

    void increaseScore(){
        mScore++;
    }

    int getScore(){
        return mScore;
    }

    int getHighScore(){
        return mHighScore;
    }

    void pause(){
        mPaused = true;
    }

    void resume(){
        mGameOver = false;
        mPaused = false;
    }

    void stopEverything(){
        mPaused = true;
        mGameOver = true;
        mThreadRunning = false;
    }

    boolean getThreadRunning(){
        return mThreadRunning;
    }

    void startThread(){
        mThreadRunning = true;
    }

    private void stopDrawing(){
        mDrawing = false;
    }

    private void startDrawing(){
        mDrawing = true;
    }

    boolean getDrawing(){
        return mDrawing;
    }

    boolean getPaused(){
        return mPaused;
    }

    boolean getGameOver(){
        return mGameOver;
    }




}
