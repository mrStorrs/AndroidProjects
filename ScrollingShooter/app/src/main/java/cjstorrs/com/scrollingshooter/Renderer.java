package cjstorrs.com.scrollingshooter;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

class Renderer {
    private Canvas mCanvas;
    private SurfaceHolder mSurfaceHolder;
    private Paint mPaint;

    Renderer(SurfaceView sh) {
        mSurfaceHolder = sh.getHolder();
        mPaint = new Paint();
    }

    void draw(GameState gs, HUD hud, ParticleSystem ps) {
        if(mSurfaceHolder.getSurface().isValid()){
            mCanvas = mSurfaceHolder.lockCanvas();
            mCanvas.drawColor(Color.argb(255, 0, 0, 0));

            if (gs.getDrawing()) {
                //draw all the game objs here
            }

            if (gs.getGameOver()) {
                //draw a background graphic here
            }

            //draw a particle system explosion here
            if(ps.mIsRunning){
                ps.draw(mCanvas, mPaint);
            }

            //now we draw the hud on top of everything else
            hud.draw(mCanvas, mPaint, gs);

            mSurfaceHolder.unlockCanvasAndPost(mCanvas);
        }
    }
}

