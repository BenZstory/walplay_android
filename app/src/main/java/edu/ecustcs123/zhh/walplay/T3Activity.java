package edu.ecustcs123.zhh.walplay;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class T3Activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_t3);
        ((TextView) findViewById(R.id.CurrentSpotMoreInfo)).setText(CurrentInfo.CurrentName);
    }
}
