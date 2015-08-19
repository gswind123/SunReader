package reader.sun.common.view;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Scroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * ListView  which shows horizontally
 * Created by yw_sun on 2015/8/12.
 */
public class HorizontalListView extends AdapterView<BaseAdapter>
        implements GestureDetector.OnGestureListener{
    /**
     * Container of the outer-customized adapter
     * Provide head and tail views
     */
    private class PrivateListAdapter extends BaseAdapter{
        private BaseAdapter mMainAdapter = null;
        private ArrayList<View> mHeadViewList = new ArrayList<View>();
        private ArrayList<View> mTailViewList = new ArrayList<View>();

        @Override
        public int getCount() {
            int mainCount = mMainAdapter==null?0:mMainAdapter.getCount();
            return mHeadViewList.size()+mainCount+mTailViewList.size();
        }
        @Override
        public Object getItem(int index) {
            int headLength = mHeadViewList.size();
            int bodyLength = mMainAdapter==null?0:mMainAdapter.getCount();
            if(index < headLength) {
                return null;
            } else if(index < (headLength+bodyLength)) {
                index -= headLength;
                return mMainAdapter==null?null:mMainAdapter.getItem(index);
            } else {
                return null;
            }
        }
        @Override
        public long getItemId(int index) {
            int headLength = mHeadViewList.size();
            int bodyLength = mMainAdapter==null?0:mMainAdapter.getCount();
            if(index < headLength) {
                return 0;
            } else if(index < (headLength+bodyLength)) {
                index -= headLength;
                return mMainAdapter==null?0:mMainAdapter.getItemId(index);
            } else {
                return 0;
            }
        }
        @Override
        public View getView(int index, View convertView, ViewGroup viewGroup) {
            int headLength = mHeadViewList.size();
            int bodyLength = mMainAdapter==null?0:mMainAdapter.getCount();
            if(index < headLength) {
                return mHeadViewList.get(index);
            } else if(index < (headLength+bodyLength)) {
                index -= headLength;
                if(mMainAdapter == null) {
                    return null;
                } else {
                    return mMainAdapter.getView(index, convertView, viewGroup);
                }
            } else {
                index -= headLength + bodyLength;
                return mTailViewList.get(index);
            }
        }
        public void setMainAdapter(BaseAdapter adapter) {
            if(mMainAdapter != null) {
                mMainAdapter.unregisterDataSetObserver(mDataObserver);
            }
            mMainAdapter = adapter;
            if(mMainAdapter != null) {
                mMainAdapter.registerDataSetObserver(mDataObserver);
            }
        }
        public void addHeadView(View headView) {
            mHeadViewList.add(headView);
        }
        public void removeHeadView(View headView) {
            mHeadViewList.remove(headView);
        }
        public void addTailView(View tailView) {
            mTailViewList.add(tailView);
        }
        public void removeTailView(View tailView) {
            mTailViewList.remove(tailView);
        }
    }

    private HashMap<Integer, View> mRecycler = new HashMap<Integer, View>();
    private PrivateListAdapter mAdapter = new PrivateListAdapter();
    private Scroller mScroller = null;
    private DataSetObserver mDataObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            resetList();
        }
    };
    private int mMaxX = 0;
    /**
     * The left and right item's offset in adapter
     */
    private int mLeftItemPos = 0;
    private int mRightItemPos = -1;
    /**
     * The left and right item's starting coordinate
     * Used to layout childs
     * mLeftItemCoord: left coord of most left visible item
     * mRightItemCoord: right coord of most right visible item
     */
    private float mLeftItemCoord = 0;
    private float mRightItemCoord = 0;

    public HorizontalListView(Context context) {
        super(context);
        initView(context);
    }
    public HorizontalListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private GestureDetector mGestureDetector = new GestureDetector(this);
    /**
     * Override {#dispatchTouchEvent} to prevent that children's
     * {#onTouch} might return true and invalidate the parent's touch events
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if(super.dispatchTouchEvent(event)) {
            onTouchEvent(event);
        }
        // else {#onTouchEvent} must be executed
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent evt) {
        super.onTouchEvent(evt);
        if(mGestureDetector != null) {
            mGestureDetector.onTouchEvent(evt);
        }
        //Don't catch event here, catch it in {#dispatchTouchEvent}
        return false;
    }

    private void initView(Context context) {
        mLeftItemCoord = getPaddingLeft();
        mRightItemCoord = mLeftItemCoord;
        mScroller = new Scroller(context);
        mMaxX = 0x7fffffff;
        layoutTest();
    }

    /**
     * Detect the rage of current list by layouting
     */
    private void layoutTest() {
        fillRightView(mMaxX);
        removeAllViews();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right ,int bottom) {
        //try to refresh views
        int leftBound = getScrollX();
        int rightBound = leftBound + right - left;
        hideInvisChilds(leftBound, rightBound);
        fillLeftViews(leftBound);
        fillRightView(rightBound);
        //layout child views
        int childCount = getChildCount();
        int startCoord = (int)mLeftItemCoord;
        for(int i=0;i<childCount;i++) {
            //make sure the every child is measured
            View child = getChildAt(i);
            int childLeft = startCoord;
            int childRight = childLeft + child.getMeasuredWidth();
            int childTop = getPaddingTop();
            int childBottom = childTop + child.getMeasuredHeight();
            child.layout(childLeft, childTop, childRight, childBottom);
            startCoord = childRight;
        }
        //Do pending operations
        while(!mOperationQueue.isEmpty()) {
            Runnable runnable = mOperationQueue.poll();
            runnable.run();
        }
    }

    /**
     * Bind a click listener on every item to trigger OnItemClick
     */
    private void bindClickListener(final View itemView, final int position, final long id) {
        itemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                HorizontalListView.this.performItemClick(itemView, position, id);
            }
        });
    }

    /**
     * Fill new views from the adapter to list
     * Notice that we know everything about the most left and right item
     */
    private void fillLeftViews(int leftBound) {
        while(mLeftItemCoord > leftBound && mLeftItemPos>0){
            mLeftItemPos -= 1;
            View convertView = mRecycler.get(mLeftItemPos);
            if(convertView != null) {
                mRecycler.remove(mLeftItemPos);
            }
            View child = mAdapter.getView(mLeftItemPos, convertView, this);
            bindClickListener(child, mLeftItemPos, mAdapter.getItemId(mLeftItemPos));
            addAndMeaureChild(child, 0);
            mLeftItemCoord -= child.getMeasuredWidth();
        }
    }
    private void fillRightView(int rightBound) {
        while(mRightItemCoord < rightBound && mRightItemPos<mAdapter.getCount()-1) {
            mRightItemPos += 1;
            View convertView = mRecycler.get(mRightItemPos);
            if(convertView != null) {
                mRecycler.remove(mRightItemPos);
            }
            View child = mAdapter.getView(mRightItemPos, convertView, this);
            bindClickListener(child, mRightItemPos, mAdapter.getItemId(mRightItemPos));
            addAndMeaureChild(child, -1);
            mRightItemCoord += child.getMeasuredWidth();
            if(mRightItemPos == mAdapter.getCount()-1) {
                mMaxX = (int)Math.max(0, mRightItemCoord - getWidth());
            }
        }
    }
    private void addAndMeaureChild(View child, int index){
        LayoutParams lp = child.getLayoutParams();
        if(lp == null) {
            lp = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.MATCH_PARENT);
        }
        int width = getWidth() - getPaddingRight() - getPaddingLeft();
        int height = getHeight() - getPaddingBottom() - getPaddingTop();
        int widthSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.UNSPECIFIED);
        int heightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        child.measure(widthSpec, heightSpec);
        this.addViewInLayout(child, index, lp);
    }
    private void hideInvisChilds(int leftBound, int rightBound) {
        //remove invisible views on the left side
        while(getChildCount()>0) {
            View view = getChildAt(0);
            int width = view.getMeasuredWidth();
            if((mLeftItemCoord + width) < leftBound) {
                mRecycler.put(mLeftItemPos, view);
                removeViewInLayout(view);
                mLeftItemCoord += width;
                mLeftItemPos += 1;
            } else {
                break;
            }
        }
        //remove on the right side
        while(getChildCount()>0) {
            int childCount = getChildCount();
            View view = getChildAt(childCount-1);
            int width = view.getMeasuredWidth();
            if((mRightItemCoord-width) > rightBound) {
                mRecycler.put(mRightItemPos, view);
                removeViewInLayout(view);
                mRightItemCoord -= width;
                mRightItemPos -= 1;
            }else {
                break;
            }
        }
    }
    @Override
    public void removeAllViews() {
        removeViewsInLayout(0, getChildCount());
        mRecycler.clear();
    }

    private void resetList() {
        mLeftItemPos = 0;
        mRightItemPos = -1;
        mLeftItemCoord = mRightItemCoord = getPaddingLeft();
        mMaxX = 0x7fffffff;

        mScroller = new Scroller(getContext());
        scrollTo(0, 0);

        removeAllViews();
        layoutTest();
        invalidate();
        requestLayout();
    }

    public void fling(int velX){
        mScroller.fling(getScrollX(), getScrollY(), velX, 0,
                0x80000000, 0x7fffffff,/*use two big edge to keep fling speed*/ 0,0);
        this.postDelayed(new Runnable(){
            @Override
            public void run() {
                mScroller.computeScrollOffset();
                int curX = mScroller.getCurrX();
                //mMaxX can be refreshed,so judge again here
                if(curX > mMaxX) {
                    mScroller.forceFinished(true);
                    curX = mMaxX;
                } else if(curX < 0) {
                    mScroller.forceFinished(true);
                    curX = 0;
                }
                scrollTo(curX, 0);
                invalidate();
                requestLayout();
                if(!mScroller.isFinished()) {
                    HorizontalListView.this.postDelayed(this,30);
                }
            }
        },30);
    }

    private void scrollBy(int deltaX) {
        int dstX = getScrollX() + deltaX;
        if(dstX < 0) {
            dstX = 0;
        } else if(dstX > mMaxX) {
            dstX = mMaxX;
        }
        scrollTo(dstX, 0);
    }

    /**
     * Cache some operations and do them after {#onLayout}
     * This is to make sure some view operations is valid
     */
    private LinkedList<Runnable> mOperationQueue = new LinkedList<Runnable>();
    public void scrollToEnd() {
        mOperationQueue.offer(new Runnable() {
            @Override
            public void run() {
                scrollBy(mMaxX);
            }
        });
        requestLayout();
    }

    @Override
    public BaseAdapter getAdapter() {
        return (BaseAdapter)mAdapter;
    }

    @Override
    public void setAdapter(BaseAdapter baseAdapter) {
        mAdapter.setMainAdapter(baseAdapter);
        resetList();
    }

    @Override
    public View getSelectedView() {
        return null;
    }

    @Override
    public void setSelection(int i) {

    }

    public void addHeadView(View headView) {
        mAdapter.addHeadView(headView);
        mAdapter.notifyDataSetChanged();
    }
    public void removeHeadView(View headView) {
        mAdapter.removeHeadView(headView);
        mAdapter.notifyDataSetChanged();
    }
    public void addTailView(View tailView) {
        mAdapter.addTailView(tailView);
        mAdapter.notifyDataSetChanged();
    }
    public void removeTailView(View tailView) {
        mAdapter.removeTailView(tailView);
        mAdapter.notifyDataSetChanged();
    }

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
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float delX, float delY) {
        scrollBy((int)delX);
        requestLayout();
        return true;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float velX, float velY) {
        fling((int)-velX);
        return true;
    }
}
