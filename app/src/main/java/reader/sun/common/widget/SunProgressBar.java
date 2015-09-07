package reader.sun.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ProgressBar;

/**
 * Progress bar with touching
 * Created by yw_sun on 2015/9/7.
 */
public class SunProgressBar extends ProgressBar implements GestureDetector.OnGestureListener{
    public interface OnSetProgressListenser{
        public void onSetProgress(int progress, SunProgressBar progressBar);
    }
    private OnSetProgressListenser mOnSetProgressListener = null;

    @Override
    public void setProgress(int progress) {
        super.setProgress(progress);
        if(mOnSetProgressListener != null) {
            mOnSetProgressListener.onSetProgress(progress, this);
        }
    }

    public void setOnSetProgreeListener(OnSetProgressListenser listener) {
        mOnSetProgressListener = listener;
    }

    private GestureDetector mGestureDetector = new GestureDetector(this);

    public SunProgressBar(Context context) {
        super(context);
    }

    public SunProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SunProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        int width = getMeasuredWidth();
        float progress = motionEvent.getX()/width;
        progress = Math.max(0, progress);
        progress = Math.min(progress, 1.f);
        setProgress((int)(getMax()*progress));
        return true;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float delX, float delY) {
        int width = getMeasuredWidth();
        int max = getMax();
        float delProgress = delX / width;
        int curProgress = (int)(delProgress*max + getProgress());
        curProgress = Math.min(curProgress, max);
        curProgress = Math.max(curProgress, 0);
        setProgress(curProgress);
        return true;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v2) {
        return false;
    }
}
