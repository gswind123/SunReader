package reader.sun.sunreader;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import reader.sun.common.foundation.util.StringUtil;
import reader.sun.common.foundation.util.SunFileOpenManager;
import reader.sun.sunreader.model.FileInfoModel;

/**
 * Activity for opening file
 * Created by yw_sun on 2015/7/16.
 */
public class SunFileOpenActivity extends SunBaseActivity {

    protected class FileChooseAdapter extends BaseAdapter {

        private ArrayList<FileInfoModel> fileArray = new ArrayList<FileInfoModel>();
        private Context context;

        public FileChooseAdapter(Context context, ArrayList<FileInfoModel> files) {
            this.context = context;
            this.fileArray = files;
        }

        @Override
        public int getCount() {
            return fileArray.size();
        }

        @Override
        public Object getItem(int i) {
            if(i<0 || i>= fileArray.size()) {
                return null;
            }
            return fileArray.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View itemView = inflater.inflate(R.layout.file_choose_list_view, null);
            if(i<0 || i>=fileArray.size()) {
                return itemView;
            }
            FileInfoModel file = fileArray.get(i);
            TextView fileNameView = (TextView)itemView.findViewById(R.id.file_name);
            ImageView fileIconView = (ImageView)itemView.findViewById(R.id.file_icon);
            if(file.isDirectory) {
                fileIconView.setBackgroundResource(R.drawable.icon_file_folder);
            } else {
                fileIconView.setBackgroundResource(R.drawable.icon_text_file);
            }
            fileNameView.setText(file.name);
            return itemView;
        }
    }

    private String mSdcardRootPath ;
    private String mCurFilePath ;

    private ArrayList<FileInfoModel> mFileLists  ;
    private FileChooseAdapter mAdatper ;

    private ListView fileListView;
    private TextView pathView;
    private ImageView backBtn;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.file_open_activity_layout, null);
        setContentView(rootView);
        fileListView = (ListView)rootView.findViewById(R.id.file_list);
        pathView = (TextView)rootView.findViewById(R.id.current_path);
        backBtn = (ImageView)rootView.findViewById(R.id.back_press_arrow);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backProcess();
            }
        });
        fileListView.setOnItemClickListener(mItemClickListener);
        mSdcardRootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        setGridViewAdapter(mSdcardRootPath);
    }

    private void setGridViewAdapter(String filePath) {
        updateFileItems(filePath);
        mAdatper = new FileChooseAdapter(this , mFileLists);
        fileListView.setAdapter(mAdatper);
    }

    private void updateFileItems(String filePath) {
        mCurFilePath = filePath ;
        pathView.setText(mCurFilePath);

        if(mFileLists == null)
            mFileLists = new ArrayList<FileInfoModel>() ;
        if(!mFileLists.isEmpty())
            mFileLists.clear() ;

        File[] files = folderScan(filePath);
        if(files == null)
            return ;
        for (int i = 0; i < files.length; i++) {
            if(files[i].isHidden()){
                continue ;
            }
            String fileAbsolutePath = files[i].getAbsolutePath() ;
            String fileName = files[i].getName();
            boolean isDirectory = false ;
            if (files[i].isDirectory()){
                isDirectory = true ;
            }
            FileInfoModel fileInfo = new FileInfoModel(fileAbsolutePath , fileName , isDirectory) ;

            mFileLists.add(fileInfo);
        }
        if(mAdatper != null)
            mAdatper.notifyDataSetChanged();
    }

    private File[] folderScan(String path) {
        File file = new File(path);
        File[] files = new File[0];
        try{
            files = file.listFiles();
        }catch(Exception e) {
        }
        return files;
    }
    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            FileInfoModel fileInfo = (FileInfoModel)((adapterView.getAdapter()).getItem(position));
            if(fileInfo.isDirectory){
                updateFileItems(fileInfo.absolutePath);
            } else {
                if(isFileSupported(fileInfo.absolutePath)) {
                    Intent dataIntent = new Intent();
                    Bundle dataBundle = new Bundle();
                    dataBundle.putString(SunFileOpenManager.FILE_PATH, fileInfo.absolutePath);
                    dataIntent.putExtras(dataBundle);
                    setResult(RESULT_OK, dataIntent);
                    finish();
                }
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode , KeyEvent event){
        if(event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK){
            backProcess();
            return true ;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void backProcess(){
        File thisFile = new File(mCurFilePath);
        String parentFilePath = thisFile.getParent();
        //If the current path has a parent, open the parent
        if(!StringUtil.emptyOrNull(parentFilePath)){
            updateFileItems(parentFilePath);
        }
        else {//if not, cancel file opening
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    private boolean isFileSupported(String filePath) {
        //TODO:txt only currently,make it configurable
        boolean isSupported = false;
        String[] path_segments = filePath.split("\\.");
        if(path_segments.length == 0) {
            return isSupported;
        }
        if(path_segments[path_segments.length-1].equalsIgnoreCase("txt")) {
            isSupported = true;
        }
        return isSupported;
    }

}
