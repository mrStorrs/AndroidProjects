package cjstorrs.com.scrollingshooter;

import android.graphics.Rect;
import android.view.MotionEvent;

import java.util.ArrayList;

import androidx.constraintlayout.widget.ConstraintSet;

class UIController implements InputObserver{
    public UIController(GameEngineBroadcaster b){
        b.addObserver(this);
    }

    @Override
    public void handleInput(MotionEvent event, GameState gameState, ArrayList<Rect> buttons){
        int i = event.getActionIndex();
        int x = (int) event.getX(i);
        int y = (int) event.getY(i);

        int eventType = event.getAction() & MotionEvent.ACTION_MASK;

        if(eventType == MotionEvent.ACTION_UP || eventType == MotionEvent.ACTION_POINTER_UP){
            if(buttons.get(HUD.PAUSE).contains(x, y)){
                //player pressed the pause button, respond diff depending on gamestate
                if(!gameState.getPaused()){
                    //pause the game
                    gameState.pause();
                } else if(gameState.getGameOver()){
                    gameState.startNewGame();
                } else if(gameState.getPaused() && !gameState.getGameOver()) {
                    gameState.resume();
                }


            }
        }
    }
}
