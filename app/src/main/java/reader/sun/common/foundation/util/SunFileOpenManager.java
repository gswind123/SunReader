package reader.sun.common.foundation.util;

import android.content.Intent;

import reader.sun.sunreader.SunBaseActivity;
import reader.sun.sunreader.SunFileOpenActivity;

/**
 * Created by yw_sun on 2015/7/17.
 */
public class SunFileOpenManager {
    static final public String FILE_PATH = "sun_file_open_manager_file_path";
    static final public int GET_TEXT_FILE = 0x3001;

    static public void goFileOpen(SunBaseActivity activity) {
        Intent intent = new Intent();
        intent.setClass(activity, SunFileOpenActivity.class);
        activity.startActivityForResult(intent, GET_TEXT_FILE);
    }
}
