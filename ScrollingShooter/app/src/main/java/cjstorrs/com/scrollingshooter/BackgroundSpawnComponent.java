package cjstorrs.com.scrollingshooter;

public class BackgroundSpawnComponent implements SpawnComponent {
    @Override
    public void spawn(Transform playerTransform, Transform t) {
        //place the bg in the top left corner
        t.setLocation(0f, 0f);
    }
}
