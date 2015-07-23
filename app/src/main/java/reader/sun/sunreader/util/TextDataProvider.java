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
 * Created by yw_sun on 2015/7/21.
 */
public class TextDataProvider implements DataProvider {


    private String mData = "《野草》英文译本序\n" +
            "\n" +
            "　　·鲁迅·\n" +
            "\n" +
            "　　冯Y·S·先生由他的友人给我看《野草》的英文译本，并且要我说几句话。可惜我不懂英文，\n" +
            "　　只能自己说几句。但我希望，译者将不嫌我只做了他所希望的一半的。\n" +
            "\n" +
            "　　这二十多篇小品，如每篇末尾所注，是一九二四至二六年在北京所作，陆续发表于期刊《语丝》\n" +
            "　　上的。大抵仅仅是随时的小感想。因为那时难于直说，所以有时措辞就很含糊了。\n" +
            "\n" +
            "　　现在举几个例罢。因为讽刺当时盛行的失恋诗，作《我的失恋》，因为憎恶社会上旁观者之多，\n" +
            "　　作《复仇》第一篇，又因为惊异于青年之消沉，作《希望》。《这样的战士》，是有感于文人学士们\n" +
            "　　帮助军阀而作。《腊叶》，是为爱我者的想要保存我而作的。段祺瑞政府枪击徒手民众后，作《淡淡\n" +
            "　　的血痕中》，其时我已避居别处；奉天派和直隶派军阀战争的时候，作《一觉》，此后我就不能住在\n" +
            "　　北京了。\n" +
            "\n" +
            "　　所以，这也可以说，大半是废驰的地狱边沿的惨白色小花，当然不会美丽。但这地狱也必须失掉。\n" +
            "　　这是由几个有雄辩和辣手，而当时还未得志的英雄们的脸色和语气所告诉我的。我于是作《失掉的好\n" +
            "　　地狱》。\n" +
            "\n" +
            "　　后来，我不再作这样的东西了。日在变化的时代，已不许这样的文章，甚而至于这样的感想存在。\n" +
            "　　我想，这也许倒是好的罢。为译本而作的序言，也应该在这里结束了。\n" +
            "\n" +
            "　　〔一九三一年〕十一月五日。\n" +
            "\n" +
            "　　〔选自《二心集》〕\n";

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
