package com.cjstorrs.pong;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Bundle;
import android.view.Display;

public class PongActivity extends Activity {

    // putting m before the instance name is a naming convention for member variables.
    private PongGame mPongGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        //create new instance/object of class PongGame
        mPongGame = new PongGame(this, size.x, size.y);
        setContentView(mPongGame);
    }

    @Override
    protected void onResume(){
        super.onResume();

        mPongGame.resume();
    }

    @Override
    protected void onPause(){
        super.onPause();

        mPongGame.pause();
    }
}
