package cjstorrs.com.scrollingshooter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import androidx.constraintlayout.widget.ConstraintSet;

interface GraphicsComponent {
    void initialize(Context c, ObjectSpec s, PointF screenSize);

    void draw(Canvas canvas, Paint paint, Transform t);
}
