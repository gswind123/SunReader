package reader.sun.common.foundation.util;

import android.os.Bundle;

import reader.sun.sunreader.SunBaseActivity;
import reader.sun.sunreader.SunFileOpenActivity;

/**
 * Manager to open file
 * invoke {#goFileOpen} and deal <code>GET_TEXT_FILE</code> in <code>onActivityResult</code>:
 * <code>intent.getExtras().getString(SunFileOpenManager.FILE_PATH)</code> to achieve dst file path
 * Created by yw_sun on 2015/7/17.
 */
public class SunFileOpenManager {
    static final public String FILE_PATH = "sun_file_open_manager_file_path";
    static final public int GET_TEXT_FILE = 0x3001;

    static public void goFileOpen(SunBaseActivity activity) {
        Bundle arguments = new Bundle();
        arguments.putInt(SunFileOpenActivity.KEY_SUPPORT_TYPE, SunFileOpenActivity.TYPE_TXT);
        activity.startActivityForResult(SunFileOpenActivity.class, arguments, GET_TEXT_FILE);
    }
}
