package edu.ecustcs123.zhh.walplay;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

public class T1Activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_t1);
        ((ImageView) findViewById(R.id.CurrentSpotPic)).setBackgroundResource(R.drawable.bfzn_006);
    }
}
