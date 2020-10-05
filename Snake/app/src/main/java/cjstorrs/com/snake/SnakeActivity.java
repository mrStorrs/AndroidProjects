package cjstorrs.com.snake;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;

public class SnakeActivity extends Activity {
    //declare an instance of SnakeGame
    SnakeGame mSnakeGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get the pixel dimmensions of the screen
        Display display = getWindowManager().getDefaultDisplay();

        //inititallize the result into a Point object
        Point size = new Point();
        display.getSize(size);

        //creat a new instance of the SnakeEngine class
        mSnakeGame = new SnakeGame(this, size);

        //make snakeEngine the view of the activity
        setContentView(mSnakeGame);
    }

    //Start the thread in snakeEngine
    @Override
    protected void onResume(){
        super.onResume();;
        mSnakeGame.resume();
    }

    //stope the thread in snakeEngine
    @Override
    protected void onPause(){
        super.onPause();
        mSnakeGame.pause();
    }
}