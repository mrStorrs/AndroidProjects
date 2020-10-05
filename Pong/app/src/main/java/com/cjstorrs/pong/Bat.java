package com.cjstorrs.pong;

import android.graphics.RectF;

class Bat {
    private RectF mRect;
    private float mLength;
    private float mXCoord;
    private float mBatSpeed;
    private int mScreenX;

    //these values are final and thus cannot be changed. they can be directly accessed by the
    //instance (in PongGame)
    final int STOPPED = 0;
    final int LEFT = 1;
    final int RIGHT = 2;

    //keeps track of if and how the ball is moving
    private int mBatMoving = STOPPED;

    Bat(int sx, int sy){ //constructor
        //bat needs to know the screen horiz res. outside of this method
        mScreenX = sx;

        //configure the size of the bat based on the screen res.
        mLength = mScreenX / 8; //1/8th of screen width
        float height = sy / 40; //height is 1/40th of screen height

        //configure the starting location of the bat. roughly in the middle in horizontal
        mXCoord = mScreenX / 2;
        float mYCoord = sy - height; //height of the bat off te bottom of the screen

        //initialize mRect based on te size and position
        mRect = new RectF(mXCoord, mYCoord, mXCoord + mLength, mYCoord + height);

        //configure speed of bat. this code means the bat can cover the width of the screen in 1 sec
        mBatSpeed = mScreenX;
    }
    //Return a reference to the mRect object
    RectF getRect(){
        return mRect;
    }
    //update the movement state passed in by the onTouchEvent method
    void setMovementState(int state){
        mBatMoving = state;
    }
    //update the bat = called each frame/loop
    void update(long fps){
        //move the bat based on the mBatMoving variable and the speed of the prev frame
        if(mBatMoving == LEFT){
            mXCoord = mXCoord - mBatSpeed / fps;
        }
        if(mBatMoving == RIGHT){
            mXCoord = mXCoord + mBatSpeed / fps;
        }
        //Stop te bat going off the screen
        if (mXCoord < 0) {
            mXCoord = 0;
        } else if (mXCoord + mLength > mScreenX){
            mXCoord = mScreenX - mLength;
        }
        //update mRect based on the results from the previous code in update
        mRect.left = mXCoord;
        mRect.right = mXCoord + mLength;
    }
}
