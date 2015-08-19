package reader.sun.sunreader;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import reader.sun.common.foundation.util.SunFileOpenManager;
import reader.sun.common.model.BookInfo;
import reader.sun.sunreader.model.TextBookInfo;
import reader.sun.sunreader.util.TextBookProcessor;


public class SunEntranceActivity extends SunBaseActivity {

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sun_entrance);
        View hello = findViewById(R.id.hello_world);
        hello.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SunFileOpenManager.goFileOpen(SunEntranceActivity.this);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == SunFileOpenManager.GET_TEXT_FILE && resultCode == RESULT_OK) {
            String fileName = data.getExtras().getString(SunFileOpenManager.FILE_PATH);
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
