package reader.sun.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import reader.sun.common.model.DataLocator;
import reader.sun.common.model.DataModel;

/**
 * The frame class of paper views for reader
 * Created by yw_sun on 2015/7/23.
 */
public class SunPaperView extends View {
    public interface PaperDataLoader {
        public DataModel readDataModel(DataLocator locator);
    };

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


    protected PaperDataLoader mDataLoader = null;
    /**
     * If data comes from outside,use the load data callback to read data
     */
    public void setDataLoader(PaperDataLoader loader) {
        this.mDataLoader = loader;
        if(!mCurrentLocator.isEmpty()) {
            loadData();
        }
    }
    //Override this to set up data
    protected void loadData() {

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        hasMeasured = true;
        //Initial updata data
        loadData();
    }

}
