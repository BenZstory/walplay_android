package edu.ecustcs123.zhh.walplay;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

public class T2Activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_t2);
        ((ListView) findViewById(R.id.CurrentSpotComment)).setBackgroundColor(Color.BLACK);
    }
}
