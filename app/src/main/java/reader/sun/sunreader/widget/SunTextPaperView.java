package reader.sun.sunreader.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

import reader.sun.common.SunUserConfig;
import reader.sun.common.foundation.util.StringUtil;
import reader.sun.common.foundation.util.SunDeviceUtil;
import reader.sun.common.model.DataModel;
import reader.sun.common.widget.SunPaperView;
import reader.sun.sunreader.model.TextDataLocator;
import reader.sun.sunreader.model.TextDataModel;

/**
 * Paper page view for full-text data
 * Use {#setDataLoader} to provide data
 * Created by yw_sun on 2015/7/17.
 */
public class SunTextPaperView extends SunPaperView {
    // lineHeight = textHeight * LineHeightFactor
    private final float LineHeightFactor = 1.1f;

    private TextDataModel mPageContent = new TextDataModel();
    private Paint mTextPaint = new Paint();
    private Context mContext = null;

    protected TextDataLocator mLocator = new TextDataLocator();

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

    @Override
    protected void loadData() {
        DataModel rawData = null;
        if(mDataLoader != null) {
            rawData = mDataLoader.readDataModel(mCurrentLocator);
        }
        if(rawData!=null && (rawData instanceof TextDataModel)) {
            mPageContent = (TextDataModel)rawData;
        }
    }

    private void drawLine(Canvas canvas,String text, int startX, int startY, int lineHeight,Paint.FontMetrics fm, Paint paint) {
        float text_x = startX;
        float text_y = startY + (lineHeight - (fm.bottom - fm.top))/2 - fm.ascent;
        canvas.drawText(text, text_x, text_y, paint);
    }

    private void refreshConfig() {
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        int text_size = SunDeviceUtil.getPixelFromDip(dm, SunUserConfig.getPaperTextSizeSp());
        if(mTextPaint != null && mTextPaint.getTextSize() == text_size) {
            return ;
        }
        mTextPaint = new Paint();
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        mTextPaint.setTextSize(text_size);
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
        refreshConfig();
        if(mPageContent.isEmpty()) {
            return ;
        }
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

