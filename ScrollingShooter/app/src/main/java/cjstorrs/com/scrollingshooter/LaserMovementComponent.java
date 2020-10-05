package cjstorrs.com.scrollingshooter;

class LaserMovementComponent implements  MovementComponent {
    @Override
    public boolean move(long fps, Transform t, Transform playerTransform) {
        return true;
    }
}
