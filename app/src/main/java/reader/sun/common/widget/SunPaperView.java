package reader.sun.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import reader.sun.common.model.DataLocator;

/**
 * The frame class of paper views for reader
 * Created by yw_sun on 2015/7/23.
 */
public class SunPaperView extends View {
    protected boolean hasMeasured = false;
    protected DataLocator mCurrentLocator = new DataLocator();

    public SunPaperView(Context context) {
        super(context);
    }
    public SunPaperView(Context context, AttributeSet attributes) {
        super(context, attributes);
    }

    public void setLocator(DataLocator locator) {
        mCurrentLocator = locator;
        loadData();
        if(hasMeasured) {
            invalidate();
        }
    }
    public DataLocator getLocator() {
        return (DataLocator)mCurrentLocator.clone();
    }

    //Override this to set up data
    protected void loadData() {}

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        hasMeasured = true;
        //Initial updata data
        loadData();
    }

}
