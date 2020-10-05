package com.cjstorrs.pong;

import android.graphics.RectF;

class Ball {
    /*here are the member variables. they are all set to private because direct access
      is not required*/
    private RectF mRect;
    private float mXVelocity;
    private float mYVelocity;
    private float mBallWidth;
    private float mBallHeight;

    Ball(int screenX){ //constructor method for Ball
        mBallWidth = screenX / 100;
        mBallHeight = screenX / 100;

        /*Initialize te rectF with 0,0,0,0. We do itt here because we only want to do it once.
        * we will initialize the detail at the start of each game.*/
        mRect = new RectF();
    }

    //return a feference to mRect to PongGame
    RectF getRect(){
        return mRect;
    }

    void update(long fps){ //update the ball pos. called each frame/loop
        /* move the ball based on teh horizontal (mXVelocity) and vertical (mYVelocity), speed
        * and the current frame rate(fps) */

        /*move te top left corner*/
        mRect.left = mRect.left + (mXVelocity / fps);
        mRect.top = mRect.top + (mYVelocity / fps);

        /*match up the bottom right corner based on the size of the ball*/
        mRect.right = mRect.left + mBallWidth;
        mRect.bottom = mRect.top + mBallHeight;
    }

    void reverseYVelocity(){ //reverse vertical direction of travel
        mYVelocity = -mYVelocity;
    }

    void reverseXVelocity() { //reverse horizontal direction of
        mXVelocity = -mXVelocity;
    }

    void reset(int x, int y){ //parameters to pass in the horizontal and vertical res of screen.
        /*initialise the four points of the rectangle which defines the ball*/
        mRect.left = x/2;
        mRect.top = 0;
        mRect.right = x/2 + mBallWidth;
        mRect.bottom = mBallHeight;

        /*this will be how fast the ball travels. We could vary this, possible as the game
        * progresses to add difficulty */
        mYVelocity = -(y/3);
        mXVelocity = (y/3);
    }

    void increaseVelocity(){
        /*increase speed by 10%*/
        mXVelocity = mXVelocity * 1.1f;
        mYVelocity = mYVelocity * 1.1f;
    }

    //bounce the ball back based on whether it hits the left or right side
    void batBounce(RectF batPosition){
        //detect center of bat
        float batCenter = batPosition.left + (batPosition.width() / 2);

        //detect center of ball
        float ballCenter = mRect.left + (mBallWidth) / 2;

        //Where on the bat did the ball hit
        float relativeIntersect = (batCenter - ballCenter);

        //pick a bounce direction
        if (relativeIntersect < 0){
            mXVelocity = Math.abs(mXVelocity); //go right
        } else { //go left
            mXVelocity = -Math.abs(mXVelocity);
        }

        //having calculated left or right for horizontal direction simply reverse the
        //vertical direction to go back up the screen
        reverseYVelocity();
    }
}
