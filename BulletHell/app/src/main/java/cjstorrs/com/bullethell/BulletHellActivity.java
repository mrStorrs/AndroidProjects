package cjstorrs.com.bullethell;

import androidx.annotation.NonNull;

import android.app.Activity;
import android.graphics.Point;

import android.os.Bundle;
import android.view.Display;

public class BulletHellActivity extends Activity {

    //an instance of the main class of this project
    private BulletHellGame mBHGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get screen res
        Display display = getWindowManager().getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);

        //call the constructor(initialize) the bulletHellGame instnace

        mBHGame = new BulletHellGame(this, size.x, size.y);
        setContentView(mBHGame);
    }

    //start the mian game thread when game is launched
    @Override
    protected void onResume() {
        super.onResume();

        mBHGame.resume();
    }

    //stop the thread when the player quits
    @Override
    protected void onPause() {
        super.onPause();

        mBHGame.pause();
    }
}