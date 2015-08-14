package reader.sun.sunreader;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import reader.sun.common.foundation.util.SunDeviceUtil;
import reader.sun.common.foundation.util.SunFileOpenManager;
import reader.sun.common.model.BookInfo;
import reader.sun.common.view.HorizontalListView;
import reader.sun.sunreader.model.TextBookInfo;
import reader.sun.sunreader.util.TextBookProcessor;


public class SunEntranceActivity extends SunBaseActivity {

    private boolean adapterState = true;

    private class DataAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 10;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if(adapterState == true) {
                ImageView image = new ImageView(SunEntranceActivity.this);
                image.setBackgroundResource(R.drawable.icon_text_file);
                return image;
            }else {
                TextView text = new TextView(SunEntranceActivity.this);
                text.setText(i+"");
                DisplayMetrics dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);
                text.setTextSize(SunDeviceUtil.getPixelFromDip(dm,30));
                return text;
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sun_entrance);
        View hello = findViewById(R.id.hello_world);
        HorizontalListView listView = (HorizontalListView)findViewById(R.id.h_list);
        final DataAdapter adapter = new DataAdapter();
        listView.setAdapter(adapter);
        ImageView start = new ImageView(this);
        start.setBackgroundResource(R.drawable.icon_text_file);
        ImageView end = new ImageView(this);
        end.setBackgroundResource(R.drawable.icon_file_folder);
        listView.addHeadView(start);
        listView.addTailView(end);
        hello.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapterState = !adapterState;
                adapter.notifyDataSetChanged();
                //SunFileOpenManager.goFileOpen(SunEntranceActivity.this);

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == SunFileOpenManager.GET_TEXT_FILE && resultCode == RESULT_OK) {
            String fileName = data.getExtras().getString(SunFileOpenManager.FILE_PATH);
            String[] file_segs = fileName.split("\\/");
            String fileDir = fileName.replace(file_segs[file_segs.length-1],"");
            TextBookInfo bookInfo = TextBookProcessor.generateBookInfo(fileName);
            Bundle arguments = new Bundle();
            arguments.putString(SunReaderActivity.KEY_SELECTED_BOOK, BookInfo.serialize(bookInfo));
            startActivity(SunReaderActivity.class, arguments);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sun_entrance, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
