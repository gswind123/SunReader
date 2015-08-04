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
    static public int toInt(String str) {
        if(emptyOrNull(str)) {
            return 0;
        }
        int res = 0;
        try{
            res = Integer.parseInt(str);
        }catch(NumberFormatException e) {
        }
        return res;
    }
}
