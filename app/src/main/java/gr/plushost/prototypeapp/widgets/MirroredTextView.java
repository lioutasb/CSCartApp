package gr.plushost.prototypeapp.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.beardedhen.androidbootstrap.FontAwesomeText;

/**
 * Created by user on 5/8/2015.
 */
public class MirroredTextView extends FontAwesomeText {

    public MirroredTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.scale(-1,1, getWidth()/2, getHeight()/2);
        super.dispatchDraw(canvas);
        canvas.restore();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        event.setLocation(getWidth() - event.getX(), event.getY());
        return super.dispatchTouchEvent(event);
    }


}