package reader.sun.common.foundation.util;


import reader.sun.sunreader.R;

/**
 * Created by yw_sun on 2015/7/16.
 */
public class SunResourceManager {
    static private SunResourceManager instance = new SunResourceManager();

    public int getFileFolderIcon(){
        return R.drawable.icon_file_folder;
    }
    public int getTextFileIcon() {
        return R.drawable.icon_text_file;
    }

    static public SunResourceManager getInstance() {
        return instance;
    }
}
