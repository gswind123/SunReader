package reader.sun.sunreader;

import android.os.Bundle;

import reader.sun.sunreader.fragment.SunReaderFragment;
import reader.sun.sunreader.util.SunFragmentManager;

/**
 * Created by yw_sun on 2015/7/17.
 */
public class SunReaderActivity extends SunBaseActivity{
    SunReaderFragment readerFragment;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        readerFragment = new SunReaderFragment();
        SunFragmentManager.initFragment(this, readerFragment);
    }

}
