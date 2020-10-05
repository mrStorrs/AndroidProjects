package cjstorrs.com.scrollingshooter;

import java.util.ArrayList;

class PhysicsEngine {
    //this signature and much more will change later in the proj
    boolean update(long fps, ArrayList<GameObject> objects, GameState gs, SoundEngine se, ParticleSystem ps){
        //update all the GameObjects
        for (GameObject object : objects){
            if (object.checkActive()){
                object.update(fps, objects.get(Level.PLAYER_INDEX).getTransform());
            }
        }

        if(ps.mIsRunning){
            ps.update(fps);
        }
        return false;
    }
    //collision method will go here.
}
