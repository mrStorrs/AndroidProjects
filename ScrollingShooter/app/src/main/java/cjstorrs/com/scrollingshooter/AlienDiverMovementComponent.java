package cjstorrs.com.scrollingshooter;

import android.graphics.PointF;

import java.util.Random;

class AlienDiverMovementComponent implements MovementComponent{
    @Override
    public boolean move(long fps, Transform t, Transform playerTransform){
        //where is the shiop?
        PointF location = t.getLocation();
        //how fast is the ship
        float speed = t.getSpeed();
        //relative speed diff with the player
        float slowDownRelativeToPlayer = 1.8f;

        //compensate for movement relative to player, but only when in view
        if(!playerTransform.getFacingRight()){
            location.x += speed * slowDownRelativeToPlayer / fps;
        } else {
            location.x -= speed * slowDownRelativeToPlayer / fps;
        }

        //fall down then respawn @ top
        location.y += speed / fps;

        if(location.y > t.getmScreenSize().y){
            //respawn @ top
            Random random = new Random();
            location.y = random.nextInt(300) - t.getObjectHeight();
            location.x = random.nextInt((int) t.getmScreenSize().x);
        }

        //update the collider
        t.updateCollider();
        return true;
    }
}
