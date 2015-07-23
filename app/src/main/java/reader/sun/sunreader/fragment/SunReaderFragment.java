package reader.sun.sunreader.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import reader.sun.sunreader.R;
import reader.sun.sunreader.model.TextDataLocator;
import reader.sun.sunreader.model.TextDataModel;
import reader.sun.sunreader.util.TextDataProvider;
import reader.sun.sunreader.widget.SunTextPaperView;

/**
 * Created by yw_sun on 2015/7/16.
 */
public class SunReaderFragment extends SunBaseFragment{
    private SunTextPaperView mCurrentPage = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.sun_reader_fragment, null);
        mCurrentPage = (SunTextPaperView)rootView.findViewById(R.id.current_page);
        return rootView;
    }
}
