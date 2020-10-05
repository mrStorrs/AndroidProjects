package cjstorrs.com.snake;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;

import java.util.ArrayList;

import androidx.constraintlayout.widget.ConstraintSet;

class Snake {
    //the location in the grid of all the segments
    private ArrayList<Point> segmentLocations;

    //how big is each segment of the snake
    private int mSegmentSize;

    //how big is the entire gird
    private Point mMoveRange;

    //where is the center of the screen horizontally in pixels
    private int halfWayPoint;

    //for the tracking movement Heading
    private enum Heading {
        UP, RIGHT, DOWN, LEFT
    }

    //start by heading to the right
    private Heading heading = Heading.RIGHT;

    //a bitmap for each direction the head can face
    private Bitmap mBitmapHeadRight;
    private Bitmap mBitmapHeadLeft;
    private Bitmap mBitmapHeadUp;
    private Bitmap mBitmapHeadDown;

    //a bitmap for the body
    private Bitmap mBitmapBody;

    //Constructor
    Snake(Context context, Point mr, int ss){
        //initialize our arraylist
        segmentLocations = new ArrayList<>();

        //initialize the segment size and movement range from the passed in parameters
        mSegmentSize = ss;
        mMoveRange = mr;

        //create and scale the bitmaps
        mBitmapHeadRight = BitmapFactory.decodeResource(context.getResources(), R.drawable.head);
        //create 3 more versions of the head for differant headings
        mBitmapHeadLeft = BitmapFactory.decodeResource(context.getResources(), R.drawable.head);
        mBitmapHeadDown = BitmapFactory.decodeResource(context.getResources(), R.drawable.head);
        mBitmapHeadUp = BitmapFactory.decodeResource(context.getResources(), R.drawable.head);

        //modify the bitmaps to face the snake head in the correct direction
        mBitmapHeadRight = Bitmap.createScaledBitmap(mBitmapHeadRight, ss, ss, false);

        //a matrix for scaling
        Matrix matrix = new Matrix();
        matrix.preScale(-1,1);
        mBitmapHeadLeft = Bitmap.createBitmap(mBitmapHeadRight, 0, 0, ss, ss, matrix, true);

        //matrix for rotating
        matrix.preRotate(-90);
        mBitmapHeadUp = Bitmap.createBitmap(mBitmapHeadRight, 0 ,0, ss, ss, matrix, true);

        //matrix operations are cumulative so rotate by 180 to face down
        matrix.preRotate(180);
        mBitmapHeadDown = Bitmap.createBitmap(mBitmapHeadRight, 0, 0, ss, ss, matrix, true);

        //create and scale the body
        mBitmapBody = BitmapFactory.decodeResource(context.getResources(), R.drawable.body);
        mBitmapBody = Bitmap.createScaledBitmap(mBitmapBody, ss, ss, false);

        //the halfway point across the screen in pixels | used to detect which side of the screen was pressed
        halfWayPoint = mr.x * ss / 2;
    }

    //get the snake ready for a new game
    void reset (int w, int h){
        //reset the heading
        heading = Heading.RIGHT;

        //delete the old contents of the ArrayList
        segmentLocations.clear();

        //start with a single snake segment
        segmentLocations.add(new Point(w / 2, h / 2));
    }

    void move(){
        //move the body. start at the back and move it to the position of the segment in front of it
        for (int i = segmentLocations.size() -1; i > 0; i--){
            //make it the same value as the next segment going forward towards the head.
            segmentLocations.get(i).x = segmentLocations.get(i-1).x;
            segmentLocations.get(i).y = segmentLocations.get(i-1).y;
        }

        //move the head in the appropirate heading, get the existing head position
        Point p = segmentLocations.get(0);

        //move it appropriatly
        switch(heading){
            case UP:
                p.y--;
                break;
            case RIGHT:
                p.x++;
                break;
            case DOWN:
                p.y++;
                break;
            case LEFT:
                p.x--;
                break;
        }

        //insert the adjusted point back into position 0
        segmentLocations.set(0,p);
    }

    boolean detectDeath(){
        //has the snake died?
        boolean dead = false;

        //hit any screen edges?
        if(segmentLocations.get(0).x == -1
                || segmentLocations.get(0).x > mMoveRange.x
                || segmentLocations.get(0).y == -1
                || segmentLocations.get(0).y > mMoveRange.y){
            dead = true;
        }

        //eaten itself?
        for(int i = segmentLocations.size() - 1; i>0; i--){
            //have any of the sections collided with the head
            if (segmentLocations.get(0).x == segmentLocations.get(i).x
                    && segmentLocations.get(0).y == segmentLocations.get(i).y){
                dead = true;
            }
        }
        return dead;
    }

    boolean checkDinner(Point l){
        if (segmentLocations.get(0).x == l.x && segmentLocations.get(0).y == l.y){
            //add a new point to the list located off-screen
            //this is ok because on the enxt call to move it will take the position of th segment
            //infront of it
            segmentLocations.add(new Point(-10, -10));
            return true;
        }
        return false;
    }

    void draw(Canvas canvas, Paint paint){
        //don't run this code if ArrayList has nothing in it
        if (!segmentLocations.isEmpty()) {
            //Draw the head
            switch(heading){
                case RIGHT:
                    canvas.drawBitmap(mBitmapHeadRight, segmentLocations.get(0).x * mSegmentSize
                            , segmentLocations.get(0).y * mSegmentSize, paint);
                    break;
                case LEFT:
                    canvas.drawBitmap(mBitmapHeadLeft, segmentLocations.get(0).x * mSegmentSize,
                            segmentLocations.get(0).y * mSegmentSize, paint);
                    break;
                case UP:
                    canvas.drawBitmap(mBitmapHeadUp, segmentLocations.get(0).x * mSegmentSize,
                            segmentLocations.get(0).y * mSegmentSize, paint);
                    break;
                case DOWN:
                    canvas.drawBitmap(mBitmapHeadDown, segmentLocations.get(0).x * mSegmentSize,
                            segmentLocations.get(0).y * mSegmentSize, paint);
                    break;
            }

            //draw the snake body one block at a time
            for (int i = 1; i< segmentLocations.size(); i++){
                canvas.drawBitmap(mBitmapBody, segmentLocations.get(i).x * mSegmentSize,
                            segmentLocations.get(i).y * mSegmentSize, paint);
            }
        }
    }

    //handle changing direction
    void switchHeading(MotionEvent motionEvent){
        //is the tap on the right hand side?
        if (motionEvent.getX() >= halfWayPoint){
            switch (heading){
                //rotate right in direction
                case UP:
                    heading = Heading.RIGHT;
                    break;
                case RIGHT:
                    heading = Heading.DOWN;
                    break;
                case DOWN:
                    heading = Heading.LEFT;
                    break;
                case LEFT:
                    heading = Heading.UP;
                    break;
            }
        } else {
            //rotate left
            switch (heading){
                //rotate right in direction
                case UP:
                    heading = Heading.LEFT;
                    break;
                case RIGHT:
                    heading = Heading.UP;
                    break;
                case DOWN:
                    heading = Heading.RIGHT;
                    break;
                case LEFT:
                    heading = Heading.DOWN;
                    break;
            }
        }

    }
}
