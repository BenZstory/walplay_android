package edu.ecustcs123.zhh.walplay;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class T3Activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        //在实际用的过程中，如果想要在子activity中得到context，需要把这个context = getParent();
        //在我的项目中是这样做的，不过我现在测试，不需要父activity中的context好像也行，如果你遇到错，注意一下这方面。
        //Context context = this.getParent();

        setContentView(R.layout.activity_t3);
        ((TextView) findViewById(R.id.CurrentSpotMoreInfo)).setText(R.string.Current_Spot_More_Info_For_Test);
    }
}
