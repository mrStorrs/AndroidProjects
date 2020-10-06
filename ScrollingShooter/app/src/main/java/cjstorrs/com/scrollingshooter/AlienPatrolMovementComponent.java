package cjstorrs.com.scrollingshooter;

import android.graphics.PointF;

import java.util.Random;

class AlienPatrolMovementComponent implements MovementComponent {

    private AlienLaserSpawner alienLaserSpawner;
    private Random mShotRandom = new Random();

    AlienPatrolMovementComponent(AlienLaserSpawner als){
        alienLaserSpawner = als;
    }

    @Override
    public boolean move(long fps, Transform t, Transform playerTransform){
        final int TAKE_SHOT = 0;
        //1 in 100 chance of shot being fired when in line with the player
        final int SHOT_CHANCE = 100;
        //where is the player?
        PointF playerLocation = playerTransform.getLocation();

        //top of the screen
        final float MIN_VERTICAL_BOUNDS = 0;
        //width and height of the screen
        float screenX = t.getmScreenSize().x;
        float screenY = t.getmScreenSize().y;

        //how far ahead can the alien see?
        float mSeeingDistance = screenX * .5f;

        //where is the alien?
        PointF loc = t.getLocation();
        //how fast is the alien
        float speed = t.getSpeed();
        //how tall is the alien
        float height = t.getObjectHeight();

        //stop alien from going too far away
        float MAX_VERTICAL_BOUNDS = screenY - height;
        final float MAX_HORIZONTAL_BOUNDS = 2 * screenX;
        final float MIN_HORIZONTAL_BOUNDS = 2 * -screenX;

        //adjust the horizontal speed relative to the players heading
        float horizontalSpeedAdjustmentRelativeToPlayer = 0;
        //how much speed up or slow down relative to player's heading
        float horizontalSpeedAdjustmentModifer = .8f;

        //can the alien "see" the player? if so make the speed relative
        if (Math.abs(loc.x - playerLocation.x) < mSeeingDistance){
            if (playerTransform.getFacingRight() != t.getFacingRight()){
                //facing a diff way, speed up alien
                horizontalSpeedAdjustmentRelativeToPlayer = speed * horizontalSpeedAdjustmentModifer;
            } else {
                //facing the same way, slow it down
                horizontalSpeedAdjustmentRelativeToPlayer = -(speed * horizontalSpeedAdjustmentModifer);
            }
        }

        //move horizontally taking into account the speed modification
        if(t.headingLeft()){
            loc.x -= (speed + horizontalSpeedAdjustmentRelativeToPlayer) / fps;

            //turn the ship around when it reaches the exten of its horiztonal patrol area
            if (loc.x < MIN_HORIZONTAL_BOUNDS){
                loc.x = MIN_HORIZONTAL_BOUNDS;
                t.headRight();
            }
        } else {
            loc.x += (speed + horizontalSpeedAdjustmentRelativeToPlayer) / fps;

            //turn the ship around when it reaches the extent of its horiztonal patrol area
            if (loc.x > MAX_HORIZONTAL_BOUNDS){
                loc.x = MAX_HORIZONTAL_BOUNDS;
                t.headLeft();
            }
        }

        //vertical sped remains the same, not affected by speed adj
        if (t.headingDown()){
            loc.y += (speed) / fps;
            if (loc.y > MAX_VERTICAL_BOUNDS){
                t.headUp();
            }
        } else {
            loc.y -= (speed) / fps;
            if (loc.y < MIN_VERTICAL_BOUNDS){
                t.headDown();
            }
        }

        //update the collider
        t.updateCollider();

        //shoot if the alien within a ships height above, below, or in line with the player?
        if (mShotRandom.nextInt(SHOT_CHANCE) == TAKE_SHOT){
            if(Math.abs(playerLocation.y - loc.y) < height){
                // is the alien facing the right direction and close enough to the player?
                if ((t.getFacingRight() && playerLocation.x > loc.x
                        || !t.getFacingRight() && playerLocation.x < loc.x)
                        && Math.abs(playerLocation.x - loc.x) < screenX){

                    //Fire!
                    alienLaserSpawner.spawnAlienLaser();
                }
            }
        }
        return true;
    }
}
