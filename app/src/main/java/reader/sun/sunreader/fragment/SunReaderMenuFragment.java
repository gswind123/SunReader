package reader.sun.sunreader.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import reader.sun.common.SunBaseFragment;
import reader.sun.common.widget.SunProgressBar;
import reader.sun.sunreader.R;

/**
 * Menu view for reader
 * Created by yw_sun on 2015/9/7.
 */
public class SunReaderMenuFragment extends SunBaseFragment{
    private SunProgressBar mProgressBar = null;

    SunProgressBar.OnSetProgressListenser mOnSetProgressListener = new SunProgressBar.OnSetProgressListenser() {
        @Override
        public void onSetProgress(int progress, SunProgressBar progressBar) {

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstance) {
        View rootView = inflater.inflate(R.layout.sun_reader_menu_fragment, null);
        mProgressBar = (SunProgressBar)rootView.findViewById(R.id.progress_bar);
        mProgressBar.setOnSetProgreeListener(mOnSetProgressListener);
        return rootView;
    }
}
