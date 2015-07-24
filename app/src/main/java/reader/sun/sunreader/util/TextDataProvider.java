package reader.sun.sunreader.util;

import android.graphics.Paint;
import android.view.View;

import java.io.File;
import java.util.ArrayList;

import reader.sun.common.DataProvider;
import reader.sun.common.model.DataLocator;
import reader.sun.common.model.DataModel;
import reader.sun.sunreader.model.TextDataLocator;
import reader.sun.sunreader.model.TextDataModel;
import reader.sun.sunreader.widget.SunTextPaperView;

/**
 * DataProvider for full-text books
 * Created by yw_sun on 2015/7/21.
 */
public class TextDataProvider implements DataProvider {
    /** Constants for text data processing */
    public final int TEXT_CAPACITY = 0x8000; //32768

    /** Data in memory */
    private String mData = "";

    @Override
    public void parseFileToMem(File srcFile, DataLocator locator) {
        //TODO:load txt file into mData
    }

    @Override
    public DataModel readData(DataLocator locator) {
        TextDataModel resultModel = new TextDataModel();
        if(!(locator instanceof TextDataLocator)) {
            return resultModel;
        }
        TextDataLocator txtLocator = (TextDataLocator)locator;
        int start = txtLocator.mStartIndex;
        int end = txtLocator.mEndIndex;
        start = Math.max(start, 0);
        start = Math.min(start, mData.length()-1);
        end  = Math.max(end, 0);
        end = Math.min(end, mData.length());
        if(end <= start) {
            return resultModel;
        }
        resultModel.mTextData = mData.substring(start, end);
        return resultModel;
    }

    public boolean reachStart(TextDataLocator locator) {
        if(locator.mStartIndex <= 0) {
            return true;
        } else return false;
    }

    public boolean reachEnd(TextDataLocator locator) {
        if(locator.mEndIndex >= mData.length()) {
            return true;
        } else return false;
    }

    /**
    * <p>Create a {#DataLocator} that can fill a {#SunTextPaperView} with a start index</p>
    * @param: start:the start index for locator; page:the view to fill
    * @return a page-filled locator
    * */
    public TextDataLocator createPageFullLocatorFromStart(int startIndex, SunTextPaperView page) {
        TextDataLocator locator = new TextDataLocator();
        startIndex = Math.max(0, startIndex);
        startIndex = Math.min(startIndex, mData.length()-1);
        locator.mStartIndex = startIndex;
        int pageWidth = page.getPageWidth();
        int pageHeight = page.getPageHeight();
        if(pageWidth <= 0 || pageHeight <= 0) {
            //paper view hasn't been measured
            int unspecMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            page.measure(unspecMeasureSpec, unspecMeasureSpec);
            pageWidth = page.getPageWidth();
            pageHeight = page.getPageHeight();
        }
        Paint paint = page.getTextPaint();
        final int lineHeight = page.getLineHeight();
        float startY = page.getPaddingTop();
        float startX = page.getPaddingLeft();
        float endX = startX + pageWidth;
        float endY = startY + pageHeight;
        float curX = startX;
        int endIndex = startIndex;
        boolean isEnd = false;
        while(!isEnd) {
            boolean isNewLine = false;
            String curChar = mData.charAt(endIndex)+"";
            float charWidth = paint.measureText(curChar);
            if(curChar.equals("\n")) {
                isNewLine = true;
                endIndex++;
            } else if((charWidth+curX)>endX) {
                isNewLine = true;
            } else {
                curX += charWidth;
                endIndex++;
            }
            if(isNewLine) {
                curX = startX;
                startY += lineHeight;
            }
            if((startY+lineHeight)>endY || endIndex>=mData.length()) {
                isEnd = true;
            }
        }
        locator.mEndIndex = endIndex;
        return locator;
    }//createPageFullLocatorFromStart

    /**
     * <p>Create a {#DataLocator} that can fill a {#SunTextPaperView} with an end index</p>
     * @param: end:the end index for locator; page:the view to fill
     * @return a page-filled locator
     * */
    public TextDataLocator createPageFullLocatorFromEnd(int endIndex, SunTextPaperView page) {
        TextDataLocator locator = new TextDataLocator();
        endIndex = Math.max(1, endIndex);
        endIndex = Math.min(endIndex, mData.length());
        locator.mEndIndex = endIndex;
        int pageWidth = page.getPageWidth();
        int pageHeight = page.getPageHeight();
        if(pageWidth <= 0 || pageHeight <= 0) {
            //paper view hasn't been measured
            int unspecMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            page.measure(unspecMeasureSpec, unspecMeasureSpec);
            pageWidth = page.getPageWidth();
            pageHeight = page.getPageHeight();
        }
        Paint paint = page.getTextPaint();
        final int lineHeight = page.getLineHeight();
        float startY = page.getPaddingTop()+pageHeight;
        float startX = page.getPaddingLeft();
        float endX = startX + pageWidth;
        float endY = page.getPaddingTop();
        float curX = startX;
        int startIndex = endIndex - 1;
        boolean isEnd = false;
        while(!isEnd) {
            boolean isNewLine = false;
            String curChar = mData.charAt(startIndex)+"";
            float charWidth = paint.measureText(curChar);
            if(curChar.equals("\n")) {
                isNewLine = true;
                startIndex--;
            } else if((charWidth+curX)>endX) {
                isNewLine = true;
            } else {
                curX += charWidth;
                startIndex--;
            }
            if(isNewLine) {
                curX = startX;
                startY -= lineHeight;
            }
            if((startY-lineHeight)<endY || startIndex<0) {
                isEnd = true;
            }
        }
        startIndex++;
        /**
         *  The startIndex must be adjusted by the endIndex:
         *      paint the page from startIndex to the endIndex,and do a clip
         *      according to the endIndex to find a real startIndex
         * */
        isEnd = false;
        startX = page.getPaddingLeft();
        startY = page.getPaddingTop();
        endX = startX + pageWidth;
        endY = startY + pageHeight;
        curX = startX;
        int iterIndex = startIndex;
        ArrayList<Integer> lineStartIndexes = new ArrayList<Integer>();
        lineStartIndexes.add(iterIndex);
        while(!isEnd) {
            boolean isNewLine = false;
            String curChar = mData.charAt(iterIndex)+"";
            float charWidth = paint.measureText(curChar);
            if(curChar.equals("\n")) {
                isNewLine = true;
                iterIndex++;
            } else if((charWidth+curX)>endX) {
                isNewLine = true;
            } else {
                curX += charWidth;
                iterIndex++;
            }
            if(isNewLine) {
                curX = startX;
                startY += lineHeight;
                lineStartIndexes.add(iterIndex);
            }
            if(iterIndex >= endIndex) {
                isEnd = true;
            }
        }
        float slideUpDis = Math.max(0, (startY+lineHeight-endY));
        int slideUpLineNum = (int)(slideUpDis/lineHeight + 0.99f);
        slideUpLineNum = Math.min(slideUpLineNum, lineStartIndexes.size()-1);
        locator.mStartIndex = lineStartIndexes.get(slideUpLineNum);
        //if start from 0, the page must be filled
        if(locator.mStartIndex == 0) {
            locator = createPageFullLocatorFromStart(0, page);
        }
        return locator;
    }//createPageFullLocatorFromEnd

}
