package edu.ecustcs123.zhh.walplay;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TabHost;
import android.widget.TextView;

import java.util.List;

public class SpotDetailActivity extends AppCompatActivity {

    private TabHost mTabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spot_detail);
        Intent intent = getIntent();
        int tmp = intent.getIntExtra("listPos", -1);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        PlayerPanelFragment playerPanelFragment = new PlayerPanelFragment();

        if(tmp != -1){
            //通过点击item进入detail,需要重置listPos
            Log.d(AppConstant.LOG.NewSpotPlaying+"_ATY", String.valueOf(tmp));
            Bundle bundle = new Bundle();
            bundle.putInt(AppConstant.KEY.NEW_PLAYING,tmp);
            playerPanelFragment.setArguments(bundle);
        }else{
            Bundle bundle = intent.getBundleExtra(AppConstant.KEY.PARCELABLE_PLAYINGINFO);
            Log.d(AppConstant.LOG.Com_Frag_Aty+"INTENT", String.valueOf(intent));
            playerPanelFragment.setArguments(bundle);           //获得playingInfo
            Log.d(AppConstant.LOG.Com_Frag_Aty+"_SAVING", String.valueOf(bundle));
            Log.d(AppConstant.LOG.Com_Frag_Aty, "putBundle_spotAty");
        }

        transaction.add(R.id.fragmentContainer_controlPanel, playerPanelFragment);
        transaction.commit();
        mTabHost=(TabHost)findViewById(R.id.tabHost);
        mTabHost.setup();
        mTabHost.addTab(mTabHost.newTabSpec("tab1").setIndicator("图片").setContent(R.id.tab1));
        mTabHost.addTab(mTabHost.newTabSpec("tab2").setIndicator("评论").setContent(R.id.tab2));
        mTabHost.addTab(mTabHost.newTabSpec("tab3").setIndicator("更多").setContent(R.id.tab3));
    }
}