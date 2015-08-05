package reader.sun.sunreader;

import android.os.Bundle;

import reader.sun.common.foundation.util.SunFragmentManager;
import reader.sun.sunreader.fragment.SunTextReaderFragment;
import reader.sun.sunreader.model.TextBookInfo;

/**
 * Created by yw_sun on 2015/7/17.
 */
public class SunReaderActivity extends SunBaseActivity{
    private SunTextReaderFragment readerFragment;

    static public final String KEY_SELECTED_BOOK = "key_selected_book";

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        Bundle arguments = getIntent().getExtras();
        TextBookInfo bookInfo = null;
        if(arguments != null) {
            String bookStr = arguments.getString(KEY_SELECTED_BOOK);
            bookInfo = new TextBookInfo(bookStr);
        }

        readerFragment = new SunTextReaderFragment();
        readerFragment.setCurrentBook(bookInfo);
        SunFragmentManager.initFragment(this, readerFragment);
    }

}
