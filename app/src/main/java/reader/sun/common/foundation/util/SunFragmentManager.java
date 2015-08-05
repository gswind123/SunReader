package reader.sun.common.foundation.util;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import reader.sun.sunreader.SunBaseActivity;
import reader.sun.common.SunBaseFragment;

/**
 * Created by yw_sun on 2015/7/17.
 */
public class SunFragmentManager {
    static public void initFragment(SunBaseActivity baseActivity, SunBaseFragment fragment) {
        initFragment(baseActivity, fragment, "", android.R.id.content);
    }
    static public void initFragment(SunBaseActivity baseActivity, SunBaseFragment fragment, String tag) {
        initFragment(baseActivity, fragment,tag, android.R.id.content);
    }

    static public void initFragment(SunBaseActivity baseActivity, SunBaseFragment fragment, String tag, int position) {
        FragmentManager fm = baseActivity.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(position ,fragment, tag);
        ft.commit();
    }
}
