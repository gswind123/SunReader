package reader.sun.sunreader.fragment;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import reader.sun.common.SunBaseFragment;
import reader.sun.common.model.DataLocator;
import reader.sun.common.model.DataModel;
import reader.sun.common.widget.SunPaperView;
import reader.sun.sunreader.R;
import reader.sun.sunreader.model.TextBookInfo;
import reader.sun.sunreader.model.TextDataLocator;
import reader.sun.sunreader.util.TextDataProvider;
import reader.sun.sunreader.widget.SunTextPaperView;


/**
 * Created by yw_sun on 2015/7/16.
 */
public class SunTextReaderFragment extends SunBaseFragment {
    private View mRootView = null;
    private SunTextPaperView mUpperPage = null;
    private SunTextPaperView mUnderPage = null;
    private TextDataProvider mDataProvider = null;
    private TextBookInfo mCurrentBook = null;

    private GestureDetector mGestureDetector;
    private GestureDetector.OnGestureListener mGestureDetectorListener = new GestureDetector.OnGestureListener() {
        @Override
        public boolean onDown(MotionEvent motionEvent) {
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
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v2) {
            return false;
        }
        @Override
        public void onLongPress(MotionEvent motionEvent) {
        }
        @Override
        public boolean onFling(MotionEvent startEvent, MotionEvent endEvent, float velocityX, float velocityY) {
            boolean handled = false;
            if(startEvent.getX() > endEvent.getX()) {
                goNextPage();
                handled = true;
            } else if(startEvent.getX() < endEvent.getX()) {
                goPrevPage();
                handled = true;
            }
            return handled;
        }
    };


    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return mGestureDetector.onTouchEvent(motionEvent);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.sun_reader_fragment, null);
        initView();
        initData();
        return mRootView;
    }

    private void initView() {
        mRootView.setOnTouchListener(mOnTouchListener);
        mUpperPage = (SunTextPaperView)mRootView.findViewById(R.id.upper_page);
        mUnderPage = (SunTextPaperView)mRootView.findViewById(R.id.under_page);
        mGestureDetector = new GestureDetector(getActivity(), mGestureDetectorListener);
    }

    private SunPaperView.PaperDataLoader mDataLoader4Paper = new SunPaperView.PaperDataLoader() {
        @Override
        public DataModel readDataModel(DataLocator locator) {
            if(mDataProvider==null || !(locator instanceof TextDataLocator)) {
                return null;
            }
            TextDataLocator curLocator = (TextDataLocator)locator;
            if(curLocator.isEmpty()) {
                locator = mDataProvider.createPageFullLocatorFromStart(curLocator.mStartIndex, mUpperPage);
            }
            return mDataProvider.readData(locator);
        }
    };

    private void initData() {
        mUpperPage.setDataLoader(mDataLoader4Paper);
        mUnderPage.setDataLoader(mDataLoader4Paper);
        if(mCurrentBook != null) {
            mDataProvider = new TextDataProvider(mCurrentBook);
            mUpperPage.setLocator(mCurrentBook.mLastLocator);
        }
    }

    public void goNextPage() {
        TextDataLocator curLocator = (TextDataLocator)mUpperPage.getLocator();
        TextDataProvider provider = mDataProvider;
        TextDataLocator nextLocator = provider.createPageFullLocatorFromStart(curLocator.mEndIndex,mUpperPage);
        mUpperPage.setLocator(nextLocator);
    }
    public void goPrevPage() {
        TextDataLocator curLocator = (TextDataLocator)mUpperPage.getLocator();
        TextDataProvider provider = mDataProvider;
        TextDataLocator nextLocator = provider.createPageFullLocatorFromEnd(curLocator.mStartIndex, mUpperPage);
        mUpperPage.setLocator(nextLocator);
    }

    public void setCurrentBook(TextBookInfo bookInfo) {
        if(bookInfo == null) {
            return ;
        }
        mCurrentBook = bookInfo;
    }

}
