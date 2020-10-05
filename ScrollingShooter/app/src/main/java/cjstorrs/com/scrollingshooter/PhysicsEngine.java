package cjstorrs.com.scrollingshooter;

class PhysicsEngine {
    //this signature and much more will change later in the proj
    boolean update(long fps, ParticleSystem ps){
        if(ps.mIsRunning){
            ps.update(fps);
        }
        return false;
    }
    //collision method will go here.
}
