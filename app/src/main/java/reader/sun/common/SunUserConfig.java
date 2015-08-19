package reader.sun.common;

import android.database.DataSetObserver;

import java.util.HashSet;

/**
 * Record and manager user configure info
 * Created by yw_sun on 2015/7/20.
 */
public class SunUserConfig {
    private static SunUserConfig mInstance = new SunUserConfig();
    /** User config values */
    private class ConfigInfo {
        public int mPaperTextSizeDip = 18;
        public String mLatestVisitedPath = "";
    }
    private ConfigInfo mConfigInfo = new ConfigInfo();
    private ConfigInfo getConfigInfo() {
        return mConfigInfo;
    }
    /** Data Observers for different usages*/
    private HashSet<DataSetObserver> mPaperObserverSet = new HashSet<DataSetObserver>();

    static public void attachPaperObserver(DataSetObserver observer) {
        if(!mInstance.mPaperObserverSet.contains(observer)) {
            mInstance.mPaperObserverSet.add(observer);
        }
    }
    static public void detachPaperObserver(DataSetObserver observer) {
        observer.onInvalidated();
        mInstance.mPaperObserverSet.remove(observer);
    }
    static public void notifyPaperConfigChange() {
        for(DataSetObserver observer : mInstance.mPaperObserverSet) {
            observer.onChanged();
        }
    }

    static public int getPaperTextSizeDip() {
        return mInstance.getConfigInfo().mPaperTextSizeDip;
    }

    static public void saveLatestVisitedPath(String filePath) {
        mInstance.getConfigInfo().mLatestVisitedPath = filePath;
    }
    static public String loadLatestVisitedPath() {
        return mInstance.getConfigInfo().mLatestVisitedPath;
    }
}
