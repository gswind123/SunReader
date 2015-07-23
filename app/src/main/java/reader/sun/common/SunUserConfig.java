package reader.sun.common;

/**
 * Created by yw_sun on 2015/7/20.
 */
public class SunUserConfig {
    static private SunUserConfig instance = new SunUserConfig();
    static public SunUserConfig getInstance() {
        return instance;
    }

    static public int getPaperTextSizeSp() {
        //TODO:返回可以定制的字号
        return 18;
    }
}
