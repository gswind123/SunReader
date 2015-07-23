package reader.sun.common.foundation.util;

import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * Created by yw_sun on 2015/7/20.
 */
public class SunDeviceUtil {
    static public int getPixelFromDip(DisplayMetrics dm,float dip) {
        return (int)(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, dm)+0.5f);
    }

}
