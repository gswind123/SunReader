package reader.sun.sunreader.model;

import reader.sun.common.model.DataLocator;

/**
 * Created by yw_sun on 2015/7/21.
 */
public class TextDataLocator extends DataLocator {
    /**
     * mData = "012345678", mStartIndex = 0, mEndIndex = 4
     * {#readData} results "0123"
     * */
    public int mStartIndex = 0;
    public int mEndIndex = 0;

    @Override
    public boolean isEmpty() {
        if(mEndIndex <= mStartIndex) {
            return true;
        } else return false;
    }
}
