package reader.sun.common.foundation.util;

/**
 * Created by yw_sun on 2015/7/20.
 */
public class StringUtil {
    static public boolean emptyOrNull(String str) {
        if(str == null || str.length() == 0) {
            return true;
        } else return false;
    }
}
