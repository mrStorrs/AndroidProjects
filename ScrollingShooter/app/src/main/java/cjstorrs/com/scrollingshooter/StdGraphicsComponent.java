package cjstorrs.com.scrollingshooter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;

class StdGraphicsComponent implements GraphicsComponent{
    private Bitmap mBitmap;
    private Bitmap mBitmapReversed;

    @Override
    public void initialize(Context context, ObjectSpec spec, PointF objectSize){
        //make a resource id out of the strin of the file name
        int resID = context.getResources().getIdentifier(spec.getBitmapName(),
                "drawable",context.getPackageName());

        //load te bitmap using the id
        mBitmap = BitmapFactory.decodeResource(context.getResources(), resID);

        //create a mirror imae of te bitmap if needed
        Matrix matrix = new Matrix();
        matrix.setScale(-1, 1);
        mBitmapReversed = Bitmap.createBitmap(mBitmap, 0, 0,mBitmap.getWidth(),
                mBitmap.getHeight(), matrix, true);
    }

    @Override
    public void draw(Canvas canvas, Paint paint, Transform t){
        if(t.getFacingRight()){
            canvas.drawBitmap(mBitmap, t.getLocation().x, t.getLocation().y, paint);
        } else {
            canvas.drawBitmap(mBitmapReversed, t.getLocation().x, t.getLocation().y, paint);
        }
    }
}
