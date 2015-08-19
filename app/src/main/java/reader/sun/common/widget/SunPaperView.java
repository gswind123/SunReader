package reader.sun.common.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import reader.sun.common.model.DataLocator;
import reader.sun.common.model.DataModel;

/**
 * The frame class of paper views for reader
 * Read hasMeasured to check if view size is determined
 * Set mDataLoader to load own data
 * Created by yw_sun on 2015/7/23.
 */
public class SunPaperView extends View {
    public interface PaperDataLoader {
        public DataModel readDataModel(DataLocator locator);
    };

    protected boolean hasMeasured = false;
    protected DataLocator mCurrentLocator = new DataLocator();
    protected DataModel   mCurrentData = new DataModel();

    /**
     * Normally data will be set when view hasn't been created
     * Set this flag to notify view to load data when {#onDraw}
     */
    private boolean dataRefreshed = true;

    public SunPaperView(Context context) {
        super(context);
    }
    public SunPaperView(Context context, AttributeSet attributes) {
        super(context, attributes);
    }

    public void setLocator(DataLocator locator) {
        mCurrentLocator = locator;
        dataRefreshed = true;
    }
    public DataLocator getLocator() {
        return (DataLocator)mCurrentLocator.clone();
    }


    /**
     * IMPORTANT:mDataLoader should be set to load own data
     */
    protected PaperDataLoader mDataLoader = null;
    public void setDataLoader(PaperDataLoader loader) {
        this.mDataLoader = loader;
        dataRefreshed = true;
    }

    private void loadData() {
        if(mDataLoader != null && dataRefreshed) {
            mCurrentData = mDataLoader.readDataModel(mCurrentLocator);
            dataRefreshed = false;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(hasMeasured == false) {
            hasMeasured = true;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //Data only loaded when needed
        loadData();
        super.onDraw(canvas);
    }

    public void refreshView() {
        this.post(new Runnable() {
            @Override
            public void run() {
                SunPaperView.this.invalidate();
            }
        });
    }

}
