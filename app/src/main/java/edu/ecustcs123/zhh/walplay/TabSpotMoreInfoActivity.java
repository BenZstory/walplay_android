package edu.ecustcs123.zhh.walplay;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import edu.ecustcs123.zhh.walplay.Utils.CurrentInfo;

public class TabSpotMoreInfoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tab_spot_moreinfo);
        ((TextView) findViewById(R.id.CurrentSpotMoreInfo)).setText(CurrentInfo.CurrentName);
    }
}
