package reader.sun.sunreader.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import reader.sun.common.SunUserConfig;
import reader.sun.common.foundation.util.StringUtil;
import reader.sun.common.foundation.util.SunDeviceUtil;
import reader.sun.sunreader.model.TextDataLocator;
import reader.sun.sunreader.model.TextDataModel;
import reader.sun.sunreader.util.TextDataProvider;

/**
 * Created by yw_sun on 2015/7/17.
 */
public class SunTextPaperView extends View {
    // lineHeight = textHeight * LineHeightFactor
    private final float LineHeightFactor = 1.1f;

    private TextDataModel mPageContent = new TextDataModel();
    private Paint mTextPaint = new Paint();
    private Context mContext = null;

    private int mDirection = 0;

    protected TextDataLocator mLocator = new TextDataLocator();
    protected TextDataProvider mProvider = new TextDataProvider();

    public SunTextPaperView(Context context) {
        super(context);
        mContext = context;
        refreshConfig();
    }
    public SunTextPaperView(Context context, AttributeSet attributes) {
        super(context, attributes);
        mContext = context;
        refreshConfig();
    }

    /**
     * Fill mPageContent, must be invoked after {#onMeasure}
     * */
    private void loadData() {
        if(mLocator.isEmpty()) {
            mLocator = mProvider.createPageFullLocatorFromStart(0, this);
        }
        mPageContent = (TextDataModel)mProvider.readData(mLocator);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        loadData();
    }

    private void drawLine(Canvas canvas,String text, int startX, int startY, int lineHeight,Paint.FontMetrics fm, Paint paint) {
        float text_x = startX;
        float text_y = startY + (lineHeight - (fm.bottom - fm.top))/2 - fm.ascent;
        canvas.drawText(text, text_x, text_y, paint);
    }

    public void refreshConfig() {
        mTextPaint = new Paint();
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        int text_size = SunDeviceUtil.getPixelFromDip(dm, SunUserConfig.getPaperTextSizeSp());
        mTextPaint.setTextSize(text_size);
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mDirection == 0) {
                    mLocator = mProvider.createPageFullLocatorFromStart(mLocator.mEndIndex,SunTextPaperView.this);
                    if(mProvider.reachEnd(mLocator)) {
                        mDirection = 1;
                    }
                } else {
                    mLocator = (TextDataLocator)mProvider.createPageFullLocatorFromEnd(mLocator.mStartIndex,SunTextPaperView.this);
                    if(mProvider.reachStart(mLocator)){
                        mDirection = 0;
                    }
                }
                loadData();
                invalidate();
            }
        });
    }

    public Paint getTextPaint() {
        return mTextPaint;
    }

    public int getPageWidth() {
        return getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
    }
    public int getPageHeight() {
        return getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
    }
    public int getLineHeight() {
        Paint.FontMetrics fm = mTextPaint.getFontMetrics();
        int textHeight = (int)(fm.bottom - fm.top);
        return (int)(textHeight * LineHeightFactor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /**
         * IMPORTANT:The text-layout method is closely related to
         * {#createPageFullLocatorFromXXX} in {#TextDataProvider}.
         * If this was modified, be ware to synchronize
         * {#TextDataProvider::createPageFullLocatorFromStart} and
         * {#TextDataProvider::createPageFullLocatorFromEnd}
         */
        final int pageWidth = getPageWidth();
        final int pageHeight = getPageHeight();
        Paint.FontMetrics fm = mTextPaint.getFontMetrics();
        final int lineHeight = getLineHeight();
        StringBuilder sb = new StringBuilder();
        int index = 0;
        boolean keep_draw = true;
        int startY = getPaddingTop();
        int startX = getPaddingLeft();
        int endX = startX + pageWidth;
        int endY = startY + pageHeight;
        int curX = startX;
        while(keep_draw) {
            String cur_char = mPageContent.mTextData.charAt(index) + "";
            int char_width = (int)mTextPaint.measureText(cur_char);
            boolean is_new_line = false;
            if(cur_char.equals("\n")) {
                is_new_line = true;
                index++;
            }else if((curX + char_width) > endX) {
                is_new_line = true;
            } else {
                curX += char_width;
                sb.append(cur_char);
                index++;
            }

            if(is_new_line){
                String line = sb.toString();
                sb  = new StringBuilder();
                if(!StringUtil.emptyOrNull(line)) {
                    drawLine(canvas, line, startX, startY, lineHeight, fm, mTextPaint);
                }
                startY += lineHeight;
                curX = startX;
            }

            if((startY + lineHeight)>endY) {
                keep_draw = false;//page is full
            } else if(index >= mPageContent.mTextData.length()) {
                keep_draw = false;
                String line = sb.toString();
                if(!StringUtil.emptyOrNull(line)) {
                    //draw the last line
                    drawLine(canvas, line, startX, startY,lineHeight, fm, mTextPaint);
                }
            }
        }
    }//onDraw

}

