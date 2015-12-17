package edu.ecustcs123.zhh.walplay;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

public class TabSpotPicActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_tab_spot_pic);
        ((ImageView) findViewById(R.id.CurrentSpotPic)).setBackgroundResource(R.drawable.bfzn_006);
    }
}
