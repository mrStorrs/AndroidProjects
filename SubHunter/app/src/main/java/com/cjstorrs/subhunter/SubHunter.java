package com.cjstorrs.subhunter;

import android.app.Activity;
import android.media.Image;
import android.os.Bundle;
import android.view.MotionEvent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.Display;
import android.util.Log;
import android.widget.ImageView;
import java.util.Random;

public class SubHunter extends Activity {
    //these variables can be seen through the SubHunter Class
    int numberHorizontalPixels;
    int numberVerticalPixels;
    int blockSize;
    int gridWidth = 40;
    int gridHeight;
    float horizontalTouched = -100;
    float verticalTouched = -100;
    int subHorizontalPosition;
    int subVerticalPosition;
    boolean hit = false;
    int shotsTaken;
    int distanceFromSub;
    boolean debugging = false;

    // here are all the objects(instances) of classes that we need to do some drawing.
    ImageView gameView;
    Bitmap blankBitmap;
    Canvas canvas;
    Paint paint;

    // code placed here will run before the player sees the app.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); //must always be the first line

        // get the current device's screen res
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        //initialize our size based variables based on the screen res
        numberHorizontalPixels = size.x;
        numberVerticalPixels = size.y;
        blockSize = numberHorizontalPixels / gridWidth;
        gridHeight = numberVerticalPixels / blockSize;

        //initialize all the objects ready for drawing
        blankBitmap = Bitmap.createBitmap(numberHorizontalPixels, numberVerticalPixels, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(blankBitmap);
        gameView = new ImageView(this);
        paint = new Paint();

        //tell android to set our drawing as the view for this app
        setContentView(gameView);

        Log.d("Debugging", "In onCreate");
        newGame();
        draw();
    }

    /* this code will execute when a new game needs to be started it ill happen when the app is first
    started and after the player wins the game */
    void newGame() {
        Random random = new Random(); //initialize new random object
        subHorizontalPosition = random.nextInt(gridWidth);
        subVerticalPosition = random.nextInt(gridHeight);
        shotsTaken = 0;

        Log.d("Debugging", "In newGame");
    }
    /* Here all the drawing is done. The grid lines, the HUD and the touch indicator. */
    void draw() {
        gameView.setImageBitmap(blankBitmap);

        // wipe the screen with a white color
        canvas.drawColor(Color.argb(255, 255, 255, 255));

        // change the paint color to black
        paint.setColor(Color.argb(255,0,0,0));
        // draw vertical lines of the grid
        for(int i = 0; i < gridWidth; i++){
            canvas.drawLine(blockSize * i, 0, blockSize * i, numberVerticalPixels, paint);
        }
        //draw horizontal lines of the grid
        for(int i = 0; i < gridHeight; i++){
            canvas.drawLine(0, blockSize * i, numberHorizontalPixels, blockSize * i, paint);
        }

        //Draw the player's shot
        canvas.drawRect(horizontalTouched * blockSize, verticalTouched * blockSize,
                (horizontalTouched * blockSize) + blockSize,
                (verticalTouched * blockSize) + blockSize, paint);

        // Re-size the text for the score and distance
        paint.setTextSize(blockSize * 2);
        paint.setColor(Color.argb(255, 0, 0, 255));
        canvas.drawText("ShotsTaken: " + shotsTaken
                + " Distance: " + distanceFromSub,
                blockSize, blockSize * 1.75f, paint);

        Log.d("Debugging", "In draw");

        if(debugging){
            printDebuggingText();
        }
    }

    /* This part of the code will handle detecting that the player has tapped the screen */
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        // checking if player has removed their firnger from the screen
        if (( motionEvent.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP){
            //process te player's shot by passing the coordinates of the player's finger to takeshot
            takeShot(motionEvent.getX(), motionEvent.getY());
        }

        Log.d("Debugging", "In onTouchEvent");
        return true;
    }

    /* This code executes when the player taps the screen. It will calculate the distance form the
    sub and decide a hit or miss */
    void takeShot(float touchX, float touchY) {
        shotsTaken++;

        //convert the flaot screen coordinates into int grid coord
        horizontalTouched = (int)touchX / blockSize;
        verticalTouched = (int)touchY / blockSize;

        //checking if shot hit sub
        hit = horizontalTouched == subHorizontalPosition && verticalTouched == subVerticalPosition;

        //how far away horizontally and vertically was the shot from the sub
        int horizontalGap = (int) horizontalTouched - subHorizontalPosition;
        int verticalGap= (int)verticalTouched- subVerticalPosition;

        //use Pythagora's theorem to get the distance travelled in a straight line
        distanceFromSub = (int)Math.sqrt((horizontalGap * horizontalGap) + (verticalGap * verticalGap));

        //if there is a hit call boom
        if(hit){
            boom();
        } else {
            draw();
        }

        Log.d("Debugging", "In takeShot");
    }

    /* "BOOM!" */
    void boom() {
        gameView.setImageBitmap(blankBitmap);

        //Wipe the screen with a red color
        canvas.drawColor(Color.argb(255, 255, 0, 0));

        //Draw some huge white text
        paint.setColor(Color.argb(255,0,0,0));
        paint.setTextSize(blockSize * 10);

        canvas.drawText("Boom!", blockSize * 4, blockSize * 14, paint);

        //Draw some text to prompt restarting
        paint.setTextSize(blockSize * 2);
        canvas.drawText("Take a shot to start again", blockSize * 8, blockSize * 18, paint);

        //start a new game
        newGame();
    }
    /* This code prints the debugging text */
    void printDebuggingText(){
        paint.setTextSize(blockSize);
        canvas.drawText("numbeerHorizontalPixels = " + numberHorizontalPixels,
                50, blockSize * 3, paint);
        canvas.drawText("numberVerticalPixels = " + numberVerticalPixels,
                50, blockSize * 4, paint);
        canvas.drawText("blockSize = " + blockSize,
                50, blockSize * 5, paint);
        canvas.drawText("gridWidth = " + gridWidth,
                50, blockSize * 6, paint);
        canvas.drawText("gridHeight = " + gridHeight,
                50, blockSize * 7, paint);
        canvas.drawText("horizontalTouched = " + horizontalTouched,
                50, blockSize * 8, paint);
        canvas.drawText("verticalTouched = " + verticalTouched,
                50, blockSize * 9, paint);
        canvas.drawText("subHorizontalPoistion = " + subHorizontalPosition,
                50, blockSize * 10, paint);
        canvas.drawText("subVerticalPosition = " + subVerticalPosition,
                50, blockSize * 11, paint);
        canvas.drawText("hit = " + hit,
                50, blockSize * 12, paint);
        canvas.drawText("shotsTaken= " + shotsTaken,
                50, blockSize * 13, paint);
        canvas.drawText("debugging = " + debugging,
                50, blockSize * 14, paint);
    }


}



