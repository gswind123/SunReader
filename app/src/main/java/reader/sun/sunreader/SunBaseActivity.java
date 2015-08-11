package reader.sun.sunreader;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * Created by yw_sun on 2015/7/16.
 */
public class SunBaseActivity extends FragmentActivity{
    public Bundle getArguments() {
        return getIntent().getExtras();
    }
    public void startActivity(Class<?> newActivity, Bundle arguments, int flags) {
        Intent intent = new Intent();
        intent.setClass(this, newActivity);
        intent.putExtras(arguments);
        intent.setFlags(flags);
        startActivity(intent);
    }
    public void startActivity(Class<?> newActivity, Bundle arguments) {
        Intent intent = new Intent();
        intent.setClass(this, newActivity);
        intent.putExtras(arguments);
        startActivity(intent);
    }
    public void startActivityForResult(Class<?> newActivity, Bundle arguments, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(this, newActivity);
        intent.putExtras(arguments);
        startActivityForResult(intent, requestCode);
    }
}
