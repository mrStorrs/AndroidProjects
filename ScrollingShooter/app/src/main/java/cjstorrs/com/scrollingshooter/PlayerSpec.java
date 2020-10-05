package cjstorrs.com.scrollingshooter;

import android.graphics.PointF;

class PlayerSpec extends ObjectSpec {
    //this is for all te unique spec of a player
    private static final String tag = "Player";
    private static final String bitmapName = "player_ship";
    private static final float speed = 1f;
    private static final PointF relativeScale = new PointF(15f, 15f);

    private static final String[] componenets = new String[]{
            "PlayerInputComponent",
            "StdGraphicsComponent",
            "PlayerMovementComponent",
            "PlayerSpawnComponent"
    };

    PlayerSpec(){
        super(tag, bitmapName, speed, relativeScale, componenets);
    }
}
