package cjstorrs.com.scrollingshooter;

import android.graphics.PointF;

class PlayerMovementComponent implements  MovementComponent {

    @Override
    public boolean move(long fps, Transform t, Transform playerTransform) {
        //how high is the screen?
        float screenHeight = t.getmScreenSize().y;
        //where is the player?
        PointF location = t.getLocation();
        //how fast is it going
        float speed = t.getSpeed();
        //how tall is the ship
        float height = t.getObjectHeight();
        //move the ship up or down if needed
        if(t.headingDown()){
            location.y += speed / fps;
        } else if(t.headingUp()){
            location.y -= speed / fps;
        }

        //update the collider
        t.updateCollider();
        return true;
    }
}
