package edu.ecustcs123.zhh.walplay;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ListView;

public class TabSpotCommentActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_tab_spot_comment);
        ((ListView) findViewById(R.id.CurrentSpotComment)).setBackgroundColor(Color.BLACK);
    }
}
