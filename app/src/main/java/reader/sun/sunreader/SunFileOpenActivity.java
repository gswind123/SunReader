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
import java.util.HashSet;

import reader.sun.common.SunUserConfig;
import reader.sun.common.foundation.util.StringUtil;
import reader.sun.common.foundation.util.SunFileOpenManager;
import reader.sun.common.view.HorizontalListView;
import reader.sun.sunreader.model.FileInfoModel;

/**
 * Activity for opening files
 * Created by yw_sun on 2015/7/16.
 */
public class SunFileOpenActivity extends SunBaseActivity {
    static public final int TYPE_TXT = 0x1;
    static public final int TYPE_PDF = 0x2;
    static public final int TYPE_PNG = 0x4;
    static public final int TYPE_JPG = 0x8;

    static public final String KEY_SUPPORT_TYPE = "key_support_type";

    private HashSet<String> mSupportTypeSet = new HashSet<String>();

    private void setSupportTypes(int flags) {
        mSupportTypeSet.clear();
        if((flags & TYPE_TXT) == TYPE_TXT) {
            mSupportTypeSet.add("txt");
        }
        if((flags & TYPE_PDF) == TYPE_PDF) {
            mSupportTypeSet.add("pdf");
        }
        if((flags & TYPE_PNG) == TYPE_PNG) {
            mSupportTypeSet.add("png");
        }
        if((flags & TYPE_JPG) == TYPE_JPG) {
            mSupportTypeSet.add("jpg");
        }
    }
    private void setIconForFile(FileInfoModel file, ImageView image) {
        String type = file.getType();
        if(file.isDirectory()) {
            image.setBackgroundResource(R.drawable.icon_file_folder);
        } else if("txt".equals(type)){
            image.setBackgroundResource(R.drawable.icon_text_file);
        } else {
            image.setBackgroundResource(R.drawable.icon_unhandle_file);
        }
    }

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

        private class FileViewHolder{
            TextView nameText;
            ImageView iconImage;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            FileViewHolder holder = null;
            View itemView = null;
            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                itemView = inflater.inflate(R.layout.file_choose_list_view, null);
                holder = new FileViewHolder();
                holder.nameText = (TextView)itemView.findViewById(R.id.file_name);
                holder.iconImage = (ImageView)itemView.findViewById(R.id.file_icon);
                itemView.setTag(holder);
            } else {
                itemView = convertView;
                holder = (FileViewHolder)itemView.getTag();
            }

            FileInfoModel file = fileArray.get(position);

            setIconForFile(file, holder.iconImage);
            holder.nameText.setText(file.getName());
            return itemView;
        }
    }

    private class FilePathAdapter extends  BaseAdapter{
        private ArrayList<String> mPathList = new ArrayList<String>();

        public FilePathAdapter(ArrayList<String> pathSeg) {
            mPathList = pathSeg;
        }

        @Override
        public int getCount() {
            return mPathList.size();
        }

        @Override
        public Object getItem(int index) {
            //Item is the co-responding path of current seg
            StringBuilder sb = new StringBuilder();
            sb.append("/");
            for(int i=0;i<=index;i++) {
                sb.append(mPathList.get(i));
                if(i != index) {
                    sb.append("/");
                }
            }
            return sb.toString();
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            ViewHolder holder = null;
            if(itemView != null) {
                holder = (ViewHolder)itemView.getTag();
            } else {
                LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                itemView = inflater.inflate(R.layout.file_path_list_item, null);
                holder = new ViewHolder();
                holder.mFolderName = (TextView)itemView.findViewById(R.id.folder_name);
                holder.mNextArrow = (ImageView)itemView.findViewById(R.id.next_arrow);
                itemView.setTag(holder);
            }
            holder.mFolderName.setText(mPathList.get(position));
            if(position == this.getCount()-1) {
                holder.mNextArrow.setVisibility(View.GONE);
            } else {
                holder.mNextArrow.setVisibility(View.VISIBLE);
            }
            return itemView;
        }
        private class ViewHolder{
            public TextView mFolderName;
            public ImageView mNextArrow;
        }
    }

    private String mSdcardRootPath ;
    private String mCurFilePath ;

    private ArrayList<FileInfoModel> mFileLists  ;
    private FileChooseAdapter mAdatper ;
    private FilePathAdapter mPathAdapter;

    private ListView fileListView;
    private HorizontalListView mFilePathList;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if(arguments != null) {
            int flags = arguments.getInt(KEY_SUPPORT_TYPE, 0);
            setSupportTypes(flags);
        }
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.file_open_activity_layout, null);
        initView(rootView);
    }

    private void initView(View rootView) {
        setContentView(rootView);
        fileListView = (ListView)rootView.findViewById(R.id.file_list);
        fileListView.setOnItemClickListener(mItemClickListener);
        mFilePathList = (HorizontalListView)rootView.findViewById(R.id.file_path_list_view);
        mFilePathList.setOnItemClickListener(mOnPathClickListener);
        View btnCancel = rootView.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(mOnFileSelectCancel);
        mSdcardRootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String latestPath = SunUserConfig.loadLatestVisitedPath();
        if(StringUtil.emptyOrNull(latestPath)) {
            latestPath = mSdcardRootPath;
        }
        setFileListViewAdapter(latestPath);
    }

    private void setFileListViewAdapter(String filePath) {
        updateFileItems(filePath);
        mAdatper = new FileChooseAdapter(this , mFileLists);
        fileListView.setAdapter(mAdatper);
    }
    private void setFilePathAdapter(String filePath) {
        ArrayList<String> pathSegList = new ArrayList<String>();
        for(String seg : filePath.split("\\/")) {
            if(!StringUtil.emptyOrNull(seg)) {
                pathSegList.add(seg);
            }
        }
        mPathAdapter = new FilePathAdapter(pathSegList);
        mFilePathList.setAdapter(mPathAdapter);
        mFilePathList.scrollToEnd();
    }

    private void updateFileItems(String filePath) {
        mCurFilePath = filePath ;
        setFilePathAdapter(filePath);

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
        File[] files = null;
        try{
            files = file.listFiles();
        }catch(Exception e) {
        }
        if(files == null) {
            files = new File[0];
        }
        return files;
    }
    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            FileInfoModel fileInfo = (FileInfoModel)((adapterView.getAdapter()).getItem(position));
            if(fileInfo.isDirectory()){
                updateFileItems(fileInfo.getAbsolutePath());
            } else {
                if(isFileSupported(fileInfo.getAbsolutePath())) {
                    Intent dataIntent = new Intent();
                    Bundle dataBundle = new Bundle();
                    dataBundle.putString(SunFileOpenManager.FILE_PATH, fileInfo.getAbsolutePath());
                    dataIntent.putExtras(dataBundle);
                    setResult(RESULT_OK, dataIntent);
                    finish();
                }
            }
        }
    };

    private AdapterView.OnItemClickListener mOnPathClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            if(position == adapterView.getAdapter().getCount()-1) {
                return ;
            }
            String filePath = (String)adapterView.getAdapter().getItem(position);
            setFileListViewAdapter(filePath);
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

    private View.OnClickListener mOnFileSelectCancel = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            setResult(RESULT_CANCELED);
            finish();
        }
    };

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
        boolean isSupported = false;
        String[] path_segments = filePath.split("\\.");
        if(path_segments.length == 0) {
            return isSupported;
        }
        String type = path_segments[path_segments.length-1].toLowerCase();
        if(mSupportTypeSet.contains(type)) {
            isSupported = true;
        }
        return isSupported;
    }

    @Override
    public void onDestroy() {
        if(!StringUtil.emptyOrNull(mCurFilePath)) {
            SunUserConfig.saveLatestVisitedPath(mCurFilePath);
        }
        super.onDestroy();
    }

}
