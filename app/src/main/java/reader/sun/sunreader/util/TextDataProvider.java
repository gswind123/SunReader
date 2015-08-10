package reader.sun.sunreader.util;

import android.graphics.Paint;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;

import reader.sun.common.DataProvider;
import reader.sun.common.model.DataLocator;
import reader.sun.common.model.DataModel;
import reader.sun.sunreader.model.ReaderConstantValue;
import reader.sun.sunreader.model.TextBookInfo;
import reader.sun.sunreader.model.TextDataLocator;
import reader.sun.sunreader.model.TextDataModel;
import reader.sun.sunreader.widget.SunTextPaperView;

/**
 * DataProvider for some full-text book
 * Created by yw_sun on 2015/7/21.
 */
public class TextDataProvider implements DataProvider {
    /** Data in memory */
    private String mData = "";
    private TextBookInfo mBookInfo = null;
    private TextDataLocator mMemDataLocator = new TextDataLocator(0, 0);

    public TextDataProvider(TextBookInfo bookInfo) {
        this.mBookInfo = bookInfo;
    }

    /**
     * Read a part of srcFile into memory
     * WARNING:make sure srcFile exists
     * @param srcFile:the source file | locator: data locator for the memory part
     */
    @Override
    public void parseFileToMem(File srcFile, DataLocator locator) {
        if(!(locator instanceof TextDataLocator) || locator.isEmpty()) {
            return ;
        }
        TextDataLocator memLocator = (TextDataLocator)locator;
        //find the nearest start and end byte offset to read file
        int startByteOffset = 0;
        int startCharDis = (int)mBookInfo.mLengthInByte;
        for(Map.Entry<Integer, Integer> entry : mBookInfo.mChar2Byte.entrySet()) {
            int charOffset = entry.getKey();
            if(charOffset <= memLocator.mStartIndex) {
                int dis = memLocator.mStartIndex - charOffset;
                if(dis < startCharDis) {
                    startCharDis = dis;
                    startByteOffset = entry.getValue();
                    mMemDataLocator.mStartIndex = charOffset;
                }
            }
        }
        //read file according to the byte offset
        int charCount = memLocator.mEndIndex - memLocator.mStartIndex;
        try{
            FileInputStream fin = new FileInputStream(srcFile);
            fin.skip(startByteOffset);
            InputStreamReader reader = new InputStreamReader(fin);
            char[] buffer = new char[charCount];
            reader.read(buffer);
            mData = new String(buffer);
        }catch(IOException e) {
            Log.e("TextDataProvider for "+mBookInfo.mBookName, e.getMessage());
        }
    }

    /**
     * outer locators base on the char offset of the whole book
     * inner locators base on the char offset of mData
     */
    private TextDataLocator inner2Outer(TextDataLocator innerLocator) {
        int start = mMemDataLocator.mStartIndex;
        return new TextDataLocator(innerLocator.mStartIndex + start, innerLocator.mEndIndex + start);
    }
    private TextDataLocator outer2Inner(TextDataLocator outerLocator) {
        int start = mMemDataLocator.mStartIndex;
        return new TextDataLocator(outerLocator.mStartIndex + start, outerLocator.mEndIndex + start);
    }

    private void updateMemData(DataLocator outerLocator) {
        if(!(outerLocator instanceof TextDataLocator)) {
            return ;
        }
        TextDataLocator pageLocator = (TextDataLocator)outerLocator;
        //if data enough for current page locator
        int threshold = ReaderConstantValue.TextCapacity>>4;
        int start_start = pageLocator.mStartIndex - mMemDataLocator.mStartIndex;
        int end_end = mMemDataLocator.mEndIndex - pageLocator.mEndIndex;
        int dataLeft = Math.min(start_start, end_end);
        dataLeft = Math.max(dataLeft, 0);
        if(dataLeft > threshold) {
            return ;
        }
        //else we need to load data
        int newStart = Math.max(0, pageLocator.mStartIndex - (ReaderConstantValue.TextCapacity>>1));
        int tailExtendDis = ReaderConstantValue.TextCapacity - (pageLocator.mStartIndex - newStart);
        int newEnd = pageLocator.mEndIndex + tailExtendDis;
        TextDataLocator newLocator = new TextDataLocator(newStart, newEnd);
        File srcFile = new File(mBookInfo.mFilePath);
        parseFileToMem(srcFile, newLocator);
    }

    @Override
    public DataModel readData(DataLocator locator) {
        updateMemData(locator);

        TextDataModel resultModel = new TextDataModel();
        if(!(locator instanceof TextDataLocator)) {
            return resultModel;
        }
        TextDataLocator txtLocator = outer2Inner((TextDataLocator)locator);
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

    /**
    * <p>Create a {#DataLocator} that can fill a {#SunTextPaperView} with a start index</p>
    * @param: start:the start index for locator; page:the view to fill
    * @return a page-filled locator
    * */
    public TextDataLocator createPageFullLocatorFromStart(int startIndex, SunTextPaperView page) {
        updateMemData(new TextDataLocator(startIndex, startIndex));

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
        return inner2Outer(locator);
    }//createPageFullLocatorFromStart

    /**
     * <p>Create a {#DataLocator} that can fill a {#SunTextPaperView} with an end index</p>
     * @param: end:the end index for locator; page:the view to fill
     * @return a page-filled locator
     * */
    public TextDataLocator createPageFullLocatorFromEnd(int endIndex, SunTextPaperView page) {
        updateMemData(new TextDataLocator(endIndex, endIndex));

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
        return inner2Outer(locator);
    }//createPageFullLocatorFromEnd

}
