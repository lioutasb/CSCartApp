package gr.plushost.prototypeapp.widgets;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import java.lang.reflect.Field;

/**
 * Custom {@link android.support.v4.view.ViewPager} implementation to resolve scroll gesture directions more accurate than a regular
 * {@link android.support.v4.view.ViewPager} component. This will make it perfectly usable into a scroll container such as {@link android.widget.ScrollView},
 * {@link android.widget.ListView}, etc.
 * <p>
 * Default ViewPager becomes hardly usable when it's nested into a scroll container. Such container will intercept any
 * touch event with minimal vertical shift from the child ViewPager. So switch the page by scroll gesture with a regular
 * {@link android.support.v4.view.ViewPager} nested into a scroll container, user will need to move his finger horizontally without vertical
 * shift. Which is obviously quite irritating. {@link SmartViewPager} has a much much better behavior at resolving
 * scrolling directions.
 */
public class SmartViewPager extends LoopViewPager {

    Handler handler;
    Runnable animateViewPager;
    boolean sliding = true;
    int delay = 0;
    private FixedSpeedScroller mScroller = null;

    // -----------------------------------------------------------------------
    //
    // Constructor
    //
    // -----------------------------------------------------------------------
    public SmartViewPager(Context context) {
        super(context);
        mGestureDetector = new GestureDetector(context, new XScrollDetector());
        init();
    }

    public SmartViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        mGestureDetector = new GestureDetector(context, new XScrollDetector());
        init();
    }

    // -----------------------------------------------------------------------
    //
    // Fields
    //
    // -----------------------------------------------------------------------
    private GestureDetector mGestureDetector;
    private boolean mIsLockOnHorizontalAxis = false;

    // -----------------------------------------------------------------------
    //
    // Methods
    //
    // -----------------------------------------------------------------------
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // decide if horizontal axis is locked already or we need to check the scrolling direction
        if (!mIsLockOnHorizontalAxis)
            mIsLockOnHorizontalAxis = mGestureDetector.onTouchEvent(event);

        // release the lock when finger is up
        if (event.getAction() == MotionEvent.ACTION_UP)
            mIsLockOnHorizontalAxis = false;

        getParent().requestDisallowInterceptTouchEvent(mIsLockOnHorizontalAxis);
        return super.onTouchEvent(event);
    }

    public void runnable(final int size, final int delay) {
        this.delay = delay;
        handler = new Handler();
        animateViewPager = new Runnable() {
            public void run() {
                if (sliding) {
                    if (getCurrentItem() == size - 1) {
                        setCurrentItem(0, true);
                    } else {
                        setCurrentItem(getCurrentItem() + 1, true);
                    }
                }
                handler.postDelayed(animateViewPager, delay);
            }
        };
        handler.postDelayed(animateViewPager, delay);

    }

    public void setSliding(boolean sliding){
        this.sliding = sliding;
    }

    public Handler getHandlers(){
        return handler;
    }

    public Runnable getRunnable(){
        return animateViewPager;
    }

    // -----------------------------------------------------------------------
    //
    // Inner Classes
    //
    // -----------------------------------------------------------------------
    private class XScrollDetector extends SimpleOnGestureListener {

        // -----------------------------------------------------------------------
        //
        // Methods
        //
        // -----------------------------------------------------------------------
        /**
         * @return true - if we're scrolling in X direction, false - in Y direction.
         */
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return Math.abs(distanceX) > Math.abs(distanceY);
        }

    }

    private void init() {
        try {
            Class<?> viewpager = ViewPager.class;
            Field scroller = viewpager.getDeclaredField("mScroller");
            scroller.setAccessible(true);
            mScroller = new FixedSpeedScroller(getContext(),
                    new DecelerateInterpolator());
            scroller.set(this, mScroller);
        } catch (Exception ignored) {
        }
    }

    public void setScrollDuration(int duration) {
        mScroller.setScrollDuration(duration);
    }

    private class FixedSpeedScroller extends Scroller {

        private int mDuration = 500;

        public FixedSpeedScroller(Context context) {
            super(context);
        }

        public FixedSpeedScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        public FixedSpeedScroller(Context context, Interpolator interpolator, boolean flywheel) {
            super(context, interpolator, flywheel);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        public void setScrollDuration(int duration) {
            mDuration = duration;
        }
    }
}
