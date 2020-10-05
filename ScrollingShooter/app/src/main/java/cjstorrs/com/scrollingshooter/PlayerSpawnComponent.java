package cjstorrs.com.scrollingshooter;

class PlayerSpawnComponent implements SpawnComponent {
    @Override
    public void spawn(Transform playerTransform, Transform t) {
        //spawn in the center of the screen
        t.setLocation(t.getmScreenSize().x/2, t.getmScreenSize().y/2);
    }
}
