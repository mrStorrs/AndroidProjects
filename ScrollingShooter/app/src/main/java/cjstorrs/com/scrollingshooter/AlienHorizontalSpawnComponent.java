package cjstorrs.com.scrollingshooter;

import android.graphics.PointF;

import java.util.Random;

class AlienHorizontalSpawnComponent implements SpawnComponent{

    @Override
    public void spawn(Transform playerLTransform, Transform t){
        //get the screen size
        PointF ss = t.getmScreenSize();

        //spawn just off screen randomly left or right
        Random random = new Random();
        boolean left = random.nextBoolean();
        //how far away?
        float distance = random.nextInt(2000) + t.getmScreenSize().x;

        //generate a height to spawn at wher ethe entire ship is vertically on-screen
        float spawnHeight = random.nextFloat() * ss.y - t.getSize().y;

        //spawn the ship
        if(left){
            t.setLocation(-distance, spawnHeight);
            t.headRight();
        } else {
            t.setLocation(distance, spawnHeight);
            t.headLeft();
        }

    }
}
