package reader.sun.common;

/**
 * Record and manager user configure info
 * Created by yw_sun on 2015/7/20.
 */
public class SunUserConfig {
    /** User config values */

    /** Single instance */
    static private SunUserConfig instance = new SunUserConfig();
    static public SunUserConfig getInstance() {
        return instance;
    }

    static public int getPaperTextSizeSp() {
        //TODO:return a customizable font size
        return 18;
    }
}
