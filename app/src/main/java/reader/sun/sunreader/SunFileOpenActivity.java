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

import reader.sun.sunreader.R;
import reader.sun.sunreader.SunBaseActivity;
import reader.sun.sunreader.model.FileInfoModel;
import reader.sun.sunreader.util.SunFileOpenManager;

/**
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

    private String mSdcardRootPath ;  //sdcard 根路径
    private String mLastFilePath ;    //当前显示的路径

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

    //配置适配器
    private void setGridViewAdapter(String filePath) {
        updateFileItems(filePath);
        mAdatper = new FileChooseAdapter(this , mFileLists);
        fileListView.setAdapter(mAdatper);
    }
    //根据路径更新数据，并且通知Adatper数据改变
    private void updateFileItems(String filePath) {
        mLastFilePath = filePath ;
        pathView.setText(mLastFilePath);

        if(mFileLists == null)
            mFileLists = new ArrayList<FileInfoModel>() ;
        if(!mFileLists.isEmpty())
            mFileLists.clear() ;

        File[] files = folderScan(filePath);
        if(files == null)
            return ;
        for (int i = 0; i < files.length; i++) {
            if(files[i].isHidden())  // 不显示隐藏文件
                continue ;

            String fileAbsolutePath = files[i].getAbsolutePath() ;
            String fileName = files[i].getName();
            boolean isDirectory = false ;
            if (files[i].isDirectory()){
                isDirectory = true ;
            }
            FileInfoModel fileInfo = new FileInfoModel(fileAbsolutePath , fileName , isDirectory) ;
            //添加至列表
            mFileLists.add(fileInfo);
        }
        //When first enter , the object of mAdatper don't initialized
        if(mAdatper != null)
            mAdatper.notifyDataSetChanged();  //重新刷新
    }
    //获得当前路径的所有文件
    private File[] folderScan(String path) {
        File file = new File(path);
        File[] files = file.listFiles();
        return files;
    }
    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> adapterView, View view, int position,
                                long id) {
            FileInfoModel fileInfo = (FileInfoModel)((adapterView.getAdapter()).getItem(position));
            if(fileInfo.isDirectory){
                updateFileItems(fileInfo.absolutePath);
            } else {
                if(isFileSupported(fileInfo.absolutePath)) {
                    Intent data = new Intent();
                    data.putExtra(SunFileOpenManager.FILE_PATH, fileInfo.absolutePath);
                    setResult(RESULT_OK, data);
                    finish();
                }
            }
        }
    };
    public boolean onKeyDown(int keyCode , KeyEvent event){
        if(event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode()
                == KeyEvent.KEYCODE_BACK){
            backProcess();
            return true ;
        }
        return super.onKeyDown(keyCode, event);
    }
    //返回上一层目录的操作
    public void backProcess(){
        //判断当前路径是不是sdcard路径 ， 如果不是，则返回到上一层。
        if (!mLastFilePath.equals(mSdcardRootPath)) {
            File thisFile = new File(mLastFilePath);
            String parentFilePath = thisFile.getParent();
            updateFileItems(parentFilePath);
        }
        else {   //是sdcard路径 ，直接结束
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    private boolean isFileSupported(String filePath) {
        boolean isSupported = false;
        String path_segments[] = filePath.split(".");
        if(path_segments[path_segments.length-1].equals("txt")) {
            isSupported = true;
        }
        return isSupported;
    }

}
